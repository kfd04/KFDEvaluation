package com.kar.kfd.gov.kfdsurvey.utils;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
//adb shell setprop debug.firebase.analytics.app com.setumis.com

public class Analytics {
    private static FirebaseAnalytics firebaseAnalytics;

    public static void intialize(Application application) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(application);
    }

    public static void track(String event) {
        track(event, null);
    }

    public static void track(String event, Bundle bundle) {
        firebaseAnalytics.logEvent(event, bundle);
    }

    public interface Param {
        String SYNC_DATA = "sync_visit_data";
        String SYNC_ALL_DATA = "sync_all_visit_data";
        String REASON = "REASON";
        String NO_INTERNET = "NO_INTERNET";
        String OTHER = "OTHER";
        String LOGIN = "login";
    }

    public interface AnalyticsEvents {
        String Login = "login_sucess";
        String USER_SESSION_EXPIRED = "session_expired";
        String USER_LOGOUT = "user_logout";
        String USER_SUBMIT_LOG = "user_submit_log";
        String USER_SYNC_ALL_DATA = "form_uploading";
        String USER_SYNC_DATA = "user_sync_data";
        String USER_SYNC_SUCCESS = "form_upload_success";
        String USER_SYNC_FAILED = "form_upload_failed";
        String USER_CHANGE_PASSWORD = "user_change_password";
        String USER_FORGOT_PASSWORD = "user_forgot_password";

    }

}