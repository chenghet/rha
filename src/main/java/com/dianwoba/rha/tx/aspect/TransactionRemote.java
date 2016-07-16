package com.dianwoba.rha.tx.aspect;

import com.dianwoba.rha.tx.CompensableTransactionType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TransactionRemote {
    CompensableTransactionType type();
}
