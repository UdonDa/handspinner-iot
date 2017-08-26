package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cerevo.blueninja.hyourowgan_ble_sample.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.w3c.dom.Text;

import twitter4j.User;

import static android.R.attr.key;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class TradeCardActivity extends AppCompatActivity implements LocationListener{
    Intent intent;
    String githubID;
    String twitterID;
    String lineID;
    String myName;
    Double latitude, longitude;
    Button exchangeCard;
    FirebaseDatabase database;
    DatabaseReference myRef;
    UserData userData;
    LocationManager locationmanager;
    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_card);

        locationmanager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressdialog=  new ProgressDialog (this);
        progressdialog.setMessage("Fetching..Location...");

        intent = getIntent();
        githubID = intent.getStringExtra("githubID");
        twitterID = intent.getStringExtra("twitterID");
        lineID = intent.getStringExtra("lineID");
        myName = intent.getStringExtra("myName");
        latitude =0.0;
        longitude = 0.0;

        //firebase initialize
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        initTextView();
        userData = new UserData(myName, githubID, twitterID, lineID, latitude, longitude);
        userData.userKey = myRef.push().getKey();
        exchangeCard = (Button) findViewById(R.id.exchange_card);
        exchangeCard.setOnClickListener(buttonClickListener);




        //パーミッションチェック
        isGpsPermission();

    }

    private void uploadUserData(final DatabaseReference databaseReference, final UserData ud) {
        //部屋に入る時，部屋の人数に合わせてuserIdを決める
        databaseReference.child("numberOfUserData").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    ud.userId = 1;
                    mutableData.setValue(1);
                    databaseReference.child("exchangeRoom").child(String.valueOf(userData.userId)).setValue(ud);
                } else {
                    int id = mutableData.getValue(int.class) + 1;
                    if(userData.userId == 0){
                        //初めての登録
                        //IDを取得してfirebaseにデータを送る
                        ud.userId = id;
                        mutableData.setValue(id);
                        databaseReference.child("exchangeRoom").child(String.valueOf(userData.userId)).setValue(ud);
                    }else{
                        //2回目以降の名刺交換
                        //userIDはfirebaseに登録済みなのでIDの更新などは行わない
                        databaseReference.child("exchangeRoom").child(String.valueOf(userData.userId)).setValue(ud);
                    }
                }
                //myRef.child(key).child("userName").setValue(user.userName);
                //user.nextUserId = 1;
                //Log.d("nextUserID", "at 297:: " + user.nextUserId);

                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
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
            }
        }
    };

    private void initTextView(){
        TextView githubView = (TextView)findViewById(R.id.github_id);
        TextView twitterView = (TextView)findViewById(R.id.twitter_id);
        TextView lineView = (TextView)findViewById(R.id.line_id);
        TextView nameView = (TextView)findViewById(R.id.my_name);

        githubView.setText(githubID);
        twitterView.setText(twitterID);
        lineView.setText(lineID);
        nameView.setText(myName);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
        }
        else {
            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5,1200,this);
            progressdialog.show();
        }
    }
}
