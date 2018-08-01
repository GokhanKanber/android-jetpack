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

package com.gokhankanber.android.architecture.paging.model.data;

public class EventData<T>
{
    private enum Event
    {
        CREATE,
        READ,
        UPDATE,
        DELETE
    }

    private boolean dispatched;
    private Content content;

    public EventData(Event event, T data)
    {
        this(event, data, false);
    }

    public EventData(Event event, T data, boolean action)
    {
        content = new Content(event, data, action);
    }

    public static <T> EventData<T> createEvent(T data)
    {
        return new EventData<>(Event.CREATE, data, false);
    }

    public static <T> EventData<T> readEvent(T data)
    {
        return new EventData<>(Event.READ, data, false);
    }

    public static <T> EventData<T> updateEvent(T data)
    {
        return new EventData<>(Event.UPDATE, data, false);
    }

    public static <T> EventData<T> deleteEvent(T data)
    {
        return new EventData<>(Event.DELETE, data, true);
    }

    public Content getContent()
    {
        if(!dispatched)
        {
            dispatched = true;
        }
        else
        {
            content = null;
        }

        return content;
    }

    public class Content
    {
        private Event event;
        private T data;
        private boolean action;

        private Content(Event event, T data, boolean action)
        {
            this.event = event;
            this.data = data;
            this.action = action;
        }

        public boolean isCreate()
        {
            return event == Event.CREATE;
        }

        public boolean isUpdate()
        {
            return event == Event.UPDATE;
        }

        public boolean isDelete()
        {
            return event == Event.DELETE;
        }

        public T getData()
        {
            return data;
        }

        public boolean hasAction()
        {
            return action;
        }
    }
}
