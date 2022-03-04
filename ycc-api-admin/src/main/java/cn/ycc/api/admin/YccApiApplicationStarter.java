package cn.ycc.api.admin;

import cn.ycc.api.admin.commons.annotation.EnableSaveSysLog;
import cn.ycc.api.admin.commons.utils.GlobalPropertiesUtils;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("cn.ycc.api.admin.mapper")
@EnableSaveSysLog
public class YccApiApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(YccApiApplicationStarter.class,args);
    }

}
