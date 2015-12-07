package com.hackerkernel.storemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewProductActivity extends AppCompatActivity {
    private static final String TAG = ViewProductActivity.class.getSimpleName();
    private final Context context = this;

    private String  pName,
                    pCode,
                    pId,
                    pImageAddress,
                    userId;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.pName) TextView productName;
    @Bind(R.id.pCode) TextView productCode;
    @Bind(R.id.pTimeAgo) TextView productTimeAgo;
    @Bind(R.id.pSize) TextView productSize;
    @Bind(R.id.pQuantity) TextView productQuantity;
    @Bind(R.id.pProfit) TextView productProfit;

    HashMap<String,String> productMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        ButterKnife.bind(this);

        pName = getIntent().getExtras().getString("pName");
        pCode = getIntent().getExtras().getString("pCode");
        pId = getIntent().getExtras().getString("pId");
        pImageAddress = getIntent().getExtras().getString("pImageAddress");

        //get userId
        DataBase db = new DataBase(this);
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

        //set views
        productName.setText(pName);
        productCode.setText(pCode);

        //fetch extra product data
        new getProductTask().execute();
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
                //Scale bitmap
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //fetch poroduct details
    class getProductTask extends AsyncTask<String,String,HashMap<String,String>>{

        @Override
        protected HashMap<String,String> doInBackground(String... params) {
            //make request to the web to fetch data
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userId",userId);
            hashMap.put("productId",pId);

            //convert hashmap into url encoded String
            String dataUrl = Functions.hashMapToEncodedUrl(hashMap);

            //fetch data from the Backend
            String jsonString = GetJson.request(DataUrl.GET_SINGLE_PRODUCT,dataUrl,"POST");

            //parse JSON
            productMap = JsonParser.SingleProductParser(jsonString);

            return productMap;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> hashMap) {
            if (hashMap.get("return").equals("true")){
                //set Views
                productTimeAgo.setText(hashMap.get("time"));
                //find out profit
                int cp = Integer.parseInt(hashMap.get("cp"));
                int sp = Integer.parseInt(hashMap.get("sp"));
                int profit = sp - cp;

                String profitStack = sp + " - "+ cp + " = "+profit;

                productProfit.setText(profitStack);
                productSize.setText(hashMap.get("size"));
                productQuantity.setText(hashMap.get("quantity"));
            }else{
                //Display the Error to user
                Functions.errorAlert(context,getString(R.string.oops),hashMap.get("message"));
            }
        }
    }
}
