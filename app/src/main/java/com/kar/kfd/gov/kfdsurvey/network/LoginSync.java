package com.kar.kfd.gov.kfdsurvey.network;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.LoginActivity;
import com.kar.kfd.gov.kfdsurvey.SurveyActivity;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey;
import com.kar.kfd.gov.kfdsurvey.utils.Analytics;
import com.kar.kfd.gov.kfdsurvey.utils.AppExecutors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginSync {
    private static final String SERVER_KEY = "Login";
    private final Database db;
    ArrayList<String> formIds = new ArrayList<>();
    SharedPreferences preferences;
    ProgressListener progressListener;
    private Context context;
    private String userName, password, item;
    private int no_of_plot_laid;

    public LoginSync(Context context, ProgressListener progressListener) {
        this.context = context;
        db = Database.initializeDB(context);
        preferences = context.getSharedPreferences(Constants.LOGIN_PREF, Context.MODE_PRIVATE);
        this.progressListener = progressListener;
    }

    public void sendData(ArrayList<String> data) throws JSONException {

        if (data == null) {
            Toast.makeText(context, "Login Details are Empty unable to Sync", Toast.LENGTH_SHORT).show();
            return;
        }
        progressListener.show();
        String url = Constants.SERVER_URL + SERVER_KEY;
        JSONObject headersjsonObject = new JSONObject();
        JSONObject userCreJson = new JSONObject();
        userCreJson.put("UserName", data.get(0));
        userCreJson.put("Password", data.get(1));
        userCreJson.put("Year", data.get(2));
        headersjsonObject.put("headers", userCreJson);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, headersjsonObject, response -> {

            try {
                Log.e("saczcac", "" + response.toString());
                if (response.has("error")) {
                    String error = response.getString("error");
                    Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                    progressListener.hide();
                } else {
                    formIds = db.getFormIds();
                    userName = data.get(0);
                    password = data.get(1);
                    item = data.get(2);
                    /*to store in Scp TSP*/
                    preferences.edit().putString(Database.EVALUATOR_NAME, userName).apply();
                    preferences.edit().putString(Database.EVALUATOR_PASSWORD, password).apply();
                    preferences.edit().putString(Database.EVALUATION_YEAR, item).apply();

                    ContentValues cvLoginDetails = new ContentValues();
                    cvLoginDetails.put(Database.EVALUATOR_NAME, userName);
                    cvLoginDetails.put(Database.EVALUATOR_PASSWORD, password);
                    cvLoginDetails.put(Database.EVALUATION_YEAR, item);
                    db.insertLogin(cvLoginDetails);

                    JSONArray samplings = response.getJSONArray("Samplings");
                    for (int i = 0; i < samplings.length(); i++) {

                        JSONObject samplingsObject = samplings.getJSONObject(i);

                        JSONArray plantingAcitivity = samplingsObject.getJSONArray("PlantActivity");
                        JSONArray boundary = samplingsObject.getJSONArray("Boundary");
                        JSONArray smc = samplingsObject.getJSONArray("Smc");
                        JSONArray species = samplingsObject.getJSONArray("Species");
                        JSONArray dibbledSpecies = samplingsObject.getJSONArray("Dibbled_species");
                        JSONArray sdp = samplingsObject.getJSONArray("SDP");
                        JSONArray sdpBenificiaryList = samplingsObject.getJSONArray("SDPlist");
                        JSONArray scpTsp = samplingsObject.getJSONArray("SCP_TSP");
                        JSONArray otherWorks = samplingsObject.getJSONArray("Otherworks");
                        JSONArray scp_tsp_indvidual = samplingsObject.getJSONArray("SCP_TSP_Indvidual");
                        JSONArray scp_tsp_community = samplingsObject.getJSONArray("SCP_TSP_Community");
                        JSONArray scp_tsp_indvlist = samplingsObject.getJSONArray("SCP_TSP_Indvlist");
                        JSONArray advanceWork = samplingsObject.getJSONArray("Advancework");
                        JSONArray advanceWorkBoundary = samplingsObject.getJSONArray("AdvworkBoundary");
                        JSONArray advaceWorkSmc = samplingsObject.getJSONArray("AdvworkSmc");
                        JSONArray nurseryWork = samplingsObject.getJSONArray("Nursery");
                        JSONArray nurserySpecies = samplingsObject.getJSONArray("NurserySpecies");

                        AppExecutors.getInstance().diskIO().execute(() -> {
                            savePlantingForm(plantingAcitivity);
                            saveSmc(smc);
                            saveBoundary(boundary);
                            saveSpecies(species);
                            saveDibbledSpecies(dibbledSpecies);
                            saveSDP(sdp);
                            saveSDPBenificiary(sdpBenificiaryList);
                            saveOtherWorks(otherWorks);
                            saveSCPTSP(scpTsp);
                            saveBenefit_Individual(scp_tsp_indvidual);
                            saveBenefit_Community(scp_tsp_community);
                            saveSCPTSPBeneficiaryList(scp_tsp_indvlist);
                            saveAdvanceWork(advanceWork);
                            saveAdvworkBoundary(advanceWorkBoundary);
                            saveAdvworkSmc(advaceWorkSmc);
                            saveNursery(nurseryWork);
                            saveNurserySpecies(nurserySpecies);
                            AppExecutors.getInstance().mainThread().execute(() -> {
                                if (context instanceof LoginActivity) {
                                    context.startActivity(new Intent(context, SurveyActivity.class));
                                    ((Activity) context).finish();
                                    Analytics.track(Analytics.AnalyticsEvents.Login);
                                } else {
                                    Toast.makeText(context, "Forms Synced", Toast.LENGTH_SHORT).show();
                                }
                                progressListener.hide();

                            });

                        });


                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context,
                        "Server Error ",
                        Toast.LENGTH_LONG).show();
                progressListener.hide();
            }

        }, error -> {

            Toast.makeText(context,
                    " Server Error", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
            // hide the progress dialog
            progressListener.hide();
        });

        VolleyNetworkUtils.getInstance().addToRequestQueue(jsonObjReq);
        //queue.add(postRequest);
    }


    private void savePlantingForm(JSONArray plantingAcitivity) {

        for (int j = 0; j < plantingAcitivity.length(); j++) {

            try {
                JSONObject plantActivityJson = plantingAcitivity.getJSONObject(j);
                String work_code = plantActivityJson.getString("work_code");
                String Uploadstatus = plantActivityJson.getString("Uploadstatus");
                String Evaltype = plantActivityJson.getString("Evaltype");
                String Evaltitle = plantActivityJson.getString("Evaltitle");
                String Eval_year = plantActivityJson.getString("Eval_year");
                String userlevel = plantActivityJson.getString("userlevel");
                String OfficeID = plantActivityJson.getString("OfficeID");
                String apo = plantActivityJson.getString("apo");
                String apo_date = plantActivityJson.getString("apo_date");
                String work_estimates = plantActivityJson.getString("work_estimates");
                String no_of_work_estimates = plantActivityJson.getString("no_of_work_estimates");
                String fnb = plantActivityJson.getString("fnb");
                String plantation_journal = plantActivityJson.getString("plantation_journal");
                String circle_name = plantActivityJson.getString("circle_name");
                String CIRCLE_ID = plantActivityJson.getString("CIRCLE_ID");
                String division_name = plantActivityJson.getString("division_name");
                String DIV_ID = plantActivityJson.getString("DIV_ID");
                String subdivision_name = plantActivityJson.getString("subdivision_name");
                String SUBDIV_ID = plantActivityJson.getString("SUBDIV_ID");
                String range_name = plantActivityJson.getString("range_name");
                String RANGE_ID = plantActivityJson.getString("RANGE_ID");
                String district_name = plantActivityJson.getString("district_name");
                String DISTRICT_CODE = plantActivityJson.getString("DISTRICT_CODE");
                String constituency_name = plantActivityJson.getString("constituency_name");
                String CONSTITUENCY_ID = plantActivityJson.getString("LA_ID");
                String form_id = plantActivityJson.getString("form_id");
                String taluk_name = plantActivityJson.getString("taluk_name");
                String TALUK_CODE = plantActivityJson.getString("TALUK_CODE");
                String grampanchayat_name = plantActivityJson.getString("grampanchayat_name");
                String PANCHAYAT_CODE = plantActivityJson.getString("PANCHAYAT_CODE");
                String village_name = plantActivityJson.getString("village_name");
                String VILLAGE_CODE = plantActivityJson.getString("VILLAGE_CODE");
                String gps_latitude = plantActivityJson.getString("gps_latitude");
                String gps_longitude = plantActivityJson.getString("gps_longitude");
                String rf_name = plantActivityJson.getString("rf_name");
                String plantation_name = plantActivityJson.getString("plantation_name");
                String legal_status_of_land = plantActivityJson.getString("legal_status_of_land");
                String legal_status_of_land_other_details = plantActivityJson.getString("legal_status_of_land_other_details");
                String average_annual_rainfall_mm = plantActivityJson.getString("average_annual_rainfall_mm");
                String year_of_planting = plantActivityJson.getString("year_of_planting");
                String gross_plantation_area_ha = plantActivityJson.getString("gross_plantation_area_ha");
                String net_plantation_area_ha = plantActivityJson.getString("net_plantation_area_ha");
                String total_no_of_sample_plots_laid = plantActivityJson.getString("total_no_of_sample_plots_laid");
                String scheme_id = plantActivityJson.getString("scheme_id");
                String scheme_name = plantActivityJson.getString("scheme_name");
                String plantation_model = plantActivityJson.getString("plantation_model");
                String plantation_model_id = plantActivityJson.getString("plantation_model_id");
                String type_of_earth_work_done = plantActivityJson.getString("type_of_earth_work_done");
                String type_of_earth_work_done_otherdetails = plantActivityJson.getString("type_of_earth_work_done_otherdetails");
                String Pit_size = plantActivityJson.getString("Pit_size");
                String Pit_espacement = plantActivityJson.getString("Pit_espacement");
                String Trench_size = plantActivityJson.getString("Trench_size");
                String Trench_espacement = plantActivityJson.getString("Trench_espacement");
                String Pit_in_Pit_size = plantActivityJson.getString("Pit_in_Pit_size");
                String Pit_in_Pit_espacement = plantActivityJson.getString("Pit_in_Pit_espacement");
                String Ripping_size = plantActivityJson.getString("Ripping_size");
                String Ripping_espacement = plantActivityJson.getString("Ripping_espacement");
                String Others_size = plantActivityJson.getString("Others_size");
                String Others_espacement = plantActivityJson.getString("Others_espacement");
                String planting_density_ha = plantActivityJson.getString("planting_density_ha");
                String no_of_years_maintained = plantActivityJson.getString("no_of_years_maintained");
                String no_of_watchers_provided = plantActivityJson.getString("no_of_watchers_provided");
                String replacement = plantActivityJson.getString("replacement");
                String replacement_year = plantActivityJson.getString("replacement_year");
                String replacement_pbsize = plantActivityJson.getString("replacement_pbsize");
                String no_of_replaced_seedlings = plantActivityJson.getString("no_of_replaced_seedlings");
                String plantation_totexp_earthwork = plantActivityJson.getString("plantation_totexp_earthwork");
                String plantation_totexp_raisingseedling = plantActivityJson.getString("plantation_totexp_raisingseedling");
                String plantation_totexp_raisingplants = plantActivityJson.getString("plantation_totexp_raisingplants");
                String plantation_totexp_mntnce_year1 = plantActivityJson.getString("plantation_totexp_mntnce_year1");
                String plantation_totexp_mntnce_year2 = plantActivityJson.getString("plantation_totexp_mntnce_year2");
                String plantation_totexp_mntnce_year3 = plantActivityJson.getString("plantation_totexp_mntnce_year3");
                String plantation_totexp_mntnce_year4 = plantActivityJson.getString("plantation_totexp_mntnce_year4");
                String plantation_totexp_mntnce_year5 = plantActivityJson.getString("plantation_totexp_mntnce_year5");
                String plantation_totexp_mntnce_year6 = plantActivityJson.getString("plantation_totexp_mntnce_year6");
                String plantation_totexp_mntnce_year7 = plantActivityJson.getString("plantation_totexp_mntnce_year7");
                String plantation_totexp_mntnce_year8 = plantActivityJson.getString("plantation_totexp_mntnce_year8");
                String plantation_totexp_mntnce_total = plantActivityJson.getString("plantation_totexp_mntnce_total");
                String plantation_sanctn_date_for_earthwork = plantActivityJson.getString("plantation_sanctn_date_for_earthwork");
                String plantation_sanctn_date_for_raisingseedling = plantActivityJson.getString("plantation_sanctn_date_for_raisingseedling");
                String plantation_sanctn_date_for_raisingplants = plantActivityJson.getString("plantation_sanctn_date_for_raisingplants");
                String plantation_sanctn_date_for_mntnce_year1 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year1");
                String plantation_sanctn_date_for_mntnce_year2 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year2");
                String plantation_sanctn_date_for_mntnce_year3 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year3");
                String plantation_sanctn_date_for_mntnce_year4 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year4");
                String plantation_sanctn_date_for_mntnce_year5 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year5");
                String plantation_sanctn_date_for_mntnce_year6 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year6");
                String plantation_sanctn_date_for_mntnce_year7 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year7");
                String plantation_sanctn_date_for_mntnce_year8 = plantActivityJson.getString("plantation_sanctn_date_for_mntnce_year8");
                String soil_type = plantActivityJson.getString("soil_type");
                String was_the_site_previously_planted = plantActivityJson.getString("was_the_site_previously_planted");
                String year_of_previous_planting = plantActivityJson.getString("year_of_previous_planting");
                String reason_for_replanting = plantActivityJson.getString("reason_for_replanting");
                String was_permission_obtained_for_replanting = plantActivityJson.getString("was_permission_obtained_for_replanting");
                String details_of_permission_for_replanting = plantActivityJson.getString("details_of_permission_for_replanting");
                String plantation_operations_asper_prescription = plantActivityJson.getString("plantation_operations_asper_prescription");
                String is_work_done_documentated_properly_in_plantation_journal = plantActivityJson.getString("is_work_done_documentated_properly_in_plantation_journal");
                String any_snr_officer_inspect_plantation_and_entries_in_journal = plantActivityJson.getString("any_snr_officer_inspect_plantation_and_entries_in_journal");
                String first_snr_officer_designation = plantActivityJson.getString("first_snr_officer_designation");
                String number_of_inspection_acf = plantActivityJson.getString("number_of_inspection_acf");
                String number_of_inspection_dcf = plantActivityJson.getString("number_of_inspection_dcf");
                String number_of_inspection_cf = plantActivityJson.getString("number_of_inspection_cf");
                String number_of_inspection_ccf = plantActivityJson.getString("number_of_inspection_ccf");
                String number_of_inspection_apccf = plantActivityJson.getString("number_of_inspection_apccf");
                String number_of_inspection_pccf = plantActivityJson.getString("number_of_inspection_pccf");
                String plantation_totexp_boundprotect = plantActivityJson.getString("plantation_totexp_boundprotect");
                String no_of_species_planted = plantActivityJson.getString("no_of_species_planted");
                String is_smc_present = plantActivityJson.getString("is_smc_present");
//                String smc_work_expenditure_as_per_norm = plantActivityJson.getString("smc_work_expenditure_as_per_norm");
                String total_budget_for_smc_work = plantActivityJson.getString("total_budget_for_smc_work");
                String is_vfc_involved_in_plantation_activity = plantActivityJson.getString("is_vfc_involved_in_plantation_activity");
                String name_of_the_vfc = plantActivityJson.getString("name_of_the_vfc");
                String Is_plantation_raised_under_JFPM_area = plantActivityJson.getString("Is_plantation_raised_under_JFPM_area");
                String does_plantation_has_multiple_blocks = plantActivityJson.getString("does_plantation_has_multiple_blocks");
                String No_of_blocks = plantActivityJson.getString("No_of_blocks");
                String plantationtype = plantActivityJson.getString("plantationtype");
                String block1_area = plantActivityJson.getString("block1_area");
                String block1_type = plantActivityJson.getString("block1_type");
                String block2_area = plantActivityJson.getString("block2_area");
                String block2_type = plantActivityJson.getString("block2_type");
                String block3_area = plantActivityJson.getString("block3_area");
                String block3_type = plantActivityJson.getString("block3_type");
                String block4_area = plantActivityJson.getString("block4_area");
                String block4_type = plantActivityJson.getString("block4_type");
                String block5_area = plantActivityJson.getString("block5_area");
                String block5_type = plantActivityJson.getString("block5_type");

                String block1_plots, block2_plots, block3_plots, block4_plots, block5_plots;
                if (!plantActivityJson.getString("block1_plots").equalsIgnoreCase("")) {
                    block1_plots = plantActivityJson.getString("block1_plots");
                } else {
                    block1_plots = "0";
                }
                if (!plantActivityJson.getString("block2_plots").equalsIgnoreCase("")) {
                    block2_plots = plantActivityJson.getString("block2_plots");
                } else {
                    block2_plots = "0";
                }
                if (!plantActivityJson.getString("block3_plots").equalsIgnoreCase("")) {
                    block3_plots = plantActivityJson.getString("block3_plots");
                } else {
                    block3_plots = "0";
                }
                if (!plantActivityJson.getString("block4_plots").equalsIgnoreCase("")) {
                    block4_plots = plantActivityJson.getString("block4_plots");
                } else {
                    block4_plots = "0";
                }
                if (!plantActivityJson.getString("block5_plots").equalsIgnoreCase("")) {
                    block5_plots = plantActivityJson.getString("block5_plots");
                } else {
                    block5_plots = "0";
                }
                no_of_plot_laid = Integer.parseInt(block1_plots) +
                        Integer.parseInt(block2_plots)
                        + Integer.parseInt(block3_plots) +
                        Integer.parseInt(block4_plots)
                        + Integer.parseInt(block5_plots);

                ContentValues cvMaster = new ContentValues();
                cvMaster.put(Database.SURVEY_ID, form_id);
                cvMaster.put(Database.WORK_CODE, work_code);
                cvMaster.put(Database.EVALUATION_TITLE, Evaltitle);
                cvMaster.put(Database.EVALUATION_YEAR, Eval_year);
                cvMaster.put(Database.USER_LEVEL, userlevel);
                cvMaster.put(Database.OFFICEID, OfficeID);
                cvMaster.put(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING);
                cvMaster.put(Database.FORM_STATUS, 0);
                cvMaster.put(Database.PHOTO_STATUS, 0);
                cvMaster.put(Database.SURVEYOR_NAME, userName);
                cvMaster.put(Database.APP_ID, 0);
                cvMaster.put(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
                cvMaster.put(Database.ENDING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));


                ContentValues cv = new ContentValues();
                cv.put(Database.FORM_ID, form_id);
                cv.put(Database.CIRCLE_NAME, circle_name.trim());
                cv.put(Database.CIRCLE_ID, CIRCLE_ID.trim());
                cv.put(Database.DIVISION_NAME, division_name.trim());
                cv.put(Database.DIVISION_CODE, DIV_ID.trim());
                cv.put(Database.SUBDIVISION_NAME, subdivision_name.trim());
                cv.put(Database.SUBDIVISION_CODE, SUBDIV_ID.trim());
                cv.put(Database.RANGE_NAME, range_name.trim());
                cv.put(Database.RANGE_CODE, RANGE_ID.trim());
                cv.put(Database.DISTRICT_NAME, district_name.trim());
                cv.put(Database.DISTRICT_CODE, DISTRICT_CODE.trim());
                cv.put(Database.CONSTITUENCY_NAME, constituency_name.trim());
                cv.put(Database.CONSTITUENCY_ID, CONSTITUENCY_ID.trim());
                cv.put(Database.TALUK_NAME, taluk_name.trim());
                cv.put(Database.TALUK_CODE, TALUK_CODE.trim());
                cv.put(Database.VILLAGE_NAME, village_name.trim());
                cv.put(Database.VILLAGE_CODE, VILLAGE_CODE.trim());
                cv.put(Database.GRAMA_PANCHAYAT_NAME, grampanchayat_name);
                cv.put(Database.PANCHAYAT_CODE, PANCHAYAT_CODE.trim());
                cv.put(Database.LEGAL_STATUS_OF_LAND, legal_status_of_land);
                cv.put(Database.RF_NAME, rf_name);
                cv.put(Database.PLANTATION_NAME, plantation_name);
                cv.put(Database.WORK_CODE, work_code);
                cv.put(Database.APO, apo);
                cv.put(Database.APO_DATE, apo_date);
                cv.put(Database.WORK_ESTIMATES, work_estimates);
                cv.put(Database.NO_OF_WORK_ESTIMATES, no_of_work_estimates);
                cv.put(Database.FNB, fnb);
                cv.put(Database.PLANTATION_JOURNAL, plantation_journal);
                cv.put(Database.YEAR_OF_PLANTING, year_of_planting);
                cv.put(Database.GROSS_PLANTATION_AREA_HA, gross_plantation_area_ha);
                cv.put(Database.NET_PLANTATION_AREA_HA, net_plantation_area_ha);
                Log.e("sdcasd", "" + total_no_of_sample_plots_laid);
                if (no_of_plot_laid == 0)
                    cv.put(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, total_no_of_sample_plots_laid);
                else
                    cv.put(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, no_of_plot_laid);
                cv.put(Database.SCHEME_ID, scheme_id);
                cv.put(Database.SCHEME_NAME, scheme_name);
                cv.put(Database.PLANTATION_MODEL, plantation_model);
                cv.put(Database.PLANTATION_MODEL_ID, plantation_model_id);
                cv.put(Database.TYPE_OF_EARTH_WORK_DONE, type_of_earth_work_done);
                cv.put(Database.TYPE_OF_EARTH_WORK_DONE_OTHERDETAILS, type_of_earth_work_done_otherdetails);
                cv.put(Database.PITSIZE, Pit_size);
                cv.put(Database.PIT_ESPACEMENT, Pit_espacement);
                cv.put(Database.TRENCH_SIZE, Trench_size);
                cv.put(Database.TRENCH_ESPACEMENT, Trench_espacement);
                cv.put(Database.PIT_IN_PIT_SIZE, Pit_in_Pit_size);
                cv.put(Database.PIT_IN_PIT_ESPACEMENT, Pit_in_Pit_espacement);
                cv.put(Database.RIPPING_SIZE, Ripping_size);
                cv.put(Database.RIPPING_ESPACEMENT, Ripping_espacement);
                cv.put(Database.OTHERS_SIZE, Others_size);
                cv.put(Database.OTHERS_ESPACEMENT, Others_espacement);
                cv.put(Database.AVERAGE_ANNUAL_RAINFALL_MM, average_annual_rainfall_mm);
                cv.put(Database.SOIL_TYPE, soil_type);
                cv.put(Database.NO_OF_WATCHERS_PROVIDED, no_of_watchers_provided);
                cv.put(Database.NO_OF_YEARS_MAINTAINED, no_of_years_maintained);
                cv.put(Database.IS_CASUALTY_REPLACEMENT_DONE, replacement);
                cv.put(Database.YEAR_OF_CASUALTY_REPLACEMENT, replacement_year);
                cv.put(Database.REPLACEMENT_PBSIZE, replacement_pbsize);
                cv.put(Database.NO_OF_REPLACED_SEEDLINGS, no_of_replaced_seedlings);
                cv.put(Database.PLANTATION_TOTEXP_EARTHWORK, plantation_totexp_earthwork);
                cv.put(Database.PLANTATION_TOTEXP_RAISINGSEEDLING, plantation_totexp_raisingseedling);
                cv.put(Database.PLANTATION_TOTEXP_RAISINGPLANTATION, plantation_totexp_raisingplants);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR1, plantation_totexp_mntnce_year1);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR2, plantation_totexp_mntnce_year2);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR3, plantation_totexp_mntnce_year3);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR4, plantation_totexp_mntnce_year4);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR5, plantation_totexp_mntnce_year5);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR6, plantation_totexp_mntnce_year6);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR7, plantation_totexp_mntnce_year7);
                cv.put(Database.PLANTATION_TOTEXP_MNTNCE_YEAR8, plantation_totexp_mntnce_year8);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_EARTHWORK, plantation_sanctn_date_for_earthwork);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_RAISINGSEEDLING, plantation_sanctn_date_for_raisingseedling);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_RAISINGPLANTS, plantation_sanctn_date_for_raisingplants);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR1, plantation_sanctn_date_for_mntnce_year1);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR2, plantation_sanctn_date_for_mntnce_year2);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR3, plantation_sanctn_date_for_mntnce_year3);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR4, plantation_sanctn_date_for_mntnce_year4);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR5, plantation_sanctn_date_for_mntnce_year5);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR6, plantation_sanctn_date_for_mntnce_year6);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR7, plantation_sanctn_date_for_mntnce_year7);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR8, plantation_sanctn_date_for_mntnce_year8);

                //   cv.put(Database.PLANTATION_TOTEXP_MNTNCE_TOTAL, plantation_totexp_mntnce_total);
                cv.put(Database.PLANTING_DENSITY_HA, planting_density_ha);
                cv.put(Database.WAS_THE_SITE_PREVIOUSLY_PLANTED, was_the_site_previously_planted);
                cv.put(Database.YEAR_OF_PREVIOUS_PLANTING, year_of_previous_planting);
                cv.put(Database.REASON_FOR_REPLANTING, reason_for_replanting);
                cv.put(Database.WAS_PERMISSION_OBTAINED_FOR_REPLANTING, was_permission_obtained_for_replanting);
                cv.put(Database.DETAILS_OF_PERMISSION_FOR_REPLANTING, details_of_permission_for_replanting);
                cv.put(Database.PLANTATION_OPERATIONS_ASPER_PRESCRIPTION, plantation_operations_asper_prescription);
                cv.put(Database.IS_WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL, is_work_done_documentated_properly_in_plantation_journal);
                cv.put(Database.ANY_SNR_OFFICER_INSPECT_PLANTATION_AND_ENTRIES_IN_JOURNAL, any_snr_officer_inspect_plantation_and_entries_in_journal);
                cv.put(Database.FIRST_SNR_OFFICER_DESIGNATION, first_snr_officer_designation);
                cv.put(Database.NUMBER_OF_INSPECTION_ACF, number_of_inspection_acf);
                cv.put(Database.NUMBER_OF_INSPECTION_DCF, number_of_inspection_dcf);
                cv.put(Database.NUMBER_OF_INSPECTION_CF, number_of_inspection_cf);
                cv.put(Database.NUMBER_OF_INSPECTION_CCF, number_of_inspection_ccf);
                cv.put(Database.NUMBER_OF_INSPECTION_APCCF, number_of_inspection_apccf);
                cv.put(Database.NUMBER_OF_INSPECTION_PCCF, number_of_inspection_pccf);
                cv.put(Database.LEGAL_STATUS_OF_LAND_OTHER_DETAILS, legal_status_of_land_other_details);
                cv.put(Database.GPS_LATITUDE, gps_latitude);
                cv.put(Database.GPS_LONGITUDE, gps_longitude);


                //Devansh
                Log.e("dscsdc", "" + does_plantation_has_multiple_blocks);
                cv.put(Database.DOES_PLANTATION_HAS_MULTIPLE_BLOCK, does_plantation_has_multiple_blocks);
                cv.put(Database.PLANTATION_TYPE, plantationtype);
                cv.put(Database.NO_OF_BLOCK, No_of_blocks);
                cv.put(Database.BLOCK1_AREA, block1_area);
                cv.put(Database.BLOCK1_TYPE, block1_type);
                cv.put(Database.BLOCK2_AREA, block2_area);
                cv.put(Database.BLOCK2_TYPE, block2_type);
                cv.put(Database.BLOCK3_AREA, block3_area);
                cv.put(Database.BLOCK3_TYPE, block3_type);
                cv.put(Database.BLOCK4_AREA, block4_area);
                cv.put(Database.BLOCK4_TYPE, block4_type);
                cv.put(Database.BLOCK5_AREA, block5_area);
                cv.put(Database.BLOCK5_TYPE, block5_type);

                cv.put(Database.BLOCK1_PLOTS, block1_plots);
                cv.put(Database.BLOCK2_PLOTS, block2_plots);
                cv.put(Database.BLOCK3_PLOTS, block3_plots);
                cv.put(Database.BLOCK4_PLOTS, block4_plots);
                cv.put(Database.BLOCK5_PLOTS, block5_plots);


                ContentValues cvVFC = new ContentValues();
                cvVFC.put(Database.FORM_ID, form_id);
                cvVFC.put(Database.WORK_CODE, work_code);
                cvVFC.put(Database.VFC_APPLICABLE, is_vfc_involved_in_plantation_activity);
                cvVFC.put(Database.NAME_OF_THE_VFC, name_of_the_vfc);
                cvVFC.put(Database.IS_JFPM_RAISED, Is_plantation_raised_under_JFPM_area);


                ContentValues cvSMC = new ContentValues();
                cvSMC.put(Database.FORM_ID, form_id);
                cvSMC.put(Database.WORK_CODE, work_code);
                cvSMC.put(Database.SMC_APPLICABLE, is_smc_present);
//                cvSMC.put(Database.SMC_WORK_EXPENDITURE_AS_PER_NORM, smc_work_expenditure_as_per_norm);
                cvSMC.put(Database.TOTAL_BUDGET_FOR_SMC_WORK, total_budget_for_smc_work);

                if (!formIds.contains(form_id) && Uploadstatus.equals("0")) {
                    db.insertIntoMaster(cvMaster);
                    db.insertIntoPlantation(cv);
                    db.insertIntoSmcSamplingMaster(cvSMC);
                    db.insertIntoVfcSampling(cvVFC);

                    cvSMC.remove(Database.WORK_CODE);
                    cvSMC.remove(Database.SMC_APPLICABLE);
//                    cvSMC.remove(Database.SMC_WORK_EXPENDITURE_AS_PER_NORM);
                    cvSMC.remove(Database.TOTAL_BUDGET_FOR_SMC_WORK);
                    int samplePlots = Integer.parseInt(total_no_of_sample_plots_laid);
                    for (int i = 0; i < samplePlots; i++) {
                        db.insertIntoSamplePlot(cvSMC);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSmc(JSONArray smc) {
        for (int j = 0; j < smc.length(); j++) {
            try {
                JSONObject smcJson = smc.getJSONObject(j);
                String form_id = smcJson.getString("form_id");
                String work_code = smcJson.getString("work_code");
                String Uploadstatus = smcJson.getString("Uploadstatus");
                String type_of_structure = smcJson.getString("type_of_structure");
                String smc_structure_length = smcJson.getString("smc_structure_length");
                String smc_structure_breadth = smcJson.getString("smc_structure_breadth");
                String smc_structure_dept = smcJson.getString("smc_structure_dept");
                String smc_structure_cost = smcJson.getString("smc_structure_cost");


                ContentValues cv = new ContentValues();
                cv.put(Database.FORM_ID, form_id);
                cv.put(Database.WORK_CODE, work_code);
                cv.put(Database.TYPE_OF_STRUCTURE, type_of_structure);
                cv.put(Database.SMC_STRUCTURE_LENGTH, smc_structure_length);
                cv.put(Database.SMC_STRUCTURE_BREADTH, smc_structure_breadth);
                cv.put(Database.SMC_STRUCTURE_DEPTH, smc_structure_dept);
                cv.put(Database.SMC_STRUCTURE_COST, smc_structure_cost);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertSMCList(cv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveBoundary(JSONArray boundary) {
        for (int j = 0; j < boundary.length(); j++) {
            try {
                JSONObject boundaryJson = boundary.getJSONObject(j);
                String form_id = boundaryJson.getString("form_id");
                String work_code = boundaryJson.getString("work_code");
                String Uploadstatus = boundaryJson.getString("Uploadstatus");
                String type_of_protection_provided = boundaryJson.getString("type_of_protection_provided");
                String type_of_protection_provided_other = boundaryJson.getString("type_of_protection_provided_other");
                String length = boundaryJson.getString("length");
                String boundary_cost = boundaryJson.getString("boundary_cost");

                ContentValues cv = new ContentValues();
                cv.put(Database.FORM_ID, form_id);
                cv.put(Database.WORK_CODE, work_code);
                cv.put(Database.TYPE_OF_PROTECTION, type_of_protection_provided);
                cv.put(Database.BOUNDARY_COST, boundary_cost);
                cv.put(Database.TOTAL_LENGTH_KMS, length);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertProtection(cv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSpecies(JSONArray species) {
        for (int j = 0; j < species.length(); j++) {
            try {
                JSONObject speciesJson = species.getJSONObject(j);
                String form_id = speciesJson.getString("form_id");
                String work_code = speciesJson.getString("work_code");
                String Uploadstatus = speciesJson.getString("Uploadstatus");
                String main_species_planted = speciesJson.getString("main_species_planted");
                String other_species_name = speciesJson.getString("other_species_name");
                String species_id = speciesJson.getString("species_id");
                String seedlings_pbsize = speciesJson.getString("seedlings_pbsize");
                String no_of_seedlings_per_ha = speciesJson.getString("no_of_seedlings_per_ha");
                String part_type = "Seedling";

                ContentValues cvSpecies = new ContentValues();
                cvSpecies.put(Database.FORM_ID, form_id);
                cvSpecies.put(Database.WORK_CODE, work_code);
                cvSpecies.put(Database.MAIN_SPECIES_PLANTED, main_species_planted);
                cvSpecies.put(Database.OTHER_SPECIES, other_species_name);
                cvSpecies.put(Database.SPECIES_ID, species_id);
                cvSpecies.put(Database.SPECIES_SIZE, seedlings_pbsize);
                cvSpecies.put(Database.TOTAL_SPECIES_COUNT, no_of_seedlings_per_ha);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0")) {
                    db.insertIntoSpeciesInventory(cvSpecies);
                    cvSpecies.put(Database.PART_TYPE, part_type);
                    db.insertIntoSamplePlotSpecies(cvSpecies);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveDibbledSpecies(JSONArray dibbledSpecies) {
        for (int j = 0; j < dibbledSpecies.length(); j++) {

            try {
                JSONObject dibbledSpeciesJson = dibbledSpecies.getJSONObject(j);
                String form_id = dibbledSpeciesJson.getString("form_id");
                String work_code = dibbledSpeciesJson.getString("work_code");
                String Uploadstatus = dibbledSpeciesJson.getString("Uploadstatus");
                String dibbled_species = dibbledSpeciesJson.getString("dibbled_species");
                String dibb_species_id = dibbledSpeciesJson.getString("dibb_species_id");
                String part_type = "Seed Dibbling";

                ContentValues cvdibbledSpecies = new ContentValues();
                cvdibbledSpecies.put(Database.FORM_ID, form_id);
                cvdibbledSpecies.put(Database.WORK_CODE, work_code);
                cvdibbledSpecies.put(Database.PART_TYPE, part_type);
                cvdibbledSpecies.put(Database.MAIN_SPECIES_PLANTED, dibbled_species);
                cvdibbledSpecies.put(Database.SPECIES_ID, dibb_species_id);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertIntoSamplePlotSpecies(cvdibbledSpecies);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSDP(JSONArray sdp) {
        for (int j = 0; j < sdp.length(); j++) {
            try {
                JSONObject sdpJson = sdp.getJSONObject(j);
                String form_id = sdpJson.getString("form_id");
                String work_code = sdpJson.getString("work_code");
                String Uploadstatus = sdpJson.getString("Uploadstatus");
                String Evaltype = sdpJson.getString("Evaltype");
                String Evaltitle = sdpJson.getString("Evaltitle");
                String Eval_year = sdpJson.getString("Eval_year");
                String userlevel = sdpJson.getString("userlevel");
                String OfficeID = sdpJson.getString("OfficeID");
                String circle_name = sdpJson.getString("circle_name");
                String CIRCLE_ID = sdpJson.getString("CIRCLE_ID");
                String division_name = sdpJson.getString("division_name");
                String DIV_ID = sdpJson.getString("DIV_ID");
                String subdivision_name = sdpJson.getString("subdivision_name");
                String SUBDIV_ID = sdpJson.getString("SUBDIV_ID");
                String range_name = sdpJson.getString("range_name");
                String RANGE_ID = sdpJson.getString("RANGE_ID");
                String district_name = sdpJson.getString("district_name");
                String DISTRICT_CODE = sdpJson.getString("DISTRICT_CODE");
                String taluk_name = sdpJson.getString("taluk_name");
                String TALUK_CODE = sdpJson.getString("TALUK_CODE");
                String constituency_name = sdpJson.getString("constituency_name");
                String CONSTITUENCY_ID = sdpJson.getString("LA_ID");
                String grampanchayat_name = sdpJson.getString("grampanchayat_name");
                String PANCHAYAT_CODE = sdpJson.getString("PANCHAYAT_CODE");
                String village_name = sdpJson.getString("village_name");
                String VILLAGE_CODE = sdpJson.getString("VILLAGE_CODE");

                // String beneficiaries_total_count = sdpJson.getString("beneficiaries_total_count");
                /*String seedlings_total_count = sdpJson.getString("seedlings_total_count");
                String villages_total_count = sdpJson.getString("villages_total_count");*/


                ContentValues cvMaster = new ContentValues();
                cvMaster.put(Database.SURVEY_ID, form_id);
                cvMaster.put(Database.WORK_CODE, work_code);
                cvMaster.put(Database.EVALUATION_TITLE, Evaltitle);
                cvMaster.put(Database.EVALUATION_YEAR, Eval_year);
                cvMaster.put(Database.USER_LEVEL, userlevel);
                cvMaster.put(Database.OFFICEID, OfficeID);
                cvMaster.put(Database.FORM_TYPE, Constants.FORMTYPE_SDP);
                cvMaster.put(Database.FORM_STATUS, 0);
                cvMaster.put(Database.PHOTO_STATUS, 0);
                cvMaster.put(Database.SURVEYOR_NAME, userName);
                cvMaster.put(Database.APP_ID, 0);
                cvMaster.put(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
                cvMaster.put(Database.ENDING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));


                ContentValues cvSDP = new ContentValues();
                cvSDP.put(Database.FORM_ID, form_id);
                cvSDP.put(Database.WORK_CODE, work_code);

                cvSDP.put(Database.CIRCLE_NAME, circle_name.trim());
                cvSDP.put(Database.CIRCLE_ID, CIRCLE_ID.trim());
                cvSDP.put(Database.DIVISION_NAME, division_name.trim());
                cvSDP.put(Database.DIVISION_CODE, DIV_ID.trim());
                cvSDP.put(Database.SUBDIVISION_NAME, subdivision_name.trim());
                cvSDP.put(Database.SUBDIVISION_CODE, SUBDIV_ID.trim());
                cvSDP.put(Database.RANGE_NAME, range_name.trim());
                cvSDP.put(Database.RANGE_CODE, RANGE_ID.trim());
                cvSDP.put(Database.DISTRICT_NAME, district_name.trim());
                cvSDP.put(Database.DISTRICT_CODE, DISTRICT_CODE.trim());
                cvSDP.put(Database.TALUK_NAME, taluk_name.trim());
                cvSDP.put(Database.TALUK_CODE, TALUK_CODE.trim());
                cvSDP.put(Database.GRAMA_PANCHAYAT_NAME, grampanchayat_name.trim());
                cvSDP.put(Database.PANCHAYAT_CODE, PANCHAYAT_CODE.trim());
                cvSDP.put(Database.VILLAGE_NAME, village_name.trim());
                cvSDP.put(Database.VILLAGE_CODE, VILLAGE_CODE);
                cvSDP.put(Database.CONSTITUENCY_NAME, constituency_name.trim());
                cvSDP.put(Database.CONSTITUENCY_ID, CONSTITUENCY_ID.trim());

                //  cvSDP.put(Database.BENEFICIARIES_TOTAL_COUNT, beneficiaries_total_count);
                /*cvSDP.put(Database.SEEDLINGS_TOTAL_COUNT, seedlings_total_count);
                cvSDP.put(Database.VILLAGES_TOTAL_COUNT, villages_total_count);*/
                if (!formIds.contains(form_id) && Uploadstatus.equals("0")) {
                    db.insertIntoMaster(cvMaster);
                    db.insertIntoSdpSampling(cvSDP);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSDPBenificiary(JSONArray sdpBenificiaryList) {
        for (int j = 0; j < sdpBenificiaryList.length(); j++) {
            try {
                JSONObject sdpBenJson = sdpBenificiaryList.getJSONObject(j);
                String form_id = sdpBenJson.getString("form_id");
                String Uploadstatus = sdpBenJson.getString("Uploadstatus");
                String name_of_beneficiary = sdpBenJson.getString("Name_of_beneficiary");
                String father_or_husband_name = sdpBenJson.getString("Father_or_husband_name");
                String Aadhar_no = sdpBenJson.getString("Aadhar_no");
                String Gender = sdpBenJson.getString("Gender");
                String Age = sdpBenJson.getString("Age");
                String Education = sdpBenJson.getString("Education");
                String Landholding_in_acres = sdpBenJson.getString("Landholding_in_acres");
                String No_of_seedlings_procured = sdpBenJson.getString("No_of_seedlings_procured");
                String Amount_paid = sdpBenJson.getString("Amount_paid");
                String scheme = sdpBenJson.getString("scheme");
                String year_of_implementation = sdpBenJson.getString("year_of_implementation");

                ContentValues cvSDPBen = new ContentValues();
                cvSDPBen.put(Database.FORM_ID, form_id);
                cvSDPBen.put(Database.NAME, name_of_beneficiary);
                cvSDPBen.put(Database.FATHER_NAME, father_or_husband_name);
                Log.e("fsdvfbvd",""+Aadhar_no);
                if (Aadhar_no.equalsIgnoreCase("NA"))
                    cvSDPBen.put(Database.AADHAR_NUMBER, "0");
                else
                    cvSDPBen.put(Database.AADHAR_NUMBER, Aadhar_no);

                cvSDPBen.put(Database.SEX, Gender);
                cvSDPBen.put(Database.AGE, Age);
                cvSDPBen.put(Database.EDUCATION, Education);
                cvSDPBen.put(Database.LAND_HOLDING_ACRE, Landholding_in_acres);
                cvSDPBen.put(Database.SEEDLING_PROCURED, No_of_seedlings_procured);
                cvSDPBen.put(Database.COST_PAID_APPLICABLE, Amount_paid);
                cvSDPBen.put(Database.PROGRAM_NAME, scheme);
                cvSDPBen.put(Database.YEAR_OF_IMPLEMENTATION, year_of_implementation);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertIntoBeneficiaries(cvSDPBen);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveOtherWorks(JSONArray otherWorks) {
        for (int i = 0; i < otherWorks.length(); i++) {
            try {
                JSONObject otherWorkJson = otherWorks.getJSONObject(i);
                String form_id = otherWorkJson.getString("form_id");
                String work_code = otherWorkJson.getString("work_code");
                String Uploadstatus = otherWorkJson.getString("Uploadstatus");
                String Evaltype = otherWorkJson.getString("Evaltype");
                String Evaltitle = otherWorkJson.getString("Evaltitle");
                String Eval_year = otherWorkJson.getString("Eval_year");
                String userlevel = otherWorkJson.getString("userlevel");
                String OfficeID = otherWorkJson.getString("OfficeID");
                String circle_name = otherWorkJson.getString("circle_name");
                String CIRCLE_ID = otherWorkJson.getString("CIRCLE_ID");
                String division_name = otherWorkJson.getString("division_name");
                String DIV_ID = otherWorkJson.getString("DIV_ID");
                String subdivision_name = otherWorkJson.getString("subdivision_name");
                String SUBDIV_ID = otherWorkJson.getString("SUBDIV_ID");
                String range_name = otherWorkJson.getString("range_name");
                String RANGE_ID = otherWorkJson.getString("RANGE_ID");
                String legal_status_of_land = otherWorkJson.getString("legal_status_of_land");
                String legal_status_of_land_other_details = otherWorkJson.getString("legal_status_of_land_other_details");
                String forest_name = otherWorkJson.getString("forest_name");
                String scheme_name = otherWorkJson.getString("scheme_name");
                String scheme_id = otherWorkJson.getString("scheme_id");
                String constituency_name = otherWorkJson.getString("constituency_name");
                String CONSTITUENCY_ID = otherWorkJson.getString("LA_ID");
                String type_of_work = otherWorkJson.getString("type_of_work");
                String name_of_work = otherWorkJson.getString("name_of_work");
                String location = otherWorkJson.getString("location");
                String year_of_execution = otherWorkJson.getString("year_of_execution");
                String estimated_cost_of_work = otherWorkJson.getString("estimated_cost_of_work");
                String total_cost_of_the_work = otherWorkJson.getString("total_cost_of_the_work");
                String work_approved_in_apo = otherWorkJson.getString("work_approved_in_apo");
                String apo_slno_present = otherWorkJson.getString("apo_slno_present");
                String apo_appr_date_present = otherWorkJson.getString("apo_appr_date_present");
                String work_approved_in_apo_slno = otherWorkJson.getString("work_approved_in_apo_slno");
                String work_approved_in_apo_approval_date = otherWorkJson.getString("work_approved_in_apo_approval_date");
                String work_approved_in_apo_no_reason = otherWorkJson.getString("work_approved_in_apo_no_reason");
                String procurement_details_if_any = otherWorkJson.getString("procurement_details_if_any");
                String procurement_mode = otherWorkJson.getString("procurement_mode");
                String procurement_mode_others = otherWorkJson.getString("procurement_mode_others");
                String procurement_amount = otherWorkJson.getString("procurement_amount");
                String who_executed_the_work = otherWorkJson.getString("who_executed_the_work");
                String who_executed_the_work_others = otherWorkJson.getString("who_executed_the_work_others");
                String work_commenced_year = otherWorkJson.getString("work_commenced_year");
                String work_commenced_month = otherWorkJson.getString("work_commenced_month");
                String work_completed_in_all_respect = otherWorkJson.getString("work_completed_in_all_respect");
                String work_completed_year = otherWorkJson.getString("work_completed_year");
                String work_completed_month = otherWorkJson.getString("work_completed_month");
//                String total_time_taken = otherWorkJson.getString("total_time_taken");
                String is_check_date_present = otherWorkJson.getString("is_check_date_present");
                String date_of_check_measurement = otherWorkJson.getString("date_of_check_measurement");
                String is_completion_date_present = otherWorkJson.getString("is_completion_date_present");
                String date_of_completion_report = otherWorkJson.getString("date_of_completion_report");
                String work_location_lat = otherWorkJson.getString("work_location_lat");
                String work_location_long = otherWorkJson.getString("work_location_long");
                String name_of_work_others = otherWorkJson.getString("name_of_work_others");
                String maintenance_fresh = otherWorkJson.getString("maintenance_fresh");
                String no_of_carins_rccpillars_rfstone = otherWorkJson.getString("no_of_carins_rccpillars_rfstone");

                ContentValues cvMaster = new ContentValues();
                cvMaster.put(Database.SURVEY_ID, form_id);
                cvMaster.put(Database.WORK_CODE, work_code);
                cvMaster.put(Database.EVALUATION_TITLE, Evaltitle);
                cvMaster.put(Database.EVALUATION_YEAR, Eval_year);
                cvMaster.put(Database.USER_LEVEL, userlevel);
                cvMaster.put(Database.OFFICEID, OfficeID);
                cvMaster.put(Database.FORM_TYPE, Constants.FORMTYPE_OTHERWORKS);
                cvMaster.put(Database.FORM_STATUS, 0);
                cvMaster.put(Database.PHOTO_STATUS, 0);
                cvMaster.put(Database.SURVEYOR_NAME, userName);
                cvMaster.put(Database.APP_ID, 0);
                cvMaster.put(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
                cvMaster.put(Database.ENDING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));


                ContentValues cvOtherWorks = new ContentValues();
                cvOtherWorks.put(Database.FORM_ID, form_id);
                cvOtherWorks.put(Database.WORK_CODE, work_code);
                cvOtherWorks.put(Database.CIRCLE_NAME, circle_name);
                cvOtherWorks.put(Database.CIRCLE_ID, CIRCLE_ID);
                cvOtherWorks.put(Database.DIVISION_NAME, division_name);
                cvOtherWorks.put(Database.DIVISION_CODE, DIV_ID);
                cvOtherWorks.put(Database.SUBDIVISION_NAME, subdivision_name);
                cvOtherWorks.put(Database.SUBDIVISION_CODE, SUBDIV_ID);
                cvOtherWorks.put(Database.RANGE_NAME, range_name);
                cvOtherWorks.put(Database.RANGE_CODE, RANGE_ID);
                cvOtherWorks.put(Database.CONSTITUENCY_NAME, constituency_name);
                cvOtherWorks.put(Database.CONSTITUENCY_ID, CONSTITUENCY_ID);
                cvOtherWorks.put(Database.FOREST_NAME, forest_name);
                cvOtherWorks.put(Database.LEGAL_STATUS_OF_LAND, legal_status_of_land);
                //  cvOtherWorks.put(Database.LEGAL_STATUS_OF_LAND_OTHER_DETAILS,legal_status_of_land_other_details);
                cvOtherWorks.put(Database.SCHEME_NAME, scheme_name);
                cvOtherWorks.put(Database.SCHEME_ID, scheme_id);
                cvOtherWorks.put(Database.TYPE_OF_WORK, type_of_work);
                cvOtherWorks.put(Database.WORK_NAME, name_of_work);
                cvOtherWorks.put(Database.WORK_LOCATION, location);
                cvOtherWorks.put(Database.EXECUTION_YEAR, year_of_execution);
                cvOtherWorks.put(Database.ESTIMATED_COST_RUPEES, estimated_cost_of_work);
                cvOtherWorks.put(Database.TOTAL_EXPENDITURE, total_cost_of_the_work);
                cvOtherWorks.put(Database.WORK_APPROVED_IN_APO, work_approved_in_apo);
                cvOtherWorks.put(Database.WORK_APPROVED_IN_APO_SLNO_AVAILABLE, apo_slno_present);
                cvOtherWorks.put(Database.WORK_APPROVED_IN_APO_SLNO, work_approved_in_apo_slno);
                cvOtherWorks.put(Database.WORK_APPROVED_IN_APO_DATE_AVAILABLE, apo_appr_date_present);
                cvOtherWorks.put(Database.WORK_APPROVED_IN_APO_APPROVAL_DATE, work_approved_in_apo_approval_date);
                cvOtherWorks.put(Database.WORK_APPROVED_IN_APO_NO_REASON, work_approved_in_apo_no_reason);
                cvOtherWorks.put(Database.WAS_PROCUREMENT_INVOLVED, procurement_details_if_any);
                cvOtherWorks.put(Database.MODE_OF_PROCUREMENT, procurement_mode);
                cvOtherWorks.put(Database.MODE_OF_PROCUREMENT_OTHERS, procurement_mode_others);
                cvOtherWorks.put(Database.PROCUREMENT_AMOUNT, procurement_amount);
                cvOtherWorks.put(Database.WHO_EXEC_WORK, who_executed_the_work);
                cvOtherWorks.put(Database.WHO_EXEC_WORK_OTHERS, who_executed_the_work_others);
                cvOtherWorks.put(Database.WHEN_WORK_START_YR, work_commenced_year);
                cvOtherWorks.put(Database.WHEN_WORK_STARTED_MONTH, work_commenced_month);
                cvOtherWorks.put(Database.WAS_WORK_COMPLETE, work_completed_in_all_respect);
                cvOtherWorks.put(Database.WHEN_WORK_COMPLETED_YEAR, work_completed_year);
                cvOtherWorks.put(Database.WHEN_WORK_COMPLETED_MONTH, work_completed_month);
//                cvOtherWorks.put(Database.TIME_TAKEN_MONTHS, total_time_taken);
                cvOtherWorks.put(Database.CHECK_MEASUREMENT_TIMESTAMP_AVAILABLE, is_check_date_present);
                cvOtherWorks.put(Database.CHECK_MEASUREMENT_TIMESTAMP, date_of_check_measurement);
                cvOtherWorks.put(Database.COMPLETION_CERTIFICATE_TIMESTAMP_AVAILABLE, is_completion_date_present);
                cvOtherWorks.put(Database.COMPLETION_CERTIFICATE_TIMESTAMP, date_of_completion_report);
                cvOtherWorks.put(Database.WORK_LOCATION_LAT, work_location_lat);
                cvOtherWorks.put(Database.WORK_LOCATION_LONG, work_location_long);
                cvOtherWorks.put(Database.OTHER_TYPE_OF_WORK, name_of_work_others);
                cvOtherWorks.put(Database.MAINTAINENACE_FRESH, maintenance_fresh);
                cvOtherWorks.put(Database.NO_OF_CARINS_RCCPILLARS_RFSTONE, no_of_carins_rccpillars_rfstone);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0")) {
                    db.insertIntoMaster(cvMaster);
                    db.insertIntoOtherWorks(cvOtherWorks);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSCPTSP(JSONArray scpTsp) {
        for (int j = 0; j < scpTsp.length(); j++) {
            try {
                JSONObject scptspJson = scpTsp.getJSONObject(j);
                String form_id = scptspJson.getString("form_id");
                String work_code = scptspJson.getString("work_code");
                String Uploadstatus = scptspJson.getString("Uploadstatus");
                String Evaltype = scptspJson.getString("Evaltype");
                String Evaltitle = scptspJson.getString("Evaltitle");
                String Eval_year = scptspJson.getString("Eval_year");
                String userlevel = scptspJson.getString("userlevel");
                String OfficeID = scptspJson.getString("OfficeID");
                String circle_name = scptspJson.getString("circle_name");
                String CIRCLE_ID = scptspJson.getString("CIRCLE_ID");
                String division_name = scptspJson.getString("division_name");
                String DIV_ID = scptspJson.getString("DIV_ID");
                String subdivision_name = scptspJson.getString("subdivision_name");
                String SUBDIV_ID = scptspJson.getString("SUBDIV_ID");
                String range_name = scptspJson.getString("range_name");
                String RANGE_ID = scptspJson.getString("RANGE_ID");
                String district_name = scptspJson.getString("district_name");
                String DISTRICT_CODE = scptspJson.getString("DISTRICT_CODE");
                String taluk_name = scptspJson.getString("taluk_name");
                String TALUK_CODE = scptspJson.getString("TALUK_CODE");
                String constituency_name = scptspJson.getString("constituency_name");
                String CONSTITUENCY_ID = scptspJson.getString("LA_ID");
                String grama_panchayat_name = scptspJson.getString("grampanchayat_name");
                String PANCHAYAT_CODE = scptspJson.getString("PANCHAYAT_CODE");
                String village_name = scptspJson.getString("village_name");
                String VILLAGE_CODE = scptspJson.getString("VILLAGE_CODE");
                String nature_of_benefit = scptspJson.getString("nature_of_benefit");
                String category_cmty = scptspJson.getString("category_cmty");
                String category_cmty_others = scptspJson.getString("category_cmty_others");
                //  String type_of_benefit = scptspJson.getString("type_of_benefit");
                //  String type_of_benefit_others = scptspJson.getString("type_of_benefit_others");


                ContentValues cvMaster = new ContentValues();
                cvMaster.put(Database.SURVEY_ID, form_id);
                cvMaster.put(Database.WORK_CODE, work_code);
                cvMaster.put(Database.EVALUATION_TITLE, Evaltitle);
                cvMaster.put(Database.EVALUATION_YEAR, Eval_year);
                cvMaster.put(Database.USER_LEVEL, userlevel);
                cvMaster.put(Database.OFFICEID, OfficeID);
                cvMaster.put(Database.FORM_TYPE, Constants.FORMTYPE_SCPTSP);
                cvMaster.put(Database.FORM_STATUS, 0);
                cvMaster.put(Database.PHOTO_STATUS, 0);
                cvMaster.put(Database.SURVEYOR_NAME, userName);
                cvMaster.put(Database.APP_ID, 0);
                cvMaster.put(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
                cvMaster.put(Database.ENDING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));

                /*This is used to update in SCPTSP form*/
                if (j == 0) {

                    preferences.edit().putString(Database.EVALUATION_TITLE, Evaltitle).apply();
                    preferences.edit().putString(Database.EVALUATION_YEAR, Eval_year).apply();
                    preferences.edit().putString(Database.USER_LEVEL, userlevel).apply();
                    preferences.edit().putString(Database.OFFICEID, OfficeID).apply();
                }

                /*This is used to update in SCPTSP form*/

                ContentValues cvSCPTSP = new ContentValues();
                cvSCPTSP.put(Database.FORM_ID, form_id);
                cvSCPTSP.put(Database.WORK_CODE, work_code);
                cvSCPTSP.put(Database.CIRCLE_NAME, circle_name.trim());
                cvSCPTSP.put(Database.CIRCLE_ID, CIRCLE_ID.trim());
                cvSCPTSP.put(Database.DIVISION_NAME, division_name.trim());
                cvSCPTSP.put(Database.DIVISION_CODE, DIV_ID.trim());
                cvSCPTSP.put(Database.SUBDIVISION_NAME, subdivision_name.trim());
                cvSCPTSP.put(Database.SUBDIVISION_CODE, SUBDIV_ID.trim());
                cvSCPTSP.put(Database.RANGE_NAME, range_name.trim());
                cvSCPTSP.put(Database.RANGE_CODE, RANGE_ID.trim());
                cvSCPTSP.put(Database.DISTRICT_NAME, district_name.trim());
                cvSCPTSP.put(Database.DISTRICT_CODE, DISTRICT_CODE.trim());
                cvSCPTSP.put(Database.TALUK_NAME, taluk_name.trim());
                cvSCPTSP.put(Database.TALUK_CODE, TALUK_CODE.trim());
                cvSCPTSP.put(Database.GRAMA_PANCHAYAT_NAME, grama_panchayat_name.trim());
                cvSCPTSP.put(Database.PANCHAYAT_CODE, PANCHAYAT_CODE.trim());
                cvSCPTSP.put(Database.VILLAGE_NAME, village_name.trim());
                cvSCPTSP.put(Database.VILLAGE_CODE, VILLAGE_CODE.trim());
                cvSCPTSP.put(Database.CONSTITUENCY_NAME, constituency_name.trim());
                cvSCPTSP.put(Database.CONSTITUENCY_ID, CONSTITUENCY_ID.trim());
                cvSCPTSP.put(Database.NATURE_OF_BENEFIT, nature_of_benefit.toUpperCase());
                cvSCPTSP.put(Database.NAME_OF_COMMUNITY, category_cmty);
                cvSCPTSP.put(Database.NAME_OF_COMMUNITY_OTHERS, category_cmty_others);

                if (!formIds.contains(form_id) && Uploadstatus.equals("0")) {
                    db.insertIntoMaster(cvMaster);

                }

                if (!formIds.contains(form_id) && nature_of_benefit.equalsIgnoreCase("INDIVIDUAL"))
                    db.insertIntoScpTsp(cvSCPTSP);
                else if (!formIds.contains(form_id) && nature_of_benefit.equalsIgnoreCase("COMMUNITY"))
                    db.insertIntoScpTsp(cvSCPTSP);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveBenefit_Individual(JSONArray individual) {
        for (int i = 0; i < individual.length(); i++) {
            try {
                JSONObject individualJson = individual.getJSONObject(i);
                String form_id = individualJson.getString("form_id");
                String indv_id = individualJson.getString("Indv_id");
                String work_code = individualJson.getString("work_code");
                String Uploadstatus = individualJson.getString("Uploadstatus");
                String type_of_benefit = individualJson.getString("type_of_benefit");
                String type_of_benefit_others = individualJson.getString("type_of_benefit_others");


                ContentValues cvIndividual = new ContentValues();
                cvIndividual.put(Database.FORM_ID, form_id);
                cvIndividual.put(Database.BENEFIT_ID, indv_id);
                cvIndividual.put(Database.WORK_CODE, work_code);
                cvIndividual.put(Database.TYPE_OF_BENEFIT, type_of_benefit);
                cvIndividual.put(Database.NATURE_OF_BENEFIT, ScpTspSamplingSurvey.INDIVIDUAL);
                cvIndividual.put(Database.TYPE_OF_BENEFIT_OTHERS, type_of_benefit_others);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertIntoScpTspSurvey(cvIndividual);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveBenefit_Community(JSONArray community) {
        for (int i = 0; i < community.length(); i++) {
            try {
                JSONObject communityJson = community.getJSONObject(i);
                String form_id = communityJson.getString("form_id");
                String indv_id = communityJson.getString("Com_id");
                String work_code = communityJson.getString("work_code");
                String Uploadstatus = communityJson.getString("Uploadstatus");
                String type_of_benefit = communityJson.getString("type_of_benefit");
                String type_of_benefit_others = communityJson.getString("type_of_benefit_others");
                String program_name = communityJson.getString("program_name");
                String program_others = communityJson.getString("program_others");
                String year_of_implementation = communityJson.getString("year_of_implementation");

                ContentValues cvCommunity = new ContentValues();
                cvCommunity.put(Database.FORM_ID, form_id);
                cvCommunity.put(Database.BENEFIT_ID, indv_id);
                cvCommunity.put(Database.WORK_CODE, work_code);
                cvCommunity.put(Database.TYPE_OF_ASSET, type_of_benefit);
                cvCommunity.put(Database.TYPE_OF_ASSET_OTHERS, type_of_benefit_others);
                cvCommunity.put(Database.PROGRAM_NAME, program_name);
                cvCommunity.put(Database.PROGRAM_NAME_OTHERS, program_others);
                cvCommunity.put(Database.YEAR_OF_IMPLEMENTATION, year_of_implementation);
                cvCommunity.put(Database.NATURE_OF_BENEFIT, ScpTspSamplingSurvey.COMMUNITY);
                //  db.insertIntoBenefit(cvCommunity);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertIntoScpTspSurvey(cvCommunity);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSCPTSPBeneficiaryList(JSONArray individualList) {
        for (int i = 0; i < individualList.length(); i++) {

            try {
                JSONObject individualJson = individualList.getJSONObject(i);
                String form_id = individualJson.getString("form_id");
                String indv_id = individualJson.getString("Indv_id");
                String Uploadstatus = individualJson.getString("Uploadstatus");
                String Sl_no = individualJson.getString("Sl_no");
                String Name_of_beneficiary = individualJson.getString("Name_of_beneficiary");
                String Father_or_husband_name = individualJson.getString("Father_or_husband_name");
                String Category_SC_ST_Others = individualJson.getString("Category_SC_ST_Others");
                String Aadhar_no = individualJson.getString("Aadhar_no");
                String Gender = individualJson.getString("Gender");
                String Age = individualJson.getString("Age");
                String Education = individualJson.getString("Education");
                String Landholding_in_acres = individualJson.getString("Landholding_in_acres");
                String program_name = individualJson.getString("program_name");
                String program_others = individualJson.getString("program_others");
                String year_of_implementation = individualJson.getString("year_of_implementation");

                ContentValues cvBenList = new ContentValues();
                cvBenList.put(Database.FORM_ID, form_id);
                cvBenList.put(Database.BENEFIT_ID, indv_id);
                cvBenList.put(Database.BENEFICIARY_CODE, Sl_no);
                cvBenList.put(Database.BENEFICIARY_NAME, Name_of_beneficiary);
                cvBenList.put(Database.BENEFICIARY_FATHER_NAME, Father_or_husband_name);
                cvBenList.put(Database.NAME_OF_COMMUNITY, Category_SC_ST_Others);
                if (Aadhar_no.equalsIgnoreCase("NA"))
                    cvBenList.put(Database.AADHAR_NUMBER, "0");
                else
                    cvBenList.put(Database.AADHAR_NUMBER, Aadhar_no);

                cvBenList.put(Database.BENEFICIARY_SEX, Gender);
                cvBenList.put(Database.BENEFICIARY_AGE, Age);
                cvBenList.put(Database.BENEFICIARY_EDUCATION, Education);
                cvBenList.put(Database.LAND_HOLDING_ACRE, Landholding_in_acres);
                cvBenList.put(Database.PROGRAM_NAME, program_name);
                cvBenList.put(Database.PROGRAM_NAME_OTHERS, program_others);
                cvBenList.put(Database.YEAR_OF_IMPLEMENTATION, year_of_implementation);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertValuesIntoSCPTSPBeneficiaries(cvBenList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAdvanceWork(JSONArray advanceWork) {
        for (int j = 0; j < advanceWork.length(); j++) {

            try {
                JSONObject advanceWorkJson = advanceWork.getJSONObject(j);
                String work_code = advanceWorkJson.getString("work_code");
                String Uploadstatus = advanceWorkJson.getString("Uploadstatus");
                String Evaltype = advanceWorkJson.getString("Evaltype");
                String Evaltitle = advanceWorkJson.getString("Evaltitle");
                String Eval_year = advanceWorkJson.getString("Eval_year");
                String userlevel = advanceWorkJson.getString("userlevel");
                String OfficeID = advanceWorkJson.getString("OfficeID");
                String apo = advanceWorkJson.getString("apo");
                String apo_date = advanceWorkJson.getString("apo_date");
                String work_estimates = advanceWorkJson.getString("work_estimates");
                String no_of_work_estimates = advanceWorkJson.getString("no_of_work_estimates");
                String fnb = advanceWorkJson.getString("fnb");
                String plantation_journal = advanceWorkJson.getString("plantation_journal");
                String circle_name = advanceWorkJson.getString("circle_name");
                String CIRCLE_ID = advanceWorkJson.getString("CIRCLE_ID");
                String division_name = advanceWorkJson.getString("division_name");
                String DIV_ID = advanceWorkJson.getString("DIV_ID");
                String subdivision_name = advanceWorkJson.getString("subdivision_name");
                String SUBDIV_ID = advanceWorkJson.getString("SUBDIV_ID");
                String range_name = advanceWorkJson.getString("range_name");
                String RANGE_ID = advanceWorkJson.getString("RANGE_ID");
                String district_name = advanceWorkJson.getString("district_name");
                String DISTRICT_CODE = advanceWorkJson.getString("DISTRICT_CODE");
                String constituency_name = advanceWorkJson.getString("constituency_name");
                String CONSTITUENCY_ID = advanceWorkJson.getString("LA_ID");
                String form_id = advanceWorkJson.getString("form_id");
                String taluk_name = advanceWorkJson.getString("taluk_name");
                String TALUK_CODE = advanceWorkJson.getString("TALUK_CODE");
                String grampanchayat_name = advanceWorkJson.getString("grampanchayat_name");
                String PANCHAYAT_CODE = advanceWorkJson.getString("PANCHAYAT_CODE");
                String village_name = advanceWorkJson.getString("village_name");
                String VILLAGE_CODE = advanceWorkJson.getString("VILLAGE_CODE");
                String gps_latitude = advanceWorkJson.getString("gps_latitude");
                String gps_longitude = advanceWorkJson.getString("gps_longitude");
                String rf_name = advanceWorkJson.getString("rf_name");
                String plantation_name = advanceWorkJson.getString("plantation_name");
                String legal_status_of_land = advanceWorkJson.getString("legal_status_of_land");
                String legal_status_of_land_other_details = advanceWorkJson.getString("legal_status_of_land_other_details");
                String average_annual_rainfall_mm = advanceWorkJson.getString("average_annual_rainfall_mm");
                String year_of_earthwork = advanceWorkJson.getString("year_of_earthwork");
                String gross_plantation_area_ha = advanceWorkJson.getString("gross_plantation_area_ha");
                String net_plantation_area_ha = advanceWorkJson.getString("net_plantation_area_ha");
                String total_no_of_sample_plots_laid = advanceWorkJson.getString("total_no_of_sample_plots_laid");
                String scheme_id = advanceWorkJson.getString("scheme_id");
                String scheme_name = advanceWorkJson.getString("scheme_name");
                String plantation_model = advanceWorkJson.getString("plantation_model");
                String plantation_model_id = advanceWorkJson.getString("plantation_model_id");
                String type_of_earth_work_done = advanceWorkJson.getString("type_of_earth_work_done");
                String type_of_earth_work_done_otherdetails = advanceWorkJson.getString("type_of_earth_work_done_otherdetails");
                String Pit_size = advanceWorkJson.getString("Pit_size");
                String Pit_espacement = advanceWorkJson.getString("Pit_espacement");
                String no_of_pits = advanceWorkJson.getString("no_of_pits");
                String Trench_size = advanceWorkJson.getString("Trench_size");
                String Trench_espacement = advanceWorkJson.getString("Trench_espacement");
                String no_of_trenches = advanceWorkJson.getString("no_of_trenches");
                String Pit_in_Pit_size = advanceWorkJson.getString("Pit_in_Pit_size");
                String Pit_in_Pit_espacement = advanceWorkJson.getString("Pit_in_Pit_espacement");
                String no_of_pit_in_pit = advanceWorkJson.getString("no_of_pit_in_pit");
                String Ripping_size = advanceWorkJson.getString("Ripping_size");
                String Ripping_espacement = advanceWorkJson.getString("Ripping_espacement");
                String no_of_riplines = advanceWorkJson.getString("no_of_riplines");
                String Others_size = advanceWorkJson.getString("Others_size");
                String Others_espacement = advanceWorkJson.getString("Others_espacement");
                String Others_no_of_units = advanceWorkJson.getString("Others_no_of_units");
                String reason_for_planting = advanceWorkJson.getString("reason_for_planting");
                String reason_for_planting_others = advanceWorkJson.getString("reason_for_planting_others");
                String plantation_totexp_earthwork = advanceWorkJson.getString("plantation_totexp_earthwork");
                String plantation_totexp_raisingseedling = advanceWorkJson.getString("plantation_totexp_raisingseedling");
                String plantation_sanctn_date_for_earthwork = advanceWorkJson.getString("plantation_sanctn_date_for_earthwork");
                String plantation_sanctn_date_for_raisingseedling = advanceWorkJson.getString("plantation_sanctn_date_for_raisingseedling");
                String soil_type = advanceWorkJson.getString("soil_type");
                String was_the_site_previously_planted = advanceWorkJson.getString("was_the_site_previously_planted");
                String year_of_previous_planting = advanceWorkJson.getString("year_of_previous_planting");
                String reason_for_replanting = advanceWorkJson.getString("reason_for_replanting");
                String was_permission_obtained_for_replanting = advanceWorkJson.getString("was_permission_obtained_for_replanting");
                String details_of_permission_for_replanting = advanceWorkJson.getString("details_of_permission_for_replanting");
                String plantation_operations_asper_prescription = advanceWorkJson.getString("plantation_operations_asper_prescription");
                String is_work_done_documentated_properly_in_plantation_journal = advanceWorkJson.getString("is_work_done_documentated_properly_in_plantation_journal");
                String any_snr_officer_inspect_plantation_and_entries_in_journal = advanceWorkJson.getString("any_snr_officer_inspect_plantation_and_entries_in_journal");
                String first_snr_officer_designation = advanceWorkJson.getString("first_snr_officer_designation");
                String number_of_inspection_acf = advanceWorkJson.getString("number_of_inspection_acf");
                String number_of_inspection_dcf = advanceWorkJson.getString("number_of_inspection_dcf");
                String number_of_inspection_cf = advanceWorkJson.getString("number_of_inspection_cf");
                String number_of_inspection_ccf = advanceWorkJson.getString("number_of_inspection_ccf");
                String number_of_inspection_apccf = advanceWorkJson.getString("number_of_inspection_apccf");
                String number_of_inspection_pccf = advanceWorkJson.getString("number_of_inspection_pccf");
                String plantation_totexp_boundprotect = advanceWorkJson.getString("plantation_totexp_boundprotect");
//                String no_of_species_planted = advanceWorkJson.getString("no_of_species_planted");
                String is_smc_present = advanceWorkJson.getString("is_smc_present");
//                String smc_work_expenditure_as_per_norm = advanceWorkJson.getString("smc_work_expenditure_as_per_norm");
                String total_budget_for_smc_work = advanceWorkJson.getString("total_budget_for_smc_work");
                String is_vfc_involved_in_plantation_activity = advanceWorkJson.getString("is_vfc_involved_in_plantation_activity");
                String name_of_the_vfc = advanceWorkJson.getString("name_of_the_vfc");
                String Is_plantation_raised_under_JFPM_area = advanceWorkJson.getString("Is_plantation_raised_under_JFPM_area");
                String Is_CPT_present = advanceWorkJson.getString("Is_CPT_present");
                String planting_on_CPT = advanceWorkJson.getString("planting_on_CPT");
                String working_plan_management_plan_prescriptions = advanceWorkJson.getString("working_plan_management_plan_prescriptions");
                String working_circle_paragraph_no = advanceWorkJson.getString("working_circle_paragraph_no");
                String why_work_was_taken_up = advanceWorkJson.getString("why_work_was_taken_up");


                ContentValues cvMaster = new ContentValues();
                cvMaster.put(Database.SURVEY_ID, form_id);
                cvMaster.put(Database.WORK_CODE, work_code);
                cvMaster.put(Database.EVALUATION_TITLE, Evaltitle);
                cvMaster.put(Database.EVALUATION_YEAR, Eval_year);
                cvMaster.put(Database.USER_LEVEL, userlevel);
                cvMaster.put(Database.OFFICEID, OfficeID);
                cvMaster.put(Database.FORM_TYPE, Constants.FORMTYPE_ADVANCEWORK);
                cvMaster.put(Database.FORM_STATUS, 0);
                cvMaster.put(Database.PHOTO_STATUS, 0);
                cvMaster.put(Database.SURVEYOR_NAME, userName);
                cvMaster.put(Database.APP_ID, 0);
                cvMaster.put(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
                cvMaster.put(Database.ENDING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));


                ContentValues cv = new ContentValues();
                cv.put(Database.FORM_ID, form_id);
                cv.put(Database.CIRCLE_NAME, circle_name.trim());
                cv.put(Database.CIRCLE_ID, CIRCLE_ID.trim());
                cv.put(Database.DIVISION_NAME, division_name.trim());
                cv.put(Database.DIVISION_CODE, DIV_ID.trim());
                cv.put(Database.SUBDIVISION_NAME, subdivision_name.trim());
                cv.put(Database.SUBDIVISION_CODE, SUBDIV_ID.trim());
                cv.put(Database.RANGE_NAME, range_name.trim());
                cv.put(Database.RANGE_CODE, RANGE_ID.trim());
                cv.put(Database.DISTRICT_NAME, district_name.trim());
                cv.put(Database.DISTRICT_CODE, DISTRICT_CODE.trim());
                cv.put(Database.CONSTITUENCY_NAME, constituency_name.trim());
                cv.put(Database.CONSTITUENCY_ID, CONSTITUENCY_ID.trim());
                cv.put(Database.TALUK_NAME, taluk_name.trim());
                cv.put(Database.TALUK_CODE, TALUK_CODE.trim());
                cv.put(Database.VILLAGE_NAME, village_name.trim());
                cv.put(Database.VILLAGE_CODE, VILLAGE_CODE.trim());
                cv.put(Database.GRAMA_PANCHAYAT_NAME, grampanchayat_name);
                cv.put(Database.PANCHAYAT_CODE, PANCHAYAT_CODE.trim());
                cv.put(Database.LEGAL_STATUS_OF_LAND, legal_status_of_land);
                cv.put(Database.RF_NAME, rf_name);
                cv.put(Database.PLANTATION_NAME, plantation_name);
                cv.put(Database.WORK_CODE, work_code);
                cv.put(Database.APO, apo);
                cv.put(Database.APO_DATE, apo_date);
                cv.put(Database.WORK_ESTIMATES, work_estimates);
                cv.put(Database.NO_OF_WORK_ESTIMATES, no_of_work_estimates);
                cv.put(Database.FNB, fnb);
                cv.put(Database.PLANTATION_JOURNAL, plantation_journal);
                cv.put(Database.YEAR_OF_EARTHWORK, year_of_earthwork);
                cv.put(Database.GROSS_PLANTATION_AREA_HA, gross_plantation_area_ha);
                cv.put(Database.NET_PLANTATION_AREA_HA, net_plantation_area_ha);
                cv.put(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, total_no_of_sample_plots_laid);
                cv.put(Database.SCHEME_ID, scheme_id);
                cv.put(Database.SCHEME_NAME, scheme_name);
                cv.put(Database.PLANTATION_MODEL, plantation_model);
                cv.put(Database.PLANTATION_MODEL_ID, plantation_model_id);
                cv.put(Database.TYPE_OF_EARTH_WORK_DONE, type_of_earth_work_done);
                cv.put(Database.TYPE_OF_EARTH_WORK_DONE_OTHERDETAILS, type_of_earth_work_done_otherdetails);
                cv.put(Database.PITSIZE, Pit_size);
                cv.put(Database.PIT_ESPACEMENT, Pit_espacement);
                cv.put(Database.NO_OF_PITS, no_of_pits);
                cv.put(Database.TRENCH_SIZE, Trench_size);
                cv.put(Database.TRENCH_ESPACEMENT, Trench_espacement);
                cv.put(Database.NO_OF_TRENCHS, no_of_trenches);
                cv.put(Database.PIT_IN_PIT_SIZE, Pit_in_Pit_size);
                cv.put(Database.PIT_IN_PIT_ESPACEMENT, Pit_in_Pit_espacement);
                cv.put(Database.NO_OF_PIT_IN_PIT, no_of_pit_in_pit);
                cv.put(Database.RIPPING_SIZE, Ripping_size);
                cv.put(Database.RIPPING_ESPACEMENT, Ripping_espacement);
                cv.put(Database.NO_OF_RIPLINE, no_of_riplines);
                cv.put(Database.OTHERS_SIZE, Others_size);
                cv.put(Database.OTHERS_ESPACEMENT, Others_espacement);
                cv.put(Database.OTHERS_NO_OF_UNITS, Others_no_of_units);
                cv.put(Database.REASON_FOR_PLANTING, reason_for_planting);
                cv.put(Database.REASON_FOR_PLANTING_OTHERS, reason_for_planting_others);
                cv.put(Database.AVERAGE_ANNUAL_RAINFALL_MM, average_annual_rainfall_mm);
                cv.put(Database.SOIL_TYPE, soil_type);
                cv.put(Database.PLANTATION_TOTEXP_EARTHWORK, plantation_totexp_earthwork);
                cv.put(Database.PLANTATION_TOTEXP_RAISINGSEEDLING, plantation_totexp_raisingseedling);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_EARTHWORK, plantation_sanctn_date_for_earthwork);
                cv.put(Database.PLANTATION_SANCTN_DATE_FOR_RAISINGSEEDLING, plantation_sanctn_date_for_raisingseedling);
                cv.put(Database.WAS_THE_SITE_PREVIOUSLY_PLANTED, was_the_site_previously_planted);
                cv.put(Database.YEAR_OF_PREVIOUS_PLANTING, year_of_previous_planting);
                cv.put(Database.REASON_FOR_REPLANTING, reason_for_replanting);
                cv.put(Database.WAS_PERMISSION_OBTAINED_FOR_REPLANTING, was_permission_obtained_for_replanting);
                cv.put(Database.DETAILS_OF_PERMISSION_FOR_REPLANTING, details_of_permission_for_replanting);
                cv.put(Database.PLANTATION_OPERATIONS_ASPER_PRESCRIPTION, plantation_operations_asper_prescription);
                cv.put(Database.IS_WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL, is_work_done_documentated_properly_in_plantation_journal);
                cv.put(Database.ANY_SNR_OFFICER_INSPECT_PLANTATION_AND_ENTRIES_IN_JOURNAL, any_snr_officer_inspect_plantation_and_entries_in_journal);
                cv.put(Database.FIRST_SNR_OFFICER_DESIGNATION, first_snr_officer_designation);
                cv.put(Database.NUMBER_OF_INSPECTION_ACF, number_of_inspection_acf);
                cv.put(Database.NUMBER_OF_INSPECTION_DCF, number_of_inspection_dcf);
                cv.put(Database.NUMBER_OF_INSPECTION_CF, number_of_inspection_cf);
                cv.put(Database.NUMBER_OF_INSPECTION_CCF, number_of_inspection_ccf);
                cv.put(Database.NUMBER_OF_INSPECTION_APCCF, number_of_inspection_apccf);
                cv.put(Database.NUMBER_OF_INSPECTION_PCCF, number_of_inspection_pccf);
                cv.put(Database.LEGAL_STATUS_OF_LAND_OTHER_DETAILS, legal_status_of_land_other_details);
                cv.put(Database.GPS_LATITUDE, gps_latitude);
                cv.put(Database.GPS_LONGITUDE, gps_longitude);

                cv.put(Database.IS_CPT_PRESENT, Is_CPT_present);
                cv.put(Database.PLANTING_ON_CPT, planting_on_CPT);
                cv.put(Database.WORKING_PLAN_MANAGEMENT_PLAN_PRESCRIPTIONS, working_plan_management_plan_prescriptions);
                cv.put(Database.WORKING_CIRCLE_PARAGRAPH_NO, working_circle_paragraph_no);
                cv.put(Database.WHY_WORK_WAS_TAKEN_UP, why_work_was_taken_up);


                ContentValues cvVFC = new ContentValues();
                cvVFC.put(Database.FORM_ID, form_id);
                cvVFC.put(Database.WORK_CODE, work_code);
                cvVFC.put(Database.VFC_APPLICABLE, is_vfc_involved_in_plantation_activity);
                cvVFC.put(Database.NAME_OF_THE_VFC, name_of_the_vfc);
                cvVFC.put(Database.IS_JFPM_RAISED, Is_plantation_raised_under_JFPM_area);


                ContentValues cvSMC = new ContentValues();
                cvSMC.put(Database.FORM_ID, form_id);
                cvSMC.put(Database.WORK_CODE, work_code);
                cvSMC.put(Database.SMC_APPLICABLE, is_smc_present);
//                cvSMC.put(Database.SMC_WORK_EXPENDITURE_AS_PER_NORM, smc_work_expenditure_as_per_norm);
                cvSMC.put(Database.TOTAL_BUDGET_FOR_SMC_WORK, total_budget_for_smc_work);

                if (!formIds.contains(form_id) && Uploadstatus.equals("0")) {
                    db.insertIntoMaster(cvMaster);
                    db.insertIntoAdvanceWork(cv);
                    db.insertIntoAdvSmcMaster(cvSMC);
                    db.insertIntoAdvVfcSampling(cvVFC);

                    cvSMC.remove(Database.WORK_CODE);
                    cvSMC.remove(Database.SMC_APPLICABLE);
//                    cvSMC.remove(Database.SMC_WORK_EXPENDITURE_AS_PER_NORM);
                    cvSMC.remove(Database.TOTAL_BUDGET_FOR_SMC_WORK);
                    int samplePlots = Integer.parseInt(total_no_of_sample_plots_laid);
                    for (int i = 0; i < samplePlots; i++) {
                        db.insertIntoAdvSamplePlot(cvSMC);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAdvworkBoundary(JSONArray boundary) {
        for (int j = 0; j < boundary.length(); j++) {
            try {
                JSONObject boundaryJson = boundary.getJSONObject(j);
                String form_id = boundaryJson.getString("form_id");
                String work_code = boundaryJson.getString("work_code");
                String Uploadstatus = boundaryJson.getString("Uploadstatus");
                String type_of_protection_provided = boundaryJson.getString("type_of_protection_provided");
                String type_of_protection_provided_other = boundaryJson.getString("type_of_protection_provided_other");
                String length = boundaryJson.getString("length");
                String boundary_cost = boundaryJson.getString("boundary_cost");

                ContentValues cv = new ContentValues();
                cv.put(Database.FORM_ID, form_id);
                cv.put(Database.WORK_CODE, work_code);
                cv.put(Database.TYPE_OF_PROTECTION, type_of_protection_provided);
                cv.put(Database.BOUNDARY_COST, boundary_cost);
                cv.put(Database.TOTAL_LENGTH_KMS, length);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertAdvProtection(cv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAdvworkSmc(JSONArray smc) {
        for (int j = 0; j < smc.length(); j++) {
            try {
                JSONObject smcJson = smc.getJSONObject(j);
                String form_id = smcJson.getString("form_id");
                String work_code = smcJson.getString("work_code");
                String Uploadstatus = smcJson.getString("Uploadstatus");
                String type_of_structure = smcJson.getString("type_of_structure");
                String smc_structure_length = smcJson.getString("smc_structure_length");
                String smc_structure_breadth = smcJson.getString("smc_structure_breadth");
                String smc_structure_dept = smcJson.getString("smc_structure_dept");
                String smc_structure_cost = smcJson.getString("smc_structure_cost");


                ContentValues cv = new ContentValues();
                cv.put(Database.FORM_ID, form_id);
                cv.put(Database.WORK_CODE, work_code);
                cv.put(Database.TYPE_OF_STRUCTURE, type_of_structure);
                cv.put(Database.SMC_STRUCTURE_LENGTH, smc_structure_length);
                cv.put(Database.SMC_STRUCTURE_BREADTH, smc_structure_breadth);
                cv.put(Database.SMC_STRUCTURE_DEPTH, smc_structure_dept);
                cv.put(Database.SMC_STRUCTURE_COST, smc_structure_cost);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertAdvSMCList(cv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveNursery(JSONArray nurseryWork) {
        for (int i = 0; i < nurseryWork.length(); i++) {

            try {
                JSONObject nurseryJson = nurseryWork.getJSONObject(i);
                String form_id = nurseryJson.getString("form_id");
                String work_code = nurseryJson.getString("work_code");
                String Uploadstatus = nurseryJson.getString("Uploadstatus");
                String Evaltype = nurseryJson.getString("Evaltype");
                String Evaltitle = nurseryJson.getString("Evaltitle");
                String Eval_year = nurseryJson.getString("Eval_year");
                String userlevel = nurseryJson.getString("userlevel");
                String OfficeID = nurseryJson.getString("OfficeID");
                String circle_name = nurseryJson.getString("circle_name");
                String division_name = nurseryJson.getString("division_name");
                String subdivision_name = nurseryJson.getString("subdivision_name");
                String range_name = nurseryJson.getString("range_name");
                String district_name = nurseryJson.getString("district_name");
                String constituency_name = nurseryJson.getString("constituency_name");
                String taluk_name = nurseryJson.getString("taluk_name");
                String grampanchayat_name = nurseryJson.getString("grampanchayat_name");
                String village_name = nurseryJson.getString("village_name");
                String CIRCLE_ID = nurseryJson.getString("CIRCLE_ID");
                String DIV_ID = nurseryJson.getString("DIV_ID");
                String SUBDIV_ID = nurseryJson.getString("SUBDIV_ID");
                String RANGE_ID = nurseryJson.getString("RANGE_ID");
                String DISTRICT_CODE = nurseryJson.getString("DISTRICT_CODE");
                String LA_ID = nurseryJson.getString("LA_ID");
                String TALUK_CODE = nurseryJson.getString("TALUK_CODE");
                String PANCHAYAT_CODE = nurseryJson.getString("PANCHAYAT_CODE");
                String VILLAGE_CODE = nurseryJson.getString("VILLAGE_CODE");
                String gps_latitude = nurseryJson.getString("gps_latitude");
                String gps_longitude = nurseryJson.getString("gps_longitude");
                String nursery_name = nurseryJson.getString("nursery_name");

                ContentValues cvMaster = new ContentValues();
                cvMaster.put(Database.SURVEY_ID, form_id);
                cvMaster.put(Database.WORK_CODE, work_code);
                cvMaster.put(Database.EVALUATION_TITLE, Evaltitle);
                cvMaster.put(Database.EVALUATION_YEAR, Eval_year);
                cvMaster.put(Database.USER_LEVEL, userlevel);
                cvMaster.put(Database.OFFICEID, OfficeID);
                cvMaster.put(Database.FORM_TYPE, Constants.FORMTYPE_NURSERY_WORK);
                cvMaster.put(Database.FORM_STATUS, 0);
                cvMaster.put(Database.PHOTO_STATUS, 0);
                cvMaster.put(Database.SURVEYOR_NAME, userName);
                cvMaster.put(Database.APP_ID, 0);
                cvMaster.put(Database.STARTING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
                cvMaster.put(Database.ENDING_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));

                ContentValues cvNursery = new ContentValues();
                cvNursery.put(Database.FORM_ID, form_id);
                cvNursery.put(Database.WORK_CODE, work_code);

                cvNursery.put(Database.CIRCLE_NAME, circle_name.trim());
                cvNursery.put(Database.CIRCLE_ID, CIRCLE_ID.trim());
                cvNursery.put(Database.DIVISION_NAME, division_name.trim());
                cvNursery.put(Database.DIVISION_CODE, DIV_ID.trim());
                cvNursery.put(Database.SUBDIVISION_NAME, subdivision_name.trim());
                cvNursery.put(Database.SUBDIVISION_CODE, SUBDIV_ID.trim());
                cvNursery.put(Database.RANGE_NAME, range_name.trim());
                cvNursery.put(Database.RANGE_CODE, RANGE_ID.trim());
                cvNursery.put(Database.CONSTITUENCY_NAME, RANGE_ID.trim());
                cvNursery.put(Database.CONSTITUENCY_ID, RANGE_ID.trim());
                cvNursery.put(Database.DISTRICT_NAME, district_name.trim());
                cvNursery.put(Database.DISTRICT_CODE, DISTRICT_CODE.trim());
                cvNursery.put(Database.TALUK_NAME, taluk_name.trim());
                cvNursery.put(Database.TALUK_CODE, TALUK_CODE.trim());
                cvNursery.put(Database.GRAMA_PANCHAYAT_NAME, grampanchayat_name.trim());
                cvNursery.put(Database.PANCHAYAT_CODE, PANCHAYAT_CODE.trim());
                cvNursery.put(Database.VILLAGE_NAME, village_name.trim());
                cvNursery.put(Database.VILLAGE_CODE, VILLAGE_CODE);
                cvNursery.put(Database.CONSTITUENCY_NAME, constituency_name.trim());
                cvNursery.put(Database.CONSTITUENCY_ID, LA_ID.trim());
                cvNursery.put(Database.NURSERY_LATITUDE, gps_latitude.trim());
                cvNursery.put(Database.NURSERY_LONGITUDE, gps_longitude.trim());
                cvNursery.put(Database.NURSERY_NAME, nursery_name.trim());

                if (!formIds.contains(form_id) && Uploadstatus.equals("0")) {
                    db.insertIntoMaster(cvMaster);
                    db.insertIntoNurseryWorks(cvNursery);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveNurserySpecies(JSONArray nurserySpecies) {
        for (int i = 0; i < nurserySpecies.length(); i++) {


            try {
                JSONObject nurserySpeciesJson = nurserySpecies.getJSONObject(i);
                String form_id = nurserySpeciesJson.getString("form_id");
                String work_code = nurserySpeciesJson.getString("work_code");
                String Uploadstatus = nurserySpeciesJson.getString("Uploadstatus");
                String scheme_name = nurserySpeciesJson.getString("scheme_name");
                String scheme_id = nurserySpeciesJson.getString("scheme_id");
                String main_species_planted = nurserySpeciesJson.getString("main_species_planted");
                String other_species_name = nurserySpeciesJson.getString("other_species_name");
                String species_id = nurserySpeciesJson.getString("species_id");
                String seedlings_pbsize = nurserySpeciesJson.getString("seedlings_pbsize");
                String no_of_seedlings_per_ha = nurserySpeciesJson.getString("no_of_seedlings_per_ha");

                ContentValues cvNurserySpecies = new ContentValues();
                cvNurserySpecies.put(Database.FORM_ID, form_id);
//                cvNurserySpecies.put(Database.WORK_CODE, work_code);
                cvNurserySpecies.put(Database.NAME_OF_THE_SCHEME, scheme_name);
                cvNurserySpecies.put(Database.SCHEME_ID, scheme_id);
                cvNurserySpecies.put(Database.MAIN_SPECIES_PLANTED, main_species_planted);
                cvNurserySpecies.put(Database.OTHER_SPECIES, other_species_name);
                cvNurserySpecies.put(Database.SPECIES_ID, species_id);
                cvNurserySpecies.put(Database.PB_SIZE, seedlings_pbsize);
                cvNurserySpecies.put(Database.NUMBER_AS_PER_RECORDS, no_of_seedlings_per_ha);
                if (!formIds.contains(form_id) && Uploadstatus.equals("0"))
                    db.insertIntoNurseryWorkBaggedSeedlings(cvNurserySpecies);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public interface ProgressListener {

        void show();

        void hide();
    }


}
