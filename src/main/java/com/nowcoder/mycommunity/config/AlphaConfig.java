package com.nowcoder.mycommunity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-03-22:37
 * 当外部传入其他类时（项目组其他成员写的类）
 * 此时不好去修改类，为其添加注解，如何通过SpringIOC获取这些类
 * 1.创建配置类AlphaConfig，通过配置类来获取其他类
 * 2.使用@Bean注解，创建获取其他类的方法，
 *      返回值类型和方法名都必须是获取类的类名
 *
 */
@Configuration
public class AlphaConfig {

    // 方法名必须以类名来命名，才能获取Bean对象实例
    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
