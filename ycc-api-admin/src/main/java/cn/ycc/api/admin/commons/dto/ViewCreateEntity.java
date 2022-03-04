package cn.ycc.api.admin.commons.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ViewCreateEntity implements Serializable {

    private String viewGroupName;
    private String projectId;
    private String[] versions;
    private Integer hasPass;
    private Boolean autoGenerate;
    private String showPass;
    private String expTime;
    private Integer isTestable;
}
