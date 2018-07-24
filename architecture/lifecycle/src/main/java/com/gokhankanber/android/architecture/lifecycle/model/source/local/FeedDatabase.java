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

package com.gokhankanber.android.architecture.lifecycle.model.source.local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.converter.DateConverter;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.dao.AuthorDao;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.dao.CategoryDao;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.dao.EntryDao;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Author;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Category;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;

@Database(entities = {Author.class, Entry.class, Category.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class FeedDatabase extends RoomDatabase
{
    private static final String DATABASE_NAME = "feed-content";
    private static FeedDatabase instance;
    private static final Object LOCK = new Object();
    public abstract AuthorDao authorDao();
    public abstract EntryDao entryDao();
    public abstract CategoryDao categoryDao();

    public static FeedDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            synchronized(LOCK)
            {
                if(instance == null)
                {
                    instance = Room.databaseBuilder(context.getApplicationContext(), FeedDatabase.class, DATABASE_NAME)
                            // .addMigrations(MIGRATION1_2)
                            .build();
                }
            }
        }

        return instance;
    }

    private static final Migration MIGRATION1_2 = new Migration(1, 2)
    {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase)
        {
            supportSQLiteDatabase.execSQL("CREATE TABLE x_" + Entry.TABLE_NAME);
        }
    };
}
