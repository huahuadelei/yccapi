package cn.ycc.api.admin.service;

import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.dto.RolePermEntity;
import cn.ycc.api.admin.commons.dto.ViewCreateEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.vo.ExportViewExtVo;
import cn.ycc.api.admin.entity.YccApiViewGroup;
import cn.ycc.api.admin.entity.YccRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:20
 */
public interface ViewService extends IService<YccApiViewGroup> {

    void saveView(ViewCreateEntity viewCreateEntity) throws YccException;

    Page<Map<String, Object>> queryViewDatas(QueryEntity<YccApiViewGroup> queryEntity);

    Map<String, Long> viewCounter() throws YccException;

    List<ExportViewExtVo> getViewExportDatas(String viewId);
}
