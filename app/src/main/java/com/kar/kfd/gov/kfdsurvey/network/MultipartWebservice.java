package com.kar.kfd.gov.kfdsurvey.network;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MultipartWebservice extends AsyncTask<Void, Void, String> {

    private static final String TAG = "MultipartWebservice";
    private String url;
    private dataFetched dataFetched;
    private HashMap<String, String> normalFields;
    private HashMap<String, FileBody> imageFields;

    public MultipartWebservice(String url, HashMap<String, String> normalFields, HashMap<String, FileBody> imageFields, dataFetched dataFetched) {

        this.url = url;
        this.normalFields = normalFields;
        this.imageFields = imageFields;
        this.dataFetched = dataFetched;
        Log.i(TAG, "MultipartWebservice: ");

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "onPreExecute: ");
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i(TAG, "doInBackground: ");
        try {

            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3 * 60 * 1000);
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3 * 60 * 1000);
            HttpPost post = new HttpPost(url);
            MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            //Add the data to the multipart entity
            if (imageFields != null) {
                for (Map.Entry<String, FileBody> entry : imageFields.entrySet()) {
                    String key = entry.getKey();
                    FileBody value = entry.getValue();
                    // do stuff
                    mpEntity.addPart(key, value);
                }
            }

            if (normalFields != null) {

                for (Map.Entry<String, String> entry : normalFields.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    // do stuff
                    mpEntity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
                }
            }

            post.setEntity(mpEntity);
            //Execute the post request
            HttpResponse response1 = client.execute(post);
            //Get the response from the server
            HttpEntity resEntity = response1.getEntity();
            String response = EntityUtils.toString(resEntity);
            Log.d("response", "is" + response);
            //Close the connection
            client.getConnectionManager().shutdown();

            return response;
        } catch (Exception e) {
            Log.i(TAG, "doInBackground: ee " + e);

            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i(TAG, "onPostExecute: " + s);
        dataFetched.onDataFetched(s);
//        progressDialog.cancel();
    }

    public interface dataFetched {
        void onDataFetched(String response);
    }
}