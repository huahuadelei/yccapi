package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.entity.YccSysLogs;
import cn.ycc.api.admin.mapper.YccSysLogsMapper;
import cn.ycc.api.admin.service.SysLogsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:21
 */
@Service
public class SysLogsServiceImpl extends ServiceImpl<YccSysLogsMapper, YccSysLogs> implements SysLogsService {

    @Override
    public Page<YccSysLogs> querySysLogPage(QueryEntity<YccSysLogs> queryEntity) throws YccException {
        Page<YccSysLogs> page = new Page<>(queryEntity.getPageNum(),queryEntity.getPageSize());
        List<YccSysLogs> yccSysLogs = getBaseMapper().selectSysLogPage(page, queryEntity.getEntity());
        page.setRecords(yccSysLogs);
        return page;
    }
}
