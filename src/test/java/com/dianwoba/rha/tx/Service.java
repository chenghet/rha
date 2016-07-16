package com.dianwoba.rha.tx;

import com.dianwoba.rha.spring.BeanSelfAware;
import com.dianwoba.rha.tx.aspect.TransactionBase;
import com.dianwoba.rha.tx.aspect.TransactionBizNo;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;


@Component
public class Service implements BeanSelfAware<Service> {
    @TransactionBase(value = "okay", types = {CompensableTransactionType.TX_ORDER_OBTAINED_2_SHOP})
    public void service(@TransactionBizNo Integer orderId) {
        System.out.println(123);
    }

    @TransactionBase(value = "okay", types = {CompensableTransactionType.TX_ORDER_OBTAINED_2_SHOP})
    public void service2(ParamDTO paramDTO) {
        System.out.println("service 2");
    }

    @TransactionBase(value = "okay", types = {CompensableTransactionType.TX_ORDER_OBTAINED_2_SHOP})
    public void service3(Order order) {
        System.out.println("service 3");
    }

    public void service4(Order order) {
        System.out.println("service4");
        getSelf().service3(order);
    }

    private Service self;
    public void setSelf(Service t) throws BeansException {
        this.self = t;
    }

    public Service getSelf() {
        return self;
    }

    public static class ParamDTO {
        private int orderId;

        public ParamDTO(int orderId) {
            this.orderId = orderId;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }
    }

    public static class Order {
        private int id;

        public Order(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

}

