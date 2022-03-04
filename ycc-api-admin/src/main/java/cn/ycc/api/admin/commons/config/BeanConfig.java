package cn.ycc.api.admin.commons.config;

import cn.ycc.api.admin.commons.Constants;
import cn.ycc.api.admin.commons.ext.SysLogConsumer;
import cn.ycc.api.admin.commons.ext.YccSysLogManager;
import cn.ycc.api.admin.commons.utils.HttpClientHelper;
import cn.ycc.api.admin.commons.utils.MailHelper;
import cn.ycc.api.admin.mapper.YccSysLogsMapper;
import cn.ycc.api.admin.service.config.ConfigHolder;
import cn.ycc.api.admin.service.config.ConfigManager;
import cn.ycc.api.admin.service.config.listener.MailConfigRefreshConfigListener;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.26 21:24
 */
@Configuration
public class BeanConfig {

    @Autowired
    private YccSysLogsMapper sysLogsMapper;

    @Bean
    @ConditionalOnMissingBean
    public SysLogConsumer sysLogConsumer() {
        return (logs) -> {
            sysLogsMapper.insert(logs);
        };
    }

    @Bean
    public YccSysLogManager sysLogManager(SysLogConsumer sysLogConsumer) {
        return new YccSysLogManager(sysLogConsumer);
    }

    @Bean
    public HttpClientHelper.HttpClientConfig httpClientConfig() {
        HttpClientHelper.HttpClientConfig httpClientConfig = new HttpClientHelper.HttpClientConfig();
        httpClientConfig.setTimeout(20000L);
        return httpClientConfig;
    }

    @Bean
    public HttpClientHelper httpClientHelper(HttpClientHelper.HttpClientConfig httpClientConfig) {
        HttpClientHelper httpClientHelper = new HttpClientHelper(httpClientConfig);
        return httpClientHelper;
    }
    @Bean
    public MailHelper mailHelper() {
        MailHelper.MailConfig mailConfig = new  MailHelper.MailConfig();
        mailConfig.setProtocol("1");
        mailConfig.setHost("1");
        mailConfig.setUsername("1");
        mailConfig.setPassword("1");
        mailConfig.setPort(1);
        mailConfig.useSSL(true);
        return new MailHelper(mailConfig);
    }

    @Bean
    @ConditionalOnBean(ConfigManager.class)
    public MailHelper mailHelper(ConfigManager configManager) {
        ConfigHolder config = configManager.getConfig(Constants.EMAIL_CONFIG_KEY);

        MailHelper.MailConfig mailConfig = new  MailHelper.MailConfig();
        mailConfig.setProtocol(config.getValue("protocol").toString());
        mailConfig.setHost(config.getValue("host").toString());
        mailConfig.setUsername(config.getValue("username").toString());
        mailConfig.setPassword(config.getValue("password").toString());
        mailConfig.setPort(Integer.valueOf(config.getValue("port").toString()));
        mailConfig.useSSL(Boolean.parseBoolean(config.getValue("useSsl").toString()));

        configManager.addConfigListener(new MailConfigRefreshConfigListener(mailConfig));

        return new MailHelper(mailConfig);
    }

    /**
     *  mailHelper 创建实例的条件
     */
    public static class MailHelperCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConfigManager manager = context.getBeanFactory().getBean(ConfigManager.class);
            ConfigHolder config = manager.getConfig(Constants.EMAIL_CONFIG_KEY);
            return config != null;
        }
    }


}

