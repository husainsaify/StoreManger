package com.hackerkernel.storemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.adapter.SalesTrackerAdapter;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.STdatePojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SaleTrackerActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.dateDropDown) Spinner mDateDropDown;
    @Bind(R.id.salesList) ListView mSalesListView;

    private List<STdatePojo> mDropdownList;
    private String mUserId,
            mDate,
            mDateId;
    private List<SalesTrackerPojo> mSalesList;

    private final String TAG = SaleTrackerActivity.class.getSimpleName();
    private final Context context = SaleTrackerActivity.this;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_tracker);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.sale_tracker));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //get user_id
        DataBase db = new DataBase(this);
        mUserId = db.getUserID();

        //create a progressDialog when we fetch sales
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        //fetch date for spinner
        new DateDropDownTask().execute();

        //when spinner is clicked
        mDateDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //store new date & dateId
                mDate = mDropdownList.get(position).getDate();
                mDateId = mDropdownList.get(position).getDateId();
                /*
                * Call AsyncTask and set mSalesListView
                * */
                new GetSalesTask().execute(mDateId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplication(), "Please select a date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * Class to fetch date for mDateDropDown Menu
    * */
    private class DateDropDownTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", mUserId);

            //build encoded url
            String data = Functions.hashMapToEncodedUrl(hashMap);

            //request the web
            String jsonString = GetJson.request(DataUrl.SALES_TRACKER_DATE_LIST, data, "POST");
            //parse json
            mDropdownList = JsonParser.STdateParser(jsonString);
            List<String> stringList = new ArrayList<>();

            if(mDropdownList.size() != 0){
                //Today's date & dateId
                mDate = mDropdownList.get(0).getDate();
                mDateId = mDropdownList.get(0).getDateId();
                Log.d(TAG,"HUS: date "+mDate);
                Log.d(TAG, "HUS: dateId " + mDateId);

                //generate String list to send to PostExecute
                for (int i = 0; i < mDropdownList.size(); i++) {
                    stringList.add(mDropdownList.get(i).getDate());
                }
            }

            //if list will be empty Null is send to PostExecute method
            return stringList;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            if(list.size() != 0){
                //set the date dropdown spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SaleTrackerActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mDateDropDown.setAdapter(adapter);
            }else{
                //show a error message of no sales
                Functions.errorAlert(context,getString(R.string.oops),getString(R.string.no_sales));
            }
            /*
            * Call AsyncTask and set mSalesListView
            * */
            new GetSalesTask().execute(mDateId);
        }
    }


    /*
    * Class to fetch all the sales of the current date
    * */
    private class GetSalesTask extends AsyncTask<String,Void,List<SalesTrackerPojo>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progressDialog
            pd.show();
        }

        @Override
        protected List<SalesTrackerPojo> doInBackground(String... params) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("user_id",mUserId);
            hashMap.put("date_id",params[0]);

            String data = Functions.hashMapToEncodedUrl(hashMap);

            String jsonString = GetJson.request(DataUrl.GET_SALES_TRACKER, data, "POST");
            Log.d(TAG, "HUS: Raw json "+jsonString);
            //parse json
            return JsonParser.SalesTrackerParser(getApplication(),jsonString);
        }

        @Override
        protected void onPostExecute(List<SalesTrackerPojo> list) {
            if(list.size() != 0){
                SalesTrackerAdapter adapter = new SalesTrackerAdapter(getApplication(),R.layout.sales_tracker_list_layout,list);
                mSalesListView.setAdapter(adapter);
            }else{
                Toast.makeText(getApplication(),getString(R.string.faild_to_load_sales_list),Toast.LENGTH_LONG).show();
            }
            pd.dismiss();
        }
    }

}
