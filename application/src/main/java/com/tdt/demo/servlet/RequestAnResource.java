package com.tdt.demo.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "requestReource", urlPatterns = "/resource")
public class RequestAnResource extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/getResource");
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.println("<html>you have got my name, my name is trigger..</html>");
        writer.flush();
        writer.close();
    }
}
