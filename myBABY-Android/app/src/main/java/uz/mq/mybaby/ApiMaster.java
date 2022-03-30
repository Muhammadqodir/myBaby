package uz.mq.mybaby;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.Console;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiMaster {

    OkHttpClient client;
    final String SERVER_URL = "http://www.mybaby.qodir.xyz/api/";
    Context context;
    static String LOG = "ApiMaster";

    public ApiMaster(Context context) {
        this.context = context;
        client = new OkHttpClient();
    }

    Result uploadAudio(File file){
        Log.e("uploadAudio", "Uploading");
        MediaType mediaType = MediaType.parse("audio/3gp");
        RequestBody requestBody = RequestBody.create(mediaType, file);

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "audio.3gp", requestBody)
                .addFormDataPart("test", "asdfasdfasdf")
                .addFormDataPart("key", "Muhammadqodir").build();
        RequestBody formBody = new FormBody.Builder()
                .add("key", "test")
                .add("test", "test")
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL+"upload_audio/index.php")
                .post(multipartBody)
                .build();

        try{
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                Log.e("RespnseContent", "content");
                String response_content = response.body().string();

                Log.e("RespnseContent", response_content);
                JSONObject result = new JSONObject(response_content);
                Result result1 = new Result(result.getBoolean("isSuccess"),
                        result.getString("message"),
                        result.getInt("result"),
                        result.getInt("accuracy"),
                        result.getString("ad_text"),
                        result.getString("ad_web"));
                String currentDate = new SimpleDateFormat("dd.MM,yyyy HH:mm", Locale.getDefault()).format(new Date());
                Utils.addToHistory(context, new HistoryModel(currentDate, result.getString("key"), result1));
                return result1;
            }else{
                Log.e("RespnseContent", "failed");
                return null;
            }
        }catch (Exception e){
            Log.e("ErrorOnUploading", e.getMessage());
            return new Result(false, "Failed to connect to the server", 0, 0, "", "");
        }
    }

    class Result{
        boolean isSuccess;
        String message;
        int accuracy;
        int result;
        String advertising_text;
        String advertisers_website;

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(int accuracy) {
            this.accuracy = accuracy;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getAdvertising_text() {
            return advertising_text;
        }

        public void setAdvertising_text(String advertising_text) {
            this.advertising_text = advertising_text;
        }

        public String getAdvertisers_website() {
            return advertisers_website;
        }

        public void setAdvertisers_website(String advertisers_website) {
            this.advertisers_website = advertisers_website;
        }

        public Result(boolean isSuccess, String message, int accuracy, int result, String advertising_text, String advertisers_website) {
            this.isSuccess = isSuccess;
            this.message = message;
            this.accuracy = accuracy;
            this.result = result;
            this.advertising_text = advertising_text;
            this.advertisers_website = advertisers_website;
        }
    }
}
