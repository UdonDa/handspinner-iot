package com.cerevo.blueninja.hyourowgan_ble_sample;

/**
 * Created by horitadaichi on 2017/08/27.
 */

public class HandspinnerValues {
    HandspinnerValues(){}


    public float mTotalRotat;
    public float mRpm;
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

}
