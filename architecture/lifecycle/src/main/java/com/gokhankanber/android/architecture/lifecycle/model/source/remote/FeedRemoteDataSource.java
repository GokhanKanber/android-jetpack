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
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import com.gokhankanber.android.architecture.lifecycle.model.data.Feed;
import com.gokhankanber.android.architecture.lifecycle.model.data.ResponseData;
import com.gokhankanber.android.architecture.lifecycle.model.util.TaskRunner;

public class FeedRemoteDataSource
{
    private static final String URL = "http://android-developers.blogspot.com/atom.xml";
    private static final String TAG = "android-developers-blogspot";
    private static volatile FeedRemoteDataSource instance;
    private static final Object LOCK = new Object();
    private Volley volley;

    private FeedRemoteDataSource(Context context)
    {
        volley = Volley.getInstance(context);
    }

    public static FeedRemoteDataSource getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized(LOCK)
            {
                if(instance == null)
                {
                    instance = new FeedRemoteDataSource(context);
                }
            }
        }

        return instance;
    }

    public LiveData<ResponseData<Feed>> getFeed()
    {
        final MediatorLiveData<ResponseData<Feed>> feed = new MediatorLiveData<>();
        final LiveData<ResponseData<String>> webResponse = volley.request(URL, TAG);
        feed.addSource(webResponse, new Observer<ResponseData<String>>()
        {
            @Override
            public void onChanged(@Nullable ResponseData<String> webResponseValue)
            {
                feed.removeSource(webResponse);

                if(webResponseValue != null)
                {
                    if(webResponseValue.success())
                    {
                        final LiveData<Feed> newFeed = parse(webResponseValue.getData());
                        feed.addSource(newFeed, new Observer<Feed>()
                        {
                            @Override
                            public void onChanged(@Nullable Feed feedValue)
                            {
                                feed.removeSource(newFeed);
                                feed.setValue(ResponseData.success(feedValue));
                            }
                        });
                    }
                    else if(webResponseValue.error())
                    {
                        feed.setValue(ResponseData.<Feed>error(webResponseValue.getMessage()));
                    }
                }
            }
        });

        return feed;
    }

    private LiveData<Feed> parse(final String xml)
    {
        final MutableLiveData<Feed> feed = new MutableLiveData<>();

        if(xml != null)
        {
            new TaskRunner()
            {
                @Override
                protected void task()
                {
                    XmlParser xmlParser = new XmlParser();
                    Feed feedValue = xmlParser.parse(xml);
                    feed.postValue(feedValue);
                }
            };
        }

        return feed;
    }
}
