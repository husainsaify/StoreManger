package com.hackerkernel.storemanager.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.HomeActivity;
import com.hackerkernel.storemanager.activity.MainActivity;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.storage.MySharedPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
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
        snack.setAction(context.getString(R.string.retry_big), new View.OnClickListener() {
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
}
