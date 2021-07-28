package com.aravindchowdary.callrecorder;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.vs00481543.phonecallrecorder.R;

public class UserPreferencesActivity extends AppCompatActivity {

    private Button nextButton,skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
        ActionBar actionBar = getSupportActionBar(); actionBar.hide();

        nextButton=(Button)findViewById(R.id.nextButton);
        skipButton =(Button) findViewById(R.id.skipButton);
        nextButton.setOnClickListener(view -> navigateToMainActivity());
        skipButton.setOnClickListener(view -> navigateToMainActivity());
    }

    public void navigateToMainActivity() {
        this.getIntent().setClass(this,MainActivity.class);
        startActivity(this.getIntent());
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


}