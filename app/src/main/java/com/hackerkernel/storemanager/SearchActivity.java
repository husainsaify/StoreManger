package com.hackerkernel.storemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private final Context context = this;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.pName) EditText pName;
    @Bind(R.id.pSize) EditText pSize;
    @Bind(R.id.pSearch) Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //when search button is pressed
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = pName.getText().toString().trim();
                String productSize = pSize.getText().toString().trim();

                //check product Name is not empty
                if (productName.isEmpty()){
                    Functions.errorAlert(context,getString(R.string.oops),getString(R.string.product_name_not_empty));
                    return;
                }

                Log.d(TAG,"HUS: age bar");
            }
        });
    }

}
