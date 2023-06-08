package com.kar.kfd.gov.kfdsurvey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import com.kar.kfd.gov.kfdsurvey.constants.Constants;

public class PermissionActivity extends AppCompatActivity {

    private Database db;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        db = new Database(this);
        Button i_agree_btn = findViewById(R.id.i_agree_btn);
        Button deny_btn = findViewById(R.id.deny_btn);
        TextView tt_content = findViewById(R.id.tt_content);

        tt_content.setText(Html.fromHtml("<pre>The App intends to capture the Sample plot &amp; activity logs of field staff and officers while on duty . To capture the same , the following permissions are required , Please note that the <br /><br />1.App would be functional only when you grant the below permissions.<br /><br />2.Your location details and logs will be captured <strong>only </strong>while using the app.<br /><br /><br />    <strong>1. LOCATION PERMISSION -<br /></strong><br />The app collects your current location whenever the app is running , both in foreground and background . <br />Please note that your location will not be used for any other purpose except for which the app is intended.<br /><br />    <strong>2. STORAGE PERMISSION -</strong> <br /><br />The app has feature of taking geo tagged photo's during sampling of plots activities. <br />Storage permission is required to store such images , save on your device storage.</pre>"));
        i_agree_btn.setOnClickListener(v -> {
            Intent loginIntent = new Intent(PermissionActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
        deny_btn.setOnClickListener(v -> {
            finish();
            finishAffinity();
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        preferences = getSharedPreferences(Constants.LOGIN_PREF, Context.MODE_PRIVATE);
        if (!db.getLoginDetails().isEmpty() || db.getLoginDetails().size() != 0) {
            startActivity(new Intent(PermissionActivity.this, SurveyActivity.class));
            finish();
        }
    }
}