package com.tdt.demo.server;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class HttpUtil {
    public static Cookie getLocalHostCookie(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getValue().equals("application-cookie")) {
                    return cookie;
                }
            }
        }

        return cookies[0];

    }

    public static Cookie getCASCookie(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("server-cookie")) {
                    return cookie;
                }
            }
        }

        return null;

    }
}
