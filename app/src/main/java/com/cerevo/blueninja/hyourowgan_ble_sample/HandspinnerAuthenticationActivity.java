package com.cerevo.blueninja.hyourowgan_ble_sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HandspinnerAuthenticationActivity extends AppCompatActivity {

    Button buttonHandspinnerAuthentication, buttonGoToMainactivity;
    TextView textViewAuthenication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handspinner_authentication);
        initViews();
    }


    private void initViews() {
        buttonGoToMainactivity = (Button)findViewById(R.id.buttonGoToMainactivity);
        buttonGoToMainactivity.setOnClickListener(buttonClickListener);
        buttonHandspinnerAuthentication = (Button)findViewById(R.id.buttonHandspinnerAuthentication);
        buttonHandspinnerAuthentication.setOnClickListener(buttonClickListener);
        textViewAuthenication = (TextView)findViewById(R.id.textViewAuthenication);
    }

    public View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonGoToMainactivity:
                    finishedAuthentication();
                    break;
                case R.id.buttonHandspinnerAuthentication:
                    /*if(ok) {
                        finishedAuthentication();
                    }*/
            }
        }
    };

    private void finishedAuthentication() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
