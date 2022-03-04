package cn.ycc.api.admin.commons.jxls.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomTools {

    private final Map<String,Integer> map = new ConcurrentHashMap<>();

    public void count(String key){

        Integer integer = map.get(key);
        if(integer==null){
            integer = 0;
        }
        map.put(key,++integer);
    }

    public Integer getCount(String key){

        Integer integer = map.get(key);
        if(integer==null){
            integer = 0;
        }
       return integer;
    }
}
