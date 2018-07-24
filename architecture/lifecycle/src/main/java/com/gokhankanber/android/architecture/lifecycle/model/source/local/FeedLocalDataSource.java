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

package com.gokhankanber.android.architecture.lifecycle.model.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.model.data.Feed;
import com.gokhankanber.android.architecture.lifecycle.model.data.ResponseData;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.dao.AuthorDao;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.dao.CategoryDao;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.dao.EntryDao;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Author;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Category;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.CategoryName;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.EntryItem;
import com.gokhankanber.android.architecture.lifecycle.model.util.TaskRunner;
import java.util.ArrayList;
import java.util.List;

public class FeedLocalDataSource
{
    private static volatile FeedLocalDataSource instance;
    private static final Object LOCK = new Object();
    private Resources resources;
    private FeedDatabase feedDatabase;
    private AuthorDao authorDao;
    private EntryDao entryDao;
    private CategoryDao categoryDao;

    private FeedLocalDataSource(Context context)
    {
        resources = context.getApplicationContext().getResources();
        feedDatabase = FeedDatabase.getInstance(context);
        authorDao = feedDatabase.authorDao();
        entryDao = feedDatabase.entryDao();
        categoryDao = feedDatabase.categoryDao();
    }

    public static FeedLocalDataSource getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized(LOCK)
            {
                if(instance == null)
                {
                    instance = new FeedLocalDataSource(context);
                }
            }
        }

        return instance;
    }

    public LiveData<List<EntryItem>> getEntryItemList()
    {
        return entryDao.loadEntryList();
    }

    public LiveData<List<EntryItem>> insert(final Feed feed)
    {
        final MediatorLiveData<List<EntryItem>> entryItemList = new MediatorLiveData<>();

        new TaskRunner()
        {
            @Override
            protected void task()
            {
                authorDao.insert(feed.getAuthors().values());
                entryDao.insert(feed.getEntryList());

                List<Category> categoryList = new ArrayList<>();

                for(Entry entry : feed.getEntryList())
                {
                    if(entry.getCategoryList() != null)
                    {
                        categoryList.addAll(entry.getCategoryList());
                    }
                }

                categoryDao.insert(categoryList);
            }

            @Override
            protected void post()
            {
                final LiveData<List<EntryItem>> localEntryItemList = getEntryItemList();
                entryItemList.addSource(localEntryItemList, new Observer<List<EntryItem>>()
                {
                    @Override
                    public void onChanged(@Nullable List<EntryItem> entryItemListValue)
                    {
                        entryItemList.removeSource(localEntryItemList);
                        entryItemList.setValue(entryItemListValue);
                    }
                });
            }
        };

        return entryItemList;
    }

    public LiveData<ResponseData<List<EntryItem>>> delete(final String entryId)
    {
        final MediatorLiveData<ResponseData<List<EntryItem>>> entryItemList = new MediatorLiveData<>();

                new TaskRunner()
                {
                    @Override
                    protected void task()
                    {
                        entryDao.delete(entryId);
                    }

                    @Override
                    protected void post()
                    {
                        final LiveData<List<EntryItem>> newEntryItemList = getEntryItemList();
                        entryItemList.addSource(newEntryItemList, new Observer<List<EntryItem>>()
                        {
                            @Override
                            public void onChanged(@Nullable List<EntryItem> entryItemListValue)
                            {
                                entryItemList.removeSource(newEntryItemList);
                                entryItemList.setValue(ResponseData.success(resources.getString(R.string.entry_deleted), entryItemListValue));
                            }
                        });
                    }
                };

        return entryItemList;
    }

    public LiveData<ResponseData<List<EntryItem>>> insert(final Entry entry)
    {
        final MediatorLiveData<ResponseData<List<EntryItem>>> entryItemList = new MediatorLiveData<>();

        new TaskRunner()
        {
            @Override
            protected void task()
            {
                entryDao.insert(entry);
            }

            @Override
            protected void post()
            {
                final LiveData<List<EntryItem>> newEntryItemList = getEntryItemList();
                entryItemList.addSource(newEntryItemList, new Observer<List<EntryItem>>()
                {
                    @Override
                    public void onChanged(@Nullable List<EntryItem> entryItemListValue)
                    {
                        entryItemList.removeSource(newEntryItemList);
                        entryItemList.setValue(ResponseData.success(entryItemListValue));
                    }
                });
            }
        };

        return entryItemList;
    }

    public LiveData<Entry> getEntry(String entryId)
    {
        final MediatorLiveData<Entry> result = new MediatorLiveData<>();
        final LiveData<Entry> entry = entryDao.loadEntry(entryId);
        result.addSource(entry, new Observer<Entry>()
        {
            @Override
            public void onChanged(@Nullable final Entry entryValue)
            {
                result.removeSource(entry);
                final LiveData<List<CategoryName>> categoryNameList = categoryDao.loadCategoryNameList(entryValue.getEntryId());
                result.addSource(categoryNameList, new Observer<List<CategoryName>>()
                {
                    @Override
                    public void onChanged(@Nullable List<CategoryName> categoryNameListValue)
                    {
                        result.removeSource(categoryNameList);
                        entryValue.setCategoryNameList(categoryNameListValue);
                        result.setValue(entryValue);
                    }
                });
            }
        });

        return result;
    }

    public LiveData<Author> getAuthor(String name)
    {
        return authorDao.loadAuthor(name);
    }
}
