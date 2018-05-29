package com.yinhai.dbcatch.po;

import java.util.concurrent.LinkedBlockingDeque;

public class EventMsgQueue {
    private static LinkedBlockingDeque<ReadMsg> msgQuue;

    public static LinkedBlockingDeque<ReadMsg> getQueue() {
        if (msgQuue == null) {
            msgQuue = new LinkedBlockingDeque<>();
        }
        return msgQuue;
    }
}
