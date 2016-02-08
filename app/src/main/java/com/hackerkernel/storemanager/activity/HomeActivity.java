package com.hackerkernel.storemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.adapter.ViewPagerAdapter;
import com.hackerkernel.storemanager.fragment.CategoryFragment;
import com.hackerkernel.storemanager.fragment.SalesTrackerFragment;
import com.hackerkernel.storemanager.storage.MySharedPreferences;
import com.hackerkernel.storemanager.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity{

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) TabLayout tabs;
    @Bind(R.id.viewpager) ViewPager viewPager;
    //Navigation drawer
    @Bind(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigationView) NavigationView mNavigationView;


    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MySharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.app_name));

        /*
        * Setup Tabs and viewPager to display Fragments in the tabs
        * */
        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        //instantiate MySharedPreferences to get user data
        mSharedPreferences = MySharedPreferences.getInstance(this);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menu_add_category:
                        mDrawerLayout.closeDrawers();
                        goToAddCategoryActivity();
                        break;
                    case R.id.menu_add_product:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplication(),AddProductActivity.class));
                        break;
                    case R.id.menu_manage_salesman:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplication(), ManageSalesmanActivity.class));
                        break;
                    case R.id.menu_sales_tracker:
                        break;
                    case R.id.menu_setting:
                        break;
                    case R.id.menu_logout:
                        Util.logout(getApplication()); //logout
                        break;
                    case R.id.menu_share:
                        break;
                }
                return true;
            }
        });

        //set user information on the Navigation drawer Header
        TextView headerStoreName = (TextView) mNavigationView.findViewById(R.id.navigationHeaderShopname);
        TextView headerFullName = (TextView) mNavigationView.findViewById(R.id.navigationHeaderName);
        headerStoreName.setText(mSharedPreferences.getUserStorename());
        headerFullName.setText(mSharedPreferences.getUserFullname());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sales_tracker_fragment,menu);
        return true;
    }

    /*
        * Create a HamBurger icon for NavigationDrawer
        * */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    /*
        * Method to setup fragments into the viewPager
        * */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CategoryFragment(), getString(R.string.category_capital));
        adapter.addFragment(new SalesTrackerFragment(), getString(R.string.salestracker_capital));
        viewPager.setAdapter(adapter);
    }

    /*
     * Method to go to AddCategoryActivity
     * */
    private void goToAddCategoryActivity() {
        startActivity(new Intent(getApplication(), AddCategoryActivity.class));
    }
}
