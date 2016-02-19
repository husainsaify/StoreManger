package com.hackerkernel.storemanager.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.hackerkernel.storemanager.pojo.SalesTrackerDatePojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.SalesTrackerList;
import com.hackerkernel.storemanager.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SalesmanSalesDetailActivity extends AppCompatActivity {
    private static final String TAG = SalesmanSalesDetailActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbarSpinner) Spinner mToolbarSpinner;
    @Bind(R.id.toolbarProgressBar) ProgressBar mToolbarProgressBar;
    @Bind(R.id.layout) RelativeLayout mLayoutForSnackbar;
    @Bind(R.id.errorMessage) TextView mErrorMessage;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    //Profit or loss layout
    @Bind(R.id.profitOrLossLayout) View mProfitOrLossLayout;
    @Bind(R.id.profitOrLoss) TextView mProfitOrLoss;
    @Bind(R.id.profitOrLossLabel) TextView mProfitOrLossLabel;
    @Bind(R.id.totalCostPrice) TextView mProfitOrLossCostPrice;
    @Bind(R.id.totalSellingPrice) TextView mProfitOrLossSellingPrice;

    private String mUserId;
    private String mSalesmanId;
    private RequestQueue mRequestQueue;
    private List<SalesTrackerDatePojo> mDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_sales_detail);
        ButterKnife.bind(this);

        //set actionbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get userId
        mUserId = MySharedPreferences.getInstance(getApplicationContext()).getUserId();

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //get Salesman Id
        if (getIntent().hasExtra(Keys.KEY_COM_SALESMANID)){
            mSalesmanId = getIntent().getExtras().getString(Keys.KEY_COM_SALESMANID);
        }else{
            Toast.makeText(getApplicationContext(),R.string.internal_error_restart_app,Toast.LENGTH_LONG).show();
            this.finish();
        }

        //add layout manager to recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(layoutManager);

        //Setup Sales Date Spinner
        checkInternetAndSetupDateSpinner();

        //Instanciate SalesTrackerList for setting up Sales Lis
        final SalesTrackerList salesTrackerList = new SalesTrackerList(this,mToolbarProgressBar,mLayoutForSnackbar,mRecyclerView,false,true,mProfitOrLossLayout,mProfitOrLoss,mProfitOrLossLabel,mProfitOrLossCostPrice,mProfitOrLossSellingPrice);


        /*
        * When a date is selected on Toolbar Spinner
        * */
        mToolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Check Selected date is All or specific date(10/2/2016)
                String dateId;
                if(position == 0){ //all
                   dateId = "all";
                }else { //specific date
                    //create new position for mDateList because on the first index is All(all sales ) is added
                    int newPosition = position - 1;
                    dateId = mDateList.get(newPosition).getDateId();
                }

                //Setup Sales RecyclerView
                salesTrackerList.CheckInternetAndSetupSalesTrackerList(dateId,mSalesmanId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salesman_sales_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                checkInternetAndSetupDateSpinner();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    * Method to check internet and if present fetch list
    * else Display no internet snackbar
    * */
    private void checkInternetAndSetupDateSpinner(){
        if(Util.isConnectedToInternet(getApplicationContext())){
            fetchSalesmanSalesDateListInBackground();
        }else {
            mToolbarSpinner.setVisibility(View.GONE);
            mProfitOrLossLayout.setVisibility(View.GONE);
            Util.noInternetSnackbar(getApplicationContext(),mLayoutForSnackbar);
        }
    }

    /*
     * Method to fetch SalesmanSalesDateList in background
     * */
    public void fetchSalesmanSalesDateListInBackground(){
        Util.setProgressBarVisible(mToolbarProgressBar,true); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_SALESMAN_SALES_DATE_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.setProgressBarVisible(mToolbarProgressBar,false); //hide progressbar
                //parse response
                parseDateListResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.setProgressBarVisible(mToolbarProgressBar,false); //hide progressbar
                Log.e("HUS","HUS: fetchSalesmanSalesDateListInBackground: "+error.getMessage());
                //handle volley error
                String stringError = VolleySingleton.handleVolleyError(error);
                if (stringError != null){
                    Util.redSnackbar(getApplication(),mLayoutForSnackbar,stringError);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Keys.KEY_COM_USERID,mUserId);
                param.put(Keys.KEY_COM_SALESMANID,mSalesmanId);
                return param;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse date List response
    * */
    private void parseDateListResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            //Parse Sales tracker date list response & store it in a list
            mDateList = JsonParser.salesTrackerDateParser(jsonObject);

            /*
            * check mDateList is not null
            * mDateList is only null when count is smaller then zero
            * or return is false
            * */
            if (mDateList != null) {
                //Setup date spinner
                setUpDateSpinnerFromList(mDateList);
            } else {
                //check count is smaller then zero or return false
                String message = jsonObject.getString(Keys.KEY_COM_MESSAGE);
                boolean returned = jsonObject.getBoolean(Keys.KEY_COM_RETURN);
                int count = jsonObject.getInt(Keys.KEY_COM_COUNT);

                //returned false
                //check for error from API
                if (!returned) {
                    //hide dateSpinner
                    mToolbarSpinner.setVisibility(View.GONE);
                    //hide recycler view
                    mRecyclerView.setVisibility(View.GONE);
                    //show error snackbar
                    Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,message);
                }
                //check for no date add. Count will be zero when no sales added
                else if (count <= 0) {

                    mErrorMessage.setVisibility(View.VISIBLE);
                    //hide ProfitOrLoss layout
                    mProfitOrLossLayout.setVisibility(View.GONE);
                    /*
                    * not hiding date spinner and recycler view because if count is zero the return from api will be false
                        and if its false recyclerView & Date spinner will be hidden above in If
                    * */
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "HUS: parseDateListResponse: " + e.getMessage());
            Log.d(TAG, "HUS: parseDateListResponse: " + response);
            mRecyclerView.setVisibility(View.GONE);
            mErrorMessage.setVisibility(View.VISIBLE);
            mErrorMessage.setText(R.string.unable_to_parse_response);
            mErrorMessage.setTextColor(Color.RED);
        }
    }

    /*
    * Method to setup Date spinner from Date List
    * */
    private void setUpDateSpinnerFromList(List<SalesTrackerDatePojo> list) {
        //create a String List For spinner
        List<String> stringList = new ArrayList<>();
        stringList.add("All");
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getDate());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_dropdown_item_1line, stringList);
        mToolbarSpinner.setVisibility(View.VISIBLE);
        mToolbarSpinner.setAdapter(adapter);
    }
}
