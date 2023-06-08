package com.kar.kfd.gov.kfdsurvey.plantation;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.advancework.AdvProtection;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.utils.ImageUtil;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.store.HPrefDataStore;
import com.ngohung.form.util.GPSTracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.SurveyCreation.getTableMetaData;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

public class Protection extends HBaseFormActivity {
    private static final int TAKE_PICTURE_REQUEST = 101;
    private String staus_new = "";
    public static final String PROTECTION_WORK_SURVEY = "Protection";
    public static final String WAS_WORK_COMPLETE = "was_work_complete";
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    HTextEntryElement workCode;
    private GoogleApiClient googleApiClient;
    private SweetAlertDialog dialog;
    private float dialogButtonFontSize;
    private boolean editable = true;
    private String formStatus = "0";
    private boolean addMode = false;
    private Database db;
    int formFilledStatus = 0;
    private HTextElement tt_end, tt_first, tt_mid;
    private File imageFile;
    private Location gpsLocation;
    static ArrayList<String> exifAttributes = new ArrayList<>();
    String[] attributes = new String[]
            {
                    ExifInterface.TAG_DATETIME,
                    ExifInterface.TAG_DATETIME_DIGITIZED,
                    ExifInterface.TAG_EXPOSURE_TIME,
                    ExifInterface.TAG_FLASH,
                    ExifInterface.TAG_FOCAL_LENGTH,
                    ExifInterface.TAG_GPS_ALTITUDE,
                    ExifInterface.TAG_GPS_ALTITUDE_REF,
                    ExifInterface.TAG_GPS_DATESTAMP,
                    ExifInterface.TAG_GPS_LATITUDE,
                    ExifInterface.TAG_GPS_LATITUDE_REF,
                    ExifInterface.TAG_GPS_LONGITUDE,
                    ExifInterface.TAG_GPS_LONGITUDE_REF,
                    ExifInterface.TAG_GPS_PROCESSING_METHOD,
                    ExifInterface.TAG_GPS_TIMESTAMP,
                    ExifInterface.TAG_MAKE,
                    ExifInterface.TAG_MODEL,
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.TAG_SUBSEC_TIME,
                    ExifInterface.TAG_WHITE_BALANCE
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Evaluation of AdvProtection measures");

        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

    }


    @Override
    protected HRootElement createRootElement() {

        final SharedPreferences protectionPref = this.getApplicationContext().getSharedPreferences(PROTECTION_WORK_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(protectionPref);

        db = new Database(getApplicationContext());
        formStatus = protectionPref.getString("formStatus", "0");
        String workCodeString = protectionPref.getString(Database.WORK_CODE, "");
        if (!workCodeString.equals("")) {
            if (workCodeString.length() - 4 > 0) {
                protectionPref.edit().putString("sample_number", workCodeString.substring(workCodeString.lastIndexOf("-") + 1)).apply();

            }
        }

        editable = Integer.parseInt(protectionPref.getString(Database.FORM_ID, "0")) == 0;

        if ( TextUtils.isEmpty(protectionPref.getString(Database.PROTECTION_MEASURE_AVAILABILITY,""))){
            protectionPref.edit().putString(Database.PROTECTION_MEASURE_AVAILABILITY,"Yes").apply();
        }
        ArrayList<HSection> sections = new ArrayList<>();

        HSection protectSection = new HSection("Basic Information");
        protectSection.setVisible(true);

        HPickerElement protectionAvailability = new HPickerElement(Database.PROTECTION_MEASURE_AVAILABILITY, "Does this Boundary Protection Measure Exist in the Field ?", "Select an Option", true, "Yes|No", store);
        protectSection.addEl(protectionAvailability);

        HSection workLocationSection = new HSection("Observations");

        HPickerElement typeOfProtection = new HPickerElement(Database.TYPE_OF_PROTECTION, "Type of protection provided", "Selec type of work", true, "Chain Link Mesh|Solar fencing|Brush wood|CPT|Barbed wire fence with stone pillars|Barbed wire fence with wooden posts|Barbed wire fence with cement posts|Tree guards|Stone wall|Compound wall|Others", store);
        workLocationSection.addEl(typeOfProtection);

        HTextAreaEntryElement otherTypeOfProtection = new HTextAreaEntryElement(Database.OTHER_TYPE_OF_PROTECTION, "Specify Others", "Specify other protection", true, store);
        typeOfProtection.addElementForValue(otherTypeOfProtection, 10);
        workLocationSection.addEl(otherTypeOfProtection);

        HNumericElement boundaryCost = new HNumericElement(Database.BOUNDARY_COST, "Expenditure Incurred(Rs)", "Enter Expenditure Incurred", true, store);
        workLocationSection.addEl(boundaryCost);

        HNumericElement totalLengthOfSelectedSample = new HNumericElement(Database.TOTAL_LENGTH_KMS, " What is the length (mt)", "Enter length in mt", true, store);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 0);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 1);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 2);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 3);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 4);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 5);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 6);


        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 7);
        typeOfProtection.addElementForValue(totalLengthOfSelectedSample, 10);
        workLocationSection.addEl(totalLengthOfSelectedSample);



        HNumericElement breadth = new HNumericElement(Database.BREADTH_OF_CPT, "What is the breadth (mt)", "Enter breadth in mt", true, store);
        typeOfProtection.addElementForValue(breadth, 3);
        typeOfProtection.addElementForValue(breadth, 10);
        typeOfProtection.addElementForValue(breadth, 7);
        workLocationSection.addEl(breadth);

        HNumericElement height = new HNumericElement(Database.TOTAL_HEIGHT, "What is the height (mt)", "Enter height in mt", true, store);
        typeOfProtection.addElementForValue(height, 0);
        typeOfProtection.addElementForValue(height, 1);
        typeOfProtection.addElementForValue(height, 2);
        typeOfProtection.addElementForValue(height, 4);
        typeOfProtection.addElementForValue(height, 5);
        typeOfProtection.addElementForValue(height, 6);


        typeOfProtection.addElementForValue(height, 8);
        typeOfProtection.addElementForValue(height, 9);
        workLocationSection.addEl(height);

        HNumericElement depth = new HNumericElement(Database.DEPTH_OF_CPT, "What is the depth (mt)", "Enter depth in mt", true, store);
        typeOfProtection.addElementForValue(depth, 3);
        typeOfProtection.addElementForValue(depth, 3);
        workLocationSection.addEl(depth);

        HPickerElement pbviseSize = new HPickerElement(Database.CPT_SIZE, "Size", "Select an option", true,
                "3 X 3 |4 X 4|5 X 5", store);
        typeOfProtection.addElementForValue(pbviseSize, 0);
        workLocationSection.addEl(pbviseSize);

        HNumericElement noOFStrands = new HNumericElement(Database.NO_OF_STRANDS, "No Of Strands (mt)", "Enter Strands", true, store);
        typeOfProtection.addElementForValue(noOFStrands, 4);
        typeOfProtection.addElementForValue(noOFStrands, 5);
        typeOfProtection.addElementForValue(noOFStrands, 6);

        typeOfProtection.addElementForValue(noOFStrands, 1);
        workLocationSection.addEl(noOFStrands);

        HTextElement observation = new HTextElement("Observations");
        workLocationSection.addEl(observation);

        HPickerElement condition = new HPickerElement(Database.PRESENT_CONDITION, "Present Condition", "select condition", true, "Rusted|Breached|Good", store);
        typeOfProtection.addElementForValue(condition, 0);
        workLocationSection.addEl(condition);


        HSection observations = new HSection("observation");


       /* HNumericElement overallWorkingQualityGrade = new HNumericElement(Database.EFFECTIVENESS_OF_CLM, " What is the effectiveness on 1-10 point scale? (1=worst, 10= excellent)", "Enter points", true, store);
        overallWorkingQualityGrade.setMaxLength(2);
        overallWorkingQualityGrade.addValidator(new HValidatorListener() {
            @Override
            public ValidationStatus isValid(HElement el) {
                if (Integer.parseInt(el.getValue()) <= 10 && Integer.parseInt(el.getValue()) > 0) {
                    return new ValidationStatus(true);
                } else
                    return new ValidationStatus(false,"value should be between 0 and 10");
            }
        });*/


        HPickerElement solarFence = new HPickerElement(Database.PRESENT_CONDITION, " Solar Fence Condition", "Select Solar Fence Condition", true, "Functional|Breached", store);
        typeOfProtection.addElementForValue(solarFence, 1);
        workLocationSection.addEl(solarFence);

        HTextEntryElement brushMaterialsUsed = new HTextEntryElement(Database.BRUSHWOOD_MATERIALS_USED, "Materials used", "Specify Materials used", true, store);
        typeOfProtection.addElementForValue(brushMaterialsUsed, 2);
        workLocationSection.addEl(brushMaterialsUsed);

   /*     HTextEntryElement noOFWatchers = new HTextEntryElement(Database.NO_OF_WATCHERS,"Enter number of watchers","Specify Watchers",false,store);
        typeOfProtection.addElementForValue(noOFWatchers,3);
        workLocationSection.addEl(noOFWatchers);*/

        HPickerElement cptcondition = new HPickerElement(Database.PRESENT_CONDITION, "CPT Condition", "Select CPT condition", true, "Good|Breached/filled|Poor", store);
        typeOfProtection.addElementForValue(cptcondition, 3);
        workLocationSection.addEl(cptcondition);

        HPickerElement mountsowing = new HPickerElement(Database.MOUNT_SOWING, "Mound Sowing", "Select Mound Sowing done or not", true, "Yes|No", store);
        typeOfProtection.addElementForValue(mountsowing, 3);
        workLocationSection.addEl(mountsowing);

        HTextAreaEntryElement resultOfMountSowing = new HTextAreaEntryElement(Database.MOUNT_SOWING_RESULT, "Mound Sowing Result", "Specify Mount Sowing Result", true, store);
//        typeOfProtection.addElementForValue(resultOfMountSowing, 3);
        mountsowing.addPosElement(resultOfMountSowing);
        workLocationSection.addEl(resultOfMountSowing);


        HPickerElement materialsUsed = new HPickerElement(Database.MATERIALS_USED, "Materials Used", "Select Materials Used", true, "Wooden|Metal|Bamboo|Split Bamboo|Others", store);
        typeOfProtection.addElementForValue(materialsUsed, 7);
        workLocationSection.addEl(materialsUsed);

        /*HPickerElement differenceRNE = new HPickerElement(Database.DIFFERENCE_R_E, "Is there any diference between the recorded and executed work", "Select option", true, "yes|No", store);
        typeOfProtection.addElementForValue(differenceRNE, 0);
        workLocationSection.addEl(differenceRNE);

        HTextAreaEntryElement detailsRNE = new HTextAreaEntryElement(Database.DETAILS_R_E, "Details", "Enter Details", true, store);
        differenceRNE.addPosElement(detailsRNE);
        workLocationSection.addEl(detailsRNE);*/

        HTextEntryElement otherTreeguards = new HTextEntryElement(Database.OTHER_TREE_GUARDS, " Other Tree Guards", "Specify Others", true);
        materialsUsed.addElementForValue(otherTreeguards, 4);
        materialsUsed.addElementForValue(otherTreeguards, 5);
        materialsUsed.addElementForValue(otherTreeguards, 6);
        workLocationSection.addEl(otherTreeguards);

        HPickerElement treeCondition = new HPickerElement(Database.TREE_CONDITION, "Present Condition", "select condition", true, "Good|Average|Poor", store);
        typeOfProtection.addElementForValue(treeCondition, 10);
        workLocationSection.addEl(treeCondition);

        HPickerElement barberedCondition = new HPickerElement(Database.BARBED_WIRE_CONDITION, " Barbed Wire Condition", "Select Barbed Wire Condition", true, "Rusted|Breached|Good", store);
        typeOfProtection.addElementForValue(barberedCondition, 4);
        typeOfProtection.addElementForValue(barberedCondition, 5);
        typeOfProtection.addElementForValue(barberedCondition, 6);
        workLocationSection.addEl(barberedCondition);

        HPickerElement stoneWallcondtion = new HPickerElement(Database.PRESENT_CONDITION, "Stone Wall Condition", "Select Stonewall Condition", true, "Good|Breached", store);
        typeOfProtection.addElementForValue(stoneWallcondtion, 8);
        workLocationSection.addEl(stoneWallcondtion);

        HPickerElement compoundWallcondtion = new HPickerElement(Database.PRESENT_CONDITION, "Compound Wall Condition", "Select Compound Condition", true, "Good|Breached", store);
        typeOfProtection.addElementForValue(compoundWallcondtion, 9);
        workLocationSection.addEl(compoundWallcondtion);


        HPickerElement overallWorkingQualityGrade = new HPickerElement(Database.EFFECTIVENESS_OF_CLM, "Is the structure effective?", "Specify ", true, "Yes|No", store);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 0);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 1);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 2);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 3);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 4);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 5);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 6);

        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 7);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 8);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 9);
        typeOfProtection.addElementForValue(overallWorkingQualityGrade, 10);
        workLocationSection.addEl(overallWorkingQualityGrade);

        HTextAreaEntryElement effectivenessReasons = new HTextAreaEntryElement(Database.EFFECTIVENESS_OF_CLM_REASONS, "Details", "Enter Details", true, store);
        overallWorkingQualityGrade.addNegElement(effectivenessReasons);
        workLocationSection.addEl(effectivenessReasons);



        HSection saveProtection = new HSection("");

        final HButtonElement save = new HButtonElement("Save");
        save.setElType(HElementType.SUBMIT_BUTTON);
        saveProtection.addEl(save);
        save.setOnClick(v -> {
            save.getButtonView().setFocusableInTouchMode(true);
            save.getButtonView().requestFocus();
            save.getButtonView().setFocusableInTouchMode(false);

            if (!checkFormData())
                showSaveFormDataAlert();
            else {
                formFilledStatus = 1;
                submitProtection();
            }


        });

        protectionAvailability.addPosSection(workLocationSection);

        sections.add(protectSection);
        sections.add(workLocationSection);
        sections.add(observations);
        sections.add(saveProtection);
        workLocationSection.disableSubElementClear();
        return new HRootElement("Protection ", sections);
    }

    private void submitProtection() {
        final SharedPreferences protectionPref = this.getApplicationContext().getSharedPreferences(PROTECTION_WORK_SURVEY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_PROTECTION, db);
            ContentValues cv = insertProtection(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), protectionPref, db);
            cv.put(Database.FINISHED_POSITION, protectionPref.getInt(Database.FINISHED_POSITION, 0));
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            if (Integer.parseInt(protectionPref.getString(Database.PROTECTION_ID, "0")) == 0) {
                db.insertProtection(cv);
            } else {
                cv.put(Database.PROTECTION_ID, protectionPref.getString(Database.PROTECTION_ID, "0"));
                db.updateTableWithId(Database.TABLE_PROTECTION, Database.PROTECTION_ID, cv);
            }
            protectionPref.edit().clear().apply();
            setClearPref(true);
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private ContentValues insertProtection(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences protectionPref, Database db) {
        ContentValues cv = new ContentValues();
        for (int i = 1; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            if (columnType.contains("INTEGER")) {
                try {
                    cv.put(columnName, Integer.parseInt(protectionPref.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("float")) {
                try {
                    cv.put(columnName, Float.parseFloat(protectionPref.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("varchar")) {
                try {
                    cv.put(columnName, Database.getTruncatedVarchar((protectionPref.getString(columnName, "")), columnType));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else {
                cv.put(columnName, protectionPref.getString(columnName, ""));
            }
        }
        long creationTimeStamp = System.currentTimeMillis() / 1000;
        cv.put(Database.CREATION_TIMESTAMP, creationTimeStamp);

        return cv;

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

    @Override
    public void onBackPressed() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PROTECTION_WORK_SURVEY, Context.MODE_PRIVATE);

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (preferences.getString("formStatus", "0").equals("0")) {
                //showSaveFormDataAlert();
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }

            if (!preferences.getString("formStatus", "0").equals("0")) {
                preferences.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }


    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitProtection());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
