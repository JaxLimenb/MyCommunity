package com.nowcoder.mycommunity.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author XiaoXin
 * @Description 从request中获取cookie值
 * @date 2022-07-08-19:30
 */
public class CookieUtil {

    public static String getValueFromCookie(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空，查找cookie内容失败！");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
