package com.yinhai.dbcatch;

import com.yinhai.dbcatch.constant.DbInitSqlCost;
import com.yinhai.dbcatch.util.AppContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DbcatchApplication {

    private static final Logger logger = LoggerFactory.getLogger(DbcatchApplication.class);

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(DbcatchApplication.class, args);
        AppContextUtil.setApplicationContext(app);
        JdbcTemplate jdbcTemplate = AppContextUtil.getBean(JdbcTemplate.class);
        initDb(jdbcTemplate);
    }

    private static void initDb(JdbcTemplate jdbcTemplate) {

        //数据源表
        String sourceDatabase = "DBC_SOURCE_DATABASE";
        List<Map<String, Object>> ds = jdbcTemplate.queryForList(String.format(DbInitSqlCost.FIND_TABLE_SQL, sourceDatabase));
        if (ds.size() == 0) {
            jdbcTemplate.execute(DbInitSqlCost.CREATE_DS_SQL);
            logger.info("后台表：" + sourceDatabase + " 创建成功！");
        } else {
            logger.info("后台表：" + sourceDatabase + " 已存在，跳过创建！");
        }
    }

}
