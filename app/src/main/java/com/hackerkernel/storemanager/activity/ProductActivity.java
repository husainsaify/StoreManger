package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.hackerkernel.storemanager.pojo.ProductPojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductActivity extends AppCompatActivity {
    //Global variable
    private static final String TAG = ProductActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.layout) CoordinatorLayout mLayout;
    @Bind(R.id.swipeRefresh) SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.productRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.emptyRecyclerView) TextView mEmptyRecyclerView;

    private String mCategoryId;
    private String mCategoryName;
    private String mUserId;
    private Database db;
    private RequestQueue mRequestQueue;
    //list
    List<ProductPojo> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);

        //get the categoryId , categoryName & userId
        if(getIntent().hasExtra("categoryId") && getIntent().hasExtra("categoryName")){
            mCategoryId = getIntent().getExtras().getString("categoryId");
            mCategoryName = getIntent().getExtras().getString("categoryName");
        }else{
            Toast.makeText(getApplication(),"Internal error. Restart app",Toast.LENGTH_LONG).show();
            return;
        }

        //set the Toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mCategoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(getApplication(),"ID "+mCategoryId,Toast.LENGTH_LONG).show();

        //Get Loggedin userId
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();

        //Set Up Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Set LayoutManger for recyclerView
        LinearLayoutManager manger = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(manger);

        //Instantiate Database
        db = new Database(this);

        /*//When user click on of the item from the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get item where user has clicked
                ProductPojo product = (ProductPojo) listView.getItemAtPosition(position);
                //Send to ViewProductActivity
                Intent intent = new Intent(ProductActivity.this,ViewProductActivity.class);
                intent.putExtra("pName",product.getProductName());
                intent.putExtra("pId", product.getProductId());
                intent.putExtra("pImageAddress",product.getProductImage());
                startActivity(intent);
            }
        });*/

        getProductListInBackground();
    }

    /*
    * Method To Get Product list from API
    * */
    public void getProductListInBackground(){
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.PRODUCT_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Parse response send by the server
                List<ProductPojo> list  = parseProductListResponse(response);
                if(list != null){
                    setupRecyclerView(list);

                    //Store in PRODUCT LIST Table
                    db.deleteProductList(mUserId,mCategoryId);
                    db.insertProductList(list);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle Volley error
                Log.d(TAG,"HUS: error "+error.getMessage());
                String errorMessage = VolleySingleton.handleVolleyError(error);
                if(errorMessage != null){
                    Util.redSnackbar(getApplication(), mLayout, errorMessage);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Keys.KEY_COM_USERID,mUserId);
                param.put(Keys.PRAM_PL_CATEGORYID,mCategoryId);
                return param;
            }
        };

        mRequestQueue.add(request);
    }

    public List<ProductPojo> parseProductListResponse(String response){
        List<ProductPojo> list = JsonParser.productListParser(response);
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
                mRecyclerView.setVisibility(View.GONE);
                mEmptyRecyclerView.setVisibility(View.VISIBLE);
                return null;
            }else{
                Log.d(TAG,"HUS: item found");
                /*
                * If result found
                * Make recyclerview visible and TextView invisible
                * */
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyRecyclerView.setVisibility(View.GONE);
                return list;
            }
        }else{
            Toast.makeText(getApplication(), R.string.unable_to_parse_response, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /*
    * Method to take a ProductPojo list and set RecyclerView
    * */
    private void setupRecyclerView(List<ProductPojo> list){
        ProductListAdapter adapter = new ProductListAdapter(getApplication());
        adapter.setList(list);
        mRecyclerView.setAdapter(adapter);
    }

    //Open addProduct Activity when FAB is clicked
    public void openAddProduct(View view){
        Intent addProductIntent = new Intent(getApplication(),AddProductActivity.class);
        addProductIntent.putExtra("categoryId",mCategoryId);
        addProductIntent.putExtra("categoryName",mCategoryName);
        startActivity(addProductIntent);
    }

    /*

    //This method will Adapte a List into a ListView
    private void updateListView(List<ProductPojo> list){
        if(list != null){
            ProductAdapter productAdapter = new ProductAdapter(ProductActivity.this,R.layout.product_list_layout,list);
            listView.setAdapter(productAdapter);
        }else{
            whenListIsEmpty.setText("No Product Listed");
        }
    }*/

    /*//Class to fetch json data from the Backend
    private class productTask extends AsyncTask<String,String,List<ProductPojo>>{
        @Override
        protected void onPreExecute() {
            //show PB
            Functions.toggleProgressBar(pb);
        }

        @Override
        protected List<ProductPojo> doInBackground(String... params) {
            *//*
            * Fetch the Product list from the web
            * *//*
            //generate a HashMap as fro sending POST params to Backend
            HashMap<String,String> productHash = new HashMap<>();
            productHash.put("userId",userId);
            productHash.put("categoryId",categoryId);

            //convert a hashmap into Encoded Url so that we can send it to Backend
            String dataUrl = Functions.hashMapToEncodedUrl(productHash);

            //request the Backend
            String jsonString = GetJson.request(ApiUrl.PRODUCT_LIST,dataUrl,"POST");

            //parse Json and store it in "productList"
            productList = JsonParser.productListParser(jsonString);


            return productList;
        }

        @Override
        protected void onPostExecute(List<ProductPojo> list) {
            //set "ProductList" to "ListView"
            updateListView(list);
            //Hide PB
            Functions.toggleProgressBar(pb);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            //refreshList(); //refresh view
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
