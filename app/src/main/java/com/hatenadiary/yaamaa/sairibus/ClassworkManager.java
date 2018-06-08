package com.hatenadiary.yaamaa.sairibus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClassworkManager {

    static SQLiteDatabase db;
    static List<Classwork> classworkList = new ArrayList<>();

    // for undo deleting
    static Classwork lastDeletedClasswork = null;
    static int lastDeletedClassworkPosition = -1;

    public static void initialize(SQLiteDatabase database) {
        db = database;

        // clear lists
        classworkList.clear();

        Cursor cursor = db.query(SQLiteDBHelper.TABLE_CLASSWORKS, null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                new Classwork(
                        cursor.getLong(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        Utility.convertTimes(cursor.getString(cursor.getColumnIndex("times")))
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static Classwork getClassworkById(long id) {
        for (Classwork classwork : classworkList) {
            if (classwork.id == id) {
                return classwork;
            }
        }
        return null;
    }

    public static Classwork addClasswork(ArrayAdapter<Classwork> adapter, String name, int[] times) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("times", Utility.convertTimes(times, false));
        long id = db.insert(SQLiteDBHelper.TABLE_CLASSWORKS, null, contentValues);

        Classwork classwork = new Classwork(id, name, times);
        adapter.notifyDataSetChanged();

        return classwork;
    }

    public static Classwork editClasswork(ArrayAdapter<Classwork> adapter, Classwork classwork, String name, int[] times) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("times", Utility.convertTimes(times, false));
        db.update(SQLiteDBHelper.TABLE_CLASSWORKS, contentValues, "_id = " + classwork.id, null);

        classwork.name = name;
        classwork.times = times;

        adapter.notifyDataSetChanged();

        return classwork;
    }

    public static void deleteClasswork(ArrayAdapter<Classwork> adapter, Classwork classwork) {
        lastDeletedClasswork = classwork;
        lastDeletedClassworkPosition = classworkList.indexOf(classwork);

        classworkList.remove(classwork);
        db.delete(SQLiteDBHelper.TABLE_CLASSWORKS, "_id = " + classwork.id, null);
        adapter.notifyDataSetChanged();
    }

    public static void undoDeleteClasswork(ArrayAdapter<Classwork> adapter) {
        classworkList.add(lastDeletedClassworkPosition, lastDeletedClasswork);

        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", lastDeletedClasswork.id);
        contentValues.put("name", lastDeletedClasswork.name);
        contentValues.put("times", Utility.convertTimes(lastDeletedClasswork.times, false));
        db.insert(SQLiteDBHelper.TABLE_CLASSWORKS, null, contentValues);

        adapter.notifyDataSetChanged();
    }

}