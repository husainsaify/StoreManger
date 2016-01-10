package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to adapt simpleList item like category & salesman list
 */
public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.ViewHolderSimpleList>{

    private LayoutInflater mInflater;
    private List<SimpleListPojo> mList = new ArrayList<>();


    public SimpleListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    public void setList(List<SimpleListPojo> list){
        mList = list;
        //update the adapter to display new items
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderSimpleList onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.simple_list_layout,parent,false);
        return new ViewHolderSimpleList(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderSimpleList holder, int position) {
        SimpleListPojo current = mList.get(position);
        //set item to  views
        holder.name.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolderSimpleList extends RecyclerView.ViewHolder {
        private TextView name;
        public ViewHolderSimpleList(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.SimpleListText);
        }
    }
}
