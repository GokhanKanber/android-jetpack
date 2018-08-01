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

package com.gokhankanber.android.architecture.paging.model.source.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.Note;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.NoteItem;

@Dao
public interface NoteDao
{
    @Query("SELECT noteid, title, updated FROM " + Note.TABLE_NAME)
    DataSource.Factory<Integer, NoteItem> loadNotes();

    @Query("SELECT * FROM " + Note.TABLE_NAME + " WHERE noteid = :noteId")
    LiveData<Note> loadNote(long noteId);

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Query("DELETE FROM " + Note.TABLE_NAME + " WHERE noteid = :noteId")
    void delete(long noteId); // int

    @Delete
    void delete(Note note);
}
