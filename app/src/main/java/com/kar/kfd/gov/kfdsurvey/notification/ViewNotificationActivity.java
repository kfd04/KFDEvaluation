package com.kar.kfd.gov.kfdsurvey.notification;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.R;

public class ViewNotificationActivity extends AppCompatActivity {

    TextView tvTitle, tvMessage;
    String title, body, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnotification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTitle = findViewById(R.id.tvTitle);
        tvMessage = findViewById(R.id.tvMessage);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("title");
            body = extras.getString("body");
            url = extras.getString("url");
            //The key argument here must match that used in the other activity
        }
        tvTitle.setText(title);
        tvMessage.setText(body);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
