import com.dianwoba.rha.service.CacheService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Het on 2016/5/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/app-context-cache-sentinal.xml")
public class RedisTest {
    @Autowired
    CacheService cacheService;

    @Test
    @Ignore
    public void listTest() {
        RedisTemplate redisTemplate = cacheService.getRedisTemplate();
        redisTemplate.opsForList().rightPush("hello", "1");
        redisTemplate.opsForList().rightPush("hello", "2");
        redisTemplate.opsForList().rightPush("hello", "3");
        redisTemplate.opsForList().rightPush("hello", "4");
        redisTemplate.opsForList().rightPush("hello", "5");
        System.out.println(redisTemplate.opsForList().range("hello", 0, 0));
        redisTemplate.delete("hello");
    }

    @Test
    @Ignore
    public void test() {
//        cacheService.removeCache(String.format(CacheKeyEn.渠道待通知订单有序集合.getKey(), "18"));
//        cacheService.removeCache(String.format(CacheKeyEn.渠道通知中订单有序集合.getKey(), "18"));
//        cacheService.removeCache(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "1"));
//        cacheService.removeCache(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "2"));
//        cacheService.removeCache(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "3"));
//        cacheService.removeCache(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "4"));
//        cacheService.removeCache(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "5"));
//        cacheService.removeCache(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "6"));

        System.out.println(cacheService.getRedisTemplate().opsForZSet().size(String.format(CacheKeyEn.渠道待通知订单有序集合.getKey(), "18")));
        System.out.println(cacheService.getRedisTemplate().opsForZSet().size(String.format(CacheKeyEn.渠道通知中订单有序集合.getKey(), "18")));
        System.out.println(cacheService.length(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "1464613864864")));
//        System.out.println(cacheService.length(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "1")));
//        System.out.println(cacheService.length(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "2")));
//        System.out.println(cacheService.length(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "3")));
//        System.out.println(cacheService.length(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "4")));
//        System.out.println(cacheService.length(String.format(CacheKeyEn.渠道订单通知队列.getKey(), "5")));
    }


    @Test
    @Ignore
    public void test2() throws Exception {
        List<A> list = new ArrayList<A>();
        A a = new A();
        a.setA(1);
        a.setB("123");
        list.add(a);
        A a2 = new A();
        a2.setA(2);
        a2.setB("234");
        list.add(a2);
        cacheService.putCache("test-list", list);

        List list2 = (List) cacheService.getCache("test-list");
        for (Object o : list2) {
            System.out.println(o);
        }
        cacheService.removeCache("test-list");
    }

    enum CacheKeyEn {
        渠道待通知订单有序集合("RC_TO_NOTIFY_CHANNEL_ID_SET_%s", "渠道待通知订单有序集合，内容是channel_id，score为时间戳", CacheService.EXPIRE_TIME_MAX),
        渠道通知中订单有序集合("RC_NOTIFYING_CHANNEL_ID_SET_%s", "渠道通知中订单有序集合，内容是channel_id，score为0", CacheService.EXPIRE_TIME_MAX),
        渠道订单通知队列("RC_CHANNEL_NOTIFY_DTO_QUEUE_%s", "渠道订单通知队列，每个channel_id一个队列", CacheService.EXPIRE_TIME_MAX);

        private String key;
        private String desc;
        private int expired;// 失效时间，单位秒

        CacheKeyEn(String key, String desc, int expired) {
            this.key = key;
            this.desc = desc;
            this.expired = expired;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getExpired() {
            return expired;
        }

        public void setExpired(int expired) {
            this.expired = expired;
        }
    }

//    @Test
//    public void pipeline() {
//        System.out.println(cacheService.getRedisTemplate().executePipelined(new SessionCallback() {
//            public Object execute(RedisOperations ops) throws DataAccessException {
//                ops.opsForValue().set("a", "a");
//                Object a = ops.opsForValue().get("a");
//                System.out.println(a);
//                if (a != null) {
//                    ops.opsForValue().set("b", "b");
//                }
//                System.out.println(ops.opsForValue().get("b"));
//                return null;
//            }
//        }));
//    }
//
//    @Test
//    @Ignore
//    public void test() {
//        System.out.println(cacheService.getRedisTemplate().execute(new SessionCallback() {
//            public Object execute(RedisOperations operations) throws DataAccessException {
//                Double score = operations.opsForZSet().score("key-set", "value");
//                System.out.println(score);
//                operations.multi();
//                if (score == null) {
//                    operations.opsForZSet().add("key-set", "value", System.currentTimeMillis());
//                }
//                operations.opsForList().leftPush("key-list", "value");
//                return operations.exec();
//            }
//        }));
//    }
//
//    @Test
//    @Ignore
//    public void test2() {
//        cacheService.zsetPut("key-set", "1", 1d);
//        cacheService.zsetPut("key-set", "5", 5d);
//        cacheService.zsetPut("key-set", "3", 3d);
//        cacheService.zsetPut("key-set", "7", 7d);
//        cacheService.zsetPut("key-set", "2", 2d);
//
//
//        cacheService.zsetPut("key-set2", "4", 4d);
//        cacheService.zsetPut("key-set2", "6", 6d);
//
//        cacheService.getRedisTemplate().opsForZSet().unionAndStore("key-set", "key-set2", "key-set");
//
//        System.out.println(cacheService.zpop("key-set"));
//        System.out.println(cacheService.zpop("key-set"));
//        System.out.println(cacheService.zpop("key-set"));
//        System.out.println(cacheService.zpop("key-set"));
//        System.out.println(cacheService.zpop("key-set"));
//        System.out.println(cacheService.zpop("key-set"));
//        System.out.println(cacheService.zpop("key-set"));
//        System.out.println(cacheService.zpop("key-set"));
//    }
//
//    @Test
//    @Ignore
//    public void clear() {
//        cacheService.removeCache("key-set");
//        cacheService.removeCache("key-list");
//        cacheService.removeCache("key-list2");
//    }
}
