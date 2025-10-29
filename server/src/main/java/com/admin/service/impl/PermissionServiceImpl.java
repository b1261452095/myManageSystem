package com.admin.service.impl;

import com.admin.entity.Permission;
import com.admin.mapper.PermissionMapper;
import com.admin.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
    
    /**
     * 递归构建树形结构
     */
    private List<Permission> buildTree(List<Permission> allPermissions, Long parentId) {
        List<Permission> children = new ArrayList<>();
        
        for (Permission permission : allPermissions) {
            if (permission.getParentId().equals(parentId)) {
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

