package com.yinhai.dbcatch.vo;

public class DatasourceVO {
    private String ds_id;
    private String ds_name;
    private String ds_type;
    private String ds_url;
    private String ds_username;
    private String ds_password;
    private String biz_user;
    private String biz_time;

    public String getDs_id() {
        return ds_id;
    }

    public void setDs_id(String ds_id) {
        this.ds_id = ds_id;
    }

    public String getDs_name() {
        return ds_name;
    }

    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }

    public String getDs_type() {
        return ds_type;
    }

    public void setDs_type(String ds_type) {
        this.ds_type = ds_type;
    }

    public String getDs_url() {
        return ds_url;
    }

    public void setDs_url(String ds_url) {
        this.ds_url = ds_url;
    }

    public String getDs_username() {
        return ds_username;
    }

    public void setDs_username(String ds_username) {
        this.ds_username = ds_username;
    }

    public String getDs_password() {
        return ds_password;
    }

    public void setDs_password(String ds_password) {
        this.ds_password = ds_password;
    }

    public String getBiz_user() {
        return biz_user;
    }

    public void setBiz_user(String biz_user) {
        this.biz_user = biz_user;
    }

    public String getBiz_time() {
        return biz_time;
    }

    public void setBiz_time(String biz_time) {
        this.biz_time = biz_time;
    }

    @Override
    public String toString() {
        return "DatasourceVO{" +
                "ds_id='" + ds_id + '\'' +
                ", ds_name='" + ds_name + '\'' +
                ", ds_type='" + ds_type + '\'' +
                ", ds_url='" + ds_url + '\'' +
                ", ds_username='" + ds_username + '\'' +
                ", ds_password='" + ds_password + '\'' +
                ", bizuser='" + biz_user + '\'' +
                ", biztime='" + biz_time + '\'' +
                '}';
    }
}
