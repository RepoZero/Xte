package com.xte.xte;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.text.TextUtils;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;



import java.net.URISyntaxException;
import java.util.ArrayList;

import utils.LruBitmapCache;

public class App extends android.app.Application {

    static Typeface B_Koodak_B;
    static Typeface roboto_light;
    static Typeface roboto_reguler;
    static Typeface roboto_thin;
    static Typeface segoe_light;
    static Typeface segoe_reguler;
    static Typeface segoe_thin;

    static String Server_Address = "http://xte.ibben.org/api/";

    static MediaPlayer NotficationSound;


    static ArrayList sender = new ArrayList<String>();
    static ArrayList text = new ArrayList<String>();
    static ArrayList guys = new ArrayList<String>();
    static String full_name;


    public static final String TAG = App.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static App mInstance;

    @Override
    public void onCreate() {


        super.onCreate();

        B_Koodak_B = Typeface.createFromAsset(getAssets(), "fonts/b_koodak_b.ttf");
        roboto_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        roboto_reguler = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        roboto_thin = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
        segoe_light = Typeface.createFromAsset(getAssets(), "fonts/segoe_light.ttf");
        segoe_reguler = Typeface.createFromAsset(getAssets(), "fonts/segoe_regular.ttf");
        segoe_thin = Typeface.createFromAsset(getAssets(), "fonts/segoe_thin.ttf");


        mInstance = this;


        NotficationSound = MediaPlayer.create(this, R.raw.xte);

    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }




}
