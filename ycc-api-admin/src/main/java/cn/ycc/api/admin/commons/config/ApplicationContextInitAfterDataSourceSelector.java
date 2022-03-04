package cn.ycc.api.admin.commons.config;

import cn.ycc.api.admin.commons.utils.ApplicationUtils;
import cn.ycc.api.admin.commons.utils.ClassLoaderHelper;
import cn.ycc.api.admin.commons.utils.GlobalPropertiesUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.12 21:30
 */
public class ApplicationContextInitAfterDataSourceSelector implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String SCHEMA_SQL_FILE = "h2/schema.sql";
    private static final String SCHEMA_SQL_INITED_COUNT_QUERY = "select  count(1)  count from information_schema.tables where table_schema in ('PUBLIC','public')";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 使用嵌入式数据库
        if (ApplicationUtils.useEmbeddedDataSource()) {
            DruidDataSource dataSource = new DruidDataSource();
            ApplicationUtils.setEmbeddedDatasourceProperties(dataSource);
            applicationContext.getBeanFactory().registerSingleton("datasource", dataSource);

            // 第一次运行执行schema
            try {
                checkSchemaInited(dataSource);
            } catch (Exception e) {
                throw new RuntimeException("运行schema.sql异常");
            }
            GlobalPropertiesUtils.setProperty("spring.h2.console.enabled", "true");
            GlobalPropertiesUtils.setProperty("spring.h2.console.settings.web-allow-others", "true");
        }
    }

    private void runnerSchemaSql(String schemaResource, Connection connection) throws IOException {
        InputStream resourceAsStream = ClassLoaderHelper.DEFAULT.getResourceAsStream(schemaResource);
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        try {
            scriptRunner.runScript(new InputStreamReader(resourceAsStream));
        }finally {
            resourceAsStream.close();
        }
    }

    private boolean checkSchemaInited(DruidDataSource dataSource) throws Exception {
        DruidPooledConnection connection = null;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SCHEMA_SQL_INITED_COUNT_QUERY);

            if (resultSet.next()) {
                long count = resultSet.getLong("count");
                if (count == 0) {
                    runnerSchemaSql(SCHEMA_SQL_FILE,connection);
                    return true;
                }
            }
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return false;
    }
}
