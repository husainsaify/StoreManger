package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.ProductPojo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter to display productList in a recyclerview
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ProductPojo> mList;

    public ProductListAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
    }

    public void setList(List<ProductPojo> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.product_list_layout,parent,false);
        return new ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListViewHolder holder, int position) {
        ProductPojo c = mList.get(position);
        holder.name.setText(c.getProductName());
        holder.code.setText(c.getProductCode());
        holder.time.setText(c.getProductTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //ViewHolder Classs
    class ProductListViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.productImage) ImageView image;
        @Bind(R.id.productName) TextView name;
        @Bind(R.id.productCode) TextView code;
        @Bind(R.id.productTime) TextView time;
        public ProductListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
