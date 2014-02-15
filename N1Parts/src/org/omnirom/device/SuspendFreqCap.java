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
package org.omnirom.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuspendFreqCap extends ListPreference implements OnPreferenceChangeListener {

    private static final String FILE = "/sys/kernel/cpufreq_cap/screen_off_max_freq";
    private static final String FILE_ENABLE = "/sys/kernel/cpufreq_cap/screen_off_cap";
    private static final String STEPS_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";

    public SuspendFreqCap(Context context, AttributeSet attrs) {
        super(context, attrs);
        initList();
    }

    public SuspendFreqCap(Context context) {
        super(context);
        initList();
    }

    private void initList() {
        String availableFrequenciesLine = Utils.getFileValue(STEPS_PATH, "");
        if (availableFrequenciesLine != null) {
            String[] frequencies = availableFrequenciesLine.split(" ");

            List<String> entriesList = new ArrayList<String>();
            entriesList.add(getContext().getResources().getString(R.string.disabled));
            for (int i = 0; i < frequencies.length; i++){
                entriesList.add(new Integer(frequencies[i]).intValue()/1000 + " " + getContext().getResources().getString(R.string.mhz));
            }

            CharSequence[] entries = entriesList.toArray(new CharSequence[entriesList.size()]);

            List<String> entriyValuesList = new ArrayList<String>();
            entriyValuesList.add("0");
            entriyValuesList.addAll(Arrays.asList(frequencies));

            CharSequence[] entryValues = entriyValuesList.toArray(new CharSequence[entriyValuesList.size()]);

            setEntries(entries);
            setEntryValues(entryValues);
            setSummary(getCurrentSummary(getValue(getContext())));
        }
    }

    public static boolean isSupported() {
        return Utils.fileWritable(FILE) && Utils.fileWritable(FILE_ENABLE);
    }

    public static String getValue(Context context) {
        String value = null;
        if (isDisabled()){
            value = "0";
        } else {
            value = Utils.getFileValue(FILE, "0");
        }
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(DeviceSettings.KEY_SUSPEND_CAP_FREQ, value);
    }

    /**
     * Restore Sweep2Wake stroke setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        String value = getValue(context);
        writeValue(value);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = (String)newValue;
        writeValue(value);
        setSummary(getCurrentSummary(value));
        return true;
    }

    private static void writeValue(String value) {
        if (value.equals("0")){
            disableCap();
        } else {
            enableCap();
            Utils.writeValue(FILE, value);
        }
    }

    private static void disableCap(){
        Utils.writeValue(FILE_ENABLE, "0");
    }

    private static void enableCap(){
        Utils.writeValue(FILE_ENABLE, "1");
    }

    private static boolean isDisabled(){
        String value = Utils.getFileValue(FILE_ENABLE, "0");
        return value.equals("0");
    }

    private CharSequence getCurrentSummary(String value){
        List<CharSequence> entries = Arrays.asList(getEntryValues());
        int idx = entries.indexOf(value);

        if (idx != -1){
            return getEntries()[idx];
        }
        return "";
    }
}
