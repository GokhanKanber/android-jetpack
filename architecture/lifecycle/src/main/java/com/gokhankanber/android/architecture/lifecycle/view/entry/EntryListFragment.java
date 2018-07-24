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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.model.data.EventData;
import com.gokhankanber.android.architecture.lifecycle.model.data.ResponseData;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.EntryItem;
import com.gokhankanber.android.architecture.lifecycle.view.about.AboutActivity;
import com.gokhankanber.android.architecture.lifecycle.viewmodel.EntryListViewModel;
import java.util.List;

public class EntryListFragment extends Fragment
{
    private SwipeRefreshLayout entryListLayout;
    private RecyclerView entryListView;
    private RecyclerView.LayoutManager layoutManager;
    private EntryAdapter entryAdapter;
    private ProgressBar loadProgressView;
    private LinearLayout noEntryLayout;
    private EntryListViewModel entryListViewModel;

    public static EntryListFragment newInstance(boolean remote)
    {
        EntryListFragment entryListFragment = new EntryListFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(EntryActivity.EXTRA_REMOTE, remote);
        entryListFragment.setArguments(arguments);

        return entryListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);

        // create view code is separated for readability
        setEntryListLayout(view);
        setEntryListView(view);
        setLoadProgressView(view);
        setNoEntryLayout(view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        boolean remote = true;

        if(args != null)
        {
            remote = args.getBoolean(EntryActivity.EXTRA_REMOTE); // do not load from remote on up navigation
        }

        observe(remote);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EntryListActivity.REQUEST_CODE_VIEW
                && resultCode == EntryActivity.RESULT_CODE_DELETE
                && data != null)
        {
            final String entryId = data.getStringExtra(EntryActivity.EXTRA_ENTRY_ID);

            if(entryId != null && !entryId.isEmpty())
            {
                if(!entryListViewModel.isLoading())
                {
                    entryListViewModel.delete(entryId);
                }
                else
                {
                    /*
                     * Attempting to delete entry while refreshing from network.
                     * Do not delete in this condition.
                     * Or implement a cancel refresh strategy, and delete entry.
                     */
                    showResult(EventData.readEvent(getString(R.string.delete_while_refreshing)).getContent());
                }
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
                entryListViewModel.delete(entryAdapter.getEntry(entryAdapter.getPosition()).getEntryId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setEntryListLayout(View view)
    {
        entryListLayout = view.findViewById(R.id.srl_entry_list);
        entryListLayout.setOnRefreshListener(entryListRefreshListener);
    }

    private void setEntryListView(View view)
    {
        entryListView = view.findViewById(R.id.rv_entry_list);
        entryListView.setHasFixedSize(true);
        entryAdapter = new EntryAdapter(entryListener);
        entryListView.setAdapter(entryAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        entryListView.setLayoutManager(layoutManager);

        registerForContextMenu(entryListView);
    }

    private void setLoadProgressView(View view)
    {
        loadProgressView = view.findViewById(R.id.load_progress);
    }

    private void setNoEntryLayout(View view)
    {
        noEntryLayout = view.findViewById(R.id.ll_no_entry);
    }

    private void observe(boolean remote)
    {
        entryListViewModel = ViewModelProviders.of(this).get(EntryListViewModel.class);

        if(remote && !entryListLayout.isRefreshing())
        {
            entryListLayout.setRefreshing(true);
        }

        entryListViewModel.getEntryItemList(remote).observe(this, new Observer<ResponseData<List<EntryItem>>>()
        {
            @Override
            public void onChanged(@Nullable ResponseData<List<EntryItem>> entryItemListResponse)
            {
                showEntryListView(entryItemListResponse);
            }
        });

        entryListViewModel.getResult().observe(this, new Observer<EventData<String>>()
        {
            @Override
            public void onChanged(@Nullable EventData<String> eventData)
            {
                if(eventData != null)
                {
                    EventData<String>.Content content = eventData.getContent();
                    showResult(content);
                }
            }
        });
    }

    private void refresh()
    {
        if(!entryListLayout.isRefreshing())
        {
            entryListLayout.setRefreshing(true);
        }

        entryListViewModel.refresh();
    }

    private void showEntryListView(ResponseData<List<EntryItem>> entryItemListResponse)
    {
        if(!(entryItemListResponse.progress() && entryItemListResponse.getData() == null))
        {
            entryAdapter.setEntryList(entryItemListResponse.getData());
        }

        if(!entryItemListResponse.progress())
        {
            entryListLayout.setRefreshing(false);
        }

        if(entryAdapter.getItemCount() > 0)
        {
            entryListLayout.setVisibility(View.VISIBLE);
            loadProgressView.setVisibility(View.GONE);
            noEntryLayout.setVisibility(View.GONE);
        }
        else
        {
            entryListLayout.setVisibility(View.GONE);

            if(entryItemListResponse.progress())
            {
                loadProgressView.setVisibility(View.VISIBLE);
                noEntryLayout.setVisibility(View.GONE);
            }
            else
            {
                loadProgressView.setVisibility(View.GONE);
                noEntryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showResult(EventData<String>.Content content)
    {
        if(content != null)
        {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.fragment_container), content.getData(), Snackbar.LENGTH_SHORT);

            if(content.isDelete() && content.hasAction())
            {
                snackbar.setAction(R.string.undo, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        entryListViewModel.undo();
                    }
                });
            }

            snackbar.show();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener entryListRefreshListener = new SwipeRefreshLayout.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            entryListViewModel.refresh();
        }
    };

    private EntryListener entryListener = new EntryListener()
    {
        @Override
        public void loadImage(String url, ImageView imageView)
        {
            if(!url.isEmpty())
            {
                Glide.with(getContext()).load(url).into(imageView);
            }
            else
            {
                Glide.with(getContext()).load(R.drawable.ic_image_72dp).into(imageView);
            }
        }

        @Override
        public void onClick(String entryId)
        {
            Intent intent = new Intent(getActivity(), EntryActivity.class);
            intent.putExtra(EntryActivity.EXTRA_ENTRY_ID, entryId);
            startActivityForResult(intent, EntryListActivity.REQUEST_CODE_VIEW);
        }

        @Override
        public void onLongClick(int position)
        {
            entryAdapter.setPosition(position);
        }

        @Override
        public void OnCreateContextMenu(ContextMenu menu, String title)
        {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_entry_list_context, menu);
            menu.setHeaderTitle(title);
        }
    };

    public interface EntryListener
    {
        void loadImage(String url, ImageView imageView);
        void onClick(String entryId);
        void onLongClick(int position);
        void OnCreateContextMenu(ContextMenu menu, String title);
    }
}
