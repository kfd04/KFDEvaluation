package com.kar.kfd.gov.kfdsurvey.plantation.smc;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.adapter.SMCAdapter;
import com.kar.kfd.gov.kfdsurvey.base.BaseActivity;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.model.SMCModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmcWorksActivity extends BaseActivity implements View.OnClickListener, SMCAdapter.ViewCallBack {

    public static final String TAG = SmcWorksActivity.class.getSimpleName();
    private List<SMCModel> mList = new ArrayList<>();
    private SMCAdapter mAdapter;
    private String formId;
    private Database db;
    private SweetAlertDialog dialog;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_smc_works;
    }

    @Override
    protected int getMenuRes() {
        return 0;
    }

    @Override
    protected void initUI() {
        RecyclerView rv = findViewById(R.id.rvSmc);
        mAdapter = new SMCAdapter(mContext, mList, rv, new LinearLayoutManager(mContext), this);
        Button saveSMC = findViewById(R.id.saveSMC);
        saveSMC.setOnClickListener(this);

        db = new Database(this);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            formId = b.getString(Database.FORM_ID);
        }
        callSMCWorks();

    }

    private void callSMCWorks() {
        Database db = new Database(this);
        Cursor cursor = db.getSmcWorks(formId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int smcId = cursor.getInt(cursor.getColumnIndex(Database.SMC_ID));
                String smcWork = cursor.getString(cursor.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                double cost = cursor.getDouble(cursor.getColumnIndex(Database.SMC_STRUCTURE_COST));
                int exist = cursor.getInt(cursor.getColumnIndex(Database.SMC_AVAILABILITY));

                mList.add(new SMCModel(smcId, smcWork, cost, exist));
            } while (cursor.moveToNext());
            cursor.close();
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initListeners() {

    }


    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected boolean isHideActionbar() {
        return false;
    }

    @Override
    protected boolean displayHomeEnabled() {
        return false;
    }

    @Override
    public String title() {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveSMC) {
            List<SMCModel> list = mAdapter.getmList();

            for (SMCModel sml : list) {

                Log.i(TAG, "onClick: " + sml.toString());
                ContentValues cvsmcWorks = new ContentValues();
                cvsmcWorks.put(Database.SMC_ID, sml.getSmcId());
                cvsmcWorks.put(Database.TYPE_OF_STRUCTURE, sml.getSmcWorks());
                cvsmcWorks.put(Database.SMC_STRUCTURE_COST, sml.getSmcCost());
                cvsmcWorks.put(Database.SMC_AVAILABILITY, sml.getExist());
                db.updateTableWithId(Database.TABLE_SMC_LIST, Database.SMC_ID, cvsmcWorks);
                Log.i(TAG, "onClick: " + sml.getSmcId() + " " + sml.getSmcWorks() + " " + sml.getSmcCost() + " " + sml.getExist());
            }

            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            if (mAdapter.valueChanged)
                calculateHighest();
        }

    }

    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void calculateHighest() {
        SharedPreferences pref = getSharedPreferences(SmcPlantationSampling.SMC_PLANTATION_SAMPLING, Context.MODE_PRIVATE);
        int formId = Integer.parseInt(pref.getString(Database.FORM_ID, "0"));


        if (pref.getString(Database.SMC_STATUS, "0").equals("0")) {
            ContentValues cv = new ContentValues();
            cv.put(Database.SMC_STATUS, 1);
            db.updateTableWithId(Database.TABLE_SMC_SAMPLING_MASTER, Database.FORM_ID, cv);
        }
        Cursor cursorParent = db.getsmcHighest(formId);
        Cursor cursorSMCHighchild = db.getSMCHighestWithFormID(formId);
        if (cursorParent.getCount() == 0 && !cursorParent.moveToFirst()) {
            if (cursorSMCHighchild != null && cursorSMCHighchild.moveToFirst())
                db.deleteSMCHigh(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.FORM_ID, formId);
        }
        if (cursorParent != null && cursorParent.moveToFirst()) {

            if (cursorSMCHighchild != null && cursorSMCHighchild.moveToFirst()) {
                int[] smcIds = db.getSMCIds(String.valueOf(formId));
                String smcpath = this.getExternalFilesDir(null) + "/Photo/" + SmcPlantationSampling.folderName + File.separator;
                for (Integer i : smcIds) {
                    File file = new File(smcpath + i);
                    deleteDir(file);
                }

                db.deleteSMCHigh(Database.TABLE_KFD_PLANTATION_SAMPLING_SMC_DETAILS_HIGHEST, Database.FORM_ID, formId);
                // TODO: 14-09-2018 file want to be deleted. 

                do {
                    String smcWork = cursorParent.getString(cursorParent.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                    Double cost = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_COST));
                    Double length = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_LENGTH));
                    Double breadth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_BREADTH));
                    Double depth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_DEPTH));

                    ContentValues cvSMCHigh = new ContentValues();
                    cvSMCHigh.put(Database.FORM_ID, formId);
                    cvSMCHigh.put(Database.TYPE_OF_STRUCTURE, smcWork);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_COST, cost);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_LENGTH, length);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_BREADTH, breadth);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_DEPTH, depth);

                    db.insertIntoInspectedSmcWorkDetails(cvSMCHigh);
                } while (cursorParent.moveToNext());
            } else {
                do {
                    String smcWork = cursorParent.getString(cursorParent.getColumnIndex(Database.TYPE_OF_STRUCTURE));
                    Double cost = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_COST));
                    Double length = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_LENGTH));
                    Double breadth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_BREADTH));
                    Double depth = cursorParent.getDouble(cursorParent.getColumnIndex(Database.SMC_STRUCTURE_DEPTH));

                    ContentValues cvSMCHigh = new ContentValues();
                    cvSMCHigh.put(Database.FORM_ID, formId);
                    cvSMCHigh.put(Database.TYPE_OF_STRUCTURE, smcWork);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_COST, cost);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_LENGTH, length);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_BREADTH, breadth);
                    cvSMCHigh.put(Database.SMC_STRUCTURE_DEPTH, depth);
                    Log.i(TAG, "calculateHighest: " + smcWork + " " + cost);
                    db.insertIntoInspectedSmcWorkDetails(cvSMCHigh);
                } while (cursorParent.moveToNext());
            }

        }

        Objects.requireNonNull(cursorSMCHighchild).close();

        Objects.requireNonNull(cursorParent).close();
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

    @Override
    public void onBackPressed() {
        //  showSaveFormDataAlert();
        showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
    }

    public void showSaveFormDataAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setMessage("Please Save SMCWorks by pressing 'SAVE'");
        alertDialog.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void viewCallBack(SMCModel smcModel) {


    }
}
