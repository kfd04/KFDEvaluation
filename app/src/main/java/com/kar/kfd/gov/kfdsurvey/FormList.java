package com.kar.kfd.gov.kfdsurvey;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.kar.kfd.gov.kfdsurvey.advancework.PlantSamplingAdvaceWork;
import com.kar.kfd.gov.kfdsurvey.advancework.PlantationSamplingAdvanceWork;
import com.kar.kfd.gov.kfdsurvey.advancework.VfcAdvacneWork;
import com.kar.kfd.gov.kfdsurvey.advancework.smc.SmcAdvanceWork;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.nursery.NurseryWorkSurvey;
import com.kar.kfd.gov.kfdsurvey.otherworks.OtherSurvey;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantSampling;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;
import com.kar.kfd.gov.kfdsurvey.plantation.VfcPlantationSampling;
import com.kar.kfd.gov.kfdsurvey.plantation.smc.SmcPlantationSampling;
import com.kar.kfd.gov.kfdsurvey.scptsp.SCPTSPType;
import com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey;
import com.kar.kfd.gov.kfdsurvey.sdp.SDPSamplingSurvey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Modified by Sarath on 2/23/2016.
 */
@SuppressLint("Range")
public class FormList extends ListActivity {
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private String formType, title;
    private String formStatus = "0";
    private FormList mSurvey = this;
    private Database db;
    private Button button, saveButton;
    private ArrayAdapter adapter;
    public Set<Integer> finishedPosition = new HashSet<>();
    private SweetAlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_fragment);
        formType = Objects.requireNonNull(getIntent().getExtras()).getString("formType", Constants.FORMTYPE_OTHERWORKS);
        formStatus = getIntent().getExtras().getString("formStatus", "0");
        // title = getIntent().getExtras().getString("title",Constants.UPLOAD_TITLE);
        title = getIntent().getExtras().getString("title");
        Bundle b = getIntent().getExtras();
        if (b != null) {
            title = b.getString("title");
        }
        button = findViewById(R.id.list_button);
        button.setVisibility(View.GONE);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setVisibility(View.GONE);
        TextView msgText = findViewById(R.id.msgText);
        msgText.setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);

        try {
            if (!title.equals(""))
                toolbar.setTitle(title);
            else
                toolbar.setTitle("Saved forms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

    }

    public static void getOtherSpecies(SharedPreferences pref, String column, String nameList) {
        List<String> listedSpecies = Arrays.asList(nameList.split("\\|"));
        String[] speciesNames = pref.getString(column, "").split("\\|");
        StringBuilder species = new StringBuilder();
        StringBuilder otherSpecies = new StringBuilder();
        for (String name : speciesNames) {
            if (listedSpecies.contains(name)) {
                if (species.toString().equals("")) {
                    species = new StringBuilder(name);
                } else {
                    species.append("|").append(name);
                }
            } else {
                if (otherSpecies.toString().equals("")) {
                    otherSpecies = new StringBuilder(name);
                } else {
                    otherSpecies.append(",").append(name);
                }
            }
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(column, species.toString()).apply();
        editor.putString(PlantationSamplingEvaluation.SPECIES_OTHER, otherSpecies.toString()).commit();
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        db = new Database(this);
        names = new ArrayList<>();
        ids = new ArrayList<>();
        Cursor cursor = db.getForms(formType, formStatus);
        adapter = new ArrayAdapter(this, R.layout.form_list_items, names) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (finishedPosition.contains(ids.get(position))) {
                    view.setBackgroundColor(Color.LTGRAY);
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }


                return view;
            }
        };


        if (cursor != null && cursor.moveToFirst()) {
            do {
                long timeStamp = cursor.getInt(cursor.getColumnIndex(Database.STARTING_TIMESTAMP));
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(timeStamp * 1000);
                String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                int formId = cursor.getInt(cursor.getColumnIndex(Database.SURVEY_ID));
                switch (formType) {
                    case Constants.FORMTYPE_PLANTSAMPLING: {
                        try {
                            Cursor cursorPlant = db.getKfdPlantationSamplingData(formId);
                            if (cursorPlant != null && cursorPlant.moveToFirst()) {
                                do {
                                    String plantationName = cursorPlant.getString(cursorPlant.getColumnIndex(Database.PLANTATION_NAME));
                                    String villageName = cursorPlant.getString(cursorPlant.getColumnIndex(Database.VILLAGE_NAME));
                                    String netPlantationArea = cursorPlant.getString(cursorPlant.getColumnIndex(Database.NET_PLANTATION_AREA_HA));
                                    String plantationModel = cursorPlant.getString(cursorPlant.getColumnIndex(Database.PLANTATION_MODEL));
                                    int pos = cursorPlant.getInt(cursorPlant.getColumnIndex(Database.FINISHED_POSITION));
                                    if (pos != -1)
                                        finishedPosition.add(pos);
                                    ids.add(formId);
                                    names.add(plantationName + "-" + netPlantationArea + "HA" + "-" + plantationModel);
                                } while (cursorPlant.moveToNext());
                            }
                        }catch (Exception e){
                            Log.e("acsdcsd",""+e.getMessage());
                        }

                        break;
                    }
                    case Constants.FORMTYPE_SDP:
                        Cursor cursorSDP = db.getKfdSdpgData(formId);
                        if (cursorSDP != null && cursorSDP.moveToFirst()) {
                            do {
                                Log.d("Executed", "cursor3");
                                String rangeName = cursorSDP.getString(cursorSDP.getColumnIndex(Database.RANGE_NAME));
                                String gpName = cursorSDP.getString(cursorSDP.getColumnIndex(Database.GRAMA_PANCHAYAT_NAME));
                                String villageName = cursorSDP.getString(cursorSDP.getColumnIndex(Database.VILLAGE_NAME));
                                int pos = cursorSDP.getInt(cursorSDP.getColumnIndex(Database.FINISHED_POSITION));
                                if (pos != -1)
                                    finishedPosition.add(pos);
                                ids.add(formId);
                                names.add("GP: " + gpName + " - Village: " + villageName);
                            } while (cursorSDP.moveToNext());
                        }
                        break;
                    case Constants.FORMTYPE_TRANSITORY_WORK: {
                        Cursor cursor2 = db.getKfdAdvNurTranFormData(formId, Database.KFD_TRANSITORY_WORKS);
                        if (cursor2 != null && cursor2.moveToFirst()) {
                            do {
                                String forestName = cursor2.getString(cursor2.getColumnIndex(Database.FOREST_NAME));
                                names.add(forestName + " " + date);
                                ids.add(formId);
                            } while (cursor2.moveToNext());
                        }
                        assert cursor2 != null;
                        cursor2.close();
                        break;
                    }
                    case Constants.FORMTYPE_OTHERWORKS:
                        Cursor otherWorkCursor = db.getOtherWorksData(formId);
                        if (otherWorkCursor != null && otherWorkCursor.moveToFirst()) {
                            do {
                                String otherWorks = otherWorkCursor.getString(otherWorkCursor.getColumnIndex(Database.TYPE_OF_WORK));
                                String subWorks = otherWorkCursor.getString(otherWorkCursor.getColumnIndex(Database.WORK_NAME));
                                String location = otherWorkCursor.getString(otherWorkCursor.getColumnIndex(Database.WORK_LOCATION));
                                int pos = otherWorkCursor.getInt(otherWorkCursor.getColumnIndex(Database.FINISHED_POSITION));
                                if (pos != -1)
                                    finishedPosition.add(pos);
                                if (!TextUtils.isEmpty(subWorks) && !subWorks.equalsIgnoreCase("NA"))
                                    names.add(location + " - " + subWorks);
                                else
                                    names.add(location + " - " + otherWorks);
                                ids.add(formId);
                            } while (otherWorkCursor.moveToNext());
                        }
                        break;
                    case Constants.FORMTYPE_SCPTSP:
                        /*query is written to get only village*/
                        Cursor scpTspCursor = db.getSCPTSPData(formId);
                        if (scpTspCursor != null && scpTspCursor.moveToFirst()) {
                            do {
                                String villageName = scpTspCursor.getString(scpTspCursor.getColumnIndex(Database.VILLAGE_NAME));
                                int pos = scpTspCursor.getInt(scpTspCursor.getColumnIndex(Database.COMPLETED_POSITION));
                                if (pos != -1)
                                    finishedPosition.add(pos);
                                ids.add(formId);
                                names.add(" Village: " + villageName);
                            } while (scpTspCursor.moveToNext());
                        }
                        break;
                    case Constants.FORMTYPE_ADVANCEWORK:
                        Cursor cursorPlant = db.getKfdAdvanceWorkData(formId);
                        if (cursorPlant != null && cursorPlant.moveToFirst()) {
                            do {
                                Log.d("Executed", "cursor2");
                                String plantationName = cursorPlant.getString(cursorPlant.getColumnIndex(Database.PLANTATION_NAME));
                                String villageName = cursorPlant.getString(cursorPlant.getColumnIndex(Database.VILLAGE_NAME));
                                String netPlantationArea = cursorPlant.getString(cursorPlant.getColumnIndex(Database.NET_PLANTATION_AREA_HA));
                                String plantationModel = cursorPlant.getString(cursorPlant.getColumnIndex(Database.PLANTATION_MODEL));
                                int pos = cursorPlant.getInt(cursorPlant.getColumnIndex(Database.FINISHED_POSITION));
                                if (pos != -1)
                                    finishedPosition.add(pos);
                                ids.add(formId);
                                names.add(plantationName + "-" + netPlantationArea + "HA" + "-" + plantationModel);
                            } while (cursorPlant.moveToNext());
                        }
                        break;
                    case Constants.FORMTYPE_NURSERY_WORK:
                        Cursor nurseryCursor = db.getNurseryData(formId);
                        if (nurseryCursor.moveToFirst()) {
                            do {
                                String nurseryName = nurseryCursor.getString(nurseryCursor.getColumnIndex(Database.NURSERY_NAME));
                                int pos = nurseryCursor.getInt(nurseryCursor.getColumnIndex(Database.FINISHED_POSITION));
                                if (pos != -1)
                                    finishedPosition.add(pos);
                                ids.add(formId);
                                names.add(" Nursery: " + nurseryName);
                            } while (nurseryCursor.moveToNext());
                        }
                        break;
                }

            } while (cursor.moveToNext());
        } else {
            showEventDialog(SweetAlertDialog.WARNING_TYPE, "No Forms.");
        }
        assert cursor != null;
        cursor.close();
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener((parent, view, position, id) -> {
            populatePreferences(position);
            return false;
        });
    }

    public void showEventDialog(int type, String msg) {
        dialog = new SweetAlertDialog(this, type);

        if (type == SweetAlertDialog.ERROR_TYPE) {

            dialog.setTitleText("Oops...")
                    .setContentText(msg);

        } else if (type == SweetAlertDialog.PROGRESS_TYPE) {

            dialog.setTitleText(msg);

        } else if (type == SweetAlertDialog.SUCCESS_TYPE) {

            dialog.setTitleText(msg)
                    .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation());

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close").setConfirmClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismissWithAnimation();
                finish();
            });

        }
        dialog.show();
    }

    private void inserIntoAdvPlantPref(int formId) {
        SharedPreferences pref = getSharedPreferences(SmcAdvanceWork.SMC_ADVANCE_WORK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Cursor cursor = db.getAdvSmcCursor(formId);

        if (cursor != null && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                editor.putString(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        editor.apply();
        pref = getSharedPreferences(VfcAdvacneWork.VFC_ADVACNE_WORK, Context.MODE_PRIVATE);
        editor = pref.edit();
        cursor = db.getAdvVfcCursor(formId);

        if (cursor != null && cursor.moveToFirst()) {
         /*   if (!cursor.getString(cursor.getColumnIndex(Database.IS_VFC_INVOLVED_IN_PLANTATION_ACTIVITY)).equals("")) {
                editor.putString(VfcPlantationSampling.VFC_APPLICABLE, "Yes");
            }*/
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                editor.putString(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        editor.apply();
    }
    private void insertIntoPlantPref(int formId) {
        SharedPreferences pref = getSharedPreferences(SmcPlantationSampling.SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Cursor cursor = db.getSmcCursor(formId);

        if (cursor != null && cursor.moveToFirst()) {
          /*  if (!cursor.getString(cursor.getColumnIndex(Database.SURVEYOR_NAME)).equals("")) {
                editor.putString(SmcPlantationSampling.SMC_APPLICABLE, "Yes");
            }*/
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                editor.putString(cursor.getColumnName(i), cursor.getString(i));
                Log.e("vdfvdfvd",""+cursor.getColumnName(i));
            }
        }
        editor.apply();

        pref = getSharedPreferences(VfcPlantationSampling.VFC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        editor = pref.edit();
        cursor = db.getVfcCursor(formId);

        if (cursor != null && cursor.moveToFirst()) {
         /*   if (!cursor.getString(cursor.getColumnIndex(Database.IS_VFC_INVOLVED_IN_PLANTATION_ACTIVITY)).equals("")) {
                editor.putString(VfcPlantationSampling.VFC_APPLICABLE, "Yes");
            }*/
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                editor.putString(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        editor.apply();
        pref = getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
        getOtherSpecies(pref, Database.MAIN_SPECIES_PLANTED, db.getNamesOfSpecies());
    }

    private void populatePreferences(int position) {
        SharedPreferences pref = null;
        SharedPreferences.Editor editor;
        int formId = ids.get(position);
        Cursor cursor = db.getSurveyMasterForFormId(formId);
        if (cursor != null && cursor.moveToFirst()) {
            switch (formType) {
                case Constants.FORMTYPE_OTHERWORKS:
                    pref = getSharedPreferences(OtherSurvey.OTHER_SURVEY, Context.MODE_PRIVATE);
                    break;
                case Constants.FORMTYPE_PLANTSAMPLING:
                    pref = getSharedPreferences(PlantationSamplingEvaluation.BASIC_INFORMATION, Context.MODE_PRIVATE);
                    break;
                case Constants.FORMTYPE_SDP:
                    pref = getSharedPreferences(SDPSamplingSurvey.SDP_SURVEY, Context.MODE_PRIVATE);
                    break;
                case Constants.FORMTYPE_SCPTSP:
                    pref = getSharedPreferences(ScpTspSamplingSurvey.SCP_TSP_SAMPLING, Context.MODE_PRIVATE);
                    break;
                case Constants.FORMTYPE_ADVANCEWORK:
                    pref = getSharedPreferences(PlantationSamplingAdvanceWork.ADVANCE_WORK_SURVEY, Context.MODE_PRIVATE);
                    break;
                case Constants.FORMTYPE_NURSERY_WORK:
                    pref = getSharedPreferences(NurseryWorkSurvey.NURSERY_WORK_SURVEY, Context.MODE_PRIVATE);
                    break;

            }
            pref.edit().clear().apply();
            editor = pref.edit();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                editor.putString(cursor.getColumnName(i), cursor.getString(i));
            }
            editor.putString("formStatus", formStatus);
            Cursor cursor1 = db.getFormDataForFormType(formId, formType);
            if (cursor1 != null && cursor1.moveToFirst()) {
                for (int i = 0; i < cursor1.getColumnCount(); i++) {
                    String columnName = cursor1.getColumnName(i);
                    String cursor1String = cursor1.getString(i);
                   /* if (cursor1.getColumnName(i).endsWith("timestamp") && !cursor1.getColumnName(i).equals(Database.STARTING_TIMESTAMP) && !cursor1.getColumnName(i).equals(Database.ENDING_TIMESTAMP) && !cursor1.getColumnName(i).equals(Database.CREATION_TIMESTAMP) && !cursor1.getColumnName(i).equals(Database.GPS_COORDINATE_CREATION_TIMESTAMP)) {
                        long timeStamp = Integer.parseInt(cursor1.getString(i));
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(timeStamp * 1000);
                        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                        editor.putString(columnName, date);
                    } else {
                        editor.putString(columnName, cursor1String);
                    }*/
                    editor.putString(columnName, cursor1String);
                }
            }
            editor.putInt(Database.FINISHED_POSITION, formId);
            editor.apply();
            Intent i = null;
            switch (formType) {
                case Constants.FORMTYPE_OTHERWORKS:
                    i = new Intent(getApplicationContext(), OtherSurvey.class);
                    break;
                case Constants.FORMTYPE_PLANTSAMPLING:
                    insertIntoPlantPref(formId);
                    i = new Intent(getApplicationContext(), PlantSampling.class);
                    break;
                case Constants.FORMTYPE_SDP:
                    i = new Intent(getApplicationContext(), SDPSamplingSurvey.class);
                    break;

                case Constants.FORMTYPE_SCPTSP:
                    i = new Intent(getApplicationContext(), SCPTSPType.class);
                    break;
                case Constants.FORMTYPE_ADVANCEWORK:
                    inserIntoAdvPlantPref(formId);
                    i = new Intent(getApplicationContext(), PlantSamplingAdvaceWork.class);
                    break;
                case Constants.FORMTYPE_NURSERY_WORK:
                    i = new Intent(getApplicationContext(), NurseryWorkSurvey.class);
                    break;

            }
            startActivity(i);
            finish();
        }
    }
}
