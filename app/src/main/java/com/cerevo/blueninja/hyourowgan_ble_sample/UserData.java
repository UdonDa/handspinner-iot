package com.cerevo.blueninja.hyourowgan_ble_sample;

/**
 * Created by wally_nagama on 2017/08/26.
 */

public class UserData {
    int userId;
    String userName;
    String githubId;
    String twitterId;
    String lineId;

    public UserData(String name, String github, String twitter, String line){
        userName = name;
        githubId = github;
        twitterId = twitter;
        lineId = line;
    }
}
