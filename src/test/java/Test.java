import com.dianwoba.rha.service.CacheService;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/app-context-cache-sentinal.xml")
public class Test {

    @Resource
    private CacheService cacheService;

    @org.junit.Test
    public void test() {
        String key = "zput-test";
        cacheService.zput(key, 1, -1);
        cacheService.zput(key, 2, -2);
        cacheService.zput(key, 3, -3);
        cacheService.zput(key, 4, -4);
        System.out.println(cacheService.getRedisTemplate().opsForZSet().size(key));
        System.out.println(cacheService.getRedisTemplate().opsForZSet().rangeByScore(key, -4, -2));
        cacheService.removeCache(key);
    }
}
