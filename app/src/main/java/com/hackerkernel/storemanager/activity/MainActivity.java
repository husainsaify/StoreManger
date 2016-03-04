package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.appIntro.MainAppIntro;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.goToLogin) Button mGoToLogin;
    @Bind(R.id.goToSignup) Button mGoToSignup;
    @Bind(R.id.appVersionCode) TextView mVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //Bind Views

        MySharedPreferences sharedPreferences = MySharedPreferences.getInstance(getApplication());

        //Show app intro if he is a first time user
        if (!sharedPreferences.getBooleanKey(MySharedPreferences.KEY_MAIN_APPINTRO)){
            //set user has viewed app intro to shared prefernce
            sharedPreferences.setBooleanKey(MySharedPreferences.KEY_MAIN_APPINTRO);

            //show app intro
            startActivity(new Intent(getApplicationContext(), MainAppIntro.class));
        }

        //add version code
        mVersion.append(" "+getString(R.string.app_version_code));

        /*
        * Check User is logged in from SharedPreferences
        * if user Is login send him to HomeActivity
        * Else be in this screen
        * */
        if(sharedPreferences.checkUser()){
            //Go to HomeActivity
            Util.goToHomeActivity(getApplication());
        }


        //Set click method on GoToLogin & GoToSignup
        mGoToLogin.setOnClickListener(this);
        mGoToSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            //go to signup
            case R.id.goToSignup:
                startActivity(new Intent(getApplication(),SignupActivity.class));
                break;
            //go to login
            case R.id.goToLogin:
                startActivity(new Intent(getApplication(),LoginActivity.class));
                break;
        }
    }
}
