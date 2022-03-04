package cn.ycc.api.admin.entity;

import cn.ycc.api.admin.commons.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ycc_api_view_group")
public class YccApiViewGroup extends BaseEntity {

    private String projectId;

    private String viewGroupName;

    private String createUserId;

    private Integer isTestable;

    private Integer hasPass;

    private String showPass;

    private Integer isExp;

    private Date expDate;

    @TableField(exist = false)
    private Integer viewStatus;

    @TableField(exist = false)
    private String likeKey;


}