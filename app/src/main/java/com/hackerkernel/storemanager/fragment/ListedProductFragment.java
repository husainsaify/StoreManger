package com.hackerkernel.storemanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.adapter.AutoCompleteProductAdapter;
import com.hackerkernel.storemanager.storage.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListedProductFragment extends Fragment {

    @Bind(R.id.customerName) EditText mCustomerName;
    @Bind(R.id.productName) AutoCompleteTextView mProductName;

    private String mUserId;

    public ListedProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listed_product, container, false);
        ButterKnife.bind(this,view);

        //Get userid
        mUserId = MySharedPreferences.getInstance(getActivity()).getUserId();

        String productSearchText = mProductName.getText().toString().trim();

        final AutoCompleteProductAdapter adapter = new AutoCompleteProductAdapter(getActivity(),productSearchText,mUserId);
        mProductName.setAdapter(adapter);

        return view;
    }

}
