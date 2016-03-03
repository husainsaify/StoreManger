package com.hackerkernel.storemanager.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hackerkernel.storemanager.extras.Keys;
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
    private String KEY_USER_PASSWORD = "userpassword";

    //key To open navigation drawer for the first time user open the app
    public static String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    //Key TO show Listed & Non Listed app intro
    public static String KEY_ADD_SALES_APPINTRO = "add_sales_app_intro";

    //DEFAULT VALUE
    private String KEY_DEFAULT = Keys.KEY_DEFAULT;
    //member variables
    private static MySharedPreferences mInstance = null;
    private static SharedPreferences mSharedPreferences;
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
                .putString(KEY_USER_PASSWORD,password).apply();
        Log.d(TAG,"HUS: Saved data in sharedPref");
    }

    public void setUser(LoginPojo data){
        mSharedPreferences.edit()
                .putString(KEY_USER_ID,data.getId()+"")
                .putString(KEY_USER_FULLNAME,data.getName())
                .putString(KEY_USER_STORENAME,data.getStorename())
                .putString(KEY_USER_EMAIL,data.getEmail())
                .putString(KEY_USER_PHONE,data.getPhone())
                .putString(KEY_USER_PASSWORD,data.getPassword()).apply();
    }

    /*
    * Method to get user data from the shared Preferences
    * */
    public String getUserId(){
        return mSharedPreferences.getString(KEY_USER_ID,KEY_DEFAULT);
    }

    public String getUserFullname(){
        return mSharedPreferences.getString(KEY_USER_FULLNAME,KEY_DEFAULT);
    }
    public String getUserEmail(){
        return mSharedPreferences.getString(KEY_USER_EMAIL,KEY_DEFAULT);
    }
    public String getUserPhone(){
        return mSharedPreferences.getString(KEY_USER_PHONE,KEY_DEFAULT);
    }
    public String getUserStorename(){
        return mSharedPreferences.getString(KEY_USER_STORENAME,KEY_DEFAULT);
    }
    public String getData(String key){
        return mSharedPreferences.getString(key,KEY_DEFAULT);
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
        mSharedPreferences.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USER_FULLNAME)
                .remove(KEY_USER_STORENAME)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_PHONE)
                .remove(KEY_USER_PASSWORD)
                .apply();
    }

    /*
    * Method to set Boolean Values for (Drawer,Appintro etc)
    *
    * This method set the value to true for the given key
    * */
    public void setBooleanKey(String keyname){
        mSharedPreferences.edit().putBoolean(keyname, true).apply();
    }

    /*
    * THIS METHOD WILL GET THE BOOLEAN VALUE
    * TRUE = MEANS USER IS OLD TO THE APP
    * FALSE = MEANS USER IS NEW (OPEN DRAWER OR SHOW APP INTRO)
    * */
    public boolean getBooleanKey(String keyname){
        return mSharedPreferences.getBoolean(keyname,false);
    }


}
