package com.hatenadiary.yaamaa.sairibus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static SQLiteDatabase db = null;

    public static final String DB_NAME = "db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_CLASSWORKS = "classworks";
    public static final String TABLE_BUSES = "buses";
    public static final String TABLE_LIBRARIES = "libraries";
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String TABLE_HOLIDAYS = "holidays";
    public static final String TABLE_MAP_COORDINATES = "map_coordinates";

    private SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (db == null) {
            db = (new SQLiteDBHelper(context)).getWritableDatabase();
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_CLASSWORKS
                        + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, times TEXT)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_BUSES
                        + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                        + "year INTEGER, month INTEGER, date INTEGER, "
                        + "section TEXT, direction TEXT, times TEXT, "
                        + "two_buses INTEGER, micro_disease INTEGER, lane INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_LIBRARIES
                        + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                        + "year INTEGER, month INTEGER, date INTEGER, "
                        + "times TEXT)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_RESTAURANTS
                        + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                        + "year INTEGER, month INTEGER, date INTEGER, "
                        + "campus TEXT, times TEXT)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_HOLIDAYS
                        + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "year INTEGER, month INTEGER, date INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_MAP_COORDINATES
                        + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "name TEXT, latitude TEXT, longitude TEXT)"
        );

        // デフォルトの授業
        ContentValues classworkContentValues = new ContentValues();
        for (int i = 0; i < Constants.defaultClassworkNames.length; i++) {
            classworkContentValues.put("name", Constants.defaultClassworkNames[i]);
            classworkContentValues.put("times", Constants.defaultClassworkTimes[i]);
            db.insert(TABLE_CLASSWORKS, null, classworkContentValues);
        }

        // デフォルトの地図座標
        ContentValues mapCoordinateContentValues = new ContentValues();
        for (int i = 0; i < Constants.mapCoordinateNames.length; i++) {
            mapCoordinateContentValues.put("name", Constants.mapCoordinateNames[i]);
            mapCoordinateContentValues.put("latitude", Constants.mapCoordinateLatitudes[i]);
            mapCoordinateContentValues.put("longitude", Constants.mapCoordinateLongitudes[i]);
            db.insert(TABLE_MAP_COORDINATES, null, mapCoordinateContentValues);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public static void clear() {
        db.beginTransaction();

        db.delete(TABLE_BUSES, null, null);
        db.delete(TABLE_LIBRARIES, null, null);
        db.delete(TABLE_RESTAURANTS, null, null);
        db.delete(TABLE_HOLIDAYS, null, null);

        db.setTransactionSuccessful();
        db.endTransaction();
    }
}