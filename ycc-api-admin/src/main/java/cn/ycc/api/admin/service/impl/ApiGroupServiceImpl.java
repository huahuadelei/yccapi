package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccApiGroup;
import cn.ycc.api.admin.entity.YccApiInfo;
import cn.ycc.api.admin.mapper.YccApiGroupMapper;
import cn.ycc.api.admin.mapper.YccApiInfoMapper;
import cn.ycc.api.admin.service.ApiGroupService;
import cn.ycc.api.admin.service.ApiInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:21
 */
@Service
public class ApiGroupServiceImpl extends ServiceImpl<YccApiGroupMapper, YccApiGroup> implements ApiGroupService {

    @Autowired
    private ApiInfoService apiInfoService;

    @Override
    @Transactional
    public void removeGroupById(String groupId) throws YccException {
        YccApiGroup yccApiGroup = super.getById(groupId);
        if(ObjectUtils.isEmpty(yccApiGroup)){
           throw new YccException("不存在的分组");
        }
        super.removeById(groupId);

        YccApiInfo yccApiInfo = new YccApiInfo();
        yccApiInfo.setProjectVersionId(yccApiGroup.getProjectVersionId());
        yccApiInfo.setGroupId(groupId);
        List<YccApiInfo> list = apiInfoService.list(new QueryWrapper<>(yccApiInfo));

        if(!ObjectUtils.isEmpty(list)){
            list.forEach(item->item.setGroupId("0"));
            apiInfoService.updateBatchById(list);
        }
    }
}
