package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;
import java.util.Date;


import twitter4j.*;
import twitter4j.auth.AccessToken;

public class Tweet {
    private Twitter mTwitter;
    private Context context;
    String TIMES = "numberOfTweet";
    SharedPreferences preferences;
    Date dTime = new Date();



    public Tweet(Context c, Twitter t) {
        this.context = c;
        this.mTwitter = t;
        preferences = c.getSharedPreferences(TIMES, Context.MODE_PRIVATE);

    }

    public void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                try {
                    //ツイート
                    //mTwitter.updateStatus("人生" + preferences.getInt(TIMES, 1) + "度目のハンドスピナー！！！！ @ " + dTime.toString() +"\n" + " ");
                    mTwitter.updateStatus("テスト");
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    showToast("ツイート成功");
                } else {
                    showToast("ツイート失敗");
                }
            }
        };
        task.execute("unchi");
    }

    private void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
