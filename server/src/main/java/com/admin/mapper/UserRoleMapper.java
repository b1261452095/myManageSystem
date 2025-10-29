package com.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.admin.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联 Mapper
 *
 * @author admin
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    
    /**
     * 根据用户ID查询角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID删除用户角色关联
     */
    void deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 批量插入用户角色关联
     */
    void batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}

