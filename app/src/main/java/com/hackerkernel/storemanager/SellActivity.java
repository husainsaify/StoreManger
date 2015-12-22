package com.hackerkernel.storemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.adapter.ACProductAdapter;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    //size quantity price
    @Bind(R.id.size) EditText size;
    @Bind(R.id.quantity) EditText quantity;
    @Bind(R.id.price) EditText price;
    @Bind(R.id.sizeLayout) LinearLayout sizeLayout;
    @Bind(R.id.quantityLayout) LinearLayout quantityLayout;
    @Bind(R.id.priceLayout) LinearLayout priceLayout;
    @Bind(R.id.loadMore) Button loadMore;
    List<EditText> sizeList;
    List<EditText> quantityList;
    List<EditText> priceList;


    private String userId;
    private String productId = null;
    private String productName;
    private String productImageAddress;
    private Uri productImageUri;

    DataBase db;
    SingleProductPojo productPojo;

    ProgressDialog pd;

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

        //instanciate size/quantity/price list
        sizeList = new ArrayList<>();
        quantityList = new ArrayList<>();
        priceList = new ArrayList<>();

        // Add size/quantity/price editText to List
        sizeList.add(size);
        quantityList.add(quantity);
        priceList.add(price);

        //Create a progressDialog
        pd = new ProgressDialog(context);
        pd.setMessage(getString(R.string.pleasewait));

        //When Load more Button is clicked
        /*
        * generate a new Size/Quantity/price EditText
        * Add them to Their layout
        * Add them to their list
        * */
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore(); //call loadMore method
            }
        });
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
            case R.id.action_ok:
                    sellProduct();
                break;
            case R.id.action_cancel: //when cancel button is pressed
                //close AddProductActivty
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * This method will get all details from "sellActivity" and send it to the backend
    * */
    private void sellProduct() {
        //check productId is not null
        if(productId == null){
            Functions.errorAlert(context, getString(R.string.oops), getString(R.string.please_select_product));
            productSearch.setFocusable(true); //set productSearch to focus
            return;
        }

        //generate size | quantity | product stack for sending it backend
        StringBuilder sizeStack = new StringBuilder();
        StringBuilder quantityStack = new StringBuilder();
        StringBuilder priceStack = new StringBuilder();

        //check size | quantity | price is not
        for (int i = 0; i < sizeList.size(); i++) {
            //get the values of size | quantity | price
            String size = sizeList.get(i).getText().toString().trim();
            String quantity = quantityList.get(i).getText().toString().trim();
            String price = priceList.get(i).getText().toString().trim();

            //check size | quantity | price is not empty
            if(size.isEmpty() || quantity.isEmpty() || price.isEmpty()){
                Functions.errorAlert(context,getString(R.string.oops),getString(R.string.fillin_all_fields));
                return;
            }else{
                //Append values to size | product | quantity stack
                sizeStack.append(size).append(",");
                quantityStack.append(quantity).append(",");
                priceStack.append(price).append(",");
            }
        }

        //Call the SellProductTask to send all the values to Backend
        new SellProductTask().execute(sizeStack.toString(),quantityStack.toString(),priceStack.toString());
    }


    /*
    * This method will create 3 new EditText for size/quantity/product
    * and append them to layout
    *  */
    public void loadMore(){
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //create a new Size / Quantity / price EditText
        EditText size = new EditText(SellActivity.this);
        EditText quantity = new EditText(SellActivity.this);
        EditText price = new EditText(SellActivity.this);

        //set their width and height
        size.setLayoutParams(layoutParams);
        quantity.setLayoutParams(layoutParams);
        price.setLayoutParams(layoutParams);

        //set inputType to number
        size.setInputType(InputType.TYPE_CLASS_NUMBER);
        quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        price.setInputType(InputType.TYPE_CLASS_NUMBER);

        //set their hint
        size.setHint(getString(R.string.size));
        quantity.setHint(getString(R.string.quantity));
        price.setHint(getString(R.string.price_per_unit));

        //add to list
        sizeList.add(size);
        quantityList.add(quantity);
        priceList.add(price);

        //append to layout
        sizeLayout.addView(size);
        quantityLayout.addView(quantity);
        priceLayout.addView(price);
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

        //cal Profit , loss or break Even to display
        int sp = Integer.parseInt(product.getSp());
        int cp = Integer.parseInt(product.getCp());
        String value;
        int label;

        if(cp > sp){//loss
            //cal loss
            int loss = cp - sp;
            value = cp + " - " + sp + " = "+loss;
            label = R.string.sp_cp_loss;
        }else {
            if (sp > cp) { //profit
                //call profit
                int profit = sp - cp;
                value = cp + " - " + sp + " = "+profit;
                label = R.string.sp_cp_profit;
            } else { // neutral CP = sales
                value = cp + " - " + sp + " = "+0;
                label = R.string.sp_cp_breakeven;
            }
        }

        pProfit.setText(value);
        profitHeader.setText(getString(label));

        /*
        * Code to set Product Image
        * */
        productImageAddress = product.getImageAddress();

        //product image is not empty
        //get Image from the web
        if (!productImageAddress.isEmpty()){
            /*
            * Get the image from Sdcard is available
            * */

            //1. get image uri from database
            Uri uri = db.getProductImageUri(productId);

            //2. check image is avaialble in sdcard or not deleted
            if(uri != null){
                String imageUriString = String.valueOf(uri);
                File file = new File(URI.create(imageUriString).getPath());

                //3. if image available in sdcard
                if(file.exists()){
                    //4. Set image to view
                    pImage.setImageURI(uri);
                    Log.d(TAG, "HUS: image avaible in sdcard");
                }else{
                    //5. image not available in sdcard deleted from sdcard
                    new DownloadImageTask().execute(productImageAddress);
                    Log.d(TAG, "HUS: image not available in sdcard deleted");
                }

            }else{
                //download the image from the web
                new DownloadImageTask().execute(productImageAddress);
                Log.d(TAG, "HUS: no image uri stored");
            }
        }else{
            //display placeholder image
            pImage.setImageResource(R.drawable.placeholder_product);
            Log.d(TAG, "HUS: showing placeholder image");
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

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //set image to imageView
            pImage.setScaleType(ImageView.ScaleType.FIT_START);
            pImage.setImageBitmap(bitmap);

            //save Image to sdcard
            productImageUri = Functions.saveImageToSD(context,bitmap);
        }
    }

    private class SellProductTask extends AsyncTask<String,Void,SimplePojo>{
        @Override
        protected void onPreExecute() {
            //show progressDialog
            pd.show();
        }

        @Override
        protected SimplePojo doInBackground(String... params) {
            //Create a hashmap
            HashMap<String,String> hashmap = new HashMap<>();
            hashmap.put("user_id",userId);
            hashmap.put("product_id",productId);
            hashmap.put("size",params[0]);
            hashmap.put("quantity",params[1]);
            hashmap.put("price",params[2]);
            //convert it into a encoded url
            String data = Functions.hashMapToEncodedUrl(hashmap);

            String jsonString = GetJson.request(DataUrl.ADD_SELL, data, "POST");

            //parse Json
            List<SimplePojo> list = JsonParser.SimpleParse(jsonString);
            assert list != null;
            return list.get(0);
        }

        @Override
        protected void onPostExecute(SimplePojo pojo) {
            if (pojo.getReturned()){
                Toast.makeText(context,pojo.getMessage(),Toast.LENGTH_LONG).show();

                //Delete product from the Local database(so that we can fetch new)
                db.deleteProduct(productId);

                //restart activity
                Intent restartIntent = getIntent();
                finish();
                startActivity(restartIntent);

            }else{
                Functions.errorAlert(context,getString(R.string.oops),pojo.getMessage());
            }
            //hide progressDialog
            pd.hide();
        }
    }
}
