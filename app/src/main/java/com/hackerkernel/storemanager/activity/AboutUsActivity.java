package com.hackerkernel.storemanager.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ListView;
import android.content.Intent;
import android.net.Uri;

import com.hackerkernel.storemanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUsActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.version) TextView mVersion;
    @Bind(R.id.list) ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.about_us));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set version code
        StringBuilder versionString = new StringBuilder().append(getString(R.string.app_version_code))
                .append(", ")
                .append(getString(R.string.app_version_name));
        mVersion.append(versionString);

        //when some one click list item
        final Intent i = new Intent(Intent.ACTION_VIEW);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    //like us on facebook
                    case 0:
                        i.setData(Uri.parse("https://www.facebook.com/hackerkernel"));
                        startActivity(i);
                        break;
                    //say hi to husain saify
                    case 1:
                        i.setData(Uri.parse("https://www.facebook.com/hackerkernel"));
                        startActivity(i);
                        break;
                    //subscribe on YT
                    case 2:
                        i.setData(Uri.parse("https://www.youtube.com/user/hunklessons"));
                        startActivity(i);
                        break;
                    //Blog
                    case 3:
                        i.setData(Uri.parse("http://blog.hackerkernel.com/"));
                        startActivity(i);
                        break;
                    //contact us
                    case 4:
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto: hackerkernel15@gmail.com"));
                        startActivity(Intent.createChooser(emailIntent, "Contact us"));
                        break;
                }
            }
        });
    }
}
