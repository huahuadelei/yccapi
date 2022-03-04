package cn.ycc.api.admin.commons.dto;

import cn.ycc.api.admin.commons.utils.HttpClientHelper;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class HttpRequestEntity implements Serializable {

    private String url;
    private String method;
    private Map<String,String> requestHeaders;
    private Map<String,Object> requestBody;
    private Map<String, List<String>> requestParam;

}
