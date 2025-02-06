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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zafaco.ias.demo.databinding.FragmentSpeedBinding;
import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleCommon.interfaces.MeasurementListener;
import com.zafaco.moduleSpeed.api.Speed;
import com.zafaco.moduleSpeed.models.measurement.MeasurementResult;
import com.zafaco.moduleSpeed.models.rtt.RttUdpResult;
import com.zafaco.moduleSpeed.models.speed.BandwidthResult;

import org.json.JSONException;
import org.json.JSONObject;

public class SpeedFragment extends Fragment
{

    private FragmentSpeedBinding binding;
    private SpeedViewModel speedViewModel;
    private JSONObject mappingInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        speedViewModel =
                new ViewModelProvider(this).get(SpeedViewModel.class);

        binding = FragmentSpeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Speed speed = Speed.getInstance(getContext());

        JSONObject mappingIasInfo = Tool.readJSON(requireContext(), "mapping_ias.json");
        JSONObject mappingAndroidInfo = Tool.readJSON(requireContext(), "mapping_android.json");
        mappingInfo = new Tool().mergeJSON(mappingIasInfo, mappingAndroidInfo);


        speedViewModel.getRtt().observe(getViewLifecycleOwner(), binding.rtt::setText);
        speedViewModel.getDownload().observe(getViewLifecycleOwner(), binding.download::setText);
        speedViewModel.getUpload().observe(getViewLifecycleOwner(), binding.upload::setText);
        speedViewModel.getStatus().observe(getViewLifecycleOwner(), binding.status::setText);
        speedViewModel.getJsonResult().observe(getViewLifecycleOwner(), binding.resultText::setText);

        Button start = binding.buttonStart;
        start.setOnClickListener(v ->
        {
            getActivity().runOnUiThread(() -> speedViewModel.setUpload("-"));
            getActivity().runOnUiThread(() -> speedViewModel.setDownload("-"));
            getActivity().runOnUiThread(() -> speedViewModel.setRtt("-"));
            getActivity().runOnUiThread(() -> speedViewModel.setStatus("-"));
            getActivity().runOnUiThread(() -> speedViewModel.setJsonResult(""));
            speed.startMeasurement(speedListener);

        });

        Button stop = binding.buttonStop;
        stop.setOnClickListener(v ->
        {
            speed.stopMeasurement();
            getActivity().runOnUiThread(() -> binding.buttonStart.setEnabled(true));
            getActivity().runOnUiThread(() -> binding.buttonStop.setEnabled(false));
        });

        return root;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Speed.getInstance(getContext()).stopMeasurement();
    }

    private final MeasurementListener<MeasurementResult> speedListener = new MeasurementListener<MeasurementResult>()
    {
        @Override
        public void onReport(MeasurementResult result)
        {
            getActivity().runOnUiThread(() -> speedViewModel.setStatus(result.getMsg()));
            switch (result.getTestCase())
            {
                case UPLOAD:
                {
                    BandwidthResult upload = result.getUploadInfo();

                    if (upload != null)
                        getActivity().runOnUiThread(() -> speedViewModel.setUpload(String.valueOf(upload.getThroughputAvgBps() / 1000000F)));
                }

                case DOWNLOAD:
                {
                    BandwidthResult upload = result.getDownloadInfo();

                    if (upload != null)
                        getActivity().runOnUiThread(() -> speedViewModel.setDownload(String.valueOf(upload.getThroughputAvgBps() / 1000000F)));
                }

                case RTT_UDP:
                {
                    RttUdpResult upload = result.getRttResult();

                    if (upload != null)
                        getActivity().runOnUiThread(() -> speedViewModel.setRtt(String.valueOf(upload.getAverageNs() / 1000000F)));
                }

            }
            JSONObject mapped = Tool.mapJSON(result.toJson(), mappingInfo);
            getActivity().runOnUiThread(() ->
            {
                try
                {
                    binding.resultText.setText(mapped.toString(5));
                } catch (JSONException ignored)
                {
                }
            });
        }

        @Override
        public void onInfo(MeasurementResult result)
        {
            getActivity().runOnUiThread(() -> speedViewModel.setStatus(result.getMsg()));
        }

        @Override
        public void onStarted(MeasurementResult result)
        {
            getActivity().runOnUiThread(() -> speedViewModel.setStatus(result.getMsg()));
            getActivity().runOnUiThread(() -> binding.buttonStart.setEnabled(false));
            getActivity().runOnUiThread(() -> binding.buttonStop.setEnabled(true));
        }

        @Override
        public void onFinished(MeasurementResult result)
        {
            getActivity().runOnUiThread(() -> speedViewModel.setStatus(result.getMsg()));
        }

        @Override
        public void onError(MeasurementResult result)
        {
            getActivity().runOnUiThread(() -> speedViewModel.setStatus(result.getMsg()));
            getActivity().runOnUiThread(() -> binding.buttonStart.setEnabled(true));
            getActivity().runOnUiThread(() -> binding.buttonStop.setEnabled(false));
        }

        @Override
        public void onCompleted(MeasurementResult result)
        {
            getActivity().runOnUiThread(() -> speedViewModel.setStatus(result.getMsg()));
            JSONObject mapped = Tool.mapJSON(result.toJson(), mappingInfo);
            getActivity().runOnUiThread(() ->
            {
                try
                {
                    binding.resultText.setText(mapped.toString(5));
                } catch (JSONException ignored)
                {
                }
            });
            getActivity().runOnUiThread(() -> binding.buttonStart.setEnabled(true));
            getActivity().runOnUiThread(() -> binding.buttonStop.setEnabled(false));
        }

        @Override
        public void onConsoleMessage(String message)
        {
        }
    };
}