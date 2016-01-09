package com.hackerkernel.storemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.SimpleListPojo;

import java.util.List;


public class CategoryAdapter extends ArrayAdapter<SimpleListPojo> {
    public static final String TAG = CategoryAdapter.class.getSimpleName();
    private Context context;
    private List<SimpleListPojo> list;

    public CategoryAdapter(Context context,int resource,List<SimpleListPojo> object){
        super(context,resource,object);
        this.context = context;
        this.list = object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_list_layout,parent, false);

        //get data from my SimpleListPojo
        SimpleListPojo categoryPojo = list.get(position);
        TextView tv = (TextView) view.findViewById(R.id.categoryText);
        tv.setText(categoryPojo.getName());
        return view;
    }
}
