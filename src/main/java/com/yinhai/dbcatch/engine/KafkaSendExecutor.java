package com.yinhai.dbcatch.engine;

import com.alibaba.fastjson.JSONObject;
import com.yinhai.dbcatch.util.DbcCost;
import com.yinhai.dbcatch.util.DbcUtil;
import com.yinhai.dbcatch.vo.EventVO;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Properties;

public class KafkaSendExecutor extends SendExecutor {
    @Override
    public void startSend() throws Exception {
        //init kafa
        String topic= DbcUtil.getProperty("message.queue.topic");
        String kafkaUrl = DbcUtil.getProperty("message.queue.url");
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaUrl);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(props);

        //begin
        String fullTab;
        List<EventVO> tabEvents;
        while (!super.stopFlag) {
            JSONObject chgMsg = super.msgQuue.poll();
            if (chgMsg == null) {
                Thread.sleep(1000);
                continue;
            }
            //match event
            fullTab = chgMsg.getString("dsId") + chgMsg.getString("tabName");
            tabEvents = super.eventCfg.get(fullTab);
            for (EventVO tabEvent : tabEvents) {
                if (tabEvent.getMonitor_ld().equals(DbcCost.MONITOR_ROW)) {//监控整行
                    String chgType = getChgType(tabEvent.getChg_type());
                    if (chgType.equals(chgMsg.getString("opType"))) {
                        //send
                        chgMsg.put("evtId",tabEvent.getEvt_id());
                        producer.send(new ProducerRecord<>(topic,fullTab,chgMsg.toJSONString())).get();
                        System.out.println("已发送："+ chgMsg.toJSONString());

                    }

                }else {//监控字段

                }

            }
            //更新进度节点
            super.jdbcTemplate.update("update DBC_EVENT_SEQ set mlog_seq=? where ds_id=? and tab_name=?",
                    chgMsg.getLong("seq"),chgMsg.getString("dsId"),chgMsg.getString("tabName"));

        }
        producer.close();
    }


    private String getChgType(String intType) {
        if (intType.equals(DbcCost.CHG_TYPE_I)) {
            return "I";
        }else if (intType.equals(DbcCost.CHG_TYPE_U)) {
            return "U";
        }else {
            return "D";
        }

    }
}
