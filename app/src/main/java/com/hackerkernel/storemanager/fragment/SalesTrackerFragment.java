package com.hackerkernel.storemanager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.AddSalesActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesTrackerFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.fabAddSales) FloatingActionButton mFabAddSales;

    public SalesTrackerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_tracker, container, false);
        ButterKnife.bind(this,view);

        mFabAddSales.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabAddSales:
                startActivity(new Intent(getActivity(), AddSalesActivity.class));
                break;
        }
    }
}
