package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccApiGroup;
import cn.ycc.api.admin.entity.YccApiInfo;
import cn.ycc.api.admin.entity.YccApiProjectVersion;
import cn.ycc.api.admin.entity.YccApiProjects;
import cn.ycc.api.admin.mapper.*;
import cn.ycc.api.admin.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl extends ServiceImpl<YccApiProjectsMapper, YccApiProjects> implements ProjectService {

    @Autowired
    private YccApiProjectsVersionMapper projectsVersionMapper;
    @Autowired
    private YccApiGroupMapper yccApiGroupMapper;
    @Autowired
    private YccApiInfoMapper yccApiInfoMapper;
    @Autowired
    private YccApiViewGroupMapper yccApiViewGroupMapper;

    @Override
    public Page<YccApiProjects> getProjectPage(QueryEntity<YccApiProjects> queryEntity) throws YccException {
        Page<YccApiProjects> page = new Page<>(queryEntity.getPageNum(), queryEntity.getPageSize());
        List<YccApiProjects> projectPage = baseMapper.getProjectPage(page, queryEntity.getEntity());
        page.setRecords(projectPage);
        return page;
    }

    @Override
    @Transactional
    public void saveProject(YccApiProjects yccApiProjects) throws YccException {
        yccApiProjects.setUpdateTime(new Date());
        yccApiProjects.setCreateTime(new Date());
        baseMapper.insert(yccApiProjects);

        if (ObjectUtils.isEmpty(yccApiProjects.getVersionInfo())) {
            yccApiProjects.setVersionInfo("default");
        }

        YccApiProjectVersion yccApiProjectVersion = new YccApiProjectVersion();
        yccApiProjectVersion.setCreateTime(new Date());
        yccApiProjectVersion.setUpdateTime(new Date());
        yccApiProjectVersion.setProjectId(yccApiProjects.getId());
        yccApiProjectVersion.setVersionInfo(yccApiProjects.getVersionInfo());
        projectsVersionMapper.insert(yccApiProjectVersion);

        yccApiProjects.setDefVersionId(yccApiProjectVersion.getId());
        baseMapper.updateById(yccApiProjects);
    }

    @Override
    @Transactional
    public void delProject(String projectId) throws YccException {
        baseMapper.deleteById(projectId);
        YccApiProjectVersion condition = new YccApiProjectVersion();
        condition.setProjectId(projectId);
        projectsVersionMapper.delete(new QueryWrapper<>(condition));
    }

    @Override
    public List<YccApiProjectVersion> getVersionsByProjectId(String projectId) throws YccException {
        return projectsVersionMapper.getVersionsByProjectId(projectId);
    }

    @Override
    @Transactional
    public void saveProjectVersion(YccApiProjectVersion yccApiProjectVersion) throws YccException {
        YccApiProjectVersion query = new YccApiProjectVersion();
        query.setVersionInfo(yccApiProjectVersion.getVersionInfo());
        query.setProjectId(yccApiProjectVersion.getProjectId());

        if (!ObjectUtils.isEmpty(projectsVersionMapper.selectList(new QueryWrapper<>(query)))) {
            throw new YccException("版本名称已存在");
        }

        yccApiProjectVersion.setCreateTime(new Date());
        yccApiProjectVersion.setUpdateTime(new Date());
        projectsVersionMapper.insert(yccApiProjectVersion);

        if (!ObjectUtils.isEmpty(yccApiProjectVersion.getCopyVersionId())) {
            copyGroupsToNewVersion(yccApiProjectVersion.getCopyVersionId(), yccApiProjectVersion.getId());
        }
    }

    @Override
    @Transactional
    public void removeVersionById(String projectVersionId) throws YccException {
        // 删除版本关联视图数据
        List<Map<String, Object>> mapList = yccApiViewGroupMapper.queryViewVersionByVersionId(projectVersionId);
        if (!ObjectUtils.isEmpty(mapList)) {
            for (Map<String, Object> map : mapList) {
                String id = (String) map.get("id");
                String viewId = (String) map.get("viewId");
                // 查询视图下所有版本
                List<Map<String, Object>> maps = yccApiViewGroupMapper.queryViewVersionsByViewId(viewId);
                /*
                    如果视图数据为空 或者 数据大于1则删除指定版本的数据
                    如果数量等于1 则把整个视图数据删除
                 */
                if (!ObjectUtils.isEmpty(maps)
                        && maps.size() == 1) {
                    yccApiViewGroupMapper.deleteById(viewId);
                }
                yccApiViewGroupMapper.deleteViewViersionById(id);
            }
        }

        // 项目删除版本
        projectsVersionMapper.deleteById(projectVersionId);

        //删除版本关联的分组
        YccApiGroup yccApiGroup = new YccApiGroup();
        yccApiGroup.setProjectVersionId(projectVersionId);
        yccApiGroupMapper.delete(new QueryWrapper<>(yccApiGroup));

        //删除版本关联的接口
        YccApiInfo yccApiInfo = new YccApiInfo();
        yccApiInfo.setProjectVersionId(projectVersionId);
        yccApiInfoMapper.delete(new QueryWrapper<>(yccApiInfo));
    }

    @Override
    public List<YccApiProjectVersion> versionList(String projectId) throws YccException {
        YccApiProjectVersion projectVersion = new YccApiProjectVersion();
        projectVersion.setProjectId(projectId);
        return projectsVersionMapper.selectList(new QueryWrapper<>(projectVersion));
    }

    /**
     * 复制指定版本下的接口到新的版本分支下
     *
     * @param newVersionId
     */
    private void copyGroupsApiInfoToNewVersion(String newVersionId, String newGroupId, List<YccApiInfo> yccApiInfos) {
        if (ObjectUtils.isEmpty(yccApiInfos)) {
            return;
        }
        for (YccApiInfo yccApiInfo : yccApiInfos) {

            yccApiInfo.setId(null);
            yccApiInfo.setProjectVersionId(newVersionId);
            yccApiInfo.setGroupId(newGroupId);
            yccApiInfo.setCreateTime(new Date());
            yccApiInfo.setUpdateTime(new Date());

            yccApiInfoMapper.insert(yccApiInfo);
        }
    }

    /**
     * 复制指定版本下的分组到新的版本分支下
     *
     * @param targetVersionId
     * @param newVersionId
     */
    private void copyGroupsToNewVersion(String targetVersionId, String newVersionId) {
        YccApiGroup yccApiGroupQuery = new YccApiGroup();
        yccApiGroupQuery.setProjectVersionId(targetVersionId);
        List<YccApiGroup> yccApiGroups = yccApiGroupMapper.selectList(new QueryWrapper<>(yccApiGroupQuery));

        YccApiInfo yccApiInfo = new YccApiInfo();
        yccApiInfo.setProjectVersionId(targetVersionId);
        List<YccApiInfo> yccApiInfos = yccApiInfoMapper.selectList(new QueryWrapper<>(yccApiInfo));

        Map<String, List<YccApiInfo>> groupApiInfoMap = yccApiInfos.parallelStream().collect(Collectors.groupingBy(YccApiInfo::getGroupId));

        // 默认分组
        List<YccApiInfo> defGroupApi = groupApiInfoMap.get("0");
        if (!ObjectUtils.isEmpty(defGroupApi)) {
            copyGroupsApiInfoToNewVersion(newVersionId, "0", defGroupApi);
        }

        if (ObjectUtils.isEmpty(yccApiGroups)) {
            return;
        }


        for (YccApiGroup yccApiGroup : yccApiGroups) {
            List<YccApiInfo> groupApiList = groupApiInfoMap.get(yccApiGroup.getId());

            yccApiGroup.setId(null);
            yccApiGroup.setProjectVersionId(newVersionId);
            yccApiGroup.setCreateTime(new Date());
            yccApiGroup.setUpdateTime(new Date());

            yccApiGroupMapper.insert(yccApiGroup);

            copyGroupsApiInfoToNewVersion(newVersionId, yccApiGroup.getId(), groupApiList);
        }


    }
}
