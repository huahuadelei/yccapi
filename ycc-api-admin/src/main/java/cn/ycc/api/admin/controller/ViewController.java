package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.commons.dto.ViewCreateEntity;
import cn.ycc.api.admin.commons.enums.JxlsTempType;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.utils.JxlsExcelExportHelper;
import cn.ycc.api.admin.commons.vo.ExportViewExtVo;
import cn.ycc.api.admin.entity.YccApiViewGroup;
import cn.ycc.api.admin.service.ViewService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jxls.common.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2021.01.01 10:43
 */
@RestController
@RequestMapping("/views")
@Slf4j
public class ViewController {

    @Autowired
    private ViewService viewService;

    @Autowired
    private JxlsExcelExportHelper jxlsExcelExportHelper;

    @PostMapping
    @LogInfo(value = "创建视图")
    public ResultBean saveView(@RequestBody ViewCreateEntity viewCreateEntity){
        viewService.saveView(viewCreateEntity);
        return ResultBean.builder().code("200").msg("保存成功").build();
    }
    @PostMapping("/query")
    @LogInfo(value = "分页查询视图",ignore = true)
    public ResultBean queryViewDatas(@RequestBody QueryEntity<YccApiViewGroup> queryEntity){
        Page<Map<String,Object>> result = viewService.queryViewDatas(queryEntity);
        return ResultBean.builder().code("200").data(result).build();
    }

    @GetMapping("/counter")
    @LogInfo(value = "统计视图数量",ignore = true)
    public ResultBean viewCounter(){
        Map<String,Long> counter = viewService.viewCounter();
        return ResultBean.builder().code("200").data(counter).build();
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam("viewId")String viewId, HttpServletResponse response){
        if(ObjectUtils.isEmpty(viewId)){
            throw new YccException("视图id不能为空");
        }
        YccApiViewGroup viewGroup = viewService.getById(viewId);
        if(ObjectUtils.isEmpty(viewGroup)){
            throw new YccException(String.format("视图ID为(%s)的数据不存在",viewId));
        }
        List<ExportViewExtVo> viewExportDatas = viewService.getViewExportDatas(viewId);

        Context context = new Context();
        context.putVar("viewList",viewExportDatas);
        try {
            jxlsExcelExportHelper.renderExcelToDownLoad(response,
                    viewGroup.getViewGroupName()+"-"+(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))) +".xlsx",
                    JxlsTempType.VIEW_EXPORT,
                    context,true);
        } catch (IOException e) {
            log.error("下载失败",e);
        }
    }


}
