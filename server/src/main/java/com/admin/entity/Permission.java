package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 *
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission")
public class Permission {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 权限名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 权限编码
     */
    @TableField("code")
    private String code;
    
    /**
     * 权限类型：menu-菜单，button-按钮，api-接口
     */
    @TableField("type")
    private String type;
    
    /**
     * 路径
     */
    @TableField("path")
    private String path;
    
    /**
     * 组件
     */
    @TableField("component")
    private String component;
    
    /**
     * 图标
     */
    @TableField("icon")
    private String icon;
    
    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;
    
    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private Long parentId;
    
    /**
     * 是否显示：0-隐藏，1-显示
     */
    @TableField("visible")
    private Integer visible;
    
    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 重定向路径
     */
    @TableField("redirect")
    private String redirect;
    
    /**
     * 是否总是显示：0-否，1-是
     */
    @TableField("always_show")
    private Integer alwaysShow;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    
    /**
     * 子权限列表（不映射到数据库）
     */
    @TableField(exist = false)
    private List<Permission> children;
}
