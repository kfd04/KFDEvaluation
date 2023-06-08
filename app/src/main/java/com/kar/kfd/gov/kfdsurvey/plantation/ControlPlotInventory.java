package com.kar.kfd.gov.kfdsurvey.plantation;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ControlPlotInventory extends HBaseFormActivity {

    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    public static final String CONTROL_PLOT_INVENTORY = "ControlPlotInventory";
    private SweetAlertDialog dialog;
    private String formStatus = "0";
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    int formFilledStatus = 0;

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

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(CONTROL_PLOT_INVENTORY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        Database db = new Database(getApplicationContext());
        String inventoryType = pref.getString(Database.PART_TYPE, "");
        String controlPlotType = pref.getString(Database.CONTROL_PLOT_TYPE, "");

        formStatus = pref.getString("formStatus", "0");
        setToolBarTitle(inventoryType);
        ArrayList<HSection> sections = new ArrayList<>();

        if (Integer.parseInt(Objects.requireNonNull(pref.getString(Database.INVENTORY_ID, "0"))) != 0) {
            if (!(OTHERS_IF_ANY_SPECIFY + "|" + db.getNamesOfSpecies()).contains(Objects.requireNonNull(pref.getString(Database.SPECIES_NAME, "")))) {
                pref.edit().putString(SPECIES_OTHER, pref.getString(Database.SPECIES_NAME, "")).apply();
                pref.edit().putString(Database.SPECIES_NAME, OTHERS_IF_ANY_SPECIFY).apply();
            }
        }

        HSection controlPlotInventory;
        if (inventoryType != null && inventoryType.equals("Natural tree found on sample plot")) {
            controlPlotInventory = new HSection("Inventory of Natural Trees found in the Control plot");
        } else {
            controlPlotInventory = new HSection("Inventory of Natural Root Stock found in the Control plot");
        }

        SharedPreferences basicInfoPref = this.getApplicationContext().getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        HPickerElement species = new HPickerElement(Database.SPECIES_NAME, "Species", "Select species names", true, OTHERS_IF_ANY_SPECIFY + "|" + db.getNamesOfSpeciesNew(basicInfoPref.getString("model_id", "1")), store);
        controlPlotInventory.addEl(species);

        HTextEntryElement speciesOther = new HTextEntryElement(SPECIES_OTHER, "Name of species ( Other )", "Enter species name", true, store);
        controlPlotInventory.addEl(speciesOther);
        species.addPosElement(speciesOther);

        HNumericElement averageGbh = new HNumericElement(Database.AVERAGE_GBH_METERS, "Average " +
                "GBH ( in >20 centimeters )", "Enter average GBH ( in > 20cm )", true, store);

        HNumericElement averageGirth = new HNumericElement(Database.AVERAGE_COLLAR_GIRTH,
                "Average collar girth ( in < 20cm )", "Enter average collar girth ( in" +
                " " +
                "centimeter )", true, store);

        if (inventoryType.equals(SamplePlotSurvey.TREE_LIST)) {
            controlPlotInventory.addEl(averageGbh);
        } else {
            controlPlotInventory.addEl(averageGirth);
        }

        HNumericElement averageHeight = new HNumericElement(Database.AVERAGE_HEIGHT_METERS, "Average height ( in meter )", "Enter average height ( in meter )", true, store);
        controlPlotInventory.addEl(averageHeight);

        HNumericElement totalNo = new HNumericElement(Database.TOTAL_COUNT, "Total No.", "Enter total no.", true, store);
        controlPlotInventory.addEl(totalNo);
        totalNo.setDecimal(false);

//        HPickerElement stateOfHealth = new HPickerElement(Database.STATE_OF_HEALTH, "State of health", "Select an option", true, "Very Good|Good|Satisfactory", store);

        HNumericElement numberTended = new HNumericElement(Database.NUMBER_TENDED, "Number tended", "Enter number of root stock tended", true, store);
        numberTended.setDecimal(false);

//        HTextEntryElement controlPlot = new HTextEntryElement(Database.CONTROL_PLOT_TYPE, "", "", false, store);

        final HButtonElement submit = new HButtonElement("Save");

        if (inventoryType.equals(SamplePlotSurvey.TREE_LIST)) {
            //  controlPlotInventory.addEl(stateOfHealth);
        }
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showSaveFormDataAlert();
            else {
                formFilledStatus = 1;
                submitControlPlotInventoryDetails();
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
            controlPlotInventory.addEl(back);
            controlPlotInventory.setNotEditable();
        } else {
            controlPlotInventory.addEl(submit);
        }
        sections.add(controlPlotInventory);

        return new HRootElement("Plot Inventory Form", sections);
    }

    private void submitControlPlotInventoryDetails() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(CONTROL_PLOT_INVENTORY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_CONTROL_PLOT_INVENTORY, db);
            ContentValues cv = insertValuesToControlPlotInventory(Objects.requireNonNull(tableMetadata.get("columnNamesList")), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            if (Integer.parseInt(pref.getString(Database.INVENTORY_ID, "0")) == 0) {
                db.insertIntoControlPlotInventory(cv);
            } else {
                cv.put(Database.INVENTORY_ID, pref.getString(Database.INVENTORY_ID, "0"));
                db.updateTableWithId(Database.TABLE_CONTROL_PLOT_INVENTORY, Database.INVENTORY_ID, cv);
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

    private ContentValues insertValuesToControlPlotInventory(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
        ContentValues cv = new ContentValues();
        for (int i = 1; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            if (columnType.contains("INTEGER")) {
                try {
                    cv.put(columnName, Integer.parseInt(Objects.requireNonNull(pref.getString(columnName, ""))));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("float")) {
                try {
                    cv.put(columnName, Float.parseFloat(Objects.requireNonNull(pref.getString(columnName, ""))));
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
        if (cv.get(Database.SPECIES_NAME).equals(OTHERS_IF_ANY_SPECIFY)) {
            cv.put(Database.SPECIES_NAME, pref.getString(SPECIES_OTHER, ""));
        }
        return cv;
    }

    private Map<String, ArrayList<String>> getTableMetaData(String tableName, Database db) {
        Cursor cursor = db.getColumnNames(tableName);
        ArrayList<String> columnNames;
        ArrayList<String> columnTypes;
        try {
            int nameIdx = cursor.getColumnIndexOrThrow("name");
            int typeIdx = cursor.getColumnIndexOrThrow("type");
            columnNames = new ArrayList<>();
            columnTypes = new ArrayList<>();
            while (cursor.moveToNext()) {

                columnNames.add(cursor.getString(nameIdx));
                columnTypes.add(cursor.getString(typeIdx));
            }

        } finally {
            cursor.close();
        }

        Map<String, ArrayList<String>> map = new HashMap();
        map.put("columnNamesList", columnNames);
        map.put("columnTypesList", columnTypes);

        return map;
    }


    @Override
    public void onBackPressed() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(CONTROL_PLOT_INVENTORY, Context.MODE_PRIVATE);

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (Objects.requireNonNull(pref.getString("formStatus", "0")).equals("0")) {
                //showSaveFormDataAlert();
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if (!Objects.requireNonNull(pref.getString("formStatus", "0")).equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }
            if ((Integer.parseInt(Objects.requireNonNull(pref.getString(Database.INVENTORY_ID, "0"))) == 0)) {
                //super.onBackPressed();// commented this to disable back button.
            }
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
        // showSaveFormDataAlert();
    }

    public void showSaveFormDataAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitControlPlotInventoryDetails());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
