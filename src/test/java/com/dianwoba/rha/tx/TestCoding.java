package com.dianwoba.rha.tx;

import javax.annotation.Resource;

public class TestCoding {

    @Resource
    private CompensableTransactionTemplate transactionTemplate;

    public void test() {
        // 基础，订单取消
        transactionTemplate.basically("bizNo",
                new BasicInvocation<Object>(CompensableTransactionType.TX_ORDER_CANCEL_2_SHOP,
                        CompensableTransactionType.TX_ORDER_CANCEL_2_RIDER) {
                    @Override
                    public Object invoke() {
                        return null;
                    }
                }, "txManager");
        // 远程，商家相关
        transactionTemplate.distributed("bizNo",
                new CascadedInvocation<Object>(CompensableTransactionType.TX_ORDER_CANCEL_2_SHOP) {
                    @Override
                    public Object invoke() {
                        return null;
                    }
                });
        // 远程，骑手相关
        transactionTemplate.distributed("bizNo",
                new CascadedInvocation<Object>(CompensableTransactionType.TX_ORDER_CANCEL_2_RIDER) {
                    @Override
                    public Object invoke() {
                        return null;
                    }
                });
    }
}
