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
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.gokhankanber.android.architecture.paging.model.data.EventData;
import com.gokhankanber.android.architecture.paging.model.source.NoteRepository;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.Note;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.NoteItem;

public class NoteListViewModel extends AndroidViewModel
{
    private NoteRepository noteRepository;
    private LiveData<PagedList<NoteItem>> notePagedList;
    private MediatorLiveData<EventData<Note>> eventData;
    private LiveData<Note> deletedNote;

    public NoteListViewModel(@NonNull Application application)
    {
        super(application);

        noteRepository = NoteRepository.getInstance(application);
    }

    public LiveData<PagedList<NoteItem>> getNotePagedList()
    {
        if(notePagedList == null)
        {
            notePagedList = noteRepository.getNoteItemList();
        }

        return notePagedList;
    }

    public LiveData<EventData<Note>> getEventData()
    {
        if(eventData == null)
        {
            eventData = new MediatorLiveData<>();
        }

        return eventData;
    }

    public void refresh()
    {
        PagedList<NoteItem> notePagedListValue = notePagedList.getValue();

        if(notePagedListValue != null)
        {
            notePagedListValue.getDataSource().invalidate();
        }
    }

    public void delete(final long noteId)
    {
        // Get note data before delete
        deletedNote = noteRepository.getNote(noteId);

        eventData.addSource(deletedNote, new Observer<Note>()
        {
            @Override
            public void onChanged(@Nullable Note note)
            {
                // note data is set
                eventData.removeSource(deletedNote);
                deletedNote.getValue().setNoteId(0); // for undo, set id value to zero to insert note.
                final LiveData<EventData<Note>> source = noteRepository.delete(noteId); // delete note and observe for result
                eventData.addSource(source, new Observer<EventData<Note>>()
                {
                    @Override
                    public void onChanged(@Nullable EventData<Note> noteEventData)
                    {
                        // note is deleted
                        eventData.removeSource(source);
                        eventData.setValue(noteEventData);
                    }
                });
            }
        });
    }

    public void undo()
    {
        if(deletedNote.getValue() != null)
        {
            noteRepository.save(deletedNote.getValue());
        }
    }
}
