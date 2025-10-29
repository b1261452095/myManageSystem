package com.admin.mapper;

import com.admin.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author admin
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户信息（包含角色信息）
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User selectUserWithRolesByUsername(@Param("username") String username);
    
    /**
     * 根据用户ID查询用户角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<com.admin.entity.Role> selectRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0")
    List<com.admin.entity.Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}
