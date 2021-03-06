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


public class AddCategoryActivity extends AppCompatActivity {
    private static final String TAG = AddCategoryActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.categoryName) EditText mCategoryName;
    @Bind(R.id.addCategory) Button mAddCategory;
    @Bind(R.id.addCategoryLinearLayout) LinearLayout mLayout;


    private List<SimplePojo> categoryList;
    private ProgressDialog pd;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this);

        //set toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null; //avoid nullpointer warning
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_category);

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));


        //instanciate the Volley RequestQueue object
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();


        //when add category button is added
        mAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = mCategoryName.getText().toString().trim();
                addCategory(category);
            }
        });
    }

    //add category
    private void addCategory(String category){
        //check category is not empty
        if(category.isEmpty()){
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.category_canot_empty));
        }else if(category.length() <= 3){
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.category_more_3_char));
        }else if(category.length() >= 20){
            Util.redSnackbar(getApplication(),mLayout,getString(R.string.category_more_20_char));
        }else{
            //Check Internet connection is available or not
            if(Util.isConnectedToInternet(getApplication())){
                /*
                * Get user Id From Shared Preferences*/
                String userId = MySharedPreferences.getInstance(getApplication()).getUserId();
                //Call addCategoryInBackground to add category to API
                addCategoryInBackground(category,userId);

            }else{ //if not connected to internet
                Util.noInternetSnackbar(getApplication(),mLayout);
            }
        }
    }

    /*
    * Method to add Category to API
    * */
    public void addCategoryInBackground(final String categoryName, final String userId){
        //show ProgressBar
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.ADD_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hide progressbar
                pd.dismiss();
                //Method to parse the response send By the API and Show Result
                parseAddCategoryResponse(response);
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
                params.put(Keys.KEY_COM_CATEGORYNAME,categoryName);
                params.put(Keys.KEY_COM_USERID,userId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    private void parseAddCategoryResponse(String response) {
        //parse response and store the result in a list
        categoryList = JsonParser.simpleParser(response);

        //check the response list is not null
        if(categoryList != null){
            SimplePojo category = categoryList.get(0);

            if(category.getReturned()){//success
                Util.greenSnackbar(getApplication(),mLayout,category.getMessage());
                //empty the exitText to enter category name
                mCategoryName.setText("");
            }else{//error
                Util.redSnackbar(getApplication(),mLayout,category.getMessage());
            }
        }else{ //when the list is null show this message
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }
}
