package com.hackerkernel.storemanager.parser;

import android.content.Context;
import android.util.Log;

import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.pojo.ACProductSearchPojo;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.pojo.LoginPojo;
import com.hackerkernel.storemanager.pojo.ProductPojo;
import com.hackerkernel.storemanager.pojo.STdatePojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;
import com.hackerkernel.storemanager.pojo.SignupPojo;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * json parser to parse json
 */
public class JsonParser {

    private static final String TAG = JsonParser.class.getSimpleName();

    public static List<SimplePojo> SimpleParse(String json){
        try {
            JSONObject jo = new JSONObject(json);
            List<SimplePojo> list = new ArrayList<>();

            SimplePojo simplePojo = new SimplePojo();
            simplePojo.setMessage(jo.getString(Keys.KEY_COM_MESSAGE));
            simplePojo.setReturned(jo.getBoolean(Keys.KEY_COM_RETURN));

            list.add(simplePojo);

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<SignupPojo> SignupParse(String json){
        try {
            JSONObject jo = new JSONObject(json);
            List<SignupPojo> list = new ArrayList<>();

            SignupPojo current = new SignupPojo();
            current.setMessage(jo.getString(Keys.KEY_COM_MESSAGE));
            current.setReturned(jo.getBoolean(Keys.KEY_COM_RETURN));

            //if response return true add ID also
            if(current.getReturned()){
                current.setUserId(jo.getString(Keys.KEY_COM_USERID));
            }

            list.add(current);

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    //login json parser
    public static List<LoginPojo> LoginParser(String json){
        try {
            JSONObject jo = new JSONObject(json);
            List<LoginPojo> loginList = new ArrayList<>();

            LoginPojo loginPojo = new LoginPojo();

            //store message and return
            loginPojo.setReturned(jo.getBoolean(Keys.KEY_COM_RETURN));
            loginPojo.setMessage(jo.getString(Keys.KEY_COM_MESSAGE));

            //if Response return success
            if(jo.getBoolean(Keys.KEY_COM_RETURN)){
                JSONArray jsonArray = jo.getJSONArray(Keys.KEY_L_USER);
                JSONObject o = jsonArray.getJSONObject(0);

                //store in the list
                loginPojo.setId(o.getInt(Keys.KEY_L_ID));
                loginPojo.setName(o.getString(Keys.KEY_L_NAME));
                loginPojo.setStorename(o.getString(Keys.KEY_L_STORENAME));
                loginPojo.setEmail(o.getString(Keys.KEY_L_EMAIL));
                loginPojo.setPhone(o.getString(Keys.KEY_L_PHONE));
                loginPojo.setPassword(o.getString(Keys.KEY_L_PASSWORD));
                loginPojo.setRegisterAt(o.getString(Keys.KEY_L_REGISTER_AT));
            }

            //add to the list
            loginList.add(loginPojo);
            return loginList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //SimpleListParser to parse json response from category , salesman list etc
    public static List<SimpleListPojo> simpleListParser(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            List<SimpleListPojo> list = new ArrayList<>();
            /*
            * If response return true
            * */
            if(jsonObject.getBoolean(Keys.KEY_COM_RETURN)){
                Log.d(TAG,"HUS: True");

                //check KEY_COM_DATA exits in json response
                if(jsonObject.has(Keys.KEY_COM_DATA) && !jsonObject.isNull(Keys.KEY_COM_DATA)){
                    Log.d(TAG,"HUS: data key exits");
                    JSONArray jsonArray = jsonObject.getJSONArray(Keys.KEY_COM_DATA);

                    //loop throw the json array
                    for (int i = 0;i < jsonArray.length();i++){
                        //get the current json object
                        JSONObject jo = jsonArray.getJSONObject(i);

                        //create a instance of SimpleListPojo
                        SimpleListPojo current = new SimpleListPojo();

                        //Store results in list
                        current.setId(jo.getString(Keys.KEY_SL_ID));
                        current.setName(jo.getString(Keys.KEY_SL_NAME));
                        current.setUser_id(jo.getString(Keys.KEY_SL_USER_ID));
                        current.setTime(jo.getString(Keys.KEY_SL_TIME));

                        //add results to the list
                        list.add(current);
                    }

                }else{ //If data not exits,Get count and store it
                    Log.d(TAG, "HUS: data key not exits");
                    //create a instance of SimpleListPojo
                    SimpleListPojo current = new SimpleListPojo();
                    current.setCount(jsonObject.getInt(Keys.KEY_SL_COUNT));

                    //Add count to the list
                    list.add(current);
                }

               //return the list
               return list;
            }else{ //If response is false return message
                Log.d(TAG, "HUS: response false "+jsonObject.getString(Keys.KEY_COM_MESSAGE));
                SimpleListPojo current = new SimpleListPojo();
                current.setReturned(jsonObject.getBoolean(Keys.KEY_COM_RETURN));
                current.setMessage(jsonObject.getString(Keys.KEY_COM_MESSAGE));
                list.add(current);
                return list;
            }

        } catch (JSONException e) {
            Log.d(TAG,"HUS: Exception");
            e.printStackTrace();
            Log.e(TAG, "Exception " + e);
            return null;
        }
    }

    //method to parse product List
    public static List<ProductPojo> productListParser(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            List<ProductPojo> list = new ArrayList<>();

            //if we have product List
            if(jsonObject.getBoolean(Keys.KEY_COM_RETURN)){

                //check Product Key is avaialble
                if(jsonObject.has(Keys.KEY_COM_DATA) && !jsonObject.isNull(Keys.KEY_COM_DATA)){

                    //fetch product
                    JSONArray jsonArray = jsonObject.getJSONArray(Keys.KEY_COM_DATA);

                    //Fetch all the Product from the jsonArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        //Set all the fetched json values to the ProductPojo
                        ProductPojo productPojo = new ProductPojo();

                        productPojo.setProductId(jo.getString(Keys.KEY_PL_ID));
                        productPojo.setProductName(jo.getString(Keys.KEY_PL_NAME));
                        productPojo.setProductImage(jo.getString(Keys.KEY_PL_IMAGE));
                        productPojo.setProductCode(jo.getString(Keys.KEY_PL_CODE));
                        productPojo.setProductTime(jo.getString(Keys.KEY_PL_TIME));

                        //Add productPojo to the list
                        list.add(productPojo);
                    }
                }else{
                    //create a instance of ProductPojo
                    ProductPojo current = new ProductPojo();
                    current.setCount(jsonObject.getInt(Keys.KEY_SL_COUNT));

                    //Add count to the list
                    list.add(current);
                }

                return list;
            }else{//If response is false return message
                Log.d(TAG, "HUS: productListParser: response false "+jsonObject.getString(Keys.KEY_COM_MESSAGE));
                ProductPojo current = new ProductPojo();
                current.setReturned(jsonObject.getBoolean(Keys.KEY_COM_RETURN));
                current.setMessage(jsonObject.getString(Keys.KEY_COM_MESSAGE));
                list.add(current);
                return list;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception " + e);
            return null;
        }
    }

    //fetch product data
    public static SingleProductPojo SingleProductParser(String jsonString){
        SingleProductPojo product = new SingleProductPojo();
        try {
            JSONObject jo = new JSONObject(jsonString);
            //put return & message into productPojo

            product.setReturned(jo.getBoolean("return"));
            product.setMessage(jo.getString("message"));

            //return is success add more item to productPojo
            if(product.getReturned()){
                //add more item
                product.setId(jo.getString("id"));
                product.setName(jo.getString("name"));
                product.setImageAddress(jo.getString("image"));
                product.setCode(jo.getString("code"));
                product.setCp(jo.getString("cp"));
                product.setSp(jo.getString("sp"));
                product.setTime(jo.getString("time"));

                //fetch Size & quantity
                JSONArray sizeJsonArray = jo.getJSONArray("size");
                JSONArray quantityJsonArray = jo.getJSONArray("quantity");
                String size = "",quantity = "";

                //generate a size & quantity string Ex "7,8,9,"
                for (int i = 0; i < sizeJsonArray.length(); i++) {
                    size += sizeJsonArray.get(i);
                    quantity += quantityJsonArray.get(i);

                    if (i < sizeJsonArray.length()){
                        size += "\n";
                        quantity += "\n";
                    }
                }

                //add size & quantity to productPojo
                product.setSize(size);
                product.setQuantity(quantity);
            }

            //return the productPojo
            return product;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //ACProductSearch json parser
    public static List<ACProductSearchPojo> ACProductSearchParser(String jsonString){
        ACProductSearchPojo product = new ACProductSearchPojo();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            //set message and return
            product.setMessage(jsonObject.getString("message"));
            product.setReturned(jsonObject.getBoolean("return"));

            List<ACProductSearchPojo> productList = new ArrayList<>();

            //check we have a success or failed "return" and count is greater then > 0
            if(product.getReturned() && jsonObject.getInt("count") > 0){

                JSONArray ja = jsonObject.getJSONArray("result");

                for (int i = 0; i < ja.length(); i++) {
                    //get jsonObject
                    JSONObject jo = ja.getJSONObject(i);

                    //store stuff in Pojo
                    ACProductSearchPojo p = new ACProductSearchPojo();
                    p.setId(jo.getString("id"));
                    p.setName(jo.getString("name"));
                    p.setCode(jo.getString("code"));

                    //add pojo to the list
                    productList.add(p);
                }
            }else{
                //log message for debugging
                Log.d(TAG,"HUS: "+product.getMessage());
            }

            return productList;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "HUS: " + e);
            return null;
        }
    }

    public static List<STdatePojo> STdateParser(String jsonParser){
        try {
            JSONObject jsonObject = new JSONObject(jsonParser);
            STdatePojo STdate = new STdatePojo();
            List<STdatePojo> list = new ArrayList<>();
            //parse
            STdate.setMessage(jsonObject.getString("message"));
            STdate.setReturned(jsonObject.getBoolean("return"));

            //check if we return true
            if(jsonObject.getBoolean("return")){
                JSONArray jsonArray = jsonObject.getJSONArray("date");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    STdatePojo stdate = new STdatePojo();
                    stdate.setDate(jo.getString("date"));
                    stdate.setDateId(jo.getString("date_id"));

                    //add to list
                    list.add(stdate);
                }
            }else{
                list.add(STdate);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<SalesTrackerPojo> SalesTrackerParser(Context context,String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            List<SalesTrackerPojo> list = new ArrayList<>();
            //returned true or success
            if(jsonObject.getBoolean("return")){
                //add products
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    SalesTrackerPojo current = new SalesTrackerPojo();
                    current.setSellId(jo.getString("sell_id"));
                    current.setQuantity(jo.getString("quantity"));
                    current.setPrice_per(jo.getString("price_per"));
                    current.setProductId(jo.getString("product_id"));
                    current.setProductImageAddress(jo.getString("product_image"));
                    current.setProductName(jo.getString("name"));
                    current.setProductCode(jo.getString("code"));
                    current.setProductCp(jo.getString("cp"));
                    current.setProductSp(jo.getString("sp"));
                    current.setCurrentCp(jo.getString("current_cp"));
                    current.setCurrentSales(jo.getString("current_sales"));

                    //add to the list
                    list.add(current);
                }
            }else{//returned false
                //show a error Toast message
                Log.d(TAG,"HUS: "+jsonObject.getString("message"));
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
