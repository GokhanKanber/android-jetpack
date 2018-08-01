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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.gokhankanber.android.architecture.paging.R;
import com.gokhankanber.android.architecture.paging.model.data.EventData;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.Note;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.NoteItem;
import com.gokhankanber.android.architecture.paging.view.about.AboutActivity;
import com.gokhankanber.android.architecture.paging.viewmodel.NoteListViewModel;

public class NoteListFragment extends Fragment
{
    private SwipeRefreshLayout noteListLayout;
    private RecyclerView noteListView;
    private RecyclerView.LayoutManager layoutManager;
    private NoteAdapter noteAdapter;
    private LinearLayout noNoteLayout;
    private FloatingActionButton addNoteView;
    private NoteListViewModel noteListViewModel;

    public static NoteListFragment newInstance()
    {
        return new NoteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        // create view code is separated for readability
        setNoteListLayout(view);
        setNoteListView(view);
        setNoNoteLayout(view);
        setAddNoteView();
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        observe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NoteListActivity.REQUEST_CODE_VIEW
                && resultCode == NoteActivity.RESULT_CODE_DELETE
                && data != null)
        {
            final long noteId = data.getLongExtra(NoteActivity.EXTRA_NOTE_ID, 0);

            if(noteId != 0)
            {
                noteListViewModel.delete(noteId);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_refresh:
                refresh();
                return true;
            case R.id.menu_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_delete:
                noteListViewModel.delete(noteAdapter.getNoteItem(noteAdapter.getPosition()).getNoteId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setNoteListLayout(View view)
    {
        noteListLayout = view.findViewById(R.id.srl_note_list);
        noteListLayout.setOnRefreshListener(noteListRefreshListener);
    }

    private void setNoteListView(View view)
    {
        noteListView = view.findViewById(R.id.rv_note_list);
        noteListView.setHasFixedSize(true);
        noteAdapter = new NoteAdapter(noteListener);
        noteListView.setAdapter(noteAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        noteListView.setLayoutManager(layoutManager);

        registerForContextMenu(noteListView);
    }

    private void setNoNoteLayout(View view)
    {
        noNoteLayout = view.findViewById(R.id.ll_no_note);
    }

    private void setAddNoteView()
    {
        addNoteView = getActivity().findViewById(R.id.fab_add_note);
        addNoteView.setOnClickListener(addNoteClickListener);
    }

    private void observe()
    {
        noteListViewModel = ViewModelProviders.of(this).get(NoteListViewModel.class);

        noteListViewModel.getNotePagedList().observe(this, new Observer<PagedList<NoteItem>>()
        {
            @Override
            public void onChanged(@Nullable PagedList<NoteItem> notePagedList)
            {
                showNoteListView(notePagedList);
            }
        });

        noteListViewModel.getEventData().observe(this, new Observer<EventData<Note>>()
        {
            @Override
            public void onChanged(@Nullable EventData<Note> noteEventData)
            {
                if(noteEventData != null)
                {
                    EventData<Note>.Content content = noteEventData.getContent();

                    if(content != null && content.isDelete())
                    {
                        showResult(EventData.deleteEvent(getString(R.string.note_deleted)).getContent());
                    }
                }
            }
        });
    }

    private void refresh()
    {
        if(!noteListLayout.isRefreshing())
        {
            noteListLayout.setRefreshing(true);
        }

        noteListViewModel.refresh();
    }

    private void showNoteListView(PagedList<NoteItem> notePagedList)
    {
        int size = 0;

        if(notePagedList != null)
        {
            size = notePagedList.size();
            noteAdapter.submitList(notePagedList);
        }

        noteListLayout.setRefreshing(false);

        if(size > 0)
        {
            noteListLayout.setVisibility(View.VISIBLE);
            noNoteLayout.setVisibility(View.GONE);
        }
        else
        {
            noteListLayout.setVisibility(View.GONE);
            noNoteLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showResult(EventData<String>.Content content)
    {
        if(content != null)
        {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.fragment_container),
                    content.getData(), Snackbar.LENGTH_SHORT);

            if(content.isDelete() && content.hasAction())
            {
                snackbar.setAction(R.string.undo, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        noteListViewModel.undo();
                    }
                });
            }

            snackbar.show();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener noteListRefreshListener = new SwipeRefreshLayout.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            noteListViewModel.refresh();
        }
    };

    private View.OnClickListener addNoteClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(getActivity(), NoteActivity.class);
            startActivity(intent);
        }
    };

    private NoteListener noteListener = new NoteListener()
    {
        @Override
        public void onClick(int position)
        {
            Intent intent = new Intent(getActivity(), NoteActivity.class);
            intent.putExtra(NoteActivity.EXTRA_NOTE_ID, noteAdapter.getNoteItem(position).getNoteId());
            startActivityForResult(intent, NoteListActivity.REQUEST_CODE_VIEW);
        }

        @Override
        public void onLongClick(int position)
        {
            noteAdapter.setPosition(position);
        }

        @Override
        public void OnCreateContextMenu(ContextMenu menu, int position)
        {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_note_list_context, menu);
            menu.setHeaderTitle(noteAdapter.getNoteItem(position).getTitle());
        }
    };

    public interface NoteListener
    {
        void onClick(int position);
        void onLongClick(int position);
        void OnCreateContextMenu(ContextMenu menu, int position);
    }
}
