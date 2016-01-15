package com.hackerkernel.storemanager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.AddCategoryActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.fabAddCategory) FloatingActionButton mFab;
    @Bind(R.id.clayout) CoordinatorLayout mLayout;
    @Bind(R.id.emptyRecyclerView) TextView mEmptyRecyclerView;
    @Bind(R.id.swipeRefresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.categoryRecyclerView) RecyclerView mCategoryRecyclerView;

    private Database db;
    private String userId;
    private List<SimpleListPojo> mCategoryList;
    private RequestQueue mRequestQueue;
    private MySharedPreferences mySharedPreferences;


    private static final String TAG = CategoryFragment.class.getSimpleName();
    public CategoryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * Get UserId & Request Queue From VolleySingleton
        * */
        mySharedPreferences = MySharedPreferences.getInstance(getActivity());
        userId = mySharedPreferences.getUserId();
        Log.d(TAG, "HUS: userId " + userId);
        //Setup Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Database
        db = new Database(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);

        /*
        * Set OnClickListener to Views
        * */
        mFab.setOnClickListener(this);

        /*
        * Set SwipeRefreshLayout
        * */
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //Add layout manager to RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mCategoryRecyclerView.setLayoutManager(layoutManager);

        /*
        * Method to fetch list from API or Sqlite database
        * */
        checkInternetAndDisplayList();

        return view;
    }

    /*
    * Check user has a Internet connected
    * if yes Fetch fresh salesman list from api and store it in sqlite database
    * if no Go to Sqlitedatabase and get the salesman list
    * if no data in SqliteDatabase show a message
    * */
    private void checkInternetAndDisplayList() {
        if(Util.isConnectedToInternet(getActivity())){ //connected
            fetchCategoryInBackground(); //fetch data
        }else{ //not connected
            showListFromSqliteDatabase(); //method to display Data in list from Sqlite database
            Util.noInternetSnackbar(getActivity(),mLayout);
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
        startActivity(new Intent(getActivity(), AddCategoryActivity.class));
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
                    Util.redSnackbar(getActivity(), mLayout, errorMessage);
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
                Util.redSnackbar(getActivity(),mLayout,list.get(0).getMessage());
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
            Toast.makeText(getActivity(), R.string.unable_to_parse_response, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /*
    * Method to set the RecyclerView
    * */
    private void setupRecyclerView(List<SimpleListPojo> list) {
        SimpleListAdapter adapter = new SimpleListAdapter(getActivity(),SimpleListAdapter.CATEGORY);
        adapter.setList(list);
        mCategoryRecyclerView.setAdapter(adapter);
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
}
