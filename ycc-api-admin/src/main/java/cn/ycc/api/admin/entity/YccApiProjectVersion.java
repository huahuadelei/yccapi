package cn.ycc.api.admin.entity;

import cn.ycc.api.admin.commons.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ycc_api_projects_versions")
public class YccApiProjectVersion extends BaseEntity {
    private String projectId;
    private String versionInfo;

    @TableField(exist = false)
    private String copyVersionId;
}