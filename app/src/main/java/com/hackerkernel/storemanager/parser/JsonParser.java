package com.hackerkernel.storemanager.parser;

import android.util.Log;

import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.pojo.AutoCompleteProductPojo;
import com.hackerkernel.storemanager.pojo.LoginPojo;
import com.hackerkernel.storemanager.pojo.ProductListPojo;
import com.hackerkernel.storemanager.pojo.ProductPojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerDatePojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;
import com.hackerkernel.storemanager.pojo.SignupPojo;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;
import com.hackerkernel.storemanager.pojo.SimplePojo;

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

    public static List<SimplePojo> simpleParser(String json) {
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
            Log.d(TAG, "HUS: simpleParser: " + e.getMessage());
            return null;
        }
    }

    public static List<SignupPojo> signupParser(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            List<SignupPojo> list = new ArrayList<>();

            SignupPojo current = new SignupPojo();
            current.setMessage(jo.getString(Keys.KEY_COM_MESSAGE));
            current.setReturned(jo.getBoolean(Keys.KEY_COM_RETURN));

            //if response return true add ID also
            if (current.getReturned()) {
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
    public static List<LoginPojo> loginParser(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            List<LoginPojo> loginList = new ArrayList<>();

            LoginPojo loginPojo = new LoginPojo();

            //store message and return
            loginPojo.setReturned(jo.getBoolean(Keys.KEY_COM_RETURN));
            loginPojo.setMessage(jo.getString(Keys.KEY_COM_MESSAGE));

            //if Response return success
            if (jo.getBoolean(Keys.KEY_COM_RETURN)) {
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
    public static List<SimpleListPojo> simpleListParser(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            List<SimpleListPojo> list = new ArrayList<>();
            /*
            * If response return true
            * */
            if (jsonObject.getBoolean(Keys.KEY_COM_RETURN)) {
                Log.d(TAG, "HUS: True");

                //check KEY_COM_DATA exits in json response
                if (jsonObject.has(Keys.KEY_COM_DATA) && !jsonObject.isNull(Keys.KEY_COM_DATA)) {
                    Log.d(TAG, "HUS: data key exits");
                    JSONArray jsonArray = jsonObject.getJSONArray(Keys.KEY_COM_DATA);

                    //loop throw the json array
                    for (int i = 0; i < jsonArray.length(); i++) {
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

                } else { //If data not exits,Get count and store it
                    Log.d(TAG, "HUS: data key not exits");
                    //create a instance of SimpleListPojo
                    SimpleListPojo current = new SimpleListPojo();
                    current.setCount(jsonObject.getInt(Keys.KEY_SL_COUNT));

                    //Add count to the list
                    list.add(current);
                }

                //return the list
                return list;
            } else { //If response is false return message
                Log.d(TAG, "HUS: response false " + jsonObject.getString(Keys.KEY_COM_MESSAGE));
                SimpleListPojo current = new SimpleListPojo();
                current.setReturned(jsonObject.getBoolean(Keys.KEY_COM_RETURN));
                current.setMessage(jsonObject.getString(Keys.KEY_COM_MESSAGE));
                list.add(current);
                return list;
            }

        } catch (JSONException e) {
            Log.d(TAG, "HUS: Exception");
            e.printStackTrace();
            Log.e(TAG, "Exception " + e);
            return null;
        }
    }

    //method to parse product List
    public static List<ProductListPojo> productListParser(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            List<ProductListPojo> list = new ArrayList<>();

            //if we have product List
            if (jsonObject.getBoolean(Keys.KEY_COM_RETURN)) {

                //check Product Key is avaialble
                if (jsonObject.has(Keys.KEY_COM_DATA) && !jsonObject.isNull(Keys.KEY_COM_DATA)) {

                    //fetch product
                    JSONArray jsonArray = jsonObject.getJSONArray(Keys.KEY_COM_DATA);

                    //Fetch all the Product from the jsonArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        //Set all the fetched json values to the ProductListPojo
                        ProductListPojo productListPojo = new ProductListPojo();

                        productListPojo.setProductId(jo.getString(Keys.KEY_COM_PRODUCTID));
                        productListPojo.setUserId(jo.getString(Keys.KEY_COM_USERID));
                        productListPojo.setCategoryId(jo.getString(Keys.KEY_COM_CATEGORYID));
                        productListPojo.setProductName(jo.getString(Keys.KEY_PL_NAME));
                        productListPojo.setProductImage(jo.getString(Keys.KEY_PL_IMAGE));
                        productListPojo.setProductCode(jo.getString(Keys.KEY_PL_CODE));
                        productListPojo.setProductTime(jo.getString(Keys.KEY_PL_TIME));

                        //Add productListPojo to the list
                        list.add(productListPojo);
                    }
                } else {
                    //create a instance of ProductListPojo
                    ProductListPojo current = new ProductListPojo();
                    current.setCount(jsonObject.getInt(Keys.KEY_SL_COUNT));

                    //Add count to the list
                    list.add(current);
                }

                return list;
            } else {//If response is false return message
                Log.d(TAG, "HUS: productListParser: response false " + jsonObject.getString(Keys.KEY_COM_MESSAGE));
                ProductListPojo current = new ProductListPojo();
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

    /*
    * Method to parse product response
    * */
    public static ProductPojo productParser(String jsonString) {
        ProductPojo product = new ProductPojo();
        try {
            JSONObject jo = new JSONObject(jsonString);

            //return is success add more item to productListPojo
            if (jo.getBoolean(Keys.KEY_COM_RETURN)) {
                //add more item
                product.setUserId(jo.getString(Keys.KEY_COM_USERID));
                product.setId(jo.getString(Keys.KEY_P_ID));
                product.setCategoryId(jo.getString(Keys.KEY_COM_CATEGORYID));
                product.setName(jo.getString(Keys.KEY_P_NAME));
                product.setImageAddress(jo.getString(Keys.KEY_P_IMAGE));
                product.setCode(jo.getString(Keys.KEY_P_CODE));
                product.setCp(jo.getString(Keys.KEY_P_CP));
                product.setSp(jo.getString(Keys.KEY_P_SP));
                product.setTime(jo.getString(Keys.KEY_P_TIME));

                //fetch Size & quantity
                JSONArray sizeJsonArray = jo.getJSONArray(Keys.KEY_P_SIZE);
                JSONArray quantityJsonArray = jo.getJSONArray(Keys.KEY_P_QUANTITY);
                String size = "", quantity = "";

                //generate a size & quantity string Ex "7\n8\n9\n"
                for (int i = 0; i < sizeJsonArray.length(); i++) {
                    size += sizeJsonArray.get(i);
                    quantity += quantityJsonArray.get(i);

                    if (i < sizeJsonArray.length()) {
                        size += "\n";
                        quantity += "\n";
                    }
                }

                //add size & quantity to productListPojo
                product.setSize(size);
                product.setQuantity(quantity);

                //return the productListPojo
                return product;
            } else {
                Log.d(TAG, "HUS: productParser: return false, message " + jo.getString(Keys.KEY_COM_MESSAGE));
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //ACProductSearch json parser
    public static List<AutoCompleteProductPojo> acProductSearchParser(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            List<AutoCompleteProductPojo> productList = new ArrayList<>();

            /*
            * If response message is True and count is greater then 0
            * means some result is found
            * */
            if (jsonObject.getBoolean(Keys.KEY_COM_RETURN) && jsonObject.getInt(Keys.KEY_COM_COUNT) > 0) {

                JSONArray ja = jsonObject.getJSONArray(Keys.KEY_COM_DATA);

                for (int i = 0; i < ja.length(); i++) {
                    //get jsonObject
                    JSONObject jo = ja.getJSONObject(i);

                    //store stuff in Pojo
                    AutoCompleteProductPojo p = new AutoCompleteProductPojo();
                    p.setId(jo.getString(Keys.KEY_AC_ID));
                    p.setName(jo.getString(Keys.KEY_AC_NAME));
                    p.setCode(jo.getString(Keys.KEY_AC_CODE));
                    p.setCp(jo.getString(Keys.KEY_AC_CP));

                    //parse size response & set to POJO
                    String sizeStack = jo.getString(Keys.KEY_AC_SIZE);
                    String[] sizeArray = sizeStack.split(",");
                    p.setSizeArray(sizeArray);

                    //add pojo to the list
                    productList.add(p);
                }
            } else {
                //no result found
                Log.d(TAG, "HUS: acProductSearchParser " + jsonString);
                //add response value to the pojo class
                AutoCompleteProductPojo product = new AutoCompleteProductPojo();
                product.setMessage(jsonObject.getString(Keys.KEY_COM_MESSAGE));
                product.setReturned(jsonObject.getBoolean(Keys.KEY_COM_RETURN));
                product.setCount(jsonObject.getInt(Keys.KEY_COM_COUNT));

                //add pojo to the list
                productList.add(product);
            }

            return productList;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "HUS: " + e);
            return null;
        }
    }

    public static List<SalesTrackerDatePojo> salesTrackerDateParser(JSONObject jsonObject) {
        try {

            //check if we return true & count greater then zero
            if (jsonObject.getBoolean(Keys.KEY_COM_RETURN) && jsonObject.getInt(Keys.KEY_COM_COUNT) > 0) {
                List<SalesTrackerDatePojo> list = new ArrayList<>();

                JSONArray jsonArray = jsonObject.getJSONArray(Keys.KEY_COM_DATA);
                //loop the data list
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    //create pojo and store it in list
                    SalesTrackerDatePojo date = new SalesTrackerDatePojo();
                    date.setDate(jo.getString(Keys.KEY_ST_DATELIST_DATE));
                    date.setDateId(jo.getString(Keys.KEY_ST_DATELIST_DATE_ID));

                    //add to list
                    list.add(date);
                }
                return list;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "HUS: salesTrackerDateParser: " + e.getMessage());
            return null;
        }
    }

    public static List<SalesTrackerPojo> salesTrackerParser(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            List<SalesTrackerPojo> list = new ArrayList<>();
            //returned true or success
            if (jsonObject.getBoolean(Keys.KEY_COM_RETURN)) {
                //Get sales key
                JSONArray ja = jsonObject.getJSONArray(Keys.KEY_ST_SALES);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    SalesTrackerPojo current = new SalesTrackerPojo();

                    //set Total costPrice & selling price
                    current.setTotalCostprice(jsonObject.getString(Keys.KEY_ST_TOTAL_COSTPRICE));
                    current.setTotalSellingprice(jsonObject.getString(Keys.KEY_ST_TOTAL_SELLINGPRICE));

                    //set sales
                    current.setSalesId(jo.getString(Keys.KEY_ST_SALES_ID));
                    current.setCustomerName(jo.getString(Keys.KEY_ST_CUSTOMER_NAME));
                    current.setSalesmanId(jo.getString(Keys.KEY_ST_SALESMAN_ID));
                    current.setSalesmanName(jo.getString(Keys.KEY_ST_SALESMAN_NAME));
                    current.setTime(jo.getString(Keys.KEY_ST_TIME));

                    //get sales_product_info
                    String productId = "",
                            productName = "",
                            productCode = "",
                            size = "",
                            quantity = "",
                            costprice = "",
                            sellingprice = "";
                    JSONArray infoArray = jo.getJSONArray(Keys.KEY_COM_DATA);
                    for (int a = 0; a < infoArray.length(); a++) {
                        JSONObject infoObj = infoArray.getJSONObject(a);
                        //store item from obj to string

                        //if product id is empty store N/A
                        if(infoObj.getString(Keys.KEY_ST_PRODUCT_ID).isEmpty()){
                            productId += "N/A\n";
                        }else{
                            productId += infoObj.getString(Keys.KEY_ST_PRODUCT_ID)+"\n";
                        }

                        //if product code is empty store N/A
                        if(infoObj.getString(Keys.KEY_ST_PRODUCT_CODE).isEmpty()){
                            productCode += "N/A\n";
                        }else{
                            productCode += infoObj.getString(Keys.KEY_ST_PRODUCT_CODE)+"\n";
                        }

                        productName += infoObj.getString(Keys.KEY_ST_PRODUCT_NAME)+"\n";
                        size += infoObj.getString(Keys.KEY_ST_SIZE)+"\n";
                        quantity += infoObj.getString(Keys.KEY_ST_QUANTITY)+"\n";
                        costprice += infoObj.getString(Keys.KEY_ST_COSTPRICE)+"\n";
                        sellingprice += infoObj.getString(Keys.KEY_ST_SELLINGPRICE)+"\n";
                    }

                    current.setProductId(productId);
                    current.setProductName(productName);
                    current.setProductCode(productCode);
                    current.setSize(size);
                    current.setQuantity(quantity);
                    current.setCostprice(costprice);
                    current.setSellingprice(sellingprice);

                    //add to list

                    list.add(current);
                }
            } else {//returned false
                SalesTrackerPojo pojo = new SalesTrackerPojo();
                pojo.setReturned(jsonObject.getBoolean(Keys.KEY_COM_RETURN));
                pojo.setMessage(jsonObject.getString(Keys.KEY_COM_MESSAGE));

                //add to list
                list.add(pojo);
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
