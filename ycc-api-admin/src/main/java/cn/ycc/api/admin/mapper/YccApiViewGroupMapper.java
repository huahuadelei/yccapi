package cn.ycc.api.admin.mapper;

import cn.ycc.api.admin.entity.YccApiViewGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface YccApiViewGroupMapper extends BaseMapper<YccApiViewGroup> {

    int insertViewVersionData(@Param("id")String id,@Param("viewId") String viewId,@Param("versionId") String versionId);

    List<Map<String,Object>> queryViewDatas(Page page, @Param("cm") YccApiViewGroup entity);
    List<Map<String,Object>> queryViewDatas( @Param("cm") YccApiViewGroup entity);

    List<Map<String,Object>> queryViewVersionsByViewId(@Param("viewId") String viewId);

    Map<String, Long> viewCounter();

    List<Map<String,Object>> queryViewVersionByVersionId(@Param("versionId") String projectVersionId);

    int deleteViewViersionById(@Param("viewVersionId") String viewVersionId);
}
