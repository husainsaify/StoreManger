package com.hackerkernel.storemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.activity.MainActivity;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SimplePojo;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.registerName) EditText mName;
    @Bind(R.id.registerPhone) EditText mPhone;
    @Bind(R.id.registerEmail) EditText mEmail;
    @Bind(R.id.registerDescribe) EditText mDescription;
    @Bind(R.id.register) Button mRegisterBtn;
    List<SimplePojo> registerList;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this); //bind views

        setSupportActionBar(toolbar); //set toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set the up button
        toolbar.setTitle("Register"); //set toolbar title

        //when register button is clicked
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(); //call the register method
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(false);
    }

    /*
    * Register method to send a registration request
    */
    private String register() {
        //store text
        String name = mName.getText().toString().trim(),
                phone = mPhone.getText().toString().trim(),
                email = mEmail.getText().toString().trim(),
                description = mDescription.getText().toString().trim();

        if(name.isEmpty() || phone.isEmpty() || email.isEmpty() || description.isEmpty()){
            alert(getString(R.string.fillin_all_fields));
        }else{
            //check name length
            if(name.length() <= 2){
                alert(getString(R.string.name_more_then_2));
            }else{
                //check phone number
                if(Functions.isValidPhoneNumber(phone)){
                    //check email
                    if(Functions.isValidEmail(email)){
                        //check description
                        if(description.length() > 6){
                            /*
                            * Execute AsyncTask and store the data in the database
                            * */
                            RegisterTask registerTask = new RegisterTask();
                            registerTask.execute(name,phone,email,description);
                        }else{
                            //show description error
                            alert(getString(R.string.invalid_desc));
                        }
                    }else{
                        //show invalid email error
                        alert(getString(R.string.invalid_email));
                    }
                }else{
                    //show invalid phone number error
                    alert(getString(R.string.invalid_phnumber));
                }
            }
        }
        return null;
    }

    //show alert box
    private void alert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(R.string.opps)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class RegisterTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //generate a hashmap of parameters to be send to server
            HashMap<String,String> registerData = new HashMap<>();
            registerData.put("register","true");
            registerData.put("name",params[0]);
            registerData.put("phone",params[1]);
            registerData.put("email",params[2]);
            registerData.put("description",params[3]);

            //convert hashmap to encoded url
            String data = Functions.hashMapToEncodedUrl(registerData);

            //fetch data from the web using GetJson class
            String response = GetJson.request(DataUrl.REGISTER_URL,data,"POST");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            //parse json
            registerList = JsonParser.SimpleParse(s);
            //store the parse result in my SimplePojo
            SimplePojo result = registerList.get(0);
            pd.dismiss(); //dismiss the Progress Dialog


            if(!result.getReturned()){ //query failed show alert message
                alert(result.getMessage());
            }else{ //sucessfull inserted in the database show success message and close the window
                Toast.makeText(getApplicationContext(),result.getMessage(),Toast.LENGTH_LONG).show();
                finish(); //close the activity
            }
        }
    } //async task

}
