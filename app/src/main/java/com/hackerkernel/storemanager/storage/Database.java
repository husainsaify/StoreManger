package com.hackerkernel.storemanager.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hackerkernel.storemanager.pojo.ProductListPojo;
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
    public void insertProductList(List<ProductListPojo> list) {
        SQLiteDatabase sqlitedatabase = helper.getWritableDatabase();
        Log.d(TAG, "HUS: insertProductList");
        //Insert data to table only when SimpleList is not null
        if(list != null){
            for (int i = 0; i < list.size(); i++)
            {
                ProductListPojo p = list.get(i);
                ContentValues contentvalues = new ContentValues();
                contentvalues.put(DatabaseHelper.COL_PL_PRODUCT_ID, p.getProductId());
                contentvalues.put(DatabaseHelper.COL_PL_USER_ID, p.getUserId());
                contentvalues.put(DatabaseHelper.COL_PL_CATEGORY_ID, p.getCategoryId());
                contentvalues.put(DatabaseHelper.COL_PL_NAME, p.getProductName());
                contentvalues.put(DatabaseHelper.COL_PL_CODE, p.getProductCode());
                contentvalues.put(DatabaseHelper.COL_PL_TIME, p.getProductTime());

                sqlitedatabase.insert(DatabaseHelper.TABLE_PRODUCT_LIST, null, contentvalues);
            }
        }

    }

    public int deleteProductList(String userId, String categoryId){
        SQLiteDatabase db = helper.getWritableDatabase();
        String where = DatabaseHelper.COL_PL_USER_ID+"=? AND "+DatabaseHelper.COL_PL_CATEGORY_ID+"=?";
        String[] whereArgs = {userId,categoryId};
        return db.delete(DatabaseHelper.TABLE_PRODUCT_LIST,where,whereArgs);
    }

    public List<ProductListPojo> getProductList(String userId,String categoryId){
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] col = {DatabaseHelper.COL_PL_PRODUCT_ID,DatabaseHelper.COL_PL_NAME,DatabaseHelper.COL_PL_CODE,DatabaseHelper.COL_PL_TIME};
        String where = DatabaseHelper.COL_PL_USER_ID+"=? AND "+DatabaseHelper.COL_PL_CATEGORY_ID+"=?";
        String[] whereArgs = {userId,categoryId};

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCT_LIST,col,where,whereArgs,null,null,null);
        //check cursor is valid
        if((cursor != null) && (cursor.getCount() > 0)){
            List<ProductListPojo> list = new ArrayList<>();
            while (cursor.moveToNext()){
                //get Data
                String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PL_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PL_NAME));
                String code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PL_CODE));
                String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PL_TIME));

                //set data
                ProductListPojo p = new ProductListPojo();
                p.setProductId(id);
                p.setProductName(name);
                p.setProductImage(""); //Leave image blank so that we display default image
                p.setProductTime(time);
                p.setProductCode(code);

                //add Model to the list
                list.add(p);
            }

            //close Cursor
            cursor.close();
            return list;
        }else{ //if cursor is empty return null
            //close cursor
            assert cursor != null;
            cursor.close();
            return null;
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper{
        //Database Schema class
        private static final int DATABASE_VERSION = 5;
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

        private static final String TABLE_PRODUCT = "product",
                COL_P_ID = "_id",
                COL_P_USER_ID = "user_id",
                COL_P_PRODUCT_ID = "product_id",
                COL_P_NAME = "name",
                COL_P_IMAGE_ADDRESS = "image_address",
                COL_P_IMAGE_URI = "image_uri",
                COL_P_CODE = "code",
                COL_P_CP = "cp",
                COL_P_SP = "sp",
                COL_P_TIME = "time";

        private static final String TABLE_SQ = "sq",
                COL_SQ_ID = "_id",
                COL_SQ_SIZE = "size",
                COL_SQ_QUANTITY = "quantity",
                COL_SQ_USER_ID = "user_id",
                COL_SQ_PRODUCT_ID = "product_id";

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
                COL_PL_PRODUCT_ID + " INTEGER ," +
                COL_PL_USER_ID + " INTEGER," +
                COL_PL_CATEGORY_ID + " INTEGER," +
                COL_PL_NAME + " VARCHAR(20),"+
                COL_PL_CODE +" TEXT," +
                COL_PL_TIME +" TEXT);";

        private String CREATE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + "(" +
                COL_P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_P_USER_ID +" INTEGER," +
                COL_P_PRODUCT_ID +" INTEGER," +
                COL_P_NAME + " TEXT," +
                COL_P_IMAGE_ADDRESS + " TEXT," +
                COL_P_IMAGE_URI + " TEXT," +
                COL_P_CODE + " TEXT," +
                COL_P_CP + " TEXT," +
                COL_P_SP + " TEXT," +
                COL_P_TIME + " TEXT);";

        private String CREATE_SQ = "CREATE TABLE "+ TABLE_SQ +"(" +
                COL_SQ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_SQ_SIZE + " INTEGER," +
                COL_SQ_QUANTITY + " INTEGER," +
                COL_SQ_USER_ID + " INTEGER," +
                COL_SQ_PRODUCT_ID + " INTEGER);";

        /*
        * DROP TABLE QUERY
        * */
        private String DROP_SALESMAN = "DROP TABLE IF EXISTS "+TABLE_SALESMAN;
        private String DROP_CATEGORY = "DROP TABLE IF EXISTS "+TABLE_CATEGORY;
        private String DROP_PRODUCT_LIST = "DROP TABLE IF EXISTS "+TABLE_PRODUCT_LIST;
        private String DROP_PRODUCT = "DROP TABLE IF EXISTS "+TABLE_PRODUCT;
        private String DROP_SQ = "DROP TABLE IF EXISTS "+TABLE_SQ;

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
            db.execSQL(CREATE_PRODUCT);
            db.execSQL(CREATE_SQ);
            Log.d(TAG, "HUS: onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //drop table if factory version upgrade
            db.execSQL(DROP_SALESMAN);
            db.execSQL(DROP_CATEGORY);
            db.execSQL(DROP_PRODUCT_LIST);
            db.execSQL(DROP_PRODUCT);
            db.execSQL(DROP_SQ);
            Log.d(TAG, "HUS: onUpgrade");
            //Call onCreate to recreate tables
            onCreate(db);
        }

        /************************************ TABLE_PRODUCT
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
         public void addProduct(ProductPojo product) {
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
         public ProductPojo getProduct(String productId) {
         db = this.getReadableDatabase();
         String q = "select * from " + TABLE_PRODUCT + " where " + COL_P_ID + " = ?";
         Cursor cursor = db.rawQuery(q, new String[]{productId});
         //their is result in table
         if (cursor.moveToFirst()) {
         //crete a instance "ProductPojo"
         ProductPojo product = new ProductPojo();
         //store Value from database into my ProductPojo
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