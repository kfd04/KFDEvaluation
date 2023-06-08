package com.kar.kfd.gov.kfdsurvey.plantation;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HGpsElement;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.HTextView;
import com.ngohung.form.el.store.HPrefDataStore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.kar.kfd.gov.kfdsurvey.SurveyCreation.getTableMetaData;

public class ControlPlotType extends HBaseFormActivity {
    public static final String CONTROL_PLOT_MASTER = "ControlPlotMaster";
    private String formStatus = "0";
    private SweetAlertDialog dialog;
    int formFilledStatus = 0;

    @Override
    protected HRootElement createRootElement() {

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(CONTROL_PLOT_MASTER, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        String inventoryType = pref.getString(Database.PART_TYPE, "");
        String title = pref.getString(Database.CONTROL_PLOT_TYPE, "");
        formStatus = pref.getString("formStatus", "0");
        setToolBarTitle(inventoryType);

        ArrayList<HSection> sections = new ArrayList<>();

        HSection controlPlotSection = new HSection(title);


        HTextView conterFactualGpsLabel = new HTextView("1.GPS readings at the centre of the control plot:");
        controlPlotSection.addEl(conterFactualGpsLabel);

        HGpsElement conterFactualLocation = new HGpsElement("Get location", true);
        if (formStatus.equals("0")) {
            controlPlotSection.addEl(conterFactualLocation);
        }


        HTextEntryElement conterFactualLatitude = new HTextEntryElement(Database.GPS_LATITUDE, "Latitude", "Click on the Gps button to get location", true, store);
        conterFactualLatitude.setNotEditable();
        conterFactualLocation.setLatitude(conterFactualLatitude);
        controlPlotSection.addEl(conterFactualLatitude);

        HTextEntryElement conterFactualLongitude = new HTextEntryElement(Database.GPS_LONGITUDE, "Longitude", "Click on the Gps button to get location", true, store);
        conterFactualLongitude.setNotEditable();
        conterFactualLocation.setLongitude(conterFactualLongitude);
        controlPlotSection.addEl(conterFactualLongitude);

        HTextEntryElement conterFactualAltitude = new HTextEntryElement(Database.GPS_ALTITUDE, "Altitude", "Click on the Gps button to get location", true, store);
        conterFactualAltitude.setNotEditable();
        conterFactualLocation.setAltitude(conterFactualAltitude);
        controlPlotSection.addEl(conterFactualAltitude);
        // ----just for saving the creation timestamp of location coordinates ----
        HTextEntryElement timestampEl = new HTextEntryElement(Database.GPS_COORDINATE_CREATION_TIMESTAMP, "", "", true, store);
        conterFactualLocation.setCreationTimeStamp(timestampEl);
        //-----------------------------------------------------------------------

        HNumericElement plantationAndCntrlPlotDist = new HNumericElement(Database.DISTANCE_FROM_PLANTATION_BOUNDRY, "2.Distance from the existing plantation ", "Enter the distance", true, store);
        controlPlotSection.addEl(plantationAndCntrlPlotDist);

        HTextEntryElement controlPlotLocatedDirection = new HTextEntryElement(Database.DIRECTION_IN_WHICH_CONTROL_PLOT_LOCATED, "3.Direction in which control plot is located", "Enter the direction", true, store);

        HTextEntryElement controlPlotType = new HTextEntryElement(Database.CONTROL_PLOT_TYPE, "", "", false, store);

        if (!title.equals("Within Plantaion")) {
            controlPlotSection.addEl(controlPlotLocatedDirection);
        }

        HButtonElement controlPlotTree = new HButtonElement("Add/View control plot Tree inventory");
        controlPlotSection.addEl(controlPlotTree);
        controlPlotTree.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.CONTROL_PLOT_INVENTORY_LIST);
            i.putExtra("Inventory-type", SamplePlotSurvey.TREE_LIST);
            i.putExtra(Database.CONTROL_PLOT_TYPE, title);
            i.putExtra("formStatus", formStatus);
            startActivity(i);
        });

        HButtonElement controlPlotRoot = new HButtonElement("Add/View control plot Root Stock inventory");
        controlPlotSection.addEl(controlPlotRoot);
        controlPlotRoot.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.CONTROL_PLOT_INVENTORY_LIST);
            i.putExtra("Inventory-type", SamplePlotSurvey.ROOTSTOCK);
            i.putExtra(Database.CONTROL_PLOT_TYPE, title);
            i.putExtra("formStatus", formStatus);
            startActivity(i);
        });

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
                saveControlPlotMaster();
            }


        });
        if (!formStatus.equals("0")) {
            controlPlotSection.setNotEditable();
        } else {
            controlPlotSection.addEl(submit);
        }
        sections.add(controlPlotSection);

        return rootElement = new HRootElement("Plot Inventory Form", sections);

    }

    private void saveControlPlotMaster() {

        SharedPreferences prefControlPlotMaster = this.getApplicationContext().getSharedPreferences(CONTROL_PLOT_MASTER, Context.MODE_PRIVATE);

        Database db = new Database(this.getApplicationContext());
        Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_CONTROL_PLOT_MASTER, db);
        ContentValues cv = insertIntoContentValues(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), prefControlPlotMaster, db);
        long anrModelId;

        cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
        cv.put(Database.FORM_ID, prefControlPlotMaster.getString(Database.FORM_ID, "0"));
        if (Integer.parseInt(prefControlPlotMaster.getString(Database.ANRMODEL_ID, "0")) == 0) {
            anrModelId = db.insertIntoControlPlotMaster(cv);

        } else {
            anrModelId = Long.parseLong(prefControlPlotMaster.getString(Database.ANRMODEL_ID, "0"));
            cv.put(Database.ANRMODEL_ID, anrModelId);
            db.updateTableWithId(Database.TABLE_CONTROL_PLOT_MASTER, Database.ANRMODEL_ID, cv);
        }
        db.updateTableWithoutId(Database.TABLE_CONTROL_PLOT_INVENTORY, Database.ANRMODEL_ID, anrModelId);
        prefControlPlotMaster.edit().clear().apply();
        setClearPref(true);
        showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");

    }

    @Override
    public void onBackPressed() {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(CONTROL_PLOT_MASTER, Context.MODE_PRIVATE);

        if (Objects.requireNonNull(pref.getString("formStatus", "0")).equals("0")) {
            //showSaveFormDataAlert();
            showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
        } else {
            super.onBackPressed();
        }
    }

    public void showSaveFormDataAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> saveControlPlotMaster());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showEventDialog(int type, String msg) {
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

    private ContentValues insertIntoContentValues(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
        ContentValues cv = new ContentValues();
        for (int i = 2; i < columnNames.size(); i++) {
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


}
