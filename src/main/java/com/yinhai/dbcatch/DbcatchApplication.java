package com.yinhai.dbcatch;

import com.yinhai.dbcatch.util.AppContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DbcatchApplication {

	public static void main(String[] args) {
		ApplicationContext app = SpringApplication.run(DbcatchApplication.class, args);
		AppContextUtil.setApplicationContext(app);
	}
}
