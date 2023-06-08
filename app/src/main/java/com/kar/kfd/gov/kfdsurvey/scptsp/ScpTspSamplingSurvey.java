package com.kar.kfd.gov.kfdsurvey.scptsp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.location.AppSettingsFrag;
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
import com.ngohung.form.el.validator.ValidationStatus;
import com.ngohung.form.util.GPSTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.scptsp.SCPTSPBeneficiary.SCPTSP_BENEFICIARY_DETAILS;

/**
 * Modified by Sarath
 */
public class ScpTspSamplingSurvey extends HBaseFormActivity {
    public static final String SCP_TSP_SAMPLING = "ScpTspSampling";
    public static final String SCP_TSP_SAMPLING_SURVEY = "ScpTspSamplingSurvey";
    public static final String folderName = Constants.FORMTYPE_SCPTSP + File.separator + "Community";
    public static final String COMMUNITY = "COMMUNITY";
    public static final String INDIVIDUAL = "INDIVIDUAL";
    public static final String BENTYPE = "BENTYPE";
    private static final String TAG = ScpTspSamplingSurvey.class.getSimpleName();
    private SweetAlertDialog dialog;
    private SurveyCreation surveyCreation;
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    private String formStatus = "0";
    Database db;
    private ScpTspSamplingSurvey mSurvey = this;
    int formFilledStatus = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

        surveyCreation = new SurveyCreation();

    }

    @Override
    protected HRootElement createRootElement() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SCP_TSP_SAMPLING_SURVEY, Context.MODE_PRIVATE);

        HPrefDataStore store = new HPrefDataStore(pref);
        db = new Database(getApplicationContext());

        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }


        ArrayList<HSection> sections = new ArrayList<>();
        formStatus = pref.getString("formStatus", "0");

       /* String workCodeString = pref.getString(Database.WORK_CODE, "");
        if (!workCodeString.equals("")) {
            if (workCodeString.length() - 4 > 0) {
                pref.edit().putString("sample_number", workCodeString.substring(workCodeString.lastIndexOf("-") + 1)).apply();
            }
        }*/

        HSection basicInformationSection = new HSection("I.Basic information of SCP/TSP/Beneficiary oriented works");
        basicInformationSection.setVisible(true);


        HPickerElement evaluvatedAssetType = new HPickerElement(Database.NATURE_OF_BENEFIT, "Nature of benefit", "", true, "INDIVIDUAL|COMMUNITY", store);
        basicInformationSection.addEl(evaluvatedAssetType);
        evaluvatedAssetType.setEditable(false);

        HButtonElement locationDetails = new HButtonElement("Location (circle, division, range & other details)");
        basicInformationSection.addEl(locationDetails);
        locationDetails.setOnClick(v -> {
            AppSettingsFrag appSettingsFrag = new AppSettingsFrag();
            Bundle bundle = new Bundle();
            bundle.putString("preference", SCP_TSP_SAMPLING_SURVEY);
            appSettingsFrag.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, appSettingsFrag, "Home page");
            transaction.addToBackStack(null);
            transaction.commit();
        });

        HTextEntryElement workCode = new HTextEntryElement(Database.WORK_CODE, "Work Code", "Enter location details to generate work code", false, store);
        basicInformationSection.addEl(workCode);
        workCode.setNotEditable();


        HPickerElement programType = new HPickerElement(Database.PROGRAM_NAME, "1.Scheme ", "Select an option", true, "SCP|TSP|CAMPA|SHGY|Others", store);
        if (pref.getString(Database.NATURE_OF_BENEFIT, "").equals(COMMUNITY))
            basicInformationSection.addEl(programType);
        programType.setEditable(false);

        HTextEntryElement programTypeOther = new HTextEntryElement(Database.PROGRAM_NAME_OTHERS, "Specify Others", "specify", true, store);
        if (pref.getString(Database.NATURE_OF_BENEFIT, "").equals(COMMUNITY)) {
            programType.addElementForValue(programTypeOther, 4);
            basicInformationSection.addEl(programTypeOther);
        }


        HPickerElement implementationYear = new HPickerElement(Database.YEAR_OF_IMPLEMENTATION, "2.Year of implementation", "Select the year", true, "2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21", store);
        if (pref.getString(Database.NATURE_OF_BENEFIT, "").equals(COMMUNITY))
            basicInformationSection.addEl(implementationYear);

        HPickerElement typeofAsset = new HPickerElement(Database.TYPE_OF_ASSET, "3.Type of Asset Provided to Community", "Select Asset", true, "Solar Street Lights|Community Solar Heaters|Bio gas Plants(Community)|Others", store);
        // evaluvatedAssetType.addElementForValue(typeofAsset,1);
        if (pref.getString(Database.NATURE_OF_BENEFIT, "").equals(COMMUNITY))
            basicInformationSection.addEl(typeofAsset);


        HPickerElement typeofBenefit = new HPickerElement(Database.TYPE_OF_BENEFIT, "" +
                "Type of Benefit", "Select Benefit", true, "Supply of seedlings|Raising plantation on pvt land|Supply of bamboos|Supply of Wooden poles|Supply and installation of solar lamps|Supply and installation of cooking gas equipment/ LPG|Supply of solar geysers(water heaters)|Construction of gobar gas (bio gas) plants|Construction of smokeless chullahs|Supply of beehive boxes|Sarala ole|Astra ole|Fodder plot|Social security plantation|Fruit orchard plantation|others", store);
        if (pref.getString(Database.NATURE_OF_BENEFIT, "").equals(INDIVIDUAL))
            basicInformationSection.addEl(typeofBenefit);

        HTextEntryElement otherBenefit = new HTextEntryElement(Database.TYPE_OF_BENEFIT_OTHERS, "Specify Others", "Specify Other Benefit", true, store);
        typeofBenefit.addElementForValue(otherBenefit, 15);
        basicInformationSection.addEl(otherBenefit);

        HTextEntryElement otherAsset = new HTextEntryElement(Database.TYPE_OF_ASSET_OTHERS, "Specify Others", "Specify Other Asset", true, store);
        typeofAsset.addElementForValue(otherAsset, 3);
        basicInformationSection.addEl(otherAsset);

        HPickerElement participatingCommunityName = new HPickerElement(Database.NAME_OF_COMMUNITY, "4.Name of the participating community ( caste )", "Select participating community", true, "SC|ST|Others", store);
        if (pref.getString(Database.NATURE_OF_BENEFIT, "").equals(COMMUNITY))
            basicInformationSection.addEl(participatingCommunityName);

        HTextEntryElement otherCommunityName = new HTextEntryElement(Database.NAME_OF_COMMUNITY_OTHERS, "Specify Others", "Specify Other Community", true, store);
        participatingCommunityName.addElementForValue(otherCommunityName, 2);
        basicInformationSection.addEl(otherCommunityName);


        HSection evaluation = new HSection("II.Evaluation");


        HNumericElement householdsInLocation = new HNumericElement(Database.NO_OF_HOUSEHOLDS_IN_LOCATION, "1.Number of households in the location", "Enter the number of households in the location", true, store);

        householdsInLocation.setDecimal(false);

        HNumericElement householdsBenefited = new HNumericElement(Database.NO_OF_HOUSEHOLDS_BENEFITED, "2.Number of households benefited by the asset ", "Enter the number of households in the location", true, store);

        householdsInLocation.setDecimal(false);

        HNumericElement extentOfAsset = new HNumericElement(Database.EXTENT_OF_ASSET_WORK, "3.Extent of Asset/Work provided", "", true, store);

        HPickerElement unitOfAsset = new HPickerElement(Database.UNIT_OF_ASSET_WORK, "Unit of Asset/Work provided", "Select unit", true, "Ha|No|Km", store);
        householdsBenefited.addValidator(el -> {
            String noHouse = householdsInLocation.getValue().trim();
            String noExtent = el.getValue().trim();

            if (!TextUtils.isEmpty(noHouse) && !TextUtils.isEmpty(noExtent)) {
                if (Long.parseLong(el.getValue()) <= Long.parseLong(householdsInLocation.getValue())) {
                    return new ValidationStatus(true);
                } else {
                    el.setValue("");
                    return new ValidationStatus(false, "Value should be less than or equal to HouseHolds.");
                }
            } else {
                el.setValue("");
                return new ValidationStatus(false, "Value should be less than or equal to HouseHolds.");
            }

        });

        HNumericElement totalCost = new HNumericElement(Database.TOTAL_COST, "Total cost incurred in Rs", "Enter total cost", true, store);


        HPickerElement workAssetExists = new HPickerElement(Database.COMMUNITY_WORK_ASSET_EXISTS_NOW, "4.Does the work/asset exists now?", "Select an option", true, "Yes|No", store);


        HTextView workAssetLocationlabel = new HTextView("i.GPS location");

        workAssetExists.addPosElement(workAssetLocationlabel);

        HGpsElement gpsElement = new HGpsElement("Get GPS location", true);
        if (formStatus.equals("0")) {

            workAssetExists.addPosElement(gpsElement);
        }

        HTextEntryElement latitudeEl = new HTextEntryElement(Database.COMMUNITY_WORK_ASSET_LAT, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);

        workAssetExists.addPosElement(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.COMMUNITY_WORK_ASSET_LONG, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);

        workAssetExists.addPosElement(longitudeEl);


        HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName);
            bundle.putString("formId", pref.getString(Database.BENEFIT_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });

        workAssetExists.addPosElement(viewPhoto);

        HTextAreaEntryElement workAssetNonExist = new HTextAreaEntryElement(Database.WORK_ASSET_NON_EXIST, "Reasons", "Specify Reasons", true, store);
        workAssetExists.addNegElement(workAssetNonExist);

        HPickerElement workAssetPresent = new HPickerElement(Database.IS_WORK_ASSET_PRESENT, "5.Is the work/asset in use at present?", "Select option", true, "Yes|No", store);


        HTextAreaEntryElement workAssetPresentNo = new HTextAreaEntryElement(Database.IS_WORK_ASSET_PRESENT_NO_REASONS, "Reasons", "Specify Reasons", true, store);

        workAssetPresent.addNegElement(workAssetPresentNo);

        HPickerElement workAssetPresentYes = new HPickerElement(Database.IS_WORK_ASSET_PRESENT_CONDITION, "Present Condition of the Asset", "Select option", true, "Excellent|Very good|Good|Average|Poor", store);

        workAssetPresent.addPosElement(workAssetPresentYes);

        HPickerElement arrangementExist = new HPickerElement(Database.IS_ARRANGEMENT_EXIST, "6.Is there any arrangement existing for maintenance of asset", "Select option", true, "Yes|No", store);


        HTextAreaEntryElement arrangementNo = new HTextAreaEntryElement(Database.IS_ARRANGEMENT_EXIST_NO, "Reasons", "Specify Reasons", true, store);

        arrangementExist.addNegElement(arrangementNo);

        HTextAreaEntryElement arrangementYes = new HTextAreaEntryElement(Database.IS_ARRANGEMENT_EXIST_YES, "Specify details", "Specify ", true, store);

        arrangementExist.addPosElement(arrangementYes);

        HPickerElement properlyLabeled = new HPickerElement(Database.IS_ASSET_PROPERLY_LABELED, "7.Is the asset properly labeled", "Select option", true, "Yes|No", store);


        HPickerElement variationFNB = new HPickerElement(Database.ANY_VARIATION_FNB_MB, "8.Is there any variation in quantiy recorded in FNB/MB compared to the site?", "Select option", true, "Yes|No", store);


        HTextAreaEntryElement variationFNBYes = new HTextAreaEntryElement(Database.ANY_VARIATION_FNB_MB_YES, "Specify details", "Specify ", true, store);
        variationFNB.addPosElement(variationFNBYes);

        HPickerElement objectivesAchieved = new HPickerElement(Database.ARE_THE_PROGRAMME_OBJECTIVES_ACHIEVED, "9.Are the Scheme objective achieved?", "Select option", true, "Yes|No", store);


        HPickerElement objectivesAchievedYes = new HPickerElement(Database.ARE_THE_PROGRAMME_OBJECTIVES_ACHIEVED_YES, "Extent of Objective?", "Select option", true, "Very well|Some What|Not at all", store);
        objectivesAchieved.addPosElement(objectivesAchievedYes);

        HTextAreaEntryElement objectiveDetails = new HTextAreaEntryElement(Database.OBJECTIVES_ACHIEVED_DETAILS, "Specify details", "Specify ", true, store);
        objectivesAchieved.addNegElement(objectiveDetails);


        HTextAreaEntryElement suggestions = new HTextAreaEntryElement(Database.SUGGESTIONS_EVALUATION, "10.Suggestions of the Evaluation ", "Specify Suggestions", true, store);


        HButtonElement scptspBeneficiary = new HButtonElement("List of Beneficiaries");
        scptspBeneficiary.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
            i.putExtra("id", Integer.parseInt(pref.getString(Database.BENEFIT_ID, "0")));
            i.putExtra("List-type", Constants.SCP_TSP_BENEFICIARY_LIST);
            i.putExtra(Database.PART_TYPE, Constants.FORMTYPE_SCPTSP);
            i.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.BENEFICIARY_STATUS, "1").apply();
            startActivity(i);
        });
        //evaluvatedAssetType.addNegSection(scptspBeneficiarySection);

        HSection annotation = new HSection("Module IV. Other details of Evaluation");
        annotation.setVisible(false);


        //   annotation.addEl(viewPhoto);

        final HButtonElement submit = new HButtonElement(" Save ");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showSaveFormDataAlert();
            else {
                formFilledStatus = 1;
                submitScpTspSampling(0);
            }


        });

        /*final HButtonElement approve = new HButtonElement(" Approve ");
        approve.setElType(HElementType.SUBMIT_BUTTON);
        approve.setOnClick(v -> {
            approve.getButtonView().setFocusableInTouchMode(true);
            approve.getButtonView().requestFocus();
            approve.getButtonView().setFocusableInTouchMode(false);
            submitScpTspSampling(1);
        });*/
        final HButtonElement back = new HButtonElement("Close Form");
        back.setElType(HElementType.SUBMIT_BUTTON);
        back.setOnClick(v -> {
            back.getButtonView().setFocusableInTouchMode(true);
            back.getButtonView().requestFocus();
            back.getButtonView().setFocusableInTouchMode(false);
            onBackPressed();
        });

        if (pref.getString(Database.NATURE_OF_BENEFIT, "").equals(COMMUNITY)) {
            evaluation.addEl(householdsInLocation);
            evaluation.addEl(householdsBenefited);
            evaluation.addEl(extentOfAsset);
            evaluation.addEl(unitOfAsset);
            evaluation.addEl(totalCost);
            evaluation.addEl(workAssetExists);
            evaluation.addEl(workAssetLocationlabel);
            evaluation.addEl(gpsElement);
            evaluation.addEl(latitudeEl);
            evaluation.addEl(longitudeEl);
            evaluation.addEl(viewPhoto);
            evaluation.addEl(workAssetNonExist);
            evaluation.addEl(workAssetPresent);
            evaluation.addEl(workAssetPresentNo);
            evaluation.addEl(workAssetPresentYes);
            evaluation.addEl(arrangementExist);
            evaluation.addEl(arrangementNo);
            evaluation.addEl(arrangementYes);
            evaluation.addEl(properlyLabeled);
            evaluation.addEl(variationFNB);
            evaluation.addEl(variationFNBYes);
            evaluation.addEl(objectivesAchieved);
            evaluation.addEl(objectivesAchievedYes);
            evaluation.addEl(objectiveDetails);
//            evaluation.addEl(qualityRating);
            evaluation.addEl(suggestions);
        } else {
            evaluation.addEl(scptspBeneficiary);
        }
        if (!formStatus.equals("0")) {
            annotation.addEl(back);
            basicInformationSection.setNotEditable();
            //   communityAssetEvaluation.setNotEditable();
            evaluation.setNotEditable();
            annotation.setNotEditable();
        } else {
            annotation.addEl(submit);
            // annotation.addEl(approve);
        }

        sections.add(basicInformationSection);
        sections.add(evaluation);
        //   sections.add(communityAssetEvaluation);
        /*if (pref.getString(Database.NATURE_OF_BENEFIT,"").equals(INDIVIDUAL))
        sections.add(evaluation);*/
        sections.add(annotation);

        return rootElement = new HRootElement("SCP and TSP Sampling - Form 4", sections);

    }

    private void submitScpTspSampling(final int flag) {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SCP_TSP_SAMPLING_SURVEY, Context.MODE_PRIVATE);
        final SharedPreferences usernamePref = getSharedPreferences(Constants.LOGIN_PREF, Context.MODE_PRIVATE);

        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(flag == 0 ? R.layout.dialog_submit_form : R.layout.dialog_approve_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = SurveyCreation.getTableMetaData(Database.TABLE_SURVEY_MASTER, db);
            ContentValues cv = insertValuesToSurveyMaster(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
//            cv.put(Database.APP_ID, db.getAppId());
            cv.put(Database.SURVEYOR_NAME, usernamePref.getString(Database.EVALUATOR_NAME, ""));
            cv.put(Database.EVALUATION_TITLE, usernamePref.getString(Database.EVALUATION_TITLE, ""));
            cv.put(Database.EVALUATION_YEAR, usernamePref.getString(Database.EVALUATION_YEAR, ""));
            cv.put(Database.USER_LEVEL, usernamePref.getString(Database.USER_LEVEL, ""));
            cv.put(Database.OFFICEID, usernamePref.getString(Database.OFFICEID, "0"));
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            int benefitId = Integer.parseInt(pref.getString(Database.BENEFIT_ID, "0"));

            if (flag == 1) {
                cv.put(Database.FORM_STATUS, 1);
            }
            db.updateSurveyMasterWithFormId(formId, cv);
            tableMetadata = SurveyCreation.getTableMetaData(Database.TABLE_SCP_N_TSP_SURVEY, db);
            cv = insertValuesToScpTspSampling(formId, tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
           /* File mediaStorageDir = surveyCreation.getPictureFolder(Constants.FORMTYPE_SCPTSP, pref.getString(Database.FORM_ID, "0"));
            if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                cv.put(Database.PHOTOS_COUNT, mediaStorageDir.list().length);
            }*/
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
            cv.put(Database.BENEFIT_ID, benefitId);
            cv.put(Database.NATURE_OF_BENEFIT, pref.getString(Database.NATURE_OF_BENEFIT, ""));
            db.updateTableWithId(Database.TABLE_SCP_N_TSP_SURVEY, Database.BENEFIT_ID, cv);

            tableMetadata = SurveyCreation.getTableMetaData(Database.TABLE_SCP_N_TSP, db);
            cv = insertValuesToScpTspSampling(formId, tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
//               db.updateTableWithId(Database.TABLE_SCP_N_TSP,Database.FORM_ID,cv);
            db.updateTwoColumn(Database.TABLE_SCP_N_TSP, Database.FORM_ID, pref.getString(Database.NATURE_OF_BENEFIT, ""), cv);
            getSharedPreferences(SCPTSP_BENEFICIARY_DETAILS, Context.MODE_PRIVATE).edit().clear().apply();
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


    private ContentValues insertValuesToSurveyMaster(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
        long endingTimeStamp = System.currentTimeMillis() / 1000;
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
                    cv.put(columnName, Database.getTruncatedVarchar((pref.getString(columnName, "")), columnType));
                }
            } else {
                cv.put(columnName, pref.getString(columnName, ""));
            }
        }
        cv.put(Database.STARTING_TIMESTAMP, Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")));
        cv.put(Database.FORM_TYPE, Constants.FORMTYPE_SCPTSP);
        cv.put(Database.ENDING_TIMESTAMP, endingTimeStamp);
        GPSTracker gpsTracker = new GPSTracker(this);
        cv.put(Database.AUTOMATIC_LATITUDE, gpsTracker.getLatitude());
        cv.put(Database.AUTOMATIC_LONGITUDE, gpsTracker.getLongitude());
        return cv;

    }

    public void showSaveFormDataAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitScpTspSampling(0));

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private ContentValues insertValuesToScpTspSampling(long formId, ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
        ContentValues cv = new ContentValues();
        cv.put(Database.FORM_ID, String.valueOf(formId));
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


    @Override
    public void onBackPressed() {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SCP_TSP_SAMPLING_SURVEY, Context.MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if (!pref.getString("formStatus", "0").equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            } else {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getResources().getString(R.string.save_form));
            }
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }


    }


}
