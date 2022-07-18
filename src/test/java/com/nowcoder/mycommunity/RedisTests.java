package com.nowcoder.mycommunity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-12-21:15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MycommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        // 存数据
        redisTemplate.opsForValue().set(redisKey, 1);
        // 取数据
        Object val = redisTemplate.opsForValue().get(redisKey);
        System.out.println(val);
        // 数据加1
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        // 数据减1
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));

    }

    @Test
    public void testHash() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets() {
        String redisKey = "test:teacher";

        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "屈二", 80);
        redisTemplate.opsForZSet().add(redisKey, "张三", 90);
        redisTemplate.opsForZSet().add(redisKey, "李四", 50);
        redisTemplate.opsForZSet().add(redisKey, "王五", 70);
        redisTemplate.opsForZSet().add(redisKey, "赵六", 60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "张三"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "张三"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "张三"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));

    }

    @Test
    public void testKeys() {
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);


    }

    // 多次访问同一个key，以绑定的形式存在，这样就不用每次调用都传入redisKey。
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations valueOperations = redisTemplate.boundValueOps(redisKey);
        valueOperations.increment();
        valueOperations.increment();
        valueOperations.increment();
        valueOperations.increment();
        valueOperations.increment();
        System.out.println(valueOperations.get());


        BoundSetOperations setOperations = redisTemplate.boundSetOps("test:teacher");
        setOperations.members().forEach(System.out::println);
    }

    // 方法内部处理事务，非声明，因为Redis不像关系型数据库，严格按照ACIO准则，而是统一提交，因此在事务中最好不要有select操作
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                // 启用事务
                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                // 空，查询无效
                System.out.println(operations.opsForSet().members(redisKey));

                // 提交事务
                return operations.exec();
            }
        });

        System.out.println(obj);
    }
}
