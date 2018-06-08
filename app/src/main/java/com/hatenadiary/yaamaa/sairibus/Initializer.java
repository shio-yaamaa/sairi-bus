package com.hatenadiary.yaamaa.sairibus;

import android.database.sqlite.SQLiteDatabase;

public class Initializer {

    public static SQLiteDatabase db;

    public static void initialize(MainActivity activity) {
        if (db == null) {
            db = SQLiteDBHelper.getDatabase(activity);
        }

        RedrawTimer.initialize(activity);

        RowTitleMaker.initialize(activity);
        PeriodViewGroupMaker.initialize(activity);

        // initialize data
        OnTouchListenerToFocus.initialize();
        ClassworkManager.initialize(db);
        BusManager.initialize(activity, db);
        LibraryManager.initialize(activity, db);
        RestaurantManager.initialize(activity, db);
        HolidayManager.initialize(activity, db);
        MapCoordinateManager.initialize(db);
    }
}