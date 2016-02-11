package com.hackerkernel.storemanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.util.GetSalesman;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalculateCommissionActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.salesmanSpinner) Spinner mSalesmanSpinner;
    @Bind(R.id.layout) RelativeLayout mLayoutForSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_commission);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.calculate_commission));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup salesman spinner
        GetSalesman getSalesman = new GetSalesman(this,mLayoutForSnackbar,mSalesmanSpinner);
        getSalesman.setupSalesmanSpinner();
    }
}
