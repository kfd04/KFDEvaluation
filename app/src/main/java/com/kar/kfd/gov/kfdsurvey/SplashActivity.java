package com.kar.kfd.gov.kfdsurvey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.network.NetworkDetector;
import com.kar.kfd.gov.kfdsurvey.utils.AttemptingUploadCheckList;

import org.json.JSONObject;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String SERVER_KEY = "update";
    Handler handler;
    TextView tvDepartmentName;
    View drawerLayout;
    boolean recommended_update, forced_update;
    int version_code;
    String url = Constants.SERVER_URL + SERVER_KEY;
    String playstoreurl;
    NetworkDetector networkDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvDepartmentName = findViewById(R.id.tvDepartmentName);
        tvDepartmentName.setText(getResources().getString(R.string.kfdl_logo_string));
        drawerLayout = findViewById(android.R.id.content);
        handler = new Handler();
        playstoreurl = getResources().getString(R.string.playsore_url);
        networkDetector = new NetworkDetector(this);
//        if (!networkDetector.detect()) {
//            Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
//            startActivity(intent);
//            finish();
//        } else{
//            Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
//            startActivity(intent);
//            finish();
//        }
            networkRequest();
    }

    private void networkRequest() {

        Thread thread = new Thread(() -> {
            try {
                String resp = AttemptingUploadCheckList.getJsonObj(url);
                JSONObject response = new JSONObject(resp);

                Log.e("wdcwcd",""+response);

                version_code = response.getInt("version_code");
                recommended_update = response.getBoolean("recommended_update");
                forced_update = response.getBoolean("forced_update");
                handler.postDelayed(() -> {
                    if (version_code > BuildConfig.VERSION_CODE && (recommended_update || forced_update)) {
                        if (recommended_update) {
                            recomendUpadate();
                        } else
                            onUpdateNeeded(playstoreurl);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);


            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        thread.start();

    }


    public void recomendUpadate() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue.")
                .setCancelable(false)

                .setPositiveButton("Update",
                        (dialog12, which) -> redirectStore(playstoreurl)).setNegativeButton("No, thanks",
                        (dialog1, which) -> {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }).create();
        dialog.show();

    }

    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue.")
                .setCancelable(false)
                .setPositiveButton("Update",
                        (dialog12, which) -> redirectStore(updateUrl)).setNegativeButton("No, thanks",
                        (dialog1, which) -> finish()).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
