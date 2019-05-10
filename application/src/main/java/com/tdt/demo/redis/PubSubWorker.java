package com.tdt.demo.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

public class PubSubWorker {
    private boolean flage = true;
    private boolean isSubscribing = false;
    private JedisCluster jedisCluster;
    private Jedis jedis;
    private JedisPubSub pubSubListener;
    private String CHANNEL_NAME;
    private RedisClientSubThread pubSubListenerThread;

    private PubSubWorker() {
    }

    public PubSubWorker(JedisCluster jedisCluster, JedisPubSub pubSubListener, String channel) {
        this.jedisCluster = jedisCluster;
        this.pubSubListener = pubSubListener;
        this.CHANNEL_NAME = channel;
        this.pubSubListenerThread = new RedisClientSubThread();
    }

    public PubSubWorker(Jedis jedis, JedisPubSub pubSubListener, String channel) {
        this.jedis = jedis;
        this.pubSubListener = pubSubListener;
        this.CHANNEL_NAME = channel;
        this.pubSubListenerThread = new RedisClientSubThread();
    }

    public void subscribe() {
      /*  if (!isSubscribing) {
            isSubscribing = true;
            pubSubListenerThread.start();
            System.out.println("=> jedis subscribed....");
        } else {
            System.out.println("=> jedis has been subscribed before....");
        }*/
    }

    public String publish(String message) {
     /*   if (jedisCluster != null && jedis == null) {
            jedisCluster.publish(CHANNEL_NAME, message);
        } else if (jedis != null && jedisCluster == null) {
            jedis.publish(CHANNEL_NAME, message);
        }
*/
        return message;
    }

    private class RedisClientSubThread extends Thread {
        @Override
        public void run() {
            while (flage) {
                if (jedisCluster != null && jedis == null) {
                    jedisCluster.subscribe(pubSubListener, CHANNEL_NAME);
                } else if (jedis != null && jedisCluster == null) {
                    jedis.subscribe(pubSubListener, CHANNEL_NAME);
                }
            }

        }
    }

}
