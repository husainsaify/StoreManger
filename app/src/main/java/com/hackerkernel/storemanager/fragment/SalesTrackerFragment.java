package com.hackerkernel.storemanager.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SalesTrackerDatePojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

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
    @Bind(R.id.fabAddSales) FloatingActionButton mFabAddSales;
    @Bind(R.id.placeholderWhenNoSalesAdded) TextView mPlaceholderWhenNoSalesAdded;
    @Bind(R.id.dateSpinnerLayout) TableLayout mDateSpinnerLayout;
    @Bind(R.id.layout) CoordinatorLayout mLayout;

    private static final String TAG = SalesTrackerFragment.class.getSimpleName();
    private String mUserid;
    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private List<SalesTrackerDatePojo> mDateList;

    public SalesTrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get userId of the logged in user
        mUserid = MySharedPreferences.getInstance(getActivity()).getUserId();

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Setup ProgressDialog
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        mDateList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_tracker, container, false);
        ButterKnife.bind(this,view);

        fetchDateListInBackground();

        mFabAddSales.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabAddSales:
                startActivity(new Intent(getActivity(), AddSalesActivity.class));
                break;
        }
    }

    /*************************** DATE LIST ************************************/

    private void fetchDateListInBackground(){
        pd.show(); //show progressbar

        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.SALES_TRACKER_DATE_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide progressbar
                parseDateListResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss(); //hide progressbar
                //handle volley errorString
                Log.e(TAG,"fetchDateListInBackground "+error.getMessage());
                String errrorString = VolleySingleton.handleVolleyError(error);
                if (errrorString != null){
                    Util.redSnackbar(getActivity(), mLayout, errrorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Keys.KEY_COM_USERID,mUserid);
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
            if(mDateList != null){
                //Setup date spinner
                Log.d(TAG, "HUS: HHH 1");
            }else{
                //check count is smaller then zero or return false
                String message = jsonObject.getString(Keys.KEY_COM_MESSAGE);
                boolean returned = jsonObject.getBoolean(Keys.KEY_COM_RETURN);
                int count = jsonObject.getInt(Keys.KEY_COM_COUNT);

                //returned false
                if(!returned){
                    mDateSpinnerLayout.setVisibility(View.GONE);
                    mPlaceholderWhenNoSalesAdded.setVisibility(View.VISIBLE);
                    mPlaceholderWhenNoSalesAdded.setText(message);
                    mPlaceholderWhenNoSalesAdded.setTextColor(Color.RED);
                }else if(count <= 0){
                    //hide date spinner and show placeholder
                    mDateSpinnerLayout.setVisibility(View.GONE);
                    mPlaceholderWhenNoSalesAdded.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG,"HUS: parseDateListResponse: "+e.getMessage());
            Log.d(TAG, "HUS: parseDateListResponse: " + response);
            mDateSpinnerLayout.setVisibility(View.GONE);
            mPlaceholderWhenNoSalesAdded.setVisibility(View.VISIBLE);
            mPlaceholderWhenNoSalesAdded.setText(R.string.unable_to_parse_response);
            mPlaceholderWhenNoSalesAdded.setTextColor(Color.RED);
        }
    }
}

