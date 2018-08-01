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
import android.view.Menu;
import android.view.MenuInflater;
import com.gokhankanber.android.architecture.paging.R;
import com.gokhankanber.android.architecture.paging.view.BaseActivity;

public class NoteListActivity extends BaseActivity
{
    public static final int REQUEST_CODE_VIEW = 100;
    private NoteListFragment noteListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        loadToolbar();
        loadView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_note_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void loadToolbar()
    {
        super.loadToolbar();

        actionBar.setTitle(R.string.notes);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void loadView()
    {
        noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(noteListFragment == null)
        {
            noteListFragment = NoteListFragment.newInstance();
            addFragment(getSupportFragmentManager(), noteListFragment, R.id.fragment_container);
        }
    }
}
