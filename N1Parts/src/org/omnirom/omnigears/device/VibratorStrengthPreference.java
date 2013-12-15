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

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.database.ContentObserver;
import android.preference.SeekBarDialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.os.Bundle;
import android.util.Log;
import android.os.Vibrator;

public class VibratorStrengthPreference extends SeekBarDialogPreference implements
        SeekBar.OnSeekBarChangeListener {

    private SeekBar mSeekBar;
    private int mOldStrength;
    private Vibrator mVibrator;
    private Button mTestButton;

    private static final int SEEK_BAR_RANGE = 100;
    private static final String FILE = "/sys/devices/platform/msm_ssbi.0/pm8921-core/pm8xxx-vib/amp";
    private static final long testVibrationPattern[] = {0,250};

    public VibratorStrengthPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        setDialogLayoutResource(R.layout.preference_dialog_vibrator_strength);
        setDialogIcon(R.drawable.ic_vibrations);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mOldStrength = Integer.parseInt(getValue(getContext()));
        mSeekBar = getSeekBar(view);
        mSeekBar.setMax(SEEK_BAR_RANGE);
        mSeekBar.setProgress(mOldStrength);

        mTestButton = (Button) view.findViewById(R.id.vib_test);
        if (!mVibrator.hasVibrator()){
            mTestButton.setEnabled(false);
        } else {
            mTestButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mVibrator.vibrate(testVibrationPattern, -1);
                }
            });
        }

        mSeekBar.setOnSeekBarChangeListener(this);
    }

    public static boolean isSupported() {
        return Utils.fileWritable(FILE);
    }

	public static String getValue(Context context) {
		return Utils.getFileValue(FILE, "94");
	}

	private void setValue(String newValue) {
	    Utils.writeValue(FILE, newValue);
	}

    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        String storedValue = PreferenceManager.getDefaultSharedPreferences(context).getString(DeviceSettings.KEY_VIBSTRENGTH, "94"); 
        Utils.writeValue(FILE, storedValue);
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
        setValue(String.valueOf(progress));
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // NA
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            setValue(String.valueOf(mSeekBar.getProgress()));
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putString(DeviceSettings.KEY_VIBSTRENGTH, String.valueOf(mSeekBar.getProgress()));
            editor.commit();
        } else {
            restoreOldState();
        }
        mVibrator.cancel();
    }

    private void restoreOldState() {
        setValue(String.valueOf(mOldStrength));
    }
}

