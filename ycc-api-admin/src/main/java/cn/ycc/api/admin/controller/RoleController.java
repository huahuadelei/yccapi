package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.commons.dto.RolePermEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccMenu;
import cn.ycc.api.admin.entity.YccRole;
import cn.ycc.api.admin.entity.YccUser;
import cn.ycc.api.admin.service.MenuService;
import cn.ycc.api.admin.service.RoleService;
import cn.ycc.api.admin.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.29 11:30
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @PostMapping("")
    @LogInfo("更新角色信息")
    public ResultBean saveAndUpdate(@RequestBody YccRole role){
        if(ObjectUtils.isEmpty(role.getId())){
            role.setCreateTime(new Date());
        }
        role.setUpdateTime(new Date());
        roleService.saveOrUpdate(role);
        return ResultBean.builder().code("200").msg("操作成功").build();
    }

    @GetMapping("")
    @LogInfo(value = "查询所有角色",ignore = true)
    public ResultBean queryRoleAll(){
        List<YccRole> list = roleService.list(new QueryWrapper<YccRole>().orderByAsc("create_time"));
        return ResultBean.builder().code("200").data(list).build();
    }

    @DeleteMapping("/{roleId}")
    @LogInfo("删除角色信息")
    @RequiresRoles("Administrator")
    public ResultBean removeRole(@PathVariable String roleId){
        if(roleId==null){
            throw new YccException("roleId不能为null");
        }
        YccRole yccRole = roleService.getById(roleId);
        if("Administrator".equalsIgnoreCase(yccRole.getRoleCode())){
            throw new YccException("[Administrator]是系统默认角色不能删除");
        }
        YccUser yccUser = new YccUser();
        yccUser.setRoleId(roleId);
        List<YccUser> list = userService.list(new QueryWrapper<YccUser>(yccUser));

        if(!ObjectUtils.isEmpty(list)){
            throw new YccException("该角色正在被某用户使用，请变更用户角色后在尝试此删除操作");
        }

        roleService.removeById(roleId);
        return ResultBean.builder().code("200").msg("删除成功").build();
    }

    @GetMapping("/{roleId}")
    @LogInfo(value = "按ID查询角色",ignore = true)
    public ResultBean getById(@PathVariable("roleId") String roleId){
        YccRole yccRole = roleService.getById(roleId);
        return ResultBean.builder().code("200").data(yccRole).build();
    }

    @GetMapping("/perms/{roleId}")
    @LogInfo(value = "获取角色权限",ignore = true)
    public ResultBean queryMenuPermsByRoleId(@PathVariable("roleId") String roleId){
        if(roleId==null){
            throw new YccException("RoleId不能为null");
        }
        List<YccMenu> yccMenus = menuService.getMenuPermsByRoleId(roleId);
        return ResultBean.builder().code("200").data(yccMenus).build();
    }

    @PostMapping("/perms/update")
    @LogInfo("角色更新权限")
    @RequiresRoles("Administrator")
    public ResultBean updateRolePerm(@RequestBody RolePermEntity  rolePermEntity){
        if(rolePermEntity.getRoleId()==null){
            throw new YccException("RoleId不能为null");
        }
        roleService.updateRolePerm(rolePermEntity);
        return ResultBean.builder().code("200").msg("更新成功").build();
    }

}
