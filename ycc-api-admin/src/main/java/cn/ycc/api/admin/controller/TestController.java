package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.commons.dto.HttpRequestEntity;
import cn.ycc.api.admin.commons.dto.HttpResponseEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.utils.HttpClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Locale;

@RestController
@RequestMapping("test")
public class TestController {
    @Autowired
    private HttpClientHelper httpClientHelper;

    @PostMapping("/http/{method}")
    @LogInfo(value = "HTTP_CLIENT调用",ignore = true)
    public ResultBean request(@PathVariable("method") String method, @RequestBody HttpRequestEntity entity){
        HttpClientHelper.RequestMethod requestMethod = parseHttpMethod(method);

        HttpClientHelper.HttpClientRequest clientRequest = new HttpClientHelper.HttpClientRequest(entity.getUrl());
        clientRequest.setMethod(requestMethod);

        clientRequest.setData(entity.getRequestBody());
        clientRequest.setParams(entity.getRequestParam());

        if(!ObjectUtils.isEmpty(entity.getRequestHeaders())){
            HttpClientHelper.Headers headers = new HttpClientHelper.Headers();
            headers.putAll(entity.getRequestHeaders());
            clientRequest.setRequestHeaders(headers);
        }

        HttpClientHelper.HttpClientResult httpClientResult = httpClientHelper.request(clientRequest);
        if(httpClientResult.isError()&&httpClientResult.getThrowable()!=null){
            throw new YccException("http_500","调用接口失败:"+httpClientResult.getThrowable().getMessage());
        }

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        httpResponseEntity.setResponseStatus(httpClientResult.getResponseStatus());
        httpResponseEntity.setContentType(httpClientResult.getContentType());
        httpResponseEntity.setResponseHeaders(httpClientResult.getResponseHeaders());
        httpResponseEntity.setResponseBody(httpClientResult.getBodyToString());

        return ResultBean.builder().code("200").data(httpResponseEntity).build();
    }

    private HttpClientHelper.RequestMethod parseHttpMethod(String method) {
        String  toUpperCaseMethod = method.toUpperCase(Locale.getDefault());
        try {
            HttpClientHelper.RequestMethod requestMethod = HttpClientHelper.RequestMethod.valueOf(toUpperCaseMethod);
            return requestMethod;
        }catch (Exception e){
            throw new RuntimeException("无效的请求方式[method],只允许;"+ Arrays.toString(HttpClientHelper.RequestMethod.values()));
        }
    }
}
