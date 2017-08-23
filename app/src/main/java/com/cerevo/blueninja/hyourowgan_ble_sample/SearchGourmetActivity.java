package com.cerevo.blueninja.hyourowgan_ble_sample;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

    TextView txtview;
    Button btn;


    LocationManager locationmanager;
    ProgressDialog progressdialog;

    void initViews() {
        txtview= (TextView)findViewById(R.id.txvGps);
        btn= (Button)findViewById(R.id.buttonFetchGps);

        btn.setOnClickListener(this);

        locationmanager= (LocationManager) getSystemService(LOCATION_SERVICE);

        progressdialog=  new ProgressDialog (this);
        progressdialog.setMessage("Fetching..Location...");
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_gourmet);
        initViews();

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
        if (view.getId()==R.id.buttonFetchGps){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Grant Permission from settings", Toast.LENGTH_SHORT).show();
            }
            else {
                locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5,1200,this);
                progressdialog.show();
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude= location.getLatitude();
        double longitude= location.getLongitude();

        txtview.setText("Location.."+latitude+" : "+longitude);

        progressdialog.dismiss();

        //locationmanager.removeUpdates(this);


        try {
            Geocoder geocoder= new Geocoder(this);
            List<Address> adrslist= geocoder.getFromLocation(latitude,longitude,2);
            if (adrslist!=null && adrslist.size()>0){
                Address address = adrslist.get(0);

                StringBuffer buffer=new StringBuffer();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    buffer.append(address.getAddressLine(i)+"/n");

                }
                txtview.setText(buffer.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
