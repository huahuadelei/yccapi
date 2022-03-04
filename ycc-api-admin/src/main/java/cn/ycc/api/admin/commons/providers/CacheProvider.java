package cn.ycc.api.admin.commons.providers;

import java.util.concurrent.TimeUnit;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 20:51
 */
public interface CacheProvider {

      void setCache(String cacheName,Object value);

      void setCache(String cacheName,Object value, long timeout, TimeUnit timeUnit);

      boolean expire(String cacheName, long timeout, TimeUnit timeUnit);

     <T>T getCache(String cacheName);

     <T> boolean removeCache(String cacheName);
}
