package com.kar.kfd.gov.kfdsurvey.advancework.smc;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HGpsElement;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.HTextView;
import com.ngohung.form.el.store.HPrefDataStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Sunil on 02-02-2017.
 */
public class AdvSmcHighest extends HBaseFormActivity {

    public static final String INSPECT_TWO_SMC_WORK = "AdvSmcHighest";
    public static final String folderName = "Advancework" + File.separator + "SMC Survey";
    public static final String smcTitle1 = "Evaluation of SMC number 1";
    public static final String smcTitle2 = "Evaluation of SMC number 2";
    String smcTitle;
    SharedPreferences pref;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private SweetAlertDialog dialog;
    private String formStatus = "0";
    private AdvSmcHighest mSurvey = this;
    private Database db;
    int formFilledStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.


    }

    @Override
    protected HRootElement createRootElement() {

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(INSPECT_TWO_SMC_WORK, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);

        ArrayList<HSection> sections = new ArrayList<>();
        formStatus = pref.getString("formStatus", "0");
        pref.edit().putString("total_volume", pref.getString(Database.SMC_STRUCTURE_TOTALVOLUME, "0")).apply();
        smcTitle = pref.getString("smcTitle", "0");
        HSection smcWorkDetails = new HSection(smcTitle);

        HTextEntryElement typeOfStructure = new HTextEntryElement(Database.TYPE_OF_STRUCTURE, "Type of structure", "Enter type of structure", false, store);
        smcWorkDetails.addEl(typeOfStructure);
        typeOfStructure.setNotEditable();


        final HNumericElement amount = new HNumericElement(Database.SMC_STRUCTURE_COST, "Expenditure Incurred(in Rupees)", "Enter Expenditure Incurred", false, store);
        smcWorkDetails.addEl(amount);
        amount.setNotEditable();


        HGpsElement gpsElement = new HGpsElement("Get GPS Location", true);
        if (formStatus.equals("0")) {
            smcWorkDetails.addEl(gpsElement);
        }

        HTextEntryElement latitudeEl = new HTextEntryElement(Database.GPS_LATITUDE, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);
        smcWorkDetails.addEl(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.GPS_LONGITUDE, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);
        smcWorkDetails.addEl(longitudeEl);

        HTextEntryElement altitudeEl = new HTextEntryElement(Database.GPS_ALTITUDE, "Altitude", "Click on the Gps button to get location", true, store);
        altitudeEl.setNotEditable();
        gpsElement.setAltitude(altitudeEl);
        smcWorkDetails.addEl(altitudeEl);
        // ----just for saving the creation timestamp of location coordinates ----
        HTextEntryElement timestampEl = new HTextEntryElement(Database.GPS_COORDINATE_CREATION_TIMESTAMP, "", "", true, store);
        gpsElement.setCreationTimeStamp(timestampEl);
        //------------------------------------------------------------------------

        HTextView workDimensions = new HTextView("Dimensions of the structure");
        smcWorkDetails.addEl(workDimensions);

        final HNumericElement workLength = new HNumericElement(Database.SMC_STRUCTURE_LENGTH, "Length ( in meter ) :", "Enter length ( in meter ) ", true, store);
        smcWorkDetails.addEl(workLength);

        final HNumericElement workBreadth = new HNumericElement(Database.SMC_STRUCTURE_BREADTH, "Breadth ( in meter ) :", "Enter breadth ( in meter ) ", true, store);
        smcWorkDetails.addEl(workBreadth);

        final HNumericElement workDepth = new HNumericElement(Database.SMC_STRUCTURE_DEPTH, "Height/depth ( in meter ) :", "Enter depth ( in meter ) ", true, store);
        smcWorkDetails.addEl(workDepth);

        final HButtonElement calculateTotalVolume = new HButtonElement("Calculate volume");
        if (formStatus.equals("0")) {
            smcWorkDetails.addEl(calculateTotalVolume);
        }

        final HTextEntryElement totalVolume = new HTextEntryElement(Database
                .SMC_STRUCTURE_TOTALVOLUME, "Total volume ( in cubic meter ) :", "Click the " +
                "button to calculate total volume", true, store);
        totalVolume.setNotEditable();
        smcWorkDetails.addEl(totalVolume);

        calculateTotalVolume.setOnClick(v -> {
            calculateTotalVolume.getButtonView().setFocusableInTouchMode(true);
            calculateTotalVolume.getButtonView().requestFocus();
            calculateTotalVolume.getButtonView().setFocusableInTouchMode(false);
            float length, breadth, depth;
            try {
                length = Float.parseFloat(workLength.getValue());
            } catch (NumberFormatException ex) {
                length = 0;
            }
            try {
                breadth = Float.parseFloat(workBreadth.getValue());
            } catch (NumberFormatException ex) {
                breadth = 0;
            }
            try {
                depth = Float.parseFloat(workDepth.getValue());
            } catch (NumberFormatException ex) {
                depth = 0;
            }

            float volume = length * breadth * depth;
            totalVolume.setValue(String.valueOf(volume));
            totalVolume.getEditText().setText(String.valueOf(volume));
        });


        HPickerElement differenceInWork = new HPickerElement(Database.WORK_DIFFERENCE, "Is there " +
                "any difference between executed & recorded smc work", "Select an option",
                true, "Yes|No", store);
        smcWorkDetails.addEl(differenceInWork);

        HTextAreaEntryElement workDifferenceDetails = new HTextAreaEntryElement(Database.DETAILS_OF_DIFF_BTWN_BILLED_AND_ACUTAL_WORK, "Details", "Enter the details of the difference between executed and recorded work", true, store);
        differenceInWork.addPosElement(workDifferenceDetails);
        smcWorkDetails.addEl(workDifferenceDetails);

        HPickerElement locationAppropriate = new HPickerElement(Database.IS_LOCATION_APPROPRIATE,
                "Is the smc structure built across the slope?", "Select an option", true, "Yes|No", store);
        smcWorkDetails.addEl(locationAppropriate);

        HTextAreaEntryElement locationAppropriateNo = new HTextAreaEntryElement(Database
                .IS_LOCATION_APPROPRIATE_REASON, "Please record your reason for evaluating it as " +
                "inappropriate", "Specify reason ",
                true,
                store);
        locationAppropriate.addNegElement(locationAppropriateNo);
        smcWorkDetails.addEl(locationAppropriateNo);


        HPickerElement constructionQuality = new HPickerElement(Database.CONSTRUCTION_QUALITY, "Construction Quality", "Select an option", false, "Good|Satisfactory|Poor|Failure", store);
        smcWorkDetails.addEl(constructionQuality);

        HTextAreaEntryElement remarks = new HTextAreaEntryElement(Database.REMARK, "Remarks",
                "Enter if any remarks", true, store);
        smcWorkDetails.addEl(remarks);

        HPickerElement purpose = new HPickerElement(Database.IS_SMC_SERVING_THE_PURPOSE,
                " Is there any indication of water collection?", "Select an option", true, "Yes|No", store);
        smcWorkDetails.addEl(purpose);

        HTextAreaEntryElement purposeNo = new HTextAreaEntryElement(Database.IS_SMC_SERVING_THE_PURPOSE_NO, "Details", "Specify Details", true, store);
        purpose.addNegElement(purposeNo);
        smcWorkDetails.addEl(purposeNo);


        HPickerElement journal = new HPickerElement(Database.IS_SELECT_PROPER, "Does the " +
                "measurements of the smc work match with the recorded value in Journal/FNB ?",
                "Select an option", true,
                "Yes|No", store);

        smcWorkDetails.addEl(journal);


        HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName);
            bundle.putString("formId", pref.getString(Database.SMC_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        smcWorkDetails.addEl(viewPhoto);


        final HButtonElement submit = new HButtonElement("Save");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        //  generalObservations.addEl(submit);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showSaveFormDataAlert();
            else {
                formFilledStatus = 1;
                submitSmcWorkDetails();
            }


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
            smcWorkDetails.addEl(back);
            smcWorkDetails.setNotEditable();

        } else {
            smcWorkDetails.addEl(submit);
        }
        sections.add(smcWorkDetails);

        return new HRootElement("Smc Work Form", sections);
    }

    private void submitSmcWorkDetails() {
        pref = this.getApplicationContext().getSharedPreferences(INSPECT_TWO_SMC_WORK, Context.MODE_PRIVATE);
        db = new Database(this.getApplicationContext());
        Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADV_SMC_HIGHEST, db);
        ContentValues cv = insertValuesToSmcWorkDetails(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
 /*       if(Integer.parseInt(pref.getString(Database.SMC_WORK_ID,"0"))==0) {
            Log.i("UPDATE", (String.valueOf(db.insertIntoInspectedSmcWorkDetails(cv))));
        }else {
            cv.put(Database.SMC_WORK_ID,Integer.parseInt(pref.getString(Database.SMC_WORK_ID,"0")));
            db.updateTableWithId(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST,Database.SMC_WORK_ID,cv);
        }*/
        cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
        cv.put(Database.SMC_ID, Integer.parseInt(pref.getString(Database.SMC_ID, "0")));
        db.updateTableWithId(Database.TABLE_ADV_SMC_HIGHEST, Database.SMC_ID, cv);
        pref.edit().clear().apply();
        setClearPref(true);
        showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
        //finish();//commented this because we have to show success pop first then close the the activity
    }


    private ContentValues insertValuesToSmcWorkDetails(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
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
            if (columnName.equals(Database.SMC_STRUCTURE_TOTALVOLUME)) {
                float depth = cv.getAsFloat(Database.SMC_STRUCTURE_DEPTH);
                float length = cv.getAsFloat(Database.SMC_STRUCTURE_LENGTH);
                float width = cv.getAsFloat(Database.SMC_STRUCTURE_BREADTH);
                float volume = depth * length * width;
                cv.put(columnName, volume);
            }
        }
        long creationTimeStamp = System.currentTimeMillis() / 1000;
        cv.put(Database.CREATION_TIMESTAMP, creationTimeStamp);
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


    @Override
    public void onBackPressed() {
        SharedPreferences pref = getSharedPreferences(INSPECT_TWO_SMC_WORK, MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (pref.getString("formStatus", "0").equals("0")) {
                Log.d("FormStatus", pref.getString("formStatus", "0"));
                // showSaveFormDataAlert();
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

        setToolBarTitle(getResources().getString(R.string.actionbar_title_other_works));

    }

    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitSmcWorkDetails());

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

                        if (smcTitle.equals(smcTitle1))
                            callSMC2();

                        finish();
                    });

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close");

        }
        dialog.show();
    }

    private void callSMC2() {
        SharedPreferences SMCpref = this.getApplicationContext().getSharedPreferences(SmcAdvanceWork.SMC_ADVANCE_WORK, Context.MODE_PRIVATE);
        int formId = Integer.parseInt(SMCpref.getString(Database.FORM_ID, "0"));
        Database db = new Database(mSurvey.getApplicationContext());
        //Cursor cursor = db.getTableForId(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST,Database.SMC_ID,2);
        Cursor cursor = db.getAdvSmcNumber2(formId);
        if (cursor != null && cursor.moveToFirst()) {
            SharedPreferences pref = getSharedPreferences(INSPECT_TWO_SMC_WORK, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor;
            pref.edit().clear().apply();
            editor = pref.edit();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                editor.putString(cursor.getColumnName(i), cursor.getString(i));
            }
            editor.putString("formStatus", formStatus);
            editor.putString("smcTitle", smcTitle2);
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), AdvSmcHighest.class);
            intent.putExtra("id", Integer.parseInt(pref.getString(Database.SMC_ID, "0")));
            intent.putExtra("List-type", Constants.INSPECT_TWO_SMC_WORKS);
            intent.putExtra("formStatus", formStatus);
            startActivity(intent);
        }
        assert cursor != null;
        cursor.close();
    }
}

