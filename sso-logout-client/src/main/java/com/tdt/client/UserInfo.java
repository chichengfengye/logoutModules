package com.tdt.client;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 3882852457399862851L;

    private String cookie;
    private String username;
    private Long loginTime;
    private Long ssoLogoutTime;

    public UserInfo() {

    }

    public UserInfo(String cookie, String username, long loginTime) {
        this.cookie = cookie;
        this.username = username;
        this.loginTime = loginTime;
    }


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

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getSsoLogoutTime() {
        return ssoLogoutTime;
    }

    public void setSsoLogoutTime(Long ssoLogoutTime) {
        this.ssoLogoutTime = ssoLogoutTime;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
