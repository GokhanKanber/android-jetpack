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

package com.gokhankanber.android.architecture.paging.model.source;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import com.gokhankanber.android.architecture.paging.model.data.EventData;
import com.gokhankanber.android.architecture.paging.model.source.local.NoteLocalDataSource;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.Note;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.NoteItem;

public class NoteRepository
{
    private static volatile NoteRepository instance;
    private static final Object LOCK = new Object();
    private NoteLocalDataSource noteLocalDataSource;

    private NoteRepository(Context context)
    {
        noteLocalDataSource = NoteLocalDataSource.getInstance(context);
    }

    public static NoteRepository getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized(LOCK)
            {
                if(instance == null)
                {
                    instance = new NoteRepository(context);
                }
            }
        }

        return instance;
    }

    public LiveData<PagedList<NoteItem>> getNoteItemList()
    {
        return noteLocalDataSource.getNoteItemList();
    }

    public LiveData<EventData<Note>> delete(long noteId)
    {
        return noteLocalDataSource.delete(noteId);
    }

    public LiveData<Note> getNote(long noteId)
    {
        return noteLocalDataSource.getNote(noteId);
    }

    public LiveData<EventData<Note>> save(Note note)
    {
        return noteLocalDataSource.save(note);
    }
}
