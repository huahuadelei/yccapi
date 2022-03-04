package cn.ycc.api.admin.service.config.listener.event;

import cn.ycc.api.admin.service.config.ConfigHolder;
import lombok.*;

import java.util.EventObject;
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ConfigEvent extends EventObject {

    public static enum EventType{
        UPDATE,
        CREATE,
        REMOVE,
        QUERY
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ConfigEvent(Object source) {
        super(source);
    }

    public ConfigEvent(Object source, EventType eventType, String configKey, ConfigHolder configHolder) {
        super(source);
        this.eventType = eventType;
        this.configKey = configKey;
        this.configHolder = configHolder;
    }

    private EventType eventType;
    private String configKey;
    private ConfigHolder configHolder;

}
