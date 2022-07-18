package com.nowcoder.mycommunity.dao;

import com.nowcoder.mycommunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-11-11:18
 */
@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量
    int selectLetterUnreadCount(int userId, String conversationId);

    // 增加私信的方法
    int insertMessage(Message message);

    // 更新私信状态
    int updateMessageStatus(List<Integer> ids, int status);

    // 查找系统消息，看是否已存在，避免重复存入
    Message selectSystemMessage(int toId, String conversationId);

    // 查询某个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题下所有通知的数量
    int selectNoticeCount(int userId, String topic);

    // 查询未读的通知数量
    int selectNoticeUnreadCount(int userId, String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
