package cn.ycc.api.admin.commons.utils;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.util.ObjectUtils;

import java.io.File;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 20:18
 */
public class ApplicationUtils {

    private static final String PROJECT_HOME_NAME = "project.home";
    private static final String JXLS_TEMPLATE_PATH_NAME = "jxls.template-dir";
    private static final String DEFAULT_JXLS_TEMPLATE_PATH = getProjectHome() + File.separator+"config"+ File.separator+"temp"+File.separator;

    private static final String DEFAULT_CACHE_USE = "embd";
    private static final String DEFAULT_DB_USE = "embd";
    private static final String DB_USE_NAME = "db.use";
    private static final String CACHE_USE_NAME = "cache.use";

    private static final String EMBEDDED_DB_DRIVER_CLASS_NAME="org.h2.Driver";
    private static final String EMBEDDED_DB_URL="jdbc:h2:"+getProjectHome()+"/data/h2-data;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE";
    private static final String EMBEDDED_DB_USERNAME="ycc-api";
    private static final String EMBEDDED_DB_PASSWORD="ycc123456";

    static {
        int index = EMBEDDED_DB_URL.lastIndexOf("/");
        File file = new File(EMBEDDED_DB_URL.substring(0, index));
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static boolean useEmbeddedCache() {
        return DEFAULT_CACHE_USE.equals(GlobalPropertiesUtils.getProperty(CACHE_USE_NAME, DEFAULT_CACHE_USE));
    }

    public static boolean useEmbeddedDataSource() {
        return DEFAULT_DB_USE.equals(GlobalPropertiesUtils.getProperty(DB_USE_NAME, DEFAULT_DB_USE));
    }

    public static String getProjectHome() {
        return GlobalPropertiesUtils.getProperty(PROJECT_HOME_NAME, GlobalPropertiesUtils.getProperty("user.dir"));
    }

    public static String getJxlsTempDir() {
        String property = GlobalPropertiesUtils.getProperty(JXLS_TEMPLATE_PATH_NAME);
        if (ObjectUtils.isEmpty(property)) {
            property = DEFAULT_JXLS_TEMPLATE_PATH;
        }
        return property;
    }

    public static void setEmbeddedDatasourceProperties(DruidDataSource dataSource) {
        dataSource.setDriverClassName(EMBEDDED_DB_DRIVER_CLASS_NAME);
        dataSource.setUrl(EMBEDDED_DB_URL);
        dataSource.setUsername(EMBEDDED_DB_USERNAME);
        dataSource.setPassword(EMBEDDED_DB_PASSWORD);
    }
}
