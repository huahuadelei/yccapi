package cn.ycc.api.admin.commons.enums;

public enum RequestHeaderNames {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ACCEPT_RANGES("Accept-Ranges"),
    AUTHORIZATION("Authorization"),
    CACHE_CONTROL ("Cache-Control"),
    CONNECTION("Connection"),
    COOKIE("Cookie"),
    USER_AGENT("User-Agent"),
    ;

    private String headerName;

    RequestHeaderNames(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}
