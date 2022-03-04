package cn.ycc.api.admin.commons.annotation;

import cn.ycc.api.admin.commons.aspect.SysLogAspect;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yuchaoqun
 * @email chaoqunyu9311@163.com
 * @date 2020.11.29 01:16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SysLogAspect.class)
public @interface EnableSaveSysLog {
}
