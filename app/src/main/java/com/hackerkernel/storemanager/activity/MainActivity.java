package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.LoginPojo;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /*@Bind(R.id.loginEmail) TextView loginEmail;
    @Bind(R.id.loginPassword) TextView loginPassword;
    @Bind(R.id.loginBtn) Button loginBtn;
    @Bind(R.id.goToRegister) TextView goToRegister;
    private ProgressDialog pd;
    private List<LoginPojo> loginList;
    //create a global varaible for SQLite database
    DataBase database;

    private Context context = MainActivity.this;*/

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.goToLogin) Button mGoToLogin;
    @Bind(R.id.goToSignup) Button mGoToSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //Bind Views

        //Set click method on GoToLogin & GoToSignup
        mGoToLogin.setOnClickListener(this);
        mGoToSignup.setOnClickListener(this);

        /*
        loginEmail.requestFocus();

        //instan the database
        database = new DataBase(this);

        *//*
        * if user record is in the SQLite database means he is login
        * Start HomeActivity
        * *//*
        if(database.loginStatus()) startHomeActivity();

        //make a progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(false);

        //when register button is clicked
        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterActivity();
            }
        });


        //when login button is clicked
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim(),
                        password = loginPassword.getText().toString().trim();
                //check email and password are not empty
                if(email.isEmpty() || password.isEmpty()){
                    Functions.errorAlert(context,getString(R.string.oops), getString(R.string.fillin_all_fields));
                }else if(!Functions.isValidEmail(email)){ //if invalid email address
                    Functions.errorAlert(context,getString(R.string.oops), getString(R.string.invalid_email));
                }else{
                    //Execute the Async Task
                    LoginTask loginTask = new LoginTask();
                    loginTask.execute(email,password);
                }
            }
        });*/
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

    /*@Override
    protected void onResume() {
        super.onResume();
        //when connection is not available
        if(!Functions.isOnline(this)){
            //show close app dialog
            Functions.closeAppWhenNoConnection(this);
        }
    }*/

    //async task class to fetch data from the web
    /*private class LoginTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //generate a hashmap to send to server
            HashMap<String,String> loginData = new HashMap<>();
            loginData.put("email",params[0]);
            loginData.put("password",params[1]);

            //convert hashmap into Encoded URL
            String data = Functions.hashMapToEncodedUrl(loginData);

            //make a request to the web
            String response = GetJson.request(DataUrl.LOGIN_URL, data, "POST");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            //parse result
            loginList = JsonParser.LoginParser(s);

            //store the ArrayList in the LoginPojo
            LoginPojo get = loginList.get(0);
            pd.dismiss();

            if(get.getReturned()){
                //store data in the SQLite database
                database.login(get);
                //Start HomeActivity
                startHomeActivity();
            }else{
                //show the alert
                Functions.errorAlert(MainActivity.this,getString(R.string.oops),get.getMessage());
            }
        }
    }

    *//*
    * Start Register ativity When Register TextView is clicked
    * *//*
    public void startRegisterActivity(){
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    //function to start home activity
    public void startHomeActivity(){
        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        //clear flags
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }*/
}
