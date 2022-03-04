package cn.ycc.api.admin.commons.providers.cache;

import cn.ycc.api.admin.commons.providers.CacheProvider;
import cn.ycc.api.admin.commons.utils.EhcacheUtils;
import cn.ycc.api.admin.commons.utils.GenericJackson2SerializerUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.util.concurrent.TimeUnit;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 21:42
 */
public class EmbeddedCacheProdiver implements CacheProvider {

    private final Cache cache;

    public EmbeddedCacheProdiver(String cacheName) {
        this.cache = EhcacheUtils.getCache(cacheName);
    }

    @Override
    public void setCache(String cacheName, Object value) {
        String asString = GenericJackson2SerializerUtils.serializeAsString(value);
        cache.put(new Element(cacheName,asString));
    }

    @Override
    public void setCache(String cacheName, Object value, long timeout, TimeUnit timeUnit) {
        setCache(cacheName,value);
    }

    @Override
    public boolean expire(String cacheName, long timeout, TimeUnit timeUnit) {
        return true;
    }

    @Override
    public <T> T getCache(String cacheName) {
        Element element = cache.get(cacheName);
        if(element == null){
            return null;
        }
        return (T)GenericJackson2SerializerUtils.deserialize(((String)element.getObjectValue()).getBytes());
    }

    @Override
    public boolean removeCache(String cacheName) {
        return cache.remove(cacheName);
    }
}
