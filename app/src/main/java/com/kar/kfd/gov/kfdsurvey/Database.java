package com.kar.kfd.gov.kfdsurvey;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.model.LocationWithID;
import com.ngohung.form.model.NamesWithID;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("Range")
public class Database extends SQLiteOpenHelper {


    public static final String FOLDER_NAME = "folderName";
    /*Nursery Columns*/
    public static final String BAGGED_SEEDLING_ID = "bagged_seedling_id";
    public static final String NURSERY_NAME = "nursery_name";
    public static final String NURSERY_LATITUDE = "nursery_latitude";
    public static final String NURSERY_LONGITUDE = "nursery_longitude";
    public static final String NURSERY_INCHARGE_NAME = "nursery_incharge_name";
    public static final String NURSERY_INCHARGE_DESIGNATION = "nursery_incharge_designation";
    public static final String NURSERY_INCHARGE_YEARSOFEXP = "nursery_incharge_yearsofexp";
    public static final String TOTAL_AREA_HACTARE = "total_area_hactare";
    public static final String IS_NURSERY_FENCED_AND_GATED = "is_nursery_fenced_and_gated";
    public static final String SOURCE_OF_WATER_SUPPLY = "source_of_water_supply";
    public static final String SOURCE_OF_WATER_SUPPLY_OTHERS = "source_of_water_supply_others";
    public static final String AVAILABILITY_OF_NURSERY_FACILITIES = "availability_of_nursery_facilities";
    public static final String OTHERS = "others";
    public static final String RECORDS_MAINTAINED = "records_maintained";
    public static final String LABOUR_ATTENDANCE_REGISTER = "labour_attendance_register";
    public static final String STORES_REGISTER = "stores_register";
    public static final String SEEDLING_STOCK_REGISTER = "seedling_stock_register";
    public static final String SEEDLING_DISTRIBUTION = "seedling_distribution";
    public static final String BUDGET_HEADWISE_NURSERY_REGISTER = "budget_headwise_nursery_register";

    public static final String KFD_NURSERY_WORKS_BAGGED_SEEDLINGS_QUALITY = "kfd_nursery_works_bagged_seedlings_quality";
    public static final String SEEDLINGS_ARRANGED_SPECIESWISE = "seedlings_arranged_specieswise";
    public static final String SEEDLINGS_ARRANGED_SPECIESWISE_REASONS = "seedlings_arranged_specieswise_reasons";
    public static final String SEEDLINGS_ARRANGED_SCHEMEWISE = "seedlings_arranged_schemewise";
    public static final String SEEDLINGS_ARRANGED_SCHEMEWISE_REASONS = "seedlings_arranged_schemewise_reasons";

    public static final String CONTAINERS_INTACT_AND_IN_GOOD_CONDITION = "containers_intact_and_in_good_condition";
    public static final String CONTAINERS_INTACT_AND_IN_GOOD_CONDITION_REASONS = "containers_intact_and_in_good_condition_reasons";
    public static final String IRRIGATION_ADEQUATE_TO_REACH_THE_BOTTOM_CONTAINER = "irrigation_adequate_to_reach_the_bottom_container";
    public static final String IRRIGATION_ADEQUATE_TO_REACH_THE_BOTTOM_CONTAINER_REASONS = "irrigation_adequate_to_reach_the_bottom_container_reasons";
    public static final String SEEDLINGS_FREE_FROM_WEEDS = "seedlings_free_from_weeds";
    public static final String SEEDLINGS_FREE_FROM_WEEDS_REASONS = "seedlings_free_from_weeds_reasons";
    public static final String SEEDLINGS_RAISED_IN_TIME = "seedlings_raised_in_time";
    public static final String SEEDLINGS_RAISED_IN_TIME_REASONS = "seedlings_raised_in_time_reasons";
    public static final String SIDE_BUDS_BEING_NIPPED_IN_TIME = "side_buds_being_nipped_in_time";
    public static final String SIDE_BUDS_BEING_NIPPED_IN_TIME_REASONS = "side_buds_being_nipped_in_time_reasons";
    public static final String SEEDLINGS_SHIFTED_AND_GRADED_AS_PER_THE_PACKAGE_OF_PRACTICES = "seedlings_shifted_and_graded_as_per_the_package_of_practices";
    public static final String SEEDLINGS_STRIKING_ROOTS_INTO_THE_SOIL = "seedlings_striking_roots_into_the_soil";
    public static final String STAKES_PROVIDED_TO_SEEDLINGS_IN_10X16 = "stakes_provided_to_seedlings_in_10x16";
    public static final String STAKES_PROVIDED_TO_SEEDLINGS_IN_10X16_REASONS = "stakes_provided_to_seedlings_in_10x16_reasons";
    public static final String SEEDLING_QUALITY_UNIFORM_IN_THE_NURSERY = "seedling_quality_uniform_in_the_nursery";
    public static final String SEEDLING_QUALITY_UNIFORM_IN_THE_NURSERY_REASONS = "seedling_quality_uniform_in_the_nursery_reasons";
    public static final String SEEDLINGS_FREE_FROM_PESTS_AND_DISEASES = "seedlings_free_from_pests_and_diseases";
    public static final String SEEDLINGS_FREE_FROM_PESTS_AND_DISEASES_DETAILS = "seedlings_free_from_pests_and_diseases_details";
    public static final String ANY_OTHER_REMARKS_ON_NURSERY = "any_other_remarks_on_nursery";

    public static final String KFD_NURSERY_WORKS_SEED_BED = "kfd_nursery_works_seed_bed";
    public static final String SEED_BED_ID = "seed_bed_id";
    public static final String NUMBER_OF_BEDS = "number_of_beds";
    public static final String SPECIES_RAISED = "species_raised";
    public static final String TYPE_OF_BEDS = "type_of_beds";
    public static final String SAPLINGS_PER_BED = "saplings_per_bed";
    public static final String BED_SIZE = "bed_size";
    public static final String AVERAGE_HEIGHT_OF_THE_SPROUTS = "average_height_of_the_sprouts";

    public static final String KFD_NURSERY_WORKS_QUALOFSBEDS_GENERAL_OBSERVATIONS = "kfd_nursery_works_qualofsbeds_general_observations";
    public static final String QUALITY_OF_SEEDBEDS = "quality_of_seedbeds";
    public static final String DESIGNATED_PLACE_FOR_SEED_BEDS = "designated_place_for_seed_beds";
    public static final String BEDS_PROPERLY_ALIGNED = "beds_properly_aligned";
    public static final String IS_SEED_BED_PRESENT = "is_seed_bed_present";
    public static final String BEDS_IRRIGATED = "beds_irrigated";
    public static final String BEDS_IRRIGATED_HOW = "beds_irrigated_how";
    public static final String BEDS_IRRIGATED_HOW_OTHER = "beds_irrigated_how_other";
    public static final String SEEDBEDS_RAISED_IN_CURRENT_YEAR = "seedbeds_raised_in_current_year";
    public static final String SEEDBEDS_FREE_FROM_WEEDS = "seedbeds_free_from_weeds";
    public static final String SEEDBEDS_FREE_FROM_PESTS_AND_DISEASES = "seedbeds_free_from_pests_and_diseases";
    public static final String SPROUTING_GERMINATION_OPTIMAL = "sprouting_germination_optimal";
    public static final String SPROUTS_ON_THE_BEDS_UNIFORM_IN_SIZE_AND_QUALITY = "sprouts_on_the_beds_uniform_in_size_and_quality";
    public static final String SEEDBEDS_USED_ANNUALLY = "seedbeds_used_annually";
    public static final String NURSERY_KEPT_CLEAR_OF_WEEDS_WASTE_COMPOSTED = "nursery_kept_clear_of_weeds_waste_composted";
    public static final String WATER_USAGE_OPTIMISED_LEAKAGES_PREVENTED = "water_usage_optimised_leakages_prevented";
    public static final String LEFT_OVER_SEEDLINGS_PROPERLY_DISPOSED = "left_over_seedlings_properly_disposed";
    public static final String SEEDLING_BEDS_PROVIDED_WITH_DISPLAY_BOARDS = "seedling_beds_provided_with_display_boards";
    public static final String DISPLAYING_TOTAL_STOCK_OF_BEDS_AND_SEEDLINGS_SCHEME = "displaying_total_stock_of_beds_and_seedlings_scheme";
    public static final String OVERALL_WORKING_QUALITY_GRADING = "overall_working_quality_grading";
    public static final String OTHER_REMARKS_AND_OBSERVATIONS = "other_remarks_and_observations";

    public static final String KFD_TRANSITORY_WORKS = "kfd_transitory_works";
    public static final String TYPE_OF_WORK = "type_of_work";
    public static final String KFD_NURSERY_WORKS_BAGGED_SEEDLINGS = "kfd_nursery_works_bagged_seedlings";
    public static final String KFD_TRANSITORY_WORKS_COMPLIANCE_WITH_THE_APPROVAL = "kfd_transitory_works_compliance_with_the_approval";
    public static final String WORK_APPROVED_IN_APO_APPROVAL_DATE = "work_approved_in_apo_approval_date";


    /*AdvProtection Start*/
    public static final String TYPE_OF_PROTECTION = "type_of_protection";
    public static final String BOUNDARY_COST = "boundary_cost";
    public static final String EFFECTIVENESS_OF_CLM = "effectiveness_of_clm";
    public static final String EFFECTIVENESS_OF_CLM_REASONS = "effectiveness_of_clm_reasons";
    public static final String BARBED_WIRE_CONDITION = "barbed_wire_condition";
    public static final String BRUSHWOOD_MATERIALS_USED = "brushwood_materials_used";
    public static final String DEPTH_OF_CPT = "depth_of_cpt";
    public static final String BREADTH_OF_CPT = "breath_of_cpt";
    public static final String CPT_SIZE = "cpt_size";
    public static final String NO_OF_STRANDS = "no_of_strands";
    public static final String TREE_CONDITION = "tree_condition";
    public static final String PRESENT_CONDITION = "present_condition";
    public static final String MOUNT_SOWING = "mount_sowing";
    public static final String MOUNT_SOWING_RESULT = "mount_sowing_result";
    public static final String MATERIALS_USED = "materials_used";
    public static final String DIFFERENCE_R_E = "difference_r_e";
    public static final String DETAILS_R_E = "details_r_e";
    public static final String OTHER_TREE_GUARDS = "other_tree_guards";
    public static final String OTHER_TYPE_OF_PROTECTION = "other_type_of_protection";
    /*AdvProtection Stop*/

    /*Evaluation of Procurement*/
    public static final String MODE_OF_PROCUREMENT = "mode_of_procurement";
    public static final String MODE_OF_PROCUREMENT_OTHERS = "mode_of_procurement_others";


    public static final String KFD_TRANSITORY_WORKS_FIRE_PROTECTION_WORKS = "kfd_transitory_works_fire_protection_works";
    public static final String TOTAL_LENGTH_KMS = "total_length_kms";
    public static final String TOTAL_HEIGHT = "total_height";


    public static final String KFD_TRANSITORY_WORKS_FIRE_VIEW_LINES = "kfd_transitory_works_fire_view_lines";


    public static final String KFD_TRANSITORY_WORKS_FOREST_PROTECTION_CAMP = "kfd_transitory_works_forest_protection_camp";

    public static final String KFD_NURSERY_WORKS = "kfd_nursery_works";


    public static final String TOTAL_SPECIES_COUNT = "total_species_count";
    public static final String EVALUATOR_NAME = "evaluator_name";
    public static final String EVALUATOR_PASSWORD = "evaluator_password";
    public static final String FIRE_AREA = "fire_area";
    public static final String FIRE_SEEDLING = "fire_seedling";
    public static final String PEST_AREA = "pest_area";
    public static final String PEST_SEEDLING = "pest_seedling";
    public static final String GRAZING_AREA = "grazing_area";
    public static final String GRAZING_SEEDLING = "grazing_seedling";
    public static final String WILDLIFE_AREA = "wild_life_area";
    public static final String ENROACHMENT_AREA = "enroahment_area";
    public static final String ENROACHMENT_SEEDLING = "enroachment_seedling";
    public static final String CAUSE_OF_DAMAGE_OTHERS_AREA = "cause_of_damage_others_area";
    public static final String CAUSE_OF_DAMAGE_OTHERS_SEEDLING = "cause_of_damage_others_seedling";
    public static final String WILDLIFE_SEEDLING = "wild_life_seedling";

    public static final String PLANTING_DENSITY_HA = "planting_density_ha";
    public static final String AUTOMATIC_LATITUDE = "automatic_latitude";
    public static final String PHOTOS_COUNT = "photos_count";
    public static final String LEGAL_STATUS_OF_LAND_OTHER_DETAILS = "legal_status_of_land_other_details";
    public static final String SCHEME_PROVISION_YES = "scheme_provision_yes";
    public static final String SCHEME_PROVISION_NO_REASONS = "scheme_provision_no_reasons";
    public static final String SAMPLEPLOTS_PHOTOS_COUNT = "sampleplots_photos_count";
    public static final String WHEN_WORK_COMPLETED_MONTH = "when_work_completed_month";
    public static final String TIME_TAKEN_MONTHS = "time_taken_months";
    public static final String FOUR_CORNERS_MARKING_METHOD = "four_corners_marking_method";
    public static final String USER_NAME = "user_name";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String APP_STATUS = "app_status";
    public static final String TABLE_SDP = "kfd_sdp";
    public static final String TABLE_BENEFICIARY = "kfd_sdp_beneficiaries";
    public static final String WHETHER_WORK_EXISTS_NOW = "whether_work_exists_now";
    public static final String BENEFICIARY_AVAILABLE_ATTIME_OF_VISIT = "beneficiary_available_attime_of_visit";
    public static final String BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON = "beneficiary_notavailable_attime_visit_reason";
    public static final String BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON_OTHER = "beneficiary_notavailable_attime_visit_reason_other";
    public static final String COMPLETION_CERTIFICATE_TIMESTAMP_AVAILABLE = "completion_certificate_timestamp_available";
    public static final String CHECK_MEASUREMENT_TIMESTAMP_AVAILABLE = "check_measurement_timestamp_available";
    public static final String WORK_APPROVED_IN_APO_DATE_AVAILABLE = "work_approved_in_apo_date_available";
    public static final String WORK_APPROVED_IN_APO_SLNO_AVAILABLE = "work_approved_in_apo_slno_available";
    public static final String COST_PAID_APPLICABLE = "cost_paid_applicable";
    public static final String PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_APPLICABLE = "planting_total_expenditure_until_now_applicable";
    public static final String CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1_APPLICABLE = "cash_incentive_received_from_kfd_under_kapy_year1_applicable";
    public static final String CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3_APPLICABLE = "cash_incentive_received_from_kfd_under_kapy_year3_applicable";
    public static final String DATABASE_NAME = "kfd_survey";
    public static final String KFD_CIRCLES_MASTER = "kfd_circles_master";
    public static final String KFD_CIRCLES_MASTER_2018 = "kfd_circles_master_2018";
    public static final String KFD_RANGE_MASTER = "kfd_range_master";
    public static final String RANGE_CODE = "RANGE_ID";
    public static final String EXPERT_NAME = "expert_name";
    public static final String KFD_SETTINGS_MASTER = "kfd_settings_master";
    public static final String LAND_HOLDING_ACRE = "land_holding_acre";
    public static final String ECONOMICAL_STATUS = "economical_status";
    public static final String ASSESTS_AVAILABLE = "assests_available";
    public static final String NO_OF_MEMBERS_IN_FAMILY = "no_of_members_in_family";
    public static final String NUMBER_OF_MEMBERS = "number_of_members";
    public static final String SEEDLINGS_PURCHASE_COUNT = "seedlings_purchase_count";
    public static final String TOTAL_COST = "total_cost";
    public static final String TYPE_OF_PLANTING = "type_of_planting";
    public static final String TYPE_OF_PLANTING_MAINCROP = "type_of_planting_maincrop";
    public static final String AGE = "age";
    public static final String AVERAGE_SPACEMENT_METERS = "aver" + AGE + "_spacement_meters";
    public static final String SURVEY_NUMBERS_WHERE_PLANTED = "survey_numbers_where_planted";
    public static final String SPECIES_SDP_BENEFICIARY = "species_sdp_beneficiary";
    public static final String FINISHED_POSITION = "finishedPosition";
    public static final String COMPLETED_POSITION = "completed_position";
    public static final String SPECIES_OTHER = "species_other";
    public static final String SEEDLING_PROCURED = "seedling_procured";
    public static final String GPS_LONGITUDE = "gps_longitude";

    ////Devansh

    public static final String DOES_PLANTATION_HAS_MULTIPLE_BLOCK = "does_plantation_has_multiple_blocks";
    public static final String PLANTATION_TYPE = "plantationtype";
    public static final String NO_OF_BLOCK = "No_of_blocks";
    public static final String BLOCK1_AREA = "block1_area";
    public static final String BLOCK1_TYPE = "block1_type";
    public static final String BLOCK2_AREA = "block2_area";
    public static final String BLOCK2_TYPE = "block2_type";
    public static final String BLOCK3_AREA = "block3_area";
    public static final String BLOCK3_TYPE = "block3_type";
    public static final String BLOCK4_AREA = "block4_area";
    public static final String BLOCK4_TYPE = "block4_type";
    public static final String BLOCK5_AREA = "block5_area";
    public static final String BLOCK5_TYPE = "block5_type";

    public static final String BLOCK1_PLOTS = "block1_plots";
    public static final String BLOCK2_PLOTS = "block2_plots";
    public static final String BLOCK3_PLOTS = "block3_plots";
    public static final String BLOCK4_PLOTS = "block4_plots";
    public static final String BLOCK5_PLOTS = "block5_plots";
    //intellis
    public static final String PLANTING_GPS_LONGITUDE = "planting_" + GPS_LONGITUDE;
    public static final String GPS_LATITUDE = "gps_latitude";
    public static final String PLANTING_GPS_LATITUDE = "planting_" + GPS_LATITUDE;
    public static final String GPS_ALTITUDE = "gps_altitude";

    public static final String GPS_LATLONG_COLLECTION = "gps_latlong_collection";

    public static final String GPS_LATLONG_COLLECTION_TWO = "gps_latlong_collection_block_two";
    public static final String GPS_LATLONG_COLLECTION_THREE = "gps_latlong_collection_block_three";
    public static final String GPS_LATLONG_COLLECTION_FOUR = "gps_latlong_collection_block_four";
    public static final String GPS_LATLONG_COLLECTION_FIVE = "gps_latlong_collection_block_five";
    public static final String GPS_LATLONG_COLLECTION_ALL = "gps_latlong_collection_all";


    public static final String GPS_SAMPLEPLOT_COLLECTION = "gps_sampleplot_collection";
    public static final String GPS_SAMPLEPLOT_COLLECTION_TWO = "gps_sampleplot_collection_two";
    public static final String GPS_SAMPLEPLOT_COLLECTION_THREE = "gps_sampleplot_collection_three";
    public static final String GPS_SAMPLEPLOT_COLLECTION_FOUR = "gps_sampleplot_collection_four";
    public static final String GPS_SAMPLEPLOT_COLLECTION_FIVE = "gps_sampleplot_collection_five";
    public static final String GPS_SAMPLEPLOT_COLLECTION_ALL = "gps_sampleplot_collection_all";


    public static final String BLOCK_NUMBER_NEW = "block_number_new";


    public static final String GPS_SAMPLEPLOT_COLLECTION_BLOCK_NO = GPS_SAMPLEPLOT_COLLECTION + "_block_no";

    public static final String GPS_MEASUREMENT = "gps_measurement";
    public static final String GPS_MEASUREMENT_TWO = "gps_measurement_two";
    public static final String GPS_MEASUREMENT_THREE = "gps_measurement_three";
    public static final String GPS_MEASUREMENT_FOUR = "gps_measurement_four";
    public static final String GPS_MEASUREMENT_FIVE = "gps_measurement_five";
    public static final String GPS_MEASUREMENT_ALL = "gps_measurement_all";


    public static final String PLANTING_GPS_ALTITUDE_METERS = "planting_" + GPS_ALTITUDE + "_meters";
    public static final String PLANTING_IRRIGATION_LEVEL = "planting_irrigation_level";
    public static final String PLANTING_IRRIGATION_LEVEL_OTHER_DETAILS = PLANTING_IRRIGATION_LEVEL + "_other_details";
    public static final String PLANTING_IRRIGATION_METHOD = "planting_irrigation_method";
    public static final String PLANTING_FERTILIZE_USED = "planting_fertilize_used";
    public static final String PLANTING_FERTILIZE_USED_DETAILS = PLANTING_FERTILIZE_USED + "_details";
    public static final String PLANTING_PRUNINGE_DONE = "planting_pruninge_done";
    public static final String PLANTING_OTHER_TREATMENT_DETAILS = "planting_other_treatment_details";
    public static final String PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_RS = "planting_total_expenditure_until_now_rs";
    /*Mortality of Seedling*/
    public static final String MORTALITY_OF_SEEDLING = "mortality_of_seedling";
    public static final String HOW_IT_WILL_BE_REDUCED = "how_it_will_be_reduced";
    public static final String PAYMENT_RECEIVED_FROM_MGNREEGS_YEAR = "payment_received_from_mgnreegs_year";
    public static final String PAYMENT_RECEIVED_FROM_MGNREEGS_RS = "payment_received_from_mgnreegs_rs";
    public static final String SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_YEAR = "subsidy_for_micro_irrigation_from_other_depts_year";
    public static final String SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_RS = "subsidy_for_micro_irrigation_from_other_depts_rs";
    public static final String CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1 = "cash_incentive_received_from_kfd_under_kapy_year1";
    public static final String CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2 = "cash_incentive_received_from_kfd_under_kapy_year2";
    public static final String CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3 = "cash_incentive_received_from_kfd_under_kapy_year3";
    public static final String CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_TOTAL = "cash_incentive_received_from_kfd_under_kapy_total";
    public static final String MODE_PAYMENT = "mode_payment";
    public static final String TOTAL_INCENTIVE = "total_incentive";
    public static final String INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED = "incentive_any_other_rewards_awards_received";
    public static final String INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED_DETAILS = INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED + "_details";
    public static final String DID_YOU_FACE_PROBLEM_IN_PROCURING_SEEDLING = "did_you_face_problem_in_procuring_seedling";
    public static final String DETAILS_OF_PROBLEM_FACED_IN_PROCURING_SEEDLING = "details_of_problem_faced_in_procuring_seedling";
    public static final String SPECIES_AS_PER_REQUIREMENT = "species_as_per_requirement";
    public static final String SPECIES_NOT_REQUIREMENT_REASONS = "species_not_requirement_reasons";
    public static final String SPECIES_SUITABLE_TO_AREA = "species_suitable_to_area";
    public static final String ARE_YOU_SATISFIED_WITH_SEEDLING_QUALITY = "are_you_satisfied_with_seedling_quality";
    public static final String REASONS_FOR_DISSATISFACTION_WITH_SEEDLING_QUALITY = "reasons_for_dissatisfaction_with_seedling_quality";
    public static final String ANY_SOCIAL_COST_IN_RAISING_TREES = "any_social_cost_in_raising_trees";
    public static final String DETAILS_OF_SOCIAL_COST_IN_RAISING_TREES = "details_of_social_cost_in_raising_trees";
    public static final String DID_YOU_BUY_SEEDLINGS_FROM_PVT_NURSERIES = "did_you_buy_seedlings_from_pvt_nurseries";
    public static final String DETAILS_OF_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES = "details_of_seedlings_purchase_from_pvt_nurseries";
    public static final String SEEDLINGS_PERFORMANCE_COMPARED_TO_KFD_SEEDLINGS = "seedlings_performance_compared_to_kfd_seedlings";
    public static final String SUGGESTIONS_TO_IMPROVE_AGRO_FORESTRY = "suggestions_to_improve_agro_forestry";
    public static final String INTERESTED_IN_BUYING_MORE_SEEDLINGS_FROM_KFD = "interested_in_buying_more_seedlings_from_kfd";
    public static final String USER_INTERESTED_SPECIES = "user_interested_species";
    public static final String TABLE_BENEFICIARY_SEEDLING = "kfd_sdp_bnfcries_sdlng_performance";
    public static final String BENEFICIARY_ID = "beneficiary_id";
    public static final String NAME = "name";
    public static final String NAME_OF_THE_SPECIES = NAME + "_of_the_species";
    public static final String NUMBER_OF_SEEDLINGS_PLANTED = "number_of_seedlings_planted";
    public static final String NUMBER_OF_SEEDLINGS_SURVIVING = "number_of_seedlings_surviving";
    public static final String AVERAGE_COLLAR_GROWTH_CMS = "aver" + AGE + "_collar_growth_cms";
    public static final String AVERAGE_HEIGHT_METERS = "aver" + AGE + "_height_meters";
    public static final String HEALTH_AND_VIGOUR = "health_and_vigour";
    public static final String ECONOMICAL_VALUE = "economical_value";
    public static final String DETAILS_OF_RETURNS = "details_of_returns";
    public static final String FATHER_NAME = "father_" + NAME;
    public static final String AADHAR_NUMBER = "aadhar_number";
    public static final String SEX = "sex";
    public static final String EDUCATION = "education";
    public static final String TOTAL_COUNT = "total_count";
    public static final String TOTAL_COUNT_SURVIVED = "total_count_survived";
    public static final String NO_OF_EMPTY_PITS = "no_of_empty_pits";
    public static final String DIBBLED_PERCENTAGE = "dibbled_percentage";
    public static final String SEEDLING_PERCENTAGE = "seedling_percentage";
    public static final String BENEFICIARIES_TOTAL_COUNT = "beneficiaries_" + TOTAL_COUNT;
    public static final String HOBLI_NAME = "hobli_name";
    public static final String GRAMA_PANCHAYAT_NAME = "grama_panchayat_name";
    public static final String PANCHAYAT_NAME = "PANCHAYAT_NAME";
    public static final String PANCHAYAT_CODE = "PANCHAYAT_CODE";
    public static final String VILLAGE_NAME = "village_name";
    public static final String VILLAGE_NAME_NEW = "VILLAGE_NAME";
    public static final String VILLAGE_CODE = "VILLAGE_CODE";
    public static final String HAMLET_NAME = "hamlet_name";
    public static final String CONSTITUENCY_NAME = "constituency_name";
    public static final String CONSTITUENCY_ID = "LA_ID";
    public static final String SURVEYOR_NAME = "surveyor_name";
    public static final String SURVEYOR_ASSISTANT_NAME = "surveyor_assistant_name";
    public static final String TABLE_PLANTATION = "kfd_plnt_smplng";
    public static final String TABLE_ADVANCEWORK = "kfd_advancework";
    public static final String TABLE_LOGIN = "kfd_login_details";
    public static final String SAMPLE_PLOT_STATUS = "sample_plot_status";
    public static final String EMPTY_PIT_STATUS = "empty_pit_status";
    public static final String DRAW_MAP_STATUS = "draw_map_status";
    public static final String FORM_FILLED_STATUS = "form_filled_status";
    public static final String REASON_FOR_REPLANTING = "reason_for_replanting";
    public static final String DETAILS_OF_PERMISSION_FOR_REPLANTING = "details_of_permission_for_replanting";
    public static final String WAS_PERMISSION_OBTAINED_FOR_REPLANTING = "was_permission_obtained_for_replanting";
    public static final String APPROXIMATE_SAPLINGS_ALIVE_TODAY = "approximate_saplings_alive_today";
    public static final String YEAR_OF_PREVIOUS_PLANTING = "year_of_previous_planting";
    public static final String WAS_THE_SITE_PREVIOUSLY_PLANTED = "was_the_site_previously_planted";
    public static final String PLANTATION_EVLTN_NATURE_OF_TERRAIN = "plantation_evltn_nature_of_terrain";
    public static final String NAME_OF_THE_SCHEME = "name_of_the_scheme";
    public static final String PB_SIZE = "pb_size";
    public static final String NUMBER_AS_PER_RECORDS = "number_as_per_records";
    public static final String NUMBER_ACTUALLY_FOUND = "number_actually_found";
    public static final String DIFFERENCE = "difference";
    public static final String AVERAGE_SEEDLING_HEIGHT_METER = "average_seedling_height_meter";
    public static final String PLANTATION_TOTEXP_MNTNCE_TOTAL = "plantation_totexp_mntnce_total";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR8 = "plantation_totexp_mntnce_year8";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR7 = "plantation_totexp_mntnce_year7";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR6 = "plantation_totexp_mntnce_year6";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR5 = "plantation_totexp_mntnce_year5";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR4 = "plantation_totexp_mntnce_year4";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR3 = "plantation_totexp_mntnce_year3";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR2 = "plantation_totexp_mntnce_year2";
    public static final String PLANTATION_TOTEXP_MNTNCE_YEAR1 = "plantation_totexp_mntnce_year1";
    public static final String PLANTATION_TOTEXP_EARTHWORK = "plantation_totexp_earthwork";
    public static final String PLANTATION_SANCTN_DATE_FOR_EARTHWORK = "plantation_sanctn_date_for_earthwork";
    public static final String PLANTATION_SANCTN_DATE_FOR_RAISINGSEEDLING = "plantation_sanctn_date_for_raisingseedling";
    public static final String PLANTATION_SANCTN_DATE_FOR_RAISINGPLANTS = "plantation_sanctn_date_for_raisingplants";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR1 = "plantation_sanctn_date_for_mntnce_year1";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR2 = "plantation_sanctn_date_for_mntnce_year2";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR3 = "plantation_sanctn_date_for_mntnce_year3";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR4 = "plantation_sanctn_date_for_mntnce_year4";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR5 = "plantation_sanctn_date_for_mntnce_year5";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR6 = "plantation_sanctn_date_for_mntnce_year6";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR7 = "plantation_sanctn_date_for_mntnce_year7";
    public static final String PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR8 = "plantation_sanctn_date_for_mntnce_year8";
    public static final String PLANTATION_EARTHWORK_BOUNDARY_PROTECTION = "plantation_earthwork_boundary_protection";
    public static final String PLANTATION_TOTEXP_RAISINGSEEDLING = "plantation_totexp_raisingseedling";
    public static final String PLANTATION_TOTEXP_RAISINGPLANTATION = "plantation_totexp_raisingplantation";
    public static final String ANY_DAMAGE_TO_PLANTATION_OBSERVED = "any_damage_to_plantation_observed";
    public static final String CAUSE_OF_DAMAGE = "cause_of_damage";
    public static final String PLANTATION_OPERATIONS_NOT_ASPER_PRESCRIPTION_VARIATION = "plantation_operations_not_asper_prescription_variation";
    public static final String PLANTATION_OPERATIONS_REASONS = "plantation_operations_reasons";
    public static final String PLANTATION_OPERATIONS_ASPER_PRESCRIPTION = "plantation_operations_asper_prescription";
    public static final String PLANTATION_OPERATIONS_ASPER_MODEL = "plantation_operations_asper_model";
    public static final String PLANTATION_OBJECTIVES_ASPER_MODEL = "plantation_objectives_asper_model";
    public static final String NO_OF_WATCHERS_PROVIDED = "no_of_watchers_provided";
    public static final String NO_OF_YEARS_MAINTAINED = "no_of_years_maintained";
    public static final String MAIN_SPECIES_PLANTED = "main_species_planted";
    public static final String SPECIES_ID = "species_id";
    public static final String TYPE_OF_EARTH_WORK_DONE = "type_of_earth_work_done";
    public static final String TYPE_OF_EARTH_WORK_DONE_OTHER_DETAILS = "type_of_earth_work_done_other_details";
    public static final String PITSIZE = "Pit_size";
    public static final String PIT_ESPACEMENT = "Pit_espacement";
    public static final String NO_OF_PITS = "no_of_pits";
    public static final String PITS_COUNTED = "pits_counted";
    public static final String TRENCH_SIZE = "Trench_size";
    public static final String TRENCH_ESPACEMENT = "Trench_espacement";
    public static final String NO_OF_TRENCHS = "no_of_trenches";
    public static final String TRENCHS_COUNTED = "trenchs_counted";
    public static final String PIT_IN_PIT_SIZE = "Pit_in_Pit_size";
    public static final String PIT_IN_PIT_ESPACEMENT = "Pit_in_Pit_espacement";
    public static final String NO_OF_PIT_IN_PIT = "no_of_pit_in_pit";
    public static final String PIT_IN_PIT_COUNTED = "pit_in_pit_counted";
    public static final String RIPPING_SIZE = "Ripping_size";
    public static final String RIPPING_ESPACEMENT = "Ripping_espacement";
    public static final String NO_OF_RIPLINE = "no_of_riplines";
    public static final String RIPLINE_COUNTED = "ripline_counted";
    public static final String OTHERS_SIZE = "Others_size";
    public static final String OTHERS_ESPACEMENT = "Others_espacement";
    public static final String OTHERS_NO_OF_UNITS = "Others_no_of_units";
    public static final String REASON_FOR_PLANTING = "reason_for_planting";
    public static final String REASON_FOR_PLANTING_OTHERS = "reason_for_planting_others";
    public static final String OTHER_COUNTED = "other_pit_counted";
    public static final String ADV_SAMPLEPLOT_REMARKS = "adv_sampleplot_remarks";
    public static final String PLANTATION_MODEL = "plantation_model";
    public static final String PLANTATION_MODEL_ID = "plantation_model_id";
    public static final String SCHEME_NAME = "scheme_name";
    public static final String SCHEME_ID = "scheme_id";
    public static final String MODEL_NAME = "model_name";
    public static final String NEW_MODEL_NAME = "Model";
    public static final String NET_PLANTATION_AREA_HA = "net_plantation_area_ha";
    public static final String GROSS_PLANTATION_AREA_HA = "gross_plantation_area_ha";
    public static final String AVERAGE_ANNUAL_RAINFALL_MM = "average_annual_rainfall_mm";
    public static final String VFC_APPLICABLE = "vfc_applicable";
    public static final String NAME_OF_THE_VFC = "name_of_the_vfc";
    public static final String IS_JFPM_RAISED = "is_jfpm_raised";
    public static final String MAINTAINENACE_FRESH = "maintenance_fresh";
    public static final String LEGAL_STATUS_OF_LAND = "legal_status_of_land";
    public static final String SCHEME_PROVISION = "scheme_provision";
    public static final String TABLE_CONTROL_PLOT_INVENTORY = "kfd_plnt_smplng_anrmodel_inventory";
    public static final String TABLE_SAMPLE_PLOT_INVENTORY = "kfd_plnt_smplng_inventory_details";
    public static final String TABLE_ADD_SPECIES = "kfd_plnt_species";
    public static final String TABLE_SAMPLEPLOT_SPECIES = "kfd_plnt_sampleplot_species";
    public static final String TABLE_PROTECTION = "kfd_plnt_protection";
    public static final String TABLE_ADV_PROTECTION = "kfd_adv_protection";
    public static final String PROTECTION_ID = "protection_id";
    public static final String TABLE_ADD_BENEFICIARY_SPECIES = "kfd_beneficiary_species";
    public static final String AVERAGE_COLLAR_GIRTH = "average_collar_girth";
    public static final String STATE_OF_HEALTH = "state_of_health";
    public static final String AVERAGE_GBH_METERS = "average_gbh_meters";
    public static final String SPECIES_NAME = "species_name";
    public static final String KNOWN_FAILURES = "known_failures";
    public static final String CALCULATED_FAILURES = "calculated_failures";
    public static final String SPECIES_AVAILABILITY = "species_availability";
    public static final String PROTECTION_MEASURE_AVAILABILITY = "protection_measure_availability";
    public static final String SPECIES_SIZE = "species_size";
    public static final String OTHER_SPECIES = "other_species_name";
    public static final String PART_TYPE = "part_type";
    public static final String SAMPLE_PLOT_SEEDLING = "Seedling";
    public static final String SAMPLE_PLOT_SEEDLING_STATUS = "sample_plot_seedling_status";
    public static final String SEEDLING_DETAIL_STATUS = "seedling_detail_status";
    public static final String SPECIES_COMPLETED_STATUS = "species_completed_status";
    public static final String SAMPLE_PLOT_SEED_DIBBLING = "Seed Dibbling";
    public static final String SAMPLE_PLOT_SEED_DIBBLING_STATUS = "sample_plot_seed_dibbling_status";
    public static final String SAMPLE_PLOT_ID = "sample_plot_id";
    public static final String TABLE_CONTROL_PLOT_MASTER = "kfd_plnt_smplng_anrmodel_master";
    public static final String TABLE_SAMPLE_PLOT_MASTER = "kfd_plnt_smplng_inventory_master";
    public static final String TABLE_ADV_SAMPLE_PLOT_MASTER = "kfd_plnt_smplng_adv_inventory_master";
    public static final String CLOSED_OR_OPEN_AREA = "closed_or_open_area";
    public static final String CONTROL_PLOT_TYPE = "control_plot_type";
    public static final String DIRECTION_IN_WHICH_CONTROL_PLOT_LOCATED = "direction_in_which_control_plot_located";
    public static final String DISTANCE_FROM_PLANTATION_BOUNDRY = "distance_from_plantation_boundry";
    public static final String ANRMODEL_ID = "anrmodel_id";
    public static final String NUMBER_OF_EMPTY_PITS_TRENCHES_FOUND = "number_of_empty_pits_trenches_found";
    public static final String SAMPLE_PLOT_PHOTOGRAPHED_ALONG_WITH_EXPERT = "sample_plot_photographed_along_with_expert";
    public static final String SAMPLE_PLOT_INVENTORY_DONE_ON = "sample_plot_inventory_done_on";
    public static final String SAMPLE_PLOT_INVENTORY_DONE_BY = "sample_plot_inventory_done_by";
    public static final String SAMPLE_PLOT_ALTITUDE = "sample_plot_altitude";
    public static final String SAMPLE_PLOT_LATITUDE = "sample_plot_latitude";
    public static final String SAMPLE_PLOT_LONGITUDE = "sample_plot_longitude";
    public static final String SAMPLE_PLOT_NUMBER = "sample_plot_number";
    public static final String SAMPLE_PLOT_SUITABLE = "sample_plot_suitable";
    public static final String SAMPLE_PLOT_NOT_SUITABLE_REASONS = "sample_plot_not_suitable_reasons";
    public static final String TABLE_SMC_SAMPLING_DETAILS = "kfd_plnt_smplng_smc_details";
    public static final String TABLE_SMC_LIST = "kfd_plnt_smplng_smc_list";
    public static final String TABLE_ADV_SMC_LIST = "kfd_adv_smc_list";
    public static final String TABLE_BENEFIT_LIST = "kfd_scp_tsp_benefit_list";
    public static final String TABLE_OTHER_SMC_LIST = "kfd_plnt_smplng_other_smc_list";
    public static final String TABLE_ADV_OTHER_SMC_LIST = "kfd_adv_other_smc_list";
    public static final String TABLE_SMC_SAMPLING_MASTER = "kfd_plnt_smplng_smc_master";
    public static final String TABLE_ADV_SMC_MASTER = "kfd_adv_smc_master";
    public static final String TABLE_VFC_SAMPLING = "kfd_plnt_smplng_vfc";
    public static final String TABLE_ADV_VFC_SAMPLING = "kfd_plnt_smplng_adv_vfc";
    public static final String SMC_REMARKS = "smc_remarks";
    public static final String IS_SMC_SERVING_THE_PURPOSE = "is_smc_serving_the_purpose";
    public static final String IS_SMC_SERVING_THE_PURPOSE_NO = "is_smc_serving_the_purpose_no";
    public static final String BREACHED_SMC = "breached_smc";
    public static final String BREACHED_SMC_YES = "breached_smc_yes";
    public static final String BUILT_WITHIN_BOUNDARY = "built_within_boundary";
    public static final String BUILT_WITHIN_BOUNDARY_NO = "built_within_boundary_no";
    public static final String SMC_CONSTRUCTION_QUALITY = "smc_construction_quality";
    public static final String DETAILS_OF_DIFF_BTWN_BILLED_AND_ACUTAL_WORK = "details_of_diff_btwn_billed_and_acutal_work";
    public static final String ANY_DIFF_BTWN_BILLED_AND_ACUTAL_WORK = "any_diff_btwn_billed_and_acutal_work";
    public static final String SMC_STRUCTURE_TOTALVOLUME = "smc_structure_totalvolume";
    public static final String SMC_STRUCTURE_BREADTH = "smc_structure_breadth";
    public static final String SMC_STRUCTURE_LENGTH = "smc_structure_length";
    public static final String SMC_STRUCTURE_DEPTH = "smc_structure_depth";
    public static final String IS_LOCATION_APPROPRIATE = "is_location_appropriate";
    public static final String IS_LOCATION_APPROPRIATE_REASON = "is_location_appropriate_reason";
    public static final String TYPE_OF_STRUCTURE = "type_of_structure";
    public static final String SMC_ID = "smc_id";
    public static final String SMC_STATUS = "smc_status";
    public static final String OTHER_SMC_ID = "other_smc_id";
    public static final String IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL = "is_dispersal_smc_accordance_with_rainfall";
    public static final String IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL_DETAILS = "is_dispersal_smc_accordance_with_rainfall_details";
    public static final String SMC_WORK_ANY_OTHER_REMARKS = "smc_work_any_other_remarks";
    public static final String OTHER_SMC_WORKS = "other_smc_works";
    public static final String SMC_WORK_STRUCTURE_DAMAGED_DETAILS = "smc_work_structure_damaged_details";
    public static final String ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED = "any_smc_work_structure_found_damaged";
    public static final String ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED_YES = "any_smc_work_structure_found_damaged_list";
    public static final String IS_SMC_STRUCTURE_SERVING_INTENDED_PURPOSE = "is_smc_structure_serving_intended_purpose";
    public static final String SMC_STRUCTURE_NOT_SERVING_INTENDED_PURPOSE_REASONS =
            "smc_structure_not_serving_intended_purpose_reasons";
    public static final String REASON_FOR_INAPPROPRIATE_LOCATION_OF_SMC_WORK = "reason_for_inappropriate_location_of_smc_work";
    public static final String IS_LOCATION_OF_SMC_WORK_OK = "is_location_of_smc_work_ok";
    public static final String IS_LOCATION_OF_SMC_WORK_INAPPROPRIATE_LIST = "is_location_of_smc_work_inappropriate_list";
    public static final String REASON_FOR_NOT_FOLLOWING_SMC_WATERSHED_PATTERN = "reason_for_not_following_smc_watershed_pattern";
    public static final String SMC_TREATMENT_FOLLOWED_WATERSHED_PATTERN = "smc_treatment_followed_watershed_pattern";
    public static final String SMC_PLANTATION_AREA_TREATED_COMPLETELY = "smc_plantation_area_treated_completely";
    public static final String ALL_SMC_STRUCTURES_BUILT_IN_FARM = "all_smc_structures_built_in_farm";
    public static final String ALL_SMC_STRUCTURES_NOT_BUILT_IN_FARM = "all_smc_structures_not_built_in_farm_list";
    public static final String WHERE_IT_WAS_DONE = "where_it_was_done";
    public static final String SMC_WORK_EXPENDITURE_AS_PER_NORM = "smc_work_expenditure_as_per_norm";
    public static final String TOTAL_BUDGET_FOR_SMC_WORK = "total_budget_for_smc_work";
    public static final String IS_SELECT_PROPER = "is_select_proper";
    public static final String SMC_STRUCTURE_COST = "smc_structure_cost";
    public static final String SMC_AVAILABILITY = "smc_availability";

    /*Regeneration plot*/
    public static final String ROOTSTOCK_AVAILABLE = "rootstock_available";
    public static final String REGENERATION_AVAILABLE = "regeneration_available";
    public static final String STEMS_WITH_COLLAR_2_10CM = "stems_with_collar_2_10cm";
    public static final String NTFP_SPECIES = "ntfp_species";
    public static final String WHICH_PART_USED_AS_NTFP = "which_part_used_as_ntfp";
    public static final String APPROX_WEIGHT = "approx_weight";
    public static final String SHRUB_VEGETATION_REMARKS = "shrub_vegetation_remarks";
    public static final String TREE_SPECIES_PRESENT = "tree_species_present";
    public static final String SPECIES_NAME_INVENTORY = "species_name_inventory";
    public static final String SPECIES_OTHER_INVENTORY = "species_other_inventory";
    public static final String STEMS_WITH_COLLAR_ABOVE_10CM = "stems_with_collar_above_10cm";
    public static final String GBH_SPECIES_ABOVE_10CM = "gbh_species_above_10cm";
    public static final String AVERAGE_HEIGHT_METERS_INVENTORY = "average_height_meters_inventory";


    /*ADD Other SMC*/
    public static final String YEAR_OF_WORK = "year_of_work";
    public static final String EXPENDITURE_INCURRED = "expenditure_incurred";
    public static final String STATUS_OF_SMC = "status_of_smc";
    /*ADD Other SMC*/
    public static final String VFC_ID = "vfc_id";
    public static final String VFC_ANY_OTHER_SPECIFY = "vfc_any_other_specify";
    public static final String POST_MAINTENANCE_DONE_BYVFC_WITHT_HEIR_OWN_FUNDS = "post_maintenance_done_byvfc_witht_heir_own_funds";
    public static final String VFC_CARRIED_OUT_COMPLIMENTARY_WORKS_LIKE_SMC = "vfc_carried_out_complimentary_works_like_smc";
    public static final String DEPT_PROVIDED_FUNDS_VFC_RAISED_PLANTATION = "dept_provided_funds_vfc_raised_plantation";
    public static final String VFC_CONTRIBUTED_VFD_FUND_FOR_PLANTING_WORK = "vfc_contributed_vfd_fund_for_planting_work";
    public static final String VFC_CONTRIBUTED_VFD_FUND_TOTAL = "vfc_contributed_vfd_fund_total";
    public static final String VFC_MEMBERS_SUPERVISED_PLANTATION_WORK = "vfc_members_supervised_plantation_work";
    public static final String VFC_PROVIDED_LABOUR_FOR_PLANTATION_WORK_PAYMENT = "vfc_provided_labour_for_plantation_work_payment";
    public static final String VFC_APPROVED_PLANTING_WORK_PROPOSAL = "vfc_approved_planting_work_proposal";
    public static final String VFC_INVOLVED_POST_MAINTENANCE_STAGE = "vfc_involved_post_maintenance_stage";
    public static final String VFC_INVOLVED_MAINTENANCE_STAGE = "vfc_involved_maintenance_stage";
    public static final String VFC_INVOLVED_PLANTING_STAGE = "vfc_involved_planting_stage";
    public static final String VFC_INVOLVED_ADVANCED_WORK_STAGE = "vfc_involved_advanced_work_stage";
    public static final String VFC_INVOLVED_LOGGING = "vfc_involved_logging";
    public static final String IS_VFC_INVOLVED_IN_PLANTATION_ACTIVITY = "is_vfc_involved_in_plantation_activity";

    public static final String IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION = "is_planting_in_accordance_with_micro_plan_prescription";
    public static final String IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION_REASONS = "is_planting_in_accordance_with_micro_plan_prescription_reasons";
    public static final String LOCALITY_NAME = "hobli_name";
    public static final String FORM_STATUS = "form_status";
    public static final String BENEFICIARY_STATUS = "beneficiary_status";
    public static final String COMMUNITY_STATUS = "community_status";
    public static final String INDIVIDUAL_STATUS = "individual_status";
    public static final String PLANTING_ACTIVITY_STATUS = "planting_activity_status";
    public static final String OUTSIDE_PLANTATION_STATUS = "outside_plantation_status";
    public static final String SMC_WORK_STATUS = "smc_work_status";
    public static final String VFC_STATUS = "vfc_status";
    public static final String BOUNDARY_STATUS = "boundary_status";
    public static final String DISCREPANCY = "discrepancy";
    public static final String AREAEXTENT = "area_extent";
    public static final String NO_OF_SEEDLING = "no_of_seedling";
    public static final String NO_OF_TRENCH = "no_of_trench";
    public static final String OTHER_DISCREPANCY = "other_discrepancy";
    public static final String DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_TRENCH_DETAI = "dscrpncy_btwn_recorded_and_observed_qty_of_plantation_trench_detai";
    public static final String DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_SEEDLING_DETAI = "dscrpncy_btwn_recorded_and_observed_qty_of_plantation_seedling_detai";
    public static final String DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_WORK_DETAI = "dscrpncy_btwn_recorded_and_observed_qty_of_plantation_work_detai";
    public static final String IS_WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL = "is_work_done_documentated_properly_in_plantation_journal";
    public static final String WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL_DETAILS = "work_done_documentated_properly_in_plantation_journal_details";
    public static final String ANY_SNR_OFFICER_INSPECT_PLANTATION_AND_ENTRIES_IN_JOURNAL = "any_snr_officer_inspect_plantation_and_entries_in_journal";
    public static final String IS_RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE = "is_results_of_plantation_work_uniform_across_site";
    public static final String RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE_DETAILS = "results_of_plantation_work_uniform_across_site_details";
    public static final String ARE_THE_SCHEME_OBJECTIVES_MET_BY_THE_WORK = "are_the_model_objectives_met_by_the_work";
    public static final String MODEL_OBJECTIVES_NOT_MET_BY_THE_WORK_REASONS = "model_objectives_not_met_by_the_work_reasons";
    public static final String SCHEME_OBJECTIVES_MET_BY_THE_WORK_DETAILS = "scheme_objectives_met_by_the_work_details";
    public static final String ANY_SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION = "any_scope_for_improving_the_performance_of_the_plantation";
    public static final String SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION_DETAILS = "scope_for_improving_the_performance_of_the_plantation_details";
    public static final String SEEDLING_ID = "seedling_id";
    public static final String INVENTORY_ID = "inventory_id";
    public static final String BOTANICAL_NAME = "botanical_name";
    public static final String LOCAL_NAME = "local_name";
    public static final String SPECIES = "species";
    public static final String PLNT_SPECIES = "species";
    public static final String PREFERENCE = "preference";
    public static final String USER_ID = "user_id";
    public static final String TABLE_SPECIES_MASTER = "kfd_species_master";
    public static final String TABLE_SPECIES_MASTER_NEW = "kfd_sdp_species_master_new";
    public static final String KFD_PLNT_SPECIES_MASTER_2018 = "kfd_plnt_species_master_2018";
    public static final String PRINCIPAL_INVESTIGATOR = "principal_investigator";
    public static final String AUTOMATIC_LONGITUDE = "automatic_longitude";
    public static final String TABLE_SCP_TSP_BENIFICIARY = "kfd_scp_tsp_benificiary";
    public static final String BENIFICIARY_ID = "benificiary_id";
    public static final String TOTAL_NO_OF_SAMPLE_PLOTS_LAID = "total_no_of_sample_plots_laid";
    public static final String TYPE_OF_EARTH_WORK_DONE_OTHERDETAILS = "type_of_earth_work_done_otherdetails";
    public static final String REASONS_FOR_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES = "reasons_for_seedlings_purchase_from_pvt_nurseries";
    public static final String DO_YOU_NEED_SPECIFIC_SUPPORT_FROM_GOVT = "do_you_need_specific_support_from_govt";
    public static final String DETAILS_OF_SPECIFIC_SUPPORT_NEEDED_FROM_GOVT = "details_of_specific_support_needed_from_govt";
    public static final String WHEN_WORK_COMPLETED_YEAR = "when_work_completed_year";
    public static final String CREATION_TIMESTAMP = "creation_timestamp";
    public static final String YEAR_OF_PLANTING = "year_of_planting";
    public static final String YEAR_OF_EARTHWORK = "year_of_earthwork";
    public static final String FOUR_CORNERS_MARKING_METHOD_OTHER = "four_corners_marking_method_other";
    public static final String TABLE_APPSETTINGS = "applications_settings";
    public static final String TABLE_NOTIFICATION = "table_notification";
    public static final String APP_ID = "app_id";
    public static final String VERSION_NAME = "version_name";
    public static final String KFD_SCHEME_MASTER = "kfd_scheme_master";
    public static final String KFD_SCHEME_MASTER_2018 = "kfd_scheme_master_2018";
    public static final String KFD_MODELS = "kfd_plnt_models";
    public static final String KFD_MODELS_NEW = "kfd_plnt_models_new";
    public static final String KFD_MODELS_2018 = "kfd_plnt_models_2018";
    public static final String KFD_PLNT_OBJECTIVES = "kfd_plnt_objectives";
    public static final String KFD_PLNT_OPERATIONS = "kfd_plnt_operations";
    public static final String TABLE_SDP_SPECIES_MASTER = "kfd_sdp_species_master";
    public static final String TABLE_GPS_COORDINATES = "kfd_plnt_smplng_gps";
    public static final String PHOTO_STATUS = "photo_status";
    public static final String CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2_APPLICABLE = "cash_incentive_received_from_kfd_under_kapy_year2_applicable";
    public static final String BENEFICIARY_WILLING_TO_PARTICIPATE_IN_SURVEY = "beneficiary_willing_to_participate_in_survey";

    //Devansh
    public static final String IS_EVALUATOR_REACHED_FIELD_OF_BENEFECIARY = "is_evaluator_reached_field_of_beneficiary";


    public static final String BENEFICIARY_NOT_WILLING_TO_PARTICIPATE_IN_SURVEY_REASON =
            "beneficiary_not_willing_to_participate_in_survey_reason";
    public static final String IS_CASUALTY_REPLACEMENT_DONE = "is_casualty_replacement_done";
    public static final String YEAR_OF_CASUALTY_REPLACEMENT = "year_of_casualty_replacement";
    public static final String REPLACEMENT_PBSIZE = "replacement_pbsize";
    public static final String NO_OF_REPLACED_SEEDLINGS = "no_of_replaced_seedlings";
    public static final String NUMBER_OF_SEEDINGS = "no_of_seedlings";
    public static final String VFC_INVOLVED_STAGE_OTHER = "vfc_involved_stage_other";
    public static final String TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST = "kfd_plnt_smplng_smc_details_highest";
    public static final String TABLE_ADV_SMC_HIGHEST = "kfd_adv_smc_highest";
    public static final String SMC_APPLICABLE = "smc_applicable";
    public static final String SMC_STRUCTURE_ALTITUDE = "smc_structure_altitude";
    public static final String CONSTRUCTION_QUALITY = "construction_quality";
    public static final String WORK_DIFFERENCE = "work_difference";
    public static final String REMARK = "remark";
    public static final String FIRST_SNR_OFFICER_DESIGNATION = "first_snr_officer_designation";
    public static final String NUMBER_OF_INSPECTION_ACF = "number_of_inspection_acf";
    public static final String NUMBER_OF_INSPECTION_DCF = "number_of_inspection_dcf";
    public static final String NUMBER_OF_INSPECTION_CF = "number_of_inspection_cf";
    public static final String NUMBER_OF_INSPECTION_CCF = "number_of_inspection_ccf";
    public static final String NUMBER_OF_INSPECTION_APCCF = "number_of_inspection_apccf";
    public static final String NUMBER_OF_INSPECTION_PCCF = "number_of_inspection_pccf";
    public static final String IS_CPT_PRESENT = "Is_CPT_present";
    public static final String PLANTING_ON_CPT = "planting_on_CPT";
    public static final String WORKING_PLAN_MANAGEMENT_PLAN_PRESCRIPTIONS = "working_plan_management_plan_prescriptions";
    public static final String WORKING_CIRCLE_PARAGRAPH_NO = "working_circle_paragraph_no";
    public static final String WHY_WORK_WAS_TAKEN_UP = "why_work_was_taken_up";
    public static final String SERVER_FORM_ID = "server_form_id";
    public static final String PLANTATION_NAME = "plantation_name";
    public static final String RF_NAME = "rf_name";
    public static final String SITE_QUALITY = "site_quality";
    public static final String SOIL_TYPE = "soil_type";
    public static final String GPS_COORDINATE_CREATION_TIMESTAMP = "gps_coordinate_creation_timestamp";
    public static final String TABLE_MODEL_ID = "table_model_id";

    //constants name for table kfd_other_works
    public static final String TABLE_OTHER_WORKS = "kfd_other_works";
    public static final String TABLE_SCP_N_TSP = "kfd_scp_tsp";
    public static final String TABLE_SCP_N_TSP_SURVEY = "kfd_scp_tsp_survey";
    public static final String FORM_ID = "form_id";
    public static final String MODEL_ID = "model_id";
    public static final String RANGE_NAME = "range_name";
    public static final String RANGE_ENAME = "RANGE_ENAME";
    public static final String FOREST_NAME = "forest_name";
    public static final String DISTRICT_NAME = "district_name";
    public static final String DISTRICT_NAME_NEW = "DISTRICT_NAME";
    public static final String DISTRICT_CODE = "DISTRICT_CODE";
    public static final String DISTRICT_ID = "district_id";
    public static final String TALUK_NAME = "TALUK_NAME";
    public static final String TALUK_CODE = "TALUK_CODE";
    public static final String WORK_NAME = "work_name";
    public static final String NO_OF_CARINS_RCCPILLARS_RFSTONE = "no_of_carins_rccpillars_rfstone";
    public static final String OTHER_TYPE_OF_WORK = "other_type_of_work";
    public static final String TYPE_OF_OFFICIER_RESBUILDINGS = "type_of_officier_resbuildings";
    public static final String TYPE_OF_OFFICIER_OFFICEBUILDINGS = "type_of_officier_officebuildings";
    public static final String WORK_CODE = "work_code";
    public static final String WORK_LOCATION = "work_location";
    public static final String EXECUTION_YEAR = "execution_year";
    public static final String ESTIMATED_COST_RUPEES = "estimated_cost_rupees";
    public static final String TOTAL_EXPENDITURE = "total_expenditure";
    public static final String WORK_LOCATION_LAT = "work_location_lat";
    public static final String WORK_LOCATION_LONG = "work_location_long";
    public static final String WORK_LOCATION_ALTI = "work_location_alti";
    public static final String WORK_APPROVED_IN_APO = "work_approved_in_apo";
    public static final String WORK_APPROVED_IN_APO_SLNO = "work_approved_in_apo_slno";
    public static final String WORK_APPROVED_IN_APO_TIMESTAMP = "work_approved_in_apo_timestamp";
    public static final String WORK_APPROVED_IN_APO_NO_REASON = "work_approved_in_apo_no_reason";
    public static final String WAS_PROCUREMENT_INVOLVED = "was_procurement_involved";
    public static final String PROCUREMENT_AMOUNT = "procurement_amount";
    public static final String WHO_EXEC_WORK = "who_executed_work";
    public static final String WHO_EXEC_WORK_OTHERS = "who_executed_work_others";
    public static final String WHEN_WORK_START_YR = "when_work_started_year";
    public static final String WHEN_WORK_STARTED_MONTH = "when_work_started_month";
    public static final String WAS_WORK_COMPLETE = "was_work_complete";
    public static final String TIME_TAKEN_TO_COMPLETE_WORK_MONTHS = "time_taken_to_complete_work_months";
    public static final String CHECK_MEASUREMENT_TIMESTAMP = "check_measurement_timestamp";
    public static final String COMPLETION_CERTIFICATE_TIMESTAMP = "completion_certificate_timestamp";
    public static final String REMARKS = "remarks";
    public static final String PURPOSE_OF_ORIGINALWORK = "purpose_of_originalwork";
    public static final String SIMILAR_WORK_INSAME_LOCALITY = "similar_work_insame_locality";
    public static final String SIMILAR_WORK_INSAME_LOCALITY_USAGE = "similar_work_insame_locality_us" + AGE;
    public static final String SIMILAR_WORK_INSAME_LOCALITY_USAGE_REASON = "similar_work_insame_locality_us" + AGE + "_reason";
    public static final String ORIGINAL_WORK_SITE_SELECTION = "original_work_site_selection";
    public static final String ORIGINAL_WORK_SITE_SELECTION_REASON = "original_work_site_selection_reason";
    public static final String ORIGINAL_WORK_DAMAGED_LOCAL_VEGETATION = "original_work_dam" + AGE + "d_local_vegetation";
    public static final String ORIGINAL_WORK_DAMAGED_LOCAL_VEGETATION_REASON = "original_work_dam" + AGE + "d_local_vegetation_reason";
    public static final String ORIGINAL_WORK_DIMENSION_WIDTH_MTRS = "original_work_dimension_width_mtrs";
    public static final String ORIGINAL_WORK_DIMENSION_HEIGHT_MTRS = "original_work_dimension_height_mtrs";
    public static final String ORIGINAL_WORK_DIMENSION_LENGTH_MTRS = "original_work_dimension_length_mtrs";
    public static final String ORIGINAL_WORK_DIMENSION_VOLUME_MTRS = "original_work_dimension_volume_mtrs";
    public static final String WORK_CARRIEDOUT_ASPER_ESTIMATE = "work_carriedout_asper_estimate";
    public static final String WORK_CARRIEDOUT_ASPER_ESTIMATE_DEVIATION = "work_carriedout_asper_estimate_deviation";
    public static final String WORK_COMPLETED_FROM_ALL_ASPECTS = "work_completed_from_all_aspects";
    public static final String WORK_NOT_COMPLETED_FROM_ALL_ASPECTS_DETAILS = "work_not_completed_from_all_aspects_details";
    public static final String ORIGINAL_WORK_QUALITY_RATING = "original_work_quality_rating";
    //    public static final String ORIGINAL_WORK_QUALITY_GRADING = "original_work_quality_grading";
    public static final String ORIGINAL_ASSET_QUALITY_RATING = "original_asset_quality_rating";
    public static final String ORIGINAL_WORK_REMARKS = "original_work_remarks";
    public static final String MAINTENANCE_WORK_QUALITY_RATING = "maintenance_work_quality_rating";
    public static final String IS_ASSET_USED_NOW = "is_asset_used_now";
    public static final String ASSET_USED_NOW_WHO = "asset_used_now_who";
    public static final String ASSET_USED_NOW_PURPOSE = "asset_used_now_purpose";
    public static final String ASSET_SERVING_INTENDED_PURPOSE = "asset_serving_intended_purpose";
    public static final String REASON_FOR_ASSET_NOT_USED_NOW = "reason_for_asset_not_used_now";
    public static final String WORK_MEETS_PROGRAM_OBJECTIVE = "work_meets_program_objective";
    public static final String REASON_FOR_WORK_DOES_NOT_MEET_OBJECTIVE = "reason_for_work_does_not_meet_objective";
    public static final String THINGS_TO_DO_FOR_MAKING_ASSET_MORE_EFFECTIVE = "things_to_do_for_making_asset_more_effective";
    public static final String UTILIZATION_REMARKS = "utilization_remarks";
    public static final String REMARKS_OF_WORK_NOT_EXIST = "remarks_of_work_not_exist";
    public static final String SIMILAR_WORK_IN_LOCALITY_PREV_DETAILS = "similar_work_in_locality_prev_details";
    //
    public static final String TABLE_SURVEY_MASTER = "kfd_survey_master";
    public final static String SURVEY_ID = "survey_id";
    public final static String FORM_TYPE = "form_type";
    public final static String STARTING_TIMESTAMP = "starting_timestamp";
    public final static String ENDING_TIMESTAMP = "ending_timestamp";
    public final static String SUBMISSION_TIMESTAMP = "submission_timestamp";
    public final static String LONG = "longitude";
    public final static String LAT = "latitude";
    public final static String CIRCLE_NAME = "circle_" + NAME;
    public final static String CIRCLE_NAME_NEW = "CIRCLE_ENAME";
    public final static String CIRCLE_ID = "CIRCLE_ID";
    public final static String DIVISION_NAME_NEW = "DIV_ENAME";
    public final static String DIVISION_CODE = "DIV_ID";
    public final static String SUBDIVISION_NAME_NEW = "SUBDIV_ENAME";
    public final static String SUBDIVISION_CODE = "SUBDIV_ID";
    public final static String DIVISION_NAME = "division_" + NAME;
    public final static String SUBDIVISION_NAME = "subdivision_" + NAME;
    public final static String SURVEYER_NAME = "surveyor_" + NAME;
    public final static String SURVEYER_DESIGNATION = "surveyor_designation";
    public final static String SURVEYER_ASSISTANT_NAME = "surveyor_assistant_" + NAME;
    public final static String KFD_STAFF_NAME = "kfd_staff_" + NAME;
    public final static String KFD_STAFF_DESIGNATION = "kfd_staff_designation";
    public final static String USER_LEVEL = "user_level";
    public final static String OFFICEID = "OfficeID";
    public final static String EVALUATION_YEAR = "evaluation_year";
    public final static String EVALUATION_TITLE = "evaluation_title";

    /*SCPTSP*/
    //community
    public static final String SCP_TSP_ID = "scp_tsp_id";
    public static final String NO_OF_HOUSEHOLDS_BENEFITED = "no_of_households_benefited";
    public static final String EXTENT_OF_ASSET_WORK = "extent_of_asset_work";
    public static final String UNIT_OF_ASSET_WORK = "unit_of_asset_work";
    public static final String WORK_ASSET_NON_EXIST = "work_asset_non_exist";
    public static final String IS_WORK_ASSET_PRESENT = "is_work_asset_present";
    public static final String IS_WORK_ASSET_PRESENT_NO_REASONS = "is_work_asset_present_no_reasons";
    public static final String IS_WORK_ASSET_PRESENT_CONDITION = "is_work_asset_present_condition";
    public static final String IS_ARRANGEMENT_EXIST = "is_arrangement_exist";
    public static final String IS_ARRANGEMENT_EXIST_YES = "is_arrangement_exist_yes";
    public static final String IS_ARRANGEMENT_EXIST_NO = "is_arrangement_exist_no";
    public static final String IS_ASSET_PROPERLY_LABELED = "is_asset_properly_labeled";
    public static final String ANY_VARIATION_FNB_MB = "any_variation_fnb_mb";
    public static final String ANY_VARIATION_FNB_MB_YES = "any_variation_fnb_mb_yes";
    public static final String ARE_THE_PROGRAMME_OBJECTIVES_ACHIEVED = "are_the_programme_objectives_achieved";
    public static final String ARE_THE_PROGRAMME_OBJECTIVES_ACHIEVED_YES = "are_the_programme_objectives_achieved_yes";
    public static final String OBJECTIVES_ACHIEVED_DETAILS = "objectives_achieved_details";
    public static final String SUGGESTIONS_EVALUATION = "suggestions_evaluation";
    //individual
    public static final String ASSET_WORK_EXIST = "asset_work_exist";
    public static final String ASSET_WORK_EXIST_NO_REASONS = "asset_work_exist_no_reasons";
    public static final String BENEFICIARY_AVAILABLE = "beneficiary_available";
    public static final String BENEFICIARY_AVAILABLE_YES = "beneficiary_available_yes";
    public static final String BENEFICIARY_AVAILABLE_NO_REASONS = "beneficiary_available_no_reasons";
    public static final String BENEFICIARY_AVAILABLE_NO_OTHER_REASONS = "beneficiary_available_no_other_reasons";
    public static final String BENEFICIARY_WILLING = "beneficiary_willing";
    public static final String BENEFICIARY_WILLING_NO_REASONS = "beneficiary_willing_no_reasons";
    public static final String SIZE_QUANTITY_ASSET = "size_quantity_asset";
    public static final String UNIT_OF_ASSET = "unit_of_asset";
    public static final String SCP_BEN_LAT = "scp_ben_lat";
    public static final String SCP_BEN_LONG = "scp_ben_long";
    public static final String BENEFICIARY_CONTRIBUTION = "beneficiary_contribution";
    public static final String AMOUNT_CONTRIBUTION = "amount_contribution";
    public static final String IS_ASSET_USED_BENEFICIARY = "is_asset_used_beneficiary";
    public static final String FREQUENCY_OF_USAGE = "frequency_of_usage";
    public static final String IS_ASSET_MAINTAINED = "is_asset_maintained";
    public static final String IS_ASSET_MAINTAINED_YES_CONDITION = "is_asset_maintained_yes_condition";
    public static final String IS_ASSET_MAINTAINED_NO_REASONS = "is_asset_maintained_no_reasons";
    public static final String IS_ASSET_PROPERLY_LABELED_NO_REASONS = "is_asset_properly_labeled_no_reasons";
    public static final String IS_ASSET_COMPANY_PRODUCT = "is_asset_company_product";
    public static final String BRAND_DETAILS = "brand_details";
    public static final String DETAILS_OF_WARRANTY = "details_of_warranty";
    public static final String VARIATION_FNB = "variation_fnb";
    public static final String VARIATION_FNB_DETAILS = "variation_fnb_details";
    public static final String BENEFICIARY_SATSFIED = "beneficiary_satsfied";
    public static final String BIOGAS_PLANT_APPROPRIATE = "biogas_plant_appropriate";
    public static final String PURPOSE_OF_USAGE = "purpose_of_usage";
    public static final String BENEFICIARY_SATSFIED_NO_REASONS = "beneficiary_satsfied_no_reasons";
    public static final String LPG_NEW_CONNECTION = "lpg_new_connection";
    public static final String LPG_NEW_CONNECTION_DETAILS = "lpg_new_connection_details";
    public static final String SUBSEQUENT_CYLINDER_BOUGHT = "subsequent_cylinder_bought";
    public static final String SUBSEQUENT_CYLINDER_BOUGHT_REASONS = "subsequent_cylinder_bought_reasons";
    public static final String TYPE_OF_ROOF = "type_of_roof";
    public static final String OTHER_TYPE_OF_ROOF = "other_type_of_roof";
    public static final String PLINTH_AREA = "plinth_area";
    public static final String LIVESTOCK_TYPES = "livestock_types";
    public static final String TOTAL_NO_OF_LIVESTOCK = "total_no_of_livestock";
    public static final String LIVESTOCK_COW_DETAILS = "livestock_cow_details";
    public static final String LIVESTOCK_BUFFALO_DETAILS = "livestock_buffalo_details";
    public static final String LIVESTOCK_SHEEP_DETAILS = "livestock_sheep_details";
    public static final String LIVESTOCK_GOAT_DETAILS = "livestock_goat_details";
    public static final String LIVESTOCK_DONKEY_DETAILS = "livestock_donkey_details";
    public static final String OTHER_LIVESTOCK_DETAILS = "other_livestock_details";
    //    public static final String QUALITY_RATING_INDIVIDUAL = "quality_rating_individual";
    public static final String SUGGESTIONS_EVALUATOR = "suggestions_evaluator";

    /*Benefit list*/
    public static final String BENEFIT_ID = "benefit_id";
    public static final String NATURE_OF_BENEFIT = "nature_of_benefit";
    public static final String PROGRAM_NAME = "program_name";
    public static final String PROGRAM_NAME_OTHERS = "program_name_others";
    public static final String YEAR_OF_IMPLEMENTATION = "year_of_implementation";
    public static final String NAME_OF_COMMUNITY = "name_of_community";
    public static final String NAME_OF_COMMUNITY_OTHERS = "name_of_community_others";
    public static final String NO_OF_HOUSEHOLDS_IN_LOCATION = "no_of_households_in_location";
    public static final String TYPE_OF_BENEFIT = "type_of_benefit";
    public static final String TYPE_OF_ASSET = "type_of_asset";
    public static final String TYPE_OF_BENEFIT_OTHERS = "type_of_benefit_others";
    public static final String TYPE_OF_ASSET_OTHERS = "type_of_asset_others";
    public static final String COMMUNITY_WORK_ASSET_EXISTS_NOW = "community_work_assest_exists_now";
    public static final String COMMUNITY_WORK_ASSET_LAT = "community_work_assest_lat";
    public static final String COMMUNITY_WORK_ASSET_LONG = "community_work_assest_long";
    public static final String BENEFICIARY_CODE = "beneficiary_code";
    public static final String BENEFICIARY_NAME = "beneficiary_name";
    public static final String BENEFICIARY_FATHER_NAME = "beneficiary_father_name";
    public static final String BENEFICIARY_SEX = "beneficiary_sex";
    public static final String BENEFICIARY_AGE = "beneficiary_age";
    public static final String BENEFICIARY_EDUCATION = "beneficiary_education";
    public static final String BENEFICIARY_LANDHOLDING_ACRES = "beneficiary_landholding_acres";
    public static final String NUMBER_TENDED = "number_tended";
    public static final String SMC_WORK_ID = "smc_work_id";
    public static final String KFD_DISTRICT_MASTER_NEW = "kfd_district_master_new";
    public static final String TABLE_CONSTITUENCY = "kfd_la_constituency";
    public static final String APO = "apo";
    public static final String APO_DATE = "apo_date";
    public static final String WORK_ESTIMATES = "work_estimates";
    public static final String NO_OF_WORK_ESTIMATES = "no_of_work_estimates";
    public static final String FNB = "fnb";
    public static final String PLANTATION_JOURNAL = "plantation_journal";
    /*Advance works*/
    public static final String QUALITY_OF_EARTH_WORK_DONE = "quality_of_earth_work_done";
    public static final String ANY_BURNING_DONE_ON_THE_SITE = "any_burning_done_on_the_site";
    public static final String ANY_BURNING_DONE_ON_THE_SITE_YES_DETAILS = "any_burning_done_on_the_site_yes_details";
    public static final String ANY_DAMAGE_DONE_TO_THE_STANDING_TREES = "any_damage_done_to_the_standing_trees";
    public static final String ANY_DAMAGE_DONE_TO_THE_STANDING_TREES_YES_DETAILS = "any_damage_done_to_the_standing_trees_yes_details";
    public static final String ANY_DAMAGE_DONE_TO_THE_SHRUB_GROWTH_AND_ROOT_STOCK = "any_damage_done_to_the_shrub_growth_and_root_stock";
    public static final String ANY_DAMAGE_DONE_TO_THE_SHRUB_GROWTH_AND_ROOT_STOCK_YES_DETAILS = "any_damage_done_to_the_shrub_growth_and_root_stock_yes_details";
    public static final String OVERALL_WORK_QUALITY_GRADING_ON_SCALE = "overall_work_quality_grading_on_scale";

    /*Notification Table*/
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_TITLE = "notification_title";
    public static final String NOTIFICATION_MESSAGE = "notification_message";
    public static final String NOTIFICATION_URL = "notification_url";
    public static final String NOTIFICATION_STATUS = "notification_status";

    /*Note*/
    public static final String EXTRA_NOTE = "extra_note";
    public static final String NO_OF_SEEDLINGS_TOTAL = "total_seedling";
    public static final String TOTAL_PERCENTAGE = "total_survival_percentage";
    public static final String TOTAL_NO_SURVIVED = "total_no_survived";
    public static final String NO_OF_EMPTY_PITS_TOTAL = "total_empty_pits";

    private static final int DATABASE_VERSION = 1;
    private static Database database;
    private Context context;
    private String formType;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //   super(context, "/sdcard/"+ DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    public static Database initializeDB(Context context) {
        if (database == null) {
            database = new Database(context);
        }
        return database;
    }

    public static String getTruncatedVarchar(String field, String columnType) {
//        int startIndex = columnType.indexOf("("); //varchar(5000)
//        int endIndex = columnType.indexOf(")");
//        int size = Integer.parseInt(columnType.substring(startIndex + 1, endIndex));
//
//        try {
//            if (field.length() > size) {
//                return field.substring(0, size);
//            } else {
//                return field;
//            }
//        } catch (Exception e) {
//            return "";
//        }
//

        return field;


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean isExist = false;

        Cursor res = null;

        try {

            res = db.rawQuery("Select * from " + tableName + " limit 1", null);

            int colIndex = res.getColumnIndex(fieldName);
            if (colIndex != -1) {
                isExist = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        return isExist;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_BENEFICIARY_SEEDLING + " ADD COLUMN " + FORM_FILLED_STATUS + " INTEGER(2) DEFAULT 0");
        }
/*        String sql;
        if (oldVersion < 5) {
            if (!isFieldExist(db, KFD_NURSERY_WORKS_BAGGED_SEEDLINGS, FINISHED_POSITION)) {
                db.execSQL("ALTER TABLE " + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS + " ADD COLUMN " + FINISHED_POSITION + " INTEGER(3) DEFAULT -1");
            }
            sql = createNotificationTable();
            db.execSQL(sql);
        }*/



 /*       if (oldVersion < 3) {

            db.execSQL("ALTER TABLE " + KFD_NURSERY_WORKS + " ADD COLUMN " + SOURCE_OF_WATER_SUPPLY_OTHERS + " varchar(100) DEFAULT NULL");
        }
        String sql;
        if (oldVersion < 4) {

            db.execSQL("ALTER TABLE " + TABLE_SURVEY_MASTER + " RENAME TO tmp_table");
            sql = createKfdSurveyMasterTableV2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_SURVEY_MASTER + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");

            db.execSQL("ALTER TABLE " + TABLE_PLANTATION + " RENAME TO tmp_table");
            sql = createKfdPlantSamplngTablev2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_PLANTATION + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");

            db.execSQL("ALTER TABLE " + TABLE_SDP + " RENAME TO tmp_table");
            sql = createKfdPlntSmplngSDPV2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_SDP + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");

            db.execSQL("ALTER TABLE " + TABLE_OTHER_WORKS + " RENAME TO tmp_table");
            sql = createOtherWorksV2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_OTHER_WORKS + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");

            db.execSQL("ALTER TABLE " + TABLE_SCP_N_TSP + " RENAME TO tmp_table");
            sql = createSCPNTSPV2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_SCP_N_TSP + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");

            db.execSQL("ALTER TABLE " + TABLE_SCP_N_TSP_SURVEY + " RENAME TO tmp_table");
            sql = createSCPNTSPSurvey();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_SCP_N_TSP_SURVEY + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");


            db.execSQL("ALTER TABLE " + KFD_NURSERY_WORKS + " RENAME TO tmp_table");
            sql = createNurseryTablev2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + KFD_NURSERY_WORKS + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");

            db.execSQL("ALTER TABLE " + TABLE_ADVANCEWORK + " RENAME TO tmp_table");
            sql = createAdvanceWorkTablev2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_ADVANCEWORK + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");
        }*/


        /* String sql = createNurseryTablev2();
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + KFD_NURSERY_WORKS + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");*/
           /* db.execSQL("BEGIN TRANSACTION");
            String sql;


            db.execSQL("DROP TABLE " + TABLE_APPSETTINGS);
            sql = createAppSettings();
            db.execSQL(sql);


            db.execSQL("DROP TABLE " + TABLE_PLANTATION);
            sql = createKfdPlantSamplngTablev2();
            db.execSQL(sql);

            *//*delete*//*
            db.execSQL("DROP TABLE " + TABLE_ADVANCEWORK);
            sql = createAdvanceWorkTablev2();
            db.execSQL(sql);

            *//*delete*//*
            db.execSQL("DROP TABLE " + KFD_NURSERY_WORKS);
            sql = createNurseryTablev2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS);
            sql = createNurseryBaggedSeedlingTable();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + KFD_NURSERY_WORKS_SEED_BED);
            sql = createNurserySeedBedTable();
            db.execSQL(sql);


           *//* db.execSQL("ALTER TABLE " + TABLE_BENEFICIARY_SEEDLING + " RENAME TO tmp_table");
            sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_BENEFICIARY_SEEDLING + "  (" +
                    SEEDLING_ID + " INTEGER PRIMARY KEY ," +
                    BENEFICIARY_ID + "  INTEGER(11) DEFAULT  0 ," +
                    NAME_OF_THE_SPECIES + "  varchar(100) DEFAULT NULL," +
                    NUMBER_OF_SEEDLINGS_PLANTED + "  INTEGER(11) DEFAULT  0 ," +
                    NUMBER_OF_SEEDLINGS_SURVIVING + "  INTEGER(11) DEFAULT  0 ," +
                    AVERAGE_COLLAR_GROWTH_CMS + "  float DEFAULT  0 ," +
                    AVERAGE_HEIGHT_METERS + "  float DEFAULT  0 ," +
                    HEALTH_AND_VIGOUR + "  varchar(20) DEFAULT NULL," +
                    CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                    REMARKS + " varchar(500) DEFAULT NULL) ";
            db.execSQL(sql);
            db.execSQL("INSERT INTO " + TABLE_BENEFICIARY_SEEDLING + " SELECT * FROM tmp_table");
            db.execSQL("DROP TABLE tmp_table");*//*

            db.execSQL("DROP TABLE " + TABLE_SAMPLE_PLOT_INVENTORY);
            sql = createTableSamplePlotInventoryV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_ADD_SPECIES);
            sql = createTableAddSpecies();
            db.execSQL(sql);

            db.execSQL(" DROP TABLE " + TABLE_SAMPLEPLOT_SPECIES);
            sql = createSamplePlotSpecies();
            db.execSQL(sql);

            db.execSQL(" DROP TABLE " + TABLE_SMC_SAMPLING_MASTER);
            sql = createSMCMaster();
            db.execSQL(sql);

            *//*delete*//*
            db.execSQL(" DROP TABLE " + TABLE_ADV_SMC_MASTER);
            sql = createAdvSMCMaster();
            db.execSQL(sql);

            db.execSQL(" DROP TABLE " + TABLE_PROTECTION);
            sql = createTableProtection();
            db.execSQL(sql);

            *//*delete*//*
            db.execSQL(" DROP TABLE " + TABLE_ADV_PROTECTION);
            sql = createTableAdvProtection();
            db.execSQL(sql);


            db.execSQL(" DROP TABLE " + TABLE_ADD_BENEFICIARY_SPECIES);
            sql = createTableAddBeneficiarySpecies();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_VFC_SAMPLING);
            sql = createTableVfcSamplingV2();
            db.execSQL(sql);

            *//*delete*//*
            db.execSQL("DROP TABLE " + TABLE_ADV_VFC_SAMPLING);
            sql = createTableAdvVfc();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_SURVEY_MASTER);
            sql = createKfdSurveyMasterTableV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST);
            sql = createTableKfdPlantationSamplingSmcDetailsHighest();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_ADV_SMC_HIGHEST);
            sql = createTableAdvSmcHighest();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_SMC_SAMPLING_DETAILS);
            sql = createKfdPlntSmplngSmcDetailsv2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_SAMPLE_PLOT_MASTER);
            sql = createKfdPlntSmplngSamplePlotMasterv2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_ADV_SAMPLE_PLOT_MASTER);
            sql = createAdvSamplePlotMaster();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_SMC_LIST);
            sql = createSmcListTable();
            db.execSQL(sql);

            *//*delete*//*
            db.execSQL("DROP TABLE " + TABLE_ADV_SMC_LIST);
            sql = createAdvSmcListTable();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_OTHER_SMC_LIST);
            sql = createOtherSmcListTable();
            db.execSQL(sql);

            *//*delete*//*
            db.execSQL("DROP TABLE " + TABLE_ADV_OTHER_SMC_LIST);
            sql = createAdvOtherSmcList();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_BENEFIT_LIST);
            sql = createTypeOFBenefit();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_CONTROL_PLOT_MASTER);
            sql = createKfdPlntSmplngAnrmodelMasterV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_CONTROL_PLOT_INVENTORY);
            sql = createKfdPlntSmplngplotInventoryV2();
            db.execSQL(sql);


            db.execSQL("DROP TABLE " + TABLE_SDP);
            sql = createKfdPlntSmplngSDPV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_BENEFICIARY);
            sql = createSDPBeneficiaryV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_OTHER_WORKS);
            sql = createOtherWorksV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_SCP_N_TSP);
            sql = createSCPNTSPV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_SCP_N_TSP_SURVEY);
            sql = createSCPNTSPSurvey();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_SCP_TSP_BENIFICIARY);
            sql = createSCPTSPBeneficiaryV2();
            db.execSQL(sql);

            db.execSQL("DROP TABLE " + TABLE_BENEFICIARY_SEEDLING);
            sql = createSeedlingPerformaceV2();
            db.execSQL(sql);
            db.execSQL("COMMIT");*/

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    public long insertIntoBeneficiaries(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_BENEFICIARY, null, cv);
    }

    public long insertValuesIntoSCPTSPBeneficiaries(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_SCP_TSP_BENIFICIARY, null, cv);

    }

    public void insertIntoNurseryWorks(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(KFD_NURSERY_WORKS, null, cv);
    }

    public void insertIntoNurseryWorksBaggedSeedlingsQuality(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        Log.i("INSERT", (String.valueOf(db.insert(KFD_NURSERY_WORKS_BAGGED_SEEDLINGS_QUALITY, null, cv))));
    }

    public void insertIntoNurseryWorksQualofsbedsGeneralObservations(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        Log.i("INSERT", (String.valueOf(db.insert(KFD_NURSERY_WORKS_QUALOFSBEDS_GENERAL_OBSERVATIONS, null, cv))));
    }

    public long insertIntoInspectedSmcWorkDetails(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, null, cv);
    }

    public long insertIntoAdvInspectedSmcWorkDetails(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_ADV_SMC_HIGHEST, null, cv);
    }

    public long insertIntoSmcWorkDetails(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_SMC_SAMPLING_DETAILS, null, cv);
    }

    public void insertSMCList(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(TABLE_SMC_LIST, null, cv);

    }

    public void insertAdvSMCList(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ADV_SMC_LIST, null, cv);

    }

    public void insertOtherSMCList(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(TABLE_OTHER_SMC_LIST, null, cv);
    }

    public void insertAdvOtherSMCList(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ADV_OTHER_SMC_LIST, null, cv);
    }

    public void insertIntoSeedlings(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_BENEFICIARY_SEEDLING, null, cv);
        Log.e("dcsd", "" + result);
    }

    public void insertIntoPlotInventory(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SAMPLE_PLOT_INVENTORY, null, cv);
    }

    public void insertIntoSpeciesInventory(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ADD_SPECIES, null, cv);
    }

    public void insertIntoSamplePlotSpecies(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SAMPLEPLOT_SPECIES, null, cv);
    }

    public void insertProtection(ContentValues cv) {

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PROTECTION, null, cv);
    }

    public void insertAdvProtection(ContentValues cv) {

        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(TABLE_ADV_PROTECTION, null, cv);
    }


    public void insertIntoBenSpeciesInventory(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ADD_BENEFICIARY_SPECIES, null, cv);
    }

    public void insertIntoPlantation(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.insertOrThrow(TABLE_PLANTATION, null, cv);
        } catch (Exception e) {
            Log.e("sdcsfv", "" + e.getMessage());
            e.printStackTrace();
        }

    }

    public void insertIntoAdvanceWork(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.insertOrThrow(TABLE_ADVANCEWORK, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long insertIntoControlPlotMaster(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();

        return db.insert(TABLE_CONTROL_PLOT_MASTER, null, cv);
    }

    public long insertIntoSmcSamplingMaster(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_SMC_SAMPLING_MASTER, null, cv);
    }

    public long insertIntoAdvSmcMaster(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_ADV_SMC_MASTER, null, cv);
    }

    public void insertIntoVfcSampling(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_VFC_SAMPLING, null, cv);
    }

    public void insertIntoAdvVfcSampling(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ADV_VFC_SAMPLING, null, cv);
    }

    public void insertIntoControlPlotInventory(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTROL_PLOT_INVENTORY, null, cv);
    }

    public long insertIntoSamplePlot(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_SAMPLE_PLOT_MASTER, null, cv);
    }

    public long insertIntoAdvSamplePlot(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_ADV_SAMPLE_PLOT_MASTER, null, cv);
    }

    public void insertIntoTransitoryWorks(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(KFD_TRANSITORY_WORKS, null, cv);
    }

    public void insertIntoTransitoryWorksComplianceApproval(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(KFD_TRANSITORY_WORKS_COMPLIANCE_WITH_THE_APPROVAL, null, cv);
    }

    public void insertIntoTransitoryWorksFireProtectionWork(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(KFD_TRANSITORY_WORKS_FIRE_PROTECTION_WORKS, null, cv);
    }

    public void insertIntoTransitoryWorksFireViewLines(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(KFD_TRANSITORY_WORKS_FIRE_VIEW_LINES, null, cv);
    }

    public void insertIntoTransitoryWorksForestProtectionCamp(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(KFD_TRANSITORY_WORKS_FOREST_PROTECTION_CAMP, null, cv);
    }

    public Cursor getKfdAdvNurTranFormData(int formId, String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public long updateTableWithoutId(String tableName, String columnName, long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnName, id);
        return db.update(tableName, cv, columnName + " =0", null);
    }

    public void updateTableWithFormId(String tableName, int formId, ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(tableName, cv, FORM_ID + " = " + formId, null);
    }

    public void updateSMCStatus(int formId) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + TABLE_SMC_SAMPLING_MASTER + " SET " + SMC_STATUS + " = 1 WHERE " + FORM_ID + " = " + formId;
        db.execSQL(query);
    }

    public void updateTableWithId(String tableName, String columnName, ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(tableName, cv, columnName + " = " + cv.get(columnName), null);
        //int res = db.update(Teacher_table,args,Teacher_name + "=?",new String[]{name});
    }

    public long updateTableWithId_sample(String tableName, String sample_id, ContentValues cv) {
        Log.e("ascZc",""+sample_id+"\n"+cv.toString());
        SQLiteDatabase db = getWritableDatabase();
        String where = SAMPLE_PLOT_ID + "=" + sample_id;
        return db.update(TABLE_SAMPLE_PLOT_MASTER, cv, where, null);
    }
/*
    public void updateSCPTSP(int formId, String string){
        SQLiteDatabase db = getWritableDatabase();
    }*/

    public void updateSurveyorName(String userName) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + TABLE_SURVEY_MASTER + " SET " + SURVEYOR_NAME + " = '" + userName + "'" + " WHERE " + SURVEYOR_NAME + " IS NULL";
        Cursor c = db.rawQuery(query, null);
        db.execSQL(query);
        c.close();

    }

    public void updateTwoColumn(String tableName, String columnName1, String nature_of_benefit, ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        cv.put(NATURE_OF_BENEFIT, "COMMUNITY");
        db.update(tableName, cv, columnName1 + " = " + cv.get(columnName1) + " and " + Database.NATURE_OF_BENEFIT + " = 'COMMUNITY'", null);
        cv.put(NATURE_OF_BENEFIT, "INDIVIDUAL");
        db.update(tableName, cv, columnName1 + " = " + cv.get(columnName1) + " and " + Database.NATURE_OF_BENEFIT + " = 'INDIVIDUAL'", null);

    }

    public void deleteSMCHigh(String tableName, String columnName, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, columnName + " = " + id, null);
    }

    public void updateSurveyMasterWithFormId(int formId, ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_SURVEY_MASTER, cv, SURVEY_ID + " = " + formId, null);
    }

    public void insertIntoSdpSampling(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SDP, null, cv);
    }

    public void insertIntoOtherWorks(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_OTHER_WORKS, null, cv);
    }

    public void insertIntoScpTsp(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SCP_N_TSP, null, cv);
    }

    public void insertIntoScpTspSurvey(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SCP_N_TSP_SURVEY, null, cv);
    }

    public void insertIntoBenefit(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BENEFIT_LIST, null, cv);
    }

    public long insertIntoMaster(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();


        return db.insert(TABLE_SURVEY_MASTER, null, cv);
    }

    public int getLastFormId() {
        int id = 0;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select " + FORM_ID + " from " + TABLE_SURVEY_MASTER;
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToLast()) {
            id = c.getInt(c.getColumnIndex(FORM_ID));
        }
        c.close();
        return id + 1;
    }

    public void saveCoordinates(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_GPS_COORDINATES, null, cv);
        db.close();
    }

    public static void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getNumberOfBeneficiaries(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;
    }

    public int getBeneficiaryNumberForFormId(int formId, int beneficiaryId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY + " WHERE " + FORM_ID + " = " + formId;
        Cursor c = db.rawQuery(query, null);
        int i = 1;
        if (c != null && c.moveToFirst()) {
            do {
                if (c.getInt(c.getColumnIndex(Database.BENEFICIARY_ID)) == beneficiaryId) {
                    return i;
                }
                i++;
            } while (c.moveToNext());
            c.close();
        }
        return 0;
    }

    public int getSamplePlotNumberForFormId(int formId, int samplePlotId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int i = 1;
        if (c != null && c.moveToFirst()) {
            do {
                if (c.getInt(c.getColumnIndex(Database.SAMPLE_PLOT_ID)) == samplePlotId) {
                    return i;
                }
                i++;
            } while (c.moveToNext());
            c.close();
        }

        return 0;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public int getAdvSamplePlotNumberForFormId(int formId, int samplePlotId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int i = 1;
        if (c != null && c.moveToFirst()) {
            do {
                if (c.getInt(c.getColumnIndex(Database.SAMPLE_PLOT_ID)) == samplePlotId) {
                    return i;
                }
                i++;
            } while (c.moveToNext());
            c.close();
        }

        return 0;
    }

    public int getSpeciesNumberForFormId(int formId, int samplePlotId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADD_SPECIES + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int i = 1;
        if (c != null && c.moveToFirst()) {
            do {
                if (c.getInt(c.getColumnIndex(Database.SAMPLE_PLOT_ID)) == samplePlotId) {
                    return i;
                }
                i++;
            } while (c.moveToNext());
            c.close();
        }

        return 0;

    }

    public Cursor getBeneficiary(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY + " WHERE " + BENEFICIARY_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public int[] getSDPBenefciaries(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int len = c.getCount();
        int[] ids = new int[len];
        int i = 0;
        while (c.moveToNext()) {
            /*beneficiary and benificary are differenet*/
            ids[i] = c.getInt(c.getColumnIndexOrThrow(BENEFICIARY_ID));
            i++;
        }
        c.close();
        return ids;
    }


    public int getNumberOfSmcWorks(int formId, String smcwork) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SMC_LIST + " WHERE " + FORM_ID + " = " + formId + " AND " + TYPE_OF_STRUCTURE + " = '" + smcwork + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;

    }

    public int getNumberOfSamplePlots(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;
    }

    public int getAdvNumberOfSamplePlots(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;
    }

    public int getSamplePlotId() {
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_SAMPLE_PLOT_MASTER;
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;
    }

    public int getNumberOfSampleSpecies(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADD_SPECIES + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;
    }

    public int getNumberOfBeneficiarySpecies(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADD_BENEFICIARY_SPECIES + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;
    }

    public Cursor getBeneficiaries(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getTrackedLocations(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GPS_COORDINATES + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getForms(String formType, String formStatus) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SURVEY_MASTER + " WHERE " + FORM_STATUS + " = " + formStatus + " AND " + FORM_TYPE + " = '" + formType + "'";
        return db.rawQuery(query, null);
    }

    public Cursor getFormDataForFormType(int formId, String formType) {
        SQLiteDatabase db = getReadableDatabase();
        String query;
        switch (formType) {
            case Constants.FORMTYPE_OTHERWORKS:
                query = "SELECT * FROM " + TABLE_OTHER_WORKS + " WHERE " + FORM_ID + " = " + formId;
                break;
            case Constants.FORMTYPE_PLANTSAMPLING:
          /*  query = "SELECT * FROM " + TABLE_PLANTATION +
                    " INNER JOIN " + TABLE_CONTROL_PLOT_MASTER + " ON " + TABLE_PLANTATION + "." + FORM_ID + " = " + TABLE_CONTROL_PLOT_MASTER + "." + FORM_ID +
                    " WHERE " + TABLE_PLANTATION + "." + FORM_ID + " = " + formId;*/
                query = "SELECT * FROM " + TABLE_PLANTATION + " WHERE " + FORM_ID + " = " + formId;
                break;
            case Constants.FORMTYPE_SDP:
                query = "SELECT * FROM " + TABLE_SDP + " WHERE " + FORM_ID + " = " + formId;
                break;
            case Constants.FORMTYPE_NURSERY_WORK:
               /* query = "SELECT * FROM " + KFD_NURSERY_WORKS + "," + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS_QUALITY + "," +
                        KFD_NURSERY_WORKS_QUALOFSBEDS_GENERAL_OBSERVATIONS + " WHERE " +
                        KFD_NURSERY_WORKS + "." + FORM_ID + " = " + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS_QUALITY + "." + FORM_ID + " and " +
                        KFD_NURSERY_WORKS + "." + FORM_ID + " = " + KFD_NURSERY_WORKS_QUALOFSBEDS_GENERAL_OBSERVATIONS + "." + FORM_ID + " and " +
                        KFD_NURSERY_WORKS + "." + FORM_ID + " = " + formId;*/
                query = "SELECT * FROM " + KFD_NURSERY_WORKS + " WHERE " + FORM_ID + " = " + formId;
                break;
            case Constants.FORMTYPE_ADVANCEWORK:
                query = "SELECT * FROM " + TABLE_ADVANCEWORK + " WHERE " + FORM_ID + " = " + formId;
                break;
            case Constants.FORMTYPE_TRANSITORY_WORK:
                query = "SELECT * FROM " + KFD_TRANSITORY_WORKS + "," + KFD_TRANSITORY_WORKS_COMPLIANCE_WITH_THE_APPROVAL + "," +
                        KFD_TRANSITORY_WORKS_FIRE_PROTECTION_WORKS + "," + KFD_TRANSITORY_WORKS_FIRE_VIEW_LINES + "," +
                        KFD_TRANSITORY_WORKS_FOREST_PROTECTION_CAMP + " WHERE " +
                        KFD_TRANSITORY_WORKS + "." + FORM_ID + " = " + KFD_TRANSITORY_WORKS_COMPLIANCE_WITH_THE_APPROVAL + "." + FORM_ID + " and " +
                        KFD_TRANSITORY_WORKS + "." + FORM_ID + " = " + KFD_TRANSITORY_WORKS_FIRE_PROTECTION_WORKS + "." + FORM_ID + " and " +
                        KFD_TRANSITORY_WORKS + "." + FORM_ID + " = " + KFD_TRANSITORY_WORKS_FIRE_VIEW_LINES + "." + FORM_ID + " and " +
                        KFD_TRANSITORY_WORKS + "." + FORM_ID + " = " + KFD_TRANSITORY_WORKS_FOREST_PROTECTION_CAMP + "." + FORM_ID + " and " +
                        KFD_TRANSITORY_WORKS + "." + FORM_ID + " = " + formId;
                break;
            default:
                query = "SELECT * FROM " + TABLE_SCP_N_TSP + " WHERE " + FORM_ID + " = " + formId;
                break;
        }
        return db.rawQuery(query, null);
    }

    private String getTransitoryWorksComplianceApprovalForUploadData(int formId) {
        String dataTableData = "";
        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_TRANSITORY_WORKS_COMPLIANCE_WITH_THE_APPROVAL + " WHERE " + FORM_ID + "= ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(KFD_TRANSITORY_WORKS_COMPLIANCE_WITH_THE_APPROVAL, this).get("columnTypesList");
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int iter = 0;
        if (c != null && c.moveToFirst()) {
            String[] colArrDataTable = c.getColumnNames();
            int id = c.getInt(c.getColumnIndex(FORM_ID));
            HashMap<String, String> dataTableMap = new HashMap<>();
            for (String col : colArrDataTable) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        dataTableMap.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                    } else {
                        dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    dataTableMap.put(col, "");
                }
                iter++;
            }
            dataTableData = gson.toJson(dataTableMap);
            //data.add(dataTableData);
            c.close();
        }
        return dataTableData;
    }

    private String getTransitoryWorksFireProtectionWorkForUploadData(int formId) {
        String dataTableData = "";
        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_TRANSITORY_WORKS_FIRE_PROTECTION_WORKS + " WHERE " + FORM_ID + "= ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(KFD_TRANSITORY_WORKS_FIRE_PROTECTION_WORKS, this).get("columnTypesList");
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int iter = 0;
        if (c != null && c.moveToFirst()) {
            String[] colArrDataTable = c.getColumnNames();
            int id = c.getInt(c.getColumnIndex(FORM_ID));
            HashMap<String, String> dataTableMap = new HashMap<>();
            for (String col : colArrDataTable) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        dataTableMap.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                    } else {
                        dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    dataTableMap.put(col, "");
                }
                iter++;
            }
            dataTableData = gson.toJson(dataTableMap);
            //data.add(dataTableData);
            c.close();
        }
        return dataTableData;
    }

    private String getTransitoryWorksFireViewLinesForUploadData(int formId) {
        String dataTableData = "";
        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_TRANSITORY_WORKS_FIRE_VIEW_LINES + " WHERE " + FORM_ID + "= ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(KFD_TRANSITORY_WORKS_FIRE_VIEW_LINES, this).get("columnTypesList");
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int iter = 0;
        if (c != null && c.moveToFirst()) {
            String[] colArrDataTable = c.getColumnNames();
            int id = c.getInt(c.getColumnIndex(FORM_ID));
            HashMap<String, String> dataTableMap = new HashMap<>();
            for (String col : colArrDataTable) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        dataTableMap.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                    } else {
                        dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    dataTableMap.put(col, "");
                }
                iter++;
            }
            dataTableData = gson.toJson(dataTableMap);
            //data.add(dataTableData);
            c.close();
        }
        return dataTableData;
    }

    private String getTransitoryWorksForestProtectionCampForUploadData(int formId) {
        String dataTableData = "";
        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_TRANSITORY_WORKS_FOREST_PROTECTION_CAMP + " WHERE " + FORM_ID + "= ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(KFD_TRANSITORY_WORKS_FOREST_PROTECTION_CAMP, this).get("columnTypesList");
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int iter = 0;
        if (c != null && c.moveToFirst()) {
            String[] colArrDataTable = c.getColumnNames();
            int id = c.getInt(c.getColumnIndex(FORM_ID));
            HashMap<String, String> dataTableMap = new HashMap<>();
            for (String col : colArrDataTable) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        dataTableMap.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                    } else {
                        dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    dataTableMap.put(col, "");
                }
                iter++;
            }
            dataTableData = gson.toJson(dataTableMap);
            //data.add(dataTableData);
            c.close();
        }
        return dataTableData;
    }

    public Cursor getSmcCursor(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SMC_SAMPLING_MASTER + " WHERE " + FORM_ID + " = " + formId;
        return db.rawQuery(query, null);
    }

    public Cursor getAdvSmcCursor(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_SMC_MASTER + " WHERE " + FORM_ID + " = " + formId;
        return db.rawQuery(query, null);
    }

    public Cursor getVfcCursor(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VFC_SAMPLING + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getAdvVfcCursor(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_VFC_SAMPLING + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getScpTspBeneficiaries(int benefitID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SCP_TSP_BENIFICIARY + " WHERE " + BENEFIT_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(benefitID)});

    }

    public Cursor getProtection(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PROTECTION + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getAdvProtection(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_PROTECTION + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getSmcWorks(String formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SMC_LIST + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getNotifications() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NOTIFICATION + " ORDER BY " + NOTIFICATION_ID + " DESC";
        return db.rawQuery(query, null);
    }

    public Cursor getAdvSmcWorks(String formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_SMC_LIST + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getsmcHighest(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT DISTINCT " + TYPE_OF_STRUCTURE + "," + SMC_STRUCTURE_COST + "," + SMC_STRUCTURE_LENGTH + "," + SMC_STRUCTURE_BREADTH + "," + SMC_STRUCTURE_DEPTH + " FROM " + TABLE_SMC_LIST + " WHERE " + SMC_AVAILABILITY + " = '1' AND " + FORM_ID + " = " + formId + " ORDER BY " + SMC_STRUCTURE_COST + " DESC LIMIT '2'";
        return db.rawQuery(query, null);

    }

    public Cursor getAdvsmcHighest(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT DISTINCT " + TYPE_OF_STRUCTURE + "," + SMC_STRUCTURE_COST + "," + SMC_STRUCTURE_LENGTH + "," + SMC_STRUCTURE_BREADTH + "," + SMC_STRUCTURE_DEPTH + " FROM " + TABLE_ADV_SMC_LIST + " WHERE " + SMC_AVAILABILITY + " = '1' AND " + FORM_ID + " = " + formId + " ORDER BY " + SMC_STRUCTURE_COST + " DESC LIMIT '2'";
        return db.rawQuery(query, null);

    }

    public Cursor getSMCHighestWithFormID(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getAdvSMCHighestWithFormID(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_SMC_HIGHEST + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getSmcNumber1(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT MAX(" + SMC_STRUCTURE_COST + ") AS " + SMC_STRUCTURE_COST + " , * FROM " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST + " WHERE " + FORM_ID + " = " + formId + " GROUP BY " + SMC_ID;
        return db.rawQuery(query, null);
    }

    public Cursor getAdvSmcNumber1(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT MAX(" + SMC_STRUCTURE_COST + ") AS " + SMC_STRUCTURE_COST + " , * FROM " + TABLE_ADV_SMC_HIGHEST + " WHERE " + FORM_ID + " = " + formId + " GROUP BY " + SMC_ID;
        return db.rawQuery(query, null);
    }

    public Cursor getSmcNumber2(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST + " WHERE " + FORM_ID + " = " + formId + " LIMIT 2-1,1";
        return db.rawQuery(query, null);
    }

    public Cursor getAdvSmcNumber2(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_SMC_HIGHEST + " WHERE " + FORM_ID + " = " + formId + " LIMIT 2-1,1";
        return db.rawQuery(query, null);
    }

    public Cursor getOtherSmcWorks(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_OTHER_SMC_LIST + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getAdvOtherSmcWorks(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_OTHER_SMC_LIST + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    //-----------------done by sunil-------------------
    public Cursor getInspectedSmcWorks(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    //------------------------------------------------
    public Cursor getSamplePlots(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getAdvSamplePlots(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getPlotInventory(int samplePlotId, String inventoryType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_INVENTORY + " WHERE " + SAMPLE_PLOT_ID + " = " + samplePlotId + " AND " + PART_TYPE + " ='" + inventoryType + "'";
        return db.rawQuery(query, null);

    }


    public Cursor getSpeciesForEmptypits(int formId, int sampleplotId, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_INVENTORY + " WHERE " + FORM_ID + " = " + formId + " AND " + SAMPLE_PLOT_ID + " = " + sampleplotId + " AND " + PART_TYPE + " ='" + partType + "'" + " AND " + SPECIES_AVAILABILITY + " = 'Yes'  ORDER BY " + TOTAL_COUNT_SURVIVED + " DESC";
        return db.rawQuery(query, null);
    }


    public Cursor getSpeciesInventory(int formId, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT DISTINCT " + MAIN_SPECIES_PLANTED + "," + OTHER_SPECIES + " FROM " + TABLE_SAMPLEPLOT_SPECIES + " WHERE " + FORM_ID + " = " + formId + " AND " + PART_TYPE + " ='" + partType + "'";
        return db.rawQuery(query, null);

    }

    public Cursor getPlantSpecies(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADD_SPECIES + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getBeneficiarySpeciesInventory(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADD_BENEFICIARY_SPECIES + " WHERE " + FORM_ID + " ='" + formId + "'";
        return db.rawQuery(query, null);

    }

    public Cursor getControlPlotInventory(int formId, String inventoryType, String control_plot_type) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTROL_PLOT_INVENTORY + " WHERE " + FORM_ID + " = " + formId + " AND " + PART_TYPE + " ='" + inventoryType + "'" + " AND " + CONTROL_PLOT_TYPE + " ='" + control_plot_type + "'";
        return db.rawQuery(query, null);

    }

    public int getSpeciesNumberFormId(int formId, int samplePlotId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADD_BENEFICIARY_SPECIES + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int i = 1;
        if (c != null && c.moveToFirst()) {
            do {
                if (c.getInt(c.getColumnIndex(Database.SAMPLE_PLOT_ID)) == samplePlotId) {
                    return i;
                }
                i++;
            } while (c.moveToNext());
            c.close();
        }
        return 0;
    }

    public int getNumberOfSeedlings(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY_SEEDLING + " WHERE " + BENEFICIARY_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(id)});
        if (c != null && c.moveToFirst()) {
            int i = c.getCount();
            c.close();
            return i;
        }
        return 0;
    }

    public int getMasterSpeciesId(String speciesName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_PLNT_SPECIES_MASTER_2018 + " WHERE " + SPECIES + " =?";
        Cursor c = db.rawQuery(query, new String[]{speciesName});
        if (c != null && c.moveToNext()) {
            int i = c.getInt(c.getColumnIndex(SPECIES_ID));
            c.close();
            return i;
        }
        return 0;
    }

    public Cursor getBeneficiarySeedlings(int id, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY_SEEDLING + " WHERE " + BENEFICIARY_ID + " = ? AND " + PART_TYPE + " =?";
        return db.rawQuery(query, new String[]{String.valueOf(id), partType});

    }

    public int getSdpSpeciesId(String speciesName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SDP_SPECIES_MASTER + " WHERE " + SPECIES + " =?";
        Cursor c = db.rawQuery(query, new String[]{speciesName});
        if (c != null && c.moveToNext()) {
            int i = c.getInt(c.getColumnIndex(SPECIES_ID));
            c.close();
            return i;
        }

        return 0;
    }

    public int gettotalSeedling(String id, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY_SEEDLING + " WHERE " + BENEFICIARY_ID + " = ? AND " + PART_TYPE + " =?";

        Cursor cursor = db.rawQuery(query, new String[]{id, partType});
        int totalSeedling = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalSeedling = totalSeedling + cursor.getInt(cursor.getColumnIndex(NUMBER_OF_SEEDLINGS_PLANTED));
            }
            cursor.close();
            return totalSeedling;
        }

        return 0;
    }

    /*To calculate sampleplot Seedling*/
    public int gettotalNoSeedling(int id, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_INVENTORY + " WHERE " + SAMPLE_PLOT_ID + " = ? AND " + PART_TYPE + " =?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id), partType});
        int totalSeedling = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalSeedling = totalSeedling + cursor.getInt(cursor.getColumnIndex(TOTAL_COUNT));
            }
            cursor.close();
            return totalSeedling;
        }

        return 0;
    }

    public int gettotalSurving(String id, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY_SEEDLING + " WHERE " + BENEFICIARY_ID + " = ? AND " + PART_TYPE + " =?";

        Cursor cursor = db.rawQuery(query, new String[]{id, partType});
        int totalSeedling = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalSeedling = totalSeedling + cursor.getInt(cursor.getColumnIndex(NUMBER_OF_SEEDLINGS_SURVIVING));
            }
            cursor.close();
            return totalSeedling;
        }

        return 0;
    }

    //abhi
    public Cursor getSeedlingforSeedlingId(int id, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY_SEEDLING + " WHERE " + SEEDLING_ID + " = ? AND " + PART_TYPE + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(id), partType});
    }

    public Cursor getSingleProtection(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PROTECTION + " WHERE " + PROTECTION_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public Cursor getSingleAdvProtection(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_PROTECTION + " WHERE " + PROTECTION_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public Cursor getTableForId(String tableName, String idColumn, int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = " + id;
        return db.rawQuery(query, null);
    }

    public Cursor getControlPlotMaster(String controlplotType, int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTROL_PLOT_MASTER + " WHERE " + CONTROL_PLOT_TYPE + " = '" + controlplotType + "'" + " AND " + FORM_ID + " = " + id;
        return db.rawQuery(query, null);
    }

    public Cursor getSurveyMasterForFormId(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SURVEY_MASTER + " WHERE " + SURVEY_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getNurserWorkBaggedSeedlings(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS + " WHERE " + FORM_ID + " = ? ";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public Cursor getNurserWorkSeedBeds(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_NURSERY_WORKS_SEED_BED + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});

    }

    /*To Calculate for sampleplot*/
    public int gettotalNoSurving(int id, String partType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_INVENTORY + " WHERE " + SAMPLE_PLOT_ID + " = ? AND " + PART_TYPE + " =?" + " AND " + SPECIES_AVAILABILITY + " = 'Yes' ";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id), partType});
        int totalSeedling = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalSeedling = totalSeedling + cursor.getInt(cursor.getColumnIndex(TOTAL_COUNT_SURVIVED));
            }
            cursor.close();
            return totalSeedling;
        }
        return 0;
    }

    private String getSeedlingData(int formId) {
        String data = "";
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY_SEEDLING + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_BENEFICIARY_SEEDLING, this).get("columnTypesList");
        List<HashMap<String, String>> seedlingData = new ArrayList<>();
        if (c != null) {
            String[] cols = c.getColumnNames();
            while (c.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();
                int iter = 0;
                for (String col : cols) {

                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            map.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                        } else {
                            String sNull = c.getString(c.getColumnIndex(col));
                            if (sNull == null)
                                sNull = "";
                            map.put(col, sNull);
                        }
                    } catch (Exception e) {
                        map.put(col, "");
                    }
                    iter++;
                }
                seedlingData.add(map);
            }
            c.close();
            data = new Gson().toJson(seedlingData);
        }
        return data;
    }


    public ArrayList<ArrayList<String>> getAllFormData() {
        ArrayList<ArrayList<String>> allForms = new ArrayList<>();

        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String dataTableData;
        String masterTableData;
        String query = "SELECT * FROM " + TABLE_SURVEY_MASTER + " WHERE " + FORM_STATUS + " = " + Constants.APPROVED;
        Cursor c = db.rawQuery(query, null);
        while (c != null && c.moveToNext()) {
            String[] colArrMasterTable = c.getColumnNames();
            int formId = c.getInt(c.getColumnIndex(SURVEY_ID));
            int iter = 0;
            ArrayList<String> data = new ArrayList<>();
            allForms.add(data);
            data.add(String.valueOf(formId));
            formType = c.getString(c.getColumnIndex(FORM_TYPE));
            ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SURVEY_MASTER, this).get("columnTypesList");
            String dataTableName = getDataTableName(formType);//getting the table name based on form type
            HashMap<String, String> masterTableMap = new HashMap<>();
            for (String col : colArrMasterTable) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        masterTableMap.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType)); //putting all the column names and value in map
                    } else {
                        masterTableMap.put(col, c.getString(c.getColumnIndex(col))); //putting all the column names and value in map
                    }
                } catch (IndexOutOfBoundsException e) {
                    masterTableMap.put(col, "");
                }

                iter++;
            }
            masterTableData = gson.toJson(masterTableMap);
            data.add(masterTableData);

            String query2 = "SELECT * FROM " + dataTableName + " WHERE " + FORM_ID + "= ?";
            columnTypes = SurveyCreation.getTableMetaData(dataTableName, this).get("columnTypesList");
            Cursor c2 = db.rawQuery(query2, new String[]{String.valueOf(formId)});
            iter = 0;
            if (!dataTableName.equalsIgnoreCase(TABLE_SCP_N_TSP)) {
                if (c2 != null && c2.moveToFirst()) {
                    String[] colArrDataTable = c2.getColumnNames();
                    int id = c2.getInt(c2.getColumnIndex(FORM_ID));
                    //   List<HashMap<String, String>> tableDataArray = new ArrayList<>();
                    HashMap<String, String> dataTableMap = new HashMap<>();
                    for (String col : colArrDataTable) {
                        try {
                            String columnType = columnTypes.get(iter);
                            if (columnType.contains("varchar")) {
                                dataTableMap.put(col, getTruncatedVarchar(c2.getString(c2.getColumnIndex(col)), columnType));
                            } else {
                                dataTableMap.put(col, c2.getString(c2.getColumnIndex(col)));
                            }
                        } catch (IndexOutOfBoundsException e) {
//                            dataTableMap.put(col, c2.getString(c2.getColumnIndex(col)));
                            dataTableMap.put(col, "");
                        }
                        iter++;
                    }
                    //  tableDataArray.add(dataTableMap);
                    dataTableData = gson.toJson(dataTableMap);
                    data.add(dataTableData);
                    c2.close();
                }
            } else {
                if (c2 != null && c2.moveToFirst()) {
                    String[] colArrDataTable = c2.getColumnNames();
                    int id = c2.getInt(c2.getColumnIndex(FORM_ID));
                    List<HashMap<String, String>> tableDataArray = new ArrayList<>();
                    do {
                        iter = 0;
                        HashMap<String, String> dataTableMap = new HashMap<>();
                        for (String col : colArrDataTable) {
                            try {
                                String columnType = columnTypes.get(iter);
                                if (columnType.contains("varchar")) {
                                    dataTableMap.put(col, getTruncatedVarchar(c2.getString(c2.getColumnIndex(col)), columnType));
                                } else {
                                    String tString = c2.getString(c2.getColumnIndex(col));
                                    if (tString == null)
                                        tString = "";
                                    dataTableMap.put(col, tString);
                                }
                            } catch (Exception e) {
                                dataTableMap.put(col, "");
                            }
                            iter++;
                        }
                        tableDataArray.add(dataTableMap);
                    } while (c2.moveToNext());
                    dataTableData = gson.toJson(tableDataArray);
                    data.add(dataTableData);
                    c2.close();
                }

            }

            switch (formType) {
                case Constants.FORMTYPE_SDP:
                    ArrayList<String> bnfsANDSeeds = getBnfData(formId);
                    data.addAll(bnfsANDSeeds);
                    break;
                case Constants.FORMTYPE_PLANTSAMPLING:

                    String plntSpecies = getPlntSpecies(formId);
                    data.add(plntSpecies);

                    ArrayList<String> samplePlotData = getSamplePlotData(formId);
                    data.addAll(samplePlotData);

                    ArrayList<String> controlPlotData = getControlPlotData(formId);
                    data.addAll(controlPlotData);

                    ArrayList<String> smcData = getSMCData(formId);
                    data.addAll(smcData);
                    String vfcData = getVFCData(formId);
                    data.add(vfcData);

                    String bounddata = getBoundaryProtectionData(formId);
                    data.add(bounddata);

                    break;

                case Constants.FORMTYPE_ADVANCEWORK:
                    ArrayList<String> advSamplePlotData = getAdvSamplePlotData(formId);
                    data.addAll(advSamplePlotData);
                    ArrayList<String> advSmcData = getAdvSMCData(formId);
                    data.addAll(advSmcData);
                    String advVfcData = getAdvVFCData(formId);
                    data.add(advVfcData);
                    String advBounddata = getAdvBoundaryProtectionData(formId);
                    data.add(advBounddata);
                    break;

                case Constants.FORMTYPE_SCPTSP:
                    String scpTspBnfData = getSTBnfData(formId);
                    data.add(scpTspBnfData);

                    String sctspSurveyData = getSCPTSPSurveyData(formId);
                    data.add(sctspSurveyData);

                    String seedlingPerformance = getSeedlingData(formId);
                    data.add(seedlingPerformance);

                    break;
                case Constants.FORMTYPE_TRANSITORY_WORK:
                    String transWorkCompAppData = getTransitoryWorksComplianceApprovalForUploadData(formId);
                    data.add(transWorkCompAppData);
                    String transWorkFireProtWorkData = getTransitoryWorksFireProtectionWorkForUploadData(formId);
                    data.add(transWorkFireProtWorkData);
                    String transWorkFireViewLinesData = getTransitoryWorksFireViewLinesForUploadData(formId);
                    data.add(transWorkFireViewLinesData);
                    String transWorkForestProtCampData = getTransitoryWorksForestProtectionCampForUploadData(formId);
                    data.add(transWorkForestProtCampData);
                    break;

                case Constants.FORMTYPE_NURSERY_WORK:
                    String nurWorkBaggedSeedling = getNurseryWorksBaggedSeedlingsForUploadData(formId);
                    data.add(nurWorkBaggedSeedling);
                    String nurWorkSeedBeds = getNurseryWorksSeedBedForUploadData(formId);
                    data.add(nurWorkSeedBeds);
                    break;
            }
        }
        return allForms;
    }

    private String getSTBnfData(int formId) {
        String data = "";
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SCP_TSP_BENIFICIARY + " WHERE " + FORM_ID + " = ?";

        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SCP_TSP_BENIFICIARY, this).get("columnTypesList");
        List<HashMap<String, String>> bnf = new ArrayList<>();
        if (c != null) {
            String[] cols = c.getColumnNames();
            while (c.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();
                int iter = 0;
                for (String col : cols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            map.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                        } else {
                            String sNull = c.getString(c.getColumnIndex(col));
                            if (sNull == null)
                                sNull = "";
                            map.put(col, sNull);
                        }
                    } catch (Exception e) {
                        map.put(col, "");
                    }
                    iter++;
                }
                bnf.add(map);
            }
            c.close();
            data = new Gson().toJson(bnf);
        }
        return data;
    }


    public ArrayList<String> getBnfData(int formId) {
        ArrayList<String> data = new ArrayList<>();
        ArrayList<List<HashMap<String, String>>> seeds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Gson gson = new Gson();
        String query3 = "SELECT * FROM " + TABLE_BENEFICIARY + " WHERE " + FORM_ID + "= ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_BENEFICIARY, this).get("columnTypesList");
        ArrayList<String> columnTypesSeedling = SurveyCreation.getTableMetaData(Database.TABLE_BENEFICIARY_SEEDLING, this).get("columnTypesList");
        Cursor c3 = db.rawQuery(query3, new String[]{String.valueOf(formId)});
        List<HashMap<String, String>> bnf = new ArrayList<>();
        List<HashMap<String, String>> seed = new ArrayList<>();
        if (c3 != null) {
            String[] bnfColms = c3.getColumnNames();
            while (c3.moveToNext()) {
                int bnfId = c3.getInt(c3.getColumnIndex(BENEFICIARY_ID));
                HashMap<String, String> map = new HashMap<>();
                int iter = 0;
                for (String col : bnfColms) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            map.put(col, getTruncatedVarchar(c3.getString(c3.getColumnIndex(col)), columnType));
                        } else {
                            map.put(col, c3.getString(c3.getColumnIndex(col)));
                        }
                    } catch (Exception e) {
                        map.put(col, "");
                        //map.put(col, c3.getString(c3.getColumnIndex(col)));
                    }
                    iter++;
                }
                bnf.add(map);
                String query4 = "SELECT * FROM " + TABLE_BENEFICIARY_SEEDLING + " WHERE " + BENEFICIARY_ID + "=?";

                Cursor c4 = db.rawQuery(query4, new String[]{String.valueOf(bnfId)});
                // List<HashMap<String, String>> seed = new ArrayList<>();
                if (c4 != null) {
                    String[] seedColms = c4.getColumnNames();
                    while (c4.moveToNext()) {
                        HashMap<String, String> map1 = new HashMap<>();
                        iter = 0;
                        for (String col : seedColms) {
                            try {
                                String columnType = columnTypesSeedling.get(iter);
                                if (columnType.contains("varchar")) {
                                    map1.put(col, getTruncatedVarchar(c4.getString(c4.getColumnIndex(col)), columnType));
                                } else {
                                    map1.put(col, c4.getString(c4.getColumnIndex(col)));
                                }
                            } catch (Exception e) {
//                                map1.put(col, c4.getString(c4.getColumnIndex(col)));
                                map1.put(col, "");
                            }
                            iter++;
                        }
                        seed.add(map1);
                    }
                    c4.close();
                }
                // seeds.add(seed);
            }
            c3.close();
        }
        String bnfJson = gson.toJson(bnf);
        data.add(bnfJson);
        data.add(gson.toJson(seed));
        return data;
    }

    public ArrayList<String> getControlPlotData(int formId) {
        Gson gson = new Gson();
        ArrayList<String> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query1 = "SELECT * FROM " + TABLE_CONTROL_PLOT_MASTER + " WHERE " + FORM_ID + "=?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_CONTROL_PLOT_MASTER, this).get("columnTypesList");
        Cursor c1 = db.rawQuery(query1, new String[]{String.valueOf(formId)});
        List<HashMap<String, String>> plotMapMasterList = new ArrayList<>();
        List<HashMap<String, String>> plotInventMapList = new ArrayList<>();
        if (c1.moveToFirst()) {
            int anrModelId = c1.getInt(c1.getColumnIndex(ANRMODEL_ID));
            String[] plotColms = c1.getColumnNames();
            //need to check all the control plots are inserting.
            do {
                HashMap<String, String> plotMap = new HashMap<>();
                int iter = 0;
                for (String plotCol : plotColms) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            plotMap.put(plotCol, getTruncatedVarchar(c1.getString(c1.getColumnIndex(plotCol)), columnType));
                        } else {
                            plotMap.put(plotCol, c1.getString(c1.getColumnIndex(plotCol)));
                        }
                    } catch (Exception e) {
                        plotMap.put(plotCol, "");
                    }
                    iter++;
                }
                plotMapMasterList.add(plotMap);
            } while (c1.moveToNext());

            c1.close();

            String query2 = "SELECT * FROM " + TABLE_CONTROL_PLOT_INVENTORY + " WHERE " + ANRMODEL_ID + "=?";
            Cursor c2 = db.rawQuery(query2, new String[]{String.valueOf(anrModelId)});
            String[] plotInventColms = c2.getColumnNames();
            columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_CONTROL_PLOT_INVENTORY, this).get("columnTypesList");
            if (c2.moveToNext()) {
                do {
                    HashMap<String, String> plotInventMap = new HashMap<>();
                    int iter = 0;
                    for (String inventoryCol : plotInventColms) {
                        try {
                            String columnType = columnTypes.get(iter);
                            if (columnType.contains("varchar")) {
                                plotInventMap.put(inventoryCol, getTruncatedVarchar(c2.getString(c2.getColumnIndex(inventoryCol)), columnType));
                            } else {
                                plotInventMap.put(inventoryCol, c2.getString(c2.getColumnIndex(inventoryCol)));
                            }
                        } catch (Exception e) {
//                            plotInventMap.put(inventoryCol, c2.getString(c2.getColumnIndex(inventoryCol)));
                            plotInventMap.put(inventoryCol, "");
                        }
                        iter++;
                    }
                    plotInventMapList.add(plotInventMap);
                } while (c2.moveToNext());
            }
            c2.close();

        }
        data.add(gson.toJson(plotMapMasterList));
        data.add(gson.toJson(plotInventMapList));
        return data;
    }

    private String getSCPTSPSurveyData(int formId) {
        String data = "";
        SQLiteDatabase db = getReadableDatabase();
        String query1 = "SELECT * FROM " + TABLE_SCP_N_TSP_SURVEY + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query1, new String[]{String.valueOf(formId)});
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SCP_N_TSP_SURVEY, this).get("columnTypesList");
        List<HashMap<String, String>> community = new ArrayList<>();
        if (c != null) {
            String[] cols = c.getColumnNames();
            while (c.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();
                int iter = 0;
                for (String col : cols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            map.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                        } else {
                            String sNull = c.getString(c.getColumnIndex(col));
                            if (sNull == null)
                                sNull = "";
                            map.put(col, sNull);
                        }
                    } catch (Exception e) {
                        map.put(col, "");
                    }
                    iter++;
                }
                community.add(map);

            }
            data = new Gson().toJson(community);
            c.close();
        }
        return data;
    }

    private String getNurseryWorksBaggedSeedlingsForUploadData(int formId) {
        String data = "";
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS + " WHERE " + FORM_ID + " = " + formId;
        Cursor c = db.rawQuery(query, null);
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.KFD_NURSERY_WORKS_BAGGED_SEEDLINGS, this).get("columnTypesList");
        List<HashMap<String, String>> kfdTran = new ArrayList<>();
        if (c != null) {
            String[] cols = c.getColumnNames();
            while (c.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();
                int iter = 0;
                for (String col : cols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            map.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                        } else {
                            map.put(col, c.getString(c.getColumnIndex(col)));
                        }
                    } catch (Exception e) {
//                        map.put(col, c.getString(c.getColumnIndex(col)));
                        map.put(col, "");
                    }
                    iter++;
                }
                kfdTran.add(map);
            }
            c.close();
            data = new Gson().toJson(kfdTran);
        }
        return data;
    }

    private String getNurseryWorksSeedBedForUploadData(int formId) {
        String data = "";
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_NURSERY_WORKS_SEED_BED + " WHERE " + FORM_ID + " = " + formId;
        Cursor c = db.rawQuery(query, null);
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.KFD_NURSERY_WORKS_SEED_BED, this).get("columnTypesList");
        List<HashMap<String, String>> kfdTran = new ArrayList<>();
        if (c != null) {
            String[] cols = c.getColumnNames();
            while (c.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();
                int iter = 0;
                for (String col : cols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            map.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                        } else {
                            map.put(col, c.getString(c.getColumnIndex(col)));
                        }
                    } catch (Exception e) {
                        map.put(col, c.getString(c.getColumnIndex(col)));
                    }
                    iter++;
                }
                kfdTran.add(map);
            }
            c.close();
            data = new Gson().toJson(kfdTran);
        }
        return data;
    }

    private String getNurseryWorksBaggedSeedlingsQualityForUploadData(int formId) {
        String dataTableData = "";
        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS_QUALITY + " WHERE " + FORM_ID + "= ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(KFD_NURSERY_WORKS_BAGGED_SEEDLINGS_QUALITY, this).get("columnTypesList");
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int iter = 0;
        if (c != null && c.moveToFirst()) {
            String[] colArrDataTable = c.getColumnNames();
            int id = c.getInt(c.getColumnIndex(FORM_ID));
            HashMap<String, String> dataTableMap = new HashMap<>();
            for (String col : colArrDataTable) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        dataTableMap.put(col, getTruncatedVarchar(c.getString(c.getColumnIndex(col)), columnType));
                    } else {
                        dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    }
                } catch (Exception e) {
//                    dataTableMap.put(col, c.getString(c.getColumnIndex(col)));
                    dataTableMap.put(col, "");
                }
                iter++;
            }
            dataTableData = gson.toJson(dataTableMap);
            //data.add(dataTableData);
            c.close();
        }
        return dataTableData;
    }

    public String getNamesOfSpecies() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SPECIES_MASTER + " ORDER BY " + LOCAL_NAME + " ASC";
        StringBuilder options = new StringBuilder();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            options = new StringBuilder(c.getString(c.getColumnIndex(LOCAL_NAME)) + " ( " + c.getString(c.getColumnIndex(BOTANICAL_NAME)) + " )");
        }
        while (c.moveToNext()) {
            options.append("|").append(c.getString(c.getColumnIndex(LOCAL_NAME))).append(" ( ").append(c.getString(c.getColumnIndex(BOTANICAL_NAME))).append(" )");
        }
        c.close();
        return options.toString();
    }

    public ArrayList<NamesWithID> getNamesofSpeciesWithId(String modelId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NamesWithID> speciesList = new ArrayList<>();
        NamesWithID species = new NamesWithID();
        species.setName("Others");
        species.setId(0);
        speciesList.add(species);
        String query = "SELECT " + SPECIES_ID + "," + SPECIES + " FROM " + KFD_PLNT_SPECIES_MASTER_2018 + " WHERE " + MODEL_ID + "=?";
        Cursor c = db.rawQuery(query, new String[]{modelId});
        while (c.moveToNext()) {
            species = new NamesWithID();
            species.setName(c.getString(c.getColumnIndex(SPECIES)));
            species.setId(c.getLong(c.getColumnIndex(SPECIES_ID)));
            speciesList.add(species);
        }
        c.close();
        return speciesList;

    }

    public ArrayList<NamesWithID> getNamesofSpeciesWithId() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NamesWithID> speciesList = new ArrayList<>();
        NamesWithID species = new NamesWithID();
        species.setName("Others");
        species.setId(0);
        speciesList.add(species);
        String query = "SELECT " + SPECIES_ID + "," + SPECIES + " FROM " + KFD_PLNT_SPECIES_MASTER_2018;
        Cursor c = db.rawQuery(query, null);
        while (c.moveToNext()) {
            species = new NamesWithID();
            species.setName(c.getString(c.getColumnIndex(SPECIES)));
            species.setId(c.getLong(c.getColumnIndex(SPECIES_ID)));
            speciesList.add(species);
        }
        c.close();
        return speciesList;

    }

    public String getNamesOfSpeciesNew(String modelId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_PLNT_SPECIES_MASTER_2018 + " WHERE " + MODEL_ID + "=?";
        //" ORDER BY " + LOCAL_NAME + " ASC";
        StringBuilder options = new StringBuilder();
        Cursor c = db.rawQuery(query, new String[]{modelId});
        if (c.moveToFirst()) {
            options = new StringBuilder(c.getString(c.getColumnIndex(PLNT_SPECIES)));
            // options = c.getString(c.getColumnIndex(LOCAL_NAME)) + " ( " + c.getString(c.getColumnIndex(BOTANICAL_NAME)) + " )";
        }
        while (c.moveToNext()) {
            options.append("|").append(c.getString(c.getColumnIndex(PLNT_SPECIES)));
            //  options = options + "|" + c.getString(c.getColumnIndex(LOCAL_NAME)) + " ( " + c.getString(c.getColumnIndex(BOTANICAL_NAME)) + " )";
        }
        c.close();
        return options.toString();
    }

    public String getNamesOfSdpSpecies() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SDP_SPECIES_MASTER;
        StringBuilder options = new StringBuilder();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
//            options = new StringBuilder(c.getString(c.getColumnIndex(LOCAL_NAME)) + " ( " + c.getString(c.getColumnIndex(BOTANICAL_NAME)) + " )");
            options = new StringBuilder(c.getString(c.getColumnIndex(SPECIES)));
        }
        while (c.moveToNext()) {
//            options.append("|").append(c.getString(c.getColumnIndex(LOCAL_NAME))).append(" ( ").append(c.getString(c.getColumnIndex(BOTANICAL_NAME))).append(" )");
            options.append("|").append(c.getString(c.getColumnIndex(SPECIES)));
        }
        c.close();
        return options.toString();
    }

    public String getNamesOfScheme() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_SCHEME_MASTER + " ORDER BY " + SCHEME_NAME + " ASC";
        StringBuilder options = new StringBuilder();
        String schemeName;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            schemeName = c.getString(c.getColumnIndex(SCHEME_NAME));
            options = new StringBuilder(schemeName.substring(0, schemeName.length() > 30 ? 30 : schemeName.length()));
        }
        while (c.moveToNext()) {
            schemeName = c.getString(c.getColumnIndex(SCHEME_NAME));
            options.append("|").append(schemeName, 0, schemeName.length() > 30 ? 30 : schemeName.length());
        }
        c.close();
        return options.toString();
    }

    public ArrayList<NamesWithID> getSchemesWithId() {
        ArrayList<NamesWithID> schemesList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_SCHEME_MASTER_2018;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            NamesWithID namesWithID = new NamesWithID();
            namesWithID.setName(cursor.getString(cursor.getColumnIndex(SCHEME_NAME)));
            namesWithID.setId(cursor.getLong(cursor.getColumnIndex(SCHEME_ID)));
            schemesList.add(namesWithID);
        }
        cursor.close();
        return schemesList;
    }

    public String getNamesOfSchemeNew() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_SCHEME_MASTER_2018;
        StringBuilder options = new StringBuilder();
        String schemeName;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            schemeName = c.getString(c.getColumnIndex(SCHEME_NAME));
            options = new StringBuilder(schemeName.substring(0, schemeName.length() > 100 ? 100 : schemeName.length()));
        }
        while (c.moveToNext()) {
            schemeName = c.getString(c.getColumnIndex(SCHEME_NAME));
            options.append("|").append(schemeName, 0, schemeName.length() > 100 ? 100 : schemeName.length());
        }
        c.close();
        return options.toString();
    }

    public String getNamesOfModels() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_MODELS;
        StringBuilder options = new StringBuilder();
        String modelName;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            modelName = c.getString(c.getColumnIndex(MODEL_NAME));
            options = new StringBuilder(modelName.substring(0, modelName.length() > 100 ? 100 : modelName.length()));
        }
        while (c.moveToNext()) {
            modelName = c.getString(c.getColumnIndex(MODEL_NAME));
            options.append("|").append(modelName, 0, modelName.length() > 100 ? 100 : modelName.length());
        }
        c.close();
        return options.toString();
    }

    public String getNewNamesOfModels() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_MODELS_2018;
        StringBuilder options = new StringBuilder();
        String newModelName;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            newModelName = c.getString(c.getColumnIndex(NEW_MODEL_NAME));
            options = new StringBuilder(newModelName.substring(0, newModelName.length() > 100 ? 100 : newModelName.length()));
        }
        while (c.moveToNext()) {
            newModelName = c.getString(c.getColumnIndex(NEW_MODEL_NAME));
            options.append("|").append(newModelName, 0, newModelName.length() > 100 ? 100 : newModelName.length());
        }
        c.close();
        return options.toString();
    }


    public ArrayList<String> getSMCData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> data = new ArrayList<>();
        Gson gson = new Gson();
        String query = "SELECT * FROM " + TABLE_SMC_SAMPLING_MASTER + " WHERE " + FORM_ID + " = ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SMC_SAMPLING_MASTER, this).get("columnTypesList");
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        String[] smcCols = c.getColumnNames();
        if (c.moveToFirst()) {
            HashMap<String, String> smcMap = new HashMap<>();
            int iter = 0;
            for (String smcCol : smcCols) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        smcMap.put(smcCol, getTruncatedVarchar(c.getString(c.getColumnIndex(smcCol)), columnType));
                    } else {
                        smcMap.put(smcCol, c.getString(c.getColumnIndex(smcCol)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    smcMap.put(smcCol, c.getString(c.getColumnIndex(smcCol)));
                    smcMap.put(smcCol, "");
                }
                iter++;
            }
            c.close();
            data.add(gson.toJson(smcMap));
            /**/
            String query3 = "SELECT * FROM " + TABLE_SMC_LIST + " WHERE " + FORM_ID + " = ?";
            Cursor c3 = db.rawQuery(query3, new String[]{String.valueOf(formId)});
            String[] smclistCols = c3.getColumnNames();
            columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SMC_LIST, this).get("columnTypesList");
            ArrayList<HashMap<String, String>> smcMapList = new ArrayList<>();
            while (c3.moveToNext()) {
                HashMap<String, String> smclist = new HashMap<>();
                iter = 0;
                for (String smclistcol : smclistCols) {
                    String columnType = columnTypes.get(iter);
                    try {
                        if (columnType.contains("varchar")) {
                            smclist.put(smclistcol, getTruncatedVarchar(c3.getString(c3.getColumnIndex(smclistcol)), columnType));
                        } else {
                            smclist.put(smclistcol, c3.getString(c3.getColumnIndex(smclistcol)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        smclist.put(smclistcol, c3.getString(c3.getColumnIndex(smclistcol)));
                        smclist.put(smclistcol, "");
                    }
                    iter++;
                }
                smcMapList.add(smclist);
            }
            c3.close();
            data.add(gson.toJson(smcMapList));
            /**/
            String query1 = "SELECT * FROM " + TABLE_OTHER_SMC_LIST + " WHERE " + FORM_ID + "=?";
            Cursor c1 = db.rawQuery(query1, new String[]{String.valueOf(formId)});
            String[] othersmCols = c1.getColumnNames();
            columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_OTHER_SMC_LIST, this).get("columnTypesList");
            ArrayList<HashMap<String, String>> otherSMCMapList = new ArrayList<>();
            while (c1.moveToNext()) {
                HashMap<String, String> otherSMCMap = new HashMap<>();
                iter = 0;
                for (String smcDetailsCol : othersmCols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            otherSMCMap.put(smcDetailsCol, getTruncatedVarchar(c1.getString(c1.getColumnIndex(smcDetailsCol)), columnType));
                        } else {
                            otherSMCMap.put(smcDetailsCol, c1.getString(c1.getColumnIndex(smcDetailsCol)));
                        }
                    } catch (IndexOutOfBoundsException e) {
//                        otherSMCMap.put(smcDetailsCol, c1.getString(c1.getColumnIndex(smcDetailsCol)));
                        otherSMCMap.put(smcDetailsCol, "");
                    }
                    iter++;
                }
                otherSMCMapList.add(otherSMCMap);
            }
            c1.close();
            data.add(gson.toJson(otherSMCMapList));
            //----------------------------done by sunil-----------------------
            String query2 = "SELECT * FROM " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST + " WHERE " + FORM_ID + "=?";
            Cursor c2 = db.rawQuery(query2, new String[]{String.valueOf(formId)});
            String[] smcDetailsHighestCols = c2.getColumnNames();
            columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, this).get("columnTypesList");
            ArrayList<HashMap<String, String>> smcDetailsHighestMapList = new ArrayList<>();
            while (c2.moveToNext()) {
                HashMap<String, String> smcDetailsHighestMap = new HashMap<>();
                iter = 0;
                for (String smcDetailsCol : smcDetailsHighestCols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            smcDetailsHighestMap.put(smcDetailsCol, getTruncatedVarchar(c2.getString(c2.getColumnIndex(smcDetailsCol)), columnType));
                        } else {
                            smcDetailsHighestMap.put(smcDetailsCol, c2.getString(c2.getColumnIndex(smcDetailsCol)));
                        }
                    } catch (IndexOutOfBoundsException e) {
//                        smcDetailsHighestMap.put(smcDetailsCol, c2.getString(c2.getColumnIndex(smcDetailsCol)));
                        smcDetailsHighestMap.put(smcDetailsCol, "");
                    }
                    iter++;
                }
                smcDetailsHighestMapList.add(smcDetailsHighestMap);
            }
            c1.close();
            data.add(gson.toJson(smcDetailsHighestMapList));
        }
        return data;
    }

    public ArrayList<String> getAdvSMCData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> data = new ArrayList<>();
        Gson gson = new Gson();
        String query = "SELECT * FROM " + TABLE_ADV_SMC_MASTER + " WHERE " + FORM_ID + " = ?";
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_ADV_SMC_MASTER, this).get("columnTypesList");
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        String[] smcCols = c.getColumnNames();
        if (c.moveToFirst()) {
            HashMap<String, String> smcMap = new HashMap<>();
            int iter = 0;
            for (String smcCol : smcCols) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        smcMap.put(smcCol, getTruncatedVarchar(c.getString(c.getColumnIndex(smcCol)), columnType));
                    } else {
                        smcMap.put(smcCol, c.getString(c.getColumnIndex(smcCol)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    smcMap.put(smcCol, c.getString(c.getColumnIndex(smcCol)));
                    smcMap.put(smcCol, "");
                }
                iter++;
            }
            c.close();
            data.add(gson.toJson(smcMap));
            /**/
            String query3 = "SELECT * FROM " + TABLE_ADV_SMC_LIST + " WHERE " + FORM_ID + " = ?";
            Cursor c3 = db.rawQuery(query3, new String[]{String.valueOf(formId)});
            String[] smclistCols = c3.getColumnNames();
            columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_ADV_SMC_LIST, this).get("columnTypesList");
            ArrayList<HashMap<String, String>> smcMapList = new ArrayList<>();
            while (c3.moveToNext()) {
                HashMap<String, String> smclist = new HashMap<>();
                iter = 0;
                for (String smclistcol : smclistCols) {
                    String columnType = columnTypes.get(iter);
                    try {
                        if (columnType.contains("varchar")) {
                            smclist.put(smclistcol, getTruncatedVarchar(c3.getString(c3.getColumnIndex(smclistcol)), columnType));
                        } else {
                            smclist.put(smclistcol, c3.getString(c3.getColumnIndex(smclistcol)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        smclist.put(smclistcol, c3.getString(c3.getColumnIndex(smclistcol)));
                        smclist.put(smclistcol, "");
                    }
                    iter++;
                }
                smcMapList.add(smclist);
            }
            c3.close();
            data.add(gson.toJson(smcMapList));
            /**/
            String query1 = "SELECT * FROM " + TABLE_ADV_OTHER_SMC_LIST + " WHERE " + FORM_ID + "=?";
            Cursor c1 = db.rawQuery(query1, new String[]{String.valueOf(formId)});
            String[] othersmCols = c1.getColumnNames();
            columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_ADV_OTHER_SMC_LIST, this).get("columnTypesList");
            ArrayList<HashMap<String, String>> otherSMCMapList = new ArrayList<>();
            while (c1.moveToNext()) {
                HashMap<String, String> otherSMCMap = new HashMap<>();
                iter = 0;
                for (String smcDetailsCol : othersmCols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            otherSMCMap.put(smcDetailsCol, getTruncatedVarchar(c1.getString(c1.getColumnIndex(smcDetailsCol)), columnType));
                        } else {
                            otherSMCMap.put(smcDetailsCol, c1.getString(c1.getColumnIndex(smcDetailsCol)));
                        }
                    } catch (IndexOutOfBoundsException e) {
//                        otherSMCMap.put(smcDetailsCol, c1.getString(c1.getColumnIndex(smcDetailsCol)));
                        otherSMCMap.put(smcDetailsCol, "");
                    }
                    iter++;
                }
                otherSMCMapList.add(otherSMCMap);
            }
            data.add(gson.toJson(otherSMCMapList));
            //----------------------------done by sunil-----------------------
            String query2 = "SELECT * FROM " + TABLE_ADV_SMC_HIGHEST + " WHERE " + FORM_ID + "=?";
            Cursor c2 = db.rawQuery(query2, new String[]{String.valueOf(formId)});
            String[] smcDetailsHighestCols = c2.getColumnNames();
            columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_ADV_SMC_HIGHEST, this).get("columnTypesList");
            ArrayList<HashMap<String, String>> smcDetailsHighestMapList = new ArrayList<>();
            while (c2.moveToNext()) {
                HashMap<String, String> smcDetailsHighestMap = new HashMap<>();
                iter = 0;
                for (String smcDetailsCol : smcDetailsHighestCols) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            smcDetailsHighestMap.put(smcDetailsCol, getTruncatedVarchar(c2.getString(c2.getColumnIndex(smcDetailsCol)), columnType));
                        } else {
                            smcDetailsHighestMap.put(smcDetailsCol, c2.getString(c2.getColumnIndex(smcDetailsCol)));
                        }
                    } catch (IndexOutOfBoundsException e) {
//                        smcDetailsHighestMap.put(smcDetailsCol, c2.getString(c2.getColumnIndex(smcDetailsCol)));
                        smcDetailsHighestMap.put(smcDetailsCol, "");
                    }
                    iter++;
                }
                smcDetailsHighestMapList.add(smcDetailsHighestMap);
            }
            c1.close();
            data.add(gson.toJson(smcDetailsHighestMapList));
        }
        return data;
    }

    public String getBoundaryProtectionData(int formId) {

        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_PROTECTION + " WHERE " + FORM_ID + "=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        String[] protColumns = c.getColumnNames();
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(TABLE_PROTECTION, this).get("columnTypesList");
        ArrayList<HashMap<String, String>> pMapList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                int iter = 0;
                HashMap<String, String> pMap = new HashMap<>();
                for (String prcol : protColumns) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            pMap.put(prcol, getTruncatedVarchar(c.getString(c.getColumnIndex(prcol)), columnType));
                        } else {
                            pMap.put(prcol, c.getString(c.getColumnIndex(prcol)));
                        }

                    } catch (Exception e) {
//                        pMap.put(prcol, c.getString(c.getColumnIndex(prcol)));
                        pMap.put(prcol, "");
                    }
                    iter++;
                }
                pMapList.add(pMap);
            } while (c.moveToNext());
        }
        String s = gson.toJson(pMapList);
        c.close();
        return s;
    }

    public String getAdvBoundaryProtectionData(int formId) {

        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_ADV_PROTECTION + " WHERE " + FORM_ID + "=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        String[] protColumns = c.getColumnNames();
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(TABLE_ADV_PROTECTION, this).get("columnTypesList");
        ArrayList<HashMap<String, String>> pMapList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                int iter = 0;
                HashMap<String, String> pMap = new HashMap<>();
                for (String prcol : protColumns) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            pMap.put(prcol, getTruncatedVarchar(c.getString(c.getColumnIndex(prcol)), columnType));
                        } else {
                            pMap.put(prcol, c.getString(c.getColumnIndex(prcol)));
                        }

                    } catch (Exception e) {
//                        pMap.put(prcol, c.getString(c.getColumnIndex(prcol)));
                        pMap.put(prcol, "");
                    }
                    iter++;
                }
                pMapList.add(pMap);
            } while (c.moveToNext());
        }
        String s = gson.toJson(pMapList);
        c.close();
        return s;
    }

    public String getVFCData(int formId) {

        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VFC_SAMPLING + " WHERE " + FORM_ID + "=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        String[] vmcCols = c.getColumnNames();
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(TABLE_VFC_SAMPLING, this).get("columnTypesList");
        //  List<HashMap<String, String>> vfcArray = new ArrayList<>();
        HashMap<String, String> vfcMap = new HashMap<>();
        if (c.moveToFirst()) {
            int iter = 0;
            for (String vmcCol : vmcCols) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        vfcMap.put(vmcCol, getTruncatedVarchar(c.getString(c.getColumnIndex(vmcCol)), columnType));
                    } else {
                        vfcMap.put(vmcCol, c.getString(c.getColumnIndex(vmcCol)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    vfcMap.put(vmcCol, c.getString(c.getColumnIndex(vmcCol)));
                    vfcMap.put(vmcCol, "");
                }
                iter++;
            }
        }
        c.close();
        // vfcArray.add(vfcMap);
        return gson.toJson(vfcMap);

    }

    public String getAdvVFCData(int formId) {

        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADV_VFC_SAMPLING + " WHERE " + FORM_ID + "=?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        String[] vmcCols = c.getColumnNames();
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(TABLE_ADV_VFC_SAMPLING, this).get("columnTypesList");
        //  List<HashMap<String, String>> vfcArray = new ArrayList<>();
        HashMap<String, String> vfcMap = new HashMap<>();
        if (c.moveToFirst()) {
            int iter = 0;
            for (String vmcCol : vmcCols) {
                try {
                    String columnType = columnTypes.get(iter);
                    if (columnType.contains("varchar")) {
                        vfcMap.put(vmcCol, getTruncatedVarchar(c.getString(c.getColumnIndex(vmcCol)), columnType));
                    } else {
                        vfcMap.put(vmcCol, c.getString(c.getColumnIndex(vmcCol)));
                    }
                } catch (IndexOutOfBoundsException e) {
//                    vfcMap.put(vmcCol, c.getString(c.getColumnIndex(vmcCol)));
                    vfcMap.put(vmcCol, "");
                }
                iter++;
            }
        }
        c.close();
        // vfcArray.add(vfcMap);
        return gson.toJson(vfcMap);

    }

    public String getPlntSpecies(int formId) {

        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADD_SPECIES + " WHERE " + FORM_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        String[] plntSpeciesColumns = c.getColumnNames();
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_ADD_SPECIES, this).get("columnTypesList");
        List<HashMap<String, String>> speciesMap = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                int iter = 0;
                HashMap<String, String> plntspMap = new HashMap<>();
                for (String plntCls : plntSpeciesColumns) {

                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            plntspMap.put(plntCls, getTruncatedVarchar(c.getString(c.getColumnIndex(plntCls)), columnType));
                        } else {
                            plntspMap.put(plntCls, c.getString(c.getColumnIndex(plntCls)));
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                        plntspMap.put(plntCls, "");
                    }
                    iter++;
                }
                speciesMap.add(plntspMap);
            } while (c.moveToNext());

        }
        c.close();
        return gson.toJson(speciesMap);

    }

    public ArrayList<String> getSamplePlotData(int formId) {
        ArrayList<String> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Gson gson = new Gson();
        String query3 = "SELECT * FROM " + TABLE_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + "=?";
        Cursor c3 = db.rawQuery(query3, new String[]{String.valueOf(formId)});
        String[] plotMasterColms = c3.getColumnNames();
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SAMPLE_PLOT_MASTER, this).get("columnTypesList");
        List<HashMap<String, String>> plotMasterMapList = new ArrayList<>();
        //ArrayList<ArrayList<HashMap<String, String>>> plotInventDetailsMapListList = new ArrayList<>();
        if (c3.moveToFirst()) {

            do {
                int samplePlotId = c3.getInt(c3.getColumnIndex(SAMPLE_PLOT_ID));
                HashMap<String, String> plotMasterMap = new HashMap<>();
                int iter = 0;
                for (String plotMasterCol : plotMasterColms) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            plotMasterMap.put(plotMasterCol, getTruncatedVarchar(c3.getString(c3.getColumnIndex(plotMasterCol)), columnType));
                        } else {
                            plotMasterMap.put(plotMasterCol, c3.getString(c3.getColumnIndex(plotMasterCol)));
                        }
                    } catch (IndexOutOfBoundsException e) {
//                        plotMasterMap.put(plotMasterCol, c3.getString(c3.getColumnIndex(plotMasterCol)));
                        plotMasterMap.put(plotMasterCol, "");
                    }
                    iter++;
                }
                plotMasterMapList.add(plotMasterMap);
              /*  String query4 = "SELECT * FROM " + TABLE_SAMPLE_PLOT_INVENTORY + " WHERE " + SAMPLE_PLOT_ID + " = " + samplePlotId;
                Cursor c4 = db.rawQuery(query4, null);
                columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SAMPLE_PLOT_INVENTORY, this).get("columnTypesList");
                String[] plotInventDetailsColms = c4.getColumnNames();
                ArrayList<HashMap<String, String>> plotInventDetailsMapList = new ArrayList<>();
                if (c4 != null && c4.moveToFirst()) {
                    do {
                        HashMap<String, String> plotInventDetailsMap = new HashMap<>();
                        iter = 0;
                        for (String plotInventDetailsCol : plotInventDetailsColms) {
                            try {
                                String columnType = columnTypes.get(iter);
                                if (columnType.contains("varchar")) {
                                    plotInventDetailsMap.put(plotInventDetailsCol, getTruncatedVarchar(c4.getString(c4.getColumnIndex(plotInventDetailsCol)), columnType));
                                } else {
                                    plotInventDetailsMap.put(plotInventDetailsCol, c4.getString(c4.getColumnIndex(plotInventDetailsCol)));
                                }
                            } catch (IndexOutOfBoundsException e) {
                                Log.i("UploadError", e.toString());
                                plotInventDetailsMap.put(plotInventDetailsCol, c4.getString(c4.getColumnIndex(plotInventDetailsCol)));
                            }
                            iter++;
                        }
                        plotInventDetailsMapList.add(plotInventDetailsMap);
                    } while (c4.moveToNext());
                }*/
                //      plotInventDetailsMapListList.add(plotInventDetailsMapList);
            } while (c3.moveToNext());
            c3.close();
        }
        String query4 = "SELECT * FROM " + TABLE_SAMPLE_PLOT_INVENTORY + " WHERE " + FORM_ID + " = ?";
        Cursor c4 = db.rawQuery(query4, new String[]{String.valueOf(formId)});
        columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_SAMPLE_PLOT_INVENTORY, this).get("columnTypesList");
        String[] plotInventDetailsColms = c4.getColumnNames();
        ArrayList<HashMap<String, String>> plotInventDetailsMapList = new ArrayList<>();
        if (c4.moveToFirst()) {
            do {
                HashMap<String, String> plotInventDetailsMap = new HashMap<>();
                int iter = 0;
                for (String plotInventDetailsCol : plotInventDetailsColms) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            plotInventDetailsMap.put(plotInventDetailsCol, getTruncatedVarchar(c4.getString(c4.getColumnIndex(plotInventDetailsCol)), columnType));
                        } else {
                            plotInventDetailsMap.put(plotInventDetailsCol, c4.getString(c4.getColumnIndex(plotInventDetailsCol)));
                        }
                    } catch (IndexOutOfBoundsException e) {
//                        plotInventDetailsMap.put(plotInventDetailsCol, c4.getString(c4.getColumnIndex(plotInventDetailsCol)));
                        plotInventDetailsMap.put(plotInventDetailsCol, "");
                    }
                    iter++;
                }
                plotInventDetailsMapList.add(plotInventDetailsMap);
            } while (c4.moveToNext());
            c4.close();
        }
        data.add(gson.toJson(plotMasterMapList));
        data.add(gson.toJson(plotInventDetailsMapList));
        //data.add(gson.toJson(plotInventDetailsMapListList));

        return data;
    }

    public ArrayList<String> getAdvSamplePlotData(int formId) {
        ArrayList<String> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Gson gson = new Gson();
        String query3 = "SELECT * FROM " + TABLE_ADV_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + "=?";
        Cursor c3 = db.rawQuery(query3, new String[]{String.valueOf(formId)});
        String[] plotMasterColms = c3.getColumnNames();
        ArrayList<String> columnTypes = SurveyCreation.getTableMetaData(Database.TABLE_ADV_SAMPLE_PLOT_MASTER, this).get("columnTypesList");
        List<HashMap<String, String>> plotMasterMapList = new ArrayList<>();
        if (c3.moveToFirst()) {

            do {
                int samplePlotId = c3.getInt(c3.getColumnIndex(SAMPLE_PLOT_ID));
                HashMap<String, String> plotMasterMap = new HashMap<>();
                int iter = 0;
                for (String plotMasterCol : plotMasterColms) {
                    try {
                        String columnType = columnTypes.get(iter);
                        if (columnType.contains("varchar")) {
                            plotMasterMap.put(plotMasterCol, getTruncatedVarchar(c3.getString(c3.getColumnIndex(plotMasterCol)), columnType));
                        } else {
                            plotMasterMap.put(plotMasterCol, c3.getString(c3.getColumnIndex(plotMasterCol)));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        plotMasterMap.put(plotMasterCol, "");
                    }
                    iter++;
                }
                plotMasterMapList.add(plotMasterMap);
            } while (c3.moveToNext());
            c3.close();
        }

        data.add(gson.toJson(plotMasterMapList));
        return data;
    }

    public String getFormType(int formId) {
        String type = "";
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + FORM_TYPE + " FROM " + TABLE_SURVEY_MASTER + " WHERE " + SURVEY_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        if (c.moveToFirst()) {
            type = c.getString(c.getColumnIndex(FORM_TYPE));
        }
        c.close();
        return type;
    }

    public String getBoundaryData(String formId) {
        Gson gson = new Gson();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select * from " + TABLE_GPS_COORDINATES + " where " + FORM_ID + "=" + formId;
        Cursor c1 = db.rawQuery(sql, null);
        ArrayList<HashMap<String, String>> gpsDataList = new ArrayList<>();
        while (c1.moveToNext()) {
            String[] colNames = c1.getColumnNames();
            HashMap<String, String> gpsMap = new HashMap<>();
            for (String col : colNames) {
                gpsMap.put(col, c1.getString(c1.getColumnIndex(col)));
            }
            gpsDataList.add(gpsMap);
        }
        c1.close();
        return gson.toJson(gpsDataList);

    }

    public boolean getPendingApprovedforms() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SURVEY_MASTER + " WHERE " + FORM_STATUS + " = 0";
        Cursor c = db.rawQuery(query, null);
        return c.getCount() > 0;
    }


    public boolean getFormFilledStatus(String tableName, String formID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + FORM_ID + " = ? AND " + FORM_FILLED_STATUS + " = 0";
        Cursor c = db.rawQuery(query, new String[]{formID});
        return c.getCount() > 0;
    }

    public boolean getSpeciesCompletedStatus(String tableName, String formID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + FORM_ID + " = ? AND " + SPECIES_COMPLETED_STATUS + " = 0";
        Cursor c = db.rawQuery(query, new String[]{formID});
        return c.getCount() > 0;
    }

    public boolean getEmptyPitStatus(String tableName, String formID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + FORM_ID + " = ? AND " + EMPTY_PIT_STATUS + " = 0";
        Cursor c = db.rawQuery(query, new String[]{formID});
        return c.getCount() > 0;
    }

    public boolean getFormFilledStatus(String tableName, String formID, String benefitType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + FORM_ID + " = ? AND " + NATURE_OF_BENEFIT + "= ? AND " + FORM_FILLED_STATUS + " = 0";
        Cursor c = db.rawQuery(query, new String[]{formID, benefitType});
        return c.getCount() > 0;
    }


    public String getFailedIdString(ArrayList<String> failedIds) {
        StringBuilder string = new StringBuilder();
        for (String s : failedIds) {
            if (string.toString().equals("")) {
                string = new StringBuilder(s);
            } else {
                string.append(", ").append(s);
            }
        }
        return string.toString();
    }

    public String getDataTableName(String formType) {
        String tableName = "";
        switch (formType) {
            case Constants.FORMTYPE_OTHERWORKS:

                tableName = TABLE_OTHER_WORKS;

                break;
            case Constants.FORMTYPE_SCPTSP:

                tableName = TABLE_SCP_N_TSP;

                break;
            case Constants.FORMTYPE_PLANTSAMPLING:

                tableName = TABLE_PLANTATION;

                break;
            case Constants.FORMTYPE_SDP:

                tableName = TABLE_SDP;

                break;
            case Constants.FORMTYPE_ADVANCEWORK:
                tableName = TABLE_ADVANCEWORK;
                break;
            case Constants.FORMTYPE_NURSERY_WORK:
                tableName = KFD_NURSERY_WORKS;
                break;
            case Constants.FORMTYPE_TRANSITORY_WORK:

                tableName = KFD_TRANSITORY_WORKS;

                break;
        }
        return tableName;
    }

    public Cursor getColumnNames(String tableName) {
        String sql = "";
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
    }

    public void setSurveyUploaded(String surveyId) {
        SQLiteDatabase db = getWritableDatabase();
        String where = SURVEY_ID + " = ?";
        ContentValues cv = new ContentValues();
        cv.put(FORM_STATUS, Constants.UPLOADED);
        db.update(TABLE_SURVEY_MASTER, cv, where, new String[]{surveyId});
        db.close();
    }

    //---------------done by sunil----------------
    public void setServerFormIdAndFormStatusToUploaded(String surveyId) {
        SQLiteDatabase db = getWritableDatabase();
        String where = SURVEY_ID + " = ?";
        ContentValues cv = new ContentValues();
        cv.put(FORM_STATUS, Constants.UPLOADED);
        // cv.put(SERVER_FORM_ID, serverFormId);
        db.update(TABLE_SURVEY_MASTER, cv, where, new String[]{surveyId});
        db.close();
    }

    //---------------------------------------------
    public void setSurveyUploaded(String surveyId, int val) {
        SQLiteDatabase db = getWritableDatabase();
        String where = SURVEY_ID + " = ?";
        ContentValues cv = new ContentValues();
        cv.put(FORM_STATUS, val);
        db.update(TABLE_SURVEY_MASTER, cv, where, new String[]{surveyId});
        db.close();
    }

    public Cursor getStats() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + FORM_TYPE + ", " + FORM_STATUS + ", count(*) tot   FROM " + TABLE_SURVEY_MASTER + " group by " + FORM_TYPE + ", " + FORM_STATUS + "";
        return db.rawQuery(sql, null);
    }

    //-----------------------changes done by sunil----------------------
    public Cursor getStatsForPhototInfoCreation() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + FORM_TYPE + ", " + FORM_STATUS + ", " + WORK_CODE + "," + SURVEY_ID + "," + SERVER_FORM_ID + " FROM " + TABLE_SURVEY_MASTER;
        return db.rawQuery(sql, null);
    }

    public Cursor getBeneficiariesInfoForPhotoCreation(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BENEFICIARY + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    //-------------------------------------------------------------------
    public Cursor getPlotInventoryInfoForPhotoCreation(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getPlotInventoryInfoForPhotoCreationInsideSamplePlotFolder() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAMPLE_PLOT_MASTER;
        return db.rawQuery(query, null);

    }

    public Cursor getWorkCodeForFormId(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SURVEY_MASTER + " WHERE " + SURVEY_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getKfdPlantationSamplingData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PLANTATION + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getKfdAdvanceWorkData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADVANCEWORK + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getKfdSdpgData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SDP + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getOtherWorksData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_OTHER_WORKS + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});

    }

    public Cursor getNurseryData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + KFD_NURSERY_WORKS + " WHERE " + FORM_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(formId)});
    }

    public Cursor getSCPTSPData(int formId) {
        SQLiteDatabase db = getReadableDatabase();
//        String query = "SELECT DISTINCT " + VILLAGE_NAME + "," + FORM_ID + " FROM " + TABLE_SCP_N_TSP + " WHERE " + FORM_ID + " = " + formId;
        String query = "SELECT DISTINCT " + VILLAGE_NAME + ", A." + FORM_ID + ",A." + COMPLETED_POSITION + " FROM " + TABLE_SCP_N_TSP + " A INNER JOIN " + TABLE_SCP_N_TSP_SURVEY + " B ON A." + FORM_ID + " = B." + FORM_ID + " WHERE A." + FORM_ID + " = " + formId;
        return db.rawQuery(query, null);

    }

    public Cursor getSCPTSPBenefitData(int formId, String benType) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SCP_N_TSP_SURVEY + " A LEFT JOIN " + TABLE_SCP_N_TSP + " B ON A." + FORM_ID + "=B." + FORM_ID + " AND A." + NATURE_OF_BENEFIT + " = B." + NATURE_OF_BENEFIT + " WHERE A." + FORM_ID + " = " + formId + " AND B." + NATURE_OF_BENEFIT + "='" + benType + "'";
        return db.rawQuery(query, null);

    }

    public Cursor getLeftJoinSCPTSP(int formId, String benType, int benId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SCP_N_TSP_SURVEY + " as a LEFT JOIN " + TABLE_SCP_N_TSP + " as b  ON " + " a." + FORM_ID + "=" + " b." + FORM_ID +
                " WHERE " + "a." + FORM_ID + " = " + formId + " AND a." + NATURE_OF_BENEFIT + " = '" + benType + "'" + " AND b." + NATURE_OF_BENEFIT + " = '" + benType + "'" + " AND a." + BENEFIT_ID + " = " + benId;
        return db.rawQuery(query, null);

    }


    //-------------------------------------------------------------------


    public ArrayList<LocationWithID> getDistrictsWithCode() {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID district = new LocationWithID();
        district.setName("Select a district");
        district.setId(0);
        list.add(district);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + DISTRICT_NAME_NEW + "," + DISTRICT_CODE + "  FROM " + KFD_DISTRICT_MASTER_NEW;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            district = new LocationWithID();
            district.setName(c.getString(c.getColumnIndex(DISTRICT_NAME_NEW)));
            district.setId(c.getLong(c.getColumnIndex(DISTRICT_CODE)));
            list.add(district);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getTaluksWithCode(String Code) {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID taluk = new LocationWithID();
        taluk.setName("Select a taluk");
        taluk.setId(0);
        list.add(taluk);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + TALUK_NAME + "," + TALUK_CODE + "  FROM " + KFD_DISTRICT_MASTER_NEW + " WHERE " + DISTRICT_CODE + " =?";
        Cursor c = db.rawQuery(sql, new String[]{Code});
        while (c.moveToNext()) {
            taluk = new LocationWithID();
            taluk.setName(c.getString(c.getColumnIndex(TALUK_NAME)));
            taluk.setId(c.getLong(c.getColumnIndex(TALUK_CODE)));
            list.add(taluk);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getGrampanchayatsWithCode(String Code) {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID panchayat = new LocationWithID();
        panchayat.setName("Select a Grampanchayat");
        panchayat.setId(0);
        list.add(panchayat);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + PANCHAYAT_NAME + "," + PANCHAYAT_CODE + "  FROM " + KFD_DISTRICT_MASTER_NEW + " WHERE " + TALUK_CODE + " =?";
        Cursor c = db.rawQuery(sql, new String[]{Code});
        while (c.moveToNext()) {
            panchayat = new LocationWithID();
            panchayat.setName(c.getString(c.getColumnIndex(PANCHAYAT_NAME)));
            panchayat.setId(c.getLong(c.getColumnIndex(PANCHAYAT_CODE)));
            list.add(panchayat);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getVillagesWithCode(String Code) {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID village = new LocationWithID();
        village.setName("Select a Village");
        village.setId(0);
        list.add(village);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + VILLAGE_NAME_NEW + "," + VILLAGE_CODE + "  FROM " + KFD_DISTRICT_MASTER_NEW + " WHERE " + PANCHAYAT_CODE + " =?";
        Cursor c = db.rawQuery(sql, new String[]{Code});
        while (c.moveToNext()) {
            village = new LocationWithID();
            village.setName(c.getString(c.getColumnIndex(VILLAGE_NAME_NEW)));
            village.setId(c.getLong(c.getColumnIndex(VILLAGE_CODE)));
            list.add(village);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getConstiuencyWithCode(String Code) {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID constituency = new LocationWithID();
        constituency.setName("Select a constituency");
        constituency.setId(0);
        list.add(constituency);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + CONSTITUENCY_NAME + "," + CONSTITUENCY_ID + "  FROM " + TABLE_CONSTITUENCY + " WHERE " + DISTRICT_ID + " =?";
        Cursor c = db.rawQuery(sql, new String[]{Code});
        while (c.moveToNext()) {
            constituency = new LocationWithID();
            constituency.setName(c.getString(c.getColumnIndex(CONSTITUENCY_NAME)));
            constituency.setId(c.getLong(c.getColumnIndex(CONSTITUENCY_ID)));
            list.add(constituency);
        }
        c.close();
        return list;
    }

    public ArrayList<LocationWithID> getConstiuencyWithCode() {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID constituency = new LocationWithID();
        constituency.setName("Select a constituency");
        constituency.setId(0);
        list.add(constituency);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + CONSTITUENCY_NAME + "," + CONSTITUENCY_ID + "  FROM " + TABLE_CONSTITUENCY;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            constituency = new LocationWithID();
            constituency.setName(c.getString(c.getColumnIndex(CONSTITUENCY_NAME)));
            constituency.setId(c.getLong(c.getColumnIndex(CONSTITUENCY_ID)));
            list.add(constituency);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getCirclesWithCode() {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID circles = new LocationWithID();
        circles.setName("Select a circle");
        circles.setId(0);
        list.add(circles);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + CIRCLE_NAME_NEW + "," + CIRCLE_ID + "  FROM " + KFD_CIRCLES_MASTER_2018 + " ORDER BY " + CIRCLE_NAME_NEW + " ASC";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            circles = new LocationWithID();
            circles.setName(c.getString(c.getColumnIndex(CIRCLE_NAME_NEW)));
            circles.setId(c.getLong(c.getColumnIndex(CIRCLE_ID)));
            list.add(circles);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getDivisionsWithCode(String Code) {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID divisions = new LocationWithID();
        divisions.setName("Select a division");
        divisions.setId(0);
        list.add(divisions);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + DIVISION_NAME_NEW + "," + DIVISION_CODE + "  FROM " + KFD_CIRCLES_MASTER_2018 + " WHERE " + CIRCLE_ID + " =?";
        Cursor c = db.rawQuery(sql, new String[]{Code});
        while (c.moveToNext()) {
            divisions = new LocationWithID();
            divisions.setName(c.getString(c.getColumnIndex(DIVISION_NAME_NEW)));
            divisions.setId(c.getLong(c.getColumnIndex(DIVISION_CODE)));
            list.add(divisions);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getSubDivisionsWithCode(String divCode) {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID subDivisions = new LocationWithID();
        subDivisions.setName("Select a subdivision");
        subDivisions.setId(0);
        list.add(subDivisions);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + SUBDIVISION_NAME_NEW + "," + SUBDIVISION_CODE + "  FROM " + KFD_CIRCLES_MASTER_2018 + " WHERE " + DIVISION_CODE + " =?";
        Cursor c = db.rawQuery(sql, new String[]{divCode});
        while (c.moveToNext()) {
            subDivisions = new LocationWithID();
            subDivisions.setName(c.getString(c.getColumnIndex(SUBDIVISION_NAME_NEW)));
            subDivisions.setId(c.getLong(c.getColumnIndex(SUBDIVISION_CODE)));
            list.add(subDivisions);
        }
        c.close();
        return list;
    }


    public ArrayList<LocationWithID> getRangesWithCode(String code) {
        ArrayList<LocationWithID> list = new ArrayList<>();
        LocationWithID ranges = new LocationWithID();
        ranges.setName("Select a range");
        ranges.setId(0);
        list.add(ranges);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT " + RANGE_ENAME + "," + RANGE_CODE + "  FROM " + KFD_CIRCLES_MASTER_2018 + " WHERE " + SUBDIVISION_CODE + " =?";
        Cursor c = db.rawQuery(sql, new String[]{code});
        while (c.moveToNext()) {
            ranges = new LocationWithID();
            ranges.setName(c.getString(c.getColumnIndex(RANGE_ENAME)));
            ranges.setId(c.getLong(c.getColumnIndex(RANGE_CODE)));
            list.add(ranges);
        }
        c.close();
        return list;
    }


    public int getUnuploadedRecords(ArrayList<String> failedIds) {
        SQLiteDatabase db = getReadableDatabase();
        String failedIdString = getFailedIdString(failedIds);
        String query = "SELECT * FROM " + TABLE_SURVEY_MASTER + " WHERE " + FORM_STATUS + " = " + Constants.APPROVED +
                " AND " + SURVEY_ID + " NOT IN (" + failedIdString + ")";
        Cursor c = db.rawQuery(query, null);
        int count = 0;
        if (c != null) {
            while (c.moveToNext()) {
                count++;
            }
            c.close();
        }

        return count;
    }


    public void insertAppSettingsTable(ArrayList<String> data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GPS_LATITUDE, data.get(0));
        cv.put(GPS_LONGITUDE, data.get(1));
        cv.put(VERSION_NAME, data.get(2));
        cv.put(APP_ID, data.get(3));
        cv.put(CREATION_TIMESTAMP, data.get(4));
        db.insert(TABLE_APPSETTINGS, null, cv);
    }

    public void insertNotification(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTIFICATION, null, cv);
    }


    public int getAppStatus() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + APP_STATUS + " FROM " + TABLE_APPSETTINGS;
        Cursor cursor = db.rawQuery(query, null);
        int appStatus = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                appStatus = cursor.getInt(0);
            }
            cursor.close();
        }
        return appStatus;
    }

    public int getAppId() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + APP_ID + " FROM " + TABLE_APPSETTINGS;
        Cursor cursor = db.rawQuery(query, null);
        int appId = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                appId = cursor.getInt(0);
            }
            cursor.close();
        }
        return appId;
    }


    public void insertIntoNurseryWorkBaggedSeedlings(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(KFD_NURSERY_WORKS_BAGGED_SEEDLINGS, null, cv);
    }

    public void insertIntoKfdNurseryWorkSeedBed(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        Log.i("INSERT", (String.valueOf(db.insert(KFD_NURSERY_WORKS_SEED_BED, null, cv))));
    }


    public void updateAppSettingsTable(ArrayList<String> data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CIRCLE_NAME, data.get(0));
        cv.put(DIVISION_NAME, data.get(1));
        cv.put(SUBDIVISION_NAME, data.get(2));
        cv.put(USER_NAME, data.get(3));
        cv.put(PHONE_NUMBER, data.get(4));
        cv.put(APP_STATUS, data.get(5));
        cv.put(APP_ID, data.get(6));
        db.update(TABLE_APPSETTINGS, cv, USER_ID + " = 1", null);
        db.close();
    }

    public void insertLogin(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_LOGIN, null, cv);
    }

    public ArrayList<String> getLoginDetails() {
        ArrayList<String> loginDetails = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_LOGIN;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                loginDetails.add(cursor.getString(cursor.getColumnIndex(EVALUATOR_NAME)));
                loginDetails.add(cursor.getString(cursor.getColumnIndex(EVALUATOR_PASSWORD)));
                loginDetails.add(cursor.getString(cursor.getColumnIndex(EVALUATION_YEAR)));

            }
            cursor.close();
        }
        return loginDetails;
    }

    public void deleteAllTestSurveys() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_LOGIN, null, null);
        /*plantation*/
        db.delete(TABLE_SURVEY_MASTER, null, null);
        db.delete(TABLE_PLANTATION, null, null);
        db.delete(TABLE_ADVANCEWORK, null, null);
        db.delete(TABLE_SMC_SAMPLING_MASTER, null, null);
        db.delete(TABLE_ADV_SMC_MASTER, null, null);
        db.delete(TABLE_SMC_SAMPLING_DETAILS, null, null);
        db.delete(TABLE_VFC_SAMPLING, null, null);
        db.delete(TABLE_CONTROL_PLOT_MASTER, null, null);
        db.delete(TABLE_CONTROL_PLOT_INVENTORY, null, null);
        db.delete(TABLE_SAMPLE_PLOT_INVENTORY, null, null);
        db.delete(TABLE_SAMPLE_PLOT_MASTER, null, null);
        db.delete(TABLE_ADV_SAMPLE_PLOT_MASTER, null, null);
        db.delete(TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, null, null);
        db.delete(TABLE_ADV_SMC_HIGHEST, null, null);
        db.delete(TABLE_ADD_BENEFICIARY_SPECIES, null, null);
        db.delete(TABLE_SAMPLEPLOT_SPECIES, null, null);
        db.delete(TABLE_OTHER_SMC_LIST, null, null);
        db.delete(TABLE_ADV_OTHER_SMC_LIST, null, null);
        db.delete(TABLE_SMC_LIST, null, null);
        db.delete(TABLE_ADV_SMC_LIST, null, null);
        db.delete(TABLE_SMC_SAMPLING_DETAILS, null, null);
        db.delete(TABLE_PROTECTION, null, null);
        db.delete(TABLE_ADV_PROTECTION, null, null);
        db.delete(TABLE_ADD_SPECIES, null, null);
        /*SDP*/
        db.delete(TABLE_SDP, null, null);
        db.delete(TABLE_BENEFICIARY, null, null);
        db.delete(TABLE_BENEFICIARY_SEEDLING, null, null);
        /*SCP_N_TSP*/
        db.delete(TABLE_SCP_N_TSP, null, null);
        db.delete(TABLE_SCP_TSP_BENIFICIARY, null, null);
        db.delete(TABLE_BENEFIT_LIST, null, null);
        db.delete(TABLE_SCP_N_TSP_SURVEY, null, null);
        /*OtherWorks*/
        db.delete(TABLE_OTHER_WORKS, null, null);

        /*Nursery works*/
        db.delete(KFD_NURSERY_WORKS, null, null);
        db.delete(KFD_NURSERY_WORKS_BAGGED_SEEDLINGS, null, null);
        db.delete(KFD_NURSERY_WORKS_SEED_BED, null, null);
        //--------done by sarath----------
        String photoDirectory = context.getExternalFilesDir(null) + "/Photo/";
        deleteFiles(photoDirectory);
        //-------------------------------
    }

    public int getPlantSamplingFormCount() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT count(*) FROM " + TABLE_SURVEY_MASTER + " WHERE " + FORM_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Constants.FORMTYPE_PLANTSAMPLING});
        int formCount = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                formCount = cursor.getInt(0);
            }
            cursor.close();
        }
        return formCount;
    }

    public int[] getSCPTSPAssetIds(String formId, String benType) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + BENEFIT_ID + " FROM " + TABLE_SCP_N_TSP_SURVEY + " WHERE " + FORM_ID + " =?" + " AND " + NATURE_OF_BENEFIT + " ='" + benType + "' ";
        Cursor c = db.rawQuery(sql, new String[]{formId});
        int len = c.getCount();
        int[] ids = new int[len];
        int i = 0;
        while (c.moveToNext()) {
            ids[i] = c.getInt(c.getColumnIndex(BENEFIT_ID));
            i++;
        }
        c.close();
        return ids;

    }

    public int[] getSamplePlotIds(String formId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + SAMPLE_PLOT_ID + " FROM " + TABLE_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " =?";
        Cursor c = db.rawQuery(sql, new String[]{formId});
        int len = c.getCount();
        int[] ids = new int[len];
        int i = 0;
        while (c.moveToNext()) {
            ids[i] = c.getInt(c.getColumnIndex(SAMPLE_PLOT_ID));
            i++;
        }
        c.close();
        return ids;
    }

    public int[] getAdvSamplePlotIds(String formId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + SAMPLE_PLOT_ID + " FROM " + TABLE_ADV_SAMPLE_PLOT_MASTER + " WHERE " + FORM_ID + " =?";
        Cursor c = db.rawQuery(sql, new String[]{formId});
        int len = c.getCount();
        int[] ids = new int[len];
        int i = 0;
        while (c.moveToNext()) {
            ids[i] = c.getInt(c.getColumnIndex(SAMPLE_PLOT_ID));
            i++;
        }
        c.close();
        return ids;
    }

    public ArrayList<String> getFormIds() {
        ArrayList<String> surveyIds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + SURVEY_ID + " FROM " + TABLE_SURVEY_MASTER;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            surveyIds.add(c.getString(c.getColumnIndex(SURVEY_ID)));
        }
        c.close();
        return surveyIds;
    }

    public int[] getSMCIds(String formId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + SMC_ID + " FROM " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST + " WHERE " + FORM_ID + " =?";
        Cursor c = db.rawQuery(sql, new String[]{formId});
        int len = c.getCount();
        int[] ids = new int[len];
        int i = 0;
        while (c.moveToNext()) {
            ids[i] = c.getInt(c.getColumnIndex(SMC_ID));
            i++;
        }
        c.close();
        return ids;
    }


    public int[] getAdvSMCIds(String formId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + SMC_ID + " FROM " + TABLE_ADV_SMC_HIGHEST + " WHERE " + FORM_ID + " =?";
        Cursor c = db.rawQuery(sql, new String[]{formId});
        int len = c.getCount();
        int[] ids = new int[len];
        int i = 0;
        while (c.moveToNext()) {
            ids[i] = c.getInt(c.getColumnIndex(SMC_ID));
            i++;
        }
        c.close();
        return ids;
    }

    public int[] getSCPTSPBeneficiaries(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT " + BENIFICIARY_ID + " FROM " + TABLE_SCP_TSP_BENIFICIARY + " WHERE " + FORM_ID + " =?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(formId)});
        int len = c.getCount();
        int[] ids = new int[len];
        int i = 0;
        while (c.moveToNext()) {
            /*beneficiary and benificary are differenet*/
            ids[i] = c.getInt(c.getColumnIndexOrThrow(BENIFICIARY_ID));
            i++;
        }
        c.close();
        return ids;
    }

    /*public String getCodeForRange(String circle, String division, String subdivision, String rangeName) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + RANGE_CODE + " FROM " + KFD_CIRCLES_MASTER + " WHERE " + CIRCLE_NAME + " = '" + circle
                + "' AND " + DIVISION_NAME + " = '" + division
                + "' AND " + SUBDIVISION_NAME + " = '" + subdivision
                + "' AND " + RANGE_NAME + " = '" + rangeName + "'";
        Cursor c = db.rawQuery(sql, null);
        String rangeCode = "";
        if (c != null && c.moveToFirst()) {
            rangeCode = c.getString(c.getColumnIndex(RANGE_CODE));
        }
        if (rangeCode.length() == 8) {
            return rangeCode;
        } else if (rangeCode.length() == 7) {
            return "0" + rangeCode;
        } else {
            return "";
        }
    }
*/

 /*   public String getCodeForRangePlant(String rangeName) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + RANGE_ID + " FROM " + KFD_RANGE_MASTER + " WHERE " + RANGE_ENAME + " = '" + rangeName + "'";
        Cursor c = db.rawQuery(sql, null);
        String rangeCode = "";
        if (c != null && c.moveToFirst()) {
            rangeCode = c.getString(c.getColumnIndex(RANGE_ID));
        }
        if (rangeCode.length() >= 0) {
            return rangeCode;
        } else if (rangeCode.length() == 7) {
            return "0" + rangeCode;
        }  else {
            return "";
        }
    }*/

    public int getUnuploadedSurveyImageFormId() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + SURVEY_ID + " from " + TABLE_SURVEY_MASTER + " where " + PHOTO_STATUS + " = 0 AND " + FORM_STATUS + " = 2";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(SURVEY_ID));
        }
        return 0;
    }

  /*  public int getUnuploadedSurveyImageFormCount() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "Select " + SURVEY_ID + " from " + TABLE_SURVEY_MASTER + " where " + PHOTO_STATUS + " = 0 AND " + FORM_STATUS + " = 2";
        Cursor c = db.rawQuery(sql, null);
        return c.getSpeciesCount();
    }*/

    public String getWorkCode(int formId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select " + WORK_CODE + " from " + TABLE_SURVEY_MASTER + " where " + SURVEY_ID + "= " + formId;
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            return c.getString(c.getColumnIndex(WORK_CODE));
        }
        return null;
    }

    public void setImagesUploaded(int formId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PHOTO_STATUS, 1);
        String where = SURVEY_ID + "=" + formId;
        db.update(TABLE_SURVEY_MASTER, cv, where, null);
    }

    private String createLoginDetails() {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_LOGIN + "(" +
                EVALUATOR_NAME + " varchar(40) DEFAULT NULL," +
                EVALUATOR_PASSWORD + " varchar(40) DEFAULT NULL," +
                EVALUATION_YEAR + " varchar(40) DEFAULT NULL" +
                ")";
    }

    private String createAppSettings() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_APPSETTINGS + "(" +
                GPS_LATITUDE + " varchar(40) DEFAULT NULL," +
                GPS_LONGITUDE + " varchar(40) DEFAULT NULL," +
                VERSION_NAME + " varchar(40) DEFAULT NULL," +
                APP_ID + " varchar(40) DEFAULT NULL," +
                CREATION_TIMESTAMP + " varchar(40) DEFAULT NULL" +
                ")";
    }

    public String createNotificationTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + "(" +
                NOTIFICATION_ID + " INTEGER(11) PRIMARY KEY ," +
                NOTIFICATION_STATUS + " INTEGER(2) DEFAULT -1," +
                NOTIFICATION_TITLE + " varchar(40) DEFAULT NULL," +
                NOTIFICATION_MESSAGE + " varchar(40) DEFAULT NULL," +
                NOTIFICATION_URL + " varchar(40) DEFAULT NULL," +
                CREATION_TIMESTAMP + " varchar(40) DEFAULT NULL" +
                ")";
    }

    private String createKfdPlantSamplngTablev2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_PLANTATION + " (" +
                FORM_ID + " INTEGER(11) DEFAULT 0 UNIQUE," +
                DRAW_MAP_STATUS + " INTEGER(2) DEFAULT 0," +
                FORM_FILLED_STATUS + " varchar(2) DEFAULT 0," +
                SAMPLE_PLOT_STATUS + " INTEGER(2) DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(2) DEFAULT -1," +
                PLANTING_ACTIVITY_STATUS + " INTEGER(2) DEFAULT 0," +
                OUTSIDE_PLANTATION_STATUS + " INTEGER(2) DEFAULT 0," +
                SMC_WORK_STATUS + " INTEGER(2) DEFAULT 0," +
                VFC_STATUS + " INTEGER(2) DEFAULT 0," +
                BOUNDARY_STATUS + " INTEGER(2) DEFAULT 0," +
                CIRCLE_NAME + " varchar(100) DEFAULT NULL," +
                CIRCLE_ID + " varchar(100) DEFAULT NULL," +
                DIVISION_NAME + " varchar(100) DEFAULT NULL," +
                DIVISION_CODE + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_NAME + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_CODE + " varchar(100) DEFAULT NULL," +
                RANGE_NAME + " varchar(100) DEFAULT NULL," +
                RANGE_CODE + " varchar(100) DEFAULT NULL," +
                DISTRICT_NAME + " varchar(100) DEFAULT NULL," +
                DISTRICT_CODE + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_NAME + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_ID + " varchar(100) DEFAULT NULL," +
                TALUK_NAME + " varchar(100) DEFAULT NULL," +
                TALUK_CODE + " varchar(100) DEFAULT NULL," +
                GRAMA_PANCHAYAT_NAME + " varchar(100) DEFAULT NULL," +
                PANCHAYAT_CODE + " varchar(100) DEFAULT NULL," +
                VILLAGE_NAME + " varchar(100) DEFAULT NULL," +
                VILLAGE_CODE + " varchar(100) DEFAULT NULL," +
                LEGAL_STATUS_OF_LAND + " varchar(50) DEFAULT NULL," +
                LEGAL_STATUS_OF_LAND_OTHER_DETAILS + " varchar(50) DEFAULT NULL," +
                RF_NAME + " varchar(100) DEFAULT NULL," +
                PLANTATION_NAME + " varchar(100) DEFAULT NULL," +
                WORK_CODE + " varchar(500) DEFAULT NULL," +
                SITE_QUALITY + " varchar(100) DEFAULT NULL," +
                APO + " varchar(10) DEFAULT NULL," +
                APO_DATE + " varchar(30) DEFAULT NULL," +
                WORK_ESTIMATES + " varchar(10) DEFAULT NULL," +
                NO_OF_WORK_ESTIMATES + " INTEGER(11) NOT NULL DEFAULT 0," +
                FNB + " varchar(10) DEFAULT NULL," +
                PLANTATION_JOURNAL + " varchar(10) DEFAULT NULL," +
                YEAR_OF_PLANTING + " varchar(15) DEFAULT NULL," +
                GROSS_PLANTATION_AREA_HA + " float DEFAULT 0," +
                NET_PLANTATION_AREA_HA + " float DEFAULT 0," +
                SCHEME_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SCHEME_NAME + " varchar(100) DEFAULT NULL," +
                PLANTATION_MODEL + " varchar(100) DEFAULT NULL," +
                PLANTATION_MODEL_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                TYPE_OF_EARTH_WORK_DONE + " varchar(100) DEFAULT NULL," +
                TYPE_OF_EARTH_WORK_DONE_OTHERDETAILS + " varchar(500) DEFAULT NULL," +
                PITSIZE + " varchar(30) DEFAULT NULL," +
                PIT_ESPACEMENT + " varchar(30) DEFAULT NULL," +

                TRENCH_SIZE + " varchar(30) DEFAULT NULL," +
                TRENCH_ESPACEMENT + " varchar(30) DEFAULT NULL," +

                PIT_IN_PIT_SIZE + " varchar(100) DEFAULT NULL," +
                PIT_IN_PIT_ESPACEMENT + " varchar(100) DEFAULT NULL," +

                RIPPING_SIZE + " varchar(30) DEFAULT NULL," +
                RIPPING_ESPACEMENT + " varchar(30) DEFAULT NULL," +

                OTHERS_SIZE + " varchar(30) DEFAULT NULL," +
                OTHERS_ESPACEMENT + " varchar(30) DEFAULT NULL," +

                AVERAGE_ANNUAL_RAINFALL_MM + " float DEFAULT 0," +
                SOIL_TYPE + " varchar(100) DEFAULT NULL," +
                NO_OF_WATCHERS_PROVIDED + " float DEFAULT 0," +
                NO_OF_YEARS_MAINTAINED + " float DEFAULT 0," +
                SCHEME_PROVISION + " varchar(50) DEFAULT NULL," +
                SCHEME_PROVISION_NO_REASONS + " varchar(100) DEFAULT NULL," +
                IS_CASUALTY_REPLACEMENT_DONE + " varchar(10) DEFAULT NULL," +
                YEAR_OF_CASUALTY_REPLACEMENT + " varchar(10) DEFAULT NULL," +
                REPLACEMENT_PBSIZE + " varchar(30) DEFAULT NULL," +
                NO_OF_REPLACED_SEEDLINGS + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_EARTHWORK + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_EARTHWORK + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_RAISINGSEEDLING + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_RAISINGSEEDLING + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_RAISINGPLANTATION + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_RAISINGPLANTS + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR1 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR1 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR2 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR2 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR3 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR3 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR4 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR4 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR5 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR5 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR6 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR6 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR7 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR7 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_YEAR8 + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_MNTNCE_YEAR8 + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_MNTNCE_TOTAL + " float DEFAULT 0," +
                PLANTING_DENSITY_HA + " INTEGER(11) NOT NULL DEFAULT 0," +
                GPS_LATITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LONGITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LATLONG_COLLECTION + " varchar(5000) DEFAULT NULL," +
                GPS_SAMPLEPLOT_COLLECTION + " varchar(500) DEFAULT NULL," +
                GPS_SAMPLEPLOT_COLLECTION_TWO + " varchar(500) DEFAULT NULL," +
                GPS_SAMPLEPLOT_COLLECTION_THREE + " varchar(500) DEFAULT NULL," +
                GPS_SAMPLEPLOT_COLLECTION_FOUR + " varchar(500) DEFAULT NULL," +
                GPS_SAMPLEPLOT_COLLECTION_FIVE + " varchar(500) DEFAULT NULL," +
                GPS_MEASUREMENT + " varchar(100) DEFAULT NULL," +
                PLANTATION_EVLTN_NATURE_OF_TERRAIN + " varchar(30) DEFAULT NULL," +
                WAS_THE_SITE_PREVIOUSLY_PLANTED + " varchar(10) DEFAULT NULL," +
                YEAR_OF_PREVIOUS_PLANTING + " INTEGER(11) NOT NULL DEFAULT 0," +
                REASON_FOR_REPLANTING + " varchar(500) DEFAULT NULL," +
                WAS_PERMISSION_OBTAINED_FOR_REPLANTING + " varchar(10) DEFAULT NULL," +
                DETAILS_OF_PERMISSION_FOR_REPLANTING + " varchar(500) DEFAULT NULL," +
                APPROXIMATE_SAPLINGS_ALIVE_TODAY + " INTEGER(11) NOT NULL DEFAULT 0," +
                PLANTATION_OPERATIONS_ASPER_PRESCRIPTION + " varchar(10) DEFAULT NULL," +
                PLANTATION_OPERATIONS_ASPER_MODEL + " varchar(50) DEFAULT NULL," +
                PLANTATION_OPERATIONS_REASONS + " varchar(100) DEFAULT NULL," +
                ANY_DAMAGE_TO_PLANTATION_OBSERVED + " varchar(10) DEFAULT NULL," +
                PLANTATION_OPERATIONS_NOT_ASPER_PRESCRIPTION_VARIATION + " varchar(500) DEFAULT NULL," +
                CAUSE_OF_DAMAGE + " varchar(100) DEFAULT NULL," +
                FIRE_AREA + " varchar(100) DEFAULT NULL," +
                FIRE_SEEDLING + " varchar(100) DEFAULT NULL," +
                PEST_AREA + " varchar(100) DEFAULT NULL," +
                PEST_SEEDLING + " varchar(100) DEFAULT NULL," +
                GRAZING_AREA + " varchar(100) DEFAULT NULL," +
                GRAZING_SEEDLING + " varchar(100) DEFAULT NULL," +
                WILDLIFE_AREA + " varchar(100) DEFAULT NULL," +
                WILDLIFE_SEEDLING + " varchar(100) DEFAULT NULL," +
                ENROACHMENT_AREA + " varchar(100) DEFAULT NULL," +
                ENROACHMENT_SEEDLING + " varchar(100) DEFAULT NULL," +
                CAUSE_OF_DAMAGE_OTHERS_AREA + " varchar(100) DEFAULT NULL," +
                CAUSE_OF_DAMAGE_OTHERS_SEEDLING + " varchar(100) DEFAULT NULL," +
                TOTAL_NO_OF_SAMPLE_PLOTS_LAID + " INTEGER(11) NOT NULL DEFAULT 0," +
                AREAEXTENT + " varchar(10) DEFAULT NULL," +
//                DISCREPANCY_AVAILABLE + " varchar(10) DEFAULT NULL," +
                DISCREPANCY + " varchar(100) DEFAULT NULL," +
                DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_WORK_DETAI + " varchar(500) DEFAULT NULL," +
                NO_OF_SEEDLING + " varchar(10) DEFAULT NULL," +
                DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_SEEDLING_DETAI + " varchar(500) DEFAULT NULL," +
                NO_OF_TRENCH + " varchar(10) DEFAULT NULL," +
                DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_TRENCH_DETAI + " varchar(500) DEFAULT NULL," +
                OTHER_DISCREPANCY + " varchar(500) DEFAULT NULL," +
                IS_WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL + " varchar(10) DEFAULT NULL," +
                WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL_DETAILS + " varchar(10) DEFAULT NULL," +
                ANY_SNR_OFFICER_INSPECT_PLANTATION_AND_ENTRIES_IN_JOURNAL + " varchar(10) DEFAULT NULL," +
                FIRST_SNR_OFFICER_DESIGNATION + " varchar(50) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_ACF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_DCF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_CF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_CCF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_APCCF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_PCCF + " varchar(10) DEFAULT NULL," +
                IS_RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE + " varchar(10) DEFAULT NULL," +
                RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE_DETAILS + " varchar(500) DEFAULT NULL," +
                ARE_THE_SCHEME_OBJECTIVES_MET_BY_THE_WORK + " varchar(10) DEFAULT NULL," +
                PLANTATION_OBJECTIVES_ASPER_MODEL + " varchar(30) DEFAULT NULL," +
                MODEL_OBJECTIVES_NOT_MET_BY_THE_WORK_REASONS + " varchar(30) DEFAULT NULL," +
                ANY_SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION + " varchar(10) DEFAULT NULL," +
                SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION_DETAILS + " varchar(500) DEFAULT NULL," +
                DISTANCE_FROM_PLANTATION_BOUNDRY + " float DEFAULT 0," +
                DIRECTION_IN_WHICH_CONTROL_PLOT_LOCATED + " varchar(50) DEFAULT NULL," +
                CLOSED_OR_OPEN_AREA + " varchar(10) DEFAULT NULL," +
                MAIN_SPECIES_PLANTED + " varchar(2000) DEFAULT NULL," +
                DOES_PLANTATION_HAS_MULTIPLE_BLOCK + " varchar(2000) DEFAULT NULL," +
                PLANTATION_TYPE + " varchar(50) DEFAULT NULL," +
                NO_OF_BLOCK + " INTEGER(11) DEFAULT NULL," +
                BLOCK1_AREA + " varchar(50) DEFAULT NULL," +
                BLOCK1_TYPE + " varchar(50) DEFAULT NULL," +
                BLOCK2_AREA + " varchar(50) DEFAULT NULL," +
                BLOCK2_TYPE + " varchar(50) DEFAULT NULL," +
                BLOCK3_AREA + " varchar(50) DEFAULT NULL," +
                BLOCK3_TYPE + " varchar(50) DEFAULT NULL," +
                BLOCK4_AREA + " varchar(50) DEFAULT NULL," +
                BLOCK4_TYPE + " varchar(50) DEFAULT NULL," +
                BLOCK5_AREA + " varchar(50) DEFAULT NULL," +
                BLOCK5_TYPE + " varchar(50) DEFAULT NULL," +
                SAMPLEPLOTS_PHOTOS_COUNT + " INTEGER(11) NOT NULL DEFAULT 0" +
                ")";

    }

    private String createAdvanceWorkTablev2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADVANCEWORK + " (" +
                FORM_ID + " INTEGER(11) DEFAULT 0 UNIQUE," +
                DRAW_MAP_STATUS + " INTEGER(2) DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) DEFAULT 0," +
                SAMPLE_PLOT_STATUS + " INTEGER(2) DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(2) DEFAULT -1," +
                PLANTING_ACTIVITY_STATUS + " INTEGER(2) DEFAULT 0," +
                SMC_WORK_STATUS + " INTEGER(2) DEFAULT 0," +
                VFC_STATUS + " INTEGER(2) DEFAULT 0," +
                BOUNDARY_STATUS + " INTEGER(2) DEFAULT 0," +
                CIRCLE_NAME + " varchar(100) DEFAULT NULL," +
                CIRCLE_ID + " varchar(100) DEFAULT NULL," +
                DIVISION_NAME + " varchar(100) DEFAULT NULL," +
                DIVISION_CODE + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_NAME + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_CODE + " varchar(100) DEFAULT NULL," +
                RANGE_NAME + " varchar(100) DEFAULT NULL," +
                RANGE_CODE + " varchar(100) DEFAULT NULL," +
                DISTRICT_NAME + " varchar(100) DEFAULT NULL," +
                DISTRICT_CODE + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_NAME + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_ID + " varchar(100) DEFAULT NULL," +
                TALUK_NAME + " varchar(100) DEFAULT NULL," +
                TALUK_CODE + " varchar(100) DEFAULT NULL," +
                GRAMA_PANCHAYAT_NAME + " varchar(100) DEFAULT NULL," +
                PANCHAYAT_CODE + " varchar(100) DEFAULT NULL," +
                VILLAGE_NAME + " varchar(100) DEFAULT NULL," +
                VILLAGE_CODE + " varchar(100) DEFAULT NULL," +
                // LOCALITY_NAME + " varchar(100) DEFAULT NULL," +
                LEGAL_STATUS_OF_LAND + " varchar(50) DEFAULT NULL," +
                LEGAL_STATUS_OF_LAND_OTHER_DETAILS + " varchar(50) DEFAULT NULL," +
                RF_NAME + " varchar(100) DEFAULT NULL," +
                PLANTATION_NAME + " varchar(100) DEFAULT NULL," +
                WORK_CODE + " varchar(500) DEFAULT NULL," +
                SITE_QUALITY + " varchar(100) DEFAULT NULL," +
                APO + " varchar(10) DEFAULT NULL," +
                APO_DATE + " varchar(30) DEFAULT NULL," +
                WORK_ESTIMATES + " varchar(10) DEFAULT NULL," +
                NO_OF_WORK_ESTIMATES + " INTEGER(11) NOT NULL DEFAULT 0," +
                FNB + " varchar(10) DEFAULT NULL," +
                PLANTATION_JOURNAL + " varchar(10) DEFAULT NULL," +
                YEAR_OF_EARTHWORK + " varchar(10) DEFAULT NULL," +
                GROSS_PLANTATION_AREA_HA + " float DEFAULT 0," +
                NET_PLANTATION_AREA_HA + " float DEFAULT 0," +
                SCHEME_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SCHEME_NAME + " varchar(100) DEFAULT NULL," +
                PLANTATION_MODEL + " varchar(100) DEFAULT NULL," +
                PLANTATION_MODEL_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                TYPE_OF_EARTH_WORK_DONE + " varchar(100) DEFAULT NULL," +
                TYPE_OF_EARTH_WORK_DONE_OTHERDETAILS + " varchar(500) DEFAULT NULL," +
                PITSIZE + " varchar(30) DEFAULT NULL," +
                PIT_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_PITS + " varchar(30) DEFAULT NULL," +
                TRENCH_SIZE + " varchar(30) DEFAULT NULL," +
                TRENCH_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_TRENCHS + " varchar(30) DEFAULT NULL," +
                PIT_IN_PIT_SIZE + " varchar(100) DEFAULT NULL," +
                PIT_IN_PIT_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_PIT_IN_PIT + " varchar(30) DEFAULT NULL," +
                RIPPING_SIZE + " varchar(30) DEFAULT NULL," +
                RIPPING_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_RIPLINE + " varchar(30) DEFAULT NULL," +
                OTHERS_SIZE + " varchar(30) DEFAULT NULL," +
                OTHERS_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                OTHERS_NO_OF_UNITS + " varchar(30) DEFAULT NULL," +
                REASON_FOR_PLANTING + " varchar(30) DEFAULT NULL," +
                REASON_FOR_PLANTING_OTHERS + " varchar(30) DEFAULT NULL," +
                AVERAGE_ANNUAL_RAINFALL_MM + " float DEFAULT 0," +
                SOIL_TYPE + " varchar(100) DEFAULT NULL," +
                PLANTATION_TOTEXP_EARTHWORK + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_EARTHWORK + " varchar(30) DEFAULT NULL," +
                PLANTATION_TOTEXP_RAISINGSEEDLING + " float DEFAULT 0," +
                PLANTATION_SANCTN_DATE_FOR_RAISINGSEEDLING + " varchar(30) DEFAULT NULL," +
                GPS_LATITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LONGITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LATLONG_COLLECTION + " varchar(5000) DEFAULT NULL," +
                GPS_SAMPLEPLOT_COLLECTION + " varchar(500) DEFAULT NULL," +
                GPS_MEASUREMENT + " varchar(100) DEFAULT NULL," +
                PLANTATION_EVLTN_NATURE_OF_TERRAIN + " varchar(30) DEFAULT NULL," +
                WAS_THE_SITE_PREVIOUSLY_PLANTED + " varchar(10) DEFAULT NULL," +
                YEAR_OF_PREVIOUS_PLANTING + " INTEGER(11) NOT NULL DEFAULT 0," +
                REASON_FOR_REPLANTING + " varchar(500) DEFAULT NULL," +
                WAS_PERMISSION_OBTAINED_FOR_REPLANTING + " varchar(10) DEFAULT NULL," +
                DETAILS_OF_PERMISSION_FOR_REPLANTING + " varchar(500) DEFAULT NULL," +
                APPROXIMATE_SAPLINGS_ALIVE_TODAY + " INTEGER(11) NOT NULL DEFAULT 0," +
                PLANTATION_OPERATIONS_ASPER_PRESCRIPTION + " varchar(10) DEFAULT NULL," +
                PLANTATION_OPERATIONS_ASPER_MODEL + " varchar(50) DEFAULT NULL," +
                PLANTATION_OPERATIONS_REASONS + " varchar(100) DEFAULT NULL," +
                ANY_DAMAGE_TO_PLANTATION_OBSERVED + " varchar(10) DEFAULT NULL," +
                PLANTATION_OPERATIONS_NOT_ASPER_PRESCRIPTION_VARIATION + " varchar(500) DEFAULT NULL," +
                CAUSE_OF_DAMAGE + " varchar(100) DEFAULT NULL," +
                FIRE_AREA + " varchar(100) DEFAULT NULL," +
                FIRE_SEEDLING + " varchar(100) DEFAULT NULL," +
                PEST_AREA + " varchar(100) DEFAULT NULL," +
                PEST_SEEDLING + " varchar(100) DEFAULT NULL," +
                GRAZING_AREA + " varchar(100) DEFAULT NULL," +
                GRAZING_SEEDLING + " varchar(100) DEFAULT NULL," +
                WILDLIFE_AREA + " varchar(100) DEFAULT NULL," +
                WILDLIFE_SEEDLING + " varchar(100) DEFAULT NULL," +
                ENROACHMENT_AREA + " varchar(100) DEFAULT NULL," +
                ENROACHMENT_SEEDLING + " varchar(100) DEFAULT NULL," +
                TOTAL_NO_OF_SAMPLE_PLOTS_LAID + " INTEGER(11) NOT NULL DEFAULT 0," +
                AREAEXTENT + " varchar(10) DEFAULT NULL," +
//                DISCREPANCY_AVAILABLE + " varchar(10) DEFAULT NULL," +
//                DISCREPANCY + " varchar(100) DEFAULT NULL," +
         /*       DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_WORK_DETAI + " varchar(500) DEFAULT NULL," +
                NO_OF_SEEDLING + " varchar(10) DEFAULT NULL," +
                DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_SEEDLING_DETAI + " varchar(500) DEFAULT NULL," +
                NO_OF_TRENCH + " varchar(10) DEFAULT NULL," +
                DSCRPNCY_BTWN_RECORDED_AND_OBSERVED_QTY_OF_PLANTATION_TRENCH_DETAI + " varchar(500) DEFAULT NULL," +*/
                IS_WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL + " varchar(10) DEFAULT NULL," +
                WORK_DONE_DOCUMENTATED_PROPERLY_IN_PLANTATION_JOURNAL_DETAILS + " varchar(10) DEFAULT NULL," +
                ANY_SNR_OFFICER_INSPECT_PLANTATION_AND_ENTRIES_IN_JOURNAL + " varchar(10) DEFAULT NULL," +
                FIRST_SNR_OFFICER_DESIGNATION + " varchar(50) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_ACF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_DCF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_CF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_CCF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_APCCF + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_INSPECTION_PCCF + " varchar(10) DEFAULT NULL," +
                IS_CPT_PRESENT + " varchar(10) DEFAULT NULL," +
                PLANTING_ON_CPT + " varchar(10) DEFAULT NULL," +
                WORKING_PLAN_MANAGEMENT_PLAN_PRESCRIPTIONS + " varchar(10) DEFAULT NULL," +
                WORKING_CIRCLE_PARAGRAPH_NO + " varchar(100) DEFAULT NULL," +
                WHY_WORK_WAS_TAKEN_UP + " varchar(10) DEFAULT NULL," +
                IS_RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE + " varchar(10) DEFAULT NULL," +
                RESULTS_OF_PLANTATION_WORK_UNIFORM_ACROSS_SITE_DETAILS + " varchar(500) DEFAULT NULL," +
                ARE_THE_SCHEME_OBJECTIVES_MET_BY_THE_WORK + " varchar(10) DEFAULT NULL," +
                PLANTATION_OBJECTIVES_ASPER_MODEL + " varchar(30) DEFAULT NULL," +
                MODEL_OBJECTIVES_NOT_MET_BY_THE_WORK_REASONS + " varchar(30) DEFAULT NULL," +
                ANY_SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION + " varchar(10) DEFAULT NULL," +
                SCOPE_FOR_IMPROVING_THE_PERFORMANCE_OF_THE_PLANTATION_DETAILS + " varchar(500) DEFAULT NULL," +
                ANY_BURNING_DONE_ON_THE_SITE + " varchar(10) DEFAULT NULL," +
                ANY_BURNING_DONE_ON_THE_SITE_YES_DETAILS + " varchar(100) DEFAULT NULL," +
                ANY_DAMAGE_DONE_TO_THE_STANDING_TREES + " varchar(10) DEFAULT NULL," +
                ANY_DAMAGE_DONE_TO_THE_STANDING_TREES_YES_DETAILS + " varchar(100) DEFAULT NULL," +
                ANY_DAMAGE_DONE_TO_THE_SHRUB_GROWTH_AND_ROOT_STOCK + " varchar(10) DEFAULT NULL," +
                ANY_DAMAGE_DONE_TO_THE_SHRUB_GROWTH_AND_ROOT_STOCK_YES_DETAILS + " varchar(100) DEFAULT NULL," +
                QUALITY_OF_EARTH_WORK_DONE + " varchar(50) DEFAULT NULL," +
             /*   DISTANCE_FROM_PLANTATION_BOUNDRY + " float DEFAULT 0," +
                DIRECTION_IN_WHICH_CONTROL_PLOT_LOCATED + " varchar(50) DEFAULT NULL," +
                CLOSED_OR_OPEN_AREA + " varchar(10) DEFAULT NULL," +*/
                MAIN_SPECIES_PLANTED + " varchar(2000) DEFAULT NULL," +
                SAMPLEPLOTS_PHOTOS_COUNT + " INTEGER(11) NOT NULL DEFAULT 0" +
                ")";
    }

    private String createNurseryTablev2() {

        return "CREATE TABLE IF NOT EXISTS " + KFD_NURSERY_WORKS + " (" +
                FORM_ID + "  INTEGER(11) DEFAULT NULL," +
                WORK_CODE + " varchar(500) DEFAULT NULL," +
                FINISHED_POSITION + " INTEGER(2) DEFAULT -1," +
                CIRCLE_NAME + " varchar(100) DEFAULT NULL," +
                CIRCLE_ID + " varchar(50) DEFAULT NULL," +
                DIVISION_NAME + " varchar(100) DEFAULT NULL," +
                DIVISION_CODE + " varchar(50) DEFAULT NULL," +
                SUBDIVISION_NAME + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_CODE + " varchar(50) DEFAULT NULL," +
                RANGE_NAME + " varchar(100) DEFAULT NULL," +
                RANGE_CODE + " varchar(50) DEFAULT NULL," +
                CONSTITUENCY_NAME + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_ID + " varchar(50) DEFAULT NULL," +
                DISTRICT_NAME + " varchar(100) DEFAULT NULL," +
                DISTRICT_CODE + " varchar(50) DEFAULT NULL," +
                TALUK_NAME + " varchar(100) DEFAULT NULL," +
                TALUK_CODE + " varchar(50) DEFAULT NULL," +
                GRAMA_PANCHAYAT_NAME + " varchar(100) DEFAULT NULL," +
                PANCHAYAT_CODE + " varchar(50) DEFAULT NULL," +
                VILLAGE_NAME + " varchar(100) DEFAULT NULL," +
                VILLAGE_CODE + " varchar(50) DEFAULT NULL," +
                FOREST_NAME + " varchar(100) DEFAULT NULL," +
                NURSERY_NAME + " varchar(150) DEFAULT NULL," +
                NURSERY_LATITUDE + " varchar(30) DEFAULT NULL," +
                NURSERY_LONGITUDE + " varchar(30) DEFAULT NULL," +
                NURSERY_INCHARGE_NAME + " varchar(100) DEFAULT NULL," +
                NURSERY_INCHARGE_DESIGNATION + " varchar(100) DEFAULT NULL," +
                NURSERY_INCHARGE_YEARSOFEXP + " int(3) NOT NULL DEFAULT 0," +
                TOTAL_AREA_HACTARE + " int(11) NOT NULL DEFAULT 0," +
                IS_NURSERY_FENCED_AND_GATED + " varchar(10) DEFAULT NULL," +
                SOURCE_OF_WATER_SUPPLY + " varchar(100) DEFAULT NULL," +
                SOURCE_OF_WATER_SUPPLY_OTHERS + " varchar(100) DEFAULT NULL," +
                AVAILABILITY_OF_NURSERY_FACILITIES + " varchar(200) DEFAULT NULL," +
                OTHERS + " varchar(100) DEFAULT NULL," +
                LABOUR_ATTENDANCE_REGISTER + " varchar(10) DEFAULT NULL," +
                STORES_REGISTER + " varchar(10) DEFAULT NULL," +
                SEEDLING_STOCK_REGISTER + " varchar(10) DEFAULT NULL," +
                SEEDLING_DISTRIBUTION + " varchar(10) DEFAULT NULL," +
                BUDGET_HEADWISE_NURSERY_REGISTER + " varchar(10) DEFAULT NULL," +

                RECORDS_MAINTAINED + " varchar(100) DEFAULT NULL," +
                SEEDLINGS_ARRANGED_SPECIESWISE + " varchar(10) DEFAULT NULL," +
                SEEDLINGS_ARRANGED_SPECIESWISE_REASONS + " varchar(100) DEFAULT NULL," +
                SEEDLINGS_ARRANGED_SCHEMEWISE + " varchar(10) DEFAULT NULL," +
                SEEDLINGS_ARRANGED_SCHEMEWISE_REASONS + " varchar(100) DEFAULT NULL," +
                CONTAINERS_INTACT_AND_IN_GOOD_CONDITION + " varchar(10) DEFAULT NULL," +
                CONTAINERS_INTACT_AND_IN_GOOD_CONDITION_REASONS + " varchar(100) DEFAULT NULL," +
                IRRIGATION_ADEQUATE_TO_REACH_THE_BOTTOM_CONTAINER + " varchar(10) DEFAULT NULL," +
                IRRIGATION_ADEQUATE_TO_REACH_THE_BOTTOM_CONTAINER_REASONS + " varchar(100) DEFAULT NULL," +
                SEEDLINGS_FREE_FROM_WEEDS + " varchar(10) DEFAULT NULL," +
                SEEDLINGS_FREE_FROM_WEEDS_REASONS + " varchar(100) DEFAULT NULL," +
                SEEDLINGS_RAISED_IN_TIME + " varchar(10) DEFAULT NULL," +
                SEEDLINGS_RAISED_IN_TIME_REASONS + " varchar(100) DEFAULT NULL," +
                SIDE_BUDS_BEING_NIPPED_IN_TIME + " varchar(10) DEFAULT NULL," +
                SIDE_BUDS_BEING_NIPPED_IN_TIME_REASONS + " varchar(100) DEFAULT NULL," +
                SEEDLINGS_SHIFTED_AND_GRADED_AS_PER_THE_PACKAGE_OF_PRACTICES + " varchar(10) DEFAULT NULL," +
                SEEDLINGS_STRIKING_ROOTS_INTO_THE_SOIL + " varchar(10) DEFAULT NULL," +
                STAKES_PROVIDED_TO_SEEDLINGS_IN_10X16 + " varchar(10) DEFAULT NULL," +
                STAKES_PROVIDED_TO_SEEDLINGS_IN_10X16_REASONS + " varchar(100) DEFAULT NULL," +
                SEEDLING_QUALITY_UNIFORM_IN_THE_NURSERY + " varchar(10) DEFAULT NULL," +
                SEEDLING_QUALITY_UNIFORM_IN_THE_NURSERY_REASONS + " varchar(100) DEFAULT NULL," +
                SEEDLINGS_FREE_FROM_PESTS_AND_DISEASES + " varchar(10) DEFAULT NULL," +
                SEEDLINGS_FREE_FROM_PESTS_AND_DISEASES_DETAILS + " varchar(100) DEFAULT NULL," +
                IS_SEED_BED_PRESENT + " varchar(10) DEFAULT NULL," +
                ANY_OTHER_REMARKS_ON_NURSERY + " varchar(100) DEFAULT NULL," +

                QUALITY_OF_SEEDBEDS + " varchar(100) DEFAULT NULL," +
                SEEDBEDS_RAISED_IN_CURRENT_YEAR + " int(11)  NULL DEFAULT 0," +

                NURSERY_KEPT_CLEAR_OF_WEEDS_WASTE_COMPOSTED + " varchar(10) DEFAULT NULL," +
                WATER_USAGE_OPTIMISED_LEAKAGES_PREVENTED + " varchar(10) DEFAULT NULL," +
                LEFT_OVER_SEEDLINGS_PROPERLY_DISPOSED + " varchar(10) DEFAULT NULL," +
                SEEDLING_BEDS_PROVIDED_WITH_DISPLAY_BOARDS + " varchar(10) DEFAULT NULL," +
                SEEDBEDS_USED_ANNUALLY + " varchar(10) DEFAULT NULL," +
                OTHER_REMARKS_AND_OBSERVATIONS + " varchar(100) DEFAULT NULL," +

                PHOTOS_COUNT + " int(11) NOT NULL DEFAULT 0" + ")";
    }


    private String createTableSamplePlotInventoryV2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SAMPLE_PLOT_INVENTORY + " (" +
                INVENTORY_ID + " INTEGER   PRIMARY KEY ," +
                SAMPLE_PLOT_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                KNOWN_FAILURES + " INTEGER(11) NOT NULL DEFAULT 0," +
                CALCULATED_FAILURES + " INTEGER(11) NOT NULL DEFAULT 0," +
                PART_TYPE + " varchar(60) DEFAULT NULL," +
                SPECIES_AVAILABILITY + " varchar(60) DEFAULT NULL," +
                SPECIES_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SPECIES_NAME + " varchar(200) DEFAULT NULL," +
                NUMBER_OF_SEEDINGS + " INTEGER(11) NOT NULL DEFAULT 0," +
                AVERAGE_GBH_METERS + " float NOT NULL DEFAULT 0," +
                AVERAGE_HEIGHT_METERS + " float NOT NULL DEFAULT 0," +
                TOTAL_COUNT + " INTEGER(11) NOT NULL DEFAULT 0," +
                TOTAL_COUNT_SURVIVED + " INTEGER(11) NOT NULL DEFAULT 0," +
                SEEDLING_PERCENTAGE + " float NOT NULL DEFAULT 0," +
                DIBBLED_PERCENTAGE + " float NOT  NULL DEFAULT 0," +
                STATE_OF_HEALTH + " varchar(50) DEFAULT NULL," +
                AVERAGE_COLLAR_GIRTH + " float NOT NULL DEFAULT 0," +
                NUMBER_TENDED + " INTEGER(11) NOT NULL DEFAULT 0, " +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0 " +
                ")";
    }

    private String createTableAddSpecies() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADD_SPECIES + " (" +
                INVENTORY_ID + " INTEGER   PRIMARY KEY ," +
                SAMPLE_PLOT_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                //PART_TYPE + " varchar(60) DEFAULT NULL," +
                SPECIES_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                MAIN_SPECIES_PLANTED + " varchar(200) DEFAULT NULL," +
                SPECIES_SIZE + " varchar(50) DEFAULT NULL," +
                OTHER_SPECIES + " varchar(200) DEFAULT NULL," +
                TOTAL_SPECIES_COUNT + " INTEGER(11) NOT NULL DEFAULT 0," +
                PLANTING_DENSITY_HA + " INTEGER(11) NOT NULL DEFAULT 0," +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0 " +
                ")";
    }

    private String createSamplePlotSpecies() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_SAMPLEPLOT_SPECIES + " (" +
                INVENTORY_ID + " INTEGER   PRIMARY KEY ," +
                SAMPLE_PLOT_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                PART_TYPE + " varchar(60) DEFAULT NULL," +
                SPECIES_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                MAIN_SPECIES_PLANTED + " varchar(200) DEFAULT NULL," +
                SPECIES_SIZE + " varchar(50) DEFAULT NULL," +
                OTHER_SPECIES + " varchar(2000) DEFAULT NULL," +
                TOTAL_SPECIES_COUNT + " INTEGER(11) NOT NULL DEFAULT 0," +
                PLANTING_DENSITY_HA + " INTEGER(11) NOT NULL DEFAULT 0," +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0 " +
                ")";
    }

    private String createTableProtection() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_PROTECTION + " (" +
                PROTECTION_ID + " INTEGER  PRIMARY KEY ," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                PROTECTION_MEASURE_AVAILABILITY + " varchar(30) DEFAULT NULL," +
                TYPE_OF_PROTECTION + " varchar(50) DEFAULT NULL," +
                OTHER_TYPE_OF_PROTECTION + " varchar(100) DEFAULT NULL," +
                TOTAL_LENGTH_KMS + " float NOT NULL DEFAULT 0," +
                BOUNDARY_COST + " float NOT NULL DEFAULT 0," +
                TOTAL_HEIGHT + " float NOT NULL DEFAULT 0," +
                BREADTH_OF_CPT + " float NOT NULL DEFAULT 0," +
                DEPTH_OF_CPT + " float NOT NULL DEFAULT 0," +
                CPT_SIZE + " varchar(50) DEFAULT NULL," +
                NO_OF_STRANDS + " float NOT NULL DEFAULT 0," +
                PRESENT_CONDITION + " varchar(50) DEFAULT NULL," +
                BRUSHWOOD_MATERIALS_USED + " varchar(100) DEFAULT NULL," +
                MOUNT_SOWING + " varchar(10) DEFAULT NULL," +
                MOUNT_SOWING_RESULT + " varchar(50) DEFAULT NULL," +
                MATERIALS_USED + " varchar(50) DEFAULT NULL," +
               /* DIFFERENCE_R_E + " varchar(10) DEFAULT NULL," +
                DETAILS_R_E + " varchar(100) DEFAULT NULL," +*/
                EFFECTIVENESS_OF_CLM + " varchar(10) DEFAULT NULL," +
                EFFECTIVENESS_OF_CLM_REASONS + " varchar(100) DEFAULT NULL," +
                OTHER_TREE_GUARDS + " varchar(50) DEFAULT NULL," +
                TREE_CONDITION + " varchar(30) DEFAULT NULL," +
                BARBED_WIRE_CONDITION + " varchar(30) DEFAULT NULL," +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(3) DEFAULT -1" +
                ")";
    }

    private String createTableAdvProtection() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADV_PROTECTION + " (" +
                PROTECTION_ID + " INTEGER  PRIMARY KEY ," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                PROTECTION_MEASURE_AVAILABILITY + " varchar(30) DEFAULT NULL," +
                TYPE_OF_PROTECTION + " varchar(50) DEFAULT NULL," +
                OTHER_TYPE_OF_PROTECTION + " varchar(100) DEFAULT NULL," +
                TOTAL_LENGTH_KMS + " float NOT NULL DEFAULT 0," +
                BOUNDARY_COST + " float NOT NULL DEFAULT 0," +
                BREADTH_OF_CPT + " float NOT NULL DEFAULT 0," +
                TOTAL_HEIGHT + " float NOT NULL DEFAULT 0," +
                DEPTH_OF_CPT + " float NOT NULL DEFAULT 0," +
                CPT_SIZE + " varchar(50) DEFAULT NULL," +
                NO_OF_STRANDS + " float NOT NULL DEFAULT 0," +
                PRESENT_CONDITION + " varchar(50) DEFAULT NULL," +
                BRUSHWOOD_MATERIALS_USED + " varchar(100) DEFAULT NULL," +
                MOUNT_SOWING + " varchar(10) DEFAULT NULL," +
                MOUNT_SOWING_RESULT + " varchar(50) DEFAULT NULL," +
                MATERIALS_USED + " varchar(50) DEFAULT NULL," +
               /* DIFFERENCE_R_E + " varchar(10) DEFAULT NULL," +
                DETAILS_R_E + " varchar(100) DEFAULT NULL," +*/
                EFFECTIVENESS_OF_CLM + " varchar(50) DEFAULT NULL," +
                EFFECTIVENESS_OF_CLM_REASONS + " varchar(50) DEFAULT NULL," +
                OTHER_TREE_GUARDS + " varchar(50) DEFAULT NULL," +
                TREE_CONDITION + " varchar(30) DEFAULT NULL," +
                BARBED_WIRE_CONDITION + " varchar(30) DEFAULT NULL," +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(3) DEFAULT -1" +
                ")";
    }

    private String createNurseryBaggedSeedlingTable() {

        return "CREATE TABLE IF NOT EXISTS " + KFD_NURSERY_WORKS_BAGGED_SEEDLINGS + " (" +
                BAGGED_SEEDLING_ID + " INTEGER   PRIMARY KEY ," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                SCHEME_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                NAME_OF_THE_SCHEME + " varchar(2000) DEFAULT NULL," +
                SPECIES_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                MAIN_SPECIES_PLANTED + " varchar(500) DEFAULT NULL," +
                OTHER_SPECIES + " varchar(200) DEFAULT NULL," +
                PB_SIZE + " varchar(50) DEFAULT NULL," +
                NUMBER_AS_PER_RECORDS + " INTEGER(11) NOT NULL DEFAULT 0," +
                NUMBER_ACTUALLY_FOUND + " INTEGER(11) DEFAULT 0," +
                DIFFERENCE + " INTEGER(11) DEFAULT 0," +
                AVERAGE_SEEDLING_HEIGHT_METER + " float NOT NULL DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(3) DEFAULT -1," +
                REMARKS + " varchar(200) DEFAULT NULL " +
                ")";
    }

    private String createNurserySeedBedTable() {

        return "CREATE TABLE IF NOT EXISTS " + KFD_NURSERY_WORKS_SEED_BED + " (" +
                SEED_BED_ID + " INTEGER   PRIMARY KEY ," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 1," +
                NAME_OF_THE_SCHEME + " varchar(2000) DEFAULT NULL," +
                NUMBER_OF_BEDS + " INTEGER(11) NOT NULL DEFAULT 0," +
                SPECIES_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SPECIES_RAISED + " varchar(2000) DEFAULT NULL," +
                TYPE_OF_BEDS + " varchar(2000) DEFAULT NULL," +
                SAPLINGS_PER_BED + " INTEGER(11) NOT NULL DEFAULT 0," +
                BED_SIZE + " varchar(10) DEFAULT NULL," +
                AVERAGE_HEIGHT_OF_THE_SPROUTS + " float NOT NULL DEFAULT 0," +
                REMARKS + " varchar(200) DEFAULT NULL " +
                ")";
    }

    private String createTableAddBeneficiarySpecies() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADD_BENEFICIARY_SPECIES + " (" +
                INVENTORY_ID + " INTEGER   PRIMARY KEY ," +
                SAMPLE_PLOT_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                MAIN_SPECIES_PLANTED + " varchar(2000) DEFAULT NULL," +
                SPECIES_SIZE + " varchar(50) DEFAULT NULL," +
                OTHER_SPECIES + " varchar(2000) DEFAULT NULL," +
                TOTAL_SPECIES_COUNT + " INTEGER(11) NOT NULL DEFAULT 0," +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0 " +
                ")";


    }

    private String createTableVfcSamplingV2() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_VFC_SAMPLING + " (" +
                VFC_ID + " INTEGER PRIMARY KEY ," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                VFC_APPLICABLE + " varchar(10) DEFAULT NULL," +
                NAME_OF_THE_VFC + " varchar(50) DEFAULT NULL," +
                IS_JFPM_RAISED + " varchar(10) DEFAULT NULL," +
                IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION + " varchar(10) DEFAULT NULL," +
                IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION_REASONS + " varchar(50) DEFAULT NULL," +
                IS_VFC_INVOLVED_IN_PLANTATION_ACTIVITY + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_LOGGING + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_ADVANCED_WORK_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_PLANTING_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_MAINTENANCE_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_POST_MAINTENANCE_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_STAGE_OTHER + " varchar(50) DEFAULT NULL," +
                VFC_APPROVED_PLANTING_WORK_PROPOSAL + " varchar(10) DEFAULT NULL," +
                VFC_PROVIDED_LABOUR_FOR_PLANTATION_WORK_PAYMENT + " varchar(10) DEFAULT NULL," +
                VFC_MEMBERS_SUPERVISED_PLANTATION_WORK + " varchar(10) DEFAULT NULL," +
                VFC_CONTRIBUTED_VFD_FUND_FOR_PLANTING_WORK + " varchar(10) DEFAULT NULL," +
                VFC_CONTRIBUTED_VFD_FUND_TOTAL + " float NOT NULL DEFAULT 0," +
                DEPT_PROVIDED_FUNDS_VFC_RAISED_PLANTATION + " varchar(10) DEFAULT NULL," +
                VFC_CARRIED_OUT_COMPLIMENTARY_WORKS_LIKE_SMC + " varchar(10) DEFAULT NULL," +
                POST_MAINTENANCE_DONE_BYVFC_WITHT_HEIR_OWN_FUNDS + " varchar(10) DEFAULT NULL," +
                VFC_ANY_OTHER_SPECIFY + " varchar(100) DEFAULT NULL)";
    }

    private String createTableAdvVfc() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADV_VFC_SAMPLING + " (" +
                VFC_ID + " INTEGER PRIMARY KEY ," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                VFC_APPLICABLE + " varchar(10) DEFAULT NULL," +
                NAME_OF_THE_VFC + " varchar(50) DEFAULT NULL," +
                IS_JFPM_RAISED + " varchar(10) DEFAULT NULL," +
                IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION + " varchar(10) DEFAULT NULL," +
                IS_PLANTING_IN_ACCORDANCE_WITH_MICRO_PLAN_PRESCRIPTION_REASONS + " varchar(50) DEFAULT NULL," +
                IS_VFC_INVOLVED_IN_PLANTATION_ACTIVITY + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_LOGGING + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_ADVANCED_WORK_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_PLANTING_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_MAINTENANCE_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_POST_MAINTENANCE_STAGE + " varchar(10) DEFAULT NULL," +
                VFC_INVOLVED_STAGE_OTHER + " varchar(50) DEFAULT NULL," +
                VFC_APPROVED_PLANTING_WORK_PROPOSAL + " varchar(10) DEFAULT NULL," +
                VFC_PROVIDED_LABOUR_FOR_PLANTATION_WORK_PAYMENT + " varchar(10) DEFAULT NULL," +
                VFC_MEMBERS_SUPERVISED_PLANTATION_WORK + " varchar(10) DEFAULT NULL," +
                VFC_CONTRIBUTED_VFD_FUND_FOR_PLANTING_WORK + " varchar(10) DEFAULT NULL," +
                VFC_CONTRIBUTED_VFD_FUND_TOTAL + " float NOT NULL DEFAULT 0," +
                DEPT_PROVIDED_FUNDS_VFC_RAISED_PLANTATION + " varchar(10) DEFAULT NULL," +
                VFC_CARRIED_OUT_COMPLIMENTARY_WORKS_LIKE_SMC + " varchar(10) DEFAULT NULL," +
                POST_MAINTENANCE_DONE_BYVFC_WITHT_HEIR_OWN_FUNDS + " varchar(10) DEFAULT NULL," +
                VFC_ANY_OTHER_SPECIFY + " varchar(100) DEFAULT NULL)";
    }


    private String createTableKfdPlantationSamplingSmcDetailsHighest() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST + "(" +
                SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
//                WORK_CODE + " VARCHAR(30) DEFAULT NULL," +
                TYPE_OF_STRUCTURE + " varchar(30) DEFAULT NULL," +
                SMC_STRUCTURE_COST + " float NOT NULL DEFAULT 0," +
                GPS_LONGITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LATITUDE + " varchar(30) DEFAULT NULL," +
                GPS_ALTITUDE + " varchar(30) DEFAULT NULL," +
                GPS_COORDINATE_CREATION_TIMESTAMP + " varchar(30) DEFAULT NULL," +
                SMC_STRUCTURE_LENGTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_BREADTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_DEPTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_TOTALVOLUME + " float NOT NULL DEFAULT 0," +
                WORK_DIFFERENCE + " varchar(30) DEFAULT NULL," +
                DETAILS_OF_DIFF_BTWN_BILLED_AND_ACUTAL_WORK + " varchar(30) DEFAULT NULL," +
                IS_LOCATION_APPROPRIATE + " varchar(10) DEFAULT NULL," +
                IS_LOCATION_APPROPRIATE_REASON + " varchar(10) DEFAULT NULL," +
                CONSTRUCTION_QUALITY + " varchar(30) DEFAULT NULL," +
                REMARK + " varchar(30) DEFAULT NULL," +
                IS_SMC_SERVING_THE_PURPOSE + " varchar(10) DEFAULT NULL," +
                IS_SMC_SERVING_THE_PURPOSE_NO + " varchar(100) DEFAULT NULL," +
                BREACHED_SMC + " varchar(10) DEFAULT NULL," +
                BREACHED_SMC_YES + " varchar(100) DEFAULT NULL," +
               /* BUILT_WITHIN_BOUNDARY + " varchar(10) DEFAULT NULL," +
                BUILT_WITHIN_BOUNDARY_NO + " varchar(100) DEFAULT NULL," +*/
                IS_SELECT_PROPER + " varchar(10) DEFAULT NULL," +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0)";

    }

    private String createTableAdvSmcHighest() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADV_SMC_HIGHEST + "(" +
                SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
//                WORK_CODE + " VARCHAR(30) DEFAULT NULL," +
                TYPE_OF_STRUCTURE + " varchar(30) DEFAULT NULL," +
                SMC_STRUCTURE_COST + " float NOT NULL DEFAULT 0," +
                GPS_LONGITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LATITUDE + " varchar(30) DEFAULT NULL," +
                GPS_ALTITUDE + " varchar(30) DEFAULT NULL," +
                GPS_COORDINATE_CREATION_TIMESTAMP + " varchar(30) DEFAULT NULL," +
                SMC_STRUCTURE_LENGTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_BREADTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_DEPTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_TOTALVOLUME + " float NOT NULL DEFAULT 0," +
                WORK_DIFFERENCE + " varchar(30) DEFAULT NULL," +
                DETAILS_OF_DIFF_BTWN_BILLED_AND_ACUTAL_WORK + " varchar(30) DEFAULT NULL," +
                IS_LOCATION_APPROPRIATE + " varchar(10) DEFAULT NULL," +
                IS_LOCATION_APPROPRIATE_REASON + " varchar(10) DEFAULT NULL," +
                CONSTRUCTION_QUALITY + " varchar(30) DEFAULT NULL," +
                REMARK + " varchar(30) DEFAULT NULL," +
                IS_SMC_SERVING_THE_PURPOSE + " varchar(10) DEFAULT NULL," +
                IS_SMC_SERVING_THE_PURPOSE_NO + " varchar(100) DEFAULT NULL," +
                BREACHED_SMC + " varchar(10) DEFAULT NULL," +
                BREACHED_SMC_YES + " varchar(100) DEFAULT NULL," +
                /*BUILT_WITHIN_BOUNDARY + " varchar(10) DEFAULT NULL," +
                BUILT_WITHIN_BOUNDARY_NO + " varchar(100) DEFAULT NULL," +*/
                IS_SELECT_PROPER + " varchar(10) DEFAULT NULL," +
                CREATION_TIMESTAMP + " INTEGER(11) NOT NULL DEFAULT 0)";

    }


    private String createKfdSurveyMasterTableV2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SURVEY_MASTER + " ( " +
                SURVEY_ID + " INTEGER   PRIMARY KEY ," +
                WORK_CODE + " varchar(500) NOT NULL," +
                SERVER_FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_TYPE + " varchar(20) NOT NULL," +
                STARTING_TIMESTAMP + " INTEGER(11) DEFAULT 0," +
                ENDING_TIMESTAMP + " INTEGER(11) DEFAULT 0," +
                SUBMISSION_TIMESTAMP + " INTEGER(11) DEFAULT 0," +
                AUTOMATIC_LONGITUDE + " varchar(30) DEFAULT NULL," +
                AUTOMATIC_LATITUDE + " varchar(30) DEFAULT NULL," +
                SURVEYOR_NAME + "  varchar(100) DEFAULT NULL," +
                USER_LEVEL + " varchar(30) DEFAULT NULL," +
                OFFICEID + " varchar(30) DEFAULT 0," +
                EVALUATION_YEAR + " varchar(30) DEFAULT NULL," +
                EVALUATION_TITLE + " varchar(500) DEFAULT NULL," +
                APP_ID + " INTEGER(11) DEFAULT 0," +
                FORM_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                PHOTO_STATUS + " INTEGER(2) NOT NULL DEFAULT 0)";


    }

    private String createSMCMaster() {

        return " CREATE TABLE IF NOT EXISTS " + TABLE_SMC_SAMPLING_MASTER + "  (" +
                SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SMC_STATUS + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                SMC_APPLICABLE + " varchar(10) DEFAULT NULL," +
                TOTAL_BUDGET_FOR_SMC_WORK + " float NOT NULL DEFAULT 0," +
                SMC_WORK_EXPENDITURE_AS_PER_NORM + " varchar(10) DEFAULT NULL," +
                ALL_SMC_STRUCTURES_BUILT_IN_FARM + " varchar(10) DEFAULT NULL," +
                WHERE_IT_WAS_DONE + " varchar(30) DEFAULT NULL," +
                SMC_PLANTATION_AREA_TREATED_COMPLETELY + " varchar(10) DEFAULT NULL," +
                SMC_TREATMENT_FOLLOWED_WATERSHED_PATTERN + " varchar(10) DEFAULT NULL," +
                REASON_FOR_NOT_FOLLOWING_SMC_WATERSHED_PATTERN + " varchar(30) DEFAULT NULL," +
                IS_LOCATION_OF_SMC_WORK_OK + " varchar(10) DEFAULT NULL," +
                REASON_FOR_INAPPROPRIATE_LOCATION_OF_SMC_WORK + " varchar(30) DEFAULT NULL," +
                IS_SMC_STRUCTURE_SERVING_INTENDED_PURPOSE + " varchar(10) DEFAULT NULL," +
                SMC_STRUCTURE_NOT_SERVING_INTENDED_PURPOSE_REASONS + " varchar(100) DEFAULT NULL," +
                SMC_WORK_STRUCTURE_DAMAGED_DETAILS + " varchar(30) DEFAULT NULL," +
                IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL + " varchar(30) DEFAULT NULL," +
                IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL_DETAILS + " varchar(100) DEFAULT NULL," +
                ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED + " varchar(30) DEFAULT NULL," +
                ALL_SMC_STRUCTURES_NOT_BUILT_IN_FARM + " varchar(100) DEFAULT NULL," +
                IS_LOCATION_OF_SMC_WORK_INAPPROPRIATE_LIST + " varchar(100) DEFAULT NULL," +
                ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED_YES + " varchar(100) DEFAULT NULL," +
                OTHER_SMC_WORKS + " varchar(10) DEFAULT NULL," +
                SMC_WORK_ANY_OTHER_REMARKS + " varchar(100) DEFAULT NULL)";
    }

    private String createAdvSMCMaster() {

        return " CREATE TABLE IF NOT EXISTS " + TABLE_ADV_SMC_MASTER + "  (" +
                SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SMC_STATUS + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                SMC_APPLICABLE + " varchar(10) DEFAULT NULL," +
                TOTAL_BUDGET_FOR_SMC_WORK + " float NOT NULL DEFAULT 0," +
                SMC_WORK_EXPENDITURE_AS_PER_NORM + " varchar(10) DEFAULT NULL," +
                ALL_SMC_STRUCTURES_BUILT_IN_FARM + " varchar(10) DEFAULT NULL," +
                WHERE_IT_WAS_DONE + " varchar(30) DEFAULT NULL," +
                SMC_PLANTATION_AREA_TREATED_COMPLETELY + " varchar(10) DEFAULT NULL," +
                SMC_TREATMENT_FOLLOWED_WATERSHED_PATTERN + " varchar(10) DEFAULT NULL," +
                REASON_FOR_NOT_FOLLOWING_SMC_WATERSHED_PATTERN + " varchar(30) DEFAULT NULL," +
                IS_LOCATION_OF_SMC_WORK_OK + " varchar(10) DEFAULT NULL," +
                REASON_FOR_INAPPROPRIATE_LOCATION_OF_SMC_WORK + " varchar(30) DEFAULT NULL," +
                IS_SMC_STRUCTURE_SERVING_INTENDED_PURPOSE + " varchar(10) DEFAULT NULL," +
                SMC_STRUCTURE_NOT_SERVING_INTENDED_PURPOSE_REASONS + " varchar(100) DEFAULT NULL," +
                SMC_WORK_STRUCTURE_DAMAGED_DETAILS + " varchar(30) DEFAULT NULL," +
                IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL + " varchar(30) DEFAULT NULL," +
                IS_DISPERSAL_SMC_ACCORDANCE_WITH_RAINFALL_DETAILS + " varchar(100) DEFAULT NULL," +
                ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED + " varchar(30) DEFAULT NULL," +
                ALL_SMC_STRUCTURES_NOT_BUILT_IN_FARM + " varchar(100) DEFAULT NULL," +
                IS_LOCATION_OF_SMC_WORK_INAPPROPRIATE_LIST + " varchar(100) DEFAULT NULL," +
                ANY_SMC_WORK_STRUCTURE_FOUND_DAMAGED_YES + " varchar(100) DEFAULT NULL," +
                OTHER_SMC_WORKS + " varchar(10) DEFAULT NULL," +
                SMC_WORK_ANY_OTHER_REMARKS + " varchar(100) DEFAULT NULL)";
    }

    private String createSmcListTable() {

        return " CREATE TABLE IF NOT EXISTS " + TABLE_SMC_LIST + " (" +
                SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                FORM_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                TYPE_OF_STRUCTURE + " varchar(30) DEFAULT NULL," +
                SMC_STRUCTURE_LENGTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_BREADTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_DEPTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_COST + " float NOT NULL DEFAULT 0," +
                SMC_AVAILABILITY + " INTEGER(11) NOT NULL DEFAULT 1)";
    }

    private String createAdvSmcListTable() {

        return " CREATE TABLE IF NOT EXISTS " + TABLE_ADV_SMC_LIST + " (" +
                SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " varchar(30) DEFAULT NULL," +
                FORM_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                TYPE_OF_STRUCTURE + " varchar(30) DEFAULT NULL," +
                SMC_STRUCTURE_LENGTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_BREADTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_DEPTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_COST + " float NOT NULL DEFAULT 0," +
                SMC_AVAILABILITY + " INTEGER(11) NOT NULL DEFAULT 1)";
    }

    private String createOtherSmcListTable() {

        return " CREATE TABLE IF NOT EXISTS " + TABLE_OTHER_SMC_LIST + " (" +
                OTHER_SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                WORK_CODE + " VARCHAR(30) DEFAULT NULL," +
                FORM_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                TYPE_OF_STRUCTURE + " VARCHAR(30) DEFAULT NULL," +
                SCHEME_NAME + " VARCHAR(30) DEFAULT NULL," +
                YEAR_OF_WORK + " VARCHAR(30) DEFAULT NULL," +
                EXPENDITURE_INCURRED + " VARCHAR(30) DEFAULT NULL," +
                STATUS_OF_SMC + " VARCHAR(30) DEFAULT NULL)";
    }

    private String createAdvOtherSmcList() {

        return " CREATE TABLE IF NOT EXISTS " + TABLE_ADV_OTHER_SMC_LIST + " (" +
                OTHER_SMC_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                WORK_CODE + " VARCHAR(30) DEFAULT NULL," +
                FORM_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                TYPE_OF_STRUCTURE + " VARCHAR(30) DEFAULT NULL," +
                SCHEME_NAME + " VARCHAR(30) DEFAULT NULL," +
                YEAR_OF_WORK + " VARCHAR(30) DEFAULT NULL," +
                EXPENDITURE_INCURRED + " VARCHAR(30) DEFAULT NULL," +
                STATUS_OF_SMC + " VARCHAR(30) DEFAULT NULL)";
    }

    private String createTypeOFBenefit() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BENEFIT_LIST + " (" +
                BENEFIT_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                WORK_CODE + " VARCHAR(30) DEFAULT NULL," +
                FORM_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                NATURE_OF_BENEFIT + " VARCHAR(200) DEFAULT NULL," +
                TYPE_OF_BENEFIT + " VARCHAR(200) DEFAULT NULL," +
                TYPE_OF_BENEFIT_OTHERS + " VARCHAR(100) DEFAULT NULL)"
                ;
    }

    private String createKfdPlntSmplngSmcDetailsv2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SMC_SAMPLING_DETAILS + " (" +
                SMC_WORK_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SMC_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                TYPE_OF_STRUCTURE + " varchar(50) DEFAULT NULL," +
                CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                GPS_LONGITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LATITUDE + " varchar(30) DEFAULT NULL," +
                GPS_ALTITUDE + " varchar(30) DEFAULT NULL," +
                GPS_COORDINATE_CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                IS_LOCATION_APPROPRIATE + " varchar(10) DEFAULT NULL," +
                SMC_STRUCTURE_LENGTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_BREADTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_DEPTH + " float NOT NULL DEFAULT 0," +
                SMC_STRUCTURE_TOTALVOLUME + " float NOT NULL DEFAULT 0," +
                ANY_DIFF_BTWN_BILLED_AND_ACUTAL_WORK + " varchar(10) DEFAULT NULL," +
                DETAILS_OF_DIFF_BTWN_BILLED_AND_ACUTAL_WORK + " varchar(500) DEFAULT NULL," +
                SMC_CONSTRUCTION_QUALITY + " varchar(30) DEFAULT NULL," +
                IS_SMC_SERVING_THE_PURPOSE + " varchar(10) DEFAULT NULL," +
                SMC_REMARKS + " varchar(500) DEFAULT NULL)";
    }

    private String createKfdPlntSmplngSamplePlotMasterv2() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_SAMPLE_PLOT_MASTER + " (" +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SAMPLE_PLOT_ID + " INTEGER PRIMARY KEY," +
                SAMPLE_PLOT_SEEDLING_STATUS + " INTEGER(2) DEFAULT 0," +
                SAMPLE_PLOT_SEED_DIBBLING_STATUS + " INTEGER(2) DEFAULT 0," +
                EMPTY_PIT_STATUS + " INTEGER(2) DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) DEFAULT 0," +
                SAMPLE_PLOT_NUMBER + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_SUITABLE + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_NOT_SUITABLE_REASONS + " varchar(100) DEFAULT NULL," +
                SAMPLE_PLOT_LONGITUDE + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_LATITUDE + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_ALTITUDE + " varchar(30) DEFAULT NULL," +
                GPS_COORDINATE_CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                FOUR_CORNERS_MARKING_METHOD + " varchar(100) DEFAULT NULL," +
                FOUR_CORNERS_MARKING_METHOD_OTHER + " varchar(100) DEFAULT NULL," +
                TOTAL_COUNT + " INTEGER(11) NOT NULL DEFAULT 0," +
                TOTAL_COUNT_SURVIVED + " INTEGER(11) DEFAULT 0," +
                NO_OF_EMPTY_PITS + " INTEGER(11) ," +
                SEEDLING_PERCENTAGE + " float NOT NULL DEFAULT 0," +
                ROOTSTOCK_AVAILABLE + " varchar(30) DEFAULT NULL," +
                REGENERATION_AVAILABLE + " varchar(30) DEFAULT NULL," +
                SPECIES_NAME + " varchar(500) DEFAULT NULL," +
                SPECIES_OTHER + " varchar(100) DEFAULT NULL," +
                STEMS_WITH_COLLAR_2_10CM + " float NOT NULL DEFAULT 0," +
                AVERAGE_COLLAR_GIRTH + " float NOT NULL DEFAULT 0," +
                AVERAGE_HEIGHT_METERS + " float NOT NULL DEFAULT 0," +
                SHRUB_VEGETATION_REMARKS + " varchar(200) DEFAULT NULL," +
                TREE_SPECIES_PRESENT + " varchar(30) DEFAULT NULL," +
                SPECIES_NAME_INVENTORY + " varchar(100) DEFAULT NULL," +
                SPECIES_OTHER_INVENTORY + " varchar(100) DEFAULT NULL," +
                STEMS_WITH_COLLAR_ABOVE_10CM + " float NOT NULL DEFAULT 0," +
                GBH_SPECIES_ABOVE_10CM + " float NOT NULL DEFAULT 0," +
                AVERAGE_HEIGHT_METERS_INVENTORY + " float NOT NULL DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(3) DEFAULT -1," +
                SAMPLE_PLOT_INVENTORY_DONE_ON + " INTEGER(11) NOT NULL DEFAULT 0)";
    }

    private String createAdvSamplePlotMaster() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_ADV_SAMPLE_PLOT_MASTER + " (" +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                SAMPLE_PLOT_ID + " INTEGER PRIMARY KEY," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                SAMPLE_PLOT_NUMBER + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_SUITABLE + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_NOT_SUITABLE_REASONS + " varchar(100) DEFAULT NULL," +
                SAMPLE_PLOT_LONGITUDE + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_LATITUDE + " varchar(30) DEFAULT NULL," +
                SAMPLE_PLOT_ALTITUDE + " varchar(30) DEFAULT NULL," +
                GPS_COORDINATE_CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                FOUR_CORNERS_MARKING_METHOD + " varchar(100) DEFAULT NULL," +
                FOUR_CORNERS_MARKING_METHOD_OTHER + " varchar(100) DEFAULT NULL," +
                /*TOTAL_COUNT + " INTEGER(11) NOT NULL DEFAULT 0," +
                TOTAL_COUNT_SURVIVED + " INTEGER(11) DEFAULT 0," +
                SEEDLING_PERCENTAGE + " float NOT NULL DEFAULT 0," +
                ROOTSTOCK_AVAILABLE + " varchar(30) DEFAULT NULL," +*/
                TYPE_OF_EARTH_WORK_DONE + " varchar(30) DEFAULT NULL," +
                PITSIZE + " varchar(30) DEFAULT NULL," +
                PIT_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_PITS + " varchar(30) DEFAULT NULL," +
                PITS_COUNTED + " varchar(30) DEFAULT NULL," +

                TRENCH_SIZE + " varchar(30) DEFAULT NULL," +
                TRENCH_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_TRENCHS + " varchar(30) DEFAULT NULL," +
                TRENCHS_COUNTED + " varchar(30) DEFAULT NULL," +

                PIT_IN_PIT_SIZE + " varchar(100) DEFAULT NULL," +
                PIT_IN_PIT_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_PIT_IN_PIT + " varchar(30) DEFAULT NULL," +
                PIT_IN_PIT_COUNTED + " varchar(30) DEFAULT NULL," +

                RIPPING_SIZE + " varchar(30) DEFAULT NULL," +
                RIPPING_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                NO_OF_RIPLINE + " varchar(30) DEFAULT NULL," +
                RIPLINE_COUNTED + " varchar(30) DEFAULT NULL," +

                OTHERS_SIZE + " varchar(30) DEFAULT NULL," +
                OTHERS_ESPACEMENT + " varchar(30) DEFAULT NULL," +
                OTHERS_NO_OF_UNITS + " varchar(30) DEFAULT NULL," +
                OTHER_COUNTED + " varchar(30) DEFAULT NULL," +
                ADV_SAMPLEPLOT_REMARKS + " varchar(100) DEFAULT NULL," +

                ROOTSTOCK_AVAILABLE + " varchar(30) DEFAULT NULL," +
                REGENERATION_AVAILABLE + " varchar(30) DEFAULT NULL," +
                SPECIES_NAME + " varchar(500) DEFAULT NULL," +
                SPECIES_OTHER + " varchar(100) DEFAULT NULL," +
                STEMS_WITH_COLLAR_2_10CM + " float NOT NULL DEFAULT 0," +
                AVERAGE_COLLAR_GIRTH + " float NOT NULL DEFAULT 0," +
                AVERAGE_HEIGHT_METERS + " float NOT NULL DEFAULT 0," +
                SHRUB_VEGETATION_REMARKS + " varchar(200) DEFAULT NULL," +
                TREE_SPECIES_PRESENT + " varchar(30) DEFAULT NULL," +
                SPECIES_NAME_INVENTORY + " varchar(100) DEFAULT NULL," +
                SPECIES_OTHER_INVENTORY + " varchar(100) DEFAULT NULL," +
                STEMS_WITH_COLLAR_ABOVE_10CM + " float NOT NULL DEFAULT 0," +
                GBH_SPECIES_ABOVE_10CM + " float NOT NULL DEFAULT 0," +
                AVERAGE_HEIGHT_METERS_INVENTORY + " float NOT NULL DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(3) DEFAULT -1," +
                SAMPLE_PLOT_INVENTORY_DONE_ON + " INTEGER(11) NOT NULL DEFAULT 0)";
    }

    private String createKfdPlntSmplngAnrmodelMasterV2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_PLOT_MASTER + " (" +
                ANRMODEL_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                GPS_LONGITUDE + " varchar(30) DEFAULT NULL," +
                GPS_ALTITUDE + " varchar(30) DEFAULT NULL," +
                GPS_LATITUDE + " varchar(30) DEFAULT NULL," +
                GPS_COORDINATE_CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                DISTANCE_FROM_PLANTATION_BOUNDRY + " float NOT NULL DEFAULT 0," +
                DIRECTION_IN_WHICH_CONTROL_PLOT_LOCATED + " varchar(100) DEFAULT NULL," +
                CONTROL_PLOT_TYPE + " varchar(30) DEFAULT NULL," +
                CLOSED_OR_OPEN_AREA + " varchar(10) DEFAULT NULL)";

    }

    private String createKfdPlntSmplngplotInventoryV2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_PLOT_INVENTORY + " (" +
                INVENTORY_ID + " INTEGER   PRIMARY KEY ," +
                ANRMODEL_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                FORM_ID + " INTEGER(11) NOT NULL DEFAULT 0," +
                PART_TYPE + " varchar(120) DEFAULT NULL," +
                CONTROL_PLOT_TYPE + " varchar(30) DEFAULT NULL," +
                SPECIES_NAME + " varchar(2000) DEFAULT NULL," +
                AVERAGE_GBH_METERS + " float NOT NULL DEFAULT 0," +
                AVERAGE_HEIGHT_METERS + " float NOT NULL DEFAULT 0," +
                TOTAL_COUNT + " INTEGER(11) NOT NULL DEFAULT 0," +
                STATE_OF_HEALTH + " varchar(50) DEFAULT NULL," +
                AVERAGE_COLLAR_GIRTH + " float NOT NULL DEFAULT 0," +
                NUMBER_TENDED + " INTEGER(11) NOT NULL DEFAULT 0" +
                ")";

    }


    private String createKfdPlntSmplngSDPV2() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_SDP + "(" +
                FORM_ID + "  INTEGER(11) DEFAULT NULL," +
                WORK_CODE + "  varchar(500) DEFAULT NULL," +
                FORM_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(2) NOT NULL DEFAULT -1," +
                BENEFICIARY_STATUS + " INTEGER(2) NOT NULL DEFAULT 0," +
                CIRCLE_NAME + " varchar(100) DEFAULT NULL," +
                CIRCLE_ID + " varchar(100) DEFAULT NULL," +
                DIVISION_NAME + " varchar(100) DEFAULT NULL," +
                DIVISION_CODE + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_NAME + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_CODE + " varchar(100) DEFAULT NULL," +
                RANGE_NAME + " varchar(100) DEFAULT NULL," +
                RANGE_CODE + " varchar(100) DEFAULT NULL," +
                DISTRICT_NAME + " varchar(100) DEFAULT NULL," +
                DISTRICT_CODE + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_NAME + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_ID + " varchar(100) DEFAULT NULL," +
                TALUK_NAME + " varchar(100) DEFAULT NULL," +
                TALUK_CODE + " varchar(100) DEFAULT NULL," +
                GRAMA_PANCHAYAT_NAME + " varchar(100) DEFAULT NULL," +
                PANCHAYAT_CODE + " varchar(100) DEFAULT NULL," +
                VILLAGE_NAME + " varchar(100) DEFAULT NULL," +
                VILLAGE_CODE + " varchar(100) DEFAULT NULL," +
                BENEFICIARIES_TOTAL_COUNT + " INTEGER(8) DEFAULT 0)";

    }

    @NonNull
    private String createSDPBeneficiaryV2() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BENEFICIARY + " (" +
                BENEFICIARY_ID + " INTEGER   PRIMARY KEY ," +
                FORM_ID + "  INTEGER(11) DEFAULT  0 ," +
                DRAW_MAP_STATUS + "  INTEGER(11) DEFAULT  0 ," +
                FORM_FILLED_STATUS + "  INTEGER(2) DEFAULT  0 ," +
                SEEDLING_DETAIL_STATUS + "  INTEGER(11) DEFAULT  0 ," +
                NAME + "  varchar(100) DEFAULT NULL," +
                FATHER_NAME + "  varchar(100) DEFAULT NULL," +
                AADHAR_NUMBER + "  INTEGER(11) DEFAULT  0 ," +
                SEX + "  varchar(10) DEFAULT NULL," +
                AGE + "  INTEGER(11) DEFAULT  0 ," +
                CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                EDUCATION + "  varchar(30) DEFAULT NULL," +
                LAND_HOLDING_ACRE + "  float DEFAULT  0 ," +
                SURVEY_NUMBERS_WHERE_PLANTED + "  varchar(100) DEFAULT NULL," +
                SEEDLINGS_PURCHASE_COUNT + "  INTEGER(11) DEFAULT  0 ," +
                PLANTATION_MODEL + " varchar(100) DEFAULT NULL," +
                PROGRAM_NAME + " varchar(100) DEFAULT NULL," +
                YEAR_OF_IMPLEMENTATION + " varchar(15) DEFAULT 0," +
                SEEDLING_PROCURED + "  INTEGER(11) DEFAULT  0 ," +
                COST_PAID_APPLICABLE + " varchar(10) DEFAULT NULL," +
                BENEFICIARY_AVAILABLE_ATTIME_OF_VISIT + " varchar(10) DEFAULT NULL," +
                BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON + " varchar(60) DEFAULT NULL," +
                BENEFICIARY_NOTAVAILABLE_ATTIME_VISIT_REASON_OTHER + " varchar(300) DEFAULT NULL," +
                BENEFICIARY_WILLING_TO_PARTICIPATE_IN_SURVEY + " varchar(10) DEFAULT NULL," +
                BENEFICIARY_NOT_WILLING_TO_PARTICIPATE_IN_SURVEY_REASON + " varchar(300) DEFAULT NULL," +
                SPECIES_SDP_BENEFICIARY + "  varchar(2000) DEFAULT NULL," +
                SPECIES_OTHER + "  varchar(200) DEFAULT NULL," +
                TOTAL_COST + " float DEFAULT 0," +
                NUMBER_OF_SEEDLINGS_PLANTED + "  INTEGER(11) DEFAULT  0 ," +
                SEEDLING_PERCENTAGE + " INTEGER(11) DEFAULT 0," +
                NUMBER_OF_SEEDLINGS_SURVIVING + " INTEGER(11) DEFAULT 0," +
                TYPE_OF_PLANTING + "  varchar(50) DEFAULT NULL," +
                TYPE_OF_PLANTING_MAINCROP + "  varchar(500) DEFAULT NULL," +
                AVERAGE_SPACEMENT_METERS + "  float DEFAULT  0 ," +
                PLANTING_GPS_LONGITUDE + "  varchar(30) DEFAULT NULL," +
                PLANTING_GPS_LATITUDE + "  varchar(30) DEFAULT NULL," +
                PLANTING_GPS_ALTITUDE_METERS + "  varchar(30) DEFAULT NULL," +
                GPS_COORDINATE_CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0, " +
                GPS_MEASUREMENT + "  varchar(30) DEFAULT NULL," +
                GPS_SAMPLEPLOT_COLLECTION + " varchar(500) DEFAULT NULL," +
                GPS_LATLONG_COLLECTION + " varchar(5000) DEFAULT NULL," +
                PLANTING_IRRIGATION_LEVEL + "  varchar(50) DEFAULT NULL," +
                PLANTING_IRRIGATION_LEVEL_OTHER_DETAILS + "  varchar(500) DEFAULT NULL," +
                PLANTING_IRRIGATION_METHOD + "  varchar(50) DEFAULT NULL," +
                PLANTING_FERTILIZE_USED + "  varchar(10) DEFAULT NULL," +
                PLANTING_FERTILIZE_USED_DETAILS + "  varchar(500) DEFAULT NULL," +
                PLANTING_PRUNINGE_DONE + "  varchar(10) DEFAULT NULL," +
                PLANTING_OTHER_TREATMENT_DETAILS + "  varchar(500) DEFAULT NULL," +
                PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_APPLICABLE + " varchar(10) DEFAULT NULL," +
                PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_RS + " float DEFAULT 0," +
                PAYMENT_RECEIVED_FROM_MGNREEGS_YEAR + " varchar(20) DEFAULT NULL," +
                PAYMENT_RECEIVED_FROM_MGNREEGS_RS + "  float DEFAULT 0 ," +
                SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_YEAR + " varchar(20) DEFAULT NULL," +
                SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_RS + " float DEFAULT  0 ," +
                CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1_APPLICABLE + " varchar(10) DEFAULT NULL, " +
                CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR1 + " float DEFAULT  0 ," +
                CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2_APPLICABLE + " varchar(10) DEFAULT NULL, " +
                CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR2 + " float DEFAULT  0 ," +
                CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3_APPLICABLE + " varchar(10) DEFAULT NULL, " +
                CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_YEAR3 + " float DEFAULT  0 ," +
                CASH_INCENTIVE_RECEIVED_FROM_KFD_UNDER_KAPY_TOTAL + " float DEFAULT  0 ," +
                MODE_PAYMENT + " varchar(30) DEFAULT NULL ," +
                INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED + "  varchar(10) DEFAULT NULL," +
                INCENTIVE_ANY_OTHER_REWARDS_AWARDS_RECEIVED_DETAILS + "  varchar(500) DEFAULT NULL," +
                DID_YOU_FACE_PROBLEM_IN_PROCURING_SEEDLING + "  varchar(10) DEFAULT NULL," +
                DETAILS_OF_PROBLEM_FACED_IN_PROCURING_SEEDLING + "  varchar(500) DEFAULT NULL," +
                SPECIES_AS_PER_REQUIREMENT + "  varchar(10) DEFAULT NULL," +
                SPECIES_NOT_REQUIREMENT_REASONS + "  varchar(200) DEFAULT NULL," +
                SPECIES_SUITABLE_TO_AREA + "  varchar(10) DEFAULT NULL," +
                ARE_YOU_SATISFIED_WITH_SEEDLING_QUALITY + "  varchar(10) DEFAULT NULL," +
                REASONS_FOR_DISSATISFACTION_WITH_SEEDLING_QUALITY + "  varchar(500) DEFAULT NULL," +
                ANY_SOCIAL_COST_IN_RAISING_TREES + "  varchar(10) DEFAULT NULL," +
                DETAILS_OF_SOCIAL_COST_IN_RAISING_TREES + "  varchar(500) DEFAULT NULL," +
                DID_YOU_BUY_SEEDLINGS_FROM_PVT_NURSERIES + "  varchar(10) DEFAULT NULL," +
                DETAILS_OF_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES + "  varchar(500) DEFAULT NULL," +
                REASONS_FOR_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES + "  varchar(500) DEFAULT NULL," +
                SEEDLINGS_PERFORMANCE_COMPARED_TO_KFD_SEEDLINGS + "  varchar(50) DEFAULT NULL," +
                INTERESTED_IN_BUYING_MORE_SEEDLINGS_FROM_KFD + "  varchar(10) DEFAULT NULL," +
                DO_YOU_NEED_SPECIFIC_SUPPORT_FROM_GOVT + " varchar(10) DEFAULT NULL," +
                DETAILS_OF_SPECIFIC_SUPPORT_NEEDED_FROM_GOVT + " varchar(500) DEFAULT NULL," +
                SUGGESTIONS_TO_IMPROVE_AGRO_FORESTRY + "  varchar(500) DEFAULT NULL," +
                FINISHED_POSITION + " INTEGER(3) DEFAULT -1" +
                ")";
    }

    private String createOtherWorksV2() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_OTHER_WORKS + " (" +
                FORM_ID + " INTEGER DEFAULT 0, " +
                FINISHED_POSITION + " INTEGER(2) DEFAULT -1," +
                CIRCLE_NAME + " varchar(100) DEFAULT NULL," +
                CIRCLE_ID + " varchar(100) DEFAULT NULL," +
                DIVISION_NAME + " varchar(100) DEFAULT NULL," +
                DIVISION_CODE + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_NAME + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_CODE + " varchar(100) DEFAULT NULL," +
                RANGE_NAME + " varchar(100) DEFAULT NULL," +
                RANGE_CODE + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_NAME + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_ID + " varchar(100) DEFAULT NULL," +
                MAINTAINENACE_FRESH + " varchar(30) DEFAULT NULL," +
                NO_OF_CARINS_RCCPILLARS_RFSTONE + " INTEGER(11) DEFAULT 0," +
                FOREST_NAME + " varchar(30) DEFAULT NULL," +
                SCHEME_NAME + " varchar(100) DEFAULT NULL," +
                SCHEME_ID + " INTEGER(11) DEFAULT 0," +
                LEGAL_STATUS_OF_LAND + " varchar(50) DEFAULT NULL, " +
                LEGAL_STATUS_OF_LAND_OTHER_DETAILS + " varchar(100) DEFAULT NULL, " +
                TYPE_OF_WORK + " varchar(100) DEFAULT NULL, " +
                WORK_NAME + " varchar(100) DEFAULT NULL, " +
                TYPE_OF_OFFICIER_RESBUILDINGS + " varchar(100) DEFAULT NULL, " +
                TYPE_OF_OFFICIER_OFFICEBUILDINGS + " varchar(100) DEFAULT NULL, " +
                OTHER_TYPE_OF_WORK + " varchar(100) DEFAULT NULL, " +
                WORK_LOCATION + " varchar(100) DEFAULT NULL, " +
                EXECUTION_YEAR + " varchar(15) DEFAULT NULL, " +
                ESTIMATED_COST_RUPEES + " INTEGER(11) DEFAULT 0, " +
                TOTAL_EXPENDITURE + " INTEGER(11) DEFAULT 0, " +
                WORK_LOCATION_LAT + " float DEFAULT 0, " +
                WORK_LOCATION_LONG + " float DEFAULT 0, " +
                WORK_LOCATION_ALTI + " varchar(30) DEFAULT NULL, " +
                WORK_CODE + " varchar(500) DEFAULT NULL, " +
                WORK_APPROVED_IN_APO + "  varchar(10) DEFAULT NULL, " +
                WORK_APPROVED_IN_APO_SLNO_AVAILABLE + " varchar(10) DEFAULT NULL, " +
                WORK_APPROVED_IN_APO_SLNO + " varchar(50) DEFAULT NULL, " +
                WORK_APPROVED_IN_APO_DATE_AVAILABLE + " varchar(10) DEFAULT NULL, " +
                WORK_APPROVED_IN_APO_APPROVAL_DATE + " varchar(10) DEFAULT NULL, " +
                WORK_APPROVED_IN_APO_TIMESTAMP + "       INTEGER(11) DEFAULT NULL, " +
                WORK_APPROVED_IN_APO_NO_REASON + "  varchar(500) DEFAULT NULL, " +
                WAS_PROCUREMENT_INVOLVED + "  varchar(10) DEFAULT NULL, " +
                MODE_OF_PROCUREMENT + "  varchar(100) DEFAULT NULL, " +
                MODE_OF_PROCUREMENT_OTHERS + "  varchar(100) DEFAULT NULL, " +
                PROCUREMENT_AMOUNT + "  INTEGER(11) DEFAULT NULL, " +
               /* WAS_PROCUREMENT_YES_TENDER_AMOUNT + "       float DEFAULT NULL, " +
                WAS_PROCUREMENT_YES_DGSD_AMOUNT + "       float DEFAULT NULL, " +
                WAS_PROCUREMENT_YES_PRICE_QUOT + "       float DEFAULT NULL, " +
                WAS_PROCUREMENT_YES_OTHER_DETAILS + "  varchar(500) default NULL," +
                WAS_PROCUREMENT_YES_OTHER_AMOUNT + "       float DEFAULT NULL, " +*/
                WHO_EXEC_WORK + "  varchar(30) DEFAULT NULL, " +
                WHO_EXEC_WORK_OTHERS + "  varchar(100) DEFAULT NULL, " +
                WHEN_WORK_START_YR + " INTEGER(11) DEFAULT 0, " +
                WHEN_WORK_STARTED_MONTH + " varchar(15) DEFAULT NULL, " +
                WAS_WORK_COMPLETE + " varchar(20) DEFAULT NULL, " +
                WHEN_WORK_COMPLETED_YEAR + " varchar(20) DEFAULT NULL, " +
                WHEN_WORK_COMPLETED_MONTH + " varchar(20) DEFAULT NULL, " +
                TIME_TAKEN_MONTHS + " INTEGER(11) DEFAULT 0, " +
                CHECK_MEASUREMENT_TIMESTAMP_AVAILABLE + " varchar(10) DEFAULT NULL, " +
                CHECK_MEASUREMENT_TIMESTAMP + " varchar(10) DEFAULT NULL, " +
                COMPLETION_CERTIFICATE_TIMESTAMP_AVAILABLE + " varchar(10) DEFAULT NULL, " +
                COMPLETION_CERTIFICATE_TIMESTAMP + " varchar(10) DEFAULT NULL, " +
                REMARKS + "  varchar(500) DEFAULT NULL, " +
                PURPOSE_OF_ORIGINALWORK + "  varchar(500) DEFAULT NULL, " +
                SIMILAR_WORK_INSAME_LOCALITY + "  varchar(10) DEFAULT NULL, " +
                SIMILAR_WORK_IN_LOCALITY_PREV_DETAILS + " varchar(500) DEFAULT NULL, " +
                SIMILAR_WORK_INSAME_LOCALITY_USAGE + "  varchar(10) DEFAULT NULL, " +
                SIMILAR_WORK_INSAME_LOCALITY_USAGE_REASON + "  varchar(500) DEFAULT NULL, " +
                ORIGINAL_WORK_SITE_SELECTION + "  varchar(10) DEFAULT NULL, " +
                ORIGINAL_WORK_SITE_SELECTION_REASON + "  varchar(500) DEFAULT NULL, " +
                ORIGINAL_WORK_DAMAGED_LOCAL_VEGETATION + "  varchar(10) DEFAULT NULL, " +
                ORIGINAL_WORK_DAMAGED_LOCAL_VEGETATION_REASON + "  varchar(500) DEFAULT NULL, " +
                WORK_CARRIEDOUT_ASPER_ESTIMATE + "  varchar(10) DEFAULT NULL, " +
                WORK_CARRIEDOUT_ASPER_ESTIMATE_DEVIATION + "  varchar(500) DEFAULT NULL, " +
                WORK_COMPLETED_FROM_ALL_ASPECTS + "  varchar(10) DEFAULT NULL, " +
                WORK_NOT_COMPLETED_FROM_ALL_ASPECTS_DETAILS + "  varchar(100) DEFAULT NULL, " +
//                ORIGINAL_WORK_QUALITY_RATING + "       INTEGER(11) DEFAULT 0, " +
                ORIGINAL_WORK_REMARKS + "  varchar(500) DEFAULT NULL, " +
                IS_ASSET_USED_NOW + "  varchar(10) DEFAULT NULL, " +
                ASSET_USED_NOW_WHO + "  varchar(100) DEFAULT NULL, " +
                ASSET_USED_NOW_PURPOSE + "  varchar(100) DEFAULT NULL, " +
                ASSET_SERVING_INTENDED_PURPOSE + "  varchar(100) DEFAULT NULL, " +
                REASON_FOR_ASSET_NOT_USED_NOW + "  varchar(500) DEFAULT NULL, " +
                WORK_MEETS_PROGRAM_OBJECTIVE + "   varchar(10) DEFAULT NULL, " +
                REASON_FOR_WORK_DOES_NOT_MEET_OBJECTIVE + "   varchar(500) DEFAULT NULL, " +
                THINGS_TO_DO_FOR_MAKING_ASSET_MORE_EFFECTIVE + "  varchar(500) DEFAULT NULL, " +
//                ORIGINAL_WORK_QUALITY_GRADING + "       INTEGER(11) DEFAULT 0, " +
//                ORIGINAL_ASSET_QUALITY_RATING + "   varchar(10) DEFAULT NULL, " +
                UTILIZATION_REMARKS + "  varchar(500) DEFAULT NULL, " +
                PHOTOS_COUNT + " INTEGER(11) NOT NULL DEFAULT 0, " +
                WHETHER_WORK_EXISTS_NOW + " varchar(10) DEFAULT NULL)";
    }

    private String createSCPNTSPV2() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCP_N_TSP + " (" +
                FORM_ID + " INTEGER(11) DEFAULT 0," +
                COMMUNITY_STATUS + " INTEGER(2) DEFAULT 0," +
                INDIVIDUAL_STATUS + " INTEGER(2) DEFAULT 0," +
                COMPLETED_POSITION + " INTEGER(2) DEFAULT -1," +
                WORK_CODE + " varchar(500) DEFAULT NULL," +
                CIRCLE_NAME + " varchar(100) DEFAULT NULL," +
                CIRCLE_ID + " varchar(100) DEFAULT NULL," +
                DIVISION_NAME + " varchar(100) DEFAULT NULL," +
                DIVISION_CODE + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_NAME + " varchar(100) DEFAULT NULL," +
                SUBDIVISION_CODE + " varchar(100) DEFAULT NULL," +
                RANGE_NAME + " varchar(100) DEFAULT NULL," +
                RANGE_CODE + " varchar(100) DEFAULT NULL," +
                DISTRICT_NAME + " varchar(100) DEFAULT NULL," +
                DISTRICT_CODE + " varchar(100) DEFAULT NULL," +
                TALUK_NAME + " varchar(100) DEFAULT NULL," +
                TALUK_CODE + " varchar(100) DEFAULT NULL," +
                HOBLI_NAME + " varchar(100) DEFAULT NULL," +
                GRAMA_PANCHAYAT_NAME + " varchar(100) DEFAULT NULL," +
                PANCHAYAT_CODE + " varchar(100) DEFAULT NULL," +
                VILLAGE_NAME + " varchar(100) DEFAULT NULL," +
                VILLAGE_CODE + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_NAME + " varchar(100) DEFAULT NULL," +
                CONSTITUENCY_ID + " varchar(100) DEFAULT NULL," +
                NATURE_OF_BENEFIT + " varchar(200) DEFAULT NULL," +
                NAME_OF_COMMUNITY + " varchar(30) DEFAULT NULL," +
                NAME_OF_COMMUNITY_OTHERS + " varchar(30) DEFAULT NULL," +
                PHOTOS_COUNT + " INTEGER(11) NOT NULL DEFAULT 0)";
    }

    private String createSCPNTSPSurvey() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCP_N_TSP_SURVEY + " (" +
                SCP_TSP_ID + " INTEGER PRIMARY KEY," +
                FORM_ID + " INTEGER(11) DEFAULT 0," +
                FORM_FILLED_STATUS + " INTEGER(2) DEFAULT 0," +
                BENEFIT_ID + " INTEGER(11) DEFAULT 0," +
                BENEFICIARY_STATUS + " INTEGER(2) DEFAULT 0," +
                WORK_CODE + " varchar(500) DEFAULT NULL," +
                FINISHED_POSITION + " INTEGER(11) DEFAULT -1," +
                NATURE_OF_BENEFIT + " varchar(200) DEFAULT NULL," +
                TYPE_OF_ASSET + " varchar(30) DEFAULT NULL," +
                TYPE_OF_BENEFIT + " varchar(100) DEFAULT NULL," +
                TYPE_OF_BENEFIT_OTHERS + " varchar(100) DEFAULT NULL," +
                TYPE_OF_ASSET_OTHERS + " varchar(30) DEFAULT NULL," +
                BENEFICIARY_CODE + " varchar(100) DEFAULT NULL," +
                BENEFICIARY_NAME + " varchar(100) DEFAULT NULL," +
                AADHAR_NUMBER + " varchar(50) DEFAULT NULL," +
                BENEFICIARY_FATHER_NAME + " varchar(100) DEFAULT NULL," +
                BENEFICIARY_SEX + " varchar(10) DEFAULT NULL," +
                BENEFICIARY_AGE + " INTEGER(11) DEFAULT 0," +
                BENEFICIARY_EDUCATION + " varchar(20) DEFAULT NULL," +
                NO_OF_HOUSEHOLDS_IN_LOCATION + " INTEGER(11) DEFAULT 0," +
                NO_OF_HOUSEHOLDS_BENEFITED + " INTEGER(11) DEFAULT 0," +
                EXTENT_OF_ASSET_WORK + " INTEGER(11) DEFAULT 0," +
                UNIT_OF_ASSET_WORK + " varchar(10) DEFAULT NULL," +
                TOTAL_COST + " float DEFAULT 0," +
                COMMUNITY_WORK_ASSET_EXISTS_NOW + " varchar(10) DEFAULT NULL," +
                COMMUNITY_WORK_ASSET_LAT + " float DEFAULT 0," +
                COMMUNITY_WORK_ASSET_LONG + " float DEFAULT 0," +
                WORK_ASSET_NON_EXIST + " varchar(30) DEFAULT NULL," +
                IS_WORK_ASSET_PRESENT + " varchar(10) DEFAULT NULL," +
                IS_WORK_ASSET_PRESENT_NO_REASONS + " varchar(100) DEFAULT NULL," +
                IS_WORK_ASSET_PRESENT_CONDITION + " varchar(10) DEFAULT NULL," +
                IS_ARRANGEMENT_EXIST + " varchar(10) DEFAULT NULL," +
                IS_ARRANGEMENT_EXIST_NO + " varchar(10) DEFAULT NULL," +
                IS_ARRANGEMENT_EXIST_YES + " varchar(10) DEFAULT NULL," +
                IS_ASSET_PROPERLY_LABELED + " varchar(10) DEFAULT NULL," +
                ANY_VARIATION_FNB_MB + " varchar(10) DEFAULT NULL," +
                ANY_VARIATION_FNB_MB_YES + " varchar(100) DEFAULT NULL," +
                ARE_THE_PROGRAMME_OBJECTIVES_ACHIEVED + " varchar(10) DEFAULT NULL," +
                ARE_THE_PROGRAMME_OBJECTIVES_ACHIEVED_YES + " varchar(20) DEFAULT NULL," +
                OBJECTIVES_ACHIEVED_DETAILS + " varchar(100) DEFAULT NULL," +
//                ORIGINAL_WORK_QUALITY_RATING + " varchar(10) DEFAULT NULL," +
                SUGGESTIONS_EVALUATION + " varchar(100) DEFAULT NULL," +

                PROGRAM_NAME + " varchar(50) DEFAULT NULL," +
                PROGRAM_NAME_OTHERS + " varchar(50) DEFAULT NULL," +
                YEAR_OF_IMPLEMENTATION + " varchar(15) DEFAULT NULL," +
                PHOTOS_COUNT + " INTEGER(11) NOT NULL DEFAULT 0)";
    }

    private String createSCPTSPBeneficiaryV2() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCP_TSP_BENIFICIARY + " (" +
                BENIFICIARY_ID + " INTEGER PRIMARY KEY, " +
                FORM_ID + " INTEGER(11) DEFAULT 0, " +
                FORM_FILLED_STATUS + " INTEGER(2) DEFAULT 0, " +
                BENEFIT_ID + " INTEGER(11) DEFAULT 0," +
                FINISHED_POSITION + " INTEGER(11) DEFAULT -1," +
                BENEFICIARY_CODE + " INTEGER(11) DEFAULT 0," +
                BENEFICIARY_NAME + " varchar(100) DEFAULT NULL," +
                BENEFICIARY_FATHER_NAME + " varchar(100) DEFAULT NULL," +
                AADHAR_NUMBER + " varchar(30) DEFAULT NULL," +
                BENEFICIARY_SEX + " varchar(10) DEFAULT NULL," +
                NAME_OF_COMMUNITY + " varchar(10) DEFAULT NULL," +
                BENEFICIARY_AGE + " INTEGER(11) DEFAULT 0," +
                BENEFICIARY_EDUCATION + " varchar(20) DEFAULT NULL," +
                GRAMA_PANCHAYAT_NAME + " varchar(20) DEFAULT NULL," +
                VILLAGE_NAME + " varchar(20) DEFAULT NULL," +
                BENEFICIARY_LANDHOLDING_ACRES + " varchar(20) DEFAULT NULL," +
                LAND_HOLDING_ACRE + " varchar(20) DEFAULT NULL," +
                ECONOMICAL_STATUS + " varchar(20) DEFAULT NULL," +
                ASSET_WORK_EXIST + " varchar(20) DEFAULT NULL," +
                ASSET_WORK_EXIST_NO_REASONS + " varchar(20) DEFAULT NULL," +
                BENEFICIARY_AVAILABLE + " varchar(20) DEFAULT NULL," +
                BENEFICIARY_AVAILABLE_NO_REASONS + " varchar(100) DEFAULT NULL," +
                BENEFICIARY_AVAILABLE_NO_OTHER_REASONS + " varchar(100) DEFAULT NULL," +
                BENEFICIARY_WILLING_TO_PARTICIPATE_IN_SURVEY + " varchar(10) DEFAULT NULL," +
                BENEFICIARY_WILLING_NO_REASONS + " varchar(10) DEFAULT NULL," +
                SIZE_QUANTITY_ASSET + " FLOAT DEFAULT 0," +
                UNIT_OF_ASSET + " varchar(10) DEFAULT NULL," +
                TOTAL_COST + " float DEFAULT 0," +
                SCP_BEN_LAT + "  varchar(30) DEFAULT NULL," +
                SCP_BEN_LONG + "  varchar(30) DEFAULT NULL," +
                BENEFICIARY_CONTRIBUTION + " varchar(10) DEFAULT NULL," +
                ASSESTS_AVAILABLE + " varchar(40) DEFAULT NULL," +
                NO_OF_MEMBERS_IN_FAMILY + " INTEGER(11) DEFAULT 0," +
                AMOUNT_CONTRIBUTION + " FLOAT DEFAULT 0," +
                IS_ASSET_USED_BENEFICIARY + " varchar(10) DEFAULT NULL," +
                FREQUENCY_OF_USAGE + " varchar(20) DEFAULT NULL," +
                IS_ASSET_MAINTAINED + " varchar(10) DEFAULT NULL," +
                IS_ASSET_MAINTAINED_NO_REASONS + " varchar(100) DEFAULT NULL," +
                IS_ASSET_MAINTAINED_YES_CONDITION + " varchar(50) DEFAULT NULL," +
                IS_ASSET_PROPERLY_LABELED + " varchar(10) DEFAULT NULL," +
                IS_ASSET_PROPERLY_LABELED_NO_REASONS + " varchar(500) DEFAULT NULL," +
                IS_ASSET_COMPANY_PRODUCT + " varchar(10) DEFAULT NULL," +
                BRAND_DETAILS + " varchar(100) DEFAULT NULL," +
                DETAILS_OF_WARRANTY + " varchar(100) DEFAULT NULL," +
                VARIATION_FNB + " varchar(50) DEFAULT NULL," +
                VARIATION_FNB_DETAILS + " varchar(500) DEFAULT NULL," +
                BENEFICIARY_SATSFIED + " varchar(10) DEFAULT NULL," +
                BENEFICIARY_SATSFIED_NO_REASONS + " varchar(100) DEFAULT NULL," +
                BIOGAS_PLANT_APPROPRIATE + " varchar(30) DEFAULT NULL," +
                PURPOSE_OF_USAGE + " varchar(100) DEFAULT NULL," +
                LPG_NEW_CONNECTION + " varchar(30) DEFAULT NULL," +
                LPG_NEW_CONNECTION_DETAILS + " varchar(100) DEFAULT NULL," +
                SUBSEQUENT_CYLINDER_BOUGHT + " varchar(30) DEFAULT NULL," +
                SUBSEQUENT_CYLINDER_BOUGHT_REASONS + " varchar(100) DEFAULT NULL," +
                TYPE_OF_ROOF + " varchar(30) DEFAULT NULL," +
                OTHER_TYPE_OF_ROOF + " varchar(100) DEFAULT NULL," +
                PLINTH_AREA + " INTEGER(11) DEFAULT 0," +
                LIVESTOCK_TYPES + " varchar(100) DEFAULT NULL," +
                TOTAL_NO_OF_LIVESTOCK + " INTEGER(11) DEFAULT 0," +
                LIVESTOCK_COW_DETAILS + " varchar(100) DEFAULT NULL," +
                LIVESTOCK_BUFFALO_DETAILS + " varchar(100) DEFAULT NULL," +
                LIVESTOCK_SHEEP_DETAILS + " varchar(100) DEFAULT NULL," +
                LIVESTOCK_GOAT_DETAILS + " varchar(100) DEFAULT NULL," +
                LIVESTOCK_DONKEY_DETAILS + " varchar(100) DEFAULT NULL," +
                OTHER_LIVESTOCK_DETAILS + " varchar(100) DEFAULT NULL," +
                CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0," +
                SUGGESTIONS_EVALUATOR + " varchar(100) DEFAULT NULL," +
                SURVEY_NUMBERS_WHERE_PLANTED + " varchar(20) DEFAULT NULL," +
                SPECIES_SDP_BENEFICIARY + " varchar(500) DEFAULT NULL," +
                SPECIES_OTHER + " varchar(50) DEFAULT NULL," +
                TYPE_OF_PLANTING + " varchar(50) DEFAULT NULL," +
                TYPE_OF_PLANTING_MAINCROP + " varchar(50) DEFAULT NULL," +
                AVERAGE_SPACEMENT_METERS + " FLOAT DEFAULT 0," +
                PLANTING_IRRIGATION_LEVEL + " varchar(10) DEFAULT NULL," +
                PLANTING_IRRIGATION_METHOD + " varchar(30) DEFAULT NULL," +
                PLANTING_IRRIGATION_LEVEL_OTHER_DETAILS + " varchar(50) DEFAULT NULL," +
                PLANTING_FERTILIZE_USED + " varchar(10) DEFAULT NULL," +
                PLANTING_FERTILIZE_USED_DETAILS + " varchar(50) DEFAULT NULL," +
                PLANTING_PRUNINGE_DONE + " varchar(20) DEFAULT NULL," +
                PLANTING_OTHER_TREATMENT_DETAILS + " varchar(50) DEFAULT NULL," +
                PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_APPLICABLE + " varchar(10) DEFAULT NULL," +
                NUMBER_OF_SEEDLINGS_PLANTED + " INTEGER(11) DEFAULT 0," +
                SEEDLING_PERCENTAGE + " INTEGER(11) DEFAULT 0," +
                NUMBER_OF_SEEDLINGS_SURVIVING + " INTEGER(11) DEFAULT 0," +
                PLANTING_TOTAL_EXPENDITURE_UNTIL_NOW_RS + " INTEGER(11) DEFAULT 0," +
                MORTALITY_OF_SEEDLING + " varchar(100) DEFAULT NULL," +
                HOW_IT_WILL_BE_REDUCED + " varchar(100) DEFAULT NULL," +
                PAYMENT_RECEIVED_FROM_MGNREEGS_YEAR + " varchar(10) DEFAULT NULL," +
                PAYMENT_RECEIVED_FROM_MGNREEGS_RS + " INTEGER(11) DEFAULT 0," +
                SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_YEAR + " varchar(10) DEFAULT NULL," +
                SUBSIDY_FOR_MICRO_IRRIGATION_FROM_OTHER_DEPTS_RS + " INTEGER(11) DEFAULT 0," +
                TOTAL_INCENTIVE + " INTEGER(11) DEFAULT 0," +
                ARE_YOU_SATISFIED_WITH_SEEDLING_QUALITY + " varchar(10) DEFAULT NULL," +
                REASONS_FOR_DISSATISFACTION_WITH_SEEDLING_QUALITY + " varchar(100) DEFAULT NULL," +
                DID_YOU_BUY_SEEDLINGS_FROM_PVT_NURSERIES + " varchar(10) DEFAULT NULL," +
                DETAILS_OF_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES + " varchar(100) DEFAULT NULL," +
                REASONS_FOR_SEEDLINGS_PURCHASE_FROM_PVT_NURSERIES + " varchar(100) DEFAULT NULL," +
                INTERESTED_IN_BUYING_MORE_SEEDLINGS_FROM_KFD + " varchar(20) DEFAULT NULL," +
                USER_INTERESTED_SPECIES + " varchar(20) DEFAULT NULL," +
                DO_YOU_NEED_SPECIFIC_SUPPORT_FROM_GOVT + " varchar(20) DEFAULT NULL," +
                DETAILS_OF_SPECIFIC_SUPPORT_NEEDED_FROM_GOVT + " varchar(100) DEFAULT NULL," +
                PROGRAM_NAME + " varchar(50) DEFAULT NULL," +
                PROGRAM_NAME_OTHERS + " varchar(50) DEFAULT NULL," +
                YEAR_OF_IMPLEMENTATION + " varchar(15) DEFAULT NULL," +
                SUGGESTIONS_TO_IMPROVE_AGRO_FORESTRY + " varchar(100) DEFAULT NULL" +
                ")";
    }

    private String createSeedlingPerformaceV2() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_BENEFICIARY_SEEDLING + "  (" +
                SEEDLING_ID + " INTEGER PRIMARY KEY ," +
                FORM_ID + "  INTEGER(11) DEFAULT  0 ," +
                BENEFICIARY_ID + "  INTEGER(11) DEFAULT  0 ," +
                SPECIES_ID + "  INTEGER(11) DEFAULT  0 ," +
                PART_TYPE + "  varchar(100) DEFAULT NULL," +
                NAME_OF_THE_SPECIES + "  varchar(100) DEFAULT NULL," +
                NUMBER_OF_SEEDLINGS_PLANTED + "  INTEGER(11) DEFAULT  0 ," +
                NUMBER_OF_SEEDLINGS_SURVIVING + "  INTEGER(11) DEFAULT  0 ," +
                AVERAGE_COLLAR_GROWTH_CMS + "  float DEFAULT  0 ," +
                AVERAGE_HEIGHT_METERS + "  float DEFAULT  0 ," +
                HEALTH_AND_VIGOUR + "  varchar(20) DEFAULT NULL," +
                ECONOMICAL_VALUE + "  varchar(20) DEFAULT NULL," +
                DETAILS_OF_RETURNS + "  varchar(100) DEFAULT NULL," +
                CREATION_TIMESTAMP + " INTEGER(11) DEFAULT 0," +
                FORM_FILLED_STATUS + " varchar(100) DEFAULT NULL, " +
                REMARKS + " varchar(500) DEFAULT NULL) ";
    }

    public void saveBeneficiary(String tableBeneficiary, String beneficiaryId, ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(tableBeneficiary, null, cv);
        db.close();

    }
    public int getLastBenID() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT MAX(" + BENEFICIARY_ID + ") FROM " + TABLE_BENEFICIARY, null);
        return (cursor.moveToFirst() ? cursor.getInt(0) : 0);
    }

    public int getSurvey_id(String form_id, String space) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT MIN(" + SAMPLE_PLOT_ID + ") FROM " + TABLE_SAMPLE_PLOT_MASTER + " where " + FORM_ID + "= " + form_id + " AND "+ BLOCK_NUMBER_NEW + "= " + space, null);
        return (cursor.moveToFirst() ? cursor.getInt(0) : 0);
    }

    public String get_new_block_number(String survey_id , String block_number) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + BLOCK_NUMBER_NEW + " FROM " + TABLE_SAMPLE_PLOT_MASTER + " where " + SAMPLE_PLOT_ID + "= " + survey_id + " AND "+ BLOCK_NUMBER_NEW + "= " + block_number, null);
        return String.valueOf((cursor.moveToFirst() ? cursor.getInt(0) : 0));
    }
}