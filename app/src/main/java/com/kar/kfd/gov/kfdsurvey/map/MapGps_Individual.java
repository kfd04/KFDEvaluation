package com.kar.kfd.gov.kfdsurvey.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.base.BaseActivity;
import com.kar.kfd.gov.kfdsurvey.camera.ImageGrid;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MapGps_Individual extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    private static final float DEFAULT_ZOOM = 17f;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0x5001;
    private final String TAG = "MapGps_Individual";
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private TextView mTvPolygon, mTvPolyline, mTvStartStop;
    private CardView mCvPolygon, mCvPolyline;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private boolean isLocationUpdateStarted = false, mIsPolygon = false, mInitialZoom = false;
    private Location mLastKnownLocation;
    private PolygonOptions mPolygonOptions;
    private PolylineOptions mPolylineOptions;
    private PolygonOptions mMarkerPolyline;
    private LocationManager mLocationManagerService;
    private SharedPreferences pref, sharedPreferences;
    private String mData, mMeasureTYpe, folderName;
    private String[] mType;
    private List<LatLng> mLatLngCollection, mTempCollection;
    private Map<String, LatLng> mSamplePlots;
    private List<Marker> mMarkerList = new ArrayList<>();
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private int samplePlots;
    Database db;
    private float dist;
    private Polyline polyline;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onNewLocation(locationResult.getLastLocation());
        }
    };
    private String block_type,block_area;
    private String status="true";


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_map_gps;
    }

    @Override
    protected int getMenuRes() {
        return R.menu.menu_map;
    }


    @Override
    protected void initUI() {

        mMapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frg_map, mMapFragment, "tag").commit();
        mTvPolygon = findViewById(R.id.tvPolygon);
        mTvPolyline = findViewById(R.id.tvPolyline);
        mTvStartStop = findViewById(R.id.tvStartStop);
        db = new Database(this);
        mCvPolygon = findViewById(R.id.cvPolygon);
        mCvPolyline = findViewById(R.id.cvPolyline);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.mLocationManagerService = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        String preferences = Objects.requireNonNull(getIntent().getExtras()).getString(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
        pref = getSharedPreferences(preferences, Context.MODE_PRIVATE);
        mData = pref.getString(Database.GPS_LATLONG_COLLECTION, "");
        mMeasureTYpe = pref.getString(Database.GPS_MEASUREMENT, "");
        block_type = getIntent().getStringExtra("block_type");
        block_area = getIntent().getStringExtra("block_area");
        mType = mMeasureTYpe != null ? mMeasureTYpe.split(":") : new String[0];
        Log.e("dcdc",""+block_type);
//        try {
//            samplePlots = Integer.parseInt(Objects.requireNonNull(pref.getString(Database.TOTAL_NO_OF_SAMPLE_PLOTS_LAID, "0")));
//        } catch (Exception e) {
//            samplePlots = 0;
//            e.printStackTrace();
//        }
        if (block_area != null && !block_area.equalsIgnoreCase(""))
            samplePlots = Integer.parseInt(block_area);
        Log.e("adcSDC",""+block_type);
        if (block_type != null) {
            if (block_type.equals("Linear")) {
                mTvPolyline.setVisibility(View.VISIBLE);
                mTvPolygon.setVisibility(View.GONE);
                mIsPolygon = false;
                mCvPolygon.setCardBackgroundColor(Color.WHITE);
                mCvPolyline.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvPolygon.setTextColor(Color.BLACK);
                mTvPolyline.setTextColor(Color.WHITE);
//                            Toast.makeText(mContext, "Polyline is Pressed", Toast.LENGTH_SHORT).show();
                pref.edit().putBoolean("Perambulation_Button_Pressed", true).apply();
            } else if (block_type.equals("Block")) {
                mTvPolyline.setVisibility(View.GONE);
                mTvPolygon.setVisibility(View.VISIBLE);
                mIsPolygon = true;
                mCvPolygon.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mCvPolyline.setCardBackgroundColor(Color.WHITE);
                mTvPolygon.setTextColor(Color.WHITE);
                mTvPolyline.setTextColor(Color.BLACK);
//                            Toast.makeText(mContext, "Polygon is Pressed", Toast.LENGTH_SHORT).show();
                pref.edit().putBoolean("Perambulation_Button_Pressed", true).apply();
            } else {
                mTvPolyline.setVisibility(View.VISIBLE);
                mTvPolygon.setVisibility(View.VISIBLE);
            }
        } else {
            mTvStartStop.setVisibility(View.GONE);
            mTvPolyline.setVisibility(View.GONE);
            mTvPolygon.setVisibility(View.GONE);
        }
        folderName = pref.getString(Database.FOLDER_NAME, "");
    }


    @Override
    protected void initListeners() {
        mMapFragment.getMapAsync(this);
        mTvPolyline.setOnClickListener(this);
        mTvPolygon.setOnClickListener(this);
        mTvStartStop.setOnClickListener(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5 * 1000);
        mLocationRequest.setFastestInterval(1000);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.clearMap:

                new AlertDialog.Builder(mContext).setTitle("Alert!")
                        .setMessage("Clear Map will clear perambulation data also?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            Toast.makeText(mContext, "Map is Cleared", Toast.LENGTH_SHORT).show();
                            mMap.clear();
                            mLatLngCollection.clear();
                            mTempCollection.clear();
                            pref.edit().remove(Database.GPS_LATITUDE).apply();
                            pref.edit().remove(Database.GPS_LONGITUDE).apply();
                            pref.edit().remove(Database.GPS_SAMPLEPLOT_COLLECTION).apply();
                            pref.edit().remove(Database.GPS_LATLONG_COLLECTION).apply();
                            removeMarker();
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                            dialog.dismiss();
                        })
                        .show();

                break;

            case R.id.takePhoto:
                ImageGrid imageGrid = new ImageGrid();
                Bundle bundle = new Bundle();
                bundle.putString("imageFolderName", folderName);
                if (pref.getString(Database.FORM_TYPE, "").equals(Constants.FORMTYPE_SDP))
                    bundle.putString("formId", pref.getString(Database.BENEFICIARY_ID, "0"));
                else
                    bundle.putString("formId", pref.getString(Database.FORM_ID, "0"));
                imageGrid.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.fragmentContainer, imageGrid, "ImageGrid");
                transaction.addToBackStack("ImageGrid");
                transaction.commit();
                break;

            case R.id.removeMarker:
                removeMarker();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    void removeMarker() {
        if (mMarkerList.size() > 0) {
            for (Marker marker : mMarkerList) {
                marker.remove();
            }
            mMarkerList.clear();
            Toast.makeText(mContext, "Markers are Removed", Toast.LENGTH_SHORT).show();
        }
        if (mTempCollection.size() > 0)
            mTempCollection.clear();
        pref.edit().remove(Database.GPS_SAMPLEPLOT_COLLECTION).apply();
        polyline.remove();
        mSamplePlots.clear();
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected boolean isHideActionbar() {
        return false;
    }

    @Override
    protected boolean displayHomeEnabled() {
        return true;
    }

    @Override
    public String title() {
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mLatLngCollection = new ArrayList<>();
        mTempCollection = new ArrayList<>();
        mSamplePlots = new HashMap<>();
        mMap = googleMap;
        gpsDialog();
        sharedPreferences = getSharedPreferences(Constants.LOGIN_PREF, Context.MODE_PRIVATE);
        if (Objects.requireNonNull(sharedPreferences.getString("mapTouch", "")).isEmpty())
            showSequence();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


        mMap.setOnMapClickListener(latLng -> {

            if (pref.getString(Database.FORM_TYPE, "").equals(Constants.FORMTYPE_PLANTSAMPLING) && (mIsPolygon || mType[0].equals("Polygon")|| mType[0].equals("Polyline"))) {
                boolean inside = PolyUtil.containsLocation(latLng, mLatLngCollection, true);
                if (mSamplePlots.size() < 2) {
                    if (inside)
                        alertDialog(latLng);
                    else
                        Toast.makeText(mContext, "Please click inside a polygon to place a marker", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MapGps_Individual.this, "You Already Placed Two Markers ", Toast.LENGTH_SHORT).show();

            }


        });


        mPolygonOptions = new PolygonOptions()
                .addAll(mLatLngCollection);
        mPolylineOptions = new PolylineOptions()
                .addAll(mLatLngCollection);
        mMarkerPolyline = new PolygonOptions();
        try {
            setUpPolyLine(mData, mType[0]);
            loadSamplePlots();
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);


    }

    public void loadSamplePlots() {
        String json = pref.getString(Database.GPS_SAMPLEPLOT_COLLECTION, "");
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, LatLng>>() {
            }.getType();
            mSamplePlots = gson.fromJson(json, type);
        }
        for (Map.Entry<String, LatLng> e : mSamplePlots != null ? mSamplePlots.entrySet() : null) {
            if (e.getKey().equals("Starting point") || e.getKey().equals("Ending point")) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(e.getValue()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(e.getKey()));
                mMarkerList.add(marker);
            } else {
                LatLng latLng = e.getValue();
                String lat = String.format("%.5f", latLng.latitude);
                String lng = String.format("%.5f", latLng.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(e.getValue()).title(e.getKey()).snippet("Lat:" + lat + "\n Lng:" + lng));
                mMarkerList.add(marker);
            }
            mPolylineOptions.add(e.getValue());

        }
        polyline = mMap.addPolyline(mPolylineOptions);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    public void alertDialog(LatLng latLng) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Dialog on Android");
        dialog.setMessage("Are you sure you want to place here?");
        dialog.setPositiveButton("Ok", (dialog1, id) -> {
                    //Action for "Delete".
                    String title = "";
                    if (mSamplePlots.size() == 0) {
                        title = "Starting point";
                    } else if (mSamplePlots.size() == 1)
                        title = "Ending point";
                    MarkerOptions markerOptions = new MarkerOptions().position(
                            new LatLng(latLng.latitude, latLng.longitude)).title(title);
                    float plotmarkers;
                    Marker marker1 = mMap.addMarker(markerOptions);
                    mSamplePlots.put(title, latLng);
                    mTempCollection.add(latLng);
                    mPolylineOptions.getPoints().clear();
                    mPolylineOptions.addAll(mTempCollection);
//                    mMarkerList.add(marker1);
                    LatLngBounds latLngBounds = getPolygonCenterPoint(mTempCollection);
                    double distance = SphericalUtil.computeDistanceBetween(latLngBounds.southwest, latLngBounds.northeast);
                    plotmarkers = (float) (distance / (samplePlots + 1));
                    if(block_type.equalsIgnoreCase("Block")) {
                        polyline = mMap.addPolyline(mPolylineOptions);
                        marker1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    }
                    if (mSamplePlots.size() == 2) {
                        generateSamplePlots(plotmarkers);
                    }


                })
                .setNegativeButton("Cancel ", (dialog12, which) -> {
                    //Action for "Cancel".
                    dialog12.dismiss();
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    public void generateSamplePlots(float plotmarkers) {
        dist = plotmarkers;
        samplePlots = Integer.parseInt(block_area);
        for (int i = 0; i < samplePlots; i++) {
            LatLng distancemarker = extrapolate(mTempCollection, mTempCollection.get(0), dist);
            if(block_type.equalsIgnoreCase("Block")){
                if (distancemarker != null) {
                String lat = String.format("%.5f", distancemarker.latitude);
                String lng = String.format("%.5f", distancemarker.longitude);
                String name = "Sample Plot " + (i + 1);
                MarkerOptions markerOptions = new MarkerOptions().position(distancemarker).title(name).snippet("Lat:" + lat + "\nLng:" + lng);
                Marker samplePlotMarker = mMap.addMarker(markerOptions);
                mSamplePlots.put(name, distancemarker);
                mMarkerList.add(samplePlotMarker);
            }
            }
            dist = dist + plotmarkers;

        }
        Gson gson = new Gson();
        String json = gson.toJson(mSamplePlots);
        pref.edit().putString(Database.GPS_SAMPLEPLOT_COLLECTION, json).apply();
        updateTable();
    }

    private LatLng extrapolate(List<LatLng> path, LatLng origin, float distance) {
        LatLng extrapolated = null;

        if (!PolyUtil.isLocationOnPath(origin, path, false, 1)) { // If the location is not on path non geodesic, 1 meter tolerance
            return null;
        }

        float accDistance = 0f;
        boolean foundStart = false;
        List<LatLng> segment = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {
            LatLng segmentStart = path.get(i);
            LatLng segmentEnd = path.get(i + 1);

            segment.clear();
            segment.add(segmentStart);
            segment.add(segmentEnd);

            double currentDistance = 0d;

            if (!foundStart) {
                if (PolyUtil.isLocationOnPath(origin, segment, false, 1)) {
                    foundStart = true;

                    currentDistance = SphericalUtil.computeDistanceBetween(origin, segmentEnd);

                    if (currentDistance > distance) {
                        double heading = SphericalUtil.computeHeading(origin, segmentEnd);
                        extrapolated = SphericalUtil.computeOffset(origin, distance - accDistance, heading);
                        break;
                    }
                }
            } else {
                currentDistance = SphericalUtil.computeDistanceBetween(segmentStart, segmentEnd);

                if (currentDistance + accDistance > distance) {
                    double heading = SphericalUtil.computeHeading(segmentStart, segmentEnd);
                    extrapolated = SphericalUtil.computeOffset(segmentStart, distance - accDistance, heading);
                    break;
                }
            }

            accDistance += currentDistance;
        }

        return extrapolated;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isLocationUpdateStarted)
                        requestLocationUpdates();
                }
            }
        }
    }


    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);
        mLastKnownLocation = location;
        if (!mInitialZoom && location != null)
            initialZoom(location);
        if (isLocationUpdateStarted) {
            if (location != null) {
                mLatLngCollection.add(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            addPoly();
        }

    }

    private void initialZoom(Location location) {
        mInitialZoom = true;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));


    }

    private void addPoly() {
        if (mLatLngCollection.size() != 0) {
            if (mIsPolygon) {
                mPolygonOptions.getPoints().clear();
                mPolygonOptions.addAll(mLatLngCollection);
                mPolygonOptions.strokeWidth(7);
                mPolygonOptions.strokeColor(Color.BLUE);
                mPolygonOptions.fillColor(Color.CYAN);
                mMap.addPolygon(mPolygonOptions);
            } else {
                mPolylineOptions.getPoints().clear();
                mPolylineOptions.addAll(mLatLngCollection);
                mMap.addPolyline(mPolylineOptions);
            }
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            showToast("Application requires location permission");
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void removeLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvPolygon:
                mIsPolygon = true;
                mCvPolygon.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mCvPolyline.setCardBackgroundColor(Color.WHITE);
                mTvPolygon.setTextColor(Color.WHITE);
                mTvPolyline.setTextColor(Color.BLACK);
                Toast.makeText(mContext, "Polygon is Pressed", Toast.LENGTH_SHORT).show();
                pref.edit().putBoolean("Perambulation_Button_Pressed", true).apply();
                addPoly();
                break;
            case R.id.tvPolyline:
                mIsPolygon = false;
                mCvPolygon.setCardBackgroundColor(Color.WHITE);
                mCvPolyline.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvPolygon.setTextColor(Color.BLACK);
                mTvPolyline.setTextColor(Color.WHITE);
                Toast.makeText(mContext, "Polyline is Pressed", Toast.LENGTH_SHORT).show();
                pref.edit().putBoolean("Perambulation_Button_Pressed", true).apply();
                addPoly();
                break;
            case R.id.tvStartStop:

                if (!pref.getBoolean("Perambulation_Button_Pressed", false)) {
                    Toast.makeText(mContext, "Choose Perambulation Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mTvStartStop.getText().toString().equalsIgnoreCase("start")) {

                    startPerambulation();


                } else if (mTvStartStop.getText().toString().equalsIgnoreCase("stop")) {
                    isLocationUpdateStarted = false;
                    Toast.makeText(mContext, "Stopped", Toast.LENGTH_SHORT).show();
                    removeLocationUpdates();
                    saveMarkerData(mIsPolygon);
                    mTvStartStop.setText("Start");
                    if (block_type.equals("Linear"))
                        drawPolylineonMap(mLatLngCollection);
                }
                break;
        }
    }

    private void startPerambulation() {
        if (mLastKnownLocation != null) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("Perambulation Starting Point").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            pref.edit().putString(Database.GPS_LATITUDE, String.valueOf(mLastKnownLocation.getLatitude())).apply();
            pref.edit().putString(Database.GPS_LONGITUDE, String.valueOf(mLastKnownLocation.getLongitude())).apply();
            isLocationUpdateStarted = true;
            Toast.makeText(mContext, "Started", Toast.LENGTH_SHORT).show();
            requestLocationUpdates();
            mTvStartStop.setText("Stop");
        } else
            Toast.makeText(mContext, "Detecting Current Location", Toast.LENGTH_SHORT).show();
    }
    private void drawPolylineonMap(List<LatLng> list) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.BLACK);
        polyOptions.width(5);
        polyOptions.addAll(list);
//            mMap.clear();
        mMap.addPolyline(polyOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 12);
        mMap.animateCamera(cu);
        LatLng thisPoint = new LatLng(list.get(0).latitude, list.get(0).longitude);
//        Polyline site = mMap.addPolyline(new PolylineOptions()
//                .add(thisPoint, new LatLng(list.get(list.size() - 1).latitude, list.get(list.size() - 1).longitude))
//                .width(5)
//                .color(Color.BLACK));
        mTvPolygon.setVisibility(View.GONE);
        mTvPolyline.setVisibility(View.GONE);
        mTvStartStop.setVisibility(View.GONE);
        float plotmarkers;
        mTempCollection.addAll(list);
        mPolylineOptions.getPoints().clear();
        mPolylineOptions.addAll(list);
        LatLngBounds latLngBounds = getPolygonCenterPoint(list);
        double distance = SphericalUtil.computeDistanceBetween(latLngBounds.southwest, latLngBounds.northeast);
        plotmarkers = (float) (distance / (samplePlots + 1));
        polyline = mMap.addPolyline(mPolylineOptions);
        generateSamplePlots(plotmarkers);
    }


    void saveMarkerData(boolean mIsPolygon) {
        StringBuilder format = new StringBuilder();
        for (LatLng latLng : mLatLngCollection) {
            format.append(latLng.latitude).append(",").append(latLng.longitude).append("|");

        }

        try {
            format = new StringBuilder(format.substring(0, format.length() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mData = format.toString();
        pref.edit().putString(Database.GPS_LATLONG_COLLECTION, mData).apply();
        if (mIsPolygon) {
            String area = null;
            try {
                double squareMeter = SphericalUtil.computeArea(mLatLngCollection);
                BigDecimal hectare = BigDecimal.valueOf(squareMeter / 10000);
                area = "Polygon: " + hectare;
            } catch (Exception e) {
                e.printStackTrace();
            }
            pref.edit().putString(Database.GPS_MEASUREMENT, area).apply();
            Log.i(TAG, "saveMarkerDataCom" + area);
        } else {
            String length = null;
            try {
                length = "Polyline: " + calcLenth(mLatLngCollection);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pref.edit().putString(Database.GPS_MEASUREMENT, length).apply();
        }
        updateTable();
     /*   else
            db.updateTableWithId(Database.TABLE_ADVANCEWORK, Database.FORM_ID, cv);*/
    }

    private void updateTable() {
        ContentValues cv = new ContentValues();
        cv.put(Database.GPS_LATLONG_COLLECTION, pref.getString(Database.GPS_LATLONG_COLLECTION, ""));
        cv.put(Database.GPS_SAMPLEPLOT_COLLECTION, pref.getString(Database.GPS_SAMPLEPLOT_COLLECTION, ""));
        cv.put(Database.FORM_ID, pref.getString(Database.FORM_ID, "0"));
        cv.put(Database.GPS_MEASUREMENT, pref.getString(Database.GPS_MEASUREMENT, ""));
        if (pref.getString(Database.FORM_TYPE, "").equals(Constants.FORMTYPE_PLANTSAMPLING)) {
            for (int i = 1; i <= samplePlots; i++) {
                if (status.equalsIgnoreCase("true")) {
                    int survey_id = Integer.parseInt(String.valueOf(db.getSurvey_id(pref.getString(Database.FORM_ID, ""), "0")));
                    ContentValues cv1 = new ContentValues();
                    cv1.put(Database.BLOCK_NUMBER_NEW, "all");
                    long status1 = db.updateTableWithId_sample(Database.TABLE_SAMPLE_PLOT_MASTER, String.valueOf(survey_id), cv1);
                    if (i == samplePlots)
                        status = "false";
                }
            }
            db.updateTableWithId(Database.TABLE_PLANTATION, Database.FORM_ID, cv);
        }
        else if (pref.getString(Database.FORM_TYPE, "").equals(Constants.FORMTYPE_SDP)) {
            cv.put(Database.BENEFICIARY_ID, pref.getString(Database.BENEFICIARY_ID, "0"));
            db.updateTableWithId(Database.TABLE_BENEFICIARY, Database.BENEFICIARY_ID, cv);
        }
    }

    private void setUpPolyLine(String datas, String s) {
        Log.i(TAG, "setUpPolyLine: " + datas);
        if (!TextUtils.isEmpty(datas)) {
            List<LatLng> tt = getMarkerData(datas);
            mLatLngCollection.addAll(tt);

            if (s.equalsIgnoreCase("Polygon")) {
                mPolygonOptions.getPoints().clear();
                mPolygonOptions.addAll(mLatLngCollection);
                mPolygonOptions.strokeColor(Color.BLUE);
                mPolygonOptions.strokeWidth(7);
                mPolygonOptions.fillColor(Color.CYAN);
                mMap.addPolygon(mPolygonOptions);
            } else {
                mPolylineOptions.getPoints().clear();
                mPolylineOptions.addAll(mLatLngCollection);
                mMap.addPolyline(mPolylineOptions);
            }

        }

    }

    private LatLngBounds getPolygonCenterPoint(List<LatLng> polygonPointsList) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < polygonPointsList.size(); i++) {
            builder.include(polygonPointsList.get(i));
        }

        return builder.build();
    }


    private Location convert(LatLng latLng) {
        Location location = new Location("d");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    private double calcLenth(List<LatLng> latLngs) {

        Location prev = convert(latLngs.get(0));
        //Location.distanceBetween();
        double length = 0;

        for (int i = 1; i < latLngs.size(); i++) {
            Location location = convert(latLngs.get(i));
            float v = prev.distanceTo(location);
            length = length + v;
            prev = location;
        }
        return length;
    }


    public List<LatLng> getMarkerData(String data) {
        ArrayList<LatLng> latLngs = new ArrayList<>();

        String[] latlng = data.split("\\|");
        if (latlng.length != 0) {

            for (String l : latlng) {
                String[] latLags = l.split(",");

                Double lat = Double.parseDouble(latLags[0]);
                Double lng = Double.parseDouble(latLags[1]);

                LatLng latLng = new LatLng(
                        lat, lng);

                latLngs.add(latLng);
            }
        }
        return latLngs;
    }


    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (isLocationUpdateStarted)
                new AlertDialog.Builder(mContext).setTitle("Plotting is in progress!")
                        .setMessage("Are you sure you want to cancel?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // continue with delete
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                            dialog.dismiss();
                        })
                        .show();

            else
                super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void gpsDialog() {
        if (!mLocationManagerService.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case RESULT_OK:
                    showToast("Gps Enabled");

                    break;
                case RESULT_CANCELED:
                    gpsDialog();
                    break;
            }
        }
    }

    private void showSequence() {
        new MaterialTapTargetSequence()
                .addPrompt(new MaterialTapTargetPrompt.Builder(MapGps_Individual.this)
                        .setTarget(findViewById(R.id.cvPolyline))
                        .setPrimaryText("Step 1")
                        .setSecondaryText("Choose Polyline or")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(MapGps_Individual.this)
                        .setTarget(findViewById(R.id.cvPolygon))
                        .setPrimaryText("Step 2")
                        .setSecondaryText(" Polygon")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(MapGps_Individual.this)
                        .setTarget(findViewById(R.id.cvStartStop))
                        .setPrimaryText("Step 3")
                        .setSecondaryText("Start or Stop Progress")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.dp40)
                        .create(), 4000)

                .show().setSequenceCompleteListener(() -> sharedPreferences.edit().putString("mapTouch", "1").apply());
    }


}
