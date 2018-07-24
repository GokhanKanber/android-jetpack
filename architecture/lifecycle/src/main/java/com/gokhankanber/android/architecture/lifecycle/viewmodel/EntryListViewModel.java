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

package com.gokhankanber.android.architecture.lifecycle.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.gokhankanber.android.architecture.lifecycle.model.data.EventData;
import com.gokhankanber.android.architecture.lifecycle.model.data.ResponseData;
import com.gokhankanber.android.architecture.lifecycle.model.source.FeedRepository;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.EntryItem;
import java.util.List;

public class EntryListViewModel extends AndroidViewModel
{
    private FeedRepository feedRepository;
    private MediatorLiveData<ResponseData<List<EntryItem>>> entryItemList;
    private MutableLiveData<EventData<String>> result;
    private LiveData<Entry> deletedEntry;
    private boolean loading;

    public EntryListViewModel(@NonNull Application application)
    {
        super(application);

        feedRepository = FeedRepository.getInstance(application);
    }

    public void refresh()
    {
        load(true, true);
    }

    public LiveData<ResponseData<List<EntryItem>>> getEntryItemList(boolean remote)
    {
        if(entryItemList == null)
        {
            entryItemList = new MediatorLiveData<>();
            load(false, remote);
        }

        return entryItemList;
    }

    public LiveData<EventData<String>> getResult()
    {
        if(result == null)
        {
            result = new MutableLiveData<>();
        }

        return result;
    }

    public boolean isLoading()
    {
        return loading;
    }

    public void setLoading(boolean loading)
    {
        this.loading = loading;
    }

    /**
     * Launch app       : false, true   (load local data immediately, then load remote data)
     * Up navigation    : false, false  (load only local data)
     * Swipe to refresh : true, true    (local data is already loaded, load remote data)
     * @param refresh
     * @param remote
     */
    private void load(final boolean refresh, boolean remote)
    {
        loading = true;
        final LiveData<ResponseData<List<EntryItem>>> newEntryItemList = feedRepository.getEntryItemList(refresh, remote);
        entryItemList.addSource(newEntryItemList, new Observer<ResponseData<List<EntryItem>>>()
        {
            @Override
            public void onChanged(@Nullable ResponseData<List<EntryItem>> entryItemListValue)
            {
                entryItemList.setValue(entryItemListValue);

                if(entryItemListValue != null && entryItemListValue.success())
                {
                    loading = false;
                    entryItemList.removeSource(newEntryItemList);
                }
            }
        });
    }

    public void delete(final String entryId)
    {
        deletedEntry = feedRepository.getEntry(entryId);
        entryItemList.addSource(deletedEntry, new Observer<Entry>()
        {
            @Override
            public void onChanged(@Nullable Entry entry)
            {
                entryItemList.removeSource(deletedEntry);
                final LiveData<ResponseData<List<EntryItem>>> newEntryItemList = feedRepository.delete(entryId);
                entryItemList.addSource(newEntryItemList, new Observer<ResponseData<List<EntryItem>>>()
                {
                    @Override
                    public void onChanged(@Nullable ResponseData<List<EntryItem>> entryItemListResponseData)
                    {
                        entryItemList.removeSource(newEntryItemList);
                        entryItemList.setValue(entryItemListResponseData);

                        if(entryItemListResponseData != null)
                        {
                            result.setValue(EventData.deleteEvent(entryItemListResponseData.getMessage()));
                        }
                    }
                });
            }
        });
    }

    public void undo()
    {
        if(deletedEntry.getValue() != null)
        {
            final LiveData<ResponseData<List<EntryItem>>> newEntryItemList = feedRepository.insert(deletedEntry.getValue());
            entryItemList.addSource(newEntryItemList, new Observer<ResponseData<List<EntryItem>>>()
            {
                @Override
                public void onChanged(@Nullable ResponseData<List<EntryItem>> entryItemListResponseData)
                {
                    entryItemList.removeSource(newEntryItemList);
                    entryItemList.setValue(entryItemListResponseData);
                }
            });
        }
    }
}
