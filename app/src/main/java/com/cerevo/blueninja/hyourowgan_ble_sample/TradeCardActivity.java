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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cerevo.blueninja.hyourowgan_ble_sample.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import twitter4j.User;

import static android.R.attr.button;
import static android.R.attr.key;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

public class TradeCardActivity extends AppCompatActivity implements LocationListener {
    Intent intent;
    String githubID, gettedGithubID;
    String twitterID, gettedTwitterID;
    String lineID, gettedLineID;
    String myName,gettedName;
    Double latitude, longitude;
    float[] distance = new float[3];
    Double gettedLatitude, gettedlongitude;
    long throwTime;
    Button exchangeCard;
    FirebaseDatabase database;
    DatabaseReference myRef;
    UserData userData;
    LocationManager locationmanager;
    ProgressDialog progressdialog;
    ChildEventListener childEventListener;
    Boolean isexchanged = false;
    int id;

    //BLE
    Button mButtonConnect;
    //回転とか
    int direction, stopPos;
    float totalRotation, rpm;

    private static final int SCAN_TIMEOUT = 20000;
    private static final String DEVICE_NAME = "HyouRowGan00";
    private static final String UUID_SERVICE_MSS = "00060000-6727-11e5-988e-f07959ddcdfb";//BlueNinja Motion sensor Service
    private static final String UUID_CHARACTERISTIC_VALUE = "00060001-6727-11e5-988e-f07959ddcdfb";//Motion sensor values.
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_card);
        mBtManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBtAdapter = mBtManager.getAdapter();
        if ((mBtAdapter == null) || !mBtAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Warning: Bluetooth Disabled.", Toast.LENGTH_SHORT).show();
            finish();
        }
        progressdialog=  new ProgressDialog (this);
        progressdialog.setMessage("Fetching..Location...");
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
        }
        else {
            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5,1200,this);
            progressdialog.show();
        }

        intent = getIntent();
        githubID = intent.getStringExtra("githubID");
        twitterID = intent.getStringExtra("twitterID");
        lineID = intent.getStringExtra("lineID");
        myName = intent.getStringExtra("myName");
        latitude = 0.0;
        longitude = 0.0;

        //firebase initialize
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //イベントリスナーを登録
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                //新規ユーザ追加時
                //GPS座標が近ければその連絡先をgetする
                if(dataSnapshot.getKey() != "id"){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.v("added", snapshot.getKey());
                        switch(snapshot.getKey()){
                            case "userName":{
                                gettedName = (String)snapshot.getValue();
                                break;
                            }
                            case "githubId":{
                                gettedGithubID = (String)snapshot.getValue();
                                break;
                            }
                            case "twitterId":{
                                gettedTwitterID = (String)snapshot.getValue();
                                break;
                            }
                            case "lineId":{
                                gettedLineID = (String)snapshot.getValue();
                                break;
                            }
                        }
                        Log.v("getted", gettedName + " " + gettedGithubID + " " + gettedTwitterID + " " + gettedLineID);
                        //reloadView();
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                if(dataSnapshot.getKey() != "id" && Integer.parseInt(dataSnapshot.getKey()) != userData.userId){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.v("changed", snapshot.getKey());
                        switch(snapshot.getKey()){
                            case "userName":{
                                gettedName = (String)snapshot.getValue();
                                break;
                            }
                            case "githubId":{
                                gettedGithubID = (String)snapshot.getValue();
                                break;
                            }
                            case "twitterId":{
                                gettedTwitterID = (String)snapshot.getValue();
                                break;
                            }
                            case "lineId":{
                                gettedLineID = (String)snapshot.getValue();
                                break;
                            }
                            case "latitude":{
                                gettedLatitude = (double)snapshot.getValue();
                                break;
                            }
                            case "longitude":{
                                gettedlongitude = (double)snapshot.getValue();
                                break;
                            }
                        }
                        Log.v("getted", gettedName + " " + gettedGithubID + " " + gettedTwitterID + " " + gettedLineID);
                    }
                    Location.distanceBetween(userData.latitude, userData.longitude, gettedLatitude, gettedlongitude, distance);
                    Log.v("distance", distance[0] + "m");
                    if( distance[0] < 100.0 && Math.abs(throwTime - System.currentTimeMillis()) < 3000 && !isexchanged){
                        //GPS座標が近ければ名刺交換処理
                        reloadView();
                        isexchanged = true;
                    }
                }else{

                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.child("exchangeRoom").addChildEventListener(childEventListener);

        initTextView();
        userData = new UserData(myName, githubID, twitterID, lineID, latitude, longitude);
        //userData.userKey = myRef.push().getKey();
        uploadUserData(myRef, userData);
        exchangeCard = (Button) findViewById(R.id.exchange_card);
        exchangeCard.setOnClickListener(buttonClickListener);
        mButtonConnect = (Button)findViewById(R.id.connect_button);
        mButtonConnect.setOnClickListener(buttonClickListener);
        //パーミッションチェック
        isGpsPermission();

        //BLE
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //mTextViewStatus.setText((String)msg.obj);
                AppState sts = AppState.values()[msg.what];
                switch (sts) {
                    case INIT:
                    case BLE_SCAN_FAILED:
                        break;
                    case BLE_CLOSED:
                    case BLE_DISCONNECTED:
                        mButtonConnect.setEnabled(true);
                        break;
                    case BLE_SRV_NOT_FOUND:
                    case BLE_NOTIF_REGISTER_FAILED:
                    case BLE_SCANNING:
                        mButtonConnect.setEnabled(false);
                        break;
                    case BLE_CONNECTED:
                    case BLE_WRITE:
                        mButtonConnect.setEnabled(false);
                        break;
                    case BLE_UPDATE_VALUE:
                        //updateValues();
                        //忍者から値が送られてきたときの処理
                        ByteBuffer buff;
                        buff = ByteBuffer.wrap(mRecvValue, 0, 4);
                        buff.order(ByteOrder.LITTLE_ENDIAN);
                        short rt = buff.getShort();
                        //停止位置
                        stopPos = rt/256;
                        //回転方向
                        direction = rt%256;
                        //mTextLastStopped.setText("停止位置："+ rt/256);
                        //mTextDirectionOfRotation.setText("回転方向: " + rt%256 );

                        //Airpressure
                        buff = ByteBuffer.wrap(mRecvValue, 2, 4);
                        buff.order(ByteOrder.LITTLE_ENDIAN);
                        int ra = buff.getInt();
                        //総回転数
                        totalRotation = (float)ra/(256*256);
                        //回転数
                        rpm = (float)ra%(256*256);
                        //mTextTotalRotation.setText(String.format("総合回転数: %7.2f", (float)ra / (256 * 256)));
                        //mTextRpm.setText(String.format("rpm: %7.2f", (float)ra % (256 * 256)));

                        if(rpm >250){
                            uploadUserData(myRef,userData);
                        }
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
                        //isFinishedAuthentication(getApplicationContext());
                        break;
                    case OFF:
                        break;
                    default:
                        break;
                }
            }
        };

    }
    @Override
    protected void onStop() {
        super.onStop();
        disconnectBLE();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isexchanged = false;
    }


    private void uploadUserData(final DatabaseReference databaseReference, final UserData ud) {
        //部屋に入る時，部屋の人数に合わせてuserIdを決める
        //int id;
        databaseReference.child("numberOfUserData").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null && mutableData.getValue(int.class) < 1) {
                    ud.userId = 1;
                    mutableData.setValue(1);
                    databaseReference.child("exchangeRoom").child(String.valueOf(userData.userId)).setValue(ud);
                } else {
                    id = mutableData.getValue(int.class) + 1;
                    Log.v("firebase", id + mutableData.getKey());
                    if (userData.userId == 0) {
                        //初めての登録
                        //IDを取得してfirebaseにデータを送る
                        userData.userId = id;
                        mutableData.setValue(id);
                        databaseReference.child("exchangeRoom").child(String.valueOf(userData.userId)).setValue(userData);
                    } else {
                        //2回目以降の名刺交換
                        //userIDはfirebaseに登録済みなのでIDの更新などは行わない
                        //まるごと送るけど，実質GPSの更新情報の更新
                        //userData.userId = id;
                        //mutableData.setValue(userData.userId);
                        databaseReference.child("exchangeRoom").child(String.valueOf(userData.userId)).setValue(userData);
                    }
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
        databaseReference.child("numberOfUserData").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                myRef.child("exchangeRoom").child(String.valueOf(userData.userId)).child("latitude").removeValue();
                myRef.child("exchangeRoom").child(String.valueOf(userData.userId)).child("longitude").removeValue();
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });

        throwTime = System.currentTimeMillis();

    }


    public View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.exchange_card:
                    //firebase
                    uploadUserData(myRef, userData);
                    showToast("firebaseにデータ送ったよ！");
                    break;
                case R.id.connect_button:{
                    connectBLE();
                    showToast("connect");
                    break;
                }
            }
        }
    };

    //最初アクティビティを開いた時に自分の連絡先情報をtextViewni反映させる
    private void initTextView() {
        TextView nameView = (TextView) findViewById(R.id.my_name);
        TextView githubView = (TextView) findViewById(R.id.github_id);
        TextView twitterView = (TextView) findViewById(R.id.twitter_id);
        TextView lineView = (TextView) findViewById(R.id.line_id);
        nameView.setText(myName);
        githubView.setText(githubID);
        twitterView.setText(twitterID);
        lineView.setText(lineID);
    }

    //連絡先を受け取った時に，受け取った情報を格納する
    private void reloadView(){
        TextView gettedNameView = (TextView)findViewById(R.id.getted_name);
        TextView gettedGithubView = (TextView)findViewById(R.id.getted_github_id);
        TextView gettedTwitterView = (TextView)findViewById(R.id.getted_twitter_id);
        TextView gettedLineView = (TextView)findViewById(R.id.getted_line_id);
        TextView gettedDistanceView = (TextView)findViewById(R.id.distance);
        gettedNameView.setText(String.valueOf(distance));
        gettedNameView.setText(gettedName);
        gettedGithubView.setText(gettedGithubID);
        gettedTwitterView.setText(gettedTwitterID);
        gettedLineView.setText(gettedLineID);
        gettedDistanceView.setText(String.valueOf(distance[0]));
    }

    //BLE
    private void updateValues() {
        short grx, gry, grz, arx, ary, arz, mrx, mry, mrz;
        int recv_len;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recv_len = mRecvValue.length;
        } else {
            recv_len = 18;
        }
        for (int offset = 0; offset < recv_len; offset += 18) {
            mHandspinnerValues = new HandspinnerValues();
            /* Convert byte array to values. */
            //Temperature
            ByteBuffer buff;
            buff = ByteBuffer.wrap(mRecvValue, 0, 4);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            short rt = buff.getShort();
            //停止位置
            stopPos = rt/256;
            //回転方向
            direction = rt%256;
            //mTextLastStopped.setText("停止位置："+ rt/256);
            //mTextDirectionOfRotation.setText("回転方向: " + rt%256 );

            //Airpressure
            buff = ByteBuffer.wrap(mRecvValue, 2, 4);
            buff.order(ByteOrder.LITTLE_ENDIAN);
            int ra = buff.getInt();
            //総回転数
            totalRotation = (float)ra/(256*256);
            //回転数
            rpm = (float)ra%(256*256);
            //mTextTotalRotation.setText(String.format("総合回転数: %7.2f", (float)ra / (256 * 256)));
            //mTextRpm.setText(String.format("rpm: %7.2f", (float)ra % (256 * 256)));

            if(rpm >250){
                uploadUserData(myRef,userData);
            }


        }
    }

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
            super.onMtuChanged(gatt, mtu, status);
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

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //GPS関連
    @Override
    public void onLocationChanged(Location location) {
        Log.v("gps changed", location.getLatitude() + "," + location.getLongitude());
        userData.latitude = location.getLatitude();
        userData.longitude = location.getLongitude();
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

    private void isGpsPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
        } else {
            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 1200, this);
            progressdialog.show();
        }
    }
}