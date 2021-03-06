package com.hackerkernel.storemanager.extras;

/**
 * CLass to feed URLS
 */
public class ApiUrl {
    //private static final String SERVER = "http://192.168.69.2/co/storemanger/";
    private static final String SERVER =
            "http://storemanager.hackerkernel.com/api/v1.0/";
    public static final String
            SIGNUP_URL = SERVER + "register.php",
            LOGIN_URL = SERVER + "login.php",
            ADD_CATEGORY = SERVER + "addCategory.php",
            GET_CATEGORY = SERVER + "categoryList.php",
            ADD_PRODUCT = SERVER + "addProduct.php",
            PRODUCT_LIST = SERVER + "productList.php",
            IMAGE_BASE_URL = SERVER + "",
            GET_PRODUCT = SERVER + "fetchProduct.php",
            DELETE_PRODUCT = SERVER + "deleteProduct.php",
            EDIT_PRODUCT = SERVER + "editProduct.php",
            AC_PRODUCT_SEARCH = SERVER + "autoCompleteProductSearch.php",
            ADD_SALES = SERVER + "addSales.php",
            SALES_TRACKER_DATE_LIST = SERVER + "salesTrackerDatelist.php",
            GET_SALES_TRACKER = SERVER + "salesTracker.php",
            PRODUCT_SEARCH = SERVER + "search.php",
            ADD_SALESMAN = SERVER + "addSalesman.php",
            GET_SALESMAN = SERVER + "salesmanList.php",
            EDIT_CATEGORY_NAME = SERVER + "edtCategory.php",
            DELETE_CATEGORY = SERVER + "deleteCategory.php",
            CAL_SALESMAN_COMMISSION = SERVER + "calculateCommission.php",
            GET_SALESMAN_SALES_DATE_LIST = SERVER + "salesmanSalesDateList.php",
            DELETE_SALES = SERVER + "deleteSales.php";
}

