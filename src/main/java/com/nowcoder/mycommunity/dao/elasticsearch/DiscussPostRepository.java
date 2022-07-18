package com.nowcoder.mycommunity.dao.elasticsearch;

import com.nowcoder.mycommunity.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-17-19:35
 * 实现接口，泛型里需要定义类型和主键类型
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
