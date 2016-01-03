package com.hackerkernel.storemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.SimplePojo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{
    //Global varaible
    private static final String TAG = AddProductActivity.class.getSimpleName();
    private final Context context = AddProductActivity.this;

    private String  categoryId,
            categoryName,
            userId;
    private int RESULT_LOAD_IMAGE = 1;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.productImage) ImageView productImage;
    @Bind(R.id.productSelectImage) Button productSelectImage;
    @Bind(R.id.productName) EditText productName;
    @Bind(R.id.productCode) EditText productCode;
    @Bind(R.id.productCostPrice) EditText productCP;
    @Bind(R.id.productSellingPrice) EditText productSP;
    @Bind(R.id.productSize) EditText productSize;
    @Bind(R.id.productQuantity) EditText productQuantity;
    @Bind(R.id.loadMore) Button loadMore;

    //layout
    @Bind(R.id.productSizeLayout) LinearLayout sizeLayout;
    @Bind(R.id.productQuantityLayout) LinearLayout quantityLayout;

    //list
    List<EditText> sizeList;
    List<EditText> quantityList;
    List<SimplePojo> productList; //list to store Return json abjects
    //ProgressDialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.add_product));

        //get categoryId , categoryName & userId from intent
        categoryId = getIntent().getExtras().getString("categoryId");
        categoryName = getIntent().getExtras().getString("categoryName");
        userId = getIntent().getExtras().getString("userId");

        //set the size & quantity list
        sizeList = new ArrayList<>();
        quantityList = new ArrayList<>();

        //add first size and Quantity EditText to ArrayList
        sizeList.add(productSize);
        quantityList.add(productQuantity);

        //set OnClicklistener
        productSelectImage.setOnClickListener(this);
        loadMore.setOnClickListener(this);

        //Create a progressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.loadMore: //when load more button is clicked
                    loadMore();
                break;
            case R.id.productSelectImage: //when select image button is clicked
                    selectImageFromGallery();
                break;
        }
    }

    //when select image button is clicked open gallery
    private void selectImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
    }

    /*
    * When image is selected from the gallery
    * set image to productImageView
    * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            productImage.setImageURI(selectedImage);
        }
    }

    private void loadMore() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //create a new Size & Quantity EditText
        EditText size = new EditText(AddProductActivity.this);
        EditText quantity = new EditText(AddProductActivity.this);

        //set their width and height
        size.setLayoutParams(layoutParams);
        quantity.setLayoutParams(layoutParams);

        //set inputType
        size.setInputType(InputType.TYPE_CLASS_NUMBER);
        quantity.setInputType(InputType.TYPE_CLASS_NUMBER);

        //set their hint
        size.setHint(getString(R.string.size));
        quantity.setHint(getString(R.string.quantity));

        //add to list
        sizeList.add(size);
        quantityList.add(quantity);

        //append to layout
        sizeLayout.addView(size);
        quantityLayout.addView(quantity);
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
                    addProduct();
                break;
            case R.id.action_cancel: //when cancel button is pressed
                //close AddProductActivty
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addProduct() {
        //get all the texts from the fields
        String  name = productName.getText().toString().trim(),
                code = productCode.getText().toString().trim(),
                cp = productCP.getText().toString().trim(),
                sp = productSP.getText().toString().trim();

        /*
        * Get Product Image if user has selected a image
        * */
        String encodedImage = "";
        if(productImage.getDrawable() != null){
            //get the image from ImageView and store it in a Bitmap
            Bitmap bitmapImage = ((BitmapDrawable) productImage.getDrawable()).getBitmap();

            //compress the image into 70% quality
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);

            //convert image to  Base64 encoded string
            encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }

        // if any field is empty
        if(name.isEmpty() || code.isEmpty() || cp.isEmpty() || sp.isEmpty()){
            Functions.errorAlert(context,getString(R.string.oops),getString(R.string.fillin_all_fields));
            return;
        }

        //check name is more then 3 char long
        if(name.length() < 3){
            Functions.errorAlert(context,getString(R.string.oops),getString(R.string.name_more_then_2));
            return;
        }

        //check code
        if(code.length() < 3){
            Functions.errorAlert(context,getString(R.string.oops),getString(R.string.pcode_more_then_2));
            return;
        }

        //generate a StringBuilder for size & quantity
        StringBuilder sizeBuilder = new StringBuilder();
        StringBuilder quantityBuilder = new StringBuilder();

        //check size & quantity
        for (int i = 0; i < sizeList.size(); i++) {
            String size = sizeList.get(i).getText().toString().trim();
            String quantity = quantityList.get(i).getText().toString().trim();

            if (size.isEmpty() || quantity.isEmpty()){
                //display error
                Functions.errorAlert(context,getString(R.string.oops),getString(R.string.size_quantity_canot_empty));
                return;
            }else{
                //add to StringBuilder
                sizeBuilder.append(size).append(",");
                quantityBuilder.append(quantity).append(",");
            }
        }

        /*
        * IF all goes well we will reach here
        * Create a Hashmap to store Data which we will send to the server
        * */
        HashMap<String,String> addProductHashMap = new HashMap<>();
        addProductHashMap.put("categoryName",categoryName);
        addProductHashMap.put("categoryId",categoryId);
        addProductHashMap.put("userId",userId);
        addProductHashMap.put("pImage",encodedImage);
        addProductHashMap.put("pName",name);
        addProductHashMap.put("pCode",code);
        addProductHashMap.put("pCP",cp);
        addProductHashMap.put("pSP",sp);
        addProductHashMap.put("pSize",sizeBuilder.toString());
        addProductHashMap.put("pQuantity",quantityBuilder.toString());

        //convert addProductHashMap into encode url
        String dataUrl = Functions.hashMapToEncodedUrl(addProductHashMap);

        //Call the addProductTask and send data to PHP backend
        new addProductTask().execute(dataUrl);
    }

    //Class to add product to server
    private class addProductTask extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show(); //show ProgressDialog
        }

        @Override
        protected String doInBackground(String... params) {
            return GetJson.request(ApiUrl.ADD_PRODUCT,params[0],"POST");
        }

        @Override
        protected void onPostExecute(String jsonString) {
            //parse json and store it in a list
            productList = JsonParser.SimpleParse(jsonString);

            SimplePojo productPojo = productList.get(0);

            pd.dismiss();

            //success
            if (productPojo.getReturned()){

                //show a success toast message
                Toast.makeText(context,productPojo.getMessage(),Toast.LENGTH_LONG).show();

                //restart activity
                Intent restartIntent = getIntent();
                finish();
                startActivity(restartIntent);

            }else{ //failed
                Functions.errorAlert(context,getString(R.string.oops),productPojo.getMessage());
            }
        }
    }
}
