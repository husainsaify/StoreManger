package com.hackerkernel.storemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class Functions {

    private static final String TAG = Functions.class.getSimpleName();

    //check email address
    public static boolean isValidEmail(CharSequence email){
        return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //check phone number
    public static boolean isValidPhoneNumber(String phone){
        return Patterns.PHONE.matcher(phone).matches();
    }

    //show alert
    public static void errorAlert(Context context,String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //check internet status
    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //close application dialog
    public static void closeAppWhenNoConnection(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection")
                .setMessage("Sorry! no intenet connectivity detected")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //open wireless settings
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0); //close app
                    }
                });

        //build the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //logout
    public static void logout(Context context){
        DataBase db = new DataBase(context);
        db.logout(); //logout
        //send user to mainActivity
        Intent intent = new Intent(context,MainActivity.class);
        //remove back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //function to show or hide the ProgressBar or loader
    public static void toggleProgressBar(ProgressBar pb){
        //if pb is invisible set it to visible
        if(pb.getVisibility() == View.INVISIBLE){
            pb.setVisibility(View.VISIBLE);
        }else{ //set it to invisible
            pb.setVisibility(View.INVISIBLE);
        }
    }

    //turn hashmap into encoded url
    public static String hashMapToEncodedUrl(HashMap<String,String> params){
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()){
            String value = null;
            try {
                key = URLEncoder.encode(key,"UTF-8");
                value = URLEncoder.encode(params.get(key),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(TAG,"ERROR " + e);
            }

            if (sb.length() > 0){
                sb.append("&");
            }

            //build a EncodedUrl string
            sb.append(key).append("=").append(value);
        }

        return sb.toString();
    }
}
