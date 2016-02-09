package com.hackerkernel.storemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.activity.ProductActivity;
import com.hackerkernel.storemanager.extras.ApiUrl;
import com.hackerkernel.storemanager.extras.Keys;
import com.hackerkernel.storemanager.network.VolleySingleton;
import com.hackerkernel.storemanager.pojo.ProductListPojo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter to display productList in a recyclerview
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ProductListPojo> mList;
    private ImageLoader mImageLoader;

    public ProductListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        //Instanciate the ImageLoader
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
    }

    public void setList(List<ProductListPojo> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.product_list_layout, parent, false);
        return new ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductListViewHolder holder, int position) {
        ProductListPojo c = mList.get(position);
        holder.name.setText(c.getProductName());
        holder.code.setText(c.getProductCode());
        holder.time.setText(c.getProductTime());

        //Set image
        String urlThumbnail = c.getProductImage();
        if(!urlThumbnail.isEmpty()){
            //Create Image Url
            String imageUrl = ApiUrl.IMAGE_BASE_URL + urlThumbnail;
            mImageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.image.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Handle volley error
                    String errorString = VolleySingleton.handleVolleyError(error);
                    if(errorString != null){
                        Toast.makeText(mContext,errorString,Toast.LENGTH_SHORT).show();
                        Log.d("HUS","HUS: productList: "+errorString+" volleyError "+error.getMessage());
                    }
                    //Set default image
                    holder.image.setImageResource(R.drawable.ic_image_not_available);
                }
            });
        }else{
            //Set default image
            holder.image.setImageResource(R.drawable.ic_image_not_available);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //ViewHolder Class
    class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.productImage) ImageView image;
        @Bind(R.id.productName) TextView name;
        @Bind(R.id.productCode) TextView code;
        @Bind(R.id.productTime) TextView time;
        public ProductListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            //Set OnClick
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ProductListPojo p = mList.get(position);
            Intent intent = new Intent(mContext, ProductActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Keys.KEY_COM_PRODUCTID,p.getProductId());
            intent.putExtra(Keys.KEY_PL_NAME,p.getProductName());
            mContext.startActivity(intent);
        }
    }
}
