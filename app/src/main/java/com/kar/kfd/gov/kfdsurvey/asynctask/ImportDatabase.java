package com.kar.kfd.gov.kfdsurvey.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.utils.ZipUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;


public class ImportDatabase extends AsyncTask<String, Void, String> {

    private final WeakReference<Activity> weakActivity;
    private ProgressDialog progressDialog;

    public ImportDatabase(Activity myActivity) {
        this.weakActivity = new WeakReference<>(myActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        progressDialog = new ProgressDialog(weakActivity.get());
        progressDialog.setMessage("Importing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        importDB(strings[0]);
        Database.deleteFiles(weakActivity.get().getExternalFilesDir(null) + "/Photo/");
        ZipUtils.unzip(strings[0] + "/EvaluationPhotos.zip", String.valueOf(weakActivity.get().getExternalFilesDir(null)));
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(weakActivity.get(), "Imported successfully", Toast.LENGTH_SHORT).show();
    }

    private void importDB(String dbPath) {

        FileChannel source;
        FileChannel destination;
        String backupDBPath = weakActivity.get().getDatabasePath(Database.DATABASE_NAME).getPath();
        String currentDBPath = dbPath + "/kfd_survey.db";
        File currentDB = new File(currentDBPath);
        if (!currentDB.exists()) {
            currentDB = new File(dbPath + "/kfdbackup.db.enc");
        }
        if (!currentDB.exists()) {
            currentDB = new File(dbPath + "/kfdbackup.enc");
        }


        File backupDB = new File(backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}