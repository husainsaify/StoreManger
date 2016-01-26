package com.hackerkernel.storemanager.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import com.hackerkernel.storemanager.R;

import java.io.ByteArrayOutputStream;

/**
 * Class to handle all the stuff realeated to ImageSelection
 */
public class ImageSeletion{

    public int TAKE_PICTURE = 1; //camera
    public int CHOSE_PICTURE = 2; //gallery
    private Context mContext;

    public ImageSeletion(Context context){
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
        }).setNegativeButton(R.string.cancel,null).show();
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
    private void captureImageFromCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity) mContext).startActivityForResult(cameraIntent, TAKE_PICTURE);
    }

    public String compressImageToBase64(Bitmap bitmap){
        //compress the image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);

        //convert image to  Base64 encoded string
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
}
