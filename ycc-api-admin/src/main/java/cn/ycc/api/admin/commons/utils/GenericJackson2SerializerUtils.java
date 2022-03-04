package cn.ycc.api.admin.commons.utils;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 21:09
 */
public class GenericJackson2SerializerUtils {
    private GenericJackson2SerializerUtils() {
        throw  new UnsupportedOperationException("不允许创建实例");
    }


   private static final  GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer;
    static{
        jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
    }

    public static GenericJackson2JsonRedisSerializer getJackson2JsonRedisSerializer(){

        return jackson2JsonRedisSerializer;
    }

    public static byte[] serialize(Object target){
        return jackson2JsonRedisSerializer.serialize(target);
    }
    public static String serializeAsString(Object target){
        return new String(serialize(target));
    }

    public static <T> T deserialize(byte[] source){
        return (T)jackson2JsonRedisSerializer.deserialize(source);
    }

}
