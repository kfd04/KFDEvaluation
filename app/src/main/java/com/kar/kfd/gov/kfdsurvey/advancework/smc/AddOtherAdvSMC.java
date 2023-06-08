package com.kar.kfd.gov.kfdsurvey.advancework.smc;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.store.HPrefDataStore;

import java.util.ArrayList;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.SurveyCreation.getTableMetaData;

public class AddOtherAdvSMC extends HBaseFormActivity {

    public static final String ADD_OTHER_ADV_SMC = "add_other_adv_smc";
    private SweetAlertDialog dialog;
    private String formStatus = "0";
    private Database database;
    int formFilledStatus = 0;
    @Override
    protected HRootElement createRootElement() {
        database = new Database(this);

        SharedPreferences preferences = this.getApplicationContext().getSharedPreferences(ADD_OTHER_ADV_SMC, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(preferences);

        ArrayList<HSection> sections = new ArrayList<>();
        formStatus = preferences.getString("formStatus", "0");

        HSection otherSMC = new HSection("Other SMC Works");

        HPickerElement typeOfStructure = new HPickerElement(Database.TYPE_OF_STRUCTURE, "Type of " +
                "smc", "Select an option", true, "Check Dam|Nala bunds|Percolation ponds|Gully checks|Rain water harvesting trenches|Gabian|Others", store);
        otherSMC.addEl(typeOfStructure);

        HPickerElement plantationAreaScheme = new HPickerElement(Database.SCHEME_NAME, "Scheme", "Select the scheme", true, database.getNamesOfSchemeNew() + "|Not Available", store);
        otherSMC.addEl(plantationAreaScheme);

        HPickerElement yearOfWork = new HPickerElement(Database.YEAR_OF_WORK, "Year of work", "Select an option", true, "2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21|Not Available", store);
        otherSMC.addEl(yearOfWork);

        HNumericElement expIncurred = new HNumericElement(Database.EXPENDITURE_INCURRED, "Expenditure Incurred", "Enter Expenditure Incurred", false, store);
        otherSMC.addEl(expIncurred);

        HPickerElement statusOfSMC = new HPickerElement(Database.STATUS_OF_SMC, "Status of smc", "Select smc status", true, "Good|Average|Poor", store);
        otherSMC.addEl(statusOfSMC);

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
                submitOtherSmcWorks();
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
            otherSMC.addEl(back);
            otherSMC.setNotEditable();
        } else {
            otherSMC.addEl(submit);
        }

        sections.add(otherSMC);

        return new HRootElement("Add Other Smc Works", sections);
    }

    private void submitOtherSmcWorks() {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADD_OTHER_ADV_SMC, Context.MODE_PRIVATE);
        Database db = new Database(this.getApplicationContext());
        Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADV_OTHER_SMC_LIST, db);
        ContentValues cv = insertValuesToOtherSmcWorks(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
        cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
        if (Integer.parseInt(pref.getString(Database.OTHER_SMC_ID, "0")) == 0) {
            db.insertAdvOtherSMCList(cv);
        } else {
            cv.put(Database.OTHER_SMC_ID, Integer.parseInt(pref.getString(Database.OTHER_SMC_ID, "0")));
            db.updateTableWithId(Database.TABLE_ADV_OTHER_SMC_LIST, Database.OTHER_SMC_ID, cv);
        }
        pref.edit().clear().apply();
        setClearPref(true);
        showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");

    }

    public void showSaveFormDataAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitOtherSmcWorks());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private ContentValues insertValuesToOtherSmcWorks(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {

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
        return cv;
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


}
