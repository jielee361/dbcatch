package com.yinhai.dbcatch.util;

public interface DbcCost {
    String ORA_GETTAB_SQL = "select table_name from user_tables";
    String ORA_GETCOL_SQL = "select column_name,data_type from user_tab_cols where table_name = '%s'";
    String ORA_PK_SQL="select cu.table_name,cu.column_name from user_cons_columns cu, user_constraints au where " +
            "cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and cu.table_name in (%s)";
    String ORA_DORP_MLOG = "drop MATERIALIZED VIEW LOG ON %s";
    String ORA_CREATE_MLOG = "create materialized view log on %s WITH PRIMARY KEY,SEQUENCE (%s) including new values";
    String MLOG_GET_SQL = "select sequence$$,dmltype$$,old_new$$,%s from MLOG$_%s where sequence$$ > %s order by sequence$$";
    String MLOG_DELETE_SQL = "delete from %s.MLOG$_%s where sequence$$ <= %s";

    String DS_TYPE_ORACLE = "1";
    String DS_TYPE_MYSQL = "2";

    String CHG_TYPE_I = "1";
    String CHG_TYPE_U = "2";
    String CHG_TYPE_D = "3";

    String MONITOR_ROW = "1";
    String MONITOR_COL = "2";
}
