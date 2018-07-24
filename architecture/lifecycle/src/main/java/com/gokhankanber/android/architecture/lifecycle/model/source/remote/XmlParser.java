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

package com.gokhankanber.android.architecture.lifecycle.model.source.remote;

import android.util.Xml;
import com.gokhankanber.android.architecture.lifecycle.model.data.Feed;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Author;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.CategoryName;
import com.gokhankanber.android.architecture.lifecycle.model.source.local.entity.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class XmlParser
{
    private static final String NS = null;
    private static final String NS_FEEDBURNER = "feedburner";
    private static final String NS_MEDIA = "media";
    private static final String NS_GD = "gd";
    private static final String ROOT = "feed";
    private static final String ITEM = "entry";
    private static final String ENTRY_ID = "id";
    private static final String PUBLISHED = "published";
    private static final String UPDATED = "updated";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String LINK = NS_FEEDBURNER + ":origLink";
    private static final String THUMBNAIL = NS_MEDIA + ":thumbnail";
    private static final String CATEGORY = "category";
    private static final String AUTHOR = "author";
    private static final String NAME = "name";
    private static final String URI = "uri";
    private static final String EMAIL = "email";
    private static final String AUTHOR_THUMBNAIL = NS_GD + ":image";
    private static final String ATTRIBUTE_URL = "url";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String ATTRIBUTE_TERM = "term";
    private Map<String, Author> authors;

    public Feed parse(String xml)
    {
        XmlPullParser xmlPullParser = Xml.newPullParser();
        List<Entry> entryList = null;

        try
        {
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(new StringReader(xml));
            xmlPullParser.nextTag();
            entryList = read(xmlPullParser);
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return new Feed(entryList, authors);
    }

    private List<Entry> read(XmlPullParser xmlPullParser)
    {
        List<Entry> entryList = null;

        try
        {
            xmlPullParser.require(XmlPullParser.START_TAG, NS, ROOT);

            while(xmlPullParser.next() != XmlPullParser.END_TAG)
            {
                if(xmlPullParser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }

                String name = xmlPullParser.getName();

                if(name.equals(ITEM))
                {
                    if(entryList == null)
                    {
                        entryList = new ArrayList<>();
                    }

                    Entry entry = getEntry(xmlPullParser);

                    if(entry != null)
                    {
                        entryList.add(entry);
                    }
                }
                else
                {
                    skip(xmlPullParser);
                }
            }
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return entryList;
    }

    private Entry getEntry(XmlPullParser xmlPullParser)
    {
        String entryId = "";
        Date published = null;
        Date updated = null;
        String title = "";
        String content = "";
        String link = "";
        String thumbnailUrl = "";
        List<CategoryName> categoryNameList = null;
        String authorName = "";

        try
        {
            xmlPullParser.require(XmlPullParser.START_TAG, NS, ITEM);

            while(xmlPullParser.next() != XmlPullParser.END_TAG)
            {
                if(xmlPullParser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }

                String name = xmlPullParser.getName();

                if(name.equals(ENTRY_ID))
                {
                    entryId = getValue(xmlPullParser, ENTRY_ID);
                }
                else if(name.equals(PUBLISHED))
                {
                    published = parseDate(getValue(xmlPullParser, PUBLISHED));
                }
                else if(name.equals(UPDATED))
                {
                    updated = parseDate(getValue(xmlPullParser, UPDATED));
                }
                else if(name.equals(TITLE))
                {
                    title = getValue(xmlPullParser, TITLE);
                }
                else if(name.equals(CONTENT))
                {
                    content = getValue(xmlPullParser, CONTENT);
                }
                else if(name.equals(LINK))
                {
                    link = getValue(xmlPullParser, LINK);
                }
                else if(name.equals(THUMBNAIL))
                {
                    thumbnailUrl = getAttributeValue(xmlPullParser, THUMBNAIL, ATTRIBUTE_URL);
                }
                else if(name.equals(CATEGORY))
                {
                    if(categoryNameList == null)
                    {
                        categoryNameList = new ArrayList<>();
                    }

                    categoryNameList.add(new CategoryName(getAttributeValue(xmlPullParser, CATEGORY, ATTRIBUTE_TERM)));
                }
                else if(name.equals(AUTHOR))
                {
                    authorName = getAuthorName(xmlPullParser);
                }
                else
                {
                    skip(xmlPullParser);
                }
            }

            if(!entryId.isEmpty())
            {
                Entry entry = new Entry(entryId, published, updated, title, content, link, thumbnailUrl, authorName);
                entry.setCategoryNameList(categoryNameList);

                return entry;
            }
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String getAuthorName(XmlPullParser xmlPullParser)
    {
        String authorName = "";
        String uri = "";
        String email = "";
        String thumbnailUrl = "";

        try
        {
            xmlPullParser.require(XmlPullParser.START_TAG, NS, AUTHOR);

            while(xmlPullParser.next() != XmlPullParser.END_TAG)
            {
                if(xmlPullParser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }

                String name = xmlPullParser.getName();

                if(name.equals(NAME))
                {
                    authorName = getValue(xmlPullParser, NAME);
                }
                else if(name.equals(URI))
                {
                    uri = getValue(xmlPullParser, URI);
                }
                else if(name.equals(EMAIL))
                {
                    email = getValue(xmlPullParser, EMAIL);
                }
                else if(name.equals(AUTHOR_THUMBNAIL))
                {
                    thumbnailUrl = getAttributeValue(xmlPullParser, AUTHOR_THUMBNAIL, ATTRIBUTE_SRC);
                }
                else
                {
                    skip(xmlPullParser);
                }
            }

            if(!authorName.isEmpty())
            {
                putAuthor(authorName, new Author(authorName, uri, email, thumbnailUrl));

                return authorName;
            }
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String getValue(XmlPullParser xmlPullParser, String tag)
    {
        String value = "";

        try
        {
            xmlPullParser.require(XmlPullParser.START_TAG, NS, tag);
            value = getText(xmlPullParser);
            xmlPullParser.require(XmlPullParser.END_TAG, NS, tag);
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return value;
    }

    private String getAttributeValue(XmlPullParser xmlPullParser, String tag, String attribute)
    {
        String value = "";

        try
        {
            xmlPullParser.require(XmlPullParser.START_TAG, NS, tag);
            String name = xmlPullParser.getName();

            if(name.equals(tag))
            {
                value = xmlPullParser.getAttributeValue(null, attribute);
                xmlPullParser.nextTag();
            }

            xmlPullParser.require(XmlPullParser.END_TAG, NS, tag);
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return value;
    }

    private String getText(XmlPullParser xmlPullParser)
    {
        String text = "";

        try
        {
            if(xmlPullParser.next() == XmlPullParser.TEXT)
            {
                text = xmlPullParser.getText();
                xmlPullParser.nextTag();
            }
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return text;
    }

    private void skip(XmlPullParser xmlPullParser)
    {
        try
        {
            if(xmlPullParser.getEventType() != XmlPullParser.START_TAG)
            {
                throw new IllegalStateException();
            }

            int depth = 1;

            while(depth != 0)
            {
                switch(xmlPullParser.next())
                {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }
        catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void putAuthor(String name, Author author)
    {
        if(authors == null)
        {
            authors = new HashMap<>();
        }

        authors.put(name, author);
    }

    private Date parseDate(String formattedDate)
    {
        Date date = null;

        try
        {
            if(formattedDate != null)
            {
                /*
                 * To get timezone:
                 * Remove ':' character from timezone, then use Z letter in date format.
                 * For Android 24 and above, no need to remove ':'. Use X letter.
                 */
                int length = formattedDate.length();

                if(length == 29)
                {
                    String timeZoneId = formattedDate.substring(length - 6, length).replace(":", "");
                    formattedDate = formattedDate.substring(0, length - 6) + timeZoneId;
                    SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
                    date = parseDateFormat.parse(formattedDate);
                }
            }
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        return date;
    }
}
