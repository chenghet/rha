package com.dianwoba.rha.tx.distribute;

/**
 * 分布式事务补偿器
 */
public abstract class TransactionCompensator {

    /**
     * 进行事务补偿
     */
    public abstract void compensate() throws Throwable;
}
