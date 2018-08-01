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

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;
import com.gokhankanber.android.architecture.paging.R;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.NoteItem;
import com.gokhankanber.android.architecture.paging.view.note.NoteListFragment.NoteListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteViewHolder extends RecyclerView.ViewHolder
{
    private NoteListener noteListener;
    private Resources resources;
    private TextView noteIdView;
    private TextView titleView;
    private TextView dateView;

    public NoteViewHolder(View itemView, NoteListener noteListener)
    {
        super(itemView);

        this.noteListener = noteListener;
        resources = itemView.getResources();
        noteIdView = itemView.findViewById(R.id.noteId);
        titleView = itemView.findViewById(R.id.title);
        dateView = itemView.findViewById(R.id.date);

        itemView.setOnClickListener(clickListener);
        itemView.setOnLongClickListener(longClickListener);
        itemView.setOnCreateContextMenuListener(contextMenuListener);
    }

    public void bind(NoteItem noteItem)
    {
        if(noteItem == null)
        {
            titleView.setText(resources.getString(R.string.loading));
        }
        else
        {
            noteIdView.setText(Long.toString(noteItem.getNoteId()));
            titleView.setText(noteItem.getTitle());
            dateView.setText(format(noteItem.getUpdated()));
        }
    }

    private String format(Date date)
    {
        SimpleDateFormat printDateFormat = new SimpleDateFormat("yyyy, MMMM dd - HH:mm", Locale.getDefault());

        return printDateFormat.format(date);
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(getAdapterPosition() != -1)
            {
                noteListener.onClick(getAdapterPosition());
            }
        }
    };

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view)
        {
            if(getAdapterPosition() != -1)
            {
                noteListener.onLongClick(getAdapterPosition());
            }

            return false;
        }
    };

    private View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener()
    {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
        {
            if(getAdapterPosition() != -1)
            {
                noteListener.OnCreateContextMenu(menu, getAdapterPosition());
            }
        }
    };
}
