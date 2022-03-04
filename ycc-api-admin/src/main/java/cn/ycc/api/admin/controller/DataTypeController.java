package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.entity.YccDataType;
import cn.ycc.api.admin.service.DataTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.12.02 20:00
 */
@RestController
@RequestMapping("data-type")
public class DataTypeController {

    @Autowired
    private DataTypeService dataTypeService;

    @GetMapping("")
    @LogInfo(value = "获取数据类型列表",ignore = true)
    public ResultBean getDataTypeList(){
        List<YccDataType> list = dataTypeService.list(new QueryWrapper<YccDataType>().orderByAsc("create_time"));
        return ResultBean.builder().code("200").data(list).build();
    }

    @PostMapping("")
    @LogInfo(value = "更新数据类型")
    public ResultBean update(@RequestBody YccDataType dataType){
        dataType.setUpdateTime(new Date());
        if(dataType.getId()==null){
            dataType.setCreateTime(new Date());
        }
        dataTypeService.saveOrUpdate(dataType);
        return ResultBean.builder().code("200").data("操作成功").build();
    }

    @DeleteMapping("/{dataTypeId}")
    @LogInfo(value = "删除数据类型")
    public ResultBean deleteDataType(@PathVariable String dataTypeId){
        dataTypeService.removeById(dataTypeId);
        return ResultBean.builder().code("200").data("操作成功").build();
    }

}
