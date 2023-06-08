package com.kar.kfd.gov.kfdsurvey.network;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.advancework.AdvSamplePlotSurvey;
import com.kar.kfd.gov.kfdsurvey.advancework.PlantationSamplingAdvanceWork;
import com.kar.kfd.gov.kfdsurvey.advancework.smc.SmcAdvanceWork;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.otherworks.OtherSurvey;
import com.kar.kfd.gov.kfdsurvey.plantation.PlantationSamplingEvaluation;
import com.kar.kfd.gov.kfdsurvey.plantation.SamplePlotSurvey;
import com.kar.kfd.gov.kfdsurvey.plantation.smc.SmcPlantationSampling;
import com.kar.kfd.gov.kfdsurvey.scptsp.SCPTSPBeneficiary;
import com.kar.kfd.gov.kfdsurvey.scptsp.ScpTspSamplingSurvey;
import com.kar.kfd.gov.kfdsurvey.sdp.SDPBeneficiarySurvey;
import com.kar.kfd.gov.kfdsurvey.service.JIService;
import com.kar.kfd.gov.kfdsurvey.utils.AttemptingUploadCheckList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SurveyData {
    private Context context;
    private static final String SERVER_KEY = "saveData";
    private final Database db;
    private final NetworkDetector detector;
    private DataUploadListener dataUploadListener;
    private static final String TAG = Constants.SARATH;


    public SurveyData(Context context) {
        this.context = context;
        db = Database.initializeDB(context);
        detector = new NetworkDetector(context);
    }


    private void sendMessage(String mess) {
        Intent intent1 = new Intent(JIService.ACTION_SYNC);
        intent1.putExtra("data", "doing");
        intent1.putExtra("message", mess);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, JIService.CHANNEL_ID);
        builder.setContentTitle("KFD Evaluation")
                .setSmallIcon(R.mipmap.kfd_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentText(mess).setAutoCancel(true);
        manager.notify(1000, builder.build());
    }


    private JSONObject formJsonData(String formId, List<String> data) throws JSONException {

        final String masterData = data.get(1);
        final String surveyData = data.get(2);
/*        int appStatus = db.getAppStatus();
        String appApprovalStatus = "";

        if (appStatus == 1) {
            appApprovalStatus = "TEST";
        } else if (appStatus == 2) {
            appApprovalStatus = "PRDN";
        }
        final String finalAppApprovalStatus = appApprovalStatus;*/

        JSONObject map = new JSONObject();
        map.put("master_data", new JSONObject(masterData));

        //map.put("api_version", versionCode);
        //map.put("app_approval_status", finalAppApprovalStatus);

        String formType = db.getFormType(Integer.parseInt(formId));
        switch (formType) {
            case Constants.FORMTYPE_SDP:
                map.put("form_data2", new JSONObject(surveyData));
                map.put("beneficiaries_data", new JSONArray(data.get(3)));
                map.put("seedling_data", new JSONArray(data.get(4)));
                // map.put("photo_data", "[]");

//                        map.put("photo_data", getImageArray(Constants.FORMTYPE_SDP, formId));
                break;

            case Constants.FORMTYPE_PLANTSAMPLING:
                map.put("form_data1", new JSONObject(surveyData));
                map.put("plnt_species", new JSONArray(data.get(3)));
                map.put("sample_plot_master", new JSONArray(data.get(4)));
                map.put("sample_plot_species", new JSONArray(data.get(5)));
                map.put("control_plot_master", new JSONArray(data.get(6)));
                map.put("control_plot_details", new JSONArray(data.get(7)));
                map.put("smc_master", new JSONObject(data.get(8)));
                map.put("smc_list", new JSONArray(data.get(9)));
                map.put("other_smc", new JSONArray(data.get(10)));
                map.put("smc_highest", new JSONArray(data.get(11)));
                map.put("vfc", new JSONObject(data.get(12)));
                map.put("boundary_protection", new JSONArray(data.get(13)));


                break;

            case Constants.FORMTYPE_OTHERWORKS:
                map.put("form_data3", new JSONObject(surveyData));
                break;

            case Constants.FORMTYPE_SCPTSP:
                map.put("form_data4", new JSONArray(surveyData));
                map.put("indv_data", new JSONArray(data.get(3)));
                map.put("com_data", new JSONArray(data.get(4)));
                map.put("supply_seed", new JSONArray(data.get(5)));
                break;
            case Constants.FORMTYPE_ADVANCEWORK:
                map.put("form_data5", new JSONObject(surveyData));
                map.put("adv_sample_plot", new JSONArray(data.get(3)));
                map.put("adv_smc_master", new JSONObject(data.get(4)));
                map.put("adv_smc_list", new JSONArray(data.get(5)));
                map.put("adv_other_smc", new JSONArray(data.get(6)));
                map.put("adv_smc_highest", new JSONArray(data.get(7)));
                map.put("adv_vfc", new JSONObject(data.get(8)));
                map.put("adv_boundary", new JSONArray(data.get(9)));
                break;

            case Constants.FORMTYPE_NURSERY_WORK:
                map.put("form_data6", new JSONObject(surveyData));
                map.put("bagged_seedling", new JSONArray(data.get(3)));
                map.put("seed_bed", new JSONArray(data.get(4)));
                break;

        }
//        logLargeString(map.toString());
        return map;
    }

    public static void longLog(String str) {
        if (str.length() > 4000) {
            Log.d("", str.substring(0, 4000));
            longLog(str.substring(4000));
        } else
            Log.d("", str);
    }

    private void uploadForm(final ArrayList<String> data) throws Exception {
        String formId = data.get(0);

        String url = Constants.SERVER_URL + SERVER_KEY;
        JSONObject body = formJsonData(formId, data);
        Log.e("edcsedfcs",""+body);
        String resp = AttemptingUploadCheckList.sendJsonObj(url, body);
        System.out.println("uploadForm: " + url + " " + body);
        JSONObject response = new JSONObject(resp);
        int result;
        result = response.getInt("saveData");

        if (result == 1)
            uploadFormImages(formId);
        else if (result == 0) {
            throw new Exception();
        }


    }

    public boolean syncAll() {
        boolean isSuccess = true;

        ArrayList<ArrayList<String>> forms = db.getAllFormData();
        for (int i = 0, formsSize = forms.size(); i < formsSize; i++) {
            ArrayList<String> form = forms.get(i);
            try {
                sendMessage((i + 1) + "/" + formsSize + " Sending.....");
                uploadForm(form);

            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            }

        }
        return isSuccess;
    }


    private void uploadFormImages(String formIdStr) throws Exception {
        int formId = Integer.parseInt(formIdStr);
        if (formId == 0) {
            return;
        }
        String formType = db.getFormType(formId);
        ArrayList<File> files = new ArrayList<>();
        switch (formType) {

            case Constants.FORMTYPE_PLANTSAMPLING:
                files = getPlantationSamplingImages(formId);
                break;

            case Constants.FORMTYPE_ADVANCEWORK:
                files = getAdvanceWorkImages(formId);
                break;
            case Constants.FORMTYPE_SDP:

                files = getSDPImages(formId);
                break;

            case Constants.FORMTYPE_SCPTSP:
                files = getSCPTSPImages(formId);
                break;

            case Constants.FORMTYPE_OTHERWORKS:
                files = getOtherWorksImages(formId);//readFileNames(getFullPath(formType, String.valueOf(formId)));
                break;
            case Constants.FORMTYPE_NURSERY_WORK:
                files = getNurseryWorkImages(formId);
                break;
        }
        uploadImageFiles(formType, formId, files);

    }


    private void uploadImageFiles(String formType, int formId, ArrayList<File> files) throws Exception {
        int size = files.size();
        if (size == 0) {
            db.setImagesUploaded(formId);
            db.setSurveyUploaded(String.valueOf(formId));
        }
        for (int i = 0; i < size; i++) {
            File file = files.get(i);

            HashMap<String, String> formData = new HashMap<>();
            formData.put("form_id", String.valueOf(formId));
            formData.put("formtype", formType);
            formData.put("name", file.getName());
            formData.put("latitude","");
            formData.put("longitude","");
            formData.put("altitude","");
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
     /*       File opt=null;

            try {
                Log.i(Constants.SARATH, "uploadImageFiles:bef com "+file.length());
                 opt= ImageUtil.compressImage(file, 1000, 1000, Bitmap.CompressFormat.PNG, 100, new File(Environment.getExternalStorageDirectory(),file.getName().split("\\.")[0]+".png").getPath());
                Log.i(Constants.SARATH, "uploadImageFiles: ap com:"+opt.length());

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(Constants.SARATH, "externalPath: "+Environment.getExternalStorageDirectory().getPath());
            Log.d(Constants.SARATH, "changedfile: "+file.getName());
            Log.d(Constants.SARATH, "changedfile: "+file.getPath());
            Log.d(Constants.SARATH, "changedfile: "+opt.getName());
            Log.d(Constants.SARATH, "changedfile: "+opt.getPath());*/
            HashMap<String, File> imageFiles = new HashMap<>();
            imageFiles.put("photo_data", file);
            String url = Constants.SERVER_URL + "savePhoto";
            int result = -1;
            try {
                String response = AttemptingUploadCheckList.sendImage(url, imageFiles, formData);
                JSONObject jsonObject = new JSONObject(response);

                result = jsonObject.getInt("savePhoto");
            } catch (SocketTimeoutException e) {
                //converting image to base64 string
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(file.getAbsolutePath()));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                JSONObject jsonObject = new JSONObject(formData);
                jsonObject.remove("photo_data");

                jsonObject.put("photo", imageString);
                url = Constants.SERVER_URL + "savePhoto1";
                result = uploadString(url, jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (result == 1) {
                if (file.exists()) {
                    String path = file.getPath();
                    int index = path.lastIndexOf("/");
                    path = path.substring(0, index);
                    String name = "ok_" + file.getName();
                    File newFile = new File(path + File.separator + name);
                    file.renameTo(newFile);
                }
            } else if (result == 0) {
                throw new Exception();
            }
            if (i == size - 1) {
                db.setImagesUploaded(formId);
                db.setSurveyUploaded(String.valueOf(formId));
            }
        }

    }

    int uploadString(String url, JSONObject jsonObject) {
        final int[] result = new int[1];
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, response -> {
            try {
                result[0] = jsonObject.getInt("savePhoto");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

            Toast.makeText(context,
                    " Server Error", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
            // hide the progress dialog
        });


        VolleyNetworkUtils.getInstance().addToRequestQueue(jsonObjReq);

        return result[0];
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


    public interface DataUploadListener {
        void onDataUpload();
    }

    public void setDataUploadListener(DataUploadListener dataUploadListener) {
        this.dataUploadListener = dataUploadListener;
    }

    DataUploadListener getDataUploadListener() {
        return dataUploadListener;
    }

    private ArrayList<File> getPlantationSamplingImages(int formId) {

        ArrayList<File> imageFiles = new ArrayList<>();
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

        String plntEvalDir_board = PlantationSamplingEvaluation.folderName_Upload_Board;
        File plntEvalPathFull_board = getFullPath(plntEvalDir_board, String.valueOf(formId));
        ArrayList<File> plntEvalFileList_board = new ArrayList<>(changeFileNames("PlantationBoard-" + formId + "-", readFileNames(plntEvalPathFull_board)));
        imageFiles.addAll(plntEvalFileList_board);


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

    private ArrayList<File> getOtherWorksImages(int formId) {
        ArrayList<File> imageFiles = new ArrayList<>();

        File otherWorksFullPath = getFullPath(Constants.FORMTYPE_OTHERWORKS, String.valueOf(formId));

        ArrayList<File> otherWorksList = new ArrayList<>(changeFileNames("Otherworks-" + formId + "-", readFileNames(otherWorksFullPath)));
        imageFiles.addAll(otherWorksList);


        File otherWorksFullPath_first = getFullPath(OtherSurvey.folderName_boundary_first, String.valueOf(formId));

        ArrayList<File> otherWorksList_first = new ArrayList<>(changeFileNames("BoundaryStart-" + formId + "-", readFileNames(otherWorksFullPath_first)));
        imageFiles.addAll(otherWorksList_first);


        File otherWorksFullPath_mid = getFullPath(OtherSurvey.folderName_boundary_mid, String.valueOf(formId));

        ArrayList<File> otherWorksList_mid = new ArrayList<>(changeFileNames("BoundaryMid-" + formId + "-", readFileNames(otherWorksFullPath_mid)));
        imageFiles.addAll(otherWorksList_mid);


        File otherWorksFullPath_end = getFullPath(OtherSurvey.folderName_boundary_end, String.valueOf(formId));

        ArrayList<File> otherWorksList_end = new ArrayList<>(changeFileNames("BoundaryEnd-" + formId + "-", readFileNames(otherWorksFullPath_end)));
        imageFiles.addAll(otherWorksList_end);

        return imageFiles;
    }

    private ArrayList<File> getNurseryWorkImages(int formId) {

        ArrayList<File> imageFiles = new ArrayList<>();

        File otherWorksFullPath = getFullPath(Constants.FORMTYPE_NURSERY_WORK, String.valueOf(formId));

        ArrayList<File> otherWorksList = new ArrayList<>(changeFileNames("NurseryWorks-" + formId + "-", readFileNames(otherWorksFullPath)));
        imageFiles.addAll(otherWorksList);

        return imageFiles;

    }

    private ArrayList<File> getAdvanceWorkImages(int formId) {

        ArrayList<File> imageFiles = new ArrayList<>();

        //loading plantation evaluation images files
        String plntEvalDir = PlantationSamplingAdvanceWork.folderName;
        File plntEvalPathFull = getFullPath(plntEvalDir, String.valueOf(formId));
        ArrayList<File> plntEvalFileList = new ArrayList<>(changeFileNames("Advancework-" + formId + "-", readFileNames(plntEvalPathFull)));
        imageFiles.addAll(plntEvalFileList);

        //loading smc images files
        String smcSurveyDir = SmcAdvanceWork.folderName;
        int[] smcIds = db.getAdvSMCIds(String.valueOf(formId));
        for (Integer i : smcIds) {
            File smcPathFull = getFullPath(smcSurveyDir, String.valueOf(i));
            ArrayList<File> smcFileList = new ArrayList<>(changeFileNames("AdvSMC-" + i + "-Photo-" + formId + "-", readFileNames(smcPathFull)));
            imageFiles.addAll(smcFileList);
        }

        //loading plantation evaluation images files
        int[] samplePlotIds = db.getAdvSamplePlotIds(String.valueOf(formId));
        String samplePlotDir = AdvSamplePlotSurvey.folderName;
        int j = 1;
        for (Integer i : samplePlotIds) {
            File samplePlotPathFull = getFullPath(samplePlotDir, String.valueOf(i));
            ArrayList<File> samplePlotFileList = new ArrayList<>(changeFileNames("AdvSamplePlot-" + j + "-Photo-" + formId + "-", readFileNames(samplePlotPathFull)));
            imageFiles.addAll(samplePlotFileList);
            j++;
        }
        int k = 1;
        String failedPlotDir = AdvSamplePlotSurvey.failedFolderName;
        for (Integer i : samplePlotIds) {
            File failedPath = getFullPath(failedPlotDir, String.valueOf(i));
            ArrayList<File> failedFileList = new ArrayList<>(changeFileNames("SamplePlotFailed-" + k + "-Photo-" + formId + "-", readFileNames(failedPath)));
            imageFiles.addAll(failedFileList);
            k++;
        }

        return imageFiles;
    }

    private ArrayList<File> getSDPImages(int formId) {
        ArrayList<File> imageFiles = new ArrayList<>();
        int[] sdpBenIds = db.getSDPBenefciaries(formId);

        String sdpdir = SDPBeneficiarySurvey.folderName;
        for (Integer i : sdpBenIds) {
            File sdpBenPath = getFullPath(sdpdir, String.valueOf(i));
            ArrayList<File> sdpBenFileList = new ArrayList<>(changeFileNames("SDPBENID-" + i + "-Photo-" + formId + "-", readFileNames(sdpBenPath)));
            imageFiles.addAll(sdpBenFileList);
        }
        String sdpMapdir = SDPBeneficiarySurvey.folderNameMap;
        for (Integer i : sdpBenIds) {
            File sdpBenPath = getFullPath(sdpMapdir, String.valueOf(i));
            ArrayList<File> sdpBenFileList = new ArrayList<>(changeFileNames("SDPMAPBENID-" + i + "-Photo-" + formId + "-", readFileNames(sdpBenPath)));
            imageFiles.addAll(sdpBenFileList);
        }
        return imageFiles;
    }

    private ArrayList<File> getSCPTSPImages(int formId) {
        ArrayList<File> imageFiles = new ArrayList<>();
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

        }

        return imageFiles;
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

    private ArrayList<File> changeFileNames(String prefix, ArrayList<File> files) {
        int i = 1;
        ArrayList<File> fileList = new ArrayList<>();
        for (File oldFile : files) {
            try {
                String parent = oldFile.getParent();
                if (!oldFile.getName().startsWith(prefix)) {
                    File newFile = new File(parent + File.separator + prefix + i + ".jpg");
                    oldFile.renameTo(newFile);
                    fileList.add(newFile);
                } else
                    fileList.add(oldFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
        return fileList;
    }

    private File getFullPath(String folderName, String formId) {
        String photoDirectory = context.getExternalFilesDir(null) + "/Photo/";
        return new File(photoDirectory + File.separator + folderName + File.separator + formId);

    }
}
