package com.hackerkernel.storemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hackerkernel.storemanager.activity.ProductActivity;
import com.hackerkernel.storemanager.adapter.ProductAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.ProductListPojo;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private final Context context = this;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.pName) EditText pName;
    @Bind(R.id.pSize) EditText pSize;
    @Bind(R.id.pSearch) Button search;
    @Bind(R.id.categorySpinner) Spinner categorySpinner;
    @Bind(R.id.searchListView) ListView searchListView;

    //dataBase
    Database db;

    //global variables
    private String userId;
    private String mFailedMessage;
    List<SimpleListPojo> categoryList;
    List<ProductListPojo> searchList;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //instanciate database
        db = new Database(this);
        //get userId from the database
        //userId = db.getUserID();

        //create progressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        //when search button is pressed
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  productName = pName.getText().toString().trim(),
                        productSize = pSize.getText().toString().trim(),
                        productCategoryId = null;

                //check product Name is not empty
                if (productName.isEmpty()) {
                    Functions.errorAlert(context, getString(R.string.oops), getString(R.string.product_name_not_empty));
                    return;
                }

                //get the position of categorySpinner which is selectecd
                int position = categorySpinner.getSelectedItemPosition();
                //some category is selected
                if(position != 0){
                    /*
                    * Create new valid "categorySpinner" position for "categoryList"
                    * because we have add a placeholder text in "select Category" in "categorySpinner"
                    * */
                    int newPosition = position - 1;
                    //fetch CategoryName & CategoryId
                    productCategoryId = categoryList.get(newPosition).getId();
                }

                /*
                * Search backend to get product
                * */
                new SearchTask().execute(userId,productName,productSize,productCategoryId);
            }
        });

        /*
        * Fetch Category list
        * */
        new CategoryTask().execute();


        //when searchListView is clicked
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });
    }


    //Background Task to fetch category from the web
    private class CategoryTask extends AsyncTask<Void,Void,List<SimpleListPojo>>{

        @Override
        protected List<SimpleListPojo> doInBackground(Void... params) {
            //create Hashmap for userId
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userId",userId);

            //convert hashmap into encoded url to send them to database
            String data = Functions.hashMapToEncodedUrl(hashMap);

            //make a request to the backend
            String jsonResponse = GetJson.request(ApiUrl.GET_CATEGORY,data,"POST");

            categoryList = JsonParser.simpleListParser(jsonResponse);
            return categoryList;
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
            hashmap.put("userId",params[0]);
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
    }
}
