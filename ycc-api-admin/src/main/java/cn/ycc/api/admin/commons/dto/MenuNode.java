package cn.ycc.api.admin.commons.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuNode implements Serializable {

    private static final long serialVersionUID = 1695621522295877653L;
    private String menuId;
    private String menuName;
    private String menuTheme;
    private String routePath;
    private Boolean isParent = false;
    private String parentId;
    private List<MenuNode> children;
    private Boolean isMenu;
    private String permCode;
    private Integer sort;

    private Boolean _showChildren=true;

}
