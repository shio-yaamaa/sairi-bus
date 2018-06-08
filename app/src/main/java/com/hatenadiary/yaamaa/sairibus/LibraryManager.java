package com.hatenadiary.yaamaa.sairibus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LibraryManager {

    static MainActivity activity;
    static SQLiteDatabase db;

    static List<Library> libraryList = new ArrayList<>();

    public static void initialize(MainActivity activity, SQLiteDatabase db) {
        LibraryManager.activity = activity;
        LibraryManager.db = db;

        // clear lists
        libraryList.clear();

        // validate data in the database
        Cursor cursor = db.query(
                SQLiteDBHelper.TABLE_LIBRARIES,
                null,
                "year = " + MainActivity.currentDate.get(Calendar.YEAR)
                        + " AND month = " + MainActivity.currentDate.get(Calendar.MONTH)
                        + " AND date = " + MainActivity.currentDate.get(Calendar.DATE),
                null, null, null, null, null
        );
        boolean isDataValid = cursor.moveToFirst();
        cursor.close();

        new LibraryDataLoader().execute(isDataValid);
    }

}