package com.admin.mapper;

import com.admin.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 *
 * @author admin
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    
    /**
     * 获取用户的权限列表
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0 " +
            "ORDER BY p.sort ASC")
    List<Permission> getUserPermissions(Long userId);
}

