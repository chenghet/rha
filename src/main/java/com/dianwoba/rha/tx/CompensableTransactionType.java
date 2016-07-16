package com.dianwoba.rha.tx;

public enum CompensableTransactionType {
    TX_ORDER_OBTAINED_2_SHOP("订单取件对于商家的事务类型", 5, "orderId"),
    TX_ORDER_FINISHED_2_RIDER("订单完成对于配送员的事务类型", 5, "orderId"),
    TX_ORDER_FINISHED_2_SHOP("订单完成对于商家的事务类型", 5, "orderId"),
    TX_ORDER_CANCEL_2_RIDER("订单取消对于配送员的事务类型", 5, "orderId"),
    TX_ORDER_CANCEL_2_SHOP("订单取消对于商家的事务类型", 5, "orderId");

    private String description;
    private int compensateTimeCycle = 5; // 单位秒
    private String autoMatchBizNoName;

    CompensableTransactionType(String description, int compensateTimeCycle, String autoMatchBizNoName) {
        this.description = description;
        this.compensateTimeCycle = compensateTimeCycle;
        this.autoMatchBizNoName = autoMatchBizNoName;
    }

    public String getDescription() {
        return description;
    }

    public int getCompensateTimeCycle() {
        return compensateTimeCycle;
    }

    public String getAutoMatchBizNoName() {
        return autoMatchBizNoName;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
