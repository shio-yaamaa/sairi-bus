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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantDataLoader extends AsyncTask<Boolean, Void, Boolean> {

    SQLiteDatabase db = RestaurantManager.db;

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
        RestaurantManager.activity.readyRestaurant(errorFlag);
    }

    private boolean parseHtml() {

        Map<RestaurantManager.Campus, String> urlMap = new HashMap<>();
        urlMap.put(RestaurantManager.Campus.TOYONAKA, Constants.RESTAURANT_TOYONAKA_URL);
        urlMap.put(RestaurantManager.Campus.SUITA, Constants.RESTAURANT_SUITA_URL);
        urlMap.put(RestaurantManager.Campus.MINOH, Constants.RESTAURANT_MINOH_URL);

        Boolean overallErrorFlag = false;
        Boolean individualErrorFlag = false;

        int year = MainActivity.currentDate.get(Calendar.YEAR);
        int month = MainActivity.currentDate.get(Calendar.MONTH);

        db.beginTransaction();
        db.delete(SQLiteDBHelper.TABLE_RESTAURANTS, null, null);

        for (Map.Entry<RestaurantManager.Campus, String> entry : urlMap.entrySet()) {

            individualErrorFlag = false;

            List<String> nameList = new ArrayList<>();
            List<String[]> timeStringsList = new ArrayList<>();
            List<Integer> dateList = new ArrayList<>();

            try {

                Document doc = Jsoup.connect(entry.getValue()).get();
                Element body = doc.body();

                // table-02のclassを持つテーブルのうち今月の食堂(1番目)のもののTBODYに含まれるTRをrestaurantTableRowsに格納
                Elements tables = body.getElementsByClass("table-02");
                Elements thisMonthTables = new Elements();
                for (Element table : tables) {
                    if (table.html().contains(String.format("%d月", month + 1))) {
                        thisMonthTables.add(table);
                    }
                }
                //Elements thisMonthTables = tables.select("td:contains(" + (month + 1) + "月)");
                Elements restaurantTableRows = thisMonthTables.get(0).child(0).children();

                // restaurantTableRowsの子TRのうち店名のTR(1番目)はrestaurantNameRowに格納し、時間のTR(2番目以降)はrestaurantTableRowsに残る
                Element restaurantNameRow = restaurantTableRows.remove(0);

                // 今月以前の月のTRを削除(長期休暇対策)
                int deleteCount = 0;
                for (Element restaurantTableRow : restaurantTableRows) {
                    if (restaurantTableRow.html().contains(String.format("%d月", month + 1))) {
                        break;
                    }
                    deleteCount++;
                }
                List<Element> singledRestaurantTableRows = restaurantTableRows.subList(deleteCount, restaurantTableRows.size());

                // 今月以降の月のTRを削除(長期休暇対策)
                int rowspan = Integer.parseInt(singledRestaurantTableRows.get(0).child(0).attr("rowspan"));
                singledRestaurantTableRows = singledRestaurantTableRows.subList(0, rowspan);

                // 食堂の名前をnameListに格納、その際にBRタグによって作られたスペースを削除
                Elements restaurantNameTableData = restaurantNameRow.children();
                for (int i = 0; i < restaurantNameTableData.size(); i++) {
                    if (i > 2) {
                        nameList.add(restaurantNameTableData.get(i).ownText().replace(" ", ""));
                    }
                }

                // TDの後ろからnameList.size()の数だけ取り、timeStringsListに格納
                // 同時に、日付をdateListに格納
                for (int i = 0; i < singledRestaurantTableRows.size(); i++) {
                    Element restaurantTableRow = singledRestaurantTableRows.get(i);
                    Elements restaurantTableData = restaurantTableRow.children();

                    timeStringsList.add(new String[nameList.size()]);

                    for (int j = 0; j < nameList.size(); j++) {
                        Element restaurantTableDatum = restaurantTableData.get(restaurantTableData.size() - nameList.size() + j);
                        timeStringsList.get(i)[j] = restaurantTableDatum.ownText().replace(" ", "");
                    }
                    dateList.add(
                            Integer.parseInt(
                                    restaurantTableData.get(
                                            restaurantTableData.size() - nameList.size() - 2
                                    ).ownText()
                            )
                    );
                }

            } catch (Exception e) {
                overallErrorFlag = true;
                individualErrorFlag = true;
                e.printStackTrace();
            }

            if (!individualErrorFlag) {

                // insert data into database
                for (int i = 0; i < dateList.size(); i++) {
                    int date = dateList.get(i);
                    for (int j = 0; j < nameList.size(); j++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", nameList.get(j));
                        contentValues.put("year", year);
                        contentValues.put("month", month);
                        contentValues.put("date", date);
                        contentValues.put("campus", entry.getKey().name);
                        contentValues.put("times", Utility.convertTimes(Utility.parseTimes(timeStringsList.get(i)[j]), false));
                        db.insert(SQLiteDBHelper.TABLE_RESTAURANTS, null, contentValues);
                    }
                }

            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return overallErrorFlag;
    }

    private void createInstance() {
        Cursor cursor = db.query(
                SQLiteDBHelper.TABLE_RESTAURANTS,
                null,
                "year = " + MainActivity.currentDate.get(Calendar.YEAR)
                        + " AND month = " + MainActivity.currentDate.get(Calendar.MONTH)
                        + " AND date = " + MainActivity.currentDate.get(Calendar.DATE),
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {

            // create Restaurant instance from database
            do {
                new Restaurant(
                        cursor.getLong(cursor.getColumnIndex("_id")),
                        RestaurantManager.Campus.judgeCampus(cursor.getString(cursor.getColumnIndex("campus"))),
                        cursor.getString(cursor.getColumnIndex("name")),
                        Utility.convertTimes(cursor.getString(cursor.getColumnIndex("times")))
                );
            } while (cursor.moveToNext());

        }

        cursor.close();
    }
}