package cn.ycc.api.admin.commons.enums;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.09 14:22
 */
public enum JxlsTempType {
    VIEW_EXPORT("view-export-template.xlsx");

    private String tempName;

    JxlsTempType(String tempName) {
        this.tempName = tempName;
    }

    public String getTempName() {
        return tempName;
    }
}
