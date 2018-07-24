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
import android.support.annotation.NonNull;
import com.gokhankanber.android.architecture.lifecycle.model.source.FeedRepository;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;

public class EntryViewModel extends AndroidViewModel
{
    private FeedRepository feedRepository;
    private String entryId;
    private LiveData<Entry> entry;

    public EntryViewModel(@NonNull Application application)
    {
        super(application);

        feedRepository = FeedRepository.getInstance(application);
    }

    public String getEntryId()
    {
        return entryId;
    }

    public void setEntryId(String entryId)
    {
        this.entryId = entryId;
    }

    public LiveData<Entry> getEntry()
    {
        if(entry == null)
        {
            entry = feedRepository.getEntry(entryId);
        }

        return entry;
    }

    public String getLink()
    {
        return getEntry().getValue().getLink();
    }

    public String getAuthorName()
    {
        return getEntry().getValue().getAuthorName();
    }
}
