package com.admin.service;

import com.admin.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author admin
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 为角色分配权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 获取角色的权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}

