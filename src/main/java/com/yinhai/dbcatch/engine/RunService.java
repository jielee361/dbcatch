package com.yinhai.dbcatch.engine;

import org.springframework.stereotype.Service;

@Service
public interface RunService {
    /**
     * 启动抓取线程，每个数据源启动一个
     * @param dsId 数据源ID
     */
    void startRead(int dsId);

    /**
     *停止抓取线程，
     * @param dsId 数据源ID
     */
    void stopRead(int dsId);

    /**
     * 启动发送线程。
     */
    void startSend();

    /**
     * 停止发送线程
     */
    void stopSend();

}
