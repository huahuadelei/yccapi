package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.entity.YccSysLogs;
import cn.ycc.api.admin.entity.YccUser;
import cn.ycc.api.admin.service.SysLogsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logs")
public class SysLogController {

    @Autowired
    private SysLogsService sysLogsService;

    @PostMapping("/query")
    @LogInfo(value = "查询系统日志",ignore = true)
    public ResultBean queryPage(@RequestBody QueryEntity<YccSysLogs> queryEntity){
        Page<YccSysLogs> sysLogsPage = sysLogsService.querySysLogPage(queryEntity);
        return ResultBean.builder().code("200").data(sysLogsPage).build();
    }
}
