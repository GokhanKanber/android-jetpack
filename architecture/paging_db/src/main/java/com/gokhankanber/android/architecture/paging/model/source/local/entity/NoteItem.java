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
import java.util.Date;

public class NoteItem
{
    @ColumnInfo(name = "noteid")
    private long noteId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "updated")
    private Date updated;

    public long getNoteId()
    {
        return noteId;
    }

    public void setNoteId(long noteId)
    {
        this.noteId = noteId;
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
}
