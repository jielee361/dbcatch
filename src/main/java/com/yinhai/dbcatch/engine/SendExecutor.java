package com.yinhai.dbcatch.engine;

import com.yinhai.dbcatch.util.AppContextUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SendExecutor {
    private JdbcTemplate jdbcTemplate = AppContextUtil.getBean(JdbcTemplate.class);
    /**
     * 启动发送
     * @throws Exception
     */
    public abstract void startSend() throws Exception;

    public void init() {

    }
}
