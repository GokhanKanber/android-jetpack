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

package com.gokhankanber.android.architecture.lifecycle.model.source.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = Entry.TABLE_NAME,
        foreignKeys = @ForeignKey(entity = Author.class, parentColumns = "name", childColumns = "authorname"),
        indices = @Index("authorname"))
public class Entry
{
    @Ignore
    public static final String TABLE_NAME = "entries";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private String entryId;

    @ColumnInfo(name = "published")
    private Date published;

    @ColumnInfo(name = "updated")
    private Date updated;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "thumbnailurl")
    private String thumbnailUrl;

    @ColumnInfo(name = "authorname")
    private String authorName;

    @Ignore
    private List<CategoryName> categoryNameList;

    public Entry()
    {
    }

    @Ignore
    public Entry(String entryId, Date published, Date updated, String title, String content, String link, String thumbnailUrl, String authorName)
    {
        this.entryId = entryId;
        this.published = published;
        this.updated = updated;
        this.title = title;
        this.content = content;
        this.link = link;
        this.thumbnailUrl = thumbnailUrl;
        this.authorName = authorName;
    }

    public String getEntryId()
    {
        return entryId;
    }

    public void setEntryId(String entryId)
    {
        this.entryId = entryId;
    }

    public Date getPublished()
    {
        return published;
    }

    public void setPublished(Date published)
    {
        this.published = published;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }

    public List<Category> getCategoryList()
    {
        List<Category> categoryList = null;

        if(categoryNameList != null)
        {
            categoryList = new ArrayList<>();

            for(CategoryName categoryName : categoryNameList)
            {
                categoryList.add(new Category(entryId, categoryName.getName()));
            }
        }

        return categoryList;
    }

    public List<CategoryName> getCategoryNameList()
    {
        return categoryNameList;
    }

    public void setCategoryNameList(List<CategoryName> categoryNameList)
    {
        this.categoryNameList = categoryNameList;
    }
}
