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

package com.zafaco.ias.demo.ui.coverage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CoverageViewModel extends ViewModel
{

    private final MutableLiveData<String> mInfoText;
    private final MutableLiveData<String> mLocationText;

    public CoverageViewModel()
    {
        mInfoText = new MutableLiveData<>();
        mInfoText.setValue("Info");

        mLocationText = new MutableLiveData<>();
        mLocationText.setValue("Points:");
    }

    public LiveData<String> getInfoText()
    {
        return mInfoText;
    }

    public LiveData<String> getLocationText()
    {
        return mLocationText;
    }

    public void setInfoText(String text)
    {
        mInfoText.setValue(text);
    }

    public void appendLocationText(String text)
    {
        String currentText = mLocationText.getValue();
        currentText += "\n";
        currentText += text;
        mLocationText.setValue(currentText);
    }
}