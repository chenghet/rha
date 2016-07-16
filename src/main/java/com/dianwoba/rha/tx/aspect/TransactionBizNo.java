package com.dianwoba.rha.tx.aspect;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2016/6/27.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TransactionBizNo {
}
