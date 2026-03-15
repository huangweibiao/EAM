# EAM 快速启动指南

## 📋 前置条件

确保已安装以下软件：

- **JDK 17+**: [下载地址](https://adoptium.net/)
- **Node.js 18+**: [下载地址](https://nodejs.org/)
- **MySQL 8.0+**: [下载地址](https://dev.mysql.com/downloads/mysql/)
- **Maven 3.8+**: [下载地址](https://maven.apache.org/download.cgi)

验证安装：

```bash
java -version
node -v
mysql --version
mvn -v
```

---

## 🚀 5分钟快速启动

### 步骤 1: 配置数据库 (1分钟)

```bash
# 1. 启动MySQL服务
sudo systemctl start mysql  # Linux/Mac
# 或 Windows: 在服务管理器中启动MySQL服务

# 2. 创建数据库并导入数据
mysql -u root -p <<EOF
CREATE DATABASE IF NOT EXISTS eam DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eam;
SOURCE backend/src/main/resources/db/init.sql;
EOF
```

### 步骤 2: 修改配置文件 (1分钟)

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root              # 修改为你的MySQL用户名
    password: your_password     # 修改为你的MySQL密码
```

**重要**: 如果没有OAuth2服务端，暂时可以跳过OAuth2配置，使用测试接口。

### 步骤 3: 启动后端服务 (2分钟)

```bash
cd backend
mvn spring-boot:run
```

等待Maven下载依赖，直到看到类似以下日志：

```
Started EamApplication in X.XXX seconds
```

**后端服务已启动**: http://localhost:8080/eam

### 步骤 4: 启动前端服务 (1分钟)

**新开一个终端窗口**：

```bash
cd frontend
npm install
npm run dev
```

**前端服务已启动**: http://localhost:5173

---

## 🎯 访问系统

### 开发环境

1. **前端开发服务器**: http://localhost:5173
2. **后端API**: http://localhost:8080/eam

### 测试API（无需OAuth2）

```bash
# 测试接口
curl http://localhost:8080/eam/api/hello

# 应该返回:
# {"code":200,"message":"Success","data":"Welcome to EAM Enterprise Asset Management System!"}
```

### OAuth2登录流程

1. 访问 http://localhost:5173
2. 点击"OAuth2单点登录"按钮
3. 完成OAuth2授权
4. 自动登录成功

---

## 🏗️ 生产环境部署

### 一键部署

```bash
./deploy.sh
```

这个脚本会：
1. 构建前端项目
2. 构建后端项目
3. 启动Spring Boot应用

### 手动部署

```bash
# 1. 构建前端
cd frontend
npm run build

# 2. 构建后端
cd ../backend
mvn clean package

# 3. 运行
java -jar target/eam-system-1.0.0.jar
```

访问: http://localhost:8080/eam

---

## 📝 常用命令

### 后端

```bash
# 启动开发服务器
cd backend && mvn spring-boot:run

# 使用启动脚本
cd backend && ./start.sh

# 清理并重新构建
cd backend && mvn clean package

# 运行测试
cd backend && mvn test
```

### 前端

```bash
# 启动开发服务器
cd frontend && npm run dev

# 使用启动脚本（Linux/Mac）
cd frontend && ./start.sh

# 使用启动脚本（Windows）
cd frontend && start.bat

# 构建生产版本
cd frontend && npm run build

# 类型检查
cd frontend && npm run type-check
```

---

## ⚙️ 配置说明

### 数据库配置

位置: `backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eam
    username: root
    password: your_password
```

### OAuth2配置

位置: `backend/src/main/resources/application.yml`

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          oauth2-server:
            client-id: your_client_id
            client-secret: your_client_secret
        provider:
          oauth2-server:
            authorization-uri: http://your-oauth2-server/oauth2/authorize
            token-uri: http://your-oauth2-server/oauth2/token
            user-info-uri: http://your-oauth2-server/oauth2/userinfo
```

详细配置请参考 [CONFIG.md](CONFIG.md)

---

## 🔧 故障排查

### 问题1: 数据库连接失败

**错误信息**: `Communications link failure`

**解决方案**:
```bash
# 检查MySQL服务是否启动
sudo systemctl status mysql

# 检查数据库是否存在
mysql -u root -p -e "SHOW DATABASES LIKE 'eam';"

# 检查用户名密码是否正确
mysql -u root -p eam
```

### 问题2: Maven依赖下载慢

**解决方案**:
```bash
# 使用阿里云Maven镜像
# 编辑 ~/.m2/settings.xml，添加:
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### 问题3: npm install 失败

**解决方案**:
```bash
# 清理缓存
npm cache clean --force

# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 重新安装
rm -rf node_modules package-lock.json
npm install
```

### 问题4: 端口被占用

**错误信息**: `Address already in use`

**解决方案**:
```bash
# Linux/Mac: 查看端口占用
lsof -i :8080
lsof -i :5173

# 杀死进程
kill -9 <PID>

# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### 问题5: OAuth2登录失败

**解决方案**:
1. 检查OAuth2服务端是否运行
2. 检查OAuth2配置是否正确
3. 检查回调地址配置
4. 查看后端日志获取详细错误信息

---

## 📚 更多资源

- [README.md](README.md) - 项目概览
- [CONFIG.md](CONFIG.md) - 详细配置指南
- [INIT_COMPLETE.md](INIT_COMPLETE.md) - 初始化完成说明

---

## 💡 开发建议

### 开发环境推荐设置

1. **IDE配置**
   - 后端: IntelliJ IDEA
   - 前端: VS Code + Volar插件

2. **数据库工具**
   - Navicat
   - DBeaver
   - MySQL Workbench

3. **API测试工具**
   - Postman
   - Apifox

### 代码风格

- 后端: 使用Lombok简化代码
- 前端: 使用Vue 3 Composition API
- 遵循ESLint和Prettier规范

---

## 🆘 获取帮助

遇到问题？

1. 查看日志文件
2. 检查配置文件
3. 参考文档（README.md, CONFIG.md）
4. 联系项目维护者

---

**祝使用愉快！** 🎉
