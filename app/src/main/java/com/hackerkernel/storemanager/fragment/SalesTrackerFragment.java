package com.hackerkernel.storemanager.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.AddSalesActivity;
import com.hackerkernel.storemanager.adapter.SalesTrackerAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SalesTrackerDatePojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.SalesTrackerList;
import com.hackerkernel.storemanager.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment to for sales tracker
 */
public class SalesTrackerFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.layout) CoordinatorLayout mLayout;
    @Bind(R.id.fabAddSales) FloatingActionButton mFabAddSales;
    @Bind(R.id.placeholderWhenNoSalesAdded) TextView mPlaceholderWhenNoSalesAdded;
    @Bind(R.id.dateSpinnerLayout) TableLayout mDateSpinnerLayout;
    @Bind(R.id.dateSpinner) Spinner mDateSpinner;
    @Bind(R.id.recyclerview) RecyclerView mSalesTrackerRecyclerView;
    @Bind(R.id.profitOrLossLayout) LinearLayout mProfitOrLossLayout;
    @Bind(R.id.totalSellingPrice) TextView mProfitOrLossSellingprice;
    @Bind(R.id.totalCostPrice) TextView mProfitOrLossCostprice;
    @Bind(R.id.profitOrLossLabel) TextView mProfitOrLossLabel;
    @Bind(R.id.profitOrLoss) TextView mProfitOrLoss;
    private ProgressBar mToolbarProgressBar;

    private static final String TAG = SalesTrackerFragment.class.getSimpleName();
    private String mUserId;
    private RequestQueue mRequestQueue;
    private List<SalesTrackerDatePojo> mDateList;
    private List<SalesTrackerPojo> mSalesTrackerList;
    private Database db;

    private String mDateId = null;

    private SalesTrackerList mSalesList;


    public SalesTrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get userId of the logged in user
        mUserId = MySharedPreferences.getInstance(getActivity()).getUserId();

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        db = new Database(getActivity());

        mDateList = new ArrayList<>();
        mSalesTrackerList = new ArrayList<>();

        //indicate the Fragment will participate in menu creation
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_tracker, container, false);
        ButterKnife.bind(this, view);

        //get the refernce of the Toolbar Porgressbar
        mToolbarProgressBar = (ProgressBar) getActivity().findViewById(R.id.toolbarProgressBar);

        //set Layout manager to recyclerView
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mSalesTrackerRecyclerView.setLayoutManager(manager);

        //SalesTracker Date List
        checkInternetAndSetupDateSpinner();

        //Setup Sales List
        mSalesList = new SalesTrackerList(getActivity(),mToolbarProgressBar,mLayout,mSalesTrackerRecyclerView,true,true,mProfitOrLossLayout,mProfitOrLoss,mProfitOrLossLabel,mProfitOrLossCostprice,mProfitOrLossSellingprice);


        //When salesTracker date Spinner is selected
        mDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDateId = mDateList.get(position).getDateId();

                //method to fetch Sales Tracker
                mSalesList.CheckInternetAndSetupSalesTrackerList(mDateId,"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFabAddSales.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddSales:
                startActivity(new Intent(getActivity(), AddSalesActivity.class));
                break;
        }
    }

    /*
    * Show menu item on HomeActivity
    * */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"HUS: onOptionsItemSelected run 1");
        switch (item.getItemId()){
            case R.id.action_refresh:
                Log.d(TAG,"HUS: onOptionsItemSelected run 2");
                refreshList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /***************************
     * DATE LIST
     ************************************/

    private void checkInternetAndSetupDateSpinner() {
        if (Util.isConnectedToInternet(getActivity())) {
            //download fresh date list bra
            fetchDateListInBackground();
        } else { //no Internet avaiable
            Util.noInternetSnackbar(getActivity(), mLayout);

            //get DateList From SQLite database
            mDateList = db.getSalesTrackerDateList(mUserId);
            setUpDateSpinnerFromList(mDateList);
        }
    }

    private void fetchDateListInBackground() {
        Util.setProgressBarVisible(mToolbarProgressBar,true); //show progressbar

        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.SALES_TRACKER_DATE_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.setProgressBarVisible(mToolbarProgressBar, false); //hide progressbar
                parseDateListResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.setProgressBarVisible(mToolbarProgressBar, false); //hide progressbar
                //handle volley errorString
                Log.e(TAG, "fetchDateListInBackground: " + error.getMessage());
                String errrorString = VolleySingleton.handleVolleyError(error);
                if (errrorString != null) {
                    Util.redSnackbar(getActivity(), mLayout, errrorString);
                }

                //get DateList From SQLite database
                mDateList = db.getSalesTrackerDateList(mUserId);
                setUpDateSpinnerFromList(mDateList);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put(Keys.KEY_COM_USERID, mUserId);
                return param;
            }
        };

        //add to request queue
        mRequestQueue.add(request);
    }

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

                //Data SalesTracker date list into SQLite database
                db.deleteSalesTrackerDateList(mUserId);
                db.insertSalesTrackerDateList(mDateList, mUserId);
            } else {
                //check count is smaller then zero or return false
                String message = jsonObject.getString(Keys.KEY_COM_MESSAGE);
                boolean returned = jsonObject.getBoolean(Keys.KEY_COM_RETURN);
                int count = jsonObject.getInt(Keys.KEY_COM_COUNT);

                //returned false
                //check for error from API
                if (!returned) {
                    mDateSpinnerLayout.setVisibility(View.GONE);
                    mProfitOrLossLayout.setVisibility(View.GONE);

                    mPlaceholderWhenNoSalesAdded.setVisibility(View.VISIBLE);
                    mPlaceholderWhenNoSalesAdded.setText(message);
                    mPlaceholderWhenNoSalesAdded.setTextColor(Color.RED);
                }
                //check for no date add. Count will be zero when no sales added
                else if (count <= 0) {
                    //hide date spinner & profit or loss layout and show placeholder
                    mDateSpinnerLayout.setVisibility(View.GONE);
                    mProfitOrLossLayout.setVisibility(View.GONE);
                    mPlaceholderWhenNoSalesAdded.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "HUS: parseDateListResponse: " + e.getMessage());
            Log.d(TAG, "HUS: parseDateListResponse: " + response);
            mDateSpinnerLayout.setVisibility(View.GONE);
            mProfitOrLossLayout.setVisibility(View.GONE);
            mPlaceholderWhenNoSalesAdded.setVisibility(View.VISIBLE);
            mPlaceholderWhenNoSalesAdded.setText(R.string.unable_to_parse_response);
            mPlaceholderWhenNoSalesAdded.setTextColor(Color.RED);
        }
    }

    private void setUpDateSpinnerFromList(List<SalesTrackerDatePojo> list) {
        //create a String List For spinner
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getDate());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, stringList);
        mDateSpinner.setAdapter(adapter);
    }

    /*
    * Method to check internet and refresh SalesTrackerList
    *
    * */
    public void refreshList(){
        if(Util.isConnectedToInternet(getActivity())){
            //refresh DateList
            fetchDateListInBackground();
            //refresh SalesTracker
            mSalesList.CheckInternetAndSetupSalesTrackerList(mDateId, "");
        }else {
            Util.noInternetSnackbar(getActivity(),mLayout);
        }
    }
}

