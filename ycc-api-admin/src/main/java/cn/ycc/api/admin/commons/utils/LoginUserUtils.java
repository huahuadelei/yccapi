package cn.ycc.api.admin.commons.utils;

import cn.ycc.api.admin.commons.context.ApplicationContextHolder;
import cn.ycc.api.admin.commons.providers.CacheProvider;
import cn.ycc.api.admin.commons.shiro.po.LoginUser;
import cn.ycc.api.admin.entity.YccUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:36
 */
public class LoginUserUtils {
    private static final CacheProvider CACHE_PROVIDER = (CacheProvider) ApplicationContextHolder.getBean(CacheProvider.class);
    private static final String CACHE_LOGIN_USER_KEY_PREFIX = "LOGIN_USER:";
    public static final long USER_LOGIN_EXPIRE_AT = 1000 * 60 * 60 * 24;

    public static LoginUser getLoginUserInfo(String userId) {
        LoginUser loginUser = CACHE_PROVIDER.getCache(CACHE_LOGIN_USER_KEY_PREFIX + userId);
        if (loginUser != null) {
            CACHE_PROVIDER.expire(CACHE_LOGIN_USER_KEY_PREFIX + userId,USER_LOGIN_EXPIRE_AT, TimeUnit.MILLISECONDS);
        }
        return loginUser;
    }

    public static void setLoginUserInfo(LoginUser loginUserInfo) {
        YccUser user = loginUserInfo.getUser();
        if(loginUserInfo==null||user == null){
            throw new IllegalArgumentException("user 对象不能为空");
        }
        user .setPassword(null);
        user .setPassSalt(null);
        CACHE_PROVIDER.setCache(CACHE_LOGIN_USER_KEY_PREFIX + user.getId(),loginUserInfo,USER_LOGIN_EXPIRE_AT,TimeUnit.MILLISECONDS);
    }

    public static  String getCurrentUser() {
        Session session = SecurityUtils.getSubject().getSession();
        return (String)session.getAttribute("userId");
    }

    public static  void setCurrentUser(String userId) {
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userId",userId);
    }

    public static void removeLoginUserInfo(String userId) {
        CACHE_PROVIDER.removeCache(CACHE_LOGIN_USER_KEY_PREFIX + userId);
    }
}
