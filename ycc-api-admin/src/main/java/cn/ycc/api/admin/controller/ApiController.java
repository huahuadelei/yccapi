package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.commons.enums.RequestHeaderNames;
import cn.ycc.api.admin.commons.enums.RequestMethods;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccApiGroup;
import cn.ycc.api.admin.entity.YccApiInfo;
import cn.ycc.api.admin.service.ApiGroupService;
import cn.ycc.api.admin.service.ApiInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequestMapping("api")
@RestController
public class ApiController {

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private ApiGroupService apiGroupService;

    @PostMapping("query")
    @LogInfo(value = "获取API列表",ignore = true)
    public ResultBean getApiPage(@RequestBody QueryEntity<YccApiInfo> queryEntity) {
        YccApiInfo entity = queryEntity.getEntity();
        if (ObjectUtils.isEmpty(entity) || ObjectUtils.isEmpty(entity.getProjectVersionId())) {
            throw new YccException("projectVersionId不能为空");
        }
        Page<YccApiInfo> page = apiInfoService.page(new Page<>(queryEntity.getPageNum(), queryEntity.getPageSize()), queryEntity.buildQueryWrapper());
        return ResultBean.builder().code("200").data(page).build();
    }

    @GetMapping("/{apiId}")
    @LogInfo(value = "获取API详情",ignore = true)
    public ResultBean getApiInfo(@PathVariable String apiId){
        YccApiInfo apiInfo = apiInfoService.getById(apiId);
        return ResultBean.builder().code("200").data(apiInfo).build();
    }

    @DeleteMapping("/{apiId}")
    @LogInfo(value = "获取API详情",ignore = true)
    public ResultBean deleteApiInfo(@PathVariable String apiId){
        apiInfoService.removeById(apiId);
        return ResultBean.builder().code("200").msg("删除成功").build();
    }

    @PostMapping("")
    @LogInfo(value = "更新API")
    public ResultBean updateAPI(@RequestBody YccApiInfo yccApiInfo) {
        yccApiInfo.setUpdateTime(new Date());
        if(ObjectUtils.isEmpty(yccApiInfo.getId())){
            yccApiInfo.setCreateTime(new Date());
        }
        apiInfoService.saveOrUpdate(yccApiInfo);
        return ResultBean.builder().code("200").msg("更新成功").build();
    }

    @GetMapping("/groups/{proVersionId}")
    @LogInfo(value = "获取分组列表",ignore = true)
    public ResultBean getApiGroupList(@PathVariable String proVersionId){

        YccApiGroup yccApiGroup = new YccApiGroup();
        yccApiGroup.setProjectVersionId(proVersionId);

        List<YccApiGroup> list = apiGroupService.list(new QueryWrapper<>(yccApiGroup));

        return ResultBean.builder().code("200").data(list).build();
    }

    @GetMapping("/headerNames")
    @LogInfo(value = "获取请求头名称列表",ignore = true)
    public ResultBean getHeaderNames(){
        String[] toArray = Arrays.stream(RequestHeaderNames.values()).map(RequestHeaderNames::getHeaderName).toArray(String[]::new);
        return ResultBean.builder().code("200").data(toArray).build();
    }
    @GetMapping("/requestMethods")
    @LogInfo(value = "获取请求方法列表",ignore = true)
    public ResultBean getRequestMethods(){
        String[] toArray = Arrays.stream(RequestMethods .values()).map(RequestMethods::toString).toArray(String[]::new);
        return ResultBean.builder().code("200").data(toArray).build();
    }



}
