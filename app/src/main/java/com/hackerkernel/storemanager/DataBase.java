package com.hackerkernel.storemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.hackerkernel.storemanager.pojo.LoginPojo;
import com.hackerkernel.storemanager.pojo.SingleProductPojo;

/**
 * Database class to insert user in the database
 */
public class DataBase extends SQLiteOpenHelper {
    //create a TAG
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
                                COL_SQ_PRODUCT_ID = "product_id";

    //create a database variable
    SQLiteDatabase db;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "(" +
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
                COL_SQ_ID + " integer primary key not null," +
                COL_SQ_SIZE + " integer not null," +
                COL_SQ_QUANTITY + " integer not null," +
                COL_SQ_USER_ID + " integer not null," +
                COL_SQ_PRODUCT_ID + " integer not null" +
                ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_SQ_TABLE);
    }

    //*************************** TABLE_USER

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
        values.put(COL_LAST_BILL_PAID, user.getLastBillPaid());
        values.put(COL_NEXT_DUE_DATE, user.getNextDueDate());
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

    //************************************ TABLE_PRODUCT

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
        int result = db.delete(TABLE_PRODUCT,COL_P_ID+"=?",new String[] {productId});
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
    /*
    * This method will take productId and image Uri
    * and store it in the prooduct table for later use
    * */
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

    //********************************** TABLE SQ
    //this method is used to insert SQ
    public boolean setSQ(String size,String quantity,String userId, String productId){
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SQ_ID,"");
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table if factory version upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SQ);
        //execute onCreate method
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table if factory version degrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SQ);
        //execute onCreate method
        onCreate(db);
    }
}
