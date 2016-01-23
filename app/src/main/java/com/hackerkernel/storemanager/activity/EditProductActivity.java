package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hackerkernel.storemanager.pojo.ProductPojo;
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

public class EditProductActivity extends AppCompatActivity {
    private static final String TAG = EditProductActivity.class.getSimpleName();

    @Bind(R.id.layout) LinearLayout mLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.productImage) ImageView mProductImage;
    @Bind(R.id.categorySpinner) Spinner mCategorySpinner;
    @Bind(R.id.productName) EditText mProductName;
    @Bind(R.id.productCode) EditText mProductCode;
    @Bind(R.id.productCostPrice) EditText mProductCP;
    @Bind(R.id.productSellingPrice) EditText mProductSP;
    @Bind(R.id.productSizeLayout) LinearLayout mSizeLayout;
    @Bind(R.id.productQuantityLayout) LinearLayout mQuantityLayout;
    @Bind(R.id.productDeleteLayout) LinearLayout mDeleteLayout;
    @Bind(R.id.done) Button mDone;

    private RequestQueue mRequestQueue;
    private String mProductId;
    private String mUserId;
    private String mCategoryId = null;
    private ProgressDialog pd;
    private Database db;
    private List<SimpleListPojo> mCategorySimpleList;
    private List<EditText> mSizeList;
    private List<EditText> mQuantityList;
    private List<ImageButton> mDeleteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        ButterKnife.bind(this);

        //set Toolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.edit_product);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Create progressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        mSizeList = new ArrayList<>();
        mQuantityList = new ArrayList<>();
        mDeleteList = new ArrayList<>();

        //get ProductId From Intent
        if (getIntent().hasExtra(Keys.KEY_COM_PRODUCTID)) {
            mProductId = getIntent().getExtras().getString(Keys.KEY_COM_PRODUCTID);
        } else {
            Toast.makeText(getApplication(), R.string.internal_error_restart_app, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //Instanciate Database
        db = new Database(this);

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //Get Loggedin userId
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();


        checkInternetAndFetchProduct();

        //Change the Category id when Another item from a spinner is selected
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryId = mCategorySimpleList.get(position).getId();
                Toast.makeText(getApplicationContext(), mCategoryId, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*
    * Method to set Category Spinner From SQlite database
    * */
    public void setUpCategorySpinner(String categoryId) {
        //setup StringList to avoid NullPointerException
        List<String> stringList = new ArrayList<>();
        //Get category data from Sqlite Database
        mCategorySimpleList = db.getAllSimpleList(Database.CATEGORY, mUserId);

        //mCategorySimpleList is not null
        if (mCategorySimpleList.size() > 0) {
            //Create a simple
            for (int i = 0; i < mCategorySimpleList.size(); i++) {
                SimpleListPojo c = mCategorySimpleList.get(i);
                //Make a Simple String list which can be used with Default spinner adapter
                stringList.add(c.getName());
            }

            //setup List to resources
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stringList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCategorySpinner.setAdapter(adapter);

            //Make categoryID selected in the spinner
            Util.setSpinnerPostionToCategoryID(mCategorySimpleList, categoryId, mCategorySpinner);
        } else {
            Toast.makeText(getApplication(), R.string.unable_to_load_category, Toast.LENGTH_LONG).show();
        }
    }


    /*
    * Method to check internet and fetch fetchProduct
    * */
    public void checkInternetAndFetchProduct() {
        if (Util.isConnectedToInternet(getApplication())) {
            fetchProductInBackground();
        } else {
            Toast.makeText(getApplication(), R.string.check_your_internet_and_try_again, Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to fetch product in background
    * */
    public void fetchProductInBackground() {
        pd.show(); //show progressDialog
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //Hide progressDialog
                parseProductResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss(); //Hide progressDialog
                Toast.makeText(getApplication(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
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

    private void parseProductResponse(String response) {
        try {
            JSONObject jo = new JSONObject(response);
            //Check Response return in true or false
            if (jo.getBoolean(Keys.KEY_COM_RETURN)) {

                //Parse the response
                ProductPojo current = JsonParser.productParser(response);

                //Check Response is valid
                if (current != null) {
                    //Set Category Id to member variable
                    mCategoryId = current.getCategoryId();
                    //setup category spinner
                    setUpCategorySpinner(mCategoryId);

                    setUpViews(current);
                } else {
                    Toast.makeText(getApplication(), R.string.unable_to_parse_response, Toast.LENGTH_LONG).show();
                }
            } else {
                //return is false show error
                Util.redSnackbar(getApplication(), mLayout, jo.getString(Keys.KEY_COM_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), R.string.unable_to_parse_response, Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to set the detials of product in the views
    * */
    public void setUpViews(ProductPojo product) {
        mProductName.setText(product.getName());
        mProductCode.setText(product.getCode());
        mProductSP.setText(product.getSp());
        mProductCP.setText(product.getCp());

        String[] sizeArray = product.getSize().split("\n");
        String[] quantityArray = product.getQuantity().split("\n");

        //Add EditText bassed on sizeArray length
        for (int i = 0; i < sizeArray.length; i++) {
            //Add editText to size & quantity layout and Button to delete layout
            EditText size = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style, null);
            EditText quantity = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style, null);

            //get Size & Quantity
            String s = sizeArray[i];
            String q = quantityArray[i];

            size.setText(s);
            quantity.setText(q);

            ImageButton deleteButton = new ImageButton(getApplication());
            deleteButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.ic_delete_black));
            deleteButton.setTag(i + 1);
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setOnClickListener(deleteListner);

            //add views to layout
            mSizeLayout.addView(size);
            mQuantityLayout.addView(quantity);
            mDeleteLayout.addView(deleteButton);

            //Add views to list
            mSizeList.add(size);
            mQuantityList.add(quantity);
            mDeleteList.add(deleteButton);
        }
    }

    /*
    * Method to add more Size, Quantity and Delete button to Their respective layout
    * */
    private void loadMore() {
        EditText size = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style, null);
        EditText quantity = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style, null);
        ImageButton deleteButton = new ImageButton(getApplication());
        deleteButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.ic_delete_black));
        deleteButton.setTag(mSizeList.size() + 1);
        deleteButton.setOnClickListener(deleteListner);
        deleteButton.setBackgroundColor(Color.TRANSPARENT);

        //add to layout
        mSizeLayout.addView(size);
        mQuantityLayout.addView(quantity);
        mDeleteLayout.addView(deleteButton);

        //add to list
        mSizeList.add(size);
        mQuantityList.add(quantity);
        mDeleteList.add(deleteButton);
    }

    /*
    * Listner when the delete button is pressed
    * */
    View.OnClickListener deleteListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mSizeList.size() > 1 && mQuantityList.size() > 1){
                int tag = (int) v.getTag();
                int index = tag - 1; //because array start with zero

                //get the views
                EditText size = mSizeList.get(index);
                EditText quantity = mQuantityList.get(index);
                ImageButton delete = mDeleteList.get(index);

                //remove from layout
                mSizeLayout.removeView(size);
                mQuantityLayout.removeView(quantity);
                mDeleteLayout.removeView(delete);

                //remove from list
                mSizeList.remove(index);
                mQuantityList.remove(index);
                mDeleteList.remove(index);

                //reset all the tags
                for (int i = 0; i < mDeleteList.size(); i++) {
                    mDeleteList.get(i).setTag(i+1);
                }
            }else{
                Toast.makeText(getApplication(), R.string.atleast_one_size_quantity_field,Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //when load more button is pressed
            case R.id.loadMore:
                loadMore();
                break;
            //when home button is pressed
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
