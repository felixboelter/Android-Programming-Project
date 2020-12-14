package com.example.stepapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StepAppSettingHelper extends SQLiteOpenHelper {

    private String DB_NAME; // the name of our database
    private Context context;
    private static final int DB_VERSION = 1; // the version of the database

    public StepAppSettingHelper(Context context,String DB_NAME) {
        super(context, DB_NAME, null, DB_VERSION);
        this.DB_NAME = DB_NAME;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME +"_TABLE"+ " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "SET_FLAG INTEGER, "
                + "GENDER TEXT, "
                + "AGE INTEGER, "
                + "HEIGHT INTEGER, "
                + "WEIGHT INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertRow(SQLiteDatabase db, int set_flag, int age,
                          int weight, int height, String gender){
        ContentValues contentValues = new ContentValues();
        contentValues.put("SET_FLAG", set_flag);
        contentValues.put("GENDER", gender);
        contentValues.put("AGE", age);
        contentValues.put("WEIGHT", weight);
        contentValues.put("HEIGHT", height);
        db.insert( DB_NAME +"_TABLE", null, contentValues);
    }

    public void deleteAll(SQLiteDatabase db){
        db.execSQL("delete from "+ DB_NAME +"_TABLE");
    }
    public void deleteRecord(SQLiteDatabase db, int _id){
        db.delete(DB_NAME +"_TABLE", "_id = ?", new String[]{Integer.toString(_id)});
    }

    public Cursor getTheTableContent(SQLiteDatabase db){
        Cursor cursor;
        cursor = db.query(DB_NAME +"_TABLE", new String[] {"_id","SET_FLAG", "GENDER",
                        "AGE", "HEIGHT", "WEIGHT"},
                null, null, null, null, null);
        return cursor;
    }
}
