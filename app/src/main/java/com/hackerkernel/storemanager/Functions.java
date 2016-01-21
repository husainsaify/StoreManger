package com.hackerkernel.storemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class Functions {

    private static final String TAG = Functions.class.getSimpleName();


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
