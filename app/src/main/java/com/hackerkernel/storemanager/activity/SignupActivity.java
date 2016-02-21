package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SignupPojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.signup_fullname) EditText mFullname;
    @Bind(R.id.signup_storename) EditText mStoreName;
    @Bind(R.id.signup_email) EditText mEmail;
    @Bind(R.id.signup_number) EditText mPhone;
    @Bind(R.id.signup_password) EditText mPassword;
    @Bind(R.id.signup_button) Button mButton;
    @Bind(R.id.signup_layout) LinearLayout mLayout;
    //fb Login
    @Bind(R.id.fb_login) LoginButton mFBLoginButton;
    private CallbackManager callbackManager;

    private RequestQueue mRequestQueue;
    private List<SignupPojo> mSignupList;
    private ProgressDialog pd;


    //Variable to store value enter by the user in the register form
    String storename;
    String fullname;
    String email;
    String phone;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        //set Toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null; //statement to avoid NullPointerException in Toolbar
        getSupportActionBar().setTitle(R.string.signup_small);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //make a progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //when Signup button is clicked
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSignup();
            }
        });

        //When Facebook login button is clicked
        callbackManager = CallbackManager.Factory.create();
        mFBLoginButton.setReadPermissions("email");
        mFBLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();
                pd.show();
                GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("HUS","HUS: fbResponse "+response.toString());
                        pd.dismiss();
                        setFacebookDataToFields(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancelled_fb_login,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Failed Facebook login "+error.getMessage(),Toast.LENGTH_LONG).show();
                Log.e(TAG, "HUS: registerCallback(fb login) " + error.getMessage());
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void checkSignup() {
        fullname = mFullname.getText().toString().trim();
        storename = mStoreName.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        phone = mPhone.getText().toString().trim();
        password = mPassword.getText().toString().trim();

        //check all the inputs are filled
        if (fullname.isEmpty() || storename.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.fillin_all_fields));
        } else if (fullname.length() <= 2) { //check fullname length
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.name_more_then_2));
        } else if (storename.length() <= 3) { //check storename length
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.storename_more_then_3));
        } else if (!Util.isValidEmail(email)) {
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.invalid_email));
        } else if (!Util.isValidPhoneNumber(phone)) {
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.invalid_phnumber));
        } else if (password.length() <= 4) {
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.password_more_then_4));
        } else {
             /*
            * Check user has InternetConnection
            * */
            if (!Util.isConnectedToInternet(getApplication())) { //no Internet
                //show a no internet connection SnackBar message
                Util.noInternetSnackbar(getApplication(), mLayout);
            } else { // internet is present
                //Make a Request to API and register the user
                registerInBackground(fullname, storename, email, phone, password);
            }

        }
    }

    private void registerInBackground(final String fullname, final String storename, final String email, final String phone, final String password) {
        //show progressbar
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.SIGNUP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hide progressbar
                        pd.dismiss();
                        //parse the register Response which we have got from api
                        parseRegisterResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hide progressbar
                        pd.dismiss();
                        //handle Volley error
                        String errorMessage = VolleySingleton.handleVolleyError(error);
                        if(errorMessage != null){
                            Util.redSnackbar(getApplication(),mLayout,errorMessage);
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.PRAM_SIGNUP_FN,fullname);
                params.put(Keys.PRAM_SIGNUP_STORENAME,storename);
                params.put(Keys.PRAM_SIGNUP_EMAIL,email);
                params.put(Keys.PRAM_SIGNUP_PHONE,phone);
                params.put(Keys.PRAM_SIGNUP_PASS,password);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Parse the response which we have got from APi
    * and if it return true login user in
    * else display the error message
    * */
    public void parseRegisterResponse(String response){
        mSignupList = JsonParser.signupParser(response);
        if(mSignupList != null){
            SignupPojo current = mSignupList.get(0);
            //request was success (Login the user)
            if(current.getReturned()){
                //Store the user details in SharedPrefernce
                MySharedPreferences mySharedPreferences = MySharedPreferences.getInstance(SignupActivity.this);
                mySharedPreferences.setUser(current.getUserId(),fullname,storename,email,phone,password);

                //send the user to HomeActivity
                Util.goToHomeActivity(getApplication());
            }else{ //request failed
                Util.redSnackbar(getApplication(),mLayout,current.getMessage());
            }
        }else{
            Toast.makeText(getApplication(), R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * METHOD TO send facebook data to signup fields
    * */
    public void setFacebookDataToFields(JSONObject data) {
        try {
            String firstname = null,
                    lastname = null,
                    email = null;
            if(data.has("first_name") && !data.isNull("first_name")){
                firstname = data.getString("first_name");
            }
            if(data.has("last_name") && !data.isNull("last_name")){
                lastname = data.getString("last_name");
            }
            if(data.has("email") && !data.isNull("email")){
                email = data.getString("email");
            }

            //set to fields
            if(firstname != null || lastname != null){
                mFullname.setText(firstname + " " + lastname);
            }
            if(email != null){
                mEmail.setText(email);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.unable_to_parse_fb_login_rsponse,Toast.LENGTH_LONG).show();
        }

    }
}
