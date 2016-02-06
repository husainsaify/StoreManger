package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;
import com.hackerkernel.storemanager.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SalesTrackerAdapter extends RecyclerView.Adapter<SalesTrackerAdapter.SalesTrackerViewHolder> {
    List<SalesTrackerPojo> mList = new ArrayList<>();
    LayoutInflater mInflator;

    public SalesTrackerAdapter(Context context){
        mInflator = LayoutInflater.from(context);
    }

    public void setList(List<SalesTrackerPojo> list){
        mList = list;
        //update RecyclerView
        notifyDataSetChanged();
    }

    @Override
    public SalesTrackerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.sales_tracker_list_layout,parent,false);
        return new SalesTrackerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SalesTrackerViewHolder holder, int position) {
        SalesTrackerPojo current = mList.get(position);

        //set to views
        holder.customerName.setText(current.getCustomerName());
        holder.productName.setText(current.getProductName());
        holder.salesmanName.setText("Sold By: " + current.getSalesmanName());
        //cal profit , loss or break even
        String r = Util.calProfitLossOrBreakeven(current.getCostprice(),current.getSellingprice(),current.getQuantity());
        holder.profitOrLoss.setText(r);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**************** VIEW HOLDER ******************/
    class SalesTrackerViewHolder extends RecyclerView.ViewHolder{
        //List layout views
        @Bind(R.id.customerName) TextView customerName;
        @Bind(R.id.productName) TextView productName;
        @Bind(R.id.salesmanName) TextView salesmanName;
        @Bind(R.id.profitOrLoss) TextView profitOrLoss;

        public SalesTrackerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
