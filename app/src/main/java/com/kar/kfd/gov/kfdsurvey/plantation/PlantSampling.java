package com.kar.kfd.gov.kfdsurvey.plantation;

import static com.kar.kfd.gov.kfdsurvey.Database.FORM_FILLED_STATUS;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_CONTROL_PLOT_INVENTORY;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_CONTROL_PLOT_MASTER;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_OTHER_SMC_LIST;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_PROTECTION;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_SAMPLE_PLOT_MASTER;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_VFC_SAMPLING;
import static com.kar.kfd.gov.kfdsurvey.plantation.Protection.PROTECTION_WORK_SURVEY;
import static com.kar.kfd.gov.kfdsurvey.plantation.VfcPlantationSampling.VFC_PLANTATION_SAMPLING;
import static com.kar.kfd.gov.kfdsurvey.plantation.smc.SmcPlantationSampling.SMC_PLANTATION_SAMPLING;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.BuildConfig;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.ViewSamplePlotMap;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.plantation.smc.SmcPlantationSampling;
import com.kar.kfd.gov.kfdsurvey.service.FloatingWindow;
import com.ngohung.form.util.GPSTracker;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


/**
 * Modified by Devansh
 */
public class PlantSampling extends AppCompatActivity {

    private Activity activity;
    public static final String PLANTSAMPLINGPREF = "PlantSamplingPreferences";
    public SharedPreferences plantSamplingPreferences;
    private SurveyCreation surveyCreation;
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    private String formStatus = "0";
    private String serverFormId = "0";
    public static DisplayMetrics metrics;
    private SweetAlertDialog dialog;
    private Database db;
    private int appStatus;
    AlertDialog alert;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantation_sampling);
        activity = this;
        surveyCreation = new SurveyCreation();
        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;
        db = new Database(this);
//        appStatus = db.getAppStatus();
        Button protection = findViewById(R.id.protection);
        formStatus = getSharedPreferences("Basic information", Context.MODE_PRIVATE).getString("formStatus", "0");
        serverFormId = getSharedPreferences("Basic information", Context.MODE_PRIVATE).getString("server_form_id", "0");//for showing map

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }
        Button vfcPlantationSurvey = findViewById(R.id.vfc);
        Button smcPlantationSurvey = findViewById(R.id.smc);
        vfcPlantationSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(activity, VfcPlantationSampling.class);
            pref.edit().putString(Database.VFC_STATUS, "1").apply();
            startActivity(intent);
        });
        smcPlantationSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SmcPlantationSampling.class);
            pref.edit().putString(Database.SMC_WORK_STATUS, "1").apply();
            startActivity(intent);
        });

     /*   Button plantationBasicInfo = findViewById(R.id.basicInfo);
        plantationBasicInfo.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PlantationSamplingBasicInfo.class);
            startActivity(intent);
        });*/

        Button plantationEvaluationDetails = findViewById(R.id.plantationEvalvuation);
        plantationEvaluationDetails.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PlantationSamplingEvaluation.class);
            pref.edit().putString(Database.PLANTING_ACTIVITY_STATUS, "1").apply();
            startActivity(intent);
        });

     /*   Button plantationAnnotation = findViewById(R.id.annotation);
        plantationAnnotation.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PlantationSamplingAnnotation.class);
            startActivity(intent);
        });*/

        Button submitPlantationDetails = findViewById(R.id.submitPlantationDetails);
        submitPlantationDetails.setOnClickListener(v -> checkFormVerify(0));

        Button approvePlantationDetails = findViewById(R.id.approvePlantationDetails);
        approvePlantationDetails.setOnClickListener(v -> checkFormVerify(1));

        Button closePlantationDetails = findViewById(R.id.closePlantationDetails);
        closePlantationDetails.setOnClickListener(v -> onBackPressed());
        Button viewSamplePlotMap = findViewById(R.id.viewSamplePlotMap);
        viewSamplePlotMap.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ViewSamplePlotMap.class);
            Log.d("SERVER_FORM_ID", serverFormId);
            if (appStatus == 1) {
                intent.putExtra("MODE", "Test");
            } else if (appStatus == 2) {
                intent.putExtra("MODE", "Prod");
            }
            intent.putExtra("SERVER_FORM_ID", serverFormId);
            startActivity(intent);
        });
        if (formStatus.equals("0")) {
            viewSamplePlotMap.setVisibility(View.GONE);
            closePlantationDetails.setVisibility(View.GONE);
        } else {
            submitPlantationDetails.setVisibility(View.GONE);
            approvePlantationDetails.setVisibility(View.GONE);
        }

        protection.setOnClickListener(v -> {
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            getSharedPreferences(PROTECTION_WORK_SURVEY, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(formId)).apply();
            Intent intent = new Intent(activity, SurveyList.class);
            intent.putExtra("id", formId);
            intent.putExtra("List-type", Constants.PLANT_PROTECTION);
            intent.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.BOUNDARY_STATUS, "1").apply();
            startActivity(intent);
        });

    }

    private void checkFormVerify(int flag) {
        SharedPreferences pref = activity.getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        SharedPreferences smcPref = activity.getSharedPreferences(SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        SharedPreferences vfcPref = activity.getSharedPreferences(VFC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        String formID = pref.getString(Database.FORM_ID, "0");
        if (flag == 0) {
            submitPlantationGeneralDetails(flag);
        } else {
            if (pref.getString(Database.PLANTING_ACTIVITY_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Planting Activity");
            } else if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_STATUS, "0")) == 0) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Please Complete sample Plot");
            } /*else if (db.getSeedlingFormFilledStatus(formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Inside SamplePlot Species");
            } */ else if (db.getFormFilledStatus(TABLE_SAMPLE_PLOT_MASTER, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in SamplePlot");
            } else if (db.getEmptyPitStatus(TABLE_SAMPLE_PLOT_MASTER, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Empty Pits in Sampleplot");
            } else if (pref.getString(FORM_FILLED_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Planting Activity");
            } else if ((pref.getString(Database.PLANTATION_MODEL, "").equals("ER Model-I(A)") || pref.getString(Database.PLANTATION_MODEL, "").equals("ANR Model-I(B)")) && pref.getString(Database.OUTSIDE_PLANTATION_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Please Complete Outside Plantation");
            } else if (db.getFormFilledStatus(TABLE_CONTROL_PLOT_MASTER, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Outside Plantation");
            } else if (db.getFormFilledStatus(TABLE_CONTROL_PLOT_INVENTORY, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Control Plot");
            } else if (pref.getString(Database.SMC_WORK_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete SMC Works");
            } else if (smcPref.getString(FORM_FILLED_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in SMC");
            } else if (db.getFormFilledStatus(TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in SMC Highest");
            } else if (db.getFormFilledStatus(TABLE_OTHER_SMC_LIST, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Other SMC");
            } else if (pref.getString(Database.VFC_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete VFC");
            } else if (vfcPref.getString(FORM_FILLED_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in VFC");
            } else if (pref.getString(Database.BOUNDARY_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Boundary Protection");
            } else if (db.getFormFilledStatus(TABLE_PROTECTION, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Protection");
            } else {
                submitPlantationGeneralDetails(flag);
            }
        }

    }

    private void submitSmcWorks(long formId) {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        Database db = new Database(this.getApplicationContext());
        if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) == 0) {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SMC_SAMPLING_MASTER, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            long smcId = db.insertIntoSmcSamplingMaster(cv);
            File mediaStorageDir = surveyCreation.getPictureFolder(SmcPlantationSampling.folderName);
            if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                mediaStorageDir.renameTo(surveyCreation.getNewPictureFolder(formId, SmcPlantationSampling.folderName));
            }
            Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(Database.TABLE_SMC_SAMPLING_DETAILS, Database.FORM_ID, formId))));
            Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(Database.TABLE_SMC_SAMPLING_DETAILS, Database.SMC_ID, smcId))));
            //--------------------------done by sunil--------------------------
            Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.FORM_ID, formId))));
            Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.SMC_ID, smcId))));
            //------------------------------------------------------------------
        } else {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SMC_SAMPLING_MASTER, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            db.updateTableWithFormId(Database.TABLE_SMC_SAMPLING_MASTER, (int) formId, cv);
        }
        pref.edit().clear().apply();
//        getSharedPreferences("SmcWorkSurvey", Context.MODE_PRIVATE).edit().clear().apply();
    }

    private void submitVfcWorks(long formId) {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(VFC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        Database db = new Database(this.getApplicationContext());
        if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) == 0) {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(TABLE_VFC_SAMPLING, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            db.insertIntoVfcSampling(cv);
        } else {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(TABLE_VFC_SAMPLING, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            db.updateTableWithFormId(TABLE_VFC_SAMPLING, (int) formId, cv);
        }
        pref.edit().clear().apply();
    }

    private void submitPlantationGeneralDetails(final int flag) {

        plantSamplingPreferences = getSharedPreferences(PLANTSAMPLINGPREF, MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        final SharedPreferences loginpref = getSharedPreferences(Constants.LOGIN_PREF, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(flag == 0 ? R.layout.dialog_submit_form : R.layout.dialog_approve_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            /*stop service */
            stopService(new Intent(this, FloatingWindow.class));
            FloatingWindow.started = false;

            long formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SURVEY_MASTER, db);
            ContentValues cv = insertValuesToSurveyMaster(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.APP_ID, BuildConfig.VERSION_CODE);
            if (flag == 1) {
                cv.put(Database.FORM_STATUS, 1);
            }
            db.updateSurveyMasterWithFormId((int) formId, cv);
            tableMetadata = getTableMetaData(Database.TABLE_PLANTATION, db);
            cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 1);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            cv.put(Database.SAMPLEPLOTS_PHOTOS_COUNT, String.valueOf(numberOfSamplePlotPhotos(String.valueOf(formId))));
            cv.put(Database.MAIN_SPECIES_PLANTED, getSpeciesNames(pref));
            cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));

            db.updateTableWithFormId(Database.TABLE_PLANTATION, (int) formId, cv);
            getSharedPreferences("PlotInventory", Context.MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(AddSpecies.SPECIES_PREF, Context.MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(ControlPlotInventory.CONTROL_PLOT_INVENTORY, Context.MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("SamplePlotDetails", Context.MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(PLANTSAMPLINGPREF, Context.MODE_PRIVATE).edit().clear().apply();
            pref.edit().clear().apply();


            submitSmcWorks(formId);
            submitVfcWorks(formId);
            File mediaStorageDir = surveyCreation.getNewPictureFolder(0, SamplePlotSurvey.folderName);
            if (mediaStorageDir.isDirectory()) {
                String[] children = mediaStorageDir.list();
                for (String child : children) {
                    new File(mediaStorageDir, child).delete();
                }
                mediaStorageDir.delete();
            }
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }


    private ContentValues insertValuesIntoContentValues(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, int startAtColumn) {
        ContentValues cv = new ContentValues();
        for (int i = startAtColumn; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            if (columnType.contains("INTEGER")) {
                try {
                    cv.put(columnName, Integer.parseInt(pref.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("float")) {
                try {
                    cv.put(columnName, Float.parseFloat(pref.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("varchar")) {
                try {
                    cv.put(columnName, Database.getTruncatedVarchar((pref.getString(columnName, "")), columnType));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else {
                cv.put(columnName, pref.getString(columnName, ""));
            }


        }
        return cv;
    }

    private ContentValues insertValuesToSurveyMaster(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
        long endingTimeStamp = System.currentTimeMillis() / 1000;
        ContentValues cv = new ContentValues();
        for (int i = 1; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            if (columnType.contains("INTEGER")) {
                try {
                    cv.put(columnName, Integer.parseInt(pref.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("float")) {
                try {
                    cv.put(columnName, Float.parseFloat(pref.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("varchar")) {
                try {
                    cv.put(columnName, Database.getTruncatedVarchar((pref.getString(columnName, "")), columnType));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else {
                cv.put(columnName, pref.getString(columnName, ""));
            }
        }
        cv.put(Database.STARTING_TIMESTAMP, Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")));
        cv.put(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING);
        cv.put(Database.ENDING_TIMESTAMP, endingTimeStamp);
        GPSTracker gpsTracker = new GPSTracker(this);
        cv.put(Database.AUTOMATIC_LATITUDE, gpsTracker.getLatitude());
        cv.put(Database.AUTOMATIC_LONGITUDE, gpsTracker.getLongitude());
        return cv;
    }

    private Map<String, ArrayList<String>> getTableMetaData(String tableName, Database db) {
        ArrayList<String> columnNames;
        ArrayList<String> columnTypes;
        try (Cursor cursor = db.getColumnNames(tableName)) {
            int nameIdx = cursor.getColumnIndexOrThrow("name");
            int typeIdx = cursor.getColumnIndexOrThrow("type");
            columnNames = new ArrayList<>();
            columnTypes = new ArrayList<>();
            while (cursor.moveToNext()) {

                columnNames.add(cursor.getString(nameIdx));
                columnTypes.add(cursor.getString(typeIdx));
            }

        }

        Map<String, ArrayList<String>> map = new HashMap();
        map.put("columnNamesList", columnNames);
        map.put("columnTypesList", columnTypes);

        return map;
    }

    private void showEventDialog(int type, String msg) {
        dialog = new SweetAlertDialog(this, type);

        if (type == SweetAlertDialog.ERROR_TYPE) {

            dialog.setTitleText("Oops...")
                    .setContentText(msg);

        } else if (type == SweetAlertDialog.PROGRESS_TYPE) {

            dialog.setTitleText(msg);

        } else if (type == SweetAlertDialog.SUCCESS_TYPE) {

            dialog.setTitleText(msg)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        // startActivity(new Intent(PlantSampling.this,SurveyActivity.class));
                        finish();
                    });

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close");

        }
        dialog.show();
    }

    private String getSpeciesNames(SharedPreferences pref) {
        String species = removeOtherSpeciesString(pref);
        String otherSpecies = getOtherSpecies(pref);
        if (species.length() > 0 && otherSpecies.length() > 0) {
            return species + "|" + otherSpecies;
        } else if (species.length() < 0) {
            return otherSpecies;
        } else {
            return species;
        }
    }

    private String getOtherSpecies(SharedPreferences pref) {
        String[] options = pref.getString(PlantationSamplingEvaluation.SPECIES_OTHER, "").split(",");
        StringBuilder builder = new StringBuilder();
        for (String s : options) {
            builder.append(s).append('|');
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    private String removeOtherSpeciesString(SharedPreferences pref) {
        String[] options = pref.getString(Database.MAIN_SPECIES_PLANTED, "").split("\\|");
        StringBuilder builder = new StringBuilder();
        for (String s : options) {
            if (!s.contains(PlantationSamplingEvaluation.OTHERS_IF_ANY_SPECIFY)) {
                builder.append(s).append('|');
            }
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        if (item.getItemId() == R.id.takeNote) {
            start_stop();
        }
        return super.onOptionsItemSelected(item);

    }

    public void start_stop() {
        if (checkPermission()) {
           /* if (FloatingWindow.started) {
                stopService(new Intent(this, FloatingWindow.class));
                FloatingWindow.started = false;
            } */
            if (!FloatingWindow.started) {
                Intent serviceIntent = new Intent(this, FloatingWindow.class);
                serviceIntent.putExtra(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
                startService(serviceIntent);
                FloatingWindow.started = true;

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

    @Override
    public void onBackPressed() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        //--------done by sunil for showing pop for saving----------
        if (pref.getString("formStatus", "0").equals("0")) {
            Log.d("FormStatus", pref.getString("formStatus", "0"));
            showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
        }
        //---------------------------------------------------------
        if (!pref.getString("formStatus", "0").equals("0")) {
            pref.edit().clear().apply();
            getSharedPreferences(SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(VFC_PLANTATION_SAMPLING, Context.MODE_PRIVATE).edit().clear().apply();
            super.onBackPressed();
        }
        if ((Integer.parseInt(pref.getString(Database.FORM_ID, "0")) != 0)) {
            Log.d("BackPress FormId!=0", "Executed");
            return;
        }

        //f0r disabling the back button
        if ((Integer.parseInt(pref.getString(Database.FORM_ID, "0")) == 0)) {
            Log.d("BackPress FormId==0", "Executed");
            //showSaveFormDataAlert();
            return;
        }

        super.onBackPressed();
    }

    private int numberOfSamplePlotPhotos(String formId) {
        Database db = new Database(getApplicationContext());
        int count = 0;
        SurveyCreation surveyCreation = new SurveyCreation();
        int[] ids = db.getSamplePlotIds(formId);
        for (int id : ids) {
            File pictureFolder = surveyCreation.getPictureFolder(SamplePlotSurvey.folderName, String.valueOf(id));
            if (pictureFolder.list() != null) {
                count = count + pictureFolder.list().length;
            }
        }
        return count;
    }


    public void showSaveFormDataAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Please Save Form-data by pressing 'SAVE'");
        alertDialog.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
