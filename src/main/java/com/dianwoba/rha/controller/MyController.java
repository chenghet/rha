package com.dianwoba.rha.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Controller
public class MyController {

    @Resource
    private Validator validator;

    @RequestMapping("/test/my")
    public @ResponseBody String my(MyRequest request) {
        Set<ConstraintViolation<MyRequest>> set = validator.validate(request);
        if (CollectionUtils.isNotEmpty(set)) {
            return ((ConstraintViolation) set.toArray()[0]).getMessage();
        }
        return JSON.toJSONString(request);
    }
}

class MyRequest {
    @NotNull(message = "a不能为空")
    private Integer a;
    @NotEmpty(message = "b不能为空")
    private String b;

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
