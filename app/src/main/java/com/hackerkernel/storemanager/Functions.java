package com.hackerkernel.storemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
                .setMessage("Sorry! no internet connectivity detected")
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

    //method to check External is available to write
    public static boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /*
    * This method will take a bitmap and save it to the sdcad
    * & return the Uri of the image
    * */
    //method to save image to SDCard
    public static Uri saveImageToSD(Context context,Bitmap bitmap){
        //check external storage
        //External storage available to Write
        if(Functions.isExternalStorageAvailable()){

            OutputStream output;
            String appName = context.getString(R.string.app_name);

            //1. Get the external storage directory
            File filePath = Environment.getExternalStorageDirectory();

            //2. Create our subdirectory
            File dir = new File(filePath.getAbsolutePath()+"/"+appName+"/");
            if(!dir.exists()){
                boolean file = dir.mkdirs();
            }

            //3. Create file name
            Date date = new Date();
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);

            //3. Create the file
            File file = new File(dir,"IMG_"+fileName+".jpg");

            //4. store image
            try{
                output = new FileOutputStream(file);

                //compress image
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();

                //5. return image Uri
                return Uri.fromFile(file);

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"HUS: "+e);
                return null;
            }
        }else{
            //not Available
            Toast.makeText(context.getApplicationContext(), R.string.external_storage_not_available, Toast.LENGTH_LONG).show();
            return null;
        }

    }
}
