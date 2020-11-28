package com.example.stepapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stepapp.R;


import java.util.Map;

public class ProfileFragment extends Fragment {


    public String getWeightText() {
        return weightText.getText().toString();
    }


    public String getHeightText() {
        return heightText.getText().toString();
    }


    public EditText weightText;
    public EditText heightText;
    public SeekBar weightSeekBar;
    public SeekBar heightSeekBar;
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
        weightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking;
            private int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // handle progress change
                progressChanged = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mProgressAtStartTracking = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                weightText.setText(Integer.toString(progressChanged)+" KG");
            }
        });
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking;
            private int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // handle progress change
                progressChanged = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mProgressAtStartTracking = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                heightText.setText(Integer.toString(progressChanged)+" cm");
            }
        });
        return root;
    }



}
