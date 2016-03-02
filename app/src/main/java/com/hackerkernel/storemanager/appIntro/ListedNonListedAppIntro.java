package com.hackerkernel.storemanager.appIntro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.AddSalesActivity;
import com.hackerkernel.storemanager.activity.MainActivity;
import com.hackerkernel.storemanager.fragment.AppIntroSlideFragment;

/**
 * Class to show app Intro for Listed & non Listed activity on AddSales
 */
public class ListedNonListedAppIntro extends AppIntro {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        addSlide(AppIntroSlideFragment.newInstance(R.layout.slide1_listed_nolisted_intro));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.slide2_listed_nolisted_intro));

        showSkipButton(true);
        showStatusBar(false);

        setVibrate(true);
        setVibrateIntensity(30);
        setDepthAnimation();
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
