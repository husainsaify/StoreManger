package com.hackerkernel.storemanager.network;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hackerkernel.storemanager.application.MyApplication;

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    //constructor- To make a new Volley Request object
    protected VolleySingleton(){
        //request que
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());

        //create image loader
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            int maxSize = (int) Runtime.getRuntime().maxMemory() / 1024 / 8;
            LruCache<String,Bitmap> cache = new LruCache<>(maxSize);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url,bitmap);
            }
        });
    }

    public static VolleySingleton getInstance(){
        if(mInstance == null){
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }
}
