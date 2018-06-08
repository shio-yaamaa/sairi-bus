package com.hatenadiary.yaamaa.sairibus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MapCoordinateManager {

    static SQLiteDatabase db;
    static List<MapCoordinate> mapCoordinateList = new ArrayList<>();

    public static void initialize(SQLiteDatabase database) {
        db = database;

        // set up mapCoordinateList
        mapCoordinateList.clear();

        Cursor cursor = db.query(SQLiteDBHelper.TABLE_MAP_COORDINATES, null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                new MapCoordinate(
                        cursor.getLong(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("latitude")),
                        cursor.getString(cursor.getColumnIndex("longitude"))
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static MapCoordinate getMapCoordinateByName(String name) {
        for (MapCoordinate mapCoordinate : mapCoordinateList) {
            if (name.contains(mapCoordinate.name)) {
                return mapCoordinate;
            }
        }
        return null;
    }
}