package com.tdt.client.redis;

import com.tdt.client.UserAccessManager;
import com.tdt.dto.UserInfo;
import com.tdt.util.UserInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

public class PubSubListener extends JedisPubSub {
    private Logger logger = LoggerFactory.getLogger(PubSubListener.class);

    @Override
    public void onMessage(String channel, String message) {
        /**
         * "+"号开头是添加
         * "-"号开头是删除
         */
        logger.info("!: receive message: [{}] in channel [{}]", message, channel);
        if (message.startsWith("+")) {
            UserInfo userInfo = UserInfoUtil.getUserInfoFromMessage(message.substring(1));
            //添加到 白名单 和 Map<cookie,username>缓存
            UserAccessManager.getInstance().addCookieUserNotPub(userInfo);

        } else if (message.startsWith("-")) {
            UserInfo userInfo = UserInfoUtil.getUserInfoFromMessage(message.substring(1));
            UserAccessManager.getInstance().removeFromWhiteList(userInfo);
        } else {
            logger.info("!: could not recognize message: {} ", message);
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
