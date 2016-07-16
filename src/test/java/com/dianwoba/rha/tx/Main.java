package com.dianwoba.rha.tx;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2016/6/27.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("app-context.xml");
        Service service = ctx.getBean(Service.class);
//        service.service(123);
//        service.service2(new Service.ParamDTO(456));
//        service.service3(new Service.Order(789));
        service.service4(new Service.Order(789));
    }
}
