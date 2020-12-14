package com.example.stepapp.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;
import com.example.stepapp.StepAppSettingHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class HomeFragment extends Fragment {
    public Context context;
    String SETTING_DB_NAME = "SETTING_DB";

    MaterialButtonToggleGroup materialButtonToggleGroup;

    // Text view and Progress Bar variables
    public TextView caloriesBurntTextView;
    public TextView stepsWakedTextView;
    public ProgressBar caloriesBurntProgressBar;
    // ACC sensors.
    private Sensor mSensorACC;
    private SensorManager mSensorManager;
    private SensorEventListener listener;

    // Step Detector sensor
    private Sensor mSensorStepDetector;

    // Completed steps
    public static int stepsCompleted = 0;

    public static double caloriesBurnt = 0;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        context = getContext();

        // Get the number of steps stored in the current date
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        stepsCompleted = StepAppOpenHelper.loadSingleRecord(getContext(), fDate);

        //caloriesBurnt = StepAppOpenHelper.loadCalories(getContext(), fDate);

        // an instance of profile info settingHelperFB
        StepAppSettingHelper stepAppSettingHelper = new StepAppSettingHelper(getContext(), SETTING_DB_NAME);
        String gender = "female";
        int age;
        int weight;
        int height;
        int setFlag = 0;
        double pace = 4.8f;
        double met = 2f;
        SQLiteDatabase mSettingDB = stepAppSettingHelper.getReadableDatabase();
        Cursor settingCursor = stepAppSettingHelper.getTheTableContent(mSettingDB);

        if (settingCursor.moveToFirst()) {
            gender = settingCursor.getString(settingCursor.getColumnIndex("GENDER"));
            age = settingCursor.getInt(settingCursor.getColumnIndex("AGE"));
            weight = settingCursor.getInt(settingCursor.getColumnIndex("WEIGHT"));
            height = settingCursor.getInt(settingCursor.getColumnIndex("HEIGHT"));
            setFlag = settingCursor.getInt(settingCursor.getColumnIndex("SET_FLAG"));
        }else{
            gender = "";
            age = 0;
            weight = 0;
            height = 0;
        }
        double BMR = 0f;
        if (gender.equals("female") || gender.equals("others")){
            BMR = 10*weight + 6.25*height - 5*age - 161;
        }else if (gender.equals("male")){
            BMR = 10*weight + 6.25*height - 5*age + 5;
        }
        met = weight*35./200;
        caloriesBurnt = BMR*met/24*stepsCompleted/(pace*1000);
        settingCursor.close();
        mSettingDB.close();
        // Text view & ProgressBar
        caloriesBurntTextView =  root.findViewById(R.id.caloriesBurnt);
        stepsWakedTextView =  root.findViewById(R.id.stepsWalked);
        stepsWakedTextView.setText(Integer.toString(stepsCompleted));
        caloriesBurntProgressBar =  root.findViewById(R.id.progressBar);
        caloriesBurntProgressBar.setMax(1000);
        //Set the Views with the number of stored steps
        if (setFlag == 0 | gender.equals("")){
            Toast.makeText(getContext(), "Not enough info to calculate!", Toast.LENGTH_SHORT).show();
            caloriesBurntTextView.setText("0 Calories");
        }else {
            caloriesBurntTextView.setText(String.format("%.2f",caloriesBurnt) + " Calories");
        }
        caloriesBurntProgressBar.setProgress((int) caloriesBurnt);

        //  Get an instance of the sensor manager.
        mSensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorACC = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // Step detector instance
        mSensorStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


        StepAppOpenHelper databaseOpenHelper = new StepAppOpenHelper(this.getContext());;
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        //Instantiate the StepCounterListener
        listener = new StepCounterListener(database, caloriesBurntTextView, stepsWakedTextView ,
                caloriesBurntProgressBar, context, stepAppSettingHelper);



        // Toggle group button
        materialButtonToggleGroup = (MaterialButtonToggleGroup) root.findViewById(R.id.toggleButtonGroup);
        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if (group.getCheckedButtonId() == R.id.toggleStart) {

                    //Place code related to Start button
                    Toast.makeText(getContext(), "START", Toast.LENGTH_SHORT).show();

                    // Check if the Accelerometer sensor exists
                    if (mSensorACC != null) {

                        // Register the ACC listener
                        mSensorManager.registerListener(listener, mSensorACC, SensorManager.SENSOR_DELAY_NORMAL);
                    } else {
                        Toast.makeText(getContext(), R.string.acc_not_available, Toast.LENGTH_SHORT).show();

                    }

                    // Check if the Step detector sensor exists
                    if (mSensorStepDetector != null) {
                        // Register the ACC listener
                        mSensorManager.registerListener(listener, mSensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);

                    } else {
                        Toast.makeText(getContext(), R.string.step_not_available, Toast.LENGTH_SHORT).show();

                    }


                } else if (group.getCheckedButtonId() == R.id.toggleStop) {
                    //Place code related to Stop button
                    Toast.makeText(getContext(), "STOP", Toast.LENGTH_SHORT).show();

                    // Unregister the listener
                    mSensorManager.unregisterListener(listener);
                }
            }
        });
        //////////////////////////////////////


        return root;


    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
        mSensorManager.unregisterListener(listener);
    }


}

// Sensor event listener
class StepCounterListener<stepsCompleted> implements SensorEventListener {

    private long lastUpdate = 0;


    // these arguments are for accessing the setting DB that contains
    // the user information
    SQLiteDatabase settingDB;
    String gender = "female";
    int age;
    int weight;
    int height;
    int setFlag = 0;
    double BMR;
    double met = 2f; // just a default value, this will be calculated exactly
    long prevTimeStamp = 0;
    double pace = 0;
    Context context;

    // ACC Step counter
    // public int mACCStepCounter = 0;

    //Get the number of stored steps for the current day
    public int mACCStepCounter = HomeFragment.stepsCompleted;


    ArrayList<Integer> mACCSeries = new ArrayList<Integer>();
    ArrayList<String> mTimeSeries = new ArrayList<String>();

    private double accMag = 0d;
    private int lastXPoint = 1;
    int stepThreshold = 10;

    // Android step detector
    public int mAndroidStepCounter = 0;


    // TextView and Progress Bar
    TextView stepsCountTextView;
    TextView calBurnedTextView;
    ProgressBar stepsCountProgressBar;

    //
    private SQLiteDatabase database;
    public String timestamp;
    public String day;
    public String hour;

    // Get the database, TextView and ProgressBar as args
    public StepCounterListener(SQLiteDatabase db, TextView tv, TextView stepsTV, ProgressBar pb,
                               Context context, StepAppSettingHelper stepAppSettingHelper){
        calBurnedTextView = tv;
        stepsCountTextView = stepsTV;
        stepsCountProgressBar = pb;
        database = db;
        this.context = context;


        // get the user info needed
        settingDB = stepAppSettingHelper.getReadableDatabase();
        Cursor settingCursor = stepAppSettingHelper.getTheTableContent(settingDB);

        if (settingCursor.moveToFirst()) {
            gender = settingCursor.getString(settingCursor.getColumnIndex("GENDER"));
            age = settingCursor.getInt(settingCursor.getColumnIndex("AGE"));
            weight = settingCursor.getInt(settingCursor.getColumnIndex("WEIGHT"));
            height = settingCursor.getInt(settingCursor.getColumnIndex("HEIGHT"));
            setFlag = settingCursor.getInt(settingCursor.getColumnIndex("SET_FLAG"));
        }else{
            gender = "";
            age = 0;
            weight = 0;
            height = 0;
        }
        if (gender.equals("female") || gender.equals("others")){
            BMR = (double) 10*weight + 6.25*height - 5*age - 161;
        }else if (gender.equals("male")){
            BMR = (double) 10*weight + 6.25*height - 5*age + 5;
        }
        met = weight*35./200;

        settingCursor.close();
        settingDB.close();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {

            // Case of the ACC
            case Sensor.TYPE_LINEAR_ACCELERATION:

                // Get x,y,z
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                //////////////////////////// -- PRINT ACC VALUES -- ////////////////////////////////////
                // Timestamp
                long timeInMillis = System.currentTimeMillis() + (event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000;

                // Convert the timestamp to date
                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String date = jdf.format(timeInMillis);


                //measuring velocity in all 3 directions
                // Getting current sample timestamp
                long currentTimeStamp = event.timestamp;
                // if first sample then interval = 0
                if(prevTimeStamp == 0)
                    prevTimeStamp = currentTimeStamp;
                double NS2Hour = 1e-9/3600;// ((1e-9 s)/1ns)*((1 min)/60s)*((1h)/60min)
                // calculating interval (in seconds)
                double interval = (currentTimeStamp - prevTimeStamp) * NS2Hour;
                // v = a*t + v0 --> last v will be v0 of current v
                pace = pace + (x + y + z) * interval; // m/h --> later, we will divide this by 1k
                // updating prevTimeStamp for next sample..
                prevTimeStamp = currentTimeStamp;

                /*
                // print a value every 1000 ms
                long curTime = System.currentTimeMillis();
                if ((curTime - lastUpdate) > 1000) {
                    lastUpdate = curTime;

                    Log.d("ACC", "X: " + String.valueOf(x) + " Y: " + String.valueOf(y) + " Z: "
                            + String.valueOf(z) + " t: " + String.valueOf(date));

                }
                */

                // Get the date, the day and the hour
                timestamp = date;
                day = date.substring(0,10);
                hour = date.substring(11,13);

                ////////////////////////////////////////////////////////////////////////////////////////

                /// STEP COUNTER ACC ////
                accMag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

                //Update the Magnitude series
                mACCSeries.add((int) accMag);

                //Update the time series
                mTimeSeries.add(timestamp);


                // Calculate ACC peaks and steps
                peakDetection();

                break;

            // case Step detector
            case Sensor.TYPE_STEP_DETECTOR:

                // Calculate the number of steps
                countSteps(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void peakDetection() {
        int windowSize = 20;

        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer, Mladenov et al.
         */
        int highestValX = mACCSeries.size(); // get the length of the series
        if (highestValX - lastXPoint < windowSize) { // if the segment is smaller than the processing window skip it
            return;
        }

        List<Integer> valuesInWindow = mACCSeries.subList(lastXPoint,highestValX);
        List<String> timesInWindow = mTimeSeries.subList(lastXPoint,highestValX);

        lastXPoint = highestValX;

        int forwardSlope = 0;
        int downwardSlope = 0;

        List<Integer> dataPointList = new ArrayList<Integer>();
        List<String> timePointList = new ArrayList<String>();


        for (int p =0; p < valuesInWindow.size(); p++){
            dataPointList.add(valuesInWindow.get(p)); // ACC Magnitude data points
            timePointList.add(timesInWindow.get(p)); // Timestamps
        }

        for (int i = 0; i < dataPointList.size(); i++) {
            if (i == 0) {
            }
            else if (i < dataPointList.size() - 1) {
                forwardSlope = dataPointList.get(i + 1) - dataPointList.get(i);
                downwardSlope = dataPointList.get(i)- dataPointList.get(i - 1);

                if (forwardSlope < 0 && downwardSlope > 0 && dataPointList.get(i) > stepThreshold ) {

                    // Update the number of steps
                    mACCStepCounter += 1;
                    // Log.d("ACC STEPS: ", String.valueOf(mACCStepCounter));

                    // Update the TextView and the ProgressBar
                    stepsCountTextView.setText(String.valueOf(mACCStepCounter));
                    stepsCountProgressBar.setProgress(mACCStepCounter);

                    // update calories burned
                    double calories_burned = BMR*met/24*mACCStepCounter/(pace*1000);
                    if (setFlag == 0 | gender.equals("")){
                        Toast.makeText(context, "Not enough info to calculate!", Toast.LENGTH_SHORT).show();
                        calBurnedTextView.setText("0 Calories");
                    }else {
                        calBurnedTextView.setText(String.format("%.2f",calories_burned) + " Calories");
                    }

                    //Insert the data in the database
                    ContentValues values = new ContentValues();
                    values.put(StepAppOpenHelper.KEY_TIMESTAMP, timePointList.get(i));
                    values.put(StepAppOpenHelper.KEY_DAY, day);
                    values.put(StepAppOpenHelper.KEY_HOUR, hour);
                    database.insert(StepAppOpenHelper.TABLE_NAME, null, values);
                }

            }
        }
    }


    // Calculate the number of steps from the step detector
    private void countSteps(float step) {

        //Step count
        mAndroidStepCounter += (int) step;
        Log.d("NUM STEPS ANDROID", "Num.steps: " + String.valueOf(mAndroidStepCounter));
    }



}



