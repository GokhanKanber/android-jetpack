/*
 * Copyright 2018 Gökhan Kanber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gokhankanber.android.architecture.lifecycle.model.source.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.gokhankanber.android.architecture.lifecycle.model.data.ResponseData;
import java.io.File;

public class Volley
{
    private static final String DEFAULT_CACHE_DIR = "feed";
    private static final int DEFAULT_DISK_USAGE_BYTES = 5 * 1024 * 1024;
    private static volatile Volley instance;
    private static final Object LOCK = new Object();
    private Context context;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private StringRequest request;

    private Volley(Context context)
    {
        this.context = context;
    }

    public static Volley getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized(LOCK)
            {
                if(instance == null)
                {
                    instance = new Volley(context);
                }
            }
        }

        return instance;
    }

    private RequestQueue getRequestQueue()
    {
        if(requestQueue == null)
        {
            File cacheDir = new File(context.getApplicationContext().getCacheDir(), DEFAULT_CACHE_DIR);
            Cache cache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }

        return requestQueue;
    }

    public ImageLoader getImageLoader()
    {
        if(imageLoader == null)
        {
            imageLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache()
            {
                private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                @Override
                public Bitmap getBitmap(String url)
                {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap)
                {
                    cache.put(url, bitmap);
                }
            });
        }

        return imageLoader;
    }

    public LiveData<ResponseData<String>> request(String url, String tag)
    {
        final MutableLiveData<ResponseData<String>> webResponse = new MutableLiveData<>();
        // cancel(tag);

        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                webResponse.setValue(ResponseData.success(response));
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                webResponse.setValue(ResponseData.<String>error(error.getMessage()));
            }
        };

        request = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        request.setTag(tag);
        getRequestQueue().add(request);

        return webResponse;
    }

    public void cancel(String tag)
    {
        if(!tag.equals(""))
        {
            getRequestQueue().cancelAll(tag);
        }
    }
}
