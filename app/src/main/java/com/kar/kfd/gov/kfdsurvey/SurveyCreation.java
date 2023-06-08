package com.kar.kfd.gov.kfdsurvey;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kar.kfd.gov.kfdsurvey.constants.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Modified by Sarath
 */
public class SurveyCreation extends Fragment {

    public static String photoDirectory;
    DisplayMetrics displayMetrics;
    private Database db;

    public static Map<String, ArrayList<String>> getTableMetaData(String tableName, Database db) {
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

        HashMap map = new HashMap();
        map.put("columnNamesList", columnNames);
        map.put("columnTypesList", columnTypes);

        return map;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_survey_creation, container, false);
        String versionName = "";
        photoDirectory = Objects.requireNonNull(getActivity()).getExternalFilesDir(null) + "/Photo/";
        displayMetrics = view.getResources().getDisplayMetrics();
        db = new Database(this.getContext());
        TextView surveyType = view.findViewById(R.id.surveyType);
        try {
            versionName = getActivity().getApplicationContext().getPackageManager().getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LinearLayout btnLayout = view.findViewById(R.id.btnLayout);
        RelativeLayout relativeLayout = view.findViewById(R.id.mainLayout);
        TextView version = new TextView(view.getContext());
        version.setId(R.id.versionTVId);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        version.setText("Version : " + versionName);
        version.setTextColor(Color.BLACK);
        params.topMargin = 50;
        int width = getDisplayWidth();
        int height = getDisplayHeight();
        if (width == 480 && height == 800) {
            params.addRule(RelativeLayout.BELOW, btnLayout.getId());
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relativeLayout.addView(version, params);
        } else {
            params.addRule(RelativeLayout.BELOW, btnLayout.getId());
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relativeLayout.addView(version, params);
        }

        Button otherworksSampling = view.findViewById(R.id.otherworksSampling);
        otherworksSampling.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FormList.class);
            i.putExtra("title", "Select Works");
            startActivity(i);
        });

        Button sdpSampling = view.findViewById(R.id.sdpSamplingButton);
        sdpSampling.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FormList.class);
            i.putExtra("formType", Constants.FORMTYPE_SDP);
            i.putExtra("title", "Select Village");
            //Intent i = new Intent(getActivity(), SDPSamplingSurvey.class);
            startActivity(i);
        });

        Button advanceWork = view.findViewById(R.id.advanceWorkButton);
        advanceWork.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FormList.class);
            i.putExtra("formType", Constants.FORMTYPE_ADVANCEWORK);
            i.putExtra("title", "Select AdvanceWork");
            startActivity(i);
        });

        Button nurseryWorks = view.findViewById(R.id.nurseryWorkButton);
        nurseryWorks.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FormList.class);
            i.putExtra("formType", Constants.FORMTYPE_NURSERY_WORK);
            i.putExtra("title", "Select Nursery");
            startActivity(i);
        });


        Button scptspSampling = view.findViewById(R.id.scptspSampling);
        scptspSampling.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FormList.class);
            i.putExtra("formType", Constants.FORMTYPE_SCPTSP);
            i.putExtra("title", "Select Village");
            startActivity(i);
        });

        Button plantSampling = view.findViewById(R.id.plantSamplingButton);
        plantSampling.setOnClickListener(v -> {

            Intent i = new Intent(getActivity(), FormList.class);
            i.putExtra("formType", Constants.FORMTYPE_PLANTSAMPLING);
            i.putExtra("title", "Select Plantation");
            startActivity(i);
        });


        return view;
    }



    private int getDisplayWidth() {
        return displayMetrics.widthPixels;
    }

    private int getDisplayHeight() {
        return displayMetrics.heightPixels;
    }

    public File getPictureFolder(String folderName) {
        File mediaStorageDir = new File(photoDirectory + File.separator + folderName + File.separator + "0");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Directory", "Couldnt create dir " + mediaStorageDir);
            }
        }
        return mediaStorageDir;
    }

    public File getPictureFolder(String folderName, String formId) {
        File mediaStorageDir = new File(photoDirectory + File.separator + folderName + File.separator + formId);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Directory", "Couldnt create dir " + mediaStorageDir);
            }
        }
        return mediaStorageDir;
    }

    public File getNewPictureFolder(long formId, String folderName) {
        return new File(photoDirectory + File.separator + folderName + File.separator + formId);
    }


}
