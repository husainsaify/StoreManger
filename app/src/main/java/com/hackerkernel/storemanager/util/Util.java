package com.hackerkernel.storemanager.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.HomeActivity;
import com.hackerkernel.storemanager.activity.MainActivity;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.Database;
import com.hackerkernel.storemanager.storage.MySharedPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {
    public static final String TAG = Util.class.getSimpleName();

    public static void redSnackbar(Context context, View layout, String text) {
        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);
        View snack = snackbar.getView();
        snack.setBackgroundColor(context.getResources().getColor(R.color.error_color));
        snackbar.show();
    }

    public static void greenSnackbar(Context context, View layout, String text) {
        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);
        View snack = snackbar.getView();
        snack.setBackgroundColor(context.getResources().getColor(R.color.successColor));
        snackbar.show();
    }

    //check email address
    public static boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //check phone number
    public static boolean isValidPhoneNumber(String phone) {
        return !(phone.length() < 6 || phone.length() > 13) && Patterns.PHONE.matcher(phone).matches();
    }

    //method to check user is connected to internet
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //if the phone can be connected to internet
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void noInternetSnackbar(Context context, View layout) {
        final Snackbar snack = Snackbar.make(layout, context.getString(R.string.please_check_your_internt), Snackbar.LENGTH_INDEFINITE);
        snack.setAction(R.string.hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss Snackbar
                snack.dismiss();
            }
        });
        snack.show();
    }

    /*
     * Method to send the user to HomeActivity
     * */
    public static void goToHomeActivity(Context context) {
        Intent categoryIntent = new Intent(context, HomeActivity.class);
        categoryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        categoryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(categoryIntent);
    }


    //logout
    public static void logout(Context context) {
        //delete the user data from Shared Prefernece
        MySharedPreferences.getInstance(context).deleteUser();

        //Delete all data from Sqlite databae
        Database db = new Database(context);
        db.deleteAllData();

        //Delete cache which is having salesTracker file
        deleteCache(context);

        //send user to mainActivity
        Intent intent = new Intent(context, MainActivity.class);
        //remove back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //method to check External is available to write
    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /*
    * Method to save image to Sdcard
    * */
    public static Uri saveImageToExternalStorage(Context context, Bitmap bitmap) {
        //Check external storage is avaialble
        if (isExternalStorageAvailable()) {
            OutputStream output;
            String appName = context.getString(R.string.app_name);

            //1. Get external storage directory
            File filePath = Environment.getExternalStorageDirectory();

            //2. Create our subdirectory
            File dir = new File(filePath.getAbsolutePath() + "/" + appName + "/");
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (!result) {
                    Log.d(TAG, "HUS: failed to create directory");
                    Toast.makeText(context, R.string.failed_create_directory, Toast.LENGTH_LONG).show();
                    return null;
                }
            }

            //3. Create file name
            Date date = new Date();
            String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);

            //4. Create a file
            File file = new File(dir, "IMG_" + filename + ".jpg");

            try {
                output = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();
                Toast.makeText(context, R.string.image_saved_successfully, Toast.LENGTH_SHORT).show();
                //5. return the Uri
                return Uri.fromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "HUS: Exception " + e.getMessage());
                Toast.makeText(context, R.string.unable_to_save_image, Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            Toast.makeText(context, R.string.external_storage_not_available, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /*
    * Method to select a Category Spinner to categoryId
    * */
    public static void setSpinnerPostionToCategoryID(List<SimpleListPojo> mCategorySimpleList, String mCategoryId, Spinner spinner) {
        int i = 0;
        int postion = -1;
        for (SimpleListPojo list : mCategorySimpleList) {
            if (list.getId().equals(mCategoryId)) {
                postion = i;
                break;
            }
            i++;
        }

        spinner.setSelection(postion);
    }

    /*
    *
    * Method to cal Profit or loss On Product which have multiple sales
    * */
    public static String calProfitLossOrBreakeven(String costprice, String sellingprice, String quantity) {

        //create a int array for sp & cp
        String[] cpArray = costprice.split("\n");
        String[] spArray = sellingprice.split("\n");
        String[] quantityArray = quantity.split("\n");

        //Variables to store total costprice & selling price
        int totalCostprice = 0;
        int totalSellingprice = 0;

        //loop through cp & sp array to get cp & sp
        for (int i = 0; i < cpArray.length; i++) {
            int currentQuantity = Integer.parseInt(quantityArray[i]);
            //add current cp & sp to Total cp & sp
            totalCostprice += (currentQuantity * Integer.parseInt(cpArray[i]));
            totalSellingprice += (currentQuantity * Integer.parseInt(spArray[i]));
        }

        String pl;
        if (totalCostprice > totalSellingprice) {//loss
            //cal loss
            int loss = totalCostprice - totalSellingprice;
            pl = "Loss: RS " + loss;
        } else {
            if (totalSellingprice > totalCostprice) { //profit
                //call profit
                int profit = totalSellingprice - totalCostprice;
                pl = "Profit: RS " + profit;
            } else { // neutral CP = sales
                pl = "Break Even";
            }
        }

        return pl;
    }

    /*
    * Method to cal Total Price weather its costprice or selling price
    * */
    public static int calTotalPrice(String price,String quantity){
        String[] priceArray = price.split("\n");
        String[] quantityArray = quantity.split("\n");

        int calPrice = 0;
        for (int i = 0; i < priceArray.length; i++) {
            int currentPrice = Integer.parseInt(priceArray[i]);
            int currentQuantity = Integer.parseInt(quantityArray[i]);

            calPrice += (currentPrice * currentQuantity);
        }
        return calPrice;
    }

    /*
    * Method to make progressbar visible and invisible
    * */
    public static void setProgressBarVisible(ProgressBar pb,boolean value){
        if(value){ //true make visible
            pb.setVisibility(View.VISIBLE);
        }else{ //false hide
            pb.setVisibility(View.GONE);
        }
    }

    /*
    * Method to take day , month & year and generate a DateId
    * */
    public static String createDateId(int day,int month,int year,int type){
        /*
        * Type
        * 1 = 06012016
        * 2 = 06/01/2016
        * */

        String m = String.valueOf(month);
        String d = String.valueOf(day);

        //check month is single character or double because we have to prepend 0 if single

        //single char
        if(day < 10){
            d = 0+d;
        }

        //single char
        if(month < 10){
            m = 0+m;
        }

        //create dateId
        if(type == 1){
            return d + m + year;
        }else{
            return d +"-"+ m +"-"+ year;
        }
    }

    /*
    * Method to setup category Spinner from SQLite database
    * */
    public static List<SimpleListPojo> setupCategorySpinnerFromDb(Context context,Database db,String userId,Spinner spinner,boolean seDefaultLabel){
        //setup StringList to avoid NullPointerException
        List<String> stringList = new ArrayList<>();
        if(seDefaultLabel){
            stringList.add("Select category");
        }

        //Get category data from Sqlite Database
        List<SimpleListPojo> categorySimpleList = db.getAllSimpleList(Database.CATEGORY,userId);

        //mCategorySimpleList is not null
        if(categorySimpleList.size() > 0){
            //Create a simple
            for (int i = 0; i < categorySimpleList.size(); i++) {
                SimpleListPojo c = categorySimpleList.get(i);
                //Make a Simple String list which can be used with Default spinner adapter
                stringList.add(c.getName());
            }

            //setup List to resources
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, stringList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            //return the list
            return categorySimpleList;
        }else{
            Toast.makeText(context,R.string.unable_to_load_category,Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /*
    * Method to delete application cache
    * */
    public static boolean deleteCache(Context context) {
        File cacheDir = context.getCacheDir();
        return deleteDir(cacheDir);
    }

    private static boolean deleteDir(File dir){
        //Delete cache
        if (dir != null && dir.isDirectory()){
            String[] childern = dir.list();
            for (int i = 0; i < childern.length; i++) {
                //delete childern
                Log.d(TAG,"HUS: FILE "+childern[i]);
                boolean success = deleteDir(new File(dir,childern[i]));
                if (!success){
                    return false;
                }
            }
            return dir.delete();
        }else if (dir != null && dir.isFile()){
            return dir.delete();
        }else{
            return false;
        }
    }
}
