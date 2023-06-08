package com.kar.kfd.gov.kfdsurvey;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;


/**
 * Created by hegde on 8/6/2016.
 */
public class PhotoInfoCreation extends Fragment {

    private File mediaStorageDir;
    File workCodeFile;
    private SweetAlertDialog dialog;
    private boolean errorOccured = false;
    private boolean dataUploaded = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_info_creation, container, false);

        Button createWorkCodeInfoFile = view.findViewById(R.id.create_work_code_info_file);
        createWorkCodeInfoFile.setOnClickListener(v -> {
            dialog = new SweetAlertDialog(getContext());
            showEventDialog(SweetAlertDialog.PROGRESS_TYPE, "Creating photo data!");
            createWorkCodeTxtFile();
            createWorkCodeTxtForSamplePlotFolder();

        });


        return view;
    }
    private void showEventDialog(int type, String msg) {
        dialog = new SweetAlertDialog(getContext(), type);

        if (type == SweetAlertDialog.ERROR_TYPE) {

            dialog.setTitleText("Oops...")
                    .setContentText(msg);

        } else if (type == SweetAlertDialog.PROGRESS_TYPE) {

            dialog.setTitleText(msg);

        } else if (type == SweetAlertDialog.SUCCESS_TYPE) {

            dialog.setTitleText(msg)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        dataUploaded = false;
                        sweetAlertDialog.dismissWithAnimation();
                    });

        } else if (type == SweetAlertDialog.WARNING_TYPE) {

            dialog.setTitleText(msg).setConfirmText("Close");

        }
        dialog.show();
    }
    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    private void createWorkCodeTxtFile() {
        Database db = new Database(getContext());
        Cursor c = db.getStatsForPhototInfoCreation();
        if (c != null) {
            while (c.moveToNext()) {
                String folderName = c.getString(c.getColumnIndex(Database.FORM_TYPE));
                int uploaded = c.getInt(c.getColumnIndex(Database.FORM_STATUS));
                String workCode = c.getString(c.getColumnIndex(Database.WORK_CODE));
                String serverFormId = c.getString(c.getColumnIndex(Database.SERVER_FORM_ID));
                String formId = c.getString(c.getColumnIndex(Database.SURVEY_ID));
                String data = workCode+"|"+serverFormId;
                switch (folderName) {
                    case "OtherWorks":
                        try {
                            mediaStorageDir = new SurveyCreation().getPictureFolder(folderName, formId);
                            workCodeFile = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
                            FileWriter writer = new FileWriter(workCodeFile);
                            writer.append(data);
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            hideDialog();
                            errorOccured=true;
                            showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                        }
                        break;
                    case "SCP&TSP":
                        try {
                            mediaStorageDir = new SurveyCreation().getPictureFolder(folderName, formId);
                            workCodeFile = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
                            FileWriter writer = new FileWriter(workCodeFile);
                            writer.append(data);
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            hideDialog();
                            errorOccured=true;
                            showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                        }
                        break;
                    case "PlantSampling":
                        folderName = "Plantation" + File.separator + "SMC Survey";
                        mediaStorageDir = new SurveyCreation().getPictureFolder(folderName, formId);
                        workCodeFile = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
                        try {
                            FileWriter writer = new FileWriter(workCodeFile);
                            writer.append(data);
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            hideDialog();
                            errorOccured=true;
                            showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                        }
                        folderName = "Plantation" + File.separator + "Evaluation details";
                        mediaStorageDir = new SurveyCreation().getPictureFolder(folderName, formId);
                        workCodeFile = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
                        try {
                            FileWriter writer = new FileWriter(workCodeFile);
                            writer.append(data);
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            hideDialog();
                            errorOccured=true;
                            showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                        }
                        Cursor cursor = db.getPlotInventoryInfoForPhotoCreation(Integer.parseInt(formId));
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                String samplePlotId = cursor.getString(cursor.getColumnIndex(Database.SAMPLE_PLOT_ID));
                                folderName = "Plantation" + File.separator + Constants.FORMTYPE_SAMPLEPLOT;
                                mediaStorageDir = new SurveyCreation().getPictureFolder(folderName, samplePlotId);
                                workCodeFile = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
                                try {
                                    FileWriter writer = new FileWriter(workCodeFile);
                                    writer.append(data);
                                    writer.flush();
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    hideDialog();
                                    errorOccured=true;
                                    showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                                }
                            }
                        }
                        cursor.close();
                        break;
                    case "SDP":
                        try {
                            mediaStorageDir = new SurveyCreation().getPictureFolder(folderName, formId);
                            workCodeFile = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
                            FileWriter writer = new FileWriter(workCodeFile);
                            writer.append(data);
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            hideDialog();
                            errorOccured=true;
                            showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                        }
                        Cursor cur = db.getBeneficiariesInfoForPhotoCreation(Integer.parseInt(formId));
                        if (cur != null) {
                            while (cur.moveToNext()) {
                                String beneficiaryId = cur.getString(cur.getColumnIndex(Database.BENEFICIARY_ID));
                                folderName = "SDPBenificiary";
                                mediaStorageDir = new SurveyCreation().getPictureFolder(folderName, beneficiaryId);
                                workCodeFile = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
                                try {
                                    FileWriter writer = new FileWriter(workCodeFile);
                                    writer.append(data);
                                    writer.flush();
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    hideDialog();
                                    errorOccured=true;
                                    showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                                }
                            }
                        }
                        cur.close();
                        break;
                }
            }
        }
        c.close();

    }
    public void createWorkCodeTxtForSamplePlotFolder(){
        mediaStorageDir = new SurveyCreation().getPictureFolder("Plantation", Constants.FORMTYPE_SAMPLEPLOT);
        File workCodeFile1 = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
        workCodeFile1.delete();
        workCodeFile1 = new File(mediaStorageDir.getPath() + File.separator + "work_code" + ".txt");
        Database db = new Database(getContext());
        Cursor c = db.getPlotInventoryInfoForPhotoCreationInsideSamplePlotFolder();
        if (c != null) {
            while (c.moveToNext()) {
                String formId = c.getString(c.getColumnIndex(Database.FORM_ID));
                String folderName = "Plantation";
                String samplePlotId = c.getString(c.getColumnIndex(Database.SAMPLE_PLOT_ID));

                Cursor cursor = db.getWorkCodeForFormId(Integer.parseInt(formId));
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String workCode = cursor.getString(cursor.getColumnIndex(Database.WORK_CODE));
                        String serverFormId = cursor.getString(cursor.getColumnIndex(Database.SERVER_FORM_ID));
                        String text = workCode +" , "+serverFormId+" , "+samplePlotId;

                        FileOutputStream fOut = null;
                        try {
                            fOut = new FileOutputStream(workCodeFile1, true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            hideDialog();
                            errorOccured=true;
                            showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                        }

                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        try {
                            osw.write(text + "\n");
                            osw.flush();
                            osw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            hideDialog();
                            errorOccured=true;
                            showEventDialog(SweetAlertDialog.ERROR_TYPE, "Photo data Failure!");
                        }
                    }
                }
                cursor.close();

            }
        }
        c.close();


        if (!errorOccured) {
            hideDialog();
            showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Photo data Success!");
        }
    }
}
