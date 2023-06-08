package com.kar.kfd.gov.kfdsurvey.advancework;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.BuildConfig;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.ViewSamplePlotMap;
import com.kar.kfd.gov.kfdsurvey.advancework.smc.SmcAdvanceWork;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.ngohung.form.util.GPSTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.kar.kfd.gov.kfdsurvey.Database.FORM_FILLED_STATUS;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_ADV_OTHER_SMC_LIST;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_ADV_PROTECTION;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_ADV_SAMPLE_PLOT_MASTER;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_ADV_SMC_HIGHEST;
import static com.kar.kfd.gov.kfdsurvey.advancework.AdvProtection.ADV_PROTECTION;
import static com.kar.kfd.gov.kfdsurvey.advancework.AdvSamplePlotSurvey.ADV_SAMPLE_PLOT_DETAILS;
import static com.kar.kfd.gov.kfdsurvey.advancework.PlantationSamplingAdvanceWork.ADVANCE_WORK_SURVEY;
import static com.kar.kfd.gov.kfdsurvey.advancework.VfcAdvacneWork.VFC_ADVACNE_WORK;
import static com.kar.kfd.gov.kfdsurvey.advancework.smc.SmcAdvanceWork.SMC_ADVANCE_WORK;

/**
 * Modified by Sarath
 */
public class PlantSamplingAdvaceWork extends AppCompatActivity {

    public static final String ADVANCEWORK_PREFERENCES = "AdvanceworkPreferences";
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    public SharedPreferences plantSamplingPreferences;
    private Activity activity;
    private SurveyCreation surveyCreation;
    private float dialogButtonFontSize;
    private String formStatus = "0";
    private String serverFormId = "0";
    private SweetAlertDialog dialog;
    private Database db;
    private int appStatus;

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
        formStatus = getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE).getString("formStatus", "0");
        serverFormId = getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE).getString("server_form_id", "0");//for showing map


        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }
        Button vfcPlantationSurvey = findViewById(R.id.vfc);
        Button smcPlantationSurvey = findViewById(R.id.smc);
        vfcPlantationSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(activity, VfcAdvacneWork.class);
            pref.edit().putString(Database.VFC_STATUS, "1").apply();
            startActivity(intent);
        });
        smcPlantationSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SmcAdvanceWork.class);
            pref.edit().putString(Database.SMC_WORK_STATUS, "1").apply();
            startActivity(intent);
        });

     /*   Button plantationBasicInfo = findViewById(R.id.basicInfo);
        plantationBasicInfo.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PlantationSamplingBasicInfo.class);
            startActivity(intent);
        });*/

        Button plantationEvaluationDetails = findViewById(R.id.plantationEvalvuation);
        plantationEvaluationDetails.setText(getResources().getString(R.string.earthwork_activity));
        plantationEvaluationDetails.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PlantationSamplingAdvanceWork.class);
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
            getSharedPreferences(ADV_PROTECTION, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(formId)).apply();
            Intent intent = new Intent(activity, SurveyList.class);
            intent.putExtra("id", formId);
            intent.putExtra("List-type", Constants.ADV_PLANT_PROTECTION);
            intent.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.BOUNDARY_STATUS, "1").apply();
            startActivity(intent);
        });

    }

    private void checkFormVerify(int flag) {
        SharedPreferences pref = activity.getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
        SharedPreferences smcPref = activity.getSharedPreferences(SMC_ADVANCE_WORK, Context.MODE_PRIVATE);
        SharedPreferences vfcPref = activity.getSharedPreferences(VFC_ADVACNE_WORK, Context.MODE_PRIVATE);
        String formID = pref.getString(Database.FORM_ID, "0");
        if (flag == 0) {
            submitAdvPlantationDetails(flag);
        } else {
            if (pref.getString(Database.PLANTING_ACTIVITY_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Planting Activity");
            } else if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_STATUS, "0")) == 0) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Please Complete sample Plot");
            } else if (db.getFormFilledStatus(TABLE_ADV_SAMPLE_PLOT_MASTER, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in SamplePlot");
            } else if (pref.getString(FORM_FILLED_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Earthwork");
            } else if (pref.getString(Database.SMC_WORK_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete SMC Works");
            } else if (smcPref.getString(FORM_FILLED_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in SMC");
            } else if (db.getFormFilledStatus(TABLE_ADV_SMC_HIGHEST, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in SMC Highest");
            } else if (db.getFormFilledStatus(TABLE_ADV_OTHER_SMC_LIST, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Other SMC");
            } else if (pref.getString(Database.VFC_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete VFC");
            } else if (vfcPref.getString(FORM_FILLED_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in VFC");
            } else if (pref.getString(Database.BOUNDARY_STATUS, "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Protection");
            } else if (db.getFormFilledStatus(TABLE_ADV_PROTECTION, formID)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Protection");
            } else {
                submitAdvPlantationDetails(flag);
            }
        }

    }

    private void submitSmcWorks(long formId) {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SMC_ADVANCE_WORK, Context.MODE_PRIVATE);
        Database db = new Database(this.getApplicationContext());
        if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) == 0) {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADV_SMC_MASTER, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            long smcId = db.insertIntoAdvSmcMaster(cv);
            File mediaStorageDir = surveyCreation.getPictureFolder(SmcAdvanceWork.folderName);
            if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                mediaStorageDir.renameTo(surveyCreation.getNewPictureFolder(formId, SmcAdvanceWork.folderName));
            }

            //--------------------------done by sunil--------------------------
            Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.FORM_ID, formId))));
            Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.SMC_ID, smcId))));
            //------------------------------------------------------------------
        } else {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADV_SMC_MASTER, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            db.updateTableWithFormId(Database.TABLE_ADV_SMC_MASTER, (int) formId, cv);
        }
        pref.edit().clear().apply();
//        getSharedPreferences("SmcWorkSurvey", Context.MODE_PRIVATE).edit().clear().apply();
    }

    private void submitVfcWorks(long formId) {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(VFC_ADVACNE_WORK, Context.MODE_PRIVATE);
        Database db = new Database(this.getApplicationContext());
        if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) == 0) {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADV_VFC_SAMPLING, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            db.insertIntoAdvVfcSampling(cv);
        } else {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADV_VFC_SAMPLING, db);
            ContentValues cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 2);
            cv.put(Database.FORM_ID, String.valueOf(formId));
            db.updateTableWithFormId(Database.TABLE_ADV_VFC_SAMPLING, (int) formId, cv);
        }
        pref.edit().clear().apply();
    }

    private void submitAdvPlantationDetails(final int flag) {

        plantSamplingPreferences = getSharedPreferences(ADVANCEWORK_PREFERENCES, MODE_PRIVATE);
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(flag == 0 ? R.layout.dialog_submit_form : R.layout.dialog_approve_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
                Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SURVEY_MASTER, db);
                ContentValues cv = insertValuesToSurveyMaster(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
                cv.put(Database.APP_ID, BuildConfig.VERSION_CODE);
                if (flag == 1) {
                    cv.put(Database.FORM_STATUS, 1);
                }
                Log.i("sarath", "onClick: " + cv.get("form_type"));
                db.updateSurveyMasterWithFormId((int) formId, cv);
                tableMetadata = getTableMetaData(Database.TABLE_ADVANCEWORK, db);
                cv = insertValuesIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, 1);
                cv.put(Database.FORM_ID, String.valueOf(formId));
                cv.put(Database.SAMPLEPLOTS_PHOTOS_COUNT, String.valueOf(numberOfSamplePlotPhotos(String.valueOf(formId))));
                cv.put(Database.MAIN_SPECIES_PLANTED, getSpeciesNames(pref));
                cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));

                db.updateTableWithFormId(Database.TABLE_ADVANCEWORK, (int) formId, cv);
//                getSharedPreferences("PlotInventory", Context.MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences(ADV_SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences(ADVANCEWORK_PREFERENCES, Context.MODE_PRIVATE).edit().clear().apply();
                pref.edit().clear().apply();


                submitSmcWorks(formId);
                submitVfcWorks(formId);
                File mediaStorageDir = surveyCreation.getNewPictureFolder(0, AdvSamplePlotSurvey.folderName);
                if (mediaStorageDir.isDirectory()) {
                    String[] children = mediaStorageDir.list();
                    for (String child : children) {
                        new File(mediaStorageDir, child).delete();
                    }
                    mediaStorageDir.delete();
                }
                showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
                alertDialog.dismiss();
            }
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

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
        cv.put(Database.FORM_TYPE, Constants.FORMTYPE_ADVANCEWORK);
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
        String[] options = pref.getString(PlantationSamplingAdvanceWork.SPECIES_OTHER, "").split(",");
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
            if (!s.contains(PlantationSamplingAdvanceWork.OTHERS_IF_ANY_SPECIFY)) {
                builder.append(s).append('|');
            }
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    @Override
    public void onBackPressed() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
        //--------done by sunil for showing pop for saving----------
        if (pref.getString("formStatus", "0").equals("0")) {
            Log.d("FormStatus", pref.getString("formStatus", "0"));
            showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
        }
        //---------------------------------------------------------
        if (!pref.getString("formStatus", "0").equals("0")) {
            pref.edit().clear().apply();
            getSharedPreferences(SMC_ADVANCE_WORK, Context.MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(VFC_ADVACNE_WORK, Context.MODE_PRIVATE).edit().clear().apply();
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
        int[] ids = db.getAdvSamplePlotIds(formId);
        for (int id : ids) {
            File pictureFolder = surveyCreation.getPictureFolder(AdvSamplePlotSurvey.folderName, String.valueOf(id));
            if (pictureFolder.list() != null) {
                count = count + pictureFolder.list().length;
            }
        }
        return count;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void showSaveFormDataAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Please Save Form-data by pressing 'SAVE'");
        alertDialog.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
