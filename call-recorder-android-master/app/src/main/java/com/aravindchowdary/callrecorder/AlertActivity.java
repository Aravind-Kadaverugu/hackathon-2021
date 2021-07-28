package com.aravindchowdary.callrecorder;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vs00481543.phonecallrecorder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlertActivity extends AppCompatActivity{

    static final String TAGS=" AlertActivity";
    TextView serviceResponse;
    FruadIdentificationReceiver receiver;
    String mobileNumber,callRecord,response,message;
    boolean isFraud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        receiver = new FruadIdentificationReceiver();
        //registerReceiver(receiver, new IntentFilter("SERVICE_RESPONSE"));
        registerReceiver(receiver, new IntentFilter("FRAUD_IDENTIFICATION"));
        response=getIntent().getStringExtra("SERVICE_RESPONSE");
        Log.i(TAGS,"Fraud identity response from server : "+response) ;
        if(response!=null) {
            try {
                JSONObject res = new JSONObject(response);
                callRecord=res.getString("callRecordId");
                isFraud=res.getBoolean("isFraud");
                mobileNumber=res.getString("mobileNumber");
                message=res.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        serviceResponse = (TextView) findViewById(R.id.serviceResponse);
        serviceResponse.setText(message);
    }


    public void onReportSpamClick() {
        Toast.makeText(this, "Spam call Reported !! ", Toast.LENGTH_SHORT).show();
        reportSpam(callRecord,isFraud,mobileNumber);
    }

    public void onNoSpamClick()
    {
        Toast.makeText(this, "Spam call Ignored !! ", Toast.LENGTH_SHORT).show();
        reportSpam(callRecord,isFraud,mobileNumber);
    }

    public void onReportSpamClick(View view)
    {
        Toast.makeText(this, "Spam call Reported !! ", Toast.LENGTH_SHORT).show();
        reportSpam(callRecord,isFraud,mobileNumber);
    }
    public void onNoSpamClick(View view)
    {
        Toast.makeText(this, "Spam call Ignored !! ", Toast.LENGTH_SHORT).show();
        reportSpam(callRecord,isFraud,mobileNumber);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //<-- Unregister to avoid memoryleak
    }

    private void reportSpam(String recordId,boolean isFraud, String mobileNumber){
        new Thread(() -> {
            try{
                Log.d("Reporting spam : ",recordId+isFraud+mobileNumber+mobileNumber);
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");

                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("record-id",recordId)
                        .addFormDataPart("isFraudCaller", String.valueOf(isFraud))
                        .addFormDataPart("fraud-mobile-number",mobileNumber)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.0.102:9085/know-your-caller/report-fraud-caller")
                        .method("POST", body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.d("Report spam response :", String.valueOf(response.body()));

            } catch (Exception e)
            {
                e.printStackTrace();
                Log.d("Exception",e.getMessage());
            }
        }).start();
    }


}
