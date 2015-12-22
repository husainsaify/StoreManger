package com.hackerkernel.storemanager.parser;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hackerkernel.storemanager.pojo.ACProductSearchPojo;
import com.hackerkernel.storemanager.pojo.CategoryPojo;
import com.hackerkernel.storemanager.pojo.LoginPojo;
import com.hackerkernel.storemanager.pojo.ProductPojo;
import com.hackerkernel.storemanager.pojo.STdatePojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;
import com.hackerkernel.storemanager.pojo.SimplePojo;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * json parser to parse json
 */
public class JsonParser {

    private static final String TAG = JsonParser.class.getSimpleName();

    public static List<SimplePojo> SimpleParse(String json){
        try {
            JSONObject jo = new JSONObject(json);
            List<SimplePojo> registerList = new ArrayList<>();

            SimplePojo simplePojo = new SimplePojo();
            simplePojo.setMessage(jo.getString("message"));
            simplePojo.setReturned(jo.getBoolean("return"));

            registerList.add(simplePojo);

            return registerList;
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
            loginPojo.setMessage(jo.getString("message"));
            loginPojo.setReturned(jo.getBoolean("return"));

            //check return
            /*
            * It was a success
            * and we will fetch all the info
            * */
            if(jo.getBoolean("return")){
                JSONArray jsonArray = jo.getJSONArray("user");
                JSONObject o = jsonArray.getJSONObject(0);

                //store in the list
                loginPojo.setId(o.getInt("id"));
                loginPojo.setName(o.getString("name"));
                loginPojo.setEmail(o.getString("email"));
                loginPojo.setPhone(o.getString("phone"));
                loginPojo.setPassword(o.getString("password"));
                loginPojo.setRegisterAt(o.getString("register_at"));
                loginPojo.setLastBillPaid(o.getString("last_bill_paid"));
                loginPojo.setNextDueDate(o.getString("next_due_date"));
                loginPojo.setActive(o.getString("active"));
            }

            //add to the list
            loginList.add(loginPojo);
            return loginList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //category list parser
    public static List<CategoryPojo> categoryParser(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            List<CategoryPojo> list = new ArrayList<>();
            
            /*
            * If data category is found
            * */
            if(jsonObject.getBoolean("return")){
                JSONArray jsonArray = jsonObject.getJSONArray("category");

                //loop throw the json array
                for (int i = 0;i < jsonArray.length();i++){
                    //get the current json object
                    JSONObject jo = jsonArray.getJSONObject(i);

                    //create a instance of categoryPojo
                    CategoryPojo cP = new CategoryPojo();
                    //store results in categoryPojo
                    cP.setId(jo.getString("id"));
                    cP.setName(jo.getString("name"));

                    //set categoryPojo to the list
                    list.add(cP);
                }
            }

            //return the list
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception " + e);
            return null;
        }
    }

    //method to parse product List
    public static List<ProductPojo> productParser(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            List<ProductPojo> list = new ArrayList<>();

            //if we have product List
            if(jsonObject.getBoolean("return")){
                //fetch product
                JSONArray jsonArray = jsonObject.getJSONArray("product");

                //Fetch all the Product from the jsonArray
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);

                    //Set all the fetched json values to the ProductPojo
                    ProductPojo productPojo = new ProductPojo();

                    productPojo.setProductId(jo.getString("productId"));
                    productPojo.setProductName(jo.getString("name"));
                    productPojo.setProductImage(jo.getString("image"));
                    productPojo.setProductCode(jo.getString("code"));

                    //Add productPojo to the list
                    list.add(productPojo);
                }
            }
            return list;
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
