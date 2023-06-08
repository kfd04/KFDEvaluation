package com.kar.kfd.gov.kfdsurvey.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;

import java.util.Calendar;

import static com.kar.kfd.gov.kfdsurvey.constants.Constants.SARATH;

public class FloatingWindow extends Service {

    WindowManager wm;
    RelativeLayout window_root;
    LinearLayout window_header;
    int LAYOUT_FLAG;
    View mFloatingView;
    ImageView imageClose;
    double width, height;
    ImageView openapp;
    EditText content_text;
    WindowManager.LayoutParams layoutParams;
    private boolean wasInFocus = true;
    public static boolean started = false;
    SharedPreferences sharedPreferences;
    String preference;

    @Override
    public void onCreate() {
        super.onCreate();
        init();

        content_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString(Database.EXTRA_NOTE, s.toString()).apply();
            }
        });
    }


    private void init() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null);

        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_close_white);
        imageClose.setVisibility(View.INVISIBLE);

        window_root = mFloatingView.findViewById(R.id.window_root);

        window_header = mFloatingView.findViewById(R.id.window_header);
        openapp = mFloatingView.findViewById(R.id.openapp);
        content_text = mFloatingView.findViewById(R.id.content_text);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(SARATH, "onStartCommand: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;


        preference = intent.getExtras().getString(Database.PREFERENCE, PlantationSamplingEvaluation.BASIC_INFORMATION);
        sharedPreferences = this.getApplicationContext().getSharedPreferences(preference, MODE_PRIVATE);
        content_text.setText(sharedPreferences.getString(Database.EXTRA_NOTE, ""));
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
                , LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.END;
        layoutParams.x = 0;
        layoutParams.y = 100;

        final WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

       /* openapp = new ImageView(this);
        openapp.setImageResource(R.mipmap.ic_launcher_round);
        ViewGroup.LayoutParams butnparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        openapp.setLayoutParams(butnparams);*/


//        window_root.addView(openapp);
        wm.addView(imageClose, imageParams);
        wm.addView(mFloatingView, layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);
        height = wm.getDefaultDisplay().getHeight();
        width = wm.getDefaultDisplay().getWidth();


        window_root.setOnTouchListener(new View.OnTouchListener() {
            //            WindowManager.LayoutParams updatepar = layoutParams;
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            long startClickTime;
            int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        imageClose.setVisibility(View.VISIBLE);

                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        imageClose.setVisibility(View.GONE);
                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        if (clickDuration < MAX_CLICK_DURATION) {
                            if (openapp.getVisibility() == View.VISIBLE) {
                                openapp.setVisibility(View.GONE);
                                window_header.setVisibility(View.VISIBLE);
                                editTextReceiveFocus();
                            } else {
                                editTextDontReceiveFocus();
                                openapp.setVisibility(View.VISIBLE);
                                window_header.setVisibility(View.GONE);
                            }
                        } else {
                            if (layoutParams.y > (height * 0.6))
                                stopSelf();
                            started = false;
                        }

                        return true;

                    case MotionEvent.ACTION_MOVE:

                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        wm.updateViewLayout(mFloatingView, layoutParams);

                        if (layoutParams.y > (height * 0.6))
                            imageClose.setImageResource(R.drawable.ic_close_red);
                        else
                            imageClose.setImageResource(R.drawable.ic_close_white);

                        return true;

                    default:
                        break;
                }

                return false;

            }
        });


/*        content_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this, "Hello", Toast.LENGTH_SHORT).show();
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
                wm.updateViewLayout(mFloatingView,layoutParams);
            }
        });*/

        return START_STICKY;
    }





    private void editTextReceiveFocus() {
        if (!wasInFocus) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            wm.updateViewLayout(mFloatingView, layoutParams);
            wasInFocus = true;
        }
    }

    private void editTextDontReceiveFocus() {
        if (wasInFocus) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            wm.updateViewLayout(mFloatingView, layoutParams);
            wasInFocus = false;
            hideKeyboard(this, content_text);
        }
    }

    private void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        started = false;
        if (window_root != null) {
            wm.removeView(mFloatingView);
        }
        if (imageClose != null) {
            wm.removeView(imageClose);
        }
    }
}
