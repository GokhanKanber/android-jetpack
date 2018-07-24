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

package com.gokhankanber.android.architecture.lifecycle.view.entry;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.EntryItem;
import com.gokhankanber.android.architecture.lifecycle.view.entry.EntryListFragment.EntryListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder>
{
    private EntryListener entryListener;
    private List<EntryItem> entryList;
    private int position;

    public EntryAdapter(EntryListener entryListener)
    {
        this.entryListener = entryListener;

    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_entry, parent, false);

        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position)
    {
        entryListener.loadImage(getEntry(position).getThumbnailUrl(), holder.thumbnailView);
        holder.titleView.setText(getEntry(position).getTitle());
        holder.dateView.setText(format(getEntry(position).getUpdated()));
    }

    @Override
    public int getItemCount()
    {
        return (entryList == null ? 0 : entryList.size());
    }

    public EntryItem getEntry(int position)
    {
        return entryList.get(position);
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public void setEntryList(final List<EntryItem> entryList)
    {
        if(entryList == null)
        {
            this.entryList = null;
            notifyDataSetChanged();

            return;
        }

        if(this.entryList == null)
        {
            this.entryList = entryList;
            notifyItemRangeInserted(0, entryList.size());
        }
        else
        {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback()
            {
                @Override
                public int getOldListSize()
                {
                    return getItemCount();
                }

                @Override
                public int getNewListSize()
                {
                    return entryList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
                {
                    return getEntry(oldItemPosition).getEntryId()
                            .equals(entryList.get(newItemPosition).getEntryId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
                {
                    EntryItem oldEntry = getEntry(oldItemPosition);
                    EntryItem newEntry = entryList.get(newItemPosition);

                    return oldEntry.getEntryId().equals(newEntry.getEntryId())
                            && oldEntry.getTitle().equals(newEntry.getTitle())
                            && oldEntry.getUpdated().getTime() == newEntry.getUpdated().getTime();
                }
            });

            this.entryList = entryList;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    private String format(Date date)
    {
        SimpleDateFormat printDateFormat = new SimpleDateFormat("yyyy, MMMM dd - HH:mm", Locale.getDefault());

        return printDateFormat.format(date);
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView thumbnailView;
        private final TextView titleView;
        private final TextView dateView;

        public EntryViewHolder(View itemView)
        {
            super(itemView);

            thumbnailView = itemView.findViewById(R.id.thumbnail);
            titleView = itemView.findViewById(R.id.title);
            dateView = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(clickListener);
            itemView.setOnLongClickListener(longClickListener);
            itemView.setOnCreateContextMenuListener(contextMenuListener);
        }

        private View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(getAdapterPosition() != -1)
                {
                    entryListener.onClick(getEntry(getAdapterPosition()).getEntryId());
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
                    entryListener.onLongClick(getAdapterPosition());
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
                    entryListener.OnCreateContextMenu(menu, getEntry(getAdapterPosition()).getTitle());
                }
            }
        };
    }
}
