package com.nowcoder.mycommunity.dao;

import org.springframework.stereotype.Repository;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-03-22:15
 */
@Repository
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
