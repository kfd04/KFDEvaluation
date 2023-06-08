package com.kar.kfd.gov.kfdsurvey.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.kar.kfd.gov.kfdsurvey.FileUtils;
import com.kar.kfd.gov.kfdsurvey.dialog.SweetAlertDialog;
import com.kar.kfd.gov.kfdsurvey.utils.ZipUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;

public class ExportDatabase extends AsyncTask<String, Void, String> {

    private final WeakReference<Activity> weakActivity;
    private ProgressDialog progressDialog;
    private SweetAlertDialog dialog;

    public ExportDatabase(Activity myActivity) {
        this.weakActivity = new WeakReference<>(myActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(weakActivity.get());
        progressDialog.setMessage("Exporting...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = exportDatabase("kfd_survey", strings[0]);
        weakActivity.get().runOnUiThread(() -> {
            switch (result) {
                case "Success":
                    showEventDialog(SweetAlertDialog.SUCCESS_TYPE, "Database Exported Successfully");
                    break;
                case "Failed":
                    showEventDialog(SweetAlertDialog.ERROR_TYPE, "Failed to Export!");
                    break;
            }
        });

        return result;
    }

    private String exportDatabase(String kfd_survey, String dataString) {
        File internalDBFile = weakActivity.get().getDatabasePath(kfd_survey);
        Uri fileUri = Uri.fromFile(internalDBFile);

        // Create the content resolver instance
        ContentResolver contentResolver = weakActivity.get().getContentResolver();

        // Create the ContentValues object to hold the file metadata
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, internalDBFile.getName());

        // Set the MIME type for the file
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");

        // For Android 11 and above, use the MediaStore API for exporting the file
        ZipUtils.zipFolder(weakActivity.get().getExternalFilesDir(null) + "/Photo/", dataString + "/EvaluationPhotos.zip");
        String result = null;
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, true);
        Uri collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = contentResolver.insert(collection, contentValues);

        try {
            FileChannel sourceChannel = new FileInputStream(internalDBFile).getChannel();
            FileOutputStream outputStream = (FileOutputStream) contentResolver.openOutputStream(item);
            FileChannel destinationChannel = outputStream.getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destinationChannel.close();
            outputStream.close();

            contentValues.clear();
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, false);
            contentResolver.update(item, contentValues, null, null);
            result = "Success";
        } catch (IOException e) {
            result = "Failed";
            e.printStackTrace();}

    // For Android versions below 11, perform a traditional file copy operation

        return result;
    }

    public void showEventDialog(int type, String msg) {
        dialog = new SweetAlertDialog(weakActivity.get(), type);

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

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}