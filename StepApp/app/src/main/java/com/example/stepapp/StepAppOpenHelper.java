package com.example.stepapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.anychart.editor.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StepAppOpenHelper extends SQLiteOpenHelper {



    private static Double Weight;
    private static Double Height;
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "stepapp";

    public static final String TABLE_NAME = "num_steps";
    public static final String KEY_ID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_DAY = "day";

    public static final String PROFILE_NAME = "profile";
    public static final String PROFILE_KEY = "profileID";
    public static final String WEIGHT_KEY = "weight";
    public static final String HEIGHT_KEY = "height";
    // Default SQL for creating a table in a database
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, " + KEY_DAY + " TEXT, " + KEY_HOUR + " TEXT, "
            + KEY_TIMESTAMP + " TEXT);";
    public static final String SQL_PROFILE = "CREATE TABLE " + PROFILE_NAME + " (" + PROFILE_KEY+
            " INTEGER PRIMARY KEY, " + WEIGHT_KEY + " TEXT, " + HEIGHT_KEY+ " TEXT);";

    // The constructor
    public StepAppOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
        db.execSQL(SQL_PROFILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        switch(oldVersion) {
//            case 1:
//                db.execSQL(CREATE_TABLE_SQL);
//            case 2:
//                db.execSQL(SQL_PROFILE);
//        }
        if (newVersion > oldVersion){
            db.execSQL(SQL_PROFILE);
        }
    }



    public static List<String> loadProfile(Context context){
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
//        Cursor cursor = database.query(StepAppOpenHelper.PROFILE_NAME, null, null,null,null,null,null);
        Cursor cursor = database.rawQuery("SELECT weight, height FROM profile", null);
        List<String> profile = new ArrayList<>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i=0;i<=cursor.getCount();i++){
//                System.out.println(cursor.getString(i));
                profile.add(cursor.getString(i));
            }

        } else{
            profile.add("0 KG");
            profile.add("0 cm");
            ContentValues values = new ContentValues();
            values.put(StepAppOpenHelper.WEIGHT_KEY,0 + " KG");
            values.put(StepAppOpenHelper.HEIGHT_KEY,0 + " cm");
            database.insert(StepAppOpenHelper.PROFILE_NAME,null,values);
        }

        cursor.close();
        database.close();
        return profile;
    }


    public static void deleteProfile(Context context){
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int numberDeletedRecords =0;

        numberDeletedRecords = database.delete(StepAppOpenHelper.PROFILE_NAME, null, null);
        database.close();

        // display the number of deleted records with a Toast message
        Toast.makeText(context,"Deleted " + String.valueOf(numberDeletedRecords) + " steps",Toast.LENGTH_LONG).show();
    }
    /**
     * Utility function to load all records in the database
     *
     * @param context: application context
     */
    public static void loadRecords(Context context){
        List<String> dates = new LinkedList<String>();
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String [] columns = new String [] {StepAppOpenHelper.KEY_TIMESTAMP};
        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, columns, null, null, StepAppOpenHelper.KEY_TIMESTAMP,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Log.d("STORED TIMESTAMPS: ", String.valueOf(dates));
    }

    /**
     * Utility function to delete all records from the data base
     *
     * @param context: application context
     */
    public static void deleteRecords(Context context){
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int numberDeletedRecords =0;

        numberDeletedRecords = database.delete(StepAppOpenHelper.TABLE_NAME, null, null);
        database.close();

        // display the number of deleted records with a Toast message
        Toast.makeText(context,"Deleted " + String.valueOf(numberDeletedRecords) + " steps",Toast.LENGTH_LONG).show();
    }

    /**
     * Utility function to load records from a single day
     *
     * @param context: application context
     * @param date: today's date
     * @return numSteps: an integer value with the number of records in the database
     */
    //
    public static Integer loadSingleRecord(Context context, String date){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        List<String> profile = StepAppOpenHelper.loadProfile(context);
        Double weight = Double.parseDouble(profile.get(0).replaceAll("[^0-9]", ""));
        Double height = Double.parseDouble(profile.get(1).replaceAll("[^0-9]", ""));
        final double calorieConstant = 0.003154;



        String where = StepAppOpenHelper.KEY_DAY + " = ?";
        String [] whereArgs = { date };

        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS TODAY: ", String.valueOf(numSteps));
        return numSteps;
    }

    public static Double getWeight(){
        return Weight;
    }
    public static Double getHeight(){
        return Height;
    }

    public static Double loadCalories(Context context, String date){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        final double calorieConstant = 0.003154;
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        List<String> profile = StepAppOpenHelper.loadProfile(context);
        Weight = Double.parseDouble(profile.get(0).replaceAll("[^0-9]", ""));
        Height = Double.parseDouble(profile.get(1).replaceAll("[^0-9]", ""));




        String where = StepAppOpenHelper.KEY_DAY + " = ?";
        String [] whereArgs = { date };

        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Double numSteps = (double) steps.size();

        return numSteps * Weight* Height* calorieConstant;
    }

    /**
     * Utility function to get the number of steps by hour for current date
     *
     * @param context: application context
     * @param date: today's date
     * @return map: map with key-value pairs hour->number of steps
     */
    //
    public static Map<Integer, Integer> loadStepsByHour(Context context, String date){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<Integer, Integer>  map = new HashMap<> ();


        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        List<String> profile = StepAppOpenHelper.loadProfile(context);

        Double weight = Double.parseDouble(profile.get(0).replaceAll("[^0-9]", ""));
        Double height = Double.parseDouble(profile.get(1).replaceAll("[^0-9]", ""));
        final double calorieConstant = 0.003154;


        Cursor cursor = database.rawQuery("SELECT hour, COUNT(*)  FROM num_steps " +
                "WHERE day = ? GROUP BY hour ORDER BY  hour ASC ", new String [] {date});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            Integer tmpKey = Integer.parseInt(cursor.getString(0));
            Double tmpValue = Double.parseDouble(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, (int) (tmpValue * weight * height * calorieConstant));


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }

    /**
     * Utility function to get the number of steps by day
     *
     * @param context: application context
     * @return map: map with key-value pairs hour->number of steps
     */
    //
    public static Map<String, Integer> loadStepsByDay(Context context){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<String, Integer>  map = new TreeMap<>();

        // 2. Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        List<String> profile = StepAppOpenHelper.loadProfile(context);

        Double weight = Double.parseDouble(profile.get(0).replaceAll("[^0-9]", ""));
        Double height = Double.parseDouble(profile.get(1).replaceAll("[^0-9]", ""));
        final double calorieConstant = 0.003154;

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT day, COUNT(*)  FROM num_steps " +
                "GROUP BY day ORDER BY day ASC ", new String [] {});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            String tmpKey = cursor.getString(0);
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            // Put the data from the database into the map
            map.put(tmpKey,(int) (tmpValue * weight * height * calorieConstant));
            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }
}









