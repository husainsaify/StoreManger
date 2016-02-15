package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.hackerkernel.storemanager.adapter.ProductListAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.ProductListPojo;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.productName) TextView mProductNameView;
    @Bind(R.id.productSize) TextView mProductSizeView;
    @Bind(R.id.search) Button mSearch;
    @Bind(R.id.categorySpinner) Spinner mCategorySpinner;
    @Bind(R.id.searchRecyclerView) RecyclerView mSearchRecyclerView;
    @Bind(R.id.linearLayout) LinearLayout mLayout;
    @Bind(R.id.emptyRecyclerView) TextView mEmptyRecyclerViewMessage;


    //global variables
    private Database db;
    private String mUserId;
    private List<SimpleListPojo> mCategoryList;
    private ProgressDialog pd;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //instanciate database
        db = new Database(this);

        //get logged in userId
        mUserId = MySharedPreferences.getInstance(getApplicationContext()).getUserId();

        //create progressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        //set Layout manger to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplication());
        mSearchRecyclerView.setLayoutManager(layoutManager);

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //setup Category spinner
        mCategoryList = Util.setupCategorySpinnerFromDb(getBaseContext(),db,mUserId,mCategorySpinner,true);

        if(mCategoryList == null){
            Util.redSnackbar(getApplication(),mLayout,getString(R.string.it_seem_your_havent_added_any_category));
        }

        //when search button is pressed
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate fields
                doSearch();
            }
        });
    }


    /*
    * Method to Valid search Input
    * */
    public void doSearch(){
        //check internet connection
        if(Util.isConnectedToInternet(getApplication())){
            String  productName = mProductNameView.getText().toString().trim(),
                    productSize = mProductSizeView.getText().toString().trim(),
                    categoryId = "";

            //check product Name is not empty
            if (productName.isEmpty()) {
                Util.redSnackbar(getApplication(),mLayout,getString(R.string.product_name_not_empty));
                return;
            }

            //get the position of categorySpinner which is selectecd
            int position = mCategorySpinner.getSelectedItemPosition();
            //some category is selected
            if(position != 0){
                    /*
                    * Subtract 1 from From Category spinner because we have added a placeholder text their
                    * */

                int newPosition = position - 1;
                //fetch CategoryName & CategoryId
                categoryId = mCategoryList.get(newPosition).getId();
            }

            /*
            * Search backend to get product
            * */
            doSearchInBackground(productName, productSize, categoryId);
        }else{
            //Show no internet snackbar
            Util.noInternetSnackbar(getApplication(),mLayout);
        }
    }

    /*
    * Method to perform search IN API
    *
    * */
    private void doSearchInBackground(final String productName, final String productSize, final String categoryId) {
        pd.show(); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.PRODUCT_SEARCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide progressbar
                List<ProductListPojo> list = parseSearchListResponse(response);
                if(list != null){
                    setupRecyclerView(list);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss(); //hide progressbar
                Log.e(TAG,"HUS: doSearchInBackground "+error.getMessage());
                //Handle volley error
                String errorString = VolleySingleton.handleVolleyError(error);
                if(errorString != null){
                    Util.redSnackbar(getApplication(),mLayout,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID,mUserId);
                params.put(Keys.PRAM_SEARCH_NAME,productName);
                params.put(Keys.PRAM_SEARCH_SIZE,productSize);
                params.put(Keys.KEY_COM_CATEGORYID,categoryId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * METHOD TO PARSE RESPONSE SEND BY SEARCH API
    * */
    private List<ProductListPojo> parseSearchListResponse(String response) {
        List<ProductListPojo> list = JsonParser.productListParser(response);
        if(list != null){
            //if response return false
            if(!list.get(0).isReturned()){
                Log.d(TAG,"HUS: parse: false");
                Util.redSnackbar(getApplication(), mLayout, list.get(0).getMessage());
                return null;
            }else if(list.get(0).getCount() == 0){
                Log.d(TAG, "HUS: count zero");
                /*
                * If count return 0 means no product added
                * Hide recyclerView and show TextView
                * */
                mSearchRecyclerView.setVisibility(View.GONE);
                mEmptyRecyclerViewMessage.setVisibility(View.VISIBLE);
                return null;
            }else{
                Log.d(TAG,"HUS: item found");
                /*
                * If result found
                * Make recyclerview visible and TextView invisible
                * */
                mSearchRecyclerView.setVisibility(View.VISIBLE);
                mEmptyRecyclerViewMessage.setVisibility(View.GONE);
                return list;
            }
        }else{
            Log.e(TAG,"HUS: parseSearchListResponse: "+response);
            Toast.makeText(getApplication(), R.string.unable_to_parse_response, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /*
    * Method to take a ProductListPojo list and set RecyclerView
    * */
    private void setupRecyclerView(List<ProductListPojo> list){
        ProductListAdapter adapter = new ProductListAdapter(getApplication());
        adapter.setList(list);
        mSearchRecyclerView.setAdapter(adapter);
    }
}
