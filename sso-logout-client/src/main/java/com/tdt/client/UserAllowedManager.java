package com.tdt.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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
    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> whiteUserList;

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
        whiteUserList = new ConcurrentHashMap<String, CopyOnWriteArraySet<String>>();

        pubSubWorker = new PubSubWorker(new RedisProperties("redis.properties"),
                new PubSubListener(),
                "session_notification");
      /*  pubSubWorker = new PubSubWorker(new JedisCluster(new HostAndPort("1234", 8080)),
                new PubSubListener(),
                "channel");*/
        pubSubWorker.subscribe();
    }

    public void addCookieUserAndPub(String cookie, String username) {
        long loginTime = System.currentTimeMillis() / 1000;
        UserInfo userInfo = new UserInfo(cookie, username, loginTime);

        addCookieUserNotPub(userInfo);
        System.out.println("begin pub to redis...");
        pubSubWorker.publish(UserInfoUtil.getUserLoginMsg(cookie, username, loginTime));
        System.out.println("pub finished...");
    }

    public void addCookieUserNotPub(UserInfo userInfo) {
        System.out.println("add user to whiteList and cookieMap...");

        String username = userInfo.getUsername();
        long loginTime = userInfo.getLoginTime();
        String cookie = userInfo.getCookie();

        cookieUserNameMap.put(cookie, username + "-loginTime=" + loginTime);
        CopyOnWriteArraySet<String> set = whiteUserList.get(username);
        if (set == null) {
            synchronized (whiteUserList) {
                set = whiteUserList.get(username);
                if (set == null) {
                    set = new CopyOnWriteArraySet<String>();
                    whiteUserList.put(username, set);
                }
            }
        }

        set.add(cookie);
    }

    /**
     * 移除用户从白名单，意思就是他无权访问接口
     *
     * @param username
     */
    public void removeFromWhiteList(String username) {
        System.out.println("remove user[" + username + "] from whiteList...");
        CopyOnWriteArraySet<String> set = whiteUserList.remove(username);
        //清除緩存中的無用cookie，這裏衹是爲了釋放緩存，不存在安全問題，安全問題已經藉助whiteUserList完成了
        for (String s : set) {
            cookieUserNameMap.remove(s);
        }
    }

    /**
     * 依据cookie判断用户是否可以访问接口
     *
     * @param cookie
     * @return
     */
    public boolean isAllowedUserByCookie(String cookie) {
        String username = cookieUserNameMap.get(cookie);
        if (username == null) {
            return false;
        }

        boolean inWhiteList = whiteUserList.contains(username.split("-loginTime=")[1]);
        if (!inWhiteList) {
            cookieUserNameMap.remove(cookie);
        }
        return inWhiteList;

    }

}
