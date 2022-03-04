package cn.ycc.api.admin.commons.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chaoqunyu
 * HttpClient工具类
 * 需要依赖 fastjson 和 httpclient 包
 */
public class HttpClientHelper {

    /**
     * 默认的httpclientHelper实例
     */
    public static final HttpClientHelper DEFAULT;

    static {
        DEFAULT = new HttpClientHelper(HttpClientConfig.DEFAULT);
    }

    /**
     * 区分域名缓存http的上下文信息（Cookie等）
     */
    private static final Map<String, HttpClientContext> cacheHttpClientContext = new ConcurrentHashMap<>();

    /**
     * 请求拦拦截器
     */
    private final Set<HttpClientRequestInterceptor> requestInterceptors = new LinkedHashSet<>();
    /**
     * 响应拦截器
     */
    private final Set<HttpClientResultInterceptor> responseInterceptors = new LinkedHashSet<>();

    /**
     * 设置监听器 可以监听请求的状态
     */
    private HttpClientRequestListener listener;

    /**
     * 默认配置
     */
    private final HttpClientConfig config;

    private final CloseableHttpClient httpClient;

    public HttpClientHelper(HttpClientConfig config) {
        this.config = config;
        this.httpClient = HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    }

    /**
     * 添加请求拦截器
     *
     * @param interceptor
     */
    public void addRequestInterceptor(HttpClientRequestInterceptor interceptor) {
        this.requestInterceptors.add(interceptor);
    }

    /**
     * 添加响应拦截器
     *
     * @param interceptor
     */
    public void addResponseInterceptor(HttpClientResultInterceptor interceptor) {
        this.responseInterceptors.add(interceptor);
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public synchronized void setRequestListener(HttpClientRequestListener listener) {
        this.listener = listener;
    }

    public HttpClientResult get(String url, Map<String, List<String>> params) {
        HttpClientRequest httpClientRequest = new HttpClientRequest(url);
        httpClientRequest.setMethod(RequestMethod.GET);
        httpClientRequest.setParams(params);
        return request(httpClientRequest);
    }

    public HttpClientResult delete(String url, Map<String, List<String>> params) {
        HttpClientRequest httpClientRequest = new HttpClientRequest(url);
        httpClientRequest.setMethod(RequestMethod.DELETE);
        httpClientRequest.setParams(params);
        return request(httpClientRequest);
    }

    public HttpClientResult post(String url, Map<String, Object> body) {
        HttpClientRequest httpClientRequest = new HttpClientRequest(url);
        httpClientRequest.setMethod(RequestMethod.POST);
        httpClientRequest.setData(body);
        return request(httpClientRequest);
    }

    public HttpClientResult put(String url, Map<String, Object> body) {
        HttpClientRequest httpClientRequest = new HttpClientRequest(url);
        httpClientRequest.setMethod(RequestMethod.PUT);
        httpClientRequest.setData(body);
        return request(httpClientRequest);
    }

    public HttpClientResult request(HttpClientRequest request) {

        // 执行请求拦截器
        handleRequestInterceptor(request);

        try {
            RequestParser parser = new RequestParser(request, config);

            HttpRequestBase requestBase = getHttpRequestBase(parser);
            HttpClientContext httpClientContext = getHttpClientContext(parser);

            CloseableHttpResponse closeableHttpResponse = httpClient.execute(requestBase, httpClientContext);
            HttpClientResult result = buildHttpClientResult(closeableHttpResponse, request);

            // 执行结果拦截器
            result = handleResponseInterceptor(result);

            // 通知监听器执行成功
            onSuccess(request, result);

            return result;
        } catch (Exception e) {

            // 通知监听器执行失败
            onError(request, e);
            return new HttpClientResult("500", e);
        }
    }

    private HttpClientContext getHttpClientContext(RequestParser parser) {
        HttpClientContext httpClientContext = cacheHttpClientContext.get(parser.getDomain());

        if (httpClientContext == null) {
            synchronized (parser.getDomain()) {
                httpClientContext = cacheHttpClientContext.get(parser.getDomain());

                if (httpClientContext == null) {
                    httpClientContext = new HttpClientContext();
                    httpClientContext.setCookieStore(new BasicCookieStore());
                    cacheHttpClientContext.put(parser.getDomain(), httpClientContext);
                }
            }
        }
        return httpClientContext;
    }

    private HttpClientResult buildHttpClientResult(CloseableHttpResponse closeableHttpResponse, HttpClientRequest request) throws IOException {
        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();

        Headers headers = new Headers();
        for (Header header : closeableHttpResponse.getAllHeaders()) {
            headers.setHeader(header.getName(), header.getValue());
        }

        HttpEntity entity = closeableHttpResponse.getEntity();
        byte[] bytes = EntityUtils.toByteArray(entity);

        return new HttpClientResult(request, headers, String.valueOf(statusCode), bytes);
    }

    private void handleRequestInterceptor(HttpClientRequest request) {
        for (HttpClientRequestInterceptor requestInterceptor : requestInterceptors) {
            requestInterceptor.handlerRequest(request);
        }
    }

    private HttpClientResult handleResponseInterceptor(HttpClientResult result) {
        for (HttpClientResultInterceptor responseInterceptor : responseInterceptors) {
            result = responseInterceptor.handlerResult(result);
        }
        return result;
    }

    private void onError(HttpClientRequest request, Throwable e) {
        if (listener != null) {
            listener.onError(request, e);
        }
    }

    private void onSuccess(HttpClientRequest request, HttpClientResult result) {
        if (listener != null) {
            listener.onSuccess(request, result);
        }
    }

    private HttpRequestBase getHttpRequestBase(RequestParser request) throws URISyntaxException {
        HttpRequestBaseBuilder httpRequestBaseBuilder;

        switch (request.getMethod()) {
            case GET:
            case OPTIONS:
            case DELETE:
            case HEAD:
            case PATCH:
                httpRequestBaseBuilder = new NonBodyHttpRequestBaseBuilder();
                break;
            case POST:
            case PUT:
                httpRequestBaseBuilder = new CarryBodyHttpRequestBaseBuilder();
                break;
            default:
                throw new HttpClientRequestException("无效的请求方法");
        }

        return httpRequestBaseBuilder.build(request);
    }


    /**
     * 响应的数据封装
     */
    public static class HttpClientResult {

        private HttpClientRequest request;

        private Headers responseHeaders;
        private String responseStatus;
        private byte[] body;

        private Throwable throwable;

        public HttpClientResult(String responseStatus, Throwable throwable) {
            this.responseStatus = responseStatus;
            this.throwable = throwable;
        }

        public HttpClientResult(HttpClientRequest request, Headers responseHeaders, String responseStatus) {
            this.request = request;
            this.responseHeaders = responseHeaders;
            this.responseStatus = responseStatus;
        }

        public HttpClientResult(HttpClientRequest request, Headers responseHeaders, String responseStatus, byte[] body) {
            this.request = request;
            this.responseHeaders = responseHeaders;
            this.responseStatus = responseStatus;
            this.body = body;
        }

        public HttpClientRequest getRequest() {
            return request;
        }

        public Headers getResponseHeaders() {
            return responseHeaders;
        }

        public String getResponseStatus() {
            return responseStatus;
        }

        public String getBodyToString() {
            return body != null ? new String(body) : "";
        }

        public JSONArray getBodyToArray() {
            return JSON.parseArray(getBodyToString());
        }

        public JSONObject getBodyToObject() {
            return JSON.parseObject(getBodyToString());
        }

        public byte[] getBody() {
            return body;
        }

        public boolean isError() {
            return this.responseStatus == "500";
        }

        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public String toString() {
            return "HttpClientResult{" +
                    "request=" + request +
                    ", responseHeaders=" + responseHeaders +
                    ", responseStatus='" + responseStatus + '\'' +
                    ", body=" + getBodyToString() +
                    '}';
        }

        public String getContentType() {
            String contentType = responseHeaders.getHeader("ContentType");
            if(contentType==null){
                contentType = responseHeaders.getHeader("content-type");
            }
            if(contentType==null){
                contentType = responseHeaders.getHeader("Content-Type");
            }
            return contentType;
        }
    }

    /**
     * httpClient请求的封装
     */
    public static class HttpClientRequest {

        private String url;
        private RequestMethod method;
        private Headers requestHeaders;
        private Map<String, Object> data;
        private Map<String, List<String>> params;
        private Long timeout;

        public HttpClientRequest(String url) {
            this.url = url;
            this.method = RequestMethod.GET;
            this.requestHeaders = new Headers();
        }

        public HttpClientRequest(String url, RequestMethod method, Headers requestHeaders, Map<String, Object> data, Map<String, List<String>> params, Long timeout) {
            this.url = url;
            this.method = method;
            this.requestHeaders = requestHeaders;
            this.data = data;
            this.params = params;
            this.timeout = timeout;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public RequestMethod getMethod() {
            return method;
        }

        public void setMethod(RequestMethod method) {
            this.method = method;
        }

        public Headers getRequestHeaders() {
            return requestHeaders;
        }

        public void setRequestHeaders(Headers requestHeaders) {
            this.requestHeaders = requestHeaders;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public void addBodyData(String paramName, Object value) {
            if (this.data == null) {
                this.data = new HashMap<>();
            }

            this.data.put(paramName, value);
        }

        public Map<String, List<String>> getParams() {
            return params;
        }

        public void setParams(Map<String, List<String>> params) {
            this.params = params;
        }

        public void addParam(String paramName, String paramValue) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            List<String> paramList = this.params.computeIfAbsent(paramName, k -> new ArrayList<>(1));
            paramList.add(paramValue);
        }

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }

        @Override
        public String toString() {
            return "HttpClientRequest{" +
                    "url='" + url + '\'' +
                    ", method=" + method +
                    ", requestHeaders=" + requestHeaders +
                    ", data=" + data +
                    ", params=" + params +
                    ", timeout=" + timeout +
                    '}';
        }
    }

    public static class Headers extends HashMap<String, String> {

        public Headers() {
            super(5);
            setContentType("application/json;charset=utf-8");
        }

        public void setContentType(String contentType) {
            put("Content-Type", contentType);
        }

        public void setHeader(String headerName, String headerValue) {
            put(headerName, headerValue);
        }

        public String getHeader(String headerName) {
            return get(headerName);
        }

        public String[] getHeaderNames() {
            return keySet().toArray(new String[0]);
        }
    }

    public static enum RequestMethod {
        POST,
        GET,
        PUT,
        DELETE,
        OPTIONS,
        HEAD,
        PATCH
    }

    public static class HttpClientConfig {
        public static final HttpClientConfig DEFAULT;

        static {
            DEFAULT = new HttpClientConfig();
        }

        private Long timeout;
        private String baseURL;
        private Headers headers;

        public HttpClientConfig() {
            this.timeout = 10000L;
            this.headers = new Headers();
        }

        public HttpClientConfig(String baseURL) {
            this();
            this.baseURL = baseURL;
        }

        public HttpClientConfig(Long timeout, String baseURL, Headers headers) {
            this.timeout = timeout;
            this.baseURL = baseURL;
            this.headers = headers;
        }

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }

        public String getBaseURL() {
            return baseURL;
        }

        public void setBaseURL(String baseURL) {
            this.baseURL = baseURL;
        }

        public Headers getHeaders() {
            return headers;
        }

        public void setHeaders(Headers headers) {
            this.headers = headers;
        }
    }

    static class RequestParser {

        private String domain;
        private String url;
        private RequestMethod method;
        private Headers requestHeaders;
        private Map<String, Object> data;
        private Map<String, List<String>> params;
        private long timeout;

        public RequestParser(HttpClientRequest request, HttpClientConfig config) {
            this.timeout = request.getTimeout() != null ? request.getTimeout() : config.getTimeout();
            this.method = request.getMethod();
            this.data = request.getData();
            this.params = request.getParams();

            initUrl(request, config);
            initHeaders(request, config);

            initDomain(config);
        }


        private void initDomain(HttpClientConfig config) {

            if (this.url.startsWith("http://") ||
                    this.url.startsWith("https://")) {
                DomainMatcher domainMatcher = new HttpDomainMatcher();
                this.domain = domainMatcher.matche(this.url);
                return;
            }

            String baseURL = config.getBaseURL();
            if (baseURL == null || baseURL.length() == 0) {
                throw new HttpClientRequestException("您的请求中包含无效的URL");
            }
            this.domain = baseURL;
        }

        private void initHeaders(HttpClientRequest request, HttpClientConfig config) {
            Headers headers = config.getHeaders();
            Headers requestHeaders = request.getRequestHeaders();

            this.requestHeaders = new Headers();
            this.requestHeaders.putAll(headers);
            this.requestHeaders.putAll(requestHeaders);
        }

        private void initUrl(HttpClientRequest request, HttpClientConfig clientConfig) {
            String baseURL = clientConfig.getBaseURL();
            baseURL = baseURL == null ? "" : baseURL.trim() + "/";

            String url = request.getUrl().trim();
            this.url = baseURL + url;
            this.url.replaceAll("/+", "/");

            // 创建url参数
            Map<String, List<String>> params = request.getParams();
            if (params != null && !params.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                boolean paramFlag = this.url.contains("?");

                if (paramFlag) {
                    builder.append("&");
                }

                for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                    for (String param : entry.getValue()) {
                        builder.append(entry.getKey()).append("=").append(param).append("&");
                    }
                }
                builder.deleteCharAt(builder.length() - 1);
                this.url = this.url + (paramFlag ? "" : "?") + builder.toString();
            }
        }

        public String getDomain() {
            return domain;
        }

        public String getUrl() {
            return url;
        }

        public RequestMethod getMethod() {
            return method;
        }

        public Headers getRequestHeaders() {
            return requestHeaders;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public Map<String, List<String>> getParams() {
            return params;
        }

        public long getTimeout() {
            return timeout;
        }

        interface DomainMatcher {
            String matche(String url);
        }

        static class HttpDomainMatcher implements DomainMatcher {
            private final Pattern DOMIAN_PATTERN = Pattern.compile("^(http://|https://)((.*?)/|(.*?)$)");

            @Override
            public String matche(String url) {
                Matcher matcher = DOMIAN_PATTERN.matcher(url);
                if (matcher.find()) {
                    return matcher.group();
                }
                return null;
            }
        }
    }

    public static interface HttpClientRequestInterceptor {
        void handlerRequest(HttpClientRequest request);
    }

    public static interface HttpClientResultInterceptor {
        HttpClientResult handlerResult(HttpClientResult result);
    }

    public static interface HttpClientRequestListener {
        void onSuccess(HttpClientRequest request, HttpClientResult result);

        void onError(HttpClientRequest request, Throwable throwable);
    }

    public static class HttpClientRequestException extends RuntimeException {
        public HttpClientRequestException() {
        }

        public HttpClientRequestException(String message) {
            super(message);
        }

        public HttpClientRequestException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpClientRequestException(Throwable cause) {
            super(cause);
        }

        public HttpClientRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    interface HttpRequestBaseBuilder {
        HttpRequestBase build(RequestParser requestParser) throws URISyntaxException;
    }

    abstract static class AbstractHttpRequestBaseBuilder implements HttpRequestBaseBuilder {
        @Override
        public HttpRequestBase build(RequestParser requestParser) throws URISyntaxException {
            HttpRequestBase httpRequestBase = this.newHttpRequestBase(requestParser);
            if (httpRequestBase == null) {
                throw new HttpClientRequestException("HttpRequestBase不能设置为空");
            }
            if (httpRequestBase.getURI() == null) {
                httpRequestBase.setURI(new URI(requestParser.getUrl()));
            }

            // 设置请求配置 超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout((int) requestParser.getTimeout()).setSocketTimeout((int) requestParser.getTimeout()).build();
            httpRequestBase.setConfig(requestConfig);

            // 设置http版本
            httpRequestBase.setProtocolVersion(HttpVersion.HTTP_1_1);

            // 设置请求头
            Headers requestHeaders = requestParser.getRequestHeaders();
            if (requestHeaders != null) {
                for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                    httpRequestBase.addHeader(entry.getKey(), entry.getValue());
                }
            }
            handleHttpBody(httpRequestBase, requestParser);
            return httpRequestBase;
        }

        protected abstract HttpRequestBase newHttpRequestBase(RequestParser requestParser);

        protected void handleHttpBody(HttpRequestBase httpRequestBase, RequestParser requestParser) {
            // hook method Non Body
        }
    }

    static class NonBodyHttpRequestBaseBuilder extends AbstractHttpRequestBaseBuilder {

        @Override
        protected HttpRequestBase newHttpRequestBase(RequestParser requestParser) {
            HttpRequestBase httpRequestBase = null;
            if (requestParser.getMethod() == RequestMethod.GET) {
                httpRequestBase = new HttpGet();
            } else if (requestParser.getMethod() == RequestMethod.OPTIONS) {
                httpRequestBase = new HttpOptions();
            } else if (requestParser.getMethod() == RequestMethod.DELETE) {
                httpRequestBase = new HttpDelete();
            } else if (requestParser.getMethod() == RequestMethod.HEAD) {
                httpRequestBase = new HttpHead();
            } else if (requestParser.getMethod() == RequestMethod.PATCH) {
                httpRequestBase = new HttpPatch();
            }
            return httpRequestBase;
        }
    }

    static class CarryBodyHttpRequestBaseBuilder extends AbstractHttpRequestBaseBuilder {

        @Override
        protected HttpRequestBase newHttpRequestBase(RequestParser requestParser) {
            HttpRequestBase httpRequestBase = null;
            if (requestParser.getMethod() == RequestMethod.POST) {
                httpRequestBase = new HttpPost();
            } else if (requestParser.getMethod() == RequestMethod.PUT) {
                httpRequestBase = new HttpPut();
            }

            return httpRequestBase;
        }

        @Override
        protected void handleHttpBody(HttpRequestBase httpRequestBase, RequestParser requestParser) {
            if(requestParser.getData()==null){
                return ;
            }
            StringEntity stringEntity = new StringEntity(JSON.toJSONString(requestParser.getData()), ContentType.APPLICATION_JSON);

            if (httpRequestBase instanceof HttpPost) {
                HttpPost httpPost = (HttpPost) httpRequestBase;
                httpPost.setEntity(stringEntity);
            } else if (httpRequestBase instanceof HttpPut) {
                HttpPut httpPut = (HttpPut) httpRequestBase;
                httpPut.setEntity(stringEntity);
            }
        }
    }


    public static void main(String[] args) {

        HttpClientHelper httpClientHelper = new HttpClientHelper(HttpClientConfig.DEFAULT);

        // 添加监听器
        httpClientHelper.setRequestListener(new HttpClientRequestListener() {
            @Override
            public void onSuccess(HttpClientRequest request, HttpClientResult result) {
                System.out.println("执行成功：req=" + request + "\nres=" + result);
            }

            @Override
            public void onError(HttpClientRequest request, Throwable throwable) {
                System.out.println("执行异常：" + request + ",errorMsg=" + throwable);
            }
        });

        // 添加拦截器  可以多个
        httpClientHelper.addRequestInterceptor(new HttpClientRequestInterceptor() {
            @Override
            public void handlerRequest(HttpClientRequest request) {
                // 在请求拦截中可以对请求的参数做更改
                request.getRequestHeaders().setHeader("requestInterceptor", "测试");
            }
        });

        // 添加响应拦截器 可以多个
        httpClientHelper.addResponseInterceptor(new HttpClientResultInterceptor() {
            @Override
            public HttpClientResult handlerResult(HttpClientResult result) {
                // 响应拦截器可以根据响应内容 定制化新的响应的内容
                return result;
            }
        });


        // 创建请求声明请求方式
        HttpClientRequest httpClientRequest = new HttpClientRequest("http://localhost:8099/ycc-api-admin/user/login");
        httpClientRequest.setMethod(RequestMethod.POST);

        // 设置请求头信息
        httpClientRequest.getRequestHeaders().setHeader("token", "123");

        // 添加url参数
        httpClientRequest.addParam("a", "1");
        httpClientRequest.addParam("b", "2");
        httpClientRequest.addParam("a", "3");

        //添加请求体数据
        httpClientRequest.addBodyData("username", "admin");
        httpClientRequest.addBodyData("password", "admin");

        HttpClientResult result = httpClientHelper.request(httpClientRequest);
        System.out.println(result);
        if (result.isError()) {
            System.out.println(result.getThrowable());
        }

        HashMap<String, List<String>> paramsMap = new HashMap<>();
        paramsMap.put("test",Arrays.asList("1","2"));
        // 简单请求 如果想自己拼接参数,第二个参数传null
        HttpClientResult clientResult = httpClientHelper.get("http://www.baidu.com/?s=哈哈哈", paramsMap);
    }
}
