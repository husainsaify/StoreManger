package com.hackerkernel.storemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.pojo.CategoryPojo;

import java.util.List;


public class CategoryAdapter extends ArrayAdapter<CategoryPojo> {
    public static final String TAG = CategoryAdapter.class.getSimpleName();
    private Context context;
    private List<CategoryPojo> list;

    public CategoryAdapter(Context context,int resource,List<CategoryPojo> object){
        super(context,resource,object);
        this.context = context;
        this.list = object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_list_layout,parent, false);

        //get data from my CategoryPojo
        CategoryPojo categoryPojo = list.get(position);
        TextView tv = (TextView) view.findViewById(R.id.categoryText);
        tv.setText(categoryPojo.getName());
        return view;
    }
}
