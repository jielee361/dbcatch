package com.yinhai.dbcatch.engine;

public interface ReadExecutor {
    void startRead() throws Exception;
    void stopRead() throws Exception;
}
