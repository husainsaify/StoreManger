package com.hackerkernel.storemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.SalesTrackerPojo;

import java.util.List;

public class SalesTrackerAdapter extends ArrayAdapter<SalesTrackerPojo> {
    private static final String TAG = SalesTrackerAdapter.class.getSimpleName();
    Context mContext;
    List<SalesTrackerPojo> mList;
    public SalesTrackerAdapter(Context context, int resource, List<SalesTrackerPojo> objects) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sales_tracker_list_layout,parent, false);
        SalesTrackerPojo current = mList.get(position);
        TextView name = (TextView) view.findViewById(R.id.productName);
        TextView porl = (TextView) view.findViewById(R.id.productPorL);

        //calculate profit or loss
        int currentSales = Integer.parseInt(current.getCurrentSales());
        int currentCp = Integer.parseInt(current.getCurrentCp());
        String pl;
        if(currentCp > currentSales){//loss
            //cal loss
            int loss = currentCp - currentSales;
            pl = "Loss of "+loss;
        }else {
            if (currentSales > currentCp) { //profit
                //call profit
                int profit = currentSales - currentCp;
                pl = "Profit of " + profit;
            } else { // neutral CP = sales
                pl = "Break Even";
            }
        }

        name.setText(current.getProductName());
        porl.setText(pl);
        return view;
    }
}
