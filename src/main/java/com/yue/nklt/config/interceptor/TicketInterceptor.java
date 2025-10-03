package com.yue.nklt.config.interceptor;

import com.yue.nklt.entity.LoginTicket;
import com.yue.nklt.entity.User;
import com.yue.nklt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 从请求头 Authorization: Bearer {ticket} 中解析登录用户，并放入 request attribute
 */
@Component
public class TicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String ticket = auth.substring(7);
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new java.util.Date())) {
                request.setAttribute("userId", loginTicket.getUserId());
                User user = userService.findUserById(loginTicket.getUserId());
                request.setAttribute("loginUser", user);
            }
        }
        return true;
    }
}


