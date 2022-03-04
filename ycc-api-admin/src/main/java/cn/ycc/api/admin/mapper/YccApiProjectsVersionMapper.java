package cn.ycc.api.admin.mapper;

import cn.ycc.api.admin.entity.YccApiProjectVersion;
import cn.ycc.api.admin.entity.YccApiProjects;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YccApiProjectsVersionMapper extends BaseMapper<YccApiProjectVersion> {

    List<YccApiProjectVersion> getVersionsByProjectId(@Param("projectId") String projectId);
}
