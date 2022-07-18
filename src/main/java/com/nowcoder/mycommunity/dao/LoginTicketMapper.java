package com.nowcoder.mycommunity.dao;

import com.nowcoder.mycommunity.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-08-10:32
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

    // 登录成功插入凭证
    // 利用注解代替xml配置文件
//    @Insert({
//            "insert into login_ticket(user_id,ticket,status,expired) ",
//            "values(#{userId}, #{ticket}, #{status}, #{expired})"})
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    // 根据浏览器传入的唯一标识ticket查找对应的LoginTicket对象
//    @Select({
//            "select id,user_id,ticket,status,expired ",
//            "from login_ticket where ticket = #{ticket}"
//    })
    LoginTicket selectByTicket(String ticket);

    // 修改状态，退出修改状态，登录修改状态
//    @Update({
//            "update login_ticket set status = #{status} ",
//            "where ticket = #{ticket}"
//    })
    int updateStatus(String ticket, int status);

    int deleteLoginTicketByUserId(String ticket, int userId);

}
