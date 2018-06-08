package com.hatenadiary.yaamaa.sairibus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HolidayDataLoader extends AsyncTask<Boolean, Void, Boolean> {

    SQLiteDatabase db = HolidayManager.db;

    @Override
    public Boolean doInBackground(Boolean... args) {

        boolean isDataValid = args[0];
        boolean errorFlag = false;

        if (!isDataValid) {
            errorFlag = parseHtml();
        }

        setIsHoliday();

        return errorFlag;
    }

    @Override
    public void onPostExecute(Boolean errorFlag) {
        HolidayManager.activity.readyHoliday(errorFlag);
    }

    private boolean parseHtml() {

        Boolean errorFlag = false;

        int year = MainActivity.currentDate.get(Calendar.YEAR);

        List<int[]> monthAndDateList = new ArrayList<>();

        try {

            Document doc = Jsoup.connect(Constants.HOLIDAY_URL).get();
            Element body = doc.body();

            Elements tables = body.getElementsByClass("tableBase");

            Element tableOfThisYear = null;
            for (Element table : tables) {
                if (table.previousElementSibling().text().contains(String.valueOf(year))) {
                    tableOfThisYear = table;
                    break;
                }
            }

            Element tableBody = tableOfThisYear.getElementsByTag("tbody").get(0);

            for (Element tableRow : tableBody.children()) {
                String monthAndDateInString = tableRow.children().get(1).text();
                monthAndDateList.add(Utility.parseMonthAndDate(monthAndDateInString));
            }

        } catch (Exception e) {
            errorFlag = true;
            e.printStackTrace();
        }

        if (!errorFlag) {
            db.beginTransaction();
            db.delete(SQLiteDBHelper.TABLE_HOLIDAYS, null, null);

            // insert data into database
            for (int[] monthAndDate : monthAndDateList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("year", year);
                contentValues.put("month", monthAndDate[0]);
                contentValues.put("date", monthAndDate[1]);

                db.insert(SQLiteDBHelper.TABLE_HOLIDAYS, null, contentValues);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return errorFlag;
    }

    private void setIsHoliday() {

        Cursor cursor = db.query(
                SQLiteDBHelper.TABLE_HOLIDAYS,
                null,
                "year = " + MainActivity.currentDate.get(Calendar.YEAR)
                        + " AND month = " + MainActivity.currentDate.get(Calendar.MONTH)
                        + " AND date = " + MainActivity.currentDate.get(Calendar.DATE),
                null, null, null, null, null
        );

        HolidayManager.isHoliday = cursor.moveToFirst();

        int dayOfWeek = MainActivity.currentDate.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1 || dayOfWeek == 7) {
            HolidayManager.isHoliday = true;
        }

        cursor.close();

    }
}