package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.cerevo.blueninja.hyourowgan_ble_sample.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class TradeCardActivity extends AppCompatActivity {
    Intent intent;
    String githubID;
    String twitterID;
    String lineID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_card);

        intent = getIntent();
        githubID = intent.getStringExtra("githubID");
        twitterID = intent.getStringExtra("twitterID");
        lineID = intent.getStringExtra("lineID");

        Log.v("pref", githubID + " " + twitterID + " " + lineID);

        initTextView();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }

    private void initTextView(){
        TextView githubView = (TextView)findViewById(R.id.github_id);
        TextView twitterView = (TextView)findViewById(R.id.twitter_id);
        TextView lineView = (TextView)findViewById(R.id.line_id);

        githubView.setText(githubID);
        twitterView.setText(twitterID);
        lineView.setText(lineID);
    }
}
