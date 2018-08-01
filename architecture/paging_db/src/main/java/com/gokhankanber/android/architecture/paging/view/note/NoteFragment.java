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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.gokhankanber.android.architecture.paging.R;
import com.gokhankanber.android.architecture.paging.model.data.EventData;
import com.gokhankanber.android.architecture.paging.model.source.local.entity.Note;
import com.gokhankanber.android.architecture.paging.view.about.AboutActivity;
import com.gokhankanber.android.architecture.paging.viewmodel.NoteViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteFragment extends Fragment
{
    private NoteViewModel noteViewModel;
    private EditText titleView;
    private TextView dateView;
    private EditText contentView;
    private FloatingActionButton okView;

    public static NoteFragment newInstance(long noteId)
    {
        NoteFragment noteFragment = new NoteFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(NoteActivity.EXTRA_NOTE_ID, noteId);
        noteFragment.setArguments(arguments);

        return noteFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        titleView = view.findViewById(R.id.title);
        dateView = view.findViewById(R.id.date);
        contentView = view.findViewById(R.id.content);
        okView = getActivity().findViewById(R.id.fab_ok);
        okView.setOnClickListener(okClickListener);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();

        if(arguments != null)
        {
            long noteId = arguments.getLong(NoteActivity.EXTRA_NOTE_ID);
            observe(noteId, savedInstanceState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_delete:
                delete();
                return true;
            case R.id.menu_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(NoteActivity.STATE_TITLE, titleView.getText().toString());
        outState.putString(NoteActivity.STATE_CONTENT, contentView.getText().toString());
    }

    private void observe(long noteId, final Bundle savedInstanceState)
    {
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.setNoteId(noteId);

        noteViewModel.getNote().observe(this, new Observer<Note>()
        {
            @Override
            public void onChanged(@Nullable Note note)
            {
                if(note != null)
                {
                    dateView.setText(format(note.getUpdated()));

                    if(savedInstanceState != null)
                    {
                        titleView.setText(savedInstanceState.getString(NoteActivity.STATE_TITLE));
                        contentView.setText(savedInstanceState.getString(NoteActivity.STATE_CONTENT));
                    }
                    else
                    {
                        titleView.setText(note.getTitle());
                        contentView.setText(note.getContent());
                    }
                }
            }
        });

        noteViewModel.getEventData().observe(this, new Observer<EventData<Note>>()
        {
            @Override
            public void onChanged(@Nullable EventData<Note> noteEventData)
            {
                if(noteEventData != null)
                {
                    EventData<Note>.Content content = noteEventData.getContent();
                    postResult(content);
                }
            }
        });
    }

    private void delete()
    {
        Intent data = new Intent();
        data.putExtra(NoteActivity.EXTRA_NOTE_ID, noteViewModel.getNoteId());
        getActivity().setResult(NoteActivity.RESULT_CODE_DELETE, data);
        getActivity().finish();
    }

    private String format(Date date)
    {
        SimpleDateFormat printDateFormat = new SimpleDateFormat("yyyy, MMMM dd - HH:mm", Locale.getDefault());

        return printDateFormat.format(date);
    }

    private void showResult(String message)
    {
        Snackbar.make(getActivity().findViewById(R.id.fragment_container), message, Snackbar.LENGTH_SHORT).show();
    }

    private void postResult(EventData<Note>.Content content)
    {
        if(content != null)
        {
            if(content.isCreate() || content.isUpdate())
            {
                getActivity().finish();
            }
        }
    }

    private View.OnClickListener okClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(!titleView.getText().toString().isEmpty())
            {
                noteViewModel.save(titleView.getText().toString(), contentView.getText().toString());
            }
            else
            {
                showResult(getString(R.string.empty_title));
            }
        }
    };
}
