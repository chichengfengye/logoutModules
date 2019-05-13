package com.tdt.client;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class UserAccessManager {
    private static Object lock = new Object();
    private static UserAccessManager userAccessManager;
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

    private UserAccessManager() {
    }

    /*public static UserAccessManager initialzerInit() {
        userAccessManager = new UserAccessManager();
        userAccessManager.init();
        return userAccessManager;
    }*/

    public static UserAccessManager getInstance() {
        if (userAccessManager == null) {
            synchronized (lock) {
                if (userAccessManager == null) {
                    userAccessManager = new UserAccessManager();
                    userAccessManager.init();
                }
            }
        }
        return userAccessManager;
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
        pubSubWorker.publish(UserInfoUtil.getUserLoginMsg(userInfo));
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
     */
    public void removeFromWhiteList(UserInfo userInfo) {
        long invalidTime = userInfo.getInvalidLoginTime();
        System.out.println("remove user[" + userInfo.getUsername() + "] from whiteList...");
        boolean removeUserFromList = true;

        /**
         * 先獲取該用戶對應的全部cookie已經登陸時間信息，然後依據修改密碼后的時間來
         * 判斷用戶是否有需要保留的cookie，如果沒有，則刪除用戶的名字，如果有，則保留
         * 用戶名字和對應的那個一cookie在白名單中。
         */
        CopyOnWriteArraySet<String> set = whiteUserList.get(userInfo.getUsername());
        //清除緩存中的無用cookie，這裏衹是爲了釋放緩存，不存在安全問題，安全問題已經藉助whiteUserList完成了
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String s = (String)iterator.next();
            String username = cookieUserNameMap.get(s);
            if (username == null) {
                cookieUserNameMap.remove(s);
            } else {
                //刪除在修改狀態之前登陸的cookie
                long loginTime = Long.parseLong(username.split("loginTime=")[1]);
                if (invalidTime >= loginTime) {
                    cookieUserNameMap.remove(s);
                    set.remove(s);
                } else {
                    //保留修改之後登陸的新的cookie
                    removeUserFromList = false;
                }
            }
        }

        if (removeUserFromList) {
            whiteUserList.remove(userInfo.getUsername());
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
        username = username.split("-loginTime=")[0];

        boolean inWhiteList = whiteUserList.containsKey(username);
        if (!inWhiteList) {
            cookieUserNameMap.remove(cookie);
        }
        return inWhiteList;

    }

}
