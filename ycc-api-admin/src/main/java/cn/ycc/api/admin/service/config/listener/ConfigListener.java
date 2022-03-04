package cn.ycc.api.admin.service.config.listener;

import cn.ycc.api.admin.service.config.listener.event.ConfigEvent;

@FunctionalInterface
public interface ConfigListener<E extends ConfigEvent> {

    void onEvent(E event);
}
