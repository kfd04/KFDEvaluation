package com.kar.kfd.gov.kfdsurvey.asynctask;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PhotosZipWorker extends Worker {

    private static final String TAG = "PhotosZipWorker";

    public PhotosZipWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Collection<Object> photoPaths = getInputData().getKeyValueMap().values();
        String zipFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "photos.zip";

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            for (Object photoPath : photoPaths) {
                File photoFile = new File(String.valueOf(photoPath));
                if (photoFile.exists()) {
                    addFileToZip(photoFile, zipOutputStream);
                }
            }
            return Result.success();
        } catch (IOException e) {
            Log.e(TAG, "Error zipping photos: " + e.getMessage());
            return Result.failure();
        }
    }

    private void addFileToZip(File file, ZipOutputStream zipOutputStream) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOutputStream.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, length);
        }

        zipOutputStream.closeEntry();
        fileInputStream.close();
    }
}
