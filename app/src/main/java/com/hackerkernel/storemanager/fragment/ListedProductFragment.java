package com.hackerkernel.storemanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    @Bind(R.id.customerName) EditText mCustomerNameView;
    @Bind(R.id.productName) AutoCompleteTextView mProductNameView;
    @Bind(R.id.productSize) EditText mProductSizeView;
    @Bind(R.id.productQuantity) EditText mProductQuantityView;
    @Bind(R.id.productCostPrice) EditText mProductCostPriceView;
    @Bind(R.id.productSellingPrice) EditText mProductSellingPriceView;

    private String mUserId;
    private String mProductName;
    private String mProductId;
    private String mProductCostPrice;

    public ListedProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listed_product, container, false);
        ButterKnife.bind(this, view);

        //Get userid
        mUserId = MySharedPreferences.getInstance(getActivity()).getUserId();

        final AutoCompleteProductAdapter adapter = new AutoCompleteProductAdapter(getActivity(), mUserId);
        mProductNameView.setAdapter(adapter);

        mProductNameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the name of the item selected from AC
                mProductName = adapter.getItem(position).getName();
                //store productId & cost price
                mProductId = adapter.getItem(position).getId();
                mProductCostPrice = adapter.getItem(position).getCp();

                //set item to dropdown
                mProductNameView.setText(mProductName);

                //set Cost price to its EditText and disable it

            }
        });
        return view;
    }

}
