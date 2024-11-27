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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.lineageos.settings.utils.FileUtils;

public class FEASUtils {
    public static final String PERFMGR_ENABLE = "perfmgr_enable";
    public static final String PERFMGR_MIN_FREQ = "min_freq_limit_level";
    public static final String PERFMGR_MAX_FREQ = "max_freq_limit_level";

    public static final String PERFMGR_ENABLE_PATH = "/sys/module/perfmgr/parameters/perfmgr_enable";
    public static final String MIN_FREQ_PATH = "/sys/module/perfmgr/parameters/min_freq_limit_level";
    public static final String MAX_FREQ_PATH = "/sys/module/perfmgr/parameters/max_freq_limit_level";

    private SharedPreferences mSharedPrefs;
    private boolean mInitialized;

    public FEASUtils(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void initialize() {
        if (!mInitialized) {
            getValue(MIN_FREQ_PATH, PERFMGR_ENABLE);
            getValue(MAX_FREQ_PATH, PERFMGR_MAX_FREQ);
            getValue(PERFMGR_ENABLE_PATH, PERFMGR_ENABLE);
            mInitialized = true;
        }
    }

    public int getValue(String PATH, String KEY) {
        String value = mSharedPrefs.getString(KEY, null);
        String currentValue = FileUtils.readOneLine(PATH);
        int ret = 0;
        if (value == null || value.isEmpty()) {
            mSharedPrefs.edit().putString(KEY, currentValue).apply();
            return Integer.parseInt(currentValue.trim());
        }
        ret = Integer.parseInt(value.trim());
        if (!value.equals(currentValue)) {
            putValue(PATH, KEY, value);
        }
        return ret;   
    }

    public boolean putValue(String PATH, String KEY, String value) {
        mSharedPrefs.edit().putString(KEY, value).apply();
        return FileUtils.writeLine(PATH, value);
    }
}
