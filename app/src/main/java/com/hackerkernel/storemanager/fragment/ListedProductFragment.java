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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.hackerkernel.storemanager.adapter.AutoCompleteProductAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.GetSalesman;
import com.hackerkernel.storemanager.util.Util;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListedProductFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.layout) RelativeLayout mLayout;
    @Bind(R.id.customerName) EditText mCustomerNameView;
    @Bind(R.id.productName) AutoCompleteTextView mProductNameView;
    @Bind(R.id.productSize) EditText mProductSizeView;
    @Bind(R.id.productQuantity) EditText mProductQuantityView;
    @Bind(R.id.productCostPrice) EditText mProductCostPriceView;
    @Bind(R.id.productSellingPrice) EditText mProductSellingPriceView;
    @Bind(R.id.salesmanSpinner) Spinner mSalesmanSpinner;
    @Bind(R.id.done) Button mDone;

    private String mUserId;
    private String mProductName = "";
    private String mProductId = "";
    private String mProductCostPrice = "";
    private String mSalesmanId = null;
    private String mSalesmanName = null;
    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private static final String TAG = ListedProductFragment.class.getSimpleName();

    public ListedProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get userId
        mUserId = MySharedPreferences.getInstance(getActivity()).getUserId();

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        pd = new ProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listed_product, container, false);
        ButterKnife.bind(this, view);

        //SETUP Salesman Spinner
        final GetSalesman getSalesman = new GetSalesman(getActivity(),mLayout,mSalesmanSpinner);
        getSalesman.setupSalesmanSpinner();

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

        final AutoCompleteProductAdapter adapter = new AutoCompleteProductAdapter(getActivity(), mUserId);
        mProductNameView.setAdapter(adapter);

        mProductNameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the name of the item selected from AC
                mProductName = adapter.getItem(position).getName();
                //store productId & cost price
                mProductId = adapter.getItem(position).getId();
                mProductCostPrice = adapter.getItem(position).getCp();

                //set item to dropdown
                mProductNameView.setText(mProductName);

                //set Cost price to its EditText and disable it
                mProductCostPriceView.setText(mProductCostPrice);
                mProductCostPriceView.setEnabled(false);

                //set focus to Size EditText
                mProductSizeView.requestFocus();
            }
        });

        mDone.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //when done is clicked
            case R.id.done:
                addSales();
                break;
        }
    }

    /************************ ADD SALES WHEN DONE BUTTON IS CLICKED **********************/

    /*
    * Method to validate inputs
    * */
    public void addSales(){
        //check internet connection
        if(Util.isConnectedToInternet(getActivity())){
            String customerName = mCustomerNameView.getText().toString().trim();
            String size = mProductSizeView.getText().toString().trim();
            String quantity = mProductQuantityView.getText().toString().trim();
            String sellingprice = mProductSellingPriceView.getText().toString().trim();

            //check all required fields are filled
            if(size.isEmpty() || quantity.isEmpty() || sellingprice.isEmpty()){
                Util.redSnackbar(getActivity(), mLayout, getString(R.string.fillin_all_fields));
                return;
            }

            if (mProductName.isEmpty() || mProductCostPrice.isEmpty() || mProductId.isEmpty()){
                Util.redSnackbar(getActivity(), mLayout, getString(R.string.select_valid_prroduct_from_product_name));
                return;
            }

            addSalesInBackground(customerName,size,quantity,sellingprice);
        }else{
            Util.noInternetSnackbar(getActivity(),mLayout);
        }
    }

    private void addSalesInBackground(final String customerName, final String size, final String quantity, final String sellingprice){
        pd.show();
        final StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.ADD_SALES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d(TAG, "HUS: respionse " + response);

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
                param.put(Keys.KEY_COM_PRODUCTID,mProductId);
                param.put(Keys.PRAM_NON_LISTED_NAME,mProductName);
                param.put(Keys.PRAM_NON_LISTED_SIZE,size);
                param.put(Keys.PRAM_NON_LISTED_QUANTITY,quantity);
                param.put(Keys.PRAM_NON_LISTED_COSTPRICE,mProductCostPrice);
                param.put(Keys.PRAM_NON_LISTED_SELLINGPRICE,sellingprice);
                param.put(Keys.PRAM_NON_LISTED_SALESMAN_ID,mSalesmanId);
                param.put(Keys.PRAM_NON_LISTED_SALESMAN_NAME,mSalesmanName);
                param.put(Keys.PRAM_LISTED_SALES_TYPE,"listed");
                return param;
            }
        };
        mRequestQueue.add(request);
    }
}
