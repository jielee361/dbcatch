package com.yinhai.dbcatch.engine;

public class ReadRunnable implements Runnable {
    private ReadExecutor readExecutor;
    private boolean isRunning = false;

    public  ReadRunnable(ReadExecutor readExecutor) {
        this.readExecutor = readExecutor;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void stopRun() throws Exception {
        this.readExecutor.stopRead();
    }

    @Override
    public void run() {
        try {
            isRunning = true;

            readExecutor.startRead();

            isRunning = false;

        }catch (Exception e) {
            isRunning = false;
            e.printStackTrace();

        }

    }
}
