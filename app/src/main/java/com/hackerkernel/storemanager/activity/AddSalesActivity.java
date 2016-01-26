package com.hackerkernel.storemanager.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.adapter.ViewPagerAdapter;
import com.hackerkernel.storemanager.fragment.ListedProductFragment;
import com.hackerkernel.storemanager.fragment.NonListedProductFragment;
import com.hackerkernel.storemanager.fragment.SalesTrackerFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddSalesActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.salesTab) TabLayout mTab;
    @Bind(R.id.salesViewPager) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales);
        ButterKnife.bind(this);

        //setToolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.add_sales);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup View pager
        setupViewPager(mViewPager);
        mTab.setupWithViewPager(mViewPager);
    }

    //Method to set up ViewPage
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListedProductFragment(),getString(R.string.listed_product_big));
        adapter.addFragment(new NonListedProductFragment(),getString(R.string.non_listed_product_big));
        viewPager.setAdapter(adapter);
    }
}
