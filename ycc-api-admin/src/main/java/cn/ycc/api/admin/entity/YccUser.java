package cn.ycc.api.admin.entity;

import cn.ycc.api.admin.commons.base.BaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("ycc_user")
@Data
public class YccUser extends BaseEntity {

    private String username;

    private String email;

    @JSONField(serialize=false)
    private String password;

    @JSONField(serialize=false)
    private String passSalt;

    private String roleId;

    @TableField(exist = false)
    private String roleName;

    @TableField(exist = false)
    private String oldPass;

}