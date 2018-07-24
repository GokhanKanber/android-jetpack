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

package com.gokhankanber.android.architecture.lifecycle.view.about;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.viewmodel.AboutViewModel;

public class AboutFragment extends Fragment
{
    private AboutViewModel aboutViewModel;
    private TextView versionView;

    public static AboutFragment newInstance()
    {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        versionView = view.findViewById(R.id.version);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        observe();
    }

    private void observe()
    {
        aboutViewModel = ViewModelProviders.of(this).get(AboutViewModel.class);

        aboutViewModel.getVersionName().observe(this, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String versionName)
            {
                if(versionName != null && !versionName.isEmpty())
                {
                    versionView.setText(getString(R.string.version, versionName));
                }
                else
                {
                    versionView.setText(null);
                }
            }
        });
    }
}
