package com.hackerkernel.storemanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.login_email) EditText mEmail;
    @Bind(R.id.login_password) EditText mPassword;
    @Bind(R.id.login_button) Button mLogin;
    @Bind(R.id.login_linearlayout) LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //set Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.login_small));


        //when login button is clcke
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim(),
                        password = mPassword.getText().toString().trim();
                //check email and password are not empty
                if (email.isEmpty() || password.isEmpty()) {
                    Util.redSnackbar(getApplication(), mLayout, getString(R.string.fillin_all_fields));
                } else if (!Util.isValidEmail(email)) { //if invalid email address
                    Util.redSnackbar(getApplication(), mLayout, getString(R.string.invalid_email));
                } else {
                    Toast.makeText(getApplication(), "Done", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
