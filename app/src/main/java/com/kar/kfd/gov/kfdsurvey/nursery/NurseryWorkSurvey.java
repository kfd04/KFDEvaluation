package com.kar.kfd.gov.kfdsurvey.nursery;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.BuildConfig;
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
import com.ngohung.form.el.HMultiPickerElement;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.store.HPrefDataStore;
import com.ngohung.form.util.GPSTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.Database.KFD_NURSERY_WORKS_BAGGED_SEEDLINGS;
import static com.kar.kfd.gov.kfdsurvey.Database.KFD_NURSERY_WORKS_SEED_BED;


/**
 * Created by Sunil on 22-05-2017.
 */

public class NurseryWorkSurvey extends HBaseFormActivity {
    public static final String NURSERY_WORK_SURVEY = "NurseryWorkSurvey";
    public static final String WAS_WORK_COMPLETE = "was_work_complete";
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private SweetAlertDialog dialog;
    private SurveyCreation surveyCreation;
    private boolean editable = true;
    private float dialogButtonFontSize;
    private String formStatus = "0";
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        surveyCreation = new SurveyCreation();

    }


    @Override
    protected HRootElement createRootElement() {
/*        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());*/

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(NURSERY_WORK_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);

        db = new Database(getApplicationContext());
        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }
        formStatus = pref.getString("formStatus", "0");


        if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) != 0) {
            editable = false;
            if (!pref.getString(Database.WHEN_WORK_COMPLETED_YEAR, "").isEmpty()) {
                pref.edit().putString(WAS_WORK_COMPLETE, "Yes").apply();
            } else {
                pref.edit().putString(WAS_WORK_COMPLETE, "No").apply();
            }
            pref.edit().putString("time_taken_months", pref.getString(Database.TIME_TAKEN_TO_COMPLETE_WORK_MONTHS, "")).apply();
            pref.edit().putString("volume_of_work", pref.getString(Database.ORIGINAL_WORK_DIMENSION_VOLUME_MTRS, "")).apply();
            String quality = pref.getString(Database.ORIGINAL_WORK_QUALITY_RATING, "");
            if (quality.length() <= 1) {
                pref.edit().putString(Database.ORIGINAL_WORK_QUALITY_RATING, "0" + quality).apply();
            }
            quality = pref.getString(Database.MAINTENANCE_WORK_QUALITY_RATING, "");
            if (quality.length() <= 1) {
                pref.edit().putString(Database.MAINTENANCE_WORK_QUALITY_RATING, "0" + quality).apply();
            }
        } else {
            editable = true;
        }

        ArrayList<HSection> sections = new ArrayList<>();

        HSection workLocationSection = new HSection("Module I. Work location");

        HButtonElement locationDetails = new HButtonElement("Location Details");
        workLocationSection.addEl(locationDetails);
        locationDetails.setOnClick(v -> {
            AppSettingsFrag appSettingsFrag = new AppSettingsFrag();
            Bundle bundle = new Bundle();
            bundle.putString("preference", NURSERY_WORK_SURVEY);
            appSettingsFrag.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, appSettingsFrag, "Home page");
            transaction.addToBackStack(null);
            transaction.commit();
        });

        HTextEntryElement workCode = new HTextEntryElement(Database.WORK_CODE, "Work Code", "Enter location details to generate work code", false, store);
        workLocationSection.addEl(workCode);
        workCode.setNotEditable();
//        workCode.setValue(generateWorkCode(pref, db));
        //  workCode.setValue(this.getApplicationContext().getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE).getString(Database.WORK_CODE,null));

        HTextEntryElement nameOfNursery = new HTextEntryElement(Database.NURSERY_NAME, "10.Name of the nursery", "Enter the name of nursery", true, store);
        workLocationSection.addEl(nameOfNursery);


        HGpsElement gpsElement = new HGpsElement("Get GPS location", true);
        if (formStatus.equals("0")) {
            workLocationSection.addEl(gpsElement);
        }

        HTextEntryElement latitudeEl = new HTextEntryElement(Database.NURSERY_LATITUDE, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);
        workLocationSection.addEl(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.NURSERY_LONGITUDE, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);
        workLocationSection.addEl(longitudeEl);


        HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(v -> {

            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", Constants.FORMTYPE_NURSERY_WORK);
            bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");

            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        workLocationSection.addEl(viewPhoto);
    /*    HButtonElement map = new HButtonElement("Draw map");
        workLocationSection.addEl(map);
        map.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapGps.class);
                i.putExtra(Database.PREFERENCE,NURSERY_WORK_SURVEY);
                startActivity(i);
            }
        });*/

        /*HTextEntryElement altitudeEl = new HTextEntryElement(Database.NURSERY_ALTITUDE, "Altitude", "Click on the Gps button to get location", true, store);
        gpsElement.setAltitude(altitudeEl);
        workLocationSection.addEl(altitudeEl);*/


        HSection facilitiesAvailable = new HSection("Module II. Facilities available at the nursery.");

        HNumericElement totalAreaHactare = new HNumericElement(Database.TOTAL_AREA_HACTARE, "1. Total Area of Nursery", "Enter Area in hactare", true, store);
        facilitiesAvailable.addEl(totalAreaHactare);


        final HPickerElement isNurseryFencedAndGated = new HPickerElement(Database.IS_NURSERY_FENCED_AND_GATED, "2. Is the nursery fenced and gated?", "Select an option", true, "Yes|No", store);
        facilitiesAvailable.addEl(isNurseryFencedAndGated);

        HMultiPickerElement sourceOfWaterSupply = new HMultiPickerElement(Database.SOURCE_OF_WATER_SUPPLY, "3. What is the source of water supply ", "Select an option", true, "Bore well|Natural Stream|Canal|Tank|Others (Specify)", store);
        facilitiesAvailable.addEl(sourceOfWaterSupply);

        HTextEntryElement executedWorkOther = new HTextEntryElement(Database.SOURCE_OF_WATER_SUPPLY_OTHERS, "Specify Others", "specify", true, store);
        facilitiesAvailable.addEl(executedWorkOther);
        sourceOfWaterSupply.addElementForValue(executedWorkOther, 4);

        /*HTextElement availableFacilitiesHeading = new HTextElement("4. Availability of Nursery facilities (Select Availability by selecting yes or no)");
        facilitiesAvailable.addEl(availableFacilitiesHeading);*/

        HMultiPickerElement nurseryFacilities = new HMultiPickerElement(Database.AVAILABILITY_OF_NURSERY_FACILITIES, "4. Availability of Nursery facilities", "Select an option", true, "Elevated water tank|Pipeline|Sprinklers|Poly house|Shade net|Germination treys and root trainers|Root trainers|Compost pit|Toilet for Staff|Pergola|Proper layout of space for seed beds|Proper layout of Pb seedlings|Store room|Watchman's shed|Working area|Resting area for labour|Fruit trees along the boundary|Pump house|Nursery board|Stock display board|Others", store);
        facilitiesAvailable.addEl(nurseryFacilities);

        HTextEntryElement other = new HTextEntryElement(Database.OTHERS, "Others", "specify", true, store);
        nurseryFacilities.addElementForValue(other, 20);
        facilitiesAvailable.addEl(other);


        HSection recordMaintainedAtNursery = new HSection("Module III. Records maintained at the nursery");

        HMultiPickerElement recordsMaintained = new HMultiPickerElement(Database.RECORDS_MAINTAINED, "Records maintained at the nursery", "Select an option", true, "Labourer attendance register|Stores register|Seedling stock register|Seedling distribution register|Budget Head wise nursery stock register", store);
        recordMaintainedAtNursery.addEl(recordsMaintained);

        HSection stockOfBaggedSeeding = new HSection("Module IV A – Stock of Bagged seedlings available at nursery.");

        HButtonElement addBaggedSeedingDetails = new HButtonElement("Stock of Bagged seedlings");
        stockOfBaggedSeeding.addEl(addBaggedSeedingDetails);
        addBaggedSeedingDetails.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.BAGGED_SEEDING_LIST);
            i.putExtra("formStatus", formStatus);
            startActivity(i);
        });

        HSection qualityOfBaggedSeedlings = new HSection("Module IV B Quality of bagged seedlings");

        HPickerElement areSeedlingArrangedSpecieWise = new HPickerElement(Database.SEEDLINGS_ARRANGED_SPECIESWISE, "1. Are the seedlings arranged species-wise in the nursery?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areSeedlingArrangedSpecieWise);

        HTextAreaEntryElement seedlingArrangedSpecieWiseReason = new HTextAreaEntryElement(Database.SEEDLINGS_ARRANGED_SPECIESWISE_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(seedlingArrangedSpecieWiseReason);
        areSeedlingArrangedSpecieWise.addNegElement(seedlingArrangedSpecieWiseReason);

        HPickerElement areSeedlingArrangedSchemeWise = new HPickerElement(Database.SEEDLINGS_ARRANGED_SCHEMEWISE, "2. Are the seedlings arranged scheme-wise in the nursery?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areSeedlingArrangedSchemeWise);

        HTextAreaEntryElement seedlingArrangedSchemeWiseReason = new HTextAreaEntryElement(Database.SEEDLINGS_ARRANGED_SCHEMEWISE_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(seedlingArrangedSchemeWiseReason);
        areSeedlingArrangedSchemeWise.addNegElement(seedlingArrangedSchemeWiseReason);

        HPickerElement areContainerIntactAndGoodCondition = new HPickerElement(Database.CONTAINERS_INTACT_AND_IN_GOOD_CONDITION, "3. Are the containers intact and in good condition?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areContainerIntactAndGoodCondition);

        HTextAreaEntryElement containerIntactAndGoodConditionReason = new HTextAreaEntryElement(Database.CONTAINERS_INTACT_AND_IN_GOOD_CONDITION_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(containerIntactAndGoodConditionReason);
        areContainerIntactAndGoodCondition.addNegElement(containerIntactAndGoodConditionReason);

        HPickerElement isIrrigationAdequateTOReachBottomContainer = new HPickerElement(Database.IRRIGATION_ADEQUATE_TO_REACH_THE_BOTTOM_CONTAINER, "4. Is the irrigation adequate to reach the bottom container?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(isIrrigationAdequateTOReachBottomContainer);

        HTextAreaEntryElement irrigationAdequateTOReachBottomContainerReason = new HTextAreaEntryElement(Database.IRRIGATION_ADEQUATE_TO_REACH_THE_BOTTOM_CONTAINER_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(irrigationAdequateTOReachBottomContainerReason);
        isIrrigationAdequateTOReachBottomContainer.addNegElement(irrigationAdequateTOReachBottomContainerReason);

        HPickerElement areSeedlingFreeFromWeeds = new HPickerElement(Database.SEEDLINGS_FREE_FROM_WEEDS, "5. Are the seedlings free from weeds?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areSeedlingFreeFromWeeds);

        HTextAreaEntryElement seedlingFreeFromWeedsReason = new HTextAreaEntryElement(Database.SEEDLINGS_FREE_FROM_WEEDS_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(seedlingFreeFromWeedsReason);
        areSeedlingFreeFromWeeds.addNegElement(seedlingFreeFromWeedsReason);

        HPickerElement areSeedlingRaisedInTime = new HPickerElement(Database.SEEDLINGS_RAISED_IN_TIME, "6. Are the seedlings raised in time? (Growing period in months = width of bag in inches)", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areSeedlingRaisedInTime);

        HTextAreaEntryElement seedlingRaisedInTimeReason = new HTextAreaEntryElement(Database.SEEDLINGS_RAISED_IN_TIME_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(seedlingRaisedInTimeReason);
        areSeedlingRaisedInTime.addNegElement(seedlingRaisedInTimeReason);

        HPickerElement areBudsBeingNippedInTime = new HPickerElement(Database.SIDE_BUDS_BEING_NIPPED_IN_TIME, "7. Are the side buds being nipped in time?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areBudsBeingNippedInTime);

        HTextAreaEntryElement budsBeingNippedInTimeReason = new HTextAreaEntryElement(Database.SIDE_BUDS_BEING_NIPPED_IN_TIME_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(budsBeingNippedInTimeReason);
        areBudsBeingNippedInTime.addNegElement(budsBeingNippedInTimeReason);

        HPickerElement areSeedlingShiftedAndGradedAsPerPractice = new HPickerElement(Database.SEEDLINGS_SHIFTED_AND_GRADED_AS_PER_THE_PACKAGE_OF_PRACTICES, "8. Are the seedlings shifted and graded as per the package of practices?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areSeedlingShiftedAndGradedAsPerPractice);

        HPickerElement areSeedlingsStrikingRootsIntoSoil = new HPickerElement(Database.SEEDLINGS_STRIKING_ROOTS_INTO_THE_SOIL, "If no, are the seedlings striking roots into the soil?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areSeedlingsStrikingRootsIntoSoil);
        areSeedlingShiftedAndGradedAsPerPractice.addNegElement(areSeedlingsStrikingRootsIntoSoil);

        HPickerElement areStakesProvidedToSeedlingsIn10X16 = new HPickerElement(Database.STAKES_PROVIDED_TO_SEEDLINGS_IN_10X16, "9. Are stakes provided to seedlings in 10x16 or bigger pbs?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areStakesProvidedToSeedlingsIn10X16);

        HTextAreaEntryElement stakesProvidedToSeedlingsIn10X16Reason = new HTextAreaEntryElement(Database.STAKES_PROVIDED_TO_SEEDLINGS_IN_10X16_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(stakesProvidedToSeedlingsIn10X16Reason);
        areStakesProvidedToSeedlingsIn10X16.addNegElement(stakesProvidedToSeedlingsIn10X16Reason);

        HPickerElement isSeedlingQualityUniformInNursery = new HPickerElement(Database.SEEDLING_QUALITY_UNIFORM_IN_THE_NURSERY, "10. Is the seedling quality uniform in the nursery?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(isSeedlingQualityUniformInNursery);

        HTextAreaEntryElement seedlingQualityUniformInNurseryReason = new HTextAreaEntryElement(Database.SEEDLING_QUALITY_UNIFORM_IN_THE_NURSERY_REASONS, "Reasons", "Enter the reason", true, store);
        qualityOfBaggedSeedlings.addEl(seedlingQualityUniformInNurseryReason);
        isSeedlingQualityUniformInNursery.addNegElement(seedlingQualityUniformInNurseryReason);

        HPickerElement areSeedlingFreeFromPestsAndDiseases = new HPickerElement(Database.SEEDLINGS_FREE_FROM_PESTS_AND_DISEASES, "11. Are the seedlings free from pests and diseases?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(areSeedlingFreeFromPestsAndDiseases);

        HTextAreaEntryElement seedlingFreeFromPestsAndDiseasesReason = new HTextAreaEntryElement(Database.SEEDLINGS_FREE_FROM_PESTS_AND_DISEASES_DETAILS, "If No Details", "Enter the details", true, store);
        qualityOfBaggedSeedlings.addEl(seedlingFreeFromPestsAndDiseasesReason);
        areSeedlingFreeFromPestsAndDiseases.addNegElement(seedlingFreeFromPestsAndDiseasesReason);

        HTextAreaEntryElement anyOtherRemark = new HTextAreaEntryElement(Database.ANY_OTHER_REMARKS_ON_NURSERY, "12. Any other remarks on nursery", "Enter your remarks", true, store);
        qualityOfBaggedSeedlings.addEl(anyOtherRemark);

        HPickerElement seedBedPresent = new HPickerElement(Database.IS_SEED_BED_PRESENT, "Is Seed bed Present?", "Select option", true, "Yes|No", store);
        qualityOfBaggedSeedlings.addEl(seedBedPresent);

        HSection aboutSeedBedSection = new HSection("Module V A – About seed beds");

        HButtonElement addAboutSeedBedsButon = new HButtonElement("Add seed bed");
        aboutSeedBedSection.addEl(addAboutSeedBedsButon);
        addAboutSeedBedsButon.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.ABOUT_SEED_BED_LIST);
            i.putExtra("formStatus", formStatus);
            startActivity(i);
        });
        aboutSeedBedSection.addEl(addAboutSeedBedsButon);

        HSection qualityOfSeedBedSection = new HSection("Module VB –Quality of Seedbeds");

        HMultiPickerElement qualityOfSeedbeds = new HMultiPickerElement(Database.QUALITY_OF_SEEDBEDS, "Quality of Seedbeds(select if it is acceptable)", "Select option", true, "Designated place for seeds|Proper irrigation facilities|Properly aligned|Free from weeds|Watered properly|Free from pests and diseases|Optimal seed sprouting/germination|unifrom in size and quality|Seed beds used annually", store);
        qualityOfSeedBedSection.addEl(qualityOfSeedbeds);

        HNumericElement noOfSeedBedsRaisedInCurrentYear = new HNumericElement(Database.SEEDBEDS_RAISED_IN_CURRENT_YEAR, " No. of seedbeds raised in current year", "Enter no. of seedbeds", true, store);
        qualityOfSeedBedSection.addEl(noOfSeedBedsRaisedInCurrentYear);

        HSection otherGeneralObservationSection = new HSection("Module VI –Other General Observation about nursery as a whole.");

        HPickerElement isNurseryKeptClearOfWeedsWaste = new HPickerElement(Database.NURSERY_KEPT_CLEAR_OF_WEEDS_WASTE_COMPOSTED, "a. Is the nursery kept clear of weeds and waste composted?", "Select option", true, "Yes|No", store);
        otherGeneralObservationSection.addEl(isNurseryKeptClearOfWeedsWaste);

        HPickerElement isWaterUsageOptimized = new HPickerElement(Database.WATER_USAGE_OPTIMISED_LEAKAGES_PREVENTED, "b. Is water usage optimised and leakages prevented?", "Select option", true, "Yes|No", store);
        otherGeneralObservationSection.addEl(isWaterUsageOptimized);

        HPickerElement isLeftOverSeedlingProperlyDisposed = new HPickerElement(Database.LEFT_OVER_SEEDLINGS_PROPERLY_DISPOSED, "c. Were the previous years' left over seedlings properly disposed off?", "Select option", true, "Yes|No", store);
        otherGeneralObservationSection.addEl(isLeftOverSeedlingProperlyDisposed);

        HPickerElement areSeedlingBedsProvidedWithDisplayBoard = new HPickerElement(Database.SEEDLING_BEDS_PROVIDED_WITH_DISPLAY_BOARDS, "d. Are the seed/ seedling beds provided with display boards?", "Select option", true, "Yes|No", store);
        otherGeneralObservationSection.addEl(areSeedlingBedsProvidedWithDisplayBoard);

        HPickerElement isThereBoardDisplayingStock = new HPickerElement(Database.SEEDBEDS_USED_ANNUALLY, "e. Is there a board displaying total stock of beds and seedlings scheme-wise in the nursery?", "Select option", true, "Yes|No", store);
        otherGeneralObservationSection.addEl(isThereBoardDisplayingStock);


        HTextAreaEntryElement otherRemarksAndObservation = new HTextAreaEntryElement(Database.OTHER_REMARKS_AND_OBSERVATIONS, "f. Any other remarks and observations", "Enter remarks and observation", true, store);
        otherGeneralObservationSection.addEl(otherRemarksAndObservation);


        final HButtonElement save = new HButtonElement("Save");
        save.setElType(HElementType.SUBMIT_BUTTON);
        save.setOnClick(v -> {
            save.getButtonView().setFocusableInTouchMode(true);
            save.getButtonView().requestFocus();
            save.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showSaveFormDataAlert();
            else
                submitNurseryDetails(0);
        });

        final HButtonElement approve = new HButtonElement("Approve");
        approve.setElType(HElementType.SUBMIT_BUTTON);
        approve.setOnClick(v -> {
            approve.getButtonView().setFocusableInTouchMode(true);
            approve.getButtonView().requestFocus();
            approve.getButtonView().setFocusableInTouchMode(false);

            String formId = pref.getString(Database.FORM_ID, "0");


            if (db.getFormFilledStatus(KFD_NURSERY_WORKS_BAGGED_SEEDLINGS, formId)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Bagged Seedling");
            } else if (db.getFormFilledStatus(KFD_NURSERY_WORKS_SEED_BED, formId)) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Seed Bed");
            } else if (!checkFormData()) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Nursery");
            } else {
                submitNurseryDetails(1);
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
            otherGeneralObservationSection.addEl(back);
            workLocationSection.setNotEditable();
            facilitiesAvailable.setNotEditable();
            recordMaintainedAtNursery.setNotEditable();
            stockOfBaggedSeeding.setNotEditable();
            qualityOfBaggedSeedlings.setNotEditable();
            aboutSeedBedSection.setNotEditable();
        } else {
            otherGeneralObservationSection.addEl(save);
            otherGeneralObservationSection.addEl(approve);
        }
        seedBedPresent.addPosSection(aboutSeedBedSection);
        seedBedPresent.addPosSection(qualityOfSeedBedSection);

        sections.add(workLocationSection);
        sections.add(facilitiesAvailable);
        sections.add(recordMaintainedAtNursery);
        sections.add(stockOfBaggedSeeding);
        sections.add(qualityOfBaggedSeedlings);
        sections.add(aboutSeedBedSection);
        sections.add(qualityOfSeedBedSection);
        sections.add(otherGeneralObservationSection);


        return new HRootElement("Nursery Works - Form 2", sections);
    }


    private void submitNurseryDetails(final int flag) {

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(NURSERY_WORK_SURVEY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(flag == 0 ? R.layout.dialog_submit_form : R.layout.dialog_approve_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {

            Map<String, ArrayList<String>> tableMetadata = SurveyCreation.getTableMetaData(Database.TABLE_SURVEY_MASTER, db);
            ContentValues cv = insertValuesToSurveyMaster(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.APP_ID, BuildConfig.VERSION_CODE);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            if (flag == 1) {
                cv.put(Database.FORM_STATUS, 1);
            }
            db.updateSurveyMasterWithFormId(formId, cv);
            tableMetadata = SurveyCreation.getTableMetaData(Database.KFD_NURSERY_WORKS, db);
            cv = insertValuesToNurseryWorks(formId, tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            File mediaStorageDir = surveyCreation.getPictureFolder(Constants.FORMTYPE_NURSERY_WORK, pref.getString(Database.FORM_ID, "0"));
                if(mediaStorageDir!= null && mediaStorageDir.list()!=null) {
                    cv.put(Database.PHOTOS_COUNT, mediaStorageDir.list().length);
                }
            cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
            db.updateTableWithFormId(Database.KFD_NURSERY_WORKS, formId, cv);
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

    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitNurseryDetails(0));

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private ContentValues insertValuesToNurseryWorks(long formId, ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
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
                    cv.put(columnName, 0);
                }
            } else {
                cv.put(columnName, pref.getString(columnName, ""));
            }
        }
        cv.put(Database.STARTING_TIMESTAMP, Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")));
        cv.put(Database.FORM_TYPE, Constants.FORMTYPE_NURSERY_WORK);
        cv.put(Database.ENDING_TIMESTAMP, endingTimeStamp);
        GPSTracker gpsTracker = new GPSTracker(this);
        cv.put(Database.AUTOMATIC_LATITUDE, gpsTracker.getLatitude());
        cv.put(Database.AUTOMATIC_LONGITUDE, gpsTracker.getLongitude());
        return cv;
    }


    @Override
    public void onBackPressed() {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(NURSERY_WORK_SURVEY, Context.MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if (pref.getString("formStatus", "0").equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
                //    super.onBackPressed();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
}

