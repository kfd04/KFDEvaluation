package com.kar.kfd.gov.kfdsurvey.plantation;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.store.HPrefDataStore;
import com.ngohung.form.el.validator.ValidationStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.Database.SPECIES_AVAILABILITY;
import static com.kar.kfd.gov.kfdsurvey.Database.SPECIES_NAME;


public class PlotInventory extends HBaseFormActivity {

    public static final String TAG = PlotInventory.class.getSimpleName();
    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    public static final String PLOT_INVENTORY = "PlotInventory";
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    private SweetAlertDialog dialog;
    private float dialogButtonFontSize;
    private String formStatus = "0";
    private int formFilledStatus = 0;
    private String speciesName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.


        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

    }

    @Override
    protected HRootElement createRootElement() {

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(PLOT_INVENTORY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        Database db = new Database(getApplicationContext());
        formStatus = pref.getString("formStatus", "0");
        speciesName = pref.getString(Database.SPECIES_NAME, "");
        String inventoryType = pref.getString(Database.PART_TYPE, "");
        ArrayList<HSection> sections = new ArrayList<>();
        setToolBarTitle(inventoryType);
        /*Log.d(TAG, "createRootElement: "+basicInfoPref.getInt("model_id",-1));
        if (Integer.parseInt(pref.getString(Database.INVENTORY_ID, "0")) != 0) {
            if (!(OTHERS_IF_ANY_SPECIFY + "|" + db.getNamesOfSpeciesNew(basicInfoPref.getInt("model_id", -1))).contains(pref.getString(SPECIES_NAME, ""))) {
                pref.edit().putString(SPECIES_OTHER, pref.getString(SPECIES_NAME, "")).apply();
                pref.edit().putString(SPECIES_NAME, OTHERS_IF_ANY_SPECIFY).apply();
            }
        }*/

        if (TextUtils.isEmpty(pref.getString(SPECIES_AVAILABILITY, ""))) {
            pref.edit().putString(Database.SPECIES_AVAILABILITY, "Yes").apply();
        }

        HSection speciesSection = new HSection("Inventory of sample plot ".concat(String.valueOf(db.getSamplePlotNumberForFormId(Integer.parseInt(pref.getString(Database.FORM_ID, "0")), Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0"))))));
        speciesSection.setVisible(true);

        HPickerElement speciesAvailability = new HPickerElement(Database.SPECIES_AVAILABILITY, "Does this Species exist in the sample plot ?", "Select an Option", true, "Yes|No", store);
        speciesSection.addEl(speciesAvailability);


        HSection plotInventory = new HSection("Observations");


        HTextEntryElement species = new HTextEntryElement(SPECIES_NAME, "Species Name ", "species name", false, store);
        species.setValue(speciesName);
        species.setNotEditable();


        HNumericElement averageGbh = new HNumericElement(Database.AVERAGE_GBH_METERS, "Average GBH ( in centimeters ) ", "Enter average GBH ( in centimeters ) ", true, store);

        HNumericElement averageGirth = new HNumericElement(Database.AVERAGE_COLLAR_GIRTH, "Average collar girth ( in " +
                "centimeters ) ", "Enter average collar girth ( in centimeters ) ", true, store);

        HNumericElement averageHeight = new HNumericElement(Database.AVERAGE_HEIGHT_METERS, "Average height ( in meters)", "Enter average height ( in meters ) ", true, store);

        HNumericElement totalNo = new HNumericElement(Database.TOTAL_COUNT, "Total No. of seedling planted in the sample plot", "Enter total no.", true, store);
        totalNo.setDecimal(true);

        HNumericElement totalNoSurvived = new HNumericElement(Database.TOTAL_COUNT_SURVIVED, "Total No. of seedlings " +
                "surviving in the plot", "Enter total no.", true, store);
        totalNoSurvived.setDecimal(false);

        totalNoSurvived.addValidator(el -> {
            String plantedSurviving = el.getValue().trim();
            if (!plantedSurviving.equals("0") && !plantedSurviving.isEmpty())
                return new ValidationStatus(true);
            else
                return new ValidationStatus(false, "Surviving Seedling should not be Zero or Empty");
        });

        HNumericElement dibbledtotalNo = new HNumericElement(Database.TOTAL_COUNT, "Total No. of seeds dibbled in the sample plot", "Enter total no.", true, store);
        totalNo.setDecimal(false);

        HNumericElement dibbledtotalNoSurvived = new HNumericElement(Database.TOTAL_COUNT_SURVIVED, "Total No. of  dibbled seeds " +
                "surviving in the plot", "Enter total no.", true, store);
        totalNoSurvived.setDecimal(false);

      /*  HNumericElement averageOfSeedlings = new HNumericElement(Database.AVERAGE_SEEDLING_HEIGHT_METER, "Average of Height of seedlings" ,"Enter total no.", true, store);
        totalNoSurvived.setDecimal(false);*/

        dibbledtotalNoSurvived.addValidator(el -> {
            String dibbled = dibbledtotalNo.getValue().trim();
            String dibbledSurviving = el.getValue().trim();

            if (!dibbled.equals("") && !dibbledSurviving.equals("")) {
                if (Long.parseLong(el.getValue()) <= Long.parseLong(dibbledtotalNo.getValue()))
                    return new ValidationStatus(true);
                else {
                    el.setValue("");
                    return new ValidationStatus(false, "Value should be less than or equal to seeds dibbled");
                }

            } else {
                el.setValue("");
                return new ValidationStatus(false, "Value should be less than or equal to seeds dibbled");
            }


        });

       /* HButtonElement calculatePercentage = new HButtonElement("Calculate Percentage");

        HNumericElement percentageOfSeedling = new HNumericElement(Database.SEEDLING_PERCENTAGE, "Survival Percentage ", "press calculate button to calculate percentage", true, store);
        percentageOfSeedling.setNotEditable();
        HNumericElement percentageOfDibbled = new HNumericElement(Database.DIBBLED_PERCENTAGE, "Survival Percentage of Dibbled seeds", "press calculate button to calculate percentage ", true, store);
        percentageOfDibbled.setNotEditable();

        calculatePercentage.setOnClick(v -> {

            calculatePercentage.getButtonView().setFocusableInTouchMode(true);
            calculatePercentage.getButtonView().requestFocus();
            calculatePercentage.getButtonView().setFocusableInTouchMode(false);
            float dibbled = 0, dibbledSurviving = 0, planted = 0, plantedSurviving = 0;
            try {
                if (!dibbledtotalNo.getValue().equals(""))
                    dibbled = Float.parseFloat(dibbledtotalNo.getValue());
            } catch (NumberFormatException e) {
                dibbled = 0;
            }

            try {
                if (!dibbledtotalNoSurvived.getValue().equals(""))
                    dibbledSurviving = Float.parseFloat(dibbledtotalNoSurvived.getValue());
            } catch (NumberFormatException e) {
                dibbledSurviving = 0;
            }

            try {
                if (!totalNo.getValue().equals(""))
                    planted = Float.parseFloat(totalNo.getValue());
            } catch (NumberFormatException e) {
                planted = 0;
            }
            try {
                if (!totalNoSurvived.getValue().equals(""))
                    plantedSurviving = Float.parseFloat(totalNoSurvived.getValue());
            } catch (NumberFormatException e) {
                plantedSurviving = 0;
            }

            float dibblePercentage = 0, seedlingPercentage = 0;
            try {
                if (dibbled >= dibbledSurviving) {
                    if (dibbled != 0) {
                        dibblePercentage = (dibbledSurviving / dibbled);
                        dibblePercentage = dibblePercentage * 100;
                    }

                } else {
                    Snackbar dibbledSnack = Snackbar.make(findViewById(android.R.id.content), "Surving seeds must be less than dibbled to calculate percentage", Snackbar.LENGTH_LONG);
                    dibbledSnack.show();
                    // Toast.makeText(PlotInventory.this, "Surving seeds must be less than dibbled to calculate percentage", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (planted >= plantedSurviving) {
                if (planted != 0) {
                    seedlingPercentage = (plantedSurviving / planted);
                    seedlingPercentage = seedlingPercentage * 100;
                }

            } else {
                Snackbar plantedSnack = Snackbar.make(findViewById(android.R.id.content), "Surving seedlings must be less than planted to calculate percentage", Snackbar.LENGTH_LONG);
                plantedSnack.show();
            }

            percentageOfDibbled.setValue(String.valueOf(dibblePercentage));
            percentageOfSeedling.setValue(String.valueOf(seedlingPercentage));
        });*/

        HPickerElement stateOfHealth = new HPickerElement(Database.STATE_OF_HEALTH, "State of growth of seedlings", "Select an option", true, "Very Good|Good|Satisfactory|Poor", store);

        HNumericElement numberTended = new HNumericElement(Database.NUMBER_TENDED, "Number tended", "Enter number of root stock tended", true, store);
        numberTended.setDecimal(false);

      /*  HNumericElement numberOfSeeding = new HNumericElement(Database.NUMBER_OF_SEEDINGS,"No of Seedlings","Enter number of seedings",true,store);
        numberOfSeeding.setDecimal(false);*/

        final HButtonElement submit = new HButtonElement("Save ");




        /*  if(inventoryType.equals(SamplePlotSurvey.CASUALTY_REPLACEMENT)){
         *//*plotInventory.addEl(species);
            plotInventory.addEl(speciesOther);*//*
         //   plotInventory.addEl(numberOfSeeding);
        }else {
            *//*plotInventory.addEl(species);
            plotInventory.addEl(speciesOther);*//*
            if (inventoryType.equals(SamplePlotSurvey.TREE_LIST)) {
                plotInventory.addEl(averageGbh);
            } else {
                plotInventory.addEl(averageGirth);
            }
            plotInventory.addEl(averageHeight);
            plotInventory.addEl(totalNo);
            plotInventory.addEl(totalNoSurvived);
            if (inventoryType.equals(SamplePlotSurvey.ROOTSTOCK)) {
                plotInventory.addEl(numberTended);
            } else {
                plotInventory.addEl(stateOfHealth);
            }
        }*/

        if (inventoryType.equals(SamplePlotSurvey.SEEDLING)) {
            plotInventory.addEl(species);
            plotInventory.addEl(averageGirth);
            plotInventory.addEl(averageHeight);
//            plotInventory.addEl(totalNo);
            plotInventory.addEl(totalNoSurvived);
          /*  plotInventory.addEl(calculatePercentage);
            plotInventory.addEl(percentageOfSeedling);*/
            plotInventory.addEl(stateOfHealth);

        } else if (inventoryType.equals(SamplePlotSurvey.SEED_DIBBLING)) {
            plotInventory.addEl(species);
            plotInventory.addEl(dibbledtotalNo);
            plotInventory.addEl(dibbledtotalNoSurvived);
            plotInventory.addEl(averageHeight);
           /* plotInventory.addEl(calculatePercentage);
            plotInventory.addEl(percentageOfDibbled);*/
            plotInventory.addEl(stateOfHealth);
        }

        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);

            if (!checkFormData()) {
                showSaveFormDataAlert();
            } else {
                formFilledStatus = 1;
                submitPlotInventoryDetails();
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

        speciesAvailability.addPosSection(plotInventory);

        if (!formStatus.equals("0")) {
            plotInventory.addEl(back);
            plotInventory.setNotEditable();
        } else {
            plotInventory.addEl(submit);
        }
        sections.add(speciesSection);
        sections.add(plotInventory);
        plotInventory.disableSubElementClear();


        return new HRootElement("Plot Inventory Form", sections);
    }

    private void submitPlotInventoryDetails() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(PLOT_INVENTORY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SAMPLE_PLOT_INVENTORY, db);
            Log.d("TABLE_META_DATA", tableMetadata.toString());
            ContentValues cv = insertValuesToPlotInventory(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.FORM_ID, pref.getString(Database.FORM_ID, "0"));
            if (Integer.parseInt(pref.getString(Database.INVENTORY_ID, "0")) == 0) {
                cv.put(Database.SPECIES_ID, pref.getInt(Database.SPECIES_ID, 0));
                cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
                db.insertIntoPlotInventory(cv);
            } else {
                cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
                cv.put(Database.INVENTORY_ID, pref.getString(Database.INVENTORY_ID, "0"));
                db.updateTableWithId(Database.TABLE_SAMPLE_PLOT_INVENTORY, Database.INVENTORY_ID, cv);
            }
            pref.edit().clear().apply();
            setClearPref(true);
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });

        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.cancel());

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

    private ContentValues insertValuesToPlotInventory(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
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
        if (cv.get(SPECIES_NAME).equals(OTHERS_IF_ANY_SPECIFY)) {
            cv.put(SPECIES_NAME, pref.getString(SPECIES_OTHER, ""));
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
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(PLOT_INVENTORY, Context.MODE_PRIVATE);

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (pref.getString("formStatus", "0").equals("0")) {
                //showSaveFormDataAlert();
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if (!pref.getString("formStatus", "0").equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }

            if ((Integer.parseInt(pref.getString(Database.INVENTORY_ID, "0")) == 0)) {
                //super.onBackPressed();//commented this for disabling back button
            }
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }


    }

    public void showSaveFormDataAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitPlotInventoryDetails());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
