package com.kar.kfd.gov.kfdsurvey;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.network.NetworkDetector;
import com.kar.kfd.gov.kfdsurvey.network.SurveyData;
import com.kar.kfd.gov.kfdsurvey.service.JIService;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Modified by Sarath
 */
public class SurveyStats extends Fragment implements SurveyData.DataUploadListener {

    private TextView plantSampAU;
    private TextView plantSampTBU;
    private TextView sdpSampAU;
    private TextView sdpSampTBU;
    private TextView sNTSampAU;
    private TextView sNTSampTBU;
    private TextView oWSampAU;
    private TextView oWSampTBU;
    private TextView advSampAU;
    private TextView advSampTBU;
    private TextView nurSampAU;
    private TextView nurSampTBU;
    private float dialogButtonFontSize;
    public static int screenWidthInPixels = 0;
    public static DisplayMetrics metrics;
    private Database db;
    private SweetAlertDialog dialog;
    private NetworkDetector detector = null;
    private ArrayList<String> failedUploadIds = new ArrayList<>();
    ProgressDialog progressDialog;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(JIService.ACTION_SYNC)) {
                String data = intent.getStringExtra("data");

                switch (data) {
                    case "start":
                        progressDialog = new ProgressDialog(requireContext());
                        progressDialog.setTitle("Uploading Survey Data");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        break;
                    case "doing":
                        String message = intent.getStringExtra("message");
                        progressDialog.setMessage(message);
                        break;
                    case "completed":
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        if (db.getUnuploadedRecords(failedUploadIds) == 0) {
                            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Survey data uploaded!");
                            onDataUpload();
                        }
                        break;
                    case "failed":
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        onDataUpload();
                        showEventDialog(SweetAlertDialog.ERROR_TYPE, "Failed to Upload!");
                        break;
                }

            }
        }
    };

    private void initializeLayout(final View view) {
        view.findViewById(R.id.btnUploadData).setOnClickListener(v -> validateUpdatingCredentials(view));
        plantSampAU = view.findViewById(R.id.pSAU);
        plantSampTBU = view.findViewById(R.id.pSTBU);
        sdpSampAU = view.findViewById(R.id.sDPSAU);
        sdpSampTBU = view.findViewById(R.id.sDPSTBU);
        sNTSampAU = view.findViewById(R.id.sNTSAU);
        sNTSampTBU = view.findViewById(R.id.sNTSTBU);
        oWSampAU = view.findViewById(R.id.oWSAU);
        oWSampTBU = view.findViewById(R.id.oWSTBU);
        advSampAU = view.findViewById(R.id.sAdAU);
        advSampTBU = view.findViewById(R.id.sAdTBU);
        nurSampAU = view.findViewById(R.id.sNUAU);
        nurSampTBU = view.findViewById(R.id.sNUTBU);
        db = new Database(getContext());
        updateStats();
    }



    private void updateStats() {
        Database db = new Database(getContext());
        Cursor c = db.getStats();
        if (c != null) {
            while (c.moveToNext()) {
                String table = c.getString(c.getColumnIndex(Database.FORM_TYPE));
                int uploaded = c.getInt(c.getColumnIndex(Database.FORM_STATUS));
                int total;
                if (uploaded == Constants.UPLOADED) {
                    switch (table) {
                        case Constants.FORMTYPE_OTHERWORKS:
                            total = c.getInt(c.getColumnIndex("tot"));
                            oWSampAU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_SCPTSP:
                            total = c.getInt(c.getColumnIndex("tot"));
                            sNTSampAU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_PLANTSAMPLING:
                            total = c.getInt(c.getColumnIndex("tot"));
                            plantSampAU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_SDP:
                            total = c.getInt(c.getColumnIndex("tot"));
                            sdpSampAU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_ADVANCEWORK:
                            total = c.getInt(c.getColumnIndex("tot"));
                            advSampAU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_NURSERY_WORK:
                            total = c.getInt(c.getColumnIndex("tot"));
                            nurSampAU.setText(String.valueOf(total));
                            break;

                        case Constants.FORMTYPE_TRANSITORY_WORK:
                            total = c.getInt(c.getColumnIndex("tot"));
//                            transitoryWorkSurveyAU.setText(String.valueOf(total));
                            break;
                    }
                } else if (uploaded == Constants.APPROVED) {
                    switch (table) {
                        case Constants.FORMTYPE_OTHERWORKS:
                            total = c.getInt(c.getColumnIndex("tot"));
                            oWSampTBU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_SCPTSP:
                            total = c.getInt(c.getColumnIndex("tot"));
                            sNTSampTBU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_PLANTSAMPLING:
                            total = c.getInt(c.getColumnIndex("tot"));
                            plantSampTBU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_SDP:
                            total = c.getInt(c.getColumnIndex("tot"));
                            sdpSampTBU.setText(String.valueOf(total));
                            break;

                        case Constants.FORMTYPE_ADVANCEWORK:
                            total = c.getInt(c.getColumnIndex("tot"));
                            advSampTBU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_NURSERY_WORK:
                            total = c.getInt(c.getColumnIndex("tot"));
                            nurSampTBU.setText(String.valueOf(total));
                            break;
                        case Constants.FORMTYPE_TRANSITORY_WORK:
                            total = c.getInt(c.getColumnIndex("tot"));
//                            transitoryWorkSurveyTBU.setText(String.valueOf(total));
                            break;
                    }
                }
            }
        }
        Objects.requireNonNull(c).close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.survey_stats_layout, container, false);
        initializeLayout(view);
        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

        detector = new NetworkDetector(view.getContext());
        dialog = new SweetAlertDialog(view.getContext());

        return view;
    }

    @Override
    public void onDataUpload() {
        resetTextViews();
        updateStats();
    }

    private void resetTextViews() {
        plantSampAU.setText("0");
        plantSampTBU.setText("0");
        sdpSampAU.setText("0");
        sdpSampTBU.setText("0");
        sNTSampAU.setText("0");
        sNTSampTBU.setText("0");
        oWSampAU.setText("0");
        oWSampTBU.setText("0");
        advSampAU.setText("0");
        oWSampTBU.setText("0");
        advSampAU.setText("0");
        advSampTBU.setText("0");
        nurSampAU.setText("0");
        nurSampTBU.setText("0");
    }

    private void validateUpdatingCredentials(final View view) {
        final SurveyData surveyData = new SurveyData(getActivity());
        surveyData.setDataUploadListener(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View customDialogLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_upload_user_credentials, null);
        alertDialogBuilder.setView(customDialogLayout).create();
        alertDialogBuilder.setCancelable(false);
        final EditText userName = customDialogLayout.findViewById(R.id.uploadUserIdET);
        final EditText password = customDialogLayout.findViewById(R.id.uploadPwdET);
        alertDialogBuilder.setPositiveButton("Submit", (dialog, which) -> {
            ArrayList<String> data = db.getLoginDetails();
            if (userName.getText().toString().trim().equalsIgnoreCase(data.get(0)) && password.getText().toString().trim().equals(data.get(1))) {

                if (!detector.detect()) {
                    showEventDialog(SweetAlertDialog.ERROR_TYPE, "Internet not connected!");
                    return;
                }
                if (db.getUnuploadedRecords(failedUploadIds) == 0) {
                    showEventDialog(SweetAlertDialog.WARNING_TYPE, "No data to upload.");
                    return;
                }
                Intent jiIntent = new Intent(requireContext(), JIService.class);
                JIService.enqueueWork(requireContext(), jiIntent);
            } else {
                Toast.makeText(getActivity().getBaseContext(), "Invalid credentials. Please enter valid credentials", Toast.LENGTH_LONG).show();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, dialogButtonFontSize);

            Button btnNegative = alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, dialogButtonFontSize);
        });
        alertDialog.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, new IntentFilter(JIService.ACTION_SYNC));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }


    public void showEventDialog(int type, String msg) {
        dialog = new SweetAlertDialog(getContext(), type);

        if (type == SweetAlertDialog.ERROR_TYPE) {

            dialog.setTitleText("Oops...")
                    .setContentText(msg);

        } else if (type == SweetAlertDialog.PROGRESS_TYPE) {

            dialog.setTitleText(msg);

        } else if (type == SweetAlertDialog.SUCCESS_TYPE) {

            dialog.setTitleText(msg)
                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close");

        }
        dialog.show();
    }

}






