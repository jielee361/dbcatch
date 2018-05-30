package com.yinhai.dbcatch.service;

import com.yinhai.dbcatch.engine.RunService;
import com.yinhai.dbcatch.vo.DatasourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MonitorService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RunService runService;

    public List<Map<String, Object>> getRunInfo() {
        return jdbcTemplate.queryForList("select ds_id,ds_name," +
                "ds_type,ds_username,run_stat,run_time from DBC_SOURCE_DATABASE");

    }

    public void startRun(DatasourceVO dsvo) throws Exception {
        runService.start(dsvo.getDs_id());
    }

    public void stopRun(DatasourceVO dsvo) throws Exception {
        runService.stop(dsvo.getDs_id());
    }

}
