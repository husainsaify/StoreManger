package com.hackerkernel.storemanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.hackerkernel.storemanager.storage.MySharedPreferences;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditProductActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    private RequestQueue mRequestQueue;
    private String mProductId;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        ButterKnife.bind(this);

        //set Toolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Edit product");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get ProductId From Intent
        if (getIntent().hasExtra(Keys.KEY_COM_PRODUCTID)){
            mProductId = getIntent().getExtras().getString(Keys.KEY_COM_PRODUCTID);
        }else {
            Toast.makeText(getApplication(), R.string.internal_error_restart_app, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Get Loggedin userId
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();

        fetchProductInBackground();
    }

    /*
    * Method to fetch product in background
    * */
    public void fetchProductInBackground(){
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplication(),response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID, mUserId);
                params.put(Keys.KEY_PL_ID, mProductId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
