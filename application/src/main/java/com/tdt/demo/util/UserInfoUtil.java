package com.tdt.demo.util;

import com.tdt.demo.application.UserInfo;

public class UserInfoUtil {
    public static String generatePubMsg(String cookie, String username) {
        return cookie + ":" + username;
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
