package com.hackerkernel.storemanager.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
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
    @Bind(R.id.productName) EditText mProductNameView;
    @Bind(R.id.productSize) EditText mProductSizeView;
    @Bind(R.id.productQuantity) EditText mProductQuanityView;
    @Bind(R.id.productCostPrice) EditText mProductCostPriceView;
    @Bind(R.id.productSellingPrice) EditText mProductSellingPriceView;

    //Member variables
    private String mUserId;
    private RequestQueue mRequestQueue;
    private String TAG = "NonListedProductFragment";
    private List<SimpleListPojo> mSalesmanList;
    private Database db;

    private int mProductInfoCounter = 0;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_non_listed_product, container, false);

        ButterKnife.bind(this, view); //Bind views

        //add product info view to the list
        mProductNameList.add(mProductNameView);
        mProductSizeList.add(mProductSizeView);
        mProductQuantityList.add(mProductQuanityView);
        mProductCostPriceList.add(mProductCostPriceView);
        mProductSellingPriceList.add(mProductSellingPriceView);
        mProductInfoCounter++; //increment the counter

        //setup salesman spinner
        setupSalesmanSpinner();

        //Set ClickListner to views
        mAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoreFields(container);
            }
        });
        mDelete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //add more button is clicked
            case R.id.delete:
                String message =  "count "+mProductInfoCounter+"/ name "+mProductCostPriceList.size()+"/" +
                        " size "+mProductSizeList.size()+"/ quan "+mProductQuantityList.size()+"/ " +
                        "cp "+mProductCostPriceList.size()+" / sp "+mProductSellingPriceList.size();
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                break;
        }
    }

    /********************** SALESMAN SPINNER ***************/

    /*
    * METHOD TO CHECK INTERNET
    * IF AVAILABLE FETCH SALESMAN FROM API AND DISPLAY IN A SPINNER
    * IF NOT GET DATA FROM SQLITE
    * */
    private void setupSalesmanSpinner(){
        //check internet
        if(Util.isConnectedToInternet(getActivity())){
            //fetch list from api
            fetchSalesmanInBackground();
        }else{
            //display spinner from sqlite
            mSalesmanList = db.getAllSimpleList(Database.SALESMAN,mUserId);
            setupSalesmanSpinnerFromList(mSalesmanList);

            //display no internet Toast
            Toast.makeText(getActivity(),R.string.please_check_your_internt,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * METHOD TO FETCH SALESMAN LIST FROM THE API &
    * STORE THEM IN THE SQLITE DATABASE
    * */
    private void fetchSalesmanInBackground(){
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_SALESMAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                //Handle Volley error
                Log.e(TAG,"ERROR "+error.getMessage());
                String errrorString = VolleySingleton.handleVolleyError(error);
                if (errrorString != null){
                    Util.redSnackbar(getActivity(),mLayout,errrorString);
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

    private List<SimpleListPojo> parseSalesmanResponse(String response) {
        List<SimpleListPojo> list = JsonParser.simpleListParser(response);

        if(list != null){
            //If json Response Return false Display message in SnackBar
            if(!list.get(0).isReturned()){
                Util.redSnackbar(getActivity(),mLayout,list.get(0).getMessage());
                return null;
            }else if(list.get(0).getCount() == 0){ //Count (Number of saleman returned)
                /*
                * If count return 0 means no salesman added
                * */
                Toast.makeText(getActivity(),"No salesman added yet",Toast.LENGTH_LONG).show();
                return null;
            }else{ //if we get true results
                /*
                * If result found
                * */
                return list;
            }
        }else{ // when return null
            Toast.makeText(getActivity(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,stringList);
        mSalesmanSpinner.setAdapter(adapter);
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
        mProductNameList.add(productName);
        mProductSizeList.add(productSize);
        mProductQuantityList.add(productQuantity);
        mProductCostPriceList.add(productCostPrice);
        mProductSellingPriceList.add(productSellingPrice);
        mProductInfoCounter++; //increment the counter

        //Append the views to layout
        mProductInfoContainerLayout.addView(view);
    }
}
