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

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class MainActivity extends AppCompatActivity {

    Button buttonShowGPIO;
    Button buttonShowPWM;
    Button buttonShowMotionSensor;
    Button buttonShowAirpressureSensor;
    Button buttonTweet;
    Button buttonSearchGourmet;
    Button buttonTradeCard;


    //Twitter
    private Twitter mTwitter;
    public Tweet mTweet;
    SharedPreferences preferences;
    Context act = this;
    String TIMES = "numberOfTweet";

    //menu
    SharedPreferences settingPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isVerifiedTwitter();

        init_button();
        init_twitter();
        settingPref = PreferenceManager.getDefaultSharedPreferences(this);
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

    /*---Twitter---*/
    public void isVerifiedTwitter() {
        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void init_button() {
        buttonShowGPIO = (Button)findViewById(R.id.buttonShowGPIO);
        buttonShowGPIO.setOnClickListener(buttonClickListener);
        buttonShowPWM = (Button)findViewById(R.id.buttonShowPWM);
        buttonShowPWM.setOnClickListener(buttonClickListener);
        buttonShowMotionSensor = (Button)findViewById(R.id.buttonShowMotionSensor);
        buttonShowMotionSensor.setOnClickListener(buttonClickListener);
        buttonShowAirpressureSensor = (Button)findViewById(R.id.buttonShowAirpressureSensor);
        buttonShowAirpressureSensor.setOnClickListener(buttonClickListener);
        buttonSearchGourmet= (Button)findViewById(R.id.buttonSearchGourmet);
        buttonSearchGourmet.setOnClickListener(buttonClickListener);
        buttonTradeCard= (Button)findViewById(R.id.buttonTradeCard);
        buttonTradeCard.setOnClickListener(buttonClickListener);
    }

    public void init_twitter() {
        buttonTweet=(Button)findViewById(R.id.buttonTweet);
        preferences = act.getSharedPreferences(TIMES, Context.MODE_PRIVATE);

        mTwitter = TwitterUtils.getTwitterInstance(act);
        mTweet = new Tweet(this, mTwitter);

        buttonTweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(TIMES, preferences.getInt(TIMES,0) + 1);
                editor.apply();
                mTweet.tweetSearchGourmet();
            }
        });
    }

    //menu関係
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent intent;
        switch (id) {
            case R.id.menuOption_1:
                //設定画面に移行
                intent=new Intent(this,com.cerevo.blueninja.hyourowgan_ble_sample.SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
