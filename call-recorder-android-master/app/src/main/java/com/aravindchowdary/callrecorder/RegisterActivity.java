package com.aravindchowdary.callrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.vs00481543.phonecallrecorder.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_register);
    }
    public void onRegisterClick(View view)
    {
        Intent intent = new Intent(this, UserPreferencesActivity.class);
        EditText name = (EditText)findViewById(R.id.editTextName);
        intent.putExtra("name", name.getText().toString());
        EditText mobile = (EditText)findViewById(R.id.editTextMobile);
        intent.putExtra("mobile", mobile.getText().toString());
        EditText email = (EditText)findViewById(R.id.editTextEmail);
        intent.putExtra("Email", email.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    public void onLoginClick(View view)
    {
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}