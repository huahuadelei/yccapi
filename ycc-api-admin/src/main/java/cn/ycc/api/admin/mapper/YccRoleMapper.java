package cn.ycc.api.admin.mapper;

import cn.ycc.api.admin.entity.YccRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YccRoleMapper extends BaseMapper<YccRole> {

    /**
     * 删除指定角色的所有菜单
     * @param roleId
     * @return
     */
    int deleteRoleMenuAll(@Param("roleId") String roleId);

    int insertRolePerms(@Param("id") String id,@Param("roleId") String roleId,@Param("menuId")String menuId);
}
