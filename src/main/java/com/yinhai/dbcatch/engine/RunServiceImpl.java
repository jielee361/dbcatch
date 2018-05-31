package com.yinhai.dbcatch.engine;

import com.yinhai.dbcatch.util.DbcCost;
import com.yinhai.dbcatch.util.DbcEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class RunServiceImpl implements RunService {

    private static Map<String,ReadRunnable> readThreadMap;
    @Autowired
    private JdbcTemplate jdbcTemplate;



    @Override
    public void start(String dsId) throws Exception {
        if (readThreadMap == null) {
            readThreadMap = new HashMap<>();
        }
        // start read
        if (readThreadMap.containsKey(dsId) ) {
            ReadRunnable readRunnable = readThreadMap.get(dsId);
            if (readRunnable.isRunning()) {
                throw new Exception("此库的抓取线程还在运行，不能再次启动！");
            }else {
                readThreadMap.remove(dsId);
                startRead(dsId);
            }
        }else {
            startRead(dsId);
        }

    }

    @Override
    public void stop(String dsId) throws Exception {
        if (readThreadMap.containsKey(dsId)) {
            readThreadMap.get(dsId).stopRun();
            jdbcTemplate.update(DbcCost.UPADTE_STAT_SQL,3,"正常停止",Integer.valueOf(dsId));
        }
    }

    private void startRead(String dsId) throws Exception {
        ReadExecutor readExecutor = new OraReadExecutor();
        readExecutor.init(dsId);
        jdbcTemplate.update(DbcCost.UPADTE_STAT_SQL,2,"已启动",Integer.valueOf(dsId));
        ReadRunnable readRunnable = new ReadRunnable(readExecutor);
        DbcEnv.getThreadPool().submit("DBCATCH-" + dsId,readRunnable);
        readThreadMap.put(dsId,readRunnable);
    }
}
