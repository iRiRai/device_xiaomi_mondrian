/*
 * Copyright (C) 2024 The LineageOS Project
 *               2024 Flakeforever
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

package org.lineageos.settings.feas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.InputFilter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import android.util.Log;

import com.android.settingslib.widget.MainSwitchPreference;

import org.lineageos.settings.R;

public class FEASFragment extends PreferenceFragment
        implements OnPreferenceChangeListener, OnCheckedChangeListener {
    
    private static final String TAG = "FEAS";
    private MainSwitchPreference mSwitchBar;
    private ListPreference mMinFreqLimitLevel;
    private ListPreference mMaxFreqLimitLevel;
    private FEASUtils mFEASUtils;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.feas_settings);

        mFEASUtils = new FEASUtils(getActivity());

        mSwitchBar = (MainSwitchPreference) findPreference("feas_enable");
        mSwitchBar.setOnPreferenceChangeListener(this);
        mSwitchBar.setChecked(mFEASUtils.getValue(
            mFEASUtils.PERFMGR_ENABLE_PATH, mFEASUtils.PERFMGR_ENABLE) == 1);

        int minFreqLimitLevel = mFEASUtils.getValue(
            mFEASUtils.MIN_FREQ_PATH, mFEASUtils.PERFMGR_MIN_FREQ);
        int maxFreqLimitLevel = mFEASUtils.getValue(
            mFEASUtils.MAX_FREQ_PATH, mFEASUtils.PERFMGR_MAX_FREQ);

        mMinFreqLimitLevel = (ListPreference) findPreference("min_freq_limit_level");
        mMinFreqLimitLevel.setValue(String.valueOf(minFreqLimitLevel));

        mMaxFreqLimitLevel = (ListPreference) findPreference("max_freq_limit_level");   
        mMaxFreqLimitLevel.setValue(String.valueOf(maxFreqLimitLevel));     

        for (Preference preference : new Preference[]{
                mMinFreqLimitLevel, mMaxFreqLimitLevel
        }) {
            preference.setOnPreferenceChangeListener(this);
        }

        updateDependencies();
    }

    private void updateDependencies() {
        boolean enabled = mSwitchBar.isChecked();
        mMinFreqLimitLevel.setEnabled(enabled);
        mMaxFreqLimitLevel.setEnabled(enabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSwitchBar) {
            boolean enabled = (Boolean) newValue;
            updateDependencies();
            return mFEASUtils.putValue(
                mFEASUtils.PERFMGR_ENABLE_PATH, mFEASUtils.PERFMGR_ENABLE, enabled? "1" : "0");
        }
        if (preference == mMinFreqLimitLevel) {
            String value = newValue.toString();
            return mFEASUtils.putValue(
                mFEASUtils.MIN_FREQ_PATH, mFEASUtils.PERFMGR_MIN_FREQ, value);
        }
        if (preference == mMaxFreqLimitLevel) {
            String value = newValue.toString();
            return mFEASUtils.putValue(
                mFEASUtils.MAX_FREQ_PATH, mFEASUtils.PERFMGR_MAX_FREQ, value);
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateDependencies();
    }
}
