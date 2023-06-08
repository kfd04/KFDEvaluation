package com.kar.kfd.gov.kfdsurvey.otherworks;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.BuildConfig;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.location.AppSettingsFrag;
import com.kar.kfd.gov.kfdsurvey.service.FloatingWindow;
import com.kar.kfd.gov.kfdsurvey.utils.ImageUtil;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HDatePickerElement;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.HGpsElement;
import com.ngohung.form.el.HNumericElement;
import com.ngohung.form.el.HPickerElement;
import com.ngohung.form.el.HRootElement;
import com.ngohung.form.el.HSection;
import com.ngohung.form.el.HTextAreaEntryElement;
import com.ngohung.form.el.HTextElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.store.HPrefDataStore;
import com.ngohung.form.util.GPSTracker;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class OtherSurvey extends HBaseFormActivity {

    public static final String OTHER_SURVEY = "Other Survey";
    public static final String TAG = OtherSurvey.class.getSimpleName();
    private static final int TAKE_PICTURE_REQUEST = 1001;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private SweetAlertDialog dialog;
    private SurveyCreation surveyCreation;
    private boolean editable = true;
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    private String formStatus = "0";
    public static DisplayMetrics metrics;
    private Database db;
    AlertDialog alert;
    TextView toolbar_title;
    private HTextElement tt_end, tt_first, tt_mid;
    private File imageFile;
    private Location gpsLocation;
    static ArrayList<String> exifAttributes = new ArrayList<>();
    String[] attributes = new String[]
            {
                    ExifInterface.TAG_DATETIME,
                    ExifInterface.TAG_DATETIME_DIGITIZED,
                    ExifInterface.TAG_EXPOSURE_TIME,
                    ExifInterface.TAG_FLASH,
                    ExifInterface.TAG_FOCAL_LENGTH,
                    ExifInterface.TAG_GPS_ALTITUDE,
                    ExifInterface.TAG_GPS_ALTITUDE_REF,
                    ExifInterface.TAG_GPS_DATESTAMP,
                    ExifInterface.TAG_GPS_LATITUDE,
                    ExifInterface.TAG_GPS_LATITUDE_REF,
                    ExifInterface.TAG_GPS_LONGITUDE,
                    ExifInterface.TAG_GPS_LONGITUDE_REF,
                    ExifInterface.TAG_GPS_PROCESSING_METHOD,
                    ExifInterface.TAG_GPS_TIMESTAMP,
                    ExifInterface.TAG_MAKE,
                    ExifInterface.TAG_MODEL,
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.TAG_SUBSEC_TIME,
                    ExifInterface.TAG_WHITE_BALANCE
            };
    private String staus_new;
    private File mediaStorageDir;
    public static final String folderName_boundary_first = Constants.FORMTYPE_OTHERWORKS + File.separator + "Boundary Consolidation First";
    public static final String folderName_boundary_mid = Constants.FORMTYPE_OTHERWORKS + File.separator + "Boundary Consolidation Mid";
    public static final String folderName_boundary_end = Constants.FORMTYPE_OTHERWORKS + File.separator + "Boundary Consolidation End";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        surveyCreation = new SurveyCreation();


        Toolbar toolbar = findViewById(R.id.toolbar); // get the reference of Toolbar
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText(OTHER_SURVEY);
        setSupportActionBar(toolbar);

    }


    @Override
    protected HRootElement createRootElement() {

        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        pref = this.getApplicationContext().getSharedPreferences(OTHER_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);

        db = new Database(getApplicationContext());
        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }
        formStatus = pref.getString("formStatus", "0");


        if (Integer.parseInt(pref.getString(Database.FORM_ID, "0")) != 0) {
            editable = false;
            if (!pref.getString(Database.WHEN_WORK_COMPLETED_YEAR, "").isEmpty()) {
                pref.edit().putString(Database.WAS_WORK_COMPLETE, "Yes").apply();
            } else {
                pref.edit().putString(Database.WAS_WORK_COMPLETE, "No").apply();
            }
            pref.edit().putString("time_taken_months", pref.getString(Database.TIME_TAKEN_TO_COMPLETE_WORK_MONTHS, "")).apply();
            pref.edit().putString("volume_of_work", pref.getString(Database.ORIGINAL_WORK_DIMENSION_VOLUME_MTRS, "")).apply();
           /* String quality = pref.getString(Database.ORIGINAL_WORK_QUALITY_RATING, "");
            if(quality.length()<=1){
                pref.edit().putString(Database.ORIGINAL_WORK_QUALITY_RATING,"0" + quality).commit();
            }
            quality = pref.getString(Database.ORIGINAL_WORK_QUALITY_GRADING, "");
            if(quality.length()<=1){
                pref.edit().putString(Database.ORIGINAL_WORK_QUALITY_GRADING,"0" + quality).commit();
            }*/
        } else {
            editable = true;
        }

        ArrayList<HSection> sections = new ArrayList<>();


        HSection workLocationSection = new HSection("I.Basic Information");
        workLocationSection.setVisible(true);

        HButtonElement locationDetails = new HButtonElement("Location Details");
        workLocationSection.addEl(locationDetails);
        locationDetails.setOnClick(v -> {
            AppSettingsFrag appSettingsFrag = new AppSettingsFrag();
            Bundle bundle = new Bundle();
            bundle.putString("preference", OTHER_SURVEY);
            appSettingsFrag.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, appSettingsFrag, "Home page");
            transaction.addToBackStack(null);
            transaction.commit();
        });

        HPickerElement maintainenceFresh = new HPickerElement(Database.MAINTAINENACE_FRESH, "Maintenance/Fresh", "Select Option", true, "Maintainenace|Fresh", store);
        workLocationSection.addEl(maintainenceFresh);

        HPickerElement legalStatusOfLand = new HPickerElement(Database.LEGAL_STATUS_OF_LAND, "6.Legal status of Stand", "Enter legal status of land", true, "RF|PF|Deemed Forest|Govt land|Municipal land|Institutional land|Gomal|Others", store);
        workLocationSection.addEl(legalStatusOfLand);

        HTextEntryElement forestName = new HTextEntryElement(Database.FOREST_NAME, "Forest Name", "", true, store);
        legalStatusOfLand.addElementForValue(forestName, 0);
        legalStatusOfLand.addElementForValue(forestName, 1);
        legalStatusOfLand.addElementForValue(forestName, 2);
        workLocationSection.addEl(legalStatusOfLand);


        HTextAreaEntryElement legalStatusOfLandOther = new HTextAreaEntryElement(Database.LEGAL_STATUS_OF_LAND_OTHER_DETAILS, "Specify ", "Enter the other type", true, store);
        legalStatusOfLand.addElementForValue(legalStatusOfLandOther, 7);
        workLocationSection.addEl(legalStatusOfLandOther);

        HPickerElement plantationAreaScheme = new HPickerElement(Database.SCHEME_NAME, Database.SCHEME_ID, "Scheme", "Select the scheme", true, 0, db.getSchemesWithId(), store);
        workLocationSection.addEl(plantationAreaScheme);

        HPickerElement workName = new HPickerElement(Database.TYPE_OF_WORK, "7.Type of the Work",
                "Select and option", true, "Buildings|Boundary consolidation|" +
                "Formation of roads|Soil & Moisture Conservation|Desilting of Tanks|Construction of bridges|" +
                "Construction of culverts|Construction of cause way|Wildlife/AdvProtection|Others", store);
        workLocationSection.addEl(workName);


        HPickerElement workNameBuilding = new HPickerElement(Database.WORK_NAME, "Type of buildings", "Select an option", true,
                "Residential Buildings|Office buildings|Training/Meeting Hall|Forest Rest House|Anti Poaching Camp/Forest AdvProtection Camp|Watch Tower " +
                        "|Intrepretation centre|Guest House|Pergola|Others",
                store);
        workName.addElementForValue(workNameBuilding, 0);
        workLocationSection.addEl(workNameBuilding);

        HPickerElement resSub = new HPickerElement(Database.TYPE_OF_OFFICIER_RESBUILDINGS, "Type of Residence", "Select an option", true, "Watcher|Forest Guard|DRFO|RFO|ACF|DCF|CF/CCF|Ministerial Staff", store);
        workNameBuilding.addElementForValue(resSub, 0);
        workLocationSection.addEl(resSub);

        HPickerElement offSub = new HPickerElement(Database.TYPE_OF_OFFICIER_OFFICEBUILDINGS, "Type of Office", "Select an option", true, "RFO|ACF|DCF|CF/CCF", store);
        workNameBuilding.addElementForValue(offSub, 1);
        workLocationSection.addEl(offSub);

        HPickerElement workNameBoundary = new HPickerElement(Database.WORK_NAME, "Type of " +
                "Boundary consolidation", "Select an option", true, "Cattle Proof Trench|" +
                "Compound wall|Stone/Boulder wall|Boundary pillars|Barbed wire fencing|Chain link mesh|Others", store);
        workName.addElementForValue(workNameBoundary, 1);
        workLocationSection.addEl(workNameBoundary);

        HNumericElement noOfCarins = new HNumericElement(Database.NO_OF_CARINS_RCCPILLARS_RFSTONE, "No of Carins/Rcc Pillars/RFstone", "Enter ", true, store);
        workNameBoundary.addElementForValue(noOfCarins, 3);
        workLocationSection.addEl(noOfCarins);


        HPickerElement workNameRoads = new HPickerElement(Database.WORK_NAME, "Type of Roads", "Select an option",
                true, "Metal Road|Pucca Road|Forest Road|Mud Road|Cement/Concrete Road|Others", store);
        workName.addElementForValue(workNameRoads, 2);
        workLocationSection.addEl(workNameRoads);

        HPickerElement workNameSMC = new HPickerElement(Database.WORK_NAME, "Type of SMC", "Select an option", true,
                "Check dam|Nalabunds|Percolation ponds|Waterholes|Vented dams|Gully checks|Others",
                store);
        workName.addElementForValue(workNameSMC, 3);
        workLocationSection.addEl(workNameSMC);

        HPickerElement wildLifeProtection = new HPickerElement(Database.WORK_NAME, "Type of AdvProtection", "Select an option", true, "Elephant Proof Trench|Railway barricades|Other special structures", store);
        workName.addElementForValue(wildLifeProtection, 8);
        workLocationSection.addEl(wildLifeProtection);

        HTextAreaEntryElement othersspecify = new HTextAreaEntryElement(Database
                .OTHER_TYPE_OF_WORK, "Specify Others", "Specify others", true, store);
        workName.addElementForValue(othersspecify, 9);
        workLocationSection.addEl(othersspecify);

        HTextAreaEntryElement otherBuildings = new HTextAreaEntryElement(Database.OTHER_TYPE_OF_WORK, "Specify Others", "Specify", true, store);
        workNameBuilding.addElementForValue(otherBuildings, 9);
        workNameBoundary.addElementForValue(otherBuildings, 6);
        workNameRoads.addElementForValue(otherBuildings, 5);
        workNameSMC.addElementForValue(otherBuildings, 6);
        wildLifeProtection.addElementForValue(otherBuildings, 2);
        workLocationSection.addEl(otherBuildings);


        HTextEntryElement location = new HTextEntryElement(Database.WORK_LOCATION,
                "8.Location of work", "Enter the Location", true, store);
        workLocationSection.addEl(location);


        HPickerElement yearExecution = new HPickerElement(Database.EXECUTION_YEAR, "9.Year of " +
                "Execution", "Select the year of execution", true, "2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21", store);
        workLocationSection.addEl(yearExecution);

        HNumericElement totalCost = new HNumericElement(Database.ESTIMATED_COST_RUPEES, "10.Estimated " +
                "cost ( in Rupees )", "Enter the total cost in Rupees", true, store);
        workLocationSection.addEl(totalCost);

        HNumericElement totalExpenditure = new HNumericElement(Database.TOTAL_EXPENDITURE, "11. Total Expenditure( in Rupees )", "Enter the total cost in Rupees", true, store);
        workLocationSection.addEl(totalExpenditure);


        HTextEntryElement workCode = new HTextEntryElement(Database.WORK_CODE, "12.Work Code", "Enter location " +
                "details to generate work code", false, store);
        workLocationSection.addEl(workCode);
        workCode.setNotEditable();
        // workCode.setValue(generateWorkCode(pref, db));


        HTextElement procedureCompliance = new HTextElement("A.Details of Approvals and Execution");
        workLocationSection.addEl(procedureCompliance);

        final HPickerElement workApproved = new HPickerElement(Database.WORK_APPROVED_IN_APO, "1.APO approval?", "Select an option", true, "Yes|No", store);
        workLocationSection.addEl(workApproved);

        HPickerElement workApprovedSlNoAvail = new HPickerElement(Database.WORK_APPROVED_IN_APO_SLNO_AVAILABLE, "Is SO No. in the Estimate Available", "Select an option", true, "Yes|No", store);
        workLocationSection.addEl(workApprovedSlNoAvail);
        workApproved.addPosElement(workApprovedSlNoAvail);

        HTextEntryElement workApprovedSLNo = new HTextEntryElement(Database.WORK_APPROVED_IN_APO_SLNO, "SO No. in the APO", "Enter SL No. in the APO", true, store);
        workLocationSection.addEl(workApprovedSLNo);
        workApprovedSlNoAvail.addPosElement(workApprovedSLNo);

        HPickerElement dateOfApprovAvail = new HPickerElement(Database.WORK_APPROVED_IN_APO_DATE_AVAILABLE, "Is Date of Approval Available", "Select an option", true, "Yes|No", store);
        workLocationSection.addEl(dateOfApprovAvail);
        workApproved.addPosElement(dateOfApprovAvail);

        HDatePickerElement workApprovedDate = new HDatePickerElement(Database.WORK_APPROVED_IN_APO_APPROVAL_DATE, "Date of approval", "Select date", true, store);
        workLocationSection.addEl(workApprovedDate);
        workApprovedDate.setDateValue(workApprovedDate.getDateValue());
        dateOfApprovAvail.addPosElement(workApprovedDate);

        HTextEntryElement workApprovedReason = new HTextEntryElement(Database
                .WORK_APPROVED_IN_APO_NO_REASON, "Why was it not approved?", "Specify the reason",
                true, store);
        workLocationSection.addEl(workApprovedReason);
        workApproved.addNegElement(workApprovedReason);

        HPickerElement procurement = new HPickerElement(Database.WAS_PROCUREMENT_INVOLVED, "2.Procurement details if any?", "Select an option", true, "Yes|No", store);
        workLocationSection.addEl(procurement);

        HPickerElement modeofProcurement = new HPickerElement(Database.MODE_OF_PROCUREMENT, "Procurement Mode", "Select an option", true, "Tender amount|DGS&D amount|Rate quotations|Others", store);
        workLocationSection.addEl(modeofProcurement);
        procurement.addPosElement(modeofProcurement);

        HTextEntryElement modeOfProcureOthers = new HTextEntryElement(Database.MODE_OF_PROCUREMENT_OTHERS, "Specify", "Enter Other Procurement mode", true, store);
        workLocationSection.addEl(modeOfProcureOthers);
        modeofProcurement.addElementForValue(modeOfProcureOthers, 3);

        HNumericElement procumentAmount = new HNumericElement(Database.PROCUREMENT_AMOUNT, "Amount( in Rupees )", "Enter Procurement Amount in Rupees", true, store);
        workLocationSection.addEl(procumentAmount);
        procurement.addPosElement(procumentAmount);

        HPickerElement executedWork = new HPickerElement(Database.WHO_EXEC_WORK, "3.Executing Agency ?", "Select an option", true, "Contractor|Department/RFO|VFC/EDC|Others" +
                "(Specify)", store);
        workLocationSection.addEl(executedWork);

        HTextAreaEntryElement executedWorkOther = new HTextAreaEntryElement(Database.WHO_EXEC_WORK_OTHERS, "Specify Others", "Enter executor of " +
                "work", true, store);
        workLocationSection.addEl(executedWorkOther);
        executedWork.addElementForValue(executedWorkOther, 3);

        final HPickerElement workStartedYear = new HPickerElement(Database.WHEN_WORK_START_YR, "4.Work Commencement year ?", "Select year", true, "2008|2009|2010|2011|2012|2013|2014|2015|2016|2017|2018|2019|2020|2021", store);
        workLocationSection.addEl(workStartedYear);
      /*  String[] year = workStartedYear.getValue().split("-");
        workStartedYear.setYears(year[0]);
        Log.i(TAG, "createRootElement: " + year[0]);
        Log.i(TAG, "createRootElement: " + store.getPref().getString(Database.WHEN_WORK_COMPLETED_YEAR, ""));*/

        final HPickerElement workStartedMonth = new HPickerElement(Database.WHEN_WORK_STARTED_MONTH, "Work commencement month?", "Select month", true, "January|February|March|April|May|June|July|August|September|October|November|December", store);
        workLocationSection.addEl(workStartedMonth);

        HPickerElement wasWorkCompleted = new HPickerElement(Database.WAS_WORK_COMPLETE, "5.Was the work completed", "Select an option", true, "Yes|No", store);
        workLocationSection.addEl(wasWorkCompleted);

        final HPickerElement workCompletedYear = new HPickerElement(Database.WHEN_WORK_COMPLETED_YEAR, "When was the work completed (year ) ?", "Select year", true, "2008|2009|2010|2011|2012|2013|2014|2015|2016|2017|2018|2019|2020|Not Available", store);
        workLocationSection.addEl(workCompletedYear);
        wasWorkCompleted.addPosElement(workCompletedYear);

        final HPickerElement workCompletedMonth = new HPickerElement(Database.WHEN_WORK_COMPLETED_MONTH, "When was the work completed ( month )?", "Select month", true, "January|February|March|April|May|June|July|August|September|October|November|December|Not Available", store);
        workLocationSection.addEl(workCompletedMonth);
        wasWorkCompleted.addPosElement(workCompletedMonth);

        /*final HButtonElement calculateTimeTaken = new HButtonElement("Calculate total time to complete work");
        workLocationSection.addEl(calculateTimeTaken);
        wasWorkCompleted.addPosElement(calculateTimeTaken);

        final HTextEntryElement totalTimeTaken = new HTextEntryElement(Database.TIME_TAKEN_MONTHS, "6.Total time ( months ) taken to complete the work", "Click the button to calculate time taken", true, store);
        workLocationSection.addEl(totalTimeTaken);
        totalTimeTaken.setNotEditable();
        wasWorkCompleted.addPosElement(totalTimeTaken);

        calculateTimeTaken.setOnClick(v -> {
            calculateTimeTaken.getButtonView().setFocusableInTouchMode(true);
            calculateTimeTaken.getButtonView().requestFocus();
            calculateTimeTaken.getButtonView().setFocusableInTouchMode(false);
            int year1, month1, year2, month2 = 0;
            try {
                year1 = Integer.parseInt(workStartedYear.getValue());
            } catch (NumberFormatException ex) {
                year1 = 0;
            }
            try {
                //     month1 = Integer.parseInt(workStartedMonth.getValue());

                month1 = workStartedMonth.getSelectedIndex() + 1;

            } catch (NumberFormatException ex) {
                month1 = 0;
            }
            try {
                year2 = Integer.parseInt(workCompletedYear.getValue());
            } catch (NumberFormatException ex) {
                year2 = 0;
            }
            try {
                //  month2 = Integer.parseInt(workCompletedMonth.getValue());
                if (!workCompletedMonth.getValue().equals("Not Available")) {
                    month2 = workCompletedMonth.getSelectedIndex() + 1;
                }
            } catch (NumberFormatException ex) {
                month2 = 0;
            }
            int timeTaken = (year2 - year1) * 12 + (month2 - month1);
            if (timeTaken < 0) {
                timeTaken = 0;
            }
            totalTimeTaken.setValue(String.valueOf(timeTaken));
            totalTimeTaken.getEditText().setText(String.valueOf(timeTaken));
        });*/

        HPickerElement checkMeasurementDatePicker = new HPickerElement(Database.CHECK_MEASUREMENT_TIMESTAMP_AVAILABLE, "7.Availability of date of check measurement?", "Select an option", true, "Yes|No", store);
        workLocationSection.addEl(checkMeasurementDatePicker);

        HDatePickerElement checkMeasurementDate = new HDatePickerElement(Database
                .CHECK_MEASUREMENT_TIMESTAMP, "Date of check measurement", "Select date",
                true, store);
        checkMeasurementDate.setDateValue(checkMeasurementDate.getDateValue());
        workLocationSection.addEl(checkMeasurementDate);
        checkMeasurementDatePicker.addPosElement(checkMeasurementDate);

        HPickerElement checkCompletionDatePicker = new HPickerElement(Database
                .COMPLETION_CERTIFICATE_TIMESTAMP_AVAILABLE, "8.Availability of Completion report?", "Select an option", true, "Yes|No", store);
        workLocationSection.addEl(checkCompletionDatePicker);

        HDatePickerElement completionFiledDate = new HDatePickerElement(Database
                .COMPLETION_CERTIFICATE_TIMESTAMP, "Date on which completion report submitted",
                "Select date", true, store);
        completionFiledDate.setDateValue(completionFiledDate.getDateValue());
        workLocationSection.addEl(completionFiledDate);
        checkCompletionDatePicker.addPosElement(completionFiledDate);


        HSection assessmentOriginalWorks = new HSection("II. Evaluation ");

        HTextElement evaluationWork = new HTextElement("A.Evaluation of work");
        assessmentOriginalWorks.addEl(evaluationWork);

        HPickerElement workExistsPicker = new HPickerElement(Database.WHETHER_WORK_EXISTS_NOW,
                "1.Whether work is existing or not?", "Select an option", true, "Yes|No",
                store);
        assessmentOriginalWorks.addEl(workExistsPicker);

        HGpsElement gpsElement = new HGpsElement("Get GPS location", true);
        if (formStatus.equals("0")) {
            assessmentOriginalWorks.addEl(gpsElement);
            workExistsPicker.addPosElement(gpsElement);
            workExistsPicker.addNegElement(gpsElement);
        }

        HTextEntryElement latitudeEl = new HTextEntryElement(Database.WORK_LOCATION_LAT, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);
        assessmentOriginalWorks.addEl(latitudeEl);
        workExistsPicker.addPosElement(latitudeEl);
        workExistsPicker.addNegElement(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.WORK_LOCATION_LONG, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);
        assessmentOriginalWorks.addEl(longitudeEl);
        workExistsPicker.addPosElement(longitudeEl);
        workExistsPicker.addNegElement(longitudeEl);


        HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(v -> {

            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", Constants.FORMTYPE_OTHERWORKS);
            bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");

            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        assessmentOriginalWorks.addEl(viewPhoto);
        workExistsPicker.addPosElement(viewPhoto);
        workExistsPicker.addNegElement(viewPhoto);

        HTextAreaEntryElement remarks = new HTextAreaEntryElement(Database.REMARKS, "Remarks if any", "Enter Remarks", true, store);
        assessmentOriginalWorks.addEl(remarks);
        workExistsPicker.addNegElement(remarks);

        tt_first = new HTextElement("Take photo during start of perambulation");
        assessmentOriginalWorks.addEl(tt_first);
//        workExistsPicker.addPosElement(tt_first);

        workName.addElementForValue(tt_first, 2);
        workName.addElementForValue(tt_first, 1);


        HButtonElement upload_first = new HButtonElement("Upload Photo(Starting of Perambulation)");
        assessmentOriginalWorks.addEl(upload_first);
//        workExistsPicker.addPosElement(upload_first);
        workName.addElementForValue(upload_first, 2);
        workName.addElementForValue(upload_first, 1);
        upload_first.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName_boundary_first);
            bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });

        tt_mid = new HTextElement("Take photo during mid of perambulation ");
        assessmentOriginalWorks.addEl(tt_mid);
//        workExistsPicker.addPosElement(tt_mid);
        workName.addElementForValue(tt_mid, 2);
        workName.addElementForValue(tt_mid, 1);

        HButtonElement upload_mid = new HButtonElement("Upload Photo(Mid of Perambulation)");
        assessmentOriginalWorks.addEl(upload_mid);
//        workExistsPicker.addPosElement(upload_mid);
        workName.addElementForValue(upload_mid, 2);
        workName.addElementForValue(upload_mid, 1);

        upload_mid.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName_boundary_mid);
            bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });


        tt_end = new HTextElement("Take photo during end of perambulation ");
        assessmentOriginalWorks.addEl(tt_end);
//        workExistsPicker.addPosElement(tt_end);
        workName.addElementForValue(tt_end, 2);
        workName.addElementForValue(tt_end, 1);

        HButtonElement upload_end = new HButtonElement("Upload photo(End of Perambulation)");
        assessmentOriginalWorks.addEl(upload_end);
//        workExistsPicker.addPosElement(upload_end);
        workName.addElementForValue(upload_end, 2);
        workName.addElementForValue(upload_end, 1);

        upload_end.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName_boundary_end);
            bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });


        HTextEntryElement originalWorkPurpose = new HTextEntryElement(Database.PURPOSE_OF_ORIGINALWORK, "2.What purpose was the work expected to serve?", "Enter purpose", true, store);
        assessmentOriginalWorks.addEl(originalWorkPurpose);
        workExistsPicker.addPosElement(originalWorkPurpose);

        HPickerElement similarWorks = new HPickerElement(Database.SIMILAR_WORK_INSAME_LOCALITY, "3.Were similar works carried out in the past in the same locality or near by (within 3 km)?", "Select an option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(similarWorks);
        workExistsPicker.addPosElement(similarWorks);

        HTextAreaEntryElement similarWorksDetails = new HTextAreaEntryElement(Database.SIMILAR_WORK_IN_LOCALITY_PREV_DETAILS, "Details of previous " +
                "work done", "Enter details", true, store);
        assessmentOriginalWorks.addEl(similarWorksDetails);
        similarWorks.addPosElement(similarWorksDetails);

        HPickerElement similarWorksEffective = new HPickerElement(Database.SIMILAR_WORK_INSAME_LOCALITY_USAGE, "Are these works put to effective use?", "Select an option", true, "Yes|No|Partly", store);
        assessmentOriginalWorks.addEl(similarWorksEffective);
        similarWorks.addPosElement(similarWorksEffective);

        HTextAreaEntryElement similarWorksIneffective = new HTextAreaEntryElement(Database.SIMILAR_WORK_INSAME_LOCALITY_USAGE_REASON, "Reasons:", "Enter reason", true, store);
        assessmentOriginalWorks.addEl(similarWorksIneffective);
        similarWorksEffective.addNegElement(similarWorksIneffective);
        similarWorksEffective.addElementForValue(similarWorksIneffective, 2);


        HPickerElement siteSelected = new HPickerElement(Database.ORIGINAL_WORK_SITE_SELECTION,
                "4.Is the site selected appropriate?", "Select an option", true, "Yes|No",
                store);
        assessmentOriginalWorks.addEl(siteSelected);
        workExistsPicker.addPosElement(siteSelected);


        HTextAreaEntryElement siteSelectedReason = new HTextAreaEntryElement(Database.ORIGINAL_WORK_SITE_SELECTION_REASON, "Reason:", "Enter reason", true, store);
        assessmentOriginalWorks.addEl(siteSelectedReason);
        siteSelected.addNegElement(siteSelectedReason);

        HPickerElement vegetationLoss = new HPickerElement(Database
                .ORIGINAL_WORK_DAMAGED_LOCAL_VEGETATION, "5.Did the work cause any loss or damage to local tree vegetation?", "Select an option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(vegetationLoss);
        workExistsPicker.addPosElement(vegetationLoss);

        HTextEntryElement vegetationLossDetails = new HTextEntryElement(Database.ORIGINAL_WORK_DAMAGED_LOCAL_VEGETATION_REASON, "Details", "Enter details", true, store);
        assessmentOriginalWorks.addEl(vegetationLossDetails);
        vegetationLoss.addPosElement(vegetationLossDetails);
/*
        HTextElement workDimensions = new HTextElement("6.What are the work dimensions?");
        assessmentOriginalWorks.addEl(workDimensions);
        workExistsPicker.addPosElement(workDimensions);

        final HNumericElement workWidth = new HNumericElement(Database.ORIGINAL_WORK_DIMENSION_WIDTH_MTRS, "Width (m) :", "Enter width (m) ", true, store);
        assessmentOriginalWorks.addEl(workWidth);
        workExistsPicker.addPosElement(workWidth);

        final HNumericElement workHeight = new HNumericElement(Database.ORIGINAL_WORK_DIMENSION_HEIGHT_MTRS, "Height/Depth (m) :", "Enter height/depth (m) ", true, store);
        assessmentOriginalWorks.addEl(workHeight);
        workExistsPicker.addPosElement(workHeight);

        final HNumericElement workLength = new HNumericElement(Database.ORIGINAL_WORK_DIMENSION_LENGTH_MTRS, "Length (m) :", "Enter length (m) ", true, store);
        assessmentOriginalWorks.addEl(workLength);
        workExistsPicker.addPosElement(workLength);

        final HButtonElement calculateWorkVolume = new HButtonElement("Calculate Volume");
        if(formStatus.equals("0")) {
            assessmentOriginalWorks.addEl(calculateWorkVolume);
            workExistsPicker.addPosElement(calculateWorkVolume);
        }

        final HTextEntryElement workVolume = new HTextEntryElement("volume_of_work","Volume (cubic metre)","Click the button to calculate volume",true,store);
        assessmentOriginalWorks.addEl(workVolume);
        workVolume.setNotEditable();
        workExistsPicker.addPosElement(workVolume);

        calculateWorkVolume.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateWorkVolume.getButtonView().setFocusableInTouchMode(true);
                calculateWorkVolume.getButtonView().requestFocus();
                calculateWorkVolume.getButtonView().setFocusableInTouchMode(false);
                float height, width, length;
                try {
                    height = Float.parseFloat(workHeight.getValue());
                } catch (NumberFormatException ex) {
                    height = 0;
                }
                try {
                    width = Float.parseFloat(workWidth.getValue());
                } catch (NumberFormatException ex) {
                    width = 0;
                }
                try {
                    length = Float.parseFloat(workLength.getValue());
                } catch (NumberFormatException ex) {
                    length = 0;
                }
                float volume = height * width * length;
                workVolume.setValue(String.valueOf(volume));
                workVolume.getEditText().setText(String.valueOf(volume));
            }
        });

        HPickerElement technicalSpecifications = new HPickerElement(Database.ORIGINAL_WORK_TECHSPEC_AVAILABLE, "7.Are technical specifications available for the work?", "Select an option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(technicalSpecifications);
        workExistsPicker.addPosElement(technicalSpecifications);

        HTextEntryElement technicalSpecificationsName = new HTextEntryElement(Database.ORIGINAL_WORK_TECHSPEC_AVAILABLE_REFERENCE, "Name of the source document", "Enter name", true, store);
        assessmentOriginalWorks.addEl(technicalSpecificationsName);
        technicalSpecifications.addPosElement(technicalSpecificationsName);

        HTextEntryElement technicalSpecificationsFollowed = new HTextEntryElement(Database.ORIGINAL_WORK_TECHSPEC_NOTAVAILABLE_DETAILS, "What specifications were followed", "Enter specifications", true, store);
        assessmentOriginalWorks.addEl(technicalSpecificationsFollowed);
        technicalSpecifications.addNegElement(technicalSpecificationsFollowed);

        HPickerElement technicalSpecificationsAdopted = new HPickerElement(Database.SPECIFICATION_ADOPTED_FOR_ESTIMATE, "8.Were the specification adopted in the estimate?", "Select an option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(technicalSpecificationsAdopted);
        workExistsPicker.addPosElement(technicalSpecificationsAdopted);*/

        HPickerElement technicalSpecificationsEstimate = new HPickerElement(Database
                .WORK_CARRIEDOUT_ASPER_ESTIMATE, "6.Was the work carried out according to the estimate?", "Select an option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(technicalSpecificationsEstimate);
        workExistsPicker.addPosElement(technicalSpecificationsEstimate);

        HTextEntryElement technicalSpecificationsEstimateDiff = new HTextEntryElement(Database.WORK_CARRIEDOUT_ASPER_ESTIMATE_DEVIATION, "What are the differences:", "List differences in specification", true, store);
        assessmentOriginalWorks.addEl(technicalSpecificationsEstimateDiff);
        technicalSpecificationsEstimate.addNegElement(technicalSpecificationsEstimateDiff);

        HPickerElement workCompleted = new HPickerElement(Database
                .WORK_COMPLETED_FROM_ALL_ASPECTS, "7.Is the work completed in all respects?",
                "Select an option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(workCompleted);
        workExistsPicker.addPosElement(workCompleted);

        HTextEntryElement workCompletedDiff = new HTextEntryElement(Database
                .WORK_NOT_COMPLETED_FROM_ALL_ASPECTS_DETAILS, "What are the differences:",
                "List differences", true, store);
        assessmentOriginalWorks.addEl(workCompletedDiff);
        workCompleted.addNegElement(workCompletedDiff);

        /*HPickerElement qualityRating = new HPickerElement(Database.ORIGINAL_WORK_QUALITY_RATING,
                "8.What is the quality rating of the work on 1-10 point scale ( 10 is best ) ?", "Enter quality rating", true, "1|2|3|4|5|6|7|8|9|10", store);
        assessmentOriginalWorks.addEl(qualityRating);
        workExistsPicker.addPosElement(qualityRating);*/


        HTextElement workUtils = new HTextElement("B. Utilization of the work");
        assessmentOriginalWorks.addEl(workUtils);
        workExistsPicker.addPosElement(workUtils);

        HPickerElement workAssetUsage = new HPickerElement(Database.IS_ASSET_USED_NOW, "1.Is the" +
                " work/asset been put to use now?", "Select option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(workAssetUsage);
        workExistsPicker.addPosElement(workAssetUsage);


        HTextEntryElement workAssetUserDetails = new HTextEntryElement(Database.ASSET_USED_NOW_WHO, "i.Who uses it?", "Enter the details", true, store);
        assessmentOriginalWorks.addEl(workAssetUserDetails);
        workAssetUsage.addPosElement(workAssetUserDetails);

        HTextEntryElement workAssetPurpose = new HTextEntryElement(Database.ASSET_USED_NOW_PURPOSE, "ii.For what purpose?", "Enter the usage purpose", true, store);
        assessmentOriginalWorks.addEl(workAssetPurpose);
        workAssetUsage.addPosElement(workAssetPurpose);


        HPickerElement workAssetServingPurpose = new HPickerElement(Database
                .ASSET_SERVING_INTENDED_PURPOSE, "iii.Is the work serving the intended purpose?",
                "Select option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(workAssetServingPurpose);
        workAssetUsage.addPosElement(workAssetServingPurpose);

        HTextAreaEntryElement workAssetUsageReason = new HTextAreaEntryElement(Database.REASON_FOR_ASSET_NOT_USED_NOW, "Reasons for previous answer", "Enter reason", true, store);
        assessmentOriginalWorks.addEl(workAssetUsageReason);
        workAssetUsage.addNegElement(workAssetUsageReason);

        HPickerElement workSchemePurpose = new HPickerElement(Database.WORK_MEETS_PROGRAM_OBJECTIVE, "2.Does the work meet the scheme / program objectives?", "Select option", true, "Yes|No", store);
        assessmentOriginalWorks.addEl(workSchemePurpose);
        workExistsPicker.addPosElement(workSchemePurpose);

        HTextAreaEntryElement workSchemeNegReason = new HTextAreaEntryElement(Database
                .REASON_FOR_WORK_DOES_NOT_MEET_OBJECTIVE, "Reasons ?", "Specify the reason",
                true, store);
        assessmentOriginalWorks.addEl(workSchemeNegReason);
        workSchemePurpose.addNegElement(workSchemeNegReason);

        HTextEntryElement workAssetEffectiveReason = new HTextEntryElement(Database.THINGS_TO_DO_FOR_MAKING_ASSET_MORE_EFFECTIVE, "3.What else needs to be done to make the work/asset work more effectively?", "Enter your suggestion", true, store);
        assessmentOriginalWorks.addEl(workAssetEffectiveReason);
        workExistsPicker.addPosElement(workAssetEffectiveReason);

      /*  HPickerElement qualityRating1 = new HPickerElement(Database.ORIGINAL_WORK_QUALITY_GRADING,
                "4.What is the work quality grading on 1-10 point scale ( 1=worst and " +
                        "10=Excellant)",
                "Enter quality grading", true, "1|2|3|4|5|6|7|8|9|10", store);
        assessmentOriginalWorks.addEl(qualityRating1);
        workExistsPicker.addPosElement(qualityRating1);*/

        HTextAreaEntryElement workAssetRemarks = new HTextAreaEntryElement(Database
                .UTILIZATION_REMARKS, "4.Remarks if any ", "Enter the remarks", true, store);
        assessmentOriginalWorks.addEl(workAssetRemarks);
        workExistsPicker.addPosElement(workAssetRemarks);

        final HButtonElement save = new HButtonElement("Save");
        save.setElType(HElementType.SUBMIT_BUTTON);
        save.setOnClick(v -> {
            save.getButtonView().setFocusableInTouchMode(true);
            save.getButtonView().requestFocus();
            save.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showSaveFormDataAlert();
            else
                submitOtherSurveyDetails(0);
        });

        final HButtonElement approve = new HButtonElement("Approve");
        approve.setElType(HElementType.SUBMIT_BUTTON);
        approve.setOnClick(v -> {
            approve.getButtonView().setFocusableInTouchMode(true);
            approve.getButtonView().requestFocus();
            approve.getButtonView().setFocusableInTouchMode(false);
            if (!checkFormData())
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields to approve");
            else {
                submitOtherSurveyDetails(1);
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
            // annotation.addEl(back);
            workLocationSection.setNotEditable();
            assessmentOriginalWorks.setNotEditable();
        } else {
            assessmentOriginalWorks.addEl(save);
            assessmentOriginalWorks.addEl(approve);
        }
        sections.add(workLocationSection);
        sections.add(assessmentOriginalWorks);


        return new HRootElement("Other Works Sampling - Form 5", sections);
    }


    private void submitOtherSurveyDetails(final int flag) {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(OTHER_SURVEY, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(flag == 0 ? R.layout.dialog_submit_form : R.layout.dialog_approve_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            /*stop service */
            stopService(new Intent(this, FloatingWindow.class));
            FloatingWindow.started = false;

            Map<String, ArrayList<String>> tableMetadata = SurveyCreation.getTableMetaData(Database.TABLE_SURVEY_MASTER, db);
            ContentValues cv = insertValuesToSurveyMaster(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            cv.put(Database.APP_ID, BuildConfig.VERSION_CODE);


            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            if (flag == 1) {
                cv.put(Database.FORM_STATUS, 1);
            }
            db.updateSurveyMasterWithFormId(formId, cv);
            tableMetadata = SurveyCreation.getTableMetaData(Database.TABLE_OTHER_WORKS, db);
            cv = insertValuesToOtherWorks(formId, tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
            File mediaStorageDir = surveyCreation.getPictureFolder(Constants.FORMTYPE_OTHERWORKS, pref.getString(Database.FORM_ID, "0"));
            if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                cv.put(Database.PHOTOS_COUNT, mediaStorageDir.list().length);
            }
            cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
            db.updateTableWithFormId(Database.TABLE_OTHER_WORKS, formId, cv);
            pref.edit().clear().apply();
            setClearPref(true);
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void takePicture(String status) {
        staus_new = status;
        String photoDirectory = OtherSurvey.this.getExternalFilesDir(null) + "/Photo/";
        GPSTracker mGpsTracker = new GPSTracker(OtherSurvey.this);
        gpsLocation = mGpsTracker.getLocation();
        if (status.equalsIgnoreCase("1")) {
            mediaStorageDir = new File(photoDirectory + File.separator + "BoundaryConsolidation" + File.separator + pref.getString(Database.FORM_ID, "") + File.separator + "Start" + File.separator +
                    gpsLocation.getLatitude() + File.separator + gpsLocation.getLongitude() + File.separator + gpsLocation.getAltitude());

        } else if (status.equalsIgnoreCase("2")) {
            mediaStorageDir = new File(photoDirectory + File.separator + "BoundaryConsolidation" + File.separator + pref.getString(Database.FORM_ID, "") + File.separator + "Middle" + File.separator +
                    gpsLocation.getLatitude() + File.separator + gpsLocation.getLongitude() + File.separator + gpsLocation.getAltitude());

        } else if (status.equalsIgnoreCase("3")) {
            mediaStorageDir = new File(photoDirectory + File.separator + "BoundaryConsolidation" + File.separator + pref.getString(Database.FORM_ID, "") + File.separator + "End" + File.separator +
                    gpsLocation.getLatitude() + File.separator + gpsLocation.getLongitude() + File.separator + gpsLocation.getAltitude());

        }
        Log.e("dcsdc", "" + mediaStorageDir.getPath());
        long imageClickTimestamp = System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(imageClickTimestamp));

        String imageFileName = "IMG_BOUNDARY_CONSOLIDATION" + timeStamp + "";
        imageFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName + ".jpg");
        File file = imageFile.getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri mImageCaptureUri = FileProvider.getUriForFile(
                OtherSurvey.this,
                OtherSurvey.this
                        .getPackageName() + ".provider", imageFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(OtherSurvey.this.getContentResolver(), "A photo", mImageCaptureUri);
            i.setClipData(clip);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    OtherSurvey.this.getPackageManager()
                            .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                OtherSurvey.this.grantUriPermission(packageName, mImageCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        try {
            startActivityForResult(i, TAKE_PICTURE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
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


    private ContentValues insertValuesToOtherWorks(long formId, ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
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

            if (columnName.equals(Database.ORIGINAL_WORK_DIMENSION_VOLUME_MTRS)) {
                float height = cv.getAsFloat(Database.ORIGINAL_WORK_DIMENSION_HEIGHT_MTRS);
                float length = cv.getAsFloat(Database.ORIGINAL_WORK_DIMENSION_LENGTH_MTRS);
                float width = cv.getAsFloat(Database.ORIGINAL_WORK_DIMENSION_WIDTH_MTRS);
                float volume = height * length * width;
                cv.put(columnName, volume);
            }
            if (columnName.equals(Database.TIME_TAKEN_TO_COMPLETE_WORK_MONTHS)) {
                int year1, year2, month2, month1;
                try {
                    year1 = cv.getAsInteger(Database.WHEN_WORK_START_YR);
                } catch (Exception ex) {
                    year1 = 0;
                }
                try {
                    month1 = cv.getAsInteger(Database.WHEN_WORK_STARTED_MONTH);
                } catch (Exception ex) {
                    month1 = 0;
                }
                try {
                    year2 = cv.getAsInteger(Database.WHEN_WORK_COMPLETED_YEAR);
                } catch (Exception e) {
                    year2 = 0;
                }
                try {
                    month2 = cv.getAsInteger(Database.WHEN_WORK_COMPLETED_MONTH);
                } catch (Exception ex) {
                    month2 = 0;
                }
                int timeTaken = (year2 - year1) * 12 + (month2 - month1);
                if (timeTaken < 0) {
                    timeTaken = 0;
                }
                cv.put(columnName, timeTaken);
            }

           /* if (columnName.equals(Database.WORK_APPROVED_IN_APO_TIMESTAMP) || columnName.equals(Database.COMPLETION_CERTIFICATE_TIMESTAMP) || columnName.equals(Database.CHECK_MEASUREMENT_TIMESTAMP)) {
                long timestamp = -1;
                try {
                    timestamp = surveyCreation.convertDateToTimeStamp(pref.getString(columnName, ""));
                } catch (ParseException e) {
                    timestamp = 0;
                }
                cv.put(columnName, timestamp);

            }*/
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
        cv.put(Database.FORM_TYPE, Constants.FORMTYPE_OTHERWORKS);
        cv.put(Database.ENDING_TIMESTAMP, endingTimeStamp);
        GPSTracker gpsTracker = new GPSTracker(this);
        cv.put(Database.AUTOMATIC_LATITUDE, gpsTracker.getLatitude());
        cv.put(Database.AUTOMATIC_LONGITUDE, gpsTracker.getLongitude());
        return cv;
    }


    @Override
    public void onBackPressed() {
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(OTHER_SURVEY, Context.MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if (pref.getString("formStatus", "0").equals("0")) {
                showEventDialog(SweetAlertDialog.WARNING_TYPE, "Save Form");
            }
            if (!pref.getString("formStatus", "0").equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }

        } else {
            getSupportFragmentManager().popBackStack();
        }


        //   workCode.setValue(generateWorkCode(pref,db));
    }

    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitOtherSurveyDetails(0));

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.takeNote:
                start_stop();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void start_stop() {
        if (checkPermission()) {
           /* if (FloatingWindow.started) {
                stopService(new Intent(this, FloatingWindow.class));
                FloatingWindow.started = false;
            } */
            if (!FloatingWindow.started) {
                Intent serviceIntent = new Intent(this, FloatingWindow.class);
                serviceIntent.putExtra(Database.PREFERENCE, OTHER_SURVEY);
                startService(serviceIntent);
                FloatingWindow.started = true;

            }
        } else {
            reqPermission();
        }

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                reqPermission();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

    private void reqPermission() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Screen overlay detected");
        alertBuilder.setMessage("Enable 'Draw over other apps' in your system setting.");
        alertBuilder.setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, RESULT_OK);
            }
        });

        alert = alertBuilder.create();
        alert.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_REQUEST) {
            try {
                copyExif(imageFile.getAbsolutePath());
                ImageUtil.compressImage(imageFile, 1000, 1000, Bitmap.CompressFormat.JPEG, 100, imageFile.getAbsolutePath()
                        , gpsLocation.getLatitude(), gpsLocation.getLongitude(), gpsLocation.getAltitude());
//                Bitmap bmp = ImagePicker.getImageFromResult(context, resultCode, data);

                pasteExif();
                if (staus_new.equalsIgnoreCase("1"))
                    tt_first.setLabel("" + imageFile);
                if (staus_new.equalsIgnoreCase("2"))
                    tt_mid.setLabel("" + imageFile);
                if (staus_new.equalsIgnoreCase("3"))
                    tt_end.setLabel("" + imageFile);

                imageFile = null;
//                createImageInfoFile(imageClickTimestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void copyExif(String oldPath) throws IOException {
        ExifInterface oldExif = new ExifInterface(oldPath);
        exifAttributes.clear();
        for (int i = 0; i < attributes.length; i++) {
            String value = oldExif.getAttribute(attributes[i]);
            exifAttributes.add(value);
        }


    }

    public void pasteExif() {
        try {
            ExifInterface newExif = new ExifInterface(imageFile.getAbsolutePath());
            for (int i = 0; i < exifAttributes.size(); i++) {
                newExif.setAttribute(attributes[i], exifAttributes.get(i));
            }
            newExif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("imageFile", imageFile);
    }
}
