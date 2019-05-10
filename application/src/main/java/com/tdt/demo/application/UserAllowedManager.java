package com.tdt.demo.application;

import com.tdt.demo.util.UserInfoUtil;
import com.tdt.demo.redis.PubSubWorker;
import com.tdt.demo.redis.RedisPubSubListener;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.*;

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
    private HashMap<String, String> cookieUserNameMap;

    /**
     * 白名单，在里面的用户是可以访问接口的
     */
    private Set<String> whiteUserList;

    private UserAllowedManager() {
    }

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
        cookieUserNameMap = new HashMap<String, String>();
        whiteUserList = new HashSet<String>();
        pubSubWorker = new PubSubWorker(new JedisCluster(new HostAndPort("1234", 8080)),
                new RedisPubSubListener(),
                "channel");
        pubSubWorker.subscribe();
    }

    public void addCookieUserAndPub(String cookie, String username) {
        addCookieUserFromRedis(cookie, username);
        pubSubWorker.publish(UserInfoUtil.generatePubMsg(cookie, username));
    }

    public void addCookieUserFromRedis(String cookie, String username) {
        cookieUserNameMap.put(cookie, username);
        whiteUserList.add(username);
    }

    /**
     * 添加用户到白名单，意思就是他有权访问接口
     *
     * @param username
     */
    public void removeFromWhiteList(String username) {
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
        return whiteUserList.contains(username);
    }

}
