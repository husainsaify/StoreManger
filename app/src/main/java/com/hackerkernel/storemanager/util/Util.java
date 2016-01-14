package com.hackerkernel.storemanager.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.View;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.CategoryActivity;
import com.hackerkernel.storemanager.activity.HomeActivity;
import com.hackerkernel.storemanager.activity.MainActivity;
import com.hackerkernel.storemanager.storage.MySharedPreferences;

public class Util {
    public static void redSnackbar(Context context,View layout,String text){
        Snackbar snackbar = Snackbar.make(layout,text,Snackbar.LENGTH_LONG);
        View snack = snackbar.getView();
        snack.setBackgroundColor(context.getResources().getColor(R.color.error_color));
        snackbar.show();
    }

    public static void greenSnackbar(Context context,View layout,String text){
        Snackbar snackbar = Snackbar.make(layout,text,Snackbar.LENGTH_LONG);
        View snack = snackbar.getView();
        snack.setBackgroundColor(context.getResources().getColor(R.color.successColor));
        snackbar.show();
    }

    //check email address
    public static boolean isValidEmail(CharSequence email){
        return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //check phone number
    public static boolean isValidPhoneNumber(String phone) {
        return !(phone.length() < 6 || phone.length() > 13) && Patterns.PHONE.matcher(phone).matches();
    }
    //method to check user is connected to internet
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //if the phone can be connected to internet
        if(connectivity != null){
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null){
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void noInternetSnackbar(Context context,View layout){
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
     * Method to send the user to CategoryActivity
     * */
    public static void goToHomeActivity(Context context){
        Intent categoryIntent = new Intent(context, HomeActivity.class);
        categoryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        categoryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(categoryIntent);
    }


    //logout
    public static void logout(Context context){
        //delete the user data from Shared Prefernece
        MySharedPreferences.getInstance(context).deleteUser();
        //send user to mainActivity
        Intent intent = new Intent(context, MainActivity.class);
        //remove back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /*public static void closeSoftKeyBoard(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(context.get)
    }*/
}
