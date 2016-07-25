package com.dianwoba.rha.tx.reids;

import com.dianwoba.rha.tx.CompensableTransactionHelper;
import com.dianwoba.rha.tx.CompensableTransactionType;
import com.dianwoba.rha.tx.TransactionItem;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public void release(final Object bizNo, final CompensableTransactionType xtype) {
        if (xtype == null) {
            return;
        }
        redisTemplate.opsForZSet().remove(getCacheKey(xtype), new TransactionItem(xtype, bizNo));
    }

    // 超时未提交部分
    public List<TransactionItem> uncommitted(CompensableTransactionType txType) {
        long bgn = -System.currentTimeMillis() + RELEASE_OVERTIME_PERIOD_MS;
        List<TransactionItem> list = new ArrayList<TransactionItem>();
        Set<ZSetOperations.TypedTuple<TransactionItem>> set =
                redisTemplate.opsForZSet().rangeByScoreWithScores(getCacheKey(txType), bgn, 0);
        for (ZSetOperations.TypedTuple<TransactionItem> tuple : set) {
            TransactionItem xitem = tuple.getValue();
            xitem.setTimestamp(tuple.getScore().longValue());
            list.add(xitem);
        }
        return list;
    }

    // 超时未释放部分
    public List<TransactionItem> unreleased(CompensableTransactionType txType) {
        long end = System.currentTimeMillis() - COMMIT_OVERTIME_PERIOD_MS;
        List<TransactionItem> list = new ArrayList<TransactionItem>();
        Set<ZSetOperations.TypedTuple<TransactionItem>> set =
                redisTemplate.opsForZSet().rangeByScoreWithScores(getCacheKey(txType), 0, end);
        for (ZSetOperations.TypedTuple<TransactionItem> tuple : set) {
            TransactionItem xitem = tuple.getValue();
            xitem.setTimestamp(-tuple.getScore().longValue());
            list.add(xitem);
        }
        return list;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static String getCacheKey(CompensableTransactionType transactionType) {
        return String.format("DTX_KEY:%s", transactionType.name());
    }
}
