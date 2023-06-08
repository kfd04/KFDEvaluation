package com.kar.kfd.gov.kfdsurvey.plantation;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.adapter.KnownSpeciesAdapter;
import com.kar.kfd.gov.kfdsurvey.base.BaseActivity;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.model.KnownSpeciesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kar.kfd.gov.kfdsurvey.plantation.SamplePlotSurvey.SAMPLE_PLOT_DETAILS;
import static com.kar.kfd.gov.kfdsurvey.plantation.SamplePlotSurvey.SEEDLING;
@SuppressLint("Range")
public class EmptyPitsActivity extends BaseActivity implements View.OnClickListener {

    String inventoryType;
    private List<KnownSpeciesModel> mList = new ArrayList<>();
    private KnownSpeciesAdapter mAdapter;

    private Database db;
    private SweetAlertDialog dialog;
    private int formId, sampleplotId, totalKnownFailures = 0, totalSurvived;
    SharedPreferences preferences;
    private String totalEmptyPits;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_empty_pits;
    }

    @Override
    protected int getMenuRes() {
        return 0;
    }

    @Override
    protected void initUI() {

        RecyclerView rvkonwnSpecies = findViewById(R.id.rvKnownSpecies);
        rvkonwnSpecies.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        Button btnSaveSpecies = findViewById(R.id.btnSaveSpecies);
        TextView tvNoOfEmptyPits = findViewById(R.id.tvNoOfEmptyPits);

        btnSaveSpecies.setOnClickListener(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            formId = b.getInt("id", 0);
            sampleplotId = b.getInt("sampleplotId", 0);
            inventoryType = b.getString("Inventory-type", "");
            totalEmptyPits=b.getString("empty_pits","");
        }
        db = new Database(this);

        preferences = getSharedPreferences(SAMPLE_PLOT_DETAILS, Context.MODE_PRIVATE);
//        totalEmptyPits = Integer.parseInt(preferences.getString(Database.NO_OF_EMPTY_PITS, ""));
        totalSurvived = Integer.parseInt(preferences.getString(Database.TOTAL_COUNT_SURVIVED, ""));
        tvNoOfEmptyPits.setText(""+totalEmptyPits);

        mAdapter = new KnownSpeciesAdapter(mContext, mList, rvkonwnSpecies, new LinearLayoutManager(mContext));


        addSpecies();


    }

    private void addSpecies() {

        Cursor cursorSpecies = db.getSpeciesForEmptypits(formId, sampleplotId, SEEDLING);
        if (cursorSpecies != null && cursorSpecies.moveToFirst()) {

            do {
                String speciesName = cursorSpecies.getString(cursorSpecies.getColumnIndex(Database.SPECIES_NAME));
                int inventory_id = cursorSpecies.getInt(cursorSpecies.getColumnIndex(Database.INVENTORY_ID));
                int speciesCount = cursorSpecies.getInt(cursorSpecies.getColumnIndex(Database.KNOWN_FAILURES));
                int calculatedFailures = cursorSpecies.getInt(cursorSpecies.getColumnIndex(Database.CALCULATED_FAILURES));
                int eachSpeciesSurvived = cursorSpecies.getInt(cursorSpecies.getColumnIndex(Database.TOTAL_COUNT_SURVIVED));
                mList.add(new KnownSpeciesModel(speciesName, speciesCount, inventory_id, eachSpeciesSurvived, calculatedFailures));
            } while (cursorSpecies.moveToNext());
        }
        Objects.requireNonNull(cursorSpecies).close();
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
        showEventDialog(SweetAlertDialog.WARNING_TYPE, getString(R.string.save_form));
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSaveSpecies) {
            List<KnownSpeciesModel> list = mAdapter.getmList();

            for (KnownSpeciesModel speciesModel : list) {

                totalKnownFailures += speciesModel.getSpeciesCount();
                speciesModel.setCalculatedFailures(0);


            }

            if (totalKnownFailures > Integer.parseInt(totalEmptyPits)) {
                totalKnownFailures = 0;
                Toast.makeText(mContext, "Known Failures must be less than or equal to Total Empty pits", Toast.LENGTH_SHORT).show();
                return;
            } else {
                int totalCalculatedFailures = 0, totalIdentifiedFailures = 0;
                for (int i = 0; i < list.size(); i++) {
                    KnownSpeciesModel speciesModel = list.get(i);
                    int eachSurvived = speciesModel.getEachSpeciesSurvived();
                    int knownFailureOfSpecies = speciesModel.getSpeciesCount();

                    totalCalculatedFailures += calculateknownFailure(eachSurvived, Integer.parseInt(totalEmptyPits), totalSurvived, totalKnownFailures) + knownFailureOfSpecies;
                    speciesModel.setCalculatedFailures(speciesModel.getSpeciesCount());
                    if (Integer.parseInt(totalEmptyPits) > (totalKnownFailures + totalIdentifiedFailures)) {
                        speciesModel.setCalculatedFailures(calculateknownFailure(eachSurvived, Integer.parseInt(totalEmptyPits), totalSurvived, totalKnownFailures) + knownFailureOfSpecies);
                        Log.i(Constants.SARATH, "calculatedFailurefirst: " + calculateknownFailure(eachSurvived, Integer.parseInt(totalEmptyPits), totalSurvived, totalKnownFailures) + knownFailureOfSpecies);
                        if (i != list.size() - 1)
                            totalIdentifiedFailures += calculateknownFailure(eachSurvived, Integer.parseInt(totalEmptyPits), totalSurvived, totalKnownFailures);

                    }

                    if (i == list.size() - 1) {
                        if (Integer.parseInt(totalEmptyPits) != (totalKnownFailures + totalIdentifiedFailures))
                            speciesModel.setCalculatedFailures((Integer.parseInt(totalEmptyPits) - (totalKnownFailures + totalIdentifiedFailures)) + knownFailureOfSpecies);
                    }

                }

                for (KnownSpeciesModel knownSpeciesModel : list) {
                    ContentValues knownSpecies = new ContentValues();
                    knownSpecies.put(Database.INVENTORY_ID, knownSpeciesModel.getInventory_id());
                    knownSpecies.put(Database.KNOWN_FAILURES, knownSpeciesModel.getSpeciesCount());
                    knownSpecies.put(Database.CALCULATED_FAILURES, knownSpeciesModel.getCalculatedFailures());
                    db.updateTableWithId(Database.TABLE_SAMPLE_PLOT_INVENTORY, Database.INVENTORY_ID, knownSpecies);
                    Log.i(Constants.SARATH, "id:" + knownSpeciesModel.getInventory_id() + " speciesName: " + knownSpeciesModel.getSpeciesName() + " count:" + knownSpeciesModel.getSpeciesCount());
                }

                preferences.edit().putString(Database.EMPTY_PIT_STATUS, "1").apply();
                showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Successfully Saved");
            }

        }

    }


    private int calculateknownFailure(int eachSurvived, int totalEmptyPits, int totalSurvived, int totalKnownFailures) {
        try {
            float survivedRatio = ((float) eachSurvived / (float) totalSurvived);
            return Math.round(survivedRatio * (totalEmptyPits - totalKnownFailures));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
