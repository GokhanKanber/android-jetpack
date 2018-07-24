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

package com.gokhankanber.android.architecture.lifecycle.view.author;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.view.BaseActivity;

public class AuthorActivity extends BaseActivity
{
    public static final String EXTRA_AUTHOR_NAME = "author-name";
    private AuthorFragment authorFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        loadToolbar();
        loadView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_author, menu);

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
        authorFragment = (AuthorFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(authorFragment == null && getIntent() != null)
        {
            String name = getIntent().getStringExtra(EXTRA_AUTHOR_NAME);
            authorFragment = AuthorFragment.newInstance(name);
            addFragment(getSupportFragmentManager(), authorFragment, R.id.fragment_container);
        }
    }
}
