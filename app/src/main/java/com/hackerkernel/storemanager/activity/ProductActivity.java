package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = ProductActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.imageView) ImageView mImage;
    @Bind(R.id.pName) TextView mName;
    @Bind(R.id.pCode) TextView mCode;
    @Bind(R.id.pTimeAgo) TextView mTime;
    @Bind(R.id.pSize) TextView mSize;
    @Bind(R.id.pQuantity) TextView mQuantity;
    @Bind(R.id.pProfit) TextView mProfit;
    @Bind(R.id.layout) CoordinatorLayout mLayout;

    private String mProductId;
    private String mProductName;
    private String mUserId;
    private ProgressDialog pd;
    private Database db;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);

        //get the ProductId & productName
        if (getIntent().hasExtra(Keys.KEY_PL_ID) && getIntent().hasExtra(Keys.KEY_PL_NAME)) {
            mProductId = getIntent().getExtras().getString(Keys.KEY_PL_ID);
            mProductName = getIntent().getExtras().getString(Keys.KEY_PL_NAME);
        } else {
            Toast.makeText(getApplication(), R.string.internal_error_restart_app, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //Toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mProductName);

        //Get userId and check it
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();
        if (mUserId.equals(Keys.KEY_DEFAULT)) {
            Toast.makeText(getApplication(), R.string.internal_error_restart_app, Toast.LENGTH_LONG).show();
            return;
        }

        //Instantiate Database
        db = new Database(this);

        //create ProgressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);

        //Instanciate Volley
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mImageLoader = VolleySingleton.getInstance().getImageLoader();

        checkInternetAndDisplay();
    }

    /*
    * Method will check internet
    * if avaiable - download a fresh product
    * if not - get from Sqlite database and show snackbar
    * */
    private void checkInternetAndDisplay() {
        if (Util.isConnectedToInternet(getApplication())) {
            fetchProductInBackground(); //get product from API
        } else {
            Util.noInternetSnackbar(getApplication(), mLayout);

            //Check Product is Available in Local database or not
            if (db.checkProduct(mUserId, mProductId)) {
                //Get product from database
                ProductPojo productPojo = db.getProduct(mUserId, mProductId);

                //set the views
                setProductViews(productPojo);

                //Call setImage Method to set the image if Available in SdCard
                setImage(productPojo.getImageAddress());
            } else {
                Toast.makeText(getApplication(), R.string.unable_display_product_info_check_inetnet, Toast.LENGTH_LONG).show();
            }
        }
    }


    private void fetchProductInBackground() {
        pd.show(); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide progressbar
                //Parse the response
                parseProductResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss(); //hide progressbar

                Log.e(TAG,"HUS: fetchProductInBackground, Volley "+error.getMessage());

                //Handle Volley Error
                String errorString = VolleySingleton.handleVolleyError(error);
                if(errorString != null){
                    Util.redSnackbar(getApplication(),mLayout,errorString);
                }

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

    /*
    * Method to parse product response
    * */
    private void parseProductResponse(String response) {
        try {
            JSONObject jo = new JSONObject(response);
            //Check Response return in true or false
            if (jo.getBoolean(Keys.KEY_COM_RETURN)) {

                //Parse the response
                ProductPojo productPojo = JsonParser.productParser(response);

                //Check Response is valid
                if (productPojo != null) {
                    //setView with Response
                    setProductViews(productPojo);

                    //method to setImage
                    setImage(productPojo.getImageAddress());

                    //Delete Product
                    db.deleteProduct(mUserId, mProductId);
                    //Delete size & Quantity
                    db.deleteSQ(mUserId, mProductId);

                    //Insert Product in SQlite database
                    db.insertProduct(productPojo);
                    //Insert size and Quantity
                    insertSizeQuantityInLocalDatabase(productPojo.getSize(), productPojo.getQuantity());

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
    * CHeck image path is avaialble in the database or not
    * if avaiable show the image from the database
    * if not
    *       check internet
    *       if internet is avaiable download a new image display it and store in db
    *       else show default image
    * */
    public void setImage(String imageAddress){
        if(db.checkProductUri(mUserId, mProductId)){
            Log.d(TAG, "HUS: setImage - uri avaiable in database");

            //Get uri from database
            Uri imageUri = db.getProductUri(mUserId, mProductId);
            String imageUriString = String.valueOf(imageUri);

            Log.d(TAG,"HUS: uri: "+imageUri);
            Log.d(TAG,"HUS: uriString: "+imageUriString);

            //check is image is available & not deleted from sdcard
            File file = new File(URI.create(imageUriString).getPath());

            if(file.exists()){
                //if file is in sdcard
                mImage.setImageURI(imageUri);
                Log.d(TAG, "HUS: setImage - image available in sdcard");
            }else{ //image is deleted, Download a new image if internet is availabe
                Log.d(TAG,"HUS: setImage - image not available in sdcard");
                checkInternetAndDownloadImage(imageAddress);
            }
        }else{
            Log.d(TAG, "HUS: setImage - uri not avaiable in database");
            //If URI is null
            checkInternetAndDownloadImage(imageAddress);
        }
    }

    /*
    * Method to check internet and download image
    * if internet is avaialble
    * else Show a Toast message
    * */
    private void checkInternetAndDownloadImage(String imageAddress){
        if(Util.isConnectedToInternet(getApplication())){ //internet avaialble
            downloadImage(imageAddress);
            Log.d(TAG,"HUS: checkInternetAndDownloadImage - internet avaialble");
        }else{ //internet not avaialbe
            Toast.makeText(getApplication(), R.string.check_your_internet_and_try_again,Toast.LENGTH_LONG).show();
            Log.d(TAG, "HUS: checkInternetAndDownloadImage - internet not avaialble");
        }
    }

    /*
    * Method to Download image
    * */
    public void downloadImage(String imageAddress) {
        if (!imageAddress.isEmpty()) {
            Log.d(TAG, "HUS: downloadImage - image download");
            String imageUrl = ApiUrl.IMAGE_BASE_URL + imageAddress;
            mImageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    mImage.setImageBitmap(response.getBitmap());

                    Bitmap bitmap = response.getBitmap();

                    //check if bitmap is not null
                    if (bitmap != null) {
                        Log.d(TAG, "HUS: Bitmap not null");
                        //Store image in Sdcard
                        Uri imageUri = Util.saveImageToExternalStorage(getApplication(), bitmap);

                        //Store image Uri in Local Sqlite database
                        db.insertProductUri(mUserId, mProductId, imageUri);
                    } else {
                        Log.d(TAG, "HUS: Bitmap is null");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "HUS: ImageLoading: " + error.getMessage());
                    Toast.makeText(getApplication(), R.string.unable_load_image, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /*
    * Method to set all the view in the ProductActivity
    * */
    private void setProductViews(ProductPojo product) {
        mName.setText(product.getName());
        mCode.setText(product.getCode());
        mTime.setText(product.getTime());
        mSize.setText(product.getSize());
        mQuantity.setText(product.getQuantity());

        //Set Profit View
        int cp = Integer.parseInt(product.getCp());
        int sp = Integer.parseInt(product.getSp());
        int profit = sp - cp;

        String profitStack = sp + " - " + cp + " = " + profit;

        mProfit.setText(profitStack);
    }

    /*
    * Method to store Size & Quantity in local SQlite database
    * Convert Size/Quantity stack (3\n3\n4\n)
    * into array and store it in database
    * */
    public void insertSizeQuantityInLocalDatabase(String sizeStack, String quantityStack) {
        String[] sizeArray = sizeStack.split("\n");
        String[] quantityArray = quantityStack.split("\n");

        for (int i = 0; i < sizeArray.length; i++) {
            String size = sizeArray[i];
            String quantity = quantityArray[i];

            //insert into db
            db.insertSQ(size, quantity, mUserId, mProductId);
        }
    }

    /*
    * method to refresh product info
    * if internet is avaialble
    * else show a Toast no internet
    * */
    public void refreshProduct(){
        //internet is available
        if(Util.isConnectedToInternet(getApplication())){
            fetchProductInBackground();
        }else{ //internet not avaialble
            Toast.makeText(getApplication(),R.string.check_your_internet_and_try_again,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to delete Product
    * */
    public void deleteProduct() {
        //Check internet Connection
        if(Util.isConnectedToInternet(getApplication())){
            //Create ALertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
            builder.setTitle(R.string.delete)
                    .setMessage(getString(R.string.are_your_sure_you_want_to_delete_this))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Delete Product From SQLite
                            if(db.checkProduct(mUserId, mProductId)){
                                int result = db.deleteProduct(mUserId,mProductId);
                                if(result == 1){
                                    //delete product from the API Backend
                                    deleteProductInBackground();
                                }else{
                                    Toast.makeText(getApplication(),getString(R.string.error_in_deleting_product),Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    })
                    .setNegativeButton(getString(R.string.no), null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{ //Not avaialble
            Toast.makeText(getApplication(),R.string.check_your_internet_and_try_again,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * method to delete product from the API in background
    * */
    private void deleteProductInBackground(){
        pd.show(); //show progressbar
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.DELETE_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide progressbar
                //Parse Delete response
                parseDeleteResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss(); //hide progressbar
                Log.e(TAG,"HUS: deleteProductInBackground, VOlley "+error.getMessage());
                //Handle Volley error
                String errorString = VolleySingleton.handleVolleyError(error);
                if(errorString != null){
                    Util.redSnackbar(getApplication(),mLayout,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID,mUserId);
                params.put(Keys.KEY_COM_PRODUCTID,mProductId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Parse delete response
    * */
    private void parseDeleteResponse(String response) {
        List<SimplePojo> list = JsonParser.simpleParser(response);
        if(list != null){
           SimplePojo current = list.get(0);
            //Check (Return) Response is true or false
            if(current.getReturned()){
                //End the activity
                startActivity(new Intent(ProductActivity.this,HomeActivity.class));
                finish();
                Toast.makeText(getApplication(),R.string.product_deleted_successfully,Toast.LENGTH_LONG).show();
            }else{
                Util.redSnackbar(getApplication(),mLayout,current.getMessage());
            }
        }else{
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                 refreshProduct();
                break;
            case R.id.action_delete:
                deleteProduct();
                break;
            case R.id.action_edit:
                //start edit activity
                Intent edit = new Intent(getApplication(),EditProductActivity.class);
                edit.putExtra(Keys.KEY_COM_PRODUCTID,mProductId);
                startActivity(edit);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
