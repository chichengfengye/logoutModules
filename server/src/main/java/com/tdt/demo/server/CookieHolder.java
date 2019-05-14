package com.tdt.demo.server;

import com.tdt.util.UserInfoUtil;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CookieHolder {
    private static Map<String, String> cookieName = new HashMap<String, String>();
    private static Map<String, Set<String>> nameCookie = new HashMap<String, Set<String>>();
    private static Jedis jedis = new Jedis("192.168.230.4", 7000);
    private static String CHANNEL_NAME = "session_notification";

    public static void publishUserOut(String username) {
        jedis.publish(CHANNEL_NAME, UserInfoUtil.getUserOutMsg(username));
    }

    public static void addCookie(String cookie, String username) {
        cookieName.put(cookie, username);

        Set<String> set = nameCookie.get(username);
        if (set == null) {
            set = new HashSet<String>();
            nameCookie.put(username, set);
        }
        set.add(cookie);

        System.out.println("add cookie for user successfully! [" + cookie + "/" + username + "]");
    }

    public static void removeCookieByUserName(String username) {
        Set<String> cookieSet = nameCookie.remove(username);
        for (String s : cookieSet) {
            cookieName.remove(s);
        }
    }

    public static String removeCookieByCookie(String cookie) {
        String name = cookieName.remove(cookie);
        nameCookie.remove(name);
        return name;
    }

//    public static String getCookieByUserName(String username) {
//        return nameCookie.get(username);
//    }

    public static String getNameByCookie(String cookie) {
        return cookieName.get(cookie);
    }
}
