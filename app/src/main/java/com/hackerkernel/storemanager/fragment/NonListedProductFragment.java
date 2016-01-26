package com.hackerkernel.storemanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackerkernel.storemanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NonListedProductFragment extends Fragment {


    public NonListedProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_non_listed_product, container, false);
    }

}
