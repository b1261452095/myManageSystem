package com.admin.service.impl;

import com.admin.entity.Role;
import com.admin.mapper.RoleMapper;
import com.admin.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现类
 *
 * @author admin
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除该角色的所有权限
        jdbcTemplate.update("DELETE FROM sys_role_permission WHERE role_id = ?", roleId);
        
        // 批量插入新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            String sql = "INSERT INTO sys_role_permission (role_id, permission_id) VALUES (?, ?)";
            for (Long permissionId : permissionIds) {
                jdbcTemplate.update(sql, roleId, permissionId);
            }
        }
    }
    
    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        String sql = "SELECT permission_id FROM sys_role_permission WHERE role_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, roleId);
    }
}

