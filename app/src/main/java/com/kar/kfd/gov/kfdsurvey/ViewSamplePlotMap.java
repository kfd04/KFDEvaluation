package com.kar.kfd.gov.kfdsurvey;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kar.kfd.gov.kfdsurvey.network.NetworkDetector;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


/**
 * Created by Sunil on 03-02-2017.
 */
public class ViewSamplePlotMap extends AppCompatActivity {

    WebView mapView;
    ProgressDialog pd;
    NetworkDetector mNetworkDetector;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_sample_plot_map);
        String serverFormId = getIntent().getStringExtra("SERVER_FORM_ID");
        String mode = getIntent().getStringExtra("MODE");
        String url;
        if(mode.equals("Prod")){
            url ="https://drive.google.com/viewerng/viewer?embedded=true&url=http://www.clefsoftware.com/sites/default/files/map-id-"+serverFormId+".pdf ";
        }else{
            url ="https://drive.google.com/viewerng/viewer?embedded=true&url=http://www.clefsoftware.com/sites/default/files/test_map-id-"+serverFormId+".pdf ";
        }
        mapView = findViewById(R.id.mapView);
        mNetworkDetector = new NetworkDetector(this);
        showMapView(url);
    }
    private void showMapView(String mapUrl){
        if(mNetworkDetector.detect()== true)
        {
            pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            //pd.show();

            mapView.getSettings().setJavaScriptEnabled(true);
            mapView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            mapView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mapView.getSettings().setDomStorageEnabled(true);
            //mapView.setOnLongClickListener(webListener);
            mapView.setWebViewClient(new WebViewClient()
            {
                @Override
                public void onPageStarted(WebView view, String url,Bitmap favicon) {
                    pd.show();
                }
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(url.startsWith("mailto:")){
                        MailTo mt = MailTo.parse(url);
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + mt.getTo()));
                        try {
                            startActivity(intent);
                        }catch(android.content.ActivityNotFoundException ex){
                            Toast.makeText(view.getContext(),"There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }else{
                        view.loadUrl(url);
                    }
                    return true;
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    pd.dismiss();
                }
            });

            mapView.setWebChromeClient(new WebChromeClient()
            {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {

                }
            });
            mapView.loadUrl(mapUrl);
        }
        else
        {
            Toast toast = Toast.makeText(this,"Not Connected To Internet", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0,0);

        }
    }
}
