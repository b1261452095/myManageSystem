package com.admin.service;

import com.admin.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 *
 * @author admin
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 根据用户名获取用户信息（包含角色信息）
     */
    User getUserWithRolesByUsername(String username);
    
    /**
     * 根据ID获取用户信息（包含角色信息）
     */
    User getUserWithRolesById(Long id);
    
    /**
     * 保存用户及其角色关联
     */
    void saveUserWithRoles(User user);
    
    /**
     * 更新用户及其角色关联
     */
    void updateUserWithRoles(User user);
}
