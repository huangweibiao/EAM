# EAM 系统配置指南

本文档详细说明EAM企业资产管理系统的各项配置。

## 目录

1. [数据库配置](#数据库配置)
2. [OAuth2配置](#oauth2配置)
3. [后端配置](#后端配置)
4. [前端配置](#前端配置)
5. [部署配置](#部署配置)

---

## 数据库配置

### 1. 创建数据库

确保MySQL服务已启动，然后执行以下SQL创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS eam DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 执行初始化脚本

```bash
mysql -u root -p eam < backend/src/main/resources/db/init.sql
```

或直接在MySQL客户端中执行 `backend/src/main/resources/db/init.sql` 文件。

### 3. 修改数据库连接配置

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/eam?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root              # 修改为你的MySQL用户名
    password: your_password     # 修改为你的MySQL密码
```

### 数据库连接池配置 (HikariCP)

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 5           # 最小空闲连接数
      maximum-pool-size: 20      # 最大连接池大小
      connection-timeout: 30000  # 连接超时时间(毫秒)
      idle-timeout: 600000      # 空闲超时时间(毫秒)
      max-lifetime: 1800000     # 连接最大生命周期(毫秒)
```

---

## OAuth2配置

本系统使用Spring Security OAuth2客户端进行单点登录集成。

### OAuth2服务端配置

你需要一个OAuth2授权服务器。可以是以下任意一种：
- Keycloak
- Spring Authorization Server
- Okta
- Auth0
- 其他标准OAuth2/OIDC提供者

### 配置OAuth2客户端

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          oauth2-server:           # 客户端注册名称(可自定义)
            client-id: eam-client                # OAuth2客户端ID
            client-secret: eam-secret             # OAuth2客户端密钥
            authorization-grant-type: authorization_code  # 授权类型
            redirect-uri: http://localhost:8080/eam/login/oauth2/code/oauth2-server  # 回调地址
            scope: read,write                     # 授权范围
        provider:
          oauth2-server:           # 提供者名称(必须与registration一致)
            authorization-uri: http://your-oauth2-server/oauth2/authorize      # 授权端点
            token-uri: http://your-oauth2-server/oauth2/token                  # 令牌端点
            user-info-uri: http://your-oauth2-server/oauth2/userinfo          # 用户信息端点
            user-name-attribute: username                                  # 用户名属性
```

### 配置说明

| 配置项 | 说明 | 示例 |
|--------|------|------|
| `client-id` | OAuth2客户端ID | `eam-client` |
| `client-secret` | OAuth2客户端密钥 | `eam-secret` |
| `authorization-grant-type` | 授权类型，通常为 `authorization_code` | `authorization_code` |
| `redirect-uri` | 授权成功后的回调地址 | `http://localhost:8080/eam/login/oauth2/code/oauth2-server` |
| `scope` | 请求的权限范围 | `read,write` |
| `authorization-uri` | OAuth2授权端点 | `http://localhost:9000/oauth2/authorize` |
| `token-uri` | OAuth2令牌端点 | `http://localhost:9000/oauth2/token` |
| `user-info-uri` | 用户信息端点 | `http://localhost:9000/oauth2/userinfo` |
| `user-name-attribute` | 用户信息中用户名的字段名 | `username` |

### 在OAuth2服务端配置回调地址

确保在OAuth2服务端配置以下回调地址：
- `http://localhost:8080/eam/login/oauth2/code/oauth2-server`

生产环境请替换为实际域名。

---

## 后端配置

### 服务器配置

```yaml
server:
  port: 8080                          # 服务端口
  servlet:
    context-path: /eam                # 应用上下文路径
```

### 应用配置

```yaml
spring:
  application:
    name: eam-system                  # 应用名称
```

### JPA配置

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update                # 数据库模式自动更新(开发环境) / validate(生产环境)
    show-sql: true                    # 显示SQL语句
    properties:
      hibernate:
        format_sql: true              # 格式化SQL输出
        dialect: org.hibernate.dialect.MySQL8Dialect  # 数据库方言
```

**生产环境建议将 `ddl-auto` 设置为 `validate` 或 `none`，避免自动更新表结构。**

### MyBatis配置

```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml    # Mapper XML文件位置
  type-aliases-package: com.eam.entity       # 实体类包路径
  configuration:
    map-underscore-to-camel-case: true       # 开启下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 日志实现
```

### 日志配置

```yaml
logging:
  level:
    com.eam: debug                   # 应用日志级别
    org.springframework.security: debug  # Security日志级别
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

**生产环境建议将日志级别调整为 `info` 或 `warn`。**

---

## 前端配置

### Vite配置

编辑 `frontend/vite.config.ts`:

```typescript
export default defineConfig({
  server: {
    port: 5173,                    # 开发服务器端口
    proxy: {
      '/eam': {
        target: 'http://localhost:8080',  // 后端服务地址
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: '../backend/src/main/resources/static',  // 构建输出目录
    emptyOutDir: true,
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus']  // 分包配置
        }
      }
    }
  }
})
```

### 生产环境部署

前端构建产物会被输出到 `backend/src/main/resources/static` 目录，实现前后端不分离部署。

修改 `outDir` 可以改变前端构建产物的输出位置。

---

## 部署配置

### 开发环境

1. 启动后端：
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. 启动前端：
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. 访问：
   - 前端开发服务器：http://localhost:5173
   - 后端API：http://localhost:8080/eam

### 生产环境

1. 构建前端：
   ```bash
   cd frontend
   npm run build
   ```

2. 打包后端：
   ```bash
   cd backend
   mvn clean package
   ```

3. 运行：
   ```bash
   java -jar backend/target/eam-system-1.0.0.jar
   ```

4. 访问：http://localhost:8080/eam

### 使用部署脚本

项目提供了便捷的部署脚本：

**Linux/Mac:**
```bash
chmod +x deploy.sh
./deploy.sh
```

**Windows:**
```bash
cd frontend
start.bat
```

### 环境变量配置

建议使用环境变量管理敏感配置：

```bash
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
export OAUTH2_CLIENT_ID=your_client_id
export OAUTH2_CLIENT_SECRET=your_client_secret
```

然后在 `application.yml` 中引用：

```yaml
spring:
  datasource:
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
```

---

## 安全配置

### HTTPS配置

生产环境建议启用HTTPS：

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: your_keystore_password
    key-store-type: PKCS12
```

### CORS配置

如需要跨域支持，可在 `SecurityConfig.java` 中添加：

```java
http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
```

### Session配置

```yaml
server:
  servlet:
    session:
      timeout: 30m              # Session超时时间
      cookie:
        http-only: true
        secure: true            # 仅HTTPS传输Cookie
```

---

## 常见问题

### 1. 数据库连接失败

**问题**: `Communications link failure`

**解决**:
- 检查MySQL服务是否启动
- 检查数据库连接配置（URL、用户名、密码）
- 检查防火墙设置

### 2. OAuth2登录失败

**问题**: OAuth2 authorization failed

**解决**:
- 检查OAuth2服务端是否正常运行
- 检查回调地址配置是否正确
- 检查客户端ID和密钥是否正确
- 查看日志获取详细错误信息

### 3. 前端无法连接后端API

**问题**: Network Error / CORS Error

**解决**:
- 检查后端服务是否正常启动
- 检查Vite代理配置
- 检查后端CORS配置

### 4. 构建失败

**问题**: Build failed

**解决**:
- 清理缓存：`mvn clean` 或 `rm -rf node_modules`
- 重新安装依赖：`npm install`
- 检查Java/Node.js/Maven版本是否符合要求

---

## 技术支持

如有问题，请查看：
1. 项目README.md
2. 日志文件
3. 本配置指南

---

**最后更新时间**: 2026-03-15
