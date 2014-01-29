/*
 *  Copyright (C) 2014 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */package org.omnirom.omniclick;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class OClickBootReceiver extends BroadcastReceiver {
    private static final String TAG = "OClickBootReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            if (prefs.getBoolean(
                    OClickControlActivity.OCLICK_START_ON_BOOT_KEY, true)
                    && prefs.getString(
                            OClickControlActivity.OCLICK_CONNECT_DEVICE, null) != null) {
                final BluetoothManager bluetoothManager = (BluetoothManager) context
                        .getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager
                        .getAdapter();
                if (bluetoothAdapter == null) {
                    Log.e(TAG, "No bluetooth support");
                    return;
                }
                if (!bluetoothAdapter.isEnabled()) {
                    Log.e(TAG, "Bluetooth support not enabled");
                    return;
                }
                try {
                    Log.d(TAG, "android.intent.action.BOOT_COMPLETED");
                    Intent startIntent = new Intent(context,
                            OClickBLEService.class);
                    context.startService(startIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Can't start load oclick service", e);
                }
            }
        }
    }
}
