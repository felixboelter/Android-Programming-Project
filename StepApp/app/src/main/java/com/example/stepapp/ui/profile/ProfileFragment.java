package com.example.stepapp.ui.profile;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.editor.Step;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;


import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {


    public String getWeightText() {
        return weightText.toString();
    }


    public String getHeightText() {
        return heightText.toString();
    }


    public TextView weightText;
    public TextView heightText;
    public SeekBar weightSeekBar;
    public SeekBar heightSeekBar;
    public List<String> profile;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        weightSeekBar = root.findViewById(R.id.weightBar);
        weightText = root.findViewById((R.id.WeightID));
        heightText = root.findViewById(R.id.HeightID);
        heightSeekBar = root.findViewById(R.id.heightBar);


        StepAppOpenHelper databaseOpenHelper = new StepAppOpenHelper(this.getContext());;
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();
        createProfile(weightText, heightText,weightSeekBar,heightSeekBar);
        setListener(weightSeekBar, weightText,database," KG", 0,StepAppOpenHelper.WEIGHT_KEY);
        setListener(heightSeekBar, heightText,database, " cm",1,StepAppOpenHelper.HEIGHT_KEY);


        return root;
    }





    private void setListener(SeekBar seekBar, final TextView textSetter, final SQLiteDatabase db,
                             final String metric, final int index, final String key){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking;
            private int progressChanged = 0;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // handle progress change
                progressChanged = seekBar.getProgress();
                textSetter.setText(Integer.toString(progressChanged)+ metric);

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textSetter.setText(Integer.toString(progressChanged)+metric);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textSetter.setText(Integer.toString(progressChanged)+ metric);
                ContentValues values = new ContentValues();
                values.put(key,progressChanged + metric);

                db.update(StepAppOpenHelper.PROFILE_NAME,values,null, null);
            }
        });
    }
    private void createProfile(TextView weight_text, TextView height_text, SeekBar weight_bar, SeekBar height_bar){
//        StepAppOpenHelper.deleteProfile(getContext());
        profile = StepAppOpenHelper.loadProfile(getContext());
        System.out.println(profile);
        if(!profile.isEmpty()) {
            weight_text.setText(profile.get(0));
            height_text.setText(profile.get(1));

            int weight_progress = Integer.parseInt(profile.get(0).replaceAll(" KG", ""));
            int height_progress = Integer.parseInt(profile.get(1).replaceAll(" cm", ""));
            weight_bar.setProgress(weight_progress);
            height_bar.setProgress(height_progress);
        }
    }



}