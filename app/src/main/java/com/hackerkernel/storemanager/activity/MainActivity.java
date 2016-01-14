package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.goToLogin) Button mGoToLogin;
    @Bind(R.id.goToSignup) Button mGoToSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //Bind Views

        /*
        * Check User is logged in from SharedPreferences
        * if user Is login send him to HomeActivity
        * Else be in this screen
        * */
        MySharedPreferences sharedPreferences = MySharedPreferences.getInstance(getApplication());
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
