package cn.ycc.api.admin.service;

import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccApiProjectVersion;
import cn.ycc.api.admin.entity.YccApiProjects;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProjectService extends IService<YccApiProjects> {
    Page<YccApiProjects> getProjectPage(QueryEntity<YccApiProjects> queryEntity) throws YccException;

    void saveProject(YccApiProjects yccApiProjects) throws YccException;

    void delProject(String projectId)throws YccException;

    List<YccApiProjectVersion> getVersionsByProjectId(String projectId)throws YccException;

    void saveProjectVersion(YccApiProjectVersion yccApiProjectVersion)throws YccException;

    void removeVersionById(String projectVersionId)throws YccException;

    List<YccApiProjectVersion> versionList(String projectId) throws YccException;
}
