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

package com.gokhankanber.android.architecture.paging.view.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.gokhankanber.android.architecture.paging.R;
import com.gokhankanber.android.architecture.paging.view.BaseActivity;

public class AboutActivity extends BaseActivity
{
    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        loadToolbar();
        loadView();
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
        aboutFragment = (AboutFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(aboutFragment == null)
        {
            aboutFragment = AboutFragment.newInstance();
            addFragment(getSupportFragmentManager(), aboutFragment, R.id.fragment_container);
        }
    }
}
