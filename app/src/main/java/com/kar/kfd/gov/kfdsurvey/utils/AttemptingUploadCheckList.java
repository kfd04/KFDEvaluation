package com.kar.kfd.gov.kfdsurvey.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttemptingUploadCheckList {

    private static final OkHttpClient okHttpClient;

    static {

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(15, TimeUnit.SECONDS); // connect timeout
        okHttpClientBuilder.readTimeout(15, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(10, TimeUnit.MINUTES);
        okHttpClient = okHttpClientBuilder.build();
    }


    public static String sendImageOld(String url, HashMap<String, File> imageFields, HashMap<String, String> normalFields) throws IOException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3 * 60 * 1000);
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3 * 60 * 1000);
        HttpPost post = new HttpPost(url);
//        MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        //Add the data to the multipart entity
        if (imageFields != null) {
            for (Map.Entry<String, File> entry : imageFields.entrySet()) {
                String key = entry.getKey();
                FileBody value = new FileBody(entry.getValue());
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
        HttpEntity multiPartEntity = mpEntity.build();

        post.setEntity(multiPartEntity);
        //Execute the post request
        HttpResponse response1 = client.execute(post);
        //Get the response from the server
        HttpEntity resEntity = response1.getEntity();
        String response = EntityUtils.toString(resEntity);
        Log.d("response", "is" + response);
        //Close the connection
        client.getConnectionManager().shutdown();

        return response;
    }


    public static String sendImage(String url, HashMap<String, File> imageFields, HashMap<String, String> normalFields) throws IOException {
        {
            final MediaType MEDIA_TYPE = MediaType.parse("image/*");
            MultipartBody.Builder requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);


            if (imageFields != null) {
                for (Map.Entry<String, File> entry : imageFields.entrySet()) {
                    String key = entry.getKey();
                    File value = entry.getValue();
                    requestBody.addFormDataPart(key, value.getName(), RequestBody.create(MEDIA_TYPE, value));


                }
            }


            if (normalFields != null) {
                for (Map.Entry<String, String> entry : normalFields.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    requestBody.addFormDataPart(key, value);

                    Log.i("mulit part", "doInBackground: " + key + "===" + value);
                }
            }

            Request request = new Request.Builder()
                    .url(url)
                    // .header("access-token", token)
                    .post(requestBody.build())
                    .build();

            Response response2 = okHttpClient.newCall(request).execute();
            return response2.body().string();
        }
    }


    public static String sendJsonObj(String Url, JSONObject jsonObject) throws IOException {

        String resStr;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // put your json here

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                // .header("access-token", token)
                .url(Url)
                .post(body)
                .build();

        Response response;
        response = okHttpClient.newCall(request).execute();
        resStr = response.body() != null ? response.body().string() : null;


        return resStr;
    }

    public static String getJsonObj(String Url) {

        String resStr = null;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // put your json here

        Request request = new Request.Builder()
                // .header("access-token", token)
                .url(Url)
                .get()
                .build();

        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            resStr = response.body() != null ? response.body().string() : null;
        } catch (IOException e) {
            // okHttpClient.connectionPool().evictAll();
            e.printStackTrace();
        }



        return resStr;
    }
}
