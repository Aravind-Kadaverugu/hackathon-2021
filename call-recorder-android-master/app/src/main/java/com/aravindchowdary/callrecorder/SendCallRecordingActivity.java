package com.aravindchowdary.callrecorder;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.aravindchowdary.callrecorder.PhoneStateReceiver.phoneNumber;
import static com.aravindchowdary.callrecorder.RecorderService.TAGS;
import static com.aravindchowdary.callrecorder.RecorderService.rec;

public class SendCallRecordingActivity  extends  AsyncTask<String, Void, Response>  {

        private Exception exception;

    @Override
    protected Response doInBackground(String... strings) {
        Log.d(TAGS, "sendCallRecord: "+"Call record sent");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("call-recording",rec,
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(rec)))
                .addFormDataPart("incoming-mobile-number","8978178830")
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.0.7:9081/know-your-caller/identify-fraud")
                .method("POST", body)
                .build();
        Response response = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();StrictMode.setThreadPolicy(policy);
            response = client.newCall(request).execute();
            response.body();
            Log.d(TAGS, "sendCallRecord: identify fraud response"+response.message());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
