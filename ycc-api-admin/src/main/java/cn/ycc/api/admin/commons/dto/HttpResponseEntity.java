package cn.ycc.api.admin.commons.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class HttpResponseEntity implements Serializable {
    private String responseStatus;
    private Map<String, String> responseHeaders;
    private String contentType;
    private String responseBody;
}
