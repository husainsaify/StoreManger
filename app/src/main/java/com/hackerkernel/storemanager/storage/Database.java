package com.hackerkernel.storemanager.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hackerkernel.storemanager.pojo.ProductPojo;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Database class to insert user in the database
 */
public class Database {
    /*
    * public variables to store SimpleList TABLE NAME
    * */
    public static final String SALESMAN = DatabaseHelper.TABLE_SALESMAN;
    public static final String CATEGORY = DatabaseHelper.TABLE_CATEGORY;

    //Make a global variable of DatabaseHelper class
    DatabaseHelper helper;
    private final String TAG = Database.class.getSimpleName();
    public Database(Context context){
        //Instantiate DatabaseHelper
        helper = new DatabaseHelper(context);
    }

    public void insertAllSimpleList(String tablename,List<SimpleListPojo> list) {
        SQLiteDatabase sqlitedatabase = helper.getWritableDatabase();
        Log.d(TAG, "HUS: insertAllSimpleList");
        //Insert data to table only when SimpleList is not null
        if(list != null){
            for (int i = 0; i < list.size(); i++)
            {
                SimpleListPojo simplelistpojo = list.get(i);
                ContentValues contentvalues = new ContentValues();
                contentvalues.put(DatabaseHelper.COL_S_ID, simplelistpojo.getId());
                contentvalues.put(DatabaseHelper.COL_S_NAME, simplelistpojo.getName());
                contentvalues.put(DatabaseHelper.COL_S_USER_ID, simplelistpojo.getUser_id());
                contentvalues.put(DatabaseHelper.COL_S_TIME, simplelistpojo.getTime());
                sqlitedatabase.insert(tablename, null, contentvalues);
            }
        }

    }

    public int deleteAllSimpleList(String tablename) {
        Log.d(TAG,"HUS: tablename "+tablename);
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.d(TAG, "HUS: deleteAllSimpleList");
        return db.delete(tablename, null, null);
    }

    public List<SimpleListPojo> getAllSimpleList(String table,String userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.d(TAG, "HUS: getAllSimpleList");
        String[] col = {DatabaseHelper.COL_S_ID,DatabaseHelper.COL_S_NAME,DatabaseHelper.COL_S_TIME};
        String[] selectionArgs = {userId};
        Cursor c = db.query(table,col,DatabaseHelper.COL_S_USER_ID + " = ?",selectionArgs,null,null,null);
        List<SimpleListPojo> list = new ArrayList<>();
        while (c.moveToNext()) {
            //retive data
            String id = c.getString(c.getColumnIndex(DatabaseHelper.COL_S_ID));
            String name = c.getString(c.getColumnIndex(DatabaseHelper.COL_S_NAME));
            String time = c.getString(c.getColumnIndex(DatabaseHelper.COL_S_TIME));
            //store data in simple list pojo
            SimpleListPojo pojo = new SimpleListPojo();
            pojo.setId(id);
            pojo.setName(name);
            pojo.setTime(time);

            //add pojo to list
            list.add(pojo);
        }

        c.close();
        return list;
    }

    /*
    * Product List
    * */

    private class DatabaseHelper extends SQLiteOpenHelper{
        //Database Schema class
        private static final int DATABASE_VERSION = 3;
        private static final String DATABASE_NAME = "storemanager";
        private final String TAG = DatabaseHelper.class.getSimpleName();
        /*
        * Table Structures
        * */

        //Salesman table
        private static final String TABLE_SALESMAN = "salesman",
                COL_S_ID = "_id",
                COL_S_NAME = "name",
                COL_S_USER_ID = "user_id",
                COL_S_TIME = "time";

        //Category table
        private static final String TABLE_CATEGORY = "category",
                COL_C_ID = "_id",
                COL_C_NAME = "name",
                COL_C_USER_ID = "user_id",
                COL_C_TIME = "time";

        //ProductList table
        private static final String TABLE_PRODUCT_LIST = "product_list",
                COL_PL_ID = "_id",
                COL_PL_PRODUCT_ID = "product_id",
                COL_PL_USER_ID = "user_id",
                COL_PL_CATEGORY_ID = "category_id",
                COL_PL_NAME = "name",
                COL_PL_CODE = "code",
                COL_PL_TIME = "time";

        /*
        * CREATE TABLE QUERY
        * */
        private String CREATE_SALESMAN = "CREATE TABLE " + TABLE_SALESMAN + "(" +
                COL_S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_S_NAME + " VARCHAR(50)," +
                COL_S_USER_ID + " INTEGER," +
                COL_S_TIME + " VARCHAR(20));";

        private String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "(" +
                COL_C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_C_NAME + " VARCHAR(50)," +
                COL_C_USER_ID + " INTEGER," +
                COL_C_TIME + " VARCHAR(20));";

        private String CREATE_PRODUCT_LIST = "CREATE TABLE "+ TABLE_PRODUCT_LIST + "(" +
                COL_PL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PL_PRODUCT_ID + "INTEGER ," +
                COL_PL_USER_ID + " INTEGER," +
                COL_PL_CATEGORY_ID + " INTEGER," +
                COL_PL_NAME + " VARCHAR(20),"+
                COL_PL_CODE +" TEXT," +
                COL_PL_TIME +" TEXT);";

        /*
        * DROP TABLE QUERY
        * */
        private String DROP_SALESMAN = "DROP TABLE IF EXISTS "+TABLE_SALESMAN;
        private String DROP_CATEGORY = "DROP TABLE IF EXISTS "+TABLE_CATEGORY;
        private String DROP_PRODUCT_LIST = "DROP TABLE IF EXISTS "+TABLE_PRODUCT_LIST;

        /*private static final String TABLE_USER = "user",
                COL_ID = "id",
                COL_NAME = "name",
                COL_EMAIL = "email",
                COL_PHONE = "phone",
                COL_PASSWORD = "password",
                COL_REGISTER_AT = "register_at",
                COL_LAST_BILL_PAID = "last_bill_paid",
                COL_NEXT_DUE_DATE = "next_due_date",
                COL_ACTIVE = "active";
        //product Table structure
        private static final String TABLE_PRODUCT = "product",
                COL_P_ID = "p_id",
                COL_P_NAME = "p_name",
                COL_P_IMAGE_ADDRESS = "p_image_address",
                COL_P_IMAGE_URI = "p_image_uri",
                COL_P_CODE = "p_code",
                COL_P_CP = "p_cp",
                COL_P_SP = "p_sp",
                COL_P_TIME = "p_time";
        //table sq
        private static final String TABLE_SQ = "sq",
                COL_SQ_ID = "id",
                COL_SQ_SIZE = "size",
                COL_SQ_QUANTITY = "quantity",
                COL_SQ_USER_ID = "user_id",
                COL_SQ_PRODUCT_ID = "product_id";*/

        /*String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "(" +
                COL_ID + " integer primary key not null," +
                COL_NAME + " text not null," +
                COL_EMAIL + " text not null," +
                COL_PHONE + " text not null," +
                COL_PASSWORD + " text not null," +
                COL_REGISTER_AT + " text not null," +
                COL_LAST_BILL_PAID + " text not null," +
                COL_NEXT_DUE_DATE + " text not null," +
                COL_ACTIVE + " text not null" +
                ")";
        //table for product
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "(" +
                COL_P_ID + " integer primary key not null," +
                COL_P_NAME + " text not null," +
                COL_P_IMAGE_ADDRESS + " text not null," +
                COL_P_IMAGE_URI + " text not null," +
                COL_P_CODE + " text not null," +
                COL_P_CP + " text not null," +
                COL_P_SP + " text not null," +
                COL_P_TIME + " text not null" +
                ")";
        //create SQ table
        String CREATE_SQ_TABLE = "CREATE TABLE "+TABLE_SQ+"(" +
                COL_SQ_ID + " integer primary key autoincrement," +
                COL_SQ_SIZE + " integer not null," +
                COL_SQ_QUANTITY + " integer not null," +
                COL_SQ_USER_ID + " integer not null," +
                COL_SQ_PRODUCT_ID + " integer not null" +
                ")";*/

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG,"HUS: Constructor");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //create table
            db.execSQL(CREATE_SALESMAN);
            db.execSQL(CREATE_CATEGORY);
            db.execSQL(CREATE_PRODUCT_LIST);
            Log.d(TAG, "HUS: onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //drop table if factory version upgrade
            db.execSQL(DROP_SALESMAN);
            db.execSQL(DROP_CATEGORY);
            db.execSQL(DROP_PRODUCT_LIST);
            Log.d(TAG, "HUS: onUpgrade");
            //Call onCreate to recreate tables
            onCreate(db);
        }

        /*/*//*************************** TABLE_USER
         //insert user information in the database
         public void login(LoginPojo user) {
         db = this.getWritableDatabase();
         // set values in the contentValues
         ContentValues values = new ContentValues();
         values.put(COL_ID, user.getId());
         values.put(COL_NAME, user.getName());
         values.put(COL_EMAIL, user.getEmail());
         values.put(COL_PHONE, user.getPhone());
         values.put(COL_PASSWORD, user.getPassword());
         values.put(COL_REGISTER_AT, user.getRegisterAt());
         values.put(COL_ACTIVE, user.getActive());
         //insert into the database
         db.insert(TABLE_USER, null, values);
         db.close();
         }
         //check user
         public boolean loginStatus() {
         db = this.getReadableDatabase();
         String q = "select * from " + TABLE_USER;
         Cursor cursor = db.rawQuery(q, null);
         if (cursor.getCount() <= 0) {
         cursor.close();
         db.close();
         return false;
         }
         cursor.close();
         db.close();
         return true;
         }
         //delete user
         public int logout() {
         db = this.getWritableDatabase();
         return db.delete(TABLE_USER, null, null);
         }
         //get all user info
         public ArrayMap<String, String> getAllUserInfo() {
         //array list to store data
         ArrayMap<String, String> value = new ArrayMap<>();
         db = this.getReadableDatabase();
         Cursor cursor = db.rawQuery("select * from " + TABLE_USER, null);
         if (cursor.moveToFirst()) {
         do {
         value.put("id", cursor.getString(0));
         value.put("name", cursor.getString(1));
         value.put("email", cursor.getString(2));
         value.put("phone", cursor.getString(3));
         value.put("password", cursor.getString(4));
         value.put("register_at", cursor.getString(5));
         value.put("last_bill_paid", cursor.getString(6));
         value.put("next_due_date", cursor.getString(7));
         value.put("active", cursor.getString(8));
         } while (cursor.moveToNext());
         }
         cursor.close();
         db.close();
         return value;
         }
         //function to get user id
         public String getUserID() {
         db = this.getReadableDatabase();
         Cursor cursor = db.rawQuery("select id from " + TABLE_USER, null);
         String id = null;
         if (cursor.moveToFirst()) {
         id = cursor.getString(0);
         }
         cursor.close();
         db.close();
         return id;
         }
         /*//************************************ TABLE_PRODUCT
         //check  product exits in the database
         public boolean checkProduct(String productId) {
         db = this.getReadableDatabase();
         String q = "select * from " + TABLE_PRODUCT + " where " + COL_P_ID + " = ?";
         Cursor cursor = db.rawQuery(q, new String[]{productId});
         //if product not found
         if (cursor.getCount() <= 0) {
         cursor.close();
         db.close();
         return false;
         }
         //if product found
         cursor.close();
         db.close();
         return true;
         }
         //Method to insert new Product in product table
         public void addProduct(SingleProductPojo product) {
         db = this.getWritableDatabase();
         //store values
         ContentValues cv = new ContentValues();
         cv.put(COL_P_ID, product.getId());
         cv.put(COL_P_NAME, product.getName());
         cv.put(COL_P_IMAGE_ADDRESS, product.getImageAddress());
         cv.put(COL_P_IMAGE_URI, "");
         cv.put(COL_P_CODE, product.getCode());
         cv.put(COL_P_SP, product.getSp());
         cv.put(COL_P_CP, product.getCp());
         cv.put(COL_P_TIME, product.getTime());
         //insert into productTable
         db.insert(TABLE_PRODUCT, null, cv);
         db.close();
         }
         //function to fetch all the product Information
         public SingleProductPojo getProduct(String productId) {
         db = this.getReadableDatabase();
         String q = "select * from " + TABLE_PRODUCT + " where " + COL_P_ID + " = ?";
         Cursor cursor = db.rawQuery(q, new String[]{productId});
         //their is result in table
         if (cursor.moveToFirst()) {
         //crete a instance "SingleProductPojo"
         SingleProductPojo product = new SingleProductPojo();
         //store Value from database into my SingleProductPojo
         product.setId(cursor.getString(0));
         product.setName(cursor.getString(1));
         product.setImageAddress(cursor.getString(2));
         product.setImageUri(cursor.getString(3));
         product.setCode(cursor.getString(4));
         product.setCp(cursor.getString(5));
         product.setSp(cursor.getString(6));
         product.setTime(cursor.getString(7));
         //get size and Quantity stack from Member methods "getSize" & "getQuanity"
         String userId = this.getUserID(); //get userId from member method
         product.setSize(this.getSize(userId, productId));
         product.setQuantity(this.getQuantity(userId,productId));
         //close cursor & db
         cursor.close();
         db.close();
         //return the Product
         return product;
         }
         return null;
         }
         //method to delete product from the database
         public int deleteProduct(String productId){
         db = this.getWritableDatabase();
         //delete from TABLE_PRODUCT
         int result = db.delete(TABLE_PRODUCT,COL_P_ID+"=?",new String[] {productId});
         //delete from TABLE_SQ
         db.delete(TABLE_SQ,COL_SQ_PRODUCT_ID+"=?",new String[]{productId});
         db.close();
         return result;
         }
         //method to delete all product from the database
         public int deleteAllProduct(){
         db = this.getWritableDatabase();
         int result = db.delete(TABLE_PRODUCT,null,null);
         db.close();
         return result;
         }
         //store product image Uri into database
         *//*
    * This method will take productId and image Uri
    * and store it in the prooduct table for later use
    * *//*
        public void addProductImageUri(String productId, Uri productUri){
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_P_IMAGE_URI, String.valueOf(productUri));
            db.update(TABLE_PRODUCT, cv, COL_P_ID + "=?", new String[]{productId});
            db.close();
        }
        //get product image uri from database
        public Uri getProductImageUri(String productId){
            db = this.getWritableDatabase();
            String q = "SELECT "+COL_P_IMAGE_URI+" FROM "+TABLE_PRODUCT+" WHERE "+COL_P_ID+"=?";
            Cursor cursor = db.rawQuery(q, new String[]{productId});
            if(cursor.moveToFirst()){
                String imageUri = cursor.getString(0);
                return Uri.parse(imageUri);
            }
            return null;
        }
        /*//********************************** TABLE SQ
         //this method is used to insert SQ
         public boolean addSQ(String size,String quantity,String userId, String productId){
         db = this.getWritableDatabase();
         ContentValues cv = new ContentValues();
         cv.put(COL_SQ_SIZE,size);
         cv.put(COL_SQ_QUANTITY,quantity);
         cv.put(COL_SQ_USER_ID,userId);
         cv.put(COL_SQ_PRODUCT_ID,productId);
         long r = db.insert(TABLE_SQ,null,cv);
         if(r == -1){
         db.close();
         return false;
         }
         //close db
         db.close();
         return true;
         }
         *//*
        * getSize & getQuantity will return size | Quantity in StringStack
        * This method will ge used by "db.getProduct" method
        * *//*
        private String getSize(String userId,String productId){
            db = this.getReadableDatabase();
            String q = "SELECT "+COL_SQ_SIZE+" FROM "+TABLE_SQ+" WHERE "+COL_SQ_USER_ID+"=? AND "+COL_SQ_PRODUCT_ID+"=?";
            Cursor cursor = db.rawQuery(q, new String[]{userId, productId});
            int i = 0;
            if (cursor.moveToFirst()){
                String size = "";
                do{
                    i++;
                    size += cursor.getString(cursor.getColumnIndex(COL_SQ_SIZE));
                    if(i < cursor.getCount()){
                        size += "\n";
                    }
                }while (cursor.moveToNext());
                db.close();
                return size;
            }
            db.close();
            return null;
        }
        private String getQuantity(String userId,String productId){
            db = this.getReadableDatabase();
            String q = "SELECT "+COL_SQ_QUANTITY+" FROM "+TABLE_SQ+" WHERE "+COL_SQ_USER_ID+"=? AND "+COL_SQ_PRODUCT_ID+"=?";
            Cursor cursor = db.rawQuery(q, new String[]{userId, productId});
            int i = 0;
            if (cursor.moveToFirst()){
                String quantity = "";
                do{
                    i++;
                    quantity += cursor.getString(cursor.getColumnIndex(COL_SQ_QUANTITY));
                    if(i < cursor.getCount()){
                        quantity += "\n";
                    }
                }while (cursor.moveToNext());
                db.close();
                return quantity;
            }
            db.close();
            return null;
        }
        public void deleteAllSQ(){
            db = this.getWritableDatabase();
            db.delete(TABLE_SQ,null,null);
            db.close();
        }*/
    }
}