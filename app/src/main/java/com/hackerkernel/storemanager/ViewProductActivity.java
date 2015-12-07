package com.hackerkernel.storemanager;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewProductActivity extends AppCompatActivity {

    private String  pName,
                    pCode,
                    pId;
    private Bitmap pImage;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.text) TextView text;
    @Bind(R.id.imageView) ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        ButterKnife.bind(this);

        pName = getIntent().getExtras().getString("pName");
        pCode = getIntent().getExtras().getString("pCode");
        pId = getIntent().getExtras().getString("pId");
        pImage = (Bitmap) getIntent().getExtras().get("pImage");

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(pName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set text
        text.setText(pCode+"\n"+pId);

        //image
        imageView.setImageBitmap(pImage);


    }
}
