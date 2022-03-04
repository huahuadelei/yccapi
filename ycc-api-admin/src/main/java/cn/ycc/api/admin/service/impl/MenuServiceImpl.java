package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.commons.dto.MenuNode;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.utils.MenuTreeUtils;
import cn.ycc.api.admin.entity.YccMenu;
import cn.ycc.api.admin.mapper.YccMenuMapper;
import cn.ycc.api.admin.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<YccMenuMapper, YccMenu> implements MenuService {

    @Override
    public List<MenuNode> getMenuTree() throws YccException {
        List<YccMenu> menus = baseMapper.selectList(new QueryWrapper<>());
        List<MenuNode> menuNodes = MenuTreeUtils.buildMeneTree(menus);
        return menuNodes;
    }

    @Override
    @Transactional
    public void removeMenuById(String menuId) throws YccException {

        // 删除当前节点菜单
        baseMapper.deleteById(menuId);

        // 查询是否有子节点 有的化 递归删除
        List<YccMenu> yccMenus = baseMapper.selectList(new QueryWrapper<YccMenu>().eq("parent_id", menuId));
        if(!ObjectUtils.isEmpty(yccMenus)){
            for (YccMenu yccMenu : yccMenus) {
                removeMenuById(yccMenu.getId());
            }
        }
    }

    @Override
    public List<YccMenu> getMenuPermsByRoleId(String roleId) {
        List<YccMenu> yccMenus = baseMapper.selectMenuPermsByRoleId(roleId, null);
        return yccMenus;
    }
}
