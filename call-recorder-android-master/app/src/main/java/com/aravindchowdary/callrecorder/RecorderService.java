package com.aravindchowdary.callrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Aravind Chowdary on 01-07-2019.
 */

public class RecorderService extends Service {

    MediaRecorder recorder;
    static final String TAGS=" Recorder Service";
    public  static  String rec = null;
    public static String phoneNumber= null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId)
    {
        recorder = new MediaRecorder();
        recorder.reset();

        phoneNumber=intent.getStringExtra("number");
        Log.i(TAGS, "Phone number in service: "+phoneNumber);

        String time=new CommonMethods().getTIme();
        String path=new CommonMethods().getPath();
        rec=path+"/"+phoneNumber+"_"+time+".amr";

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recorder.setOutputFile(rec);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();

        Log.i(TAGS, "onStartCommand: "+"Recording started");

        return START_NOT_STICKY;
    }

    public void onDestroy()
    {
        super.onDestroy();
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder=null;

        Log.i(TAGS, "onDestroy: "+"Recording stopped");

        new Thread(() -> {
            try {

                Log.i(TAGS, "onDestroy: "+"Recording being sent to server.");
                // determine whether the file is in use by another thread
                    Thread.sleep(3000);
                    // Perform upload audio operation
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(45, TimeUnit.SECONDS)
                            .writeTimeout(45, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");

                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("call-recording", rec,
                                    RequestBody.create(MediaType.parse("application/octet-stream"),
                                            new File(rec)))
                            .addFormDataPart("incoming-mobile-number", phoneNumber)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://192.168.0.7:9084/know-your-caller/identify-fraud")
                            .method("POST", body)
                            .build();
                    Response response = client.newCall(request).execute();
                    if(response!=null && response.body()!=null) {
                        Log.i(TAGS, "Response from server :" + response.body().string());
                        JSONObject Jobject = new JSONObject(response.body().string());
                        String callTranscript = Jobject.getString("callTranscript");
                        Boolean isFraud = Jobject.getBoolean("isFraud");
                        Log.i(TAGS, "Call transcript :" + callTranscript + " . Call status :" + isFraud);
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}