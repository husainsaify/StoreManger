package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;
import com.hackerkernel.storemanager.storage.Database;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = ProductActivity.class.getSimpleName();

    private String mProductId;
    private String userId;
    private Bitmap imageBitmap;
    private Uri productImageUri;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.pName) TextView productName;
    @Bind(R.id.pCode) TextView productCode;
    @Bind(R.id.pTimeAgo) TextView productTimeAgo;
    @Bind(R.id.pSize) TextView productSize;
    @Bind(R.id.pQuantity) TextView productQuantity;
    @Bind(R.id.pProfit) TextView productProfit;

    //list
    List<SimplePojo> deleteProductList;

    //Pojo for product
    SingleProductPojo productPojo;
    ProgressDialog pd;
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        ButterKnife.bind(this);

        //get userId
        db = new Database(this);
        //userId = db.getUserID();

        //Toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Hello");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                    imageView.setImageURI(imageUri);
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
            imageView.setImageResource(R.drawable.placeholder_product);
            Log.d(TAG, "HUS: showing placeholder image");
        }*/

        //create ProgressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));

        /*
        * Check Product exits in local database
        * if exits Fetch data and display in views
        * if not exits fetch it from Backend and then store it in local database
        * */

        /*if(db.checkProduct(mProductId)){ //product exits in local database
            //fetch product
            SingleProductPojo fetchedProduct = db.getProduct(mProductId);

            //set "SingleProductPojo" to views
            setProductViews(fetchedProduct);
        }else{
            //fetch product from backend
            new getProductTask().execute();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_product,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
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

    //method to view "SingleProductPojo to views"
    private void setProductViews(SingleProductPojo product){
        //set Views
        productName.setText(product.getName());
        productCode.setText(product.getCode());
        productTimeAgo.setText(product.getTime());
        //find out profit
        int cp = Integer.parseInt(product.getCp());
        int sp = Integer.parseInt(product.getSp());
        int profit = sp - cp;

        String profitStack = sp + " - "+ cp + " = "+profit;

        productProfit.setText(profitStack);
        productSize.setText(product.getSize());
        productQuantity.setText(product.getQuantity());
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
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(imageBitmap);

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
    class getProductTask extends AsyncTask<String,String,SingleProductPojo>{
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected SingleProductPojo doInBackground(String... params) {
            //make request to the web to fetch data
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userId",userId);
            hashMap.put("productId",mProductId);

            //convert hashmap into url encoded String
            String dataUrl = Functions.hashMapToEncodedUrl(hashMap);

            //fetch data from the Backend
            String jsonString = GetJson.request(ApiUrl.GET_SINGLE_PRODUCT,dataUrl,"POST");

            //parse JSON and store results in productPojo
            productPojo = JsonParser.SingleProductParser(jsonString);

            return productPojo;
        }

        @Override
        protected void onPostExecute(SingleProductPojo product) {
            //success
            if (product.getReturned()){

                //set "SingleProductPojo" to view
                setProductViews(product);
            }else{
                //Display the Error to user
                Functions.errorAlert(context,getString(R.string.oops),product.getMessage());
            }
            pd.dismiss();

            //add this product to the Local SQLite database
            db.addProduct(productPojo);

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
                db.addSQ(size,quantity,userId,mProductId);
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
