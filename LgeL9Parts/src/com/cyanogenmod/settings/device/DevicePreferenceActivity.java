/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.settings.device;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import com.cyanogenmod.settings.device.R;

public class DevicePreferenceActivity extends PreferenceFragment {

    public static final String SHARED_PREFERENCES_BASENAME = "com.cyanogenmod.settings.device";
    public static final String ACTION_UPDATE_PREFERENCES = "com.cyanogenmod.settings.device.UPDATE";
    public static final String KEY_GPU_OVERCLOCK = "gpu_overclock";
    public static final String KEY_IVA_OVERCLOCK = "iva_overclock";
    public static final String KEY_MAX_SLEEP_FREQUENCY = "max_sleep_frequency";

    private ListPreference mGpuOverclock;
    private ListPreference mIvaOverclock;
    private ListPreference mMaxSleepFrequency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        mGpuOverclock = (ListPreference) findPreference(KEY_GPU_OVERCLOCK);
        mGpuOverclock.setEnabled(GpuOverclock.isSupported());
        mGpuOverclock.setOnPreferenceChangeListener(new GpuOverclock());
        GpuOverclock.updateSummary(mGpuOverclock, Integer.parseInt(mGpuOverclock.getValue()));

	mIvaOverclock = (ListPreference) findPreference(KEY_IVA_OVERCLOCK);
        mIvaOverclock.setEnabled(IvaOverclock.isSupported());
        mIvaOverclock.setOnPreferenceChangeListener(new IvaOverclock());
        IvaOverclock.updateSummary(mIvaOverclock, Integer.parseInt(mIvaOverclock.getValue()));

	mMaxSleepFrequency = (ListPreference) findPreference(KEY_MAX_SLEEP_FREQUENCY);
        mMaxSleepFrequency.setEnabled(MaxSleepFrequency.isSupported());
        mMaxSleepFrequency.setOnPreferenceChangeListener(new MaxSleepFrequency());
        MaxSleepFrequency.updateSummary(mMaxSleepFrequency, Integer.parseInt(mMaxSleepFrequency.getValue()));
    }
}
