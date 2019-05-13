package com.tdt.server;

import com.tdt.client.RedisProperties;
import com.tdt.client.UserInfoUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class PubSubWorker {
    private Jedis jedis;
    private static Object lock = new Object();
    private static PubSubWorker pubSubWorker;
    private static String CHANNEL_NAME = "";


    private PubSubWorker() {
    }

    public static PubSubWorker getInstance() {
        if (pubSubWorker == null) {
            synchronized (lock) {
                if (pubSubWorker == null) {
                    RedisProperties properties = new RedisProperties("redis.properties");
                    HostAndPort hostAndPort = properties.getHostAndPort();
                    pubSubWorker = new PubSubWorker();
                    pubSubWorker.jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort());
                    CHANNEL_NAME = properties.getChannelName();
                }
            }
        }
        return pubSubWorker;
    }

    public void publishUserOutMsg(String username) {
        jedis.publish(CHANNEL_NAME, UserInfoUtil.getUserOutMsg(username));
    }

}
