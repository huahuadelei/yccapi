package cn.ycc.api.admin.commons.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;

import java.util.*;



public class GlobalPropertiesUtils implements EnvironmentPostProcessor {

    private static Properties extendProperties;
    private static Environment globalEnvironment;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        GlobalPropertiesUtils.globalEnvironment = environment;
        environment.getPropertySources().addLast(new PropertiesPropertySource("extendProperties", GlobalPropertiesUtils.extendProperties = new Properties()));
    }

    public static void setProperty(String propertyNmae,String propertyValue){
        GlobalPropertiesUtils.extendProperties.setProperty(propertyNmae,propertyValue);
    }

    public static String getProperty(String name,String defValue){
        return GlobalPropertiesUtils.globalEnvironment.getProperty(name,defValue);
    }

    public static String getProperty(String name){
        return getProperty(name,null);
    }


    public static boolean hasProperty(String propertyName){
        return GlobalPropertiesUtils.globalEnvironment.containsProperty(propertyName);
    }
}
