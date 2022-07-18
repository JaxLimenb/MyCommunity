package com.nowcoder.mycommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MycommunityApplication {

	@PostConstruct
	public void init() {
		// 解决Netty启动冲突的问题（Redis和Elasticsearch启动都要依赖Netty）
		// see Netty4Utils.setAvailableProcessors();
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(MycommunityApplication.class, args);
	}

}
