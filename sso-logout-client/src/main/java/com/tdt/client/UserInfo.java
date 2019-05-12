package com.tdt.client;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 3882852457399862851L;

    private String cookie;
    private String username;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
