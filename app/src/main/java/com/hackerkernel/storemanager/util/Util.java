package com.hackerkernel.storemanager.util;

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

}
