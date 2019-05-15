package com.tdt.client;

import com.tdt.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UserAccessFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(UserAccessFilter.class);
    private UserAccessManager userAccessManager;

    public void init(FilterConfig filterConfig) throws ServletException {
        // init redis client to sub
        userAccessManager = UserAccessManager.getInstance();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Cookie localCookie = null;

        //TODO 判断 黑名单 以及 两个cookie
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        localCookie = HttpUtil.getApplicationCookie(request);

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
                    logger.info("没有ticket, 拒绝....");
                    refuseRequest(servletResponse);
                } else {
                    /**
                     * 有ticket，那就是第一次登陆了，也就是
                     * 当前处于从cas server重定向回来的阶段，
                     * 放行
                     */
                    logger.info("第一次登陆，放行....");
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
                logger.info("user two cookie is null,按照第一次处理，cas会重定向的，因此放行....");
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
                logger.info("没有cas cookie，无效请求，拒绝");
                refuseRequest(servletResponse);
            }
            //有cas cookie
            else {
                if (UserAccessManager.getInstance().isAllowedUserByCookie(localCookie.getValue())) {
                    logger.info("在白名单，放行。。。。");
                    servletRequest.setAttribute("accessall-username",UserAccessManager.getInstance().getUserNameByCookie(localCookie.getValue()));
                    goOn(servletRequest, servletResponse, filterChain);
                } else {
                    logger.info("不在白名单，拒绝。。。。");
                    refuseRequest(servletResponse);
                }

            }
        }

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
        return HttpUtil.hasCASCookie(servletRequest);
    }

    private boolean isUserInWhiteList(String cookie) {
        return userAccessManager.isAllowedUserByCookie(cookie);
    }



    public void destroy() {

    }
}
