package com.cerevo.blueninja.hyourowgan_ble_sample;


import android.Manifest;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

import twitter4j.Twitter;

public class SearchGourmetActivity extends AppCompatActivity implements View.OnClickListener, LocationListener{

    //値保持するクラス
    Coordinate coordinate;
    private Double mLat, mLng;

    TextView txvGps;
    Button btn;
    Button buttonGoogleMap;

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
        //ハンドスピナー連携部分がないので、適当に値を入れてる
        coordinate.mHandspinnerLat = 3.0;
        coordinate.mHandspinnerLng = 3.0;
        initViews();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
        }
        else {
            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5,1200,this);
            progressdialog.show();
        }
    }

    void initViews() {
        txvGps= (TextView)findViewById(R.id.txvGps);
        btn= (Button)findViewById(R.id.buttonFetchGps);
        buttonGoogleMap= (Button)findViewById(R.id.buttonGoogleMap);
        btn.setOnClickListener(this);
        buttonGoogleMap.setOnClickListener(this);
        //Gps
        locationmanager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressdialog=  new ProgressDialog (this);
        progressdialog.setMessage("Fetching..Location...");
        coordinate.mHandspinnerLat = 10.0;
        coordinate.mHandspinnerLng = 10.0;
        //Twitter
        preferences = act.getSharedPreferences(TIMES, Context.MODE_PRIVATE);
        mTwitter = TwitterUtils.getTwitterInstance(act);
        mTweet = new Tweet(this, mTwitter);
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
            mLat = coordinate.mGpsLat + coordinate.mHandspinnerLat;
            mLng = coordinate.mGpsLng + coordinate.mHandspinnerLng;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(TIMES, preferences.getInt(TIMES,0) + 1);
            editor.apply();
            mTweet.tweetSearchGourmet();
            //Uri uri = Uri.parse("geo:"+mLat+","+mLng+"?q=居酒屋");
            Uri uri = Uri.parse("geo:"+43.06417+","+141.34694+"?q=コンビニ");//デバック用
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        coordinate.mGpsLat = location.getLatitude();
        coordinate.mGpsLng = location.getLongitude();
        txvGps.setText("Location.."+coordinate.mGpsLat+" : "+coordinate.mGpsLng); //+latitude+" : "+longitude
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
