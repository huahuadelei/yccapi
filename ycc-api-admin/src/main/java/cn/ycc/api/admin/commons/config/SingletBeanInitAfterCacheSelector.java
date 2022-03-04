package cn.ycc.api.admin.commons.config;

import cn.ycc.api.admin.commons.providers.CacheProvider;
import cn.ycc.api.admin.commons.providers.cache.EmbeddedCacheProdiver;
import cn.ycc.api.admin.commons.providers.cache.RedisTemplateCacheProdiver;
import cn.ycc.api.admin.commons.utils.ApplicationUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 21:30
 */
@Component
public class SingletBeanInitAfterCacheSelector implements SmartInitializingSingleton {
    @Autowired
    private DefaultListableBeanFactory listableBeanFactory;

    @Override
    public void afterSingletonsInstantiated() {
        CacheProvider cacheProvider;
        if(ApplicationUtils.useEmbeddedCache()){
            cacheProvider = new EmbeddedCacheProdiver("local");
        }else {
            cacheProvider = new RedisTemplateCacheProdiver();
            listableBeanFactory.autowireBean(cacheProvider);
        }

        listableBeanFactory.registerSingleton("cacheProvider",cacheProvider);
    }
}
