package com.hackerkernel.storemanager.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class to handle all the stuff realeated to ImageSelection
 */
public class ImageSeletion {

    public int TAKE_PICTURE = 1; //camera
    public int CHOSE_PICTURE = 2; //gallery
    public String mCameraImage = null;
    private Context mContext;

    public ImageSeletion(Context context) {
        this.mContext = context;
    }

    public void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(R.array.select_picture_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //Take picture
                        captureImageFromCamera(); //open camera
                        break;
                    case 1: //choose picture
                        selectImageFromGallery(); //open gallary
                        break;
                }
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    /*
    * Code to open gallery and select Image from their
    * */
    private void selectImageFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        ((Activity) mContext).startActivityForResult(galleryIntent, CHOSE_PICTURE);
    }

    /*
    * This method will open camera and allow user to take a picture
    * */
    private void captureImageFromCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure camera is available
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null){
            try {
                File image = createImageTempFile();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(image));
                ((Activity) mContext).startActivityForResult(cameraIntent, TAKE_PICTURE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(mContext, R.string.failed_to_create_camera_image,Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(mContext, R.string.failed_to_open_camera,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to create a temp file for camera where camera image will be saved
    * */
    private File createImageTempFile() throws IOException {
        //create name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_"+timestamp;
        //Storage dir for file
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image =  File.createTempFile(fileName, ".jpg", storageDir);
        //store image path to Member varaible
        mCameraImage = image.getAbsolutePath();
        return image;
    }

    //Method to get Camera Image Path
    public String getCameraImagePath() {
        return mCameraImage;
    }

}
