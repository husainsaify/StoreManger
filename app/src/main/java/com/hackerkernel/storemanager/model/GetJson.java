package com.hackerkernel.storemanager.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to make a request to the web and fetch the reponse
 */
public class GetJson {

    private static final String TAG = GetJson.class.getSimpleName();

    public static String request(String stringUrl,String data,String method){
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //set method
            con.setRequestMethod(method);
            con.setDoInput(true);
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(os,"UTF-8"));

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();

            //check for response
            int responseCode = con.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                //get the response
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();

                //fetch the response line by line
                while ((line = br.readLine()) != null){
                    sb.append(line).append("\n");
                }

                //return the string
                return sb.toString();
            }else{
                Log.e(TAG,"INVALID response code "+ responseCode);
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
