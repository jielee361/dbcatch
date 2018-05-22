package com.yinhai.dbcatch.util;

public interface DbcCost {
    String ORA_GETTAB_SQL = "select table_name from user_tables";
    String ORA_GETCOL_SQL = "select column_name,data_type from user_tab_cols where table_name = '%s'";

    String DS_TYPE_ORACLE = "1";
    String DS_TYPE_MYSQL = "2";

    String CHG_TYPE_I = "1";
    String CHG_TYPE_U = "2";
    String CHG_TYPE_D = "3";

    String MONITOR_ROW = "1";
    String MONITOR_COL = "2";
}
