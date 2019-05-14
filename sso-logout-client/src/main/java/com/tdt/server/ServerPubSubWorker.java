package com.tdt.server;

import com.tdt.client.redis.RedisPropertiesLoader;
import com.tdt.util.UserInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class ServerPubSubWorker {
    private Logger logger = LoggerFactory.getLogger(ServerPubSubWorker.class);
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
                    RedisPropertiesLoader properties = new RedisPropertiesLoader("redis.properties");
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
        logger.info("==> publish user[{}] out", username);
        jedis.publish(CHANNEL_NAME, UserInfoUtil.getUserOutMsg(username));
        logger.info("publish [{}] out successfully!", username);
    }

}
