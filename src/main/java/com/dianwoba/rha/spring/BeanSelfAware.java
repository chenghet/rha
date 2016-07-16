package com.dianwoba.rha.spring;

import org.springframework.beans.factory.Aware;

/**
 * Created by Administrator on 2016/7/4.
 */
public interface BeanSelfAware<T> extends Aware{
    void setSelf(T self);
}
