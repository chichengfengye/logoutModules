package com.tdt.demo.filter;

import com.tdt.util.HttpUtil;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CASFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CASFilter init....");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String ticket = servletRequest.getParameter("ticket");
        Cookie cookie = HttpUtil.getApplicationCookie(servletRequest);
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
        HttpServletRequest request = ((HttpServletRequest) servletRequest);
        String uri = request.getContextPath();
        String uri1 = request.getRequestURI();
        String uri2 = request.getServletPath();
        String uri3 = request.getLocalAddr();
        String uri4 = request.getRemoteHost();
        String service = request.getParameter("service");

        String uuu = "http://localhost/casLogin?username=%s&pass=%s&service=%s";
        uuu = String.format(uuu, servletRequest.getParameter("username"), servletRequest.getParameter("pass"),service);
        response.sendRedirect(uuu);

    }

    public void destroy() {

    }
}
