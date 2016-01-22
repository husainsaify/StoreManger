package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddSalesman extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.salesmanName) EditText mSalesmanName;
    @Bind(R.id.addSalesman) Button mAddSalesman;
    @Bind(R.id.addSalesmanLinearLayout) LinearLayout mLayout;


    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private List<SimplePojo> mSalesmanList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_salesman);
        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_salesman);

        //Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Progress Dialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));

        /*
        * When mAddSalesman button is click call CheckSalesman method to validate salesman
        * */
        mAddSalesman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSalesman();
            }
        });
    }

    /*
    * Method to validate salesman name
    * */
    private void checkSalesman() {
        String name = mSalesmanName.getText().toString().trim();
        //check category is not empty
        if(name.isEmpty()){
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.name_cannot_be_empty));
        }else if(name.length() <= 3){
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.name_to_short));
        }else if(name.length() >= 20){
            Util.redSnackbar(getApplication(),mLayout,getString(R.string.name_should_be_less_20));
        }else{
            //Check Internet connection is available or not
            if(Util.isConnectedToInternet(getApplication())){
                /*
                * Get user Id From Shared Preferences*/
                String userId = MySharedPreferences.getInstance(getApplication()).getUserId();
                //Call addCategoryInBackground to add category to API
                addSalesmanInBackground(name, userId);

            }else{ //if not connected to internet
                Util.noInternetSnackbar(getApplication(),mLayout);
            }
        }
    }

    private void addSalesmanInBackground(final String name, final String userId) {
        //show progress Dialog
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.ADD_SALESMAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hide progressDialog
                pd.dismiss();
                //Method to parse the response send By the API and Show Result
                parseAddSalesResponse(response);
            }
        }, new Response.ErrorListener() {
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.PRAM_AS_SALESMAN,name);
                params.put(Keys.KEY_COM_USERID,userId);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseAddSalesResponse(String response) {
        //parse response and store the result in a list
        mSalesmanList = JsonParser.simpleParse(response);

        //check the response list is not null
        if(mSalesmanList != null){
            SimplePojo category = mSalesmanList.get(0);

            if(category.getReturned()){//success
                Util.greenSnackbar(getApplication(),mLayout,category.getMessage());
                //empty the exitText to enter category name
                mSalesmanName.setText("");
            }else{//error
                Util.redSnackbar(getApplication(),mLayout,category.getMessage());
            }
        }else{ //when the list is null show this message
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }
}
