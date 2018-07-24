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

package com.gokhankanber.android.architecture.lifecycle.model.source.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Author;
import java.util.Collection;

@Dao
public interface AuthorDao
{
    @Query("SELECT * FROM " + Author.TABLE_NAME + " WHERE name = :name")
    LiveData<Author> loadAuthor(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Collection<Author> authors);
}
