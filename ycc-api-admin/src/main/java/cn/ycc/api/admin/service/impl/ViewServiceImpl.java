package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.commons.base.BaseEntity;
import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.dto.RolePermEntity;
import cn.ycc.api.admin.commons.dto.ViewCreateEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.utils.IdWorker;
import cn.ycc.api.admin.commons.utils.JsonUtils;
import cn.ycc.api.admin.commons.utils.KeyGenerateUtils;
import cn.ycc.api.admin.commons.utils.LoginUserUtils;
import cn.ycc.api.admin.commons.vo.ExportApiInfoVo;
import cn.ycc.api.admin.commons.vo.ExportViewExtVo;
import cn.ycc.api.admin.entity.YccApiGroup;
import cn.ycc.api.admin.entity.YccApiInfo;
import cn.ycc.api.admin.entity.YccApiViewGroup;
import cn.ycc.api.admin.entity.YccRole;
import cn.ycc.api.admin.mapper.YccApiViewGroupMapper;
import cn.ycc.api.admin.mapper.YccRoleMapper;
import cn.ycc.api.admin.service.ApiGroupService;
import cn.ycc.api.admin.service.ApiInfoService;
import cn.ycc.api.admin.service.RoleService;
import cn.ycc.api.admin.service.ViewService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:21
 */
@Service
public class ViewServiceImpl extends ServiceImpl<YccApiViewGroupMapper, YccApiViewGroup> implements ViewService {

    private static final Pattern expTimePattern = Pattern.compile("[A-Z]{1}:\\d+");

    @Autowired
    private ApiGroupService groupService;
    @Autowired
    private ApiInfoService apiInfoService;

    @Override
    @Transactional
    public void saveView(ViewCreateEntity viewCreateEntity) throws YccException {
        if (ObjectUtils.isEmpty(viewCreateEntity.getViewGroupName())) {
            throw new YccException("视图名称不能为空");
        }

        // 判断视图名称是否存在
        YccApiViewGroup yccApiViewGroup = new YccApiViewGroup();
        yccApiViewGroup.setViewGroupName(viewCreateEntity.getViewGroupName());
        List<YccApiViewGroup> yccApiViewGroups = getBaseMapper().selectList(new QueryWrapper<YccApiViewGroup>(yccApiViewGroup));

        if (!ObjectUtils.isEmpty(yccApiViewGroups)) {
            throw new YccException("视图名称已存在");
        }

        // 创建视图pojo对象
        YccApiViewGroup viewPojo = new YccApiViewGroup();
        viewPojo.setViewGroupName(viewCreateEntity.getViewGroupName());
        viewPojo.setCreateUserId(LoginUserUtils.getCurrentUser());
        viewPojo.setProjectId(viewCreateEntity.getProjectId());
        viewPojo.setCreateTime(new Date());
        viewPojo.setUpdateTime(new Date());


        // 判断是否启用了密码
        if (!ObjectUtils.isEmpty(viewCreateEntity.getHasPass())
                && 1 == viewCreateEntity.getHasPass()) {
            if (!ObjectUtils.isEmpty(viewCreateEntity.getAutoGenerate()) && viewCreateEntity.getAutoGenerate()) {
                viewPojo.setShowPass(KeyGenerateUtils.generateHybridKey(4));
            } else {
                viewPojo.setShowPass(viewCreateEntity.getShowPass());
            }
            viewPojo.setHasPass(1);
        } else {
            viewPojo.setHasPass(0);
        }

        //判断是否有过期时间
        if (!ObjectUtils.isEmpty(viewCreateEntity.getExpTime())) {
            parseExpTime(viewCreateEntity.getExpTime(), viewPojo);
        }
        viewPojo.setIsTestable(viewCreateEntity.getIsTestable());
        // 保存视图
        baseMapper.insert(viewPojo);

        for (String versionId : viewCreateEntity.getVersions()) {
            // 创建分支数据
            baseMapper.insertViewVersionData(String.valueOf(IdWorker.DEFAULT.nextId()), viewPojo.getId(), versionId);
        }

    }

    @Override
    public Page<Map<String, Object>> queryViewDatas(QueryEntity<YccApiViewGroup> queryEntity) {
        Page page = new Page<>(queryEntity.getPageNum(), queryEntity.getPageSize());
        List<Map<String, Object>> list = baseMapper.queryViewDatas(page, queryEntity.getEntity());
        for (Map<String, Object> obj : list) {
            String vid = (String)obj.get("viewId");
            List<Map<String, Object>> maps = baseMapper.queryViewVersionsByViewId(vid);
            if(ObjectUtils.isEmpty(maps)){
                maps = Collections.emptyList();
            }
            obj.put("versionList",maps);
        }
        page.setRecords(list);
        return page;
    }

    @Override
    public Map<String, Long> viewCounter() throws YccException {
        return baseMapper.viewCounter();
    }

    @Override
    public List<ExportViewExtVo> getViewExportDatas(String viewId) {

        YccApiViewGroup queryCondition = new YccApiViewGroup();
        queryCondition.setId(viewId);
        List<Map<String, Object>> list = baseMapper.queryViewDatas(queryCondition);
        for (Map<String, Object> obj : list) {
            String vid = (String)obj.get("viewId");
            List<Map<String, Object>> maps = baseMapper.queryViewVersionsByViewId(vid);
            if(ObjectUtils.isEmpty(maps)){
                maps = Collections.emptyList();
            }
            obj.put("versionList",maps);
        }
        if (ObjectUtils.isEmpty(list) || list.size() != 1) {
            return Collections.emptyList();
        }

        List<ExportViewExtVo> result = new ArrayList<>();

        // 获取视图版本数据
        Map<String, Object> viewData = list.get(0);
        List<Map<String, Object>> versionList = (List<Map<String, Object>>) viewData.get("versionList");

        for (Map<String, Object> versionData : versionList) {

            ExportViewExtVo exportViewExtVo = new ExportViewExtVo();
            exportViewExtVo.setProjectName((String) viewData.get("projectName"));
            exportViewExtVo.setVersionName((String) versionData.get("versionName"));

            Map<String, String> groupNameMapping = loadGroupNameMapping((String) versionData.get("versionId"));
            exportViewExtVo.setGroupNameMapping(groupNameMapping);

            List<ExportApiInfoVo> exportApiInfoVos = loadApiInfoVos((String) versionData.get("versionId"));
            exportViewExtVo.setApiInfoVos(exportApiInfoVos);

            result.add(exportViewExtVo);
        }
        return result;
    }

    private List<ExportApiInfoVo> loadApiInfoVos(String versionId) {
        List<ExportApiInfoVo> result = new ArrayList<>();

        YccApiInfo yccApiInfo = new YccApiInfo();
        yccApiInfo.setProjectVersionId(versionId);

        List<YccApiInfo> apiInfoList = apiInfoService.list(new QueryWrapper<>(yccApiInfo));
        for (YccApiInfo apiInfo : apiInfoList) {

            ExportApiInfoVo exportApiInfoVo = new ExportApiInfoVo();
            exportApiInfoVo.setDesc(apiInfo.getApiDesc());
            exportApiInfoVo.setUrl(apiInfo.getApiUrl());
            exportApiInfoVo.setGroupId(apiInfo.getGroupId());
            exportApiInfoVo.setMethod(apiInfo.getReqMethod());

            ExportApiInfoVo.Headers headers = formatHeaders(apiInfo);
            exportApiInfoVo.setHeaders(headers);

            ExportApiInfoVo.ParamsVo paramsVo = formatParamsVo(apiInfo);
            exportApiInfoVo.setParamsVo(paramsVo);

            ExportApiInfoVo.ResultVo resultVo = formatResultVo(apiInfo);
            exportApiInfoVo.setResultVo(resultVo);

            result.add(exportApiInfoVo);
        }

        return result;
    }

    private ExportApiInfoVo.ResultVo formatResultVo(YccApiInfo apiInfo) {
        String resContent = apiInfo.getResContent();

        ExportApiInfoVo.ResultVo resultVo = new ExportApiInfoVo.ResultVo();

        JSONObject jsonObject = JSON.parseObject(resContent);
        if (ObjectUtils.isEmpty(jsonObject)) {
            return resultVo;
        }

        JSONObject successed = jsonObject.getJSONObject("successed");

        if (!ObjectUtils.isEmpty(successed)
                && (!ObjectUtils.isEmpty(successed.getString("json")) || (successed.getBoolean("desced") && successed.getJSONArray("propertyDescs").size() > 0))
        ) {
            resultVo.setSuccessData(true);
            resultVo.setSuccess(successed);
        }

        JSONObject errored = jsonObject.getJSONObject("errored");
        if (!ObjectUtils.isEmpty(errored)
                && (!ObjectUtils.isEmpty(errored.getString("json")) || (errored.getBoolean("desced") && errored.getJSONArray("propertyDescs").size() > 0))
        ) {
            resultVo.setErrorData(true);
            resultVo.setError(errored);
        }


        return resultVo;
    }

    private ExportApiInfoVo.ParamsVo formatParamsVo(YccApiInfo apiInfo) {
        String reqContent = apiInfo.getReqContent();

        ExportApiInfoVo.ParamsVo paramsVo = new ExportApiInfoVo.ParamsVo();

        JSONObject jsonObject = JSON.parseObject(reqContent);
        if (ObjectUtils.isEmpty(jsonObject)) {
            return paramsVo;
        }

        JSONObject body = jsonObject.getJSONObject("body");
        JSONArray bodyDatas;
        if (!ObjectUtils.isEmpty(body)
                && !ObjectUtils.isEmpty(bodyDatas = body.getJSONArray("definitions"))) {
            paramsVo.setExistsBody(true);
            paramsVo.setBodyDatas(bodyDatas);
        }

        JSONObject query = jsonObject.getJSONObject("query");
        JSONArray queryDatas;
        if (!ObjectUtils.isEmpty(query)
                && !ObjectUtils.isEmpty(queryDatas = query.getJSONArray("definitions"))) {
            paramsVo.setExistsQuery(true);
            paramsVo.setQueryDatas(queryDatas);
        }

        JSONObject param = jsonObject.getJSONObject("param");
        JSONArray paramDatas;
        if (!ObjectUtils.isEmpty(body)
                && !ObjectUtils.isEmpty(paramDatas = param.getJSONArray("definitions"))) {
            paramsVo.setExistsURL(true);
            paramsVo.setUrlDatas(paramDatas);
        }
        return paramsVo;
    }

    private ExportApiInfoVo.Headers formatHeaders(YccApiInfo apiInfo) {
        String reqHeaders = apiInfo.getReqHeaders();

        if (ObjectUtils.isEmpty(reqHeaders)) {
            return ExportApiInfoVo.Headers.EMPTY_HEADERS;
        }
        ExportApiInfoVo.Headers headers = new ExportApiInfoVo.Headers();

        JSONArray jsonArray = JSON.parseArray(reqHeaders);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String key = jsonObject.getString("key");
            String value = jsonObject.getString("value");
            headers.add(new ExportApiInfoVo.Header(key, value));
        }
        return headers;
    }

    private Map<String, String> loadGroupNameMapping(String versionId) {
        YccApiGroup yccApiGroup = new YccApiGroup();
        yccApiGroup.setProjectVersionId(versionId);
        List<YccApiGroup> versionList = groupService.list(new QueryWrapper<>(yccApiGroup));

        YccApiGroup group = new YccApiGroup();
        group.setId("0");
        group.setGroupName("默认分组");
        versionList.add(0, group);

        return versionList.stream().collect(Collectors.toMap(BaseEntity::getId, YccApiGroup::getGroupName));
    }

    private void parseExpTime(String expTime, YccApiViewGroup viewPojo) {

        // 如果不符合指定格式 不做处理
        Matcher matcher = expTimePattern.matcher(expTime);
        if (!matcher.matches()) {
            return;
        }
        viewPojo.setIsExp(1);

        int index = expTime.indexOf(":");
        String type = expTime.substring(0, index).trim();
        int num = Integer.parseInt(expTime.substring(index + 1).trim());

        LocalDateTime dateTime = LocalDateTime.now();
        switch (type) {
            case "D":
                dateTime = dateTime.plusDays(num);
                break;
            case "M":
                dateTime = dateTime.plusMonths(num);
                break;
            case "Y":
                dateTime = dateTime.plusYears(num);
                break;
        }
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date from = Date.from(instant);
        viewPojo.setExpDate(from);
    }

}
