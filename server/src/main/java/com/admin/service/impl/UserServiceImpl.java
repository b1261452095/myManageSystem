package com.admin.service.impl;

import com.admin.entity.User;
import com.admin.mapper.UserMapper;
import com.admin.mapper.UserRoleMapper;
import com.admin.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 *
 * @author admin
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    
    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("deleted", 0);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User getUserWithRolesByUsername(String username) {
        User user = userMapper.selectUserWithRolesByUsername(username);
        if (user != null && user.getId() != null) {
            try {
                // 获取用户的角色列表
                java.util.List<com.admin.entity.Role> roles = userMapper.selectRolesByUserId(user.getId());
                if (roles != null && !roles.isEmpty()) {
                    // 为每个角色获取权限列表
                    for (com.admin.entity.Role role : roles) {
                        if (role.getId() != null) {
                            role.setPermissions(userMapper.selectPermissionsByRoleId(role.getId()));
                        }
                    }
                }
                user.setRoles(roles);
            } catch (Exception e) {
                // 如果查询角色权限失败，至少返回用户基本信息
                e.printStackTrace();
                user.setRoles(new java.util.ArrayList<>());
            }
        }
        return user;
    }
    
    @Override
    public User getUserWithRolesById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            // 获取用户的角色ID列表
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(id);
            user.setRoleIds(roleIds);
            
            // 获取用户的角色详细信息
            if (roleIds != null && !roleIds.isEmpty()) {
                List<com.admin.entity.Role> roles = userMapper.selectRolesByUserId(id);
                user.setRoles(roles);
            }
        }
        return user;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserWithRoles(User user) {
        // 保存用户基本信息
        this.save(user);
        
        // 保存用户角色关联
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            userRoleMapper.batchInsert(user.getId(), user.getRoleIds());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserWithRoles(User user) {
        // 更新用户基本信息
        this.updateById(user);
        
        // 删除原有角色关联
        userRoleMapper.deleteByUserId(user.getId());
        
        // 保存新的角色关联
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            userRoleMapper.batchInsert(user.getId(), user.getRoleIds());
        }
    }
}
