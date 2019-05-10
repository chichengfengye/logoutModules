package com.tdt.demo.redis;

import com.tdt.demo.application.UserAllowedManager;
import com.tdt.demo.application.UserInfo;
import com.tdt.demo.util.UserInfoUtil;
import redis.clients.jedis.JedisPubSub;

public class RedisPubSubListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        System.out.printf("receive message: ["+ message + "] in channel ["+ channel +"]");
        UserInfo userInfo = UserInfoUtil.getUserInfoFromMessage(message);
        //添加到 白名单 和 Map<cookie,username>缓存
        UserAllowedManager.getInstance().addCookieUserFromRedis(userInfo.getCookie(),userInfo.getUsername());
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        super.onSubscribe(channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        super.onUnsubscribe(channel, subscribedChannels);
    }
}
