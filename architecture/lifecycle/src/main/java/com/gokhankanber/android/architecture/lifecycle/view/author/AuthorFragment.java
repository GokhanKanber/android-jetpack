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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Author;
import com.gokhankanber.android.architecture.lifecycle.view.about.AboutActivity;
import com.gokhankanber.android.architecture.lifecycle.viewmodel.AuthorViewModel;

public class AuthorFragment extends Fragment
{
    private AuthorViewModel authorViewModel;
    private ImageView thumbnailView;
    private TextView nameView;
    private TextView uriView;
    private TextView emailView;

    public static AuthorFragment newInstance(String name)
    {
        AuthorFragment authorFragment = new AuthorFragment();
        Bundle arguments = new Bundle();
        arguments.putString(AuthorActivity.EXTRA_AUTHOR_NAME, name);
        authorFragment.setArguments(arguments);

        return authorFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_author, container, false);
        thumbnailView = view.findViewById(R.id.thumbnail);
        nameView = view.findViewById(R.id.name);
        uriView = view.findViewById(R.id.uri);
        emailView = view.findViewById(R.id.email);
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
            String name = arguments.getString(AuthorActivity.EXTRA_AUTHOR_NAME);
            observe(name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void observe(final String name)
    {
        authorViewModel = ViewModelProviders.of(this).get(AuthorViewModel.class);
        authorViewModel.setName(name);

        authorViewModel.getAuthor().observe(this, new Observer<Author>()
        {
            @Override
            public void onChanged(@Nullable Author author)
            {
                if(!author.getThumbnailUrl().isEmpty())
                {
                    Glide.with(getContext()).load(author.getThumbnailUrl()).into(thumbnailView);
                }
                else
                {
                    Glide.with(getContext()).load(R.drawable.ic_image_72dp).into(thumbnailView);
                }

                nameView.setText(author.getName());
                uriView.setText(author.getUri());
                emailView.setText(author.getEmail());
            }
        });
    }
}
