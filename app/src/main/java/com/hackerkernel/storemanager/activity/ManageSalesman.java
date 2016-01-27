package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class ManageSalesman extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fabAddSalesman) FloatingActionButton fab;
    @Bind(R.id.clayout) CoordinatorLayout mLayout;
    @Bind(R.id.salesmanRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.emptyRecyclerView) TextView mEmptyRecyclerViewText;
    @Bind(R.id.swipeRefresh) SwipeRefreshLayout mSwipeRefreshLayout;

    private RequestQueue mRequestQueue;
    private List<SimpleListPojo> mSalesmanList;
    private Database db;
    private ProgressDialog pd;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_salesman);
        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.manage_salesman);

        //Database
        db = new Database(this);

        //volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Add layout manager to RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //make a progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        /*
        * When Floating action button is clicked Go to AddSalesman Activity
        * */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageSalesman.this,AddSalesman.class));
            }
        });

        userId = MySharedPreferences.getInstance(getApplication()).getUserId();

        /*
        * Check user has a Internet connected
        * if yes Fetch fresh salesman list from api and store it in sqlite database
        * if no Go to Sqlitedatabase and get the salesman list
        * if no data in SqliteDatabase show a message
        * */
        checkInternetAndDisplayList();
    }

    /*
    * Check user has a Internet connected
    * if yes Fetch fresh salesman list from api and store it in sqlite database
    * if no Go to Sqlitedatabase and get the salesman list
    * if no data in SqliteDatabase show a message
    * */
    private void checkInternetAndDisplayList() {
        if(Util.isConnectedToInternet(getApplication())){ //connected
            fetchSalesmanInBackground(); //fetch data
        }else{ //not connected
            showListFromSqliteDatabase(); //method to display Data in list from Sqlite database
            Util.noInternetSnackbar(getApplication(),mLayout);
            //method to stop swipeRefreshlayout refresh icon
            stopRefreshing();
        }
    }

    /*
    * Method to fetch salesman from sqlite database and display it in RecyclerView
    * */
    private void showListFromSqliteDatabase() {
        List<SimpleListPojo> list = db.getAllSimpleList(Database.SALESMAN,userId);
        if(list != null){
            setupRecyclerView(list);
        }
    }


    public void fetchSalesmanInBackground(){
        startRefreshing();
        //get user id
        final String userId = MySharedPreferences.getInstance(getApplication()).getUserId();
        //Request API for salesman list
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_SALESMAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopRefreshing();
                //Call the method to parse json response
                mSalesmanList = parseSalesmanResponse(response);
                if(mSalesmanList != null){
                    //Call SetRecyclerView to setup Recyclerview
                    setupRecyclerView(mSalesmanList);
                    //Store new Salesman list in Sqlite Database
                    db.deleteAllSimpleList(Database.SALESMAN);
                    db.insertAllSimpleList(Database.SALESMAN, mSalesmanList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopRefreshing();
                //handle Volley error
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

    /*
    * Method to set the RecyclerView
    * */
    private void setupRecyclerView(List<SimpleListPojo> list) {
        SimpleListAdapter adapter = new SimpleListAdapter(getApplication(),SimpleListAdapter.SALESMAN);
        adapter.setList(list);
        mRecyclerView.setAdapter(adapter);
    }

    private List<SimpleListPojo> parseSalesmanResponse(String response) {
        List<SimpleListPojo> list = JsonParser.simpleListParser(response);

        if(list != null){
            //If json Response Return false Display message in SnackBar
            if(!list.get(0).isReturned()){
                Util.redSnackbar(getApplication(),mLayout,list.get(0).getMessage());
                return null;
            }else if(list.get(0).getCount() == 0){ //Count (Number of saleman returned)
                /*
                * If count return 0 means no salesman added
                * Hide recyclerView and show TextView
                * */
                mRecyclerView.setVisibility(View.GONE);
                mEmptyRecyclerViewText.setVisibility(View.VISIBLE);
                return null;
            }else{ //if we get true results
                /*
                * If result found
                * Make recyclerview visible and TextView invisible
                * */
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyRecyclerViewText.setVisibility(View.GONE);
                return list;
            }
        }else{ // when return null
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /* When Swipe to refresh layout is called */
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
}
