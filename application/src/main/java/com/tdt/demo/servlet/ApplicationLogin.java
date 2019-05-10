package com.tdt.demo.servlet;

import com.tdt.demo.application.UserAllowedManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet(name = "loginToken", urlPatterns = "/userLogin")
public class ApplicationLogin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String pass = req.getParameter("pass");

        if (username == null || pass == null) {
            PrintWriter printWriter = resp.getWriter();
            printWriter.println("<html> username and pass required</html>");

            printWriter.flush();
            printWriter.close();
        } else {
            String sessionId = username + "-" + pass + "-" + UUID.randomUUID().toString();

            Cookie cookie = new Cookie("application-cookie", sessionId);
            resp.addCookie(cookie);
            resp.setContentType("text/html");

            UserAllowedManager.getInstance().addCookieUserAndPub(sessionId, username);

            PrintWriter printWriter = resp.getWriter();
            printWriter.println("<html> has set application cookie</html>");

            printWriter.flush();
            printWriter.close();
        }


    }

    @Override
    public void init() throws ServletException {
        super.init();
    }
}
