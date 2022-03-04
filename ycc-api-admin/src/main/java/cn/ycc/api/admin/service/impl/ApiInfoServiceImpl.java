package cn.ycc.api.admin.service.impl;

import cn.ycc.api.admin.entity.YccApiInfo;
import cn.ycc.api.admin.entity.YccDataType;
import cn.ycc.api.admin.mapper.YccApiInfoMapper;
import cn.ycc.api.admin.mapper.YccDataTypeMapper;
import cn.ycc.api.admin.service.ApiInfoService;
import cn.ycc.api.admin.service.DataTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.24 21:21
 */
@Service
public class ApiInfoServiceImpl extends ServiceImpl<YccApiInfoMapper, YccApiInfo> implements ApiInfoService {

}
