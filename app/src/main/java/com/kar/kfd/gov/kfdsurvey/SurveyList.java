package com.kar.kfd.gov.kfdsurvey;

import static com.kar.kfd.gov.kfdsurvey.advancework.AdvProtection.ADV_PROTECTION;
import static com.kar.kfd.gov.kfdsurvey.nursery.BaggedSeedlingAvailableAtNurserySurvey.BAGGED_SEEDLINGS_AT_NURSERY_SURVEY;
import static com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation.BASIC_INFORMATION;
import static com.kar.kfd.gov.kfdsurvey.plantation.SamplePlotSurvey.SAMPLE_PLOT_DETAILS;
import static com.kar.kfd.gov.kfdsurvey.sdp.SDPBeneficiarySurvey.OTHERS_IF_ANY_SPECIFY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.kar.kfd.gov.kfdsurvey.advancework.AdvProtection;
import com.kar.kfd.gov.kfdsurvey.advancework.AdvSamplePlotSurvey;
import com.kar.kfd.gov.kfdsurvey.advancework.smc.AddOtherAdvSMC;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.map.MapGps;
import com.kar.kfd.gov.kfdsurvey.nursery.AboutSeedBedSurvey;
import com.kar.kfd.gov.kfdsurvey.nursery.BaggedSeedlingAvailableAtNurserySurvey;
import com.kar.kfd.gov.kfdsurvey.plantation.AddSpecies;
import com.kar.kfd.gov.kfdsurvey.plantation.ControlPlotInventory;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;
import com.kar.kfd.gov.kfdsurvey.plantation.PlotInventory;
import com.kar.kfd.gov.kfdsurvey.plantation.Protection;
import com.kar.kfd.gov.kfdsurvey.plantation.SamplePlotSurvey;
import com.kar.kfd.gov.kfdsurvey.plantation.smc.AddOtherSMC;
import com.kar.kfd.gov.kfdsurvey.plantation.smc.SmcHighest;
import com.kar.kfd.gov.kfdsurvey.scptsp.SCPTSPBeneficiary;
import com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey;
import com.kar.kfd.gov.kfdsurvey.sdp.SDPBeneficiarySurvey;
import com.ngohung.form.el.store.HPrefDataStore;
import com.ngohung.form.util.GPSTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Modified by devansh
 */
@SuppressLint("Range")
public class SurveyList extends ListActivity implements View.OnClickListener {
    public Set<Integer> finishedPosition = new HashSet<>();
    public Set<Integer> speciesPosition = new HashSet<>();
    SurveyList mSurvey = this;
    public static final String folderName = "Plantation" + File.separator + "Evaluation details";

    Database db;
    String listType, inventoryType, partType, control_plot_type, spName, speciesName, formStatus = "0";
    GridView gridView;
    ListView listView;
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private int id, sampleplotId, formId, speciesCount = 0, samplePlots;
    private Button button, saveButton;
    private ArrayAdapter<String> adapter;
    static final String TAG = Constants.SARATH;
    private String mData;
    private List<LatLng> lat_lng;
    private LatLng mid_point;
    private double curr_lat, curr_lon, mid_lan, mid_lon;
    private double distance_check;
    private String no_of_perambulate;
    private SharedPreferences basicInfoPref;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_fragment);
        Bundle b = getIntent().getExtras();
        button = findViewById(R.id.list_button);
        saveButton = findViewById(R.id.saveButton);
        assert b != null;
        listType = b.getString("List-type", "");
        partType = b.getString(Database.PART_TYPE, "");
        inventoryType = b.getString("Inventory-type", "");
        control_plot_type = b.getString(Database.CONTROL_PLOT_TYPE, "");
        formStatus = b.getString("formStatus", "0");
        id = b.getInt("id", 0);//form id
        formId = b.getInt("formId", 0);
        sampleplotId = b.getInt("sampleplotId", 0);
        no_of_perambulate = b.getString("no_of_perambulate", "0");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(listType);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        gridView = findViewById(R.id.grid_view);
        listView = findViewById(android.R.id.list);
        saveButton.setOnClickListener(this);

        GPSTracker gpsTracker = new GPSTracker(this);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc != null) {
            curr_lon = loc.getLongitude();
            curr_lat = loc.getLatitude();
        }
        Log.e("dwqcsdc", "" + curr_lat + "\n" + curr_lon);

        if (partType.equals(ScpTspSamplingSurvey.COMMUNITY)) {
            toolbar.setTitle("Type Of Asset");
        } else if (partType.equals(ScpTspSamplingSurvey.INDIVIDUAL)) {
            toolbar.setTitle("Type of Benefit");
        }
        basicInfoPref = this.getApplicationContext().getSharedPreferences(BASIC_INFORMATION, Context
                .MODE_PRIVATE);
        try {
            samplePlots = Integer.parseInt(basicInfoPref.getString(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, "0"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (listType.equals(Constants.SPECIES_LIST)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), AddSpecies.class);
                getSharedPreferences(AddSpecies.SPECIES_PREF, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText(R.string.add_species);
            if (Integer.parseInt(basicInfoPref.getString(Database.SAMPLE_PLOT_STATUS, "0")) != 0) {
                button.setVisibility(View.INVISIBLE);
            }
        }

        if (listType.equals(Constants.BENEFICIARY_SPECIES_LIST)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), BeneficiaryAddSpecies.class);
                getSharedPreferences(AddSpecies.SPECIES_PREF, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add Species");
        }

        if (listType.equals(Constants.BENEFICIARY_LIST)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), SDPBeneficiarySurvey.class);
                getSharedPreferences(SDPBeneficiarySurvey.BENEFICIARY_DETAILS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                i.putExtra("from", "add");
                startActivity(i);
            });
            button.setText("Add Beneficiary to be Sampled");
            button.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }

        if (listType.equals(Constants.SCP_TSP_BENEFICIARY_LIST)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), SCPTSPBeneficiary.class);
                getSharedPreferences(SCPTSPBeneficiary.SCPTSP_BENEFICIARY_DETAILS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add SCP and TSP Beneficiary");
            button.setVisibility(View.INVISIBLE);
        }

        /*     if (listType.equals(Constants.SMC_WORKS_LIST)) {
         *//*listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);*//*
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), SmcWorks.class);
                getSharedPreferences(SmcWorks.SMC_WORKS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add SMC Work");
            //button.setVisibility(View.INVISIBLE);
        }*/

        if (listType.equals(Constants.OTHER_SMC_WORKS)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), AddOtherSMC.class);
                getSharedPreferences(AddOtherSMC.ADD_OTHER_SMC, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add Other SMC works");
            saveButton.setVisibility(View.VISIBLE);
        }
        if (listType.equals(Constants.ADV_OTHER_SMC_WORKS)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), AddOtherAdvSMC.class);
                getSharedPreferences(AddOtherAdvSMC.ADD_OTHER_ADV_SMC, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add Other SMC works");
            saveButton.setVisibility(View.VISIBLE);
        }

        if (listType.equals(Constants.SAMPLE_PLOT_LIST)) {
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            mData = basicInfoPref.getString(Database.GPS_LATLONG_COLLECTION, "");

            getMarkerData(mData);

            Log.e("asdcasdc", "" + listType);

            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), SamplePlotSurvey.class);
                getSharedPreferences(SAMPLE_PLOT_DETAILS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);

            });
            button.setText("Add Sample Plot");
            button.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);

        }
        if (listType.equals(Constants.NO_OF_BLOCK)) {
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
//            mData = basicInfoPref.getString(Database.GPS_LATLONG_COLLECTION, "");
//
//            getMarkerData(mData);

            Log.e("asdcasdc", "" + listType);

            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), MapGps.class);
                i.putExtra(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
                basicInfoPref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
                basicInfoPref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING).apply();
                basicInfoPref.edit().putString(Database.FOLDER_NAME, folderName).apply();
                startActivity(i);

            });
            button.setText("Add No of Block");
            button.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);

        }
        if (listType.equals(Constants.ADV_SAMPLE_PLOT_LIST)) {
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), AdvSamplePlotSurvey.class);
                getSharedPreferences(SAMPLE_PLOT_DETAILS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add Sample Plot");
            button.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);

        }

        if (listType.equals(Constants.LOCATION_TRACKER) || !formStatus.equals("0")) {
            button.setVisibility(View.GONE);
        }

        if (listType.equals(Constants.PLOT_INVENTORY_LIST)) {
            button.setOnClickListener(v -> {
                SharedPreferences pref = getSharedPreferences(PlotInventory.PLOT_INVENTORY, Context.MODE_PRIVATE);
                if (!pref.getString(Database.PART_TYPE, "").equals(inventoryType)) {
                    pref.edit().clear().apply();
                }
                pref.edit().putString(Database.PART_TYPE, inventoryType).apply();
                pref.edit().putString(Database.SAMPLE_PLOT_ID, String.valueOf(sampleplotId)).apply();
                pref.edit().putString(Database.FORM_ID, String.valueOf(id)).apply();

                speciesAlertDialog(button, sampleplotId, id);
            });
            button.setText("Add Species");
            saveButton.setVisibility(View.VISIBLE);
            if (inventoryType.equals(SamplePlotSurvey.SEEDLING))
                button.setVisibility(View.VISIBLE);
            else
                button.setVisibility(View.INVISIBLE);
        }


        if (listType.equals(Constants.CONTROL_PLOT_INVENTORY_LIST)) {
            button.setOnClickListener(v -> {
                SharedPreferences pref = getSharedPreferences(ControlPlotInventory.CONTROL_PLOT_INVENTORY, Context.MODE_PRIVATE);
                if (!pref.getString(Database.PART_TYPE, "").equals(inventoryType)) {
                    pref.edit().clear().apply();
                }
                pref.edit().putString(Database.PART_TYPE, inventoryType).apply();
                pref.edit().putString(Database.CONTROL_PLOT_TYPE, control_plot_type).apply();
                //  pref.edit().putString(Database.ANRMODEL_ID, String.valueOf(id)).apply();
                pref.edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                Intent i = new Intent(mSurvey.getApplicationContext(), ControlPlotInventory.class);
                startActivity(i);
            });
            saveButton.setVisibility(View.VISIBLE);
            button.setText("Add Control Plot Inventory");
        }

        if (listType.equals(Constants.SEEDLINGS_LIST)) {
            button.setOnClickListener(v -> {
                if (db.getNumberOfSeedlings(id) < 5) {
                    Intent i = new Intent(mSurvey.getApplicationContext(), SeedlingsSurvey.class);
                    getSharedPreferences(SeedlingsSurvey.SEEDLINGS_SURVEY, MODE_PRIVATE).edit().putString(Database.BENEFICIARY_ID, String.valueOf(id)).apply();
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "You have already added details of 5 seedlings", Toast.LENGTH_SHORT).show();
                }
            });
            button.setText("Add Seedling");
            button.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }

        if (listType.equals(Constants.INSPECT_TWO_SMC_WORKS)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), SmcHighest.class);
                getSharedPreferences(SmcHighest.INSPECT_TWO_SMC_WORK, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add Inspected SMC Work");
        }

        if (listType.equals(Constants.PLANT_PROTECTION)) {
            button.setOnClickListener(v -> {
                Intent intent = new Intent(mSurvey.getApplicationContext(), Protection.class);
                getSharedPreferences(Protection.PROTECTION_WORK_SURVEY, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(intent);
                finishedPosition.add(names.size());
                button.setText("Add Protection Type");
                saveButton.setVisibility(View.VISIBLE);
            });

        }
        if (listType.equals(Constants.ADV_PLANT_PROTECTION)) {
            button.setOnClickListener(v -> {
                Intent intent = new Intent(mSurvey.getApplicationContext(), AdvProtection.class);
                getSharedPreferences(ADV_PROTECTION, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(intent);
                finishedPosition.add(names.size());
                button.setText("Add Protection Type");
                saveButton.setVisibility(View.VISIBLE);
            });

        }

        if (listType.equals(Constants.BENEFIT_LIST)) {
            button.setVisibility(View.GONE);
        }
    }

    private void speciesAlertDialog(View view, int sampleplotId, int form_id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SurveyList.this);
        View customDialogLayout = this.getLayoutInflater().inflate(R.layout.dialog_upload_user_credentials, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(true);


        TextView header = customDialogLayout.findViewById(R.id.submitFormHeader);
        header.setText("Enter Species");
        TextView speciesLabel = customDialogLayout.findViewById(R.id.uploadUserIdLabel);
        speciesLabel.setText("Other Species Name");
        TextView passwordLabel = customDialogLayout.findViewById(R.id.uploadPwdLabel);
        passwordLabel.setVisibility(View.GONE);
        EditText speciesName = customDialogLayout.findViewById(R.id.uploadUserIdET);
        // speciesName.setHint("Enter Other Species Name");
        EditText password = customDialogLayout.findViewById(R.id.uploadPwdET);
        password.setVisibility(View.GONE);

        alertDialogBuilder.setPositiveButton("Submit", (dialog, which) -> {
            String spName = speciesName.getText().toString().trim();
            if (!spName.equals("")) {
                names.add(spName);
                String part_type = "Seedling";
                ContentValues cvSpecies = new ContentValues();
                cvSpecies.put(Database.FORM_ID, form_id);
                cvSpecies.put(Database.SAMPLE_PLOT_ID, sampleplotId);
                cvSpecies.put(Database.MAIN_SPECIES_PLANTED, spName);
                cvSpecies.put(Database.PART_TYPE, part_type);
                db.insertIntoSamplePlotSpecies(cvSpecies);
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        GPSTracker gpsTracker = new GPSTracker(this);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc != null) {
            curr_lon = loc.getLongitude();
            curr_lat = loc.getLatitude();
        }
        mData = basicInfoPref.getString(Database.GPS_LATLONG_COLLECTION, "");
        db = new Database(this);
        names = new ArrayList<>();
        ids = new ArrayList<>();
        speciesPosition.add(-1);

        adapter = new ArrayAdapter<String>(this, R.layout.custom_list_textview, names) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                switch (listType) {

                    case Constants.SAMPLE_PLOT_LIST:
                        view.setBackground(ContextCompat.getDrawable(SurveyList.this, R.drawable.box));

                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                            view.setBackground(ContextCompat.getDrawable(SurveyList.this, R.drawable.box));
                        }
                        break;
                    case Constants.NO_OF_BLOCK:
                        view.setBackground(ContextCompat.getDrawable(SurveyList.this, R.drawable.box));

                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                            view.setBackground(ContextCompat.getDrawable(SurveyList.this, R.drawable.box));
                        }
                        break;
                    case Constants.ADV_SAMPLE_PLOT_LIST:
                        view.setBackground(ContextCompat.getDrawable(SurveyList.this, R.drawable.box));

                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                            view.setBackground(ContextCompat.getDrawable(SurveyList.this, R.drawable.box));
                        }
                        break;

                    case Constants.PLOT_INVENTORY_LIST:

                        if (speciesPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;

                 /*   case Constants.BENEFICIARY_LIST:
                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;

                    case Constants.PLANT_PROTECTION:
                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;
                    case Constants.ADV_PLANT_PROTECTION:
                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;

                    case Constants.SEEDLINGS_LIST:
                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;
                    case Constants.SCP_TSP_BENEFICIARY_LIST:
                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;
                    case Constants.BENEFIT_LIST:
                        if (partType.equals(ScpTspSamplingSurvey.INDIVIDUAL)) {
                            if (finishedPosition.contains(position)) {
                                view.setBackgroundColor(Color.LTGRAY);
                            } else {
                                view.setBackgroundColor(Color.TRANSPARENT);
                            }
                        } else {
                            if (finishedPosition.contains(position)) {
                                view.setBackgroundColor(Color.LTGRAY);
                            } else {
                                view.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }

                        break;*/
                    default:
                        if (finishedPosition.contains(position)) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        break;
                }

                return view;


            }
        };


        if (listType.equals(Constants.BENEFICIARY_LIST)) {
            Cursor cursor = db.getBeneficiaries(id);
            finishedPosition.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(Database.NAME));
                    String fatherName = cursor.getString(cursor.getColumnIndex(Database.FATHER_NAME));
                    names.add("Name: " + name + " -  Father/Husband name: " + fatherName);
                    int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    if (pos != -1)
                        finishedPosition.add(pos);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.BENEFICIARY_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }
        if (listType.equals(Constants.NO_OF_BLOCK)) {
            Log.e("sdvsv", "" + id);
//            Log.e("sdvsv",""+cursor + "\n"+ id);


            for (int i = 1; i < Integer.parseInt(no_of_perambulate) + 1; i++) {
//                Cursor cursor = db.getKfdPlantationSamplingData(id);
//                if (cursor != null && cursor.moveToFirst()) {
//                    int j = 0;
//                    Log.e("ascadc",""+j);
//                    finishedPosition.clear();
//                    do {
//                        String block_type = "";
//                        if(j==0) {
//                            block_type =
//                                    cursor.getString(cursor.getColumnIndex(Database.BLOCK1_TYPE));
//                        }
//                        if(j==1) {
//                            block_type =
//                                    cursor.getString(cursor.getColumnIndex(Database.BLOCK2_TYPE));
//                        }
//                        if(j==2) {
//                            block_type =
//                                    cursor.getString(cursor.getColumnIndex(Database.BLOCK3_TYPE));
//                        }
//                        if(j==3) {
//                            block_type =
//                                    cursor.getString(cursor.getColumnIndex(Database.BLOCK4_TYPE));
//                        }
//                        if(j==4) {
//                            block_type =
//                                    cursor.getString(cursor.getColumnIndex(Database.BLOCK5_TYPE));
//                        }
//                        Log.e("adcasc",""+j);
////                        ids.add(block_type);
//                        ids.add(j);
//                        int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
//                        if (pos != -1)
//                            finishedPosition.add(pos);
//                        j++;
//                    } while (cursor.moveToNext());
//                }
//                assert cursor != null;
//                cursor.close();
                ids.add(i);
                Log.e("adcasc", "" + i);

                names.add("Block : " + i);
            }

        }


        if (listType.equals(Constants.BAGGED_SEEDING_LIST)) {
            Cursor cursor = db.getNurserWorkBaggedSeedlings(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String speciesName = cursor.getString(cursor.getColumnIndex(Database.MAIN_SPECIES_PLANTED));
                    String schemeName = cursor.getString(cursor.getColumnIndex(Database.NAME_OF_THE_SCHEME));
                    int pos = 0;
                    try {
                        pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (speciesName.equalsIgnoreCase("Others")) {
                        speciesName = cursor.getString(cursor.getColumnIndex(Database.OTHER_SPECIES));
                        names.add("Scheme: " + schemeName + " - Species: " + speciesName);
                    } else {
                        names.add("Scheme: " + schemeName + " - Species: " + speciesName);
                    }
                    if (pos != -1)
                        finishedPosition.add(pos);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.BAGGED_SEEDLING_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.ABOUT_SEED_BED_LIST)) {
            Cursor cursor = db.getNurserWorkSeedBeds(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String speciesName = cursor.getString(cursor.getColumnIndex(Database.NUMBER_OF_BEDS));
                    String schemeName = cursor.getString(cursor.getColumnIndex(Database.NAME_OF_THE_SCHEME));
                    String seedlingsPerBed = cursor.getString(cursor.getColumnIndex(Database.NUMBER_OF_BEDS));
                    names.add("Scheme Name: " + schemeName + "- Species Name: " + speciesName + " -Seedlings per beds: " + seedlingsPerBed);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.SEED_BED_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.LOCATION_TRACKER)) {
            Cursor cursor = db.getTrackedLocations(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String latitude = cursor.getString(cursor.getColumnIndex(Database.LAT));
                    String longitude = cursor.getString(cursor.getColumnIndex(Database.LONG));
                    names.add(("Latitude : " + latitude + ", Longitude : " + longitude));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.SCP_TSP_BENEFICIARY_LIST)) {
            finishedPosition.clear();
            Cursor cursor = db.getScpTspBeneficiaries(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(Database.BENEFICIARY_NAME));
                    String fatherName = cursor.getString(cursor.getColumnIndex(Database.BENEFICIARY_FATHER_NAME));
                    int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    if (pos != -1)
                        finishedPosition.add(pos);
                    names.add("Name: " + name + " - Father/Husband name: " + fatherName);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.BENIFICIARY_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.PLANT_PROTECTION)) {
            Cursor cursor = db.getProtection(id);

            finishedPosition.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String protection = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_PROTECTION));
                    names.add(protection);
                    int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    if (pos != -1)
                        finishedPosition.add(pos);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.PROTECTION_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), Protection.class);
                getSharedPreferences(Protection.PROTECTION_WORK_SURVEY, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                SharedPreferences pref = getSharedPreferences(Protection.PROTECTION_WORK_SURVEY, Context.MODE_PRIVATE);
                pref.edit().putInt(Database.FINISHED_POSITION, names.size()).apply();
                startActivity(i);
            });
            button.setText("Add Protection Type");
        }
        if (listType.equals(Constants.ADV_PLANT_PROTECTION)) {
            Cursor cursor = db.getAdvProtection(id);

            finishedPosition.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String protection = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_PROTECTION));
                    names.add(protection);
                    int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    if (pos != -1)
                        finishedPosition.add(pos);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.PROTECTION_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), AdvProtection.class);
                SharedPreferences pref = getSharedPreferences(ADV_PROTECTION, Context.MODE_PRIVATE);
                pref.edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                pref.edit().putInt(Database.FINISHED_POSITION, names.size()).apply();
                startActivity(i);
            });
            button.setText("Add Protection Type");
        }
        if (listType.equals(Constants.BAGGED_SEEDING_LIST)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), BaggedSeedlingAvailableAtNurserySurvey.class);
                SharedPreferences pref = getSharedPreferences(BAGGED_SEEDLINGS_AT_NURSERY_SURVEY, Context.MODE_PRIVATE);
                pref.edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                pref.edit().putInt(Database.FINISHED_POSITION, names.size()).apply();
                startActivity(i);
            });
            button.setText("Add Bagged Seedlings");
        }
        if (listType.equals(Constants.ABOUT_SEED_BED_LIST)) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(mSurvey.getApplicationContext(), AboutSeedBedSurvey.class);
                getSharedPreferences(AboutSeedBedSurvey.ABOUT_SEED_BED_SURVEY, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                startActivity(i);
            });
            button.setText("Add species Bed");
        }


        if (listType.equals(Constants.OTHER_SMC_WORKS)) {
            Cursor cursor = db.getOtherSmcWorks(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String smcwork = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                    String scheme = cursor.getString(cursor.getColumnIndex(Database.SCHEME_NAME));
                    names.add("SMC Work: " + smcwork + " Scheme:" + scheme);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.OTHER_SMC_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }
        if (listType.equals(Constants.ADV_OTHER_SMC_WORKS)) {
            Cursor cursor = db.getAdvOtherSmcWorks(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String smcwork = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                    String scheme = cursor.getString(cursor.getColumnIndex(Database.SCHEME_NAME));
                    names.add("SMC Work: " + smcwork + " Scheme:" + scheme);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.OTHER_SMC_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }
        if (listType.equals(Constants.BENEFIT_LIST)) {
            Cursor cursor = db.getSCPTSPBenefitData(id, partType);
            if (cursor != null && cursor.moveToFirst()) {
                finishedPosition.clear();
                do {
                    String typeofBenefit;
                    if (partType.equals(ScpTspSamplingSurvey.INDIVIDUAL)) {
                        typeofBenefit = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_BENEFIT));
                    } else {
                        typeofBenefit = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_ASSET));
                    }
                    names.add(typeofBenefit);
                    int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    if (pos != -1)
                        finishedPosition.add(pos);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.BENEFIT_ID)));
                } while (cursor.moveToNext());
            }

            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.SAMPLE_PLOT_LIST)) {
            Cursor cursor = db.getSamplePlots(id);
            if (cursor != null && cursor.moveToFirst()) {
                int i = 1;
                finishedPosition.clear();
                do {

                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.SAMPLE_PLOT_ID)));
                    int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    if (pos != -1)
                        finishedPosition.add(pos);
                    names.add("Sample plot " + i);
                    i++;
                } while (cursor.moveToNext());
            }
            assert cursor != null;
            cursor.close();

          /*  if (ids.size() == samplePlots){
                button.setEnabled(false);
                button.setBackgroundColor(getResources().getColor(R.color.disabled_color));
            }*/


            //    names.clear();

         /*   for (int i = 1; i <= samplePlots; i++) {
                names.add("Sample plot " + i);
            }*/


        }

        if (listType.equals(Constants.ADV_SAMPLE_PLOT_LIST)) {
            Cursor cursor = db.getAdvSamplePlots(id);
            if (cursor != null && cursor.moveToFirst()) {
                int i = 1;
                finishedPosition.clear();
                do {

                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.SAMPLE_PLOT_ID)));
                    int pos = cursor.getInt(cursor.getColumnIndex(Database.FINISHED_POSITION));
                    if (pos != -1)
                        finishedPosition.add(pos);
                    names.add("Sample plot " + i);
                    i++;
                } while (cursor.moveToNext());
            }
            assert cursor != null;
            cursor.close();

        }

        if (listType.equals(Constants.PLOT_INVENTORY_LIST)) {

            Cursor cursor = db.getPlotInventory(sampleplotId, inventoryType);

            if (cursor != null && cursor.moveToFirst()) {
                int i = 0;
                do {
                    spName = cursor.getString(cursor.getColumnIndex(Database.SPECIES_NAME));
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.INVENTORY_ID)));
                    speciesPosition.add(i);
                    i++;
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
            Cursor cursorSpecies = db.getSpeciesInventory(id, partType);
            if (cursorSpecies != null && cursorSpecies.moveToFirst()) {

                do {
                    String speciesName = cursorSpecies.getString(cursorSpecies.getColumnIndex(Database.MAIN_SPECIES_PLANTED));
                    if (speciesName.equalsIgnoreCase("Others"))
                        speciesName = cursorSpecies.getString(cursorSpecies.getColumnIndex(Database.OTHER_SPECIES));
                    names.add(speciesName);
                } while (cursorSpecies.moveToNext());
            }
            Objects.requireNonNull(cursorSpecies).close();


        }


        if (listType.equals(Constants.SPECIES_LIST)) {
            Cursor cursor = db.getPlantSpecies(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String speciesName = cursor.getString(cursor.getColumnIndex(Database.MAIN_SPECIES_PLANTED));
                    int totalSpecies = cursor.getInt(cursor.getColumnIndex(Database.TOTAL_SPECIES_COUNT));
                    String stateOfHealth = cursor.getString(cursor.getColumnIndex(Database.SPECIES_SIZE));
                    if (speciesName.equalsIgnoreCase("Others")) {
                        String otherSpecies = cursor.getString(cursor.getColumnIndex(Database.OTHER_SPECIES));
                        names.add("Species: " + otherSpecies + " -  Polybag size: " + stateOfHealth);
                    } else
                        names.add("Species: " + speciesName + " -  Polybag size: " + stateOfHealth);
                    speciesCount += totalSpecies;
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.INVENTORY_ID)));
                } while (cursor.moveToNext());

                if (names.size() == cursor.getCount()) {
                    // names.add("Total no of seedling: " + speciesCount + "| Total Species: " + names.size());
                    speciesCount = 0;
                }
            }
            Objects.requireNonNull(cursor).close();

        }

        if (listType.equals(Constants.BENEFICIARY_SPECIES_LIST)) {
            //    Cursor cursor = db.getSpeciesInventory(id,inventoryType);
            Cursor cursor = db.getBeneficiarySpeciesInventory(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String speciesName = cursor.getString(cursor.getColumnIndex(Database.MAIN_SPECIES_PLANTED));
                    int totalSpecies = cursor.getInt(cursor.getColumnIndex(Database.TOTAL_SPECIES_COUNT));
                    String stateOfHealth = cursor.getString(cursor.getColumnIndex(Database.SPECIES_SIZE));
                    //  names.add(speciesName+" "+totalSpecies+" "+stateOfHealth);
                    names.add("Species: " + speciesName + " |Cost of seedling: " + totalSpecies + " |Polybag size: " + stateOfHealth);
                    speciesCount += totalSpecies;
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.INVENTORY_ID)));
                } while (cursor.moveToNext());

                if (names.size() == cursor.getCount()) {
                    names.add("Total cost of seedling: " + speciesCount + "| Total Species: " + names.size());
                    speciesCount = 0;
                }
            }
            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.CONTROL_PLOT_INVENTORY_LIST)) {
            Cursor cursor = db.getControlPlotInventory(id, inventoryType, control_plot_type);
            if (cursor != null && cursor.moveToNext()) {
                do {
                    String speciesName = cursor.getString(cursor.getColumnIndex(Database.SPECIES_NAME));
                    int totalSpecies = cursor.getInt(cursor.getColumnIndex(Database.TOTAL_COUNT));
                    names.add("Species: " + speciesName + " |Total Species: " + totalSpecies);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.INVENTORY_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.SEEDLINGS_LIST)) {
            Cursor cursor = db.getBeneficiarySeedlings(id, partType);
            if (cursor != null && cursor.moveToFirst()) {
                int i = 0;
                finishedPosition.clear();
                do {
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.SEEDLING_ID)));
                    finishedPosition.add(i);
                    i++;
                } while (cursor.moveToNext());
            }
            SharedPreferences preferences;
            if (partType.equalsIgnoreCase(Constants.FORMTYPE_SCPTSP)) {
                preferences = getApplicationContext().getSharedPreferences(SCPTSPBeneficiary.SCPTSP_BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
            } else {
                preferences = getApplicationContext().getSharedPreferences(SDPBeneficiarySurvey.BENEFICIARY_DETAILS, Context.MODE_PRIVATE);
            }
            if (preferences != null)
                getSpeciesNames(preferences);
            if (finishedPosition.size() == names.size()) {
                preferences.edit().putString(Database.SEEDLING_DETAIL_STATUS, "1").apply();
            }
            Objects.requireNonNull(cursor).close();
        }

        if (listType.equals(Constants.INSPECT_TWO_SMC_WORKS)) {
            Cursor cursor = db.getInspectedSmcWorks(id);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String smcStructureType = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                    float smcStructureVolume = cursor.getFloat(cursor.getColumnIndex(Database.SMC_STRUCTURE_TOTALVOLUME));
                    String smcConstrQuality = cursor.getString(cursor.getColumnIndex(Database.CONSTRUCTION_QUALITY));
                    names.add("Structure: " + smcStructureType + " |Volume: " + smcStructureVolume + " |Quality: " + smcConstrQuality);
                    ids.add(cursor.getInt(cursor.getColumnIndex(Database.SMC_WORK_ID)));
                } while (cursor.moveToNext());
            }
            Objects.requireNonNull(cursor).close();
            if (ids.size() == 2) {
                button.setEnabled(false);
                button.setBackgroundColor(getResources().getColor(R.color.disabled_color));
            }
        }


        setListAdapter(adapter);
        gridView.setAdapter(adapter);


        finishedPosition.add(-1);
        gridView.setOnItemClickListener((parent, view, position, id1) -> {
            if (listType.equals(Constants.SAMPLE_PLOT_LIST) || listType.equals(Constants.ADV_SAMPLE_PLOT_LIST)
                    || listType.equals(Constants.NO_OF_BLOCK)) {
                populatePreferences(position);
             /*   boolean isPrevFined = finishedPosition.contains(position - 1);
                if (!isPrevFined) {
                    showAlertDialog("Complete Sample Plot by Sequence");
                    return;
                } else
                if (finishedPosition.contains(position)) {
                    populatePreferences(position);
                } else {
                    Intent i = new Intent(mSurvey.getApplicationContext(), AdvSamplePlotSurvey.class);
                    i.putExtra("position", position);
                    getSharedPreferences(ADV_SAMPLE_PLOT_DETAILS, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(id)).apply();
                    startActivity(i);
                }*/

            }/* else if (listType.equals(Constants.SMC_WORKS_LIST)) {
                Intent i = new Intent(mSurvey.getApplicationContext(), SmcWorks.class);
                startActivity(i);
            }*/
        });
    }

    private String getSpeciesNames(SharedPreferences pref) {
        return removeOtherSpeciesString(pref);
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
                names.add(s);
            }
        }

        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id1) {
        super.onListItemClick(l, v, position, id1);
        Object o = this.getListAdapter().getItem(position);
        speciesName = o.toString();
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        /*To get speciesId*/
//        Cursor cursor = db.getBeneficiary(id);
        if (listType.equals(Constants.PLOT_INVENTORY_LIST)) {
            pref = getSharedPreferences(PlotInventory.PLOT_INVENTORY, Context.MODE_PRIVATE);
            editor = pref.edit();
            SharedPreferences preferences = getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
            String formId = preferences.getString(Database.FORM_ID, "0");


            boolean isPrevFinished = speciesPosition.contains(position - 1);
            if (!isPrevFinished) {
                showAlertDialog("Complete Species by Sequence");
                return;
            } else if (speciesPosition.contains(position)) {
                populatePreferences(position);
            } else {
                if (!pref.getString(Database.PART_TYPE, "").equals(inventoryType)) {
                    pref.edit().clear().apply();
                }
                editor.putString(Database.PART_TYPE, inventoryType);
                editor.putString(Database.SAMPLE_PLOT_ID, String.valueOf(sampleplotId));
                editor.putString(Database.FORM_ID, formId);
                editor.putInt("speciesPosition", position);
                editor.putString(Database.SPECIES_NAME, speciesName);
                editor.putInt(Database.SPECIES_ID, db.getMasterSpeciesId(speciesName));
                editor.apply();
                Intent i = new Intent(this, PlotInventory.class);
                startActivity(i);
            }

        } else if (listType.equals(Constants.SEEDLINGS_LIST)) {
            pref = getSharedPreferences(SeedlingsSurvey.SEEDLINGS_SURVEY, MODE_PRIVATE);
            editor = pref.edit();
            editor.putString(Database.PART_TYPE, partType);
            editor.putString(Database.NAME_OF_THE_SPECIES, speciesName);
            editor.putInt(Database.SPECIES_ID, db.getSdpSpeciesId(speciesName));
            editor.apply();
            boolean isPrevFinished = finishedPosition.contains(position - 1);
            if (!isPrevFinished) {
                showAlertDialog("Complete Species by Sequence");
                return;
            } else if (finishedPosition.contains(position)) {
                populatePreferences(position);
            } else {
                Intent sl = new Intent(mSurvey.getApplicationContext(), SeedlingsSurvey.class);
                getSharedPreferences(SeedlingsSurvey.SEEDLINGS_SURVEY, MODE_PRIVATE).edit().putString(Database.BENEFICIARY_ID, String.valueOf(id)).apply();
                getSharedPreferences(SeedlingsSurvey.SEEDLINGS_SURVEY, MODE_PRIVATE).edit().putString(Database.FORM_ID, String.valueOf(formId)).apply();
                startActivity(sl);
            }

        } else if (!listType.equals(Constants.LOCATION_TRACKER)) {
            populatePreferences(position);

        }
    }

    private void populatePreferences(int position) {
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        int id = 0;
        Log.e("dsdvsdv", "" + ids);
        if (ids.size() >= position) {
            try {
                id = ids.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        Cursor cursor;
        switch (listType) {

            case Constants.BENEFICIARY_LIST:
                cursor = db.getBeneficiary(id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences("BeneficiaryDetails", Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.putInt(Database.FINISHED_POSITION, position);
                    editor.apply();
                    FormList.getOtherSpecies(pref, Database.SPECIES, db.getNamesOfSdpSpecies());
                    Intent i = new Intent(getApplicationContext(), SDPBeneficiarySurvey.class);
                    startActivity(i);
                }
                break;

            case Constants.SEEDLINGS_LIST:

                cursor = db.getSeedlingforSeedlingId(id, partType);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences("SeedlingsSurvey", Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString(Database.NAME_OF_THE_SPECIES, speciesName);
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), SeedlingsSurvey.class);
                    startActivity(i);
                }
                break;

            case Constants.PLANT_PROTECTION:
                cursor = db.getSingleProtection(id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences(Protection.PROTECTION_WORK_SURVEY, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.putInt(Database.FINISHED_POSITION, position);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), Protection.class);
                    startActivity(i);
                }
                break;
            case Constants.ADV_PLANT_PROTECTION:
                cursor = db.getSingleAdvProtection(id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences(ADV_PROTECTION, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.putInt(Database.FINISHED_POSITION, position);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), AdvProtection.class);
                    startActivity(i);
                }
                break;
            case Constants.SCP_TSP_BENEFICIARY_LIST:

                cursor = db.getTableForId(Database.TABLE_SCP_TSP_BENIFICIARY, Database.BENIFICIARY_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences("SCPTSPBeneficiaryDetails", Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString(Database.BENEFICIARY_ID, String.valueOf(id));
                    editor.putString("formStatus", formStatus);
                    editor.putInt(Database.FINISHED_POSITION, position);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), SCPTSPBeneficiary.class);
                    startActivity(i);
                }
                break;

            case Constants.ABOUT_SEED_BED_LIST:

                cursor = db.getTableForId(Database.KFD_NURSERY_WORKS_SEED_BED, Database.SEED_BED_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences("AboutSeedBedSurvey", Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), AboutSeedBedSurvey.class);
                    startActivity(i);
                }
                break;

         /*   case Constants.SMC_WORKS_LIST:



                cursor = db.getTableForId(Database.TABLE_SMC_LIST, Database.SMC_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences("smc_works", Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), SmcWorks.class);
                    startActivity(i);
                }
                break;*/

            case Constants.OTHER_SMC_WORKS:
                cursor = db.getTableForId(Database.TABLE_OTHER_SMC_LIST, Database.OTHER_SMC_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences(AddOtherSMC.ADD_OTHER_SMC, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), AddOtherSMC.class);
                    startActivity(i);
                }
                break;
            case Constants.ADV_OTHER_SMC_WORKS:
                cursor = db.getTableForId(Database.TABLE_ADV_OTHER_SMC_LIST, Database.OTHER_SMC_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences(AddOtherAdvSMC.ADD_OTHER_ADV_SMC, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), AddOtherAdvSMC.class);
                    startActivity(i);
                }
                break;
            case Constants.SAMPLE_PLOT_LIST:
                cursor = db.getTableForId(Database.TABLE_SAMPLE_PLOT_MASTER, Database.SAMPLE_PLOT_ID, id);
                String does_plantation_has_multiple_block = basicInfoPref.getString(Database.DOES_PLANTATION_HAS_MULTIPLE_BLOCK,"0");
                String value_block;
                Log.e("hdasvhds",""+distance_check);
                if (cursor != null && cursor.moveToFirst()) {
                    if (distance_check <= 400000) {
                        value_block = cursor.getString(cursor.getColumnIndex(Database.BLOCK_NUMBER_NEW));
                        pref = getSharedPreferences("SamplePlotDetails", Context.MODE_PRIVATE);
                        pref.edit().clear().apply();
                        editor = pref.edit();
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            editor.putString(cursor.getColumnName(i), cursor.getString(i));
                        }
                        editor.putString("formStatus", formStatus);
                        editor.putInt(Database.FINISHED_POSITION, position);
                        editor.apply();
                        if(!value_block.equalsIgnoreCase("0")) {
                            Intent i = new Intent(getApplicationContext(), SamplePlotSurvey.class);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(SurveyList.this, "Perambulation not done for this sample plot , Please complete it first", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SurveyList.this, "You are outside geofence area" + distance_check, Toast.LENGTH_SHORT).show();
//                        pref = getSharedPreferences("SamplePlotDetails", Context.MODE_PRIVATE);
//                        pref.edit().clear().apply();
//                        editor = pref.edit();
//                        for (int i = 0; i < cursor.getColumnCount(); i++) {
//                            editor.putString(cursor.getColumnName(i), cursor.getString(i));
//                        }
//                        editor.putString("formStatus", formStatus);
//                        editor.putInt(Database.FINISHED_POSITION, position);
//                        editor.apply();
//                        Intent i = new Intent(getApplicationContext(), SamplePlotSurvey.class);
//                        startActivity(i);
                    }
                }
                break;
            case Constants.NO_OF_BLOCK:
                pref = this.getApplicationContext().getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
                HPrefDataStore store = new HPrefDataStore(pref);
                String block_type = null, block_area = null, block_no = "";
                Log.e("sdvsvd", "" + id);
                if (id == 1) {
                    block_type =
                            store.getPref().getString(Database.BLOCK1_TYPE, "1");
                    block_area =
                            store.getPref().getString(Database.BLOCK1_AREA, "1");
                    block_no = "1";
                    get_intent(block_type, block_area, block_no);
                }
                if (id == 2) {
                    block_type =
                            store.getPref().getString(Database.BLOCK2_TYPE, "1");
                    block_area =
                            store.getPref().getString(Database.BLOCK2_AREA, "1");
                    block_no = "2";
                    String sample_plot_coll_one = store.getPref().getString(Database.GPS_SAMPLEPLOT_COLLECTION, "");
                    if (sample_plot_coll_one.equalsIgnoreCase("")) {
                        Toast.makeText(SurveyList.this, "Please Perambulate Block 1", Toast.LENGTH_SHORT).show();
                    } else {
                        get_intent(block_type, block_area, block_no);
                    }
                }
                if (id == 3) {
                    block_type =
                            store.getPref().getString(Database.BLOCK3_TYPE, "1");
                    block_area =
                            store.getPref().getString(Database.BLOCK3_AREA, "1");
                    block_no = "3";
                    String sample_plot_coll_two = store.getPref().getString(Database.GPS_SAMPLEPLOT_COLLECTION_TWO, "");
                    if (sample_plot_coll_two.equalsIgnoreCase("")) {
                        Toast.makeText(SurveyList.this, "Please Perambulate Block 2", Toast.LENGTH_SHORT).show();
                    } else {
                        get_intent(block_type, block_area, block_no);
                    }
                }
                if (id == 4) {
                    block_type =
                            store.getPref().getString(Database.BLOCK4_TYPE, "1");
                    block_area =
                            store.getPref().getString(Database.BLOCK4_AREA, "1");
                    block_no = "4";
                    String sample_plot_coll_three = store.getPref().getString(Database.GPS_SAMPLEPLOT_COLLECTION_THREE, "");
                    if (sample_plot_coll_three.equalsIgnoreCase("")) {
                        Toast.makeText(SurveyList.this, "Please Perambulate Block 3", Toast.LENGTH_SHORT).show();
                    } else {
                        get_intent(block_type, block_area, block_no);
                    }
                }
                if (id == 5) {
                    block_type =
                            store.getPref().getString(Database.BLOCK5_TYPE, "1");
                    block_area =
                            store.getPref().getString(Database.BLOCK1_AREA, "1");
                    block_no = "5";
                    String sample_plot_coll_four = store.getPref().getString(Database.GPS_SAMPLEPLOT_COLLECTION_FOUR, "");
                    if (sample_plot_coll_four.equalsIgnoreCase("")) {
                        Toast.makeText(SurveyList.this, "Please Perambulate Block 4", Toast.LENGTH_SHORT).show();
                    } else {
                        get_intent(block_type, block_area, block_no);
                    }
                }

                break;
            case Constants.ADV_SAMPLE_PLOT_LIST:
                cursor = db.getTableForId(Database.TABLE_ADV_SAMPLE_PLOT_MASTER, Database.SAMPLE_PLOT_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences(AdvSamplePlotSurvey.ADV_SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.putInt(Database.FINISHED_POSITION, position);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), AdvSamplePlotSurvey.class);
                    startActivity(i);
                }
                break;

            case Constants.PLOT_INVENTORY_LIST:
                cursor = db.getTableForId(Database.TABLE_SAMPLE_PLOT_INVENTORY, Database.INVENTORY_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    SharedPreferences preferences = getSharedPreferences(BASIC_INFORMATION, Context.MODE_PRIVATE);
                    String formId = preferences.getString(Database.FORM_ID, "0");
                    pref = getSharedPreferences(PlotInventory.PLOT_INVENTORY, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                  /*  editor.putString(Database.PART_TYPE, inventoryType);
                    editor.putString(Database.INVENTORY_ID, String.valueOf(id));
                    editor.putString(Database.FORM_ID, formId);*/
                    editor.putString(Database.SPECIES_NAME, speciesName);
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), PlotInventory.class);
                    startActivity(i);
                }

                break;

            case Constants.SPECIES_LIST:

                cursor = db.getTableForId(Database.TABLE_ADD_SPECIES, Database.INVENTORY_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences(AddSpecies.SPECIES_PREF, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString(Database.SAMPLE_PLOT_ID, String.valueOf(position + 1));
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), AddSpecies.class);
                    startActivity(i);
                }
                break;

            case Constants.BENEFICIARY_SPECIES_LIST:
                cursor = db.getTableForId(Database.TABLE_ADD_BENEFICIARY_SPECIES, Database.INVENTORY_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences(AddSpecies.SPECIES_PREF, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), BeneficiaryAddSpecies.class);
                    startActivity(i);
                }
                break;

            case Constants.BENEFIT_LIST:
                int benformId = Integer.parseInt(getSharedPreferences(ScpTspSamplingSurvey.SCP_TSP_SAMPLING, Context.MODE_PRIVATE).getString(Database.FORM_ID, "0"));
                if (partType.equals(ScpTspSamplingSurvey.COMMUNITY)) {
                    cursor = db.getLeftJoinSCPTSP(benformId, ScpTspSamplingSurvey.COMMUNITY, id);
                } else {
                    cursor = db.getLeftJoinSCPTSP(benformId, ScpTspSamplingSurvey.INDIVIDUAL, id);
                }
                pref = getSharedPreferences(ScpTspSamplingSurvey.SCP_TSP_SAMPLING_SURVEY, Context.MODE_PRIVATE);
                if (cursor != null && cursor.moveToFirst()) {

                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.putInt(Database.FINISHED_POSITION, position);
                    editor.apply();
                    Intent benList = new Intent(getApplicationContext(), ScpTspSamplingSurvey.class);
                    startActivity(benList);
                }

                break;

            case Constants.CONTROL_PLOT_INVENTORY_LIST:
                cursor = db.getTableForId(Database.TABLE_CONTROL_PLOT_INVENTORY, Database.INVENTORY_ID, id);
                if (cursor != null && cursor.moveToFirst()) {

                    pref = getSharedPreferences(ControlPlotInventory.CONTROL_PLOT_INVENTORY, Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);

                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), ControlPlotInventory.class);
                    startActivity(i);
                }
                break;

            case Constants.INSPECT_TWO_SMC_WORKS:
                cursor = db.getTableForId(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.SMC_WORK_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences("AdvSmcHighest", Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), SmcHighest.class);
                    startActivity(i);
                }
                break;

            case Constants.BAGGED_SEEDING_LIST:
                cursor = db.getTableForId(Database.KFD_NURSERY_WORKS_BAGGED_SEEDLINGS, Database.BAGGED_SEEDLING_ID, id);
                if (cursor != null && cursor.moveToFirst()) {
                    pref = getSharedPreferences("BaggedSeedlingsAtNurserySurvey", Context.MODE_PRIVATE);
                    pref.edit().clear().apply();
                    editor = pref.edit();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        editor.putString(cursor.getColumnName(i), cursor.getString(i));
                    }
                    editor.putString("formStatus", formStatus);
                    editor.putInt(Database.FINISHED_POSITION, position);
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), BaggedSeedlingAvailableAtNurserySurvey.class);
                    startActivity(i);
                }
                break;


        }

    }

    private void get_intent(String block_type, String block_area, String block_no) {
        Intent intent = new Intent(mSurvey.getApplicationContext(), MapGps.class);
        intent.putExtra(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
        intent.putExtra("block_type", block_type);
        intent.putExtra("block_area", block_area);
        intent.putExtra("block_no", block_no);
        basicInfoPref.edit().putString(Database.DRAW_MAP_STATUS, "1").apply();
        basicInfoPref.edit().putString(Database.FORM_TYPE, Constants.FORMTYPE_PLANTSAMPLING).apply();
        basicInfoPref.edit().putString(Database.FOLDER_NAME, folderName).apply();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (formStatus.equals("0")) {
            switch (listType) {

                case Constants.PLOT_INVENTORY_LIST:
                    if (speciesPosition.size() == names.size() + 1) {
                        super.onBackPressed();
                    } else {
                        String message = "Complete All the Species and Save Button";
                        showAlertDialog(message);
                    }
                    break;
                case Constants.SAMPLE_PLOT_LIST:
                    if (finishedPosition.size() == names.size() + 1) {
                        super.onBackPressed();
                    } else {
                        String message = "Complete all the sample plot survey , you are no able to filled data outside sample plot area.";
                        showAlertDialog(message);
                    }
                    break;
               /* case Constants.BENEFICIARY_LIST:
                    if (finishedPosition.size() == names.size() + 1) {
                        super.onBackPressed();
                    } else {
                        String message = "Complete All the Beneficiaries List";
                        showAlertDialog(message);
                    }
                    break;*/

               /* case Constants.SCP_TSP_BENEFICIARY_LIST:
                    if (finishedPosition.size() == names.size() + 1) {
                        super.onBackPressed();
                    } else {
                        String message = "Complete All the Beneficiaries List";
                        showAlertDialog(message);
                    }
                    break;*/

              /*  case Constants.BENEFIT_LIST:
                    if (finishedPosition.size() == names.size() + 1) {
                        super.onBackPressed();
                    } else {
                        if (partType.equals(ScpTspSamplingSurvey.COMMUNITY)) {
                            String message = " Complete all the Asset List";
                            showAlertDialog(message);
                        } else if (partType.equals(ScpTspSamplingSurvey.INDIVIDUAL)) {
                            String message = " Complete all the Benefit List";
                            showAlertDialog(message);
                        }
                    }
                    break;*/

           /* case Constants.SEEDLINGS_LIST:
                if (finishedPosition.size() == names.size()+1){
                    super.onBackPressed();
                }else {
                    String message="Complete All the Species and Save Button";
                    showAlertDialog(message);
                }*/

                default:
                    super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alertBuilder.setNegativeButton("Exit", ((dialogInterface, i) -> finish()));
        alertBuilder.show();
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.saveButton) {
            onBackPressed();
        }

    }

    public void getMarkerData(String data) {
        ArrayList<LatLng> latLngs1 = new ArrayList<>();

        String[] latlng = data.split("\\|");
        if (latlng.length != 0) {

            for (String l : latlng) {
                String[] latLags = l.split(",");

                double lat = Double.parseDouble(latLags[0]);
                double lng = Double.parseDouble(latLags[1]);
                Log.e("asxasdc", "" + lat + "\n" + lng);
                LatLng latLng = new LatLng(
                        lat, lng);

                latLngs1.add(latLng);
            }
            computeCentroid(latLngs1);
        }
    }

    private void computeCentroid(List<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }
        mid_lan = latitude / n;
        mid_lon = longitude / n;

        distance_check = distance(curr_lat, curr_lon, mid_lan, mid_lon);
        Log.e("sadcsa", "" + distance_check);

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000f; // Radius of the earth in m
        double dLat = (lat1 - lat2) * Math.PI / 180f;
        double dLon = (lon1 - lon2) * Math.PI / 180f;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(curr_lat * Math.PI / 180f) * Math.cos(mid_lan * Math.PI / 180f) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2f * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        Log.e("zadsadv", "" + d);
        return d;
    }
    @Override
    public void onResume() {
        super.onResume();
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc != null) {
            curr_lon = loc.getLongitude();
            curr_lat = loc.getLatitude();
        }
        mData = basicInfoPref.getString(Database.GPS_LATLONG_COLLECTION, "");
//        getMarkerData(mData);
    }

}
