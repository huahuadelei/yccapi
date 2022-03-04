package cn.ycc.api.admin.service.config;

import cn.ycc.api.admin.entity.YccSysConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonConfigHolder implements ConfigHolder {

    private YccSysConfig sysConfig;
    private JSONObject jsonObject;

    public JsonConfigHolder(YccSysConfig sysConfig) {
        this.sysConfig = sysConfig;
        this.jsonObject = sysConfig.getConfigContent() == null ? null : JSON.parseObject(sysConfig.getConfigContent());
    }

    @Override
    public YccSysConfig getOriginConfig() {
        return sysConfig;
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("不支持查询默认请使用带参数的方法");
    }

    @Override
    public Object getValue(String key) {
        if(jsonObject == null){
            return null;
        }
        return jsonObject.get(key);
    }

    public String[] getKeyNames(){
        return jsonObject.keySet().toArray(new String[0]);
    }
}
