package com.tdt.demo.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet(name = "getCASToken", urlPatterns = "/casLogin")
public class CASLogin extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("casLogin servlet init....");
        super.init();
    }

    @Override
    public void init() throws ServletException {


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("============== getCASToken ==========");

        String sessionId = "server-" + UUID.randomUUID().toString();

        Cookie cookie = new Cookie("server-cookie",sessionId);

        CookieHolder.addCookie(cookie.getValue(), req.getParameter("username"));

        resp.addCookie(cookie);

        System.out.println(" resp.sendRedirect(\"http://localhost:8081/?ticket=\"" + sessionId);

        String uri = req.getRequestURI();



        resp.sendRedirect("http://localhost:"+ req.getParameter("service") +"/userLogin?username="+ req.getParameter("username") +"&pass="+ req.getParameter("pass") +"&ticket=" + sessionId);

//        resp.setContentType("text/html");
//
//        PrintWriter printWriter = resp.getWriter();
//        printWriter.println("<html> has set cookie</html>");
//
//        printWriter.flush();
//        printWriter.close();
    }
}
