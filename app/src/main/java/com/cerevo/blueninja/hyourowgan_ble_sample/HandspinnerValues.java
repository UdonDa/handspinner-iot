package com.cerevo.blueninja.hyourowgan_ble_sample;

/**
 * Created by horitadaichi on 2017/08/27.
 */

public class HandspinnerValues {
    HandspinnerValues(){}

    public double mGyroX, mGyroY, mGyroZ;
    public double mAccelX, mAccelY, mAccelZ;
    public double mMagnX, mMagnY, mMagnZ;

    public double mKeyGyroX, mKeyGyroY, mKeyGyroZ;
    public double mKeyAccelX, mKeyAccelY, mKeyAccelZ;
    public double mKeyMagnX, mKeyMagnY, mKeyMagnZ;

    public Double mLat;//ハンドスピナーで超成分の緯度
    public Double mLng;//　経度
    public Boolean mIsFinished;
}
