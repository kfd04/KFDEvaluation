package com.kar.kfd.gov.kfdsurvey.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.kar.kfd.gov.kfdsurvey.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private final ImageCaptureListener imageCaptureListener;
    private Context mContext;
    private ArrayList<File> imageFiles;
    private ImageAdapter adapter = this;
    private int REQ_WITDH = 150;
    private int REQ_HEIGHT = 150;
    private String formStatus;

    // Constructor
    public ImageAdapter(ImageCaptureListener imageCaptureListener, Context c, File[] files, String formStatus) {
        mContext = c;
        this.imageCaptureListener = imageCaptureListener;
        if (files != null) {
            imageFiles = new ArrayList<>(Arrays.asList(files));
        } else {
            imageFiles = new ArrayList<>();
        }
        this.formStatus = formStatus;
    }

    public int getCount() {
        return imageFiles.size();
    }

    public Object getItem(int position) {
        return imageFiles.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            assert inflater != null;
            convertView = inflater.inflate(R.layout.survey_image_view, null);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.image_view);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFiles.get(position).getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, REQ_WITDH, REQ_HEIGHT);
        options.inJustDecodeBounds = false;
        if (imageFiles.get(position).exists() && imageFiles.get(position).isFile()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFiles.get(position).getAbsolutePath(), options);
            holder.imageView.setImageBitmap(bitmap);
        }
        holder.imageView.setOnClickListener(v -> viewGallery(imageFiles.get(position)));
        if (formStatus.equals("0")) {
            holder.imageView.setOnLongClickListener(v -> {
                removeFromGrid(position);
                return true;
            });
        }
        return convertView;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    private void removeFromGrid(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Would you like to delete this image?");
        builder.setCancelable(true);
        builder.setPositiveButton("Delete", (dialog, which) -> {
            imageFiles.get(position).delete();
            imageFiles.remove(position);
            imageCaptureListener.onCheckImage();
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void viewGallery(File file) {

        Uri mImageCaptureUri = FileProvider.getUriForFile(
                mContext,
                mContext.getApplicationContext()
                        .getPackageName() + ".provider", file);

        Intent view = new Intent();
        view.setAction(Intent.ACTION_VIEW);
        view.setData(mImageCaptureUri);
        List<ResolveInfo> resInfoList =
                mContext.getPackageManager()
                        .queryIntentActivities(view, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            mContext.grantUriPermission(packageName, mImageCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        view.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(mImageCaptureUri, "image/*");
        mContext.startActivity(intent);
    }


    static class ViewHolder {
        ImageView imageView;
    }

    interface ImageCaptureListener {
        void onCheckImage();
    }
}