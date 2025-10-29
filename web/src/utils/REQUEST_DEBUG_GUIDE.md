# 请求拦截器调试指南

## 📖 响应拦截器工作原理

### 拦截器执行流程

```
发起请求 
  ↓
请求拦截器（添加 token 等）
  ↓
发送到服务器
  ↓
收到响应
  ↓
响应拦截器判断
  ├─ HTTP 状态码 2xx → 成功回调
  │   ├─ 检查业务码 (res.code)
  │   ├─ code = 200/0 → 返回数据 ✅
  │   └─ code ≠ 200/0 → Promise.reject ❌
  │
  └─ HTTP 状态码 4xx/5xx → 失败回调 ❌
      ├─ 401: 未授权
      ├─ 403: 无权限
      ├─ 404: 资源不存在
      ├─ 500: 服务器错误
      └─ 其他错误
```

## 🔍 常见问题诊断

### 问题 1：error 回调一直触发

#### 可能原因：

**1️⃣ 后端服务未启动**
```bash
# 检查后端是否运行
curl http://localhost:8080/api/auth/login

# 或者查看进程
ps aux | grep java
```

**解决方案**：
```bash
# 启动后端服务
cd server
./mvnw spring-boot:run

# 或使用脚本
cd ../
./start-backend.sh
```

---

**2️⃣ 端口冲突**
```bash
# 检查 8080 端口是否被占用
lsof -i :8080

# 杀死占用端口的进程
kill -9 <PID>
```

---

**3️⃣ 网络错误或超时**

症状：
- 控制台显示 `error.request` 存在但 `error.response` 不存在
- 错误信息：`Network Error` 或 `timeout`

解决方案：
```javascript
// 检查超时设置（request.ts）
const service = axios.create({
  baseURL: '/api',
  timeout: 15000, // 可以适当增加
})
```

---

**4️⃣ CORS 跨域问题**

症状：
- 浏览器控制台报错：`Access-Control-Allow-Origin`
- Network 面板显示请求被阻止

解决方案：
```java
// 后端添加 CORS 配置（SecurityConfig.java）
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    // ...
}
```

---

**5️⃣ 代理配置问题**

检查 `vite.config.ts`：
```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080', // 确保端口正确
      changeOrigin: true,
      // 如果需要重写路径
      // rewrite: (path) => path.replace(/^\/api/, '')
    },
  },
}
```

---

**6️⃣ 循环调用问题**

症状：
- 错误无限循环触发
- `logout()` 被反复调用

原因：
```
请求失败 → 401错误 → 调用logout() 
    ↓
logout()发起请求 → 又失败 → 又调用logout() 
    ↓
无限循环 💥
```

解决方案（已在代码中实现）：
```typescript
// 使用标志位防止重复调用
let isRefreshing = false

if (res.code === 401) {
  if (!isRefreshing) {
    isRefreshing = true
    userStore.logout()
    setTimeout(() => {
      isRefreshing = false
    }, 3000)
  }
}
```

## 🛠️ 调试技巧

### 1. 使用浏览器开发者工具

**Network 面板**：
- 查看请求是否发出
- 查看响应状态码
- 查看响应内容

**Console 面板**：
- 查看错误日志
- 使用 `console.log` 打印关键信息

### 2. 添加调试日志

```typescript
// 在响应拦截器中添加日志
service.interceptors.response.use(
  (response) => {
    console.log('✅ 响应成功:', response.config.url, response.data)
    // ...
  },
  (error) => {
    console.log('❌ 响应错误:', {
      url: error.config?.url,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message,
    })
    // ...
  }
)
```

### 3. 检查请求和响应数据

```typescript
// 在调用 API 时添加日志
try {
  const res = await getUserList(params)
  console.log('用户列表响应:', res)
} catch (error) {
  console.error('获取用户列表失败:', error)
}
```

## 📋 错误类型对照表

| 错误类型 | 触发位置 | 原因 | 解决方案 |
|---------|---------|------|---------|
| `error.response` 存在 | 失败回调 | HTTP 状态码错误 | 检查后端接口 |
| `error.request` 存在，`response` 不存在 | 失败回调 | 请求超时/网络错误 | 检查网络和后端服务 |
| 两者都不存在 | 失败回调 | 请求配置错误 | 检查代码逻辑 |
| `res.code !== 200` | 成功回调 | 业务逻辑错误 | 检查业务代码 |

## 🎯 完整的错误处理流程

```typescript
// API 调用处
try {
  const res = await getUserList(params)
  // 成功处理
  return res.data
} catch (error: any) {
  // 错误已经在拦截器中处理过了
  // 这里可以做额外的业务处理
  console.error('业务层错误:', error)
  throw error
}
```

## 🚀 快速排查步骤

1. **检查后端是否启动**
   ```bash
   curl http://localhost:8080/api/auth/login
   ```

2. **检查浏览器 Network 面板**
   - 请求是否发出？
   - 状态码是多少？
   - 响应内容是什么？

3. **检查控制台错误日志**
   - 是什么类型的错误？
   - 错误信息是什么？

4. **检查代理配置**
   - `vite.config.ts` 中的 proxy 配置是否正确？

5. **重启前后端服务**
   ```bash
   # 重启前端
   npm run dev
   
   # 重启后端
   cd server && ./mvnw spring-boot:run
   ```

## 💡 最佳实践

### 1. 统一错误处理

在拦截器中统一处理错误，避免在每个 API 调用处重复处理：

```typescript
// ✅ 推荐：拦截器统一处理
service.interceptors.response.use(
  (response) => response.data,
  (error) => {
    // 统一错误处理
    handleError(error)
    return Promise.reject(error)
  }
)
```

### 2. 区分错误类型

```typescript
// 业务错误
if (res.code !== 200) {
  // 显示业务错误信息
}

// HTTP 错误
if (error.response) {
  // 显示 HTTP 错误信息
}

// 网络错误
if (!error.response && error.request) {
  // 显示网络错误信息
}
```

### 3. 防止重复提示

使用标志位或防抖，避免错误提示多次弹出：

```typescript
let isRefreshing = false

if (error.code === 401 && !isRefreshing) {
  isRefreshing = true
  // 处理错误
  setTimeout(() => isRefreshing = false, 3000)
}
```

### 4. 优雅降级

```typescript
try {
  const res = await getUserList()
  return res.data
} catch (error) {
  // 返回空数组，而不是让页面崩溃
  return { data: [], total: 0 }
}
```

## 🔗 相关资源

- [Axios 文档](https://axios-http.com/docs/intro)
- [Vite 代理配置](https://vitejs.dev/config/server-options.html#server-proxy)
- [HTTP 状态码](https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status)

