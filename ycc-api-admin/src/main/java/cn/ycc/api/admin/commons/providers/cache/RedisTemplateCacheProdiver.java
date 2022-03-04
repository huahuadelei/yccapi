package cn.ycc.api.admin.commons.providers.cache;

import cn.ycc.api.admin.commons.providers.CacheProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 21:05
 */
public class RedisTemplateCacheProdiver implements CacheProvider {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public void setCache(String cacheName, Object value) {
        redisTemplate.boundValueOps(cacheName).set(value);
    }

    @Override
    public void setCache(String cacheName, Object value, long timeout, TimeUnit timeUnit) {
         redisTemplate.boundValueOps(cacheName).set(value,timeout,timeUnit);
    }

    @Override
    public boolean expire(String cacheName, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(cacheName,timeout,timeUnit);
    }

    @Override
    public <T> T getCache(String cacheName) {
        return (T)redisTemplate.boundValueOps(cacheName).get();
    }

    @Override
    public boolean removeCache(String cacheName) {
       return redisTemplate.delete(cacheName);
    }
}
