package com.nowcoder.mycommunity.dao;

import com.nowcoder.mycommunity.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-10-20:08
 */
@Mapper
public interface CommentMapper {

    // 查询当前帖子需要显示的评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentByTargetId(int targetId);

    Comment selectCommentByEntityId(int entityId);
}
