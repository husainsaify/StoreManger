package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.hackerkernel.storemanager.Functions;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.model.GetJson;
import com.hackerkernel.storemanager.parser.JsonParser;
import com.hackerkernel.storemanager.pojo.ACProductSearchPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ACProductAdapter extends ArrayAdapter<ACProductSearchPojo> {
    private static final String TAG = ACProductAdapter.class.getSimpleName();
    private Context context;
    private List<ACProductSearchPojo> suggestion;
    private String userId;

    public ACProductAdapter(Context context, String pName,String userId) {
        super(context, R.layout.ac_product_list_layout);
        this.suggestion = new ArrayList<>();
        this.context = context;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return suggestion.size();
    }

    @Override
    public ACProductSearchPojo getItem(int position) {
        return suggestion.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if(constraint != null){
                    String search = constraint.toString();
                    //fetch data from the web
                    try {
                        suggestion = new ACProductSearchTask().execute(search,userId).get();

                        if(suggestion.size() < 1){
                            Toast.makeText(context,"No product found",Toast.LENGTH_SHORT).show();
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        Log.d(TAG,"HUS: "+e);
                    }

                    // Now assign the values and count to the FilterResults object
                    filterResults.values = suggestion;
                    filterResults.count = suggestion.size();

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
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
        ACProductSearchPojo product = suggestion.get(position);
        TextView name = (TextView) view.findViewById(R.id.ACproductName);
        TextView code = (TextView) view.findViewById(R.id.ACproductCode);

        //hookUp product to view
        name.setText(product.getName());
        code.setText(product.getCode());
        return view;
    }

    /*
    *
    * ASYNC TASK
    * */

    private class ACProductSearchTask extends AsyncTask<String,Void,List<ACProductSearchPojo>> {

        @Override
        protected List<ACProductSearchPojo> doInBackground(String... params) {
            //generate hashmap to send to backend
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("s", params[0]);
            hashMap.put("user_id",params[1]);
            //convert hashmap into encoded url
            String data = Functions.hashMapToEncodedUrl(hashMap);

            //get data from the backend
            String jsonString = GetJson.request(ApiUrl.AC_PRODUCT_SEARCH, data, "POST");
            //parse json
            return JsonParser.acProductSearchParser(jsonString);
        }
    }
}
