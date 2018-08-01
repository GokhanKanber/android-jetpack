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

package com.gokhankanber.android.architecture.paging.view.note;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gokhankanber.android.architecture.paging.R;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.NoteItem;
import com.gokhankanber.android.architecture.paging.view.note.NoteListFragment.NoteListener;

// For a custom adapter, use PagedListAdapterHelper.
public class NoteAdapter extends PagedListAdapter<NoteItem, NoteViewHolder>
{
    private NoteListener noteListener;
    private int position;

    public NoteAdapter(NoteListener noteListener)
    {
        super(DIFF_CALLBACK);

        this.noteListener = noteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_note, parent, false);

        return new NoteViewHolder(view, noteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position)
    {
        NoteItem noteItem = getNoteItem(position);
        holder.bind(noteItem);
    }

    public NoteItem getNoteItem(int position)
    {
        return getItem(position);
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public static final DiffUtil.ItemCallback<NoteItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<NoteItem>()
    {
        @Override
        public boolean areItemsTheSame(NoteItem oldNote, NoteItem newNote)
        {
            return oldNote.getNoteId() == newNote.getNoteId();
        }

        @Override
        public boolean areContentsTheSame(NoteItem oldNote, NoteItem newNote)
        {
            return oldNote.getTitle().equals(newNote.getTitle())
                    && oldNote.getUpdated().getTime() == newNote.getUpdated().getTime();
        }
    };
}
