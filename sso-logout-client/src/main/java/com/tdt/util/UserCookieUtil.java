package com.tdt.util;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserCookieUtil {
    public static Cookie getApplicationCookie(ServletRequest servletRequest) {
        return getCookieByKey("JSESSIONID", servletRequest);
    }

    public static AttributePrincipal getCASCookie(ServletRequest servletRequest) {
        return retrievePrincipalFromSessionOrRequest(servletRequest);
        //        return getCookieByKey("server-cookie", servletRequest);
    }

    public static boolean hasCASCookie(ServletRequest servletRequest) {
        return retrievePrincipalFromSessionOrRequest(servletRequest) != null;
//                return getCookieByKey("server-cookie", servletRequest) != null;
    }
    /**
     * 黏贴自cas的requestWrapperFilter
     * @param servletRequest
     * @return
     */
    private static AttributePrincipal retrievePrincipalFromSessionOrRequest(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        Assertion assertion = (Assertion) ((Assertion) (session == null ? request.getAttribute("_const_cas_assertion_") : session.getAttribute("_const_cas_assertion_")));
        return assertion == null ? null : assertion.getPrincipal();
    }

    public static Cookie getCookieByKey(String key, ServletRequest servletRequest) {
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
