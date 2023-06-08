package com.kar.kfd.gov.kfdsurvey.plantation.smc;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HMultiPickerElement;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.store.HPrefDataStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/**
 * Modified by Sarath
 */
public class SmcPlantationSampling extends HBaseFormActivity {

    public static final String INSPECT_TWO_SMC_WORK = "SmcHighest";
    public static final String folderName = "Plantation" + File.separator + "SMC Survey";
    public static final String SMC_APPLICABLE = "smc_applicable";
    public static final String SMC_PLANTATION_SAMPLING = "SMCPlantationSampling";
    private static final String TAG = SmcPlantationSampling.class.getSimpleName();
    int formId;
    private SmcPlantationSampling mSurvey = this;
    private String formStatus = "0";
    private StringBuilder smcListNames = new StringBuilder();
    private ArrayList<Integer> ids;
    private Database database;
    public boolean wantToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadFormData();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected HRootElement createRootElement() {

        database = new Database(this);
        ids = new ArrayList<>();
        smcListNames = new StringBuilder();
        SharedPreferences preferences = this.getApplicationContext().getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        try {
            formId = Integer.parseInt(preferences.getString(Database.FORM_ID, "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Cursor cursor = database.getSmcWorks(String.valueOf(formId));

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String smcwork = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                int amount = cursor.getInt(cursor.getColumnIndex(Database.SMC_STRUCTURE_COST));
                smcListNames.append(smcwork).append(" : Rs. ").append(amount).append("|");
                ids.add(cursor.getInt(cursor.getColumnIndex(Database.SMC_ID)));
            } while (cursor.moveToNext());

        } else {
            smcListNames.append("No Smc");
            ids.add(0);
        }
        assert cursor != null;
        cursor.close();


        ArrayList<HSection> sections = new ArrayList<>();
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) == 0 || Integer.parseInt(pref.getString(Database.CREATION_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.CREATION_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }
        formStatus = getSharedPreferences("Basic information", Context.MODE_PRIVATE).getString("formStatus", "0");
        if (!pref.getString(Database.SMC_WORK_ANY_OTHER_REMARKS, "").equals("")) {
            pref.edit().putString("smc_work_other_remarks", "Yes").apply();
        }

       /* if(pref.getString(SMC_APPLICABLE,"").equals("")){
            pref.edit().putString(SMC_APPLICABLE,"No").commit();
        }*/

        if (pref.getString(Database.SMC_STATUS, "0").equals("0")) {

            calculateHighest();
        }

        long timeStamp = Integer.parseInt(pref.getString(Database.CREATION_TIMESTAMP, String.valueOf(new Date().getTime())));
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        pref.edit().putString("survey_date", date).apply();

        HSection applicatbleSmc = new HSection("Basic information on SMC works(To be Recorded by Evaluator)");

        HPickerElement isApplicable = new HPickerElement(SMC_APPLICABLE, "Are there any SMC works " +
                "taken up in this plantation?", "Select an option", true, "Yes|No", store);
        applicatbleSmc.addEl(isApplicable);


        HButtonElement listWorks = new HButtonElement("A.View SMC Works");
        isApplicable.addPosElement(listWorks);
        applicatbleSmc.addEl(listWorks);
        listWorks.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), SmcWorksActivity.class);
            // i.putExtra("id",Integer.parseInt(pref.getString(Database.FORM_ID,"0")));
            i.putExtra(Database.FORM_ID, String.valueOf(formId));
            i.putExtra("List-type", Constants.SMC_WORKS_LIST);
            i.putExtra("formStatus", formStatus);
            startActivity(i);
        });


        HSection sampleSurveySmvWork = new HSection("B.Evaluate two SMC works " +
                "where the expenditure was the highest & 2nd Highest");


        HButtonElement SmcWork1 = new HButtonElement("Smc Number 1");
        sampleSurveySmvWork.addEl(SmcWork1);
        SmcWork1.setOnClick(v -> {

            Database db = new Database(mSurvey.getApplicationContext());
            Cursor cursor1 = db.getSmcNumber1(formId);
            if (cursor1.getCount() != 0 && cursor1.moveToFirst()) {
                SharedPreferences pref1 = getSharedPreferences(INSPECT_TWO_SMC_WORK, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                pref1.edit().clear().apply();
                editor = pref1.edit();
                for (int i = 0; i < cursor1.getColumnCount(); i++) {
                    editor.putString(cursor1.getColumnName(i), cursor1.getString(i));
                }
                editor.putString("formStatus", formStatus);
                editor.putString("smcTitle", "Evaluation of SMC number 1");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), SmcHighest.class);
                intent.putExtra("id", Integer.parseInt(pref1.getString(Database.FORM_ID, "0")));
                intent.putExtra("List-type", Constants.INSPECT_TWO_SMC_WORKS);
                intent.putExtra("formStatus", formStatus);

                startActivity(intent);
            }

            Objects.requireNonNull(cursor1).close();

        });

        HButtonElement SmcWork2 = new HButtonElement("Smc Number 2");
        sampleSurveySmvWork.addEl(SmcWork2);
        SmcWork2.setOnClick(v -> {

            Database db = new Database(mSurvey.getApplicationContext());
            Cursor cursor12 = db.getSmcNumber2(formId);
            if (cursor12 != null && cursor12.moveToFirst()) {
                SharedPreferences pref12 = getSharedPreferences(INSPECT_TWO_SMC_WORK, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                pref12.edit().clear().apply();
                editor = pref12.edit();
                for (int i = 0; i < cursor12.getColumnCount(); i++) {
                    editor.putString(cursor12.getColumnName(i), cursor12.getString(i));
                }
                editor.putString("formStatus", formStatus);
                editor.putString("smcTitle", "Evaluation of SMC number 2");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), SmcHighest.class);
                intent.putExtra("id", Integer.parseInt(pref12.getString(Database.FORM_ID, "0")));
                intent.putExtra("List-type", Constants.INSPECT_TWO_SMC_WORKS);
                intent.putExtra("formStatus", formStatus);
                startActivity(intent);
            }
            Objects.requireNonNull(cursor12).close();
        });


        HSection generalObservations = new HSection("C. General observations of SMC " +
                "works and their effectiveness");

        HNumericElement totalBudget = new HNumericElement(Database.TOTAL_BUDGET_FOR_SMC_WORK, "1.What was the total budget devoted to SMC works ( in Rupees ) ?", "Enter budget in Rs.", true, store);
        generalObservations.addEl(totalBudget);

       /* HPickerElement expenditurePerNorms = new HPickerElement(Database.SMC_WORK_EXPENDITURE_AS_PER_NORM, "2.Whether the SMC expenditure is as per the norms prescribed ( 15% of total planting cost ) ?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(expenditurePerNorms);*/

        HPickerElement builtInPlantation = new HPickerElement(Database.ALL_SMC_STRUCTURES_BUILT_IN_FARM, "2.Whether all the SMC structures were built within plantation boundary?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(builtInPlantation);

        HMultiPickerElement smcList = new HMultiPickerElement(Database.ALL_SMC_STRUCTURES_NOT_BUILT_IN_FARM, "Which ones ?(Select from Smc List)", "Select an option", false, smcListNames.toString(), store);
        builtInPlantation.addNegElement(smcList);
        generalObservations.addEl(smcList);

        HTextEntryElement builtInPlantationNo = new HTextEntryElement(Database
                .WHERE_IT_WAS_DONE, "Where it was done?", "", true, store);
        builtInPlantation.addNegElement(builtInPlantationNo);
        generalObservations.addEl(builtInPlantationNo);

        HPickerElement plantationTreated = new HPickerElement(Database.SMC_PLANTATION_AREA_TREATED_COMPLETELY, "3.Whether the plantation area was treated completely?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(plantationTreated);

        HPickerElement watershedPattern = new HPickerElement(Database.SMC_TREATMENT_FOLLOWED_WATERSHED_PATTERN, "4.Whether SMC treatment followed Watershed principle?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(watershedPattern);

        HTextAreaEntryElement watershedPatternReasons = new HTextAreaEntryElement(Database.REASON_FOR_NOT_FOLLOWING_SMC_WATERSHED_PATTERN, "Reasons", "Enter reasons for not following watershed patter", true, store);
        generalObservations.addEl(watershedPatternReasons);
        watershedPattern.addNegElement(watershedPatternReasons);

        HPickerElement locationAppropriate = new HPickerElement(Database.IS_LOCATION_OF_SMC_WORK_OK, "5.Whether the locations of individual SMC works were found appropriate?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(locationAppropriate);

        HMultiPickerElement smcList1 = new HMultiPickerElement(Database.IS_LOCATION_OF_SMC_WORK_INAPPROPRIATE_LIST, "Which ones ?(Select from Smc List)", "Select an option", false, "" + smcListNames,
                store);
        locationAppropriate.addNegElement(smcList1);
        generalObservations.addEl(smcList1);

        HTextAreaEntryElement locationInappropriateDetails = new HTextAreaEntryElement(Database.REASON_FOR_INAPPROPRIATE_LOCATION_OF_SMC_WORK, "Details", "Enter details of innapropriate structures", false, store);
        locationAppropriate.addNegElement(locationInappropriateDetails);
        generalObservations.addEl(locationInappropriateDetails);
        //  locationAppropriate.addNegElement(locationInappropriateDetails);


        HPickerElement purposeAppropriate = new HPickerElement(Database.IS_SMC_STRUCTURE_SERVING_INTENDED_PURPOSE,
                "6.Is there any indication of water collection?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(purposeAppropriate);

        HTextAreaEntryElement reasons = new HTextAreaEntryElement(Database.SMC_STRUCTURE_NOT_SERVING_INTENDED_PURPOSE_REASONS,
                "Details", "Specify details", true, store);
        generalObservations.addEl(reasons);
        purposeAppropriate.addNegElement(reasons);

        HPickerElement structureDamaged = new HPickerElement(Database.ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED, "7.Was " +
                "any structure found breached or damaged?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(structureDamaged);

        HMultiPickerElement smcList2 = new HMultiPickerElement(Database.ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED_YES, "Which ones ?(Select from Smc List)", "Select an option", false, "" + smcListNames,
                store);
        structureDamaged.addPosElement(smcList2);
        generalObservations.addEl(smcList2);

        HTextAreaEntryElement structureDamagedDetails = new HTextAreaEntryElement(Database.SMC_WORK_STRUCTURE_DAMAGED_DETAILS, "Details", "Enter details of breached or damaged structures", true, store);
        generalObservations.addEl(structureDamagedDetails);
        structureDamaged.addPosElement(structureDamagedDetails);

        HPickerElement dispersalSmc = new HPickerElement(Database.IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL,
                "8.Is the dispersal of smc structure are in accordance with average rainfall?", "Select an option", true, "Yes|No", store);
        generalObservations.addEl(dispersalSmc);

        HTextAreaEntryElement dispersalSmcDetails = new HTextAreaEntryElement(Database.IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL_DETAILS, "Details", "Enter details of breached or damaged structures", true, store);
        generalObservations.addEl(dispersalSmcDetails);
        dispersalSmc.addNegElement(dispersalSmcDetails);
       /* HPickerElement damageToTrees = new HPickerElement(Database.ANY_DAMAGE_CAUSED_TO_TREE_ROOTS_BY_SMC_WORK,"10.Was any damage done to local trees or root zone by the SMC works?","Select an option",true,"Yes|No",store);
        generalObservations.addEl(damageToTrees);

        HTextAreaEntryElement damageToTreesDetails = new HTextAreaEntryElement(Database.DETAILS_OF_DAMAGE_CAUSED_TO_TREE_ROOTS_BY_SMC_WORK,"Details","Enter details of damage done",true,store);
        generalObservations.addEl(damageToTreesDetails);
        damageToTrees.addPosElement(damageToTreesDetails);

        HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageGrid imageGrid = new ImageGrid();
                Bundle bundle = new Bundle();
                bundle.putString("imageFolderName", folderName);
                bundle.putString("formId",pref.getString(Database.FORM_ID,"0"));
                bundle.putString("formStatus",formStatus);
                imageGrid.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
                transaction.addToBackStack("ImageGrid");
                transaction.commit();
            }
        });
        generalObservations.addEl(viewPhoto);
        damageToTrees.addPosElement(viewPhoto);*/

        /*HPickerElement otherSuggestions = new HPickerElement("smc_work_other_remarks","9.Any " +
                "other remarks/comments/suggestions?","Select an option",true,"Yes|No",store);
        generalObservations.addEl(otherSuggestions);*/

        HTextAreaEntryElement otherSuggestionsDetails = new HTextAreaEntryElement(Database.SMC_WORK_ANY_OTHER_REMARKS, "9.Any " +
                "other remarks/comments/suggestions?", "Enter remarks", true, store);
        generalObservations.addEl(otherSuggestionsDetails);
//        otherSuggestions.addPosElement(otherSuggestionsDetails);

        HSection otherSMCWork = new HSection("D.Other SMC Works");

        HPickerElement othersmc = new HPickerElement(Database.OTHER_SMC_WORKS, "1.Is there any other SMC works noticed in plantation?", "Select an option", true, "Yes|No", store);
        otherSMCWork.addEl(othersmc);
        Log.i(Constants.SARATH, "othersmc: " + othersmc.getValue());

        /*othersmc.addValueChangedListener(el -> {
            HPickerElement pickerItem = (HPickerElement) el;

            if (pickerItem.getValue().equalsIgnoreCase("yes")) {

                Intent i = new Intent(getApplicationContext(), SurveyList.class);
                i.putExtra("id", Integer.parseInt(pref.getString(Database.FORM_ID, "0")));
                i.putExtra("List-type", Constants.OTHER_SMC_WORKS);
                i.putExtra("formStatus", formStatus);
                startActivity(i);
            }

        });*/

        HButtonElement addOtherSMC = new HButtonElement("ADD OTHER SMC");
        othersmc.addPosElement(addOtherSMC);
        otherSMCWork.addEl(addOtherSMC);
        addOtherSMC.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyList.class);
            i.putExtra("id", Integer.parseInt(pref.getString(Database.FORM_ID, "0")));
            i.putExtra("List-type", Constants.OTHER_SMC_WORKS);
            i.putExtra("formStatus", formStatus);
            startActivity(i);
        });

        HSection smcVerify = new HSection("");

        final HButtonElement verifySmcEntries = new HButtonElement("Save");
        verifySmcEntries.setElType(HElementType.SUBMIT_BUTTON);
        smcVerify.addEl(verifySmcEntries);
        verifySmcEntries.setOnClick(v -> {
            verifySmcEntries.getButtonView().setFocusableInTouchMode(true);
            verifySmcEntries.getButtonView().requestFocus();
            verifySmcEntries.getButtonView().setFocusableInTouchMode(false);

            if (!checkFormData()) {
                showSaveFormDataAlert();
            } else {
                wantToExit = true;
                onBackPressed();
            }


        });


        isApplicable.addPosSection(generalObservations);
        isApplicable.addPosSection(sampleSurveySmvWork);

        if (!formStatus.equals("0")) {
            applicatbleSmc.setNotEditable();
            generalObservations.setNotEditable();
            sampleSurveySmvWork.setNotEditable();
            smcVerify.setNotEditable();
        }
        sections.add(applicatbleSmc);
        sections.add(sampleSurveySmvWork);
        sections.add(generalObservations);
        sections.add(otherSMCWork);
        sections.add(smcVerify);

        return new HRootElement("SMC Plantation Sampling", sections);
    }



    @Override
    public void onBackPressed() {
        if (!checkFormData()) {
            if (wantToExit)
                super.onBackPressed();
            else
                showSaveFormDataAlert();
        } else {
            SharedPreferences pref = getSharedPreferences(SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
            pref.edit().putString(Database.FORM_FILLED_STATUS, "1").apply();
            super.onBackPressed();

        }



    }

    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> {
                    wantToExit = true;
                    onBackPressed();
                });

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void calculateHighest() {
        SharedPreferences pref = getSharedPreferences(SmcPlantationSampling.SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        // int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
        ContentValues cv = new ContentValues();
        cv.put(Database.SMC_STATUS, 1);

        // db.updateSMCStatus(formId);
        database.updateTableWithFormId(Database.TABLE_SMC_SAMPLING_MASTER, formId, cv);
        pref.edit().putString(Database.SMC_STATUS, "1").apply();

        Cursor cursorParent = database.getsmcHighest(formId);
        Cursor cursorSMCHighchild = database.getSMCHighestWithFormID(formId);
        if (cursorParent != null && cursorParent.moveToFirst()) {

            if (cursorSMCHighchild != null && cursorSMCHighchild.moveToFirst()) {


                database.deleteSMCHigh(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.FORM_ID, formId);
                do {
                    String smcWork = cursorParent.getString(cursorParent.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                    Double cost = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_COST));
                    Double length = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_LENGTH));
                    Double breadth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_BREADTH));
                    Double depth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_DEPTH));

                    ContentValues cvSMCHigh = new ContentValues();
                    cvSMCHigh.put(Database.FORM_ID, formId);
                    cvSMCHigh.put(Database.TYPE_OF_STRUCTURE, smcWork);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_COST, cost);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_LENGTH, length);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_BREADTH, breadth);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_DEPTH, depth);
                    Log.i(TAG, "calculateHighest: " + smcWork + " " + cost);
                    database.insertIntoInspectedSmcWorkDetails(cvSMCHigh);
                } while (cursorParent.moveToNext());
            } else {
                do {
                    String smcWork = cursorParent.getString(cursorParent.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                    Double cost = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_COST));
                    Double length = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_LENGTH));
                    Double breadth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_BREADTH));
                    Double depth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_DEPTH));

                    ContentValues cvSMCHigh = new ContentValues();
                    cvSMCHigh.put(Database.FORM_ID, formId);
                    cvSMCHigh.put(Database.TYPE_OF_STRUCTURE, smcWork);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_COST, cost);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_LENGTH, length);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_BREADTH, breadth);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_DEPTH, depth);
                    Log.i(TAG, "calculateHighest: " + smcWork + " " + cost);
                    database.insertIntoInspectedSmcWorkDetails(cvSMCHigh);
                } while (cursorParent.moveToNext());
            }

        }

        Objects.requireNonNull(cursorSMCHighchild).close();

        Objects.requireNonNull(cursorParent).close();
    }


}
