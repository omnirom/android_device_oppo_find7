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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.TwoStatePreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

public class DeviceSettings extends PreferenceActivity  {

    public static final String KEY_S2WSWITCH = "s2w_switch";
    public static final String KEY_S2WSTROKE = "s2w_stroke";
    public static final String KEY_S2WLENGTH = "s2w_length";
    public static final String KEY_DOUBLETAB2WAKE_SWITCH = "s2w_double_tap_wake";
    public static final String KEY_DOUBLETAP2WAKE_DURATION = "s2w_double_tap_duration";
    public static final String KEY_DOUBLETAP2WAKE_BARRIER = "s2w_double_tap_barrier";
    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_SUSPEND_CAP_FREQ = "suspend_cap_freq";
    public static final String KEY_SUSPEND_CAP_CORE = "suspend_cap_core";
    public static final String KEY_BACKTOUCH = "backtouch";

    //private TwoStatePreference mS2WSwitch;
    //private ListPreference mS2WStroke;
    //private ListPreference mS2WLength;
    private TwoStatePreference mDoubleTap2WakeSwitch;
    private ListPreference mDoubleTap2WakeDuration;
    //private ListPreference mDoubleTap2WakeBarrier;
    private VibratorStrengthPreference mVibratorStrength;
    private SuspendFreqCap mSuspendFreqCap;
    private SuspendCoreCap mSuspendCoreCap;
    private TwoStatePreference mBackTouchSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);

        /*mS2WSwitch = (TwoStatePreference) findPreference(KEY_S2WSWITCH);
        mS2WSwitch.setEnabled(Sweep2WakeSwitch.isSupported());
        mS2WSwitch.setChecked(Sweep2WakeSwitch.isEnabled(this));
        mS2WSwitch.setOnPreferenceChangeListener(new Sweep2WakeSwitch());

        mS2WStroke = (ListPreference) findPreference(KEY_S2WSTROKE);
        mS2WStroke.setEnabled(Sweep2WakeStroke.isSupported());
        mS2WStroke.setValue(Sweep2WakeStroke.getValue(this));
        mS2WStroke.setOnPreferenceChangeListener(new Sweep2WakeStroke());

        mS2WLength = (ListPreference) findPreference(KEY_S2WLENGTH);
        mS2WLength.setEnabled(Sweep2WakeMinLength.isSupported());
        mS2WLength.setValue(squashLengthValue(Sweep2WakeMinLength.getValue(this)));
        mS2WLength.setOnPreferenceChangeListener(new Sweep2WakeMinLength());*/

        mDoubleTap2WakeSwitch = (TwoStatePreference) findPreference(KEY_DOUBLETAB2WAKE_SWITCH);
        mDoubleTap2WakeSwitch.setEnabled(DoubleTap2WakeSwitch.isSupported());
        mDoubleTap2WakeSwitch.setChecked(DoubleTap2WakeSwitch.isEnabled(this));
        mDoubleTap2WakeSwitch.setOnPreferenceChangeListener(new DoubleTap2WakeSwitch());

        mDoubleTap2WakeDuration = (ListPreference) findPreference(KEY_DOUBLETAP2WAKE_DURATION);
        mDoubleTap2WakeDuration.setEnabled(DoubleTap2WakeDuration.isSupported());
        mDoubleTap2WakeDuration.setValue(squashDurationValue(DoubleTap2WakeDuration.getValue(this)));
        mDoubleTap2WakeDuration.setOnPreferenceChangeListener(new DoubleTap2WakeDuration());

        /*mDoubleTap2WakeBarrier = (ListPreference) findPreference(KEY_DOUBLETAP2WAKE_BARRIER);
        mDoubleTap2WakeBarrier.setEnabled(DoubleTap2WakeBarrier.isSupported());
        mDoubleTap2WakeBarrier.setValue(DoubleTap2WakeBarrier.getValue(this));
        mDoubleTap2WakeBarrier.setOnPreferenceChangeListener(new DoubleTap2WakeBarrier());*/

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());

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

    /*private String squashLengthValue(String value) {
        // map it to 325, 500, 850 if not exact value
        int intValue=new Integer(value).intValue();
        if(intValue==325 || intValue==500 || intValue==850){
            return value;
        }
        // we found a different value in sysfs
        // map it to our 3 possible length
        if(intValue<325)
            return "325";
        if(intValue>850)
            return "850";
        if(intValue>325 && intValue <500){
            int diff1=intValue-325;
            int diff2=500-intValue;
            return (diff1<diff2)?"325":"500";
        }
        if(intValue>500 && intValue <850){
            int diff1=intValue-500;
            int diff2=850-intValue;
            return (diff1<diff2)?"500":"850";
        }
        return value;
    }*/

    private String squashDurationValue(String value) {
        // map it to 150, 200, 250, 300, 350 if not exact value
        int intValue=new Integer(value).intValue();
        if(intValue==150 || intValue==200 || intValue==250 || intValue==300 || intValue==350){
            return value;
        }
        // we found a different value in sysfs
        // map it to our 5 possible durations
        if(intValue<150)
            return "150";
        if(intValue>350)
            return "350";
        if(intValue>300 && intValue <350){
            int diff1=intValue-300;
            int diff2=350-intValue;
            return (diff1<diff2)?"300":"350";
        }
        if(intValue>250 && intValue <300){
            int diff1=intValue-250;
            int diff2=300-intValue;
            return (diff1<diff2)?"250":"300";
        }
        if(intValue>200 && intValue <250){
            int diff1=intValue-200;
            int diff2=250-intValue;
            return (diff1<diff2)?"200":"250";
        }
        if(intValue>150 && intValue <200){
            int diff1=intValue-150;
            int diff2=200-intValue;
            return (diff1<diff2)?"150":"200";
        }
        return value;
    }
}
