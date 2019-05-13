package com.tdt.client;

import redis.clients.jedis.JedisPubSub;

public class PubSubListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        /**
         * "+"号开头是添加
         * "-"号开头是删除
         */
        System.out.println("receive message: [" + message + "] in channel [" + channel + "]");
        if (message.startsWith("+")) {
            UserInfo userInfo = UserInfoUtil.getUserInfoFromMessage(message.substring(1));
            //添加到 白名单 和 Map<cookie,username>缓存
            UserAllowedManager.getInstance().addCookieUserNotPub(userInfo.getCookie(), userInfo.getUsername());

        } else if (message.startsWith("-")) {
            String username = message.substring(1);
            UserAllowedManager.getInstance().removeFromWhiteList(username);
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
