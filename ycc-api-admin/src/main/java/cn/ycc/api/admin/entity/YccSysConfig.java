package cn.ycc.api.admin.entity;

import cn.ycc.api.admin.commons.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("ycc_sys_config")
@Data
public class YccSysConfig extends BaseEntity {

     private Integer configType;
     private String configKey;
     private String configValue;
     private String configContent;
     private String configDesc;

}
