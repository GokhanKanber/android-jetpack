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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import com.gokhankanber.android.architecture.paging.R;
import com.gokhankanber.android.architecture.paging.view.BaseActivity;

public class NoteActivity extends BaseActivity
{
    public static final String EXTRA_NOTE_ID = "note-id";
    public static final String STATE_TITLE = "note-title";
    public static final String STATE_CONTENT = "note-content";
    public static final int RESULT_CODE_DELETE = 100;
    private NoteFragment noteFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        loadToolbar();
        loadView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_note, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void loadToolbar()
    {
        super.loadToolbar();

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void loadView()
    {
        noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        long noteId = 0;

        if(noteFragment == null)
        {
            if(getIntent() != null)
            {
                noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, 0);
            }

            noteFragment = NoteFragment.newInstance(noteId);
            addFragment(getSupportFragmentManager(), noteFragment, R.id.fragment_container);
        }
    }
}
