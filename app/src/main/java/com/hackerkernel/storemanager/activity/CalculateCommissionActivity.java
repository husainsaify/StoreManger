package com.hackerkernel.storemanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.util.DatePicker;
import com.hackerkernel.storemanager.util.GetSalesman;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalculateCommissionActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.salesmanSpinner) Spinner mSalesmanSpinner;
    @Bind(R.id.layout) RelativeLayout mLayoutForSnackbar;
    @Bind(R.id.fromDateButton) Button mFromDateButton;
    @Bind(R.id.toDateButton) Button mToDateButton;


    private String mSalesmanId;

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
        final GetSalesman getSalesman = new GetSalesman(this,mLayoutForSnackbar,mSalesmanSpinner);
        getSalesman.setupSalesmanSpinner();

        //when Salesman is selected from spinnner
        mSalesmanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSalesmanId = getSalesman.getSalesman(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mFromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker picker = new DatePicker();
                picker.show(getSupportFragmentManager(), "fromDate");
            }
        });

        mToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker picker = new DatePicker();
                picker.show(getSupportFragmentManager(),"toDate");
            }
        });
    }
}
