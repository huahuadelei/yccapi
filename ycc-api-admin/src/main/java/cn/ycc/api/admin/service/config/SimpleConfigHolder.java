package cn.ycc.api.admin.service.config;

import cn.ycc.api.admin.entity.YccSysConfig;

public class SimpleConfigHolder implements ConfigHolder {

    private YccSysConfig yccSysConfig;

    public SimpleConfigHolder(YccSysConfig yccSysConfig) {
        this.yccSysConfig = yccSysConfig;
    }

    @Override
    public YccSysConfig getOriginConfig() {
        return yccSysConfig;
    }

    @Override
    public Object getValue() {
        return yccSysConfig.getConfigValue();
    }

    @Override
    public Object getValue(String key) {
        return yccSysConfig.getConfigValue();
    }
}
