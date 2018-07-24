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

package com.gokhankanber.android.architecture.lifecycle.model.data;

import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Author;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;
import java.util.List;
import java.util.Map;

public class Feed
{
    private List<Entry> entryList;
    private Map<String, Author> authors;

    public Feed(List<Entry> entryList, Map<String, Author> authors)
    {
        this.entryList = entryList;
        this.authors = authors;
    }

    public List<Entry> getEntryList()
    {
        return entryList;
    }

    public Map<String, Author> getAuthors()
    {
        return authors;
    }
}
