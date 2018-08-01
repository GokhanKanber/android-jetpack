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

package com.gokhankanber.android.architecture.paging.model.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import com.gokhankanber.android.architecture.paging.model.data.EventData;
import com.gokhankanber.android.architecture.paging.model.source.local.dao.NoteDao;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.Note;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.NoteItem;
import com.gokhankanber.android.architecture.paging.model.util.TaskRunner;

public class NoteLocalDataSource
{
    private final int pageSize = 30;
    private static volatile NoteLocalDataSource instance;
    private static final Object LOCK = new Object();
    private NoteDatabase noteDatabase;
    private NoteDao noteDao;

    private NoteLocalDataSource(Context context)
    {
        noteDatabase = NoteDatabase.getInstance(context);
        noteDao = noteDatabase.noteDao();
    }

    public static NoteLocalDataSource getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized(LOCK)
            {
                if(instance == null)
                {
                    instance = new NoteLocalDataSource(context);
                }
            }
        }

        return instance;
    }

    public LiveData<PagedList<NoteItem>> getNoteItemList()
    {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setPrefetchDistance(pageSize * 3)
                .setEnablePlaceholders(true)
                .build();

        return new LivePagedListBuilder<>(noteDao.loadNotes(), config)
                // .setFetchExecutor(fetchExecutor)
                .build();
    }

    public LiveData<EventData<Note>> delete(long noteId)
    {
        Note note = new Note();
        note.setNoteId(noteId);

        return delete(note);
    }

    public LiveData<EventData<Note>> delete(final Note note)
    {
        final MutableLiveData<EventData<Note>> eventData = new MutableLiveData<>();

        new TaskRunner()
        {
            @Override
            protected void task()
            {
                noteDao.delete(note.getNoteId());
            }

            @Override
            protected void post()
            {
                eventData.setValue(EventData.deleteEvent(note));
            }
        };

        return eventData;
    }

    public LiveData<EventData<Note>> insert(final Note note)
    {
        final MutableLiveData<EventData<Note>> eventData = new MutableLiveData<>();

        new TaskRunner()
        {
            @Override
            protected void task()
            {
                noteDao.insert(note);
            }

            @Override
            protected void post()
            {
                eventData.setValue(EventData.createEvent(note));
            }
        };

        return eventData;
    }

    public LiveData<EventData<Note>> update(final Note note)
    {
        final MutableLiveData<EventData<Note>> eventData = new MutableLiveData<>();

        new TaskRunner()
        {
            @Override
            protected void task()
            {
                noteDao.update(note);
            }

            @Override
            protected void post()
            {
                eventData.setValue(EventData.createEvent(note));
            }
        };

        return eventData;
    }

    public LiveData<Note> getNote(long noteId)
    {
        return noteDao.loadNote(noteId);
    }

    public LiveData<EventData<Note>> save(Note note)
    {
        if(note.getNoteId() == 0)
        {
            return insert(note);
        }
        else
        {
            return update(note);
        }
    }
}
