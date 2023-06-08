package com.kar.kfd.gov.kfdsurvey.nursery;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sunil on 23-05-2017.
 */

public class AboutSeedBedSurvey extends HBaseFormActivity {

    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    public static final String ABOUT_SEED_BED_SURVEY = "AboutSeedBedSurvey";
    private SweetAlertDialog dialog;
    private String formStatus = "0";
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0, formFilledStatus = 0;
    public static DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

    }


    @Override
    protected HRootElement createRootElement() {

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ABOUT_SEED_BED_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        Database db = new Database(getApplicationContext());

        ArrayList<HSection> sections = new ArrayList<>();
        if(Integer.parseInt(pref.getString(Database.FORM_ID,"0"))!=0) {
            if(!db.getNamesOfSpecies().contains(pref.getString(Database.NAME_OF_THE_SPECIES,""))){
                pref.edit().putString(SPECIES_OTHER,pref.getString(Database.NAME_OF_THE_SPECIES,"")).apply();
                pref.edit().putString(Database.NAME_OF_THE_SPECIES,OTHERS_IF_ANY_SPECIFY).apply();
            }
        }

        formStatus = pref.getString("formStatus","0");

        HSection aboutSeedbedsSection = new HSection("About seed beds");

        HPickerElement schemeName = new HPickerElement(Database.NAME_OF_THE_SCHEME, "Name of the scheme/program", "Select scheme names", true, db.getNamesOfSchemeNew(), store);
        aboutSeedbedsSection.addEl(schemeName);

        HNumericElement numberOfBeds = new HNumericElement(Database.NUMBER_OF_BEDS,"No of beds","Enter No. of beds",true,store);
        aboutSeedbedsSection.addEl(numberOfBeds);

        HTextEntryElement speciesRaised = new HTextEntryElement(Database.SPECIES_RAISED,"Species raised","Enter Species raised",true,store );
        aboutSeedbedsSection.addEl(speciesRaised);

    /*    HMultiPickerElement mainSpeciesPlanted = new HMultiPickerElement(Database
                .SPECIES_RAISED, "Species raised", "Select species names",true,"Others|"+db.getNamesofSpeciesWithId(), store);
        aboutSeedbedsSection.addEl(mainSpeciesPlanted);
        if (mainSpeciesPlanted.getValue().isEmpty()) {
            mainSpeciesPlanted.setValue("Others");
            mainSpeciesPlanted.setSelectedIndex(0);
            store.saveValueToStore(Database.SPECIES_RAISED, "Others");
        }*/

        HPickerElement typeOfBeds = new HPickerElement(Database.TYPE_OF_BEDS,"Type of beds(Raised/Sunken)","Select Type of beds",true,"Raised|Sunken",store);
        aboutSeedbedsSection.addEl(typeOfBeds);

        HNumericElement saplingsPerBed = new HNumericElement(Database.SAPLINGS_PER_BED,"Seedlings per bed","Enter Saplings per bed",true,store);
        aboutSeedbedsSection.addEl(saplingsPerBed);

        HTextEntryElement bedSize = new HTextEntryElement(Database.BED_SIZE, "Bed Size", "Enter Bed Size", true, store);
        aboutSeedbedsSection.addEl(bedSize);

        HNumericElement averageHeightOfSprout = new HNumericElement(Database.AVERAGE_HEIGHT_OF_THE_SPROUTS,"Aveg. height of the sprouts","Enter Aveg. height of the sprouts",true,store);
        aboutSeedbedsSection.addEl(averageHeightOfSprout);

        HTextAreaEntryElement remarks = new HTextAreaEntryElement(Database.REMARKS, "Remarks", "Enter Remarks", true, store);
        aboutSeedbedsSection.addEl(remarks);

        final HButtonElement submit = new HButtonElement("Save");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showSaveFormDataAlert();
            else {
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
        if(!formStatus.equals("0")) {
            aboutSeedbedsSection.addEl(back);
            aboutSeedbedsSection.setNotEditable();
        }else {
            aboutSeedbedsSection.addEl(submit);
        }
        sections.add(aboutSeedbedsSection);

        return new HRootElement("Seedlings Form", sections);
    }

    private void submitSeedlingPerformance(){
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ABOUT_SEED_BED_SURVEY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.KFD_NURSERY_WORKS_SEED_BED, db);
            ContentValues cv = insertValuesToSeedlings(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            if (Integer.parseInt(pref.getString(Database.SEED_BED_ID, "0")) == 0) {
                db.insertIntoKfdNurseryWorkSeedBed(cv);
            } else {
                cv.put(Database.SEED_BED_ID, pref.getString(Database.SEED_BED_ID, "0"));
                db.updateTableWithId(Database.KFD_NURSERY_WORKS_SEED_BED, Database.SEED_BED_ID, cv);
            }
            pref.edit().clear().apply();
            setClearPref(true);
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());

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


    private ContentValues insertValuesToSeedlings(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db){
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
                    cv.put(columnName, Database.getTruncatedVarchar((pref.getString(columnName, "")),columnType));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else {
                cv.put(columnName, pref.getString(columnName, ""));
            }
        }
        /*long creationTimeStamp = System.currentTimeMillis() / 1000;
        cv.put(Database.CREATION_TIMESTAMP, creationTimeStamp);
        if(cv.get(Database.NAME_OF_THE_SPECIES).equals(OTHERS_IF_ANY_SPECIFY)){
            cv.put(Database.NAME_OF_THE_SPECIES,pref.getString(SPECIES_OTHER, ""));
        }*/
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
    public void onBackPressed() {
        SharedPreferences pref = getSharedPreferences(ABOUT_SEED_BED_SURVEY,MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if(pref.getString("formStatus","0").equals("0")){
                Log.d("FormStatus",pref.getString("formStatus","0"));
                //showSaveFormDataAlert();
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if(!pref.getString("formStatus","0").equals("0")){
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitSeedlingPerformance());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

