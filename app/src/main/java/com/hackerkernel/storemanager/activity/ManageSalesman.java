package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class ManageSalesman extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fabAddSalesman) FloatingActionButton fab;
    @Bind(R.id.clayout) CoordinatorLayout mLayout;
    @Bind(R.id.salesmanRecyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.emptyRecyclerView) TextView mEmptyRecyclerViewText;

    private RequestQueue mRequestQueue;
    private List<SimpleListPojo> mSalesmanList;
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
        Database database = new Database(this);
        database.test();

        //volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        /*
        * When Floating action button is clicked Go to AddSalesman Activity
        * */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageSalesman.this,AddSalesman.class));
            }
        });

        /*
        * Check user has a Internet connection or not
        * */
        if(Util.isConnectedToInternet(getApplication())){ //connected
            fetchSalesmanInBackground(); //fetch data
        }else{ //not connected
            Util.noInternetSnackbar(getApplication(),mLayout);
        }
    }


    public void fetchSalesmanInBackground(){
        //get user id
        final String userId = MySharedPreferences.getInstance(getApplication()).getUserId();
        //Request API for salesman list
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_SALESMAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Call the method to parse json response
                mSalesmanList = parseSalesmanResponse(response);
                if(mSalesmanList != null){
                    //Call SetRecyclerView to setup Recyclerview
                    setupRecyclerView();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle Volley error
                String errorMessage = VolleySingleton.handleVolleyError(error);
                if(errorMessage != null){
                    Util.redSnackbar(getApplication(), mLayout, errorMessage);
                }
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

    private void setupRecyclerView() {
        SimpleListAdapter adapter = new SimpleListAdapter(getApplication());
        adapter.setList(mSalesmanList);
        Log.d("HUS", "HUS: " + adapter.getItemCount());
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

}
