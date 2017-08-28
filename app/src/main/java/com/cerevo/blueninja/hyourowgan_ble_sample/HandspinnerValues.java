package com.cerevo.blueninja.hyourowgan_ble_sample;

/**
 * Created by horitadaichi on 2017/08/27.
 */

public class HandspinnerValues {
    HandspinnerValues(){}


    public int mTotalRotat;
    public int mRpm;
    public int mLastPositionStopped;//(0 ~ 7が入る)
    public int mDirectionOfRotation;

    public int mKeyDirectionOfRotation;
    public int mKeyRpm;

    public Double mLat;
    public Double mLng;
    public Double myLat, myLng;

    int stopPos, direction;
    float totalRotate, rpm;

    public void setValues(int sp, int dir, float tr, float rpm){
        stopPos = sp;
        direction = dir;
        totalRotate = tr;
        this.rpm = rpm;
    }

    public void calcidokedo(Double usrLat, Double usrLng){
        mLat = usrLat+(rpm/400)*100*Math.sin((direction/4.0)*Math.PI);
        mLng = usrLng+(rpm/400)*100*Math.cos((direction/4.0)*Math.PI);
    }
}
