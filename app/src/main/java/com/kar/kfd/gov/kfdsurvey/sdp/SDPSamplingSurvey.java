package com.kar.kfd.gov.kfdsurvey.sdp;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.BuildConfig;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.location.AppSettingsFrag;
import com.kar.kfd.gov.kfdsurvey.service.FloatingWindow;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.store.HPrefDataStore;
import com.ngohung.form.util.GPSTracker;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_BENEFICIARY;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_BENEFICIARY_SEEDLING;
import static com.kar.kfd.gov.kfdsurvey.SeedlingsSurvey.SEEDLINGS_SURVEY;
import static com.kar.kfd.gov.kfdsurvey.sdp.SDPBeneficiarySurvey.BENEFICIARY_DETAILS;


public class SDPSamplingSurvey extends HBaseFormActivity {

    public static final String SDP_SURVEY = "SDP Survey";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private SweetAlertDialog dialog;
    private SDPSamplingSurvey mSurvey = this;
    Database db;
    private SurveyCreation surveyCreation;
    HTextEntryElement textElement;
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    private String formStatus = "0";
    AlertDialog alert;
    TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;
        surveyCreation = new SurveyCreation();

        Toolbar toolbar = findViewById(R.id.toolbar); // get the reference of Toolbar
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText(SDP_SURVEY);
        setSupportActionBar(toolbar);
    }




    @Override
    protected HRootElement createRootElement() {

        db = new Database(mSurvey.getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SDP_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);

        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }
        formStatus = pref.getString("formStatus", "0");


        ArrayList<HSection> sections = new ArrayList<>();

        HSection basicInformation = new HSection("I: Basic information of Seedlings Distribution to Public");

        HButtonElement locationDetails = new HButtonElement("A.Location Details");
        basicInformation.addEl(locationDetails);
        locationDetails.setOnClick(v -> {
            AppSettingsFrag appSettingsFrag = new AppSettingsFrag();
            Bundle bundle = new Bundle();
            bundle.putString("preference", SDP_SURVEY);
            appSettingsFrag.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, appSettingsFrag, "Home page");
            transaction.addToBackStack("Location");
            transaction.commit();
        });

        HTextEntryElement workCode = new HTextEntryElement(Database.WORK_CODE, "10.Work Code", "Enter location details to generate work " +
                "code", false, store);
        basicInformation.addEl(workCode);
        workCode.setNotEditable();


        HButtonElement beneficiaryDetails = new HButtonElement(" Beneficiary Details");
        basicInformation.addEl(beneficiaryDetails);
        beneficiaryDetails.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.BENEFICIARY_LIST);
            i.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.BENEFICIARY_STATUS, "1").apply();
            startActivity(i);
        });



        final HButtonElement submit = new HButtonElement("Save ");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);

            if (db.getFormFilledStatus(TABLE_BENEFICIARY, pref.getString(Database.FORM_ID, "0")))
                showSaveFormDataAlert();
            else
                submitSdpSamplingSurvey(0);
        });

        final HButtonElement approve = new HButtonElement("Approve ");
        approve.setElType(HElementType.SUBMIT_BUTTON);
        approve.setOnClick(v -> {
            approve.getButtonView().setFocusableInTouchMode(true);
            approve.getButtonView().requestFocus();
            approve.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields to approve");
            else if (pref.getString(Database.BENEFICIARY_STATUS, "0").equals("0"))
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Beneficiaries to approve");
            else if (db.getFormFilledStatus(TABLE_BENEFICIARY, pref.getString(Database.FORM_ID, "0")))
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Beneficifiaries");
            else if (db.getFormFilledStatus(TABLE_BENEFICIARY_SEEDLING, pref.getString(Database.FORM_ID, "0")))
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Seedling Details");
            else
                submitSdpSamplingSurvey(1);

        });

        final HButtonElement back = new HButtonElement("Close Form");
        back.setElType(HElementType.SUBMIT_BUTTON);
        back.setOnClick(v -> {
            back.getButtonView().setFocusableInTouchMode(true);
            back.getButtonView().requestFocus();
            back.getButtonView().setFocusableInTouchMode(false);
            onBackPressed();
        });
        if (!formStatus.equals("0")) {
            basicInformation.addEl(back);
            basicInformation.setNotEditable();
            basicInformation.setNotEditable();
        } else {
            basicInformation.addEl(submit);
            basicInformation.addEl(approve);
        }


        sections.add(basicInformation);

        return new HRootElement("Evaluation of seedling distribution to public - Form 2", sections);
    }

    private void submitSdpSamplingSurvey(final int flag) {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SDP_SURVEY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(flag == 0 ? R.layout.dialog_submit_form : R.layout.dialog_approve_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            /*stop service */
            stopService(new Intent(this, FloatingWindow.class));
            FloatingWindow.started = false;

            if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) == 0) {
                Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SURVEY_MASTER, db);
                ContentValues cv = insertValuesToSurveyMaster(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
                cv.put(Database.APP_ID, BuildConfig.VERSION_CODE);
                if (flag == 1) {
                    cv.put(Database.FORM_STATUS, 1);
                }
                long formId = db.insertIntoMaster(cv);
                tableMetadata = getTableMetaData(Database.TABLE_SDP, db);
                cv = insertValuesToSdpSamplingSurvey(formId, tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
                db.insertIntoSdpSampling(cv);
                File mediaStorageDir = surveyCreation.getPictureFolder(Constants.FORMTYPE_SDP);
                if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                    mediaStorageDir.renameTo(surveyCreation.getNewPictureFolder(formId, Constants.FORMTYPE_SDP));
                    mediaStorageDir.renameTo(surveyCreation.getNewPictureFolder(formId, Constants.FORMTYPE_SDP));
                }
                Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(TABLE_BENEFICIARY, Database.FORM_ID, formId))));
            } else {
                Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SURVEY_MASTER, db);
                ContentValues cv = insertValuesToSurveyMaster(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
                cv.put(Database.APP_ID, BuildConfig.VERSION_CODE);
                int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
                if (flag == 1) {
                    cv.put(Database.FORM_STATUS, 1);
                }
                db.updateSurveyMasterWithFormId(formId, cv);
                tableMetadata = getTableMetaData(Database.TABLE_SDP, db);
                cv = insertValuesToSdpSamplingSurvey(formId, tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
                cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
                db.updateTableWithFormId(Database.TABLE_SDP, formId, cv);
            }
            getSharedPreferences(SEEDLINGS_SURVEY, Context.MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(BENEFICIARY_DETAILS, Context.MODE_PRIVATE).edit().clear().apply();
            pref.edit().clear().apply();
            setClearPref(true);
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });

        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void showSaveFormDataAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitSdpSamplingSurvey(0));

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                        finish();
                    });

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close");

        }
        dialog.show();
    }


    private ContentValues insertValuesToSdpSamplingSurvey(long formId, ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
        ContentValues cv = new ContentValues();
        cv.put(Database.FORM_ID, String.valueOf(formId));
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
        cv.put(Database.FORM_TYPE, Constants.FORMTYPE_SDP);
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

        HashMap map = new HashMap();
        map.put("columnNamesList", columnNames);
        map.put("columnTypesList", columnTypes);

        return map;
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SDP_SURVEY, Context.MODE_PRIVATE);
        if (textElement != null) {
            textElement.getEditText().setText(String.valueOf(db.getNumberOfBeneficiaries(Integer.parseInt(pref.getString(Database.FORM_ID, "0")))));
        }
    }

    @Override
    public void onBackPressed() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SDP_SURVEY, Context.MODE_PRIVATE);

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (pref.getString("formStatus", "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if (!pref.getString("formStatus", "0").equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.takeNote:
                start_stop();
                break;
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
                serviceIntent.putExtra(Database.PREFERENCE, SDP_SURVEY);
                startService(serviceIntent);
                FloatingWindow.started = true;

            }
        } else {
            reqPermission();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
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

}
