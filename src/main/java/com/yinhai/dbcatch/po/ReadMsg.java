package com.yinhai.dbcatch.po;

import java.util.List;

public class ReadMsg {
    private String tabName;
    private List<String> colValue;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public List<String> getColValue() {
        return colValue;
    }

    public void setColValue(List<String> colValue) {
        this.colValue = colValue;
    }

    @Override
    public String toString() {
        String cols = "";
        for (String s : colValue) {
            cols = cols + s +",";
        }
        return "ReadMsg{" +
                "tabName='" + tabName + '\'' +
                ", colValue=" + cols +
                '}';
    }
}
