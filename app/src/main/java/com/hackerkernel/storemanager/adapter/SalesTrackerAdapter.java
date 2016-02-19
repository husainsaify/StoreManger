package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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
    private Context mContext;

    public SalesTrackerAdapter(Context context){
        this.mInflator = LayoutInflater.from(context);
        //store context
        this.mContext = context;
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
    class SalesTrackerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        //List layout views
        @Bind(R.id.customerName) TextView customerName;
        @Bind(R.id.productName) TextView productName;
        @Bind(R.id.salesmanName) TextView salesmanName;
        @Bind(R.id.profitOrLoss) TextView profitOrLoss;

        //Alert dialog views
        View alertview;
        TextView alertName;
        TextView alertCode;
        TextView alertSize;
        TextView alertQuantity;
        TextView alertSellingPrice;
        TextView alertCostPrice;
        TextView alertSoldBy;
        TextView alertTotalSellingPrice;
        TextView alertTotalCostPrice;
        TextView alertProfitOrLoss;

        public SalesTrackerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //set on click for Recycler view item
            itemView.setOnClickListener(this);

            //create context menu
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            //create alertDialog
            //get current list
            int pos = getAdapterPosition();

            SalesTrackerPojo current = mList.get(pos);

            alertview = mInflator.inflate(R.layout.sales_tracker_alert_dialog, null);

            alertName = (TextView) alertview.findViewById(R.id.productName);
            alertCode = (TextView) alertview.findViewById(R.id.productCode);
            alertSize = (TextView) alertview.findViewById(R.id.productSize);
            alertQuantity = (TextView) alertview.findViewById(R.id.productQuantity);
            alertSellingPrice = (TextView) alertview.findViewById(R.id.sellingPrice);
            alertCostPrice = (TextView) alertview.findViewById(R.id.costPrice);
            alertSoldBy = (TextView) alertview.findViewById(R.id.soldBy);
            alertTotalSellingPrice = (TextView) alertview.findViewById(R.id.totalSellingPrice);
            alertTotalCostPrice = (TextView) alertview.findViewById(R.id.totalCostPrice);
            alertProfitOrLoss = (TextView) alertview.findViewById(R.id.profitOrLoss);
            //calculate prfit or loss
            String profitOrLoss = Util.calProfitLossOrBreakeven(current.getCostprice(),current.getSellingprice(),current.getQuantity());

            //calculate Total selling price & cost price
            int totalCostPrice = Util.calTotalPrice(current.getCostprice(),current.getQuantity());
            int totalSellingPrice = Util.calTotalPrice(current.getSellingprice(),current.getQuantity());

            //set values to the textView
            alertName.setText(current.getProductName());
            alertCode.setText(current.getProductCode());
            alertSize.setText(current.getSize());
            alertQuantity.setText(current.getQuantity());
            alertSellingPrice.setText(current.getSellingprice());
            alertCostPrice.setText(current.getCostprice());
            alertSoldBy.setText(current.getSalesmanName());
            alertTotalCostPrice.setText(totalCostPrice+"");
            alertTotalSellingPrice.setText(totalSellingPrice+"");
            alertProfitOrLoss.setText(profitOrLoss);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(alertview)
                    .setPositiveButton(R.string.ok,null);
            AlertDialog dialog = builder.create();

            dialog.show();
        }

        /*
        * Long press Context menu
        * */
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.action_delete, getAdapterPosition(), R.string.delete);
        }
    }
}
