package com.kar.kfd.gov.kfdsurvey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.constants.Constants;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


/**
 * Modified by Sarath
 */
public class UploadedFormList extends AppCompatActivity {

    private Activity activity;
    private String formStatus = "2";
    private String title ="Uploaded Form List";
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploaded_form_list);
        activity = this;


        Button plantSampling = findViewById(R.id.plant_sampling);
        plantSampling.setOnClickListener(v -> {
            Intent intent = new Intent(activity, FormList.class);
            intent.putExtra("formType", Constants.FORMTYPE_PLANTSAMPLING);
            intent.putExtra("formStatus", formStatus);
            intent.putExtra("title", title);
            startActivity(intent);
        });
        Button sdpSampling = findViewById(R.id.sdp_sampling);
        sdpSampling.setOnClickListener(v -> {
            Intent intent = new Intent(activity, FormList.class);
            intent.putExtra("formStatus", formStatus);
            intent.putExtra("formType", Constants.FORMTYPE_SDP);
            intent.putExtra("title", Constants.UPLOAD_TITLE);
            startActivity(intent);
        });

        Button scptspSampling = findViewById(R.id.scptsp_sampling);
        scptspSampling.setOnClickListener(v -> {
            Intent intent = new Intent(activity, FormList.class);
            intent.putExtra("formStatus", formStatus);
            intent.putExtra("formType", Constants.FORMTYPE_SCPTSP);
            intent.putExtra("title", Constants.UPLOAD_TITLE);
            startActivity(intent);
        });

        Button otherworksSampling = findViewById(R.id.otherworks_sampling);
        otherworksSampling.setOnClickListener(v -> {
            Intent intent = new Intent(activity, FormList.class);
            intent.putExtra("formStatus", formStatus);
            intent.putExtra("formType", Constants.FORMTYPE_OTHERWORKS);
            intent.putExtra("title", Constants.UPLOAD_TITLE);
            startActivity(intent);
        });

    }
}