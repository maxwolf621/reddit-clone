package com.pttbackend.pttclone.config.databaseconfiguration;

public enum DataSourceType {
    MASTER("READ_WRITE"),
    SLAVE("READ_ONLY");

    private String s;

    DataSourceType(String s){
        this.s = s;
    }
    public String getType(){
        return this.s;
    }
}
