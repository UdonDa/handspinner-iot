package com.cerevo.blueninja.hyourowgan_ble_sample;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import twitter4j.Twitter;

public class SearchGourmetActivity extends AppCompatActivity implements View.OnClickListener, LocationListener{

    //値保持するクラス
    Coordinate coordinate;
    TextView txvGps, mTextViewStatus;
    Button btn, mButtonConnect;
    Button buttonGoogleMap;
    CheckBox checkBoxAuthenticate;

    private static final int SCAN_TIMEOUT = 20000;
    private static final String DEVICE_NAME = "HyouRowGan00";
    private static final String UUID_SERVICE_APSS = "00060000-6727-11e5-988e-f07959ddcdfb";
    private static final String UUID_CHARACTERISTIC_VALUE = "00060001-6727-11e5-988e-f07959ddcdfb";
    private static final String UUID_CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String LOG_TAG = "HRG_APSS";

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
    private enum HandspinnerState {
        ON,
        OFF
    }
    private AppState mAppState = AppState.INIT;
    private HandspinnerState mHandspinnerState = HandspinnerState.OFF;
    private void setStatus(AppState state) {
        Message msg = new Message();
        msg.what = state.ordinal();
        msg.obj = state.name();
        mAppState = state;
        mHandler.sendMessage(msg);
    }
    private void setHandspinnerStatus(HandspinnerState state) {
        Message msg = new Message();
        msg.what = state.ordinal();
        msg.obj = state.name();
        mHandspinnerState = state;
        mHandspinnerHandler.sendMessage(msg);
    }

    private BluetoothManager mBtManager;
    private BluetoothAdapter mBtAdapter;
    private BluetoothGatt mGatt;
    private BluetoothGatt mBtGatt;
    private BluetoothGattCharacteristic mCharacteristic;
    private HandspinnerValues mHandspinnerValues;
    private Handler mHandler,mHandspinnerHandler;

    private AppState getStats() {
        return mAppState;
    }
    private HandspinnerState getHandspinnerStats() {
        return mHandspinnerState;
    }
    private byte[] mRecvValue;

    LocationManager locationmanager;
    ProgressDialog progressdialog;

    //Twitter
    private Twitter mTwitter;
    public Tweet mTweet;
    SharedPreferences preferences;
    Context act = this;
    String TIMES = "numberOfTweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_gourmet);
        coordinate = new Coordinate();
        mHandspinnerValues = new HandspinnerValues();
        //ハンドスピナー連携部分がないので、適当に値を入れてる
        mHandspinnerValues.mLat = 3.0;
        mHandspinnerValues.mLng = 3.0;
        initViews();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
        }
        else {
            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5,1200,this);
            progressdialog.show();
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mTextViewStatus.setText((String)msg.obj);
                AppState sts = AppState.values()[msg.what];
                switch (sts) {
                    case INIT:
                    case BLE_SCAN_FAILED:
                        break;
                    case BLE_CLOSED:
                    case BLE_DISCONNECTED:
                        mButtonConnect.setEnabled(true);
                        checkBoxAuthenticate.setEnabled(false);
                        checkBoxAuthenticate.setChecked(false);
                        break;
                    case BLE_SRV_NOT_FOUND:
                    case BLE_NOTIF_REGISTER_FAILED:
                    case BLE_SCANNING:
                        mButtonConnect.setEnabled(false);
                        checkBoxAuthenticate.setEnabled(false);
                        checkBoxAuthenticate.setChecked(false);
                        break;
                    case BLE_CONNECTED:
                    case BLE_WRITE:
                        mButtonConnect.setEnabled(false);
                        checkBoxAuthenticate.setEnabled(true);
                        break;
                    case BLE_UPDATE_VALUE:
                        mHandspinnerValues = new HandspinnerValues();
                        ByteBuffer buff;
                        buff = ByteBuffer.wrap(mRecvValue, 0, 2);
                        buff.order(ByteOrder.LITTLE_ENDIAN);
                        short rt = buff.getShort();
                        //mTextLastStopped.setText("停止位置："+ rt/256);
                        int mDirection = rt % 256;
                        //mTextViewDirectionOfRotation.setText("回転方向: " + rt%256 );

                        buff = ByteBuffer.wrap(mRecvValue, 2, 4);
                        buff.order(ByteOrder.LITTLE_ENDIAN);
                        int ra = buff.getInt();
                        //mTextTotalRotation.setText(String.format("総合回転数: %7.2f", (float)ra / (256 * 256)));
                        float rpm = ra % (256*256);
                        //mTextViewRpm.setText(String.format("rpm: %7.2f", (float)ra % (256 * 256)));
                        break;
                }
            }
        };

        mHandspinnerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                HandspinnerState sts = HandspinnerState.values()[msg.what];
                switch (sts) {
                    case ON:
                        break;
                    case OFF:
                        break;
                    default:
                        break;
                }
            }
        };
    }

    void initViews() {
        mBtManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBtAdapter = mBtManager.getAdapter();
        if ((mBtAdapter == null) || !mBtAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Warning: Bluetooth Disabled.", Toast.LENGTH_SHORT).show();
            finish();
        }
        txvGps= (TextView)findViewById(R.id.txvGps);
        btn= (Button)findViewById(R.id.buttonFetchGps);
        buttonGoogleMap= (Button)findViewById(R.id.buttonGoogleMap);
        btn.setOnClickListener(this);
        buttonGoogleMap.setOnClickListener(this);
        checkBoxAuthenticate = (CheckBox)findViewById(R.id.checkBoxActive);
        checkBoxAuthenticate.setOnClickListener(checkboxClickListener);
        mButtonConnect = (Button)findViewById(R.id.buttonConnect);
        mButtonConnect.setOnClickListener(this);
        mTextViewStatus = (TextView)findViewById(R.id.textViewStatus);
        //Gps
        locationmanager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressdialog=  new ProgressDialog (this);
        progressdialog.setMessage("Fetching..Location...");
        coordinate.mUserGpsLat = 10.0;
        coordinate.mUserGpsLng = 10.0;
        //Twitter
        preferences = act.getSharedPreferences(TIMES, Context.MODE_PRIVATE);
        mTwitter = TwitterUtils.getTwitterInstance(act);
        mTweet = new Tweet(this, mTwitter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setStatus(AppState.INIT);

        checkBoxAuthenticate.setChecked(false);
        checkBoxAuthenticate.setEnabled(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectBLE();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.buttonFetchGps) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
            } else {
                locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                progressdialog.show();
            }
        }

        if(view.getId()==R.id.buttonGoogleMap) {
            Double mLat, mLng;
            //unchi
            mLat = coordinate.mUserGpsLat + mHandspinnerValues.mLat;
            mLng = coordinate.mUserGpsLng + mHandspinnerValues.mLng;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(TIMES, preferences.getInt(TIMES,0) + 1);
            editor.apply();
            mTweet.tweetSearchGourmet();
            Uri uri = Uri.parse("geo:"+mLat+","+mLng+"?q=居酒屋");
            //Uri uri = Uri.parse("geo:"+43.06417+","+141.34694+"?q=コンビニ");//デバック用
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        if(view.getId()==R.id.buttonConnect) {
            connectBLE();
        }
    }

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

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (DEVICE_NAME.equals(device.getName())) {
                //HyouRowGanを発見
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
            BluetoothGattService service = gatt.getService(UUID.fromString(UUID_SERVICE_APSS));
            if (service == null) {
                //サービスが見つからない
                setStatus(AppState.BLE_SRV_NOT_FOUND);
                return;
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
            setStatus(AppState.BLE_CONNECTED);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //super.onCharacteristicChanged(gatt, characteristic);
            if (UUID_CHARACTERISTIC_VALUE.equals(characteristic.getUuid().toString())) {
                byte read_data[] = characteristic.getValue();
                mRecvValue = Arrays.copyOf(read_data, 6);
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

    @Override
    public void onLocationChanged(Location location) {
        coordinate.mUserGpsLat = location.getLatitude();
        coordinate.mUserGpsLng = location.getLongitude();
        txvGps.setText("Location.."+coordinate.mUserGpsLat+" : "+coordinate.mUserGpsLng); //+latitude+" : "+longitude
        progressdialog.dismiss();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
