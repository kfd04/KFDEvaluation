package com.kar.kfd.gov.kfdsurvey.scptsp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.BuildConfig;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.SurveyList;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_SCP_N_TSP_SURVEY;
import static com.kar.kfd.gov.kfdsurvey.Database.TABLE_SCP_TSP_BENIFICIARY;


public class SCPTSPType extends AppCompatActivity implements View.OnClickListener {

    Button btnCommunity, btnIndividual, btnSave, btnApprove;
    SharedPreferences preferences;
    public static int screenWidthInPixels = 0;
    Database db;
    int formId;
    public static DisplayMetrics metrics;
    private float dialogButtonFontSize;
    ArrayList<String> benefit = new ArrayList<>();
    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scptsptype);
        btnCommunity = findViewById(R.id.btnCommunity);
        btnIndividual = findViewById(R.id.btnIndividual);
        btnSave = findViewById(R.id.btnSave);
        btnApprove = findViewById(R.id.btnApprove);
        db = Database.initializeDB(this);
        metrics = getResources().getDisplayMetrics();
        screenWidthInPixels = metrics.widthPixels;
        dialogButtonFontSize = (screenWidthInPixels * Constants.dialogFontSizeInPixel) / 800;

        btnCommunity.setOnClickListener(this);
        btnIndividual.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnApprove.setOnClickListener(this);
        preferences = getSharedPreferences(ScpTspSamplingSurvey.SCP_TSP_SAMPLING, Context.MODE_PRIVATE);
        formId = Integer.parseInt(preferences.getString(Database.FORM_ID, "0"));
        getNatureOfBenefit();


    }

    private void getNatureOfBenefit() {
        Cursor cursor = db.getTableForId(Database.TABLE_SCP_N_TSP, Database.FORM_ID, formId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String names = cursor.getString(cursor.getColumnIndex(Database.NATURE_OF_BENEFIT));
                benefit.add(names);
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onClick(View v) {
        Intent i;

        ContentValues cv = new ContentValues();
        switch (v.getId()) {
            case R.id.btnCommunity:
                preferences.edit().putString(Database.COMMUNITY_STATUS, "1").apply();
                cv.put(Database.COMMUNITY_STATUS, "1");
                db.updateTableWithFormId(Database.TABLE_SCP_N_TSP, formId, cv);
                if (!benefit.contains(ScpTspSamplingSurvey.COMMUNITY)) {
                    Toast.makeText(this, "There is No Community to Evaluate", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(getApplicationContext(), SurveyList.class);
                i.putExtra("id", formId);
                i.putExtra("List-type", Constants.BENEFIT_LIST);
                i.putExtra(Database.PART_TYPE, ScpTspSamplingSurvey.COMMUNITY);
                preferences.edit().putString(ScpTspSamplingSurvey.BENTYPE, ScpTspSamplingSurvey.COMMUNITY).apply();
                startActivity(i);
                break;
            case R.id.btnIndividual:
                preferences.edit().putString(Database.INDIVIDUAL_STATUS, "1").apply();

                cv.put(Database.INDIVIDUAL_STATUS, "1");
                db.updateTableWithFormId(Database.TABLE_SCP_N_TSP, formId, cv);
                if (!benefit.contains(ScpTspSamplingSurvey.INDIVIDUAL)) {
                    Toast.makeText(this, "There is No Individual to Evaluate", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(getApplicationContext(), SurveyList.class);
                i.putExtra("id", formId);
                i.putExtra("List-type", Constants.BENEFIT_LIST);
                i.putExtra(Database.PART_TYPE, ScpTspSamplingSurvey.INDIVIDUAL);
                preferences.edit().putString(ScpTspSamplingSurvey.BENTYPE, ScpTspSamplingSurvey.INDIVIDUAL).apply();
                startActivity(i);
                break;

            case R.id.btnSave:
               /* if (preferences.getString(Database.COMMUNITY_STATUS, "0").equals("0")) {
                    showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Community");
                } else if (preferences.getString(Database.INDIVIDUAL_STATUS, "0").equals("0"))
                    showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Individual");
                else*/
                    submitScpTspSampling(0);
                break;

            case R.id.btnApprove:
                if (preferences.getString(Database.COMMUNITY_STATUS, "0").equals("0")) {
                    showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Community");
                } else if (db.getFormFilledStatus(TABLE_SCP_N_TSP_SURVEY, String.valueOf(formId), ScpTspSamplingSurvey.COMMUNITY)) {
                    showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Community");
                } else if (preferences.getString(Database.INDIVIDUAL_STATUS, "0").equals("0"))
                    showEventDialog(SweetAlertDialog.WARNING_TYPE, "Complete Individual");
                else if (db.getFormFilledStatus(TABLE_SCP_TSP_BENIFICIARY, String.valueOf(formId))) {
                    showEventDialog(SweetAlertDialog.WARNING_TYPE, "Fill all the fields in Individual");
                } else
                    submitScpTspSampling(1);
                break;
        }
    }

    private void submitScpTspSampling(int flag) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View customDialogLayout = this.getLayoutInflater().inflate(flag == 0 ? R.layout.dialog_submit_form : R.layout.dialog_approve_form, null);
        alertDialogBuilder.setView(customDialogLayout);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        customDialogLayout.findViewById(R.id.alert_submit).setOnClickListener(v -> {
            ContentValues cv = new ContentValues();
            if (flag == 0) {
                cv.put(Database.COMPLETED_POSITION, preferences.getInt(Database.FINISHED_POSITION, -1));
                db.updateTableWithFormId(Database.TABLE_SCP_N_TSP, formId, cv);
            }
            if (flag == 1) {
                cv.put(Database.FORM_STATUS, 1);
                cv.put(Database.APP_ID, BuildConfig.VERSION_CODE);
                db.updateSurveyMasterWithFormId(formId, cv);
            }
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            alertDialog.dismiss();
        });
        customDialogLayout.findViewById(R.id.alert_cancel).setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();


    }

    @Override
    public void onBackPressed() {
        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences(ScpTspSamplingSurvey.SCP_TSP_SAMPLING, Context.MODE_PRIVATE);

        if (pref.getString("formStatus", "0").equals("0")) {
            showEventDialog(SweetAlertDialog.WARNING_TYPE, "Save Form");
        }
        if (!pref.getString("formStatus", "0").equals("0")) {
            pref.edit().clear();
            super.onBackPressed();
        }

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
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    });

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close");
        }
        dialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
