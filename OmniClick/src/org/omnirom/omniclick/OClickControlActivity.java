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
 */
package org.omnirom.omniclick;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class OClickControlActivity extends Activity {
    private final static String TAG = OClickControlActivity.class
            .getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_ALERT_TYPE = "ALERT_TYPE";

    public static final String OCLICK_CONNECT_DEVICE = "connect_device";
    public static final String OCLICK_CONNECT_NAME = "connect_name";

    public static final String OCLICK_START_ON_BOOT_KEY = "start_on_boot";
    public static final String OCLICK_PROXIMITY_ALERT_KEY = "proximity_alert";
    public static final String OCLICK_FIND_PHONE_ALERT_KEY = "find_phone_alert";
    public static final String OCLICK_FIND_PHONE_ALERT_TONE_KEY = "find_phone_alert_tone";
    public static final String OCLICK_SNAP_PICTURE_KEY = "snap_picture";
    public static final String OCLICK_MUSIC_CONTROL_KEY = "music_control";

    public static final String CONNECTING_ACTION = "org.omnirom.omniclick.connecting_oclick";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_SELECT_RINGTONE = 2;

    private TextView mConnectionState;
    private TextView mFindPhoneAlertTone;
    private TextView mFindPhoneAlertToneTitle;
    private TextView mDeviceAddressField;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private boolean mConnecting;
    private BluetoothAdapter mBluetoothAdapter;
    private OCLickReceiver mReceiver;
    private SharedPreferences mPrefs;
    private boolean mRingtoneSelect;
    private Handler mHandler;
    private BroadcastReceiver mGattUpdateReceiver;
    private CheckBox mFindPhoneAlert;
    private TextView mFindPhoneAlertTitle;
    private CheckBox mMusicControl;
    private TextView mMusicControlTitle;
    private TextView mMusicControlSummary;
    private View mProgress;
    private ProgressBar mProgressBar;
    private Button mCallOClick;

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    private class GattBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (OClickBLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mDeviceAddress = mPrefs.getString(
                        OClickControlActivity.OCLICK_CONNECT_DEVICE, null);
                mDeviceName = mPrefs.getString(
                        OClickControlActivity.OCLICK_CONNECT_NAME, null);
                setConnecting(false);
                initConnectionState();
            } else if (OClickBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                setConnecting(false);
                initConnectionState();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oclick_main);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDeviceAddressField = (TextView) findViewById(R.id.device_address);

        mDeviceAddress = mPrefs.getString(
                OClickControlActivity.OCLICK_CONNECT_DEVICE, null);
        mDeviceName = mPrefs.getString(
                OClickControlActivity.OCLICK_CONNECT_NAME, null);

        CheckBox startOnBoot = (CheckBox) findViewById(R.id.start_on_boot);
        startOnBoot.setChecked(mPrefs.getBoolean(
                OClickControlActivity.OCLICK_START_ON_BOOT_KEY, true));
        startOnBoot.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mPrefs.edit()
                        .putBoolean(
                                OClickControlActivity.OCLICK_START_ON_BOOT_KEY,
                                isChecked).commit();
            }
        });

        CheckBox proximityAlert = (CheckBox) findViewById(R.id.proximity_alert);
        proximityAlert.setChecked(mPrefs.getBoolean(
                OClickControlActivity.OCLICK_PROXIMITY_ALERT_KEY, true));
        proximityAlert
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        mPrefs.edit()
                                .putBoolean(
                                        OClickControlActivity.OCLICK_PROXIMITY_ALERT_KEY,
                                        isChecked).commit();
                    }
                });

        mFindPhoneAlertTitle = (TextView) findViewById(R.id.find_phone_alert_title);

        mFindPhoneAlert = (CheckBox) findViewById(R.id.find_phone_alert);
        mFindPhoneAlert.setChecked(mPrefs.getBoolean(
                OClickControlActivity.OCLICK_FIND_PHONE_ALERT_KEY, true));
        mFindPhoneAlert
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        mPrefs.edit()
                                .putBoolean(
                                        OClickControlActivity.OCLICK_FIND_PHONE_ALERT_KEY,
                                        isChecked).commit();
                        mFindPhoneAlertTone.setEnabled(isChecked);
                        mFindPhoneAlertToneTitle.setEnabled(isChecked);
                        mMusicControl.setEnabled(!isChecked);
                        mMusicControlTitle.setEnabled(!isChecked);
                        mMusicControlSummary.setEnabled(!isChecked);
                    }
                });

        mFindPhoneAlertToneTitle = (TextView) findViewById(R.id.find_phone_alert_ringtone_title);
        mFindPhoneAlertTone = (TextView) findViewById(R.id.find_phone_alert_ringtone);
        mFindPhoneAlertTone.setEnabled(mFindPhoneAlert.isChecked());
        mFindPhoneAlertToneTitle.setEnabled(mFindPhoneAlert.isChecked());

        mFindPhoneAlertTone.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    selectAlertRingtone();
                }
                return true;
            }
        });

        CheckBox snapPicture = (CheckBox) findViewById(R.id.snap_picture);
        snapPicture.setChecked(mPrefs.getBoolean(
                OClickControlActivity.OCLICK_SNAP_PICTURE_KEY, true));
        snapPicture.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mPrefs.edit()
                        .putBoolean(
                                OClickControlActivity.OCLICK_SNAP_PICTURE_KEY,
                                isChecked).commit();
            }
        });

        mMusicControlTitle = (TextView) findViewById(R.id.music_control_title);
        mMusicControlSummary = (TextView) findViewById(R.id.music_control_summary);
        mMusicControl = (CheckBox) findViewById(R.id.music_control);
        mMusicControl.setChecked(mPrefs.getBoolean(
                OClickControlActivity.OCLICK_MUSIC_CONTROL_KEY, false));
        mMusicControl.setEnabled(!mFindPhoneAlert.isChecked());
        mMusicControlTitle.setEnabled(!mFindPhoneAlert.isChecked());
        mMusicControlSummary.setEnabled(!mFindPhoneAlert.isChecked());
        mFindPhoneAlert.setEnabled(!mMusicControl.isChecked());
        mFindPhoneAlertTitle.setEnabled(!mMusicControl.isChecked());
        mMusicControl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mPrefs.edit()
                        .putBoolean(
                                OClickControlActivity.OCLICK_MUSIC_CONTROL_KEY,
                                isChecked).commit();
                mFindPhoneAlert.setEnabled(!isChecked);
                mFindPhoneAlertTitle.setEnabled(!isChecked);
            }
        });

        mCallOClick = (Button) findViewById(R.id.start_alert);
        mCallOClick.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!mConnected) {
                        return true;
                    }
                    Intent alertIntent = new Intent(
                            OClickBLEService.ACTION_START_ALERT);
                    alertIntent.putExtra(EXTRAS_ALERT_TYPE, 1);
                    OClickControlActivity.this.sendBroadcast(alertIntent);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent alertIntent = new Intent(
                                    OClickBLEService.ACTION_STOP_ALERT);
                            OClickControlActivity.this
                                    .sendBroadcast(alertIntent);
                        }
                    }, 5000); // 5s
                }
                return true;
            }
        });

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mProgress = inflater.inflate(R.layout.actionbar_indeterminate_progress,
                null);
        mProgressBar = (ProgressBar) mProgress
                .findViewById(R.id.refresh_progress_bar);
        mProgressBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setConnecting(false);
                    Intent startIntent = new Intent(OClickControlActivity.this,
                            OClickBLEService.class);
                    OClickControlActivity.this.stopService(startIntent);
                }
                return true;
            }
        });

        mGattUpdateReceiver = new GattBroadcastReceiver();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        mReceiver = new OCLickReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTING_ACTION);
        registerReceiver(mReceiver, filter);

        initConnectionState();
        mHandler.post(mRingtoneLookupRunnable);

        if (mBluetoothAdapter.isEnabled()) {
            setupConnection();
        }
    }

    private void initConnectionState() {
        if (mDeviceName != null) {
            getActionBar().setTitle(mDeviceName);
        } else {
            getActionBar().setTitle(
                    getResources().getString(R.string.unknown_device));
        }
        if (mDeviceAddress != null) {
            mDeviceAddressField.setText(mDeviceAddress);
        } else {
            mDeviceAddressField.setText(getResources().getString(
                    R.string.unknown_device));
        }

        if (OClickBLEService.mIsRunning) {
            mConnected = OClickBLEService.mConnected;
        } else {
            mConnected = false;
        }
        updateConnectionState(mConnected ? R.string.connected
                : R.string.disconnected);
        mCallOClick.setEnabled(mConnected);
        invalidateOptionsMenu();
    }

    private void rescanDevice() {
        if (OClickBLEService.mIsRunning) {
            Intent startIntent = new Intent(this, OClickBLEService.class);
            this.stopService(startIntent);
        }
        final Intent intent = new Intent(this, OClickScanActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        setupConnection();
    }

    private void setupConnection() {
        if (mDeviceAddress != null) {
            if (!OClickBLEService.mIsRunning) {
                setConnecting(true);
                Intent startIntent = new Intent(this, OClickBLEService.class);
                this.startService(startIntent);
            } else if (!OClickBLEService.mConnected) {
                setConnecting(true);
                Intent connectIntent = new Intent(
                        OClickBLEService.ACTION_CONNECT);
                connectIntent.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                this.sendBroadcast(connectIntent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        if (mGattUpdateReceiver != null) {
            unregisterReceiver(mGattUpdateReceiver);
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.oclick_menu, menu);
        if (mConnecting) {
            menu.findItem(R.id.menu_refresh).setActionView(mProgress);
        } else {
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_scan:
            if (mBluetoothAdapter.isEnabled()) {
                setConnecting(false);
                rescanDevice();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OClickBLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(OClickBLEService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    private void selectAlertRingtone() {
        // Launch the ringtone picker
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
        Uri currentRingtone = getCurrentRingtone();
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                currentRingtone);
        startActivityForResult(intent, REQUEST_SELECT_RINGTONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User choose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }
        }
        if (requestCode == REQUEST_SELECT_RINGTONE) {
            if (resultCode != Activity.RESULT_CANCELED && data != null) {
                Uri uri = data
                        .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                mPrefs.edit()
                        .putString(OCLICK_FIND_PHONE_ALERT_TONE_KEY,
                                uri.toString()).commit();
                mHandler.post(mRingtoneLookupRunnable);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Runnable mRingtoneLookupRunnable = new Runnable() {
        public void run() {
            updateRingtoneName();
        }
    };

    private void updateRingtoneName() {
        CharSequence summary = null;
        try {
            Uri currentRingtone = getCurrentRingtone();
            Cursor cursor = getContentResolver().query(currentRingtone,
                    new String[] { MediaStore.Audio.Media.TITLE }, null, null,
                    null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    summary = cursor.getString(0);
                    mFindPhoneAlertTone.setText(summary);
                }
                cursor.close();
            }
        } catch (SQLiteException sqle) {
            Log.d(TAG, "", sqle);
        }
    }

    class OCLickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CONNECTING_ACTION)) {
                setConnecting(true);
            }
        }
    };

    private void setConnecting(boolean value) {
        mConnecting = value;
        invalidateOptionsMenu();
    }

    private Uri getCurrentRingtone() {
        String currentRingtoneString = mPrefs.getString(
                OClickControlActivity.OCLICK_FIND_PHONE_ALERT_TONE_KEY, null);
        Uri currentRingtone = null;
        if (currentRingtoneString != null) {
            currentRingtone = Uri.parse(currentRingtoneString);
        } else {
            currentRingtone = RingtoneManager.getActualDefaultRingtoneUri(this,
                    RingtoneManager.TYPE_RINGTONE);
        }
        return currentRingtone;
    }
}
