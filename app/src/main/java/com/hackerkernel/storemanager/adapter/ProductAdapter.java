package com.hackerkernel.storemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerkernel.storemanager.R;
import com.hackerkernel.storemanager.URL.DataUrl;
import com.hackerkernel.storemanager.pojo.ProductPojo;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<ProductPojo> {
    public static final String TAG = ProductPojo.class.getSimpleName();
    private Context context;
    private List<ProductPojo> list;

    public ProductAdapter(Context context, int resource, List<ProductPojo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.product_list_layout, parent, false);

        //get Data from ProductPojo
        ProductPojo productPojo = list.get(position);
        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView productCode = (TextView) view.findViewById(R.id.productCode);

        productName.setText(productPojo.getProductName());
        productCode.setText(productPojo.getProductCode());

        //Set Image

        //if image is already available
        if(productPojo.getBitmap() != null){
            ImageView productImage = (ImageView) view.findViewById(R.id.productImage);
            productImage.setImageBitmap(productPojo.getBitmap());
        }else{
            //Fetch Image with our LazyLoader
            Container container = new Container();
            container.productPojo = productPojo;
            container.view = view;

            new lazyLoadImage().execute(container);
        }
        return view;
    }

    class Container{
        public ProductPojo productPojo;
        public View view;
        public Bitmap bitmap;
    }

    //create a Async task to LazyLoad image
    private class lazyLoadImage extends AsyncTask<Container,Void,Container>{

        @Override
        protected Container doInBackground(Container... params) {
            Container container = params[0];
            ProductPojo productPojo = container.productPojo;

            //Check image is not empty
            Bitmap bitmap = null;
            if(!productPojo.getProductImage().isEmpty()){
                try{
                    //generate Image URL
                    String imageURL = DataUrl.IMAGE_BASE_URL + productPojo.getProductImage();
                    //get Image
                    InputStream in = (InputStream) new URL(imageURL).getContent();
                    //convert image into a bitmap
                    bitmap = BitmapFactory.decodeStream(in);
                    //store image
                    productPojo.setBitmap(bitmap);
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{ //generate a placeholder image
                //convert a Drawable into a Bitmap
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.placeholder_product);
                productPojo.setBitmap(bitmap);
            }
            //Add image to Container
            container.bitmap = bitmap;
            return container;
        }

        @Override
        protected void onPostExecute(Container container) {
            ImageView productImage = (ImageView) container.view.findViewById(R.id.productImage);
            productImage.setImageBitmap(container.bitmap);
            //save the bitmap for future use
            container.productPojo.setBitmap(container.bitmap);
        }
    }

}
