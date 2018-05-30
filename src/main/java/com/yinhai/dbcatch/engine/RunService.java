package com.yinhai.dbcatch.engine;

import org.springframework.stereotype.Service;

@Service
public interface RunService {
    /**
     * 启动线程
     * @param dsId 数据源ID
     */
    void start(String dsId) throws Exception;

    /**
     *停止线程，
     * @param dsId 数据源ID
     */
    void stop(String dsId) throws Exception;

}
