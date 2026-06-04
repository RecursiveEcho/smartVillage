# SmartVillages — 智慧乡村综合管理系统

本仓库为 **monorepo**，采用 **微服务架构**：后端按业务拆分为独立进程，前端为 Vue 3 + Vite 单页应用。

## 目录结构

```
SmartVillages/
├── SmartVillages-Backend/        # 后端：Spring Boot 3 + Maven 多模块/多服务
│   ├── common/                   #   公共库（无启动类）：安全、异常、工具类
│   ├── common-lib/               #   纯 POJO/DTO 公共库（无 Spring 依赖）
│   ├── auth/ / admin/ / announcement/ / feature/
│   │   interaction/ / media/ / management/
│   │                           #   业务 jar 模块，仅依赖 common
│   ├── auth-service/             #   认证微服务（端口 8082）
│   ├── business-service/         #   业务微服务（端口 8081）
│   ├── media-service/            #   媒体微服务（端口 8083，含 RabbitMQ）
│   ├── gateway-service/          #   Spring Cloud Gateway（端口 8080）
│   ├── service/                  #   单进程启动模块（遗留，可独立使用）
│   └── scripts/                  #   辅助工具（数据生成脚本等）
├── SmartVillages-Web/            # 前端：Vue 3 + Vite
└── 开发手册.md / 项目回顾.md      # 开发笔记
```

## 架构概览

```
                ┌──────────────┐
                │  Gateway     │  ← 端口 8080（入口）
                │  8080        │
                └──────┬───────┘
         ┌─────────────┼──────────────┐
         ▼             ▼              ▼
   ┌──────────┐ ┌───────────┐ ┌────────────┐
   │ Auth     │ │ Business  │ │ Media      │
   │ Service  │ │ Service   │ │ Service    │
   │ 8082     │ │ 8081      │ │ 8083       │
   └──────────┘ └───────────┘ └────────────┘
```

## 快速开始

### 启动依赖

- **MySQL**：创建数据库 `smartVillage`，执行各模块 SQL（位于 `service/src/main/resources/sql/`）
- **Redis**：默认 `localhost:6379`
- **RabbitMQ**（仅 media-service 需要）：默认 `localhost:5672`

### 微服务启动（推荐）

按依赖顺序启动各个服务，或分别打包运行：

```bash
cd SmartVillages-Backend

# 构建所有模块
./mvnw clean install -DskipTests

# 依次启动（每个服务一个终端）
java -jar gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar   # 网关 8080
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar         # 认证 8082
java -jar business-service/target/business-service-0.0.1-SNAPSHOT.jar # 业务 8081
java -jar media-service/target/media-service-0.0.1-SNAPSHOT.jar       # 媒体 8083
```

或者使用 Maven 插件逐个启动：

```bash
./mvnw -pl gateway-service -am spring-boot:run   # 先启动网关
./mvnw -pl auth-service -am spring-boot:run       # 再启动认证
# 其它服务同理
```

### 前端启动

```bash
cd SmartVillages-Web
npm install
npm run dev
```

构建：

```bash
npm run build
```

## 联调约定

- **网关统一入口**：`http://localhost:8080`（所有请求经 Gateway 转发）
- 请求头 **`token`**：携带 JWT 字符串
- 登录：`POST /auth/login`，Body JSON：`username`、`password`
- 接口文档（直连各微服务）：`http://<host>:<port>/doc.html`

## 各服务详细说明

- [后端 README](SmartVillages-Backend/README.md)
- [前端 README](SmartVillages-Web/README.md)
