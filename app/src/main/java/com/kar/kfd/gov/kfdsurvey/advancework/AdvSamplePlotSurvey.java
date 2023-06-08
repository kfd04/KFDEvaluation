package com.kar.kfd.gov.kfdsurvey.advancework;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
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
import com.ngohung.form.el.HTextElement;
import com.ngohung.form.el.HTextEntryElement;
import com.ngohung.form.el.HTextView;
import com.ngohung.form.el.store.HPrefDataStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation.BASIC_INFORMATION;


public class AdvSamplePlotSurvey extends HBaseFormActivity {

    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    public static final String folderName = "AdvanceWork" + File.separator + Constants.FORMTYPE_SAMPLEPLOT;
    public static final String failedFolderName = "AdvanceWork" + File.separator + Constants.FAILED_SAMPLEPLOT;
    public static final String ADV_SAMPLE_PLOT_DETAILS = "AdvSamplePlotDetails";
    /* private HNumericElement totalNo;
     private HNumericElement totalNoSurvived;
     private HNumericElement percentageOfSeedling;*/
    int benId = 0, formFilledStatus = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private SweetAlertDialog dialog;
    private String formStatus = "0";
    private AdvSamplePlotSurvey mSurvey = this;
    private Database db;
    private int sampleplotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }




    @Override
    protected HRootElement createRootElement() {

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADV_SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences basicInfoPref = this.getApplicationContext().getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        db = new Database(this.getApplicationContext());

        ArrayList<HSection> sections = new ArrayList<>();

        formStatus = pref.getString("formStatus", "0");
        HSection samplePlot = new HSection("Sample Plot Inventory");

        HTextEntryElement samplePlotNo = new HTextEntryElement(Database.SAMPLE_PLOT_NUMBER, "Sample Plot No.", "Enter sample plot no", false, store);
        samplePlotNo.setNotEditable();
        if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")) == 0) {
            samplePlotNo.setValue(String.valueOf(db.getAdvNumberOfSamplePlots(Integer.parseInt(pref.getString(Database.FORM_ID, "0"))) + 1));
            //     pref.edit().putString(Database.SAMPLE_PLOT_ID,String.valueOf(db.getNumberOfSamplePlots(Integer.parseInt(pref.getString(Database.FORM_ID,"0"))) + 1)).apply();
            sampleplotId = db.getSamplePlotId() + 1;
        } else {
            samplePlotNo.setValue(String.valueOf(db.getAdvSamplePlotNumberForFormId(Integer.parseInt(pref.getString(Database.FORM_ID, "0")), Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")))));
            // sampleplotId =db.getSamplePlotNumberForFormId(Integer.parseInt(pref.getString(Database.FORM_ID,"0")),Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID,"0")));
            sampleplotId = Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0"));
        }
        samplePlot.addEl(samplePlotNo);

/*
        HPickerElement sampleplotSuitable = new HPickerElement(Database.SAMPLE_PLOT_SUITABLE, "Whether the Location is suitable for laying sample plot ?", "Select an Option", true, "Yes|No", store);
        samplePlot.addEl(sampleplotSuitable);

        HTextAreaEntryElement reasons = new HTextAreaEntryElement(Database.SAMPLE_PLOT_NOT_SUITABLE_REASONS, "Reasons", "Specify reasons", true, store);
        sampleplotSuitable.addNegElement(reasons);
        samplePlot.addEl(reasons);

        HButtonElement failedSamplePlotPhoto = new HButtonElement("View/Take photographs");
        failedSamplePlotPhoto.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", failedFolderName);
            bundle.putString("formId", String.valueOf(sampleplotId));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        sampleplotSuitable.addNegElement(failedSamplePlotPhoto);
        samplePlot.addEl(failedSamplePlotPhoto);

        HTextElement samplePlotHint = new HTextElement("Please lay sample plot on left or right side of the given Location");
        sampleplotSuitable.addNegElement(samplePlotHint);
        samplePlot.addEl(samplePlotHint);*/

     /*   HButtonElement map = new HButtonElement("View Map");
        samplePlot.addEl(map);
        map.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), MapGps.class);
            i.putExtra(Database.PREFERENCE, BASIC_INFORMATION);
//            Evaluationpref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
            startActivity(i);
        });*/

        HGpsElement gpsElement = new HGpsElement("Get GPS coordinates (at the centre of sample plot)", true);
        if (formStatus.equals("0")) {
            samplePlot.addEl(gpsElement);
        }


        HTextEntryElement latitudeEl = new HTextEntryElement(Database.SAMPLE_PLOT_LATITUDE, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);
        samplePlot.addEl(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.SAMPLE_PLOT_LONGITUDE, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);
        samplePlot.addEl(longitudeEl);

        HTextEntryElement altitudeEl = new HTextEntryElement(Database.SAMPLE_PLOT_ALTITUDE, "Altitude", "Click on the Gps button to get location", true, store);
        altitudeEl.setNotEditable();
        gpsElement.setAltitude(altitudeEl);
        samplePlot.addEl(altitudeEl);
        // ----just for saving the creati
        // n timestamp of location coordinates ----
        HTextEntryElement timestampEl = new HTextEntryElement(Database.GPS_COORDINATE_CREATION_TIMESTAMP, "", "", true, store);
        gpsElement.setCreationTimeStamp(timestampEl);


        //-----------------------------------------------------------------------
        HPickerElement cornersMarkingProcedure = new HPickerElement(Database
                .FOUR_CORNERS_MARKING_METHOD, "How are the 4 corners of the plot marked?",
                "Select an option", true, "Boulder stone with spray paint|Paint band on the " +
                "trees|Wooden Pegs|Flags|Any Other(specify)", store);
        samplePlot.addEl(cornersMarkingProcedure);

        HTextAreaEntryElement cornersMarkingProcedureOthers = new HTextAreaEntryElement(Database.FOUR_CORNERS_MARKING_METHOD_OTHER, "Specify", "Enter the details", true, store);
        samplePlot.addEl(cornersMarkingProcedureOthers);
        cornersMarkingProcedure.addElementForValue(cornersMarkingProcedureOthers, 4);

        HTextView photoButtonLabel = new HTextView("Take Photo at the centre of the sample plot along with evaluator");
        samplePlot.addEl(photoButtonLabel);


        HButtonElement viewPhoto = new HButtonElement("View/Take photographs");
        viewPhoto.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName);
            bundle.putString("formId", String.valueOf(sampleplotId));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        samplePlot.addEl(viewPhoto);

        HTextElement seedlingLabel = new HTextElement("Part 1: Inventory of EarthWork ");
        samplePlot.addEl(seedlingLabel);


        final HMultiPickerElement earthWorkType = new HMultiPickerElement(Database.TYPE_OF_EARTH_WORK_DONE,
                "Type of earth work done", "Select an option", true, "Pit|Trench & mound|Pit in Pit|Ripping|Others"
                , store);
        samplePlot.addEl(earthWorkType);
        HPickerElement pitSize = new HPickerElement(Database.PITSIZE, "pitsize", "Select pit size", true, "0.30m X 0.30m X 0.30m|0.45m X 0.45m X 0.45m|0.60m X 0.60m X 0.60m|0.75m X 0.75m X 0.75m|1m X 1m X 1m", store);
        pitSize.setHidden(true);
        samplePlot.addEl(pitSize);

        HPickerElement pitEspacement = new HPickerElement(Database.PIT_ESPACEMENT, "Pit espacement", "Select pit espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        pitEspacement.setHidden(true);
        samplePlot.addEl(pitEspacement);

        HNumericElement noOfpits = new HNumericElement(Database.NO_OF_PITS, "Number of pits recorded in MB/FNB per 0.1 hectare", "Enter number", true, store);
        samplePlot.addEl(noOfpits);
        noOfpits.setHidden(true);


        HNumericElement pitsCounted = new HNumericElement(Database.PITS_COUNTED, "Number actually counted", "Enter number", true, store);
        samplePlot.addEl(pitsCounted);
        pitsCounted.setHidden(true);

        HPickerElement trenchSize = new HPickerElement(Database.TRENCH_SIZE, "Trench size", "Enter trench size", true, "4m X 0.5m X 0.5m", store);
        trenchSize.setHidden(true);
        samplePlot.addEl(trenchSize);

        HPickerElement trenchespacement = new HPickerElement(Database.TRENCH_ESPACEMENT, "Trench espacement", "Enter trench espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        trenchespacement.setHidden(true);
        samplePlot.addEl(trenchespacement);

        HNumericElement noOfTrenches = new HNumericElement(Database.NO_OF_TRENCHS, "Number of trenches recorded in MB/FNB per 0.1 hectare", "Enter number", true, store);
        samplePlot.addEl(noOfTrenches);
        noOfpits.setHidden(true);


        HNumericElement trenchesCounted = new HNumericElement(Database.TRENCHS_COUNTED, "Number actually counted", "Enter number", true, store);
        samplePlot.addEl(trenchesCounted);
        pitsCounted.setHidden(true);

        HTextEntryElement pitInPitSize = new HTextEntryElement(Database.PIT_IN_PIT_SIZE, "Pit in pit size", "Enter Pit in pit size in meters", true, store);
        pitInPitSize.setHidden(true);
        samplePlot.addEl(pitInPitSize);

        HPickerElement ppEspacement = new HPickerElement(Database.PIT_IN_PIT_ESPACEMENT, "Pit in pit size espacement", "Enter Pit in pit size espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        ppEspacement.setHidden(true);
        samplePlot.addEl(ppEspacement);


        HNumericElement noOfPitInPit = new HNumericElement(Database.NO_OF_PIT_IN_PIT, "Number of PitInPit recorded in MB/FNB per 0.1 hectare", "Enter number", true, store);
        samplePlot.addEl(noOfPitInPit);
        noOfpits.setHidden(true);


        HNumericElement pitinpitCounted = new HNumericElement(Database.PIT_IN_PIT_COUNTED, "Number actually counted", "Enter number", true, store);
        samplePlot.addEl(pitinpitCounted);
        pitsCounted.setHidden(true);

        HNumericElement rippingSize = new HNumericElement(Database.RIPPING_SIZE, "Ripline size", "Enter Ripline size", true, store);
        rippingSize.setHidden(true);
        samplePlot.addEl(rippingSize);

        HPickerElement rippingEspacement = new HPickerElement(Database.RIPPING_ESPACEMENT, "Ripping espacement", "Enter Running mtr", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        rippingEspacement.setHidden(true);
        samplePlot.addEl(rippingEspacement);

        HNumericElement noOfRipline = new HNumericElement(Database.NO_OF_RIPLINE, "Number of Ripline recorded in MB/FNB per 0.1 hectare", "Enter number", true, store);
        samplePlot.addEl(noOfRipline);
        noOfpits.setHidden(true);


        HNumericElement riplineCounted = new HNumericElement(Database.RIPLINE_COUNTED, "Number actually counted", "Enter number", true, store);
        samplePlot.addEl(riplineCounted);
        pitsCounted.setHidden(true);

        HTextEntryElement othersSize = new HTextEntryElement(Database.OTHERS_SIZE, "Others size", "Enter Others size", true, store);
        othersSize.setHidden(true);
        samplePlot.addEl(othersSize);

        HPickerElement othersespacement = new HPickerElement(Database.OTHERS_ESPACEMENT, "Others espacement", "Enter Others espacement", true, "1.5m X 1.5m|2.5m X 2.5m|3m X 3m|4m X 5m|5m X 4m|5m X 5m|6m X 6m|7m X 7m|10m X 10m", store);
        othersespacement.setHidden(true);
        samplePlot.addEl(othersespacement);

        HNumericElement noOfOthers = new HNumericElement(Database.OTHERS_NO_OF_UNITS, "Number of Others recorded in MB/FNB per 0.1 hectare", "Enter number", true, store);
        samplePlot.addEl(noOfOthers);
        noOfpits.setHidden(true);


        HNumericElement othersCounted = new HNumericElement(Database.OTHER_COUNTED, "Number actually counted", "Enter number", true, store);
        samplePlot.addEl(othersCounted);
        pitsCounted.setHidden(true);

        earthWorkType.setListener((pos, options, values) -> {

            for (int i = 0; i < options.length; i++) {

                String option = options[i];
                boolean isChecked = !values[i];

                switch (option) {

                    case "Pit":
                        pitSize.setHidden(isChecked);
                        pitEspacement.setHidden(isChecked);
                        noOfpits.setHidden(isChecked);
                        pitsCounted.setHidden(isChecked);
                        break;

                    case "Trench & mound":
                        trenchSize.setHidden(isChecked);
                        trenchespacement.setHidden(isChecked);
                        noOfTrenches.setHidden(isChecked);
                        trenchesCounted.setHidden(isChecked);
                        break;

                    case "Pit in Pit":
                        pitInPitSize.setHidden(isChecked);
                        ppEspacement.setHidden(isChecked);
                        noOfPitInPit.setHidden(isChecked);
                        pitinpitCounted.setHidden(isChecked);
                        break;
                    case "Ripping":
                        rippingSize.setHidden(isChecked);
                        rippingEspacement.setHidden(isChecked);
                        noOfRipline.setHidden(isChecked);
                        riplineCounted.setHidden(isChecked);
                        break;
                    case "Others":
                        othersSize.setHidden(isChecked);
                        othersespacement.setHidden(isChecked);
                        noOfOthers.setHidden(isChecked);
                        othersCounted.setHidden(isChecked);
                        break;


                   /* default:
                        itemDetails.setHidden(true);
                        seedlingDetails.setHidden(true);
                        trenchDetails.setHidden(true);
                        break;*/

                }
            }
        });

       /* HTextEntryElement diffIfAny = new HTextEntryElement(Database.GTDSB_OF_ADVWORK_DIFFERENCE_IF_ANY,"Difference if any","Enter difference",true,store );
        samplePlot.addEl(diffIfAny);
*/
        HTextAreaEntryElement remarks = new HTextAreaEntryElement(Database.ADV_SAMPLEPLOT_REMARKS, "Remarks", "Enter Remarks", true, store);
        samplePlot.addEl(remarks);


        HTextElement regenerationPlot = new HTextElement("Part 2:Inventory of Natural Regeneration");
        samplePlot.addEl(regenerationPlot);

        HTextView samplePlotInstruction = new HTextView("Please lay 3m*3m regeneration plot on south west corner of the sampleplot ");
        samplePlot.addEl(samplePlotInstruction);


        HPickerElement regenerationAvailable = new HPickerElement(Database.REGENERATION_AVAILABLE, "Whether natural regeneration is present ?", "Select an Option", true, "Yes|No", store);
        samplePlot.addEl(regenerationAvailable);

        HPickerElement rootstockAvailable = new HPickerElement(Database.ROOTSTOCK_AVAILABLE, "Whether any root stock(collar diameter between 2-10 cm) present?", "Select an Option", true, "Yes|No", store);
        regenerationAvailable.addPosElement(rootstockAvailable);
        samplePlot.addEl(rootstockAvailable);


        HSection regnerationSection = new HSection("Regeneration Details");


        HMultiPickerElement species = new HMultiPickerElement(Database.SPECIES_NAME, "1.Species", "Select species names", true, OTHERS_IF_ANY_SPECIFY + "|" + db.getNamesOfSpeciesNew(basicInfoPref.getString("model_id", "1")), store);
        regnerationSection.addEl(species);

        HTextEntryElement speciesOther = new HTextEntryElement(SPECIES_OTHER, "Name of species ( Other )", "Enter species name", true, store);
        regnerationSection.addEl(speciesOther);
        species.addPosElement(speciesOther);

        HNumericElement totalnoOfSpecies = new HNumericElement(Database.STEMS_WITH_COLLAR_2_10CM,
                "2.Total number of Stems with collar diameter between 2 -10cm", "Enter numbers ", true, store);
        regnerationSection.addEl(totalnoOfSpecies);


        HNumericElement averageGirth = new HNumericElement(Database.AVERAGE_COLLAR_GIRTH,
                "3.Average collar girth(cm) ", "Enter average collar girth ( in < 10cm)", true, store);
        regnerationSection.addEl(averageGirth);

        HNumericElement averageGbh = new HNumericElement(Database.AVERAGE_HEIGHT_METERS, "4.Average " +
                "Height(m)", "Enter average height in meters", true, store);
        regnerationSection.addEl(averageGbh);

   /*     HPickerElement replantPermCheck = new HPickerElement(Database.NTFP_SPECIES, "5.Is it a NTFP Species", "Select an option", true, "Yes|No", store);
        regnerationSection.addEl(replantPermCheck);

        HTextEntryElement ntfpSpeciesPart = new HTextEntryElement(Database.WHICH_PART_USED_AS_NTFP, "a)Which part of the plant is used as NTFP", "specify", true, store);
        replantPermCheck.addPosElement(ntfpSpeciesPart);
        regnerationSection.addEl(ntfpSpeciesPart);

        HNumericElement approxWeight = new HNumericElement(Database.APPROX_WEIGHT, "b)Approx.Weight in grams", "specify", true, store);
        replantPermCheck.addPosElement(approxWeight);
        regnerationSection.addEl(approxWeight);*/

        HTextAreaEntryElement remarks1 = new HTextAreaEntryElement(Database.SHRUB_VEGETATION_REMARKS, "5.Remarks", "about the conditionof shrub vegetation", true, store);
        regnerationSection.addEl(remarks1);

        HSection inventorySection = new HSection("Inventory of Natural Trees");
        regenerationAvailable.addPosSection(inventorySection);

      /*
        HTextElement inventory = new HTextElement("Inventory of Natural Trees");
        regenerationAvailable.addPosElement(inventory);*/


        HPickerElement speciesPresent = new HPickerElement(Database.TREE_SPECIES_PRESENT, "Whether any Natural tree species  present ?", "Select an Option", true, "Yes|No", store);
        regenerationAvailable.addPosElement(speciesPresent);
        inventorySection.addEl(speciesPresent);

        HMultiPickerElement speciesInventory = new HMultiPickerElement(Database.SPECIES_NAME_INVENTORY, "1.Species", "Select species names", true, OTHERS_IF_ANY_SPECIFY + "|" + db.getNamesOfSpeciesNew(basicInfoPref.getString("model_id", "1")), store);
        inventorySection.addEl(speciesInventory);
        speciesPresent.addPosElement(speciesInventory);

        HTextEntryElement speciesOtherInventory = new HTextEntryElement(Database.SPECIES_OTHER_INVENTORY, "Name of species ( Other )", "Enter species name", true, store);
        inventorySection.addEl(speciesOtherInventory);
        speciesInventory.addPosElement(speciesOtherInventory);


        HNumericElement totalnoSpecies = new HNumericElement(Database.STEMS_WITH_COLLAR_ABOVE_10CM,
                "2.Total number of Stems with GBH above 10cm", "Enter numbers ", true, store);
        inventorySection.addEl(totalnoSpecies);
        speciesPresent.addPosElement(totalnoSpecies);

        HNumericElement dbhSpecies = new HNumericElement(Database.GBH_SPECIES_ABOVE_10CM, "3.Average GBH ( above 10 cm)", "Enter avearage GBH in centimeters", true, store);
        inventorySection.addEl(dbhSpecies);
        speciesPresent.addPosElement(dbhSpecies);

        HNumericElement averageHeight = new HNumericElement(Database.AVERAGE_HEIGHT_METERS_INVENTORY, "4.Average height ( in meter )", "Enter average height ( in meter )", true, store);
        inventorySection.addEl(averageHeight);
        speciesPresent.addPosElement(averageHeight);


        //----------------------done by sunil-----------------------
       /* HTextElement casualtyReplacementLabel = new HTextElement("Part 3: Casualty Repalcement");
        samplePlot.addEl(casualtyReplacementLabel);

        HButtonElement casualtyRepalcement = new HButtonElement("Add/View Seed Casualty Repalcement");
        samplePlot.addEl(casualtyRepalcement);
        casualtyRepalcement.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
                i.putExtra("id",Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID,"0")));
                i.putExtra("List-type", Constants.PLOT_INVENTORY_LIST);
                i.putExtra("Inventory-type", CASUALTY_REPLACEMENT);
                i.putExtra("formStatus", formStatus);
                startActivity(i);
            }
        });*/
        //----------------------------------------------------------

       /* HNumericElement noOfEmptyTrenches =  new HNumericElement(Database.NUMBER_OF_EMPTY_PITS_TRENCHES_FOUND,"No of empty trenches/pits/thallies/rip lines etc. found in the sample plot","Enter number",true,store);
        samplePlot.addEl(noOfEmptyTrenches);
        noOfEmptyTrenches.setDecimal(false);*/

        final HButtonElement submit = new HButtonElement("Save");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            /*if (!checkFormData())
                displayFormErrorMsg("Validation Error", "Please fill all the fields");
            else if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_SEEDLING_STATUS, "0")) == 0)
                displayFormErrorMsg("Validation Error", "Please Complete Seedlings Planted");
            else if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_SEED_DIBBLING_STATUS, "0")) == 0)
                displayFormErrorMsg("Validation Error", "Please Complete Dibbled Seedling Invetory");
            else*/
            if (!checkFormData())
                showSaveFormDataAlert();
            else {
                formFilledStatus = 1;
                submitSamplePlotDetails();
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
            samplePlot.addEl(back);
            samplePlot.setNotEditable();
        } else {
            rootstockAvailable.addPosSection(regnerationSection);
            regenerationAvailable.addPosSection(inventorySection);
            inventorySection.addEl(submit);
        }
        sections.add(samplePlot);
        sections.add(regnerationSection);
        sections.add(inventorySection);
//        sections.add(groundTruthingDetails);

        return new HRootElement("Sample Plot Form", sections);
    }

    private void submitSamplePlotDetails() {
        SurveyCreation surveyCreation = new SurveyCreation();
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADV_SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
        Database db = new Database(this.getApplicationContext());
        Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_ADV_SAMPLE_PLOT_MASTER, db);
        ContentValues cv = insertValuesToSamplePlot(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
        long samplePlotId;
        cv.put(Database.FORM_ID, pref.getString(Database.FORM_ID, "0"));

        if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")) == 0) {
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            samplePlotId = db.insertIntoAdvSamplePlot(cv);
            File mediaStorageDir = surveyCreation.getPictureFolder(folderName);
            if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                mediaStorageDir.renameTo(surveyCreation.getNewPictureFolder(samplePlotId, folderName));
            }
        } else {
            samplePlotId = Long.parseLong(pref.getString(Database.SAMPLE_PLOT_ID, "0"));
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            cv.put(Database.SAMPLE_PLOT_ID, samplePlotId);
            cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
            db.updateTableWithId(Database.TABLE_ADV_SAMPLE_PLOT_MASTER, Database.SAMPLE_PLOT_ID, cv);
        }
//        Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(Database.TABLE_SAMPLE_PLOT_INVENTORY, Database.SAMPLE_PLOT_ID, samplePlotId))));
        pref.edit().clear().apply();
        setClearPref(true);
        showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");

    }


    private ContentValues insertValuesToSamplePlot(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
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
        long creationTimeStamp = System.currentTimeMillis() / 1000;
        cv.put(Database.SAMPLE_PLOT_INVENTORY_DONE_ON, creationTimeStamp);
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
    public void onBackPressed() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADV_SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (pref.getString("formStatus", "0").equals("0")) {
                Log.d("FormStatus", pref.getString("formStatus", "0"));
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if (!pref.getString("formStatus", "0").equals("0")) {
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }
            if ((Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")) == 0)) {
                //super.onBackPressed();//commented this for disabling back button
            }
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }


    }

    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, Are you sure want to Exit?");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitSamplePlotDetails());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
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

 /*   public void calculatePercentage() {

        float planted = 0, plantedSurviving = 0;

        try {
            if (!totalNo.getValue().equals(""))
                planted = Float.parseFloat(totalNo.getValue());
        } catch (NumberFormatException e) {
            planted = 0;
        }
        try {
            if (!totalNoSurvived.getValue().equals(""))
                plantedSurviving = Float.parseFloat(totalNoSurvived.getValue());
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

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ADV_SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
        benId = Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0"));
       /* totalNo.setValue(String.valueOf(db.gettotalNoSeedling(benId, SEEDLING)));
        totalNoSurvived.setValue(String.valueOf(db.gettotalNoSurving(benId, SEEDLING)));
        calculatePercentage();*/
    }
}
