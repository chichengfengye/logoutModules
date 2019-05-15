package com.tdt.client.redis;

import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import org.slf4j.Logger;

public class ClientPubSubWorker {
    private Logger logger = LoggerFactory.getLogger(ClientPubSubWorker.class);

    private JedisCluster jedisCluster;
    private Jedis jedisSubscriber;
    private JedisPool jedisPublisher;
    private JedisPubSub pubSubListener;
    private boolean isSubscribing = false;
    private String CHANNEL_NAME;
    private RedisClientSubThread pubSubListenerThread;

    private ClientPubSubWorker() {
    }

/*    public ServerPubSubWorker(JedisCluster jedisCluster , JedisPubSub pubSubListener, String channel) {
        this.jedisCluster = jedisCluster;
        this.pubSubListener = pubSubListener;
        this.CHANNEL_NAME = channel;
        this.pubSubListenerThread = new RedisClientSubThread();
    }*/

    public ClientPubSubWorker(RedisPropertiesLoader redisPropertiesLoader, JedisPubSub pubSubListener) {
        this.pubSubListener = pubSubListener;
        this.CHANNEL_NAME = redisPropertiesLoader.getChannelName();
        this.pubSubListenerThread = new RedisClientSubThread();
        this.jedisSubscriber = new Jedis(redisPropertiesLoader.getHostAndPort().getHost(), redisPropertiesLoader.getHostAndPort().getPort());

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisPropertiesLoader.getMaxIdle());
        config.setMaxTotal(redisPropertiesLoader.getMaxTotal());
        config.setMinIdle(redisPropertiesLoader.getMinIdle());
        config.setMaxWaitMillis(redisPropertiesLoader.getMaxWait());
        config.setTestOnBorrow(redisPropertiesLoader.isTestOnBorrow());
        this.jedisPublisher = new JedisPool(config, redisPropertiesLoader.getHostAndPort().getHost(), redisPropertiesLoader.getHostAndPort().getPort());

    }

    public void subscribe() {
        if (!isSubscribing) {
            isSubscribing = true;
            pubSubListenerThread.start();
            logger.info("==> subscriber subscribe channel[ {} ] successfully!", CHANNEL_NAME);
        } else {
            logger.info("!: subscriber has subscribed a channel before....");
        }
    }

    public String publish(String message) {
        Jedis jedis = null;
        try {
            if (jedisPublisher != null && jedisCluster == null) {
                jedis = jedisPublisher.getResource();
                jedis.publish(CHANNEL_NAME, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e1) {
                    e.printStackTrace();
                    e.printStackTrace();
                }
            }
        }

        return message;
    }

    private class RedisClientSubThread extends Thread {
        private int maxException = 10;

        @Override
        public void run() {
            while (maxException <= 10) {
                try {
                    if (jedisCluster != null && jedisSubscriber == null) {
                        jedisCluster.subscribe(pubSubListener, CHANNEL_NAME);
                    } else if (jedisSubscriber != null && jedisCluster == null) {
                        jedisSubscriber.subscribe(pubSubListener, CHANNEL_NAME);
                        maxException++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
