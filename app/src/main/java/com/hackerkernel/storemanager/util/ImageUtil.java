package com.hackerkernel.storemanager.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Class to decode image and other stuff
 */
public class ImageUtil {
    /*
    *  Method to get full path from it Uri
    * */
    public static String getFilePathFromUri(Context context, Uri uri) {
        String filePath = null;
        String[] filePathCol = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathCol, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int colIndex = cursor.getColumnIndex(filePathCol[0]);
            filePath = cursor.getString(colIndex);
            cursor.close();
        }
        return filePath;
    }

    /*
    * Method to calculate image InSampleSize (BitmapFactory)
    * */
    private static int calculateImageInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("HUS", "HUS: IMAGE old: height " + height + " width " + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /*
    * METHOD to decode HighResolution bitmap
    * */
    public static Bitmap decodeBitmapFromFilePath(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateImageInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        Log.d("HUS", "HUS: IMAGE new: height " + bitmap.getHeight() + " width " + bitmap.getWidth());
        return bitmap;
    }

    /*
    * METHOD to get image uri from bitmap
    * */
    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /*
    * METHOD to compress image to Base64 String
    * */
    public static String compressImageToBase64(Bitmap bitmap) {
        //compress the image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        //convert image to  Base64 encoded string
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
}
