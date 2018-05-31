package com.yinhai.dbcatch.service;

import com.yinhai.dbcatch.util.CommonConn;
import com.yinhai.dbcatch.util.DbcCost;
import com.yinhai.dbcatch.vo.DatasourceVO;
import com.yinhai.dbcatch.vo.EventVO;
import com.yinhai.dbcatch.vo.ResultKV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class EventService {
    @Autowired
    private JdbcTemplate JdbcTemplate;

    public ResultKV[] getDatasource() {
        String getDsSql="select * from DBC_SOURCE_DATABASE";
        List<Map<String, Object>> dss = JdbcTemplate.queryForList(getDsSql);
        ResultKV[] resultKVS = new ResultKV[dss.size()];
        for (int i=0;i<dss.size();i++) {
            ResultKV resultKV = new ResultKV(dss.get(i).get("ds_id").toString(),dss.get(i).get("ds_name").toString());
            resultKVS[i] = resultKV;
        }
        return resultKVS;

    }

    public ResultKV[] getTableName(DatasourceVO dsvo) throws Exception {
        Connection oraConn = getSourceConn(dsvo.getDs_id());
        //获取表名
        Statement st = oraConn.createStatement();
        ResultSet rs = st.executeQuery(DbcCost.ORA_GETTAB_SQL);
        ArrayList<String> tables = new ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString(1));
        }

        ResultKV[] rtables = new ResultKV[tables.size()];
        for (int i=0;i<tables.size();i++) {
            ResultKV rkv = new ResultKV(tables.get(i),tables.get(i));
            rtables[i] = rkv;
        }
        st.close();
        oraConn.close();

        return  rtables;
    }

    public ResultKV[] getTableCol(EventVO evo) throws Exception {
        Connection oraConn = getSourceConn(evo.getDs_id());
        //获取表名
        Statement st = oraConn.createStatement();
        ResultSet rs = st.executeQuery(String.format(DbcCost.ORA_GETCOL_SQL,evo.getTab_name()));
        ArrayList<String> cols = new ArrayList<>();
        while (rs.next()) {
            cols.add(rs.getString(1));
        }
        ResultKV[] rcols = new ResultKV[cols.size()];
        for (int i=0;i<cols.size();i++) {
            ResultKV rkv = new ResultKV(cols.get(i),cols.get(i));
            rcols[i] = rkv;
        }
        st.close();
        oraConn.close();
        return  rcols;
    }

    private Connection getSourceConn(String ds_id) throws Exception {
        //获取数据源信息
        String getDsSql="select * from DBC_SOURCE_DATABASE where ds_id = " + ds_id;
        Map<String, Object> dsi = JdbcTemplate.queryForMap(getDsSql);
        //获取源库连接
        Connection conn;
        if ("1".equals(dsi.get("ds_type").toString())) {//oracle

        conn = CommonConn.getOraConnection(dsi.get("ds_url").toString(),
                    dsi.get("ds_username").toString(), dsi.get("ds_password").toString());
        }else { //mysql
            conn = null;

        }
        return conn;
    }

    public void addEvent(EventVO evo) {
        String colMsg = evo.getMsg_col();
        if (colMsg.length()<1) {
            colMsg = " ";
        }
        String sql = "insert into DBC_EVENT_CFG (evt_id,evt_name,subject,classify,ds_id,tab_name,monitor_ld," +
                "chg_type,monitor_type,chg_old,chg_new,msg_col,bizuser,biztime) values (?,?,?,?,?,?,?,?,?,?,?,?," +
                "'sys',sysdate)";
        JdbcTemplate.update(sql,
                evo.getEvt_id(),
                evo.getEvt_name(),
                evo.getSubject(),
                evo.getClassify(),
                evo.getDs_id(),
                evo.getTab_name(),
                evo.getMonitor_ld(),
                evo.getChg_type(),
                evo.getMonitor_type(),
                evo.getChg_old(),
                evo.getChg_new(),
                colMsg.substring(0,colMsg.length() - 1)
        );
    }

    public List<Map<String, Object>> getAll() {
        String sql = "select a.evt_id,a.evt_name,a.subject,a.classify,a.ds_id,b.ds_name,a.tab_name,a.monitor_ld,\n" +
                "case when a.monitor_ld=1 then '监控整行' else '监控字段' end monitor_ld_desc,a.chg_type,\n" +
                "case a.chg_type when 1 then '插入' when 2 then '修改' when 3 then '删除' end chg_type_desc,\n" +
                "a.monitor_type,case when a.monitor_type=1 then '触发器' else '物化视图日志' end monitor_type_desc,\n" +
                "a.bizuser,a.biztime from DBC_EVENT_CFG a\n" +
                "left join dbc_source_database b on a.ds_id = b.ds_id";
        return JdbcTemplate.queryForList(sql);
    }

    public void deleteEvent(EventVO evo) {
        JdbcTemplate.update("delete from DBC_EVENT_CFG where evt_id = ?", evo.getEvt_id());
        JdbcTemplate.update("delete from DBC_EVENT_SEQ where evt_id = ?", evo.getEvt_id());

    }
}
