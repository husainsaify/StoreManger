package com.hackerkernel.storemanager.appIntro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.AddSalesActivity;

/**
 * Class to show app Intro for Listed & non Listed activity on AddSales
 */
public class ListedNonListedAppIntro extends AppIntro {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance(getString(R.string.listed_product_big),
                "Sale those product which are already added to Store Manger",R.drawable.listed_app_intro,ContextCompat.getColor(getApplicationContext(), R.color.primaryColor)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.non_listed_product_big),
                "Sale those product which are not added to Store Manager",R.drawable.non_listed_app_intro, ContextCompat.getColor(getApplicationContext(),R.color.primaryColor)));
        showSkipButton(false);
        showStatusBar(false);

        setVibrate(true);
        setVibrateIntensity(30);
        setZoomAnimation();
    }

    @Override
    public void onSkipPressed() {
        Toast.makeText(getApplicationContext(), R.string.skipped_app_intro, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AddSalesActivity.class);
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
