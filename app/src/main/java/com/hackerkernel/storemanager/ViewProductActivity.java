package com.hackerkernel.storemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.hackerkernel.storemanager.URL.DataUrl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewProductActivity extends AppCompatActivity {
    private static final String TAG = ViewProductActivity.class.getSimpleName();
    private final Context context = this;

    private String  pName,
                    pCode,
                    pId,
                    pImageAddress;
    private Bitmap pImage;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.imageView) ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        ButterKnife.bind(this);

        pName = getIntent().getExtras().getString("pName");
        pCode = getIntent().getExtras().getString("pCode");
        pId = getIntent().getExtras().getString("pId");
        pImageAddress = getIntent().getExtras().getString("pImageAddress");
        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(pName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //check Product Has a image or we have to Display a PlaceHolder Image
        if(!pImageAddress.isEmpty()){
            //fetch the Image and display it
            new getImageTask().execute(pImageAddress);
        }else{
            //Show the placeHolder image
            imageView.setImageResource(R.drawable.placeholder_product);
        }


    }

    //Fetch Image
    class getImageTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            //generate Image Full url
            String imageUrl = DataUrl.IMAGE_BASE_URL + params[0];

            //fetch Image from the server
            try {
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();

                //return image to "onPostExecute"
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                //Scale bitmap
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
