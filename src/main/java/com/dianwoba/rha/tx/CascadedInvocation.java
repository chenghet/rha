package com.dianwoba.rha.tx;

/**
 * 级联部分的调用
 *
 * @author Het
 */
public abstract class CascadedInvocation<T> {

    private CompensableTransactionType transactionType;

    public CascadedInvocation(CompensableTransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Construct parameters must not be null.");
        }
        this.transactionType = transactionType;
    }

    /**
     * 分布式部分的调用
     *
     * @return
     */
    public abstract T invoke() throws Throwable;

    public CompensableTransactionType getTransactionType() {
        return transactionType;
    }
}
