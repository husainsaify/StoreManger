package com.hackerkernel.storemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.ProductPojo;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<ProductPojo> {
    public static final String TAG = ProductPojo.class.getSimpleName();
    private Context context;
    private List<ProductPojo> list;

    public ProductAdapter(Context context, int resource, List<ProductPojo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.product_list_layout, parent, false);

        //get Data from ProductPojo
        ProductPojo productPojo = list.get(position);
        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView productCode = (TextView) view.findViewById(R.id.productCode);
        ImageView productImage = (ImageView) view.findViewById(R.id.productImage);

        productName.setText(productPojo.getProductName());
        productCode.setText(productPojo.getProductCode());

        return view;
    }
}
