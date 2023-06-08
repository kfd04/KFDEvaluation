package com.kar.kfd.gov.kfdsurvey.plantation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.HTextView;
import com.ngohung.form.el.store.HPrefDataStore;

import java.util.ArrayList;

/**
 * Modified by Sarath
 */
public class VfcPlantationSampling extends HBaseFormActivity {

    public static final String VFC_APPLICABLE = "vfc_applicable";
    private String formStatus = "0";
    public static final String VFC_PLANTATION_SAMPLING = "VFCPlantationSampling";
    public boolean wantToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected HRootElement createRootElement() {

        HSection vfcSection = new HSection("Observations");
        ArrayList<HSection> sections = new ArrayList<>();

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(VFC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);

        formStatus = getSharedPreferences("Basic information", Context.MODE_PRIVATE).getString("formStatus", "0");
        if (pref.getString(VFC_APPLICABLE, "").equals("")) {
            pref.edit().putString(VFC_APPLICABLE, "No").apply();
        }

        HSection vfcApplicable = new HSection("Basic information of VFC");

        HPickerElement isApplicable = new HPickerElement(VFC_APPLICABLE, "Is VFC present?", "Select an option", true, "Yes|No", store);
        vfcApplicable.addEl(isApplicable);

        HTextEntryElement nameoFVFC = new HTextEntryElement(Database.NAME_OF_THE_VFC, "Name of" +
                " VFC if any", "Enter the name of VFC", true, store);
        vfcApplicable.addEl(nameoFVFC);
        isApplicable.addPosElement(nameoFVFC);

        HPickerElement isJFPM = new HPickerElement(Database.IS_JFPM_RAISED, "Is the plantation raised in JFPM " +
                "area?", "Select an option", true, "Yes|No", store);
        isApplicable.addPosElement(isJFPM);
        vfcApplicable.addEl(isJFPM);


        HPickerElement vfcPlantationActCheck = new HPickerElement(Database.IS_VFC_INVOLVED_IN_PLANTATION_ACTIVITY, "1.Whether the VFC was actually involved in the plantation activity?", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcPlantationActCheck);

        HTextView vfcInvolvStageLabel = new HTextView("a.At what stage was the VFC " +
                "involved?");
        vfcSection.addEl(vfcInvolvStageLabel);
        vfcPlantationActCheck.addPosElement(vfcInvolvStageLabel);

        HPickerElement loggingStage = new HPickerElement(Database.VFC_INVOLVED_LOGGING, "i" +
                ".Logging/extraction stage", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(loggingStage);
        vfcPlantationActCheck.addPosElement(loggingStage);

        HPickerElement advanceWorkStage = new HPickerElement(Database
                .VFC_INVOLVED_ADVANCED_WORK_STAGE, "ii.Advance work stage", "Select an option",
                true, "Yes|No", store);
        vfcSection.addEl(advanceWorkStage);
        vfcPlantationActCheck.addPosElement(advanceWorkStage);

        HPickerElement plantingStage = new HPickerElement(Database.VFC_INVOLVED_PLANTING_STAGE,
                "iii.Planting stage", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(plantingStage);
        vfcPlantationActCheck.addPosElement(plantingStage);

        HPickerElement maintenanceStage = new HPickerElement(Database
                .VFC_INVOLVED_MAINTENANCE_STAGE, "iv.Maintenance stage", "Select an option",
                true, "Yes|No", store);
        vfcSection.addEl(maintenanceStage);
        vfcPlantationActCheck.addPosElement(maintenanceStage);

        HPickerElement postMaintenanceStage = new HPickerElement(Database
                .VFC_INVOLVED_POST_MAINTENANCE_STAGE, "v.Post maintenance stage", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(postMaintenanceStage);
        vfcPlantationActCheck.addPosElement(postMaintenanceStage);

        HTextAreaEntryElement specifyOther = new HTextAreaEntryElement(Database.VFC_INVOLVED_STAGE_OTHER,
                "vi. Others (Specify)", "Please specify", true, store);
        vfcSection.addEl(specifyOther);
        vfcPlantationActCheck.addPosElement(specifyOther);


        HPickerElement plantingPrescCheck = new HPickerElement(Database.IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION, "2.Is there any micro plan prescriptions?", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(plantingPrescCheck);
        vfcPlantationActCheck.addPosElement(plantingPrescCheck);

        HTextAreaEntryElement otherSuggestionsDetails = new HTextAreaEntryElement(Database
                .IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION_REASONS, "Reasons", "Specify reason", true, store);
        plantingPrescCheck.addNegElement(otherSuggestionsDetails);
        vfcSection.addEl(otherSuggestionsDetails);

        HTextView vfcInvolved = new HTextView("3.How was the VFC involved ?");
        vfcSection.addEl(vfcInvolved);
        vfcPlantationActCheck.addPosElement(vfcInvolved);

        HPickerElement vfcApprovedProposal = new HPickerElement(Database
                .VFC_APPROVED_PLANTING_WORK_PROPOSAL, "i.VFC approved the planting work proposal", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcApprovedProposal);
        vfcPlantationActCheck.addPosElement(vfcApprovedProposal);

        HPickerElement vfcProvidedLabour = new HPickerElement(Database
                .VFC_PROVIDED_LABOUR_FOR_PLANTATION_WORK_PAYMENT, "ii.VFC provided the labour " +
                "force for the plantation work on payment", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcProvidedLabour);
        vfcPlantationActCheck.addPosElement(vfcProvidedLabour);

        HPickerElement vfcSupervisedPlantationWrk = new HPickerElement(Database
                .VFC_MEMBERS_SUPERVISED_PLANTATION_WORK, "iii.VFC members supervised the " +
                "plantation work", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcSupervisedPlantationWrk);
        vfcPlantationActCheck.addPosElement(vfcSupervisedPlantationWrk);

        HPickerElement vfcContributedPlantingWrk = new HPickerElement(Database
                .VFC_CONTRIBUTED_VFD_FUND_FOR_PLANTING_WORK, "iv.VFC contributed VFD fund for " +
                "the planting work", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcContributedPlantingWrk);
        vfcPlantationActCheck.addPosElement(vfcContributedPlantingWrk);

        HNumericElement totalFund = new HNumericElement(Database.VFC_CONTRIBUTED_VFD_FUND_TOTAL,
                "Total VFD fund contributed", "Please specify", true, store);
        vfcContributedPlantingWrk.addPosElement(totalFund);
        vfcSection.addEl(totalFund);


        HPickerElement vfcRaisedPlantation = new HPickerElement(Database
                .DEPT_PROVIDED_FUNDS_VFC_RAISED_PLANTATION, "v.Dept provided the funds and VFC raised the plantation", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcRaisedPlantation);
        vfcPlantationActCheck.addPosElement(vfcRaisedPlantation);

        HPickerElement vfcCarriedComplimentaryWrk = new HPickerElement(Database
                .VFC_CARRIED_OUT_COMPLIMENTARY_WORKS_LIKE_SMC, "vi.VFC carried out complimentary" +
                " works like SMC", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcCarriedComplimentaryWrk);
        vfcPlantationActCheck.addPosElement(vfcCarriedComplimentaryWrk);

        HPickerElement vfcPostMaintenanceDone = new HPickerElement(Database
                .POST_MAINTENANCE_DONE_BYVFC_WITHT_HEIR_OWN_FUNDS, "vii.Post maintenance is done" +
                " by VFC with their own funds", "Select an option", true, "Yes|No", store);
        vfcSection.addEl(vfcPostMaintenanceDone);
        vfcPlantationActCheck.addPosElement(vfcPostMaintenanceDone);

        HTextAreaEntryElement vfcOther = new HTextAreaEntryElement(Database.VFC_ANY_OTHER_SPECIFY, "viii" +
                ". Any others", "Please specify", true, store);
        vfcSection.addEl(vfcOther);
        vfcPlantationActCheck.addPosElement(vfcOther);


        HSection saveVfc = new HSection("");

        final HButtonElement verifyVfcEntries = new HButtonElement("Save");
        verifyVfcEntries.setElType(HElementType.SUBMIT_BUTTON);
        saveVfc.addEl(verifyVfcEntries);
        verifyVfcEntries.setOnClick(v -> {
            verifyVfcEntries.getButtonView().setFocusableInTouchMode(true);
            verifyVfcEntries.getButtonView().requestFocus();
            verifyVfcEntries.getButtonView().setFocusableInTouchMode(false);

            if (!checkFormData()) {
                showSaveFormDataAlert();
            } else {
                wantToExit = true;
                onBackPressed();
            }

        });

        isJFPM.addPosSection(vfcSection);

        if (!formStatus.equals("0")) {
            vfcApplicable.setNotEditable();
            vfcSection.setNotEditable();
            saveVfc.setNotEditable();
        }
        sections.add(vfcApplicable);
        sections.add(vfcSection);
        sections.add(saveVfc);

        return new HRootElement("Vfc Plantation Sampling", sections);
    }

    public void setToolBarTitle(String title, boolean addMode) {
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));


    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(VFC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);

    }

    @Override
    public void onBackPressed() {
        if (!checkFormData()) {
            if (wantToExit)
                super.onBackPressed();
            else
                showSaveFormDataAlert();
        } else {
            SharedPreferences pref = this.getApplicationContext().getSharedPreferences(VFC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
            pref.edit().putString(Database.FORM_FILLED_STATUS, "1").apply();
            super.onBackPressed();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

}
