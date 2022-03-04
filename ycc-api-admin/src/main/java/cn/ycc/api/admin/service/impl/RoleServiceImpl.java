package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.commons.dto.RolePermEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.utils.IdWorker;
import cn.ycc.api.admin.entity.YccRole;
import cn.ycc.api.admin.mapper.YccRoleMapper;
import cn.ycc.api.admin.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:21
 */
@Service
public class RoleServiceImpl extends ServiceImpl<YccRoleMapper, YccRole> implements RoleService {

    @Override
    @Transactional
    public void updateRolePerm(RolePermEntity rolePermEntity) throws YccException {
        // 删除该角色所有菜单 从关联表去除
        baseMapper.deleteRoleMenuAll(rolePermEntity.getRoleId());
        if(!ObjectUtils.isEmpty(rolePermEntity.getMenuIds())){
            for (String menuId : rolePermEntity.getMenuIds()) {
                baseMapper.insertRolePerms(String.valueOf(IdWorker.DEFAULT.nextId()),rolePermEntity.getRoleId(),menuId);
            }
        }
    }
}
