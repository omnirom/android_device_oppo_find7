/*
* Copyright (C) 2013 The OmniROM Project
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
package org.omnirom.omnigears.device;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;

public class DeviceSettings extends PreferenceActivity  {

    public static final String KEY_DOUBLETAB_SWITCH = "double_tap";
    public static final String KEY_CAMERA_SWITCH = "camera";
    public static final String KEY_MUSIC_SWITCH = "music";
    public static final String KEY_TORCH_SWITCH = "torch";

    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_SUSPEND_CAP_FREQ = "suspend_cap_freq";
    public static final String KEY_SUSPEND_CAP_CORE = "suspend_cap_core";
    public static final String KEY_BACKTOUCH = "backtouch";

    private TwoStatePreference mDoubleTapSwitch;
    private TwoStatePreference mCameraSwitch;
    private TwoStatePreference mMusicSwitch;
    private TwoStatePreference mTorchSwitch;

    private SuspendFreqCap mSuspendFreqCap;
    private SuspendCoreCap mSuspendCoreCap;
    private TwoStatePreference mBackTouchSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);

        mDoubleTapSwitch = (TwoStatePreference) findPreference(KEY_DOUBLETAB_SWITCH);
        mDoubleTapSwitch.setEnabled(DoubleTapSwitch.isSupported());
        mDoubleTapSwitch.setChecked(DoubleTapSwitch.isEnabled(this));
        mDoubleTapSwitch.setOnPreferenceChangeListener(new DoubleTapSwitch());

        mCameraSwitch = (TwoStatePreference) findPreference(KEY_CAMERA_SWITCH);
        mCameraSwitch.setEnabled(CameraGestureSwitch.isSupported());
        mCameraSwitch.setChecked(CameraGestureSwitch.isEnabled(this));
        mCameraSwitch.setOnPreferenceChangeListener(new CameraGestureSwitch());

        mMusicSwitch = (TwoStatePreference) findPreference(KEY_MUSIC_SWITCH);
        mMusicSwitch.setEnabled(MusicGestureSwitch.isSupported());
        mMusicSwitch.setChecked(MusicGestureSwitch.isEnabled(this));
        mMusicSwitch.setOnPreferenceChangeListener(new MusicGestureSwitch());

        mTorchSwitch = (TwoStatePreference) findPreference(KEY_TORCH_SWITCH);
        mTorchSwitch.setEnabled(TorchGestureSwitch.isSupported());
        mTorchSwitch.setChecked(TorchGestureSwitch.isEnabled(this));
        mTorchSwitch.setOnPreferenceChangeListener(new TorchGestureSwitch());

        mSuspendFreqCap = (SuspendFreqCap) findPreference(KEY_SUSPEND_CAP_FREQ);
        mSuspendFreqCap.setEnabled(SuspendFreqCap.isSupported());
        mSuspendFreqCap.setValue(SuspendFreqCap.getValue(this));
        mSuspendFreqCap.setOnPreferenceChangeListener(mSuspendFreqCap);

        mSuspendCoreCap = (SuspendCoreCap) findPreference(KEY_SUSPEND_CAP_CORE);
        mSuspendCoreCap.setEnabled(SuspendCoreCap.isSupported());
        mSuspendCoreCap.setValue(SuspendCoreCap.getValue(this));
        mSuspendCoreCap.setOnPreferenceChangeListener(mSuspendCoreCap);

        mBackTouchSwitch= (TwoStatePreference) findPreference(KEY_BACKTOUCH);
        mBackTouchSwitch.setEnabled(BackTouchSwitch.isSupported());
        mBackTouchSwitch.setChecked(BackTouchSwitch.isEnabled(this));
        mBackTouchSwitch.setOnPreferenceChangeListener(new BackTouchSwitch());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
