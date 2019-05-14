package com.tdt.util;

import com.alibaba.fastjson.JSON;
import com.tdt.dto.UserInfo;

public class UserInfoUtil {
    public static String getUserOutMsg(String username) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setInvalidLoginTime(System.currentTimeMillis() / 1000L);
        return "-" + JSON.toJSONString(userInfo);
    }

    public static String getUserLoginMsg(UserInfo userInfo) {
        return "+" + JSON.toJSONString(userInfo);
    }

    /**
     * msg:
     * username-cookie-loginTime=11912312312
     *
     * @param subMsg
     * @return
     */
    public static UserInfo getUserInfoFromMessage(String subMsg) {
        if (subMsg != null) {
            return (UserInfo) JSON.parseObject(subMsg, UserInfo.class);
        }

        return null;
    }

}
