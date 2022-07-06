package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.UserMapper;
import com.nowcoder.mycommunity.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-05-21:18
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
