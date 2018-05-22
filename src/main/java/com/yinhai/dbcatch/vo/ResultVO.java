package com.yinhai.dbcatch.vo;

import java.io.Serializable;
import java.util.Arrays;

public class ResultVO implements Serializable {
    private static final long serialVersionUID = 812376774103405857L;
    private static final int ERROR = 0;
    private static final int SUCCESS = 1;

    private int state;
    private String message;
    private ResultKV[] data;

    public ResultVO(String msg) {
        this.state = SUCCESS;
        this.message = msg;
    }

    public ResultVO(Throwable e) {
        this.state = ERROR;
        this.message = e.getMessage();
    }

    public ResultVO(ResultKV[] data) {
        this.data = data;
        this.state=SUCCESS;

    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultKV[] getData() {
        return data;
    }

    public void setData(ResultKV[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "state=" + state +
                ", message='" + message + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
