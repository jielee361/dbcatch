package com.yinhai.dbcatch.engine;

import com.alibaba.fastjson.JSONObject;
import com.yinhai.dbcatch.po.EventMsgQueue;
import com.yinhai.dbcatch.util.AppContextUtil;
import com.yinhai.dbcatch.vo.EventVO;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class SendExecutor {
    JdbcTemplate jdbcTemplate;
    LinkedBlockingDeque<JSONObject> msgQuue;
    Map<String,List<EventVO>> eventCfg;//配置的事件
    boolean stopFlag = false;

    public void stopSend() {
        this.stopFlag = true;
    }


    /**
     * 启动发送
     * @throws Exception
     */
    public abstract void startSend() throws Exception;

    public void init() {
        jdbcTemplate = AppContextUtil.getBean(JdbcTemplate.class);
        msgQuue = EventMsgQueue.getQueue();
        eventCfg = new HashMap<>();

        List<Map<String, Object>> events = jdbcTemplate.queryForList("select * from DBC_EVENT_CFG");
        for (Map<String, Object> event : events) {
            EventVO eventVO = new EventVO();
            eventVO.setEvt_id(event.get("evt_id").toString());
            eventVO.setEvt_name(event.get("evt_name").toString());
            eventVO.setDs_id(event.get("ds_id").toString());
            eventVO.setTab_name(event.get("tab_name").toString());
            eventVO.setChg_type(event.get("chg_type").toString());
            eventVO.setChg_new(event.get("chg_new") + "");
            eventVO.setChg_old(event.get("chg_old") + "");
            eventVO.setMonitor_ld(event.get("monitor_ld").toString());
            String fullTab = eventVO.getDs_id()+eventVO.getTab_name();
            if (eventCfg.containsKey(fullTab)) {
                eventCfg.get(fullTab).add(eventVO);
            }else {
                List<EventVO> eventLs = new ArrayList<>();
                eventLs.add(eventVO);
                eventCfg.put(fullTab,eventLs);
            }
        }


    }
}
