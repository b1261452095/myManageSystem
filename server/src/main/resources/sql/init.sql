-- 创建数据库
CREATE DATABASE IF NOT EXISTS admin_system DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE admin_system;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) COMMENT '用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_code (code),
    INDEX idx_deleted (deleted)
) COMMENT '角色表';

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    type VARCHAR(20) NOT NULL COMMENT '权限类型：menu-菜单，button-按钮，api-接口',
    path VARCHAR(255) COMMENT '路径',
    component VARCHAR(255) COMMENT '组件',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    visible TINYINT DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    redirect VARCHAR(255) COMMENT '重定向路径',
    always_show TINYINT DEFAULT 0 COMMENT '是否总是显示：0-否，1-是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_code (code),
    INDEX idx_type (type),
    INDEX idx_parent_id (parent_id),
    INDEX idx_deleted (deleted)
) COMMENT '权限表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) COMMENT '用户角色关联表';

-- 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) COMMENT '角色权限关联表';

-- 插入初始数据

-- 插入超级管理员用户（密码：123456，使用BCrypt加密）
INSERT INTO sys_user (username, password, nickname, email, status) VALUES 
('admin', '$2a$10$Pbj5a11BoFK1mpU093YnGOZiiXRDqCwgw3sMqbC8HQLDnD2x6JDHq', '超级管理员', 'admin@example.com', 1);

-- 插入角色
INSERT INTO sys_role (name, code, description) VALUES 
('超级管理员', 'SUPER_ADMIN', '拥有系统所有权限'),
('普通用户', 'USER', '普通用户权限');

-- 插入权限（菜单）
-- 一级菜单（分别插入以获取准确的ID）
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('工作台', 'dashboard', 'menu', '/dashboard', 'dashboard/index', 'dashboard', 1, 0, 1, 1);
SET @dashboard_id = LAST_INSERT_ID();

INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('系统管理', 'system', 'menu', '/system', NULL, 'setting', 2, 0, 1, 1);
SET @system_id = LAST_INSERT_ID();

-- 系统管理子菜单（分别插入以获取准确的ID）
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('用户管理', 'system:user', 'menu', '/user/list', 'user/list', 'user', 1, @system_id, 1, 1);
SET @user_menu_id = LAST_INSERT_ID();

INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('角色管理', 'system:role', 'menu', '/system/role', 'system/role/index', 'team', 2, @system_id, 1, 1);
SET @role_menu_id = LAST_INSERT_ID();

INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('权限管理', 'system:permission', 'menu', '/system/permission', 'system/permission/index', 'safety', 3, @system_id, 1, 1);
SET @permission_menu_id = LAST_INSERT_ID();

INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('菜单管理', 'system:menu', 'menu', '/system/menu', 'system/menu/index', 'menu', 4, @system_id, 1, 1);
SET @menu_menu_id = LAST_INSERT_ID();

-- 用户管理按钮权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('用户查询', 'system:user:list', 'button', '', '', '', 1, @user_menu_id, 0, 1),
('用户新增', 'system:user:add', 'button', '', '', '', 2, @user_menu_id, 0, 1),
('用户修改', 'system:user:edit', 'button', '', '', '', 3, @user_menu_id, 0, 1),
('用户删除', 'system:user:delete', 'button', '', '', '', 4, @user_menu_id, 0, 1);

-- 角色管理按钮权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('角色查询', 'system:role:list', 'button', '', '', '', 1, @role_menu_id, 0, 1),
('角色新增', 'system:role:add', 'button', '', '', '', 2, @role_menu_id, 0, 1),
('角色修改', 'system:role:edit', 'button', '', '', '', 3, @role_menu_id, 0, 1),
('角色删除', 'system:role:delete', 'button', '', '', '', 4, @role_menu_id, 0, 1);

-- 权限管理按钮权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('权限查询', 'system:permission:list', 'button', '', '', '', 1, @permission_menu_id, 0, 1),
('权限新增', 'system:permission:add', 'button', '', '', '', 2, @permission_menu_id, 0, 1),
('权限修改', 'system:permission:edit', 'button', '', '', '', 3, @permission_menu_id, 0, 1),
('权限删除', 'system:permission:delete', 'button', '', '', '', 4, @permission_menu_id, 0, 1);

-- 菜单管理按钮权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, parent_id, visible, status) VALUES 
('菜单查询', 'system:menu:list', 'button', '', '', '', 1, @menu_menu_id, 0, 1),
('菜单新增', 'system:menu:add', 'button', '', '', '', 2, @menu_menu_id, 0, 1),
('菜单修改', 'system:menu:edit', 'button', '', '', '', 3, @menu_menu_id, 0, 1),
('菜单删除', 'system:menu:delete', 'button', '', '', '', 4, @menu_menu_id, 0, 1);

-- 给超级管理员分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 给超级管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 1, id FROM sys_permission WHERE deleted = 0;
