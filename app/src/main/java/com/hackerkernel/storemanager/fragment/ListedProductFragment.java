package com.hackerkernel.storemanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

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
    private String productName;
    private String productId;
    private String productCostPrice;

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
        mProductName.setAdapter(adapter);

        mProductName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the name of the item selected from AC
                productName = adapter.getItem(position).getName();
                //store productId & cost price
                productId = adapter.getItem(position).getId();
                productCostPrice = adapter.getItem(position).getCp();

                //set item to dropdown
                mProductName.setText(productName);

                Toast.makeText(getActivity(),productId+"/"+productCostPrice,Toast.LENGTH_LONG).show();
                //fetch product
                //getProductData(productId);
            }
        });
        return view;
    }

}
