package cn.ycc.api.admin.mapper;

import cn.ycc.api.admin.entity.YccRole;
import cn.ycc.api.admin.entity.YccSysLogs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YccSysLogsMapper extends BaseMapper<YccSysLogs> {
    List<YccSysLogs> selectSysLogPage(Page<YccSysLogs> page, @Param("e") YccSysLogs entity);
}
