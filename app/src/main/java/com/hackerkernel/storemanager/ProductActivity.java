package com.hackerkernel.storemanager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hackerkernel.storemanager.adapter.ProductAdapter;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.ProductPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductActivity extends AppCompatActivity {
    //Global variable
    private static final String TAG = ProductActivity.class.getSimpleName();
    private final Context context = this;

    private String  categoryId,
            categoryName,
            userId;
    private DataBase db;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.productList) ListView listView;
    @Bind(R.id.whenListIsEmpty) TextView whenListIsEmpty;
    @Bind(R.id.progressBar) ProgressBar pb;

    //list
    List<ProductPojo> productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);

        //get the categoryId , categoryName & userId
        categoryId = getIntent().getExtras().getString("categoryId");
        categoryName = getIntent().getExtras().getString("categoryName");
        db = new DataBase(this);
        userId = db.getUserID();

        //initalize list
        productList = new ArrayList<>();

        //set the Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //When user click on of the item from the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get item where user has clicked
                ProductPojo product = (ProductPojo) listView.getItemAtPosition(position);
                //Send to ViewProductActivity
                Intent intent = new Intent(ProductActivity.this,ViewProductActivity.class);
                intent.putExtra("pName",product.getProductName());
                intent.putExtra("pId", product.getProductId());
                intent.putExtra("pImageAddress",product.getProductImage());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Do all the stuff and Set Result to ListView
        refreshList();
    }

    public void refreshList(){
        //fetch Json data from the Backend and Parse it into a ListView
        new productTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshList(); //refresh view
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Open addProduct Activity when FAB is clicked
    public void openAddProduct(View view){
        Intent addProductIntent = new Intent(ProductActivity.this,AddProductActivity.class);
        addProductIntent.putExtra("categoryId",categoryId);
        addProductIntent.putExtra("categoryName",categoryName);
        addProductIntent.putExtra("userId",userId);
        startActivity(addProductIntent);
    }

    //This method will Adapte a List into a ListView
    private void updateListView(List<ProductPojo> list){
        if(list != null){
            ProductAdapter productAdapter = new ProductAdapter(ProductActivity.this,R.layout.product_list_layout,list);
            listView.setAdapter(productAdapter);
        }else{
            whenListIsEmpty.setText("No Product Listed");
        }
    }

    //Class to fetch json data from the Backend
    private class productTask extends AsyncTask<String,String,List<ProductPojo>>{
        @Override
        protected void onPreExecute() {
            //show PB
            Functions.toggleProgressBar(pb);
        }

        @Override
        protected List<ProductPojo> doInBackground(String... params) {
            /*
            * Fetch the Product list from the web
            * */
            //generate a HashMap as fro sending POST params to Backend
            HashMap<String,String> productHash = new HashMap<>();
            productHash.put("userId",userId);
            productHash.put("categoryId",categoryId);

            //convert a hashmap into Encoded Url so that we can send it to Backend
            String dataUrl = Functions.hashMapToEncodedUrl(productHash);

            //request the Backend
            String jsonString = GetJson.request(ApiUrl.GET_PRODUCT,dataUrl,"POST");

            //parse Json and store it in "productList"
            productList = JsonParser.productParser(jsonString);


            return productList;
        }

        @Override
        protected void onPostExecute(List<ProductPojo> list) {
            //set "ProductList" to "ListView"
            updateListView(list);
            //Hide PB
            Functions.toggleProgressBar(pb);
        }
    }
}
