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

    class SimpleListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        @Bind(R.id.SimpleListText) TextView name;

        public SimpleListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            /*Item click listener*/
            itemView.setOnClickListener(this);

            //register floating context menu when adapter is used for Category
            if(mActivityName.equals(CATEGORY)){
                //context menu
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            /*
            * Check if SimpleListAdapter is used for CATEGORY
            * */
            if(mActivityName.equals(CATEGORY)){
                int position = getAdapterPosition();
                SimpleListPojo current = mList.get(position);
                //go to product activity
                Intent productIntent = new Intent(mContext, ProductListActivity.class);
                //set categoryId and CategoryName in intenet
                productIntent.putExtra(Keys.PRAM_PL_CATEGORYID, current.getId());
                productIntent.putExtra(Keys.PRAM_PL_CATEGORYNAME, current.getName());
                mContext.startActivity(productIntent);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE,R.id.action_edit, Menu.NONE, R.string.edit_category_name);
            menu.add(Menu.NONE,R.id.action_delete, Menu.NONE, R.string.delete_category);
        }
    }
}
