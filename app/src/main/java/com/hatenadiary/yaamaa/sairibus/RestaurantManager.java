package com.hatenadiary.yaamaa.sairibus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class RestaurantManager {

    static MainActivity activity;
    static SQLiteDatabase db;

    public enum Campus {
        TOYONAKA("豊中", new ArrayList<Restaurant>()),
        SUITA("吹田", new ArrayList<Restaurant>()),
        MINOH("箕面", new ArrayList<Restaurant>());

        public final String name;
        public ArrayList<Restaurant> restaurantList;

        Campus(String name, ArrayList<Restaurant> restaurantList) {
            this.name = name;
            this.restaurantList = restaurantList;
        }

        static Campus judgeCampus(String name) {
            for (Campus campus : Campus.values()) {
                if (campus.name.equals(name)) {
                    return campus;
                }
            }
            return null;
        }

        static void clear() {
            for (Campus campus : Campus.values()) {
                campus.restaurantList.clear();
            }
        }
    }

    public static void initialize(MainActivity activity, SQLiteDatabase db) {
        RestaurantManager.activity = activity;
        RestaurantManager.db = db;

        // clear lists
        Campus.clear();

        // validate data in the database
        Cursor cursor = db.query(
                SQLiteDBHelper.TABLE_RESTAURANTS,
                null,
                "year = " + MainActivity.currentDate.get(Calendar.YEAR)
                        + " AND month = " + MainActivity.currentDate.get(Calendar.MONTH)
                        + " AND date = " + MainActivity.currentDate.get(Calendar.DATE),
                null, null, null, null, null
        );
        boolean isDataValid = cursor.moveToFirst();
        cursor.close();

        new RestaurantDataLoader().execute(isDataValid);
    }

}