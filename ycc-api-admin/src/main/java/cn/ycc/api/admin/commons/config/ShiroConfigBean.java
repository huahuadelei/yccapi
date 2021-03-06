package cn.ycc.api.admin.commons.config;

import cn.ycc.api.admin.commons.shiro.realm.CoutomRealm;
import cn.ycc.api.admin.commons.shiro.JwtTokenLoginAdapterFilter;
import cn.ycc.api.admin.commons.utils.PropertiesFileUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.*;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.23 18:33
 */
@Configuration
public class ShiroConfigBean {


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("authc",new JwtTokenLoginAdapterFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        Map<String, String> filters = PropertiesFileUtils.getFileProperties("shiro_filters");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filters);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(Realm realm){
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionIdCookieEnabled(false);
        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);


        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setRealm(realm);
        webSecurityManager.setSessionManager(defaultWebSessionManager);
        webSecurityManager.setRememberMeManager(new RememberMeManager() {
            @Override
            public PrincipalCollection getRememberedPrincipals(SubjectContext subjectContext) {
                return null;
            }

            @Override
            public void forgetIdentity(SubjectContext subjectContext) {

            }

            @Override
            public void onSuccessfulLogin(Subject subject, AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {

            }

            @Override
            public void onFailedLogin(Subject subject, AuthenticationToken authenticationToken, AuthenticationException e) {

            }

            @Override
            public void onLogout(Subject subject) {

            }
        });
        return webSecurityManager;
    }

    @Bean
    public Realm realm(){
        return new CoutomRealm();
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
