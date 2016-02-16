package com.hackerkernel.storemanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.extras.Keys;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SalesmanSalesDetailActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbarSpinner) Spinner mToolbarSpinner;

    private String mSalesmanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesman_sales_detail);
        ButterKnife.bind(this);
        //set actionbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get Salesman id
        if (getIntent().hasExtra(Keys.KEY_COM_SALESMANID)){
            mSalesmanId = getIntent().getExtras().getString(Keys.KEY_COM_SALESMANID);
        }else{
            Toast.makeText(getApplicationContext(),R.string.internal_error_restart_app,Toast.LENGTH_LONG).show();
            this.finish();
        }
    }
}
