package com.kar.kfd.gov.kfdsurvey.map;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kar.kfd.gov.kfdsurvey.R;
import com.squareup.otto.Subscribe;

import java.util.Date;

/**
 * Created by hp on 09-Sep-17.
 */

public class GPSService1 extends Service {

    private static final int NOTIFICATION_ID = 12345678;
    private final String TAG = "GPSService";
    private final IBinder mBinder = new GpsLoggingBinder();
    private boolean mChangingConfiguration = false;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final String CHANNEL_ID = "100";
    //    private LocationCallback mLocationCallback;
//    private LocationCallback weakLocationCallback;
    private Handler mServiceHandler;
    private Location mLastLocation = null;
    /**
     * The current location.
     */
    private Location mLocation;
    int i = 0;

    /*    private static class LocationCallbackReference extends LocationCallback {

            private WeakReference<LocationCallback> locationCallbackRef;

            LocationCallbackReference(LocationCallback locationCallback) {
                locationCallbackRef = new WeakReference<LocationCallback>(locationCallback);
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationCallbackRef != null && locationCallbackRef.get() != null) {
                    locationCallbackRef.get().onLocationResult(locationResult);
                }
            }
        }*/
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onNewLocation(locationResult.getLastLocation());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
     /*   mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };*/

//        weakLocationCallback = new LocationCallbackReference(mLocationCallback);

        initLocationRequest();
        getLastKnowLocation(null);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        Log.i(TAG, "onCreate");
        startLocationUpdate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    public void onNewLocation(Location location) {
        if (location != null)
            try {
                Log.d(TAG, "onLocationChanged trip  " + " lat " + location.getLatitude() + " lng " + location.getLongitude());
                Log.d(TAG, "onLocationChanged speed " + location.getSpeed());
                Intent intent = new Intent("GPSLocationUpdates");
                // You can also include some extra data.
                intent.putExtra("Status", "adsdc");
                Bundle b = new Bundle();
                b.putParcelable("Location", location);
                intent.putExtra("Location", b);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                i++;
                Location startPoint = new Location("locationA");
                double distance = 0.0;
                if (mLastLocation != null) {
                    Log.d(TAG, "accuracy " + mLastLocation.getAccuracy());
                    startPoint.setLatitude(mLastLocation.getLatitude());
                    startPoint.setLongitude(mLastLocation.getLongitude());
//                    startPoint.setAccuracy(mLastLocation.getAccuracy());
                    mLastLocation.getTime();
                }

                mLastLocation = location;

                Location endPoint = new Location("locationA");
                endPoint.setLatitude(mLastLocation.getLatitude());
                endPoint.setLongitude(mLastLocation.getLongitude());

                if (startPoint.getLatitude() != 0.0) {
                    distance = startPoint.distanceTo(endPoint);
//                    Log.e("startpoint",  startPoint.getLatitude() + "");
//
//                    Log.d("test",""+startPoint.getLongitude());
//
//
//                    Log.e("DIStance",  Math.round(distance) +"di" +mLastLocation.getTime() + "");
                    //     Toast.makeText(this,"distance: "+distance ,Toast.LENGTH_LONG).show();

                }


                if (distance < 100.0) {
                    //      Toast.makeText(this,"insert : "+distance,Toast.LENGTH_LONG).show();

                    Log.e("Accuracy", "" + mLastLocation.getAccuracy());

                }
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: " + e);
            }
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
        stopService();
        mLocationCallback = null;
        stopForeground(true);
        super.onDestroy();
        Log.i(TAG, "onDestroy - Estou sendo destruido ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    public void stopService() {
        Log.d(TAG, "stopService: ");
        stopSelf();
        stopLocationUpdate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
//        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        Log.i(TAG, "Starting foreground service");
        startForeground(NOTIFICATION_ID, getNotification());
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    private void initLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdate() {
        Log.i(TAG, "Requesting location updates");
        startForeground(NOTIFICATION_ID, getNotification());

        /*    foreGroundNotification();*/
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());

//            scheduledLocationUpdate();
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
        Log.d(TAG, "startLocationUpdate: ");
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createdNotificationChannel() {
        String channelName = "Gps Notification";
        NotificationChannel chan = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    public void stopLocationUpdate() {
        Log.i(TAG, "Removing location updates");
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
        stopForeground(true);
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//            stopScheduler();
//            stopSelf();
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    public void getLastKnowLocation(final SeekLocationCallback seekLocationCallback) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(() -> Toast.makeText(GPSService1.this.getApplicationContext(), "Location Permission", Toast.LENGTH_SHORT).show());
        } else {
            try {
           /* final LocationRequest aLocationRequest = LocationRequest.create().setNumUpdates(1).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            final LocationCallback aLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult.getLastLocation() != null) {
                        mLocation = locationResult.getLastLocation();
                        if (seekLocationCallback != null)
                            seekLocationCallback.seekLocationUpdate(mLocation);
                    } else {
                        Log.w(TAG, "Failed to get location.");
                        if (seekLocationCallback != null)
                            seekLocationCallback.seekLocationUpdate(null);
                    }
                }
            };
            mFusedLocationClient.requestLocationUpdates(aLocationRequest,
                    aLocationCallback, Looper.myLooper());*/
            /*mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });*/
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            mLocation = location;
                            Log.e("aSXASx", "" + location);
                            if (seekLocationCallback != null)
                                seekLocationCallback.seekLocationUpdate(mLocation);
                        } else {
                            Log.w(TAG, "Failed to get location.");
                            if (seekLocationCallback != null)
                                seekLocationCallback.seekLocationUpdate(null);
                        }
                    }
                });

            } catch (SecurityException unlikely) {
                Log.e(TAG, "Lost location permission." + unlikely);
            }
        }

    }

    private Notification getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createdNotificationChannel();
        }

        Intent intent = new Intent(this, MapGps.class);
        @SuppressLint("WrongConstant")
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle("Recording Gps")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.kfdlogo)
                .setTicker(getString(R.string.app_name))
                .setWhen(System.currentTimeMillis()).build();
    }

    public interface SeekLocationCallback {
        void seekLocationUpdate(Location location);
    }

    /**
     * Can be used from calling classes as the go-between for methods and
     * properties.
     */
    public class GpsLoggingBinder extends Binder {
        public GPSService1 getService() {

            return GPSService1.this;

        }
    }

    /*private ScheduledExecutorService mScheduledExecutorService;

    private void scheduledLocationUpdate() {
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.wtf(TAG, "run: ");
                onNewLocation(mFusedLocationClient.getLastLocation().getResult());
                Log.wtf(TAG, "run: ");
            }
        }, 0, PreferenceUtil.getGpsFreq(), TimeUnit.MILLISECONDS);

    }

    private void stopScheduler() {
        if (mScheduledExecutorService != null)
            mScheduledExecutorService.shutdown();
    }*/
    //
}


