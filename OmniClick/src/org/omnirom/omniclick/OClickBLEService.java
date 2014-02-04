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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManagerNative;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.media.IAudioService;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

/**
 */
public class OClickBLEService extends Service implements
        OnSharedPreferenceChangeListener {
    private final static String TAG = OClickBLEService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private Handler mRssiPoll;
    boolean mAlerting;
    private Ringtone mRingtone;
    private SharedPreferences mPrefs;
    private int mRingerModeSaved;
    private int mRingerVolumeSaved;

    public static boolean mIsRunning;
    public static boolean mConnected;
    private OClickReceiver mReceiver = new OClickReceiver();

    public final static String ACTION_GATT_CONNECTED = "org.omnirom.omniclick.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "org.omnirom.omniclick.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_CANCEL_ALERT_PHONE = "org.omnirom.omniclick.ACTION_CANCEL_ALERT_PHONE";
    public static final String ACTION_CONNECT = "org.omnirom.omniclick.ACTION_CONNECT";
    public static final String ACTION_DISCONNECT = "org.omnirom.omniclick.ACTION_DISCONNECT";
    public static final String ACTION_START_ALERT = "org.omnirom.omniclick.ACTION_START_ALERT";
    public static final String ACTION_STOP_ALERT = "org.omnirom.omniclick.ACTION_STOP_ALERT";

    private final String LIST_UUID = "UUID";

    public class OClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CANCEL_ALERT_PHONE)) {
                if (mRingtone.isPlaying()) {
                    Log.d(TAG, "Stopping ring alarm");
                    restoreRingtoneProperties();
                    mRingtone.stop();
                }
            }
            if (intent.getAction().equals(ACTION_CONNECT)) {
                if (!mConnected) {
                    String deviceName = intent
                            .getStringExtra(OClickControlActivity.EXTRAS_DEVICE_ADDRESS);
                    connect(deviceName);
                }
            }
            if (intent.getAction().equals(ACTION_DISCONNECT)) {
                if (mConnected) {
                    disconnect();
                }
            }
            if (intent.getAction().equals(ACTION_START_ALERT)) {
                if (mConnected) {
                    int alertType = intent.getIntExtra(
                            OClickControlActivity.EXTRAS_ALERT_TYPE, 2);
                    startAlert(alertType);
                }
            }
            if (intent.getAction().equals(ACTION_STOP_ALERT)) {
                if (mConnected) {
                    stopAlert();
                }
            }
        }
    };

    // Implements callback methods for GATT events that the app cares about. For
    // example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                mConnected = true;
                broadcastUpdate(intentAction);
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnected = false;
                broadcastUpdate(intentAction);
                mRssiPoll.removeCallbacksAndMessages(null);
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        private void broadcastUpdate(final String action) {
            final Intent intent = new Intent(action);
            sendBroadcast(intent);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // just for debugging
                displayGattServices(getSupportedGattServices());

                registerOClickButton(gatt);

                toggleRssiListener();
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onReliableWriteCompleted : " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "Characteristic changed " + characteristic.getUuid());

            int clickNum = 0;
            if (OClickGattAttributes.OPPO_OTOUCH_CLICK1_UUID
                    .equals(characteristic.getUuid())
                    || OClickGattAttributes.OPPO_OTOUCH_CLICK2_UUID
                            .equals(characteristic.getUuid())) {
                clickNum = readOClickClicks(characteristic);
            }
            if (clickNum == 0) {
                return;
            }

            Log.d(TAG, String.format("Received click: %d", clickNum));
            boolean findPhoneAlert = mPrefs.getBoolean(
                    OClickControlActivity.OCLICK_FIND_PHONE_ALERT_KEY, true);
            boolean musicControl = mPrefs.getBoolean(
                    OClickControlActivity.OCLICK_MUSIC_CONTROL_KEY, false);
            boolean snapPicture = mPrefs.getBoolean(
                    OClickControlActivity.OCLICK_SNAP_PICTURE_KEY, true);

            if (clickNum == 2) {
                if (snapPicture && isCameraActive()) {
                    Log.d(TAG, "camera active");
                    triggerVirtualDownKeypress(KeyEvent.KEYCODE_FOCUS);
                } else if (findPhoneAlert) {
                    handleFindMeAlert();
                } else if (musicControl) {
                    Log.d(TAG, "start/stop music");
                    dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                }
            }
            if (clickNum == 1) {
                if (snapPicture /* && isCameraActive() */) {
                    Log.d(TAG, "snap picture");
                    triggerVirtualKeypress(KeyEvent.KEYCODE_CAMERA);
                }
                if (musicControl && isMusicActive()) {
                    Log.d(TAG, "next track");
                    dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_NEXT);
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            byte[] value = new byte[1];
            BluetoothGattCharacteristic charS = gatt.getService(
                    OClickGattAttributes.LINK_LOSS_UUID).getCharacteristic(
                    OClickGattAttributes.LINK_LOSS_CHAR_UUID);
            if (rssi < -90 && !mAlerting) {
                // start alert
                Log.d(TAG, "Start proximity alert : " + rssi);
                value[0] = 2;
                if (charS != null) {
                    charS.setValue(value);
                    mBluetoothGatt.writeCharacteristic(charS);
                }
                mAlerting = true;
            } else if (rssi > -90 && mAlerting) {
                // stop alert
                Log.d(TAG, "Stop proximity alert : " + rssi);
                value[0] = 0;
                mAlerting = false;
                charS.setValue(value);
                mBluetoothGatt.writeCharacteristic(charS);
            }
        }

        private void registerOClickButton(BluetoothGatt gatt) {
            // Register trigger notification (Used for camera/alarm)
            BluetoothGattService service = gatt
                    .getService(OClickGattAttributes.OPPO_OTOUCH_UUID);
            BluetoothGattCharacteristic trigger = service
                    .getCharacteristic(OClickGattAttributes.OPPO_OTOUCH_CLICK1_UUID);
            if (trigger == null) {
                trigger = service
                        .getCharacteristic(OClickGattAttributes.OPPO_OTOUCH_CLICK2_UUID);
            }
            gatt.setCharacteristicNotification(trigger, true);
        }

        private int readOClickClicks(BluetoothGattCharacteristic characteristic) {
            int clickNum = 0;

            int format = -1;
            int flag = characteristic.getProperties();
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            int value = characteristic.getIntValue(format, 0);
            if (value == 1) {
                clickNum = 1;
            } else if (value == 32) {
                clickNum = 2;
            }
            return clickNum;
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        Log.d(TAG, "" + gattServiceData.toString() + " "
                + gattCharacteristicData.toString());
    }

    public class LocalBinder extends Binder {
        OClickBLEService getService() {
            return OClickBLEService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service being started");

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        mRssiPoll = new Handler();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CANCEL_ALERT_PHONE);
        filter.addAction(ACTION_CONNECT);
        filter.addAction(ACTION_DISCONNECT);
        filter.addAction(ACTION_START_ALERT);
        filter.addAction(ACTION_STOP_ALERT);
        registerReceiver(mReceiver, filter);

        Uri currentRingtone = RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_RINGTONE);
        String currentRingtoneString = mPrefs.getString(
                OClickControlActivity.OCLICK_FIND_PHONE_ALERT_TONE_KEY, null);
        if (currentRingtoneString != null) {
            currentRingtone = Uri.parse(currentRingtoneString);
        }
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(),
                currentRingtone);
        mIsRunning = true;
        initialize();

        String defaultDevice = mPrefs.getString(
                OClickControlActivity.OCLICK_CONNECT_DEVICE, null);
        if (defaultDevice != null) {
            connect(defaultDevice);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service being killed");
        mRssiPoll.removeCallbacksAndMessages(null);
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        unregisterReceiver(mReceiver);
        mIsRunning = false;
        disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     * 
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     * 
     * @param address
     *            The device address of the destination device.
     * 
     * @return Return true if the connection is initiated successfully. The
     *         connection result is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG,
                    "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     * 
     * @param characteristic
     *            The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     * 
     * @param characteristic
     *            Characteristic to act on.
     * @param enabled
     *            If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     * 
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }

    private void toggleRssiListener() {
        boolean fence = mPrefs.getBoolean(
                OClickControlActivity.OCLICK_PROXIMITY_ALERT_KEY, true);

        mRssiPoll.removeCallbacksAndMessages(null);
        if (fence) {
            Log.d(TAG, "Enabling rssi listener");
            mRssiPoll.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothGatt.readRemoteRssi();
                    mRssiPoll.postDelayed(this, 2000);
                }
            }, 100);
        }
    }

    private void startAlert(int alertType) {
        byte[] value = new byte[1];
        BluetoothGattCharacteristic charS = mBluetoothGatt.getService(
                OClickGattAttributes.IMMEDIATE_ALERT_UUID).getCharacteristic(
                OClickGattAttributes.IMMEDIATE_ALERT_CHAR_UUID);

        Log.d(TAG, "Start alert");
        value[0] = Integer.valueOf(alertType).byteValue();
        if (charS != null) {
            charS.setValue(value);
            mBluetoothGatt.writeCharacteristic(charS);
        }
    }

    private void stopAlert() {
        byte[] value = new byte[1];
        BluetoothGattCharacteristic charS = mBluetoothGatt.getService(
                OClickGattAttributes.IMMEDIATE_ALERT_UUID).getCharacteristic(
                OClickGattAttributes.IMMEDIATE_ALERT_CHAR_UUID);

        Log.d(TAG, "Stop alert");
        value[0] = 0;
        if (charS != null) {
            charS.setValue(value);
            mBluetoothGatt.writeCharacteristic(charS);
        }
    }

    private void handleFindMeAlert() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mRingtone.isPlaying()) {
            Log.d(TAG, "Stopping ring alarm");
            restoreRingtoneProperties();
            mRingtone.stop();
            notificationManager.cancel(0);
            return;
        }

        Log.d(TAG, "Executing ring alarm");
        // unmute and set volume to max
        saveRingtoneProperties();
        adjustRingtoneProperties();
        mRingtone.play();
        Notification.Builder builder = new Notification.Builder(
                OClickBLEService.this);
        builder.setSmallIcon(R.drawable.locator_icon);
        builder.setContentTitle(getResources().getString(
                R.string.find_phone_alert_notification_title));
        builder.setContentText(getResources().getString(
                R.string.find_phone_alert_notification_text));
        builder.setAutoCancel(true);
        builder.setOngoing(true);

        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 0, new Intent(ACTION_CANCEL_ALERT_PHONE), 0);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(0, builder.build());
    }

    private void triggerVirtualKeypress(final int keyCode) {
        InputManager im = InputManager.getInstance();
        long now = SystemClock.uptimeMillis();

        final KeyEvent downEvent = new KeyEvent(now, now, KeyEvent.ACTION_DOWN,
                keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD);
        final KeyEvent upEvent = KeyEvent.changeAction(downEvent,
                KeyEvent.ACTION_UP);

        im.injectInputEvent(downEvent,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        im.injectInputEvent(upEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    private void triggerVirtualDownKeypress(final int keyCode) {
        InputManager im = InputManager.getInstance();
        long now = SystemClock.uptimeMillis();

        final KeyEvent downEvent = new KeyEvent(now, now, KeyEvent.ACTION_DOWN,
                keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD);
        final KeyEvent upEvent = KeyEvent.changeAction(downEvent,
                KeyEvent.ACTION_UP);

        im.injectInputEvent(downEvent,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    private IAudioService getAudioService() {
        IAudioService audioService = IAudioService.Stub
                .asInterface(ServiceManager.checkService(Context.AUDIO_SERVICE));
        if (audioService == null) {
            Log.w(TAG, "Unable to find IAudioService interface.");
        }
        return audioService;
    }

    boolean isMusicActive() {
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            return false;
        }
        return am.isLocalOrRemoteMusicActive();
    }

    private void adjustRingtoneProperties() {
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            return;
        }
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_RING,
                am.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
    }

    private void restoreRingtoneProperties() {
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            return;
        }
        am.setRingerMode(mRingerModeSaved);
        am.setStreamVolume(AudioManager.STREAM_RING, mRingerVolumeSaved, 0);
    }

    private void saveRingtoneProperties() {
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            return;
        }
        mRingerModeSaved = am.getRingerMode();
        mRingerVolumeSaved = am.getStreamVolume(AudioManager.STREAM_RING);
    }

    private void dispatchMediaKeyWithWakeLockToAudioService(int keycode) {
        if (ActivityManagerNative.isSystemReady()) {
            IAudioService audioService = getAudioService();
            if (audioService != null) {
                try {
                    KeyEvent event = new KeyEvent(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN,
                            keycode, 0);
                    audioService.dispatchMediaKeyEventUnderWakelock(event);
                    event = KeyEvent.changeAction(event, KeyEvent.ACTION_UP);
                    audioService.dispatchMediaKeyEventUnderWakelock(event);
                } catch (RemoteException e) {
                    Log.e(TAG, "dispatchMediaKeyEvent threw exception " + e);
                }
            }
        }
    }

    private boolean isCameraActive() {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTasks = am
                .getRecentTasks(3, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        final ActivityManager.RecentTaskInfo recentInfo = recentTasks.get(0);

        Intent intent = new Intent(recentInfo.baseIntent);
        if (intent.toString().contains("focal")
                || intent.toString().contains("camera")) {
            return true;
        }

        if (recentInfo.origActivity != null) {
            intent.setComponent(recentInfo.origActivity);
        }

        String pkgName = intent.getComponent().getPackageName().toLowerCase();
        if (pkgName.contains("focal") || pkgName.contains("camera")) {
            return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        Log.d(TAG, "onSharedPreferenceChanged");
        if (key.equals(OClickControlActivity.OCLICK_PROXIMITY_ALERT_KEY)) {
            if (mConnected) {
                toggleRssiListener();
            }
        }
        if (key.equals(OClickControlActivity.OCLICK_FIND_PHONE_ALERT_TONE_KEY)) {
            String currentRingtoneString = sharedPreferences.getString(
                    OClickControlActivity.OCLICK_FIND_PHONE_ALERT_TONE_KEY,
                    null);
            if (currentRingtoneString != null) {
                Uri currentRingtone = Uri.parse(currentRingtoneString);
                mRingtone = RingtoneManager.getRingtone(
                        getApplicationContext(), currentRingtone);
            }
        }
    }
}
