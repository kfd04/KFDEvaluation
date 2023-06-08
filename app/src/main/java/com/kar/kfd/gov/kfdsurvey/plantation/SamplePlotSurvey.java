package com.kar.kfd.gov.kfdsurvey.plantation;

import static com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation.BASIC_INFORMATION;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyCreation;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.map.MapGps;
import com.kar.kfd.gov.kfdsurvey.map.MapGps_Individual;
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
import java.util.List;
import java.util.Map;


public class SamplePlotSurvey extends HBaseFormActivity {

    public static final String SPECIES_OTHER = "species_other";
    public static final String OTHERS_IF_ANY_SPECIFY = " ( Others if any (specify)  )";
    public static final String folderName = "Plantation" + File.separator + Constants.FORMTYPE_SAMPLEPLOT;
    public static final String failedFolderName = "Plantation" + File.separator + Constants.FAILED_SAMPLEPLOT;
    public static final String TREE_LIST = "Natural tree found on sample plot";
    public static final String ROOTSTOCK = "Natural root stock found and tended on sample plot";
    public static final String SEEDLING = "Planted seedling surviving as on date of sampling";
    public static final String SEED_DIBBLING = "Results of seed dibbling";
    public static final String CASUALTY_REPLACEMENT = "Casualty Repalcement";
    public static final String SAMPLE_PLOT_DETAILS = "SamplePlotDetails";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private SweetAlertDialog dialog;
    private String formStatus = "0";
    private SamplePlotSurvey mSurvey = this;
    private Database db;
    private int sampleplotId;
    private int formFilledStatus = 0;

    private HNumericElement totalNo;
    private HNumericElement totalNoSurvived;
    private HNumericElement percentageOfSeedling;
    int benId = 0;
    private HNumericElement noOfEmptyPits;
    private String mData;
    private List<LatLng> lat_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }


    @Override
    protected HRootElement createRootElement() {

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences basicInfoPref = this.getApplicationContext().getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        HPrefDataStore store_ = new HPrefDataStore(basicInfoPref);
        db = new Database(this.getApplicationContext());
        ArrayList<HSection> sections = new ArrayList<>();
        formStatus = pref.getString("formStatus", "0");
        HSection samplePlot = new HSection("Sample Plot Inventory");

        HTextEntryElement samplePlotNo = new HTextEntryElement(Database.SAMPLE_PLOT_NUMBER, "Sample Plot No.", "Enter sample plot no", false, store);
        samplePlotNo.setNotEditable();
        if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")) == 0) {
            samplePlotNo.setValue(String.valueOf(db.getNumberOfSamplePlots(Integer.parseInt(pref.getString(Database.FORM_ID, "0"))) + 1));
            //     pref.edit().putString(Database.SAMPLE_PLOT_ID,String.valueOf(db.getNumberOfSamplePlots(Integer.parseInt(pref.getString(Database.FORM_ID,"0"))) + 1)).apply();
            sampleplotId = db.getSamplePlotId() + 1;
        } else {
            samplePlotNo.setValue(String.valueOf(db.getSamplePlotNumberForFormId(Integer.parseInt(pref.getString(Database.FORM_ID, "0")), Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")))));
            // sampleplotId =db.getSamplePlotNumberForFormId(Integer.parseInt(pref.getString(Database.FORM_ID,"0")),Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID,"0")));
            sampleplotId = Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0"));
        }
        samplePlot.addEl(samplePlotNo);


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
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        sampleplotSuitable.addNegElement(failedSamplePlotPhoto);
        samplePlot.addEl(failedSamplePlotPhoto);

        HTextElement samplePlotHint = new HTextElement("Please lay sample plot on left or right side of the given Location");
        sampleplotSuitable.addNegElement(samplePlotHint);
        samplePlot.addEl(samplePlotHint);

        HButtonElement map = new HButtonElement("View Map");
        samplePlot.addEl(map);
        samplePlot.setVisible(false);
        String block_no = pref.getString(Database.BLOCK_NUMBER_NEW,"");
        Log.e("dacsd",""+block_no);
        map.setOnClick(v -> {
            if(block_no.equalsIgnoreCase("all")) {
                Intent intent = new Intent(mSurvey.getApplicationContext(), MapGps_Individual.class);
                intent.putExtra(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
                intent.putExtra("block_area", "");
                intent.putExtra("block_no", pref.getString(Database.BLOCK_NUMBER_NEW, ""));
                basicInfoPref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
                basicInfoPref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING).apply();
                basicInfoPref.edit().putString(Database.FOLDER_NAME, folderName).apply();
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(mSurvey.getApplicationContext(), MapGps.class);
                intent.putExtra(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
                intent.putExtra("block_area", "");
                intent.putExtra("block_no", pref.getString(Database.BLOCK_NUMBER_NEW, ""));
                basicInfoPref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
                basicInfoPref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING).apply();
                basicInfoPref.edit().putString(Database.FOLDER_NAME, folderName).apply();
                startActivity(intent);
            }
//            Intent intent = new Intent(mSurvey.getApplicationContext(), MapGps.class);
//            intent.putExtra(Database.PREFERENCE, BASIC_INFORMATION);
////            basicInfoPref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
//            startActivity(intent);
        });

        HGpsElement gpsElement = new HGpsElement("Get GPS coordinates (at the centre of sample plot)", true);
        sampleplotSuitable.addPosElement(gpsElement);
        sampleplotSuitable.addNegElement(gpsElement);
        if (formStatus.equals("0")) {
            samplePlot.addEl(gpsElement);
        }


        HTextEntryElement latitudeEl = new HTextEntryElement(Database.SAMPLE_PLOT_LATITUDE, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);
        sampleplotSuitable.addPosElement(latitudeEl);
        sampleplotSuitable.addNegElement(latitudeEl);
        samplePlot.addEl(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.SAMPLE_PLOT_LONGITUDE, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);
        sampleplotSuitable.addPosElement(longitudeEl);
        sampleplotSuitable.addNegElement(longitudeEl);
        samplePlot.addEl(longitudeEl);

        HTextEntryElement altitudeEl = new HTextEntryElement(Database.SAMPLE_PLOT_ALTITUDE, "Altitude", "Click on the Gps button to get location", true, store);
        altitudeEl.setNotEditable();
        gpsElement.setAltitude(altitudeEl);
        sampleplotSuitable.addPosElement(altitudeEl);
        sampleplotSuitable.addNegElement(altitudeEl);
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
        sampleplotSuitable.addPosElement(cornersMarkingProcedure);
        sampleplotSuitable.addNegElement(cornersMarkingProcedure);
        samplePlot.addEl(cornersMarkingProcedure);

        HTextAreaEntryElement cornersMarkingProcedureOthers = new HTextAreaEntryElement(Database.FOUR_CORNERS_MARKING_METHOD_OTHER, "Specify", "Enter the details", true, store);
        samplePlot.addEl(cornersMarkingProcedureOthers);
        sampleplotSuitable.addPosElement(cornersMarkingProcedure);
        cornersMarkingProcedure.addElementForValue(cornersMarkingProcedureOthers, 4);

        HTextView photoButtonLabel = new HTextView("Take Photo at the centre of the sample plot along with evaluator");
        sampleplotSuitable.addPosElement(photoButtonLabel);
        sampleplotSuitable.addNegElement(photoButtonLabel);
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
        sampleplotSuitable.addPosElement(viewPhoto);
        sampleplotSuitable.addNegElement(viewPhoto);
        samplePlot.addEl(viewPhoto);

       /* HTextEntryElement inventoryDoneBy = new HTextEntryElement(Database.SAMPLE_PLOT_INVENTORY_DONE_BY,"Inventory done by :","Enter the name",true,store);
        samplePlot.addEl(inventoryDoneBy);
        inventoryDoneBy.setEditable(false);
        inventoryDoneBy.setValue( this.getApplicationContext().getSharedPreferences("SA", Context.MODE_PRIVATE).getString(Database.EVALUATOR_NAME,null));*/
      /*  HTextEntryElement inventoryDoneBy = new HTextEntryElement(Database.SAMPLE_PLOT_INVENTORY_DONE_BY,"Inventory done by :","Enter the name",true,store);
        samplePlot.addEl(inventoryDoneBy);

        HTextElement infoTextInventory = new HTextElement("Below Question is Related to : Inventory of planted seedlings surviving as on the date of sampling");
        samplePlot.addEl(infoTextInventory);

        HTextElement treeLabel = new HTextElement("Part 1. Inventory of natural trees found on the sample plot");
        samplePlot.addEl(treeLabel);

        HButtonElement treeListInventory = new HButtonElement("Add/View Tree Inventory");
        samplePlot.addEl(treeListInventory);
        treeListInventory.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
                i.putExtra("id",Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID,"0")));
                i.putExtra("List-type", Constants.PLOT_INVENTORY_LIST);
                i.putExtra("Inventory-type", TREE_LIST);
                i.putExtra("formStatus", formStatus);
                startActivity(i);
            }
        });

        HTextElement rootLabel = new HTextElement("Part 2: Inventory of natural root stock found in the sample plot and tended");
        samplePlot.addEl(rootLabel);

        HButtonElement rootStockInventory = new HButtonElement("Add/View Root Stock Inventory");
        samplePlot.addEl(rootStockInventory);
        rootStockInventory.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
                i.putExtra("id",Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID,"0")));
                i.putExtra("List-type", Constants.PLOT_INVENTORY_LIST);
                i.putExtra("Inventory-type", ROOTSTOCK);
                i.putExtra("formStatus", formStatus);
                startActivity(i);
            }
        });*/
        HTextElement seedlingLabel = new HTextElement("Part 1: Inventory of Planted Seedlings ");
        sampleplotSuitable.addPosElement(seedlingLabel);
        sampleplotSuitable.addNegElement(seedlingLabel);
        samplePlot.addEl(seedlingLabel);

        HButtonElement seedlingInventory = new HButtonElement("Add/View Seedlings Planted");
        sampleplotSuitable.addPosElement(seedlingInventory);
        sampleplotSuitable.addNegElement(seedlingInventory);
        samplePlot.addEl(seedlingInventory);
        seedlingInventory.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
            i.putExtra("id", Integer.parseInt(pref.getString(Database.FORM_ID, "0")));
            i.putExtra("sampleplotId", Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")));
            i.putExtra("List-type", Constants.PLOT_INVENTORY_LIST);
            i.putExtra("Inventory-type", SEEDLING);
            i.putExtra(Database.PART_TYPE, Database.SAMPLE_PLOT_SEEDLING);
            i.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.SAMPLE_PLOT_SEEDLING_STATUS, "1").apply();
            startActivity(i);
        });

        totalNoSurvived = new HNumericElement(Database.TOTAL_COUNT_SURVIVED, "Total Number of seedling survived", "survived", true, store);
        sampleplotSuitable.addPosElement(totalNoSurvived);
        sampleplotSuitable.addNegElement(totalNoSurvived);
        totalNoSurvived.setNotEditable();
        samplePlot.addEl(totalNoSurvived);

//        String value_pits = pref.getString(Database.NO_OF_EMPTY_PITS, "");
//        Log.e("ADCSADC", "" + value_pits);
//        if (value_pits.equalsIgnoreCase("0"))
//            noOfEmptyPits = new HNumericElement("", "Total number of empty pits in sample plot", "Enter no of empty pits", false, store);
//        else
        noOfEmptyPits = new HNumericElement(Database.NO_OF_EMPTY_PITS, "Total number of empty pits in sample plot", "Enter no of empty pits", false, store);
        Log.e("sdvsdv", "" + noOfEmptyPits.getValue());
        sampleplotSuitable.addPosElement(noOfEmptyPits);
        sampleplotSuitable.addNegElement(noOfEmptyPits);
        samplePlot.addEl(noOfEmptyPits);

        HButtonElement survival_percentage_btn = new HButtonElement("Calculate Survival %");
        survival_percentage_btn.doValidation();
        samplePlot.addEl(survival_percentage_btn);
        sampleplotSuitable.addPosElement(survival_percentage_btn);
        sampleplotSuitable.addNegElement(survival_percentage_btn);
        HTextElement percentage = new HTextElement("Percentage");

        sampleplotSuitable.addPosElement(percentage);
        sampleplotSuitable.addNegElement(percentage);
        samplePlot.addEl(percentage);
        survival_percentage_btn.setOnClick(view -> {
            if (totalNoSurvived.getEditText().getText().toString().equalsIgnoreCase("0")) {
                Toast.makeText(SamplePlotSurvey.this, "Please fill seedling Planted details",
                        Toast.LENGTH_SHORT).show();
            } else if (noOfEmptyPits.getEditText().getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(SamplePlotSurvey.this, "Please enter total number of empty pits",
                        Toast.LENGTH_SHORT).show();
            } else {
                double total_survived = Double.parseDouble(totalNoSurvived.getEditText().getText().toString()) +
                        Double.parseDouble(noOfEmptyPits.getEditText().getText().toString());
                store.getPref().getString(Database.NO_OF_EMPTY_PITS,noOfEmptyPits.getEditText().getText().toString());
                double survival_percentage = (Double.parseDouble(totalNoSurvived.getEditText().getText().toString()) / total_survived) * 100;

                percentage.setLabel("Survival percent : " + survival_percentage + " %");
            }
        });

        HTextElement iFailedSeedling = new HTextElement("Information of Failed Seedlings");
        sampleplotSuitable.addPosElement(iFailedSeedling);
        sampleplotSuitable.addNegElement(iFailedSeedling);
        samplePlot.addEl(iFailedSeedling);

        HButtonElement emptyPits = new HButtonElement("Empty Pits");
        sampleplotSuitable.addPosElement(emptyPits);
        sampleplotSuitable.addNegElement(emptyPits);
        samplePlot.addEl(emptyPits);
        emptyPits.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), EmptyPitsActivity.class);
            i.putExtra("id", Integer.parseInt(pref.getString(Database.FORM_ID, "0")));
            i.putExtra("sampleplotId", Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")));
            i.putExtra("empty_pits", noOfEmptyPits.getEditText().getText().toString());
            i.putExtra("List-type", Constants.PLOT_INVENTORY_LIST);
            i.putExtra("Inventory-type", SEEDLING);
            i.putExtra(Database.PART_TYPE, Database.SAMPLE_PLOT_SEEDLING);
            i.putExtra("formStatus", formStatus);

            if (totalNoSurvived.getEditText().getText().toString().equalsIgnoreCase("0"))
                Toast.makeText(mSurvey, "Complete Seedlings planted to get total survived", Toast.LENGTH_SHORT).show();
            else if (noOfEmptyPits.getEditText().getText().toString().isEmpty() ||
                    noOfEmptyPits.getEditText().getText().toString().equals("0")) {
                Toast.makeText(mSurvey, "Enter Total Number of Empty pits Found", Toast.LENGTH_SHORT).show();
            } else
                startActivity(i);
        });

      /*  HTextElement PlantedSeedling = new HTextElement("Survival Percentage of Planted Seedlings");
        sampleplotSuitable.addPosElement(PlantedSeedling);
        sampleplotSuitable.addNegElement(PlantedSeedling);
        samplePlot.addEl(PlantedSeedling);



        HButtonElement calculatePercentage = new HButtonElement("Calculate Percentage");
        sampleplotSuitable.addPosElement(calculatePercentage);
        sampleplotSuitable.addNegElement(calculatePercentage);
        samplePlot.addEl(calculatePercentage);*/


        HTextElement seedDibblingLabel = new HTextElement("Part 2: Results of seed dibbling (if any)");
        sampleplotSuitable.addPosElement(seedDibblingLabel);
        sampleplotSuitable.addNegElement(seedDibblingLabel);
        samplePlot.addEl(seedDibblingLabel);

        HButtonElement seedDibbling = new HButtonElement("View Dibbled seedling Inventory");
        sampleplotSuitable.addPosElement(seedDibbling);
        sampleplotSuitable.addNegElement(seedDibbling);
        samplePlot.addEl(seedDibbling);
        seedDibbling.setOnClick(v -> {
            Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
            i.putExtra("id", Integer.parseInt(pref.getString(Database.FORM_ID, "0")));
            i.putExtra("sampleplotId", Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")));
            i.putExtra("List-type", Constants.PLOT_INVENTORY_LIST);
            i.putExtra("Inventory-type", SEED_DIBBLING);
            i.putExtra(Database.PART_TYPE, Database.SAMPLE_PLOT_SEED_DIBBLING);
            i.putExtra("formStatus", formStatus);
            pref.edit().putString(Database.SAMPLE_PLOT_SEED_DIBBLING_STATUS, "1").apply();
            startActivity(i);
        });

        HTextElement regenerationPlot = new HTextElement("Part 3:Inventory of Natural Regeneration");
        sampleplotSuitable.addPosElement(regenerationPlot);
        sampleplotSuitable.addNegElement(regenerationPlot);
        samplePlot.addEl(regenerationPlot);

        HTextView samplePlotInstruction = new HTextView("Please lay 3m*3m regeneration plot on south west corner of the sampleplot ");
        sampleplotSuitable.addPosElement(samplePlotInstruction);
        sampleplotSuitable.addNegElement(samplePlotInstruction);
        samplePlot.addEl(samplePlotInstruction);


        HPickerElement regenerationAvailable = new HPickerElement(Database.REGENERATION_AVAILABLE, "Whether natural regeneration is present ?", "Select an Option", true, "Yes|No", store);
        sampleplotSuitable.addPosElement(regenerationAvailable);
        sampleplotSuitable.addNegElement(regenerationAvailable);
        samplePlot.addEl(regenerationAvailable);

        HPickerElement rootstockAvailable = new HPickerElement(Database.ROOTSTOCK_AVAILABLE, "Whether any root stock(collar diameter between 2-10 cm) present?", "Select an Option", true, "Yes|No", store);
        regenerationAvailable.addPosElement(rootstockAvailable);
        samplePlot.addEl(rootstockAvailable);


        HSection regnerationSection = new HSection("Regeneration Plot");


        HMultiPickerElement species = new HMultiPickerElement(Database.SPECIES_NAME, "1.Species", "Select species names", true, OTHERS_IF_ANY_SPECIFY + "|" + db.getNamesOfSpeciesNew(basicInfoPref.getString("model_id", "1")), store);
        regnerationSection.addEl(species);

        HTextEntryElement speciesOther = new HTextEntryElement(SPECIES_OTHER, "Name of species ( Other )", "Enter species name", true, store);
        regnerationSection.addEl(speciesOther);
        species.addPosElement(speciesOther);

        HNumericElement totalnoOfSpecies = new HNumericElement(Database.STEMS_WITH_COLLAR_2_10CM,
                "2.Total number of Stems with collar girth between 2 -10cm", "Enter numbers ", true, store);
        regnerationSection.addEl(totalnoOfSpecies);


        HNumericElement averageGirth = new HNumericElement(Database.AVERAGE_COLLAR_GIRTH,
                "3.Average collar girth ", "Enter average collar girth ( in < 10cm)", true, store);
        regnerationSection.addEl(averageGirth);

        HNumericElement averageGbh = new HNumericElement(Database.AVERAGE_HEIGHT_METERS, "4.Average " +
                "Height", "Enter average height in meters", true, store);
        regnerationSection.addEl(averageGbh);

   /*     HPickerElement replantPermCheck = new HPickerElement(Database.NTFP_SPECIES, "5.Is it a NTFP Species", "Select an option", true, "Yes|No", store);
        regnerationSection.addEl(replantPermCheck);

        HTextEntryElement ntfpSpeciesPart = new HTextEntryElement(Database.WHICH_PART_USED_AS_NTFP, "a)Which part of the plant is used as NTFP", "specify", true, store);
        replantPermCheck.addPosElement(ntfpSpeciesPart);
        regnerationSection.addEl(ntfpSpeciesPart);

        HNumericElement approxWeight = new HNumericElement(Database.APPROX_WEIGHT, "b)Approx.Weight in grams", "specify", true, store);
        replantPermCheck.addPosElement(approxWeight);
        regnerationSection.addEl(approxWeight);*/

        HTextAreaEntryElement remarks = new HTextAreaEntryElement(Database.SHRUB_VEGETATION_REMARKS, "5.Remarks", "about the conditionof shrub vegetation", true, store);
        regnerationSection.addEl(remarks);


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
            if (basicInfoPref.getString(Database.NO_OF_EMPTY_PITS_TOTAL, "").equalsIgnoreCase("")) {
                basicInfoPref.edit().putString(Database.NO_OF_EMPTY_PITS_TOTAL, noOfEmptyPits.getEditText().getText().toString()).apply();
                basicInfoPref.edit().putString(Database.TOTAL_NO_SURVIVED, totalNoSurvived.getEditText().getText().toString()).apply();
            } else {
                String no_of_empty_pit = basicInfoPref.getString(Database.NO_OF_EMPTY_PITS_TOTAL, "0");
                String no_of_survived_plant = basicInfoPref.getString(Database.TOTAL_NO_SURVIVED, "0");

                if (noOfEmptyPits.getEditText().toString().equalsIgnoreCase("")) {
                    int total_final_pits = Integer.parseInt(noOfEmptyPits.getEditText().getText().toString()) + Integer.parseInt(no_of_empty_pit);
                    int total_no_survived = Integer.parseInt(totalNoSurvived.getEditText().getText().toString()) + Integer.parseInt(no_of_survived_plant);
                    basicInfoPref.edit().putString(Database.NO_OF_EMPTY_PITS_TOTAL, String.valueOf(total_final_pits)).apply();
                    basicInfoPref.edit().putString(Database.TOTAL_NO_SURVIVED, String.valueOf(total_no_survived)).apply();
                }
                else {
//                    Toast.makeText(SamplePlotSurvey.this,"Please enter no. of empty pits",Toast.LENGTH_SHORT).show();
                }
                Log.e("asdcsd", "" + basicInfoPref.getString(Database.NO_OF_EMPTY_PITS_TOTAL, ""));

            }
            Log.e("jvhgvhgvhg", "" + basicInfoPref.getString(Database.NO_OF_EMPTY_PITS_TOTAL, ""));


            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
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

        return new HRootElement("Sample Plot Form", sections);
    }

    private void submitSamplePlotDetails() {
        SurveyCreation surveyCreation = new SurveyCreation();
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
        Database db = new Database(this.getApplicationContext());
        Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_SAMPLE_PLOT_MASTER, db);
        ContentValues cv = insertValuesToSamplePlot(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
        long samplePlotId;
        cv.put(Database.FORM_ID, pref.getString(Database.FORM_ID, "0"));

        if (Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0")) == 0) {
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            samplePlotId = db.insertIntoSamplePlot(cv);
            File mediaStorageDir = surveyCreation.getPictureFolder(folderName);
            if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                mediaStorageDir.renameTo(surveyCreation.getNewPictureFolder(samplePlotId, folderName));
            }
        } else {
            samplePlotId = Long.parseLong(pref.getString(Database.SAMPLE_PLOT_ID, "0"));
            cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
            cv.put(Database.SAMPLE_PLOT_ID, samplePlotId);
            cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
            db.updateTableWithId(Database.TABLE_SAMPLE_PLOT_MASTER, Database.SAMPLE_PLOT_ID, cv);
        }
        Log.i("UPDATE", (String.valueOf(db.updateTableWithoutId(Database.TABLE_SAMPLE_PLOT_INVENTORY, Database.SAMPLE_PLOT_ID, samplePlotId))));
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
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
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
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    public void showSaveFormDataAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Some fieds are empty, you are not able to filled data outside perambulation area.  Are you sure want to Exit?");
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

        SharedPreferences pref = this.getApplicationContext().getSharedPreferences(SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
        benId = Integer.parseInt(pref.getString(Database.SAMPLE_PLOT_ID, "0"));
        Log.i(Constants.SARATH, "benId: " + benId);
//        totalNo.setValue(String.valueOf(db.gettotalNoSeedling(benId, SEEDLING)));
        totalNoSurvived.setValue(String.valueOf(db.gettotalNoSurving(benId, SEEDLING)));
//        calculatePercentage();
    }

}
