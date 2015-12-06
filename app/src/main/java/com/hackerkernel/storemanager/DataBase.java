package com.hackerkernel.storemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.hackerkernel.storemanager.pojo.LoginPojo;

/**
 * Database class to insert user in the database
 */
public class DataBase extends SQLiteOpenHelper {
    //create a tag vide
    private static final String TAG = DataBase.class.getSimpleName();
    //database version
    private static final int DATABASE_VERSION = 1;
    //database name
    private static final String DATABASE_NAME = "storemanager.db";
    //table structure
    private static final String TABLE_USER = "user",
                                COL_ID = "id",
                                COL_NAME = "name",
                                COL_EMAIL = "email",
                                COL_PHONE = "phone",
                                COL_PASSWORD = "password",
                                COL_REGISTERE_AT = "register_at",
                                COL_LAST_BILL_PAID = "last_bill_paid",
                                COL_NEXT_DUE_DATE = "next_due_date",
                                COL_ACTIVE = "active";
    //create a database variable
    SQLiteDatabase db;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE "+ TABLE_USER +" (" +
                COL_ID +" integer primary key not null," +
                COL_NAME +" text not null," +
                COL_EMAIL +" text not null," +
                COL_PHONE +" text not null," +
                COL_PASSWORD +" text not null," +
                COL_REGISTERE_AT+" text not null," +
                COL_LAST_BILL_PAID +" text not null," +
                COL_NEXT_DUE_DATE +" text not null," +
                COL_ACTIVE +" text not null" +
                ")";

        db.execSQL(CREATE_USER_TABLE);

        Log.d(TAG, "Table Created");
    }

    //insert user information in the database
    public void login(LoginPojo user){
        db = this.getWritableDatabase();
        // set values in the contentValues
        ContentValues values = new ContentValues();
        values.put(COL_ID, user.getId());
        values.put(COL_NAME, user.getName());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PHONE, user.getPhone());
        values.put(COL_PASSWORD, user.getPassword());
        values.put(COL_REGISTERE_AT, user.getRegisterAt());
        values.put(COL_LAST_BILL_PAID, user.getLastBillPaid());
        values.put(COL_NEXT_DUE_DATE, user.getNextDueDate());
        values.put(COL_ACTIVE, user.getActive());

        //insert into the database
        db.insert(TABLE_USER,null,values);
        db.close();
        Log.d(TAG,"Record inserted in the database");
    }

    //check user
    public boolean loginStatus(){
        db = this.getReadableDatabase();
        String q = "select * from "+ TABLE_USER;
        Cursor cursor = db.rawQuery(q, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    //delete user
    public int logout(){
        db = this.getWritableDatabase();

        return db.delete(TABLE_USER,null,null);
    }

    //get all user info
    public ArrayMap<String,String> getAllUserInfo(){
        //array list to store data
        ArrayMap<String,String> value = new ArrayMap<>();
        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from "+TABLE_USER, null);
        if (cursor.moveToFirst()){
            do{
                value.put("id",cursor.getString(0));
                value.put("name",cursor.getString(1));
                value.put("email",cursor.getString(2));
                value.put("phone",cursor.getString(3));
                value.put("password",cursor.getString(4));
                value.put("register_at",cursor.getString(5));
                value.put("last_bill_paid",cursor.getString(6));
                value.put("next_due_date",cursor.getString(7));
                value.put("active",cursor.getString(8));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return value;
    }

    //function to get user id
    public String getUserID(){
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id from "+TABLE_USER,null);
        String id = null;
        if(cursor.moveToFirst()){
            id = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return id;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table if factory version upgrads
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USER);
        //execute onCreate method
        onCreate(db);
    }
}
