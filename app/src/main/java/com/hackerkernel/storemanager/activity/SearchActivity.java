package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.storemanager.Functions;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.adapter.ProductAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.ProductListPojo;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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


    //global variables
    private Database db;
    private String mUserId;
    private List<SimpleListPojo> mCategoryList;
    private ProgressDialog pd;
    private RequestQueue mRequestQueue;

    private String mFailedMessage;
    List<ProductListPojo> searchList;

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


        //when searchListView is clicked
        /*searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductListPojo current = searchList.get(position);
                //Send to ProductActivity
                Intent intent = new Intent(SearchActivity.this,ProductActivity.class);
                intent.putExtra("pName",current.getProductName());
                intent.putExtra("pId", current.getProductId());
                intent.putExtra("pImageAddress",current.getProductImage());
                startActivity(intent);
            }
        });*/
    }


    /*
    * Method to Valid search Input
    * */
    public void doSearch(){
        //check internet connection
        if(Util.isConnectedToInternet(getApplication())){
            String  productName = mProductNameView.getText().toString().trim(),
                    productSize = mProductSizeView.getText().toString().trim(),
                    categoryId = null;

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
            doSearchInBackground(mUserId, productName, productSize, categoryId);
        }else{
            //Show no internet snackbar
            Util.noInternetSnackbar(getApplication(),mLayout);
        }
    }

    /*
    * Method to perform search IN API
    *
    * */
    private void doSearchInBackground(String mUserId, String productName, String productSize, String categoryId) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.PRODUCT_SEARCH, new Response.Listener<String>() {
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
                return super.getParams();
            }
        };

        mRequestQueue.add(request);
    }


    //Background Task to fetch category from the web
    /*private class CategoryTask extends AsyncTask<Void,Void,List<SimpleListPojo>>{

        @Override
        protected List<SimpleListPojo> doInBackground(Void... params) {
            //create Hashmap for mUserId
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("mUserId", mUserId);

            //convert hashmap into encoded url to send them to database
            String data = Functions.hashMapToEncodedUrl(hashMap);

            //make a request to the backend
            String jsonResponse = GetJson.request(ApiUrl.GET_CATEGORY,data,"POST");

            mCategoryList = JsonParser.simpleListParser(jsonResponse);
            return mCategoryList;
        }

        @Override
        protected void onPostExecute(List<SimpleListPojo> category) {
            //populate category spinner
            List<String> lable = new ArrayList<>();
            lable.add("Select Category");

            for (int i = 0; i < category.size(); i++) {
                lable.add(category.get(i).getName());
            }

           ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this,
                   android.R.layout.simple_spinner_item, lable);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            categorySpinner.setAdapter(adapter);
        }
    }

    //background task for search
    private class SearchTask extends AsyncTask<String,Void,List<ProductListPojo>>{
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected List<ProductListPojo> doInBackground(String... params) {
            HashMap<String,String> hashmap = new HashMap<>();
            hashmap.put("mUserId",params[0]);
            hashmap.put("name",params[1]);
            if(!params[2].isEmpty()){ //size is not empty
                hashmap.put("size",params[2]);
            }
            if(params[3]!= null){//category id is not empty or null
                hashmap.put("categoryId",params[3]);
            }

            //convert hashMap into a encoded string
            String data = Functions.hashMapToEncodedUrl(hashmap);
            String  jsonString = GetJson.request(ApiUrl.PRODUCT_SEARCH,data,"POST");
            //parse Json and check we have some result or not
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                //check return is true or false
                if(jsonObject.getBoolean("return")){ //return is true

                    //parse json data and store it in searchList
                    searchList = JsonParser.productListParser(jsonString);
                    return searchList;

                }else{ //return is false
                    //store error message in mFailedMessage
                    mFailedMessage = jsonObject.getString("message");
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mFailedMessage = "Failed to parse json";
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ProductListPojo> list) {
            if(list != null){ //adapt the arrayList and show in a listView
                ProductAdapter productAdapter = new ProductAdapter(SearchActivity.this,R.layout.product_list_layout,list);
                searchListView.setAdapter(productAdapter);
            }else{ // show failed message
                Toast.makeText(getApplication(),mFailedMessage,Toast.LENGTH_LONG).show();
                searchListView.setAdapter(null);
            }
            pd.dismiss();
        }
    }*/
}
