package com.nowcoder.mycommunity.controller.interceptor;

import com.nowcoder.mycommunity.entity.LoginTicket;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.CookieUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-08-19:28
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 在请求开始之初，通过请求找到用户，并将用户暂存到线程对应的对象hostHolder里（线程安全）
     * @param request 请求，包含Cookies信息，里面存有ticket用于提前获取用户信息
     * @param response 响应
     * @param handler 拦截器对象
     * @return 返回true程序向下继续进行
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValueFromCookie(request, "ticket");
        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);
            // 检查凭证是否有效
            // 当凭证不为空、状态未失效、超时时间晚于当前时间
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
//                System.out.println(user);
                // 在当前线程中存入User对象，便于控制器使用
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 在模板引擎之前将user存入model里
     * @param request 请求
     * @param response 响应
     * @param handler 拦截器
     * @param modelAndView 模板引擎
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在模板引擎处理完后将hostHolder清空
     * @param request 请求
     * @param response 响应
     * @param handler 拦截器
     * @param ex 异常
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
