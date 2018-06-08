package com.hatenadiary.yaamaa.sairibus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
            errorFlag = parseCsv();
        }

        setIsHoliday();

        return errorFlag;
    }

    @Override
    public void onPostExecute(Boolean errorFlag) {
        HolidayManager.activity.readyHoliday(errorFlag);
    }

    private boolean parseCsv() {

        Boolean errorFlag = false;

        int year = MainActivity.currentDate.get(Calendar.YEAR);

        List<int[]> monthAndDateList = new ArrayList<>();

        try {
            URL url = new URL(Constants.HOLIDAY_URL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split(",")[0].split("-")[0].equals(String.valueOf(year))) { // If the line is for the target year
                    String[] yearMonthDateStrings = line.split(",")[0].split("-");
                    int[] monthAndDate = new int[] {Integer.parseInt(yearMonthDateStrings[1]), Integer.parseInt(yearMonthDateStrings[2])};
                    Log.d("myTag", "Holiday: " + monthAndDate[0] + "/" + monthAndDate[1]);
                    monthAndDateList.add(monthAndDate);
                }
            }
            reader.close();
        } catch (MalformedURLException e) {
            errorFlag = true;
            e.printStackTrace();
        } catch (IOException e) {
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