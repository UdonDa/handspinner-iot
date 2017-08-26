package com.cerevo.blueninja.hyourowgan_ble_sample;

/**
 * Created by wally_nagama on 2017/08/26.
 */

public class UserData {
    int userId = 0;
    String userKey;
    String userName;
    String githubId;
    String twitterId;
    String lineId;
    Double latitude;
    Double longitude;

    public UserData(String myName, String githubId, String twitterId, String lineId, Double latitude, Double longitude){
        this.userName = myName;
        this.githubId = githubId;
        this.twitterId = twitterId;
        this.lineId = lineId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
