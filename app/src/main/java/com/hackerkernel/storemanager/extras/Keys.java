package com.hackerkernel.storemanager.extras;

/**
 * a interface to store API Keys
 */
public interface Keys {
    //common Keys
    String KEY_COM_RETURN = "return";
    String KEY_COM_MESSAGE = "message";
    String KEY_COM_USERID = "user_id";
    //Login Keys params
    String KEY_LOGIN_EMAIL_PRAM = "email";
    String KEY_LOGIN_PASSWORD_PRAM = "password";
    //Signup keys params
    String KEY_SIGNUP_FN_PRAM = "fullname";
    String KEY_SIGNUP_STORENAME_PRAM = "storename";
    String KEY_SIGNUP_EMAIL_PRAM = "email";
    String KEY_SIGNUP_PHONE_PRAM = "phone";
    String KEY_SIGNUP_PASS_PRAM = "password";

    //login response
    String KEY_L_USER = "user",
            KEY_L_ID = "id",
            KEY_L_NAME = "name",
            KEY_L_STORENAME = "storename",
            KEY_L_EMAIL = "email",
            KEY_L_PHONE = "phone",
            KEY_L_PASSWORD = "password",
            KEY_L_REGISTER_AT = "register_at";
}