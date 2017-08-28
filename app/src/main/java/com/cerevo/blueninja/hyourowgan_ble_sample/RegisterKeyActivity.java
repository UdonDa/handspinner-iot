package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class RegisterKeyActivity extends AppCompatActivity {
    private Button mButtonConnect, mButtonRegisterKey;
    private TextView mTextViewValueGyro, mTextViewValueAccel, mTextViewValueMagm, mTextViewValueRotationNum, mTextViewStatus;
    private CheckBox mCheckBoxActive;

    private static final int SCAN_TIMEOUT = 20000;
    private static final String DEVICE_NAME = "HyouRowGan00";
    private static final String UUID_SERVICE_MSS = "00050000-6727-11e5-988e-f07959ddcdfb";//BlueNinja Motion sensor Service
    private static final String UUID_CHARACTERISTIC_VALUE = "00050001-6727-11e5-988e-f07959ddcdfb";//Motion sensor values.
    private static final String UUID_CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";//キャラクタリスティック設定UUID
    private static final String LOG_TAG = "HRG_MSS";
    private enum AppState {
        INIT,
        BLE_SCANNING,
        BLE_SCAN_FAILED,
        BLE_DEV_FOUND,
        BLE_SRV_FOUND,
        BLE_CHARACTERISTIC_NOT_FOUND,
        BLE_CONNECTED,
        BLE_DISCONNECTED,
        BLE_SRV_NOT_FOUND,
        BLE_READ_SUCCESS,
        BLE_NOTIF_REGISTERD,
        BLE_NOTIF_REGISTER_FAILED,
        BLE_WRITE_FALIED,
        BLE_WRITE,
        BLE_UPDATE_VALUE,
        BLE_CLOSED
    }
    private AppState mAppState = AppState.INIT;
    private void setStatus(AppState state) {
        Message msg = new Message();
        msg.what = state.ordinal();
        msg.obj = state.name();

        mAppState = state;
        mHandler.sendMessage(msg);
    }
    private BluetoothManager mBtManager;
    private BluetoothAdapter mBtAdapter;
    private BluetoothGatt mGatt;
    private BluetoothGatt mBtGatt;
    private BluetoothGattCharacteristic mCharacteristic;
    private HandspinnerValues mHandspinnerValues;
    private Handler mHandler;

    private AppState getStats()
    {
        return mAppState;
    }

    private byte[] mRecvValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_key);
        initViews();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mTextViewStatus.setText((String)msg.obj);
                AppState sts = AppState.values()[msg.what];
                switch (sts) {
                    case INIT:
                    case BLE_SCAN_FAILED:
                    case BLE_CLOSED:
                    case BLE_DISCONNECTED:
                        mButtonConnect.setEnabled(true);
                        mCheckBoxActive.setEnabled(false);
                        mCheckBoxActive.setChecked(false);
                        break;
                    case BLE_SRV_NOT_FOUND:
                    case BLE_NOTIF_REGISTER_FAILED:
                    case BLE_SCANNING:
                        mButtonConnect.setEnabled(false);
                        mCheckBoxActive.setEnabled(false);
                        mCheckBoxActive.setChecked(false);
                        break;
                    case BLE_CONNECTED:
                    case BLE_WRITE:
                        mButtonConnect.setEnabled(false);
                        mCheckBoxActive.setEnabled(true);
                        break;
                    case BLE_UPDATE_VALUE:
                        updateValues();
                        break;
                }
            }
        };
    }

    private void initViews() {
        mBtManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBtAdapter = mBtManager.getAdapter();
        if ((mBtAdapter == null) || !mBtAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Warning: Bluetooth Disabled.", Toast.LENGTH_SHORT).show();
            finish();
        }

        mButtonConnect = (Button)findViewById(R.id.buttonConnect);
        mButtonConnect.setOnClickListener(buttonClickLinstener);
        mButtonRegisterKey = (Button)findViewById(R.id.buttonRegisterKey);
        mButtonRegisterKey.setOnClickListener(buttonClickLinstener);
        mTextViewValueGyro = (TextView)findViewById(R.id.textViewKeyGyro);
        mTextViewValueAccel = (TextView)findViewById(R.id.textViewKeyAccel);
        mTextViewValueMagm = (TextView)findViewById(R.id.textViewKeyMagm);
        mTextViewValueRotationNum = (TextView)findViewById(R.id.textViewKeyRotationNumber);
        mTextViewStatus = (TextView)findViewById(R.id.textViewBleStatus);
        mCheckBoxActive = (CheckBox)findViewById(R.id.checkBoxActive);
        mCheckBoxActive.setOnClickListener(checkboxClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setStatus(AppState.INIT);
        mCheckBoxActive.setChecked(false);
        mCheckBoxActive.setEnabled(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectBLE();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void updateValues() {
        short grx, gry, grz, arx, ary, arz, mrx, mry, mrz;
        int recv_len;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recv_len = mRecvValue.length;
        } else {
            recv_len = 18;
        }
        for (int offset = 0; offset < recv_len; offset += 18) {


            /*---この下でハンドスピナーの値を入れて行く！---*/
            mHandspinnerValues = new HandspinnerValues();
            /* Convert byte array to values. */
            ByteBuffer buff;
            //Gyro X
            buff = ByteBuffer.wrap(mRecvValue, offset + 0, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            grx = buff.getShort();
            mHandspinnerValues.mKeyGyroX = (double) grx / 16.4;
            //Gyro Y
            buff = ByteBuffer.wrap(mRecvValue, offset + 2, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            gry = buff.getShort();
            mHandspinnerValues.mKeyGyroY = (double) gry / 16.4;
            //Gyro Z
            buff = ByteBuffer.wrap(mRecvValue, offset + 4, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            grz = buff.getShort();
            mHandspinnerValues.mKeyGyroZ = (double) grz / 16.4;
            //Accel X
            buff = ByteBuffer.wrap(mRecvValue, offset + 6, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            arx = buff.getShort();
            mHandspinnerValues.mKeyAccelX = (double) arx*10 / 2048;
            //Accel Y
            buff = ByteBuffer.wrap(mRecvValue, offset + 8, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            ary = buff.getShort();
            mHandspinnerValues.mKeyAccelY = (double) ary*10 / 2048;
            //Accel Z
            buff = ByteBuffer.wrap(mRecvValue, offset + 10, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            arz = buff.getShort();
            mHandspinnerValues.mKeyAccelZ = (double) arz*10 / 2048;
            //Magneto X
            buff = ByteBuffer.wrap(mRecvValue, offset + 12, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            mrx = buff.getShort();
            mHandspinnerValues.mKeyMagnX = mrx;
            //Magneto Y
            buff = ByteBuffer.wrap(mRecvValue, offset + 14, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            mry = buff.getShort();
            mHandspinnerValues.mKeyMagnY = mry;
            //Magneto Z
            buff = ByteBuffer.wrap(mRecvValue, offset + 16, 2);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            mrz = buff.getShort();
            mHandspinnerValues.mKeyMagnZ = mrz;

            mTextViewValueGyro.setText(" x: " + String.valueOf(mHandspinnerValues.mGyroX) + "\n y: " + String.valueOf(mHandspinnerValues.mGyroY) + "\n z: "+ String.valueOf(mHandspinnerValues.mGyroZ));
            mTextViewValueAccel.setText(" x: "+ String.valueOf(mHandspinnerValues.mAccelX) + "\n y: " + String.valueOf(mHandspinnerValues.mAccelY) + "\n z: " +String.valueOf(mHandspinnerValues.mAccelZ) + " [m/s^2]");
            mTextViewValueMagm.setText(" x: " + String.valueOf(mHandspinnerValues.mMagnX) + "\n y: " + String.valueOf(mHandspinnerValues.mMagnY) + "\n z: "+ String.valueOf(mHandspinnerValues.mMagnZ));

        }
    }

    private View.OnClickListener buttonClickLinstener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonConnect:
                    connectBLE();
                    break;
                case R.id.buttonDisconnect:
                    switch (getStats()) {
                        case BLE_SCANNING:
                            mBtAdapter.stopLeScan(mLeScanCallback);
                            setStatus(AppState.BLE_CLOSED);
                            break;
                        default:
                            disconnectBLE();
                            break;
                    }
                    break;
            }
        }
    };

    private View.OnClickListener checkboxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox chk = (CheckBox)v;
            if (chk.isChecked()) {
                enableBLENotification();
            } else {
                disableBLENotification();
            }
        }
    };

    /* BLEスキャンコールバック */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (DEVICE_NAME.equals(device.getName())) {
                setStatus(AppState.BLE_DEV_FOUND);
                mBtAdapter.stopLeScan(this);
                mBtGatt = device.connectGatt(getApplicationContext(), false, mBluetoothGattCallback);
            }
        }
    };

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    /* 接続 */
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    /* 切断 */
                    setStatus(AppState.BLE_DISCONNECTED);
                    mBtGatt = null;
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService service = gatt.getService(UUID.fromString(UUID_SERVICE_MSS));
            if (service == null) {
                //サービスが見つからない
                setStatus(AppState.BLE_SRV_NOT_FOUND);
            } else {
                //サービスが見つかった
                setStatus(AppState.BLE_SRV_FOUND);
                mCharacteristic = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_VALUE));
                if (mCharacteristic == null) {
                    //Characteristicが見つからない
                    setStatus(AppState.BLE_CHARACTERISTIC_NOT_FOUND);
                    return;
                }
            }
            mGatt = gatt;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                gatt.requestMtu(40);
            }
            setStatus(AppState.BLE_CONNECTED);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (UUID_CHARACTERISTIC_VALUE.equals(characteristic.getUuid().toString())) {
                byte read_data[] = characteristic.getValue();
                mRecvValue = Arrays.copyOf(read_data, 36);
                setStatus(AppState.BLE_UPDATE_VALUE);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Log.i(LOG_TAG, String.format("mtu=%d", mtu));
            //super.onMtuChanged(gatt, mtu, status);
        }
    };

    private void connectBLE() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtAdapter.stopLeScan(mLeScanCallback);
                if (AppState.BLE_SCANNING.equals(getStats())) {
                    setStatus(AppState.BLE_SCAN_FAILED);
                }
            }
        }, SCAN_TIMEOUT);

        mBtAdapter.stopLeScan(mLeScanCallback);
        mBtAdapter.startLeScan(mLeScanCallback);
        setStatus(AppState.BLE_SCANNING);
    }

    private void disconnectBLE() {
        if (mBtGatt != null) {
            disableBLENotification();

            mBtGatt.close();
            mBtGatt = null;
            mCharacteristic = null;

            setStatus(AppState.BLE_CLOSED);
        }
    }

    private void enableBLENotification() {
        if (mGatt.setCharacteristicNotification(mCharacteristic, true)) {
            BluetoothGattDescriptor desc = mCharacteristic.getDescriptor(UUID.fromString(UUID_CLIENT_CHARACTERISTIC_CONFIG));
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (mGatt.writeDescriptor(desc)) {
                setStatus(AppState.BLE_NOTIF_REGISTERD);
                return;
            }
        }
        setStatus(AppState.BLE_NOTIF_REGISTER_FAILED);
    }

    private void disableBLENotification() {
        BluetoothGattDescriptor desc = mCharacteristic.getDescriptor(UUID.fromString(UUID_CLIENT_CHARACTERISTIC_CONFIG));
        desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        if (mGatt.writeDescriptor(desc)) {
            if (mGatt.setCharacteristicNotification(mCharacteristic, false)) {
                setStatus(AppState.BLE_NOTIF_REGISTERD);
                return;
            }
        }
        setStatus(AppState.BLE_NOTIF_REGISTER_FAILED);
    }
}
