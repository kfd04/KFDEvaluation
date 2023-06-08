package com.ngohung.form.el;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.ngohung.form.constant.HConstants;
import com.ngohung.form.el.validator.ValidationStatus;
import com.ngohung.form.util.GPSTracker;


public class HGpsElement extends HElement {
    protected int elType;
    private HTextEntryElement latitude,longitude,altitude,timestamp;

    public HGpsElement(String label, boolean required)
    {
        this.label = label;
        this.value = HConstants.BLANK;
        this.required = required;
        elType = HElementType.GPS_BUTTON;
    }

    @Override
    public void loadValueForUI(View v) {
        if(label == null) // only load value if there is something
            return;

        if(v instanceof Button){
            Button button = (Button) v;
            button.setText(label);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GPSTracker gpsTracker = new GPSTracker(v.getContext(), v);
                    gpsTracker.getLocation();
                    if (gpsTracker.canGetLocation()) {
                        if(latitude!=null) {
                            latitude.setValue(Double.toString(gpsTracker.getLatitude()));
                        }
                        if (longitude!=null){
                            longitude.setValue(Double.toString(gpsTracker.getLongitude()));
                        }
                        if (altitude!=null){
                            altitude.setValue(Double.toString(gpsTracker.getAltitude()));
                        }
                        if(timestamp!=null){
                            timestamp.setValue(Long.toString(System.currentTimeMillis() / 1000));
                            Log.d("HGPSTimestamp",String.valueOf(System.currentTimeMillis() / 1000));
                        }
                        if(Double.toString(gpsTracker.getLatitude()).equals("0.0")&&
                                Double.toString(gpsTracker.getLongitude()).equals("0.0")&&
                                Double.toString(gpsTracker.getAltitude()).equals("0.0")){

                        }else{
                            Snackbar snackbar = Snackbar
                                    .make(v, "Location Coordinates saved successfully", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            // showLocationSuccessAlert(v.getContext());

                        }
                    }/*else{
                        gpsTracker.showSnackBar();
                    }*/
                }
            });
        }

    }

    @Override
    public void saveValueFromUI(View v) {

    }

    @Override
    public ValidationStatus doValidationForUI(View v) {
        return null;
    }

    public int getElType() {
        return HElementType.GPS_BUTTON;
    }

    public void setLatitude(HTextEntryElement latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(HTextEntryElement longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(HTextEntryElement altitude) {
        this.altitude = altitude;
    }
    public void setCreationTimeStamp(HTextEntryElement timestamp) {
        this.timestamp = timestamp;
    }


}
