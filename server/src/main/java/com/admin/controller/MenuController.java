package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.entity.Permission;
import com.admin.service.PermissionService;
import com.admin.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单控制器
 *
 * @author admin
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
    
    private final PermissionService permissionService;
    
    /**
     * 获取当前用户的菜单权限
     */
    @ApiOperation("获取当前用户菜单")
    @GetMapping("/user-menus")
    public Result<List<Permission>> getUserMenus() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            return Result.error("用户未登录");
        }
        
        // TODO: 通过用户名获取用户ID，然后获取用户权限
        // 这里暂时返回所有菜单类型的权限
        List<Permission> permissions = permissionService.list();
        
        // 只返回菜单类型的权限
        List<Permission> menuPermissions = permissions.stream()
                .filter(p -> "menu".equals(p.getType()))
                .collect(Collectors.toList());
        
        return Result.success(buildTree(menuPermissions, 0L));
    }
    
    /**
     * 构建权限树
     */
    private List<Permission> buildTree(List<Permission> permissions, Long parentId) {
        return permissions.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .peek(p -> {
                    List<Permission> children = buildTree(permissions, p.getId());
                    if (!children.isEmpty()) {
                        p.setChildren(children);
                    }
                })
                .collect(Collectors.toList());
    }
}

