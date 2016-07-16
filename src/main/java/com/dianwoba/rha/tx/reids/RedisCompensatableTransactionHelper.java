package com.dianwoba.rha.tx.reids;

import com.dianwoba.rha.tx.CompensableTransactionHelper;
import com.dianwoba.rha.tx.TransactionItem;
import com.dianwoba.rha.tx.CompensableTransactionType;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式事务补偿机制的帮助类-redis
 *
 * @author Het on 2016/6/24.
 */
public class RedisCompensatableTransactionHelper implements CompensableTransactionHelper {

    private RedisTemplate redisTemplate;

    public RedisCompensatableTransactionHelper(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 预设
    public void preset(final Object bizNo, final CompensableTransactionType... txTypes) {
        if (txTypes == null) {
            return;
        }
        new AbstractRedisTransaction(redisTemplate) {
            public void redisOps(RedisOperations redisOperations) {
                long t = System.currentTimeMillis();
                for (CompensableTransactionType type : txTypes) {
                    redisOperations.opsForZSet().add(getCacheKey(type), new TransactionItem(type, bizNo), -t);
                }
            }
        }.execute();
    }

    // 提交
    public void commit(final Object bizNo, final CompensableTransactionType... txTypes) {
        if (txTypes == null) {
            return;
        }
        new AbstractRedisTransaction(redisTemplate) {
            public void redisOps(RedisOperations redisOperations) {
                long t = System.currentTimeMillis();
                for (CompensableTransactionType type : txTypes) {
                    redisOperations.opsForZSet().add(getCacheKey(type), new TransactionItem(type, bizNo, t), t);
                }
            }
        }.execute();
    }

    // 释放
    public void release(final Object bizNo, final CompensableTransactionType... txTypes) {
        if (txTypes == null) {
            return;
        }
        new AbstractRedisTransaction(redisTemplate) {
            public void redisOps(RedisOperations redisOperations) {
                for (CompensableTransactionType type : txTypes) {
                    redisOperations.opsForZSet().remove(getCacheKey(type), new TransactionItem(type, bizNo));
                }
            }
        }.execute();
    }

    // 超时未提交部分
    public List<TransactionItem> uncommitted(CompensableTransactionType txType) {
        long end = System.currentTimeMillis() - COMMIT_OVERTIME_PERIOD_MS;
        List obj = new ArrayList<Object>();
        obj.addAll(redisTemplate.opsForZSet().rangeByScore(getCacheKey(txType), 0, end));
        return obj;
    }

    // 超时未释放部分
    public List<TransactionItem> unreleased(CompensableTransactionType txType) {
        long bgn = -System.currentTimeMillis() + RELEASE_OVERTIME_PERIOD_MS;
        List obj = new ArrayList<Object>();
        obj.addAll(redisTemplate.opsForZSet().rangeByScore(getCacheKey(txType), bgn, 0));
        return obj;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static String getCacheKey(CompensableTransactionType transactionType) {
        return String.format("DTX_KEY:%s", transactionType.name());
    }
}
