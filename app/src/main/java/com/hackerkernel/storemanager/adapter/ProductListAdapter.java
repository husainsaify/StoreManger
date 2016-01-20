package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackerkernel.storemanager.pojo.ProductPojo;

import java.util.List;

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
        return null;
    }

    @Override
    public void onBindViewHolder(ProductListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    //ViewHolder Classs
    class ProductListViewHolder extends RecyclerView.ViewHolder{

        public ProductListViewHolder(View itemView) {
            super(itemView);
        }
    }
}
