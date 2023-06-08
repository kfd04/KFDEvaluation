package com.kar.kfd.gov.kfdsurvey.advancework;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.location.AppSettingsFrag;
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
import java.util.ArrayList;

import static com.kar.kfd.gov.kfdsurvey.advancework.AdvSamplePlotSurvey.ADV_SAMPLE_PLOT_DETAILS;

/**
 * Modified by Sarath
 */
public class PlantationSamplingAdvanceWork extends HBaseFormActivity {

    public static final String folderName = "Advancework" + File.separator + "Evaluation Photos";
    public static final String ADVANCE_WORK_SURVEY = "AdvanceWorkSurvey";
    public static final String TAKE_GPS_RECORDING = "Take GPS Readings";
    public static final String CASUALTY_REPLACEMENT = "Casualty Replacement";
    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    Database db;
    HTextEntryElement textElement;
    FragmentManager manager;
    SurveyCreation surveyCreation;
    int modelId, formFilledStatus = 0;
    String previousYears;

    private PlantationSamplingAdvanceWork mSurvey = this;
    private String formStatus = "0";
    private TextView toolBarRightText;
    private HTextEntryElement workCode;
    private boolean addMode;
    private HPickerElement prevPlantationYear;
    private HPickerElement yearOfCasualtyReplacement;
    private Boolean mSeedling = false;
    private HPrefDataStore store;
    private HTextEntryElement gpsMeasure;
    private SweetAlertDialog dialog;
    private boolean wantToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getSupportFragmentManager();
        surveyCreation = (SurveyCreation) manager.findFragmentById(R.id.fragmentContainer);


        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.plantation_evaluation);
        toolbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));

        toolBarRightText = findViewById(R.id.toolbar_right_subtitle);


    }

    @Override
    protected HRootElement createRootElement() {

        ArrayList<HSection> sections = new ArrayList<>();
        db = new Database(getApplicationContext());
        final GPSTracker gpsTracker = new GPSTracker(mSurvey);

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
        store = new HPrefDataStore(pref);

        formStatus = pref.getString("formStatus", "0");
        if (Integer.parseInt(pref.getString(Database.STARTING_TIMESTAMP, "0")) == 0) {
            pref.edit().putString(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000)).apply();
        }

        previousYears = "2003|2004|2005|2006|2007|2008|2009|2010|2011|2012|2013";

        /*Basic Information*/


        final HSection basicInfoSection = new HSection(" Basic information of AdvanceWork(to be recorded by evaluator)");

        HButtonElement locationDetails = new HButtonElement("A. Location Details");
        basicInfoSection.addEl(locationDetails);
        locationDetails.setOnClick(v -> {
            AppSettingsFrag appSettingsFrag = new AppSettingsFrag();
            Bundle bundle = new Bundle();
            bundle.putString("preference", ADVANCE_WORK_SURVEY);
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
                "12.Name of Advance work ", "Enter the plantation name", true, store);
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
        workEstimates.addPosElement(noOfWorkEstimates);

        HPickerElement fnb = new HPickerElement(Database.FNB, "3. FNB", "Select an option", true, "Yes|No", store);
        basicInfoSection.addEl(fnb);

        HPickerElement plantationJournal = new HPickerElement(Database.PLANTATION_JOURNAL, "4. " +
                "Plantation Journal", "Select an option", true, "Yes|No", store);
        basicInfoSection.addEl(plantationJournal);
        //------------------------------------------------------------------------------------------


        HTextElement plantationBasicInfo = new HTextElement("C. Details of plantation as per  plantation" +
                " journal");
        basicInfoSection.addEl(plantationBasicInfo);

        HPickerElement yearOfEarthWork = new HPickerElement(Database.YEAR_OF_EARTHWORK, "1.Year of EarthWork", "Select an option", true, "2010-11|2011-2012|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20", store);
        basicInfoSection.addEl(yearOfEarthWork);


        HNumericElement grossPlantationArea = new HNumericElement(Database.GROSS_PLANTATION_AREA_HA, "2.Gross Advance work area ( in hectare ) as per plantation journal", "Enter the gross plantation area ( in hectare )", true, store);
        basicInfoSection.addEl(grossPlantationArea);


        HNumericElement netPlantationArea = new HNumericElement(Database.NET_PLANTATION_AREA_HA, "3.Net Advance work area ( in hectare ) as per plantation journal", "Enter the net plantation area ( in hectare )", true, store);
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

        HPickerElement plantationModel = new HPickerElement(Database.PLANTATION_MODEL, "5.Proposed model", "Select an option", true, db.getNewNamesOfModels(), store);
        modelId = plantationModel.getIndex() + 1;
        store.saveValueToStore(Database.MODEL_ID, String.valueOf(modelId));
        basicInfoSection.addEl(plantationModel);

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

        HPickerElement pitSize = new HPickerElement(Database.PITSIZE, "Pit size", "Select pit size", true, "0.30m X 0.30m X 0.30m|0.45m X 0.45m X 0.45m|0.60m X 0.60m X 0.60m|0.75m X 0.75m X 0.75m|1m X 1m X 1m", store);
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

        HNumericElement othersSize = new HNumericElement(Database.OTHERS_SIZE, "Others size", "Enter Others size", true, store);
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


                   /* default:
                        itemDetails.setHidden(true);
                        seedlingDetails.setHidden(true);
                        trenchDetails.setHidden(true);
                        break;*/

                }
            }
        });

        /*final HTextAreaEntryElement earthWorkDetails = new HTextAreaEntryElement(Database.TYPE_OF_EARTH_WORK_DONE_OTHERDETAILS, "Details of earth work done", "Enter details of earth work", true, store);
        basicInfoSection.addEl(earthWorkDetails);
        earthWorkType.addElementForValue(earthWorkDetails, 4);*/


        Log.d("SelectedValueSunil", pref.getString(Database.NO_OF_YEARS_MAINTAINED, "0"));


        HNumericElement annualAverageRainfall = new HNumericElement(Database
                .AVERAGE_ANNUAL_RAINFALL_MM, "7.Average annual rainfall in the locality ( in millimeters ) ", "Enter the annual average rainfall ( in millimeters ) ", true, store);
        basicInfoSection.addEl(annualAverageRainfall);

        HPickerElement soilType = new HPickerElement(Database.SOIL_TYPE, "8.Soil Type", "Select an option", true, "Red soil|Lateritic soil|Black soil|Alluvial soil|Coastal soil", store);
        basicInfoSection.addEl(soilType);

        HPickerElement reasonForPlanting = new HPickerElement(Database.REASON_FOR_PLANTING, "Reason for Planting", "Select an option", true, "Working Plan/Micro Plan prescription|Clear felled area of last year|Highly degraded natural forest area|Forest area recovered from encroachment|Forest land prone for encroachment|Compensatory afforestation area|Open/barren area notified under Section 4|Degraded government (revenue) land|Failed plantation reboised|Requested by other department|Others", store);
        basicInfoSection.addEl(reasonForPlanting);

        HTextEntryElement reasonForPlantingOthers = new HTextEntryElement(Database.REASON_FOR_PLANTING_OTHERS, "Reason for Planting Others", "specify", true, store);
        reasonForPlanting.addElementForValue(reasonForPlantingOthers, 10);
        basicInfoSection.addEl(reasonForPlantingOthers);

        HTextView totalExpenditure = new HTextView("9.Expenditure incurred for planting and maintenance");
        basicInfoSection.addEl(totalExpenditure);
        HTextView totalAdvanceWork = new HTextView("Advance work ( in Rupees )");
        basicInfoSection.addEl(totalAdvanceWork);

        final HNumericElement earthWork = new HNumericElement(Database.PLANTATION_TOTEXP_EARTHWORK, "a.Earth work ", "Enter 0 if not applicable", true, store);
        basicInfoSection.addEl(earthWork);

        HDatePickerElement earthworkDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_EARTHWORK, " Sanctioned Date for Earth work", "Select Date", false, store);
        basicInfoSection.addEl(earthworkDate);

        final HNumericElement seedlingWork = new HNumericElement(Database
                .PLANTATION_TOTEXP_RAISINGSEEDLING, "b.Cost of raising seedlings", "Enter 0 if not" +
                " applicable", true, store);
        basicInfoSection.addEl(seedlingWork);

        HDatePickerElement seedlingDate = new HDatePickerElement(Database.PLANTATION_SANCTN_DATE_FOR_RAISINGSEEDLING, " Sanctioned Date for Raising seedling", "Select Date", false, store);
        basicInfoSection.addEl(seedlingDate);

        if (!formStatus.equals("0")) {
            basicInfoSection.setNotEditable();
        }


        final HSection evaluationSection = new HSection("INFORMATION TO BE RECORDED BY EVALUATOR");

        HTextView inCaseGuaRoadsidePlantation = new HTextView("Note:In case of GUA, Institutional, canal bank,Roadside or any other linear plantations," +
                " entire plantation is to be considered as single sample plot.If GUA plantations are raised in multiple blocks, biggest block should be considered as sample plot.");
        evaluationSection.addEl(inCaseGuaRoadsidePlantation);

      /*  HTextElement workObservationsLabel = new HTextElement("A. Preliminary observations");
        evaluationSection.addEl(workObservationsLabel);*/

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


        /*HButtonElement map = new HButtonElement("Perambulate Around the Plantation");
        evaluationSection.addEl(map);
        map.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), MapGps.class);
            i.putExtra(Database.PREFERENCE, PlantationSamplingAdvanceWork.ADVANCE_WORK_SURVEY);
            pref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
            pref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_ADVANCEWORK).apply();
            pref.edit().putString(Database.FOLDER_NAME, folderName).apply();
            startActivity(i);
        });

        gpsMeasure = new HTextEntryElement(Database.GPS_MEASUREMENT, "Gps Measurement(Polyline in metres/Polygon in Hectares)", "Approximate distance ", false);
        gpsMeasure.setNotEditable();
        gpsMeasure.setMaxLength(13);
        gpsMeasure.setValue(pref.getString(Database.GPS_MEASUREMENT, ""));
        evaluationSection.addEl(gpsMeasure);*/


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

        HTextElement inventoryOfSamplePlots = new HTextElement("A: Inventory of sample plots");
        evaluationSection.addEl(inventoryOfSamplePlots);

        HNumericElement totalNoOfSamplePlot = new HNumericElement(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, "Total number of sample plots laid", "Enter the number of sample plots laid", true, store);
        totalNoOfSamplePlot.setNotEditable();
        evaluationSection.addEl(totalNoOfSamplePlot);

        HButtonElement listSamplePlots = new HButtonElement("Sample Plots");
        evaluationSection.addEl(listSamplePlots);
        listSamplePlots.setOnClick(v -> {
            Intent i = new Intent(PlantationSamplingAdvanceWork.this, SurveyList.class);
            int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
            getSharedPreferences(ADV_SAMPLE_PLOT_DETAILS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(formId)).apply();
            i.putExtra("id", formId);
            i.putExtra("List-type", Constants.ADV_SAMPLE_PLOT_LIST);
            i.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.SAMPLE_PLOT_STATUS, "1").apply();
            startActivity(i);
        });


        HTextElement plantationObservation = new HTextElement("B.General observations in the " +
                "plantation");
        evaluationSection.addEl(plantationObservation);


        HPickerElement sitePrevPlantedCheck = new HPickerElement(Database.WAS_THE_SITE_PREVIOUSLY_PLANTED, "1.Was the site previously planted in last 10 years?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(sitePrevPlantedCheck);


        prevPlantationYear = new HPickerElement(Database.YEAR_OF_PREVIOUS_PLANTING, "a.Year of previous planting", "Choose year", true, previousYears, store);
        sitePrevPlantedCheck.addPosElement(prevPlantationYear);
        /*previousYears = pref.getString(Database.YEAR_OF_PREVIOUS_PLANTING,"");
        prevPlantationYear.setYears(previousYears);*/
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


        HPickerElement operationPrescModelCheck = new HPickerElement(Database.PLANTATION_OPERATIONS_ASPER_PRESCRIPTION, "2.Whether all operations were carried out as per the sanctioned estimate?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(operationPrescModelCheck);


        HTextAreaEntryElement variationListCheck = new HTextAreaEntryElement(Database.PLANTATION_OPERATIONS_NOT_ASPER_PRESCRIPTION_VARIATION, "List the operations NOT Carried out", "Enter the variations", true, store);
        operationPrescModelCheck.addNegElement(variationListCheck);
        evaluationSection.addEl(variationListCheck);

        HTextAreaEntryElement reasons = new HTextAreaEntryElement(Database.PLANTATION_OPERATIONS_REASONS, "Reason for NOT carrying out the operations", "Enter the variations", true, store);
        operationPrescModelCheck.addNegElement(reasons);
        evaluationSection.addEl(reasons);


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
        wildLifeArea.setDecimal(true);
        evaluationSection.addEl(enroachmentArea);

        HNumericElement enroachmentSeedling = new HNumericElement(Database.ENROACHMENT_SEEDLING, "Seedlings affected", "Enter the no. of seedlings", true, store);
        enroachmentSeedling.setHidden(true);
        wildLifeSeedling.setDecimal(true);
        evaluationSection.addEl(enroachmentSeedling);


        HPickerElement workDocCheck = new HPickerElement(Database
                .IS_WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL, "3.Whether all the particulars have been documented in the plantation journal?", "Select an option", true, "Yes|No|Partially", store);
        evaluationSection.addEl(workDocCheck);

        HPickerElement enteriesRegularityCheck = new HPickerElement(Database
                .WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL_DETAILS, "Whether RFO & " +
                "ACF have entered their observations?", "Select an option", true,
                "Yes|No", store);
        workDocCheck.addPosElement(enteriesRegularityCheck);
        evaluationSection.addEl(enteriesRegularityCheck);

        HPickerElement seniorOfficerCheckPlantation = new HPickerElement(Database
                .ANY_SNR_OFFICER_INSPECT_PLANTATION_AND_ENTRIES_IN_JOURNAL, "4.Did any  officer(s) of the rank of ACF  and above  inspect the Advance Work and made entries in the journal?", "Select an option", true, "Yes|No", store);
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

        HPickerElement cptPresent = new HPickerElement(Database.IS_CPT_PRESENT, "5.Is CPT present?", "Select Option", true, "Yes|No", store);
        evaluationSection.addEl(cptPresent);

        HPickerElement plantingOnCPT = new HPickerElement(Database.PLANTING_ON_CPT, "Is planting done on CPT?", "Select Option", true, "Yes|No", store);
        evaluationSection.addEl(plantingOnCPT);
        cptPresent.addPosElement(plantingOnCPT);

        HPickerElement workingPlanPrescription = new HPickerElement(Database.WORKING_PLAN_MANAGEMENT_PLAN_PRESCRIPTIONS, "6.Is the work approved in working Plan/Management Plan prescriptions?", "Select Option", true, "Yes|No", store);
        evaluationSection.addEl(workingPlanPrescription);

        HTextEntryElement workingCircleparagraphNo = new HTextEntryElement(Database.WORKING_CIRCLE_PARAGRAPH_NO, "Working Circle Paragraph Number", "Enter Working Circle paragraph number", true, store);
        evaluationSection.addEl(workingCircleparagraphNo);
        workingPlanPrescription.addPosElement(workingCircleparagraphNo);

        HTextEntryElement whyWorkWasTakenUp = new HTextEntryElement(Database.WHY_WORK_WAS_TAKEN_UP, "Why work was Taken up?", "Select Option", true, store);
        evaluationSection.addEl(whyWorkWasTakenUp);
        workingPlanPrescription.addNegElement(whyWorkWasTakenUp);


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

        HPickerElement natureOfTerrain = new HPickerElement(Database
                .PLANTATION_EVLTN_NATURE_OF_TERRAIN, "7.What is the nature of terrain?", "Select an option", true, "Flat|Gentle Slope|Steep Slope", store);
        evaluationSection.addEl(natureOfTerrain);


        HPickerElement plantationWorkUniformityCheck = new HPickerElement(Database
                .IS_RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE, "8.Are the results of Advance Work uniform across the site?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(plantationWorkUniformityCheck);

        HTextAreaEntryElement plantationWorkUniformityDetails = new HTextAreaEntryElement(Database.RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE_DETAILS, "What could be the reasons?", "Enter the details", true, store);
        plantationWorkUniformityCheck.addNegElement(plantationWorkUniformityDetails);
        evaluationSection.addEl(plantationWorkUniformityDetails);

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

        /*HPickerElement performanceImprovScopeCheck = new HPickerElement(Database
                .ANY_SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION, "9.Is there any scope for further improving the performance of the plantation?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(performanceImprovScopeCheck);

        HTextAreaEntryElement performanceImprovScopeHow = new HTextAreaEntryElement(Database.SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION_DETAILS, "Suggestions for improving performance", "Enter your suggestion here", true, store);
        performanceImprovScopeCheck.addPosElement(performanceImprovScopeHow);
        evaluationSection.addEl(performanceImprovScopeHow);*/


        HPickerElement qualityOfEarthWorkDone = new HPickerElement(Database.QUALITY_OF_EARTH_WORK_DONE, "9. What is the quality of earth work done?", "Select an option", true, "Excellent|Very good|Good|Average|Poor", store);
        evaluationSection.addEl(qualityOfEarthWorkDone);

        HPickerElement isBurningDoneOnSite = new HPickerElement(Database.ANY_BURNING_DONE_ON_THE_SITE, "10. Is any burning done on the site?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(isBurningDoneOnSite);

        HTextAreaEntryElement detailsOfDamageDone = new HTextAreaEntryElement(Database.ANY_BURNING_DONE_ON_THE_SITE_YES_DETAILS, "Details of damage done", "Enter details", true, store);
        evaluationSection.addEl(detailsOfDamageDone);
        isBurningDoneOnSite.addPosElement(detailsOfDamageDone);

        HPickerElement isDamageDoneToStandingTrees = new HPickerElement(Database.ANY_DAMAGE_DONE_TO_THE_STANDING_TREES, "11. Is any damage done to the standing trees?", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(isDamageDoneToStandingTrees);

        HTextAreaEntryElement detailsOfDamageDoneToStandingTrees = new HTextAreaEntryElement(Database.ANY_DAMAGE_DONE_TO_THE_STANDING_TREES_YES_DETAILS, "Details of damage done", "Enter details", true, store);
        evaluationSection.addEl(detailsOfDamageDoneToStandingTrees);
        isDamageDoneToStandingTrees.addPosElement(detailsOfDamageDoneToStandingTrees);

        HPickerElement isDamageDoneToShrubAndRoot = new HPickerElement(Database.ANY_DAMAGE_DONE_TO_THE_SHRUB_GROWTH_AND_ROOT_STOCK, "12. Is any damage done to the shrub growth and root stock? ", "Select an option", true, "Yes|No", store);
        evaluationSection.addEl(isDamageDoneToShrubAndRoot);

        HTextAreaEntryElement detailsOfDamageDoneToShrubAndRoot = new HTextAreaEntryElement(Database.ANY_DAMAGE_DONE_TO_THE_SHRUB_GROWTH_AND_ROOT_STOCK_YES_DETAILS, "Details of damage done", "Enter details", true, store);
        evaluationSection.addEl(detailsOfDamageDoneToShrubAndRoot);
        isDamageDoneToShrubAndRoot.addPosElement(detailsOfDamageDoneToShrubAndRoot);

   /*     HNumericElement workQualityGrading = new HNumericElement(Database.OVERALL_WORK_QUALITY_GRADING_ON_SCALE, "xvi. Overall work quality grading on 1-10 scale (1= worst and 10=Excellent)(Interactive coloured scale to Grade)", "Enter grade from 1-10", false, store);
        evaluationSection.addEl(workQualityGrading);
*/

        String plantationType = pref.getString(Database.PLANTATION_MODEL, "");


        final HButtonElement evaluationDetailsButton = new HButtonElement("Save ");
        evaluationDetailsButton.setElType(HElementType.SUBMIT_BUTTON);
        evaluationDetailsButton.setOnClick(v -> {
            evaluationDetailsButton.getButtonView().setFocusableInTouchMode(true);
            evaluationDetailsButton.getButtonView().requestFocus();
            evaluationDetailsButton.getButtonView().setFocusableInTouchMode(false);

            if (!checkFormData()) {
                showSaveFormDataAlert();
            } else {
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
                SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
                pref.edit().putString(Database.FORM_FILLED_STATUS, "1").apply();
                super.onBackPressed();
            }
        } else
            super.onBackPressed();

    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
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
