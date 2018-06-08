package com.hatenadiary.yaamaa.sairibus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BusManager {

    static MainActivity activity;
    static SQLiteDatabase db;

    static List<String> sectionList = new ArrayList<>();
    static List<List<ArrayList<String>>> directionList = new ArrayList<>();
    static List<Bus> busList = new ArrayList<>();

    public static void initialize(MainActivity activity, SQLiteDatabase db) {
        BusManager.activity = activity;
        BusManager.db = db;

        // clear lists
        sectionList.clear();
        directionList.clear();
        busList.clear();

        // validate data in the database
        Cursor cursor = db.query(SQLiteDBHelper.TABLE_BUSES, null, null, null, null, null, null, null);
        boolean isDataValid = (cursor.moveToFirst()
                && Utility.isBusDataValid(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("date"))));
        cursor.close();

        new BusDataLoader().execute(isDataValid);
    }
}