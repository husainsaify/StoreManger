package com.hackerkernel.storemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hackerkernel.storemanager.pojo.SalesTrackerDatePojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SaleTrackerActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.dateDropDown) Spinner mDateDropDown;
    @Bind(R.id.salesList) ListView mSalesListView;
    @Bind(R.id.profitOrLossLabel) TextView mProfitOrLossLabel;
    @Bind(R.id.profitOrLoss) TextView mProfitOrLossValue;
    @Bind(R.id.costPrice) TextView mCostPriceValue;
    @Bind(R.id.sellingPrice) TextView mSellingPriceValue;


    private List<SalesTrackerDatePojo> mDropdownList;
    private String mUserId,
            mDate,
            mDateId,
            mFailedMessage;
    private List<SalesTrackerPojo> mSalesList;
    private int mTotalCP,
                mTotalSales;

    private final String TAG = SaleTrackerActivity.class.getSimpleName();
    private final Context context = SaleTrackerActivity.this;

    ProgressDialog pd;
    AlertDialog.Builder dialog; //a alert dialog to display product details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_tracker);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.sale_tracker));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /*//get user_id
        Database db = new Database(this);
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
                *//*
                * Call AsyncTask and set mSalesListView
                * *//*
                new GetSalesTask().execute(mDateId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplication(), "Please select a date", Toast.LENGTH_SHORT).show();
            }
        });


        //when Sales Tracker listView is clicked
        mSalesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SalesTrackerPojo current = mSalesList.get(position);
                setAlertDialog(current);
            }
        });*/
    }

    /*public void setAlertDialog(final SalesTrackerPojo c){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.sales_tracker_alert_dialog,null);
        //find view and from the layout
        TextView name = (TextView) view.findViewById(R.id.pName),
                 code = (TextView) view.findViewById(R.id.pCode),
                 quantity = (TextView) view.findViewById(R.id.pQuantity),                 sp = (TextView) view.findViewById(R.id.pSp),
                 cp = (TextView) view.findViewById(R.id.pCp),
                 totalSales = (TextView) view.findViewById(R.id.pTotalSales),
                 totalCp = (TextView) view.findViewById(R.id.pTotalCP),
                 profitOrLossLabel = (TextView) view.findViewById(R.id.profitOrLossLabel),
                 profitOrLoss = (TextView) view.findViewById(R.id.profitOrLoss);

        //set values to the views
        name.setText(c.getProductName());
        code.setText(c.getProductCode());
        quantity.setText(c.getQuantity());
        sp.setText(c.getPrice_per());
        cp.setText(c.getProductCp());
        totalSales.setText(c.getCurrentSales());
        totalCp.setText(c.getCurrentCp());

        //cal profit or loss
        int currentSale = Integer.parseInt(c.getCurrentSales());
        int currentCp = Integer.parseInt(c.getCurrentCp());
        int value;
        if(currentCp > currentSale){//loss
            //cal loss
            value = currentCp - currentSale;
            profitOrLossLabel.setText(getString(R.string.loss));
        }else {
            if (currentSale > currentCp) { //profit
                //call profit
                value = currentSale - currentCp;
                profitOrLossLabel.setText(getString(R.string.profit));
            } else { // neutral CP = sales
                value = 0;
                profitOrLossLabel.setText(getString(R.string.break_even));
            }
        }

        profitOrLoss.setText(value + "");

        dialog = new AlertDialog.Builder(SaleTrackerActivity.this)
                .setView(view)
                .setPositiveButton(getString(R.string.view_product), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send to ProductActivity
                        sendToViewProductActivity(c);
                    }
                })
                .setNegativeButton(getString(R.string.ok),null);
        AlertDialog builder = dialog.show();
    }

    public void sendToViewProductActivity(SalesTrackerPojo c){
        //send user to ProductActivity
        Intent intent = new Intent(SaleTrackerActivity.this,ProductActivity.class);
        intent.putExtra("pName", c.getProductName());
        intent.putExtra("pId", c.getProductId());
        intent.putExtra("pImageAddress",c.getProductImageAddress());
        startActivity(intent);
    }

    //method to update total sales cp & profit & loss
    public void updateTotalStats(){
        String label;
        int value;
        if(mTotalCP > mTotalSales){//loss
            //cal loss
            value = mTotalCP - mTotalSales;
            label = "Loss";
        }else {
            if (mTotalSales > mTotalCP) { //profit
                //call profit
                value = mTotalSales - mTotalCP;
                label = "Profit";
            } else { // neutral CP = sales
                value = 0;
                label = "Break Even";
            }
        }

        //set Label and value to TextView
        mProfitOrLossLabel.setText(label);
        mProfitOrLossValue.setText(String.valueOf(value));
        mCostPriceValue.setText(String.valueOf(mTotalCP));
        mSellingPriceValue.setText(String.valueOf(mTotalSales));
    }

    *//*
    * Class to fetch date for mDateDropDown Menu
    * *//*
    private class DateDropDownTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", mUserId);

            //build encoded url
            String data = Functions.hashMapToEncodedUrl(hashMap);

            //request the web
            String jsonString = GetJson.request(ApiUrl.SALES_TRACKER_DATE_LIST, data, "POST");
            //parse json
            mDropdownList = JsonParser.salesTrackerDateParser(jsonString);
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
            *//*
            * Call AsyncTask and set mSalesListView
            * *//*
            new GetSalesTask().execute(mDateId);
        }
    }


    *//*
    * Class to fetch all the sales of the current date
    * *//*
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

            String jsonString = GetJson.request(ApiUrl.GET_SALES_TRACKER, data, "POST");
            Log.d(TAG, "HUS: Raw json "+jsonString);

            //parse json for TotalCP & TotalSp & build the salesTrackerList
            try {
                JSONObject jo = new JSONObject(jsonString);
                if(jo.getBoolean("return")){
                    mTotalSales = Integer.parseInt(jo.getString("total_sales"));
                    mTotalCP = Integer.parseInt(jo.getString("total_cp"));
                    //parse json for SalesTrackerList
                    mSalesList = JsonParser.salesTrackerParser(SaleTrackerActivity.this,jsonString);
                    return mSalesList;
                }else{
                    *//*
                    * store the error message in a Global variable
                    * So that we can show error message from MainThread
                    * *//*
                    mFailedMessage = jo.getString("message");
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG,"HUS: Failed to parse json "+e);
                mFailedMessage = "Failed to parse json";
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<SalesTrackerPojo> list) {
            if(list != null){
                SalesTrackerAdapter adapter = new SalesTrackerAdapter(getApplication(),R.layout.sales_tracker_list_layout,list);
                mSalesListView.setAdapter(adapter);
                //call updateSalesState to calculate and set total Sales Total CP & p or l
                updateTotalStats();
            }else{
                Toast.makeText(getApplication(),mFailedMessage,Toast.LENGTH_LONG).show();
            }
            pd.dismiss();
        }
    }*/

}
