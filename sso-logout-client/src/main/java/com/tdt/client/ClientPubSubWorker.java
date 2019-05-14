package com.tdt.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

public class ClientPubSubWorker {
    private RedisProperties redisProperties;
    private boolean flage = true;
    private boolean isSubscribing = false;
    private JedisCluster jedisCluster;
    private Jedis jedisSubscriber;
    private Jedis jedisPublisher;
    private JedisPubSub pubSubListener;
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

    public ClientPubSubWorker(RedisProperties redisProperties, JedisPubSub pubSubListener, String channel) {
        this.jedisSubscriber = new Jedis(redisProperties.getHostAndPort().getHost(),redisProperties.getHostAndPort().getPort());
        this.jedisPublisher = new Jedis(redisProperties.getHostAndPort().getHost(),redisProperties.getHostAndPort().getPort());
        this.pubSubListener = pubSubListener;
        this.CHANNEL_NAME = channel;
        this.pubSubListenerThread = new RedisClientSubThread();
    }

    public void subscribe() {
        if (!isSubscribing) {
            isSubscribing = true;
            pubSubListenerThread.start();
            System.out.println("=> jedisSubscriber subscribed....");
        } else {
            System.out.println("=> jedisSubscriber has been subscribed before....");
        }
    }

    public String publish(String message) {
        try {
            if (jedisCluster != null && jedisPublisher == null) {
                jedisCluster.publish(CHANNEL_NAME, message);
            } else if (jedisPublisher != null && jedisCluster == null) {
                jedisPublisher.publish(CHANNEL_NAME, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    private class RedisClientSubThread extends Thread {
        @Override
        public void run() {
            while (flage) {
                try {
                    if (jedisCluster != null && jedisSubscriber == null) {
                        jedisCluster.subscribe(pubSubListener, CHANNEL_NAME);
                    } else if (jedisSubscriber != null && jedisCluster == null) {
                        jedisSubscriber.subscribe(pubSubListener, CHANNEL_NAME);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
