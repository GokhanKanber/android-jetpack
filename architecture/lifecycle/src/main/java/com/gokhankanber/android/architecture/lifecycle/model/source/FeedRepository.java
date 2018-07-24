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

package com.gokhankanber.android.architecture.lifecycle.model.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.model.data.Feed;
import com.gokhankanber.android.architecture.lifecycle.model.data.ResponseData;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.FeedLocalDataSource;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Author;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.EntryItem;
import com.gokhankanber.android.architecture.lifecycle.model.source.remote.FeedRemoteDataSource;
import java.util.List;

public class FeedRepository
{
    private Resources resources;
    private static volatile FeedRepository instance;
    private static final Object LOCK = new Object();
    private FeedLocalDataSource feedLocalDataSource;
    private FeedRemoteDataSource feedRemoteDataSource;

    private FeedRepository(Context context)
    {
        resources = context.getResources();
        feedLocalDataSource = FeedLocalDataSource.getInstance(context);
        feedRemoteDataSource = FeedRemoteDataSource.getInstance(context);
    }

    public static FeedRepository getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized(LOCK)
            {
                if(instance == null)
                {
                    instance = new FeedRepository(context);
                }
            }
        }

        return instance;
    }

    public LiveData<ResponseData<List<EntryItem>>> getEntryItemList(boolean refresh, boolean remote)
    {
        MediatorLiveData<ResponseData<List<EntryItem>>> entryItemList = new MediatorLiveData<>();
        entryItemList.setValue(ResponseData.<List<EntryItem>>progress(null)); // start load progress

        if(refresh)
        {
            refresh(entryItemList);
        }
        else
        {
            load(entryItemList, remote);
        }

        return entryItemList;
    }

    private void load(final MediatorLiveData<ResponseData<List<EntryItem>>> entryItemList, final boolean remote)
    {
        final LiveData<List<EntryItem>> localEntryItemList = feedLocalDataSource.getEntryItemList();
        entryItemList.addSource(localEntryItemList, new Observer<List<EntryItem>>()
        {
            @Override
            public void onChanged(@Nullable List<EntryItem> entryItemListValue)
            {
                entryItemList.removeSource(localEntryItemList);

                if(remote)
                {
                    entryItemList.setValue(ResponseData.progress(entryItemListValue)); // view local data before refresh
                    refresh(entryItemList);
                }
                else
                {
                    entryItemList.setValue(ResponseData.success(entryItemListValue));
                }
            }
        });
    }

    /**
     * Refresh from remote.
     * @param entryItemList
     */
    private void refresh(final MediatorLiveData<ResponseData<List<EntryItem>>> entryItemList)
    {
        final LiveData<ResponseData<Feed>> feed = feedRemoteDataSource.getFeed();
        entryItemList.addSource(feed, new Observer<ResponseData<Feed>>()
        {
            @Override
            public void onChanged(@Nullable ResponseData<Feed> feedValue)
            {
                entryItemList.removeSource(feed);

                if(feedValue != null)
                {
                    if(feedValue.success())
                    {
                        final LiveData<List<EntryItem>> newLocalEntryItemList = feedLocalDataSource.insert(feedValue.getData());
                        entryItemList.addSource(newLocalEntryItemList, new Observer<List<EntryItem>>()
                        {
                            @Override
                            public void onChanged(@Nullable List<EntryItem> entryItemListValue)
                            {
                                entryItemList.removeSource(newLocalEntryItemList);
                                entryItemList.setValue(ResponseData.success(resources.getString(R.string.network_data_loaded), entryItemListValue));
                            }
                        });
                    }
                    else if(feedValue.error())
                    {
                        entryItemList.setValue(ResponseData.<List<EntryItem>>error(feedValue.getMessage()));
                    }
                }
            }
        });
    }

    public LiveData<ResponseData<List<EntryItem>>> delete(String entryId)
    {
        return feedLocalDataSource.delete(entryId);
    }

    public LiveData<ResponseData<List<EntryItem>>> insert(Entry entry)
    {
        return feedLocalDataSource.insert(entry);
    }

    public LiveData<Entry> getEntry(String entryId)
    {
        return feedLocalDataSource.getEntry(entryId);
    }

    public LiveData<Author> getAuthor(String name)
    {
        return feedLocalDataSource.getAuthor(name);
    }
}
