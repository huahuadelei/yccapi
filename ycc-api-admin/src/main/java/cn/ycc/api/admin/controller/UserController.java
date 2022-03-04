package cn.ycc.api.admin.controller;

import cn.ycc.api.admin.commons.annotation.LogInfo;
import cn.ycc.api.admin.commons.base.QueryEntity;
import cn.ycc.api.admin.commons.base.ResultBean;
import cn.ycc.api.admin.commons.exceptions.YccException;
import cn.ycc.api.admin.commons.shiro.po.LoginUser;
import cn.ycc.api.admin.commons.utils.LoginUserUtils;
import cn.ycc.api.admin.commons.utils.PasswordEncoder;
import cn.ycc.api.admin.entity.YccUser;
import cn.ycc.api.admin.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/current/resetPass")
    public ResultBean resetCurrentPass(@RequestBody YccUser user){
        if(ObjectUtils.isEmpty(user)||ObjectUtils.isEmpty(user.getPassword())){
            throw new YccException("新密码不能为空");
        }
        if(ObjectUtils.isEmpty(user.getOldPass())){
            throw new YccException("旧密码不能为空");
        }

        YccUser yccUser = userService.getById(LoginUserUtils.getCurrentUser());

        if(!yccUser.getPassword().equals(PasswordEncoder.encode(user.getOldPass(),yccUser.getPassSalt()))){
            throw new YccException("旧密码输入有误");
        }

        yccUser.setUpdateTime(new Date());
        yccUser.setPassword(PasswordEncoder.encode(user.getPassword(),yccUser.getPassSalt()));
        userService.updateById(yccUser);
        return ResultBean.builder().code("200").msg("修改成功").build();
    }

    @GetMapping("/getUserInfo")
    @LogInfo(value = "获取用户信息",ignore = true)
    public ResultBean getUserInfo(){
        LoginUser userInfo = LoginUserUtils.getLoginUserInfo(LoginUserUtils.getCurrentUser());
        return ResultBean.builder().code("200").data(userInfo).build();
    }

    @PostMapping("/login")
    @LogInfo("用户登录")
    public ResultBean login(@RequestBody YccUser user)throws YccException {
        String token = userService.login(user);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token",token);

        return ResultBean.builder()
                .code("200")
                .data(resultMap)
                .build();
    }

    @PostMapping("/create")
    @RequiresRoles("Administrator")
    @LogInfo("创建用户")
    public ResultBean regist(@RequestBody YccUser user)throws YccException {
        if(ObjectUtils.isEmpty(user)||ObjectUtils.isEmpty(user.getUsername())){
            throw new YccException("用户名不能为空");
        }
        if(ObjectUtils.isEmpty(user.getPassword())){
            throw new YccException("密码不能为空");
        }
        if(ObjectUtils.isEmpty(user.getRoleId())){
            throw new YccException("用户角色ID不能为空");
        }
        userService.registeUser(user);
        return ResultBean.builder()
                .code("200")
                .msg("注册成功")
                .build();
    }

    @PostMapping("/query")
    @LogInfo(value = "查询用户列表",ignore = true)
    public ResultBean queryPage(@RequestBody QueryEntity<YccUser> queryEntity){
        QueryWrapper<YccUser> queryWrapper = queryEntity.buildQueryWrapper().select("username", "id","email","update_time","create_time","role_id");
        Page<YccUser> userPage = userService.page(new Page<>(queryEntity.getPageNum(),queryEntity.getPageSize()),queryWrapper);
        return ResultBean.builder().code("200").data(userPage).build();
    }

    @PutMapping("/update")
    @LogInfo("更新用户信息")
    public ResultBean updateUser(@RequestBody YccUser yccUser){
        if(ObjectUtils.isEmpty(yccUser)||ObjectUtils.isEmpty(yccUser.getId())){
            throw new YccException("400","用户ID不能为空");
        }
        // 用户名不可修改 \ 密码在此接口修改\ 盐值由系统生成不允许用户设置
        yccUser.setUsername(null);
        yccUser.setPassword(null);
        yccUser.setPassSalt(null);

        yccUser.setUpdateTime(new Date());
        userService.updateById(yccUser);

        LoginUserUtils.removeLoginUserInfo(yccUser.getId());

        return ResultBean.builder().code("200").msg("更新成功").build();
    }


    @PostMapping("/resetPass")
    @LogInfo("重置用户密码")
    public ResultBean resetPass(@RequestBody YccUser yccUser){
        if(ObjectUtils.isEmpty(yccUser)||ObjectUtils.isEmpty(yccUser.getId())){
            throw new YccException("400","用户ID不能为空");
        }
        if(ObjectUtils.isEmpty(yccUser.getPassword())){
            throw new YccException("400","密码不能设置为空");
        }

        YccUser dbUser = userService.getById(yccUser.getId());

        yccUser.setPassword(PasswordEncoder.encode(yccUser.getPassword(),dbUser.getPassSalt()));
        yccUser.setUpdateTime(new Date());
        userService.updateById(yccUser);
        return ResultBean.builder().code("200").msg("重置密码成功").build();
    }

    @DeleteMapping("/{userId}")
    @RequiresRoles("Administrator")
    @LogInfo("删除用户")
    public ResultBean removeUser(@PathVariable String userId){
        if(userId == null){
            throw new YccException("用户ID不可为空");
        }
        YccUser dbUser = userService.getById(userId);
        if("admin".equals(dbUser.getUsername())){
            throw new YccException("[admin]是系统默认用户不能删除");
        }
        userService.removeById(userId);
        return ResultBean.builder().code("200").msg("删除成功").build();
    }

}
