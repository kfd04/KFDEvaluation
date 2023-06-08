package com.kar.kfd.gov.kfdsurvey.advancework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.advancework.smc.SmcAdvanceWork;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class FieldDataCollection extends AppCompatActivity {

    private Activity activity;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_data_collection);
        activity = this;

        Button fieldDataCollection = findViewById(R.id.fdc);
        Button vfcPlantationSurvey = findViewById(R.id.vfc);
        Button smcPlantationSurvey = findViewById(R.id.smc);
        Button protection = findViewById(R.id.protection);

        smcPlantationSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SmcAdvanceWork.class);
            startActivity(intent);
        });

        fieldDataCollection.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PlantationSamplingEvaluation.class);
            startActivity(intent);
        });

        vfcPlantationSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(activity, VfcAdvacneWork.class);
            startActivity(intent);
        });

        protection.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AdvProtection.class);
            startActivity(intent);
        });
    }
}
