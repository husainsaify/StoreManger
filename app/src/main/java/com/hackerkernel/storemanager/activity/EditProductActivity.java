package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.ProductPojo;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.ImageSeletion;
import com.hackerkernel.storemanager.util.ImageUtil;
import com.hackerkernel.storemanager.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = EditProductActivity.class.getSimpleName();

    @Bind(R.id.layout) LinearLayout mLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.productImage) ImageView mProductImage;
    @Bind(R.id.categorySpinner) Spinner mCategorySpinner;
    @Bind(R.id.productName) EditText mProductName;
    @Bind(R.id.productCostPrice) EditText mProductCP;
    @Bind(R.id.productSellingPrice) EditText mProductSP;
    @Bind(R.id.productSizeLayout) LinearLayout mSizeLayout;
    @Bind(R.id.productQuantityLayout) LinearLayout mQuantityLayout;
    @Bind(R.id.productDeleteLayout) LinearLayout mDeleteLayout;
    @Bind(R.id.done) Button mDone;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private String mProductId = null;
    private String mUserId;
    private String mCategoryId = null;
    private String mProductCode = null;
    private ProgressDialog pd;
    private Database db;
    private List<SimpleListPojo> mCategorySimpleList;
    private List<EditText> mSizeList;
    private List<EditText> mQuantityList;
    private List<Button> mDeleteList;

    //ImageSelection class with will help in selecting image
    private ImageSeletion mImageSelection;
    private Bitmap mImageBitmap; //variable to store selected image

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
        mImageLoader = VolleySingleton.getInstance().getImageLoader();

        //Get Loggedin userId
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();

        //Instanciate ImageSelection class which will help in selecting image
        mImageSelection = new ImageSeletion(this);

        checkInternetAndFetchProduct();

        //Change the Category id when Another item from a spinner is selected
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryId = mCategorySimpleList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //add ClickListener for Done Button
        mDone.setOnClickListener(this);

        mProductImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //when done button is pressed
            case R.id.done:
                doneProductEditing();
                break;
            //when image is clicked
            case R.id.productImage:
                //Select image
                mImageSelection.selectImage();
                break;
        }
    }

    /*
    * This method will run when a user has selected an image
    * this method will set the selected image to the views
    * and store the selected image in a member varaible
    * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take picture (camera)
        if(requestCode == mImageSelection.TAKE_PICTURE && resultCode == RESULT_OK){
            //Get camera image path
            String imagePath = mImageSelection.getCameraImagePath();

            //check image path is not null
            if (imagePath != null){
                //decode image from file path and store it in member variable
                mImageBitmap = ImageUtil.decodeBitmapFromFilePath(imagePath,280,150);

                //check Selected image is not bull
                if (mImageBitmap != null)
                    mProductImage.setImageBitmap(mImageBitmap);
                else
                    Toast.makeText(getApplicationContext(), R.string.failed_to_decode_image_from_camera,Toast.LENGTH_LONG).show();

            }else{
                Util.redSnackbar(getApplicationContext(), mLayout, getString(R.string.failed_to_load_image));
            }
        }
        //choose picture (gallery)
        else if(requestCode == mImageSelection.CHOSE_PICTURE && resultCode == RESULT_OK && data != null){
            //get image uri
            Uri imageUri = data.getData();

            //Get image path
            String imagePath = ImageUtil.getFilePathFromUri(getApplicationContext(),imageUri);

            //check image path is not null
            if (imagePath != null){
                //Decode Selected bitmap and store in member variable
                mImageBitmap = ImageUtil.decodeBitmapFromFilePath(imagePath,280,150);

                //check decoded image is not null
                if (mImageBitmap != null)
                    mProductImage.setImageBitmap(mImageBitmap);
                else
                    Toast.makeText(getApplicationContext(),R.string.failed_to_decode_image_from_gallery,Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(getApplicationContext(), R.string.file_not_found, Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
        * Method to set Category Spinner From SQlite database
        * */
    private void setUpCategorySpinner(String categoryId) {
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
    private void checkInternetAndFetchProduct() {
        if (Util.isConnectedToInternet(getApplication())) {
            fetchProductInBackground();
        } else {
            Toast.makeText(getApplication(), R.string.check_your_internet_and_try_again, Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to fetch product in background
    * */
    private void fetchProductInBackground() {
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
                params.put(Keys.KEY_COM_PRODUCTID, mProductId);
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
                    //Set Category Id & Product Id & ProductCode to member variable
                    mCategoryId = current.getCategoryId();
                    mProductId = current.getId();
                    mProductCode = current.getCode();

                    String imageAddress = current.getImageAddress();

                    //setup category spinner
                    setUpCategorySpinner(mCategoryId);

                    //set data to the views
                    setUpViews(current);

                    //set product image
                    setupProductImage(imageAddress);
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
    private void setUpViews(ProductPojo product) {
        mProductName.setText("");
        mProductName.append(product.getName()); //set cursor to the last element of editText
        mProductSP.setText(product.getSp());
        mProductCP.setText(product.getCp());

        String[] sizeArray = product.getSize().split("\n");
        String[] quantityArray = product.getQuantity().split("\n");

        //Add EditText bassed on sizeArray length
        for (int i = 0; i < sizeArray.length; i++) {
            //Add editText to size & quantity layout and Button to delete layout
            EditText size = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style_number, null);
            EditText quantity = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style_number, null);

            //get Size & Quantity
            String s = sizeArray[i];
            String q = quantityArray[i];

            size.setText(s);
            quantity.setText(q);

            Button deleteButton = new Button(getApplication());
            deleteButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setGravity(Gravity.CENTER);
            deleteButton.setTag(i + 1);
            deleteButton.setOnClickListener(deleteListner);
            //set icon
            Drawable icon = ContextCompat.getDrawable(getApplication(), R.drawable.ic_delete_black);
            deleteButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

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
        EditText size = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style_number, null);
        EditText quantity = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style_number, null);

        Button deleteButton = new Button(getApplication());
        deleteButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //set icon and background to button
        deleteButton.setBackgroundColor(Color.TRANSPARENT);
        deleteButton.setGravity(Gravity.CENTER);

        deleteButton.setTag(mSizeList.size() + 1);
        deleteButton.setOnClickListener(deleteListner);

        //set icon
        Drawable icon = ContextCompat.getDrawable(getApplication(), R.drawable.ic_delete_black);
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

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
                Button delete = mDeleteList.get(index);

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

    /*
    * Method to load image From sdcard if avaiable else From THe API
    * */
    private void setupProductImage(String imageAddress){
        //check image is set by the user or not for the product
        if(imageAddress != null && !imageAddress.isEmpty()){
            //check image Uri is avaialble or not
            if(db.checkProductUri(mUserId,mProductId)){
                //get the Uri of product image
                Uri imageUri = db.getProductUri(mUserId,mProductId);
                String imageUriString = String.valueOf(imageUri);

                //check file exits or not
                File file = new File(URI.create(imageUriString).getPath());

                if(file.exists()){
                    mProductImage.setImageURI(imageUri);
                }else{
                    //fetch image from API
                    fetchImageInBackground(imageAddress);
                }
            }else{
                //fetch image from API
                fetchImageInBackground(imageAddress);
            }
        }
    }


    private void fetchImageInBackground(String imageAddress){
        //check Internet Connection
        if(Util.isConnectedToInternet(getApplication())){
            String imageUrl = ApiUrl.IMAGE_BASE_URL + imageAddress;
            mImageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    //set image to view
                    mProductImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "HUS: fetchImageInBackground: " + error.getMessage());
                    //Handle volley error
                    String errorString = VolleySingleton.handleVolleyError(error);
                    if (errorString != null) {
                        Util.redSnackbar(getApplication(), mLayout, errorString);
                    }
                }
            });
        }
    }

    /*
    *
    * THis method is called when user press the done button
    * This method will check info is valid or not
    * */
    private void doneProductEditing() {
        //check internet
        if(Util.isConnectedToInternet(getApplication())){
            //get text from the views
            String name = mProductName.getText().toString().trim(),
                    sp = mProductSP.getText().toString().trim(),
                    cp = mProductCP.getText().toString().trim();

            // if any field is empty
            if(name.isEmpty() || cp.isEmpty() || sp.isEmpty()){
                Util.redSnackbar(getApplication(), mLayout, getString(R.string.fillin_all_fields));
                return;
            }

            //check categoryId & productId is not null
            if(mCategoryId == null || mProductId == null){
                Util.redSnackbar(getApplication(), mLayout, getString(R.string.please_check_your_internt));
                return;
            }

            //code to convert image to Base64
            String encodedImage = "";
            if(mImageBitmap != null){
                encodedImage = ImageUtil.compressImageToBase64(mImageBitmap);
            }

            //check name is more then 3 char long
            if(name.length() < 3){
                Util.redSnackbar(getApplication(),mLayout,getString(R.string.name_more_then_2));
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


            //Call method to store product info in api
            doneEditingInBackground(encodedImage,name,cp,sp,sizeBuilder.toString(),quantityBuilder.toString());
        }else{
            Toast.makeText(getApplication(),R.string.check_your_internet_and_try_again,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * This method will edit the product details in API
    * */
    private void doneEditingInBackground(final String encodedImage, final String name, final String cp, final String sp, final String size, final String quantity){
        //show progressbar
        pd.show();

        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.EDIT_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide progressDialog
                parseDoneEditingResponse(response);
                Log.d(TAG,"HUS: doneEditingInBackground "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //Handle Volley error
                error.printStackTrace();
                Log.e(TAG,"HUS: message "+error.getMessage()+"/cause "+error.getCause());

                String errorString = VolleySingleton.handleVolleyError(error);
                if(errorString != null){
                    Util.redSnackbar(getApplicationContext(),mLayout,errorString);
                }

                //Delete old product data from sqlite
                deleteOldProductData();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Keys.KEY_COM_PRODUCTID,mProductId);
                param.put(Keys.KEY_COM_CATEGORYID,mCategoryId);
                param.put(Keys.KEY_COM_USERID,mUserId);
                param.put(Keys.PRAM_AP_IMAGE,encodedImage);
                param.put(Keys.PRAM_AP_NAME,name);
                param.put(Keys.PRAM_AP_CODE,mProductCode);
                param.put(Keys.PRAM_AP_CP,cp);
                param.put(Keys.PRAM_AP_SP,sp);
                param.put(Keys.PRAM_AP_SIZE,size);
                param.put(Keys.PRAM_AP_QUANTITY,quantity);
                return param;
            }
        };

        mRequestQueue.add(request);
    }

    private void parseDoneEditingResponse(String response) {
        List<SimplePojo> list = JsonParser.simpleParser(response);
        if(list != null){
            SimplePojo current = list.get(0);
            if(current.getReturned()){
                //success
                Toast.makeText(getApplicationContext(),current.getMessage(),Toast.LENGTH_LONG).show();

                //Delete old product data from sqlite
                deleteOldProductData();

                //Start Product activity
                Intent productIntent = new Intent(getApplication(),ProductActivity.class);
                productIntent.putExtra(Keys.KEY_COM_PRODUCTID, mProductId);
                productIntent.putExtra(Keys.KEY_PL_NAME, mProductName.getText().toString().trim());
                //Clear task
                productIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(productIntent);
            }else{
                //failed
                Util.redSnackbar(getApplicationContext(),mLayout,current.getMessage());
            }
        }else{
            Toast.makeText(getApplicationContext(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to delete old product data from SQLITE
    * */
    private void deleteOldProductData(){
        //Delete old product data from SQLite database
        db.deleteProduct(mUserId,mProductId);
        db.deleteSQ(mUserId, mProductId);
        db.deleteProductUri(mUserId,mProductId);
    }

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
