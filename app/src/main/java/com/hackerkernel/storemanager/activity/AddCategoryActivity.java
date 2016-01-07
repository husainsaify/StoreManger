package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hackerkernel.storemanager.DataBase;
import com.hackerkernel.storemanager.Functions;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SimplePojo;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddCategoryActivity extends AppCompatActivity {
    private static final String TAG = AddCategoryActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.categoryName) EditText categoryName;
    @Bind(R.id.addCategory) Button addCategory;
    /*
    * Using register pojo because the returned json is same as register
    * */
    List<SimplePojo> categoryList;


    public final Context context = AddCategoryActivity.this;

    ProgressDialog pd;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this);

        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_category);

        //make progressDialog
        pd = new ProgressDialog(context);
        pd.setMessage(getString(R.string.pleasewait));

        //instan the database
        db = new DataBase(this);

        //when add category button is added
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categoryName.getText().toString().trim();
                addCategory(category);
            }
        });
    }

    //add category
    private void addCategory(String category){
        //check category is not empty
        if(category.isEmpty()){
            Functions.errorAlert(context, getString(R.string.oops), getString(R.string.category_canot_empty));
        }else if(category.length() <= 3){
            Functions.errorAlert(context,getString(R.string.oops),getString(R.string.category_more_3_char));
        }else if(category.length() >= 20){
            Functions.errorAlert(context,getString(R.string.oops),getString(R.string.category_more_20_char));
        }else{
            //get the user id
            String userId = db.getUserID();

            //execute the aysnc task
            CategoryTask categoryTask = new CategoryTask();
            categoryTask.execute(category,userId);
        }
    }

    //async task to add category to database
    private class CategoryTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //generate hashmap to values to be send to the Server
            HashMap<String,String> categoryData = new HashMap<>();
            categoryData.put("categoryName",params[0]);
            categoryData.put("userId",params[1]);

            //convert hashmap into EncodedUrl
            String data = Functions.hashMapToEncodedUrl(categoryData);

            //make a request to the web and
            return GetJson.request(ApiUrl.ADD_CATEGORY,data,"POST");
        }

        @Override
        protected void onPostExecute(String s) {
            //parse return json and store the result in a list
            categoryList = JsonParser.SimpleParse(s);
            SimplePojo category = categoryList.get(0);

            pd.dismiss(); // dismiss the progress dialog

            if(category.getReturned()){//success
                Toast.makeText(context,category.getMessage(),Toast.LENGTH_LONG).show();
                //empty the exitText to enter categoryname
                categoryName.setText("");
            }else{//erro
                Functions.errorAlert(context,getString(R.string.oops),category.getMessage());

            }

        }
    }
}
