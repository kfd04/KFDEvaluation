package com.kar.kfd.gov.kfdsurvey.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.advancework.PlantationSamplingAdvanceWork;
import com.kar.kfd.gov.kfdsurvey.model.LocationWithID;
import com.kar.kfd.gov.kfdsurvey.nursery.NurseryWorkSurvey;
import com.kar.kfd.gov.kfdsurvey.otherworks.OtherSurvey;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;
import com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey;
import com.kar.kfd.gov.kfdsurvey.sdp.SDPSamplingSurvey;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Modified by Sarath
 */
public class AppSettingsFrag extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String DISTRICT = Database.DISTRICT_NAME;
    public static final String DISTRICT_ID = Database.DISTRICT_CODE;
    public static final String TALUK = Database.TALUK_NAME;
    public static final String TALUK_ID = Database.TALUK_CODE;
    public static final String CIRCLE = Database.CIRCLE_NAME;
    public static final String CIRCLE_ID = Database.CIRCLE_ID;
    public static final String DIVISION = Database.DIVISION_NAME;
    public static final String DIVISION_ID = Database.DIVISION_CODE;
    public static final String SUBDIVISION = Database.SUBDIVISION_NAME;
    public static final String SUBDIVISION_ID = Database.SUBDIVISION_CODE;
    public static final String RANGE = Database.RANGE_NAME;
    public static final String RANGE_ID = Database.RANGE_CODE;
    public static final String SUR_NAME = "SURV";
    public static final String SUR_ASST_NAME = "SURVASST";
    public static final String STAFF_NAME = "STFFKFD";
    public static final String STAFF_DESG_NAME = "STFFDSG";
    public static final String HOBLI_NAME = Database.HOBLI_NAME;
    public static final String GRAM_PANCHAYAT_NAME = Database.GRAMA_PANCHAYAT_NAME;
    public static final String GRAM_PANCHAYAT_ID = Database.PANCHAYAT_CODE;
    public static final String VILLAGE_NAME = Database.VILLAGE_NAME;
    public static final String VILLAGE_ID = Database.VILLAGE_CODE;
    public static final String CONSTITUENCY_NAME = Database.CONSTITUENCY_NAME;
    public static final String CONSTITUENCY_ID = Database.CONSTITUENCY_ID;
    public static final String FOREST_NAME = Database.FOREST_NAME;
    public static final String LOCALITY_NAME = Database.LOCALITY_NAME;
    public static final String HAMLET_NAME = Database.HAMLET_NAME;
    public static final String SAMPLE_NUMBER = "sample_number";
    public static final String INVESTIGATOR = "PRNCPL";
    public static final String NUM_1 = "1.";
    public static final String NUM_2 = "2.";
    private static final String NUM_3 = "3.";
    private static final String NUM_4 = "4.";
    private static final String NUM_5 = "5.";
    private static final String NUM_6 = "6.";
    private static final String NUM_6_A = "6(a).";
    private static final String NUM_7 = "7.";
    private static final String NUM_8 = "8.";
    private static final String NUM_9 = "9.";
    private static final String NUM_10 = "10.";
    private static final String NUM_11 = "11.";

    public String[] prefKeys = new String[]{DISTRICT, TALUK, CIRCLE, DIVISION, SUBDIVISION, RANGE, SUR_NAME, SUR_ASST_NAME, STAFF_NAME, STAFF_DESG_NAME};

    private Database db;
    private ArrayList<LocationWithID> districts = new ArrayList<>();
    private ArrayList<LocationWithID> constituency = new ArrayList<>();
    private ArrayList<LocationWithID> circles = new ArrayList<>();
    private Spinner talukNameSpin;
    private Spinner circleNameSpin;
    private Spinner divNameSpin;
    private Spinner subDivSpin;
    private Spinner rangeSpin;
    private Spinner villSpin;
    private Spinner expertNameSpin;
    private Spinner districtNameSpin;
    private Spinner constituencyNameSpin;
    private Spinner gpSpin;
    private ArrayList<LocationWithID> taluks;
    private ArrayList<LocationWithID> gps;
    private ArrayList<LocationWithID> villages;
    private ArrayList<LocationWithID> divisions;
    private ArrayList<LocationWithID> ranges;
    private ArrayList<LocationWithID> subdivision;
    private Spinner[] spinners;
    private EditText sNameET;
    private EditText sAsstNameET;
    private EditText staffNameET;
    private EditText staffDesgET;
    private EditText sampleNoET;
    private SharedPreferences sharedPreferences;
    private String formStatus = "0";
    private Context context;
    private String prefKey;
    private boolean isTalukSet = false;
    private boolean isCircleSet = false;
    private boolean isDivSet = false;
    private boolean isSubDivSet = false;
    private boolean isRangeSet = false;
    private boolean isConsSet = false;
    private EditText localityNameET;
    private EditText forestNameET;
    private EditText hamletNameET;
    private EditText consNameET;
    private ArrayList<String> experts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.app_config, container, false);
        prefKey = "";
        if (getArguments() != null) {
            prefKey = getArguments().getString("preference");
        }
        db = new Database(container.getContext());
        sharedPreferences = layout.getContext().getSharedPreferences(prefKey, Context.MODE_PRIVATE);
        context = container.getContext();
        formStatus = sharedPreferences.getString("formStatus", "0");
        initializeViews(layout);
        return layout;
    }


    private void initializeViews(View layout) {

        Context context = layout.getContext();

        setSectionSeparator(layout);

        districtNameSpin =  layout.findViewById(R.id.disSpin);
        districtNameSpin.setTag(DISTRICT);
        districts = db.getDistrictsWithCode();
        initializeSpinner(districtNameSpin, context);

        constituencyNameSpin = layout.findViewById(R.id.consSpin);
        constituencyNameSpin.setTag(CONSTITUENCY_NAME);

        talukNameSpin =  layout.findViewById(R.id.talSpin);
        talukNameSpin.setTag(TALUK);


        circleNameSpin =  layout.findViewById(R.id.cirSpin);
        circleNameSpin.setTag(CIRCLE);

        divNameSpin =  layout.findViewById(R.id.divSpin);
        divNameSpin.setTag(DIVISION);

        subDivSpin =  layout.findViewById(R.id.subDivSpin);
        subDivSpin.setTag(SUBDIVISION);

        rangeSpin =  layout.findViewById(R.id.rangeSpin);
        rangeSpin.setTag(RANGE);

      /*  hobliSpin = layout.findViewById(R.id.hobliSpin);
        hobliSpin.setTag(HOBLI_NAME);*/

        gpSpin = layout.findViewById(R.id.gpSpin);
        gpSpin.setTag(GRAM_PANCHAYAT_NAME);
        circleNameSpin.setEnabled(false);
        villSpin = layout.findViewById(R.id.villSpin);
        villSpin.setTag(VILLAGE_NAME);


        expertNameSpin =  layout.findViewById(R.id.expertNameSpin);
        expertNameSpin.setTag(INVESTIGATOR);

        spinners = new Spinner[]{talukNameSpin, circleNameSpin, divNameSpin, subDivSpin, rangeSpin, constituencyNameSpin, gpSpin, villSpin};

        localityNameET =  layout.findViewById(R.id.localityNameET);
        localityNameET.addTextChangedListener(new ConfigTextWatcher(localityNameET));
        localityNameET.setText(readPref(LOCALITY_NAME));

        hamletNameET =  layout.findViewById(R.id.hamletNameET);
        hamletNameET.addTextChangedListener(new ConfigTextWatcher(hamletNameET));
        hamletNameET.setText(readPref(HAMLET_NAME));

        forestNameET =  layout.findViewById(R.id.forestNameET);
        forestNameET.addTextChangedListener(new ConfigTextWatcher(forestNameET));
        forestNameET.setText(readPref(FOREST_NAME));

        sNameET =  layout.findViewById(R.id.surveyorNameET);
        sNameET.addTextChangedListener(new ConfigTextWatcher(sNameET));
        sNameET.setText(readPref(SUR_NAME));

        sAsstNameET = layout.findViewById(R.id.surveyorAsstNameET);
        sAsstNameET.addTextChangedListener(new ConfigTextWatcher(sAsstNameET));
        sAsstNameET.setText(readPref(SUR_ASST_NAME));

        staffNameET =  layout.findViewById(R.id.kfdStaffNameET);
        staffNameET.addTextChangedListener(new ConfigTextWatcher(staffNameET));
        staffNameET.setText(readPref(STAFF_NAME));

        staffDesgET =  layout.findViewById(R.id.kfdStaffDesgET);
        staffDesgET.addTextChangedListener(new ConfigTextWatcher(staffDesgET));
        staffDesgET.setText(readPref(STAFF_DESG_NAME));

        sampleNoET =  layout.findViewById(R.id.sampleNoET);
        //  sampleNoET.setText(readPref(SAMPLE_NUMBER));
        sampleNoET.setText("1");
        sampleNoET.addTextChangedListener(new ConfigTextWatcher(sampleNoET));


        layout.findViewById(R.id.btn).setOnClickListener(v -> {
            writePref(SAMPLE_NUMBER, "1");
            Objects.requireNonNull(getActivity()).onBackPressed();
        });

        ArrayList<String> talukNames = new ArrayList<>();
        talukNames.add(readPref(TALUK_ID));
        initializeSpinner(talukNameSpin, context);

        ArrayList<String> constiutencyNames = new ArrayList<>();
        constiutencyNames.add(readPref(CONSTITUENCY_ID));
        initializeSpinner(constituencyNameSpin, context);

        ArrayList<String> circleNames = new ArrayList<>();
        circleNames.add(readPref(CIRCLE_ID));
        initializeSpinner(circleNameSpin, context);


        initializeSpinner(divNameSpin, context);


        initializeSpinner(subDivSpin, context);

        initializeSpinner(rangeSpin, context);

//        initializeSpinner(hobliSpin, context);

        initializeSpinner(gpSpin, context);

        initializeSpinner(villSpin, context);

        ArrayAdapter<String> expertAda = new ArrayAdapter<>(context, R.layout.spinner_textview, experts);
        expertNameSpin.setAdapter(expertAda);
        initializeSpinner(expertNameSpin, context);
        layout.findViewById(R.id.lHamlet).setVisibility(View.GONE);

        if (!formStatus.equals("0")) {
            districtNameSpin.setEnabled(false);
            constituencyNameSpin.setEnabled(false);
            talukNameSpin.setEnabled(false);
            circleNameSpin.setEnabled(false);
            divNameSpin.setEnabled(false);
            subDivSpin.setEnabled(false);
            rangeSpin.setEnabled(false);
//            hobliSpin.setEnabled(false);
            villSpin.setEnabled(false);
            expertNameSpin.setEnabled(false);
            gpSpin.setEnabled(false);
            localityNameET.setKeyListener(null);
            hamletNameET.setKeyListener(null);
            forestNameET.setKeyListener(null);
            sNameET.setKeyListener(null);
            sAsstNameET.setKeyListener(null);
            staffNameET.setKeyListener(null);
            staffDesgET.setKeyListener(null);
            sampleNoET.setKeyListener(null);
        }

        switch (prefKey) {

            case ScpTspSamplingSurvey.SCP_TSP_SAMPLING_SURVEY:
                layout.findViewById(R.id.localityName).setVisibility(View.GONE);
                localityNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.lLocality).setVisibility(View.GONE);
                layout.findViewById(R.id.sLocality).setVisibility(View.GONE);

                layout.findViewById(R.id.hamletName).setVisibility(View.GONE);
                hamletNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.lHamlet).setVisibility(View.GONE);

                /*layout.findViewById(R.id.hobliName).setVisibility(View.GONE);
                hobliSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lHobli).setVisibility(View.GONE);
                layout.findViewById(R.id.sHobli).setVisibility(View.GONE);*/

                numberLabels(ScpTspSamplingSurvey.SCP_TSP_SAMPLING_SURVEY, layout);
                break;
            case OtherSurvey.OTHER_SURVEY:
                layout.findViewById(R.id.talukName).setVisibility(View.GONE);
                talukNameSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lTaluk).setVisibility(View.GONE);
                layout.findViewById(R.id.sTaluk).setVisibility(View.GONE);
                layout.findViewById(R.id.districtName).setVisibility(View.GONE);
                districtNameSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lDistrict).setVisibility(View.GONE);
                layout.findViewById(R.id.sCons).setVisibility(View.GONE);
/*                layout.findViewById(R.id.hobliName).setVisibility(View.GONE);
//                  hobliNameET.setVisibility(View.GONE);
                hobliSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lHobli).setVisibility(View.GONE);
                layout.findViewById(R.id.sHobli).setVisibility(View.GONE);*/
                layout.findViewById(R.id.gpName).setVisibility(View.GONE);
                gpSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lGP).setVisibility(View.GONE);
                layout.findViewById(R.id.sGP).setVisibility(View.GONE);
                layout.findViewById(R.id.villName).setVisibility(View.GONE);
                villSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lVillage).setVisibility(View.GONE);
                layout.findViewById(R.id.sVillage).setVisibility(View.GONE);
                layout.findViewById(R.id.localityName).setVisibility(View.GONE);
                localityNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.lLocality).setVisibility(View.GONE);
                layout.findViewById(R.id.sLocality).setVisibility(View.GONE);
                layout.findViewById(R.id.hamletName).setVisibility(View.GONE);
                hamletNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.l11).setVisibility(View.GONE);
                layout.findViewById(R.id.s11).setVisibility(View.GONE);
                layout.findViewById(R.id.s17).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.forestName).setVisibility(View.GONE);
                forestNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.sForestName).setVisibility(View.GONE);
                layout.findViewById(R.id.lHamlet).setVisibility(View.GONE);

                layout.findViewById(R.id.consName).setVisibility(View.VISIBLE);
                constituencyNameSpin.setVisibility(View.VISIBLE);
                layout.findViewById(R.id.cl).setVisibility(View.VISIBLE);
                numberLabels(OtherSurvey.OTHER_SURVEY, layout);

                break;
            case SDPSamplingSurvey.SDP_SURVEY:
                layout.findViewById(R.id.localityName).setVisibility(View.GONE);
                localityNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.lLocality).setVisibility(View.GONE);
                layout.findViewById(R.id.sLocality).setVisibility(View.GONE);
             /*   layout.findViewById(R.id.hobliName).setVisibility(View.GONE);
                hobliSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lHobli).setVisibility(View.GONE);
                layout.findViewById(R.id.sHobli).setVisibility(View.GONE);*/
                layout.findViewById(R.id.hamletName).setVisibility(View.GONE);
                hamletNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.lHamlet).setVisibility(View.GONE);
                numberLabels(SDPSamplingSurvey.SDP_SURVEY, layout);
                break;
            case PlantationSamplingEvaluation.BASIC_INFORMATION:
             /*   layout.findViewById(R.id.hobliName).setVisibility(View.GONE);
                hobliSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lHobli).setVisibility(View.GONE);
                layout.findViewById(R.id.sHobli).setVisibility(View.GONE);*/
                layout.findViewById(R.id.gpName).setVisibility(View.VISIBLE);
                gpSpin.setVisibility(View.VISIBLE);
                layout.findViewById(R.id.lGP).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.sGP).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.hamletName).setVisibility(View.GONE);
                hamletNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.l11).setVisibility(View.GONE);
                layout.findViewById(R.id.s11).setVisibility(View.GONE);
                numberLabels(PlantationSamplingEvaluation.BASIC_INFORMATION, layout);
                break;
            case PlantationSamplingAdvanceWork.ADVANCE_WORK_SURVEY:
            /*    layout.findViewById(R.id.hobliName).setVisibility(View.GONE);
                hobliSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lHobli).setVisibility(View.GONE);
                layout.findViewById(R.id.sHobli).setVisibility(View.GONE);*/
                layout.findViewById(R.id.gpName).setVisibility(View.VISIBLE);
                gpSpin.setVisibility(View.VISIBLE);
                layout.findViewById(R.id.lGP).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.sGP).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.hamletName).setVisibility(View.GONE);
                hamletNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.l11).setVisibility(View.GONE);
                layout.findViewById(R.id.s11).setVisibility(View.GONE);
                numberLabels(PlantationSamplingEvaluation.BASIC_INFORMATION, layout);
                break;
            case NurseryWorkSurvey.NURSERY_WORK_SURVEY:
               /* layout.findViewById(R.id.hobliName).setVisibility(View.GONE);
                hobliSpin.setVisibility(View.GONE);
                layout.findViewById(R.id.lHobli).setVisibility(View.GONE);
                layout.findViewById(R.id.sHobli).setVisibility(View.GONE);*/
                layout.findViewById(R.id.gpName).setVisibility(View.VISIBLE);
                gpSpin.setVisibility(View.VISIBLE);
                layout.findViewById(R.id.lGP).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.sGP).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.villName).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.lVillage).setVisibility(View.GONE);
                layout.findViewById(R.id.sVillage).setVisibility(View.GONE);
                layout.findViewById(R.id.localityName).setVisibility(View.GONE);
                localityNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.lLocality).setVisibility(View.GONE);
                layout.findViewById(R.id.sLocality).setVisibility(View.GONE);
                layout.findViewById(R.id.hamletName).setVisibility(View.GONE);
                hamletNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.l11).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.s11).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.s17).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.forestName).setVisibility(View.GONE);
                forestNameET.setVisibility(View.GONE);
                layout.findViewById(R.id.lHamlet).setVisibility(View.GONE);
                numberLabels(NurseryWorkSurvey.NURSERY_WORK_SURVEY, layout);
                break;


        }

    }


    private void numberLabels(String survey, View layout) {
        switch (survey) {
            case ScpTspSamplingSurvey.SCP_TSP_SAMPLING_SURVEY:

                TextView cirNm1 = ( layout.findViewById(R.id.circleName));
                cirNm1.setText(NUM_1 + cirNm1.getText());
                TextView divNm1 = ( layout.findViewById(R.id.divName));
                divNm1.setText(NUM_2 + divNm1.getText());
                TextView sDivNm1 = ( layout.findViewById(R.id.subDivName));
                sDivNm1.setText(NUM_3 + sDivNm1.getText());
                TextView rangeNm1 = ( layout.findViewById(R.id.rangeName));
                rangeNm1.setText(NUM_4 + rangeNm1.getText());
                TextView disNm1 = ( layout.findViewById(R.id.districtName));
                disNm1.setText(NUM_5 + disNm1.getText());
                TextView consName1 = layout.findViewById(R.id.consName);
                consName1.setText(NUM_6 + consName1.getText());
                TextView talukNm1 = ( layout.findViewById(R.id.talukName));
                talukNm1.setText(NUM_7 + talukNm1.getText());
                TextView gp1 = ( layout.findViewById(R.id.gpName));
                gp1.setText(NUM_8 + gp1.getText());
                TextView villNm1 = ( layout.findViewById(R.id.villName));
                villNm1.setText(NUM_9 + villNm1.getText());
                TextView hmNm1 = ( layout.findViewById(R.id.hamletName));
                hmNm1.setText(NUM_11 + hmNm1.getText());
                break;
            case OtherSurvey.OTHER_SURVEY:

                TextView cirNm2 = ( layout.findViewById(R.id.circleName));
                cirNm2.setText(NUM_1 + cirNm2.getText());
                TextView divNm2 = ( layout.findViewById(R.id.divName));
                divNm2.setText(NUM_2 + divNm2.getText());
                TextView sDivNm2 = ( layout.findViewById(R.id.subDivName));
                sDivNm2.setText(NUM_3 + sDivNm2.getText());
                TextView rangeNm2 = ( layout.findViewById(R.id.rangeName));
                rangeNm2.setText(NUM_4 + rangeNm2.getText());
                TextView consName2 = layout.findViewById(R.id.consName);
                consName2.setText(NUM_5 + consName2.getText());
                break;
            case SDPSamplingSurvey.SDP_SURVEY:

                TextView cirNm3 = ( layout.findViewById(R.id.circleName));
                cirNm3.setText(NUM_1 + cirNm3.getText());
                TextView divNm3 = ( layout.findViewById(R.id.divName));
                divNm3.setText(NUM_2 + divNm3.getText());
                TextView sDivNm3 = ( layout.findViewById(R.id.subDivName));
                sDivNm3.setText(NUM_3 + sDivNm3.getText());
                TextView rangeNm3 = ( layout.findViewById(R.id.rangeName));
                rangeNm3.setText(NUM_4 + rangeNm3.getText());
                TextView disNm3 = ( layout.findViewById(R.id.districtName));
                disNm3.setText(NUM_5 + disNm3.getText());
                TextView consName3 = layout.findViewById(R.id.consName);
                consName3.setText(NUM_6 + consName3.getText());
                TextView talukNm3 = ( layout.findViewById(R.id.talukName));
                talukNm3.setText(NUM_7 + talukNm3.getText());
                TextView gp = ( layout.findViewById(R.id.gpName));
                gp.setText(NUM_8 + gp.getText());
                TextView villNm = ( layout.findViewById(R.id.villName));
                villNm.setText(NUM_9 + villNm.getText());
                TextView hmNm = ( layout.findViewById(R.id.hamletName));
                hmNm.setText(NUM_11 + hmNm.getText());

                break;

            case PlantationSamplingEvaluation.BASIC_INFORMATION:

                TextView cirNm4 = ( layout.findViewById(R.id.circleName));
                cirNm4.setText(NUM_1 + cirNm4.getText());
                TextView divNm4 = ( layout.findViewById(R.id.divName));
                divNm4.setText(NUM_2 + divNm4.getText());
                TextView sDivNm4 = ( layout.findViewById(R.id.subDivName));
                sDivNm4.setText(NUM_3 + sDivNm4.getText());
                TextView rangeNm4 = ( layout.findViewById(R.id.rangeName));
                rangeNm4.setText(NUM_4 + rangeNm4.getText());
                TextView disNm4 = ( layout.findViewById(R.id.districtName));
                disNm4.setText(NUM_5 + disNm4.getText());
                TextView consName4 = layout.findViewById(R.id.consName);
                consName4.setText(NUM_6 + consName4.getText());
                TextView talukNm4 = ( layout.findViewById(R.id.talukName));
                talukNm4.setText(NUM_7 + talukNm4.getText());
                TextView gp4 = layout.findViewById(R.id.gpName);
                gp4.setText(NUM_8 + gp4.getText());
                TextView villNm4 = ( layout.findViewById(R.id.villName));
                villNm4.setText(NUM_9 + villNm4.getText());

                break;

            case NurseryWorkSurvey.NURSERY_WORK_SURVEY:
                TextView cirNm5 = ( layout.findViewById(R.id.circleName));
                cirNm5.setText(NUM_1 + cirNm5.getText());
                TextView divNm5 = ( layout.findViewById(R.id.divName));
                divNm5.setText(NUM_2 + divNm5.getText());
                TextView sDivNm5 = ( layout.findViewById(R.id.subDivName));
                sDivNm5.setText(NUM_3 + sDivNm5.getText());
                TextView rangeNm5 = ( layout.findViewById(R.id.rangeName));
                rangeNm5.setText(NUM_4 + rangeNm5.getText());
                TextView disNm5 = ( layout.findViewById(R.id.districtName));
                disNm5.setText(NUM_5 + disNm5.getText());
                TextView consName5 = layout.findViewById(R.id.consName);
                consName5.setText(NUM_6 + consName5.getText());
                TextView talukNm5 = ( layout.findViewById(R.id.talukName));
                talukNm5.setText(NUM_7 + talukNm5.getText());
                TextView gp5 = layout.findViewById(R.id.gpName);
                gp5.setText(NUM_8 + gp5.getText());
                TextView villNm5 = ( layout.findViewById(R.id.villName));
                villNm5.setText(NUM_9 + villNm5.getText());
                break;

            case PlantationSamplingAdvanceWork.ADVANCE_WORK_SURVEY:
                TextView cirNm6 = (layout.findViewById(R.id.circleName));
                cirNm6.setText(NUM_1 + cirNm6.getText());
                TextView divNm6 = (layout.findViewById(R.id.divName));
                divNm6.setText(NUM_2 + divNm6.getText());
                TextView sDivNm6 = (layout.findViewById(R.id.subDivName));
                sDivNm6.setText(NUM_3 + sDivNm6.getText());
                TextView rangeNm6 = (layout.findViewById(R.id.rangeName));
                rangeNm6.setText(NUM_4 + rangeNm6.getText());
                TextView disNm6 = (layout.findViewById(R.id.districtName));
                disNm6.setText(NUM_5 + disNm6.getText());
                TextView consName6 = layout.findViewById(R.id.consName);
                consName6.setText(NUM_6 + consName6.getText());
                TextView talukNm6 = (layout.findViewById(R.id.talukName));
                talukNm6.setText(NUM_7 + talukNm6.getText());
                TextView gp6 = layout.findViewById(R.id.gpName);
                gp6.setText(NUM_8 + gp6.getText());
                TextView villNm6 = (layout.findViewById(R.id.villName));
                villNm6.setText(NUM_9 + villNm6.getText());
                break;

        }
    }


    private void setSectionSeparator(View layout) {
        View section1 = layout.findViewById(R.id.section1);
        TextView locationSection =  section1.findViewById(R.id.sectionHeaderText);
        locationSection.setText("I.Work Location Details");
        View section2 = layout.findViewById(R.id.section2);
        TextView surveyorSection =  section2.findViewById(R.id.sectionHeaderText);
        surveyorSection.setText("Surveyor Details");
    }

    private void initializeSpinner(Spinner spinner, Context context) {
        switch (spinner.getId()) {
            case R.id.disSpin:
                String disV = readPref(DISTRICT_ID);


                districts = db.getDistrictsWithCode();
                ArrayAdapter<LocationWithID> adapter = new ArrayAdapter<>(context, R.layout.spinner_textview, districts);

                ArrayList<String> disIds = new ArrayList<>();


                for (LocationWithID district : districts) {
                    disIds.add(String.valueOf(district.getId()));

                }
                spinner.setAdapter(adapter);
                setPrefValue(spinner, disV, disIds);
                break;

            case R.id.talSpin:
                String talV = readPref(TALUK_ID);
                LocationWithID disName = (LocationWithID) districtNameSpin.getSelectedItem();
                taluks = db.getTaluksWithCode(String.valueOf(disName.getId()));
                ArrayAdapter<LocationWithID> adapter1 = new ArrayAdapter<>(context, R.layout.spinner_textview, taluks);
                ArrayList<String> talIds = new ArrayList<>();

                for (LocationWithID taluk : taluks) {
                    talIds.add(String.valueOf(taluk.getId()));
                }
                spinner.setAdapter(adapter1);
                setPrefValue(spinner, talV, talIds);
                break;
            case R.id.gpSpin:
                String gpV = readPref(GRAM_PANCHAYAT_ID);
                LocationWithID talukName = (LocationWithID) talukNameSpin.getSelectedItem();
                gps = db.getGrampanchayatsWithCode(String.valueOf(talukName.getId()));
                ArrayAdapter<LocationWithID> hobliAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, gps);
                ArrayList<String> gpIds = new ArrayList<>();

                for (LocationWithID gp : gps) {
                    gpIds.add(String.valueOf(gp.getId()));
                }
                spinner.setAdapter(hobliAdapter);
                setPrefValue(spinner, gpV, gpIds);
                break;
            case R.id.villSpin:
                String villV = readPref(VILLAGE_ID);
                LocationWithID gpName = (LocationWithID) gpSpin.getSelectedItem();
                villages = db.getVillagesWithCode(String.valueOf(gpName.getId()));
                ArrayAdapter<LocationWithID> villageAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, villages);
                ArrayList<String> villIds = new ArrayList<>();

                for (LocationWithID vill : villages) {
                    villIds.add(String.valueOf(vill.getId()));
                }
                spinner.setAdapter(villageAdapter);
                setPrefValue(spinner, villV, villIds);
                break;
            case R.id.consSpin:
                String consV = readPref(CONSTITUENCY_ID);
                /*In otherworks there is no district id to map constituency*/
                if (!prefKey.equals(OtherSurvey.OTHER_SURVEY)) {
                    LocationWithID disNameCons = (LocationWithID) districtNameSpin.getSelectedItem();
                    constituency = db.getConstiuencyWithCode(String.valueOf(disNameCons.getId()));
                } else
                    constituency = db.getConstiuencyWithCode();
                ArrayAdapter<LocationWithID> consAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, constituency);
                ArrayList<String> consIds = new ArrayList<>();
                for (LocationWithID cons: constituency){
                    consIds.add(String.valueOf(cons.getId()));
                }
                spinner.setAdapter(consAdapter);
                setPrefValue(spinner, consV, consIds);
                break;
            case R.id.cirSpin:
                String cirV = readPref(CIRCLE_ID);
                circles = db.getCirclesWithCode();
                ArrayAdapter<LocationWithID> adapter2 = new ArrayAdapter<>(context, R.layout.spinner_textview, circles);
                ArrayList<String> cirIds = new ArrayList<>();
                for (LocationWithID circ : circles) {
                    cirIds.add(String.valueOf(circ.getId()));
                }
                spinner.setAdapter(adapter2);
                setPrefValue(spinner, cirV, cirIds);
                break;
            case R.id.divSpin:
                String divV = readPref(DIVISION_ID);
                LocationWithID circName = (LocationWithID) circleNameSpin.getSelectedItem();
                divisions = db.getDivisionsWithCode(String.valueOf(circName.getId()));
                ArrayAdapter<LocationWithID> adapter3 = new ArrayAdapter<>(context, R.layout.spinner_textview, divisions);
                ArrayList<String> divIds = new ArrayList<>();
                for (LocationWithID div : divisions) {
                    divIds.add(String.valueOf(div.getId()));
                }
                spinner.setAdapter(adapter3);
                setPrefValue(spinner, divV, divIds);
                break;
            case R.id.subDivSpin:
                String sDivV = readPref(SUBDIVISION_ID);
                LocationWithID divname = (LocationWithID) divNameSpin.getSelectedItem();
                subdivision = db.getSubDivisionsWithCode(String.valueOf(divname.getId()));
                ArrayAdapter<LocationWithID> adapter4 = new ArrayAdapter<>(context, R.layout.spinner_textview, subdivision);
                ArrayList<String> subdivIds = new ArrayList<>();
                for (LocationWithID subdiv : subdivision) {
                    subdivIds.add(String.valueOf(subdiv.getId()));
                }
                spinner.setAdapter(adapter4);
                setPrefValue(spinner, sDivV, subdivIds);
                break;
            case R.id.rangeSpin:
                String rangeV = readPref(RANGE_ID);
                LocationWithID subdivName = (LocationWithID) subDivSpin.getSelectedItem();
                ranges = db.getRangesWithCode(String.valueOf(subdivName.getId()));
                ArrayAdapter<LocationWithID> adapter5 = new ArrayAdapter<>(context, R.layout.spinner_textview, ranges);
                ArrayList<String> rangeids = new ArrayList<>();
                for (LocationWithID range : ranges) {
                    rangeids.add(String.valueOf(range.getId()));
                }
                spinner.setAdapter(adapter5);
                setPrefValue(spinner, rangeV, rangeids);
                break;
            case R.id.expertNameSpin:
                String expert = readPref(INVESTIGATOR);
                expert = expert == null ? "" : expert;
                int index = experts.indexOf(expert);
                expertNameSpin.setSelection(index);
                break;
        }

        spinner.postDelayed(() -> {
            spinner.setOnItemSelectedListener(this);
        }, 2000);

    }

    private void setPrefValue(Spinner spinner, String valueofid, ArrayList<String> listofid) {
        if (!valueofid.startsWith("Select")) {
            int index = listofid.indexOf(valueofid);
                if (index != -1) {
                spinner.setSelection(index, false);
                switch (spinner.getId()) {
                    case R.id.talSpin:
                        isTalukSet = true;
                        break;
                    case R.id.consSpin:
                        isConsSet = true;
                        break;
                    case R.id.cirSpin:
                        isCircleSet = true;
                        break;
                    case R.id.divSpin:
                        isDivSet = true;
                        break;
                    case R.id.subDivSpin:
                        isSubDivSet = true;
                        break;
                    case R.id.rangeSpin:
                        isRangeSet = true;
                        break;
                }
            } else {
                spinner.setSelection(0);
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (position != 0) {
            switch (spinner.getId()) {
                case R.id.disSpin:
                    LocationWithID disValue = (LocationWithID) districtNameSpin.getSelectedItem();
                    writePref(DISTRICT, String.valueOf(disValue.getName()));
                    writePref(DISTRICT_ID, String.valueOf(disValue.getId()));
                    constituencyNameSpin.setEnabled(true);
                    talukNameSpin.setEnabled(true);
                    break;

                case R.id.talSpin:
                    LocationWithID talValue = (LocationWithID) talukNameSpin.getSelectedItem();
                    writePref(TALUK, talValue.getName());
                    writePref(TALUK_ID, String.valueOf(talValue.getId()));
                    gpSpin.setEnabled(true);
                    circleNameSpin.setEnabled(true);
                    break;

                case R.id.gpSpin:
                    LocationWithID gpValue = (LocationWithID) gpSpin.getSelectedItem();
                    writePref(GRAM_PANCHAYAT_NAME, gpValue.getName());
                    writePref(GRAM_PANCHAYAT_ID, String.valueOf(gpValue.getId()));
                    villSpin.setEnabled(true);
                    break;

                case R.id.villSpin:
                    LocationWithID villValue = (LocationWithID) villSpin.getSelectedItem();
                    writePref(VILLAGE_NAME, villValue.getName());
                    writePref(VILLAGE_ID, String.valueOf(villValue.getId()));
                    break;

                case R.id.consSpin:
                    LocationWithID consValue = (LocationWithID) constituencyNameSpin.getSelectedItem();
                    writePref(CONSTITUENCY_NAME, consValue.getName());
                    writePref(CONSTITUENCY_ID, String.valueOf(consValue.getId()));
                    talukNameSpin.setEnabled(true);
                    break;
                case R.id.cirSpin:
                    LocationWithID circValue = (LocationWithID) circleNameSpin.getSelectedItem();
                    writePref(CIRCLE, circValue.getName());
                    writePref(CIRCLE_ID, String.valueOf(circValue.getId()));
                    divNameSpin.setEnabled(true);
                    break;
                case R.id.divSpin:
                    LocationWithID divValue = (LocationWithID) divNameSpin.getSelectedItem();
                    writePref(DIVISION, divValue.getName());
                    writePref(DIVISION_ID, String.valueOf(divValue.getId()));
                    subDivSpin.setEnabled(true);
                    break;
                case R.id.subDivSpin:
                    LocationWithID subDivValue = (LocationWithID) subDivSpin.getSelectedItem();
                    writePref(SUBDIVISION, subDivValue.getName());
                    writePref(SUBDIVISION_ID, String.valueOf(subDivValue.getId()));
                    rangeSpin.setEnabled(true);
                    break;
                case R.id.rangeSpin:
                    LocationWithID rangeValue = (LocationWithID) rangeSpin.getSelectedItem();
                    writePref(RANGE, rangeValue.getName());
                    writePref(RANGE_ID, String.valueOf(rangeValue.getId()));
                    break;
                case R.id.expertNameSpin:
                    writePref(INVESTIGATOR, (String) expertNameSpin.getSelectedItem());
                    break;
            }
            resetSpinnerAdapter(position, context, spinner);
        }

        if (position == 0) {
            switch (spinner.getId()) {
                case R.id.disSpin:
                    discardPref(0, 1);
                    disableSpinners(0, 1);
                    break;
                case R.id.cirSpin:
                    discardPref(2);
                    disableSpinners(2);
                    break;
                case R.id.divSpin:
                    discardPref(3);
                    disableSpinners(3);
                    break;
                case R.id.subDivSpin:
                    discardPref(4);
                    disableSpinners(4);
                    break;
            }
        }

    }

    private void discardPref(int index) {

        for (int i = index; i < prefKeys.length; i++) {
            sharedPreferences.edit().remove(prefKeys[i]).apply();
        }
    }

    private void discardPref(int fromIndex, int toIndex) {
        if (!(toIndex <= prefKeys.length - 1)) {
            return;
        }
        for (int i = fromIndex; i < toIndex; i++) {
            sharedPreferences.edit().remove(prefKeys[i]).apply();
        }
    }


    private void disableSpinners(int fromIndex) {
        for (int i = fromIndex; i < spinners.length; i++) {
            spinners[i].setSelection(0);
            spinners[i].setEnabled(false);
        }
    }

    private void disableSpinners(int fromIndex, int toIndex) {
        if (!(toIndex <= spinners.length - 1)) {
            return;
        }
        for (int i = fromIndex; i < toIndex; i++) {
            spinners[i].setSelection(0);
            spinners[i].setEnabled(false);
        }
    }

    private void resetSpinnerAdapter(int position, Context context, Spinner spinner) {

        if (position != 0) {
            String tag = (String) spinner.getTag();
            switch (tag) {
                case DISTRICT:
                    LocationWithID disId = districts.get(position);
                    constituency = db.getConstiuencyWithCode(String.valueOf(disId.getId()));
                    ArrayAdapter<LocationWithID> consAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, constituency);
                    constituencyNameSpin.setAdapter(consAdapter);
                    taluks = db.getTaluksWithCode(String.valueOf(disId.getId()));
                    ArrayAdapter<LocationWithID> disAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, taluks);
                    talukNameSpin.setAdapter(disAdapter);
                    break;
                case TALUK:
                    LocationWithID talukId = taluks.get(position);
                    gps = db.getGrampanchayatsWithCode(String.valueOf(talukId.getId()));
                    ArrayAdapter<LocationWithID> gpAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, gps);
                    gpSpin.setAdapter(gpAdapter);
                    break;

                case GRAM_PANCHAYAT_NAME:
                    LocationWithID gpId = (LocationWithID) gpSpin.getSelectedItem();
                    villages = db.getVillagesWithCode(String.valueOf(gpId.getId()));
                    ArrayAdapter<LocationWithID> villageAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, villages);
                    villSpin.setAdapter(villageAdapter);
                    break;

                case CONSTITUENCY_NAME:
                  /*  taluks = db.getTaluksNew(districts.get(position));
                    ArrayAdapter<String> disAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, taluks);
                    talukNameSpin.setAdapter(disAdapter);*/
                    break;
                case CIRCLE:
                    LocationWithID circId = (LocationWithID) circleNameSpin.getSelectedItem();
                    divisions = db.getDivisionsWithCode(String.valueOf(circId.getId()));
                    ArrayAdapter<LocationWithID> divAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, divisions);
                    divNameSpin.setAdapter(divAdapter);
                    break;
                case DIVISION:
                    LocationWithID divId = (LocationWithID) divNameSpin.getSelectedItem();
                    subdivision = db.getSubDivisionsWithCode(String.valueOf(divId.getId()));
                    ArrayAdapter<LocationWithID> subDAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, subdivision);
                    subDivSpin.setAdapter(subDAdapter);
                    break;
                case SUBDIVISION:
                    LocationWithID subdivId = (LocationWithID) subDivSpin.getSelectedItem();
                    ranges = db.getRangesWithCode(String.valueOf(subdivId.getId()));
                    ArrayAdapter<LocationWithID> rangeAdapter = new ArrayAdapter<>(context, R.layout.spinner_textview, ranges);
                    rangeSpin.setAdapter(rangeAdapter);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void writePref(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    private String readPref(String key) {
        return sharedPreferences.getString(key, "");
    }

    class ConfigTextWatcher implements TextWatcher {

        private View view;

        ConfigTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = String.valueOf(s);
            if (view == sNameET) {
                writePref(SUR_NAME, text);
            } else if (view == sampleNoET) {
                writePref(SAMPLE_NUMBER, text);
            } else if (view == sAsstNameET) {
                writePref(SUR_ASST_NAME, text);
            } else if (view == staffNameET) {
                writePref(STAFF_NAME, text);
            } else if (view == staffDesgET) {
                writePref(STAFF_DESG_NAME, text);
            } else if (view == localityNameET) {
                writePref(LOCALITY_NAME, text);
            } else if (view == hamletNameET) {
                writePref(HAMLET_NAME, text);
            } else if (view == forestNameET) {
                writePref(FOREST_NAME, text);
            }
        }
    }
}
