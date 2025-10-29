package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.entity.Permission;
import com.admin.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 权限管理控制器
 *
 * @author admin
 */
@Api(tags = "权限管理")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    
    /**
     * 获取所有权限（树形结构）
     */
    @ApiOperation("获取权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<List<Permission>> getPermissionTree() {
        List<Permission> permissions = permissionService.getPermissionTree();
        return Result.success(permissions);
    }
    
    /**
     * 获取所有权限（列表）
     */
    @ApiOperation("获取权限列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<List<Permission>> getPermissionList(
            @RequestParam(required = false) String type
    ) {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }
        queryWrapper.orderByAsc("sort");
        
        List<Permission> permissions = permissionService.list(queryWrapper);
        return Result.success(permissions);
    }
    
    /**
     * 根据ID获取权限
     */
    @ApiOperation("根据ID获取权限")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }
    
    /**
     * 新增权限
     */
    @ApiOperation("新增权限")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> createPermission(@Valid @RequestBody Permission permission) {
        // 检查权限编码是否已存在
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", permission.getCode());
        if (permissionService.count(queryWrapper) > 0) {
            return Result.error("权限编码已存在");
        }
        
        permissionService.save(permission);
        return Result.success();
    }
    
    /**
     * 更新权限
     */
    @ApiOperation("更新权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> updatePermission(@PathVariable Long id, @Valid @RequestBody Permission permission) {
        Permission existPermission = permissionService.getById(id);
        if (existPermission == null) {
            return Result.error("权限不存在");
        }
        
        // 如果修改了权限编码，检查是否重复
        if (!existPermission.getCode().equals(permission.getCode())) {
            QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", permission.getCode());
            queryWrapper.ne("id", id);
            if (permissionService.count(queryWrapper) > 0) {
                return Result.error("权限编码已存在");
            }
        }
        
        permission.setId(id);
        permissionService.updateById(permission);
        return Result.success();
    }
    
    /**
     * 删除权限
     */
    @ApiOperation("删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> deletePermission(@PathVariable Long id) {
        // 检查是否有子权限
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        if (permissionService.count(queryWrapper) > 0) {
            return Result.error("该权限下有子权限，无法删除");
        }
        
        permissionService.removeById(id);
        return Result.success();
    }
}

