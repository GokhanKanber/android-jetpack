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

package com.gokhankanber.android.architecture.lifecycle.model.util;

import android.arch.core.executor.ArchTaskExecutor;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import java.util.concurrent.Executor;

public abstract class TaskRunner
{
    public TaskRunner()
    {
        this(ArchTaskExecutor.getIOThreadExecutor());
    }

    public TaskRunner(@NonNull Executor executor)
    {
        executor.execute(taskRunnable);
    }

    Runnable taskRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            task();
            ArchTaskExecutor.getMainThreadExecutor().execute(postRunnable);
        }
    };

    Runnable postRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            post();
        }
    };

    @WorkerThread
    protected abstract void task();

    @MainThread
    protected void post()
    {
    }
}
