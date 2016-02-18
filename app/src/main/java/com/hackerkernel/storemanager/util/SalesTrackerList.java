package com.hackerkernel.storemanager.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.adapter.SalesTrackerAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to setup SalesTrackerList (Recyclerview)
 */
public class SalesTrackerList {

    private static final String TAG = SalesTrackerList.class.getSimpleName();
    private Context mContext;
    private ProgressBar mToolbarProgressBar;
    private View mLayout;
    private Boolean mSaveToCache;
    private List<SalesTrackerPojo> mSalesTrackerList;
    private RecyclerView mSalesTrackerRecyclerView;
    private Boolean mDisplayProfitOrLossLayout;

    //Profit or loss layout
    private View mProfitOrLossLayout;
    private TextView mProfitOrLoss;
    private TextView mProfitOrLossLabel;
    private TextView mProfitOrLossCostprice;
    private TextView mProfitOrLossSellingprice;

    //Prams to send to API
    private String mUserId;
    private String mDateId;
    private String mSalesmanId;

    //Volley
    private RequestQueue mRequestQueue;

    /*
    * Constructor which does not take Profit & loss layout in account
    * */
    public SalesTrackerList(Context context,ProgressBar toolbarProgressBar,View layoutForSnackbar,RecyclerView salesTrackerRecyclerView,Boolean saveToCache,Boolean displayProfitOrLossLayout){
        this.mContext = context;
        this.mToolbarProgressBar = toolbarProgressBar;
        this.mLayout = layoutForSnackbar;
        this.mSalesTrackerRecyclerView = salesTrackerRecyclerView;
        this.mSaveToCache = saveToCache;
        //Profit or loss
        this.mDisplayProfitOrLossLayout = displayProfitOrLossLayout;

        //Get UserId & setup Volley
        mUserId = MySharedPreferences.getInstance(mContext).getUserId();
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
    }

    /*
    * Constructor which take profit or loss layout in account
    * */
    public SalesTrackerList(Context context,ProgressBar toolbarProgressBar,View layoutForSnackbar,RecyclerView salesTrackerRecyclerView,Boolean saveToCache,Boolean displayProfitOrLossLayout,View profitOrLossLayout,TextView profitOrloss,TextView profitOrLossLabel,TextView profitOrLossCostprice,TextView profitOrLossSellingprice){
        this.mContext = context;
        this.mToolbarProgressBar = toolbarProgressBar;
        this.mLayout = layoutForSnackbar;
        this.mSalesTrackerRecyclerView = salesTrackerRecyclerView;
        this.mSaveToCache = saveToCache;

        //Profit or loss
        this.mDisplayProfitOrLossLayout = displayProfitOrLossLayout;
        this.mProfitOrLossLayout = profitOrLossLayout;
        this.mProfitOrLoss = profitOrloss;
        this.mProfitOrLossLabel = profitOrLossLabel;
        this.mProfitOrLossCostprice = profitOrLossCostprice;
        this.mProfitOrLossSellingprice = profitOrLossSellingprice;

        //Get UserId & setup Volley
        mUserId = MySharedPreferences.getInstance(mContext).getUserId();
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
    }

    /*
    *
    * If internet is avaialble get fresh salestracker list
    * else get list from chache or show No internet Snackbar
    * */
    //Leave salesm
    public void CheckInternetAndSetupSalesTrackerList(String dateId,String salesmanId) {
        //save DateId & SalesmanId
        this.mDateId = dateId;
        this.mSalesmanId = salesmanId;

        /*
        * Check internet if present fetch sales data From API
        * Check Chache and display list else show no internet snackbar
        * */
        if (Util.isConnectedToInternet(mContext)){
            fetchSalesTrackerInBackground();
        } else {
            //Hide ProfitOrLossLayout (If mDisplayProfitOrLoss is true)
            if (mDisplayProfitOrLossLayout){
                mProfitOrLossLayout.setVisibility(View.GONE);
            }
            //saveToCache is true
            if(mSaveToCache){
                setupSalesTrackerListFromCache(mDateId);
            }
            //show no Internet Snackbar
            else{
                Util.noInternetSnackbar(mContext, mLayout);
            }
        }
    }

    /*
    * Method to fetch fresh SalesTracker List From API
    * & save to Chache If mSaveToCache is true
    *
    * */
    private void fetchSalesTrackerInBackground() {
        Util.setProgressBarVisible(mToolbarProgressBar,true); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_SALES_TRACKER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.setProgressBarVisible(mToolbarProgressBar,false); //hide progressbar
                //parse SalesTracker Response
                parseSalesTrackerResponse(response);

                //Check mSaveToCache is true save data to check
                if(mSaveToCache){
                    //save sales tracker json response to o cache
                    saveSalesTrackerToCache(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.setProgressBarVisible(mToolbarProgressBar,false); //hide progressbar
                Log.e(TAG, "HUS: fetchSalesTrackerInBackground: " + error.getMessage());
                //handle volley error
                String errorString = VolleySingleton.handleVolleyError(error);
                if (errorString != null) {
                    Util.redSnackbar(mContext, mLayout, errorString);
                }

                //Check mSaveToCache is true if yes display SalesTracker which is save in cache
                if(mSaveToCache){
                    //Display SalesTracker List From cache (Local storage)
                    setupSalesTrackerListFromCache(mDateId);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put(Keys.KEY_COM_USERID, mUserId);
                param.put(Keys.KEY_COM_SALESMANID,mSalesmanId);
                param.put(Keys.KEY_ST_DATELIST_DATE_ID, mDateId);
                return param;
            }
        };

        mRequestQueue.add(request);
    }

    private void parseSalesTrackerResponse(String response) {
        mSalesTrackerList = JsonParser.salesTrackerParser(response);

        //check list is not null
        if (mSalesTrackerList != null) {
            //check returned
            Log.d(TAG, "HUS: " + mSalesTrackerList.get(0).getReturned());
            //return is false
            if (!mSalesTrackerList.get(0).getReturned()) {
                Util.redSnackbar(mContext, mLayout, mSalesTrackerList.get(0).getMessage());
            }
            else {
                setupSalesTrackerRecyclerViewFromList(mSalesTrackerList);

                //Set Profit & Loss State (if mDisplayProfitOrLoss is true)
                if (mDisplayProfitOrLossLayout){
                    setProfitOrLossState(mSalesTrackerList.get(0).getTotalCostprice(), mSalesTrackerList.get(0).getTotalSellingprice());
                }
            }
        } else {
            Toast.makeText(mContext, R.string.unable_to_parse_response, Toast.LENGTH_LONG).show();
            Log.e(TAG, "HUS: parseSalesTrackerResponse: " + response);
        }
    }

    private void setupSalesTrackerRecyclerViewFromList(List<SalesTrackerPojo> list) {
        //If mDisplayProfitOrLoss is true then only display ProfitOrLoss Layout
        if(mDisplayProfitOrLossLayout){
            showSalesTrackerVisible(true); //set ST visible
        }

        SalesTrackerAdapter adapter = new SalesTrackerAdapter(mContext);
        adapter.setList(list);
        mSalesTrackerRecyclerView.setAdapter(adapter);
    }

    /*************************** Profit & loss layout **************************/

    /*
    * Method to cal profit loss from total cp & sp and display it
    * */
    private void setProfitOrLossState(String totalCp, String totalSp) {
        int cp = Integer.parseInt(totalCp);
        int sp = Integer.parseInt(totalSp);

        String label;
        int value;
        if (cp > sp) {//loss
            //cal loss
            value = cp - sp;
            label = "Loss";
        } else {
            if (sp > cp) { //profit
                //call profit
                value = sp - cp;
                label = "Profit";
            } else { // neutral CP = sales
                value = 0;
                label = "Break Even";
            }
        }

        //set Label and value to TextView
        mProfitOrLossLabel.setText(label);
        mProfitOrLoss.setText(String.valueOf(value));
        mProfitOrLossCostprice.setText(String.valueOf(cp));
        mProfitOrLossSellingprice.setText(String.valueOf(sp));
    }

    /*
    * METHOD TO DISPLAY SALES TRACKER LIST FROM CACHE
    * */
    private void setupSalesTrackerListFromCache(String dateId) {
        String jsonResponse = getSalesTrackerFromCache(dateId);
        if (jsonResponse != null) {
            parseSalesTrackerResponse(jsonResponse);
        } else {
            Toast.makeText(mContext, R.string.check_internt_failed_to_get_st_from_local, Toast.LENGTH_LONG).show();
            //showSalesTrackerVisible(false);
        }
    }

    /*
    * Method to toggle ST RecyclerView and ProfitOrLoss layout
    * */
    private void showSalesTrackerVisible(boolean para){
        if (para){ //true display
            mProfitOrLossLayout.setVisibility(View.VISIBLE);
            mSalesTrackerRecyclerView.setVisibility(View.VISIBLE);
        }else{ //false hide
            mProfitOrLossLayout.setVisibility(View.GONE);
            mSalesTrackerRecyclerView.setVisibility(View.GONE);
        }
    }

    /************************
     * Method to store and retrive SalesTracker json response in cache
     ************************/
    private void saveSalesTrackerToCache(String jsonString) {
        File cacheDir = mContext.getCacheDir();
        File file = new File(cacheDir.getAbsolutePath(), mDateId);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "HUS: saveSalesTrackerToCache: " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "HUS: saveSalesTrackerToCache: " + e.getMessage());
            }
        }
    }

    private String getSalesTrackerFromCache(String dateId) {
        File cacheDir = mContext.getCacheDir();
        File file = new File(cacheDir.getAbsolutePath(), dateId);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int read;
            StringBuilder sb = new StringBuilder();
            while ((read = fis.read()) != -1) {
                sb.append((char) read);
            }
            fis.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "HUS: getSalesTrackerFromCache: " + e.getMessage());
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "HUS: getSalesTrackerFromCache: " + e.getMessage());
                }
            }
        }
    }
}
