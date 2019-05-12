package com.tdt.demo.filter;

import com.tdt.client.HttpUtil;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CASFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CASFilter init....");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String ticket = servletRequest.getParameter("ticket");
        Cookie cookie = HttpUtil.getLocalHostCookie(servletRequest);
        if (cookie == null) {
            if (ticket == null) {
                redirectToCASServer(servletRequest, servletResponse);
            } else {
                if (validateTicket(ticket)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
                redirectToCASServer(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);

        }
    }

    private boolean validateTicket(String ticket) {
//        HttpClient httpClient = new HttpClient();
//        HttpGet
        return true;

    }

    private void redirectToCASServer(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.sendRedirect("http://localhost/casLogin?username="+ servletRequest.getParameter("username") +"&pass="+servletRequest.getParameter("pass"));

    }

    public void destroy() {

    }
}
