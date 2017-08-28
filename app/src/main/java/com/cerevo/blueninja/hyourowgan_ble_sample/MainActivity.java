package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class MainActivity extends AppCompatActivity {
    Button buttonShowMotionSensor,buttonTweet,buttonSearchGourmet,buttonTradeCard;
    SharedPreferences settingPref;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_button();
        settingPref = PreferenceManager.getDefaultSharedPreferences(this);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected  void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.buttonShowMotionSensor:
                    intent = new Intent(getApplicationContext(), MotionSensorsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.buttonSearchGourmet:
                    intent = new Intent(getApplicationContext(), SearchGourmetActivity.class);
                    startActivity(intent);
                    break;
                case R.id.buttonTradeCard:
                    String githubID = settingPref.getString("github_text", "none");
                    String twitterID = settingPref.getString("twitter_text", "none");
                    String lineID = settingPref.getString("line_text", "none");
                    String name = settingPref.getString("name_text", "none");
                    intent = new Intent(getApplicationContext(), TradeCardActivity.class);
                    intent.putExtra("githubID", githubID);
                    intent.putExtra("twitterID", twitterID);
                    intent.putExtra("lineID", lineID);
                    intent.putExtra("myName", name);
                    startActivity(intent);
                    break;
            }
        }
    };

    public void init_button() {
        buttonShowMotionSensor = (Button)findViewById(R.id.buttonShowMotionSensor);
        buttonShowMotionSensor.setOnClickListener(buttonClickListener);
        buttonSearchGourmet= (Button)findViewById(R.id.buttonSearchGourmet);
        buttonSearchGourmet.setOnClickListener(buttonClickListener);
        buttonTradeCard= (Button)findViewById(R.id.buttonTradeCard);
        buttonTradeCard.setOnClickListener(buttonClickListener);
    }

    //menu関係
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.menuOption_1:
                intent=new Intent(this,com.cerevo.blueninja.hyourowgan_ble_sample.SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
