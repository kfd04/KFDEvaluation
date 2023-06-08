package com.kar.kfd.gov.kfdsurvey.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.network.SurveyData;
import com.kar.kfd.gov.kfdsurvey.utils.Analytics;

public class JIService extends JobIntentService {

    public static final String ACTION_SYNC = "action-sync";
    public static final String CHANNEL_ID = "100";


    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, JIService.class, 123, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createdNotificationChennal();
        }
        foreGroundNotification();

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Intent intent1 = new Intent(ACTION_SYNC);
        intent1.putExtra("data", "start");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

        SurveyData data = new SurveyData(getApplicationContext());

        Analytics.track(Analytics.AnalyticsEvents.USER_SYNC_ALL_DATA);
        boolean isSuccess = data.syncAll();


        intent1 = new Intent(ACTION_SYNC);
        if (isSuccess) {
            Analytics.track(Analytics.AnalyticsEvents.USER_SYNC_SUCCESS);
            intent1.putExtra("data", "completed");
        } else {
            Analytics.track(Analytics.AnalyticsEvents.USER_SYNC_FAILED);
            intent1.putExtra("data", "failed");
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("KFD Evaluation")
                .setContentText(isSuccess ? "Form Upload Success" : "Form Upload Failed")
                .setSmallIcon(R.mipmap.kfd_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        manager.notify(1001, builder.build());
    }

    private void foreGroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("KFD Evaluation")
                .setContentText("Uploading in progress")
                .setSmallIcon(R.mipmap.kfd_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);
        startForeground(1000, builder.build());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createdNotificationChennal() {
        String channelName = "Sync Service Notification";
        NotificationChannel chan = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onStopCurrentWork() {
        return super.onStopCurrentWork();
    }
}
