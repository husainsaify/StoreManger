package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.storemanager.Functions;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.AutoCompleteProductPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCompleteProductAdapter extends ArrayAdapter<AutoCompleteProductPojo> {
    private static final String TAG = AutoCompleteProductAdapter.class.getSimpleName();
    private Context context;
    private List<AutoCompleteProductPojo> suggestion;
    private String userId;

    //Volley Request Queue
    private RequestQueue mRequestQueue;

    public AutoCompleteProductAdapter(Context context, String userId) {
        super(context, R.layout.ac_product_list_layout);
        this.suggestion = new ArrayList<>();
        this.context = context;
        this.userId = userId;

        //setup volley request queue
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
    }

    @Override
    public int getCount() {
        return suggestion.size();
    }

    @Override
    public AutoCompleteProductPojo getItem(int position) {
        return suggestion.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {
                    String search = constraint.toString();
                    //fetch data from the web & set to the list
                    getProductInBackground(search);

                    // Now assign the values and count to the FilterResults object
                    filterResults.values = suggestion;
                    filterResults.count = suggestion.size();

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return myFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.ac_product_list_layout, parent, false);

        //get data from my SimpleListPojo
        AutoCompleteProductPojo product = suggestion.get(position);
        TextView name = (TextView) view.findViewById(R.id.ACproductName);
        TextView code = (TextView) view.findViewById(R.id.ACproductCode);

        //hookUp product to view
        name.setText(product.getName());
        code.setText(product.getCode());
        return view;
    }

    /*
    * GET PRODUCT DATA FROM THE API
    *
    * */
    public void getProductInBackground(final String search){
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.AC_PRODUCT_SEARCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parse response
                parseProductResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle Volley error
                String errorString = VolleySingleton.handleVolleyError(error);
                if(errorString != null){
                    Toast.makeText(context,errorString,Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Keys.PRAM_AC_PRODUCTNAME,search);
                params.put(Keys.KEY_COM_USERID,userId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    //Method to parse response send by the API
    private void parseProductResponse(String response) {
        //parse the response
        List<AutoCompleteProductPojo> list = JsonParser.acProductSearchParser(response);

        //check list is not null
        if(list != null){ //json parse was succesfull
            //check return
            if(!list.get(0).getReturned()){
                Toast.makeText(context,list.get(0).getMessage(),Toast.LENGTH_LONG).show();
            }else if(list.get(0).getCount() == 0){ //check count
                Toast.makeText(context,"No Product found",Toast.LENGTH_SHORT).show();
            }else{ //means Product is successfully found
                suggestion = list;
            }
        }else{
            //json parsing failed
            Toast.makeText(context,R.string.unable_to_parse_response,Toast.LENGTH_LONG).show();
        }
    }
}
