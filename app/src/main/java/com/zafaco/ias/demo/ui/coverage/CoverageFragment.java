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

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;
import com.zafaco.ias.demo.MainActivity;
import com.zafaco.ias.demo.R;
import com.zafaco.ias.demo.databinding.FragmentCoverageBinding;
import com.zafaco.moduleCommon.Log;
import com.zafaco.moduleCoverage.Coverage;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class CoverageFragment extends Fragment
{
    private static final String TAG = "CoverageFragment";

    private View mView;
    private FragmentCoverageBinding binding;
    public Coverage mCoverage = null;
    private String mTrackID;
    private ConcurrentMap<String, AtomicLong> counter;
    private ActivityResultLauncher<String[]> resultListenerCoverage;

    private CoverageViewModel coverageViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        coverageViewModel = new ViewModelProvider(this).get(CoverageViewModel.class);

        binding = FragmentCoverageBinding.inflate(inflater, container, false);
        mView = binding.getRoot();

        coverageViewModel.getInfoText().observe(getViewLifecycleOwner(), binding.textInfo::setText);
        coverageViewModel.getLocationText().observe(getViewLifecycleOwner(), binding.textLocations::setText);

        resultListenerCoverage = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result ->
        {
            if (!result.containsValue(false))
            {
                getContext().bindService(new Intent(getContext(), Coverage.class), mConnection, Context.BIND_AUTO_CREATE);
            }
        });
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
            {
                resultListenerCoverage.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            } else
            {
                resultListenerCoverage.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        } else
        {
            getContext().bindService(new Intent(getContext(), Coverage.class), mConnection, Context.BIND_AUTO_CREATE);
        }


        return mView;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;

        getContext().stopService(new Intent(getContext(), Coverage.class));

        getContext().unbindService(mConnection);
        if (mCoverage != null)
        {
            mCoverage.onStopCommand();
            mCoverage = null;
        }
    }


    class IncomingHandlerCallback implements Handler.Callback
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            try
            {
                JSONObject jsonData = (JSONObject) msg.obj;

                LatLng position = new LatLng(jsonData.getDouble("app_latitude"), jsonData.getDouble("app_longitude"));

                sortingCategory(jsonData.getString("app_access_category"), jsonData.getString("app_operator_sim_mcc"), jsonData.getString("app_operator_sim_mnc"), position);

            } catch (Exception ex)
            {
                Log.warning(TAG, ex);
            }



            return true;
        }
    }

    class IncomingHandlerInfoCallback implements Handler.Callback
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            try
            {
                JSONObject jsonData = (JSONObject) msg.obj;





                if (mView == null || getContext() == null)
                {
                    return true;
                }

                switch (jsonData.getString("warning"))
                {
                    case "gps":
                        coverageViewModel.setInfoText("minAccuracyNotReached");
                        break;
                    case "wifi":
                        coverageViewModel.setInfoText("wifiActive");
                        break;
                    case "airplane":
                        coverageViewModel.setInfoText("airplaneModeActive");
                        break;
                    case "sim<1":
                        coverageViewModel.setInfoText("noSimCardActive");
                        break;
                    case "sim>1":
                        coverageViewModel.setInfoText("multipleSimCardsActive");
                        break;
                    case "age":
                        coverageViewModel.setInfoText("gpsToOld");
                        break;
                    case "no":
                        coverageViewModel.setInfoText("No warning");
                        break;
                }

            } catch (Exception ex)
            {
                Log.warning(TAG, ex);
            }



            return true;
        }
    }


    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mCoverage = ((Coverage.LocalBinder) service).getService();
            mCoverage.resetThreadCounter();
            mCoverage.setHandler(new Handler(new IncomingHandlerCallback()), new Handler(new IncomingHandlerInfoCallback()));
            mCoverage.setClass(MainActivity.class);

            mTrackID = mCoverage.getTrackID();

            if (!mCoverage.isRunning())
            {
                mTrackID = "" + System.currentTimeMillis();

                Intent intent = new Intent(getContext(), Coverage.class);


                intent.putExtra("min_time", 1000);
                intent.putExtra("min_accuracy", 50);
                intent.putExtra("min_distance", 1);
                intent.putExtra("min_distance_deadzone", 1);


                intent.putExtra("app_track", "");
                intent.putExtra("app_track_id", mTrackID);

                intent.putExtra("app_version", "demo");

                intent.putExtra("service", 102);

                intent.putExtra("app_icon", R.drawable.ic_notifications_black_24dp);
                intent.putExtra("app_action_icon", R.mipmap.ic_launcher);
                intent.putExtra("app_action_text", "text");
                intent.putExtra("app_action_fragment", 1);
                intent.putExtra("app_intent_class", MainActivity.class.getName());


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    getContext().startForegroundService(intent);
                } else
                {
                    getContext().startService(intent);
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mCoverage = null;
        }
    };


    private void sortingCategory(String app_access_category, String app_operator_sim_mcc, String app_operator_sim_mnc, LatLng position)
    {
        String point = "point(" +
                app_operator_sim_mcc + "-" + app_operator_sim_mnc + ", " +
                app_access_category + ", " +
                position.toString() + ")";

        coverageViewModel.appendLocationText(point);
    }
}