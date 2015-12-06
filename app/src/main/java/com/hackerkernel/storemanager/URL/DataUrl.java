package com.hackerkernel.storemanager.URL;

/**
 * CLass to feed URLS
 */
public class DataUrl {
    private static final String SERVER = "http://192.168.56.1/";
    public static final String
            REGISTER_URL = SERVER + "co/storemanger/register.php",
            LOGIN_URL = SERVER +  "co/storemanger/login.php",
            ADD_CATEGORY = SERVER + "co/storemanger/addCategory.php",
            GET_CATEGORY = SERVER + "co/storemanger/categoryList.php",
            ADD_PRODUCT = SERVER + "co/storemanger/addProduct.php",
            GET_PRODUCT = SERVER + "co/storemanger/productList.php";
}
