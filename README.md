# 企业级后台管理系统

一个基于 Vue 3 + Spring Boot 的现代化企业级后台管理系统，采用前后端分离架构，提供完整的用户管理、角色管理、权限控制等功能。

## 🚀 项目特性

- 🎯 **现代化技术栈**：Vue 3 + TypeScript + Spring Boot 3
- 🎨 **优雅的UI**：基于 Element Plus 组件库
- 🔐 **完整的认证授权**：JWT + Spring Security
- 📱 **响应式设计**：支持多种设备访问
- 🐳 **容器化部署**：Docker + Docker Compose
- 📚 **API文档**：集成 Knife4j (Swagger)
- 🎪 **代码规范**：ESLint + Prettier

## 🛠️ 技术栈

### 前端技术栈
- **框架**：Vue 3.4+
- **语言**：TypeScript
- **构建工具**：Vite 5.x
- **UI框架**：Ant Design Vue 4.x（按需加载）
- **状态管理**：Pinia 2.x
- **路由管理**：Vue Router 4.x
- **HTTP客户端**：Axios
- **自动导入**：unplugin-vue-components、unplugin-auto-import

### 后端技术栈
- **框架**：Spring Boot 3.2+
- **语言**：Java 17
- **安全框架**：Spring Security
- **数据库**：MySQL 8.0
- **缓存**：Redis
- **ORM**：MyBatis Plus
- **认证**：JWT
- **API文档**：Knife4j
- **构建工具**：Maven

## 📁 项目结构

```
企业级后台管理系统/
├── web/                     # 前端项目 (Vue 3 + TypeScript)
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 公共组件
│   │   ├── layout/          # 布局组件
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # 状态管理
│   │   ├── types/           # 类型定义
│   │   ├── utils/           # 工具函数
│   │   └── views/           # 页面组件
│   ├── package.json
│   ├── vite.config.ts
│   └── Dockerfile
├── server/                  # 后端项目 (Spring Boot + Java 17)
│   ├── src/main/java/com/admin/
│   │   ├── common/          # 公共模块
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── dto/             # 数据传输对象
│   │   ├── entity/          # 实体类
│   │   ├── mapper/          # 数据访问层
│   │   ├── security/        # 安全配置
│   │   ├── service/         # 业务逻辑层
│   │   └── util/            # 工具类
│   ├── src/main/resources/
│   │   ├── sql/             # SQL脚本
│   │   └── application.yml  # 配置文件
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml       # Docker编排文件
├── start-backend.sh         # 后端启动脚本
└── start-frontend.sh        # 前端启动脚本
```

## 🚀 快速开始

### 环境要求

- Node.js 18+
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose（可选）

### 方式一：Docker 部署（推荐）

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd 企业级后台管理系统
   ```

2. **启动服务**
   ```bash
   docker-compose up -d
   ```

3. **访问系统**
   - 前端地址：http://localhost
   - 后端API：http://localhost:8080
   - API文档：http://localhost:8080/doc.html

### 方式二：本地开发

#### 后端启动

1. **创建数据库**
   ```sql
   CREATE DATABASE admin_system DEFAULT CHARSET utf8mb4;
   ```

2. **导入初始数据**
   ```bash
   mysql -u root -p admin_system < server/src/main/resources/sql/init.sql
   ```

3. **修改配置**
   编辑 `server/src/main/resources/application.yml`，配置数据库和Redis连接信息

4. **启动后端**
   ```bash
   # 使用启动脚本（推荐）
   ./start-backend.sh
   
   # 或者手动启动
   cd server
   mvn spring-boot:run
   ```

#### 前端启动

1. **使用启动脚本（推荐）**
   ```bash
   ./start-frontend.sh
   ```

2. **或者手动启动**
   ```bash
   cd web
   npm install
   npm run dev
   ```

3. **访问系统**
   浏览器访问：http://localhost:3000

## 👤 默认账号

- **用户名**：admin
- **密码**：123456

## 📋 主要功能

### 系统管理
- [x] 用户管理：用户的增删改查、状态管理
- [x] 角色管理：角色的增删改查、权限分配
- [x] 权限管理：菜单权限、按钮权限、接口权限
- [x] 登录认证：JWT token认证
- [x] 权限控制：基于RBAC的权限控制

### 系统监控
- [ ] 系统监控：CPU、内存、磁盘使用情况
- [ ] 接口监控：接口调用统计、响应时间
- [ ] 日志管理：系统日志、操作日志查询

### 开发工具
- [x] API文档：在线API文档
- [ ] 代码生成：基于数据库表生成CRUD代码
- [ ] 系统配置：动态配置管理

## 🔧 开发指南

### 前端开发

1. **新增页面**
   - 在 `src/views` 下创建页面组件
   - 在 `src/router/index.ts` 中配置路由
   - 在 `src/api` 中添加接口调用

2. **状态管理**
   - 使用 Pinia 进行状态管理
   - 在 `src/stores` 下创建对应的store

3. **样式规范**
   - 使用 SCSS 预处理器
   - 遵循 BEM 命名规范
   - 支持响应式设计

### 后端开发

1. **新增模块**
   - 创建实体类（Entity）
   - 创建数据访问层（Mapper）
   - 创建业务逻辑层（Service）
   - 创建控制器（Controller）

2. **数据库操作**
   - 使用 MyBatis Plus 简化CRUD操作
   - 支持分页查询、条件查询
   - 支持逻辑删除

3. **接口文档**
   - 使用 Knife4j 注解生成API文档
   - 访问地址：http://localhost:8080/doc.html

## 🐳 Docker 部署

项目已配置完整的 Docker 化部署方案：

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

## 📊 系统截图

### 登录页面
- 现代化的登录界面设计
- 支持用户名密码登录
- 响应式布局适配

### 仪表板
- 数据统计卡片展示
- 图表数据可视化
- 系统运行状态监控

### 用户管理
- 用户列表展示
- 用户信息增删改查
- 用户状态管理

### 角色管理
- 角色列表管理
- 角色权限分配
- 权限树形结构展示

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 开源协议

本项目基于 MIT 协议开源，详情请参阅 [LICENSE](LICENSE) 文件。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱：admin@example.com
- 项目地址：[GitHub Repository]

---

⭐ 如果这个项目对你有帮助，请给一个 Star 支持一下！
