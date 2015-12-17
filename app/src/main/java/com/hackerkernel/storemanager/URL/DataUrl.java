package com.hackerkernel.storemanager.URL;

/**
 * CLass to feed URLS
 */
public class DataUrl {
    private static final String SERVER = "http://192.168.69.2/";
    public static final String
            REGISTER_URL = SERVER + "co/storemanger/register.php",
            LOGIN_URL = SERVER +  "co/storemanger/login.php",
            ADD_CATEGORY = SERVER + "co/storemanger/addCategory.php",
            GET_CATEGORY = SERVER + "co/storemanger/categoryList.php",
            ADD_PRODUCT = SERVER + "co/storemanger/addProduct.php",
            GET_PRODUCT = SERVER + "co/storemanger/productList.php",
            IMAGE_BASE_URL = SERVER + "co/storemanger/",
            GET_SINGLE_PRODUCT = SERVER + "co/storemanger/fetchProduct.php",
            DELETE_PRODUCT = SERVER + "co/storemanger/deleteProduct.php",
            PRODUCT_SEARCH = SERVER + "co/storemanger/searchProduct.php",
            ADD_SELL = SERVER + "co/storemanger/addSell.php";
                

        /*private static final String SERVER = "http://demo.hackerkernel.com/";
        public static final String
                REGISTER_URL = SERVER + "storemanger/register.php",
                LOGIN_URL = SERVER +  "storemanger/login.php",
                ADD_CATEGORY = SERVER + "storemanger/addCategory.php",
                GET_CATEGORY = SERVER + "storemanger/categoryList.php",
                ADD_PRODUCT = SERVER + "storemanger/addProduct.php",
                GET_PRODUCT = SERVER + "storemanger/productList.php",
                IMAGE_BASE_URL = SERVER + "storemanger/",
                GET_SINGLE_PRODUCT = SERVER + "storemanger/fetchProduct.php",
                DELETE_PRODUCT = SERVER + "storemanger/deleteProduct.php",
                PRODUCT_SEARCH = SERVER + "storemanger/searchProduct.php";*/
}
