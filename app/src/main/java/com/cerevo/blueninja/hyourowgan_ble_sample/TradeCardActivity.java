package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.content.Intent;
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

public class TradeCardActivity extends AppCompatActivity {
    Intent intent;
    String githubID;
    String twitterID;
    String lineID;
    String myName;
    Button exchangeCard;
    FirebaseDatabase database;
    DatabaseReference myRef;
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_card);

        intent = getIntent();
        githubID = intent.getStringExtra("githubID");
        twitterID = intent.getStringExtra("twitterID");
        lineID = intent.getStringExtra("lineID");
        myName = intent.getStringExtra("myName");

        initTextView();
        userData = new UserData(myName, githubID, twitterID, lineID);
        exchangeCard = (Button) findViewById(R.id.exchange_card);
        exchangeCard.setOnClickListener(buttonClickListener);

        //firebase initialize
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    private void uploadUserData(final DatabaseReference databaseReference, final UserData ud) {
        //部屋に入る時，部屋の人数に合わせてuserIdを決める
        databaseReference.child("User").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    ud.userId = 1;
                    mutableData.setValue(ud);
                } else {
                    int id = mutableData.getValue(int.class) + 1;
                    ud.userId = id;
                    mutableData.setValue(ud);
                }
                /*
                databaseReference.child(user.userKey).child("userId").setValue(user.userId);
                myRef.child(key).child("userName").setValue(user.userName);
                user.nextUserId = 1;
                Log.d("nextUserID", "at 297:: " + user.nextUserId);
                */
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
}
