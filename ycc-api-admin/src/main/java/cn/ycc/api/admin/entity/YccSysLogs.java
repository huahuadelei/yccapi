package cn.ycc.api.admin.entity;

import cn.ycc.api.admin.commons.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ycc_sys_logs")
public class YccSysLogs extends BaseEntity {
    private Integer successed;
    private String logInfo;
    private String ipAddr;
    private String userId;
    private Long invokeTimes;
    private String classMethod;
    private String reqUrl;
    private String reqMethod;
    private String reqHeader;
    private String reqData;
    private String respData;
    private String errorMsg;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String startDate;

    @TableField(exist = false)
    private String endDate;

}