package cn.ycc.api.admin.entity;

import cn.ycc.api.admin.commons.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ycc_data_type")
public class YccDataType extends BaseEntity {

    private String typeName;

    private String typeDesc;

}