package com.aravindchowdary.callrecorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.example.vs00481543.phonecallrecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static com.aravindchowdary.callrecorder.DirectReplyIntent.KEY_NOTIFY_ID;
import static com.aravindchowdary.callrecorder.DirectReplyIntent.KEY_TEXT_REPLY;


/**
 * Created by Aravind Chowdary on 01-07-2019.
 */

public class RecorderService extends Service {

    MediaRecorder recorder;
    static final String TAGS=" Recorder Service";
    public  static  String rec = null;
    public static String phoneNumber= null;
    public static final int NOTIF_ID = 82;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        registerReceiver(new FruadIdentificationReceiver(), new IntentFilter("FRAUD_IDENTIFICATION"));
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

                Log.i(TAGS, "onDestroy: " + "Recording being sent to server.");
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
                        .url("http://192.168.0.102:9085/know-your-caller/identify-fraud")
                        .method("POST", body)
                        .build();
                Response response = client.newCall(request).execute();
                String responseString=response.body().string();
                if (responseString != null&& responseString.contains("true")) {
                    Log.i(TAGS, "Response from server :" + responseString);
                    createNotification(responseString,phoneNumber);
                    //sendDataToActivity(response.body().string());
                }

            } catch (Exception e) {
                Log.i(TAGS,e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void createNotification(String serviceResponse,String mobileNumber) {

        // Construct pending intent to serve as action for notification item
        Intent intent = new Intent(this, AlertActivity.class);
        intent.setAction("FRAUD_IDENTIFICATION");
        intent.putExtra( "SERVICE_RESPONSE",serviceResponse);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DemoApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_callicon)
                .setContentTitle("KnowURCaller.")
                .setContentText(serviceResponse)
                .setStyle(new NotificationCompat.MessagingStyle("KnowURCaller"))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_call_block, "Block", pIntent)
                .addAction(R.drawable.ic_not_action, "Ignore", pIntent)
                .setContentIntent(pIntent);

        if (android.os.Build.VERSION.SDK_INT >= 24) {

            Intent directReplyIntent = new Intent(this, DirectReplyIntent.class);
            directReplyIntent.putExtra(KEY_NOTIFY_ID, NOTIF_ID);

            int flags = FLAG_CANCEL_CURRENT;
            PendingIntent directReplyPendingIntent =
                    PendingIntent.getService(this, 0, directReplyIntent, flags);
            // http://android-developers.blogspot.com/2016/06/notifications-in-android-n.html

            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel("Type Message").build();

            NotificationCompat.Action block = new NotificationCompat.Action.Builder(
                    R.drawable.ic_call_block, "Block", directReplyPendingIntent)
                    .build();

            NotificationCompat.Action ignore = new NotificationCompat.Action.Builder(
                    R.drawable.ic_not_action, "Ignore", directReplyPendingIntent)
                    .build();
            builder.addAction(block);
            builder.addAction(ignore);

        }
        Notification notification = builder.build();

        // Hide the notification after its selected
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID, notification);
    }

    private void sendDataToActivity(String serviceResponse)
    {
        Intent serviceResponseBroadcaster = new Intent();
        serviceResponseBroadcaster.setClass(this,AlertActivity.class);
        serviceResponseBroadcaster.setAction("FRAUD_IDENTIFICATION");
        serviceResponseBroadcaster.putExtra( "SERVICE_RESPONSE",serviceResponse);
        sendBroadcast(serviceResponseBroadcaster);
    }
}