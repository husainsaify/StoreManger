package com.hackerkernel.storemanager.extras;

/**
 * a interface to store API Keys
 */
public interface Keys {
    //common Keys
    String KEY_COM_RETURN = "return";
    String KEY_COM_MESSAGE = "message";
    String KEY_COM_USERID = "userId";
    String KEY_COM_DATA = "data";
    String KEY_DEFAULT = "";

    //Login Keys params
    String KEY_LOGIN_EMAIL_PRAM = "email";
    String KEY_LOGIN_PASSWORD_PRAM = "password";

    //Signup keys params
    String KEY_SIGNUP_FN_PRAM = "fullname";
    String KEY_SIGNUP_STORENAME_PRAM = "storename";
    String KEY_SIGNUP_EMAIL_PRAM = "email";
    String KEY_SIGNUP_PHONE_PRAM = "phone";
    String KEY_SIGNUP_PASS_PRAM = "password";

    //add category params
    String KEY_AC_CATEGORY_NAME_PRAM = "categoryName";

    //add salesman params
    String KEY_AS_SALESMAN_PRAM = "salesman";

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
    String KEY_AP_CATEGORYNAME = "categoryName",
            KEY_AP_CATEGORYID = "categoryId",
            KEY_AP_USERID = "userId",
            KEY_AP_IMAGE = "pImage",
            KEY_AP_NAME = "pName",
            KEY_AP_CODE = "pCode",
            KEY_AP_CP = "pCP",
            KEY_AP_SP = "pSP",
            KEY_AP_SIZE = "pSize",
            KEY_AP_QUANTITY = "pQuantity";
}
