package com.hackerkernel.storemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.adapter.ACProductAdapter;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SellActivity extends AppCompatActivity {
    private static final String TAG = SellActivity.class.getSimpleName();
    private final Context context = this;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.productSearch) AutoCompleteTextView productSearch;
    @Bind(R.id.toolbar_progress_bar) ProgressBar progressBar;
    @Bind(R.id.pName) TextView pName;
    @Bind(R.id.pCode) TextView pCode;
    @Bind(R.id.pDate) TextView pDate;
    @Bind(R.id.pSize) TextView pSize;
    @Bind(R.id.pQuantity) TextView pQuantity;
    @Bind(R.id.pProfit) TextView pProfit;
    @Bind(R.id.sizeHeader) TextView sizeHeader;
    @Bind(R.id.quantityHeader) TextView quantityHeader;
    @Bind(R.id.profitHeader) TextView profitHeader;
    @Bind(R.id.pImage) ImageView pImage;

    private String userId;
    private String productId;
    private String productName;
    private String productImageAddress;

    DataBase db;
    SingleProductPojo productPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.sell));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get the userId
        db = new DataBase(this);
        userId = db.getUserID();

        String productSearchText = productSearch.getText().toString().trim();

        final ACProductAdapter adapter = new ACProductAdapter(this,productSearchText,userId);
        productSearch.setAdapter(adapter);

        productSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the name of the item selected from AC
                productName = adapter.getItem(position).getName();
                //store productId
                productId = adapter.getItem(position).getId();
                //set item to dropdown
                productSearch.setText(productName);
                //fetch product
                getProductData(productId);
            }
        });
    }

    //method to fetch product
    public void getProductData(String productId){
        //product available in sqlite database
        if(db.checkProduct(productId)){
            //get data from SQlite database
            SingleProductPojo product = db.getProduct(productId);

            setProductData(product);
        }else{
            //fetch product from the Backend
            new DownloadProductTask().execute();
        }
    }

    //hookup product data to the views
    public void setProductData(SingleProductPojo product){
        //headers
        sizeHeader.setText(getString(R.string.size));
        quantityHeader.setText(getString(R.string.quantity));
        profitHeader.setText(getString(R.string.sp_cp_profit));
        //views
        pName.setText(product.getName());
        pCode.setText(product.getCode());
        pDate.setText(product.getTime());
        pSize.setText(product.getSize());
        pQuantity.setText(product.getQuantity());
        int profit = Integer.parseInt(product.getSp()) - Integer.parseInt(product.getCp());
        String profitStack = product.getSp() +" - "+ product.getCp() +" = "+profit;
        pProfit.setText(profitStack);

        /*
        * Code to set Product Image
        * */
        productImageAddress = product.getImageAddress();

        //product image is not empty
        //get Image from the web
        if (!productImageAddress.isEmpty()){
            try {
                Bitmap productImage = new DownloadImageTask().execute(productImageAddress).get();
                if(productImage != null){
                    //set image to imageView
                    pImage.setScaleType(ImageView.ScaleType.FIT_START);
                    pImage.setImageBitmap(productImage);

                    //save Image to sdcard
                    saveImageToSD(productImage);

                }else{
                    //if null is returned instead of bitmap from DownloadImageTask
                    Toast.makeText(getApplicationContext(),getString(R.string.unable_fetch_image),Toast.LENGTH_LONG).show();
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Log.e(TAG,"HUS: "+e);
            }
        }else{
            //display placeholder image
            pImage.setImageResource(R.drawable.placeholder_product);
        }
    }

    //method to save image to SDCard
    public void saveImageToSD(Bitmap bitmap){
        //check external storage
        //External storage available to Write
        if(Functions.isExternalStorageAvailable()){

            OutputStream output;
            String appName = getString(R.string.app_name);

            //1. Get the external storage directory
            File filePath = Environment.getExternalStorageDirectory();

            //2. Create our subdirectory
            File dir = new File(filePath.getAbsolutePath()+"/"+appName+"/");
            if(!dir.exists()){
                dir.mkdirs();
            }

            //3. Create file name
            String fileName = productName.replaceAll(" ","_").toLowerCase();

            //3. Create the file
            File file = new File(dir,"IMG_"+fileName+".jpg");

            //4. store image
            try{
                output = new FileOutputStream(file);

                //compress image
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,output);
                output.flush();
                output.close();

                Log.d(TAG,"HUS: Uri "+ Uri.fromFile(file));

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"HUS: "+e);
            }
            //4. return the Uri

        }else{
            //not Available
            Toast.makeText(getApplicationContext(), R.string.external_storage_prblm,Toast.LENGTH_LONG).show();
        }

    }

    //Fetch data from the backend
    private class DownloadProductTask extends AsyncTask<Void,Void,SingleProductPojo>{
        @Override
        protected void onPreExecute() {
            //show progressBar
            Functions.toggleProgressBar(progressBar);
        }

        @Override
        protected SingleProductPojo doInBackground(Void... params) {
            //make request to the web to fetch data
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userId",userId);
            hashMap.put("productId",productId);

            //convert hashmap into url encoded String
            String dataUrl = Functions.hashMapToEncodedUrl(hashMap);

            //fetch data from the Backend
            String jsonString = GetJson.request(DataUrl.GET_SINGLE_PRODUCT, dataUrl, "POST");

            //parse JSON and store results in productPojo
            productPojo = JsonParser.SingleProductParser(jsonString);
            return productPojo;
        }

        @Override
        protected void onPostExecute(SingleProductPojo product) {
            //hookup views
            setProductData(product);

            //store data in the database for later reference
            db.addProduct(product);

            //hide progressBar
            Functions.toggleProgressBar(progressBar);
        }
    }

    private class DownloadImageTask extends AsyncTask<String,Void,Bitmap>{

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
    }
}
