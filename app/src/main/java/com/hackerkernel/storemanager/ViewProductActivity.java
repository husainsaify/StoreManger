package com.hackerkernel.storemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewProductActivity extends AppCompatActivity {
    private static final String TAG = ViewProductActivity.class.getSimpleName();
    private final Context context = this;

    private String pId;
    private String userId;
    private Bitmap imageBitmap;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.pName) TextView productName;
    @Bind(R.id.pCode) TextView productCode;
    @Bind(R.id.pTimeAgo) TextView productTimeAgo;
    @Bind(R.id.pSize) TextView productSize;
    @Bind(R.id.pQuantity) TextView productQuantity;
    @Bind(R.id.pProfit) TextView productProfit;

    //Pojo for product
    SingleProductPojo productPojo;
    ProgressDialog pd;
    DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        ButterKnife.bind(this);

        String pName = getIntent().getExtras().getString("pName");
        pId = getIntent().getExtras().getString("pId");
        String pImageAddress = getIntent().getExtras().getString("pImageAddress");

        //get userId
        db = new DataBase(this);
        userId = db.getUserID();

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(pName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //check Product Has a image or we have to Display a PlaceHolder Image
        if(!pImageAddress.isEmpty()){
            //fetch the Image and display it
            new getImageTask().execute(pImageAddress);
        }else{
            //Show the placeHolder image
            imageView.setImageResource(R.drawable.placeholder_product);
        }

        //create ProgressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));

        //check product exits in database or not
        if(db.productExits(pId)){
            Log.d(TAG,"HUS: product exits");

            //fetch product
            SingleProductPojo p = db.getProduct(pId);
            Log.d(TAG,"HUS: id "+p.getId());
            Log.d(TAG,"HUS: name "+p.getName());
            Log.d(TAG,"HUS: image address "+p.getImageAddress());
            Log.d(TAG,"HUS: code "+p.getCode());
            Log.d(TAG,"HUS: size "+p.getSize());
            Log.d(TAG,"HUS: quantity "+p.getQuantity());
            Log.d(TAG,"HUS: cp "+p.getCp());
            Log.d(TAG,"HUS: sp "+p.getSp());
            Log.d(TAG,"HUS: time "+p.getTime());
        }else{
            Log.d(TAG,"HUS: product does not exits");
        }

        //fetch extra product data
        //new getProductTask().execute();
    }

    //Fetch Image
    class getImageTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            //generate Image Full url
            String imageUrl = DataUrl.IMAGE_BASE_URL + params[0];
            //fetch Image from the server
            try {
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();

                //return image to "onPostExecute"
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
            }else{
                /*
                * Seams their is some issue is retrieving image
                * - store placeholder image (Drawable) into "imageBitmap" (Bitmap) global variable
                * - Throw and error Log & toast
                * */

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
            hashMap.put("productId",pId);

            //convert hashmap into url encoded String
            String dataUrl = Functions.hashMapToEncodedUrl(hashMap);

            //fetch data from the Backend
            String jsonString = GetJson.request(DataUrl.GET_SINGLE_PRODUCT,dataUrl,"POST");

            //parse JSON and store results in productPojo
            productPojo = JsonParser.SingleProductParser(jsonString);

            return productPojo;
        }

        @Override
        protected void onPostExecute(SingleProductPojo product) {
            //success
            if (product.getReturned()){
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
            }else{
                //Display the Error to user
                Functions.errorAlert(context,getString(R.string.oops),product.getMessage());
            }
            pd.dismiss();

            //add this product to the database
            db.addProduct(productPojo);
        }
    }
}
