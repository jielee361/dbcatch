package com.yinhai.dbcatch.util;

public interface DbcCost {
    String ORA_GETTAB_SQL = "select table_name from user_tables";
    String ORA_GETCOL_SQL = "select column_name,data_type from user_tab_cols where table_name = '%s'";
}
