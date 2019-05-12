package com.tdt.client;

import com.tdt.client.UserInfo;

public class UserInfoUtil {
    public static String getUserLoginMsg(String cookie, String username) {
        return "+" + cookie + ":" + username;
    }

    public static UserInfo getUserInfoFromMessage(String subMsg) {
        if (subMsg != null) {
            String[] arr = subMsg.split(":");
            if (arr.length == 2) {
                UserInfo userInfo = new UserInfo();
                userInfo.setCookie(arr[0]);
                userInfo.setUsername(arr[1]);
                return userInfo;
            }
        }

        return null;
    }
}
