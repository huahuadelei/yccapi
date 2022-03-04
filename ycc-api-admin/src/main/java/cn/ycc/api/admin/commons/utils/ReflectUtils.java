package cn.ycc.api.admin.commons.utils;

import java.lang.reflect.Field;

public abstract class ReflectUtils {

    public static void $set(Object target,String columnName,Object value) throws IllegalAccessException {
        Field field = getClassField(target,columnName);
        field.set(target,value);
    }



    public static Object $get(Object target,String columnName) throws IllegalAccessException {
        Field field = getClassField(target,columnName);
       return field.get(target);
    }

    private static Field getClassField(Object target, String columnName) {
        Class<?> targetClass = target.getClass();
        try {
            Field declaredField = targetClass.getDeclaredField(columnName);
            if(!declaredField.isAccessible()){
                declaredField.setAccessible(true);
            }
            return declaredField;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(columnName+" column is not found in ["+targetClass.getName()+"]");
        }
    }
}
