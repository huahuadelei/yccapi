package cn.ycc.api.admin.commons.config;

import cn.ycc.api.admin.commons.utils.ApplicationUtils;
import cn.ycc.api.admin.commons.utils.JxlsExcelExportHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.09 14:12
 */
@Configuration
public class JxlsConfig {

    @Bean
    public JxlsExcelExportHelper jxlsExcelExportHelper(){
        return new JxlsExcelExportHelper(ApplicationUtils.getJxlsTempDir());
    }
}
