package com.dianwoba.rha.tx;

/**
 * 分布式调用的基础部分
 *
 * @author Het
 */
public abstract class BasicInvocation<T> {

    private CompensableTransactionType[] transactionTypes;

    public BasicInvocation(CompensableTransactionType... txTypes) {
        this.transactionTypes = txTypes;
    }

    /**
     * 基础事务的调用
     *
     * @return
     */
    public abstract T invoke() throws Throwable;

    public CompensableTransactionType[] getTransactionTypes() {
        return transactionTypes;
    }
}