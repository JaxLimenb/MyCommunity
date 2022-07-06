package com.nowcoder.mycommunity.dao;

import com.nowcoder.mycommunity.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-05-17:13
 * SQL语句在配置文件中写，Mybatis会自动生成对应的实现类来进行数据库读写操作
 *
 * 步骤：
 * 1.定义针对数据库中某一个表操作的通用方法（UserMapper操作user表）
 * 2.在entity中定义该表对应的类，根据字段声明相应的成员变量的名，及get和set和toString方法
 * 3.在resources的mapper中创建对应表的配置文件，写sql语句，完善接口中对应的方法。
 * 4.使用IOC容器自动装配获取对应接口的对象，利用对象操作数据库中相应的表
 *
 */
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    // 返回的int为数据库修改（操作）的行数
    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
