package com.kar.kfd.gov.kfdsurvey;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kar.kfd.gov.kfdsurvey.asynctask.ExportDatabase;
import com.kar.kfd.gov.kfdsurvey.asynctask.ImportDatabase;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.network.LoginSync;
import com.kar.kfd.gov.kfdsurvey.network.NetworkDetector;
import com.kar.kfd.gov.kfdsurvey.notification.NotificationActivity;
import com.kar.kfd.gov.kfdsurvey.service.FloatingWindow;
import com.kar.kfd.gov.kfdsurvey.utils.Analytics;
import com.kar.kfd.gov.kfdsurvey.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class SurveyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoginSync.ProgressListener {

    private static final String TAG = Constants.SARATH;
    private static final int REQUEST_LOCATION = 1000;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    private FragmentManager fragmentManager;
    private Database db;
    SharedPreferences sharedPreferences;
    NetworkDetector networkDetector;
    private Toolbar toolbar;
    FirebaseFirestore firestore;
    AlertDialog alert;
    boolean started = false;
    // AlertDialog alertDialogSurvey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_creation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getpermission();

        fragmentManager = getSupportFragmentManager();


        SurveyCreation surveyCreation = new SurveyCreation();
        callFragment(surveyCreation, false);

        db = new Database(this.getBaseContext());
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(settings);

        sharedPreferences = getSharedPreferences(Constants.LOGIN_PREF, Context.MODE_PRIVATE);
        networkDetector = new NetworkDetector(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_user);

        View headerView = navigationView.getHeaderView(0);
        ImageView drawerImage = headerView.findViewById(R.id.imageView);
        TextView drawerUsername = headerView.findViewById(R.id.tvUserName);
        drawerImage.setImageDrawable(myDrawable);
        String username = sharedPreferences.getString(Database.EVALUATOR_NAME, "");
        drawerUsername.setText(username);
        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        FirebaseCrashlytics.getInstance().setUserId(username);
        FirebaseAnalytics.getInstance(this).setUserProperty("user_name", username);
        if (sharedPreferences.getString("tapTouch", "").isEmpty())
            toolbar.postDelayed(this::showSequence, 1000);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String token = instanceIdResult.getToken();
            String id = instanceIdResult.getId();
            String usertoken = sharedPreferences.getString("usertoken", "");
            if (usertoken.isEmpty()) {
                writeDataFireStore(id, username, token);
                sharedPreferences.edit().putString("usertoken", token).apply();
            }

        });

    }

    private void get_location_check() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(SurveyActivity.this)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setCancelable(false)
                    .setPositiveButton(R.string.open_setting, (paramDialogInterface, paramInt) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .show();
        }
    }

    private void getpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_LOCATION);
        }
    }

    private void writeDataFireStore(String id, String username, String token) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", username);
        user.put("token", token);
        user.put("time_stamp", new Timestamp(new Date()));
        firestore.collection("user_token").add(user)
                .addOnSuccessListener(documentReference -> Log.d(Constants.SARATH, "DocumentSnapshot path " + documentReference.getPath()))
                .addOnFailureListener(e -> Log.w(Constants.SARATH, "Error adding document", e));
    }


    private void showSequence() {
        new MaterialTapTargetSequence()
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(findViewById(R.id.plantSamplingButton))
                        .setPrimaryText("Step 1")
                        .setSecondaryText("Complete Plantation Forms and Save")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(findViewById(R.id.sdpSamplingButton))
                        .setPrimaryText("Step 2")
                        .setSecondaryText("Complete SDP Forms and Save")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(findViewById(R.id.otherworksSampling))
                        .setPrimaryText("Step 3")
                        .setSecondaryText("Complete OtherWorks and Save")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(findViewById(R.id.scptspSampling))
                        .setPrimaryText("Step 4")
                        .setSecondaryText("Complete SCPTSP and Save")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(findViewById(R.id.advanceWorkButton))
                        .setPrimaryText("Step 5")
                        .setSecondaryText("Complete AdvanceWork and Save")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(findViewById(R.id.nurseryWorkButton))
                        .setPrimaryText("Step 6")
                        .setSecondaryText("Complete NurseryWork and Save")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(toolbar.getChildAt(1))
                        .setPrimaryText("Step 7")
                        .setSecondaryText("Edit,Sync,Upload,Logout")
                        .setAnimationInterpolator(new FastOutSlowInInterpolator())
                        .setMaxTextWidth(R.dimen.tap_target_menu_max_width)
                        .setIcon(R.drawable.ic_menu)
                        .setFocalPadding(R.dimen.dp40)
                        .create())

                .show().setSequenceCompleteListener(() -> sharedPreferences.edit().putString("tapTouch", "1").apply());


    }


    private void callFragment(Fragment fragment, boolean addToBackStack) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, "Home page");
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 9999:
                if (data != null) {
                    Uri uri = data.getData();
                    String path = FileUtil.getFullPathFromTreeUri(uri, this);
                    new ExportDatabase(this).execute(path);
                }
                break;
            case 8888:
                if (data != null) {
//                    Uri uri = data.getData();
//                    String path = FileUtil.getFullPathFromTreeUri(uri, this);
//                    new ImportDatabase(this).execute(path);
                    Uri fileUri = data.getData();
                    if (fileUri != null) {
                        replaceDatabase(fileUri);
                    }

                }

                break;
        }
    }
    private void replaceDatabase(Uri fileUri) {
        String filename = getFileName(fileUri);
        if (filename == null) {
            Toast.makeText(this, "Failed to get the file name", Toast.LENGTH_SHORT).show();
            return;
        }

        File destination = getDatabasePath("kfd_survey");
        File tempFile = new File(destination.getParent(), filename);

        try {
            copyFile(fileUri, tempFile);
            if (tempFile.renameTo(destination)) {
                Toast.makeText(this, "Database replaced successfully", Toast.LENGTH_SHORT).show();
                // You may need to re-open your database connection here
            } else {
                Toast.makeText(this, "Failed to replace the database", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error replacing the database: " + e.getMessage());
            Toast.makeText(this, "Error replacing the database", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri fileUri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }

    private void copyFile(Uri sourceUri, File destination) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(sourceUri);
        OutputStream outputStream = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent;
        if (id == R.id.mHome) {
            SurveyCreation surveyCreation = new SurveyCreation();
            callFragment(surveyCreation, false);
        } else if (id == R.id.mEdit_plantation) {
            intent = new Intent(getBaseContext(), FormList.class);
            intent.putExtra("formType", Constants.FORMTYPE_PLANTSAMPLING);
            intent.putExtra("title", "Saved Forms");
            startActivity(intent);

        } else if (id == R.id.mEdit_sdp) {
            intent = new Intent(getBaseContext(), FormList.class);
            intent.putExtra("formType", Constants.FORMTYPE_SDP);
            intent.putExtra("title", "Saved Forms");
            startActivity(intent);

        } else if (id == R.id.mEdit_otherworks) {
            intent = new Intent(getBaseContext(), FormList.class);
            intent.putExtra("formType", Constants.FORMTYPE_OTHERWORKS);
            intent.putExtra("title", "Saved Forms");
            startActivity(intent);

        } else if (id == R.id.mEdit_scptsp) {
            intent = new Intent(getBaseContext(), FormList.class);
            intent.putExtra("formType", Constants.FORMTYPE_SCPTSP);
            intent.putExtra("title", "Saved Forms");
            startActivity(intent);

        } else if (id == R.id.mEdit_advancework) {
            intent = new Intent(getBaseContext(), FormList.class);
            intent.putExtra("formType", Constants.FORMTYPE_ADVANCEWORK);
            intent.putExtra("title", "Saved Forms");
            startActivity(intent);

        } else if (id == R.id.mEdit_nurseryworks) {
            intent = new Intent(getBaseContext(), FormList.class);
            intent.putExtra("formType", Constants.FORMTYPE_NURSERY_WORK);
            intent.putExtra("title", "Saved Forms");
            startActivity(intent);

        } else if (id == R.id.mSync) {
            if (!networkDetector.detect()) {
                Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            } else
                sync();

        } else if (id == R.id.mUpload) {
            SurveyStats surveyStats = new SurveyStats();
            callFragment(surveyStats, true);

        } else if (id == R.id.mExport) {
            if (Build.VERSION.SDK_INT < 21)
                new ExportDatabase(this).execute("");
            else {
                Intent directoryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                directoryIntent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(directoryIntent, "Choose Directory"), 9999);
            }

        } /*else if (id == R.id.mWindow) {
            start_stop();

        }*/ else if (id == R.id.mLogout) {
            showAlert();
        } else if (id == R.id.mImport) {
//            String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
//            new ImportDatabase().execute(dir);
//            Intent filePickerintent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//            startActivityForResult(filePickerintent, 8888);
            Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent1.setType("*/*");  // Set the MIME type to allow all file types
            intent1.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent1, 8888);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void start_stop() {
        if (checkPermission()) {
            if (started) {
                stopService(new Intent(this, FloatingWindow.class));
                started = false;
            } else {
                startService(new Intent(this, FloatingWindow.class));
                started = true;

            }
        } else {
            reqPermission();
        }

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                reqPermission();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

    private void reqPermission() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Screen overlay detected");
        alertBuilder.setMessage("Enable 'Draw over other apps' in your system setting.");
        alertBuilder.setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, RESULT_OK);
            }
        });

        alert = alertBuilder.create();
        alert.show();


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void sync() {
        Toast.makeText(this, "Syncing", Toast.LENGTH_SHORT).show();
        try {
            LoginSync sync = new LoginSync(SurveyActivity.this, this);
            sync.sendData(db.getLoginDetails());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle(Html.fromHtml("<b><font color='red'>" + getString(R.string.warning) + "</b>"));
        alertDialog.setMessage("Survey Data will be Erased");
        alertDialog.setPositiveButton("Ok", (dialog, which) -> validateCredentials());
        alertDialog.setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss());
        alertDialog.show();
    }

    private void validateCredentials() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = getLayoutInflater().inflate(R.layout.dialog_upload_user_credentials, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        final EditText userName = customDialogLayout.findViewById(R.id.uploadUserIdET);
        final EditText password = customDialogLayout.findViewById(R.id.uploadPwdET);
        alertDialogBuilder.setPositiveButton("Erase", (dialog, which) -> {
            ArrayList<String> data = db.getLoginDetails();
            if (userName.getText().toString().trim().equals(data.get(0)) && password.getText().toString().trim().equals(data.get(1))) {
                db.deleteAllTestSurveys();
                sharedPreferences.edit().clear().apply();
                Intent login = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(login);
                Toast.makeText(this, "Data Sucessfully Erased.", Toast.LENGTH_LONG).show();
                finish();
                Analytics.track(Analytics.AnalyticsEvents.USER_LOGOUT);

            } else {
                Toast.makeText(this, "Invalid credentials. Please enter valid credentials", Toast.LENGTH_LONG).show();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(15);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(15);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_surveyactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_aboutus) {
            Intent aboutIntent = new Intent(this, Aboutus.class);
            startActivity(aboutIntent);
        }

        if (id == R.id.menu_notification) {
            Intent notificationIntent = new Intent(this, NotificationActivity.class);
            startActivity(notificationIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void onStart() {
        super.onStart();
        get_location_check();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //   Toast.makeText(HomeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                showDialogOK(
                        (dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ActivityCompat.requestPermissions(SurveyActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                            REQUEST_LOCATION);
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    finish();
                                    // proceed with logic by disabling the related features or quit the app.
                                    break;
                            }
                        });
            }
            return;
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("Without this permission the app is unable to find your location. Are you sure you want to deny this permission?")
                .setPositiveButton("Open Settings", okListener)
                .setNegativeButton("Cancel", okListener)
                .setCancelable(false)
                .create()
                .show();
    }
}

