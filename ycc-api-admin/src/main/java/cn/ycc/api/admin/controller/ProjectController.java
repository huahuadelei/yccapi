package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.entity.YccApiGroup;
import cn.ycc.api.admin.entity.YccApiProjectVersion;
import cn.ycc.api.admin.entity.YccApiProjects;
import cn.ycc.api.admin.service.ApiGroupService;
import cn.ycc.api.admin.service.ProjectService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApiGroupService apiGroupService;

    @PostMapping("/query")
    @LogInfo(value = "分页查询项目列表",ignore = true)
    public ResultBean getProjectPage(@RequestBody QueryEntity<YccApiProjects> queryEntity){
        Page<YccApiProjects> list = projectService.getProjectPage(queryEntity);
        return ResultBean.builder().code("200").data(list).build();
    }

    @GetMapping("")
    @LogInfo(value = "查询项目列表",ignore = true)
    public ResultBean getProjectList(){
        List<YccApiProjects> list = projectService.list();
        return ResultBean.builder().code("200").data(list).build();
    }

    @GetMapping("/versions")
    @LogInfo(value = "查询项目版本列表",ignore = true)
    public ResultBean getProjectVersionList(@RequestParam String projectId){
        List<YccApiProjectVersion> list = projectService.versionList(projectId);
        return ResultBean.builder().code("200").data(list).build();
    }

    @PostMapping("")
    @LogInfo(value = "添加项目")
    public ResultBean saveProject(@RequestBody YccApiProjects yccApiProjects){
        projectService.saveProject(yccApiProjects);
        return ResultBean.builder().code("200").msg("保存成功").build();
    }

    @PostMapping("/version")
    @LogInfo(value = "添加项目版本")
    public ResultBean saveProjectVersion(@RequestBody YccApiProjectVersion yccApiProjectVersion){
        projectService.saveProjectVersion(yccApiProjectVersion);
        return ResultBean.builder().code("200").msg("保存成功").build();
    }

    @PutMapping("")
    @LogInfo(value = "修改项目信息")
    public ResultBean updateProject(@RequestBody YccApiProjects yccApiProjects){
        projectService.updateById(yccApiProjects);
        return ResultBean.builder().code("200").msg("修改成功").build();
    }

    @DeleteMapping("/{projectId}")
    @LogInfo(value = "删除项目")
    public ResultBean delProject(@PathVariable String projectId){
        projectService.delProject(projectId);
        return ResultBean.builder().code("200").msg("删除成功").build();
    }

    @GetMapping("/{projectId}")
    @LogInfo(value = "查询项目信息")
    public ResultBean getProjectById(@PathVariable String projectId){
        YccApiProjects serviceById = projectService.getById(projectId);
        return ResultBean.builder().code("200").data(serviceById).build();
    }

    @GetMapping("/versions/{projectId}")
    @LogInfo(value = "查询项目版本信息")
    public ResultBean getProjectVersionByProId(@PathVariable String projectId){
        List<YccApiProjectVersion> versions = projectService.getVersionsByProjectId(projectId);
        return ResultBean.builder().code("200").data(versions).build();
    }


    @PostMapping("/group")
    @LogInfo(value = "新增项目分组")
    public ResultBean addProjectGroup(@RequestBody YccApiGroup apiGroup){
        apiGroup.setUpdateTime(new Date());
        apiGroup.setCreateTime(new Date());
        apiGroup.setSort(0);
        apiGroupService.save(apiGroup);
        return ResultBean.builder().code("200").msg("新增成功").build();
    }

    @DeleteMapping("/version/{projectVersionId}")
    @LogInfo(value = "删除项目版本")
    public ResultBean removeProjectVersion(@PathVariable String projectVersionId){
        projectService.removeVersionById(projectVersionId);
        return ResultBean.builder().code("200").msg("删除成功").build();
    }

    @DeleteMapping("/group/{groupId}")
    @LogInfo(value = "删除项目分组")
    public ResultBean addProjectGroup(@PathVariable String groupId){
        apiGroupService.removeGroupById(groupId);
        return ResultBean.builder().code("200").msg("删除成功").build();
    }
}
