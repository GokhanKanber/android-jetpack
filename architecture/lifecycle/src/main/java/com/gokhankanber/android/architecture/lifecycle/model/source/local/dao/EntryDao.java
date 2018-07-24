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

package com.gokhankanber.android.architecture.lifecycle.model.source.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.EntryItem;
import java.util.Date;
import java.util.List;

@Dao
public interface EntryDao
{
    @Query("SELECT entryid, updated, title, thumbnailurl FROM " + Entry.TABLE_NAME + " ORDER BY updated DESC")
    LiveData<List<EntryItem>> loadEntryList();

    @Query("SELECT entryid, updated, title, thumbnailurl FROM " + Entry.TABLE_NAME + " WHERE updated BETWEEN :from AND :to")
    LiveData<List<EntryItem>> loadEntryList(Date from, Date to);

    @Query("SELECT * FROM " + Entry.TABLE_NAME + " WHERE entryid = :entryId")
    LiveData<Entry> loadEntry(String entryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Entry> entryList); // long[], List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Entry entry);

    @Query("DELETE FROM " + Entry.TABLE_NAME + " WHERE entryid = :entryId")
    void delete(String entryId); // int

    @Delete
    void delete(Entry entry); // int
}
