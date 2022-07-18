package com.nowcoder.mycommunity.controller.interceptor;

import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.MessageService;
import com.nowcoder.mycommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-17-11:15
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            int allUnreadCount = letterUnreadCount + noticeUnreadCount;
            modelAndView.addObject("allUnreadCount", allUnreadCount);
        }
    }
}
