package com.dianwoba.rha.tx;

import java.io.Serializable;

public class TransactionItem implements Serializable {

    private CompensableTransactionType transactionType;
    private Object bizNo;
    private transient long timestamp;

    public TransactionItem(CompensableTransactionType transactionType, Object bizNo) {
       this(transactionType, bizNo, null);
    }

    public TransactionItem(CompensableTransactionType transactionType, Object bizNo, Long timestamp) {
        this.transactionType = transactionType;
        this.bizNo = bizNo;
        this.timestamp = timestamp == null ? System.currentTimeMillis() : timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        return transactionType == transactionType && bizNo.equals(bizNo);
    }

    @Override
    public int hashCode() {
        return bizNo.hashCode() * 37 + transactionType.hashCode();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getBizNo() {
        return bizNo;
    }

    public void setBizNo(Object bizNo) {
        this.bizNo = bizNo;
    }

    public CompensableTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CompensableTransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
