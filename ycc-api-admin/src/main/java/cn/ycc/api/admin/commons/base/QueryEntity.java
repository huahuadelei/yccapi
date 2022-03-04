package cn.ycc.api.admin.commons.base;

import cn.ycc.api.admin.commons.utils.ReflectUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class QueryEntity<T> {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private T entity;
    private Map<String, Object> likeBox;


    public QueryWrapper<T> buildQueryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (!ObjectUtils.isEmpty(likeBox)
                && !ObjectUtils.isEmpty(likeBox.get("likeValue"))
                && !ObjectUtils.isEmpty(likeBox.get("column"))) {

            String likeValue = likeBox.get("likeValue").toString();
            Object col = likeBox.get("column");

            Set<String> columns = null;

            boolean flag = false;

            if (col instanceof String) {
                String[] split = col.toString().split(",");
                columns = Arrays.stream(split).collect(Collectors.toSet());
                flag=true;
            } else if (col instanceof String[]) {
                columns = Arrays.stream((String[]) col).collect(Collectors.toSet());
                flag=true;
            } else if (col instanceof List) {
                columns = new HashSet<>(((List<String>) col));
                flag=true;
            }

            if(flag){
                Set<String> finalColumns = columns;
                int i = 0;
                for (String finalColumn : finalColumns) {
                    queryWrapper.like(finalColumn, likeValue);
                    if (i != finalColumns.size() - 1) {
                        queryWrapper.or();
                    }
                    i++;
                }
            }
    }

        if (entity!= null) {
            queryWrapper.setEntity(entity);
        }
        return queryWrapper;
}
}
