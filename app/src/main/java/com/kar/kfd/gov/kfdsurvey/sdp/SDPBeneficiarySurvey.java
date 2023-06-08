package com.kar.kfd.gov.kfdsurvey.sdp;

import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_BENEFICIARY;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_SAMPLE_PLOT_MASTER;
import static com.kar.kfd.gov.kfdsurvey.sdp.SDPSamplingSurvey.SDP_SURVEY;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.map.MapGps;
import com.kar.kfd.gov.kfdsurvey.map.MapGps_Beneficiary;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class SDPBeneficiarySurvey extends HBaseFormActivity {

    //    public static final String folderName = Constants.FORMTYPE_SDP_BENIFICIARY;
    public static final String folderName = Constants.FORMTYPE_SDP_BENIFICIARY;
    public static final String folderNameMap = Constants.SDPBENIFICIARY_MAP;
    public static final String OTHERS_IF_ANY_SPECIFY = "  Others if any (specify)  ";
    public static final String BENEFICIARY_DETAILS = "BeneficiaryDetails";
    private SweetAlertDialog dialog;
    private SDPBeneficiarySurvey mSurvey = this;
    private Database db;
    private String formStatus = "0";
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;

    public int formFilledStatus = 0;

    private HTextEntryElement gpsMeasure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

    }


    @Override
    protected HRootElement createRootElement() {

        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
        SharedPreferences sdpPref = getSharedPreferences(SDP_SURVEY, Context.MODE_PRIVATE);
        HPrefDataStore store = new HPrefDataStore(pref);
        db = new Database(this.getApplicationContext());

        ArrayList<HSection> sections = new ArrayList<>();
        formStatus = pref.getString("formStatus", "0");

        HSection beneficiaryInformation = new HSection("I.Basic Information of Beneficiary");

        HTextElement benDetails = new HTextElement("A.Beneficiary Details");
        beneficiaryInformation.addEl(benDetails);
        pref.edit().putString("total_incentive", pref.getString(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_TOTAL, "0")).apply();


        HNumericElement beneficiaryNo = new HNumericElement("beneficiary_no", "1.Beneficiary No", "Enter the beneficiary no.", true, store);
        beneficiaryInformation.addEl(beneficiaryNo);
        beneficiaryNo.setNotEditable();
        if ((Integer.parseInt(pref.getString(Database.BENEFICIARY_ID, "0")) == 0)) {
            beneficiaryNo.setValue(String.valueOf(db.getNumberOfBeneficiaries(Integer.parseInt(pref.getString(Database.FORM_ID, "0"))) + 1));
        } else {
            beneficiaryNo.setValue(String.valueOf(db.getBeneficiaryNumberForFormId(Integer.parseInt(pref.getString(Database.FORM_ID, "0")), Integer.parseInt(pref.getString(Database.BENEFICIARY_ID, "0")))));
        }

        HTextEntryElement beneficiaryName = new HTextEntryElement(Database.NAME, "2.Name", "Enter the name of the beneficiary", true, store);
        beneficiaryInformation.addEl(beneficiaryName);

        HTextEntryElement fathersName = new HTextEntryElement(Database.FATHER_NAME,
                "3.Father's/Husband's" +
                        " Name", "Enter the name of the beneficiary's father", true, store);
        beneficiaryInformation.addEl(fathersName);

        HNumericElement aadharNumber = new HNumericElement(Database.AADHAR_NUMBER, "4.Aadhar Number(if not available put 0)", "", true, store);
        beneficiaryInformation.addEl(aadharNumber);
        aadharNumber.setMaxLength(12);

        HPickerElement beneficiarySex = new HPickerElement(Database.SEX, "5.Gender", "Select an option", true, "Male|Female", store);
        beneficiaryInformation.addEl(beneficiarySex);

        HNumericElement beneficiaryAge = new HNumericElement(Database.AGE, "6.Age (years)", "Enter age", true, store);
        beneficiaryInformation.addEl(beneficiaryAge);
        beneficiaryAge.setDecimal(false);

        HPickerElement beneficiaryEducation = new HPickerElement(Database.EDUCATION,
                "7.Education", "Select an option", true, "Illiterate|Below class X|Class X|PUC|Diploma|Graduate|PG|Doctorate", store);
        beneficiaryInformation.addEl(beneficiaryEducation);

        HTextEntryElement gramPanchayatName = new HTextEntryElement(Database.GRAMA_PANCHAYAT_NAME, "8.Grampanchayat Name", "Enter Grampanchayat Name", true, store);
        beneficiaryInformation.addEl(gramPanchayatName);
        gramPanchayatName.setValue(sdpPref.getString(Database.GRAMA_PANCHAYAT_NAME, ""));


        HTextEntryElement villageName = new HTextEntryElement(Database.VILLAGE_NAME, "9.Village Name", "Enter Village Name", true, store);
        beneficiaryInformation.addEl(villageName);
        villageName.setValue(sdpPref.getString(Database.VILLAGE_NAME, ""));

        HPickerElement programName = new HPickerElement(Database.PROGRAM_NAME,
                "10.Scheme/Program", "Enter the name of the program", true, "Raising of Seedling for Public Distribution|" +
                "Krishi Aranaya Protsaha Yojane|Mahatma Gandhi National Rural Employment Act|Siri Chandana Vana", store);
        beneficiaryInformation.addEl(programName);


        HPickerElement yearOfImplementation = new HPickerElement(Database.YEAR_OF_IMPLEMENTATION,
                "11.Year of Implementation", "Select year", true,
                "2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21",
                store);
        beneficiaryInformation.addEl(yearOfImplementation);

        HNumericElement totalLandholding = new HNumericElement(Database.LAND_HOLDING_ACRE, "12.Land Holding ( in acres ) ", "Enter land holding ( in acres )", true, store);
        beneficiaryInformation.addEl(totalLandholding);

        Log.e("dsads",""+Integer.parseInt(String.valueOf(db.getLastBenID()+1)));

        HTextEntryElement surveyNosWherePlanted = new HTextEntryElement(Database
                .SURVEY_NUMBERS_WHERE_PLANTED, "13.Survey no(s) where planted", "Enter survey no(s)", true, store);
        beneficiaryInformation.addEl(surveyNosWherePlanted);

        /*HPickerElement plantationModel = new HPickerElement(Database.PLANTATION_MODEL, "5.Plantation model", "Select an option", true,db.getNamesOfModels(), store);
        modelId = plantationModel.getSelectedIndex()+1;

        beneficiaryInformation.addEl(plantationModel);

        plantationModel.addValueChangedListener(new HValueChangedListener() {
            @Override
            public void onValueChanged(HElement el) {
                //  int id = el.getElType();
                HPickerElement pickerItem = (HPickerElement) el;
                Log.d("onValueChanged", "onValueChanged: "+pickerItem.getIndex()+1+pickerItem.getValue());
                modelId = pickerItem.getIndex()+1;
                pref.edit().putInt("model_id",modelId ).commit();

            }
        });

        pref.edit().putInt("model_id",modelId ).commit();

        HButtonElement listSpecies = new HButtonElement("Add/View details of species");
        beneficiaryInformation.addEl(listSpecies);
        listSpecies.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SurveyList.class);
                int formId = Integer.parseInt(pref.getString(Database.FORM_ID,"0"));
                i.putExtra("id",formId);
                i.putExtra("List-type", Constants.BENEFICIARY_SPECIES_LIST);
                i.putExtra("formStatus", formStatus);
                startActivity(i);
            }
        });*/
       /* HPickerElement beneficiaryAvail = new HPickerElement(Database.BENEFICIARY_AVAILABLE_ATTIME_OF_VISIT,"a.Was the beneficiary available at the time of visit?","Select an option",true,"Yes|No",store);
        beneficiaryInformation.addEl(beneficiaryAvail);

        HPickerElement beneficiaryNotAvail = new HPickerElement(Database.BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON,"Reason","Select an option",true,"Door Lock|Respondent not available|Beneficiary unaware of the programme|Any others(specify)",store);
        beneficiaryInformation.addEl(beneficiaryNotAvail);
        beneficiaryAvail.addNegElement(beneficiaryNotAvail);

        HTextAreaEntryElement beneficiaryNotAvailOtherReason = new HTextAreaEntryElement(Database.BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON_OTHER,"Any others","Please specify",false,store);
        beneficiaryInformation.addEl(beneficiaryNotAvailOtherReason);
        beneficiaryNotAvail.addElementForValue(beneficiaryNotAvailOtherReason, 3);

        HPickerElement beneficiaryWillingToParticipate = new HPickerElement(Database.BENEFICIARY_WILLING_TO_PARTICIPATE_IN_SURVEY,"b.Was he willing to participate in the survey?","Select an option",true,"Yes|No",store);
        beneficiaryInformation.addEl(beneficiaryWillingToParticipate);
        beneficiaryAvail.addPosElement(beneficiaryWillingToParticipate);

        HTextAreaEntryElement  beneficiaryWillingToParticipate1 = new HTextAreaEntryElement
                (Database.BENEFICIARY_NOT_WILLING_TO_PARTICIPATE_IN_SURVEY_REASON,"Specify " +
                        "Reason","Please Specify Reason",false,store);
        beneficiaryInformation.addEl(beneficiaryWillingToParticipate1);
        beneficiaryWillingToParticipate.addNegElement(beneficiaryWillingToParticipate1);*/



       /* HTextAreaEntryElement polybagsize = new HTextAreaEntryElement(SPECIES_OTHER,
                "Poly bag size","Enter poly bag size",false,store);
        beneficiaryInformation.addEl(polybagsize);*/

    /*    HPickerElement pbviseSize = new HPickerElement(Database.SPECIES_SIZE, "Size of polybag", "Select an option", false, "5 X 8|" +
                "6 X 9|8 X 12|10 X 16|14 X 20", store);
        beneficiaryInformation.addEl(pbviseSize);*/


        HNumericElement number = new HNumericElement(Database.SEEDLING_PROCURED,
                "14.Total Number of Seedlings Procured", "Specify number", true, store);
        number.setEditable(true);
        beneficiaryInformation.addEl(number);


        HNumericElement amountPaid = new HNumericElement(Database.COST_PAID_APPLICABLE,
                "15.Total Amount paid", "Enter amount paid", true, store);
        beneficiaryInformation.addEl(amountPaid);


    /*   HTextAreaEntryElement speciesOther = new HTextAreaEntryElement(SPECIES_OTHER,"Other Species ( separated by comma ) ","Press put a comma after each species name",false,store);
        beneficiaryInformation.addEl(speciesOther);

        HNumericElement costPaid = new HNumericElement(Database.COST_PAID,"10.Cost Paid ( in " +
              "Rupees ) ","Enter cost paid in Rupees and 'Enter Not-Available' if data is not available",true,store);
        beneficiaryInformation.addEl(costPaid);

       *//* HTextView costPaid = new HTextView("10.Cost Paid");
        beneficiaryInformation.addEl(costPaid);*//*

        final HNumericElement costPaid5x8 = new HNumericElement(Database.COST_PAID_5X8,"Cost Paid 5x8 ( in Rupees ) ","Enter cost paid in Rupees and 'Enter Not-Available' if data is not available",true,store);
        beneficiaryInformation.addEl(costPaid5x8);

        final HNumericElement costPaid6x9 = new HNumericElement(Database.COST_PAID_6X9,"Cost Paid 6x9( in Rupees ) ","Enter cost paid in Rupees and 'Enter Not-Available' if data is not available",true,store);
        beneficiaryInformation.addEl(costPaid6x9);

        final HNumericElement costPaid8x12 = new HNumericElement(Database.COST_PAID_8X12,"Cost Paid 8x12( in Rupees ) ","Enter cost paid in Rupees and 'Enter Not-Available' if data is not available",true,store);
        beneficiaryInformation.addEl(costPaid8x12);

        final HNumericElement costPaid14x20 = new HNumericElement(Database.COST_PAID_14X20,"Cost Paid 14x20( in Rupees ) ","Enter cost paid in Rupees and 'Enter Not-Available' if data is not available",true,store);
        beneficiaryInformation.addEl(costPaid14x20);

        final HButtonElement calculateTotal = new HButtonElement("Calculate Total");
        beneficiaryInformation.addEl(calculateTotal);

        final HNumericElement autoCalculatedTotal = new HNumericElement(Database.TOTAL_COST,"Total Cost Paid ( in Rupees ) ","Enter cost paid in Rupees and 'Enter Not-Available' if data is not available",true,store);
        beneficiaryInformation.addEl(autoCalculatedTotal);

        calculateTotal.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotal.getButtonView().setFocusableInTouchMode(true);
                calculateTotal.getButtonView().requestFocus();
                calculateTotal.getButtonView().setFocusableInTouchMode(false);
                float cost5x8, cost6x9, cost8x12,cost14x20;
                try {
                    cost5x8 = Float.parseFloat(costPaid5x8.getValue());
                } catch (NumberFormatException ex) {
                    cost5x8 = 0;
                }
                try {
                    cost6x9 = Float.parseFloat(costPaid6x9.getValue());
                } catch (NumberFormatException ex) {
                    cost6x9 = 0;
                }
                try {
                    cost8x12 = Float.parseFloat(costPaid8x12.getValue());
                } catch (NumberFormatException ex) {
                    cost8x12 = 0;
                }
                try {
                    cost14x20 = Float.parseFloat(costPaid14x20.getValue());
                } catch (NumberFormatException ex) {
                    cost14x20 = 0;
                }
                float total = cost5x8 + cost6x9 + cost8x12 + cost14x20;
                autoCalculatedTotal.setValue(String.valueOf(total));
                autoCalculatedTotal.getEditText().setText(String.valueOf(total));
            }
        });
        */
        HSection plantingDetails = new HSection("II. Evaluation");


        HPickerElement beneficiaryAvail = new HPickerElement(Database.BENEFICIARY_AVAILABLE_ATTIME_OF_VISIT, "Was the beneficiary available at the time of visit?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(beneficiaryAvail);

        HPickerElement beneficiaryNotAvail = new HPickerElement(Database.BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON, "Reason", "Select an option", true, "Door Lock|Respondent not available|Beneficiary unaware of the programme|Others", store);
        plantingDetails.addEl(beneficiaryNotAvail);
        beneficiaryAvail.addNegElement(beneficiaryNotAvail);

        HTextAreaEntryElement beneficiaryNotAvailOtherReason = new HTextAreaEntryElement(Database.BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON_OTHER, "Any others", "Please specify", true, store);
        plantingDetails.addEl(beneficiaryNotAvailOtherReason);
        beneficiaryNotAvail.addElementForValue(beneficiaryNotAvailOtherReason, 3);

        HPickerElement beneficiaryWillingToParticipate = new HPickerElement(Database.BENEFICIARY_WILLING_TO_PARTICIPATE_IN_SURVEY, "Was he willing to participate in the survey?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(beneficiaryWillingToParticipate);
        beneficiaryAvail.addPosElement(beneficiaryWillingToParticipate);

        HPickerElement is_evaluator_reached = new HPickerElement(Database.IS_EVALUATOR_REACHED_FIELD_OF_BENEFECIARY, "Is evaluator able to reach the field of beneficiary and record the performance of seedlings ?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(is_evaluator_reached);
        beneficiaryAvail.addNegElement(is_evaluator_reached);
        beneficiaryWillingToParticipate.addNegElement(is_evaluator_reached);

        HTextAreaEntryElement beneficiaryWillingToParticipate1 = new HTextAreaEntryElement
                (Database.BENEFICIARY_NOT_WILLING_TO_PARTICIPATE_IN_SURVEY_REASON, "Specify " +
                        "Reason", "Please Specify Reason", true, store);
        plantingDetails.addEl(beneficiaryWillingToParticipate1);
        beneficiaryWillingToParticipate.addNegElement(beneficiaryWillingToParticipate1);

        HButtonElement viewPhoto = new HButtonElement("View/Take photographs of Beneficiary/Field");
        viewPhoto.setOnClick(v -> {
            ImageGrid imageGrid = new ImageGrid();
            Bundle bundle = new Bundle();
            bundle.putString("imageFolderName", folderName);
            bundle.putString("formId", pref.getString(Database.BENEFICIARY_ID, "0"));
            bundle.putString("formStatus", formStatus);
            imageGrid.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, imageGrid, "ImageGrid");
            transaction.addToBackStack("ImageGrid");
            transaction.commit();
        });
        plantingDetails.addEl(viewPhoto);


        HGpsElement gpsElement = new HGpsElement("Get GPS location", true);
        if (formStatus.equals("0")) {
            plantingDetails.addEl(gpsElement);
//              beneficiaryWillingToParticipate.addPosElement(gpsElement);
        }

        HTextEntryElement latitudeEl = new HTextEntryElement(Database.PLANTING_GPS_LATITUDE, "Latitude", "Click on the Gps button to get location", true, store);
        latitudeEl.setNotEditable();
        gpsElement.setLatitude(latitudeEl);
        plantingDetails.addEl(latitudeEl);
//        beneficiaryWillingToParticipate.addPosElement(latitudeEl);

        HTextEntryElement longitudeEl = new HTextEntryElement(Database.PLANTING_GPS_LONGITUDE, "Longitude", "Click on the Gps button to get location", true, store);
        longitudeEl.setNotEditable();
        gpsElement.setLongitude(longitudeEl);
        plantingDetails.addEl(longitudeEl);
//        beneficiaryWillingToParticipate.addPosElement(longitudeEl);

        HTextEntryElement altitudeEl = new HTextEntryElement(Database.PLANTING_GPS_ALTITUDE_METERS, "Altitude", "Click on the Gps button to get location", true, store);
        altitudeEl.setNotEditable();
        gpsElement.setAltitude(altitudeEl);
        plantingDetails.addEl(altitudeEl);
//        beneficiaryWillingToParticipate.addPosElement(altitudeEl);
        // ----just for saving the creation timestamp of location coordinates ----
        HTextEntryElement timestampEl = new HTextEntryElement(Database.GPS_COORDINATE_CREATION_TIMESTAMP, "", "", true, store);
        gpsElement.setCreationTimeStamp(timestampEl);

        /*HTextElement plantingLabel = new HTextElement("Planting Details");
        plantingDetails.addEl(plantingLabel);*/
        //  beneficiaryWillingToParticipate.addPosElement(plantingLabel);
        HTextElement beneficiaryAvailDetails = new HTextElement("A. Planting details");
        plantingDetails.addEl(beneficiaryAvailDetails);
        beneficiaryWillingToParticipate.addPosElement(beneficiaryAvailDetails);
        is_evaluator_reached.addPosElement(beneficiaryAvailDetails);

        HMultiPickerElement species = new HMultiPickerElement(Database.SPECIES_SDP_BENEFICIARY, "Species procured", "Select species", true, "Others|" + db.getNamesOfSdpSpecies(), store);
        plantingDetails.addEl(species);
        beneficiaryWillingToParticipate.addPosElement(species);
        is_evaluator_reached.addPosElement(species);

        HTextEntryElement otherSpecies = new HTextEntryElement(Database.SPECIES_OTHER, OTHERS_IF_ANY_SPECIFY, "Specify Other Species", true, store);
        species.addElementForValue(otherSpecies, 0);
        plantingDetails.addEl(otherSpecies);


        HPickerElement typeOfPlanting = new HPickerElement(Database.TYPE_OF_PLANTING, "1.Type of " +
                "planting", "Select an option", true, "Agro forestry(bund plantation)|Farm " +
                "forestry(block plantation)", store);
        plantingDetails.addEl(typeOfPlanting);
        beneficiaryWillingToParticipate.addPosElement(typeOfPlanting);
        is_evaluator_reached.addPosElement(typeOfPlanting);


        HTextAreaEntryElement typeOfPlantingmain = new HTextAreaEntryElement(Database
                .TYPE_OF_PLANTING_MAINCROP, "What is the maincrop?", "Specify", true, store);
        plantingDetails.addEl(typeOfPlantingmain);
        typeOfPlanting.addElementForValue(typeOfPlantingmain, 0);
        //typeOfPlanting.addElementForValue(typeOfPlantingmain,1);

        HNumericElement averageSpacement = new HNumericElement(Database.AVERAGE_SPACEMENT_METERS,
                "2.Average Espacement ( in meters ) ", "Enter average Espacement ( in meters ) ",
                true, store);
        plantingDetails.addEl(averageSpacement);
        beneficiaryWillingToParticipate.addPosElement(averageSpacement);
        is_evaluator_reached.addPosElement(averageSpacement);

        HButtonElement map = new HButtonElement("Perambulate");
        plantingDetails.addEl(map);
        beneficiaryWillingToParticipate.addPosElement(map);
        is_evaluator_reached.addPosElement(map);
        map.setOnClick(v -> {
            Intent i = new Intent(getApplicationContext(), MapGps_Beneficiary.class);
            i.putExtra(Database.PREFERENCE, BENEFICIARY_DETAILS);
            pref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
            pref.edit().putString("formId", pref.getString(Database.BENEFICIARY_ID, "0")).apply();
            pref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_SDP).apply();
            pref.edit().putString(Database.FOLDER_NAME, folderNameMap).apply();
            startActivity(i);
        });

        gpsMeasure = new HTextEntryElement(Database.GPS_MEASUREMENT, "Gps Measurement(Polyline in metres/Polygon in Hectares)", "Approximate distance in meters", true);
        gpsMeasure.setNotEditable();
        gpsMeasure.setMaxLength(13);
        gpsMeasure.setValue(pref.getString(Database.GPS_MEASUREMENT, ""));
        plantingDetails.addEl(gpsMeasure);
        beneficiaryWillingToParticipate.addPosElement(gpsMeasure);
        is_evaluator_reached.addPosElement(gpsMeasure);
        //------------------------------------------------------------------------


//"Well " +        "irrigated|Canal irrigated|Other (specify)|Not irrigated"
        HPickerElement irrigation = new HPickerElement(Database.PLANTING_IRRIGATION_LEVEL,
                "3.Whether the Plantation is Irrigated or not?", "Select an option", true,
                "Yes|No", store);
        plantingDetails.addEl(irrigation);
        beneficiaryWillingToParticipate.addPosElement(irrigation);
//        is_evaluator_reached.addPosElement(irrigation);

//"Well " +  "irrigated|Canal " +        "irrigated|Other (specify)|Not irrigated"
        HPickerElement irrigationMethod = new HPickerElement(Database.PLANTING_IRRIGATION_METHOD,
                "Irrigation Method", "Select an option", true, "Canal|Open Well|Bore Well|Drip " +
                "Irrigation|Farm Pond|Other (specify)", store);
        plantingDetails.addEl(irrigationMethod);
        irrigation.addPosElement(irrigationMethod);


        HTextAreaEntryElement irrigationOther = new HTextAreaEntryElement(Database.PLANTING_IRRIGATION_LEVEL_OTHER_DETAILS, "Specify irrigation type", "Enter details", true, store);
        irrigationMethod.addElementForValue(irrigationOther, 6);
        plantingDetails.addEl(irrigationOther);

        HPickerElement fertilizeApplication = new HPickerElement(Database
                .PLANTING_FERTILIZE_USED, "4.Fertilizer application", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(fertilizeApplication);
        beneficiaryWillingToParticipate.addPosElement(fertilizeApplication);

        HTextAreaEntryElement fertilizeApplicationDetails = new HTextAreaEntryElement(Database
                .PLANTING_FERTILIZE_USED_DETAILS, "Specify", "Enter details", true, store);
//        plantingDetails.addEl(fertilizeApplicationDetails);
        fertilizeApplication.addPosElement(fertilizeApplicationDetails);
        beneficiaryWillingToParticipate.addPosElement(fertilizeApplicationDetails);

        HPickerElement pruning = new HPickerElement(Database.PLANTING_PRUNINGE_DONE, "5.Pruning",
                "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(pruning);
        beneficiaryWillingToParticipate.addPosElement(pruning);
//        is_evaluator_reached.addPosElement(pruning);

        HTextAreaEntryElement otherTreatments = new HTextAreaEntryElement(Database
                .PLANTING_OTHER_TREATMENT_DETAILS, "6.Other treatments,if any", "Enter other treatments done", true, store);
        plantingDetails.addEl(otherTreatments);
        beneficiaryWillingToParticipate.addPosElement(otherTreatments);
//        is_evaluator_reached.addPosElement(otherTreatments);

        HPickerElement totalExpenditureAvailable = new HPickerElement(Database
                .PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_APPLICABLE, "7. Is the total expenditure " +
                "incurred information available?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(totalExpenditureAvailable);
        beneficiaryWillingToParticipate.addPosElement(totalExpenditureAvailable);
//        is_evaluator_reached.addPosElement(totalExpenditureAvailable);

        HNumericElement totalExpenditure = new HNumericElement(Database.PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_RS, "Total expenditure incurred till date ( in Rupees )", "Enter total expenditure in rupees", true, store);
        plantingDetails.addEl(totalExpenditure);
        totalExpenditureAvailable.addPosElement(totalExpenditure);

        HTextElement seedlingSection = new HTextElement("B: Performance of seedlings");
        plantingDetails.addEl(seedlingSection);
        beneficiaryWillingToParticipate.addPosElement(seedlingSection);
        is_evaluator_reached.addPosElement(seedlingSection);

        species.setListener((which, options, values) -> Log.d("sarath", "valueChanged: " + options.length));

        HButtonElement removeSeedling = new HButtonElement("Seedling Details");
        plantingDetails.addEl(removeSeedling);
        beneficiaryWillingToParticipate.addPosElement(removeSeedling);
        is_evaluator_reached.addPosElement(removeSeedling);
        removeSeedling.setOnClick(v -> {
            if (!TextUtils.isEmpty(species.getValue())) {
                Intent i = new Intent(mSurvey.getApplicationContext(), SurveyList.class);
                i.putExtra("id", Integer.parseInt(pref.getString(Database.BENEFICIARY_ID, "0")));
                i.putExtra("List-type", Constants.SEEDLINGS_LIST);
                i.putExtra("formStatus", formStatus);
                i.putExtra(Database.PART_TYPE, Constants.FORMTYPE_SDP);
                int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));
                i.putExtra("formId", formId);
                if (!TextUtils.isEmpty(species.getValue())) {
                    startActivity(i);
                } else {
                    Toast.makeText(mSurvey, "Select Atleast one Species ", Toast.LENGTH_SHORT).show();
                }
            }

        });

       /* HButtonElement calcTotalSeedling = new HButtonElement("Calculate Total Seedling");

        plantingDetails.addEl(calcTotalSeedling);*/

   /*     noOfSeedlingsPlanted = new HNumericElement(Database
                .NUMBER_OF_SEEDLINGS_PLANTED, "1.Total number of seedlings planted", "Fill the seedling details to calculate", true, store);
        plantingDetails.addEl(noOfSeedlingsPlanted);
        beneficiaryWillingToParticipate.addPosElement(noOfSeedlingsPlanted);
        noOfSeedlingsPlanted.setDecimal(false);
        noOfSeedlingsPlanted.setNotEditable();


        noOfSeedlingsSurviving = new HNumericElement(Database.NUMBER_OF_SEEDLINGS_SURVIVING, "2.Total number of seedlings surviving", "Fill the seedling details to calculate", true, store);
        plantingDetails.addEl(noOfSeedlingsSurviving);
        beneficiaryWillingToParticipate.addPosElement(noOfSeedlingsSurviving);
        noOfSeedlingsSurviving.setDecimal(false);
        noOfSeedlingsSurviving.setNotEditable();

        percentageOfSeedling = new HNumericElement(Database.SEEDLING_PERCENTAGE, "3.Survival Percentage ", "press calculate button to calculate percentage", true, store);
        plantingDetails.addEl(percentageOfSeedling);
        beneficiaryWillingToParticipate.addPosElement(percentageOfSeedling);
        percentageOfSeedling.setNotEditable();*/




        /*calcTotalSeedling.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int totalseedling = db.gettotalSeedling(benId, Constants.FORMTYPE_SDP);
                noOfSeedlingsPlanted.setValue(String.valueOf(totalseedling));
                noOfSeedlingsSurviving.setValue(String.valueOf(db.gettotalSurving(benId,Constants.FORMTYPE_SDP)));

            }
        });*/
        HTextElement incentivesRecieved = new HTextElement("C: Incentives received");
        plantingDetails.addEl(incentivesRecieved);
        beneficiaryWillingToParticipate.addPosElement(incentivesRecieved);

       /* HTextElement  cashIncentivesReceived = new HTextElement("1.Cash Incentives received");
        incentivesRecieved.addEl(cashIncentivesReceived);

        HTextElement  incentivesTypeReceived = new HTextElement("Type of Incentive received");
        incentivesRecieved.addEl(incentivesTypeReceived);*/

        HTextView mgnreegsIncentive = new HTextView("1.Payment received for earth works from " +
                "MGNREGS etc.");
        plantingDetails.addEl(mgnreegsIncentive);
        beneficiaryWillingToParticipate.addPosElement(mgnreegsIncentive);

        HPickerElement mgnreegsYearReceived = new HPickerElement(Database.PAYMENT_RECEIVED_FROM_MGNREEGS_YEAR, "Year in which received", "Enter year", true, "2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|2020-21|Not Applicable", store);
        plantingDetails.addEl(mgnreegsYearReceived);
        beneficiaryWillingToParticipate.addPosElement(mgnreegsYearReceived);

        HNumericElement mgnreegsAmount = new HNumericElement(Database.PAYMENT_RECEIVED_FROM_MGNREEGS_RS, "Amount in Rupees", "Enter amount in rupees", true, store);
        plantingDetails.addEl(mgnreegsAmount);
        beneficiaryWillingToParticipate.addPosElement(mgnreegsAmount);
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
        plantingDetails.addEl(otherDeptIncentive);
        beneficiaryWillingToParticipate.addPosElement(otherDeptIncentive);

        HPickerElement otherDeptYearReceived = new HPickerElement(Database.SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_YEAR, "Year in which received", "Enter year", true, "2009-10|2010-11|2011-12|2012-13|2013-14|2014-15|2015-16|2016-17|2017-18|2018-19|2019-20|Not Applicable", store);
        plantingDetails.addEl(otherDeptYearReceived);
        beneficiaryWillingToParticipate.addPosElement(otherDeptYearReceived);

        HNumericElement otherDeptAmount = new HNumericElement(Database.SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_RS, "Amount in Rupees", "Enter amount in rupees", true, store);
        plantingDetails.addEl(otherDeptAmount);
        beneficiaryWillingToParticipate.addPosElement(otherDeptAmount);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 0);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 1);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 2);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 3);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 4);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 5);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 6);
        otherDeptYearReceived.addElementForValue(otherDeptAmount, 7);

        HTextView kfdIncentive = new HTextView("3.Incentive received from KFD under KAPY");
        plantingDetails.addEl(kfdIncentive);
        beneficiaryWillingToParticipate.addPosElement(kfdIncentive);

        HPickerElement kfdIncentiveYearOneAvailable = new HPickerElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1_APPLICABLE, "Year 1 cash incentive available", "Select option", true, "Yes|No", store);
        plantingDetails.addEl(kfdIncentiveYearOneAvailable);
        beneficiaryWillingToParticipate.addPosElement(kfdIncentiveYearOneAvailable);

        HPickerElement modePayment = new HPickerElement(Database.MODE_PAYMENT, "Mode Payment", "Enter payment mode", true, "RTGS|NEFT|DD|Cash", store);
        plantingDetails.addEl(modePayment);
        kfdIncentiveYearOneAvailable.addPosElement(modePayment);


        final HNumericElement kfdIncentiveYearOne = new HNumericElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1, "For Year 1 ( in Rupees ) :", "Enter cash incentive for year 1 ( in Rupees )", true, store);
        plantingDetails.addEl(kfdIncentiveYearOne);
        kfdIncentiveYearOneAvailable.addPosElement(kfdIncentiveYearOne);


        HPickerElement kfdIncentiveYearTwoAvailable = new HPickerElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2_APPLICABLE, "Year 2 cash incentive available", "Select option", true, "Yes|No", store);
        plantingDetails.addEl(kfdIncentiveYearTwoAvailable);
        beneficiaryWillingToParticipate.addPosElement(kfdIncentiveYearTwoAvailable);

        HPickerElement modePayment2 = new HPickerElement(Database.MODE_PAYMENT, "Mode Payment",
                "Enter payment mode", true, "RTGS|NEFT|DD|Cash", store);
        plantingDetails.addEl(modePayment2);
        kfdIncentiveYearTwoAvailable.addPosElement(modePayment2);

        final HNumericElement kfdIncentiveYearTwo = new HNumericElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2, "For Year 2 ( in Rupees ) :", "Enter cash incentive for year 2 ( in Rupees )", true, store);
        plantingDetails.addEl(kfdIncentiveYearTwo);
        kfdIncentiveYearTwoAvailable.addPosElement(kfdIncentiveYearTwo);

        HPickerElement kfdIncentiveYearThreeAvailable = new HPickerElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3_APPLICABLE, "Year 3 cash incentive available", "Select option", true, "Yes|No", store);
        plantingDetails.addEl(kfdIncentiveYearThreeAvailable);
        beneficiaryWillingToParticipate.addPosElement(kfdIncentiveYearThreeAvailable);

        HPickerElement modePayment3 = new HPickerElement(Database.MODE_PAYMENT, "Mode Payment",
                "Enter payment mode", true, "RTGS|NEFT|DD|Cash", store);
        plantingDetails.addEl(modePayment3);
        kfdIncentiveYearTwoAvailable.addPosElement(modePayment3);
        kfdIncentiveYearThreeAvailable.addPosElement(modePayment3);

        final HNumericElement kfdIncentiveYearThree = new HNumericElement(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3, "For Year 3 ( in Rupees ) :", "Enter cash incentive for year 3 ( in Rupees )", true, store);
        plantingDetails.addEl(kfdIncentiveYearThree);
        kfdIncentiveYearThreeAvailable.addPosElement(kfdIncentiveYearThree);

      /*  final HButtonElement calculateTotalIncentives = new HButtonElement("Calculate total incentive recieved");
        if (formStatus.equals("0")) {
            plantingDetails.addEl(calculateTotalIncentives);
            beneficiaryWillingToParticipate.addPosElement(calculateTotalIncentives);
        }

        final HTextEntryElement totalIncentives = new HTextEntryElement("total_incentive", "Total :", "Click the button to calculate the total", true, store);
        plantingDetails.addEl(totalIncentives);
        beneficiaryWillingToParticipate.addPosElement(totalIncentives);
        totalIncentives.setNotEditable();

        calculateTotalIncentives.setOnClick(v -> {
            calculateTotalIncentives.getButtonView().setFocusableInTouchMode(true);
            calculateTotalIncentives.getButtonView().requestFocus();
            calculateTotalIncentives.getButtonView().setFocusableInTouchMode(false);
            float year1, year2, year3;
            try {
                year1 = Float.parseFloat(kfdIncentiveYearOne.getValue());
            } catch (NumberFormatException ex) {
                year1 = 0;
            }
            try {
                year2 = Float.parseFloat(kfdIncentiveYearTwo.getValue());
            } catch (NumberFormatException ex) {
                year2 = 0;
            }
            try {
                year3 = Float.parseFloat(kfdIncentiveYearThree.getValue());
            } catch (NumberFormatException ex) {
                year3 = 0;
            }
            float total = year1 + year2 + year3;
            totalIncentives.setValue(String.valueOf(total));
            totalIncentives.getEditText().setText(String.valueOf(total));
        });
*/

        HPickerElement otherRewards = new HPickerElement(Database.INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED, "4.Any awards/rewards/prizes received", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(otherRewards);
        beneficiaryWillingToParticipate.addPosElement(otherRewards);


        HTextAreaEntryElement otherRewardsDetails = new HTextAreaEntryElement(Database.INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED_DETAILS, "Details", "Enter details", true, store);
        plantingDetails.addEl(otherRewardsDetails);
        otherRewards.addPosElement(otherRewardsDetails);

        HTextElement otherGeneralInformation = new HTextElement("D: Beneficiary Feedback");
        plantingDetails.addEl(otherGeneralInformation);
        beneficiaryWillingToParticipate.addPosElement(otherGeneralInformation);

        HPickerElement problemsFaced = new HPickerElement(Database
                .DID_YOU_FACE_PROBLEM_IN_PROCURING_SEEDLING, "1.Did he/she face any problems in " +
                "procuring seedlings?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(problemsFaced);
        beneficiaryWillingToParticipate.addPosElement(problemsFaced);

        HTextAreaEntryElement problemsFacedDetails = new HTextAreaEntryElement(Database
                .DETAILS_OF_PROBLEM_FACED_IN_PROCURING_SEEDLING, "Specify", "Enter details of " +
                "problems faced", true, store);
        problemsFaced.addPosElement(problemsFacedDetails);
        plantingDetails.addEl(problemsFacedDetails);

        HPickerElement requiredSpecies = new HPickerElement(Database.SPECIES_AS_PER_REQUIREMENT, "2.Did he/she get the species of Seedlings as per their requirement?", "Select an Option", true, "Yes|No", store);
        plantingDetails.addEl(requiredSpecies);
        beneficiaryWillingToParticipate.addPosElement(requiredSpecies);

        HTextAreaEntryElement notRequiredReasons = new HTextAreaEntryElement(Database
                .SPECIES_NOT_REQUIREMENT_REASONS, "Specify", "Reasons"
                , true, store);
        requiredSpecies.addNegElement(notRequiredReasons);
//        plantingDetails.addEl(notRequiredReasons);
        beneficiaryWillingToParticipate.addPosElement(notRequiredReasons);

        HPickerElement suitableSpecies = new HPickerElement(Database.SPECIES_SUITABLE_TO_AREA, "3.whether the species of seedlings planted are suitable to area", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(suitableSpecies);
        beneficiaryWillingToParticipate.addPosElement(suitableSpecies);

        HPickerElement satisfiedWithQuality = new HPickerElement(Database
                .ARE_YOU_SATISFIED_WITH_SEEDLING_QUALITY, "4.Is he/she satisfied with the quality" +
                " of  seedlings procured?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(satisfiedWithQuality);
        beneficiaryWillingToParticipate.addPosElement(satisfiedWithQuality);

        HTextAreaEntryElement unsatisfiedReasons = new HTextAreaEntryElement(Database.REASONS_FOR_DISSATISFACTION_WITH_SEEDLING_QUALITY, "Reasons", "Enter reasons why you are unsatisfied with the quality", true, store);
        satisfiedWithQuality.addNegElement(unsatisfiedReasons);
//        plantingDetails.addEl(unsatisfiedReasons);
        beneficiaryWillingToParticipate.addPosElement(unsatisfiedReasons);


        HPickerElement privateSourcesSeedlings = new HPickerElement(Database
                .DID_YOU_BUY_SEEDLINGS_FROM_PVT_NURSERIES, "5.Did he/she procure seedlings from private nurseries ?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(privateSourcesSeedlings);
        beneficiaryWillingToParticipate.addPosElement(privateSourcesSeedlings);

        HTextAreaEntryElement privateSourcesDetails = new HTextAreaEntryElement(Database.DETAILS_OF_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES, "Details of purchase", "Enter details of the purchase", true, store);
        plantingDetails.addEl(privateSourcesDetails);
        privateSourcesSeedlings.addPosElement(privateSourcesDetails);

        HTextAreaEntryElement privateSourcesReasons = new HTextAreaEntryElement(Database
                .REASONS_FOR_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES, "Reasons for procuring from " +
                "private sources(specify)", "Specify reasons for procuring from private sources", true,
                store);
        plantingDetails.addEl(privateSourcesReasons);
        privateSourcesSeedlings.addPosElement(privateSourcesReasons);

        HPickerElement privateSourcesPerformance = new HPickerElement(Database.SEEDLINGS_PERFORMANCE_COMPARED_TO_KFD_SEEDLINGS, "How did the seedlings procured from private nurseries performed?", "Select an option", true, "Better|Same as KFD Seedling|Poor", store);
        plantingDetails.addEl(privateSourcesPerformance);
        privateSourcesSeedlings.addPosElement(privateSourcesPerformance);

        HPickerElement interestedInBuying = new HPickerElement(Database
                .INTERESTED_IN_BUYING_MORE_SEEDLINGS_FROM_KFD, "6.Whether he/she is interested in" +
                " " +
                "procuring more seedlings from KFD?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(interestedInBuying);
        beneficiaryWillingToParticipate.addPosElement(interestedInBuying);

        HPickerElement specifiedSupport = new HPickerElement(Database
                .DO_YOU_NEED_SPECIFIC_SUPPORT_FROM_GOVT, "7.Does he/she require any specific " +
                "support from the government?", "Select an option", true, "Yes|No", store);
        plantingDetails.addEl(specifiedSupport);
        beneficiaryWillingToParticipate.addPosElement(specifiedSupport);

        HTextAreaEntryElement specifiedSupportDetails = new HTextAreaEntryElement(Database
                .DETAILS_OF_SPECIFIC_SUPPORT_NEEDED_FROM_GOVT, "Specify", "Specify the details of " +
                "support required", true, store);
        plantingDetails.addEl(specifiedSupportDetails);
        specifiedSupport.addPosElement(specifiedSupportDetails);

        HTextAreaEntryElement farmSuggestions = new HTextAreaEntryElement(Database
                .SUGGESTIONS_TO_IMPROVE_AGRO_FORESTRY, "8.Any suggestions/comments given by " +
                "beneficiary? ", "Enter suggestions", true, store);
        plantingDetails.addEl(farmSuggestions);
        beneficiaryWillingToParticipate.addPosElement(farmSuggestions);

        HSection submitSec = new HSection("");

        final HButtonElement submit = new HButtonElement("Save");
        submit.setElType(HElementType.SUBMIT_BUTTON);
        submit.setOnClick(v -> {
            submit.getButtonView().setFocusableInTouchMode(true);
            submit.getButtonView().requestFocus();
            submit.getButtonView().setFocusableInTouchMode(false);
            String seedling_status = pref.getString(Database.SEEDLING_DETAIL_STATUS, "");
            if (!checkFormData())
                showSaveFormDataAlert("Some fieds are empty, Are you sure want to Exit?");
            else if (!TextUtils.isEmpty(species.getValue()) && !seedling_status.equals("1")) {
                showSaveFormDataAlert("Seedling Details are not completed, Are you sure want to Exit?");
            } else {
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
            submitSec.addEl(back);
            beneficiaryInformation.setNotEditable();
            plantingDetails.setNotEditable();
            submitSec.setNotEditable();
        } else {
            submitSec.addEl(submit);
        }

        sections.add(beneficiaryInformation);
        sections.add(plantingDetails);
        sections.add(submitSec);


        return new HRootElement("Beneficiary Form", sections);
    }

    private void submitBeneficiaryDetails() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
        final Database db = new Database(this.getApplicationContext());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_submit_form, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            String from = getIntent().getStringExtra("from");
            if (from == null) {
                Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_BENEFICIARY, db);
                ContentValues cv = insertValuesToBeneficiaries(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
                cv.put(Database.SPECIES_SDP_BENEFICIARY, getSpeciesNames(pref));
                cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
                cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
                cv.put(Database.BENEFICIARY_ID, pref.getString(Database.BENEFICIARY_ID, "0"));
                db.updateTableWithId(Database.TABLE_BENEFICIARY, Database.BENEFICIARY_ID, cv);
            }
            else if(from.equalsIgnoreCase("add")){
                Map<String, ArrayList<String>> tableMetadata = getTableMetaData(Database.TABLE_BENEFICIARY, db);
                ContentValues cv = insertValuesToBeneficiaries(tableMetadata.get("columnNamesList"), tableMetadata.get("columnTypesList"), pref, db);
                cv.put(Database.SPECIES_SDP_BENEFICIARY, getSpeciesNames(pref));
                cv.put(Database.FINISHED_POSITION, pref.getInt(Database.FINISHED_POSITION, 0));
                cv.put(Database.FORM_FILLED_STATUS, formFilledStatus);
                cv.put(Database.BENEFICIARY_ID, Integer.parseInt(String.valueOf(db.getLastBenID()+1)));
                db.saveBeneficiary(Database.TABLE_BENEFICIARY, Database.BENEFICIARY_ID, cv);
            }
           /* if (Integer.parseInt(pref.getString(Database.BENEFICIARY_ID, "0")) == 0) {
                long beneficiaryId = db.insertIntoBeneficiaries(cv);
                db.updateTableWithoutId(Database.TABLE_BENEFICIARY_SEEDLING, Database.BENEFICIARY_ID, beneficiaryId);
                File mediaStorageDir = surveyCreation.getPictureFolder(folderName);
                if (mediaStorageDir != null && mediaStorageDir.list() != null) {
                    mediaStorageDir.renameTo(surveyCreation.getNewPictureFolder(beneficiaryId, folderName));
                }
            } else {
                cv.put(Database.FORM_FILLED_STATUS,formFilledStatus);
                cv.put(Database.BENEFICIARY_ID, pref.getString(Database.BENEFICIARY_ID, "0"));
                db.updateTableWithId(Database.TABLE_BENEFICIARY, Database.BENEFICIARY_ID, cv);
            }*/
            pref.edit().clear().apply();
            setClearPref(true);
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private String getSpeciesNames(SharedPreferences pref) {
        String species = removeOtherSpeciesString(pref);
        String otherSpecies = getOtherSpecies(pref);
        if (species.length() > 0 && otherSpecies.length() > 0) {
            return species + "|" + otherSpecies;
        } else {
            Log.d("species", "getSpeciesNames: " + species);
            return species;
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

    private String getOtherSpecies(SharedPreferences pref) {
        String[] options = pref.getString(Database.SPECIES_OTHER, "").split(",");
        StringBuilder builder = new StringBuilder();
        for (String s : options) {
            builder.append(s).append('|');
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    private String removeOtherSpeciesString(SharedPreferences pref) {
        String[] options = pref.getString(Database.SPECIES_SDP_BENEFICIARY, "").split("\\|");
        StringBuilder builder = new StringBuilder();
        for (String s : options) {
            if (!s.contains(OTHERS_IF_ANY_SPECIFY)) {
                builder.append(s).append('|');
            }
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    private ContentValues insertValuesToBeneficiaries(ArrayList<String> columnNames, ArrayList<String> columnTypes, SharedPreferences pref, Database db) {
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

            if (columnName.equals(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_TOTAL)) {
                int cashYear1, cashYear2, cashYear3;
                try {
                    cashYear1 = cv.getAsInteger(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1);
                } catch (Exception ex) {
                    cashYear1 = 0;
                }
                try {
                    cashYear2 = cv.getAsInteger(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2);
                } catch (Exception ex) {
                    cashYear2 = 0;
                }
                try {
                    cashYear3 = cv.getAsInteger(Database.CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3);
                } catch (Exception e) {
                    cashYear3 = 0;
                }
                float totalAmount = cashYear1 + cashYear2 + cashYear3;
                cv.put(columnName, totalAmount);
            }
        }
        long creationTimeStamp = System.currentTimeMillis() / 1000;
        cv.put(Database.CREATION_TIMESTAMP, creationTimeStamp);

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
        SharedPreferences pref = getSharedPreferences(BENEFICIARY_DETAILS, MODE_PRIVATE);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            //--------done by sunil for showing pop for saving----------
            if (pref.getString("formStatus", "0").equals("0")) {
                Log.d("FormStatus", pref.getString("formStatus", "0"));
                showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
            }
            //---------------------------------------------------------
            if (!pref.getString("formStatus", "0").equals("0")) {
                Log.d("FormStatus", pref.getString("formStatus", "0"));
                pref.edit().clear().apply();
                setClearPref(true);
                super.onBackPressed();
            }

        } else {
            getSupportFragmentManager().popBackStack();
        }


    }

    public void showSaveFormDataAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> submitBeneficiaryDetails());

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences(BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
        gpsMeasure.setValue(pref.getString(Database.GPS_MEASUREMENT, ""));
        /*String benId = pref.getString(Database.BENEFICIARY_ID, "0");
        int totalseedling = db.gettotalSeedling(benId, Constants.FORMTYPE_SDP);
        noOfSeedlingsPlanted.setValue(String.valueOf(totalseedling));
        noOfSeedlingsSurviving.setValue(String.valueOf(db.gettotalSurving(benId, Constants.FORMTYPE_SDP)));
        calculatePercentage();*/

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
