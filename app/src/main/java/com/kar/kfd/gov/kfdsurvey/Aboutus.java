package com.kar.kfd.gov.kfdsurvey;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Aboutus extends AppCompatActivity {

    TextView tvAppName, tvVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        tvAppName = findViewById(R.id.tvAppName);
        tvVersionName = findViewById(R.id.tvVersionName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        tvAppName.setText(getResources().getString(R.string.app_name));
        tvVersionName.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
