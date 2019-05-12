package com.tdt.demo.server;

import java.util.ArrayList;
import java.util.HashSet;

public class SessionUtil {
    private static HashSet<String> list = new HashSet<String>();

    public static void addTicket(String ticket) {
        list.add(ticket);
    }

    public static boolean validateTicket(String ticket) {
        return list.contains(ticket);
    }
}
