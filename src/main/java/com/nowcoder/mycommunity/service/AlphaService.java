package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-03-22:28
 * 在实际开发中，一般
 * 使用Controller控制Service，
 * 使用Service调用Dao
 *
 */
@Service
@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService() {
//        System.out.println("实例化AlphaService过程");
    }

    @PostConstruct
    public void init() {
//        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    // 模拟实现查询的业务
    public String findDao() {
        return "findDao result: " + alphaDao.select();
    }
}
