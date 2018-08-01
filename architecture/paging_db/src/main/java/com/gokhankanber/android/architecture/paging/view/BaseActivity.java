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

package com.gokhankanber.android.architecture.paging.view;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.gokhankanber.android.architecture.paging.R;

public abstract class BaseActivity extends AppCompatActivity
{
    protected Toolbar toolbar;
    protected ActionBar actionBar;

    protected abstract void loadView();

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                return up();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @CallSuper
    protected void loadToolbar()
    {
        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
    }

    protected void addFragment(FragmentManager fragmentManager, Fragment fragment, int frameId)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public boolean up()
    {
        Intent upIntent = NavUtils.getParentActivityIntent(this);

        if(NavUtils.shouldUpRecreateTask(this, upIntent))
        {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        }
        else
        {
            NavUtils.navigateUpTo(this, upIntent);
        }

        return true;
    }
}
