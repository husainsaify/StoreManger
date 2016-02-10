package com.hackerkernel.storemanager.extras;

/**
 * a interface to store API Keys & PRAM
 */
public interface Keys {
    //common Keys
    String KEY_COM_RETURN = "return";
    String KEY_COM_MESSAGE = "message";
    String KEY_COM_USERID = "userId";
    String KEY_COM_DATA = "data";
    String KEY_COM_PRODUCTID = "productId";
    String KEY_COM_COUNT = "count";
    String KEY_COM_CATEGORYID = "categoryId";
    String KEY_COM_CATEGORYNAME = "categoryName";
    String KEY_DEFAULT = "";


    //Login Keys params
    String PRAM_LOGIN_EMAIL = "email";
    String PRAM_LOGIN_PASSWORD = "password";

    //Signup keys params
    String PRAM_SIGNUP_FN = "fullname";
    String PRAM_SIGNUP_STORENAME = "storename";
    String PRAM_SIGNUP_EMAIL = "email";
    String PRAM_SIGNUP_PHONE = "phone";
    String PRAM_SIGNUP_PASS = "password";

    //add salesman params
    String PRAM_AS_SALESMAN = "salesman";

    //login response
    String KEY_L_USER = "user",
            KEY_L_ID = "id",
            KEY_L_NAME = "name",
            KEY_L_STORENAME = "storename",
            KEY_L_EMAIL = "email",
            KEY_L_PHONE = "phone",
            KEY_L_PASSWORD = "password",
            KEY_L_REGISTER_AT = "register_at";

    //SimpleList response (category & salesman list)
    String KEY_SL_ID = "id",
            KEY_SL_NAME = "name",
            KEY_SL_USER_ID = "user_id",
            KEY_SL_TIME = "time",
            KEY_SL_COUNT = "count";

    //Add products
    String PRAM_AP_USERID = "userId",
            PRAM_AP_IMAGE = "pImage",
            PRAM_AP_NAME = "pName",
            PRAM_AP_CODE = "pCode",
            PRAM_AP_CP = "pCP",
            PRAM_AP_SP = "pSP",
            PRAM_AP_SIZE = "pSize",
            PRAM_AP_QUANTITY = "pQuantity";

    //product list
    String KEY_PL_NAME = "name",
            KEY_PL_IMAGE = "image",
            KEY_PL_CODE = "code",
            KEY_PL_TIME = "time";


    //Keys Product
    String KEY_P_ID = "id",
            KEY_P_NAME = "name",
            KEY_P_IMAGE = "image",
            KEY_P_CODE = "code",
            KEY_P_SIZE = "size",
            KEY_P_QUANTITY = "quantity",
            KEY_P_TIME = "time",
            KEY_P_CP = "cp",
            KEY_P_SP = "sp";

    //Add sales (Non listed)
    String PRAM_NON_LISTED_CUSTOMER_NAME = "customerName",
            PRAM_NON_LISTED_PRODUCTCODE = "productCode",
            PRAM_NON_LISTED_NAME = "name",
            PRAM_NON_LISTED_SIZE = "size",
            PRAM_NON_LISTED_QUANTITY = "quantity",
            PRAM_NON_LISTED_COSTPRICE = "costprice",
            PRAM_NON_LISTED_SELLINGPRICE = "sellingprice",
            PRAM_NON_LISTED_SALESMAN_ID = "salesmanId",
            PRAM_NON_LISTED_SALESMAN_NAME = "salesmanName";

    //Add sales (Listed)
    String PRAM_LISTED_SALES_TYPE = "salesType";

    //Auto Complete Product search PRAM
    String PRAM_AC_PRODUCTNAME = "productName";

    //Auto Complete Product search KEYS
    String KEY_AC_ID = "id",
            KEY_AC_NAME = "name",
            KEY_AC_CODE = "code",
            KEY_AC_CP = "CP",
            KEY_AC_SIZE = "size";

    //Sales Tracker Date KEYS
    String KEY_ST_DATELIST_DATE = "date",
            KEY_ST_DATELIST_DATE_ID = "date_id";

    //Sales Tracker KEYS
    String KEY_ST_TOTAL_COSTPRICE = "total_costprice",
            KEY_ST_TOTAL_SELLINGPRICE = "total_sellingprice",
            KEY_ST_SALES = "sales",
            KEY_ST_SALES_ID = "sales_id",
            KEY_ST_CUSTOMER_NAME = "customer_name",
            KEY_ST_SALESMAN_ID = "salesman_id",
            KEY_ST_SALESMAN_NAME = "salesman_name",
            KEY_ST_TIME = "time",
            KEY_ST_PRODUCT_ID = "product_id",
            KEY_ST_PRODUCT_NAME = "product_name",
            KEY_ST_PRODUCT_CODE = "product_code",
            KEY_ST_SIZE = "size",
            KEY_ST_QUANTITY = "quantity",
            KEY_ST_COSTPRICE = "costprice",
            KEY_ST_SELLINGPRICE = "sellingprice";
}
