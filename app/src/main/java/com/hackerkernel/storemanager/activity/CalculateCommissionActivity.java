package com.hackerkernel.storemanager.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.CalculateCommissionPojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.GetSalesman;
import com.hackerkernel.storemanager.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalculateCommissionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CalculateCommissionActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.salesmanSpinner) Spinner mSalesmanSpinner;
    @Bind(R.id.layout) RelativeLayout mLayoutForSnackbar;
    @Bind(R.id.fromDateButton) Button mFromDateButton;
    @Bind(R.id.toDateButton) Button mToDateButton;
    @Bind(R.id.toDateLabel) TextView mToDateLabel;
    @Bind(R.id.fromDateLabel) TextView mFromDateLabel;
    @Bind(R.id.calculateCommission) Button mCalculateCommission;
    @Bind(R.id.commissionPercentageEditText) EditText mCommissionPercentageEditText;
    //Info
    @Bind(R.id.errorMessage) TextView mErrorMessageInfo;
    @Bind(R.id.commissionInfoTable) TableLayout mCommissionInfoLayout;
    @Bind(R.id.commissionAmount) TextView mCommissionAmountInfo;
    @Bind(R.id.totalSellingPrice) TextView mTotalSellingPriceInfo;
    @Bind(R.id.totalCostPrice) TextView mTotalCostPriceInfo;
    @Bind(R.id.profitOrLoss) TextView mProfitOrLossInfo;
    @Bind(R.id.totalSales) TextView mTotalSalesInfo;
    @Bind(R.id.totalProductSold) TextView mTotalProductSoldInfo;


    private String mSalesmanId = "";
    private String mUserId;
    private RequestQueue mRequestQueue;

    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mToDatePickerDialog;
    private int mYear;
    private int mMonth;
    private int mDay;

    //Varaible to store from & to date id Ex: 06012016
    private String mFromDateId = "";
    private String mToDateId = "";
    private String mPercentage = "";

    //Variable to store from & to date Ex: 06/01/2016
    private String mFromDate = "";
    private String mToDate = "";


    //Field to cal From date is not greater then TO date
    private SimpleDateFormat simpleDateFormat;
    private Date mDateFrom;
    private Date mDateTo;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_commission);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.calculate_commission));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup salesman spinner
        final GetSalesman getSalesman = new GetSalesman(this,mLayoutForSnackbar,mSalesmanSpinner);
        getSalesman.setupSalesmanSpinner();

        //when Salesman is selected from spinner
        mSalesmanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSalesmanId = getSalesman.getSalesman(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Get user id
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();

        //Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //get Today date
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        //create ProgressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        setOnClickOnView();

        setDatePicker();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fromDateButton:
                mFromDatePickerDialog.show();
                break;
            case R.id.toDateButton:
                mToDatePickerDialog.show();
                break;
            case R.id.calculateCommission:
                calculateCommission();
                break;
        }
    }

    private void setOnClickOnView(){
        mFromDateButton.setOnClickListener(this);
        mToDateButton.setOnClickListener(this);
        mCalculateCommission.setOnClickListener(this);
    }

    /*
    * METHOD TO SET DATE PICKER BOTH FROM AND TO
    * */
    private void setDatePicker(){

        //From date picker
        mFromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1; //add one to month because month start with zero index

                mFromDateId = Util.createDateId(dayOfMonth,monthOfYear,year,1);
                mFromDate = Util.createDateId(dayOfMonth, monthOfYear, year, 2);

                //set selected Date to label
                mFromDateLabel.setText(new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear).append("/").append(year));
                //set date
                try {
                    mDateFrom = simpleDateFormat.parse(dayOfMonth+"-"+monthOfYear+"-"+year);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "HUS: setDatePicker " + e.getMessage());
                    Toast.makeText(getApplication(),"Unable to parse date format",Toast.LENGTH_LONG).show();
                }
            }
        },mYear,mMonth,mDay);
        //disable future dates
        mFromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


        //TO date picker
        mToDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1; //add one to month because month start with zero index

                mToDateId = Util.createDateId(dayOfMonth,monthOfYear,year,1);
                mToDate = Util.createDateId(dayOfMonth, monthOfYear, year, 2);

                //set selected Date to label
                mToDateLabel.setText(new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear).append("/").append(year));
                //set date
                try {
                    mDateTo = simpleDateFormat.parse(dayOfMonth+"-"+monthOfYear+"-"+year);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "HUS: setDatePicker " + e.getMessage());
                    Toast.makeText(getApplication(),"Unable to parse date format",Toast.LENGTH_LONG).show();
                }
            }
        },mYear,mMonth,mDay);
        //disable future dates
        mToDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }


    /*
    * METHOD TO PERFORM CHECK AND CALCULATE COMMISSION
    * */
    private void calculateCommission() {
        //check internet connection
        if(Util.isConnectedToInternet(getApplication())){
            mPercentage = mCommissionPercentageEditText.getText().toString().trim();

            //check salesman
            if (mSalesmanId.isEmpty()){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.select_salesman));
            }
            //check from date
            else if (mFromDateId.isEmpty()){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.select_from_date));
            }
            //check to date
            else if (mToDateId.isEmpty()){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.select_to_date));
            }
            //check to date is
            else if (mDateFrom.compareTo(mDateTo) > 0){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.from_date_cannot_be_after_to));
            }
            //check Commission editText is not empty
            else if(mPercentage.isEmpty()){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.enter_commission_percentage));
            }
            //check percentage in not more then 100
            else if(Float.parseFloat(mPercentage) > 100){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.commission_per_cannot_be_more_then_100));
            }
            //request api
            else{
                calculateCommissionInBackground();
            }
        }else{
            Util.noInternetSnackbar(getApplication(),mLayoutForSnackbar);
        }
    }

    /*
    * Method to send Commission date to API backend
    * */
    private void calculateCommissionInBackground(){
        pd.show(); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.CAL_SALESMAN_COMMISSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide(); //hide progressbar
                parseSalesmanCommissionResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide(); //hide progressbar
                Log.e(TAG,"HUS: calculateCommissionInBackground "+error.getMessage());
                //handle volley error
                String errorString = VolleySingleton.handleVolleyError(error);
                if (errorString != null){
                    Util.redSnackbar(getApplication(),mLayoutForSnackbar,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID,mUserId);
                params.put(Keys.PRAM_COMMISSION_FROMDATE,mFromDate);
                params.put(Keys.PRAM_COMMISSION_TODATE,mToDate);
                params.put(Keys.KEY_COM_SALESMANID,mSalesmanId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    private void parseSalesmanCommissionResponse(String response) {
        List<CalculateCommissionPojo> list = JsonParser.parseCalculateCommission(response);
        //check list is not empty
        if(list != null){
            if (list.get(0).getReturned()){ //true
                setupViewsFromList(list);
            }else{
                mErrorMessageInfo.setVisibility(View.VISIBLE);
                mCommissionInfoLayout.setVisibility(View.GONE);
                mErrorMessageInfo.setText(list.get(0).getMessage());
            }
        }else{
            Toast.makeText(getApplicationContext(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
            Log.e(TAG,"HUS: parseSalesmanCommissionResponse "+response);
        }
    }


    /*
    *
    * Method to setup all view like cost price, sellingprice, Total sales From List
    * */
    private void setupViewsFromList(List<CalculateCommissionPojo> list) {
        //Hide error message and display info layout
        mErrorMessageInfo.setVisibility(View.GONE);
        mCommissionInfoLayout.setVisibility(View.VISIBLE);

        CalculateCommissionPojo current = list.get(0);

        //Calculate Percentage
        float per = (Integer.parseInt(current.getSellingprice()) * Float.parseFloat(mPercentage)) / 100;
        //calculate profit or loss
        int costprice = Integer.parseInt(current.getCostprice());
        int sellingprice = Integer.parseInt(current.getSellingprice());

        String profitOrLoss;
        if (costprice > sellingprice) {//loss
            //cal loss
            int loss = costprice - sellingprice;
            profitOrLoss = "Loss: RS " + loss;
        } else {
            if (sellingprice > costprice) { //profit
                //call profit
                int profit = sellingprice - costprice;
                profitOrLoss = "Profit: RS " + profit;
            } else { // neutral CP = sales
                profitOrLoss = "Break Even";
            }
        }

        mCommissionAmountInfo.setText(String.valueOf(per));
        mTotalCostPriceInfo.setText(current.getCostprice());
        mTotalSellingPriceInfo.setText(current.getSellingprice());
        mTotalSalesInfo.setText(current.getNoOfSales());
        mTotalProductSoldInfo.setText(current.getNoOfItemSold());
        mProfitOrLossInfo.setText(profitOrLoss);

    }


}
