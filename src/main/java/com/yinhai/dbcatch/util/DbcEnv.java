package com.yinhai.dbcatch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DbcEnv {
    public static ThreadPoolUtil threadPool;//线程池
    public static Map<String,ArrayList<String>> tabCols;//表字段
    public static Map<String,Integer> catchNum;

    public static ThreadPoolUtil getThreadPool() {
        if (threadPool == null) {
            threadPool = ThreadPoolUtil.getThreadPool(8);
        }
        return threadPool;
    }

    public static Map<String,ArrayList<String>> getTabCols() {
        if (tabCols == null) {
            tabCols = new HashMap<>();
        }
        return tabCols;
    }

    public static  Map<String,Integer> getCatchNum() {
        if (catchNum == null) {
            catchNum = new HashMap<>();
        }
        return catchNum;
    }
}
