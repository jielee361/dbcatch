package com.yinhai.dbcatch.po;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.LinkedBlockingDeque;

public class EventMsgQueue {
    private static LinkedBlockingDeque<JSONObject> msgQuue;

    public static synchronized LinkedBlockingDeque<JSONObject> getQueue() {
        if (msgQuue == null) {
            msgQuue = new LinkedBlockingDeque<>();
        }
        return msgQuue;
    }
}
