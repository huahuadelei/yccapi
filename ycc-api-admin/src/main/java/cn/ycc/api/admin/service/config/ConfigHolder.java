package cn.ycc.api.admin.service.config;

import cn.ycc.api.admin.entity.YccSysConfig;

public interface ConfigHolder {

    YccSysConfig getOriginConfig();

    Object getValue();

    Object getValue(String key);

}
