package cn.ycc.api.admin.service.config;

import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.entity.YccSysConfig;
import cn.ycc.api.admin.service.SysConfigService;
import cn.ycc.api.admin.service.config.listener.ConfigListener;
import cn.ycc.api.admin.service.config.listener.event.ConfigEvent;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ConfigManager {

    private List<ConfigListener> listenerList;
    @Autowired
    private SysConfigService sysConfigService;

    public ConfigManager() {
        listenerList = new ArrayList<>();
    }

    public final void addConfigListener(ConfigListener listener){
        this.listenerList.add(listener);
    }

    public ConfigHolder getConfig(String configKey){
        YccSysConfig yccSysConfig = new YccSysConfig();
        yccSysConfig.setConfigKey(configKey);
        YccSysConfig sysConfig = sysConfigService.getOne(new QueryWrapper<>(yccSysConfig));
        ConfigHolder configHolder = wrapConfig(sysConfig);

        ConfigEvent configEvent = new ConfigEvent(this, ConfigEvent.EventType.QUERY, configKey, configHolder);
        publishConfigEvent(configEvent);

        return configHolder;
    }
    public void updateConfig(String configKey,YccSysConfig config) {
        YccSysConfig queryEntity = new YccSysConfig();
        queryEntity.setConfigKey(configKey);
        YccSysConfig sysConfig = sysConfigService.getOne(new QueryWrapper<>(queryEntity));

        config.setUpdateTime(new Date());
        if (sysConfig != null) {
            config.setId(sysConfig.getId());
            sysConfigService.updateById(config);
            ConfigEvent configEvent = new ConfigEvent(this, ConfigEvent.EventType.UPDATE, configKey, wrapConfig(config));
            publishConfigEvent(configEvent);
        } else {
            config.setCreateTime(new Date());
            sysConfigService.save(config);
            ConfigEvent configEvent = new ConfigEvent(this, ConfigEvent.EventType.CREATE, configKey, wrapConfig(config));
            publishConfigEvent(configEvent);
        }

    }

    private void publishConfigEvent(ConfigEvent configEvent) {
        if(listenerList.isEmpty()){
            return;
        }
        for (ConfigListener listener : listenerList) {
            listener.onEvent(configEvent);
        }
    }

    private ConfigHolder wrapConfig(YccSysConfig sysConfig) {
        if(sysConfig==null){
            return null;
        }

        Integer configType = sysConfig.getConfigType();
        ConfigHolder configHolder = null;
        if(configType == 1){
            configHolder = new SimpleConfigHolder(sysConfig);
        }else if(configType == 2){
            configHolder = new JsonConfigHolder(sysConfig);
        }
        return configHolder;
    }

}
