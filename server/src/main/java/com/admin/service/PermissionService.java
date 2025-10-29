package com.admin.service;

import com.admin.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author admin
 */
public interface PermissionService extends IService<Permission> {
    
    /**
     * 获取权限树形结构
     */
    List<Permission> getPermissionTree();
    
    /**
     * 获取用户的权限列表（根据用户的角色）
     */
    List<Permission> getUserPermissions(Long userId);
}

