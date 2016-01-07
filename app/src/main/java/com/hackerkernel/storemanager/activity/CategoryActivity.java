package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hackerkernel.storemanager.DataBase;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.SaleTrackerActivity;
import com.hackerkernel.storemanager.SearchActivity;
import com.hackerkernel.storemanager.pojo.CategoryPojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CategoryActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.listView) ListView categoryListView;
    @Bind(R.id.progressBar) ProgressBar pb;
    @Bind(R.id.whenListIsEmpty)TextView whenListIsEmpty;
    @Bind(R.id.fabAddCategory) FloatingActionButton mFab;
    @Bind(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigationView) NavigationView mNavigationView;

    private MySharedPreferences mySharedPreferences;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DataBase db;
    private String userId;
    private List<CategoryPojo> categoryList;

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

        //instant DB
        //db = new DataBase(this);
        //get userId
        //userId = db.getUserID();

        //when a item from the ListView is clicked
        /*categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //store clicked item in CategoryPojo so that later we can get CategoryId and CategoryName
                CategoryPojo mCategoryName = (CategoryPojo) categoryListView.getItemAtPosition(position);
                //go to product activity
                Intent productIntent = new Intent(CategoryActivity.this, ProductActivity.class);
                //set categoryId and CategoryName in intenet
                productIntent.putExtra("categoryId", mCategoryName.getId());
                productIntent.putExtra("mCategoryName", mCategoryName.getName());
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
                Intent intent = new Intent(CategoryActivity.this,SearchActivity.class);
                startActivity(intent);
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
    * Method to go to AddCategoryActivity
    * */
    private void goToAddCategoryActivity() {
        startActivity(new Intent(CategoryActivity.this, AddCategoryActivity.class));
    }

    //function to updateListView
    /*private void updateListView(List<CategoryPojo> list){
        //if their is data in the list
        //if their is no data in the list
        if (list.size() > 0){
            //call CategoryAdapter and adapter a Custom ListView
            CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,R.layout.category_list_layout,list);
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
            categoryList = JsonParser.categoryParser(s);
            //call the updateListView method and update the "categoryListView"
            updateListView(categoryList);
            //hide the progressbar
            Functions.toggleProgressBar(pb); //hide progressbar
        }
    }*/
}
