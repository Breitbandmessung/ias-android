/*
 *     Copyright (C) 2016-2025 zafaco GmbH
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License version 3
 *     as published by the Free Software Foundation.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zafaco.ias.demo.ui.speed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SpeedViewModel extends ViewModel
{

    private final MutableLiveData<String> mStatus;
    private final MutableLiveData<String> mJsonResult;
    private final MutableLiveData<String> mDownload;
    private final MutableLiveData<String> mUpload;
    private final MutableLiveData<String> mRtt;

    public SpeedViewModel()
    {
        mStatus = new MutableLiveData<>();
        mStatus.setValue("-");
        mJsonResult = new MutableLiveData<>();
        mJsonResult.setValue("");
        mDownload = new MutableLiveData<>();
        mDownload.setValue("-");
        mUpload = new MutableLiveData<>();
        mUpload.setValue("-");
        mRtt = new MutableLiveData<>();
        mRtt.setValue("-");
    }

    public LiveData<String> getStatus()
    {
        return mStatus;
    }

    public LiveData<String> getJsonResult()
    {
        return mJsonResult;
    }

    public LiveData<String> getDownload()
    {
        return mDownload;
    }

    public LiveData<String> getUpload()
    {
        return mUpload;
    }

    public LiveData<String> getRtt()
    {
        return mRtt;
    }

    public void setStatus(String value)
    {
        mStatus.setValue(value);
    }

    public void setJsonResult(String value)
    {
        mJsonResult.setValue(value);
    }

    public void setUpload(String value)
    {
        mUpload.setValue(value);
    }

    public void setDownload(String value)
    {
        mDownload.setValue(value);
    }

    public void setRtt(String value)
    {
        mRtt.setValue(value);
    }

}