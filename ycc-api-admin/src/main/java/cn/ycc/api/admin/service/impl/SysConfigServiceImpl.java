package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.commons.dto.RolePermEntity;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.utils.IdWorker;
import cn.ycc.api.admin.entity.YccRole;
import cn.ycc.api.admin.entity.YccSysConfig;
import cn.ycc.api.admin.mapper.YccRoleMapper;
import cn.ycc.api.admin.mapper.YccSysConfigMapper;
import cn.ycc.api.admin.service.RoleService;
import cn.ycc.api.admin.service.SysConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:21
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<YccSysConfigMapper, YccSysConfig> implements SysConfigService {

}
