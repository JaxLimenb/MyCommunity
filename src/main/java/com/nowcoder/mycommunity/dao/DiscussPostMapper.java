package com.nowcoder.mycommunity.dao;

import com.nowcoder.mycommunity.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-05-20:40
 */
@Mapper
public interface DiscussPostMapper {

    /**
     * 查询数据库中的帖子，显示在首页
     * @param userId 根据userId来查，如果存在则加入到sql语句中，不存在默认为0
     * @param offset sql语句中LIMIT的第一项，从第几条数据开始查
     * @param limit sql语句中LIMIT的第二项，每页显示几条数据
     * @return 返回查询得到的帖子集合
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询数据库中帖子表的行数
     * @param userId 根据userId来查，动态传入
     * @return 返回表的行数
     * 如果需要动态添加条件，即这里的userId是动态传入的，可以不传，
     * 且该方法的只存在一个参数时，需要添加注解@Param("别名")，否则会报错
     */
    int selectDiscussPostRows(@Param("userId") int userId);

}
