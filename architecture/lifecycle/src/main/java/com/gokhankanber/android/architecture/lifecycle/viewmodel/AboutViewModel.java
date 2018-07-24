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

package com.gokhankanber.android.architecture.lifecycle.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public class AboutViewModel extends AndroidViewModel
{
    private MutableLiveData<String> versionName;

    public AboutViewModel(@NonNull Application application)
    {
        super(application);
    }

    public LiveData<String> getVersionName()
    {
        if(versionName == null)
        {
            versionName = new MutableLiveData<>();

            try
            {
                // query on main thread?
                PackageInfo packageInfo = getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);

                versionName.setValue(packageInfo.versionName);
            }
            catch(PackageManager.NameNotFoundException e)
            {
                versionName.setValue(null);
                e.printStackTrace();
            }
        }

        return versionName;
    }
}
