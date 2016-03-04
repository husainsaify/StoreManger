package com.hackerkernel.storemanager.appIntro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.MainActivity;

/**
 * Main App intro
 */
public class MainAppIntro extends AppIntro {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        int primaryColor = ContextCompat.getColor(getApplicationContext(),R.color.primaryColor);

        addSlide(AppIntroFragment.newInstance(
                "Add Category","Add multiple category and organize your stock efficiently",R.drawable.main_app_intro_1_add_category,primaryColor));

        addSlide(AppIntroFragment.newInstance(
                "Add Product","Add product inside category",R.drawable.main_app_intro_2_add_product,primaryColor));

        addSlide(AppIntroFragment.newInstance(
                "Manage Salesman","Manage and add multiple salesman for one store",R.drawable.main_app_intro_3_manage_salesman,primaryColor));

        addSlide(AppIntroFragment.newInstance(
                "Sales Tracker","Keep track of your daily sales",R.drawable.main_app_intro_4_track_your_sales,primaryColor));

        addSlide(AppIntroFragment.newInstance(
                "Calculate Commission","Calculate commission of salesman in just on click",R.drawable.main_app_intro_5_calculate_commission,primaryColor));

        addSlide(AppIntroFragment.newInstance(
                "Search Product","Searching your stock has never been so easy",R.drawable.main_app_intro_6_search_product,primaryColor));

        showSkipButton(true);
        showStatusBar(false);

        setVibrate(true);
        setVibrateIntensity(30);
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed() {
        Toast.makeText(getApplicationContext(), R.string.skipped_app_intro, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onSlideChanged() {

    }
}
