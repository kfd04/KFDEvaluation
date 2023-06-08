package com.kar.kfd.gov.kfdsurvey;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

public class DatabaseExporter {
    public static void exportDatabase(Context context, String databaseName) {
        File internalDBFile = context.getDatabasePath(databaseName);
        Uri fileUri = Uri.fromFile(internalDBFile);

        // Create the content resolver instance
        ContentResolver contentResolver = context.getContentResolver();

        // Create the ContentValues object to hold the file metadata
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, internalDBFile.getName());

        // Set the MIME type for the file
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");

        // For Android 11 and above, use the MediaStore API for exporting the file
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, true);
            Uri collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri item = contentResolver.insert(collection, contentValues);

            try {
                contentResolver.openOutputStream(item).close();
                contentResolver.update(item, contentValues, null, null);
                contentResolver.releasePersistableUriPermission(item, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                contentValues.clear();
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, false);
                contentResolver.update(item, contentValues, null, null);
            }
        }
        // For Android versions below 11, use the traditional file copy approach
        else {
            File externalDBFile = new File(Environment.getExternalStorageDirectory(), databaseName);
            try {
                FileUtils.copyFile(internalDBFile, externalDBFile);
            } catch (IOException e) {
                Log.e("WDCASD",""+e.getMessage());
                e.printStackTrace();
            }
        }

        Toast.makeText(context, "Database exported to storage", Toast.LENGTH_SHORT).show();
    }
}
