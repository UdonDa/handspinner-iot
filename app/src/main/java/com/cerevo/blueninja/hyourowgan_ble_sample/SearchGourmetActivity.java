package com.cerevo.blueninja.hyourowgan_ble_sample;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class SearchGourmetActivity extends AppCompatActivity implements View.OnClickListener, LocationListener{

    TextView txvGps;
    Button btn;
    Button buttonGoogleMap;


    LocationManager locationmanager;
    ProgressDialog progressdialog;
    public Double mLat;
    public Double mLng;
    //ハンドスピナーで修正後の値
    public Double testLat;

    public Double dx_handspinner = 10.0;

    void initViews() {
        txvGps= (TextView)findViewById(R.id.txvGps);
        btn= (Button)findViewById(R.id.buttonFetchGps);
        buttonGoogleMap= (Button)findViewById(R.id.buttonGoogleMap);

        btn.setOnClickListener(this);
        buttonGoogleMap.setOnClickListener(this);

        locationmanager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        progressdialog=  new ProgressDialog (this);
        progressdialog.setMessage("Fetching..Location...");
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_gourmet);
        initViews();

        txvGps.setText("テストお");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
        }
        else {
            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5,1200,this);
            progressdialog.show();
        }
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
            /*
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            */

            //Uri uri = Uri.parse("geo:0,0?q=東京駅");
            //Uri uri = Uri.parse("geo:35.681382,139.766084?z=13");
            Uri uri = Uri.parse("geo:"+mLat+dx_handspinner+","+mLng+"?q=コンビニ");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLat = location.getLatitude();
        mLng = location.getLongitude();

        txvGps.setText("Location.."+testLat+" : "+location.getLongitude()); //+latitude+" : "+longitude


        progressdialog.dismiss();

        //locationmanager.removeUpdates(this);
/*
        try {
            Geocoder geocoder= new Geocoder(this);
            List<Address> adrslist= geocoder.getFromLocation(latitude,longitude,2);
            if (adrslist!=null && adrslist.size()>0){
                Address address = adrslist.get(0);

                StringBuffer buffer=new StringBuffer();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    buffer.append(address.getAddressLine(i)+"/n");

                }
                //txvGps.setText(buffer.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
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
