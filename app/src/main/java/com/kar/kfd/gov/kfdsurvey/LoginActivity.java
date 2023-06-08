package com.kar.kfd.gov.kfdsurvey;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.network.LoginSync;
import com.kar.kfd.gov.kfdsurvey.network.NetworkDetector;
import com.ngohung.form.util.GPSTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, LoginSync.ProgressListener {


    private static final int PERMISSION_REQUEST_CODE = 1011;
    EditText et_UserName, et_Password;
    Button btn_login;
    String userName, password;
    NetworkDetector networkDetector;
    SweetAlertDialog dialog;
    ArrayList<String> data = new ArrayList<>();
    SharedPreferences preferences;
    Database db;
    View progressBar;
    Spinner spYear;
    private String item;
    View parentLayout;
    GPSTracker gpsTracker;

    GoogleMap map;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new Database(this);
        networkDetector = new NetworkDetector(this);
        et_UserName = findViewById(R.id.et_UserName);
        et_Password = findViewById(R.id.et_Password);
        btn_login = findViewById(R.id.btn_login);
        dialog = new SweetAlertDialog(this);
        progressBar = findViewById(R.id.progress_bar);
        spYear = findViewById(R.id.spYear);
        spYear.setOnItemSelectedListener(this);
        parentLayout = findViewById(android.R.id.content);
        gpsTracker = new GPSTracker(this);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String time = format.format(new Date());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(adapter);

        btn_login.setOnClickListener(this);

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(String.valueOf(gpsTracker.getLatitude()));
        list.add(String.valueOf(gpsTracker.getLongitude()));
        list.add(BuildConfig.VERSION_NAME);
        list.add(String.valueOf(BuildConfig.VERSION_CODE));
        list.add(time);
        db.insertAppSettingsTable(list);


        mapFragment.getMapAsync(googleMap -> map = googleMap);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

    }


    private void initilizeMap() {
        if (map == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.map));

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        preferences = getSharedPreferences(Constants.LOGIN_PREF, Context.MODE_PRIVATE);
        userName = preferences.getString(Database.EVALUATOR_NAME, "");

        if (!db.getLoginDetails().isEmpty() || db.getLoginDetails().size() != 0) {
            startActivity(new Intent(LoginActivity.this, SurveyActivity.class));
            finish();
        }
        checkPermisision();
    }

    public void checkPermisision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()), ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager
                .PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                boolean locationDen = false;
                boolean storageDen = false;
                boolean locationDen_Back = false;
                final ArrayList<String> newRequesrPer = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)
                            && grantResult == PackageManager.PERMISSION_DENIED) {
                        locationDen = true;
                        newRequesrPer.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && grantResult == PackageManager.PERMISSION_DENIED) {
                        storageDen = true;
                        newRequesrPer.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    if (permission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            && grantResult == PackageManager.PERMISSION_DENIED) {
                        locationDen_Back = true;
                        newRequesrPer.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    }
                }
                if (locationDen || storageDen || locationDen_Back) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ) {
                        String text = "Kindly allow all the permissions for  App  for" +
                                " a better  experience.";
                        if (parentLayout != null) {
                            Snackbar snackbar = Snackbar
                                    .make(parentLayout, text, Snackbar.LENGTH_LONG)
                                    .setDuration(20000)
                                    .setAction("OK", view -> requestPermissions(
                                            newRequesrPer.toArray(new String[]{}),
                                            PERMISSION_REQUEST_CODE));
                            View sbView = snackbar.getView();
                            snackbar.show();
                        } else {
                            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                        }
                    } else {

                        Snackbar snackbar = Snackbar.make(Objects.requireNonNull(parentLayout), "Kindly allow all the permissions for  App  for a better  experience...", Snackbar.LENGTH_LONG);
                        snackbar.setAction("settings", v -> {
                            Intent intent = new Intent();
                            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        });
                        snackbar.setDuration(20000);
                        snackbar.show();
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                userName = et_UserName.getText().toString().trim();
                password = et_Password.getText().toString().trim();
               /* userName = "user1-p1";
                password = "user111";
                item = "2018-19";*/
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(LoginActivity.this, "Please Enter UserName", Toast.LENGTH_SHORT).show();
                    et_UserName.setError("Please Enter UserName");
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else if (!networkDetector.detect()) {
                    showEventDialog(SweetAlertDialog.ERROR_TYPE, "Internet not connected!");
                } else if (item.startsWith("Select")) {
                    Toast.makeText(this, "Please Select Year", Toast.LENGTH_SHORT).show();
                } else {
                    data.clear();
                    data.add(userName);
                    data.add(password);
                    data.add(item);
                    try {
                        LoginSync sync = new LoginSync(LoginActivity.this, this);
                        sync.sendData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void showEventDialog(int type, final String msg) {
        dialog = new SweetAlertDialog(this, type);

        if (type == SweetAlertDialog.ERROR_TYPE) {
            dialog.setTitleText("Oops...").setContentText(msg);
        } else if (type == SweetAlertDialog.PROGRESS_TYPE) {

            dialog.setTitleText(msg);

        } else if (type == SweetAlertDialog.SUCCESS_TYPE) {

            dialog.setTitleText(msg)
                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close");

        }
        dialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    protected void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
    }

    protected void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void show() {
        showProgress();
    }

    @Override
    public void hide() {
        hideProgress();
    }
}
