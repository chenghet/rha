package com.dianwoba.rha.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class CompensableTransactionTemplate {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CompensableTransactionHelper transactionCompensateHelper;

    /**
     * 事务发起者的调用
     *
     * @param basic
     * @param txManager
     * @return
     */
    public <T> T basically(Object bizNo, BasicInvocation<T> basic, String txManager) {
        PlatformTransactionManager tx = applicationContext.getBean(txManager, PlatformTransactionManager.class);
        TransactionStatus transaction = tx.getTransaction(new DefaultTransactionDefinition());
        T ret;
        try {
            ret = basic.invoke();
            transactionCompensateHelper.preset(bizNo, basic.getTransactionTypes()); // 事务提交前，preset事务
            tx.commit(transaction);
            try {
                transactionCompensateHelper.commit(bizNo, basic.getTransactionTypes()); // 提交commit
            } catch (Throwable t) {
                // log the error
            }
        } catch (Throwable t) {
            transactionCompensateHelper.release(bizNo, basic.getTransactionTypes()); // 基础事务提交失败，回滚redis事务hold
            tx.rollback(transaction);
            throw new RuntimeException(t);
        }
        return ret;
    }

    /**
     * 事务发起者的调用
     *
     * @param basic
     * @param txManager
     * @return
     */
    public <T> T basicallyWithNoTransaction(Object bizNo, BasicInvocation<T> basic) {
        T ret;
        transactionCompensateHelper.preset(bizNo, basic.getTransactionTypes()); // 事务提交前，preset事务
        try {
            ret = basic.invoke();
            try {
                transactionCompensateHelper.commit(bizNo, basic.getTransactionTypes()); // 提交commit
            } catch (Throwable t) {
                // log the error
            }
        } catch (Throwable t) {
            transactionCompensateHelper.release(bizNo, basic.getTransactionTypes()); // 基础事务提交失败，回滚redis事务hold
            throw new RuntimeException(t);
        }
        return ret;
    }

    /**
     * 分布式事务部分调用
     *
     * @param cascadedInvocation
     * @return
     */
    public <T> T distributed(Object bizNo, CascadedInvocation<T> cascadedInvocation) {
        T ret;
        try {
            ret = cascadedInvocation.invoke();
            transactionCompensateHelper.release(bizNo, cascadedInvocation.getTransactionType()); // 分布式事务执行成功，是否
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return ret;
    }
}
