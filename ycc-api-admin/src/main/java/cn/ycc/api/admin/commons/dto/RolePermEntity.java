package cn.ycc.api.admin.commons.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.29 20:38
 */
@Data
public class RolePermEntity implements Serializable {
    private String roleId;
    private List<String> menuIds;

}
