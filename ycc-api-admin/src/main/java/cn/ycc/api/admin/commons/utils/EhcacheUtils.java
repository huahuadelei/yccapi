package cn.ycc.api.admin.commons.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 22:33
 */
public class EhcacheUtils {
    private static final CacheManager cacheManager ;
    static {
        cacheManager = CacheManager.create(EhcacheUtils.class.getClassLoader().getResourceAsStream("ehcache.xml"));
    }
    public static Cache getCache(String cacheName){
        return cacheManager.getCache(cacheName);
    }
}
