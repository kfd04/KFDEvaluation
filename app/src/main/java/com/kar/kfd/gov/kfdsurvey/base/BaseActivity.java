package com.kar.kfd.gov.kfdsurvey.base;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


/**
 * Created by Shylesh on 06-Dec-17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutRes();

    protected abstract int getMenuRes();

    protected abstract void initUI();

    protected abstract void initListeners();

    protected abstract boolean isFullScreen();

    protected abstract boolean isHideActionbar();

    protected abstract boolean displayHomeEnabled();

    public abstract String title();

  //  public abstract boolean isIntentAvailable();

    protected final Context mContext = BaseActivity.this;
    private ProgressBar progressBar;
    private View mActivityView;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        View view = getLayoutInflater().inflate(R.layout.activity_sportz, null);
        FrameLayout layout = view.findViewById(R.id.root);
        progressBar = view.findViewById(R.id.progress_bar);
        mActivityView = getLayoutInflater().inflate(getLayoutRes(), null);
        layout.addView(mActivityView, 0);
        setContentView(layout);

        initUI();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (0 != getMenuRes()) getMenuInflater().inflate(getMenuRes(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
    }

    protected void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    }
}
