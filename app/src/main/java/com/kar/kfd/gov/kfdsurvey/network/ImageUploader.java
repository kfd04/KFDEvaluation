package com.kar.kfd.gov.kfdsurvey.network;

import android.content.Context;
import android.util.Log;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;
import com.kar.kfd.gov.kfdsurvey.plantation.SamplePlotSurvey;
import com.kar.kfd.gov.kfdsurvey.plantation.smc.SmcPlantationSampling;
import com.kar.kfd.gov.kfdsurvey.scptsp.SCPTSPBeneficiary;
import com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey;
import com.kar.kfd.gov.kfdsurvey.sdp.SDPBeneficiarySurvey;

import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Modified by Sarath
 */
class ImageUploader {
    private static final String SERVER_KEY = "savePhoto";
    private static final int SUCCESS = 1;
    private static final int FAILURE = 0;
    private static final int INVALID_PARAMETER = -1;
    private final SurveyData surveyData;
    private Context context;
    private Database db;
    private boolean block = false;// for single file
    private boolean sCalled = false;//for new form


    ImageUploader(Context context, SurveyData surveyData) {
        this.context = context;
        db = new Database(context);
        this.surveyData = surveyData;

    }

    void upload(String formIdStr) {
            if (!sCalled) {
                int formId = Integer.parseInt(formIdStr);
                if (formId == 0) {
                    return;
                }
                String formType = db.getFormType(formId);
                ArrayList<File> files = null;
                switch (formType) {

                    case Constants.FORMTYPE_PLANTSAMPLING:
                        files = getPlantationSamplingImages(formId);
                        break;
                    case Constants.FORMTYPE_SDP:

                        files = getSDPImages(formId);
                        break;

                    case Constants.FORMTYPE_SCPTSP:
                        files = getSCPTSPImages(formId);
                        break;

                    case Constants.FORMTYPE_OTHERWORKS:
                        files = readFileNames(getFullPath(formType, String.valueOf(formId)));
                        break;
                }
                uploadSerially(formType, formId, files);
            }


    }

    private ArrayList<File> getSDPImages(int formId) {
        ArrayList<File> imageFiles = new ArrayList<>();
        Log.i(Constants.SARATH, "getSDPImagesformId: " + formId);
        int[] sdpBenIds = db.getSDPBenefciaries(formId);
        int j = 1;
        String sdpdir = SDPBeneficiarySurvey.folderName;
        for (Integer i : sdpBenIds) {
            File sdpBenPath = getFullPath(sdpdir, String.valueOf(i));
            ArrayList<File> sdpBenFileList = new ArrayList<>(changeFileNames("SDPBENID-" + i + "-Photo-" + formId + "-", readFileNames(sdpBenPath)));
            imageFiles.addAll(sdpBenFileList);
            j++;
        }
        return imageFiles;
    }

    private ArrayList<File> getSCPTSPImages(int formId) {
        ArrayList<File> imageFiles = new ArrayList<>();
        Log.i(Constants.SARATH, "getSCPTSPImagesformId: " + formId);
        /*loading community images*/
        String communityDir = ScpTspSamplingSurvey.folderName;
        int[] assetIds = db.getSCPTSPAssetIds(String.valueOf(formId), ScpTspSamplingSurvey.COMMUNITY);

        for (int id : assetIds) {
            File SCPTSPCommunityPath = getFullPath(communityDir, String.valueOf(id));
            ArrayList<File> communityAssetFileList = new ArrayList<>(changeFileNames("SCPTSPCommunityasset-" + id + "-Photo-" + formId + "-", readFileNames(SCPTSPCommunityPath)));
            imageFiles.addAll(communityAssetFileList);
        }

        /*loading beneficiary images*/
        String individualDir = SCPTSPBeneficiary.folderName;
        int[] beneficiaryId = db.getSCPTSPBeneficiaries(formId);

        for (int id : beneficiaryId) {
            File SCPTSPIndividaulPath = getFullPath(individualDir, String.valueOf(id));
            ArrayList<File> individualFileList = new ArrayList<>(changeFileNames("SCPTSPIndividualBen-" + id + "-Photo-" + formId + "-", readFileNames(SCPTSPIndividaulPath)));
            imageFiles.addAll(individualFileList);
         /* for (int benId : beneficiaryId){
              File SCPTSPIndividaulPath = getFullPath(individualDir,String.valueOf(benId));
              ArrayList<File> individualFileList = new ArrayList<>(changeFileNames("SCPTSPIndividual-"+id+"-"+benId,readFileNames(SCPTSPIndividaulPath)));
              imageFiles.addAll(individualFileList);
          }*/
        }

        return imageFiles;
    }

    private ArrayList<File> getPlantationSamplingImages(int formId) {

        ArrayList<File> imageFiles = new ArrayList<>();
        Log.i(Constants.SARATH, "getPlantationSamplingImagesformId: " + formId);
        //loading smc images files
        String smcSurveyDir = SmcPlantationSampling.folderName;
        int[] smcIds = db.getSMCIds(String.valueOf(formId));
        for (Integer i : smcIds) {
            File smcPathFull = getFullPath(smcSurveyDir, String.valueOf(i));
            ArrayList<File> smcFileList = new ArrayList<>(changeFileNames("SMC-" + i + "-Photo-" + formId + "-", readFileNames(smcPathFull)));
            imageFiles.addAll(smcFileList);
        }

        //loading plantation evaluation images files
        String plntEvalDir = PlantationSamplingEvaluation.folderName;
        File plntEvalPathFull = getFullPath(plntEvalDir, String.valueOf(formId));
        ArrayList<File> plntEvalFileList = new ArrayList<>(changeFileNames("Evaluation-" + formId + "-", readFileNames(plntEvalPathFull)));
        imageFiles.addAll(plntEvalFileList);

        //loading plantation evaluation images files
        int[] samplePlotIds = db.getSamplePlotIds(String.valueOf(formId));
        String samplePlotDir = SamplePlotSurvey.folderName;
        int j = 1;
        for (Integer i : samplePlotIds) {
            File samplePlotPathFull = getFullPath(samplePlotDir, String.valueOf(i));
            ArrayList<File> samplePlotFileList = new ArrayList<>(changeFileNames("SamplePlot-" + j + "-Photo-" + formId + "-", readFileNames(samplePlotPathFull)));
            imageFiles.addAll(samplePlotFileList);
            j++;
        }
        int k = 1;
        String failedPlotDir = SamplePlotSurvey.failedFolderName;
        for (Integer i : samplePlotIds) {
            File failedPath = getFullPath(failedPlotDir, String.valueOf(i));
            ArrayList<File> failedFileList = new ArrayList<>(changeFileNames("SamplePlotFailed-" + k + "-Photo-" + formId + "-", readFileNames(failedPath)));
            imageFiles.addAll(failedFileList);
            k++;
        }

        return imageFiles;
    }

    private ArrayList<File> changeFileNames(String prefix, ArrayList<File> files) {
        int i = 1;
        ArrayList<File> fileList = new ArrayList<>();
        for (File file : files) {
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                String parent = file.getParent();
                File newFile = new File(parent + File.separator + prefix + i + ".jpg");
                inChannel = new FileInputStream(file).getChannel();
                outChannel = new FileOutputStream(newFile).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                fileList.add(newFile);
                if (file.exists()) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inChannel != null) {
                        inChannel.close();
                    }
                    if (outChannel != null) {
                        outChannel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            i++;
        }
        return fileList;
    }


    private void uploadSerially(String formType, int formId, ArrayList<File> files) {
        sCalled = true;
        while (true) {
            if (!block) {
                if (files.size() == 0) {
                    sCalled = false;
                    db.setImagesUploaded(formId);
                    db.setSurveyUploaded(String.valueOf(formId));
                    Log.d(Constants.SARATH, "uploadedFormId: " + formId);
                    surveyData.getDataUploadListener().onDataUpload();
                    //total
                    return;
                }

            /*    File file = null;
                try {
                    file = new Compressor(context).compressToFile(files.get(0));
                } catch (Exception e) {
                    //if comperss failed sent original
                    file = files.get(0);
                    e.printStackTrace();
                }*/
                File file = files.get(0);


                HashMap<String, String> formData = new HashMap<>();
                formData.put("form_id", String.valueOf(formId));
                formData.put("formtype", formType);
                formData.put("name", file.getName());
                if (formType.equals(Constants.FORMTYPE_SDP) || formType.equals(Constants.FORMTYPE_SCPTSP)) {
                    formData.put("ben_id", getId(file.getName()));
                }
                if (formType.equals(Constants.FORMTYPE_PLANTSAMPLING)) {
                    if (file.getName().startsWith("SamplePlot")) {
                        formData.put("ben_id", getId(file.getName()));
                    }
                    if (file.getName().startsWith("SMC")) {
                        formData.put("ben_id", getId(file.getName()));
                    }
                }
                HashMap<String, FileBody> imageFiles = new HashMap<>();
                FileBody localFileBody = new FileBody(file);
                imageFiles.put("photo_data", localFileBody);

//                File finalFile = file;
                block = true;
                MultipartWebservice webservice = new
                        MultipartWebservice(Constants.SERVER_URL + SERVER_KEY, formData, imageFiles, s -> {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int result = jsonObject.getInt(SERVER_KEY);
                        ImageUploader.this.onApiResponse(result, file, formId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                webservice.execute();


//               UploaderTask uploaderTask = new UploaderTask(formId);
//               uploaderTask.execute(file);

                files.remove(0);


            }
        }

    }

    private String getId(String name) {

        try {
            String[] id = name.split("-", 0);
            return id[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private ArrayList<File> readFileNames(File file) {

        String[] names = file.list((dir, filename) -> {
        /*    if (new File(dir, filename).isDirectory()) {
                return false;
            }
            return !filename.toLowerCase().startsWith("ok");*/
            //--------------- done by sunil ----------------

            //for not selecting the text file while changing the name
            if (new File(dir, filename).isDirectory()) {
                return false;
            }
            if (filename.toLowerCase().startsWith("ok")) {
                return false;
            }
            if (filename.lastIndexOf('.') > 0) {
                int lastIndex = filename.lastIndexOf('.');
                String str = filename.substring(lastIndex);
                return str.equals(".jpg");
            }
            return false;
            //------------------------------------------------
        });

        ArrayList<File> files = new ArrayList<>();
        if (names != null && names.length > 0) {
            for (String name : names) {
                files.add(new File(file + File.separator + name));
            }
        }
        return files;
    }

   /* private class UploaderTask extends AsyncTask<File, Void, String> {

        private final int formId;
        private File cFile = null;

        public UploaderTask(int formId) {
            this.formId = formId;
        }

        @Override
        protected String doInBackground(File... params) {
            Log.d("Uploader", "do in background " + formId + "");
            block = true;
            cFile = params[0];


            OkHttpClient okHttpClient = getHttpClient();


            // String workCode = db.getWorkCode(formId);

            RequestBody rBody = getRequestBody(String.valueOf(formId), params[0]);

            Request request = createRequest(rBody);
            try {
                Log.i("sarath", "doInBackground: " + request.body().contentLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String responseMsg = null;
            try {
                Response response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    responseMsg = response.body().string();
                }
            } catch (IOException e) {
                error = true;
                e.printStackTrace();
            }
            Log.i("sarath", "doInBackground: " + responseMsg);
            return responseMsg;
        }

        private Request createRequest(RequestBody rBody) {
            return new Request.Builder()
                    .url(Constants.SERVER_URL + SERVER_KEY)
//                    .url("http://192.168.50.108/index1.php")
                    .addHeader("content-type", "multipart/form-data")
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .post(rBody)
                    .build();
        }

        @NonNull
        private MultipartBody getRequestBody(String formId, File file) {
            int appStatus = db.getAppStatus();

            String appApprovalStatus = "";

            if (appStatus == 1) {
                appApprovalStatus = "TEST";
            } else if (appStatus == 2) {
                appApprovalStatus = "PRDN";
            }
            Log.i("vvvv", "getRequestBody: " + file.getAbsolutePath());
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    // .addFormDataPart("work_code", workCode)
                    .addFormDataPart("form_id", formId)
                    .addFormDataPart("name", file.getName())
//                    .addFormDataPart("app_approval_status", appApprovalStatus)
                    .addFormDataPart("photo_data", file.getName(), RequestBody.create(MEDIA_TYPE, file)).build();
        }

        @NonNull
        private OkHttpClient getHttpClient() {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            return new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                int result = jsonObject.getInt(SERVER_KEY);
                onApiResponse(result,cFile);
                Log.d("Uploader", "on post exec " + s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }*/

    private void prefixFileName(File cFile) {
        Thread t = new Thread() {
            @Override
            public void run() {
                if (cFile != null && cFile.exists()) {
                    String path = cFile.getPath();
                    int index = path.lastIndexOf("/");
                    path = path.substring(0, index);
                    String name = "ok_" + cFile.getName();
                    File newFile = new File(path + File.separator + name);
                    cFile.renameTo(newFile);
                }
            }
        };
        t.run();
    }

    private void onApiResponse(int result, File cFile, int formId) {
        switch (result) {
            case SUCCESS:
                block = false;
                Log.d(Constants.SARATH, "on api response success");
                prefixFileName(cFile);
                break;
            case FAILURE:
                block = false;
                Log.i(Constants.SARATH, "onApiResponse failure: ");
                break;
            case INVALID_PARAMETER:
                block = false;
                Log.i(Constants.SARATH, "onApiResponse invalid: ");
                break;
        }
    }


    private File getFullPath(String folderName, String formId) {
        String photoDirectory = context.getExternalFilesDir(null) + "/Photo/";
        return new File(photoDirectory + File.separator + folderName + File.separator + formId);

    }




}
