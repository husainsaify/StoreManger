package com.hackerkernel.storemanager.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.hackerkernel.storemanager.pojo.ProductListPojo;
import com.hackerkernel.storemanager.pojo.ProductPojo;
import com.hackerkernel.storemanager.pojo.SalesTrackerDatePojo;
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

    public Database(Context context) {
        //Instantiate DatabaseHelper
        helper = new DatabaseHelper(context);
    }

    //***************************** SimpleList

    //Insert all the simple list in db
    public void insertAllSimpleList(String tablename, List<SimpleListPojo> list) {
        SQLiteDatabase sqlitedatabase = helper.getWritableDatabase();
        Log.d(TAG, "HUS: insertAllSimpleList");
        //Insert data to table only when SimpleList is not null
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
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

    //delete all SimpleList from db
    public int deleteAllSimpleList(String tablename) {
        Log.d(TAG, "HUS: tablename " + tablename);
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.d(TAG, "HUS: deleteAllSimpleList");
        return db.delete(tablename, null, null);
    }

    //get all simple list from db
    public List<SimpleListPojo> getAllSimpleList(String table, String userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.d(TAG, "HUS: getAllSimpleList");
        String[] col = {DatabaseHelper.COL_S_ID, DatabaseHelper.COL_S_NAME, DatabaseHelper.COL_S_TIME};
        String[] selectionArgs = {userId};
        Cursor c = db.query(table, col, DatabaseHelper.COL_S_USER_ID + " = ?", selectionArgs, null, null, null);
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

    //******************************** Product List

    //insert all product list in db
    public void insertProductList(List<ProductListPojo> list) {
        SQLiteDatabase sqlitedatabase = helper.getWritableDatabase();
        Log.d(TAG, "HUS: insertProductList");
        //Insert data to table only when SimpleList is not null
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
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

    //delete all Product list from db
    public int deleteProductList(String userId, String categoryId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String where = DatabaseHelper.COL_PL_USER_ID + "=? AND " + DatabaseHelper.COL_PL_CATEGORY_ID + "=?";
        String[] whereArgs = {userId, categoryId};
        return db.delete(DatabaseHelper.TABLE_PRODUCT_LIST, where, whereArgs);
    }

    //get all product list from db
    public List<ProductListPojo> getProductList(String userId, String categoryId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] col = {DatabaseHelper.COL_PL_PRODUCT_ID, DatabaseHelper.COL_PL_NAME, DatabaseHelper.COL_PL_CODE, DatabaseHelper.COL_PL_TIME};
        String where = DatabaseHelper.COL_PL_USER_ID + "=? AND " + DatabaseHelper.COL_PL_CATEGORY_ID + "=?";
        String[] whereArgs = {userId, categoryId};

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCT_LIST, col, where, whereArgs, null, null, null);
        //check cursor is valid
        if ((cursor != null) && (cursor.getCount() > 0)) {
            List<ProductListPojo> list = new ArrayList<>();
            while (cursor.moveToNext()) {
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
        } else { //if cursor is empty return null
            //close cursor
            assert cursor != null;
            cursor.close();
            return null;
        }
    }

    //************************** Product

    //Method to insert new Product in product table
    public void insertProduct(ProductPojo product) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //store values
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_P_USER_ID, product.getUserId());
        cv.put(DatabaseHelper.COL_P_PRODUCT_ID, product.getId());
        cv.put(DatabaseHelper.COL_P_NAME, product.getName());
        cv.put(DatabaseHelper.COL_P_IMAGE_ADDRESS, product.getImageAddress());
        cv.put(DatabaseHelper.COL_P_CODE, product.getCode());
        cv.put(DatabaseHelper.COL_P_CP, product.getCp());
        cv.put(DatabaseHelper.COL_P_SP, product.getSp());
        cv.put(DatabaseHelper.COL_P_TIME, product.getTime());
        //insert into productTable
        db.insert(DatabaseHelper.TABLE_PRODUCT, null, cv);
        db.close();
    }

    //method to get product information
    public ProductPojo getProduct(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseHelper.COL_P_NAME,
                DatabaseHelper.COL_P_IMAGE_ADDRESS,
                DatabaseHelper.COL_P_CODE,
                DatabaseHelper.COL_P_TIME,
                DatabaseHelper.COL_P_CP,
                DatabaseHelper.COL_P_SP};
        String selection = DatabaseHelper.COL_P_USER_ID + "=? AND " + DatabaseHelper.COL_P_PRODUCT_ID + "=?";
        String[] selectionArgs = {userId, productId};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCT, columns, selection, selectionArgs, null, null, null);

        //their is result in table
        if (cursor.moveToFirst()) {
            //crete a instance "ProductPojo"
            ProductPojo product = new ProductPojo();
            //get value from the database
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_NAME));
            String imageAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_IMAGE_ADDRESS));
            String code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_CODE));
            String cp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_CP));
            String sp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_SP));
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_TIME));
            //get size and Quantity stack from Member methods
            String size = this.getSize(userId, productId);
            String quantity = this.getQuantity(userId, productId);

            product.setName(name);
            product.setImageAddress(imageAddress);
            product.setCode(code);
            product.setCp(cp);
            product.setSp(sp);
            product.setTime(time);
            product.setSize(size);
            product.setQuantity(quantity);

            //close cursor & db
            cursor.close();
            db.close();
            //return the Product
            return product;
        }
        return null;
    }

    //check  product exits in the database
    public boolean checkProduct(String userId, String productId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] col = {DatabaseHelper.COL_P_NAME};
        String where = DatabaseHelper.COL_P_USER_ID + "=? AND " + DatabaseHelper.COL_P_PRODUCT_ID + "=?";
        String[] args = {userId, productId};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCT, col, where, args, null, null, null);

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

    //Method to delete product
    public int deleteProduct(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String where = DatabaseHelper.COL_P_USER_ID + "=? AND " + DatabaseHelper.COL_P_PRODUCT_ID + "=?";
        String[] args = {userId, productId};
        return db.delete(DatabaseHelper.TABLE_PRODUCT, where, args);
    }

    //************* SQ (Size and Quantity)

    //method to insert SQ - size and quantity
    public void insertSQ(String size, String quantity, String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_SQ_SIZE, size);
        cv.put(DatabaseHelper.COL_SQ_QUANTITY, quantity);
        cv.put(DatabaseHelper.COL_SQ_USER_ID, userId);
        cv.put(DatabaseHelper.COL_SQ_PRODUCT_ID, productId);
        db.insert(DatabaseHelper.TABLE_SQ, null, cv);
        //close db
        db.close();
    }

    //Method to delete SQ
    public void deleteSQ(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String where = DatabaseHelper.COL_SQ_USER_ID + "=? AND " + DatabaseHelper.COL_SQ_PRODUCT_ID + "=?";
        String[] whereArg = {userId, productId};
        db.delete(DatabaseHelper.TABLE_SQ, where, whereArg);
    }

    //method to get size as (4\n5\n6\n)
    private String getSize(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] col = {DatabaseHelper.COL_SQ_SIZE};
        String selection = DatabaseHelper.COL_SQ_USER_ID + "=? AND " + DatabaseHelper.COL_SQ_PRODUCT_ID + "=?";
        String[] where = {userId, productId};
        Cursor cursor = db.query(DatabaseHelper.TABLE_SQ, col, selection, where, null, null, null);
        int i = 0;
        if (cursor.moveToFirst()) {
            String size = "";
            do {
                i++;
                size += cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_SQ_SIZE));
                if (i < cursor.getCount()) {
                    size += "\n";
                }
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return size;
        }
        cursor.close();
        db.close();
        return null;
    }

    //Method to get Quantity like (4\n5\n6\n)
    private String getQuantity(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] col = {DatabaseHelper.COL_SQ_QUANTITY};
        String selection = DatabaseHelper.COL_SQ_USER_ID + "=? AND " + DatabaseHelper.COL_SQ_PRODUCT_ID + "=?";
        String[] where = {userId, productId};
        Cursor cursor = db.query(DatabaseHelper.TABLE_SQ, col, selection, where, null, null, null);

        int i = 0;
        if (cursor.moveToFirst()) {
            String quantity = "";
            do {
                i++;
                quantity += cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_SQ_QUANTITY));
                if (i < cursor.getCount()) {
                    quantity += "\n";
                }
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return quantity;
        }
        cursor.close();
        db.close();
        return null;
    }

    /********************** PRODUCT_URI **************************/

     //Method to check product image uri of a particular product is avaialble or not
    public boolean checkProductUri(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        //Conditions
        String[] col = {DatabaseHelper.COL_URI_IMAGE_URI};
        String where = DatabaseHelper.COL_URI_USER_ID + "=? AND " + DatabaseHelper.COL_URI_PRODUCT_ID + "=?";
        String[] args = {userId, productId};

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCT_URI, col, where, args, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false; //not available
        }
        cursor.close();
        db.close();
        return true; //available
    }

    //store image uri to Database
    public void insertProductUri(String userId, String productId, Uri productUri) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_URI_IMAGE_URI, String.valueOf(productUri));
        cv.put(DatabaseHelper.COL_URI_USER_ID, userId);
        cv.put(DatabaseHelper.COL_URI_PRODUCT_ID, productId);

        //Insert into product uri table
        db.insert(DatabaseHelper.TABLE_PRODUCT_URI, null, cv);
        db.close();
    }

    //get product image uri from database
    public Uri getProductUri(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        //Conditions
        String[] col = {DatabaseHelper.COL_URI_IMAGE_URI};
        String where = DatabaseHelper.COL_URI_USER_ID + "=? AND " + DatabaseHelper.COL_URI_PRODUCT_ID + "=?";
        String[] args = {userId, productId};

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCT_URI, col, where, args, null, null, null);

        if (cursor.moveToFirst()) {
            String imageUri = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_URI_IMAGE_URI));
            cursor.close();
            return Uri.parse(imageUri);
        }
        cursor.close();
        return null;
    }

    //method to delete product uri
    public int deleteProductUri(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String where = DatabaseHelper.COL_URI_USER_ID + "=? AND " + DatabaseHelper.COL_URI_PRODUCT_ID + "=?";
        String[] args = {userId, productId};
        return db.delete(DatabaseHelper.TABLE_PRODUCT_URI, where, args);
    }

    /************************* SALES TRACKER DATELIST *********************************/
    //insert all datelist to the SQLite database
    public void insertSalesTrackerDateList(List<SalesTrackerDatePojo> list,String userId){
        SQLiteDatabase db = helper.getWritableDatabase();
        //loop through the list
        if (list != null){
            for (int i = 0; i < list.size(); i++) {
                SalesTrackerDatePojo current = list.get(i);
                //insert into database
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COL_ST_DATELIST_DATE,current.getDate());
                cv.put(DatabaseHelper.COL_ST_DATELIST_DATEID,current.getDateId());
                cv.put(DatabaseHelper.COL_ST_DATELIST_USER_ID,userId);

                db.insert(DatabaseHelper.TABLE_ST_DATELIST,null,cv);
            }
        }
        //close database
        db.close();
    }

    //delete all salesTracker datelist
    public void deleteSalesTrackerDateList(String userId){
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = DatabaseHelper.COL_ST_DATELIST_USER_ID +" = ?";
        String[] whereArgs = {userId};
        db.delete(DatabaseHelper.TABLE_ST_DATELIST,whereClause,whereArgs);
        db.close();
    }

    //method to get all the salestracker datelist
    public List<SalesTrackerDatePojo> getSalesTrackerDateList(String userId){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] col = {DatabaseHelper.COL_ST_DATELIST_DATE,DatabaseHelper.COL_ST_DATELIST_DATEID};
        String where = DatabaseHelper.COL_ST_DATELIST_USER_ID + "=?";
        String[] args = {userId};
        String orderBy = DatabaseHelper.COL_ST_DATELIST_ID+" ASC";
        Cursor cursor = db.query(DatabaseHelper.TABLE_ST_DATELIST, col, where, args, null, null, orderBy);

        List<SalesTrackerDatePojo> list = new ArrayList<>();
        while (cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ST_DATELIST_DATE));
            String dateid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ST_DATELIST_DATEID));

            SalesTrackerDatePojo pojo = new SalesTrackerDatePojo();
            pojo.setDateId(dateid);
            pojo.setDate(date);

            //add to list
            list.add(pojo);
        }

        //close cursor
        cursor.close();
        db.close();

        return list;
    }

    /*
    * Method to delete all data
    * */
    public void deleteAllData(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_SALESMAN,null,null);
        db.delete(DatabaseHelper.TABLE_CATEGORY,null,null);
        db.delete(DatabaseHelper.TABLE_PRODUCT_LIST,null,null);
        db.delete(DatabaseHelper.TABLE_PRODUCT,null,null);
        db.delete(DatabaseHelper.TABLE_SQ,null,null);
        db.delete(DatabaseHelper.TABLE_PRODUCT_URI,null,null);
        db.delete(DatabaseHelper.TABLE_ST_DATELIST,null,null);
    }


    /*********************** DATABASE SCHEMA CLASS **********************************/
    private class DatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 7;
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

        private static final String TABLE_PRODUCT_URI = "product_uri",
                COL_URI_ID = "_id",
                COL_URI_IMAGE_URI = "image_uri",
                COL_URI_USER_ID = "user_id",
                COL_URI_PRODUCT_ID = "product_id";

        //Sales tracker Datelist
        private static final String TABLE_ST_DATELIST = "sales_tracker_datelist",
                COL_ST_DATELIST_ID = "_id",
                COL_ST_DATELIST_DATE = "date",
                COL_ST_DATELIST_DATEID = "date_id",
                COL_ST_DATELIST_USER_ID = "user_id";

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

        private String CREATE_PRODUCT_LIST = "CREATE TABLE " + TABLE_PRODUCT_LIST + "(" +
                COL_PL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PL_PRODUCT_ID + " INTEGER ," +
                COL_PL_USER_ID + " INTEGER," +
                COL_PL_CATEGORY_ID + " INTEGER," +
                COL_PL_NAME + " VARCHAR(20)," +
                COL_PL_CODE + " TEXT," +
                COL_PL_TIME + " TEXT);";

        private String CREATE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + "(" +
                COL_P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_P_USER_ID + " INTEGER," +
                COL_P_PRODUCT_ID + " INTEGER," +
                COL_P_NAME + " TEXT," +
                COL_P_IMAGE_ADDRESS + " TEXT," +
                COL_P_CODE + " TEXT," +
                COL_P_CP + " TEXT," +
                COL_P_SP + " TEXT," +
                COL_P_TIME + " TEXT);";

        private String CREATE_SQ = "CREATE TABLE " + TABLE_SQ + "(" +
                COL_SQ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_SQ_SIZE + " INTEGER," +
                COL_SQ_QUANTITY + " INTEGER," +
                COL_SQ_USER_ID + " INTEGER," +
                COL_SQ_PRODUCT_ID + " INTEGER);";

        private String CREATE_PRODUCT_URI = "CREATE TABLE " + TABLE_PRODUCT_URI + "(" +
                COL_URI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_URI_IMAGE_URI + " TEXT," +
                COL_URI_USER_ID + " INTEGER," +
                COL_URI_PRODUCT_ID + " INTEGER);";

        private String CREATE_ST_DATELIST = "CREATE TABLE " + TABLE_ST_DATELIST + "(" +
                COL_ST_DATELIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ST_DATELIST_DATE + " TEXT," +
                COL_ST_DATELIST_DATEID + " TEXT," +
                COL_ST_DATELIST_USER_ID + " INTEGER);";

        /*
        * DROP TABLE QUERY
        * */
        private String DROP_SALESMAN = "DROP TABLE IF EXISTS " + TABLE_SALESMAN;
        private String DROP_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_CATEGORY;
        private String DROP_PRODUCT_LIST = "DROP TABLE IF EXISTS " + TABLE_PRODUCT_LIST;
        private String DROP_PRODUCT = "DROP TABLE IF EXISTS " + TABLE_PRODUCT;
        private String DROP_SQ = "DROP TABLE IF EXISTS " + TABLE_SQ;
        private String DROP_PRODUCT_URI = "DROP TABLE IF EXISTS " + TABLE_PRODUCT_URI;
        private String DROP_ST_DATELIST = "DROP TABLE IF EXISTS " + TABLE_ST_DATELIST;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG, "HUS: Constructor");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //create table
            db.execSQL(CREATE_SALESMAN);
            db.execSQL(CREATE_CATEGORY);
            db.execSQL(CREATE_PRODUCT_LIST);
            db.execSQL(CREATE_PRODUCT);
            db.execSQL(CREATE_SQ);
            db.execSQL(CREATE_PRODUCT_URI);
            db.execSQL(CREATE_ST_DATELIST);
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
            db.execSQL(DROP_PRODUCT_URI);
            db.execSQL(DROP_ST_DATELIST);
            Log.d(TAG, "HUS: onUpgrade");
            //Call onCreate to recreate tables
            onCreate(db);
        }
    }
}