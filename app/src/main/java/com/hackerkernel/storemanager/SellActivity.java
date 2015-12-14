package com.hackerkernel.storemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.hackerkernel.storemanager.adapter.ACProductAdapter;
import com.hackerkernel.storemanager.pojo.ACProductSearchPojo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SellActivity extends AppCompatActivity {
    private static final String TAG = SellActivity.class.getSimpleName();
    private final Context context = this;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.productSearch) AutoCompleteTextView productSearch;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.sell));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get the userId
        DataBase db = new DataBase(this);
        userId = db.getUserID();

        String productSearchText = productSearch.getText().toString().trim();

        final ACProductAdapter adapter = new ACProductAdapter(this,productSearchText,userId);
        productSearch.setAdapter(adapter);

        productSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the name of the item selected from AC
                String name = adapter.getItem(position).getName();
                //set item to dropdown
                productSearch.setText(name);
            }
        });
    }

}
