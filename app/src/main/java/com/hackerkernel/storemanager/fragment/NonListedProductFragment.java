package com.hackerkernel.storemanager.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.GetSalesman;
import com.hackerkernel.storemanager.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NonListedProductFragment extends Fragment implements View.OnClickListener {
    //Views
    @Bind(R.id.layout) RelativeLayout mLayout;
    @Bind(R.id.salesmanSpinner) Spinner mSalesmanSpinner;
    @Bind(R.id.linearLayout) LinearLayout mProductInfoContainerLayout;
    @Bind(R.id.addMore) Button mAddMore;
    @Bind(R.id.delete) Button mDelete;
    @Bind(R.id.productInfo) View mProductInfo;
    @Bind(R.id.customerName) EditText mCustomerName;
    @Bind(R.id.productName) EditText mProductNameView;
    @Bind(R.id.productSize) EditText mProductSizeView;
    @Bind(R.id.productQuantity) EditText mProductQuanityView;
    @Bind(R.id.productCostPrice) EditText mProductCostPriceView;
    @Bind(R.id.productSellingPrice) EditText mProductSellingPriceView;
    @Bind(R.id.done) Button mDone;

    //Member variables
    private String mUserId;
    private RequestQueue mRequestQueue;
    private String TAG = "NonListedProductFragment";
    private List<SimpleListPojo> mSalesmanList;
    private Database db;
    private ProgressDialog pd;
    private String mSalesmanId = null;
    private String mSalesmanName = null;


    private int mProductInfoCounter = 0;
    private List<View> mProductInfoList = new ArrayList<>();
    private List<EditText> mProductNameList = new ArrayList<>();
    private List<EditText> mProductSizeList = new ArrayList<>();
    private List<EditText> mProductQuantityList = new ArrayList<>();
    private List<EditText> mProductCostPriceList = new ArrayList<>();
    private List<EditText> mProductSellingPriceList = new ArrayList<>();


    public NonListedProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instanciate UserId
        mUserId = MySharedPreferences.getInstance(getActivity()).getUserId();
        //volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        //database
        db = new Database(getActivity());

        //Create a progressDialog
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_non_listed_product, container, false);

        ButterKnife.bind(this, view); //Bind views

        //add product info view to the list
        mProductInfoList.add(mProductInfo); // All the Views of product INfo
        mProductNameList.add(mProductNameView);
        mProductSizeList.add(mProductSizeView);
        mProductQuantityList.add(mProductQuanityView);
        mProductCostPriceList.add(mProductCostPriceView);
        mProductSellingPriceList.add(mProductSellingPriceView);
        mProductInfoCounter++; //increment the counter

        //SETUP Salesman Spinner
        final GetSalesman getSalesman = new GetSalesman(getActivity(),mLayout,mSalesmanSpinner);
        getSalesman.setupSalesmanSpinner();

        //When Salesman is selected From Spinner
        mSalesmanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Get the current salesman selected
                SimpleListPojo salesman = getSalesman.getSalesman(position);

                mSalesmanId = salesman.getId();
                mSalesmanName = salesman.getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set ClickListner to views
        mAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoreFields(container);
            }
        });
        mDelete.setOnClickListener(this);
        mDone.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //add more button is clicked
            case R.id.delete:
                    deleteField();
                break;
            //when done button is pressed add sales to API
            case R.id.done:
                    addSalesToAPI();
                break;
        }
    }

    /******************** ADD MORE BUTTON PRESSED *****************/
    private void addMoreFields(ViewGroup container){
        //Inflate the ProductInfo field layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.include_non_listed_product_info, container, false);

        //Find view From the layout which is inflated
        EditText productName = (EditText) view.findViewById(R.id.productName);
        EditText productSize = (EditText) view.findViewById(R.id.productSize);
        EditText productQuantity = (EditText) view.findViewById(R.id.productQuantity);
        EditText productCostPrice = (EditText) view.findViewById(R.id.productCostPrice);
        EditText productSellingPrice = (EditText) view.findViewById(R.id.productSellingPrice);

        //add view to the list
        mProductInfoList.add(view);
        mProductNameList.add(productName);
        mProductSizeList.add(productSize);
        mProductQuantityList.add(productQuantity);
        mProductCostPriceList.add(productCostPrice);
        mProductSellingPriceList.add(productSellingPrice);
        mProductInfoCounter++; //increment the counter

        //Append the views to layout
        mProductInfoContainerLayout.addView(view);
    }

    /************************ DELETE BUTTON is pressed *************************/
    public void deleteField(){
        int index = mProductInfoCounter - 1;
        if (index > 0){ //means its not the last View
            View view = mProductInfoList.get(index);

            //remove from the list
            mProductInfoList.remove(index);
            mProductNameList.remove(index);
            mProductSizeList.remove(index);
            mProductQuantityList.remove(index);
            mProductCostPriceList.remove(index);
            mProductSellingPriceList.remove(index);

            //remove view from parent
            mProductInfoContainerLayout.removeView(view);

            mProductInfoCounter--; //decrement the counter

        }else{
            Toast.makeText(getActivity(), R.string.atleast_have_1_product_info,Toast.LENGTH_LONG).show();
        }
    }

    /************************ DONE BUTTON IS PRESSED *************************/
    private void addSalesToAPI() {
        //check internet connection
        if(Util.isConnectedToInternet(getActivity())){
            //perform check

            //get the Customer name
            String customerName = mCustomerName.getText().toString().trim();

            //StringBuilder to Store Product info fields stack
            StringBuilder nameStack = new StringBuilder();
            StringBuilder sizeStack = new StringBuilder();
            StringBuilder quantityStack = new StringBuilder();
            StringBuilder costpriceStack = new StringBuilder();
            StringBuilder sellingpriceStack = new StringBuilder();

            //Loop throw the list and get the text which are stored in the views
            for (int i = 0; i < mProductInfoCounter; i++) {
                //store Product Info details in varaible
                String name = mProductNameList.get(i).getText().toString().trim();
                String size = mProductSizeList.get(i).getText().toString().trim();
                String quantity = mProductQuantityList.get(i).getText().toString().trim();
                String costprice = mProductCostPriceList.get(i).getText().toString().trim();
                String sellingprice = mProductSellingPriceList.get(i).getText().toString().trim();

                //check fields are not empty
                if(name.isEmpty() || size.isEmpty() || quantity.isEmpty() || costprice.isEmpty() || sellingprice.isEmpty()){
                    Util.redSnackbar(getActivity(),mLayout,getString(R.string.fillin_all_fields));
                    Log.d(TAG,"HUS: HELLO empty field no "+ i);
                    return; //to end the loop & method
                }else{
                    //add text to stack
                    nameStack.append(name);
                    sizeStack.append(size);
                    quantityStack.append(quantity);
                    costpriceStack.append(costprice);
                    sellingpriceStack.append(sellingprice);

                    //check it last element
                    if(i < mProductInfoCounter-1){
                        nameStack.append(",");
                        sizeStack.append(",");
                        quantityStack.append(",");
                        costpriceStack.append(",");
                        sellingpriceStack.append(",");
                    }
                }
            } //FOR

            addSalesInBackground(customerName, nameStack.toString(), sizeStack.toString(), quantityStack.toString(), costpriceStack.toString(), sellingpriceStack.toString(), mSalesmanId, mSalesmanName);
        }else{
            //show no internet message
            Util.noInternetSnackbar(getActivity(), mLayout);
        }
    }

    /*
    *
    * Method to stores sales information in API
    * */
    private void addSalesInBackground(final String customerName, final String name, final String size, final String quantity, final String costprice, final String sellingprice, final String salesmanId, final String salesmanName) {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.ADD_SALES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d(TAG,"HUS: respionse "+response);
                parseAddSalesResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //handle volley error
                Log.d(TAG,"HUS: addSalesInBackground "+error.getMessage());
                String errorString = VolleySingleton.handleVolleyError(error);
                if (errorString != null){
                    Util.redSnackbar(getActivity(),mLayout,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Keys.KEY_COM_USERID,mUserId);
                param.put(Keys.PRAM_NON_LISTED_CUSTOMER_NAME,customerName);
                param.put(Keys.PRAM_NON_LISTED_NAME,name);
                param.put(Keys.PRAM_NON_LISTED_SIZE,size);
                param.put(Keys.PRAM_NON_LISTED_QUANTITY,quantity);
                param.put(Keys.PRAM_NON_LISTED_COSTPRICE,costprice);
                param.put(Keys.PRAM_NON_LISTED_SELLINGPRICE,sellingprice);
                param.put(Keys.PRAM_NON_LISTED_SALESMAN_ID,salesmanId);
                param.put(Keys.PRAM_NON_LISTED_SALESMAN_NAME,salesmanName);
                return param;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseAddSalesResponse(String response) {
        List<SimplePojo> list = JsonParser.simpleParser(response);
        if (list != null){
            SimplePojo current = list.get(0);
            if (current.getReturned()){ //true means succes
                Toast.makeText(getActivity(), current.getMessage(), Toast.LENGTH_LONG).show();

                //clear data From the fields
                clearFieldsData();
            }else{ //false error
                Util.redSnackbar(getActivity(),mLayout,current.getMessage());
            }
        }else{
            Toast.makeText(getActivity(), R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to clear old data from the fields
    * */
    private void clearFieldsData() {
        mCustomerName.setFocusable(true);
        mCustomerName.setText("");

        //clear all the product info for fields
        for (int i = 0; i < mProductInfoCounter; i++) {
            mProductNameList.get(i).setText("");
            mProductSizeList.get(i).setText("");
            mProductQuantityList.get(i).setText("");
            mProductCostPriceList.get(i).setText("");
            mProductSellingPriceList.get(i).setText("");
        }
    }
}
