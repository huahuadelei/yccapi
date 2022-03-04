package cn.ycc.api.admin.mapper;

import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.entity.YccApiInfo;
import cn.ycc.api.admin.entity.YccApiProjects;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YccApiProjectsMapper extends BaseMapper<YccApiProjects> {
    List<YccApiProjects> getProjectPage(Page<YccApiProjects> page,@Param("e") YccApiProjects projects);

}
