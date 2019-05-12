package com.tdt.client;

import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserAllowedManager {
    private static Object lock = new Object();
    private static UserAllowedManager userAllowedManager;
    private PubSubWorker pubSubWorker;
    /**
     * cookieUserNameMap 数据内容:
     * {
     * cookie: username,
     * .....
     * }
     */
    private ConcurrentHashMap<String, String> cookieUserNameMap;

    /**
     * 白名单，在里面的用户是可以访问接口的
     */
    private Set<String> whiteUserList;

    private UserAllowedManager() {
    }

    /*public static UserAllowedManager initialzerInit() {
        userAllowedManager = new UserAllowedManager();
        userAllowedManager.init();
        return userAllowedManager;
    }*/

    public static UserAllowedManager getInstance() {
        if (userAllowedManager == null) {
            synchronized (lock) {
                if (userAllowedManager == null) {
                    userAllowedManager = new UserAllowedManager();
                    userAllowedManager.init();
                }
            }
        }
        return userAllowedManager;
    }

    public void init() {
        cookieUserNameMap = new ConcurrentHashMap<String, String>();
        whiteUserList = new HashSet<String>();

        Jedis jedisSubscriber = new Jedis("192.168.171.3", 7000);
        Jedis jedisPublisher = new Jedis("192.168.171.3", 7000);
        pubSubWorker = new PubSubWorker(jedisSubscriber,jedisPublisher,
                new RedisPubSubListener(),
                "session_notification");
      /*  pubSubWorker = new PubSubWorker(new JedisCluster(new HostAndPort("1234", 8080)),
                new RedisPubSubListener(),
                "channel");*/
        pubSubWorker.subscribe();
    }

    public void addCookieUserAndPub(String cookie, String username) {
        addCookieUserFromRedis(cookie, username);
        System.out.println("begin pub to redis...");
        pubSubWorker.publish(UserInfoUtil.getUserLoginMsg(cookie, username));
        System.out.println("pub finished...");
    }

    public void addCookieUserFromRedis(String cookie, String username) {
        System.out.println("add user to whiteList and cookieMap...");
        cookieUserNameMap.put(cookie, username);
        whiteUserList.add(username);
    }

    /**
     * 移除用户从白名单，意思就是他无权访问接口
     *
     * @param username
     */
    public void removeFromWhiteList(String username) {
        System.out.println("remove user[" + username + "] from whiteList...");
        whiteUserList.remove(username);
    }

    /**
     * 依据cookie判断用户是否可以访问接口
     *
     * @param cookie
     * @return
     */
    public boolean isUserAllowedByCookie(String cookie) {
        String username = cookieUserNameMap.get(cookie);
        if (username == null) {
            return false;
        }

        boolean inWhiteList = whiteUserList.contains(username);
        if (!inWhiteList) {
            cookieUserNameMap.remove(cookie);
        }
        return inWhiteList;

    }

}
