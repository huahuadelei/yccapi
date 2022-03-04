package cn.ycc.api.admin.entity;

import cn.ycc.api.admin.commons.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("ycc_api_projects")
public class YccApiProjects extends BaseEntity {

    private String projectDesc;

    private String projectName;

    private String profileTestUrl;

    private String profileDevUrl;

    private String defVersionId;

    @TableField(exist = false)
    private List<YccApiProjectVersion> versionList;

    @TableField(exist = false)
    private String versionInfo;


}