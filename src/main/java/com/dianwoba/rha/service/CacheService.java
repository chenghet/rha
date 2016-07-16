package com.dianwoba.rha.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存操作
 */
@Component
public class CacheService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private RedisTemplate<String, Object> redisTemplate;

	private RedisTemplate<String, String> sRedisTemplate;

	/*
	 * 缓存最大过期时间-一个月
	 */
	public static final int EXPIRE_TIME_MAX = 30 * 24 * 3600;

	/*
	 * 缓存过期时间-半天
	 */
	public static final int EXPIRE_TIME_HALFDAY = 12 * 3600;

	/*
	 * 缓存过期时间-整天
	 */
	public static final int EXPIRE_TIME_ONEDAY = 24 * 3600;

	/******************************
	 ********* 缓存操作 ***********
	 ******************************/

	/**
	 * 设置缓存
	 *
	 * @param key
	 * @param value
	 */
	public void putCache(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
		} catch (Exception e) {
			logger.error("PUT cache exception [key=" + key + ", value=" + value + "].", e);
		}
	}

	/**
	 * 设置缓存，并设定缓存时长（秒）
	 *
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void putCache(String key, Object value, int expire) {
		try {

			redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error("PUT cache exception [key=" + key + ", value=" + value + ", expire=" + expire + "].", e);
		}
	}

	/**
	 * 获取缓存数据
	 *
	 * @param key
	 * @return
	 */
	public Object getCache(String key) {
		try {

			return redisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			logger.error("GET cache exception [key=" + key + "].", e);
		}
		return null;
	}

	/**
	 * 删除缓存
	 *
	 * @param key
	 */
	public void removeCache(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			logger.error("Remove cache exception [key=" + key + "].", e);
		}
	}

	/******************************
	 ********* 队列操作 ***********
	 ******************************/

	/**
	 * 队列缓存设置
	 *
	 * @param key
	 * @param value
	 */
	public void putQueue(String key, Object value) {
		try {

			redisTemplate.opsForList().leftPush(key, value);

		} catch (Exception e) {
			logger.error("PUT Queue cache exception [key=" + key + ", value=" + value + "].", e);
		}
	}

	/**
	 * 获取队列缓存
	 *
	 * @param key
	 * @return
	 */
	public Object getQueue(String key) {
		try {

			return redisTemplate.opsForList().rightPop(key);

		} catch (Exception e) {
			logger.error("GET Queue cache exception [key=" + key + "].", e);
			return null;
		}
	}

	public <T> T head(String key) {
		List<Object> objects = null;
		try {
			objects = redisTemplate.opsForList().range(key, 0, 1);
		} catch (Throwable e) {
			logger.error("get head from cache exception [key=" + key + "].", e);
			return null;
		}
		return objects != null && objects.size() != 0 ? (T) objects.get(0) : null;
	}

	/******************************
	 ********* 栈操作 ***********
	 ******************************/
	public void putStack(String key, Object value) {
		try {
			redisTemplate.opsForList().leftPush(key, value);
		} catch (Exception e) {
			logger.error("PUT Stack cache exception [key=" + key + ", value=" + value + "].", e);
		}
	}

	public Object getStack(String key) {
		try {
			return redisTemplate.opsForList().leftPop(key);

		} catch (Exception e) {
			logger.error("GET Stack cache exception [key=" + key + "].", e);
			return null;
		}
	}

	public int length(String key) {

		try {
			return redisTemplate.opsForList().size(key).intValue();
		} catch (Exception e) {
			logger.error("GET cache length exception [key=" + key + "].", e);
			return 0;
		}
	}

	public void expire(String key, long timeout, TimeUnit unit) {
		try {
			redisTemplate.expire(key, timeout, unit);
		} catch (Exception e) {
			logger.error("SET expire time exception [key=" + key + "].", e);
		}
	}

	/******************************
	 ********* hash操作 ***********
	 ******************************/
	/**
	 * hash put all
	 *
	 * @param key
	 * @param map
	 * @author Het.C
	 * @date 2015年10月12日
	 */
	public void hputs(String key, HashMap<? extends Object, ? extends Object> map) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
		} catch (Exception e) {
			logger.error("PUT All Hash exception [key=" + key + "].", e);
		}
	}

	/**
	 * hash put
	 *
	 * @param key
	 * @param hashKey
	 * @param value
	 * @author Het.C
	 * @date 2015年10月12日
	 */
	public void hput(String key, Object hashKey, Object value) {
		try {
			redisTemplate.opsForHash().put(key, hashKey, value);
		} catch (Exception e) {
			logger.error("PUT Hash exception [key=" + key + "].", e);
		}
	}

	/**
	 * hash get
	 *
	 * @param key
	 * @param hashKey
	 * @return
	 * @author Het.C
	 * @date 2015年10月12日
	 */
	public Object hget(String key, Object hashKey) {
		try {
			return redisTemplate.opsForHash().get(key, hashKey);
		} catch (Exception e) {
			logger.error("GET Hash exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * hash remove
	 *
	 * @param key
	 * @param hashKey
	 * @author Het.C
	 * @date 2015年10月12日
	 */
	public void hrem(String key, Object hashKey) {
		try {
			redisTemplate.opsForHash().delete(key, hashKey);
		} catch (Exception e) {
			logger.error("DELETE Hash exception [key=" + key + "].", e);
		}
	}

	/**
	 * hash size
	 *
	 * @param key
	 * @return
	 * @author Het.C
	 * @date 2015年10月12日
	 */
	public long hsize(String key) {
		try {
			return redisTemplate.opsForHash().size(key);
		} catch (Exception e) {
			logger.error("GET Hash size exception [key=" + key + "].", e);
			return 0;
		}
	}

	/**
	 * hash keys
	 *
	 * @param key
	 * @return
	 * @author Het.C
	 * @date 2015年10月12日
	 */
	public Set<?> hkeys(String key) {
		try {
			return redisTemplate.opsForHash().keys(key);
		} catch (Exception e) {
			logger.error("GET Hash size exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * hash keys
	 *
	 * @param key
	 * @return
	 * @author Het.C
	 * @date 2015年10月12日
	 */
	public List<Object> hvals(String key) {
		try {
			return redisTemplate.opsForHash().values(key);
		} catch (Exception e) {
			logger.error("GET Hash size exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * 取出Map
	 */
	public Map<Object, Object> hMap(String key) {
		try {
			return redisTemplate.opsForHash().entries(key);
		} catch (Exception e) {
			logger.error("GET Map size exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * 查询该hashKey对应的field是否存在
	 */
	public boolean hexists(String key, Object hashKey) {
		try {
			return redisTemplate.opsForHash().hasKey(key, hashKey);
		} catch (Exception e) {
			logger.error("GET Map size exception [key=" + key + "].", e);
			return false;
		}
	}

	/******************************
	 ********* set操作 ***********
	 ******************************/
	/**
	 * set add
	 *
	 * @param key
	 * @param values
	 */
	public void sadd(String key, Object... values) {
		try {
			redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			logger.error("Add set exception [key=" + key + "].", e);
		}
	}

	/**
	 * set exist
	 *
	 * @param key
	 */
	public Long ssize(String key) {
		try {
			return redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			logger.error("Get set size exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * set get
	 *
	 * @param key
	 */
	public Set<?> sget(String key) {
		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			logger.error("Get set exception [key=" + key + "].", e);
			return null;
		}
	}

	public Long srem(String key, Object... values) {
		try {
			return redisTemplate.opsForSet().remove(key, values);
		} catch (Throwable e) {
			logger.error("Get set exception [key=" + key + "].", e);
			return null;
		}
	}
	/************************************************************
	 ********************** zset 操作*****************************
	 ************************************************************/
	/**
	 * @author zengdan 往Zset插入数据
	 * @deprecated
	 */
	public void zsetPut(String key, Object hashKey, Double score) {
		try {
			redisTemplate.opsForZSet().add(key, hashKey, score);
		} catch (Exception e) {
			logger.error("PUT Zset exception [key=" + key + "].", e);
		}
	}

	/**
	 * 往Zset插入数据
	 */
	public void zput(String key, Object value, double score) {
		try {
			redisTemplate.opsForZSet().add(key, value, score);
		} catch (Exception e) {
			logger.error("PUT Zset exception [key=" + key + "].", e);
		}
	}

	/**
	 * @author zengdan 查询Zset,按照开始结束score
	 * @deprecated
	 */
	public Set<?> zsetGet(String key, Double arg0, Double arg1) {
		try {
			return redisTemplate.opsForZSet().rangeByScore(key, arg0, arg1);
		} catch (Exception e) {
			logger.error("GET Zset exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * zset score get
	 */
	public Set<?> zsget(String key, Double arg0, Double arg1) {
		try {
			return redisTemplate.opsForZSet().rangeByScore(key, arg0, arg1);
		} catch (Exception e) {
			logger.error("GET Zset exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * zset range get
	 */
	public Set<?> zrget(String key, Long start, Long end) {
		try {
			return redisTemplate.opsForZSet().range(key, start, end);
		} catch (Exception e) {
			logger.error("GET Zset exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * @author chensheng 删除Zset中的元素
	 * @param String redis缓存键
	 * @param Object 需要删除的对象本身
	 */
	public Long zrem(String key, Object... values){
		try {
			return redisTemplate.opsForZSet().remove(key, values);
		} catch (Exception e) {
			logger.error("GET Zset exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * 将zset作为消息队列
	 *
	 * @param key
	 * @return
	 */
	public Object zpop(final String key) {
		try {
			return redisTemplate.execute(new SessionCallback<Object>() {
				public Object execute(RedisOperations operations) throws DataAccessException {
					operations.multi();
					operations.opsForZSet().range(key, 0, 0);
					operations.opsForZSet().removeRange(key, 0, 0);
					List<Object> lst = operations.exec();
					if (lst != null && lst.size() != 0) {
						Set set = (Set) lst.get(0);
						if (set != null && set.size() != 0) {
							return set.toArray()[0];
						}
					}
					return null;
				}
			});
		} catch (Exception e) {
			logger.error("zpop exception [key=" + key + "].", e);
			return null;
		}
	}

	/**
	 * 模糊查询
	 */
	public Set<String> fuzzyQuery(String pattern) {
		try {
			return redisTemplate.keys(pattern);
		} catch (Exception e) {
			logger.error("GET fuzzyQuery exception [key=" + pattern + "].", e);
			return null;
		}
	}

	/************************************************************
	 ****************** distribute lock **********************
	 ************************************************************/
	/**
	 * lock
	 *
	 * @param key
	 * @return
	 * @Deprecated
	 */
	public boolean acquireDistributeLock(final String key, String version) {
		Boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {

			public Boolean doInRedis(RedisConnection connection) {
				//fix JedisConnection cannot be cast to StringRedisConnection
				StringRedisConnection conn = new DefaultStringRedisConnection(connection);
				String lockKey = String.format("%s_%s", "REDIS_LOCK", key);
				long current = conn.time();
				//?
				while (System.currentTimeMillis() < 50) {

					if (conn.setNX(lockKey, String.valueOf(current))) {
						conn.expire(lockKey, 1000);
						return true;
					} else {
						logger.debug("key:{} locked by {}", lockKey, conn.get(lockKey));
					}

					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				logger.debug("acquire lock is fail ,key:{}", lockKey);
				return false;
			}
		}, true);

		return result;
	}

	/**
	 * lock
	 * @param key
	 * @return
	 * @Deprecated
	 */
	public boolean acquireDistributeLock(String key) {
		return acquireDistributeLock(key, 0L, TimeUnit.MILLISECONDS);
	}

	/**
	 * try lock
	 * @param key
	 * @param timeout
	 * @param unit
	 * @return
	 * @Deprecated
	 */
	public boolean acquireDistributeLock(final String key, final long timeout, final TimeUnit unit) {
		Boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) {
				StringRedisConnection conn = new DefaultStringRedisConnection(connection);
				String lockKey = String.format("%s_%s", "REDIS_LOCK", key);
				long current = conn.time();
				long nano = System.nanoTime();
				do {
					if (conn.setNX(lockKey, String.valueOf(current))) {
						conn.expire(key, 1000);
						return true;
					} else
						logger.info("key:{} locked by {}", lockKey, conn.get(lockKey));
					if (timeout == 0)
						break;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while ((System.nanoTime() - nano) < unit.toNanos(timeout));
				logger.debug("key:{} try lock fail", lockKey);
				return false;
			}
		}, true);
		return result;
	}

	/**
	 * release lock
	 *
	 * @param key
	 */
	public void releaseDistributeLock(String key) {
		String lockKey = String.format("%s_%s", "REDIS_LOCK", key);
		redisTemplate.delete(lockKey);
	}

	/************************************************************
	 ****************** distribute lock version 2 ***************
	 ************************************************************/

	/**
	 * 缓存锁，默认锁超时时间为10秒
	 *
	 * @param key
	 */
	public void lock(String key) {
		lock(key, 10000);
	}

	/**
	 * 带锁超时时间的缓存锁
	 *
	 * @param key
	 * @param lockTimeoutMS 锁超时时间
	 */
	public void lock(String key, int lockTimeoutMS) {
		tryLock(key, -1, lockTimeoutMS);
	}

	/**
	 * 尝试获取缓存锁，在超时时间内，如果获取成功，则返回true；否则，返回false；默认锁超时时间为10秒；
	 *
	 * @param key
	 * @param timeoutMS 请求锁超时时间，如果timeout<=0，则没有请求超时时间；但如果timeoutMS<=0，建议使用lock
	 * @return
	 */
	public boolean tryLock(String key, int timeoutMS) {
		return tryLock(key, timeoutMS, 10000);
	}

	/**
	 * 尝试获取带锁超时时间的缓存锁，在超时时间内，如果获取成功，则返回true；否则，返回false；
	 *
	 * @param key
	 * @param timeoutMS 请求锁超时时间，如果timeoutMS<=0，则没有请求超时时间；但如果timeoutMS<=0，建议使用lock
	 * @param lockTimeoutMS 锁超时时间
	 * @return
	 */
	public boolean tryLock(String key, int timeoutMS, int lockTimeoutMS) {
		long t = System.currentTimeMillis();
		String _lockKey = String.format("%s_%s", key, "lock_v2");
		while (true) {
			if (redisTemplate.opsForValue().setIfAbsent(_lockKey, "lock")) {
				redisTemplate.expire(_lockKey, lockTimeoutMS, TimeUnit.MILLISECONDS);
				return true;
			}
			if (timeoutMS >= 0 && System.currentTimeMillis() - t > timeoutMS) {
				return false;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * 清楚锁标记，请在finally中执行
	 *
	 * @param key
	 */
	public void unlock(String key) {
		String _lockKey = String.format("%s_%s", key, "lock_v2");
		removeCache(_lockKey);
	}

	/************************************************************
	 **************** StringRedisSerializer *****************
	 ************************************************************/
	public void sIncrement(String key, Long delta) {
		try {
			sRedisTemplate.opsForValue().increment(key, delta);

		} catch (Exception e) {
			logger.error("sIncrement cache exception [key=" + key + ", delta=" + delta + "].", e);
		}
	}

	public void sSetCache(String key, String value) {
		try {
			sRedisTemplate.opsForValue().set(key, value);
		} catch (Exception e) {
			logger.error("sSetCache cache exception [key=" + key + ", value=" + value + "].", e);
		}
	}

	public String sGetCache(String key) {
		try {
			return sRedisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			logger.error("sSetCache cache exception [key=" + key + "].", e);
			return null;
		}
	}

	public List<String> sMget(List<String> keys) {
		try {
			return sRedisTemplate.opsForValue().multiGet(keys);
		} catch (Throwable e) {
			logger.error("sMget cache exception [key=" + keys + "].", e);
			return null;
		}
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisTemplate<String, String> getsRedisTemplate() {
		return sRedisTemplate;
	}

	public void setsRedisTemplate(RedisTemplate<String, String> sRedisTemplate) {
		this.sRedisTemplate = sRedisTemplate;
	}

}
