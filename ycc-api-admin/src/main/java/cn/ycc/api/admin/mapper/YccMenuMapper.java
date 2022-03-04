package cn.ycc.api.admin.mapper;

import cn.ycc.api.admin.entity.YccMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YccMenuMapper extends BaseMapper<YccMenu> {

    /**
     *  获取角色所有菜单权限
     * @param roleId 角色 Id
     * @Param isMenu 是否查询菜单 1查询菜单 0查询权限 null查询全部
     * @return
     */
    List<YccMenu> selectMenuPermsByRoleId(@Param("roleId") String roleId,@Param("isMenu")  Integer isMenu);
}
