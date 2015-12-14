package com.hackerkernel.storemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.adapter.ACProductAdapter;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;

import java.util.HashMap;

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

    private String userId;
    private String productId;

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
                String name = adapter.getItem(position).getName();
                //store productId
                productId = adapter.getItem(position).getId();
                //set item to dropdown
                productSearch.setText(name);
                //fetch product
                getProductData(productId);
            }
        });
    }

    //method to fetch product
    public void getProductData(String productId){
        //product available in sqlite database
        if(db.checkProduct(productId)){
            //get data from Sqlite database
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
}
