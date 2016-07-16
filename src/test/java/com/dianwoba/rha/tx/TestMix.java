package com.dianwoba.rha.tx;

import com.dianwoba.rha.spring.BeanSelfAware;
import com.dianwoba.rha.tx.aspect.TransactionBase;
import com.dianwoba.rha.tx.aspect.TransactionBizNo;

import javax.annotation.Resource;

/**
 * 编码和注解结合
 */
public class TestMix implements BeanSelfAware<TestMix> {

    @Resource
    private CompensableTransactionTemplate transactionTemplate;

    public void cancel(Long orderId) {
        // 基础事务
        self.innerCancel(orderId);  // 要是的类内部调用切面生效，必须这么搞

        // 远程，商家相关
        transactionTemplate.distributed(orderId,
                new CascadedInvocation<Void>(CompensableTransactionType.TX_ORDER_CANCEL_2_SHOP) {
                    @Override
                    public Void invoke() throws Throwable {
                        return null;
                    }
                });
        // 远程，骑手相关
        transactionTemplate.distributed(orderId,
                new CascadedInvocation<Void>(CompensableTransactionType.TX_ORDER_CANCEL_2_RIDER) {
                    @Override
                    public Void invoke() throws Throwable {
                        return null;
                    }
                });
    }

    @TransactionBase(value = "txManager", types = {CompensableTransactionType.TX_ORDER_CANCEL_2_RIDER,
            CompensableTransactionType.TX_ORDER_CANCEL_2_SHOP})
    public void innerCancel(@TransactionBizNo Long orderId) {
        // do some thing
    }

    private TestMix self;

    @Override
    public void setSelf(TestMix self) {
        this.self = self;
    }
}
