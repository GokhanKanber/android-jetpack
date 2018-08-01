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

package com.gokhankanber.android.architecture.paging.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.gokhankanber.android.architecture.paging.model.data.EventData;
import com.gokhankanber.android.architecture.paging.model.source.NoteRepository;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.Note;

public class NoteViewModel extends AndroidViewModel
{
    private NoteRepository noteRepository;
    private LiveData<Note> note;
    private MediatorLiveData<EventData<Note>> eventData;
    private long noteId;

    public NoteViewModel(@NonNull Application application)
    {
        super(application);

        noteRepository = NoteRepository.getInstance(application);
    }

    public long getNoteId()
    {
        return noteId;
    }

    public void setNoteId(long noteId)
    {
        this.noteId = noteId;
    }

    public LiveData<Note> getNote()
    {
        if(note == null)
        {
            note = noteRepository.getNote(noteId);
        }

        return note;
    }

    public LiveData<EventData<Note>> getEventData()
    {
        if(eventData == null)
        {
            eventData = new MediatorLiveData<>();
        }

        return eventData;
    }

    public void save(String title, String content)
    {
        final LiveData<EventData<Note>> source = noteRepository.save(new Note(noteId, title, content));
        eventData.addSource(source, new Observer<EventData<Note>>()
        {
            @Override
            public void onChanged(@Nullable EventData<Note> noteEventData)
            {
                eventData.removeSource(source);
                eventData.setValue(noteEventData);
            }
        });
    }
}
