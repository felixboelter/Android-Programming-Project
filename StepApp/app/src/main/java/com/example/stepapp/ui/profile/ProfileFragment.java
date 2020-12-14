package com.example.stepapp.ui.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.stepapp.MainActivity;
import com.example.stepapp.R;
import com.example.stepapp.StepAppSettingHelper;


import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    String SETTING_DB_NAME = "SETTING_DB";
    private SQLiteDatabase settingDB;
    private StepAppSettingHelper stepAppSettingHelper;

    String gender = "female";
    int age;
    int weight;
    int height;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        if (container != null) {
            container.removeAllViews();
        }

        stepAppSettingHelper = new StepAppSettingHelper(getContext(), SETTING_DB_NAME);
        settingDB = stepAppSettingHelper.getReadableDatabase();
        Cursor settingCursor = stepAppSettingHelper.getTheTableContent(settingDB);

        if (settingCursor.moveToFirst()){
            gender = settingCursor.getString(settingCursor.getColumnIndex("GENDER"));
            age = settingCursor.getInt(settingCursor.getColumnIndex("AGE"));
            weight = settingCursor.getInt(settingCursor.getColumnIndex("WEIGHT"));
            height = settingCursor.getInt(settingCursor.getColumnIndex("HEIGHT"));
            TextView genderTV = (TextView)root.findViewById(R.id.prof_gender_tv_id);
            genderTV.setText(gender);
            TextView ageTV = (TextView)root.findViewById(R.id.prof_age_tv_id);
            ageTV.setText(Integer.toString(age));
            TextView heightTV = (TextView)root.findViewById(R.id.prof_height_tv_id);
            heightTV.setText(Integer.toString(height));
            TextView weightTV = (TextView)root.findViewById(R.id.prof_weight_tv_id);
            weightTV.setText(Integer.toString(weight));
        }
        settingCursor.close();
        settingDB.close();

        Button setInfo = root.findViewById(R.id.set_info_btn);
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.setting_picker_layout);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(
                        getContext(),R.drawable.gray_bg));

                Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);
                Button setButton = (Button) dialog.findViewById(R.id.dialog_set_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        femaleTV.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.line_gray_bg));
                        maleTV.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                        othersTV.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                        gender = "female";
                    }
                });

                maleTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maleTV.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.line_gray_bg));
                        femaleTV.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                        othersTV.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                        gender = "male";
                    }
                });

                othersTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        othersTV.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.line_gray_bg));
                        femaleTV.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                        maleTV.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                        gender = "others";
                    }
                });
                setButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        age = agePicker.getValue();
                        weight = weightPicker.getValue();
                        height = heightPicker.getValue();

                        settingDB = stepAppSettingHelper.getReadableDatabase();
                        stepAppSettingHelper.deleteAll(settingDB);
                        settingDB.close();

                        settingDB = stepAppSettingHelper.getWritableDatabase();
                        stepAppSettingHelper.insertRow(settingDB,1, age, weight,
                                height, gender);
                        settingDB.close();

                        TextView genderTV = (TextView)root.findViewById(R.id.prof_gender_tv_id);
                        genderTV.setText(gender);
                        TextView ageTV = (TextView)root.findViewById(R.id.prof_age_tv_id);
                        ageTV.setText(Integer.toString(age));
                        TextView heightTV = (TextView)root.findViewById(R.id.prof_height_tv_id);
                        heightTV.setText(Integer.toString(height));
                        TextView weightTV = (TextView)root.findViewById(R.id.prof_weight_tv_id);
                        weightTV.setText(Integer.toString(weight));
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return root;
    }
}
