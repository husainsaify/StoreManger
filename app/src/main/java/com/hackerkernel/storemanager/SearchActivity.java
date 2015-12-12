package com.hackerkernel.storemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.CategoryPojo;

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

    //dataBase
    DataBase db;

    //global variables
    private String userId;
    List<CategoryPojo> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //instanciate database
        db = new DataBase(this);
        //get userId from the database
        userId = db.getUserID();

        //when search button is pressed
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = pName.getText().toString().trim();
                String productSize = pSize.getText().toString().trim();

                //check product Name is not empty
                if (productName.isEmpty()){
                    Functions.errorAlert(context,getString(R.string.oops),getString(R.string.product_name_not_empty));
                    return;
                }
            }
        });

        new CategoryTask().execute();
    }


    //Background Task to fetch category from the web
    private class CategoryTask extends AsyncTask<Void,Void,List<CategoryPojo>>{

        @Override
        protected List<CategoryPojo> doInBackground(Void... params) {
            //create Hashmap for userId
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userId",userId);

            //convert hashmap into encoded url to send them to database
            String data = Functions.hashMapToEncodedUrl(hashMap);

            //make a request to the backend
            String jsonResponse = GetJson.request(DataUrl.GET_CATEGORY,data,"POST");

            categoryList = JsonParser.categoryParser(jsonResponse);
            return categoryList;
        }

        @Override
        protected void onPostExecute(List<CategoryPojo> category) {
            //populate category spinner
            List<String> lable = new ArrayList<>();
            lable.add("Select Category");

            for (int i = 0; i < category.size(); i++) {
                lable.add(category.get(i).getName());
            }

           ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this,
                   android.R.layout.simple_spinner_item,lable);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            categorySpinner.setAdapter(adapter);
        }
    }

}
