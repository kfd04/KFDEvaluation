package com.kar.kfd.gov.kfdsurvey.network;


import android.text.TextUtils;

import androidx.annotation.CallSuper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ngohung.form.FormApplication;

public class VolleyNetworkUtils extends FormApplication {

    public static final String TAG = VolleyNetworkUtils.class
            .getSimpleName();
    private static VolleyNetworkUtils mInstance;
    private RequestQueue mRequestQueue;


    public static synchronized VolleyNetworkUtils getInstance() {
        return mInstance;
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
// set the default tag if tag is empty
        req.setShouldCache(false);
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
