package com.dianwoba.rha.tx.distribute;

import com.dianwoba.rha.tx.CompensableTransactionHelper;
import com.dianwoba.rha.tx.CascadedInvocation;

import java.util.List;

/**
 * 分布式事务补偿器的工厂类
 */
public class TransactionCompensatorFactory {

    public CompensableTransactionHelper transactionCompensateHelper;

    public TransactionCompensator getTransactionCompensator(final CascadedInvocation invocation) {
        return new TransactionCompensator() {
            @Override
            public void compensate() throws Throwable {
                List list = transactionCompensateHelper.uncommitted(invocation.getTransactionType());
                for (java.lang.Object bizNo : list) {
                    invocation.invoke();
                    transactionCompensateHelper.release(bizNo, invocation.getTransactionType());
                }
            }
        };
    }
}

