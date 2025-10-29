package com.admin.service.impl;

import com.admin.entity.Permission;
import com.admin.mapper.PermissionMapper;
import com.admin.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author admin
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    
    private final PermissionMapper permissionMapper;
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public List<Permission> getPermissionTree() {
        // 获取所有权限
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<Permission> allPermissions = list(queryWrapper);
        
        // 构建树形结构
        return buildTree(allPermissions, 0L);
    }
    
    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return permissionMapper.getUserPermissions(userId);
    }
    
    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        String sql = "SELECT permission_id FROM sys_role_permission WHERE role_id = ? AND deleted = 0";
        return jdbcTemplate.queryForList(sql, Long.class, roleId);
    }
    
    /**
     * 递归构建树形结构
     */
    private List<Permission> buildTree(List<Permission> allPermissions, Long parentId) {
        List<Permission> children = new ArrayList<>();
        
        for (Permission permission : allPermissions) {
            // 空值安全检查
            Long permissionParentId = permission.getParentId();
            if (permissionParentId == null) {
                permissionParentId = 0L;
            }
            
            if (permissionParentId.equals(parentId)) {
                // 递归查找子节点
                List<Permission> subChildren = buildTree(allPermissions, permission.getId());
                if (!subChildren.isEmpty()) {
                    permission.setChildren(subChildren);
                }
                children.add(permission);
            }
        }
        
        return children;
    }
}

