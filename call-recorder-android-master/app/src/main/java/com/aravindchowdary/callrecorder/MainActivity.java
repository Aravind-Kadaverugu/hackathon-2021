package com.aravindchowdary.callrecorder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

@RequiresApi(28)
public class MainActivity extends AppCompatActivity implements Handler.Callback{
    //Intializing all variables..
    private TextView startTV, stopTV, playTV, stopplayTV, statusTV;
    private EditText userFirstName,userLastName,userMobileNumber,userEmailAddress;
    //creating a variable for medi recorder object class.
    private MediaRecorder mRecorder;
    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;
    //string variable is created for storing a file name
    private static String mFileName = null;
    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private android.os.Handler handler;
    private Button buttonUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        //initialize all variables with their layout items
        statusTV = findViewById(R.id.idTVstatus);
        startTV = findViewById(R.id.btnRecord);
        stopTV = findViewById(R.id.btnStop);
        playTV = findViewById(R.id.btnPlay);
        stopplayTV = findViewById(R.id.btnStopPlay);
        buttonUpload = findViewById(R.id.button);
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        playTV.setBackgroundColor(getResources().getColor(R.color.gray));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.gray));
        handler = new Handler(this);
        startTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start recording method will start the recording of audio.
                startRecording();
            }
        });
        stopTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause Recording method will pause the recording of audio.
                pauseRecording();

            }
        });
        playTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play audio method will play the audio which we have recorded
                playAudio();
            }
        });
        stopplayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pause play method will pause the play of audio
                pausePlaying();
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileUpload(handler);
            }
        });
    }

    private void startRecording() {
        // check permission method is used to check that the user has granted permission to record nd store the audio.
        if (CheckPermissions()) {
            //setbackgroundcolor method will change the background color of text view.
            stopTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
            startTV.setBackgroundColor(getResources().getColor(R.color.gray));
            playTV.setBackgroundColor(getResources().getColor(R.color.gray));
            stopplayTV.setBackgroundColor(getResources().getColor(R.color.gray));
            //we are here initializing our filename variable with the path of the recorded audio file.
            mFileName = getExternalFilesDir("/").getAbsolutePath();
            mFileName += "/AudioRecording.3gp";
            //below method is used to initialize the media recorder clss
            mRecorder = new MediaRecorder();
            //below method is used to set the audio source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //below method is used to set the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //below method is used to set the audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //below method is used to set the output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);
            try {
                //below mwthod will prepare our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start the audio recording.
            mRecorder.start();
            statusTV.setText("Recording Started");
        } else {
            //if audio recording permissions are not granted by user below method will ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        //this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NOTIFICATION_POLICY);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED&& result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE,ACCESS_NOTIFICATION_POLICY}, REQUEST_AUDIO_PERMISSION_CODE);
    }


    public void playAudio() {
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.gray));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        //for playing our recorded audio we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            Log.d("File Name:",mFileName);
            Uri.fromFile(new File(mFileName));
            Log.d("URI:", String.valueOf(Uri.fromFile(new File(mFileName))));
            //below method is used to set the data source which will be our file name
            mPlayer.setDataSource(mFileName);
            //below method will prepare our media player
            mPlayer.prepare();
            //below method will start our media player.
            mPlayer.start();
            statusTV.setText("Recording Started Playing");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }


    }

    public void pauseRecording() {
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

        if(mRecorder!=null) {
            //below method will stop the audio recording.
            mRecorder.stop();
            //below method will release the media recorder class.
            mRecorder.release();
            mRecorder = null;
        }
        File file = new File(mFileName);
        Log.d("File", String.valueOf(file));
        statusTV.setText("Recording Stopped");

    }

    public void pausePlaying() {
        //this method will release the media player class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.gray));
        statusTV.setText("Recording Play Stopped");

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Log.i("File Upload", "Response :: " + msg.obj);
        String success = 1 == msg.arg1 ? "File Upload Success" : "File Upload Error";
        return false;
    }

    public void userProfileUpload(final Handler handler) {

        new Thread(() -> {
            try{
                String firstName = getIntent().getExtras().getString("name");
                String lastName = getIntent().getExtras().getString("name");
                String mobileNumber = getIntent().getExtras().getString("mobile");
                String emailAddress = getIntent().getExtras().getString("Email");
                Log.d("User Details",firstName+lastName+mobileNumber+emailAddress);
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");

                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file",mFileName,
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        new File(mFileName)))
                        .addFormDataPart("firstName",firstName)
                        .addFormDataPart("lastName",lastName)
                        .addFormDataPart("mobileNumber",mobileNumber)
                        .addFormDataPart("emailAddress",emailAddress)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.0.102:9085/know-your-caller/create-user")
                        .method("POST", body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.d("Create User Profile :", String.valueOf(response.body()));
                Intent i = new Intent(this, SecondaryActivity.class);
                startActivity(i);

            } catch (Exception e)
            {
                e.printStackTrace();
                Log.d("Exception",e.getMessage());
            }
        }).start();

    }
    // convert mFileName a string into a file and post it to the server

}
