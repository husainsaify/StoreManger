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
        cv.put(DatabaseHelper.COL_P_IMAGE_URI, "");
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
                DatabaseHelper.COL_P_IMAGE_URI,
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
            String imageUri = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_IMAGE_URI));
            String code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_CODE));
            String cp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_CP));
            String sp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_SP));
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_P_TIME));
            //get size and Quantity stack from Member methods
            String size = this.getSize(userId, productId);
            String quantity = this.getQuantity(userId, productId);

            product.setName(name);
            product.setImageAddress(imageAddress);
            product.setImageUri(imageUri);
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
    public void deleteProduct(String userId, String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String where = DatabaseHelper.COL_P_USER_ID + "=? AND " + DatabaseHelper.COL_P_PRODUCT_ID + "=?";
        String[] args = {userId, productId};
        db.delete(DatabaseHelper.TABLE_PRODUCT, where, args);
    }

    //store image uri to Database
    public void insertProductImageUri(String userId,String productId, Uri productUri) {
        SQLiteDatabase db = helper.getWritableDatabase();

        //Conditions
        String where = DatabaseHelper.COL_P_USER_ID + "=? AND " + DatabaseHelper.COL_P_PRODUCT_ID + "=?";
        String[] args = {userId, productId};

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_P_IMAGE_URI, String.valueOf(productUri));
        db.update(DatabaseHelper.TABLE_PRODUCT, cv, where, args);
        db.close();
    }

    //get product image uri from database
    public Uri getProductImageUri(String userId,String productId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        //Conditions
        String[] col = {DatabaseHelper.COL_P_IMAGE_URI};
        String where = DatabaseHelper.COL_P_USER_ID + "=? AND " + DatabaseHelper.COL_P_PRODUCT_ID + "=?";
        String[] args = {userId, productId};

        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCT,col,where,args,null,null,null);

        if (cursor.moveToFirst()) {
            String imageUri = cursor.getString(0);
            cursor.close();
            return Uri.parse(imageUri);
        }
        cursor.close();
        return null;
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

    private class DatabaseHelper extends SQLiteOpenHelper {
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
                COL_P_IMAGE_URI + " TEXT," +
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

        /*
        * DROP TABLE QUERY
        * */
        private String DROP_SALESMAN = "DROP TABLE IF EXISTS " + TABLE_SALESMAN;
        private String DROP_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_CATEGORY;
        private String DROP_PRODUCT_LIST = "DROP TABLE IF EXISTS " + TABLE_PRODUCT_LIST;
        private String DROP_PRODUCT = "DROP TABLE IF EXISTS " + TABLE_PRODUCT;
        private String DROP_SQ = "DROP TABLE IF EXISTS " + TABLE_SQ;

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
         //method to delete all product from the database
         public int deleteAllProduct(){
         db = this.getWritableDatabase();
         int result = db.delete(TABLE_PRODUCT,null,null);
         db.close();
         return result;
         }
         //store product image Uri into database
         */
        /*//********************************** TABLE SQ

        public void deleteAllSQ(){
            db = this.getWritableDatabase();
            db.delete(TABLE_SQ,null,null);
            db.close();
        }*/
    }
}