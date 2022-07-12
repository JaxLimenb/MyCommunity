package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.util.MyCommunityUtil;
import com.nowcoder.mycommunity.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-09-21:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MycommunityApplication.class)
public class SensitiveFilterTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "xin🆕赌❤博❤这里可以赌博，可以吸毒，可以开票，可以嫖娼，哈哈哈！❤赌❤博❤";
//        String text = "赌博这里可以赌博，可以吸毒";
        String filterText = sensitiveFilter.filter(text);
        System.out.println(filterText);
    }
}
