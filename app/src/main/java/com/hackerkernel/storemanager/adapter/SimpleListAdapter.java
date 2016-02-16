package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hackerkernel.storemanager.activity.ProductListActivity;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.SalesmanSalesDetailActivity;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter to adapt simpleList item like category & salesman list
 */
public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.SimpleListViewHolder>{

    private LayoutInflater mInflater;
    private List<SimpleListPojo> mList = new ArrayList<>();
    private Context mContext;
    private String mActivityName;

    //name of Activity using SimpleListAdapter
    public static String CATEGORY = "category";
    public static String SALESMAN = "salesman";

    public SimpleListAdapter(Context context,String activityName){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mActivityName = activityName;
    }

    public void setList(List<SimpleListPojo> list){
        mList = list;
        //update the adapter to display new items
        notifyDataSetChanged();
    }

    @Override
    public SimpleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.simple_list_layout,parent,false);
        return new SimpleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleListViewHolder holder, int position) {
        SimpleListPojo current = mList.get(position);
        //set item to  views
        holder.name.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SimpleListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.SimpleListText) TextView name;

        public SimpleListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            /*Item click listener*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            /*
            * Check if SimpleListAdapter is used for CATEGORY
            * */
            if(mActivityName.equals(CATEGORY)){
                SimpleListPojo current = mList.get(position);
                //go to product activity
                Intent productIntent = new Intent(mContext, ProductListActivity.class);
                //set categoryId and CategoryName in intenet
                productIntent.putExtra(Keys.KEY_COM_CATEGORYID, current.getId());
                productIntent.putExtra(Keys.KEY_COM_CATEGORYNAME, current.getName());
                mContext.startActivity(productIntent);
            }else if (mActivityName.equals(SALESMAN)){
                SimpleListPojo current = mList.get(position);
                //go to ManageSalesman
                Intent productIntent = new Intent(mContext, SalesmanSalesDetailActivity.class);
                productIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                productIntent.putExtra(Keys.KEY_COM_SALESMANID, current.getId());
                productIntent.putExtra("name", current.getName());
                mContext.startActivity(productIntent);
            }
        }
    }
}
