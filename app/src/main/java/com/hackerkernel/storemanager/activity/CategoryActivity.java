package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hackerkernel.storemanager.AddCategoryActivity;
import com.hackerkernel.storemanager.DataBase;
import com.hackerkernel.storemanager.Functions;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.SaleTrackerActivity;
import com.hackerkernel.storemanager.SearchActivity;
import com.hackerkernel.storemanager.adapter.CategoryAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.CategoryPojo;
import com.hackerkernel.storemanager.util.Util;
import com.melnykov.fab.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CategoryActivity extends AppCompatActivity {
    private static final String TAG = CategoryActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.listView) ListView categoryListView;
    @Bind(R.id.progressBar) ProgressBar pb;
    @Bind(R.id.whenListIsEmpty)TextView whenListIsEmpty;
    @Bind(R.id.fabSell) FloatingActionButton fabSell;

    private DataBase db;
    private String userId;
    private List<CategoryPojo> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        //set toolbar
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getString(R.string.app_name));

        //instant DB
        //db = new DataBase(this);
        //get userId
        //userId = db.getUserID();

        //when a item from the ListView is clicked
        /*categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //store clicked item in CategoryPojo so that later we can get CategoryId and CategoryName
                CategoryPojo categoryName = (CategoryPojo) categoryListView.getItemAtPosition(position);
                //go to product activity
                Intent productIntent = new Intent(CategoryActivity.this, ProductActivity.class);
                //set categoryId and CategoryName in intenet
                productIntent.putExtra("categoryId", categoryName.getId());
                productIntent.putExtra("categoryName", categoryName.getName());
                startActivity(productIntent);
            }
        });

        //when fabSell button is clicke open the sells activity
        fabSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open sell activity
                Intent sellIntent = new Intent(CategoryActivity.this,SellActivity.class);
                startActivity(sellIntent);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Fetch category from the backend
        //new FetchCategoryTask().execute(userId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            //sales Tracker
            case R.id.action_sale_tracker:
                startActivity(new Intent(CategoryActivity.this,SaleTrackerActivity.class));
                break;
            //refresh
            case R.id.action_refresh:
                //Fetch category from the backend
                new FetchCategoryTask().execute(userId);
                break;
            //logout
            case R.id.action_logout:
                Util.logout(this); //logout
                break;
            //add new category
            case R.id.action_add_category:
                //show the add category alertDialog
                startActivity(new Intent(CategoryActivity.this,AddCategoryActivity.class));
                break;
            //search
            case R.id.action_search:
                Intent intent = new Intent(CategoryActivity.this,SearchActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //function to updateListView
    private void updateListView(List<CategoryPojo> list){
        //if their is data in the list
        //if their is no data in the list
        if (list.size() > 0){
            //call CategoryAdapter and adapter a Custom ListView
            CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,R.layout.category_list_layout,list);
            categoryListView.setAdapter(adapter);
        }else
            whenListIsEmpty.setText(getString(R.string.not_added_category));
    }

    //Private class to fetch category list from the database
    private class FetchCategoryTask extends AsyncTask<String,String,String>{

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
            categoryList = JsonParser.categoryParser(s);
            //call the updateListView method and update the "categoryListView"
            updateListView(categoryList);
            //hide the progressbar
            Functions.toggleProgressBar(pb); //hide progressbar
        }
    }
}
