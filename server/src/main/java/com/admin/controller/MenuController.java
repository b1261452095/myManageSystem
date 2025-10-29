package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.entity.Permission;
import com.admin.entity.Role;
import com.admin.entity.User;
import com.admin.service.PermissionService;
import com.admin.service.UserService;
import com.admin.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单管理控制器
 *
 * @author admin
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户的菜单树（根据权限）
     */
    @ApiOperation("获取当前用户的菜单树")
    @GetMapping("/user-menus")
    public Result<List<Permission>> getUserMenus(HttpServletRequest request) {
        try {
            // 从请求头获取token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 从token中获取用户名
            String username = jwtUtil.getUsernameFromToken(token);
            
            // 获取用户信息（包含角色）
            User user = userService.getUserWithRolesById(userService.lambdaQuery()
                    .eq(User::getUsername, username)
                    .one()
                    .getId());
            
            if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            
            // 检查是否是超级管理员
            boolean isSuperAdmin = user.getRoles().stream()
                    .anyMatch(role -> "SUPER_ADMIN".equals(role.getCode()));
            
            List<Permission> userMenus;
            
            if (isSuperAdmin) {
                // 超级管理员：返回所有启用且可见的菜单
                LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Permission::getType, "menu")
                        .eq(Permission::getStatus, 1)
                        .eq(Permission::getVisible, 1)
                        .orderByAsc(Permission::getSort);
                userMenus = permissionService.list(queryWrapper);
            } else {
                // 普通用户：根据角色权限获取菜单
                Set<Long> permissionIds = new HashSet<>();
                for (Role role : user.getRoles()) {
                    // 这里需要查询角色关联的权限ID
                    // 暂时先返回空列表，后续完善
                    permissionIds.addAll(permissionService.getPermissionIdsByRoleId(role.getId()));
                }
                
                if (permissionIds.isEmpty()) {
                    return Result.success(new ArrayList<>());
                }
                
                // 查询这些权限中的菜单
                LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(Permission::getId, permissionIds)
                        .eq(Permission::getType, "menu")
                        .eq(Permission::getStatus, 1)
                        .eq(Permission::getVisible, 1)
                        .orderByAsc(Permission::getSort);
                userMenus = permissionService.list(queryWrapper);
            }
            
            // 构建树形结构
            List<Permission> tree = buildTree(userMenus, 0L);
            
            return Result.success(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户菜单失败");
        }
    }
    
    /**
     * 获取菜单树（所有菜单，用于管理）
     */
    @ApiOperation("获取菜单树")
    @GetMapping("/tree")
    @PreAuthorize("hasAnyAuthority('system:menu:list', 'SUPER_ADMIN')")
    public Result<List<Permission>> getMenuTree() {
        // 查询所有菜单类型的权限
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getType, "menu")
                .orderByAsc(Permission::getSort);
        
        List<Permission> allMenus = permissionService.list(queryWrapper);
        
        // 构建树形结构
        List<Permission> tree = buildTree(allMenus, 0L);
        
        return Result.success(tree);
    }

    /**
     * 获取菜单详情
     */
    @ApiOperation("获取菜单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('system:menu:list', 'SUPER_ADMIN')")
    public Result<Permission> getMenuById(@PathVariable Long id) {
        Permission menu = permissionService.getById(id);
        if (menu == null) {
            return Result.error("菜单不存在");
        }
        return Result.success(menu);
    }

    /**
     * 创建菜单
     */
    @ApiOperation("创建菜单")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('system:menu:add', 'SUPER_ADMIN')")
    public Result<String> createMenu(@RequestBody Permission menu) {
        // 验证菜单类型
        if (!"menu".equals(menu.getType())) {
            menu.setType("menu");
        }
        
        // 验证编码唯一性
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getCode, menu.getCode());
        if (permissionService.count(queryWrapper) > 0) {
            return Result.error("菜单编码已存在");
        }
        
        // 设置默认值
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getAlwaysShow() == null) {
            menu.setAlwaysShow(0);
        }
        
        boolean success = permissionService.save(menu);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 更新菜单
     */
    @ApiOperation("更新菜单")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('system:menu:edit', 'SUPER_ADMIN')")
    public Result<String> updateMenu(@PathVariable Long id, @RequestBody Permission menu) {
        Permission existMenu = permissionService.getById(id);
        if (existMenu == null) {
            return Result.error("菜单不存在");
        }
        
        // 验证菜单类型
        if (!"menu".equals(existMenu.getType())) {
            return Result.error("只能更新菜单类型的权限");
        }
        
        // 验证编码唯一性（排除自己）
        if (menu.getCode() != null && !menu.getCode().equals(existMenu.getCode())) {
            LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Permission::getCode, menu.getCode());
            if (permissionService.count(queryWrapper) > 0) {
                return Result.error("菜单编码已存在");
            }
        }
        
        // 验证父菜单
        if (menu.getParentId() != null && !menu.getParentId().equals(0L)) {
            if (menu.getParentId().equals(id)) {
                return Result.error("父菜单不能是自己");
            }
            // 验证父菜单存在且为菜单类型
            Permission parentMenu = permissionService.getById(menu.getParentId());
            if (parentMenu == null || !"menu".equals(parentMenu.getType())) {
                return Result.error("父菜单不存在或不是菜单类型");
            }
        }
        
        menu.setId(id);
        menu.setType("menu"); // 确保类型不被修改
        boolean success = permissionService.updateById(menu);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除菜单
     */
    @ApiOperation("删除菜单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('system:menu:delete', 'SUPER_ADMIN')")
    public Result<String> deleteMenu(@PathVariable Long id) {
        Permission menu = permissionService.getById(id);
        if (menu == null) {
            return Result.error("菜单不存在");
        }
        
        if (!"menu".equals(menu.getType())) {
            return Result.error("只能删除菜单类型的权限");
        }
        
        // 检查是否有子菜单
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getParentId, id)
                .eq(Permission::getType, "menu");
        if (permissionService.count(queryWrapper) > 0) {
            return Result.error("请先删除子菜单");
        }
        
        boolean success = permissionService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除菜单
     */
    @ApiOperation("批量删除菜单")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyAuthority('system:menu:delete', 'SUPER_ADMIN')")
    public Result<String> batchDeleteMenu(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的菜单");
        }
        
        // 验证所有菜单
        for (Long id : ids) {
            Permission menu = permissionService.getById(id);
            if (menu == null) {
                return Result.error("菜单ID " + id + " 不存在");
            }
            if (!"menu".equals(menu.getType())) {
                return Result.error("ID " + id + " 不是菜单类型");
            }
            
            // 检查是否有子菜单
            LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Permission::getParentId, id)
                    .eq(Permission::getType, "menu");
            if (permissionService.count(queryWrapper) > 0) {
                return Result.error("菜单 " + menu.getName() + " 存在子菜单，无法删除");
            }
        }
        
        boolean success = permissionService.removeByIds(ids);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取父菜单选项（用于下拉选择）
     */
    @ApiOperation("获取父菜单选项")
    @GetMapping("/parent-options")
    @PreAuthorize("hasAnyAuthority('system:menu:list', 'SUPER_ADMIN')")
    public Result<List<Permission>> getParentMenuOptions() {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getType, "menu")
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSort);
        
        List<Permission> allMenus = permissionService.list(queryWrapper);
        List<Permission> tree = buildTree(allMenus, 0L);
        
        return Result.success(tree);
    }

    /**
     * 构建树形结构
     */
    private List<Permission> buildTree(List<Permission> allMenus, Long parentId) {
        List<Permission> tree = new ArrayList<>();
        
        // 按父ID分组
        Map<Long, List<Permission>> groupedMenus = allMenus.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
        
        // 获取指定父ID的菜单
        List<Permission> parentMenus = groupedMenus.getOrDefault(parentId, new ArrayList<>());
        
        for (Permission menu : parentMenus) {
            // 递归查找子菜单
            List<Permission> children = groupedMenus.getOrDefault(menu.getId(), new ArrayList<>());
            if (!children.isEmpty()) {
                // 递归构建子树
                menu.setChildren(buildTreeRecursive(children, groupedMenus));
            }
            tree.add(menu);
        }
        
        return tree;
    }

    /**
     * 递归构建树形结构
     */
    private List<Permission> buildTreeRecursive(List<Permission> menus, Map<Long, List<Permission>> groupedMenus) {
        for (Permission menu : menus) {
            List<Permission> children = groupedMenus.getOrDefault(menu.getId(), new ArrayList<>());
            if (!children.isEmpty()) {
                menu.setChildren(buildTreeRecursive(children, groupedMenus));
            }
        }
        return menus;
    }
}
