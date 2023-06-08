package com.kar.kfd.gov.kfdsurvey;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.multidex.MultiDex;

import com.kar.kfd.gov.kfdsurvey.network.VolleyNetworkUtils;
import com.kar.kfd.gov.kfdsurvey.utils.Analytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Modified by Sarath
 */
public class Application extends VolleyNetworkUtils {

    @Override
    public void onCreate() {
        super.onCreate();
        Analytics.intialize(this);
        try {
            copyDatabaseIfNotExists();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void copyDatabaseIfNotExists() throws IOException {
        SQLiteDatabase checkDB = null;
       String dbName = "/data/data/" + getApplicationContext().getPackageName() + "/databases/" + Database.DATABASE_NAME;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbName, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch ( SQLiteException e) {
            e.printStackTrace();
        }

        if (checkDB == null) {

            InputStream myInput = getAssets().open("kfd_survey.db");
            String dbFolderName = "/data/data/" + getApplicationContext().getPackageName() + "/" + "databases";
            File dbFolder = new File(dbFolderName);
            dbFolder.mkdirs();
            String outFileName = dbFolderName + "/" + "kfd_survey";
            OutputStream myOutput = new FileOutputStream(new File(outFileName));

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
    }
}
