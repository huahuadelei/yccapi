package cn.ycc.api.admin.service;

import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccSysLogs;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:20
 */
public interface SysLogsService extends IService<YccSysLogs> {
    Page<YccSysLogs> querySysLogPage(QueryEntity<YccSysLogs> queryEntity) throws YccException;
}
