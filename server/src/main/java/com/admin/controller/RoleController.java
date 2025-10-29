package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.entity.Role;
import com.admin.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 *
 * @author admin
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    /**
     * 获取角色列表（分页）
     */
    @ApiOperation("获取角色列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Map<String, Object>> getRoleList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code
    ) {
        Page<Role> page = new Page<>(current, pageSize);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (code != null && !code.isEmpty()) {
            queryWrapper.like("code", code);
        }
        
        Page<Role> rolePage = roleService.page(page, queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", rolePage.getRecords());
        result.put("total", rolePage.getTotal());
        result.put("current", rolePage.getCurrent());
        result.put("pageSize", rolePage.getSize());
        
        return Result.success(result);
    }
    
    /**
     * 获取所有角色（不分页）
     */
    @ApiOperation("获取所有角色")
    @GetMapping("/all")
    public Result<List<Role>> getAllRoles() {
        List<Role> roles = roleService.list();
        return Result.success(roles);
    }
    
    /**
     * 根据ID获取角色
     */
    @ApiOperation("根据ID获取角色")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }
    
    /**
     * 新增角色
     */
    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> createRole(@Valid @RequestBody Role role) {
        // 检查角色编码是否已存在
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", role.getCode());
        if (roleService.count(queryWrapper) > 0) {
            return Result.error("角色编码已存在");
        }
        
        roleService.save(role);
        return Result.success();
    }
    
    /**
     * 更新角色
     */
    @ApiOperation("更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
        Role existRole = roleService.getById(id);
        if (existRole == null) {
            return Result.error("角色不存在");
        }
        
        // 如果修改了角色编码，检查是否重复
        if (!existRole.getCode().equals(role.getCode())) {
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", role.getCode());
            queryWrapper.ne("id", id);
            if (roleService.count(queryWrapper) > 0) {
                return Result.error("角色编码已存在");
            }
        }
        
        role.setId(id);
        roleService.updateById(role);
        return Result.success();
    }
    
    /**
     * 删除角色
     */
    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        
        // 不允许删除超级管理员角色
        if ("SUPER_ADMIN".equals(role.getCode())) {
            return Result.error("不允许删除超级管理员角色");
        }
        
        roleService.removeById(id);
        return Result.success();
    }
    
    /**
     * 批量删除角色
     */
    @ApiOperation("批量删除角色")
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> batchDeleteRole(@RequestBody List<Long> ids) {
        List<Role> roles = roleService.listByIds(ids);
        for (Role role : roles) {
            if ("SUPER_ADMIN".equals(role.getCode())) {
                return Result.error("不允许删除超级管理员角色");
            }
        }
        
        roleService.removeByIds(ids);
        return Result.success();
    }
    
    /**
     * 为角色分配权限
     */
    @ApiOperation("为角色分配权限")
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds
    ) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }
    
    /**
     * 获取角色的权限列表
     */
    @ApiOperation("获取角色的权限列表")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissionIds(roleId);
        return Result.success(permissionIds);
    }
}

