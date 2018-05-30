package com.yinhai.dbcatch.engine;

import com.yinhai.dbcatch.po.EventMsgQueue;
import com.yinhai.dbcatch.po.ReadMsg;
import com.yinhai.dbcatch.util.AppContextUtil;
import com.yinhai.dbcatch.util.CommonConn;
import com.yinhai.dbcatch.util.DbcCost;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class OraReadExecutor implements ReadExecutor {

    private JdbcTemplate jdbcTemplate;

    private List<Map<String, Object>> events;
    private Map<String, Object> dsInfo;
    private Map<String,List<String>> pkMap;
    private Map<String,Long> tabMlogSeq;
    private boolean stopFlag = false;
    private LinkedBlockingDeque<ReadMsg> msgQuue;
    @Override
    public void init(String dsId) throws Exception {
        jdbcTemplate = AppContextUtil.getBean(JdbcTemplate.class);
        msgQuue = EventMsgQueue.getQueue();
        //get all ds event
        events = jdbcTemplate.queryForList("select * from DBC_EVENT_CFG where ds_id = " + dsId);
        dsInfo = jdbcTemplate.queryForMap("select * from DBC_SOURCE_DATABASE where ds_id = " + dsId);
        //check source cfg
        Connection conn = CommonConn.getOraConnection(dsInfo.get("ds_url").toString() ,
                dsInfo.get("ds_username").toString(), dsInfo.get("ds_password").toString());
        Statement st = conn.createStatement();
        //get pk
        String tables = "";
        for (Map<String, Object> event : events) {
            tables = tables + "'"+event.get("tab_name").toString() + "',";
        }
        if (tables.length() < 2 ) {
            throw new Exception("未获取到任何已配置的事件！");
        }
        tables = tables.substring(0,tables.length() - 1);
        ResultSet rs = st.executeQuery(String.format(DbcCost.ORA_PK_SQL, tables));
        pkMap = new HashMap<>(events.size());
        while (rs.next()) {
            String tableName = rs.getString(1);
            if (pkMap.containsKey(tableName)) {
                pkMap.get(tableName).add(rs.getString(2));
            }else {
                ArrayList<String> tabpk = new ArrayList<>();
                tabpk.add(rs.getString(2));
                pkMap.put(tableName,tabpk);
            }
        }
        rs.close();
        //create mlog
        for (Map<String, Object> event : events) {
            String tableName = event.get("tab_name").toString();
            List<Map<String, Object>> evtseq = jdbcTemplate.queryForList("select * from DBC_EVENT_SEQ where evt_id = '"
                    + event.get("evt_id").toString() + "'");
            if (evtseq.size() == 0) {//如果第一此启动，要创建MLOG
                List<String> pkl = pkMap.get(tableName);
                if (pkl == null) {
                    throw new Exception("未获取到此表的主键:" + tableName);
                }
                String mlogCols = event.get("msg_col").toString();
                for (String pk : pkl) {//主键不能包含在创建MLOG的字段中
                    mlogCols = mlogCols.replace(pk + ",","").replace("," + pk,"");
                }
                try {
                    st.execute(String.format(DbcCost.ORA_DORP_MLOG,tableName));
                }catch (Exception e) {
                }
                st.execute(String.format(DbcCost.ORA_CREATE_MLOG,tableName,mlogCols));
                jdbcTemplate.update("insert into DBC_EVENT_SEQ (evt_id,mlog_seq,tab_name) VALUES (?,?,?)",
                        event.get("evt_id").toString(),0,tableName);

            }
        }
        //初始化 seq
        tabMlogSeq = new HashMap<>(events.size());
        List<Map<String, Object>> evtseqs = jdbcTemplate.queryForList("select * from DBC_EVENT_SEQ");
        for (Map<String, Object> evtseq : evtseqs) {
            tabMlogSeq.put(evtseq.get("tab_name").toString(),Long.valueOf(evtseq.get("mlog_seq").toString()));
        }
        st.close();
        conn.close();
    }
    @Override
    public void startRead() throws Exception {
        Connection conn = null;
        try {
            conn = CommonConn.getOraConnection(dsInfo.get("ds_url").toString() ,
                    dsInfo.get("ds_username").toString(), dsInfo.get("ds_password").toString());
            Statement st = conn.createStatement();
            st.setFetchSize(500);
            int getNum;
            Long seq = 0L;
            while (!stopFlag) {
                getNum = 0;
                for (Map<String, Object> event : events) {
                    seq = 0L;
                    String tabName = event.get("tab_name").toString();
                    String msgCol = event.get("msg_col").toString();
                    int colNum = msgCol.split(",").length + 3;
                    ResultSet rs = st.executeQuery(String.format(DbcCost.MLOG_GET_SQL, msgCol,
                            tabName, tabMlogSeq.get(tabName)));
                    while (rs.next()) {
                        List<String> colValues = new ArrayList<>(colNum);
                        for (int i=1;i<=colNum;i++ ) {
                            colValues.add(rs.getString(i));
                        }
                        seq = rs.getLong(1);
                        ReadMsg readMsg = new ReadMsg();
                        readMsg.setTabName(tabName);
                        readMsg.setColValue(colValues);
                        //放入队列
                        msgQuue.add(readMsg);
                        System.out.println(readMsg.toString());
                        getNum ++;
                    }
                    rs.close();
                    if (seq > 0L) {
                        tabMlogSeq.put(tabName,seq);
                    }

                }
                if (getNum == 0) {
                    System.out.println("stop 3 second");
                    Thread.sleep(3000);
                }

            }
        }catch (Exception e) {
            throw e;

        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    public void stopRead() {
        this.stopFlag = true;
    }

}
