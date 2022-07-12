package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-10-19:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MycommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1() {
//        Object obj = alphaService.save1();
        Object obj = alphaService.save2();
        System.out.println(obj);
    }
}
