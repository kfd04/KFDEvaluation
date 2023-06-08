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

import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;
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

public class BeneficiaryAddSpecies extends HBaseFormActivity {


    public static final String folderName = Constants.FORMTYPE_ADD_BENEFICIARY_SPECIES;
    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = "Others";
    public static final String SPECIES_PREF = "BeneficiaryAddSpecies";
    private SweetAlertDialog dialog;
    private BeneficiaryAddSpecies mSurvey = this;
    private Database db;
    private String formStatus = "0";
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;

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

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        final SharedPreferences globalPref = this.getApplicationContext().getSharedPreferences("BeneficiaryDetails", Context.MODE_PRIVATE);
        final SharedPreferences prefSpecies = this.getApplicationContext().getSharedPreferences(SPECIES_PREF, Context.MODE_PRIVATE);
        String modelId = globalPref.getString("model_id", "1");
        HPrefDataStore store = new HPrefDataStore(prefSpecies);
        db = new Database(this.getApplicationContext());

        ArrayList<HSection> sections = new ArrayList<>();
        formStatus = prefSpecies.getString("formStatus", "0");
        String inventoryType = pref.getString(Database.PART_TYPE, "");
        HSection basicInfoSection = new HSection("Species Details");
        setToolBarTitle(inventoryType);

        HTextEntryElement samplePlotNo = new HTextEntryElement(Database.SAMPLE_PLOT_ID, "Sample species No.", "Enter sample species no", false, store);
        samplePlotNo.setNotEditable();

        if(Integer.parseInt(prefSpecies.getString(Database.SAMPLE_PLOT_ID,"0"))==0) {
            samplePlotNo.setValue(String.valueOf(db.getNumberOfBeneficiarySpecies(Integer.parseInt(prefSpecies.getString(Database.FORM_ID,"0"))) + 1));
        } else {
            samplePlotNo.setValue(String.valueOf(db.getSpeciesNumberFormId(Integer.parseInt(prefSpecies.getString(Database.FORM_ID,"0")),Integer.parseInt(prefSpecies.getString(Database.SAMPLE_PLOT_ID,"0")))));
        }
        basicInfoSection.addEl(samplePlotNo);

        final HPickerElement mainSpeciesPlanted = new HPickerElement(Database.MAIN_SPECIES_PLANTED, "Species name", "Select an option", false, "others|" + db.getNamesOfSpeciesNew(modelId), store);
        basicInfoSection.addEl(mainSpeciesPlanted);

        HTextAreaEntryElement speciesOther = new HTextAreaEntryElement(Database.OTHER_SPECIES, "Other Species", "Press put a comma after each species name", false, store);
        mainSpeciesPlanted.addElementForValue(speciesOther, 0);
        basicInfoSection.addEl(speciesOther);
        HPickerElement pbviseSize = new HPickerElement(Database.SPECIES_SIZE, "Size of polybag", "Select an option", false, "5 X 8|" +
                "6 X 9|8 X 12|10 X 16|14 X 20", store);
        basicInfoSection.addEl(pbviseSize);

        HNumericElement pbviseSizeValue = new HNumericElement(Database.TOTAL_SPECIES_COUNT, "Cost of seedling", "Enter cost of seedlings", false, store);
        basicInfoSection.addEl(pbviseSizeValue);





        final HButtonElement submit = new HButtonElement("Save Species Details");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            submitSpeciesDetails();
        });
        final HButtonElement back = new HButtonElement("Close Form");
        back.setElType(HElementType.SUBMIT_BUTTON);
        back.setOnClick(v -> {
            back.getButtonView().setFocusableInTouchMode(true);
            back.getButtonView().requestFocus();
            back.getButtonView().setFocusableInTouchMode(false);
            onBackPressed();
        });
        basicInfoSection.addEl(submit);
        if (!formStatus.equals("0")) {
            basicInfoSection.addEl(back);
            basicInfoSection.setNotEditable();
        } else {
            basicInfoSection.addEl(submit);
        }
        sections.add(basicInfoSection);
        return new HRootElement("Species Form", sections);
    }

    private void submitSpeciesDetails() {
        final SharedPreferences prefSpecies = this.getApplicationContext().getSharedPreferences(SPECIES_PREF, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADD_BENEFICIARY_SPECIES, db);
            Log.d("TABLE_META_DATA", tableMetadata.toString());
            ContentValues cv = insertValuesToPlotInventory(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), prefSpecies, db);
            if (Integer.parseInt(prefSpecies.getString(Database.INVENTORY_ID, "0")) == 0) {
                db.insertIntoBenSpeciesInventory(cv);
            } else {
                cv.put(Database.INVENTORY_ID, prefSpecies.getString(Database.INVENTORY_ID, "0"));
                db.updateTableWithId(Database.TABLE_ADD_BENEFICIARY_SPECIES, Database.INVENTORY_ID, cv);
            }
            prefSpecies.edit().clear().apply();
            setClearPref(true);
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private ContentValues insertValuesToPlotInventory(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences prefSpecies, Database db) {
        ContentValues cv = new ContentValues();
        for (int i = 1; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            if (columnType.contains("INTEGER")) {
                try {
                    cv.put(columnName, Integer.parseInt(prefSpecies.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("float")) {
                try {
                    cv.put(columnName, Float.parseFloat(prefSpecies.getString(columnName, "")));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else if (columnType.contains("varchar")) {
                try {
                    cv.put(columnName, Database.getTruncatedVarchar((prefSpecies.getString(columnName, "")), columnType));
                } catch (Exception e) {
                    cv.put(columnName, 0);
                }
            } else {
                cv.put(columnName, prefSpecies.getString(columnName, ""));
            }
        }
        long creationTimeStamp = System.currentTimeMillis() / 1000;
        cv.put(Database.CREATION_TIMESTAMP, creationTimeStamp);
        if (cv.get(Database.MAIN_SPECIES_PLANTED).equals(OTHERS_IF_ANY_SPECIFY)) {
            cv.put(Database.MAIN_SPECIES_PLANTED, prefSpecies.getString(SPECIES_OTHER, ""));
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
        final SharedPreferences pref1 = this.getApplicationContext().getSharedPreferences(SPECIES_PREF, Context.MODE_PRIVATE);

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (pref1.getString("formStatus", "0").equals("0")) {
                // showSaveFormDataAlert();
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if (!pref1.getString("formStatus", "0").equals("0")) {
                pref1.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

        setToolBarTitle(getResources().getString(R.string.actionbar_title_plot_species));

    }

    public void showSaveFormDataAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setMessage("Please Save Form-data by pressing 'SAVE'");
        alertDialog.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }


}
