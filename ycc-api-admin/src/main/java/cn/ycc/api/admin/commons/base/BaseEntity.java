package cn.ycc.api.admin.commons.base;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class BaseEntity implements Serializable {

    @TableId
    protected String id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

    @TableLogic
    protected Integer del;
}