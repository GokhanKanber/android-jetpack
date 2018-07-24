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

public class ResponseData<T>
{
    private enum Status
    {
        PROGRESS,
        SUCCESS,
        ERROR
    }

    private Status status;
    private String message;
    private T data;

    private ResponseData(Status status, String message, T data)
    {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public boolean progress()
    {
        return status == Status.PROGRESS;
    }

    public boolean success()
    {
        return status == Status.SUCCESS;
    }

    public boolean error()
    {
        return status == Status.ERROR;
    }

    public String getMessage()
    {
        return message;
    }

    public T getData()
    {
        return data;
    }

    public static <T> ResponseData<T> progress(T data)
    {
        return new ResponseData<>(Status.PROGRESS, null, data);
    }

    public static <T> ResponseData<T> success(T data)
    {
        return success(null, data);
    }

    public static <T> ResponseData<T> success(String message, T data)
    {
        return new ResponseData<>(Status.SUCCESS, message, data);
    }

    public static <T> ResponseData<T> error(String message)
    {
        return new ResponseData<>(Status.ERROR, message, null);
    }
}
