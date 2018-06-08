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

public class LibraryDataLoader extends AsyncTask<Boolean, Void, Boolean> {

    SQLiteDatabase db = LibraryManager.db;

    @Override
    public Boolean doInBackground(Boolean... args) {

        boolean isDataValid = args[0];
        boolean errorFlag = false;

        if (!isDataValid) {
            errorFlag = parseHtml();
        }

        createInstance();

        return errorFlag;
    }

    @Override
    public void onPostExecute(Boolean errorFlag) {
        LibraryManager.activity.readyLibrary(errorFlag);
    }

    private boolean parseHtml() {

        String[] urls = new String[]{
                Constants.LIBRARY_SOUGOU_URL,
                Constants.LIBRARY_SEIMEI_URL,
                Constants.LIBRARY_RIKOU_URL,
                Constants.LIBRARY_GAIKOKU_URL
        };

        db.delete(SQLiteDBHelper.TABLE_LIBRARIES, null, null);

        Boolean overallErrorFlag = false;
        Boolean individualErrorFlag = false;

        int year = MainActivity.currentDate.get(Calendar.YEAR);
        int month = MainActivity.currentDate.get(Calendar.MONTH);

        for (String url : urls) {

            individualErrorFlag = false;

            String name = null;
            List<Integer> dateList = new ArrayList<>();
            List<String> timeStringsList = new ArrayList<>();

            try {

                Document doc = Jsoup.connect(url).get();
                Element body = doc.body();

                // 図書館名をnameに格納
                name = doc.title().substring(0, doc.title().indexOf("図書館"));

                // 日付と営業時間のtrをtableRowsに格納
                Element table = body.getElementsByTag("table").get(0);
                Elements tableRows = table.getElementsByTag("tr");
                tableRows.remove(0);

                // それぞれのtableRowのtableDataをdateListとtimeStringsListに格納
                for (Element tableRow : tableRows) {
                    for (Element tableDatum : tableRow.children()) {
                        String tableDatumSource = tableDatum.html().replace(" ", "").replace("\t", "");
                        if (tableDatumSource.equals("<br>")) {
                            continue;
                        }
                        String[] splitTableDatum = tableDatumSource.split("<br>");

                        if (!splitTableDatum[0].equals("")) {
                            // dateList
                            dateList.add(Integer.parseInt(splitTableDatum[0]));

                            // timeStringsList
                            timeStringsList.add(Utility.convertTimes(Utility.parseTimes(splitTableDatum[1]), false));
                        }
                    }
                }

            } catch (Exception e) {
                overallErrorFlag = true;
                individualErrorFlag = true;
                e.printStackTrace();
            }

            if (!individualErrorFlag) {
                db.beginTransaction();

                // insert data into database
                for (int i = 0; i < dateList.size(); i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("name", name);
                    contentValues.put("year", year);
                    contentValues.put("month", month);
                    contentValues.put("date", dateList.get(i));
                    contentValues.put("times", timeStringsList.get(i));
                    db.insert(SQLiteDBHelper.TABLE_LIBRARIES, null, contentValues);
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            }
        }

        return overallErrorFlag;
    }

    private void createInstance() {

        Cursor cursor = db.query(
                SQLiteDBHelper.TABLE_LIBRARIES,
                null,
                "year = " + MainActivity.currentDate.get(Calendar.YEAR)
                        + " AND month = " + MainActivity.currentDate.get(Calendar.MONTH)
                        + " AND date = " + MainActivity.currentDate.get(Calendar.DATE),
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {

            // create Library instance from database
            do {
                new Library(
                        cursor.getLong(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        Utility.convertTimes(cursor.getString(cursor.getColumnIndex("times")))
                );
            } while (cursor.moveToNext());

        }

        cursor.close();

    }
}