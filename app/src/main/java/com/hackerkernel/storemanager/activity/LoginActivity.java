package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.pojo.LoginPojo;
import com.hackerkernel.storemanager.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.login_email) EditText mEmail;
    @Bind(R.id.login_password) EditText mPassword;
    @Bind(R.id.login_button) Button mLogin;
    @Bind(R.id.login_linearlayout) LinearLayout mLayout;

    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private List<LoginPojo> loginList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //set Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.login_small));

        //make a progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        //get RequestQueue from our volley singleton
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //when login button is clcke
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLoginCredientials();
            }
        });
    }

    /*
    * This method check Email & password are not empty & internet is present
    * if all this is true, This method will call the "loginInBackground()" method
    * */
    private void checkLoginCredientials() {
        String email = mEmail.getText().toString().trim(),
                password = mPassword.getText().toString().trim();
        //check email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.fillin_all_fields));
        } else if (!Util.isValidEmail(email)) { //if invalid email address
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.invalid_email));
        } else {
            /*
            * Check user has InternetConnection
            * */
            if(!Util.isConnectedToInternet(getApplication())){ //no Internet
                //show a no internet connection SnackBar message
                Util.noInternetSnackbar(getApplication(),mLayout);

            }else{ // internet is present
                /*
                * Make a request to API and check Email & Password is Valid or not
                * */
                loginInBackground(email, password);
            }
        }
    }


    //method to do login in background FROM API
    private void loginInBackground(final String email, final String password) {
        //show progress Dialog
        pd.show();

        //make Request to API
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Hide Progress Dialog
                        pd.hide();
                        parseLoginResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Hide Progress Dialog
                        pd.hide();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_LOGIN_EMAIL_PRAM,email);
                params.put(Keys.KEY_LOGIN_PASSWORD_PRAM,password);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * This method parse json response from the api
    * & and API return false we will show error Message
    * and If API return true we will log the user in
    * */
    private void parseLoginResponse(String response) {
        Toast.makeText(getApplication(),response,Toast.LENGTH_LONG).show();
    }
}
