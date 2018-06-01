package com.yinhai.dbcatch.engine;

public class SendRunnable implements Runnable{

    private SendExecutor sendExecutor;
    private boolean isRunning = false;

    public SendRunnable(SendExecutor sendExecutor) {
        this.sendExecutor = sendExecutor;
    }

    public boolean isRunning() {
        return isRunning ;
    }

    public void stopSend() {
        sendExecutor.stopSend();
    }

    @Override
    public void run() {
        try {
            isRunning = true;

            sendExecutor.startSend();

            isRunning = false;

        }catch (Exception e) {
            isRunning = false;
            e.printStackTrace();
        }

    }
}
