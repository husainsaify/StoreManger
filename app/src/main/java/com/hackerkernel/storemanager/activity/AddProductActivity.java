package com.hackerkernel.storemanager.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{
    //Global varaible
    private static final String TAG = AddProductActivity.class.getSimpleName();


    private int TAKE_PICTURE = 1; //camera
    private int CHOSE_PICTURE = 2; //gallery

    @Bind(R.id.linearLayout) LinearLayout mLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.productImage) ImageView mProductImage;
    @Bind(R.id.categorySpinner) Spinner mCategorySpinner;
    @Bind(R.id.productName) EditText mProductName;
    @Bind(R.id.productCode) EditText mProductCode;
    @Bind(R.id.productCostPrice) EditText mProductCP;
    @Bind(R.id.productSellingPrice) EditText mProductSP;
    @Bind(R.id.productSizeLayout) LinearLayout mSizeLayout;
    @Bind(R.id.productQuantityLayout) LinearLayout mQuantityLayout;
    @Bind(R.id.productDeleteLayout) LinearLayout mDeleteLayout;
    @Bind(R.id.productSize) EditText mProductSize;
    @Bind(R.id.productQuantity) EditText mProductQuantity;
    @Bind(R.id.productDelete) Button mProductDelete;
    @Bind(R.id.addProduct) Button mAddProduct;


    private String mUserId;
    private List<SimpleListPojo> mCategorySimpleList;
    private List<String> mCategoryStringList;
    private Database db;
    private String mCategoryId;
    private String mCategoryName;
    private ProgressDialog pd;
    //list to store (size,Qunatity and delete refernce)
    private List<EditText> mSizeList;
    private List<EditText> mQuantityList;
    private List<Button> mDeleteList;
    private List<SimplePojo> productList; //list to store Return json abjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        //set toolbar
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.add_product));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Username From SharedPreferences
        mUserId = MySharedPreferences.getInstance(getApplication()).getUserId();

        //Initialize Database
        db = new Database(getApplication());

        //Set OnClickMethod on productImage(Camera icon)
        mProductImage.setOnClickListener(this);

        //Set Delete button background transparent & OnClickMethod & its tag
        mProductDelete.setBackgroundColor(Color.TRANSPARENT);
        mProductDelete.setOnClickListener(deleteBtnClick);
        mProductDelete.setTag(1);

        //setup category Spinner
        setUpCategorySpinner();

        /*
        * Check intent has send categoryId & categoryName
        * if yes store them in Member variables
        * */
        if(getIntent().hasExtra("categoryId") && getIntent().hasExtra("categoryName")){
            mCategoryId = getIntent().getExtras().getString("categoryId");
            mCategoryName = getIntent().getExtras().getString("categoryName");
            //Set spinner to category Id
            setSpinnerPostionToCategoryID();
        }

        //When An item is selected in spinner
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Store CategoryId & categoryName
                mCategoryId = mCategorySimpleList.get(position).getId();
                mCategoryName = mCategorySimpleList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*
        * Store Size, Quantity and delete in their List
        * */
        mSizeList = new ArrayList<>();
        mQuantityList = new ArrayList<>();
        mDeleteList = new ArrayList<>();
        mSizeList.add(mProductSize);
        mQuantityList.add(mProductQuantity);
        mDeleteList.add(mProductDelete);

        //Create a progressDialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.pleasewait));
        pd.setCancelable(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //when Camera image is clicked open ChosePicture alertDialog
            case R.id.productImage:
                openSelectPictureDialog();
                break;
        }
    }

    /*
    * Method to dynamically add Size, Quantity and Delete
    * */
    private void loadMore() {
        //Set delete button
        Button delete = new Button(getApplication());
        delete.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //set icon and background to button
        delete.setBackgroundColor(Color.TRANSPARENT);
        delete.setGravity(Gravity.CENTER);

        //setTag to the button
        delete.setTag(mDeleteList.size() + 1);
        delete.setOnClickListener(deleteBtnClick);

        //set icon
        Drawable icon = ContextCompat.getDrawable(getApplication(),R.drawable.ic_delete_black);
        delete.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        //set size & quantity
        EditText size = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style, null); // Magic!
        EditText quantity = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style, null);

        //add to list
        mSizeList.add(size);
        mQuantityList.add(quantity);
        mDeleteList.add(delete);

        //append to layout
        mSizeLayout.addView(size);
        mQuantityLayout.addView(quantity);
        mDeleteLayout.addView(delete);
    }


    /*
    * Click handler for Delete Button
    * THis method delete Size & quantity from layout & list
    * */
    View.OnClickListener deleteBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //only allow to remove views when its more then one
            if(mSizeList.size() > 1  && mQuantityList.size() > 1){
                int tag = (int) v.getTag();
                int index = tag - 1; //because array starts with zero and tag starts with 1
                //Get the reference of size,Quantity and delete button

                EditText size = mSizeList.get(index);
                EditText quantity = mQuantityList.get(index);
                Button delete = mDeleteList.get(index);

                //Remove views from layout
                mSizeLayout.removeView(size);
                mQuantityLayout.removeView(quantity);
                mDeleteLayout.removeView(delete);

                //Remove views from list
                mSizeList.remove(index);
                mQuantityList.remove(index);
                mDeleteList.remove(index);

                //reset all the delete button tags
                for (int i = 0; i < mDeleteList.size(); i++) {
                    mDeleteList.get(i).setTag(i+1);
                }
            }else{
                Toast.makeText(getApplication(), R.string.atleast_one_size_quantity_field,Toast.LENGTH_LONG).show();
            }
        }
    };

    /*
    * Method to set Spinner value to the category id send by the Intent
    * */
    private void setSpinnerPostionToCategoryID() {
        int i = 0;
        int postion = -1;
        for (SimpleListPojo list : mCategorySimpleList){
            if(list.getId().equals(mCategoryId)){
                postion = i;
                break;
            }
            i++;
        }

        mCategorySpinner.setSelection(postion);
    }

    /*
    * Method to setup category Spinner
    * */
    public void setUpCategorySpinner(){
        //setup StringList to avoid NullPointerException
        mCategoryStringList = new ArrayList<>();
        //Get category data from Sqlite Database
        mCategorySimpleList = db.getAllSimpleList(Database.CATEGORY,mUserId);

        //mCategorySimpleList is not null
        if(mCategorySimpleList.size() > 0){
            //Create a simple
            for (int i = 0; i < mCategorySimpleList.size(); i++) {
                SimpleListPojo c = mCategorySimpleList.get(i);
                //Make a Simple String list which can be used with Default spinner adapter
                mCategoryStringList.add(c.getName());
            }

            //setup List to resources
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,mCategoryStringList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCategorySpinner.setAdapter(adapter);
        }else{
            Toast.makeText(getApplication(),R.string.unable_to_load_category,Toast.LENGTH_LONG).show();
        }
    }


    /*
    * Method to
    * Show a Dialog to Take a picture or chose a picture
    * */
    private void openSelectPictureDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.select_picture_option,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //Take picture
                        captureImageFromCamera(); //open camera
                        break;
                    case 1: //choose picture
                        selectImageFromGallery(); //open gallary
                        break;
                }
            }
        }).setNegativeButton(R.string.cancel, null)
                .show();
    }

    /*
    * Code to open gallery and select Image from their
    * */
    private void selectImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, CHOSE_PICTURE);
    }

    /*
    * This method will open camera and allow user to take a picture
    * */
    private void captureImageFromCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,TAKE_PICTURE);
    }

    /*
    * When image is selected from the gallery
    * set image to mProductImage
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //camera
        if(requestCode == TAKE_PICTURE && resultCode == RESULT_OK && data != null){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mProductImage.setImageBitmap(photo);
            Log.d(TAG,"HUS: camera intent result");
        }else if(requestCode == CHOSE_PICTURE && resultCode == RESULT_OK && data != null){ //gallery
            Uri selectedImage = data.getData();
            mProductImage.setImageURI(selectedImage);
            Log.d(TAG, "HUS: gallery intent result");
        }
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
            case R.id.loadMore:
                loadMore();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*private void addProduct() {
        //get all the texts from the fields
        String  name = mProductName.getText().toString().trim(),
                code = mProductCode.getText().toString().trim(),
                cp = mProductCP.getText().toString().trim(),
                sp = mProductSP.getText().toString().trim();

        *//*
        * Get Product Image if user has selected a image
        * *//*
        String encodedImage = "";
        if(mProductImage.getDrawable() != null){
            //get the image from ImageView and store it in a Bitmap
            Bitmap bitmapImage = ((BitmapDrawable) mProductImage.getDrawable()).getBitmap();

            //compress the image into 70% quality
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);

            //convert image to  Base64 encoded string
            encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }

        // if any field is empty
        if(name.isEmpty() || code.isEmpty() || cp.isEmpty() || sp.isEmpty()){
            Functions.errorAlert(context, getString(R.string.oops), getString(R.string.fillin_all_fields));
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
        for (int i = 0; i < mSizeList.size(); i++) {
            String size = mSizeList.get(i).getText().toString().trim();
            String quantity = mQuantityList.get(i).getText().toString().trim();

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

        *//*
        * IF all goes well we will reach here
        * Create a Hashmap to store Data which we will send to the server
        * *//*
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
    }*/
}
