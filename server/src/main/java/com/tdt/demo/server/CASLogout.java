package com.tdt.demo.server;

import com.tdt.server.ServerPubSubWorker;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "casLogout", urlPatterns = "/casLogout")
public class CASLogout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Cookie cookie = cookies[0];
            if (cookie.getName().equals("server-cookie")) {
                username = CookieHolder.getNameByCookie(cookie.getValue());
                if (username != null) {
                    CookieHolder.removeCookieByUserName(username);
                }
            }
        }

//        if (username != null) {
            ServerPubSubWorker.getInstance().publishUserOutMsg(username);
//        }

    }
}
