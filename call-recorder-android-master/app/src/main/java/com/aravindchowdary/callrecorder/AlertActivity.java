package com.aravindchowdary.callrecorder;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vs00481543.phonecallrecorder.R;

public class AlertActivity extends AppCompatActivity{

    static final String TAGS=" AlertActivity";
    TextView serviceResponse;
    FruadIdentificationReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        receiver = new FruadIdentificationReceiver();
        registerReceiver(receiver, new IntentFilter("SERVICE_RESPONSE"));
        String response=getIntent().getStringExtra("SERVICE_RESPONSE");
        Log.i(TAGS,"Service response : "+response);
        serviceResponse = (TextView) findViewById(R.id.serviceResponse);
        serviceResponse.setText(response);
    }


    public void onReportSpamClick() {
        Toast.makeText(this, "Spam call Reported !! ", Toast.LENGTH_SHORT).show();
    }

    public void onNoSpamClick()
    {
        Toast.makeText(this, "Spam call Ignored !! ", Toast.LENGTH_SHORT).show();
    }

    public void onReportSpamClick(View view)
    {
        Toast.makeText(this, "Spam call Reported !! ", Toast.LENGTH_SHORT).show();
    }
    public void onNoSpamClick(View view)
    {
        Toast.makeText(this, "Spam call Ignored !! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //<-- Unregister to avoid memoryleak
    }


}
