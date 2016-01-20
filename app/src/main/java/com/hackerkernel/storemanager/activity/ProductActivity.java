package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    @Bind(R.id.layout) LinearLayout mLayout;

    private String mProductId;
    private String mProductName;
    private String mUserId;
    private ProgressDialog pd;
    private Database db;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private Uri productImageUri;
    //list
    List<SimplePojo> deleteProductList;

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
        if(mUserId.equals(Keys.KEY_DEFAULT)){
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

        /*//check Product Has a image or we have to Display a PlaceHolder Image
        if(!pImageAddress.isEmpty()){

            //get the image Uri from the SQlite database
            Uri imageUri = db.getProductImageUri(mProductId);

            //display image which is stored in sdcard using uri
            if(imageUri != null){

                //check is image is available & not deleted from sdcard
                String imageUriString = String.valueOf(imageUri);
                File file = new File(URI.create(imageUriString).getPath());

                if(file.exists()){
                    //if file is in sdcard
                    mImage.setImageURI(imageUri);
                    Log.d(TAG,"HUS: image available in sdcard");
                }else{
                    //deleted from sdcard
                    new getImageTask().execute(pImageAddress); //fetch a new image from the web
                    Log.d(TAG, "HUS: image not available in sdcard deleted");
                }
            }else{
                //fetch the Image and display it
                new getImageTask().execute(pImageAddress);
                Log.d(TAG, "HUS: no image uri stored");
            }
        }else{
            //Show the placeHolder image
            mImage.setImageResource(R.drawable.placeholder_product);
            Log.d(TAG, "HUS: showing placeholder image");
        }*/

        /*
        * Check Product exits in local database
        * if exits Fetch data and display in views
        * if not exits fetch it from Backend and then store it in local database
        * */

        /*if(db.checkProduct(mProductId)){ //product exits in local database
            //fetch product
            ProductPojo fetchedProduct = db.getProduct(mProductId);

            //set "ProductPojo" to views
            setProductViews(fetchedProduct);
        }else{
            //fetch product from backend
            new getProductTask().execute();
        }*/
    }

    /*
    * Method will check internet
    * if avaiable - download a fresh product
    * if not - get from Sqlite database and show snackbar
    * */
    private void checkInternetAndDisplay(){
        if(Util.isConnectedToInternet(getApplication())){
            fetchProductInBackground(); //get product from API
        }else{
            Util.noInternetSnackbar(getApplication(),mLayout);

            //Check Product is Available in Local database or not
            if(db.checkProduct(mUserId,mProductId)){
                //Get product from database
                ProductPojo productPojo = db.getProduct(mUserId, mProductId);
                //set the views
                setProductViews(productPojo);
            }else{
                Toast.makeText(getApplication(), R.string.unable_display_product_info_check_inetnet,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchProductInBackground(){
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Parse the response
                parseProductResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.KEY_COM_USERID,mUserId);
                params.put(Keys.KEY_PL_ID,mProductId);
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
            if(jo.getBoolean(Keys.KEY_COM_RETURN)){

                //Parse the response
                ProductPojo productPojo = JsonParser.productParser(response);

                //Check Response is valid
                if(productPojo != null){
                    //setView with Response
                    setProductViews(productPojo);
                    //Download image
                    downloadImage(productPojo.getImageAddress());

                    //Delete Product
                    db.deleteProduct(mUserId, mProductId);
                    //Delete size & Quantity
                    db.deleteSQ(mUserId,mProductId);

                    //Insert Product in SQlite database
                    db.insertProduct(productPojo);
                    //Insert size and Quantity
                    insertSizeQuantityInLocalDatabase(productPojo.getSize(),productPojo.getQuantity());

                }else{
                    Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
                }
            }else{
                //return is false show error
                Util.redSnackbar(getApplication(),mLayout,jo.getString(Keys.KEY_COM_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to Download image
    * */
    public void downloadImage(String imageAddress){
        if(!imageAddress.isEmpty()){
            String imageUrl = ApiUrl.IMAGE_BASE_URL + imageAddress;
            mImageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    mImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG,"HUS: ImageLoading: "+error.getMessage());
                    Toast.makeText(getApplication(),R.string.unable_load_image,Toast.LENGTH_LONG).show();
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

        String profitStack = sp + " - "+ cp + " = "+profit;

        mProfit.setText(profitStack);
    }

    /*
    * Method to store Size & Quantity in local SQlite database
    * Convert Size/Quantity stack (3\n3\n4\n)
    * into array and store it in database
    * */
    public void insertSizeQuantityInLocalDatabase(String sizeStack,String quantityStack){
        String[] sizeArray = sizeStack.split("\n");
        String[] quantityArray = quantityStack.split("\n");

        for (int i = 0; i < sizeArray.length; i++) {
            String size = sizeArray[i];
            String quantity = quantityArray[i];

            //insert into db
            db.insertSQ(size, quantity, mUserId, mProductId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                //deleteProduct();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*private void deleteProduct() {
        *//*
        * Delete the product
        * - Local SQLite database
        * - Backend
        * *//*

        //check product is store in database and then delete it
        if(db.checkProduct(mProductId)){
            //delete it from SQLite database
            int result = db.deleteProduct(mProductId);
            if(result == 1){
                //delete product from the Backend
                new deleteProductTask().execute();
            }else{
                Toast.makeText(context,getString(R.string.error_in_deleting_product),Toast.LENGTH_LONG).show();
            }

        }
    }

    //method to view "ProductPojo to views"
    private void setProductViews(ProductPojo product){
        //set Views
        productName.setText(product.getName());
        mCode.setText(product.getCode());
        mTime.setText(product.getTime());
        //find out profit
        int cp = Integer.parseInt(product.getCp());
        int sp = Integer.parseInt(product.getSp());
        int profit = sp - cp;

        String profitStack = sp + " - "+ cp + " = "+profit;

        mProfit.setText(profitStack);
        mSize.setText(product.getSize());
        mQuantity.setText(product.getQuantity());
    }


    //Fetch Image
    class getImageTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            //generate Image Full url
            String imageUrl = ApiUrl.IMAGE_BASE_URL + params[0];
            //fetch Image from the server
            try {
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();

                //return image to "onPostExecute"
                Log.d(TAG,"HUS: downloded bitmap");
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                //store bitmap in "imageBitmap" global variable for later reference
                imageBitmap = bitmap;
                //display the image
                mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImage.setImageBitmap(imageBitmap);

                *//*
                * Store image to sdCard
                * save image Uri to global varaible "productImageUri"
                * so that we can store the image Uri in db
                * *//*

                productImageUri = Functions.saveImageToSD(context,imageBitmap);

                //add ProductImageUri to database
                db.addProductImageUri(mProductId,productImageUri);
            }else{
                *//*
                * Seams their is some issue is retrieving image
                * - store placeholder image (Drawable) into "imageBitmap" (Bitmap) global variable
                * - Throw and error Log & toast
                * *//*

                imageBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.placeholder_product);

                Toast.makeText(context,getString(R.string.unable_fetch_image),Toast.LENGTH_SHORT).show();
                Log.e(TAG,"HUS: Unable to fetch image");
            }
        }

    }

    //fetch product details
    class getProductTask extends AsyncTask<String,String,ProductPojo>{
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected ProductPojo doInBackground(String... params) {
            //make request to the web to fetch data
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userId",userId);
            hashMap.put("productId",mProductId);

            //convert hashmap into url encoded String
            String dataUrl = Functions.hashMapToEncodedUrl(hashMap);

            //fetch data from the Backend
            String jsonString = GetJson.request(ApiUrl.GET_PRODUCT,dataUrl,"POST");

            //parse JSON and store results in productPojo
            productPojo = JsonParser.productParser(jsonString);

            return productPojo;
        }

        @Override
        protected void onPostExecute(ProductPojo product) {
            //success
            if (product.getReturned()){

                //set "ProductPojo" to view
                setProductViews(product);
            }else{
                //Display the Error to user
                Functions.errorAlert(context,getString(R.string.oops),product.getMessage());
            }
            pd.dismiss();

            //add this product to the Local SQLite database
            db.insertProduct(productPojo);

            *//*
            * Get Size & Quantity from "productPojo"
            * and store them in `SQ` Local database
            * Convert This 2,3,4
            * into
            * Array 2
            *       3
            *       4
            * *//*
            String sizeArray[] = productPojo.getSize().split("\n");
            String quantityArray[] = productPojo.getQuantity().split("\n");

            for (int i = 0; i < sizeArray.length; i++) {
                String size = sizeArray[i];
                String quantity = quantityArray[i];

                //insert into db
                db.insertSQ(size,quantity,userId,mProductId);
            }
        }
    }

    //class to delete product
    class deleteProductTask extends AsyncTask<Void,Void,SimplePojo>{

        @Override
        protected SimplePojo doInBackground(Void... params) {
            //create a hashmap of Productid & userid
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userId",userId);
            hashMap.put("productId",mProductId);

            //convert hashmap to encoded url
            String data = Functions.hashMapToEncodedUrl(hashMap);

            String jsonString = GetJson.request(ApiUrl.DELETE_PRODUCT,data,"POST");

            //parse json
            deleteProductList = JsonParser.SimpleParse(jsonString);

            //parse result into pojo
            return deleteProductList.get(0);
        }

        @Override
        protected void onPostExecute(SimplePojo deletePojo) {
            //check result
            if (deletePojo.getReturned()){
                //success
                Toast.makeText(context,getString(R.string.product_deleted_successfully),Toast.LENGTH_SHORT).show();
                //exit the activity
                finish();
            }else{
                //error
                Functions.errorAlert(context,getString(R.string.oops),deletePojo.getMessage());
            }
        }
    }*/

}
