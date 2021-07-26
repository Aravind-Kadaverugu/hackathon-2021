package com.aravindchowdary.callrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

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
    static final String TAGS=" Inside Service";
    public  static  String rec = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId)
    {
        recorder = new MediaRecorder();
        recorder.reset();

        String phoneNumber=intent.getStringExtra("number");
        Log.d(TAGS, "Phone number in service: "+phoneNumber);

        String time=new CommonMethods().getTIme();

        //String path=new CommonMethods().getPath();

        String mFileName = getExternalFilesDir("/").getAbsolutePath();

        rec=mFileName+"/"+phoneNumber+"_"+time+".3gp";

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        recorder.setOutputFile(rec);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();

        Log.d(TAGS, "onStartCommand: "+"Recording started");

        return START_NOT_STICKY;
    }

    public void onDestroy()
    {
        super.onDestroy();
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder=null;

        Log.d(TAGS, "onDestroy: "+"Recording stopped");
    }
}