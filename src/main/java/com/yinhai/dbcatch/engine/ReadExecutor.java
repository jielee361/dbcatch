package com.yinhai.dbcatch.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class ReadExecutor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Map<String, Object>> events;
    private Map<String, Object> dsInfo;

    public void init(int dsId) {
        //get all ds event
        events = jdbcTemplate.queryForList("select * from DBC_EVENT_CFG where ds_id = " + dsId);
        dsInfo = jdbcTemplate.queryForMap("select * from DBC_SOURCE_DATABASE where ds_id = " + dsId);
        //check source cfg


    }
    public void startRead() {

    }

    public void stopRead() {

    }
}
