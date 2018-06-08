package com.hatenadiary.yaamaa.sairibus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

public class HolidayManager {

    static MainActivity activity;
    static SQLiteDatabase db;

    public static boolean isHoliday = false;

    public static void initialize(MainActivity activity, SQLiteDatabase db) {
        HolidayManager.activity = activity;
        HolidayManager.db = db;

        // validate data in the database
        Cursor cursor = db.query(
                SQLiteDBHelper.TABLE_HOLIDAYS,
                null,
                "year = " + MainActivity.currentDate.get(Calendar.YEAR),
                null, null, null, null, null
        );
        boolean isDataValid = cursor.moveToFirst();
        cursor.close();

        new HolidayDataLoader().execute(isDataValid);
    }

}