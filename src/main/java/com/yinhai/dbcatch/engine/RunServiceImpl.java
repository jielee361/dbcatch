package com.yinhai.dbcatch.engine;

import com.yinhai.dbcatch.util.ThreadPoolUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class RunServiceImpl implements RunService {

    public static Map<String,ReadRunnable> readThreadMap;
    public static ThreadPoolUtil threadPool;

    @Override
    public void start(String dsId) throws Exception {
        if (readThreadMap == null) {
            readThreadMap = new HashMap<>();
        }
        if (threadPool == null) {
            threadPool = ThreadPoolUtil.getThreadPool(8);
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
        }
    }

    private void startRead(String dsId) throws Exception {
        ReadExecutor readExecutor = new OraReadExecutor();
        readExecutor.init(dsId);
        ReadRunnable readRunnable = new ReadRunnable(readExecutor);
        threadPool.submit("DBCATCH-" + dsId,readRunnable);
        readThreadMap.put(dsId,readRunnable);
    }
}
