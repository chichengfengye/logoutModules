package com.tdt.util;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpUtil {
    public static Cookie getApplicationCookie(ServletRequest servletRequest) {
        return getCookieByKey("application-cookie", servletRequest);
    }

    public static Cookie getCASCookie(ServletRequest servletRequest) {
        return getCookieByKey("server-cookie", servletRequest);
    }
    /**
     * 黏贴自cas的requestWrapperFilter
     * @param servletRequest
     * @return
     */
    private AttributePrincipal retrievePrincipalFromSessionOrRequest(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        Assertion assertion = (Assertion) ((Assertion) (session == null ? request.getAttribute("_const_cas_assertion_") : session.getAttribute("_const_cas_assertion_")));
        return assertion == null ? null : assertion.getPrincipal();
    }

    private static Cookie getCookieByKey(String key, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length >0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }

        return null;
    }

}
