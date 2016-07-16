package com.dianwoba.rha.tx.reids;


import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;

/**
 * Redis事务模板
 * Created by Administrator on 2016/6/6.
 */
public abstract class AbstractRedisTransaction {

    private RedisTemplate redisTemplate;

    public AbstractRedisTransaction() {
    }

    public AbstractRedisTransaction(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<Object> execute() throws DataAccessException {
        return (List<Object>) redisTemplate.execute(new SessionCallback<Object>() {
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.multi();
                AbstractRedisTransaction.this.redisOps(redisOperations);
                return redisOperations.exec();
            }
        });
    }

    public abstract void redisOps(RedisOperations redisOperations);

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}

