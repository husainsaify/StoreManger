package com.hackerkernel.storemanager.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Class To fetch salesman From API if their is internet access
* else from the SQLite
* and Return a List of salesman
* */

public class GetSalesman {

    private RequestQueue mRequestQueue;
    private String mUserId;
    private Context mContext;
    private View mLayout;
    private Spinner mSalesmanSpinner;
    private List<SimpleListPojo> mSalesmanList;
    private Database db;
    private ProgressDialog pd;
    private static final String TAG = GetSalesman.class.getSimpleName();

    public GetSalesman(Context context,View layoutForSnackbar,Spinner salesmanSpinner){
        //set volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        //get userId of the user
        mUserId = MySharedPreferences.getInstance(context).getUserId();
        //Instanciate Database
        db = new Database(context);
        //Create ProgressDialog
        pd = new ProgressDialog(context);

        //store reference of the context & layout & spinner
        this.mContext = context;
        this.mLayout = layoutForSnackbar;
        this.mSalesmanSpinner = salesmanSpinner;
    }

    /*
    * METHOD TO CHECK INTERNET
    * IF AVAILABLE FETCH SALESMAN FROM API AND DISPLAY IN A SPINNER
    * IF NOT GET DATA FROM SQLITE
    * */
    public void setupSalesmanSpinner(){
        //check internet
        if(Util.isConnectedToInternet(mContext)){
            //fetch list from api
            fetchSalesmanInBackground();
        }else{
            //display spinner from sqlite
            mSalesmanList = db.getAllSimpleList(Database.SALESMAN,mUserId);
            setupSalesmanSpinnerFromList(mSalesmanList);

            //display no internet Toast
            Toast.makeText(mContext,R.string.please_check_your_internt,Toast.LENGTH_LONG).show();
        }
    }


    /*
    * METHOD TO FETCH SALESMAN LIST FROM THE API &
    * STORE THEM IN THE SQLITE DATABASE
    * */
    private void fetchSalesmanInBackground(){
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_SALESMAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                mSalesmanList = parseSalesmanResponse(response);
                if (mSalesmanList != null){
                    setupSalesmanSpinnerFromList(mSalesmanList);
                    //store fresh result in database
                    db.deleteAllSimpleList(Database.SALESMAN);
                    db.insertAllSimpleList(Database.SALESMAN,mSalesmanList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.d(TAG,"HUS: fetchSalesmanInBackground: Error "+error);
                //Handle Volley error
                Log.e(TAG, "ERROR " + error.getMessage());
                String errrorString = VolleySingleton.handleVolleyError(error);
                if (errrorString != null){
                    Util.redSnackbar(mContext,mLayout,errrorString);
                }

                //display salesman spinner from sqlite
                mSalesmanList = db.getAllSimpleList(Database.SALESMAN,mUserId);
                setupSalesmanSpinnerFromList(mSalesmanList);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID,mUserId);
                return params;
            }
        };

        //add to queue
        mRequestQueue.add(request);
    }

    /*
    *
    * Method to parse the salesman response from API which is called
    * By fetchSalesmanInBackground
    * */
    private List<SimpleListPojo> parseSalesmanResponse(String response) {
        List<SimpleListPojo> list = JsonParser.simpleListParser(response);

        if(list != null){
            //If json Response Return false Display message in SnackBar
            if(!list.get(0).isReturned()){
                Util.redSnackbar(mContext,mLayout,list.get(0).getMessage());
                return null;
            }else if(list.get(0).getCount() == 0){ //Count (Number of saleman returned)
                /*
                * If count return 0 means no salesman added
                * */
                Toast.makeText(mContext, "No salesman added yet", Toast.LENGTH_LONG).show();
                return null;
            }else{ //if we get true results
                /*
                * If result found
                * */
                return list;
            }
        }else{ // when return null
            Toast.makeText(mContext, R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void setupSalesmanSpinnerFromList(List<SimpleListPojo> list){
        //create a string List
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getName());
        }

        //set list to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_spinner_dropdown_item,stringList);
        mSalesmanSpinner.setAdapter(adapter);
    }

    /*
    * Getter for getting salesman From mSalesmanList
    * */
    public SimpleListPojo getSalesman(int position){
        return mSalesmanList.get(position);
    }
}
