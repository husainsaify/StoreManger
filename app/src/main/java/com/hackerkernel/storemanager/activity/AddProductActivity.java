package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.ImageSeletion;
import com.hackerkernel.storemanager.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{
    //Global varaible
    private static final String TAG = AddProductActivity.class.getSimpleName();

    @Bind(R.id.addProductLayout) LinearLayout mLayout;
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
    @Bind(R.id.productSize) EditText mProductSize;
    @Bind(R.id.productQuantity) EditText mProductQuantity;
    @Bind(R.id.productDelete) Button mProductDelete;
    @Bind(R.id.addProduct) Button mAddProduct;


    private String mUserId;
    private List<SimpleListPojo> mCategorySimpleList;
    private String mCategoryId;
    private String mCategoryName;
    private Database db;
    private ProgressDialog pd;
    //list to store (size,Qunatity and delete refernce)
    private List<EditText> mSizeList;
    private List<EditText> mQuantityList;
    private List<Button> mDeleteList;
    //Volley
    private RequestQueue mRequestQueue;
    //ImageSelection class (TO select image)
    private ImageSeletion mImageSelection;

    //variable to hold camera or gallery
    private Bitmap mSelectedImage = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        //set toolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.add_product));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Username From SharedPreferences
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();

        //initalize database
        db = new Database(this);

        //Set Delete button background transparent & OnClickMethod & its tag
        mProductDelete.setBackgroundColor(Color.TRANSPARENT);
        mProductDelete.setOnClickListener(deleteBtnClick);
        mProductDelete.setTag(1);

        //Set UP Volley RequestQueue
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        //setup category Spinner
        mCategorySimpleList = Util.setupCategorySpinnerFromDb(getBaseContext(),db,mUserId,mCategorySpinner,false);

        //check category is not null
        if (mCategorySimpleList == null){
            Util.redSnackbar(getApplication(),mLayout,getString(R.string.it_seem_your_havent_added_any_category));
            return;
        }
        /*
        * Check intent has send categoryId & categoryName
        * if yes store them in Member variables
        * */
        if(getIntent().hasExtra(Keys.KEY_COM_CATEGORYID) && getIntent().hasExtra(Keys.KEY_COM_CATEGORYNAME)){
            mCategoryId = getIntent().getExtras().getString(Keys.KEY_COM_CATEGORYID);
            mCategoryName = getIntent().getExtras().getString(Keys.KEY_COM_CATEGORYNAME);

            //Set spinner to category Id
            Util.setSpinnerPostionToCategoryID(mCategorySimpleList,mCategoryId,mCategorySpinner);
        }

        //When An item is selected in spinner
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Store CategoryId & categoryName
                mCategoryId = mCategorySimpleList.get(position).getId();
                mCategoryName = mCategorySimpleList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), R.string.select_category_to_continue,Toast.LENGTH_LONG).show();
            }
        });

        /*
        * Store Size, Quantity and delete in their List
        * */
        mSizeList = new ArrayList<>();
        mQuantityList = new ArrayList<>();
        mDeleteList = new ArrayList<>();
        mSizeList.add(mProductSize);
        mQuantityList.add(mProductQuantity);
        mDeleteList.add(mProductDelete);

        //Create a progressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        //Instanciate ImageSelection class
        mImageSelection = new ImageSeletion(this);

        //Set OnClickMethod on productImage(Camera icon)
        mProductImage.setOnClickListener(this);

        //set OnclickMethod on insertProduct
        mAddProduct.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //when Camera image is clicked open ChosePicture alertDialog
            case R.id.productImage:
                mImageSelection.selectImage();
                break;
            //When add product button is clicked
            case R.id.addProduct:
                addProduct();
                break;
        }
    }

    /*
    * Method to dynamically add Size, Quantity and Delete
    * */
    private void loadMore() {
        //Set delete button
        Button delete = new Button(getApplication());
        delete.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //set icon and background to button
        delete.setBackgroundColor(Color.TRANSPARENT);
        delete.setGravity(Gravity.CENTER);

        //setTag to the button
        delete.setTag(mDeleteList.size() + 1);
        delete.setOnClickListener(deleteBtnClick);

        //set icon
        Drawable icon = ContextCompat.getDrawable(getApplication(),R.drawable.ic_delete_black);
        delete.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        //set size & quantity
        EditText size = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style_number, null);
        EditText quantity = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style_number, null);

        //add to list
        mSizeList.add(size);
        mQuantityList.add(quantity);
        mDeleteList.add(delete);

        //append to layout
        mSizeLayout.addView(size);
        mQuantityLayout.addView(quantity);
        mDeleteLayout.addView(delete);
    }


    /*
    * Click handler for Delete Button
    * THis method delete Size & quantity from layout & list
    * */
    View.OnClickListener deleteBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //only allow to remove views when its more then one
            if(mSizeList.size() > 1  && mQuantityList.size() > 1){
                int tag = (int) v.getTag();
                int index = tag - 1; //because array starts with zero and tag starts with 1
                //Get the reference of size,Quantity and delete button

                EditText size = mSizeList.get(index);
                EditText quantity = mQuantityList.get(index);
                Button delete = mDeleteList.get(index);

                //Remove views from layout
                mSizeLayout.removeView(size);
                mQuantityLayout.removeView(quantity);
                mDeleteLayout.removeView(delete);

                //Remove views from list
                mSizeList.remove(index);
                mQuantityList.remove(index);
                mDeleteList.remove(index);

                //reset all the delete button tags
                for (int i = 0; i < mDeleteList.size(); i++) {
                    mDeleteList.get(i).setTag(i+1);
                }
            }else{
                Toast.makeText(getApplication(), R.string.atleast_one_size_quantity_field,Toast.LENGTH_LONG).show();
            }
        }
    };

    /*
    * When image is selected from the gallery
    * set image to mProductImage (imageView)
    * and store in a bitmap member variable
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc();
        //camera
        if(requestCode == mImageSelection.TAKE_PICTURE && resultCode == RESULT_OK && data != null){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mProductImage.setImageBitmap(photo);

            //set Camera image into Member variable
            mSelectedImage = photo;
        }else if(requestCode == mImageSelection.CHOSE_PICTURE && resultCode == RESULT_OK && data != null){ //gallery
            Uri selectedImageUri = data.getData();
            try {
                //set gallery image into Member varialble
                mSelectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                mProductImage.setImageBitmap(mSelectedImage);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"HUS: onActivityResult "+e.getMessage());
                Toast.makeText(getApplication(),getString(R.string.file_not_found),Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.loadMore:
                loadMore();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addProduct() {
        //get all the texts from the fields
        String  name = mProductName.getText().toString().trim(),
                code = mProductCode.getText().toString().trim(),
                cp = mProductCP.getText().toString().trim(),
                sp = mProductSP.getText().toString().trim();

        /*
        * Get image if user has added one
        * */
        String encodedImage = "";
        if(mSelectedImage != null){
            //Compress image to Base64
            encodedImage = mImageSelection.compressImageToBase64(mSelectedImage);
        }

        // if any field is empty
        if(name.isEmpty() || code.isEmpty() || cp.isEmpty() || sp.isEmpty()){
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.fillin_all_fields));
            return;
        }

        //check name is more then 3 char long
        if(name.length() < 3){
            Util.redSnackbar(getApplication(),mLayout,getString(R.string.name_more_then_2));
            return;
        }

        //check code
        if(code.length() < 3){
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.pcode_more_then_2));
            return;
        }

        //check sp is not smaller then cp
        int difference = Integer.parseInt(sp) - Integer.parseInt(cp);
        if(difference < 0){
            Util.redSnackbar(getApplication(), mLayout, getString(R.string.sp_cannot_be_then_cp));
            return;
        }

        //generate a StringBuilder for size & quantity
        StringBuilder sizeBuilder = new StringBuilder();
        StringBuilder quantityBuilder = new StringBuilder();

        //check size & quantity
        for (int i = 0; i < mSizeList.size(); i++) {
            String size = mSizeList.get(i).getText().toString().trim();
            String quantity = mQuantityList.get(i).getText().toString().trim();

            if (size.isEmpty() || quantity.isEmpty()){
                //display error
                Util.redSnackbar(getApplication(),mLayout,getString(R.string.size_quantity_canot_empty));
                return;
            }else{
                //add to StringBuilder
                sizeBuilder.append(size).append(",");
                quantityBuilder.append(quantity).append(",");
            }
        }

        //Check user has internet connection of not
        if(Util.isConnectedToInternet(getApplication())){
            //Call addProductInBackground to add product to API
            addProductInBackground(encodedImage,name,code,cp,sp,sizeBuilder.toString(),quantityBuilder.toString());
        }else{
            Util.noInternetSnackbar(getApplicationContext(),mLayout);
        }
    }

     /*
      * Method to make a call to API and save product data
      * */
    private void addProductInBackground(final String encodedImage, final String name, final String code, final String cp, final String sp, final String size, final String quantity){
        pd.show(); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST,ApiUrl.ADD_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide progressbar
                Log.d(TAG,"HUS: response "+response);
                //parse response
                parseAddProductResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss(); //hide progressbar
                //handle Volley error
                String errorMessage = VolleySingleton.handleVolleyError(error);
                if(errorMessage != null){
                    Util.redSnackbar(getApplication(),mLayout,errorMessage);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Keys.KEY_COM_CATEGORYNAME,mCategoryName);
                param.put(Keys.KEY_COM_CATEGORYID,mCategoryId);
                param.put(Keys.KEY_COM_USERID,mUserId);
                param.put(Keys.PRAM_AP_IMAGE,encodedImage);
                param.put(Keys.PRAM_AP_NAME,name);
                param.put(Keys.PRAM_AP_CODE,code);
                param.put(Keys.PRAM_AP_CP,cp);
                param.put(Keys.PRAM_AP_SP,sp);
                param.put(Keys.PRAM_AP_SIZE,size);
                param.put(Keys.PRAM_AP_QUANTITY,quantity);
                return param;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse addProductInBackground response and display result
    * */
    private void parseAddProductResponse(String response){
        //parse response and store the result in a list
        List<SimplePojo> list = JsonParser.simpleParser(response);

        //check the response list is not null
        if(list != null){
            SimplePojo current = list.get(0);

            if(current.getReturned()){//success
                Toast.makeText(getApplication(),current.getMessage(),Toast.LENGTH_LONG).show();
                //restart activity
                Intent restartIntent = getIntent();
                finish();
                startActivity(restartIntent);
            }else{//error
                Util.redSnackbar(getApplication(),mLayout,current.getMessage());
            }
        }else{ //when the list is null show this message
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }
}
