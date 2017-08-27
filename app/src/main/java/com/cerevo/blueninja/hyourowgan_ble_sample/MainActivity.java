package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    Button buttonShowGPIO;
    Button buttonShowPWM;
    Button buttonShowMotionSensor;
    Button buttonShowAirpressureSensor;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        buttonShowGPIO = (Button)findViewById(R.id.buttonShowGPIO);
        buttonShowGPIO.setOnClickListener(buttonClickListener);
        buttonShowPWM = (Button)findViewById(R.id.buttonShowPWM);
        buttonShowPWM.setOnClickListener(buttonClickListener);
        buttonShowMotionSensor = (Button)findViewById(R.id.buttonShowMotionSensor);
        buttonShowMotionSensor.setOnClickListener(buttonClickListener);
        buttonShowAirpressureSensor = (Button)findViewById(R.id.buttonShowAirpressureSensor);
        buttonShowAirpressureSensor.setOnClickListener(buttonClickListener);

    }

    public View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.buttonShowGPIO:
                    intent = new Intent(getApplicationContext(), GpioActivity.class);
                    startActivity(intent);
                    break;
                case R.id.buttonShowPWM:
                    intent = new Intent(getApplicationContext(), PwmActivity.class);
                    startActivity(intent);
                    break;
                case R.id.buttonShowMotionSensor:
                    intent = new Intent(getApplicationContext(), MotionSensorActivity.class);
                    startActivity(intent);
                    break;
                case R.id.buttonShowAirpressureSensor:
                    intent = new Intent(getApplicationContext(), AirpressureActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
