package cn.ycc.api.admin.service;

import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccApiGroup;
import cn.ycc.api.admin.entity.YccApiInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:20
 */
public interface ApiGroupService extends IService<YccApiGroup> {
    void removeGroupById(String groupId) throws YccException;
}
