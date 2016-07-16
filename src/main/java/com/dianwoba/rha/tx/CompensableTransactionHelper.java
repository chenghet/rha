package com.dianwoba.rha.tx;

import java.util.List;

/**
 * 分布式事务补偿机制的帮助类
 * <p>
 * Created by Het on 2016/6/2.
 */
public interface CompensableTransactionHelper {

    long COMMIT_OVERTIME_PERIOD_MS = 60 * 1000; // 1分钟
    long RELEASE_OVERTIME_PERIOD_MS = 3 * 60 * 1000; // 3分钟

    /**
     * 分布式事务执行的发起方事务提交之前，预置入事务消息（未决状态）
     *
     * @param bizNo
     * @param txTypes
     */
    void preset(Object bizNo, CompensableTransactionType... txTypes);

    /**
     * 分布式事务执行的发起方事务提交之后，commit事务消息（已决状态）
     *
     * @param bizNo
     * @param txTypes
     */
    void commit(Object bizNo, CompensableTransactionType... txTypes);

    /**
     * 查询出已经预置入，但是并没有被确认的消息
     *
     * @param txType
     * @return
     */
    List uncommitted(CompensableTransactionType txType);

    /**
     * 释放分布式事务执行的hold
     *
     * @param bizNo
     * @param txTypes
     */
    void release(Object bizNo, CompensableTransactionType... txTypes);

    /**
     * 查询出已经释放超的事务
     *
     * @param txType
     * @return
     */
    List unreleased(CompensableTransactionType txType);

}
