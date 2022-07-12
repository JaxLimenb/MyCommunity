package com.nowcoder.mycommunity.util;

import com.nowcoder.mycommunity.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-08-19:47
 * 作用：持有用户信息，用于代替session对象。
 */
@Component
public class HostHolder {

    // 通过线程工具保证线程安全
    // 所有的存、取、清理都是线程安全的
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
