package com.kar.kfd.gov.kfdsurvey;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.store.HPrefDataStore;
import com.ngohung.form.el.validator.ValidationStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey.SCP_TSP_SAMPLING_SURVEY;
import static com.kar.kfd.gov.kfdsurvey.sdp.SDPSamplingSurvey.SDP_SURVEY;


public class SeedlingsSurvey extends HBaseFormActivity {

    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    public static final String SEEDLINGS_SURVEY = "SeedlingsSurvey";
    private SweetAlertDialog dialog;
    private String formStatus = "0";
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    private String speciesNamePref;
    public int formFilledStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

    }

    public void setToolBarTitle(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
    }



    @Override
    protected HRootElement createRootElement() {

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SEEDLINGS_SURVEY, Context.MODE_PRIVATE);
        SharedPreferences sdpPref = this.getApplicationContext().getSharedPreferences(SDP_SURVEY, Context.MODE_PRIVATE);
        SharedPreferences scpSamplingPref = getSharedPreferences(SCP_TSP_SAMPLING_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        Database db = new Database(getApplicationContext());
        speciesNamePref = pref.getString(Database.NAME_OF_THE_SPECIES, "");

        ArrayList<HSection> sections = new ArrayList<>();
        if (Integer.parseInt(pref.getString(Database.SEEDLING_ID, "0")) != 0) {
            if (!db.getNamesOfSdpSpecies().contains(pref.getString(Database.NAME_OF_THE_SPECIES, ""))) {
                pref.edit().putString(SPECIES_OTHER, pref.getString(Database.NAME_OF_THE_SPECIES, "")).apply();
                pref.edit().putString(Database.NAME_OF_THE_SPECIES, OTHERS_IF_ANY_SPECIFY).apply();
            }
        }

        formStatus = pref.getString("formStatus", "0");

        HSection performanceOfSeedlings = new HSection("Performance of seedlings");//db.getNamesOfSpeciesNew(basicInfoPref.getInt("model_id",-1))

        HTextEntryElement speciesName = new HTextEntryElement(Database.NAME_OF_THE_SPECIES, "Name of species:", "Select species names", false, store);
        performanceOfSeedlings.addEl(speciesName);
        speciesName.setValue(speciesNamePref);
        speciesName.setNotEditable();

       /* HTextEntryElement speciesOther = new HTextEntryElement(SPECIES_OTHER,"Name of species ( Other )","Enter species name",true,store);
        performanceOfSeedlings.addEl(speciesOther);*/

        // speciesName.addPosElement(speciesOther);

        HNumericElement noOfSeedlingsPlanted = new HNumericElement(Database.NUMBER_OF_SEEDLINGS_PLANTED, "Number of seedlings planted", "Enter number", true, store);
        performanceOfSeedlings.addEl(noOfSeedlingsPlanted);
        noOfSeedlingsPlanted.setDecimal(false);

        HNumericElement noOfSeedlingsSurviving = new HNumericElement(Database.NUMBER_OF_SEEDLINGS_SURVIVING, "Number of seedlings surviving", "Enter number", true, store);
        performanceOfSeedlings.addEl(noOfSeedlingsSurviving);
        noOfSeedlingsSurviving.setDecimal(false);

        noOfSeedlingsSurviving.addValidator(el -> {
            String planted = noOfSeedlingsPlanted.getValue().trim();
            String plantedSurviving = el.getValue().trim();

            if (!planted.equals("") && !plantedSurviving.equals("")) {

                if (Long.parseLong(el.getValue()) <= Long.parseLong(noOfSeedlingsPlanted.getValue()))
                    return new ValidationStatus(true);
                else {
                    el.setValue("");
                    return new ValidationStatus(false, "Value should be less than or equal to seedlings planted");
                }

            } else {
                el.setValue("");
                return new ValidationStatus(false, "Value should be less than or equal to seedlings planted");
            }


        });

        HNumericElement averageCollarGirth = new HNumericElement(Database.AVERAGE_COLLAR_GROWTH_CMS, "Average collar girth ( in centimeters )", "Enter collar girth ( in centimeters ) ", true, store);
        performanceOfSeedlings.addEl(averageCollarGirth);

        HNumericElement averageHeight = new HNumericElement(Database.AVERAGE_HEIGHT_METERS, "Average height ( in meter ) ", "Enter height ( in meter )", true, store);
        performanceOfSeedlings.addEl(averageHeight);

        HPickerElement healthAndVigor = new HPickerElement(Database.HEALTH_AND_VIGOUR, "Health " +
                "and vigour", "Select an option", true, "Very Good|Good|Average|Poor|Failure",
                store);
        performanceOfSeedlings.addEl(healthAndVigor);

        HPickerElement economicalValue = new HPickerElement(Database.ECONOMICAL_VALUE, "Species planted has Economical Value ?", "Select an option", true, "Yes|No", store);

        String typeOfBenefit = scpSamplingPref.getString(Database.TYPE_OF_BENEFIT, "");
        if (typeOfBenefit.equalsIgnoreCase("Social security plantation") || typeOfBenefit.equalsIgnoreCase("Fruit orchard plantation")) {
            performanceOfSeedlings.addEl(economicalValue);
        }

        HTextAreaEntryElement detailsOfReturns = new HTextAreaEntryElement(Database.DETAILS_OF_RETURNS, "Details of returns excepted in long run", "Enter Details", true, store);
        economicalValue.addPosElement(detailsOfReturns);
        performanceOfSeedlings.addEl(detailsOfReturns);

        HTextAreaEntryElement remarksSeedling = new HTextAreaEntryElement(Database.REMARKS, "Remarks", "Enter remarks", true, store);
        performanceOfSeedlings.addEl(remarksSeedling);

        final HButtonElement submit = new HButtonElement("Save");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData()) {
                showSaveFormDataAlert();
            } else {
                formFilledStatus = 1;
                submitSeedlingPerformance();
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
            performanceOfSeedlings.addEl(back);
            performanceOfSeedlings.setNotEditable();
        } else {
            performanceOfSeedlings.addEl(submit);
        }
        sections.add(performanceOfSeedlings);

        return new HRootElement("Seedlings Form", sections);
    }

    private void submitSeedlingPerformance() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SEEDLINGS_SURVEY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_BENEFICIARY_SEEDLING, db);
            ContentValues cv = insertValuesToSeedlings(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.PART_TYPE, pref.getString(Database.PART_TYPE, ""));
//            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            if (Integer.parseInt(pref.getString(Database.SEEDLING_ID, "0")) == 0) {
                Log.e("qsxAS","D");
                cv.put(Database.SPECIES_ID, pref.getInt(Database.SPECIES_ID, 0));
                Log.e("ASXAsX",""+cv);
                db.insertIntoSeedlings(cv);
            } else {
                Log.e("qsxAS","A");
                cv.put(Database.SEEDLING_ID, pref.getString(Database.SEEDLING_ID, "0"));
                db.updateTableWithId(Database.TABLE_BENEFICIARY_SEEDLING, Database.SEEDLING_ID, cv);
            }
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
                (arg0, arg1) -> submitSeedlingPerformance());

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


    private ContentValues insertValuesToSeedlings(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
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
        long creationTimeStamp = System.currentTimeMillis() / 1000;
        cv.put(Database.CREATION_TIMESTAMP, creationTimeStamp);
        if (cv.get(Database.NAME_OF_THE_SPECIES).equals(OTHERS_IF_ANY_SPECIFY)) {
            cv.put(Database.NAME_OF_THE_SPECIES, pref.getString(SPECIES_OTHER, ""));
        }
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
        SharedPreferences pref = getSharedPreferences(SEEDLINGS_SURVEY, MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (pref.getString("formStatus", "0").equals("0")) {
                // showSaveFormDataAlert();
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if (!pref.getString("formStatus", "0").equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }


    }
}
