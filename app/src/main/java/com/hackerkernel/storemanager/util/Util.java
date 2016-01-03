package com.hackerkernel.storemanager.util;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.View;

import com.hackerkernel.storemanager.R;

public class Util {
    public static void redSnackbar(Context context,View layout,String text){
        Snackbar snackbar = Snackbar.make(layout,text,Snackbar.LENGTH_LONG);
        View snack = snackbar.getView();
        snack.setBackgroundColor(context.getResources().getColor(R.color.error_color));
        snackbar.show();
    }

    //check email address
    public static boolean isValidEmail(CharSequence email){
        return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //check phone number
    public static boolean isValidPhoneNumber(String phone) {
        if (phone.length() < 6 || phone.length() > 13) {
            return false;
        } else {
            return Patterns.PHONE.matcher(phone).matches();
        }
    }
    //method to check user is connected to internet
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
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
}
