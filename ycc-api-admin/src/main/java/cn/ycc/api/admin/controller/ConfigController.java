package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.entity.YccSysConfig;
import cn.ycc.api.admin.service.SysConfigService;
import cn.ycc.api.admin.service.config.ConfigHolder;
import cn.ycc.api.admin.service.config.ConfigManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private ConfigManager configManager;

    @GetMapping("/{configKey}")
    @RequiresRoles("Administrator")
    @LogInfo(value = "获取配置信息", ignore = true)
    public ResultBean getConfig(@PathVariable String configKey) {
        ConfigHolder configHolder = configManager.getConfig(configKey);
        return ResultBean.builder().code("200").data(configHolder.getOriginConfig()).build();
    }

    @PutMapping("/{configKey}")
    @RequiresRoles("Administrator")
    @LogInfo(value = "修改配置信息")
    public ResultBean updateConfig(@PathVariable String configKey, @RequestBody YccSysConfig config) {
        configManager.updateConfig(configKey,config);
        return ResultBean.builder().code("200").msg("更新成功").build();
    }

}
