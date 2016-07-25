package com.dianwoba.rha.tx.aspect;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TransactionBizNo {
}
