package com.hackerkernel.storemanager.extras;

/**
 * CLass to feed URLS
 */
public class ApiUrl {
        private static final String SERVER = "http://192.168.69.2/co/storemanger/";
        //private static final String SERVER = "http://storemanager.hackerkernel.com/";
        public static final String
                SIGNUP_URL = SERVER + "register.php",
                LOGIN_URL = SERVER +  "login.php",
                ADD_CATEGORY = SERVER + "addCategory.php",
                GET_CATEGORY = SERVER + "categoryList.php",
                ADD_PRODUCT = SERVER + "addProduct.php",
                PRODUCT_LIST = SERVER + "productList.php",
                IMAGE_BASE_URL = SERVER + "",
                GET_SINGLE_PRODUCT = SERVER + "fetchProduct.php",
                DELETE_PRODUCT = SERVER + "deleteProduct.php",
                AC_PRODUCT_SEARCH = SERVER + "searchProduct.php",
                ADD_SELL = SERVER + "addSell.php",
                SALES_TRACKER_DATE_LIST = SERVER + "salesTrackerDatelist.php",
                GET_SALES_TRACKER = SERVER + "salesTracker.php",
                PRODUCT_SEARCH = SERVER + "search.php",
                ADD_SALESMAN = SERVER + "addSalesman.php",
                GET_SALESMAN = SERVER + "salesmanList.php";

}
