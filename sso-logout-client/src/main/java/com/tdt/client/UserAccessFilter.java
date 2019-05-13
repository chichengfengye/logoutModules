package com.tdt.client;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UserAccessFilter implements Filter {
    private UserAllowedManager userAllowedManager;

    public void init(FilterConfig filterConfig) throws ServletException {
        // init redis client to sub
        userAllowedManager = UserAllowedManager.getInstance();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Cookie localCookie = null;

        //TODO 判断 黑名单 以及 两个cookie
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        localCookie = HttpUtil.getLocalHostCookie(request);

        /**
         * 没有localCookie，那么就是三种情况：
         * 可能是第一次请求，
         * 可能是第一次cas登陆成功后的重定向，
         * 可能是用户故意删除localcookie
         */
        if (localCookie == null) {
            /**
             * 有cas cookie，那么是上面的两种情况：
             * A. 第一次登陆成功后的重定向
             * B. 用户故意删除local cookie，但是保留cas cookie
             */
            if (hasCASCookie(servletRequest)) {
                String ticket = getTicket(servletRequest);
                if (ticket == null) {
                    /**
                     * 没有ticket，要么是用户故意删除ticket，要么就是
                     * 故意删除local cookie的恶意用户，但不管是哪个，都
                     *  不是正常流程，所以让他重新登陆
                     */
                    System.out.println("没有ticket, 拒绝....");
                    refuseRequest(servletResponse);
                } else {
                    /**
                     * 有ticket，那就是第一次登陆了，也就是
                     * 当前处于从cas server重定向回来的阶段，
                     * 放行
                     */
                    System.out.println("第一次登陆，放行....");
                    goOn(servletRequest, servletResponse, filterChain);
                }
            } else {
                /**
                 * 没有cas cookie，那就有可能是两种情况：
                 * A.用户第一次登陆 或者
                 * B.用户故意删除两个cookie才会到这里
                 * 这两个行为的结果都是需要用户去cas重新登陆。
                 * 所以，这里需要做的就是放行，因为后面的cas客户端的filter就有让用户去cas重新登陆的过滤逻辑
                 */
                System.out.println("user two cookie is null,按照第一次处理，cas会重定向的，因此放行....");
                goOn(servletRequest, servletResponse, filterChain);
            }
        }
        /**
         * 有localCookie，不需要验证该cookie的正确性，因为后边的cas filter会做，
         * 这里只需要做后续的cas cookie判断
         */
        else {
            //没有cas cookie
            if (!hasCASCookie(servletRequest)) {
                //没有cas cookie，无效请求，拒绝
                System.out.println("没有cas cookie，无效请求，拒绝");
                refuseRequest(servletResponse);
            }
            //有cas cookie
            else {
                if (UserAllowedManager.getInstance().isAllowedUserByCookie(localCookie.getValue())) {
                    System.out.println("在白名单，放行。。。。");
                    goOn(servletRequest, servletResponse, filterChain);
                } else {
                    System.out.println("不在白名单，拒绝。。。。");
                    refuseRequest(servletResponse);
                }

            }
        }


     /*   //--本地应用程序的cookie不存在 && CAS的cookie也不存在  cas一定会让他重新登录，也就是按照他是第一次请求，所以放行
        //--就算是伪装故意删除的，也是按照第一次来访问处理，后续的cas会重定向登陆 保证安全
        if (localCookie == null && !hasCASCookie(servletRequest)) {
            System.out.println("cookie=null cascookie=null");
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (localCookie != null && hasCASCookie(servletRequest) && isUserInWhiteList(localCookie.getValue())) {
            System.out.println("cookie!=null cascookie!=null InWhiteList=true");
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            System.out.println("other cookie status or not in whitelist");
            refuseRequest(servletResponse);
        }*/


    }

    private void goOn(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void refuseRequest(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = ((HttpServletResponse)servletResponse);
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");

        PrintWriter out = response.getWriter();
        out.print("{\"code\":\"401\",\"message\":\"your state has been changed, please login (again if you had login) for security\"}");
        out.flush();

    }

    private String getTicket(ServletRequest servletRequest) {
        String ticket = servletRequest.getParameter("ticket");
        return ticket;
    }

    private boolean hasCASCookie(ServletRequest servletRequest) {
        return HttpUtil.getCASCookie(servletRequest) != null;
//        return retrievePrincipalFromSessionOrRequest(servletRequest) != null;
    }

    private boolean isUserInWhiteList(String cookie) {
        return userAllowedManager.isAllowedUserByCookie(cookie);
    }

    /**
     * 黏贴自cas的requestWrapperFilter
     * @param servletRequest
     * @return
     */
/*    private AttributePrincipal retrievePrincipalFromSessionOrRequest(ServletRequest servletRequest) {
      *//*  HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        Assertion assertion = (Assertion) ((Assertion) (session == null ? request.getAttribute("_const_cas_assertion_") : session.getAttribute("_const_cas_assertion_")));
        return assertion == null ? null : assertion.getPrincipal();*//*
    }*/

    public void destroy() {

    }
}
