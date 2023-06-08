package com.kar.kfd.gov.kfdsurvey.camera;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.utils.ImageUtil;
import com.ngohung.form.HBaseFormActivity;
import com.ngohung.form.util.GPSTracker;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ImageGrid extends Fragment implements ImageAdapter.ImageCaptureListener {

    GridView gridView;
    File mediaStorageDir;
    String formStatus = "0";
    Context context;
    long imageClickTimestamp;
    FilenameFilter fileNameFilter;
    private static final int TAKE_PICTURE_REQUEST = 9871;
    View layout;
    private File imageFile;
    static ArrayList<String> exifAttributes = new ArrayList<>();
    String[] attributes = new String[]
            {
                    ExifInterface.TAG_DATETIME,
                    ExifInterface.TAG_DATETIME_DIGITIZED,
                    ExifInterface.TAG_EXPOSURE_TIME,
                    ExifInterface.TAG_FLASH,
                    ExifInterface.TAG_FOCAL_LENGTH,
                    ExifInterface.TAG_GPS_ALTITUDE,
                    ExifInterface.TAG_GPS_ALTITUDE_REF,
                    ExifInterface.TAG_GPS_DATESTAMP,
                    ExifInterface.TAG_GPS_LATITUDE,
                    ExifInterface.TAG_GPS_LATITUDE_REF,
                    ExifInterface.TAG_GPS_LONGITUDE,
                    ExifInterface.TAG_GPS_LONGITUDE_REF,
                    ExifInterface.TAG_GPS_PROCESSING_METHOD,
                    ExifInterface.TAG_GPS_TIMESTAMP,
                    ExifInterface.TAG_MAKE,
                    ExifInterface.TAG_MODEL,
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.TAG_SUBSEC_TIME,
                    ExifInterface.TAG_WHITE_BALANCE
            };
    private Location gpsLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        formStatus = Objects.requireNonNull(getArguments()).getString("formStatus", "0");
        if (savedInstanceState != null)
            imageFile = (File) savedInstanceState.getSerializable("imageFile");
        GPSTracker mGpsTracker = new GPSTracker(getContext());
        gpsLocation = mGpsTracker.getLocation();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("imageFile", imageFile);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String folderName = Objects.requireNonNull(getArguments()).getString("imageFolderName");
        String formId = getArguments().getString("formId");
        String photoDirectory = Objects.requireNonNull(getActivity()).getExternalFilesDir(null) + "/Photo/";
        mediaStorageDir = new File(photoDirectory + File.separator + folderName + File.separator + formId);
        layout = inflater.inflate(R.layout.image_grid_view, container, false);
        if (!formStatus.equals("0")) {
            layout.findViewById(R.id.delete_photo).setVisibility(View.GONE);
        }

        if (getActivity() instanceof HBaseFormActivity)
            ((HBaseFormActivity) Objects.requireNonNull(getActivity())).setToolBarTitle("Capture image");

        gridView = layout.findViewById(R.id.gridview);
        fileNameFilter = (dir, name) -> {
            if (name.lastIndexOf('.') > 0) {
                int lastIndex = name.lastIndexOf('.');
                String str = name.substring(lastIndex);
                return str.equals(".jpg");
            }
            return false;
        };
        layout.findViewById(R.id.capture_photo).setOnClickListener(v -> {
            GPSTracker mGpsTracker = new GPSTracker(context);
            Location gpsLocation = mGpsTracker.getLocation();
            if (gpsLocation != null)
                takePicture();
            else
                Toast.makeText(context, "Enable GPS", Toast.LENGTH_SHORT).show();
        });
        layout.findViewById(R.id.save_photo).setOnClickListener(view -> getActivity().onBackPressed());
        return layout;
    }

    public void takePicture() {

        imageClickTimestamp = System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(imageClickTimestamp));

        String imageFileName = "IMG_" + timeStamp + "";
        imageFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName + ".jpg");
        File file = imageFile.getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri mImageCaptureUri = FileProvider.getUriForFile(
                context,
                this.context
                        .getPackageName() + ".provider", imageFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(context.getContentResolver(), "A photo", mImageCaptureUri);
            i.setClipData(clip);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    context.getPackageManager()
                            .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, mImageCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        try {
            startActivityForResult(i, TAKE_PICTURE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_REQUEST) {
            try {
                copyExif(imageFile.getAbsolutePath());
                ImageUtil.compressImage(imageFile, 1000, 1000, Bitmap.CompressFormat.JPEG, 100, imageFile.getAbsolutePath()
                ,gpsLocation.getLatitude(),gpsLocation.getLongitude(),gpsLocation.getAltitude());
//                Bitmap bmp = ImagePicker.getImageFromResult(context, resultCode, data);

                pasteExif();
                imageFile = null;
//                createImageInfoFile(imageClickTimestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public void copyExif(String oldPath) throws IOException {
        ExifInterface oldExif = new ExifInterface(oldPath);
        exifAttributes.clear();
        for (int i = 0; i < attributes.length; i++) {
            String value = oldExif.getAttribute(attributes[i]);
            exifAttributes.add(value);
        }


    }

    public void pasteExif() {
        try {
            ExifInterface newExif = new ExifInterface(imageFile.getAbsolutePath());
            for (int i = 0; i < exifAttributes.size(); i++) {
                newExif.setAttribute(attributes[i], exifAttributes.get(i));
            }
            newExif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        gridView.setAdapter(new ImageAdapter(this, getContext(), mediaStorageDir.listFiles(fileNameFilter), formStatus));
        onCheckImage();
    }

    private void createImageInfoFile(long timeStamp) {
        String imageFileTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(timeStamp));
        String imageInfoFileName = "0_" + imageFileTimeStamp + "_";
        File imageInfoFile = new File(mediaStorageDir.getPath() + File.separator + imageInfoFileName + ".txt");
        String date = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss", Locale.getDefault()).format(new Date(timeStamp));

        String info;
        if (gpsLocation != null) {
            info = timeStamp + "|" + gpsLocation.getLatitude() + "|" + gpsLocation.getLongitude() + "|DATE:" + date;
        } else {
            info = timeStamp + "|" + "0.0" + "|" + "0.0" + "|DATE:" + date;
        }
        try {
            FileWriter writer = new FileWriter(imageInfoFile);
            writer.append(info);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCheckImage() {
        int i = 0;

        if (mediaStorageDir.listFiles() != null) {
            for (File file : mediaStorageDir.listFiles()) {
                if (file.getName().contains(".jpg")) {
                    i++;
                }
            }
        }

        if (i >= 3) {
            layout.findViewById(R.id.capture_photo).setVisibility(View.GONE);
            layout.findViewById(R.id.save_photo).setVisibility(View.VISIBLE);
        } else {
            layout.findViewById(R.id.capture_photo).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.save_photo).setVisibility(View.GONE);
        }
    }

}
