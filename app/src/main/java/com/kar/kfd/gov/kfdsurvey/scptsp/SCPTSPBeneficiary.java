package com.kar.kfd.gov.kfdsurvey.scptsp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HGpsElement;
import com.ngohung.form.el.HMultiPickerElement;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.HTextView;
import com.ngohung.form.el.store.HPrefDataStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey.SCP_TSP_SAMPLING;
import static com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey.SCP_TSP_SAMPLING_SURVEY;
import static com.kar.kfd.gov.kfdsurvey.sdp.SDPBeneficiarySurvey.OTHERS_IF_ANY_SPECIFY;

/**
 * Modified by Sarath
 */
public class SCPTSPBeneficiary extends HBaseFormActivity {
    public static final String SCPTSP_BENEFICIARY_DETAILS = "SCPTSPBeneficiaryDetails";
    public static final String folderName = Constants.FORMTYPE_SCPTSP + File.separator + "Individual";
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    Database db;
    private float dialogButtonFontSize;
    private String formStatus = "0";
    private SweetAlertDialog dialog;
    private HNumericElement noOfSeedlingsPlanted;
    private HNumericElement noOfSeedlingsSurviving;
    private HNumericElement percentageOfSeedling;
    int formFilledStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;
    }


    @Override
    protected HRootElement createRootElement() {

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SCPTSP_BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences scpPref = getSharedPreferences(SCP_TSP_SAMPLING, Context.MODE_PRIVATE);
        SharedPreferences scpSamplingPref = getSharedPreferences(SCP_TSP_SAMPLING_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        db = Database.initializeDB(this);


        HSection benOrientEvalActivities = new HSection("I.Basic Information of Beneficiary");
        ArrayList<HSection> sections = new ArrayList<>();

        formStatus = pref.getString("formStatus", "0");
        HNumericElement beneficiaryNumberCode = new HNumericElement(Database.BENEFICIARY_CODE, "1.Beneficiary number/code", "Enter the beneficiary number/code", true, store);
        benOrientEvalActivities.addEl(beneficiaryNumberCode);

        HTextEntryElement beneficiaryName = new HTextEntryElement(Database.BENEFICIARY_NAME, "2.Name", "Enter the name ", true, store);
        benOrientEvalActivities.addEl(beneficiaryName);

        HTextEntryElement beneficiaryFatherName = new HTextEntryElement(Database
                .BENEFICIARY_FATHER_NAME, "3.Father's/Husband's name", "Enter the father's name" +
                " ",
                true, store);
        benOrientEvalActivities.addEl(beneficiaryFatherName);

        HNumericElement aadharNumber = new HNumericElement(Database.AADHAR_NUMBER, "4.Aadhar Number(if not available put 0)", "", true, store);
        benOrientEvalActivities.addEl(aadharNumber);

        HTextEntryElement participatingCommunityName = new HTextEntryElement(Database.NAME_OF_COMMUNITY, "5.Caste ", "Caste", true, store);
        benOrientEvalActivities.addEl(participatingCommunityName);
//        participatingCommunityName.setNotEditable();

        HPickerElement beneficiarySex = new HPickerElement(Database.BENEFICIARY_SEX, "6.Gender", "Select an option", true, "Male|Female", store);
        benOrientEvalActivities.addEl(beneficiarySex);

        HNumericElement beneficiaryAge = new HNumericElement(Database.BENEFICIARY_AGE, "7.Age", "Enter the age", true, store);
        benOrientEvalActivities.addEl(beneficiaryAge);
        beneficiaryAge.setDecimal(false);

        HPickerElement beneficiaryEducation = new HPickerElement(Database.BENEFICIARY_EDUCATION, "8.Education", "Select an option", true, "Illiterate|Below class X|Class X|PUC|Diploma|Graduate|PG|Doctorate", store);
        benOrientEvalActivities.addEl(beneficiaryEducation);

        HTextEntryElement gramPanchayatName = new HTextEntryElement(Database.GRAMA_PANCHAYAT_NAME, "9.Grampanchayat Name", "Enter Grampanchayat Name", true, store);
        benOrientEvalActivities.addEl(gramPanchayatName);
        gramPanchayatName.setValue(scpPref.getString(Database.GRAMA_PANCHAYAT_NAME, ""));

        HTextEntryElement villageName = new HTextEntryElement(Database.VILLAGE_NAME, "10.Village Name", "Enter Village Name", true, store);
        benOrientEvalActivities.addEl(villageName);
        villageName.setValue(scpPref.getString(Database.VILLAGE_NAME, ""));

        HPickerElement programType = new HPickerElement(Database.PROGRAM_NAME, "11.Scheme ", "Select an option", true, "SCP|TSP|CAMPA|SHGY|Others", store);
        benOrientEvalActivities.addEl(programType);

        HTextEntryElement programTypeOther = new HTextEntryElement(Database.PROGRAM_NAME_OTHERS, "Specify Others", "specify", true, store);
        programType.addElementForValue(programTypeOther, 4);
        benOrientEvalActivities.addEl(programTypeOther);

        HPickerElement implementationYear = new HPickerElement(Database.YEAR_OF_IMPLEMENTATION, "12.Year of implementation", "Select the year", true, "2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21", store);
        benOrientEvalActivities.addEl(implementationYear);


        HNumericElement totalLandholding = new HNumericElement(Database.LAND_HOLDING_ACRE, "13.Land Holding ( in acres ) ", "Enter land holding ( in acres )", true, store);
        benOrientEvalActivities.addEl(totalLandholding);

        HPickerElement beneficiaryTotalLandHold = new HPickerElement(Database
                .BENEFICIARY_LANDHOLDING_ACRES, "14.Total landholding", "Select an option",
                true, "0-1 acre|< 1-2 acres|2 - 5 acres|5 - 10 acres |> 10 acres",
                store);
        benOrientEvalActivities.addEl(beneficiaryTotalLandHold);



       /* HNumericElement no_of_members = new HNumericElement(Database.NUMBER_OF_MEMBERS, "16.Number of members in the family", "Enter number of members", true, store);
        benOrientEvalActivities.addEl(totalLandholding);*/

        HSection scptspBeneficiarySection = new HSection("II. Evaluation of individual " +
                "beneficiary work/asset");

        HPickerElement benAvailable = new HPickerElement(Database.BENEFICIARY_AVAILABLE, "1.Was the beneficiary available at the time of visit?", "Select option", true, "Yes|No", store);


        HPickerElement benAvailableNo = new HPickerElement(Database.BENEFICIARY_AVAILABLE_NO_REASONS, "Reasons", "Select option", true, "Door lock|Beneficiary refused to participate|Beneficiary unaware of Programme|Any other specify", store);
        benAvailable.addNegElement(benAvailableNo);


        HTextAreaEntryElement otherReasons = new HTextAreaEntryElement(Database.BENEFICIARY_AVAILABLE_NO_OTHER_REASONS, "Specify Others ", "Select option", true, store);
        benAvailableNo.addElementForValue(otherReasons, 3);
//        benAvailable.addNegElement(otherReasons);

        HPickerElement benAvailableYes = new HPickerElement(Database.BENEFICIARY_WILLING_TO_PARTICIPATE_IN_SURVEY, "Was the beneficiary Willing to participate in the Evaluation?", "Select option", true, "Yes|No", store);
        benAvailable.addPosElement(benAvailableYes);


        HGpsElement gpsElement = new HGpsElement("Get GPS location", true);


        HTextEntryElement latitudeEl = new HTextEntryElement(Database.SCP_BEN_LAT, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);


        HTextEntryElement longitudeEl = new HTextEntryElement(Database.SCP_BEN_LONG, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);


        HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName);
            //  bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formId", pref.getString(Database.BENEFICIARY_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });


        HPickerElement assetExist = new HPickerElement(Database.ASSET_WORK_EXIST, "2.Does the Asset/Benefit provided to beneficiary exist?", "Select option", true, "Yes|No", store);
        benAvailable.addPosElement(assetExist);

        HPickerElement assetExistNoReasons = new HPickerElement(Database.ASSET_WORK_EXIST_NO_REASONS, "Reasons", "Select option", true, "Asset not provided|Asset sold off|Asset destroyed ", store);
        assetExist.addNegElement(assetExistNoReasons);


        HTextAreaEntryElement nonParticipateReasons = new HTextAreaEntryElement(Database.BENEFICIARY_WILLING_NO_REASONS, "Specify Reasons", "Specify ", true, store);
        benAvailableYes.addNegElement(nonParticipateReasons);

        HNumericElement assetSize = new HNumericElement(Database.SIZE_QUANTITY_ASSET, "3.Size/quantity of the Asset provided", "Specify ", true, store);
        benAvailableYes.addPosElement(assetSize);

        HPickerElement unitOfAsset = new HPickerElement(Database.UNIT_OF_ASSET, "Unit Of Asset", "Select Unit of Asset", true, "Ha|No", store);
        benAvailableYes.addPosElement(unitOfAsset);

        HNumericElement totalCost = new HNumericElement(Database.TOTAL_COST, "Total cost incurred in Rs", "Enter total cost", true, store);
        benAvailableYes.addPosElement(totalCost);

        HPickerElement benContribution = new HPickerElement(Database.BENEFICIARY_CONTRIBUTION, "4.Was there any beneficiary contibution?", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(benContribution);


        HNumericElement amountContributed = new HNumericElement(Database.AMOUNT_CONTRIBUTION, "Amount of Beneficiary contributions", "Enter Amount", true, store);
        benContribution.addPosElement(amountContributed);

        HPickerElement economicalStatus = new HPickerElement(Database.ECONOMICAL_STATUS, "Economical Status of Beneficiary ", "Select option", true, "APL|BPL", store);
        benAvailableYes.addPosElement(economicalStatus);

        HMultiPickerElement assetsAvailable = new HMultiPickerElement(Database.ASSESTS_AVAILABLE, "Assets present with the beneficiary?", "Select option", true, "Own House|2 Wheeler|4 Wheeler|Tv", store);
        benAvailableYes.addPosElement(assetsAvailable);
//        benOrientEvalActivities.addEl(assetsAvailable);

        HNumericElement noOfMembers = new HNumericElement(Database.NO_OF_MEMBERS_IN_FAMILY, "Number of members available in the family", "Enter in numbers", true, store);
        benAvailableYes.addPosElement(noOfMembers);

        //  benContribution.addPosElement(workAssetLocationlabel);
       /* benContribution.addPosElement(gpsElement);
        benContribution.addPosElement(latitudeEl);
        benContribution.addPosElement(longitudeEl);
        benContribution.addPosElement(viewPhoto);*/


        HPickerElement assetUsed = new HPickerElement(Database.IS_ASSET_USED_BENEFICIARY, "5.Is the Asset being used by the Beneficiary", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(assetUsed);

        HPickerElement usageFrequency = new HPickerElement(Database.FREQUENCY_OF_USAGE, "Frequency of usage", "Select option", true, "Daily|Weekly|Occsionally|Rarely|Never", store);
        assetUsed.addPosElement(usageFrequency);

        HPickerElement assetmaintained = new HPickerElement(Database.IS_ASSET_MAINTAINED, "6.Is the asset being maintained?", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(assetmaintained);

        HTextAreaEntryElement assetNonmaintenace = new HTextAreaEntryElement(Database.IS_ASSET_MAINTAINED_NO_REASONS, "Specify Reasons", "Specify ", true, store);
        assetmaintained.addNegElement(assetNonmaintenace);

        HPickerElement assetCondition = new HPickerElement(Database.IS_ASSET_MAINTAINED_YES_CONDITION, "Present condition of the asset", "Select option", true, "Excellent|very Good|Good|Average|Poor", store);
        assetmaintained.addPosElement(assetCondition);

        HPickerElement assetLabeled = new HPickerElement(Database.IS_ASSET_PROPERLY_LABELED, "7.Is the work/asset properly labeled?", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(assetLabeled);

        HTextAreaEntryElement assetNonlabeled = new HTextAreaEntryElement(Database.IS_ASSET_PROPERLY_LABELED_NO_REASONS, "Specify Reasons", "Specify ", true, store);
        assetLabeled.addNegElement(assetNonlabeled);
        benAvailableYes.addPosElement(assetNonlabeled);

        HPickerElement isAssetCompanyProduct = new HPickerElement(Database.IS_ASSET_COMPANY_PRODUCT, "8.Is the asset a company product?", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(isAssetCompanyProduct);

        HTextAreaEntryElement brandDetails = new HTextAreaEntryElement(Database.BRAND_DETAILS, "Brand details", "Specify ", true, store);
        isAssetCompanyProduct.addPosElement(brandDetails);


        HTextAreaEntryElement detailsOfWarranty = new HTextAreaEntryElement(Database.DETAILS_OF_WARRANTY, "Details of warranty provided", "Specify ", true, store);
        isAssetCompanyProduct.addPosElement(detailsOfWarranty);

        HPickerElement variationFNBInd = new HPickerElement(Database.VARIATION_FNB, "9.Is there any variation in the quantity provided to Beneficiary compared to FNB/MB entries? ", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(variationFNBInd);

        HTextAreaEntryElement variationFNBIndDetails = new HTextAreaEntryElement(Database.VARIATION_FNB_DETAILS, "Specify Reasons", "Specify ", true, store);
        variationFNBInd.addPosElement(variationFNBIndDetails);
        benAvailableYes.addPosElement(variationFNBIndDetails);

        HPickerElement bensatisfied = new HPickerElement(Database.BENEFICIARY_SATSFIED, "10.Is the Beneficiary satisfied with the benefit", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(bensatisfied);

        HTextAreaEntryElement bensatisfiedNo = new HTextAreaEntryElement(Database.BENEFICIARY_SATSFIED_NO_REASONS, "Specify Reasons", "Specify ", true, store);
        bensatisfied.addNegElement(bensatisfiedNo);

        HPickerElement lpgNewConnection = new HPickerElement(Database.LPG_NEW_CONNECTION, "Is this a new connection?", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(lpgNewConnection);

        HTextAreaEntryElement lpgNewConnectionDetails = new HTextAreaEntryElement(Database.LPG_NEW_CONNECTION_DETAILS, "Details", "Specify ", true, store);
        lpgNewConnection.addNegElement(lpgNewConnectionDetails);

        HPickerElement cylinderBought = new HPickerElement(Database.SUBSEQUENT_CYLINDER_BOUGHT, "Is Subsequent cylinder bought?", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(cylinderBought);

        HTextAreaEntryElement cylinderBoughtReasons = new HTextAreaEntryElement(Database.SUBSEQUENT_CYLINDER_BOUGHT_REASONS, "Details", "Specify ", true, store);
        cylinderBought.addNegElement(cylinderBoughtReasons);

        HPickerElement typeOfRoof = new HPickerElement(Database.TYPE_OF_ROOF, "Type of Roof", "Select option", true, "Tiled|RCC|Others", store);
        benAvailableYes.addPosElement(typeOfRoof);

        HTextAreaEntryElement otherTypeOfRoof = new HTextAreaEntryElement(Database.OTHER_TYPE_OF_ROOF, "Other Type of Roof", "Enter Details", true, store);
        typeOfRoof.addElementForValue(otherTypeOfRoof, 2);
        HTextEntryElement plinthArea = new HTextEntryElement(Database.PLINTH_AREA, "Approximate plinth area", "Specify in sqft", true, store);
        benAvailableYes.addPosElement(plinthArea);

        HMultiPickerElement typesOfLivestock = new HMultiPickerElement(Database.LIVESTOCK_TYPES, "Types of Live Stock", "Select an option", true, "Cow|Buffalo|Sheep|Goat|Donkey|Others", store);
        benAvailableYes.addPosElement(typesOfLivestock);

        HTextEntryElement totalNoOfLivestock = new HTextEntryElement(Database.TOTAL_NO_OF_LIVESTOCK, "Total Number of Livestock", "Enter livestock", true, store);
        benAvailableYes.addPosElement(totalNoOfLivestock);

        HTextAreaEntryElement cowdetails = new HTextAreaEntryElement(Database.LIVESTOCK_COW_DETAILS, "Cow details", "Enter Details", true, store);
        cowdetails.setHidden(true);

        HTextAreaEntryElement buffalodetails = new HTextAreaEntryElement(Database.LIVESTOCK_BUFFALO_DETAILS, "Buffalo details", "Enter Details", true, store);
        buffalodetails.setHidden(true);

        HTextAreaEntryElement sheepdetails = new HTextAreaEntryElement(Database.LIVESTOCK_SHEEP_DETAILS, "Sheep details", "Enter Details", true, store);
        sheepdetails.setHidden(true);

        HTextAreaEntryElement goatdetails = new HTextAreaEntryElement(Database.LIVESTOCK_GOAT_DETAILS, "Goat details", "Enter Details", true, store);
        goatdetails.setHidden(true);

        HTextAreaEntryElement donkeydetails = new HTextAreaEntryElement(Database.LIVESTOCK_DONKEY_DETAILS, "Donkey details", "Enter Details", true, store);
        donkeydetails.setHidden(true);

        HTextAreaEntryElement otherlivestockdetails = new HTextAreaEntryElement(Database.OTHER_LIVESTOCK_DETAILS, "Other Livestock details", "Enter Details", true, store);
        otherlivestockdetails.setHidden(true);

        typesOfLivestock.setListener((which, options, values) -> {
            for (int i = 0; i < options.length; i++) {
                String option = options[i];
                boolean isChecked = !values[i];

                switch (option) {

                    case "Cow":
                        cowdetails.setHidden(isChecked);
                        break;

                    case "Buffalo":
                        buffalodetails.setHidden(isChecked);
                        break;

                    case "Sheep":
                        sheepdetails.setHidden(isChecked);
                        break;

                    case "Goat":
                        goatdetails.setHidden(isChecked);
                        break;

                    case "Donkey":
                        donkeydetails.setHidden(isChecked);
                        break;
                    case "Others":
                        otherlivestockdetails.setHidden(isChecked);
                        break;

                    default:
                        typesOfLivestock.clearValue();
                        cowdetails.setHidden(true);
                        buffalodetails.setHidden(true);
                        sheepdetails.setHidden(true);
                        goatdetails.setHidden(true);
                        donkeydetails.setHidden(true);
                        otherlivestockdetails.setHidden(true);
                        break;

                }

            }
        });
        HPickerElement biogasAppropriate = new HPickerElement(Database.BIOGAS_PLANT_APPROPRIATE, "Is the size of Biogas plant Appropriate", "Select option", true, "Yes|No", store);
        benAvailableYes.addPosElement(biogasAppropriate);

        HTextAreaEntryElement purposeOfUsage = new HTextAreaEntryElement(Database.PURPOSE_OF_USAGE, "Purpose of usage", "Specify ", true, store);
        biogasAppropriate.addPosElement(purposeOfUsage);

     /*   HPickerElement qualityRatingInd = new HPickerElement(Database.QUALITY_RATING_INDIVIDUAL,
                "11.Overall quality grading of the Asset/work on 1-10 point scale (1-Worst,10 is Excellent ) ?", "Enter quality rating", true, "1|2|3|4|5|6|7|8|9|10", store);
        benAvailableYes.addPosElement(qualityRatingInd);*/

        HTextAreaEntryElement suggestionsEvaluator = new HTextAreaEntryElement(Database.SUGGESTIONS_EVALUATOR, "11.Suggestions of the evaluator, if any", "Specify ", true, store);
        benAvailableYes.addPosElement(suggestionsEvaluator);

        HSection supply_of_Seedlings = new HSection("Evaluation of Performance of Seedlings");

        HTextElement beneficiaryAvailDetails = new HTextElement("A. Planting details");

        HNumericElement surveyNosWherePlanted = new HNumericElement(Database
                .SURVEY_NUMBERS_WHERE_PLANTED, "1.Survey no(s) where planted", "Enter survey no(s)", true, store);


        HMultiPickerElement species = new HMultiPickerElement(Database.SPECIES_SDP_BENEFICIARY, "Species procured", "Select species", true, "Others|" + db.getNamesOfSdpSpecies(), store);


        HTextEntryElement otherSpecies = new HTextEntryElement(Database.SPECIES_OTHER, OTHERS_IF_ANY_SPECIFY, "Specify Other Species", true, store);
        species.addElementForValue(otherSpecies, 0);

//        HButtonElement calcTotalSeedling = new HButtonElement("Calculate Total Seedling");


        noOfSeedlingsPlanted = new HNumericElement(Database
                .NUMBER_OF_SEEDLINGS_PLANTED, "1.Number of seedlings planted", "It will be calculated automatically", false, store);
        noOfSeedlingsPlanted.setEditable(false);
        noOfSeedlingsPlanted.setDecimal(false);

        noOfSeedlingsSurviving = new HNumericElement(Database.NUMBER_OF_SEEDLINGS_SURVIVING, "2.Total number of seedlings surviving", "It will be calculated automatically", false, store);
        noOfSeedlingsSurviving.setDecimal(false);
        noOfSeedlingsSurviving.setEditable(false);

        /*percentageOfSeedling = new HNumericElement(Database.SEEDLING_PERCENTAGE, "3.Survival Percentage ", "It will be calculated automatically", false, store);
        percentageOfSeedling.setNotEditable();*/

        /*calcTotalSeedling.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int benId = Integer.parseInt(pref.getString(Database.BENEFICIARY_CODE, "0"));
                int totalseedling = db.gettotalSeedling(benId, Constants.FORMTYPE_SCPTSP);
                noOfSeedlingsPlanted.setValue(String.valueOf(totalseedling));
                Log.i(Constants.SARATH, "onClick: " + totalseedling);
            }
        });*/

        HPickerElement typeOfPlanting = new HPickerElement(Database.TYPE_OF_PLANTING, "2.Type of " +
                "planting", "Select an option", true, "Agro forestry(bund plantation)|Farm " +
                "forestry(block plantation)", store);


        HTextAreaEntryElement typeOfPlantingmain = new HTextAreaEntryElement(Database
                .TYPE_OF_PLANTING_MAINCROP, "What is the maincrop?", "Specify", true, store);

        typeOfPlanting.addElementForValue(typeOfPlantingmain, 0);
        //typeOfPlanting.addElementForValue(typeOfPlantingmain,1);

        HNumericElement averageSpacement = new HNumericElement(Database.AVERAGE_SPACEMENT_METERS,
                "3.Average Espacement ( in meters ) ", "Enter average Espacement ( in meters ) ",
                true, store);

  /*      HButtonElement viewPhoto = new HButtonElement("View/Take photographs of Beneficiary/Field");
        viewPhoto.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName",folderName);
            bundle.putString("formId", pref.getString(Database.BENEFICIARY_ID, "0"));
            bundle.putString("formStatus",formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });*/

        //beneficiaryAvail.addPosElement(viewPhoto);


        //beneficiaryWillingToParticipate.addPosElement(altitudeEl);
        // ----just for saving the creation timestamp of location coordinates ----


      /*  HButtonElement map = new HButtonElement("Draw map");
        plantingDetails.addEl(map);
        map.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), MapGps.class);
            i.putExtra(Database.PREFERENCE,BENEFICIARY_DETAILS);
            startActivity(i);
        });*/
        //------------------------------------------------------------------------


        HPickerElement irrigation = new HPickerElement(Database.PLANTING_IRRIGATION_LEVEL,
                "4.Whether the Plantation is Irrigated or not?", "Select an option", true,
                "Yes|No", store);

        HPickerElement irrigationMethod = new HPickerElement(Database.PLANTING_IRRIGATION_METHOD,
                "Irrigation Method", "Select an option", true, "Canal|Open Well|Bore Well|Drip " +
                "Irrigation|Farm Pond|Other (specify)", store);

        irrigation.addPosElement(irrigationMethod);

        HTextAreaEntryElement irrigationOther = new HTextAreaEntryElement(Database.PLANTING_IRRIGATION_LEVEL_OTHER_DETAILS, "Specify irrigation type", "Enter details", true, store);
        irrigationMethod.addElementForValue(irrigationOther, 5);


        HPickerElement fertilizeApplication = new HPickerElement(Database
                .PLANTING_FERTILIZE_USED, "5.Fertilizer application", "Select an option", true, "Yes|No", store);


        HTextAreaEntryElement fertilizeApplicationDetails = new HTextAreaEntryElement(Database
                .PLANTING_FERTILIZE_USED_DETAILS, "Specify", "Enter details", true, store);

        fertilizeApplication.addPosElement(fertilizeApplicationDetails);


        HPickerElement pruning = new HPickerElement(Database.PLANTING_PRUNINGE_DONE, "6.Pruning",
                "Select an option", true, "Yes|No", store);


        HTextAreaEntryElement otherTreatments = new HTextAreaEntryElement(Database
                .PLANTING_OTHER_TREATMENT_DETAILS, "7.Other treatments,if any", "Enter other treatments done", true, store);


        HPickerElement totalExpenditureAvailable = new HPickerElement(Database
                .PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_APPLICABLE, "8. Is the total expenditure " +
                "incurred information available?", "Select an option", true, "Yes|No", store);


        HNumericElement totalExpenditure = new HNumericElement(Database.PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_RS, "Total expenditure incurred till date ( in Rupees )", "Enter total expenditure in rupees", true, store);
        totalExpenditureAvailable.addPosElement(totalExpenditure);


        HTextElement seedlingSection = new HTextElement("B: Performance of seedlings");


        HButtonElement removeSeedling = new HButtonElement("Seedling Details");
        removeSeedling.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyList.class);
            i.putExtra("id", Integer.parseInt(pref.getString(Database.BENEFICIARY_ID, "0")));
            i.putExtra("List-type", Constants.SEEDLINGS_LIST);
            i.putExtra("formStatus", formStatus);
            i.putExtra(Database.PART_TYPE, Constants.FORMTYPE_SCPTSP);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            i.putExtra("formId", formId);
            startActivity(i);
        });

        HTextElement mortalityOfSeedling = new HTextElement("C: Mortality of Seedling");

        HTextAreaEntryElement factorsOfMortality = new HTextAreaEntryElement(Database.MORTALITY_OF_SEEDLING, "Factors responsible for Mortality of Seedlings ", "Specify factors", true, store);

        HTextAreaEntryElement factorReduced = new HTextAreaEntryElement(Database.HOW_IT_WILL_BE_REDUCED, "How It will be reduced?", "Specify ", true, store);


        HTextElement incentivesRecieved = new HTextElement("C: Incentives received");


        HTextView mgnreegsIncentive = new HTextView("1.Payment received for earth works from " +
                "MGNREGS etc.");


        HPickerElement mgnreegsYearReceived = new HPickerElement(Database.PAYMENT_RECEIVED_FROM_MGNREEGS_YEAR, "Year in which received", "Enter year", true, "2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21|Not Applicable", store);


        HNumericElement mgnreegsAmount = new HNumericElement(Database.PAYMENT_RECEIVED_FROM_MGNREEGS_RS, "Amount in Rupees", "Enter amount in rupees", true, store);

        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 0);
        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 1);
        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 2);
        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 3);
        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 4);
        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 5);
        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 6);
        mgnreegsYearReceived.addElementForValue(mgnreegsAmount, 7);

        HTextView otherDeptIncentive = new HTextView("2.Subsidy received towards micro " +
                "irrigation from other departments");


        HPickerElement otherDeptYearReceived = new HPickerElement(Database.SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_YEAR, "Year in which received", "Enter year", true, "2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|Not Applicable", store);


        HNumericElement otherDeptAmount = new HNumericElement(Database.SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_RS, "Amount in Rupees", "Enter amount in rupees", true, store);

        otherDeptYearReceived.addElementForValue(otherDeptAmount, 0);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 1);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 2);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 3);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 4);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 5);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 6);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 7);

       /* HTextView kfdIncentive = new HTextView("3.Incentive received from KFD under KAPY");


        HPickerElement kfdIncentiveYearOneAvailable = new HPickerElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1_APPLICABLE, "Year 1 cash incentive available", "Select option", true, "Yes|No", store);


        HPickerElement modePayment = new HPickerElement(Database.MODE_PAYMENT1, "Mode Payment", "Enter payment mode", true, "RTGS|NEFT|DD|Cash", store);

        kfdIncentiveYearOneAvailable.addPosElement(modePayment);


        final HNumericElement kfdIncentiveYearOne = new HNumericElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1, "For Year 1 ( in Rupees ) :", "Enter cash incentive for year 1 ( in Rupees )", true, store);

        kfdIncentiveYearOneAvailable.addPosElement(kfdIncentiveYearOne);


        HPickerElement kfdIncentiveYearTwoAvailable = new HPickerElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2_APPLICABLE, "Year 2 cash incentive available", "Select option", true, "Yes|No", store);


        HPickerElement modePayment2 = new HPickerElement(Database.MODE_PAYMENT2, "Mode Payment",
                "Enter payment mode", true, "RTGS|NEFT|DD|Cash", store);

        kfdIncentiveYearTwoAvailable.addPosElement(modePayment2);

        final HNumericElement kfdIncentiveYearTwo = new HNumericElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2, "For Year 2 ( in Rupees ) :", "Enter cash incentive for year 2 ( in Rupees )", true, store);

        kfdIncentiveYearTwoAvailable.addPosElement(kfdIncentiveYearTwo);

        HPickerElement kfdIncentiveYearThreeAvailable = new HPickerElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3_APPLICABLE, "Year 3 cash incentive available", "Select option", true, "Yes|No", store);


        HPickerElement modePayment3 = new HPickerElement(Database.MODE_PAYMENT3, "Mode Payment",
                "Enter payment mode", true, "RTGS|NEFT|DD|Cash", store);

        kfdIncentiveYearTwoAvailable.addPosElement(modePayment3);
        kfdIncentiveYearThreeAvailable.addPosElement(modePayment3);

        final HNumericElement kfdIncentiveYearThree = new HNumericElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3, "For Year 3 ( in Rupees ) :", "Enter cash incentive for year 3 ( in Rupees )", true, store);

        kfdIncentiveYearThreeAvailable.addPosElement(kfdIncentiveYearThree);*/

      /*  final HButtonElement calculateTotalIncentives = new HButtonElement("Calculate total incentive recieved");


        final HTextEntryElement totalIncentives = new HTextEntryElement(Database.TOTAL_INCENTIVE, "Total :", "Click the button to calculate the total", true, store);
        totalIncentives.setNotEditable();

        calculateTotalIncentives.setOnClick(v -> {
            calculateTotalIncentives.getButtonView().setFocusableInTouchMode(true);
            calculateTotalIncentives.getButtonView().requestFocus();
            calculateTotalIncentives.getButtonView().setFocusableInTouchMode(false);
            float mgnreegesAmount, otherAmount;

            try {
                mgnreegesAmount = Float.parseFloat(mgnreegsAmount.getValue());
            } catch (NumberFormatException e) {
                mgnreegesAmount = 0;
            }
            try {
                otherAmount = Float.parseFloat(otherDeptAmount.getValue());
            } catch (NumberFormatException e) {
                otherAmount = 0;
            }

            float total = mgnreegesAmount + otherAmount;
            totalIncentives.setValue(String.valueOf(total));
            totalIncentives.getEditText().setText(String.valueOf(total));
        });*/


      /*  HPickerElement otherRewards = new HPickerElement(Database.INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED, "4.Any awards/rewards/prizes received", "Select an option", true, "Yes|No", store);


        HTextAreaEntryElement otherRewardsDetails = new HTextAreaEntryElement(Database.INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED_DETAILS, "Details", "Enter details", true, store);

        otherRewards.addPosElement(otherRewardsDetails);*/

        HTextElement otherGeneralInformation = new HTextElement("D: Beneficiary Feedback");



        HPickerElement satisfiedWithQuality = new HPickerElement(Database
                .ARE_YOU_SATISFIED_WITH_SEEDLING_QUALITY, "1.Is he/she satisfied with the quality" +
                " of  seedlings procured?", "Select an option", true, "Yes|No", store);


        HTextAreaEntryElement unsatisfiedReasons = new HTextAreaEntryElement(Database.REASONS_FOR_DISSATISFACTION_WITH_SEEDLING_QUALITY, "Reasons", "Enter reasons why you are unsatisfied with the quality", true, store);
        satisfiedWithQuality.addNegElement(unsatisfiedReasons);


        HPickerElement interestedInBuying = new HPickerElement(Database
                .INTERESTED_IN_BUYING_MORE_SEEDLINGS_FROM_KFD, "2.Whether he/she is interested in" +
                " " +
                "getting more seedlings from KFD?", "Select an option", true, "Yes|No", store);

        HTextAreaEntryElement userInterestedSpecies = new HTextAreaEntryElement(Database.USER_INTERESTED_SPECIES, "Specify Species", "Specify", true, store);
        interestedInBuying.addPosElement(userInterestedSpecies);

        HPickerElement specifiedSupport = new HPickerElement(Database
                .DO_YOU_NEED_SPECIFIC_SUPPORT_FROM_GOVT, "3.Does he/she require any specific " +
                "support from the government?", "Select an option", true, "Yes|No", store);


        HTextAreaEntryElement specifiedSupportDetails = new HTextAreaEntryElement(Database
                .DETAILS_OF_SPECIFIC_SUPPORT_NEEDED_FROM_GOVT, "Specify", "Specify the details of " +
                "support required", true, store);

        specifiedSupport.addPosElement(specifiedSupportDetails);

        HTextAreaEntryElement farmSuggestions = new HTextAreaEntryElement(Database
                .SUGGESTIONS_TO_IMPROVE_AGRO_FORESTRY, "4.Any suggestions/comments given by " +
                "beneficiary? ", "Enter suggestions", true, store);

        String typeOfBenefit = scpSamplingPref.getString(Database.TYPE_OF_BENEFIT, "");
        scptspBeneficiarySection.addEl(benAvailable);
        scptspBeneficiarySection.addEl(benAvailableNo);
        scptspBeneficiarySection.addEl(gpsElement);
        scptspBeneficiarySection.addEl(latitudeEl);
        scptspBeneficiarySection.addEl(longitudeEl);
        scptspBeneficiarySection.addEl(viewPhoto);
        scptspBeneficiarySection.addEl(assetExist);
        scptspBeneficiarySection.addEl(assetExistNoReasons);
        scptspBeneficiarySection.addEl(otherReasons);
        scptspBeneficiarySection.addEl(benAvailableYes);
        scptspBeneficiarySection.addEl(nonParticipateReasons);
        scptspBeneficiarySection.addEl(assetSize);
        scptspBeneficiarySection.addEl(unitOfAsset);
        scptspBeneficiarySection.addEl(totalCost);
        scptspBeneficiarySection.addEl(benContribution);
        scptspBeneficiarySection.addEl(amountContributed);
        scptspBeneficiarySection.addEl(economicalStatus);
        scptspBeneficiarySection.addEl(assetsAvailable);
        scptspBeneficiarySection.addEl(noOfMembers);
        scptspBeneficiarySection.addEl(assetUsed);
        scptspBeneficiarySection.addEl(usageFrequency);
        scptspBeneficiarySection.addEl(assetmaintained);
        scptspBeneficiarySection.addEl(assetNonmaintenace);
        scptspBeneficiarySection.addEl(assetCondition);
        scptspBeneficiarySection.addEl(assetLabeled);
        scptspBeneficiarySection.addEl(assetNonlabeled);
        scptspBeneficiarySection.addEl(isAssetCompanyProduct);
        scptspBeneficiarySection.addEl(brandDetails);
        scptspBeneficiarySection.addEl(detailsOfWarranty);
        scptspBeneficiarySection.addEl(variationFNBInd);
        scptspBeneficiarySection.addEl(variationFNBIndDetails);
        scptspBeneficiarySection.addEl(bensatisfied);
        scptspBeneficiarySection.addEl(bensatisfiedNo);
        if (typeOfBenefit.equalsIgnoreCase("Supply and installation of cooking gas equipment/ LPG")) {
            scptspBeneficiarySection.addEl(lpgNewConnection);
            scptspBeneficiarySection.addEl(lpgNewConnectionDetails);
            scptspBeneficiarySection.addEl(cylinderBought);
            scptspBeneficiarySection.addEl(cylinderBoughtReasons);
        }
        if (typeOfBenefit.equalsIgnoreCase("Supply of solar geysers(water heaters)")) {
            scptspBeneficiarySection.addEl(typeOfRoof);
            scptspBeneficiarySection.addEl(otherTypeOfRoof);
            scptspBeneficiarySection.addEl(plinthArea);
        }
        if (typeOfBenefit.equalsIgnoreCase("Fodder plot") || typeOfBenefit.equalsIgnoreCase("Construction of gobar gas (bio gas) plants")) {

            if (typeOfBenefit.equalsIgnoreCase("Construction of gobar gas (bio gas) plants"))
                scptspBeneficiarySection.addEl(biogasAppropriate);

            scptspBeneficiarySection.addEl(typesOfLivestock);
            scptspBeneficiarySection.addEl(totalNoOfLivestock);
            scptspBeneficiarySection.addEl(cowdetails);
            scptspBeneficiarySection.addEl(buffalodetails);
            scptspBeneficiarySection.addEl(sheepdetails);
            scptspBeneficiarySection.addEl(goatdetails);
            scptspBeneficiarySection.addEl(donkeydetails);
            scptspBeneficiarySection.addEl(otherlivestockdetails);
        }
//        scptspBeneficiarySection.addEl(qualityRatingInd);
        scptspBeneficiarySection.addEl(suggestionsEvaluator);
        if (typeOfBenefit.equalsIgnoreCase("Supply of Seedlings") || typeOfBenefit.equalsIgnoreCase("Social security plantation") || typeOfBenefit.equalsIgnoreCase("Fruit orchard plantation")) {
            supply_of_Seedlings.addEl(beneficiaryAvailDetails);
            supply_of_Seedlings.addEl(surveyNosWherePlanted);
            supply_of_Seedlings.addEl(species);
            supply_of_Seedlings.addEl(otherSpecies);
//            supply_of_Seedlings.addEl(calcTotalSeedling);
//            supply_of_Seedlings.addEl(percentageOfSeedling);
            supply_of_Seedlings.addEl(typeOfPlanting);
            supply_of_Seedlings.addEl(typeOfPlantingmain);
            supply_of_Seedlings.addEl(averageSpacement);
            supply_of_Seedlings.addEl(irrigation);
            supply_of_Seedlings.addEl(irrigationMethod);
            supply_of_Seedlings.addEl(irrigationOther);
            supply_of_Seedlings.addEl(fertilizeApplication);
            supply_of_Seedlings.addEl(fertilizeApplicationDetails);
            supply_of_Seedlings.addEl(pruning);
            supply_of_Seedlings.addEl(otherTreatments);
            supply_of_Seedlings.addEl(totalExpenditureAvailable);
            supply_of_Seedlings.addEl(totalExpenditure);
            supply_of_Seedlings.addEl(seedlingSection);
            supply_of_Seedlings.addEl(noOfSeedlingsPlanted);
            supply_of_Seedlings.addEl(noOfSeedlingsSurviving);
            supply_of_Seedlings.addEl(removeSeedling);
            if (typeOfBenefit.equalsIgnoreCase("Supply of Seedlings")) {
                supply_of_Seedlings.addEl(incentivesRecieved);
                supply_of_Seedlings.addEl(mgnreegsIncentive);
                supply_of_Seedlings.addEl(mgnreegsYearReceived);
                supply_of_Seedlings.addEl(mgnreegsAmount);
                supply_of_Seedlings.addEl(otherDeptIncentive);
                supply_of_Seedlings.addEl(otherDeptYearReceived);
                supply_of_Seedlings.addEl(otherDeptAmount);
           /* supply_of_Seedlings.addEl(kfdIncentive);
            supply_of_Seedlings.addEl(kfdIncentiveYearOneAvailable);
            supply_of_Seedlings.addEl(modePayment);
            supply_of_Seedlings.addEl(kfdIncentiveYearOne);
            supply_of_Seedlings.addEl(kfdIncentiveYearTwoAvailable);
            supply_of_Seedlings.addEl(modePayment2);
            supply_of_Seedlings.addEl(kfdIncentiveYearTwo);
            supply_of_Seedlings.addEl(kfdIncentiveYearThreeAvailable);
            supply_of_Seedlings.addEl(modePayment3);
            supply_of_Seedlings.addEl(kfdIncentiveYearThree);*/
             /*   if (formStatus.equals("0")) {
                    supply_of_Seedlings.addEl(calculateTotalIncentives);
                }
                supply_of_Seedlings.addEl(totalIncentives);*/
            /*supply_of_Seedlings.addEl(otherRewards);
            supply_of_Seedlings.addEl(otherRewardsDetails);*/

            } else if (typeOfBenefit.equalsIgnoreCase("Social security plantation") || typeOfBenefit.equalsIgnoreCase("Fruit orchard plantation")) {
                supply_of_Seedlings.addEl(mortalityOfSeedling);
                supply_of_Seedlings.addEl(factorsOfMortality);
                supply_of_Seedlings.addEl(factorReduced);
            }

            supply_of_Seedlings.addEl(otherGeneralInformation);
            /*supply_of_Seedlings.addEl(problemsFaced);
            supply_of_Seedlings.addEl(problemsFacedDetails);
            supply_of_Seedlings.addEl(privateSourcesDetails);*/
            supply_of_Seedlings.addEl(satisfiedWithQuality);
            supply_of_Seedlings.addEl(unsatisfiedReasons);
           /* supply_of_Seedlings.addEl(privateSourcesSeedlings);
            supply_of_Seedlings.addEl(privateSourcesReasons);
            supply_of_Seedlings.addEl(privateSourcesPerformance);*/
            supply_of_Seedlings.addEl(interestedInBuying);
            supply_of_Seedlings.addEl(userInterestedSpecies);
            supply_of_Seedlings.addEl(specifiedSupport);
            supply_of_Seedlings.addEl(specifiedSupportDetails);
            supply_of_Seedlings.addEl(farmSuggestions);

        }

        HSection annotation = new HSection("");
        annotation.setVisible(false);

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
                submitBeneficiaryDetails();
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
            annotation.addEl(back);
            annotation.setNotEditable();
        } else {
            annotation.addEl(submit);
        }
        benAvailableYes.addPosSection(supply_of_Seedlings);
        sections.add(benOrientEvalActivities);
        sections.add(scptspBeneficiarySection);
        sections.add(supply_of_Seedlings);
        sections.add(annotation);


        return rootElement = new HRootElement("SCP and TSP beneficiaries", sections);

    }

    private void setVisibilityBeneficiary(HPickerElement wasBeneficiaryAvailableReason, HPickerElement wasBeneficiaryParticipating, HElement benefitEvaluation) {
        int value = wasBeneficiaryAvailableReason.getIndex();
        if (value == 1) {
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 2);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 3);
            wasBeneficiaryParticipating.addPosElement(benefitEvaluation);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 1);
        } else if (value == 2) {
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 3);
            wasBeneficiaryParticipating.addPosElement(benefitEvaluation);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 1);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 2);
        } else if (value == 3) {
            wasBeneficiaryParticipating.addPosElement(benefitEvaluation);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 1);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 2);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 3);
        } else {
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 1);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 2);
            wasBeneficiaryAvailableReason.addElementForValue(benefitEvaluation, 3);
            wasBeneficiaryParticipating.addPosElement(benefitEvaluation);
        }
    }

    private void submitBeneficiaryDetails() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SCPTSP_BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SCP_TSP_BENIFICIARY, db);
            ContentValues cv = insertValuesToSCPTSPBeneficiaries(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            if (Integer.parseInt(pref.getString(Database.BENIFICIARY_ID, "0")) == 0) {
                long beneficiaryId = db.insertValuesIntoSCPTSPBeneficiaries(cv);
            } else {
                cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
                cv.put(Database.BENIFICIARY_ID, Integer.parseInt(pref.getString(Database.BENIFICIARY_ID, "0")));
                db.updateTableWithId(Database.TABLE_SCP_TSP_BENIFICIARY, Database.BENIFICIARY_ID, cv);
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

    private ContentValues insertValuesToSCPTSPBeneficiaries(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
        ContentValues cv = new ContentValues();
        long endingTimeStamp = System.currentTimeMillis() / 1000;
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
        cv.put(Database.CREATION_TIMESTAMP, endingTimeStamp);
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
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SCPTSP_BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
        String benId = pref.getString(Database.BENEFICIARY_ID, "0");
        int totalseedling = db.gettotalSeedling(benId, Constants.FORMTYPE_SCPTSP);
        noOfSeedlingsPlanted.setValue(String.valueOf(totalseedling));
        noOfSeedlingsSurviving.setValue(String.valueOf(db.gettotalSurving(benId, Constants.FORMTYPE_SCPTSP)));
//        calculatePercentage();
    }

/*    public void calculatePercentage() {

        float planted = 0, plantedSurviving = 0;

        try {
            if (!noOfSeedlingsPlanted.getValue().equals(""))
                planted = Float.parseFloat(noOfSeedlingsPlanted.getValue());
        } catch (NumberFormatException e) {
            planted = 0;
        }
        try {
            if (!noOfSeedlingsSurviving.getValue().equals(""))
                plantedSurviving = Float.parseFloat(noOfSeedlingsSurviving.getValue());
        } catch (NumberFormatException e) {
            plantedSurviving = 0;
        }

        float seedlingPercentage = 0;


        if (planted >= plantedSurviving) {
            if (planted != 0) {
                seedlingPercentage = (plantedSurviving / planted);
                seedlingPercentage = seedlingPercentage * 100;
            }

        } else {
            Snackbar plantedSnack = Snackbar.make(findViewById(android.R.id.content), "Surving seedlings must be less than planted to calculate percentage", Snackbar.LENGTH_LONG);
            plantedSnack.show();
        }

        percentageOfSeedling.setValue(String.valueOf(seedlingPercentage));
    }*/

    public void showSaveFormDataAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitBeneficiaryDetails());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences pref = getSharedPreferences(SCPTSP_BENEFICIARY_DETAILS, Context.MODE_PRIVATE);

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
