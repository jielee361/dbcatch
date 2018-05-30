package com.yinhai.dbcatch.service;

import com.yinhai.dbcatch.vo.DatasourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class DataSourceService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addDb(DatasourceVO dsvo) {
        String sql = "insert into dbc_source_database (ds_id,ds_name,ds_type,ds_url,ds_username," +
                "ds_password,bizuser,biztime) values (SEQ_DBC.NEXTVAL,?,?,?,?,?,'sys',sysdate)";
        jdbcTemplate.update(sql, dsvo.getDs_name(),
                dsvo.getDs_type(),
                dsvo.getDs_url(),
                dsvo.getDs_username(),
                dsvo.getDs_password());

    }

    public List<Map<String, Object>> getAll() {
        return jdbcTemplate.queryForList("select * from DBC_SOURCE_DATABASE");

    }

    public void deleteDb(DatasourceVO dsvo) {
        jdbcTemplate.update("delete from dbc_source_database where ds_id = ?", dsvo.getDs_id());

    }

}
