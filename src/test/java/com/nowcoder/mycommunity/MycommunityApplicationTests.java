package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.config.AlphaConfig;
import com.nowcoder.mycommunity.dao.AlphaDao;
import com.nowcoder.mycommunity.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MycommunityApplication.class)
class MycommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);
		System.out.println(applicationContext.hashCode());
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
//		AlphaDao alphaDao = applicationContext.getBean("alphaDaoHibernateImpl", AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManagement() {
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);

		System.out.println(simpleDateFormat.format(new Date()));
	}

	// 依赖注入
	@Autowired
	@Qualifier("alphaDaoHibernateImpl")
	private AlphaDao alphaDao;
	@Autowired
	private AlphaService alphaService;
	@Autowired
	private AlphaConfig alphaConfig;

	@Test
	public void testDI() {
		System.out.println(alphaDao);
		System.out.println(alphaDao.select());
		System.out.println(alphaService);
		System.out.println(alphaConfig);

	}
}
