/*
* Copyright (C) 2014 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.ContentResolver;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class CameraFlipSwitch implements OnPreferenceChangeListener {
    private Context mContext;

    public CameraFlipSwitch(Context context) {
        mContext = context;
    }

    public static boolean isSupported() {
        return true;
    }

    public static boolean isEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.OPPO_CAMERA_FLIP_ENABLED, 1) != 0;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.OPPO_CAMERA_FLIP_ENABLED, enabled ? 1 : 0);
        return true;
    }
}
