package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.hackerkernel.storemanager.SearchActivity;
import com.hackerkernel.storemanager.adapter.SimpleListAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CategoryActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = CategoryActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigationView) NavigationView mNavigationView;
    @Bind(R.id.fabAddCategory) FloatingActionButton mFab;
    @Bind(R.id.clayout) CoordinatorLayout mLayout;
    @Bind(R.id.emptyRecyclerView) TextView mEmptyRecyclerView;
    @Bind(R.id.swipeRefresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.categoryRecyclerView) RecyclerView mCategoryRecyclerView;


    private MySharedPreferences mySharedPreferences;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Database db;
    private String userId;
    private List<SimpleListPojo> mCategoryList;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        //set toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.app_name));

        /*
        * Set OnClickListener to Views
        * */
        mFab.setOnClickListener(this);

        /*
        * Set SwipeRefreshLayout
        * */
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //instantiate MySharedPreferences to get user data
        mySharedPreferences = MySharedPreferences.getInstance(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open,R.string.close);

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menu_add_category:
                        mDrawerLayout.closeDrawers();
                        goToAddCategoryActivity();
                        break;
                    case R.id.menu_manage_salesman:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(CategoryActivity.this, ManageSalesman.class));
                        break;
                    case R.id.menu_sales_tracker:
                        break;
                    case R.id.menu_setting:
                        break;
                    case R.id.menu_logout:
                        Util.logout(getApplication()); //logout
                        break;
                    case R.id.menu_share:
                        break;
                }
                return true;
            }
        });

        //set user information on the Navigation drawer Header
        TextView headerStoreName = (TextView) mNavigationView.findViewById(R.id.navigationHeaderShopname);
        TextView headerFullName = (TextView) mNavigationView.findViewById(R.id.navigationHeaderName);
        headerStoreName.setText(mySharedPreferences.getUserStorename());
        headerFullName.setText(mySharedPreferences.getUserFullname());


        //Get userId
        userId = mySharedPreferences.getUserId();
        Log.d(TAG, "HUS: userId " + userId);

        //Setup Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Add layout manager to RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCategoryRecyclerView.setLayoutManager(layoutManager);

        //Database
        db = new Database(this);

        /*
        * Method to fetch list from API or Sqlite database
        * */
        checkInternetAndDisplayList();

        //get userId
        //userId = db.getUserID();

        //when a item from the ListView is clicked
        /*categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //store clicked item in SimpleListPojo so that later we can get CategoryId and CategoryName
                SimpleListPojo mCategoryName = (SimpleListPojo) categoryListView.getItemAtPosition(position);
                //go to product activity
                Intent productIntent = new Intent(CategoryActivity.this, ProductActivity.class);
                //set categoryId and CategoryName in intenet
                productIntent.putExtra("categoryId", mCategoryName.getId());
                productIntent.putExtra("mCategoryName", mCategoryName.getName());
                startActivity(productIntent);
            }
        });*/
    }

    /*
    * Check user has a Internet connected
    * if yes Fetch fresh salesman list from api and store it in sqlite database
    * if no Go to Sqlitedatabase and get the salesman list
    * if no data in SqliteDatabase show a message
    * */
    private void checkInternetAndDisplayList() {
        if(Util.isConnectedToInternet(getApplication())){ //connected
            fetchCategoryInBackground(); //fetch data
        }else{ //not connected
            showListFromSqliteDatabase(); //method to display Data in list from Sqlite database
            Util.noInternetSnackbar(getApplication(),mLayout);
            //method to stop swipeRefreshlayout refresh icon
            stopRefreshing();
        }
    }

    /*
    * Method to fetch categopry from sqlite database and display it in RecyclerView
    * */
    private void showListFromSqliteDatabase() {
        List<SimpleListPojo> list = db.getAllSimpleList(Database.CATEGORY,userId);
        if(list != null){
            setupRecyclerView(list);
        }
    }

    /*
    * Method to go to AddCategoryActivity
    * */
    private void goToAddCategoryActivity() {
        startActivity(new Intent(CategoryActivity.this, AddCategoryActivity.class));
    }

    /*
    * Method to fetch Category From API
    * */
    public void fetchCategoryInBackground(){
        startRefreshing();
        //Request API for salesman list
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopRefreshing();
                //Call the method to parse json response
                Log.d(TAG,"HUS: "+response);
                mCategoryList = parseCategoryResponse(response);
                if(mCategoryList != null){
                    //Call SetRecyclerView to setup Recyclerview
                    setupRecyclerView(mCategoryList);
                }

                //Store new Salesman list in Sqlite Database
                db.deleteAllSimpleList(Database.CATEGORY);
                db.insertAllSimpleList(Database.CATEGORY,mCategoryList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopRefreshing();
                //handle Volley error
                Log.d(TAG,"HUS: error "+error.getMessage());
                String errorMessage = VolleySingleton.handleVolleyError(error);
                if(errorMessage != null){
                    Util.redSnackbar(getApplication(), mLayout, errorMessage);
                }

                /*
                * Show Salesman data from the Sqlitedatabase
                * */
                showListFromSqliteDatabase();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID,userId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    private List<SimpleListPojo> parseCategoryResponse(String response) {
        List<SimpleListPojo> list = JsonParser.simpleListParser(response);

        if(list != null){
            //If json Response Return false Display message in SnackBar
            if(!list.get(0).isReturned()){
                Log.d(TAG,"HUS: false blocked");
                Util.redSnackbar(getApplication(),mLayout,list.get(0).getMessage());
                return null;
            }else if(list.get(0).getCount() == 0){ //Count (Number of saleman returned)
                Log.d(TAG,"HUS: count zero");
                /*
                * If count return 0 means no salesman added
                * Hide recyclerView and show TextView
                * */
                mCategoryRecyclerView.setVisibility(View.GONE);
                mEmptyRecyclerView.setVisibility(View.VISIBLE);
                return null;
            }else{ //if we get true results
                Log.d(TAG,"HUS: item found");
                /*
                * If result found
                * Make recyclerview visible and TextView invisible
                * */
                mCategoryRecyclerView.setVisibility(View.VISIBLE);
                mEmptyRecyclerView.setVisibility(View.GONE);
                return list;
            }
        }else{ // when return null
            Toast.makeText(getApplication(), R.string.unable_to_parse_response, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /*
    * Method to set the RecyclerView
    * */
    private void setupRecyclerView(List<SimpleListPojo> list) {
        SimpleListAdapter adapter = new SimpleListAdapter(getApplication());
        adapter.setList(list);
        mCategoryRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //search
            case R.id.action_search:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //go to Add Category when fab was clicked
            case R.id.fabAddCategory:
                goToAddCategoryActivity();
                break;
        }
    }

    /*
    * Method is called when SwipeRefresh is triggered
    * */
    @Override
    public void onRefresh() {
        checkInternetAndDisplayList();
    }

    //method to stop swipeRefreshlayout refresh icon
    private void stopRefreshing() {
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startRefreshing(){
        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    //function to updateListView
    /*private void updateListView(List<SimpleListPojo> list){
        //if their is data in the list
        //if their is no data in the list
        if (list.size() > 0){
            //call CategoryAdapter and adapter a Custom ListView
            CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,R.layout.simple_list_layout,list);
            categoryListView.setAdapter(adapter);
        }else
            whenListIsEmpty.setText(getString(R.string.not_added_category));
    }*/



    //Private class to fetch category list from the database
    /*private class FetchCategoryTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            Functions.toggleProgressBar(pb); //show progressbar
        }

        @Override
        protected String doInBackground(String... params) {

            //generate hashmap to store data
            HashMap<String,String> fetchCategoryData = new HashMap<>();
            fetchCategoryData.put("userId",params[0]);

            //convert hashmap into EncodedUrl
            String data = Functions.hashMapToEncodedUrl(fetchCategoryData);

            //get the response from the web using GetJson class
            return GetJson.request(ApiUrl.GET_CATEGORY,data,"POST");
        }

        @Override
        protected void onPostExecute(String s) {
            //parse returned json and store it in a "categoryList"
            categoryList = JsonParser.simpleListParser(s);
            //call the updateListView method and update the "categoryListView"
            updateListView(categoryList);
            //hide the progressbar
            Functions.toggleProgressBar(pb); //hide progressbar
        }
    }*/
}
