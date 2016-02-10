package com.hackerkernel.storemanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    //Global variable
    private static final String TAG = ProductListActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.layout) CoordinatorLayout mLayout;
    @Bind(R.id.swipeRefresh) SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.productRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.emptyRecyclerView) TextView mEmptyRecyclerView;
    @Bind(R.id.toolbarProgressBar) ProgressBar mToolbarProgressBar;

    private String mCategoryId;
    private String mCategoryName;
    private String mUserId;
    private Database db;
    private RequestQueue mRequestQueue;

    //Edit Category name Dialog
    private EditText mEditCategoryNameEditText;
    private AlertDialog mEditDialog;

    //delete category
    private AlertDialog mDeleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ButterKnife.bind(this);

        //get the categoryId & categoryName
        if(getIntent().hasExtra(Keys.KEY_COM_CATEGORYID) && getIntent().hasExtra(Keys.KEY_COM_CATEGORYNAME)){
            mCategoryId = getIntent().getExtras().getString(Keys.KEY_COM_CATEGORYID);
            mCategoryName = getIntent().getExtras().getString(Keys.KEY_COM_CATEGORYNAME);
        }else{
            Toast.makeText(getApplication(), R.string.internal_error_restart_app,Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //set the Toolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mCategoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Loggedin userId
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();

        //Set Up Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Set LayoutManger for recyclerView
        LinearLayoutManager manger = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(manger);

        //Instantiate Database
        db = new Database(this);

        //Instanciate edit Category name EditText
        mEditCategoryNameEditText = (EditText) LayoutInflater.from(this).inflate(R.layout.edit_text_style_text,null);
        mEditCategoryNameEditText.setText(mCategoryName);
        mEditCategoryNameEditText.setSelection(mEditCategoryNameEditText.getText().length()); //Set curose to the last alphabet of the editText

        //Create Edit Category name Dialog
        createEditCategoryDialog();
        //create Delete category dialog
        createDeleteCategoryDialog();

        //Instantiate SwipeToRefreshLayout
        mSwipeRefresh.setOnRefreshListener(this);
        checkInternetAndDisplayList();

    }

    /********** MENU *****/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //when back button is clicked
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_edit_category_name:
                showEditCategoryDialog();
                break;
            case R.id.action_delete_category:
                showDeleteCategoryDialog();
                break;
        }
        return true;
    }


    /*
    * Check user has a Internet connected
    * if yes Fetch fresh product list from api and store it in sqlite database
    * if no Go to Sqlitedatabase and get the product list
    * if no data in SqliteDatabase show a message
    * */
    private void checkInternetAndDisplayList() {
        if(Util.isConnectedToInternet(getApplication())){ //connected
            fetchProductListInBackground(); //fetch data
        }else{ //not connected
            showListFromSqliteDatabase(); //method to display Data in list from Sqlite database
            Util.noInternetSnackbar(getApplication(), mLayout);
            //method to stop swipeRefreshlayout refresh icon
            stopRefreshing();
        }
    }

    /*
    * Method To Get Product list from API
    * */
    public void fetchProductListInBackground(){
        startRefreshing(); //show refresh
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.PRODUCT_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopRefreshing(); //Stop refresh
                //Parse response send by the server
                List<ProductListPojo> list  = parseProductListResponse(response);
                if(list != null){
                    setupRecyclerView(list);

                    //Store in PRODUCT LIST Table
                    db.deleteProductList(mUserId,mCategoryId);
                    db.insertProductList(list);
                }else{
                    //Show list from Local sqlite database
                    showListFromSqliteDatabase();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopRefreshing(); //Stop refresh
                //handle Volley error
                Log.d(TAG,"HUS: error "+error.getMessage());
                String errorMessage = VolleySingleton.handleVolleyError(error);
                if(errorMessage != null){
                    Util.redSnackbar(getApplication(), mLayout, errorMessage);
                }

                //Show list from Sqlite Database
                showListFromSqliteDatabase();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Keys.KEY_COM_USERID,mUserId);
                param.put(Keys.KEY_COM_CATEGORYID,mCategoryId);
                return param;
            }
        };

        mRequestQueue.add(request);
    }

    private void showListFromSqliteDatabase() {
        //Display List From SQlite database
        List<ProductListPojo> list = db.getProductList(mUserId, mCategoryId);
        if(list != null){
            setupRecyclerView(list);
        }
    }

    public List<ProductListPojo> parseProductListResponse(String response){
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
    * Method to take a ProductListPojo list and set RecyclerView
    * */
    private void setupRecyclerView(List<ProductListPojo> list){
            ProductListAdapter adapter = new ProductListAdapter(getApplication());
            adapter.setList(list);
            mRecyclerView.setAdapter(adapter);
    }

    //Open insertProduct Activity when FAB is clicked
    public void openAddProduct(View view){
        Intent addProductIntent = new Intent(getApplication(),AddProductActivity.class);
        addProductIntent.putExtra("categoryId",mCategoryId);
        addProductIntent.putExtra("categoryName",mCategoryName);
        startActivity(addProductIntent);
    }

    /*
    * Swipe to refresh
    * */

    @Override
    public void onRefresh() {
        checkInternetAndDisplayList();
    }

    //method to stop swipeRefreshlayout refresh icon
    private void stopRefreshing() {
        if(mSwipeRefresh.isRefreshing()){
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void startRefreshing(){
        if(!mSwipeRefresh.isRefreshing()){
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                }
            });
        }
    }

    /******************** Edit Category name *****************/
    private void createEditCategoryDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_category_name)
                .setView(mEditCategoryNameEditText)
                .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Validate new Category name
                        validateCategoryName();
                    }
                })
                .setNegativeButton(R.string.cancel,null);
        mEditDialog = builder.create();
    }

    /*
    * Check internet connection and show Edit Category name mEditDialog
    * */
    private void showEditCategoryDialog(){
        if(Util.isConnectedToInternet(getApplication())){
            mEditDialog.show();
        }else {
            Toast.makeText(getApplicationContext(),R.string.please_check_your_internt,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to validate new Category name
    * */
    private void validateCategoryName(){
        //get new category name
        String newCategoryName = mEditCategoryNameEditText.getText().toString().trim();

        //check new category is does not match old category name
        if (newCategoryName.equals(mCategoryName)){
            Util.redSnackbar(getApplication(), mLayout, "Enter a new name for category");
        }
        else if(newCategoryName.length() <= 3){ //categoryname should not be to short
            Util.redSnackbar(getApplication(),mLayout,"Category name should be more then 3 characters");
        }
        else if(newCategoryName.length() > 20){
            Util.redSnackbar(getApplication(),mLayout,"Category name should not be more then 20 characters");
        }else{
            //Call method to Update category name on API
            editCategoryNameInBackground(newCategoryName);
        }
    }

    /*
    * Method to connect with API and edit Category name is Background
    * */
    private void editCategoryNameInBackground(final String newCategoryName) {
        Util.setProgressBarVisible(mToolbarProgressBar,true); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.EDIT_CATEGORY_NAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.setProgressBarVisible(mToolbarProgressBar, false); //hide progressbar
                //parse response & check response was success or not
                if(parseEditCategoryResponse(response)){
                    //set new category name to the title
                    assert getSupportActionBar() != null;
                    getSupportActionBar().setTitle(newCategoryName);
                    //set new category name to Edit category mEditDialog
                    mEditCategoryNameEditText.setText("");
                    mEditCategoryNameEditText.append(newCategoryName);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.setProgressBarVisible(mToolbarProgressBar, false); //hide progressbar
                Log.e(TAG, "HUS: editCategoryNameInBackground " + error.getMessage());
                //handle error
                String stringError = VolleySingleton.handleVolleyError(error);
                if(stringError != null){
                    Util.redSnackbar(getApplicationContext(),mLayout,stringError);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_CATEGORYID,mCategoryId);
                params.put(Keys.KEY_COM_CATEGORYNAME,newCategoryName);
                params.put(Keys.KEY_COM_USERID,mUserId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    private boolean parseEditCategoryResponse(String json) {
        boolean response = false;
        List<SimplePojo> list = JsonParser.simpleParser(json);
        if(list != null){
            SimplePojo current = list.get(0);
            //check return is true or false
            if (current.getReturned()){ //success
                Util.greenSnackbar(getApplicationContext(),mLayout,current.getMessage());
                //set response to true because operation was succesfull
                response = true;
            }else { //false
                Util.redSnackbar(getApplicationContext(), mLayout, current.getMessage());
            }
        }else{
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }

        return response;
    }

    /************************ DELETE CATEGORY *************************/
    private void createDeleteCategoryDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete)
                .setMessage("Are you sure? you want to delete this category. Deleting this category will automatically delete all its product")
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete method to delete Category from api
                        deleteCategoryNameInBackground();
                    }
                })
                .setNegativeButton(R.string.cancel,null);
        mDeleteDialog = builder.create();
    }

    /*
    * Check internet connection and show delete Category
    * */
    private void showDeleteCategoryDialog(){
        if(Util.isConnectedToInternet(getApplication())){
            mDeleteDialog.show();
        }else {
            Toast.makeText(getApplicationContext(),R.string.please_check_your_internt,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * METHOD TO CONNECT TO API AND DELETE PRODUCT
    * */
    private void deleteCategoryNameInBackground() {
        Util.setProgressBarVisible(mToolbarProgressBar,true); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.DELETE_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.setProgressBarVisible(mToolbarProgressBar, false); //hide progressbar
                //parse response & check response was success or not
                parseDeleteCategoryResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.setProgressBarVisible(mToolbarProgressBar, false); //hide progressbar
                Log.e(TAG, "HUS: editCategoryNameInBackground " + error.getMessage());
                //handle error
                String stringError = VolleySingleton.handleVolleyError(error);
                if(stringError != null){
                    Util.redSnackbar(getApplicationContext(),mLayout,stringError);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_CATEGORYID,mCategoryId);
                params.put(Keys.KEY_COM_USERID,mUserId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse Response from Delete Product in background
    * */
    private void parseDeleteCategoryResponse(String json) {
        List<SimplePojo> list = JsonParser.simpleParser(json);
        if(list != null){
            SimplePojo pojo = list.get(0);
            if (pojo.getReturned()){ //success
                //Close activity
                NavUtils.navigateUpFromSameTask(ProductListActivity.this);
            }else{ //failed
                Util.redSnackbar(getApplicationContext(),mLayout,pojo.getMessage());
            }
        }else{
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }
}
