package cn.ycc.api.admin.commons.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ExportViewExtVo implements Serializable {
    private String versionName;
    private String projectName;
    private Map<String,String> groupNameMapping;
    private List<ExportApiInfoVo> apiInfoVos;

    public String getGroupNameById(String groupId){
        return groupNameMapping.get(groupId);
    }
}