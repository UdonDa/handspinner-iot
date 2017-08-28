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
}
