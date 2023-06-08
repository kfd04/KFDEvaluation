package com.kar.kfd.gov.kfdsurvey.plantation;

import static com.kar.kfd.gov.kfdsurvey.plantation.AddSpecies.SPECIES_PREF;
import static com.kar.kfd.gov.kfdsurvey.plantation.ControlPlotType.CONTROL_PLOT_MASTER;
import static com.kar.kfd.gov.kfdsurvey.plantation.SamplePlotSurvey.SAMPLE_PLOT_DETAILS;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.location.AppSettingsFrag;
import com.kar.kfd.gov.kfdsurvey.map.MapGps;
import com.kar.kfd.gov.kfdsurvey.map.MapGps_Individual;
import com.kar.kfd.gov.kfdsurvey.utils.ImageUtil;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.el.HButtonElement;
import com.ngohung.form.el.HDatePickerElement;
import com.ngohung.form.el.HElementType;
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
import com.ngohung.form.util.GPSTracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Modified by Sarath
 */
public class PlantationSamplingEvaluation extends HBaseFormActivity {

    public static final String folderName = "Plantation" + File.separator + "Evaluation details";
    public static final String folderName_Upload_Board = "Plantation" + File.separator + "Upload Board Plantation";
    public static final String BASIC_INFORMATION = "Basic information";
    public static final String TAKE_GPS_RECORDING = "Take GPS Readings";
    public static final String CASUALTY_REPLACEMENT = "Casualty Replacement";
    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    private static final int TAKE_PICTURE_REQUEST = 101;
    //    public static PlantationSamplingEvaluation mPlantationEvaluation;
    Database db;
    private String radioText = "Yes";
    HTextEntryElement textElement;
    FragmentManager manager;
    SurveyCreation surveyCreation;
    int modelId;
    String previousYears, causualtyReplacementYears;
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
    private PlantationSamplingEvaluation mSurvey = this;
    private String formStatus = "0";
    private HTextEntryElement workCode;
    private HPickerElement prevPlantationYear;
    private HPickerElement yearOfCasualtyReplacement;
    private Boolean mSeedling = false;
    private HPrefDataStore store;
    private HTextEntryElement gpsMeasure;
    public boolean wantToExit = false;
    private TextView toolBarRightText;
    private SharedPreferences pref;
    private long imageClickTimestamp;
    private File imageFile;
    private File mediaStorageDir;
    private Location gpsLocation;
    private HTextElement image_board;
    private File mediaStorageDir_board_image;
    //survival

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mPlantationEvaluation = this;
        manager = getSupportFragmentManager();
        surveyCreation = (SurveyCreation) manager.findFragmentById(R.id.fragmentContainer);
        if (savedInstanceState != null)
            imageFile = (File) savedInstanceState.getSerializable("imageFile");
        GPSTracker mGpsTracker = new GPSTracker(PlantationSamplingEvaluation.this);
        gpsLocation = mGpsTracker.getLocation();
        String photoDirectory = PlantationSamplingEvaluation.this.getExternalFilesDir(null) + "/Photo/";
        if (gpsLocation != null) {
            mediaStorageDir = new File(photoDirectory + File.separator + folderName + File.separator +
                    gpsLocation.getLatitude() + File.separator +
                    gpsLocation.getLongitude() + File.separator + gpsLocation.getAltitude());

            mediaStorageDir_board_image = new File(photoDirectory + File.separator + folderName_Upload_Board + File.separator +
                    gpsLocation.getLatitude() + File.separator +
                    gpsLocation.getLongitude() + File.separator + gpsLocation.getAltitude());
        } else {
            mediaStorageDir = new File(photoDirectory + File.separator + folderName + File.separator +
                    0.0 + File.separator +
                    0.0 + File.separator + 0.0);

            mediaStorageDir_board_image = new File(photoDirectory + File.separator + folderName_Upload_Board + File.separator +
                    0.0 + File.separator +
                    0.0 + File.separator + 0.0);
        }
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.plantation_evaluation);
        toolbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));

        toolBarRightText = findViewById(R.id.toolbar_right_subtitle);
        ArrayList<ArrayList<String>> forms = db.getAllFormData();
        Log.e("ACSD", "" + forms.size());
    }

    @Override
    protected HRootElement createRootElement() {

        ArrayList<HSection> sections = new ArrayList<>();
        db = new Database(getApplicationContext());
        final GPSTracker gpsTracker = new GPSTracker(mSurvey);

        pref = this.getApplicationContext().getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
        store = new HPrefDataStore(pref);
        formStatus = pref.getString("formStatus", "0");
        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }

        previousYears = "1990|1991|1992|1993|1994|1995|1996|1997|1998|1999|2000|2001";

        /*Basic Information*/


        final HSection basicInfoSection = new HSection(" Basic information of plantation(to be recorded by evaluator)");

        HButtonElement locationDetails = new HButtonElement("A. Location Details");
        basicInfoSection.addEl(locationDetails);
        locationDetails.setOnClick(v -> {
            AppSettingsFrag appSettingsFrag = new AppSettingsFrag();
            Bundle bundle = new Bundle();
            bundle.putString("preference", BASIC_INFORMATION);
            appSettingsFrag.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, appSettingsFrag, "Home page");
            transaction.addToBackStack(null);
            transaction.commit();
        });


        HPickerElement vfcPostMaintenanceDone = new HPickerElement(Database.LEGAL_STATUS_OF_LAND,
                " 10.Legal status of land", "Select an option", true, "RF|PF|Deemed Forest|Govt land|Municipal land|Institutional land|Gomal|Section 4|Others", store);
        basicInfoSection.addEl(vfcPostMaintenanceDone);

        HTextAreaEntryElement vfcPostMaintenanceDoneOtherDetails = new HTextAreaEntryElement(Database.LEGAL_STATUS_OF_LAND_OTHER_DETAILS, "Specify ", "Enter the other type", true, store);
        vfcPostMaintenanceDone.addElementForValue(vfcPostMaintenanceDoneOtherDetails, 8);
        basicInfoSection.addEl(vfcPostMaintenanceDoneOtherDetails);

        HTextEntryElement rfName = new HTextEntryElement(Database.RF_NAME, "11.Name of the RF/PF/DF",
                "Enter" +
                        " the RF name", true, store);
        vfcPostMaintenanceDone.addElementForValue(rfName, 0);
        vfcPostMaintenanceDone.addElementForValue(rfName, 1);
        vfcPostMaintenanceDone.addElementForValue(rfName, 2);
        basicInfoSection.addEl(rfName);

        HTextEntryElement plantationName = new HTextEntryElement(Database.PLANTATION_NAME,
                "12.Name of Plantation ", "Enter the plantation name", true, store);
        basicInfoSection.addEl(plantationName);

        workCode = new HTextEntryElement(Database.WORK_CODE, "13.Work Code", "Enter location details to generate work code", false, store);
        basicInfoSection.addEl(workCode);
        workCode.setNotEditable();
        // workCode.setValue(generateWorkCode(pref, db));


        HTextElement documentsConsulted = new HTextElement("B. Documents Maintained");
        basicInfoSection.addEl(documentsConsulted);


        HPickerElement apo = new HPickerElement(Database.APO, "1. APO", "Select an option", true, "Yes|No", store);
        basicInfoSection.addEl(apo);

        HDatePickerElement apoDate = new HDatePickerElement(Database.APO_DATE, "APO Sanctioned Date", "Select Date", false, store);
        basicInfoSection.addEl(apoDate);

        HPickerElement workEstimates = new HPickerElement(Database.WORK_ESTIMATES, "2. Work Estimates", "Select an option", true, "Yes|No|Partially", store);
        basicInfoSection.addEl(workEstimates);

        HNumericElement noOfWorkEstimates = new HNumericElement(Database.NO_OF_WORK_ESTIMATES, "Number of Work Estimates", "Enter work estimates", false, store);
        basicInfoSection.addEl(noOfWorkEstimates);

        HPickerElement fnb = new HPickerElement(Database.FNB, "3. FNB", "Select an option", true, "Yes|No", store);
        basicInfoSection.addEl(fnb);

        HPickerElement plantationJournal = new HPickerElement(Database.PLANTATION_JOURNAL, "4. " +
                "Plantation Journal", "Select an option", true, "Yes|No", store);
        basicInfoSection.addEl(plantationJournal);
        //------------------------------------------------------------------------------------------


        HTextElement plantationBasicInfo = new HTextElement("C. Details of plantation as per  plantation" +
                " journal");
        basicInfoSection.addEl(plantationBasicInfo);

        HPickerElement yearOfPlanting = new HPickerElement(Database.YEAR_OF_PLANTING, "1.Year of Planting", "Select an option", true, "2000-01|2001-02|2002-03|2003-04|2004-05|2005-06|2006-07|2007-08|2008-09|2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21", store);
        basicInfoSection.addEl(yearOfPlanting);

        HNumericElement grossPlantationArea = new HNumericElement(Database.GROSS_PLANTATION_AREA_HA, "2.Gross plantation area ( in hectare ) as per plantation journal", "Enter the gross plantation area ( in hectare )", true, store);
        basicInfoSection.addEl(grossPlantationArea);


        HNumericElement netPlantationArea = new HNumericElement(Database.NET_PLANTATION_AREA_HA, "3.Net plantation area ( in hectare ) as per plantation journal", "Enter the net plantation area ( in hectare )", true, store);
        basicInfoSection.addEl(netPlantationArea);
        netPlantationArea.setNotEditable();


      /*  String netplantarea = pref.getString(Database.NET_PLANTATION_AREA_HA, "");
        if (!netplantarea.equals("")) {
            int netarea = Integer.parseInt(netplantarea);
            double x = netarea * (0.02);
            int samplePlot = (int) (x / 0.1);
            samplePlots = String.valueOf(samplePlot);
        }
        netPlantationArea.addValueChangedListener(el -> {
            String area = netPlantationArea.getEditText().getText().toString();
            if (!area.equals("")) {
                int netarea = Integer.parseInt(area);
                double x = netarea * (0.02);
                int samplePlot = (int) (x / 0.1);
                samplePlots = String.valueOf(samplePlot);

            }
        });*/

        HPickerElement plantationAreaScheme = new HPickerElement(Database.SCHEME_NAME, Database.SCHEME_ID, "4.Scheme", "Select the scheme", true, 0, db.getSchemesWithId(), store);
        basicInfoSection.addEl(plantationAreaScheme);

        HPickerElement plantationModel = new HPickerElement(Database.PLANTATION_MODEL, "5.Plantation model", "Select an option", true, db.getNewNamesOfModels(), store);
        modelId = plantationModel.getIndex() + 1;
        store.saveValueToStore(Database.MODEL_ID, String.valueOf(modelId));
        basicInfoSection.addEl(plantationModel);
        plantationModel.setEditable(false);

        plantationModel.addValueChangedListener(el -> {
            HPickerElement pickerItem = (HPickerElement) el;
            Log.d("onValueChanged", "onValueChanged: " + pickerItem.getIndex() + 1 + pickerItem.getValue());
            modelId = pickerItem.getIndex() + 1;
            pref.edit().putString(Database.PLANTATION_MODEL, pickerItem.getValue()).apply();
            store.saveValueToStore(Database.MODEL_ID, String.valueOf(modelId));
            reloadFormData();

        });


          /*


        HPickerElement totalSpeciesCount = new HPickerElement(Database.TOTAL_SPECIES_COUNT,"6. Total Species planted","Select an option",false,"1|2|3|4" +
                "|5|6|7|8|9|10",store);
        basicInfoSection.addEl(totalSpeciesCount);

        final HPickerElement mainSpeciesPlanted = new HPickerElement(Database.MAIN_SPECIES_PLANTED, "Species planted", "Select species names", false,db.getNamesOfSpeciesNew(modelId) , store);
        basicInfoSection.addEl(mainSpeciesPlanted);

        HPickerElement pbviseSize = new HPickerElement(Database.TOTAL_SEEDLINGS_PLANTED_PBSIZE,"Size of species planted","Select an option",false,"5 X 8|" +
                "6 X 9|8 X 12|10 X 16|14 X 20",store);
        basicInfoSection.addEl(pbviseSize);

        HTextEntryElement pbviseSizeValue = new HTextEntryElement(Database.TOTAL_SEEDLINGS_PLANTED_PBSIZE_VALUE, "No. of seedlings", "Enter number of seedlings", false, store);
        mainSpeciesPlanted.addElementForValue(totalSpeciesCount,0);
        basicInfoSection.addEl(pbviseSizeValue);

        HTextAreaEntryElement speciesOther = new HTextAreaEntryElement(SPECIES_OTHER,"Other Species ( separated by comma ) ","Press put a comma after each species name",false,store);
        mainSpeciesPlanted.addElementForValue(totalSpeciesCount,0);
        basicInfoSection.addEl(speciesOther);


        HTextEntryElement plantationModelOthers = new HTextEntryElement(Database.PLANTATION_MODEL_OTHERS, "Others", "Enter the other plantation model type", false, store);
        mainSpeciesPlanted.addElementForValue(totalSpeciesCount,0);
        basicInfoSection.addEl(plantationModelOthers);*/

    /*    Log.d("createRootElement", "createRootElement: " + modelId);
        plantationModel.addValueChangedListener(new HValueChangedListener() {
            @Override
            public void onValueChanged(HElement el) {
                //  int id = el.getElType();
                HPickerElement pickerItem = (HPickerElement) el;
                Log.d("onValueChanged", "onValueChanged: " + pickerItem.getIndex() + 1 + pickerItem.getValue());
                modelId = pickerItem.getIndex() + 1;
                pref.edit().putInt("model_id", modelId).commit();

            }
        });

        pref.edit().putInt("model_id", modelId).commit();*/


        final HMultiPickerElement earthWorkType = new HMultiPickerElement(Database.TYPE_OF_EARTH_WORK_DONE,
                "6.Type of earth work done", "Select an option", true, "Pit|Trench & mound|Pit in Pit|Ripping|Others"
                , store);
        basicInfoSection.addEl(earthWorkType);

        HPickerElement pitSize = new HPickerElement(Database.PITSIZE, "Pitsize", "Select pit size", true, "0.30m X 0.30m X 0.30m|0.45m X 0.45m X 0.45m|0.60m X 0.60m X 0.60m|0.75m X 0.75m X 0.75m|1m X 1m X 1m", store);
        pitSize.setHidden(true);
        basicInfoSection.addEl(pitSize);

        HPickerElement pitEspacement = new HPickerElement(Database.PIT_ESPACEMENT, "Pit espacement", "Select pit espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        pitEspacement.setHidden(true);
        basicInfoSection.addEl(pitEspacement);

        HPickerElement trenchSize = new HPickerElement(Database.TRENCH_SIZE, "Trench size", "Enter trench size", true, "4m X 0.5m X 0.5m", store);
        trenchSize.setHidden(true);
        basicInfoSection.addEl(trenchSize);

        HPickerElement trenchespacement = new HPickerElement(Database.TRENCH_ESPACEMENT, "Trench espacement", "Enter trench espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        trenchespacement.setHidden(true);
        basicInfoSection.addEl(trenchespacement);

        HTextEntryElement pitInPitSize = new HTextEntryElement(Database.PIT_IN_PIT_SIZE, "Pit in pit size", "Enter Pit in pit size in meters", true, store);
        pitInPitSize.setHidden(true);
        basicInfoSection.addEl(pitInPitSize);

        HPickerElement ppEspacement = new HPickerElement(Database.PIT_IN_PIT_ESPACEMENT, "Pit in pit size espacement", "Enter Pit in pit size espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        ppEspacement.setHidden(true);
        basicInfoSection.addEl(ppEspacement);

        HNumericElement rippingSize = new HNumericElement(Database.RIPPING_SIZE, "Ripline size", "Enter Ripline size", true, store);
        rippingSize.setHidden(true);
        basicInfoSection.addEl(rippingSize);

        HPickerElement rippingEspacement = new HPickerElement(Database.RIPPING_ESPACEMENT, "Ripping espacement", "Enter Running mtr", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        rippingEspacement.setHidden(true);
        basicInfoSection.addEl(rippingEspacement);

        HTextEntryElement othersSize = new HTextEntryElement(Database.OTHERS_SIZE, "Others size", "Enter Others size", true, store);
        othersSize.setHidden(true);
        basicInfoSection.addEl(othersSize);

        HPickerElement othersespacement = new HPickerElement(Database.OTHERS_ESPACEMENT, "Others espacement", "Enter Others espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        othersespacement.setHidden(true);
        basicInfoSection.addEl(othersespacement);

        earthWorkType.setListener((pos, options, values) -> {

            for (int i = 0; i < options.length; i++) {

                String option = options[i];
                boolean isChecked = !values[i];

                switch (option) {

                    case "Pit":
                        pitSize.setHidden(isChecked);
                        pitEspacement.setHidden(isChecked);
                        break;

                    case "Trench & mound":
                        trenchSize.setHidden(isChecked);
                        trenchespacement.setHidden(isChecked);
                        break;

                    case "Pit in Pit":
                        pitInPitSize.setHidden(isChecked);
                        ppEspacement.setHidden(isChecked);
                        break;
                    case "Ripping":
                        rippingSize.setHidden(isChecked);
                        rippingEspacement.setHidden(isChecked);
                        break;
                    case "Others":
                        othersSize.setHidden(isChecked);
                        othersespacement.setHidden(isChecked);
                        break;


                }
            }
        });

        /*final HTextAreaEntryElement earthWorkDetails = new HTextAreaEntryElement(Database.TYPE_OF_EARTH_WORK_DONE_OTHERDETAILS, "Details of earth work done", "Enter details of earth work", true, store);
        basicInfoSection.addEl(earthWorkDetails);
        earthWorkType.addElementForValue(earthWorkDetails, 4);*/

        HButtonElement listSpecies = new HButtonElement("Details of Seedlings Planted");
        basicInfoSection.addEl(listSpecies);
        listSpecies.setOnClick(v -> {

            Intent i = new Intent(getApplicationContext(), SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            getSharedPreferences(SPECIES_PREF, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(formId)).apply();
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.SPECIES_LIST);
            i.putExtra("formStatus", formStatus);
            i.putExtra(Database.PART_TYPE, "Seedling");
            startActivity(i);


        });

        HNumericElement pbviseSizeValue = new HNumericElement(Database.PLANTING_DENSITY_HA,
                "7.Planting Density(No. of seedlings per ha)", "Enter number of seedlings",
                true,
                store);
        basicInfoSection.addEl(pbviseSizeValue);


        Log.d("SelectedValueSunil", pref.getString(Database.NO_OF_YEARS_MAINTAINED, "0"));


        HNumericElement annualAverageRainfall = new HNumericElement(Database
                .AVERAGE_ANNUAL_RAINFALL_MM, "8.Average annual rainfall in the locality ( in millimeters ) ", "Enter the annual average rainfall ( in millimeters ) ", true, store);
        basicInfoSection.addEl(annualAverageRainfall);

        HPickerElement soilType = new HPickerElement(Database.SOIL_TYPE, "9.Soil Type", "Select an option", true, "Red soil|Lateritic soil|Black soil|Alluvial soil|Coastal soil", store);
        basicInfoSection.addEl(soilType);

        HPickerElement plantationNoWatchersProvided = new HPickerElement(Database
                .NO_OF_WATCHERS_PROVIDED, "10.Number of watchers provided", "Select an option", true, "0|1|2|3|4|5", store);
        basicInfoSection.addEl(plantationNoWatchersProvided);

        HPickerElement plantationNoYearsMaintained = new HPickerElement(Database
                .NO_OF_YEARS_MAINTAINED, "11.Number of years maintained as on date", "Select an option", true, "0|1|2|3|4|5|6|7|8", store);
        basicInfoSection.addEl(plantationNoYearsMaintained);


        HPickerElement casualtyReplacementCheck = new HPickerElement(Database
                .IS_CASUALTY_REPLACEMENT_DONE, "12. Whether the casualty replacement has been  " +
                "done? ",
                "Select an option", true, "Yes|No", store);
        basicInfoSection.addEl(casualtyReplacementCheck);

        yearOfCasualtyReplacement = new HPickerElement(Database.YEAR_OF_CASUALTY_REPLACEMENT, "Year of Casualty Replacement", "Select an option", true, "2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21", store);
        casualtyReplacementCheck.addPosElement(yearOfCasualtyReplacement);
        basicInfoSection.addEl(yearOfCasualtyReplacement);


        HMultiPickerElement pbviseSize = new HMultiPickerElement(Database.REPLACEMENT_PBSIZE, "Size of Polybag used for replacement", "Select an option", true, "4 X 6|5 X 8|" +
                "6 X 9|8 X 12|10 X 16|14 X 20", store);
        casualtyReplacementCheck.addPosElement(pbviseSize);
        basicInfoSection.addEl(pbviseSize);

        HNumericElement noOfSeedlings = new HNumericElement(Database
                .NO_OF_REPLACED_SEEDLINGS, "No of Seedlings used for replacement", "Enter number of seedlings", true, store);
        casualtyReplacementCheck.addPosElement(noOfSeedlings);
        basicInfoSection.addEl(noOfSeedlings);

        yearOfPlanting.addValueChangedListener(el -> {
            HPickerElement pickerElement = (HPickerElement) el;

            String year = pickerElement.getValue();

            switch (year) {

                case "2000-01":
                    previousYears = "1990|1991|1992|1993|1994|1995|1996|1997|1998|1999|2000";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2000-01|2001-02|2002-03|2003-04|2004-05|2005-06|2006-07|2007-08|2008-09|2009-10";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2001-02":
                    previousYears = "1991|1992|1993|1994|1995|1996|1997|1998|1999|2000|2001";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2001-02|2002-03|2003-04|2004-05|2005-06|2006-07|2007-08|2008-09|2009-10|2010-11";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2002-03":
                    previousYears = "1992|1993|1994|1995|1996|1997|1998|1999|2000|2001|2002";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2002-03|2003-04|2004-05|2005-06|2006-07|2007-08|2008-09|2009-10|2010-11|2011-12";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2003-04":
                    previousYears = "1993|1994|1995|1996|1997|1998|1999|2000|2001|2002|2003";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2003-04|2004-05|2005-06|2006-07|2007-08|2008-09|2009-10|2010-11|2011-12|2012-13";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2004-05":
                    previousYears = "1994|1995|1996|1997|1998|1999|2000|2001|2002|2003|2004";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2004-05|2005-06|2006-07|2007-08|2008-09|2009-10|2010-11|2011-12|2012-13|2013-14";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2005-06":
                    previousYears = "1995|1996|1997|1998|1999|2000|2001|2002|2003|2004|2005";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2005-06|2006-07|2007-08|2008-09|2009-10|2010-11|2011-12|2012-13|2013-14|2014-15";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2006-07":
                    previousYears = "1996|1997|1998|1999|2000|2001|2002|2003|2004|2005|2006";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2006-07|2007-08|2008-09|2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2007-08":
                    previousYears = "1997|1998|1999|2000|2001|2002|2003|2004|2005|2006|2007";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2007-08|2008-09|2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2008-09":
                    previousYears = "1998|1999|2000|2001|2002|2003|2004|2005|2006|2007|2008";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2008-09|2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2009-10":
                    previousYears = "1999|2000|2001|2002|2003|2004|2005|2006|2007|2008|2009";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2010-11":
                    previousYears = "2000|2001|2002|2003|2004|2005|2006|2007|2008|2009|2010";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2011-12":
                    previousYears = "2001|2002|2003|2004|2005|2006|2007|2008|2009|2010|2011";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2012-13":
                    previousYears = "2002|2003|2004|2005|2006|2007|2008|2009|2010|2011|2012";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;

                case "2013-14":
                    previousYears = "2003|2004|2005|2006|2007|2008|2009|2010|2011|2012|2013";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2014-15|2015-16|2016-17|2017-18|2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
                case "2014-15":
                    previousYears = "2004|2005|2006|2007|2008|2009|2010|2011|2012|2013|2014";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2015-16|2016-17|2017-18|2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
                case "2015-16":
                    previousYears = "2005|2006|2007|2008|2009|2010|2011|2012|2013|2014|2015";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2016-17|2017-18|2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
                case "2016-17":
                    previousYears = "2006|2007|2008|2009|2010|2011|2012|2013|2014|2015|2016";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2017-18|2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
                case "2017-18":
                    previousYears = "2007|2008|2009|2010|2011|2012|2013|2014|2015|2016|2017";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2018-19|2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
                case "2018-19":
                    previousYears = "2008|2009|2010|2011|2012|2013|2014|2015|2016|2017|2018";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2019-20";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
                case "2019-20":
                    previousYears = "2009|2010|2011|2012|2013|2014|2015|2016|2017|2018|2019|2020";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2020-21";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
                case "2020-21":
                    previousYears = "2009|2010|2011|2012|2013|2014|2015|2016|2017|2018|2019|2020|2021";
                    setPrevPlantationYear(previousYears);
                    causualtyReplacementYears = "2021-22";
                    savecausualtyReplacementYears(causualtyReplacementYears);
                    break;
            }
        });


        HTextView totalExpenditure = new HTextView("13.Expenditure incurred for planting and maintenance");
        basicInfoSection.addEl(totalExpenditure);
        HTextView totalAdvanceWork = new HTextView("1.Advance work ( in Rupees )");
        basicInfoSection.addEl(totalAdvanceWork);
        final HNumericElement earthWork = new HNumericElement(Database.PLANTATION_TOTEXP_EARTHWORK, "a.Earth work ", "Enter 0 if not applicable", true, store);
        basicInfoSection.addEl(earthWork);

        HDatePickerElement earthworkDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_EARTHWORK, " Sanctioned Date for Earth work", "Select Date", false, store);
        basicInfoSection.addEl(earthworkDate);

        final HNumericElement seedlingWork = new HNumericElement(Database
                .PLANTATION_TOTEXP_RAISINGSEEDLING, "b.Cost of raising seedlings", "Enter 0 if not" +
                " applicable", true, store);
        basicInfoSection.addEl(seedlingWork);

        HDatePickerElement seedlingDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_RAISINGSEEDLING, " Sanctioned Date for Raising seedlings", "Select Date", false, store);
        basicInfoSection.addEl(seedlingDate);

        final HNumericElement plantationWork = new HNumericElement(Database
                .PLANTATION_TOTEXP_RAISINGPLANTATION, "C.Cost of raising plantation", "Enter 0 if not" +
                " applicable", true, store);
        basicInfoSection.addEl(plantationWork);

        HDatePickerElement plantsDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_RAISINGPLANTS, " Sanctioned Date for Raising Plantations", "Select Date", false, store);
        basicInfoSection.addEl(plantsDate);

        final HNumericElement firstYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR1, "2.Expenditure incurred in 1st year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 1);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 2);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 3);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 4);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 5);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 6);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 7);
        plantationNoYearsMaintained.addElementForValue(firstYearMaintenance, 8);
        basicInfoSection.addEl(firstYearMaintenance);

        HDatePickerElement firstYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR1, "Sanctioned date for year1 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 1);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 2);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 3);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 4);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 5);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 6);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 7);
        plantationNoYearsMaintained.addElementForValue(firstYearDate, 8);
        basicInfoSection.addEl(firstYearDate);

        final HNumericElement secondYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR2, "3.Expenditure incurred in 2nd year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(secondYearMaintenance, 2);
        plantationNoYearsMaintained.addElementForValue(secondYearMaintenance, 3);
        plantationNoYearsMaintained.addElementForValue(secondYearMaintenance, 4);
        plantationNoYearsMaintained.addElementForValue(secondYearMaintenance, 5);
        plantationNoYearsMaintained.addElementForValue(secondYearMaintenance, 6);
        plantationNoYearsMaintained.addElementForValue(secondYearMaintenance, 7);
        plantationNoYearsMaintained.addElementForValue(secondYearMaintenance, 8);
        basicInfoSection.addEl(secondYearMaintenance);

        HDatePickerElement secondYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR2, "sanctioned date for year2 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(secondYearDate, 2);
        plantationNoYearsMaintained.addElementForValue(secondYearDate, 3);
        plantationNoYearsMaintained.addElementForValue(secondYearDate, 4);
        plantationNoYearsMaintained.addElementForValue(secondYearDate, 5);
        plantationNoYearsMaintained.addElementForValue(secondYearDate, 6);
        plantationNoYearsMaintained.addElementForValue(secondYearDate, 7);
        plantationNoYearsMaintained.addElementForValue(secondYearDate, 8);
        basicInfoSection.addEl(secondYearDate);

        final HNumericElement thirdYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR3, "4.Expenditure incurred in 3rd year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(thirdYearMaintenance, 3);
        plantationNoYearsMaintained.addElementForValue(thirdYearMaintenance, 4);
        plantationNoYearsMaintained.addElementForValue(thirdYearMaintenance, 5);
        plantationNoYearsMaintained.addElementForValue(thirdYearMaintenance, 6);
        plantationNoYearsMaintained.addElementForValue(thirdYearMaintenance, 7);
        plantationNoYearsMaintained.addElementForValue(thirdYearMaintenance, 8);
        basicInfoSection.addEl(thirdYearMaintenance);

        HDatePickerElement thirdYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR3, "sanctioned date for year3 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(thirdYearDate, 3);
        plantationNoYearsMaintained.addElementForValue(thirdYearDate, 4);
        plantationNoYearsMaintained.addElementForValue(thirdYearDate, 5);
        plantationNoYearsMaintained.addElementForValue(thirdYearDate, 6);
        plantationNoYearsMaintained.addElementForValue(thirdYearDate, 7);
        plantationNoYearsMaintained.addElementForValue(thirdYearDate, 8);
        basicInfoSection.addEl(thirdYearDate);

        final HNumericElement fourthYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR4, "5.Expenditure incurred in 4th year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(fourthYearMaintenance, 4);
        plantationNoYearsMaintained.addElementForValue(fourthYearMaintenance, 5);
        plantationNoYearsMaintained.addElementForValue(fourthYearMaintenance, 6);
        plantationNoYearsMaintained.addElementForValue(fourthYearMaintenance, 7);
        plantationNoYearsMaintained.addElementForValue(fourthYearMaintenance, 8);
        basicInfoSection.addEl(fourthYearMaintenance);

        HDatePickerElement fourthYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR4, "sanctioned date for year4 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(fourthYearDate, 4);
        plantationNoYearsMaintained.addElementForValue(fourthYearDate, 5);
        plantationNoYearsMaintained.addElementForValue(fourthYearDate, 6);
        plantationNoYearsMaintained.addElementForValue(fourthYearDate, 7);
        plantationNoYearsMaintained.addElementForValue(fourthYearDate, 8);
        basicInfoSection.addEl(fourthYearDate);

        final HNumericElement fifthYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR5, "6.Expenditure incurred in 5th year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(fifthYearMaintenance, 5);
        plantationNoYearsMaintained.addElementForValue(fifthYearMaintenance, 6);
        plantationNoYearsMaintained.addElementForValue(fifthYearMaintenance, 7);
        plantationNoYearsMaintained.addElementForValue(fifthYearMaintenance, 8);
        basicInfoSection.addEl(fifthYearMaintenance);

        HDatePickerElement fifthYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR5, "sanctioned date for year5 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(fifthYearDate, 5);
        plantationNoYearsMaintained.addElementForValue(fifthYearDate, 6);
        plantationNoYearsMaintained.addElementForValue(fifthYearDate, 7);
        plantationNoYearsMaintained.addElementForValue(fifthYearDate, 8);
        basicInfoSection.addEl(fifthYearDate);

        final HNumericElement sixthYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR6, "7.Expenditure incurred in 6th year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(sixthYearMaintenance, 6);
        plantationNoYearsMaintained.addElementForValue(sixthYearMaintenance, 7);
        plantationNoYearsMaintained.addElementForValue(sixthYearMaintenance, 8);
        basicInfoSection.addEl(sixthYearMaintenance);

        HDatePickerElement sixthYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR6, "sanctioned date for year6 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(sixthYearDate, 6);
        plantationNoYearsMaintained.addElementForValue(sixthYearDate, 7);
        plantationNoYearsMaintained.addElementForValue(sixthYearDate, 8);
        basicInfoSection.addEl(sixthYearDate);

        final HNumericElement seventhYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR7, "8.Expenditure incurred in 7th year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(seventhYearMaintenance, 7);
        plantationNoYearsMaintained.addElementForValue(seventhYearMaintenance, 8);
        basicInfoSection.addEl(seventhYearMaintenance);

        HDatePickerElement seventhYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR7, "sanctioned date for year7 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(seventhYearDate, 7);
        plantationNoYearsMaintained.addElementForValue(seventhYearDate, 8);
        basicInfoSection.addEl(seventhYearDate);

        final HNumericElement eighthYearMaintenance = new HNumericElement(Database.PLANTATION_TOTEXP_MNTNCE_YEAR8, "9.Expenditure incurred in 8th year ( in Rupees ) ", "Enter 0 if not applicable", true, store);
        plantationNoYearsMaintained.addElementForValue(eighthYearMaintenance, 8);
        basicInfoSection.addEl(eighthYearMaintenance);

        HDatePickerElement eighthYearDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR8, "sanctioned date for year8 maintainenace", "Select Date", false, store);
        plantationNoYearsMaintained.addElementForValue(eighthYearDate, 8);
        basicInfoSection.addEl(eighthYearDate);
/*
        final HButtonElement calculateTotalCosts = new HButtonElement("Calculate total cost");
        if (formStatus.equals("0")) {
            basicInfoSection.addEl(calculateTotalCosts);
        }

        final HTextEntryElement totalCosts = new HTextEntryElement(Database.PLANTATION_TOTEXP_MNTNCE_TOTAL, "Total cost ( in Rupees ) ", "Click the button to calculate total cost", true, store);
        totalCosts.setNotEditable();
        basicInfoSection.addEl(totalCosts);*/

        /*HTextEntryElement siteQuality = new HTextEntryElement(Database.SITE_QUALITY, "Site Quality as per working plan", "Enter the site quality", false, store);
        basicInfoSection.addEl(siteQuality);*/


       /* calculateTotalCosts.setOnClick(v -> {
            calculateTotalCosts.getButtonView().setFocusableInTouchMode(true);
            calculateTotalCosts.getButtonView().requestFocus();
            calculateTotalCosts.getButtonView().setFocusableInTouchMode(false);
            float year1, year2, year3, year4, year5, year6, year7, year8, seedlingFloat, planting;
            float firstYear, secondYear, thirdYear, fourthYear, earthWorkFloat, seedling;
            try {
                year1 = Float.parseFloat(firstYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year1 = 0;
            }
            try {
                year2 = Float.parseFloat(secondYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year2 = 0;
            }
            try {
                year3 = Float.parseFloat(thirdYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year3 = 0;
            }
            try {
                year4 = Float.parseFloat(fourthYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year4 = 0;
            }
            try {
                year5 = Float.parseFloat(fifthYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year5 = 0;
            }
            try {
                year6 = Float.parseFloat(sixthYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year6 = 0;
            }
            try {
                year7 = Float.parseFloat(seventhYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year7 = 0;
            }
            try {
                year8 = Float.parseFloat(eighthYearMaintenance.getValue());
            } catch (NumberFormatException ex) {
                year8 = 0;
            }
            try {
                seedling = Float.parseFloat(seedlingWork.getValue());
            } catch (NumberFormatException ex) {
                seedling = 0;
            }
            try {
                planting = Float.parseFloat(plantationWork.getValue());
            } catch (NumberFormatException ex) {
                planting = 0;
            }
            try {
                earthWorkFloat = Float.parseFloat(earthWork.getValue());
            } catch (NumberFormatException ex) {
                earthWorkFloat = 0;
            }

            float total = year1 + year2 + year3 + year4 + year5 + year6 + year7 + year8 + seedling + planting + earthWorkFloat;
            totalCosts.setValue(String.valueOf(total));
            totalCosts.getEditText().setText(String.valueOf(total));
        });
*/

        if (!formStatus.equals("0")) {
            basicInfoSection.setNotEditable();
        }


        final HSection evaluationSection = new HSection("INFORMATION TO BE RECORDED BY EVALUATOR");

        HTextView inCaseGuaRoadsidePlantation = new HTextView("Note:In case of GUA, Institutional, canal bank,Roadside or any other linear plantations," +
                " entire plantation is to be considered as single sample plot.If GUA plantations are raised in multiple blocks, biggest block should be considered as sample plot.");
        evaluationSection.addEl(inCaseGuaRoadsidePlantation);

        HTextElement workObservationsLabel = new HTextElement("A. Preliminary observations");
        evaluationSection.addEl(workObservationsLabel);

        //rfomundargea
        //123

 /*       final HTextView trackedLocation = new HTextView("Click on the button to get GPS Location");


        HGpsElement gpsElement = new HGpsElement("Get GPS Location", true);
        if (formStatus.equals("0")) {
            evaluationSection.addEl(trackedLocation);
            evaluationSection.addEl(gpsElement);
        }



        HTextEntryElement latitudeEl = new HTextEntryElement(Database.GPS_LATITUDE, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);
        evaluationSection.addEl(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.GPS_LONGITUDE, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);
        evaluationSection.addEl(longitudeEl);*/


        HButtonElement map = new HButtonElement("Perambulate Around the Plantation");
        evaluationSection.addEl(map);
        map.setOnClick(v -> {
            String does_plantation_has_multiple_block =
                    store.getPref().getString(Database.DOES_PLANTATION_HAS_MULTIPLE_BLOCK, "");
            String plantation_type =
                    store.getPref().getString(Database.PLANTATION_TYPE, "1");

            if (does_plantation_has_multiple_block.equalsIgnoreCase("")
                    || does_plantation_has_multiple_block.equalsIgnoreCase("No")) {
                Log.e("sadcasd", "" + store.getPref().getString(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, ""));
                Intent i = new Intent(mSurvey.getApplicationContext(), MapGps_Individual.class);
                i.putExtra(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
                i.putExtra("block_type", plantation_type);
                i.putExtra("block_area", store.getPref().getString(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, ""));
                pref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
                pref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING).apply();
                pref.edit().putString(Database.FOLDER_NAME, folderName).apply();
                startActivity(i);
            } else {

                String no_of_block =
                        store.getPref().getString(Database.NO_OF_BLOCK, "1");
                int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
                Intent i = new Intent(getApplicationContext(), SurveyList.class);
                i.putExtra("no_of_perambulate", no_of_block);
                i.putExtra("id", formId);
                pref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING).apply();
                pref.edit().putString(Database.FOLDER_NAME, folderName).apply();
                pref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
                i.putExtra("List-type", Constants.NO_OF_BLOCK);
                startActivity(i);
//                get_dialog_ask_plantation_block(no_of_block);
            }
        });

        gpsMeasure = new HTextEntryElement(Database.GPS_MEASUREMENT, "Gps Measurement(Polyline in metres/Polygon in Hectares)", "Approximate distance ", false);
        gpsMeasure.setNotEditable();
        gpsMeasure.setMaxLength(20);
        evaluationSection.addEl(gpsMeasure);

        if (!pref.getString(Database.GPS_MEASUREMENT_TWO, "").equalsIgnoreCase("")) {
            gpsMeasure = new HTextEntryElement(Database.GPS_MEASUREMENT_TWO, "Gps Measurement 2 (Polyline in metres/Polygon in Hectares)", "Approximate distance ", false);
            gpsMeasure.setNotEditable();
            gpsMeasure.setMaxLength(20);
            evaluationSection.addEl(gpsMeasure);
        }
        if (!pref.getString(Database.GPS_MEASUREMENT_THREE, "").equalsIgnoreCase("")) {
            gpsMeasure = new HTextEntryElement(Database.GPS_MEASUREMENT_THREE, "Gps Measurement 3 (Polyline in metres/Polygon in Hectares)", "Approximate distance ", false);
            gpsMeasure.setNotEditable();
            gpsMeasure.setMaxLength(20);
            evaluationSection.addEl(gpsMeasure);
        }
        if (!pref.getString(Database.GPS_MEASUREMENT_FOUR, "").equalsIgnoreCase("")) {
            gpsMeasure = new HTextEntryElement(Database.GPS_MEASUREMENT_FOUR, "Gps Measurement 4 (Polyline in metres/Polygon in Hectares)", "Approximate distance ", false);
            gpsMeasure.setNotEditable();
            gpsMeasure.setMaxLength(20);
            evaluationSection.addEl(gpsMeasure);
        }
        if (!pref.getString(Database.GPS_MEASUREMENT_FIVE, "").equalsIgnoreCase("")) {
            gpsMeasure = new HTextEntryElement(Database.GPS_MEASUREMENT_FIVE, "Gps Measurement 5 (Polyline in metres/Polygon in Hectares)", "Approximate distance ", false);
            gpsMeasure.setNotEditable();
            gpsMeasure.setMaxLength(20);
            evaluationSection.addEl(gpsMeasure);
        }


      /*  HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(v -> {
            permission();
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName);
            bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        evaluationSection.addEl(viewPhoto);*/


        //----------------------done by sunil------------------------

        //-----------------------------------------------------------

        HTextElement inventoryOfSamplePlots = new HTextElement("B: Inventory of sample plots");
        evaluationSection.addEl(inventoryOfSamplePlots);

//
        Log.e("dsfcdf", "" + pref.getString(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, ""));
        HPickerElement totalNoOfSamplePlot = new HPickerElement(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, "Total number of sample plots laid", "Enter the number of sample plots laid", true, "1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20", store);
        totalNoOfSamplePlot.setEditable(false);
        evaluationSection.addEl(totalNoOfSamplePlot);
        Log.e("sadcsdc", "" + totalNoOfSamplePlot.getValue());

        HButtonElement listSamplePlots = new HButtonElement("Sample Plots");
        evaluationSection.addEl(listSamplePlots);
        listSamplePlots.setOnClick(v -> {
            Intent i = new Intent(PlantationSamplingEvaluation.this, SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            getSharedPreferences(SAMPLE_PLOT_DETAILS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(formId)).apply();
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.SAMPLE_PLOT_LIST);
            i.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.SAMPLE_PLOT_STATUS, "1").apply();
            String[] perambulationType = new String[0];
            if (!gpsMeasure.getValue().isEmpty())
                perambulationType = gpsMeasure.getValue().split(":");
            if (pref.getString(Database.GPS_LATLONG_COLLECTION, "").equals("")) {
                Toast.makeText(getApplicationContext(), "Perambulate the plantation first", Toast.LENGTH_SHORT).show();
            } else if (perambulationType.length == 0) {
                Toast.makeText(getApplicationContext(), "Press Start and Stop Button in Map to calculate area", Toast.LENGTH_SHORT).show();
            } else if (perambulationType[0].equals("Polygon") && pref.getString(Database.GPS_SAMPLEPLOT_COLLECTION, "").equals("")) {
                Toast.makeText(getApplicationContext(), "Lay Sample Plots in Map", Toast.LENGTH_SHORT).show();
            } else
                startActivity(i);

        });
        Log.e("sdcSDC", "" + pref.getString(Database.NO_OF_EMPTY_PITS_TOTAL, ""));
        if (!pref.getString(Database.TOTAL_NO_SURVIVED, "").equalsIgnoreCase("")) {
            double total_survived = Double.parseDouble(pref.getString(Database.TOTAL_NO_SURVIVED, "")) +
                    Double.parseDouble(pref.getString(Database.NO_OF_EMPTY_PITS_TOTAL, ""));

            double survival_percentage = (Double.parseDouble(pref.getString(Database.TOTAL_NO_SURVIVED, "")) / total_survived) * 100;
            pref.edit().putString(Database.TOTAL_PERCENTAGE, String.valueOf(survival_percentage)).apply();
        }
        HNumericElement total_survival_perc_et = new HNumericElement(Database.TOTAL_PERCENTAGE, "Total Survival Percentage", "Complete all survey to find out survival percentage", false, store);
        evaluationSection.addEl(total_survival_perc_et);
        total_survival_perc_et.setEditable(false);

        HTextElement plantationObservation = new HTextElement("C.General observations in the " +
                "plantation");
        evaluationSection.addEl(plantationObservation);


        HPickerElement natureOfTerrain = new HPickerElement(Database
                .PLANTATION_EVLTN_NATURE_OF_TERRAIN, "1.What is the nature of terrain?", "Select an option", true, "Flat|Gentle Slope|Steep Slope", store);
        evaluationSection.addEl(natureOfTerrain);


        HPickerElement sitePrevPlantedCheck = new HPickerElement(Database.WAS_THE_SITE_PREVIOUSLY_PLANTED, "2.Was the site previously planted in last 10 years of raising of this plantation?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(sitePrevPlantedCheck);


        prevPlantationYear = new HPickerElement(Database.YEAR_OF_PREVIOUS_PLANTING, "a.Year of previous planting", "Choose year", true, previousYears, store);
        sitePrevPlantedCheck.addPosElement(prevPlantationYear);
        evaluationSection.addEl(prevPlantationYear);


        HTextAreaEntryElement plantingReasons = new HTextAreaEntryElement(Database.REASON_FOR_REPLANTING, "b.Reasons for replanting", "Enter the reasons for replanting", true, store);
        sitePrevPlantedCheck.addPosElement(plantingReasons);
        evaluationSection.addEl(plantingReasons);

        HPickerElement replantPermCheck = new HPickerElement(Database.WAS_PERMISSION_OBTAINED_FOR_REPLANTING, "c.Whether reboising/replanting permission was obtained?", "Select an option", true, "Yes|No", store);
        sitePrevPlantedCheck.addPosElement(replantPermCheck);
        evaluationSection.addEl(replantPermCheck);


        HTextAreaEntryElement replantPermDetails = new HTextAreaEntryElement(Database.DETAILS_OF_PERMISSION_FOR_REPLANTING, "Details of permission", "Enter the details", true, store);
        replantPermCheck.addPosElement(replantPermDetails);
        evaluationSection.addEl(replantPermDetails);


        HNumericElement numOfSamplingsInSite = new HNumericElement(Database.APPROXIMATE_SAPLINGS_ALIVE_TODAY, "d.Approximate number of planted saplings standing on the site as of today", "Enter number of saplings", true, store);
        sitePrevPlantedCheck.addPosElement(numOfSamplingsInSite);
        evaluationSection.addEl(numOfSamplingsInSite);
        numOfSamplingsInSite.setDecimal(false);


        HPickerElement operationPrescModelCheck = new HPickerElement(Database.PLANTATION_OPERATIONS_ASPER_PRESCRIPTION, "3.Whether all operations were carried out as per the sanctioned estimate?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(operationPrescModelCheck);


        HTextAreaEntryElement variationListCheck = new HTextAreaEntryElement(Database.PLANTATION_OPERATIONS_NOT_ASPER_PRESCRIPTION_VARIATION, "List the operations NOT Carried out", "Enter the variations", true, store);
        operationPrescModelCheck.addNegElement(variationListCheck);
        evaluationSection.addEl(variationListCheck);

        HTextAreaEntryElement reasons = new HTextAreaEntryElement(Database.PLANTATION_OPERATIONS_REASONS, "Reason for NOT carrying out the operations", "Enter the variations", true, store);
        operationPrescModelCheck.addNegElement(reasons);
        evaluationSection.addEl(reasons);


        HPickerElement damageToSeedlingCheck = new HPickerElement(Database.ANY_DAMAGE_TO_PLANTATION_OBSERVED, "4.Are any kind of damages to the tree growth and seedlings observed in the plantation?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(damageToSeedlingCheck);

        /*HTextAreaEntryElement omittedItems = new HTextAreaEntryElement(Database
                .PLANTATION_OPERATIONS_NOT_ASPER_PRESCRIPTION_VARIATION, "Please list the " +
                "Omitted Items",
                "Enter the Omitted Items", true, store);
        damageToSeedlingCheck.addNegElement(omittedItems);
        evaluationSection.addEl(omittedItems);*/

        HMultiPickerElement damageList = new HMultiPickerElement(Database.CAUSE_OF_DAMAGE, "Cause of damage", "Select an option", true, "Due to fire|Due to pest|Due to grazing|Due to wildLife|Due to enroachment|Others", store);
        damageToSeedlingCheck.addPosElement(damageList);
        evaluationSection.addEl(damageList);


        HTextElement fire = new HTextElement("Due to fire");
        fire.setHidden(true);
        evaluationSection.addEl(fire);

        HNumericElement fireArea = new HNumericElement(Database.FIRE_AREA, "Extent of damage (Ha)", "Enter area (Ha)", true, store);
        fireArea.setHidden(true);
        fireArea.setDecimal(true);
        evaluationSection.addEl(fireArea);

        HNumericElement fireSeedling = new HNumericElement(Database.FIRE_SEEDLING, "Seedlings affected", "Enter the no. of seedlings", true, store);
        fireSeedling.setHidden(true);
        fireSeedling.setDecimal(true);
        evaluationSection.addEl(fireSeedling);

        HTextElement pest = new HTextElement("Due to pest");
        pest.setHidden(true);
        evaluationSection.addEl(pest);

        HNumericElement pestArea = new HNumericElement(Database.PEST_AREA, "Extent of damage (Ha)", "Enter area damaged(Ha)", true, store);
        pestArea.setHidden(true);
        pestArea.setDecimal(true);
        evaluationSection.addEl(pestArea);

        HNumericElement pestSeedling = new HNumericElement(Database.PEST_SEEDLING, "Seedlings affected", "Enter the no. of seedlings", true, store);
        pestSeedling.setHidden(true);
        pestSeedling.setDecimal(true);
        evaluationSection.addEl(pestSeedling);

        HTextElement grazing = new HTextElement("Due to grazing");
        grazing.setHidden(true);
        evaluationSection.addEl(grazing);

        HNumericElement grazingArea = new HNumericElement(Database.GRAZING_AREA, "Extent of damage (Ha)", "Enter area damaged(Ha)", true, store);
        grazingArea.setHidden(true);
        grazingArea.setDecimal(true);
        evaluationSection.addEl(grazingArea);

        HNumericElement grazingSeedling = new HNumericElement(Database.GRAZING_SEEDLING, "Seedlings affected", "Enter the no. of seedlings", true, store);
        grazingSeedling.setHidden(true);
        grazingSeedling.setDecimal(true);
        evaluationSection.addEl(grazingSeedling);

        HTextElement wildLife = new HTextElement("Due to wildLife");
        wildLife.setHidden(true);
        evaluationSection.addEl(wildLife);

        HNumericElement wildLifeArea = new HNumericElement(Database.WILDLIFE_AREA, "Extent of damage (Ha)", "Enter area damaged(Ha)", true, store);
        wildLifeArea.setHidden(true);
        wildLifeArea.setDecimal(true);
        evaluationSection.addEl(wildLifeArea);

        HNumericElement wildLifeSeedling = new HNumericElement(Database.WILDLIFE_SEEDLING, "Seedlings affected", "Enter the no. of seedlings", true, store);
        wildLifeSeedling.setHidden(true);
        wildLifeSeedling.setDecimal(true);
        evaluationSection.addEl(wildLifeSeedling);

        HTextElement enroachment = new HTextElement("Due to enroachment");
        enroachment.setHidden(true);
        evaluationSection.addEl(enroachment);

        HNumericElement enroachmentArea = new HNumericElement(Database.ENROACHMENT_AREA, "Extent of damage (Ha)", "Enter area damaged(Ha)", true, store);
        enroachmentArea.setHidden(true);
        enroachmentArea.setDecimal(true);
        evaluationSection.addEl(enroachmentArea);

        HNumericElement enroachmentSeedling = new HNumericElement(Database.ENROACHMENT_SEEDLING, "Seedlings affected", "Enter the no. of seedlings", true, store);
        enroachmentSeedling.setHidden(true);
        enroachmentSeedling.setDecimal(true);
        evaluationSection.addEl(enroachmentSeedling);

        HTextElement others = new HTextElement("Due to others");
        others.setHidden(true);
        evaluationSection.addEl(others);

        HNumericElement otherArea = new HNumericElement(Database.CAUSE_OF_DAMAGE_OTHERS_AREA, "Extent of damage(Others) (Ha)", "Enter area damaged(Ha)", true, store);
        otherArea.setHidden(true);
        otherArea.setDecimal(true);
        evaluationSection.addEl(otherArea);

        HNumericElement otherSeedling = new HNumericElement(Database.CAUSE_OF_DAMAGE_OTHERS_SEEDLING, "Seedlings affected(Others)", "Enter the no. of seedlings", true, store);
        otherSeedling.setHidden(true);
        otherSeedling.setDecimal(true);
        evaluationSection.addEl(otherSeedling);

        damageToSeedlingCheck.addValueChangedListener(el -> {
            HPickerElement pickerItem = (HPickerElement) el;
            if (pickerItem.getValue().equalsIgnoreCase("No")) {
                damageList.clearValue();
                fire.setHidden(true);
                fireArea.setHidden(true);
                fireSeedling.setHidden(true);
                pest.setHidden(true);
                pestArea.setHidden(true);
                pestSeedling.setHidden(true);
                grazing.setHidden(true);
                grazingArea.setHidden(true);
                grazingSeedling.setHidden(true);
                wildLife.setHidden(true);
                wildLifeArea.setHidden(true);
                wildLifeSeedling.setHidden(true);
                enroachment.setHidden(true);
                enroachmentArea.setHidden(true);
                enroachmentSeedling.setHidden(true);
                others.setHidden(true);
                otherArea.setHidden(true);
                otherSeedling.setHidden(true);
            }

        });
        damageList.setListener((which, options, values) -> {

            for (int i = 0; i < options.length; i++) {

                String option = options[i];
                boolean isChecked = !values[i];

                switch (option) {


                    case "Due to fire":
                        fire.setHidden(isChecked);
                        fireArea.setHidden(isChecked);
                        fireSeedling.setHidden(isChecked);
                        break;

                    case "Due to pest":
                        pest.setHidden(isChecked);
                        pestArea.setHidden(isChecked);
                        pestSeedling.setHidden(isChecked);
                        break;

                    case "Due to grazing":
                        grazing.setHidden(isChecked);
                        grazingArea.setHidden(isChecked);
                        grazingSeedling.setHidden(isChecked);
                        break;
                    case "Due to wildLife":
                        wildLife.setHidden(isChecked);
                        wildLifeArea.setHidden(isChecked);
                        wildLifeSeedling.setHidden(isChecked);
                        break;
                    case "Due to enroachment":
                        enroachment.setHidden(isChecked);
                        enroachmentArea.setHidden(isChecked);
                        enroachmentSeedling.setHidden(isChecked);
                        break;

                    case "Others":
                        others.setHidden(isChecked);
                        otherArea.setHidden(isChecked);
                        otherSeedling.setHidden(isChecked);
                        break;


                   /* default:
                        itemDetails.setHidden(true);
                        seedlingDetails.setHidden(true);
                        trenchDetails.setHidden(true);
                        break;*/

                }
            }
        });


//        HTextView discrepancyCheck = new HTextView("5.Is there any discrepancy noticed with regard to following?");
        HMultiPickerElement discrepancy = new HMultiPickerElement(Database.DISCREPANCY, "5.Is there any discrepancy noticed with regard to following?", "Select an option", true, "Area/Extent|No. of seedlings|No. of trench or pits|Others|None", store);
        evaluationSection.addEl(discrepancy);


        HNumericElement itemDetails = new HNumericElement(Database
                .DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_WORK_DETAI, "Record the discrepancy(Area in Hectare)",
                "Record extent of variation in numbers", true, store);
        itemDetails.setHidden(true);
        evaluationSection.addEl(itemDetails);

        HNumericElement seedlingDetails = new HNumericElement(Database.DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_SEEDLING_DETAI, "Actual No. of seedlings", "Record extent of variation in numbers", true, store);
        seedlingDetails.setHidden(true);
        evaluationSection.addEl(seedlingDetails);


        HNumericElement trenchDetails = new HNumericElement(Database
                .DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_TRENCH_DETAI, "Actual No. of trench or pits", "Record extent of variation in numbers", true, store);
        trenchDetails.setHidden(true);
        evaluationSection.addEl(trenchDetails);

        HNumericElement otherDiscrepancy = new HNumericElement(Database
                .OTHER_DISCREPANCY, "Other discrepancy & extent", "Record extent of variation in numbers", true, store);
        otherDiscrepancy.setHidden(true);
        evaluationSection.addEl(otherDiscrepancy);

        discrepancy.setListener((which, options, values) -> {

            for (int i = 0; i < options.length; i++) {

                String option = options[i];
                boolean isChecked = !values[i];

                switch (option) {

                    case "Area/Extent":
                        itemDetails.setHidden(isChecked);
                        break;

                    case "No. of seedlings":
                        seedlingDetails.setHidden(isChecked);
                        break;

                    case "No. of trench or pits":
                        trenchDetails.setHidden(isChecked);
                        break;

                    case "Others":
                        otherDiscrepancy.setHidden(isChecked);
                        break;

                }
            }

        });

        HPickerElement workDocCheck = new HPickerElement(Database
                .IS_WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL, "6.Whether all the particulars have been documented in the plantation journal?", "Select an option", true, "Yes|No|Partially", store);
        evaluationSection.addEl(workDocCheck);

        HPickerElement enteriesRegularityCheck = new HPickerElement(Database
                .WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL_DETAILS, "Whether RFO & " +
                "ACF have entered their observations ?", "Select an option", true,
                "Yes|No", store);
        workDocCheck.addPosElement(enteriesRegularityCheck);
        evaluationSection.addEl(enteriesRegularityCheck);

        HPickerElement schemeProvision = new HPickerElement(Database.SCHEME_PROVISION,
                "7.Whether the plantation  maintained" +
                        " as per the scheme/model provisions?", "Select an option", true, "Yes|No",
                store);
        evaluationSection.addEl(schemeProvision);


        HTextAreaEntryElement schemeProvisionNo = new HTextAreaEntryElement(Database.SCHEME_PROVISION_NO_REASONS, "Details of" +
                " non maintenance", "", true, store);
        schemeProvision.addElementForValue(schemeProvisionNo, 1);
        evaluationSection.addEl(schemeProvisionNo);

        HPickerElement seniorOfficerCheckPlantation = new HPickerElement(Database
                .ANY_SNR_OFFICER_INSPECT_PLANTATION_AND_ENTRIES_IN_JOURNAL, "8.Did any  officer(s) of the rank of ACF  and above  inspect the plantation area and made entries in the journal?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(seniorOfficerCheckPlantation);


        HMultiPickerElement designationOfOfficer1 = new HMultiPickerElement(Database.FIRST_SNR_OFFICER_DESIGNATION, "Designations of officer", "Select an option", true, "ACF|DCF|CF|CCF|APCCF|PCCF", store);
        seniorOfficerCheckPlantation.addPosElement(designationOfOfficer1);
        evaluationSection.addEl(designationOfOfficer1);

        HPickerElement acfDate = new HPickerElement(Database.NUMBER_OF_INSPECTION_ACF, "Number of visits by ACF", "Choose number of visits", true, "1|2|3|4|5|6|7|8|9|10|more than 10", store);
        seniorOfficerCheckPlantation.addPosElement(designationOfOfficer1);
        acfDate.setHidden(true);
        evaluationSection.addEl(acfDate);


        HPickerElement dcfDate = new HPickerElement(Database.NUMBER_OF_INSPECTION_DCF, "Number of visits by DCF", "Choose number of visits", true, "1|2|3|4|5|6|7|8|9|10|more than 10", store);
        seniorOfficerCheckPlantation.addPosElement(designationOfOfficer1);
        dcfDate.setHidden(true);
        evaluationSection.addEl(dcfDate);


        HPickerElement cfDate = new HPickerElement(Database.NUMBER_OF_INSPECTION_CF, "Number of visits by CF", "Choose number of visits", true, "1|2|3|4|5|6|7|8|9|10|more than 10", store);
        seniorOfficerCheckPlantation.addPosElement(designationOfOfficer1);
        cfDate.setHidden(true);
        evaluationSection.addEl(cfDate);


        HPickerElement ccfDate = new HPickerElement(Database.NUMBER_OF_INSPECTION_CCF, "Number of visits by CCF", "Choose number of visits", true, "1|2|3|4|5|6|7|8|9|10|more than 10", store);
        seniorOfficerCheckPlantation.addPosElement(designationOfOfficer1);
        ccfDate.setHidden(true);
        evaluationSection.addEl(ccfDate);


        HPickerElement apccfDate = new HPickerElement(Database.NUMBER_OF_INSPECTION_APCCF, "Number of visits by APCCF", "Choose number of visits", true, "1|2|3|4|5|6|7|8|9|10|more than 10", store);
        seniorOfficerCheckPlantation.addPosElement(designationOfOfficer1);
        apccfDate.setHidden(true);
        evaluationSection.addEl(apccfDate);


        HPickerElement pccfDate = new HPickerElement(Database.NUMBER_OF_INSPECTION_PCCF, "Number of visits by PCCF", "Choose number of visits", true, "1|2|3|4|5|6|7|8|9|10|more than 10", store);
        seniorOfficerCheckPlantation.addPosElement(designationOfOfficer1);
        pccfDate.setHidden(true);
        evaluationSection.addEl(pccfDate);


        designationOfOfficer1.setListener((which, options, value) -> {

            for (int i = 0; i < options.length; i++) {

                String option = options[i];
                boolean isChecked = !value[i];

                switch (option) {

                    case "ACF":
                        acfDate.setHidden(isChecked);
                        break;

                    case "DCF":
                        dcfDate.setHidden(isChecked);
                        break;

                    case "CF":
                        cfDate.setHidden(isChecked);
                        break;
                    case "CCF":
                        ccfDate.setHidden(isChecked);
                        break;
                    case "APCCF":

                        apccfDate.setHidden(isChecked);
                        break;
                    case "PCCF":
                        pccfDate.setHidden(isChecked);
                        break;
                    default:
                        designationOfOfficer1.clearValue();
                        acfDate.setHidden(true);
                        dcfDate.setHidden(true);
                        cfDate.setHidden(true);
                        ccfDate.setHidden(true);
                        apccfDate.setHidden(true);
                        pccfDate.setHidden(true);
                        break;

                }
            }
        });


   /*     HPickerElement plantationWorkUniformityCheck = new HPickerElement(Database
                .IS_RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE, "8.Are the results of plantation work uniform across the site?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(plantationWorkUniformityCheck);

        HTextAreaEntryElement plantationWorkUniformityDetails = new HTextAreaEntryElement(Database.RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE_DETAILS, "What could be the reasons?", "Enter the details", true, store);
        plantationWorkUniformityCheck.addNegElement(plantationWorkUniformityDetails);
        evaluationSection.addEl(plantationWorkUniformityDetails);*/

    /*    final HPickerElement schemeObjectiveCheck = new HPickerElement(Database
                .ARE_THE_SCHEME_OBJECTIVES_MET_BY_THE_WORK, "9.Whether the objectives of the " +
                "model are met?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(schemeObjectiveCheck);


        HMultiPickerElement objectivesAsPerModel = new HMultiPickerElement(Database.PLANTATION_OBJECTIVES_ASPER_MODEL, "Objectives As Per Model", "Select Objectives", false, db.getNamesOfObjectives(modelId), store);
        schemeObjectiveCheck.addPosElement(objectivesAsPerModel);
        evaluationSection.addEl(objectivesAsPerModel);

        HTextAreaEntryElement schemeObjectiveMaintained = new HTextAreaEntryElement(Database
                .SCHEME_OBJECTIVES_MET_BY_THE_WORK_DETAILS, "How the objectives are met?",
                "Enter" +
                        " how the objectives are met", true, store);
        schemeObjectiveCheck.addPosElement(schemeObjectiveMaintained);
        evaluationSection.addEl(schemeObjectiveMaintained);

        HTextAreaEntryElement schemeObjectiveNoReasons = new HTextAreaEntryElement(Database.MODEL_OBJECTIVES_NOT_MET_BY_THE_WORK_REASONS, "What could be the reasons?", "Enter why the objective is not met", true, store);
        schemeObjectiveCheck.addNegElement(schemeObjectiveNoReasons);
        evaluationSection.addEl(schemeObjectiveNoReasons);
*/

      /*  HPickerElement performanceImprovScopeCheck = new HPickerElement(Database
                .ANY_SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION, "9.Is there any  scope for further improving the performance of the plantation?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(performanceImprovScopeCheck);

        HTextAreaEntryElement performanceImprovScopeHow = new HTextAreaEntryElement(Database.SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION_DETAILS, "Suggestions for improving performance", "Enter your suggestion here", true, store);
        performanceImprovScopeCheck.addPosElement(performanceImprovScopeHow);
        evaluationSection.addEl(performanceImprovScopeHow);*/


        String plantationType = pref.getString(Database.PLANTATION_MODEL, "");
        if (plantationType.equals("ER Model-I(A)") | plantationType.equals("ANR Model-I(B)")) {


            HTextElement conterFactualDetails = new HTextElement("D.Inventory of counter-factual " +
                    "(control plot) in respect of Ecorestoration/ANR model plantations only");
            evaluationSection.addEl(conterFactualDetails);


            HButtonElement controlPlotOutside = new HButtonElement("Outside Plantation");
            evaluationSection.addEl(controlPlotOutside);
            controlPlotOutside.setOnClick(v -> {
                /*SharedPreferences preferences = getSharedPreferences(CONTROL_PLOT_INVENTORY,Context.MODE_PRIVATE);
                preferences.edit().putString("controlPlotType","Outside Plantaion").commit();
                Intent i = new Intent(mPlantationEvaluation.getApplicationContext(), ControlPlotType.class);
                int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
                i.putExtra("id", formId);
                i.putExtra("List-type", Constants.CONTROL_PLOT_INVENTORY_LIST);
                i.putExtra("Inventory-type", "Outside");
                i.putExtra("formStatus", formStatus);
                i.putExtra("Outside", "outside");
                startActivity(i);*/

                SharedPreferences prefControlPot = getSharedPreferences(CONTROL_PLOT_MASTER, Context.MODE_PRIVATE);
                prefControlPot.edit().putString("controlPlotType", "Outside Plantaion").apply();

                int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
                pref.edit().putString(Database.OUTSIDE_PLANTATION_STATUS, "1").apply();
                Cursor cursor = db.getControlPlotMaster("Outside Plantaion", formId);
                if (cursor != null && cursor.moveToFirst()) {
                    prefControlPot.edit().clear().apply();
                    SharedPreferences.Editor editor = prefControlPot.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), ControlPlotType.class);
                    startActivity(intent);

                } else {
                    prefControlPot = getSharedPreferences(CONTROL_PLOT_MASTER, Context.MODE_PRIVATE);
                    prefControlPot.edit().putString("controlPlotType", "Outside Plantaion").apply();
                    prefControlPot.edit().putString(Database.CONTROL_PLOT_TYPE, "Outside Plantaion").apply();
                    prefControlPot.edit().putString(Database.FORM_ID, pref.getString(Database.FORM_ID, "0")).apply();
                    Intent i = new Intent(PlantationSamplingEvaluation.this, ControlPlotType.class);
                    i.putExtra("id", formId);
                    i.putExtra("List-type", Constants.CONTROL_PLOT_INVENTORY_LIST);
                    i.putExtra("Inventory-type", "WithIn");
                    i.putExtra("formStatus", formStatus);
                    startActivity(i);

                }
            });
        }
        HButtonElement uploadPlantationBoard = new HButtonElement("Upload Plantation Board Image");
        basicInfoSection.addEl(uploadPlantationBoard);
        uploadPlantationBoard.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName_Upload_Board);
            bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });

        image_board = new HTextElement("Plantation Board Image Name");
        basicInfoSection.addEl(image_board);


        final HButtonElement evaluationDetailsButton = new HButtonElement("Save ");
        evaluationDetailsButton.setElType(HElementType.SUBMIT_BUTTON);
        evaluationDetailsButton.setOnClick(v -> {
            evaluationDetailsButton.getButtonView().setFocusableInTouchMode(true);
            evaluationDetailsButton.getButtonView().requestFocus();
            evaluationDetailsButton.getButtonView().setFocusableInTouchMode(false);


            if (!checkFormData())
                showSaveFormDataAlert();
            else {
                wantToExit = true;
                onBackPressed();
            }

        });


        evaluationSection.addEl(evaluationDetailsButton);


        if (!formStatus.equals("0")) {
            evaluationSection.setNotEditable();
        }
        sections.add(basicInfoSection);
        sections.add(evaluationSection);

        return new HRootElement("", sections);
    }

    public void takePicture() {

        imageClickTimestamp = System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(imageClickTimestamp));

        String imageFileName = "PlantationBoard" + timeStamp + "";
        imageFile = new File(mediaStorageDir_board_image.getPath() + File.separator + imageFileName + ".jpg");
        File file = imageFile.getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri mImageCaptureUri = FileProvider.getUriForFile(
                PlantationSamplingEvaluation.this,
                PlantationSamplingEvaluation.this
                        .getPackageName() + ".provider", imageFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(PlantationSamplingEvaluation.this.getContentResolver(), "A photo", mImageCaptureUri);
            i.setClipData(clip);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    PlantationSamplingEvaluation.this.getPackageManager()
                            .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                PlantationSamplingEvaluation.this.grantUriPermission(packageName, mImageCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        try {
            startActivityForResult(i, TAKE_PICTURE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_REQUEST) {
            try {
                copyExif(imageFile.getAbsolutePath());
                if (gpsLocation != null) {
                    ImageUtil.compressImage(imageFile, 1000, 1000, Bitmap.CompressFormat.JPEG, 100, imageFile.getAbsolutePath()
                            , gpsLocation.getLatitude(), gpsLocation.getLongitude(), gpsLocation.getAltitude());
                } else {
                    ImageUtil.compressImage(imageFile, 1000, 1000, Bitmap.CompressFormat.JPEG, 100, imageFile.getAbsolutePath()
                            , 0.0, 0.0, 0.0);
                }
//                Bitmap bmp = ImagePicker.getImageFromResult(context, resultCode, data);

                pasteExif();
                image_board.setLabel("" + imageFile);
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

    private void get_dialog_ask_plantation_block(String no_of_block) {
        Log.e("dcasdcsd", "" + no_of_block);
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_ask_plantation_block);

        EditText no_of_block_et = dialog.findViewById(R.id.no_of_block_et);
        TextView no_of_block_tt = dialog.findViewById(R.id.no_of_block_tt);
        RadioGroup radio_group_yes_no = dialog.findViewById(R.id.radio_group_yes_no);
        Button submit_btn = dialog.findViewById(R.id.alert_submit);
        Button cancel_btn = dialog.findViewById(R.id.alert_cancel);
        radioText = "Yes";
        radio_group_yes_no.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = (RadioButton) radioGroup.findViewById(i);
            radioText = rb.getText().toString();
            if (radioText.equalsIgnoreCase("Yes")) {
                no_of_block_et.setVisibility(View.GONE);
                no_of_block_tt.setVisibility(View.GONE);
            } else {
                no_of_block_et.setVisibility(View.VISIBLE);
                no_of_block_tt.setVisibility(View.VISIBLE);
            }
        });
        submit_btn.setOnClickListener(view -> {

            if (radioText.equalsIgnoreCase("Yes")) {
                dialog.dismiss();
                String plantation_type =
                        store.getPref().getString(Database.PLANTATION_TYPE, "1");
                int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));

                Intent i = new Intent(mSurvey.getApplicationContext(), MapGps.class);
                i.putExtra(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
                pref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
                i.putExtra("id", formId);
                i.putExtra("block_no", "all");
                i.putExtra("block_type", plantation_type);
                i.putExtra("block_area", store.getPref().getString(Database.NET_PLANTATION_AREA_HA, ""));
                pref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING).apply();
                pref.edit().putString(Database.FOLDER_NAME, folderName).apply();
                startActivity(i);
            } else {
                int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
                if (no_of_block_et.getText().toString().isEmpty()) {
                    Toast.makeText(PlantationSamplingEvaluation.this, "Please enter Number of Block", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(no_of_block_et.getText().toString()) > Integer.parseInt(no_of_block)) {
                    Toast.makeText(PlantationSamplingEvaluation.this, "Maximum of " + no_of_block + " blocks can be perambulate", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Intent i = new Intent(getApplicationContext(), SurveyList.class);
                    i.putExtra("no_of_perambulate", no_of_block_et.getText().toString());
                    i.putExtra("id", formId);
                    i.putExtra("List-type", Constants.NO_OF_BLOCK);
                    startActivity(i);
                }
            }
        });
        cancel_btn.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void setPrevPlantationYear(String years) {

        if (prevPlantationYear != null)
            prevPlantationYear.setYears(years);
    }

    private void savecausualtyReplacementYears(String years) {
        if (yearOfCasualtyReplacement != null) {
            yearOfCasualtyReplacement.setYears(years);
        }

    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (!checkFormData()) {

                if (wantToExit)
                    super.onBackPressed();
                else
                    showSaveFormDataAlert();
            } else {
                SharedPreferences pref = this.getApplicationContext().getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
                pref.edit().putString(Database.FORM_FILLED_STATUS, "1").apply();
                super.onBackPressed();
            }
        } else
            super.onBackPressed();


    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
        Log.e("fwcaf", "" + textElement);
        if (textElement != null) {
            textElement.setValue(pref.getString(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, "0"));
        }


    }

    public void insertCoordinates(Location location, String formId) {
        ContentValues cv = new ContentValues();
        if (db != null) {
            cv.put(Database.FORM_ID, Integer.parseInt(formId));
            cv.put(Database.LAT, location.getLatitude());
            cv.put(Database.LONG, location.getLongitude());
            cv.put(Database.CREATION_TIMESTAMP, System.currentTimeMillis() / 1000);
            db.saveCoordinates(cv);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
        gpsMeasure.setValue(pref.getString(Database.GPS_MEASUREMENT, ""));
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
