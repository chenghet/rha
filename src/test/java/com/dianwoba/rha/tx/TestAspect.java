package com.dianwoba.rha.tx;

import com.dianwoba.rha.spring.BeanSelfAware;
import com.dianwoba.rha.tx.aspect.TransactionBase;
import com.dianwoba.rha.tx.aspect.TransactionBizNo;
import com.dianwoba.rha.tx.aspect.TransactionRemote;

/**
 * 全注解方式
 */
public class TestAspect implements BeanSelfAware<TestAspect> {

    // fixme 只是演示，这里的切面并不会生效，事务的编织应该在manager的上一层完成
    public void cancel(Long orderId) {
        // 基础，订单取消
        innerCancel(orderId);
        // 远程，商家相关
        shopRemote(orderId);
        // 远程，骑手相关
        riderRemote(orderId);
    }


    // 基础，订单取消
    @TransactionBase(types = {CompensableTransactionType.TX_ORDER_CANCEL_2_RIDER,
            CompensableTransactionType.TX_ORDER_CANCEL_2_SHOP})
    public void innerCancel(@TransactionBizNo Long orderId) {
    }

    // 远程，商家相关
    @TransactionRemote(type = CompensableTransactionType.TX_ORDER_CANCEL_2_SHOP)
    public void shopRemote(@TransactionBizNo Long orderId) {
    }

    // 远程，骑手相关
    @TransactionRemote( type = CompensableTransactionType.TX_ORDER_CANCEL_2_RIDER)
    private void riderRemote(@TransactionBizNo Long orderId) {
    }


    private TestAspect self;
    public void setSelf(TestAspect self) {
        this.self = self;
    }

    public TestAspect getSelf() {
        return this.self;
    }
}
