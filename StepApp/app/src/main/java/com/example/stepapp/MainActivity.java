package com.example.stepapp;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stepapp.ui.home.HomeFragment;
import com.example.stepapp.ui.map.MapFragment;
import com.example.stepapp.ui.profile.ProfileFragment;
import com.example.stepapp.ui.report.DayFragment;
import com.example.stepapp.ui.report.HourFragment;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.mapboxsdk.maps.Style;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String SETTING_DB_NAME = "SETTING_DB";
    private SQLiteDatabase settingDB;
    private StepAppSettingHelper stepAppSettingHelper;
    boolean settingIsEmpty = false;

    String gender = "female";
    int age;
    int weight;
    int height;


    private DrawerLayout drawer;
    private NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int REQUEST_ACTIVITY_RECOGNITION_PERMISSION = 45;

    private boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        // Setup drawer view

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_hour,R.id.nav_day,R.id.nav_map ,R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupDrawerContent(navigationView);
        // Ask for activity recognition permission
        if (runningQOrLater) {
            getActivity();
        }

        stepAppSettingHelper = new StepAppSettingHelper(this, SETTING_DB_NAME);
        settingDB = stepAppSettingHelper.getReadableDatabase();
        Cursor settingCursor = stepAppSettingHelper.getTheTableContent(settingDB);

        if (!settingCursor.moveToFirst()){
            settingIsEmpty = true;
        }else {
            settingIsEmpty = false;
        }
        settingCursor.close();
        settingDB.close();

        if (settingIsEmpty){
            Log.i("SETTING", "Cursor is empty");
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.setting_picker_layout);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(
                    this,R.drawable.gray_bg));

            Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);
            Button setButton = (Button) dialog.findViewById(R.id.dialog_set_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gender = "female";
                    dialog.dismiss();
                }
            });
            final NumberPicker agePicker = (NumberPicker) dialog.findViewById(R.id.age_picker);
            agePicker.setMinValue(0);
            agePicker.setMaxValue(100);
            agePicker.setValue(25);

            final NumberPicker weightPicker = (NumberPicker) dialog.findViewById(R.id.weight_picker);
            weightPicker.setMinValue(0);
            weightPicker.setMaxValue(200);
            weightPicker.setValue(70);

            final NumberPicker heightPicker = (NumberPicker) dialog.findViewById(R.id.height_picker);
            heightPicker.setMinValue(0);
            heightPicker.setMaxValue(250);
            heightPicker.setValue(175);

            final TextView maleTV = (TextView)dialog.findViewById(R.id.male_tv_id);
            final TextView femaleTV = (TextView)dialog.findViewById(R.id.female_tv_id);
            final TextView othersTV = (TextView)dialog.findViewById(R.id.others_tv_id);
            femaleTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    femaleTV.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.line_gray_bg));
                    maleTV.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                    othersTV.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                    gender = "female";
                }
            });

            maleTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maleTV.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.line_gray_bg));
                    femaleTV.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                    othersTV.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                    gender = "male";
                }
            });

            othersTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    othersTV.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.line_gray_bg));
                    femaleTV.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                    maleTV.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                    gender = "others";
                }
            });

            setButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    age = agePicker.getValue();
                    weight = weightPicker.getValue();
                    height = heightPicker.getValue();

                    settingDB = stepAppSettingHelper.getWritableDatabase();
                    stepAppSettingHelper.insertRow(settingDB,1, age, weight,
                            height, gender);
                    settingDB.close();
                    settingIsEmpty = false;
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clickedac
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Steps");
                }
                break;
            case R.id.nav_hour:
                fragmentClass = HourFragment.class;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Hour");
                }
                break;
            case R.id.nav_day:
                fragmentClass = DayFragment.class;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Day");
                }
                break;
            case R.id.nav_map:
                fragmentClass = MapFragment.class;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Map");
                }
                break;
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Profile");
                }
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawer.closeDrawers();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    // Ask for permission
    private void getActivity() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACTIVITY_RECOGNITION},
                    REQUEST_ACTIVITY_RECOGNITION_PERMISSION);
        } else {
            return;        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACTIVITY_RECOGNITION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getActivity();
                }  else {
                    Toast.makeText(this,
                            R.string.step_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }

                return;
        }
    }



}