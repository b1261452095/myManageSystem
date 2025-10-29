package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.entity.User;
import com.admin.service.UserService;
import com.admin.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author admin
 */
@Api(tags = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 获取当前用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getCurrentUserInfo() {
        String username = SecurityUtil.getCurrentUsername();
        User user = userService.getUserWithRolesByUsername(username);
        // 清除敏感信息
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
    
    /**
     * 获取用户列表（分页）
     */
    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status
    ) {
        Page<User> page = new Page<>(current, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (nickname != null && !nickname.isEmpty()) {
            queryWrapper.like("nickname", nickname);
        }
        if (email != null && !email.isEmpty()) {
            queryWrapper.like("email", email);
        }
        if (phone != null && !phone.isEmpty()) {
            queryWrapper.like("phone", phone);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        Page<User> userPage = userService.page(page, queryWrapper);
        
        // 为每个用户加载角色信息并清除密码
        List<User> records = userPage.getRecords();
        records.forEach(user -> {
            user.setPassword(null);
            // 加载用户的角色信息
            User userWithRoles = userService.getUserWithRolesById(user.getId());
            if (userWithRoles != null) {
                user.setRoleIds(userWithRoles.getRoleIds());
                user.setRoles(userWithRoles.getRoles());
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", records);
        result.put("total", userPage.getTotal());
        result.put("current", userPage.getCurrent());
        result.put("pageSize", userPage.getSize());
        
        return Result.success(result);
    }
    
    /**
     * 根据ID获取用户
     */
    @ApiOperation("根据ID获取用户")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserWithRolesById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
    
    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> createUser(@Valid @RequestBody User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (userService.count(queryWrapper) > 0) {
            return Result.error("用户名已存在");
        }
        
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }
        
        userService.saveUserWithRoles(user);
        return Result.success();
    }
    
    /**
     * 更新用户
     */
    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User existUser = userService.getById(id);
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        
        // 不允许修改超级管理员账号
        if ("admin".equals(existUser.getUsername())) {
            return Result.error("不允许修改超级管理员账号");
        }
        
        // 如果修改了用户名，检查是否重复
        if (!existUser.getUsername().equals(user.getUsername())) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", user.getUsername());
            queryWrapper.ne("id", id);
            if (userService.count(queryWrapper) > 0) {
                return Result.error("用户名已存在");
            }
        }
        
        user.setId(id);
        // 如果提供了新密码，加密后更新；否则保留原密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existUser.getPassword());
        }
        
        userService.updateUserWithRoles(user);
        return Result.success();
    }
    
    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 不允许删除超级管理员
        if ("admin".equals(user.getUsername())) {
            return Result.error("不允许删除超级管理员账号");
        }
        
        userService.removeById(id);
        return Result.success();
    }
    
    /**
     * 批量删除用户
     */
    @ApiOperation("批量删除用户")
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> batchDeleteUser(@RequestBody List<Long> ids) {
        // 检查是否包含超级管理员
        List<User> users = userService.listByIds(ids);
        for (User user : users) {
            if ("admin".equals(user.getUsername())) {
                return Result.error("不允许删除超级管理员账号");
            }
        }
        
        userService.removeByIds(ids);
        return Result.success();
    }
}
