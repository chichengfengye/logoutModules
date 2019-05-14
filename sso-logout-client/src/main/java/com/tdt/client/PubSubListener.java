package com.tdt.client;

import com.tdt.dto.UserInfo;
import com.tdt.util.UserInfoUtil;
import redis.clients.jedis.JedisPubSub;

public class PubSubListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        /**
         * "+"号开头是添加
         * "-"号开头是删除
         */
        System.out.println("!: receive message: [" + message + "] in channel [" + channel + "]");
        if (message.startsWith("+")) {
            UserInfo userInfo = UserInfoUtil.getUserInfoFromMessage(message.substring(1));
            //添加到 白名单 和 Map<cookie,username>缓存
            UserAccessManager.getInstance().addCookieUserNotPub(userInfo);

        } else if (message.startsWith("-")) {
            UserInfo userInfo = UserInfoUtil.getUserInfoFromMessage(message.substring(1));
            UserAccessManager.getInstance().removeFromWhiteList(userInfo);
        } else {
            System.out.println("!: could not notified message: "+ message);
        }
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
