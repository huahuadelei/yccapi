package cn.ycc.api.admin.service.config.listener;

import cn.ycc.api.admin.commons.Constants;
import cn.ycc.api.admin.commons.utils.MailHelper;
import cn.ycc.api.admin.service.config.ConfigHolder;
import cn.ycc.api.admin.service.config.listener.event.ConfigEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailConfigRefreshConfigListener implements ConfigListener<ConfigEvent> {

    private   MailHelper.MailConfig mailConfig;

    public MailConfigRefreshConfigListener(MailHelper.MailConfig mailConfig) {
        this.mailConfig = mailConfig;
    }

    @Override
    public void onEvent(ConfigEvent event) {
        log.info(event.toString());
        if(event.getEventType() == ConfigEvent.EventType.UPDATE && Constants.EMAIL_CONFIG_KEY.equals(event.getConfigKey())){
            ConfigHolder config = event.getConfigHolder();

            mailConfig.setProtocol(config.getValue("protocol").toString());
            mailConfig.setHost(config.getValue("host").toString());
            mailConfig.setUsername(config.getValue("username").toString());
            mailConfig.setPassword(config.getValue("password").toString());
            mailConfig.setPort(Integer.valueOf(config.getValue("port").toString()));
            mailConfig.useSSL(Boolean.parseBoolean(config.getValue("useSsl").toString()));

        }
    }
}
