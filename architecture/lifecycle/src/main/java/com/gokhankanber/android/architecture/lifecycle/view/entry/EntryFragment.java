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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.gokhankanber.android.architecture.lifecycle.R;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.CategoryName;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;
import com.gokhankanber.android.architecture.lifecycle.view.about.AboutActivity;
import com.gokhankanber.android.architecture.lifecycle.view.author.AuthorActivity;
import com.gokhankanber.android.architecture.lifecycle.viewmodel.EntryViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryFragment extends Fragment
{
    private EntryViewModel entryViewModel;
    private WebView contentView;

    public static EntryFragment newInstance(String entryId)
    {
        EntryFragment entryFragment = new EntryFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EntryActivity.EXTRA_ENTRY_ID, entryId);
        entryFragment.setArguments(arguments);

        return entryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        contentView = view.findViewById(R.id.content);
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
            String entryId = arguments.getString(EntryActivity.EXTRA_ENTRY_ID);
            observe(entryId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_copy_link:
                copyLink();
                return true;
            case R.id.menu_open_in_browser:
                openInBrowser();
                return true;
            case R.id.menu_author:
                Intent intent = new Intent(getActivity(), AuthorActivity.class);
                intent.putExtra(AuthorActivity.EXTRA_AUTHOR_NAME, entryViewModel.getAuthorName());
                startActivity(intent);
                return true;
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

    private void observe(String entryId)
    {
        entryViewModel = ViewModelProviders.of(this).get(EntryViewModel.class);
        entryViewModel.setEntryId(entryId);

        entryViewModel.getEntry().observe(this, new Observer<Entry>()
        {
            @Override
            public void onChanged(@Nullable Entry entry)
            {
                if(entry != null)
                {
                    contentView.loadData(getContent(entry), "text/html", "utf-8");
                }
            }
        });
    }

    /**
     * Styles html content.
     * Fits images to the screen width.
     * Adds title, date, and link as header.
     * @param entry
     * @return html data
     */
    private String getContent(Entry entry)
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int padding = 8;
        String style = "<style>"
                + "body"
                + "{"
                + "    margin: " + padding + "px;"
                + "}"
                + "h3.header"
                + "{"
                + "color: #2f3699;"
                + "}"
                + "span.header, a.header"
                + "{"
                + "    color: #aaaaaa;"
                + "}"
                + "div.container"
                + "{"
                + "    word-wrap: break-word;"
                + "}"
                + "img"
                + "{"
                + "    max-width: " + ((int) (displayMetrics.widthPixels / displayMetrics.density) - 2 * padding) + "px;"
                + "}"
                + "</style>";
        String header = "<h3 class=\"header\">" + entry.getTitle() + "</h3>"
                + "<span class=\"header\">" + format(entry.getUpdated()) + "</span><br /><br />"
                + "<a class=\"header\" href=\"" + entry.getLink() + "\">" + entry.getLink() + "</a><br /><br />"
                + getCategories(entry);
        String data = style + "<div class=\"container\">" + header + entry.getContent() + "</div>";

        return data;
    }

    private String getCategories(Entry entry)
    {
        String categoryNames = "";
        int length;

        for(CategoryName categoryName : entry.getCategoryNameList())
        {
            categoryNames += "<span class=\"header\">" + categoryName.getName() + "</span>, ";
        }

        length = categoryNames.length();

        if(length > 2)
        {
            categoryNames = categoryNames.substring(0, length - 2) + "<br /><br />";
        }

        return categoryNames;
    }

    private void copyLink()
    {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("link", entryViewModel.getLink());
        clipboardManager.setPrimaryClip(clipData);
        Snackbar.make(getActivity().findViewById(R.id.fragment_container), getString(R.string.link_is_copied), Snackbar.LENGTH_SHORT).show();
    }

    private void openInBrowser()
    {
        Uri uri = Uri.parse(entryViewModel.getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void delete()
    {
        Intent data = new Intent();
        data.putExtra(EntryActivity.EXTRA_ENTRY_ID, entryViewModel.getEntryId());
        getActivity().setResult(EntryActivity.RESULT_CODE_DELETE, data);
        getActivity().finish();
    }

    private String format(Date date)
    {
        SimpleDateFormat printDateFormat = new SimpleDateFormat("yyyy, MMMM dd - HH:mm", Locale.getDefault());

        return printDateFormat.format(date);
    }
}
