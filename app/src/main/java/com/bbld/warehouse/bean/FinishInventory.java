package com.bbld.warehouse.bean;

/**
 * 库存盘点决策
 * Created by likey on 2017/6/8.
 */

public class FinishInventory {
    private int status;
    private String mes;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
}
