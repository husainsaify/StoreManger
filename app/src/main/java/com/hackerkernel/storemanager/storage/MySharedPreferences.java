package com.hackerkernel.storemanager.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hackerkernel.storemanager.pojo.LoginPojo;

/**
 * Class to handle shared prefrences data
 */
public class MySharedPreferences {
    private static final String TAG = MySharedPreferences.class.getSimpleName();
    //keys
    private String PREFERENCE_NAME = "storemanager";
    private String KEY_USER_ID = "userid";
    private String KEY_USER_FULLNAME = "userfullname";
    private String KEY_USER_STORENAME = "userstorename";
    private String KEY_USER_EMAIL = "useremail";
    private String KEY_USER_PHONE = "userphonenumber";
    private String KEY_USER_PASWWORD = "userpassword";
    private String KEY_DEFAULT = "";
    //member variables
    private static MySharedPreferences mInstance = null;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mSPEditor;
    private static Context mContext;

    private MySharedPreferences(){
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context context){
        mContext = context;
        if(mInstance == null){
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    //method to set a user login
    public void setUser(String userId,String fullname,String storename,String email,String phone,String password){
        mSharedPreferences.edit()
                .putString(KEY_USER_ID,userId)
                .putString(KEY_USER_FULLNAME,fullname)
                .putString(KEY_USER_STORENAME,storename)
                .putString(KEY_USER_EMAIL,email)
                .putString(KEY_USER_PHONE,phone)
                .putString(KEY_USER_PASWWORD,password).apply();
        Log.d(TAG,"HUS: Saved data in sharedPref");
    }

    public void setUser(LoginPojo data){
        mSharedPreferences.edit()
                .putString(KEY_USER_ID,data.getId()+"")
                .putString(KEY_USER_FULLNAME,data.getName())
                .putString(KEY_USER_STORENAME,data.getStorename())
                .putString(KEY_USER_EMAIL,data.getEmail())
                .putString(KEY_USER_PHONE,data.getPhone())
                .putString(KEY_USER_PASWWORD,data.getPassword()).apply();
    }

    /*
    * Method to check some user is logged in or not
    * */
    public boolean checkUser(){
        String userId = mSharedPreferences.getString(KEY_USER_ID, KEY_DEFAULT);
        if(userId.equals(KEY_DEFAULT)){
            return false;//no user
        }
        return true; //user found;
    }

    public void deleteUser(){
        mSharedPreferences.edit().clear().apply();
    }
}
