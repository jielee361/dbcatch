package com.yinhai.dbcatch.engine;

import com.alibaba.fastjson.JSONObject;
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
    private Map<String, List<String>> pkMap;
    private Map<String, List<String>> colsMap;
    private Map<String, String> colStrMap;
    private Map<String, Long> tabMlogSeq;
    private boolean stopFlag = false;
    private LinkedBlockingDeque<JSONObject> msgQuue;
    private int dsId;

    @Override
    public void init(String dsId) throws Exception {
        this.dsId = Integer.valueOf(dsId);
        jdbcTemplate = AppContextUtil.getBean(JdbcTemplate.class);
        msgQuue = EventMsgQueue.getQueue();
        colsMap = new HashMap<>();
        colStrMap = new HashMap<>();
        //get all ds event
        events = jdbcTemplate.queryForList("select * from DBC_EVENT_CFG where ds_id = " + dsId);
        dsInfo = jdbcTemplate.queryForMap("select * from DBC_SOURCE_DATABASE where ds_id = " + dsId);
        //check source cfg
        Connection conn = CommonConn.getOraConnection(dsInfo.get("ds_url").toString(),
                dsInfo.get("ds_username").toString(), dsInfo.get("ds_password").toString());
        Statement st = conn.createStatement();

        //get cols
        String tables = "";
        String cols = "";
        for (Map<String, Object> event : events) {
            String tabName = event.get("tab_name").toString();
            tables = tables + "'" + tabName + "',";
            cols = event.get("msg_col").toString();
            String[] colsA = cols.split(",");
            List<String> colsL = new ArrayList<>();
            for (int i = 0; i < colsA.length; i++) {
                colsL.add(colsA[i]);
            }
            if (colsMap.containsKey(tabName)) {
                colsL.removeAll(colsMap.get(tabName));
                colsMap.get(tabName).addAll(colsL);
            } else {
                colsMap.put(tabName, colsL);
            }

        }
        if (tables.length() < 2) {
            throw new Exception("未获取到任何已配置的事件！");
        }
        //get pk
        tables = tables.substring(0, tables.length() - 1);
        ResultSet rs = st.executeQuery(String.format(DbcCost.ORA_PK_SQL, tables));
        pkMap = new HashMap<>(events.size());
        while (rs.next()) {
            String tableName = rs.getString(1);
            if (pkMap.containsKey(tableName)) {
                pkMap.get(tableName).add(rs.getString(2));
            } else {
                ArrayList<String> tabpk = new ArrayList<>();
                tabpk.add(rs.getString(2));
                pkMap.put(tableName, tabpk);
            }
        }
        rs.close();
        //create mlog
        for (String tname : colsMap.keySet()) {
            List<Map<String, Object>> evtseq = jdbcTemplate.queryForList("select * from DBC_EVENT_SEQ where ds_id=? " +
                    "and tab_name=?", this.dsId, tname);
            List<String> colsL = colsMap.get(tname);
            String colsStr = "";
            for (int m = 0;m<colsL.size(); m++) {
                colsStr = colsStr + colsL.get(m) + ",";
            }
            colsStr = colsStr.substring(0, colsStr.length() - 1);
            colStrMap.put(tname,colsStr);
            if (evtseq.size() == 0) {
                createMlog(tname,colsStr,conn);
                jdbcTemplate.update("insert into DBC_EVENT_SEQ (ds_id,mlog_seq,tab_name,mlog_cols) VALUES (?,?,?,?)",
                        this.dsId, 0, tname, colsStr);
            } else {
                List<String> mlogL = new ArrayList<>();
                String[] mlogA = evtseq.get(0).get("mlog_cols").toString().split(",");
                for (int k = 0; k < mlogA.length; k++) {
                    mlogL.add(mlogA[k]);
                }
                if (!colsL.containsAll(mlogL)) { //有新增字段
                    createMlog(tname,colsStr,conn);
                    jdbcTemplate.update("UPDATE DBC_EVENT_SEQ set mlog_cols=? where ds_id=? and tab_name=?",
                            colsStr,this.dsId,tname);
                }
            }
        }

        //初始化 seq
        tabMlogSeq = new HashMap<>(colsMap.size());
        List<Map<String, Object>> evtseqs = jdbcTemplate.queryForList("select * from DBC_EVENT_SEQ");
        for (Map<String, Object> evtseq : evtseqs) {
            tabMlogSeq.put(evtseq.get("tab_name").toString(), Long.valueOf(evtseq.get("mlog_seq").toString()));
        }
        st.close();
        conn.close();
    }

    private void createMlog(String tableName, String cols, Connection conn) throws Exception {
        List<String> pkl = pkMap.get(tableName);
        if (pkl == null) {
            throw new Exception("未获取到此表的主键:" + tableName);
        }
        for (String pk : pkl) {//主键不能包含在创建MLOG的字段中
            cols = cols.replace(pk + ",", "").replace("," + pk, "");
        }
        Statement st = conn.createStatement();

        try {
            st.execute(String.format(DbcCost.ORA_DORP_MLOG, tableName));
        } catch (Exception e) {
        }
        st.execute(String.format(DbcCost.ORA_CREATE_MLOG, tableName, cols));
        st.close();
    }

    @Override
    public void updateStat(int stat, String runLog) {
        jdbcTemplate.update(DbcCost.UPADTE_STAT_SQL, stat, runLog, Integer.valueOf(dsId));
    }

    @Override
    public void startRead() throws Exception {
        Connection conn = null;
        try {
            conn = CommonConn.getOraConnection(dsInfo.get("ds_url").toString(),
                    dsInfo.get("ds_username").toString(), dsInfo.get("ds_password").toString());
            Statement st = conn.createStatement();
            st.setFetchSize(500);
            int getNum;
            Long seq = 0L;
            List<String> cosList;
            while (!stopFlag) {
                getNum = 0;
                for (String tabName : colsMap.keySet()) {
                    seq = 0L;
                    cosList = colsMap.get(tabName);
                    ResultSet rs = st.executeQuery(String.format(DbcCost.MLOG_GET_SQL, colStrMap.get(tabName),
                            tabName, tabMlogSeq.get(tabName)));
                    int colSize = cosList.size() + 3;
                    while (rs.next()) {
                        JSONObject rowJson = new JSONObject(6);
                        rowJson.put("dsId",this.dsId);
                        rowJson.put("tabName",tabName);
                        seq = rs.getLong(1);
                        rowJson.put("seq",seq);
                        rowJson.put("opType",rs.getString(2));
                        rowJson.put("oldnew",rs.getString(3));
                        JSONObject colJson = new JSONObject(cosList.size());
                        for (int i = 4; i <= colSize; i++) {
                            colJson.put(cosList.get(i - 4),rs.getString(i));
                        }
                        rowJson.put("cols",colJson);
                        //放入队列
                        msgQuue.add(rowJson);
                        System.out.println("抓取到：" + rowJson.toJSONString());
                        getNum++;
                    }
                    rs.close();
                    if (seq > 0L) {
                        tabMlogSeq.put(tabName, seq);
                    }

                }
                if (getNum == 0) {
                    System.out.println("stop 3 second");
                    Thread.sleep(3000);
                }

            }
        } catch (Exception e) {
            throw e;

        } finally {
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
