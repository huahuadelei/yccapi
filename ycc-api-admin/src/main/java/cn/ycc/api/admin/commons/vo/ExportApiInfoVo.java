package cn.ycc.api.admin.commons.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class ExportApiInfoVo implements Serializable {

    private String desc;
    private String method;
    private String url;
    private String groupId;
    private Headers headers;
    private ParamsVo paramsVo;
    private ResultVo resultVo;


    public static final class Headers extends ArrayList<Header> implements Serializable {
        public static final Headers EMPTY_HEADERS = new Headers();
    }

    public static final class Header implements Serializable {
        private String key;
        private String value;

        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }


    public static class ParamsVo implements Serializable {

        private boolean existsQuery = false;
        private boolean existsURL = false;
        private boolean existsBody = false;

        private JSONArray queryDatas;
        private JSONArray urlDatas;
        private JSONArray bodyDatas;

        public boolean isExistsQuery() {
            return existsQuery;
        }

        public void setExistsQuery(boolean existsQuery) {
            this.existsQuery = existsQuery;
        }

        public boolean isExistsURL() {
            return existsURL;
        }

        public void setExistsURL(boolean existsURL) {
            this.existsURL = existsURL;
        }

        public boolean isExistsBody() {
            return existsBody;
        }

        public void setExistsBody(boolean existsBody) {
            this.existsBody = existsBody;
        }

        public JSONArray getQueryDatas() {
            return queryDatas;
        }

        public void setQueryDatas(JSONArray queryDatas) {
            this.queryDatas = queryDatas;
        }

        public JSONArray getUrlDatas() {
            return urlDatas;
        }

        public void setUrlDatas(JSONArray urlDatas) {
            this.urlDatas = urlDatas;
        }

        public JSONArray getBodyDatas() {
            return bodyDatas;
        }

        public void setBodyDatas(JSONArray bodyDatas) {
            this.bodyDatas = bodyDatas;
        }
    }

    public static final class ResultVo implements Serializable {
        private boolean isErrorData = false;
        private boolean isSuccessData = false;
        private JSONObject success;
        private JSONObject error;

        public boolean isErrorData() {
            return isErrorData;
        }

        public void setErrorData(boolean errorData) {
            isErrorData = errorData;
        }

        public boolean isSuccessData() {
            return isSuccessData;
        }

        public void setSuccessData(boolean successData) {
            isSuccessData = successData;
        }

        public JSONObject getSuccess() {
            return success;
        }

        public void setSuccess(JSONObject success) {
            this.success = success;
        }

        public JSONObject getError() {
            return error;
        }

        public void setError(JSONObject error) {
            this.error = error;
        }
    }


}
