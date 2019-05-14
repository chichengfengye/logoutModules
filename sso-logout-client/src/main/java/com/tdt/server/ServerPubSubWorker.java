package com.tdt.server;

import com.tdt.client.RedisProperties;
import com.tdt.util.UserInfoUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class ServerPubSubWorker {
    private Jedis jedis;
    private static Object lock = new Object();
    private static ServerPubSubWorker serverPubSubWorker;
    private static String CHANNEL_NAME = "";


    private ServerPubSubWorker() {
    }

    public static ServerPubSubWorker getInstance() {
        if (serverPubSubWorker == null) {
            synchronized (lock) {
                if (serverPubSubWorker == null) {
                    RedisProperties properties = new RedisProperties("redis.properties");
                    HostAndPort hostAndPort = properties.getHostAndPort();
                    serverPubSubWorker = new ServerPubSubWorker();
                    serverPubSubWorker.jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort());
                    CHANNEL_NAME = properties.getChannelName();
                }
            }
        }
        return serverPubSubWorker;
    }

    public void publishUserOutMsg(String username) {
        System.out.println("==> publish user["+ username +"] out");
        jedis.publish(CHANNEL_NAME, UserInfoUtil.getUserOutMsg(username));
        System.out.println("publish ["+ username +"] out successfully!");
    }

}
