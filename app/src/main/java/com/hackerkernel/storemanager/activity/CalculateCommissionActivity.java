package com.hackerkernel.storemanager.activity;

import android.app.DatePickerDialog;
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
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.GetSalesman;
import com.hackerkernel.storemanager.util.Util;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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


    private String mSalesmanId = "";
    private String mUserId;
    private RequestQueue mRequestQueue;

    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mToDatePickerDialog;
    private int mYear;
    private int mMonth;
    private int mDay;

    //Varaible to store from & to date
    private String mFromDateId = "";
    private String mToDateId = "";

    //Field to cal From date is not greater then TO date
    private SimpleDateFormat simpleDateFormat;
    private Date mDateFrom;
    private Date mDateTo;

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

        //when Salesman is selected from spinnner
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
                calculateCommmission();
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

                mFromDateId = createDateId(dayOfMonth,monthOfYear,year);
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

                mToDateId = createDateId(dayOfMonth,monthOfYear,year);
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
    * Method to take day , month & year and generate a DateId
    * */
    private String createDateId(int day,int month,int year){
        String m = String.valueOf(month);
        String d = String.valueOf(day);

        //check month is single character or double because we have to prepend 0 if single

        //single char
        if(day < 10){
            d = 0+d;
        }

        //single char
        if(month < 10){
            m = 0+m;
        }

        //create dateId
        return d + m + year;
    }

    /*
    * METHOD TO PERFORM CHECK AND CALCULATE COMMISSION
    * */
    private void calculateCommmission() {
        //check internet connection
        if(Util.isConnectedToInternet(getApplication())){
            String percentage = mCommissionPercentageEditText.getText().toString().trim();

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
            //check from and to date are not same
            else if (Integer.parseInt(mFromDateId) == Integer.parseInt(mToDateId)){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.select_from_to_date));
            }
            //check to date is
            else if (mDateFrom.compareTo(mDateTo) > 0){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.from_date_cannot_be_after_to));
            }
            //check Commission editText is not empty
            else if(percentage.isEmpty()){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.enter_commission_percentage));
            }
            //check percentage in not more then 100
            else if(Integer.parseInt(percentage) > 100){
                Util.redSnackbar(getApplicationContext(),mLayoutForSnackbar,getString(R.string.commission_per_cannot_be_more_then_100));
            }
            //request api
            else{
                calculateCommmissionInBackground(percentage);
            }
        }else{
            Util.noInternetSnackbar(getApplication(),mLayoutForSnackbar);
        }
    }

    /*
    * Method to send Commission date to API backend
    * */
    private void calculateCommmissionInBackground(final String percentage){
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.CAL_SALESMAN_COMMISSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID,mUserId);
                params.put("salesmanId",mSalesmanId);
                params.put("fromDateId",mFromDateId);
                params.put("toDateId",mToDateId);
                params.put("percentage",percentage);
                return params;
            }
        };

        mRequestQueue.add(request);
    }
}
