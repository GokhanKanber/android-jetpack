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

package com.gokhankanber.android.architecture.paging.model.source.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = Note.TABLE_NAME)
public class Note
{
    @Ignore
    public static final String TABLE_NAME = "notes";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noteid")
    private long noteId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "updated")
    private Date updated;

    public Note()
    {
    }

    @Ignore
    public Note(long noteId, String title, String content)
    {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        updated = new Date();
    }

    public long getNoteId()
    {
        return noteId;
    }

    public void setNoteId(long noteId)
    {
        this.noteId = noteId;
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

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }
}
