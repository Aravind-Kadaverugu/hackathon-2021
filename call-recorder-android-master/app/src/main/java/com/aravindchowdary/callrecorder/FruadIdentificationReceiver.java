package com.aravindchowdary.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

public class FruadIdentificationReceiver extends BroadcastReceiver {

    static final String TAGS=" FruadReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAGS ,"Received Fraud Identification Intent.");

        if(intent.getAction().equals("FRAUD_IDENTIFICATION"))
        {
            String serviceResponse = intent.getStringExtra("SERVICE_RESPONSE");
            try {
                if (serviceResponse != null) {
                    JSONObject Jobject = new JSONObject(serviceResponse);
                    String callTranscript = Jobject.getString("callTranscript");
                    Boolean isFraud = Jobject.getBoolean("isFraud");
                    Log.i(TAGS, "Call transcript :" + callTranscript + " . Call status :" + isFraud);
                    alertDialog();
                }
            }catch(Exception e){

            }
            // Show it in GraphView
        }
    }

    private void alertDialog() {

    }
    
}