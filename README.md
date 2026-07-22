# SmartVillages — 智慧乡村综合管理系统

面向村务治理场景的全栈项目：后端基于 **Spring Boot 3 + Maven 多模块 + 微服务**，前端基于 **Vue 3 + Vite**。  
统一入口为 **Spring Cloud Gateway（8090）**，覆盖登录鉴权、公告、留言、村务、媒体上传审核等核心业务。

## 项目亮点

- **微服务拆分**：`auth / business / media / admin + gateway`，通过 Nacos 注册发现与 Gateway 路由转发，消除硬编码服务地址。
- **Redis 缓存 + 登录限流**：高频读场景做缓存；登录按 `IP + username` 限流，降低无效请求和数据库压力。
- **Spring Security + JWT**：无状态鉴权，`JwtSecurityFilter` 解析 token 并写入 `SecurityContext`，按 admin / cadre / villager 控制接口边界。
- **OSS + RabbitMQ 异步绑定**：媒体上传后先落库，审核通过后再异步通知业务模块完成资源绑定，降低媒体服务与业务服务耦合；业务失败可进入死信队列。
- **部署联调经验**：处理过 Nginx 反向代理、CORS、Nacos 注册、网关转发、数据库表结构不一致等问题。

## 架构概览

```text
                ┌────────────────┐
                │  Gateway 8090  │  前端统一入口
                └───────┬────────┘
         ┌──────────────┼────────────────┐
         ▼              ▼                ▼
   ┌──────────┐  ┌────────────┐  ┌────────────┐
   │ Auth     │  │ Business   │  │ Media      │
   │ 8082     │  │ 8081       │  │ 8083       │
   └──────────┘  └────────────┘  └────────────┘
         ▲
         │ Feign
   ┌──────────┐
   │ Admin    │  8084
   └──────────┘
```

### 服务端口

| 服务 | 端口 | 职责 |
| --- | --- | --- |
| `gateway-service` | 8090 | 统一入口与路由转发 |
| `business-service` | 8081 | 公告、留言、风采、村务等业务 |
| `auth-service` | 8082 | 登录、JWT、用户相关能力 |
| `media-service` | 8083 | 媒体上传、审核、MQ 发送 |
| `admin-service` | 8084 | 管理端能力，Feign 调用 auth-service |
| `SmartVillages-Web` | 5173 | 前端开发服务 |

## 技术栈

- **后端**：Java、Spring Boot 3、Spring Security、JWT、MyBatis-Plus、MySQL、Redis、RabbitMQ、Spring Cloud Gateway、Nacos、OpenFeign、阿里云 OSS
- **前端**：Vue 3、Vite
- **工程**：Maven 多模块、统一异常与返回体、环境变量优先配置

## 目录结构

```text
SmartVillages/
├── SmartVillages-Backend/     # 后端微服务与业务模块
│   ├── gateway-service/
│   ├── auth-service/
│   ├── business-service/
│   ├── media-service/
│   ├── admin-service/
│   ├── common/ / common-lib/
│   ├── auth/ admin/ announcement/ feature/ interaction/ media/ management/
│   └── docs/backend-runbook.md
├── SmartVillages-Web/         # 前端 Vue 3 + Vite
└── README.md
```

## 快速开始

### 1. 准备依赖

启动前确认以下服务可用：

- MySQL（库名建议：`smartVillage`）
- Redis
- RabbitMQ（media 链路需要）
- Nacos（`8848` + `9848`）

可选检查：

```bash
lsof -nP -iTCP:3306 -sTCP:LISTEN
lsof -nP -iTCP:6379 -sTCP:LISTEN
lsof -nP -iTCP:5672 -sTCP:LISTEN
lsof -nP -iTCP:8848 -sTCP:LISTEN
lsof -nP -iTCP:9848 -sTCP:LISTEN
```

初始化 SQL 位于：

```text
SmartVillages-Backend/service/src/main/resources/sql/
```

后端环境变量示例见：

```text
SmartVillages-Backend/env.example
```

### 2. 启动后端

```bash
cd SmartVillages-Backend
./mvnw clean install -DskipTests
```

推荐启动顺序：

```bash
# 1) 先起业务服务
./mvnw -pl auth-service spring-boot:run
./mvnw -pl business-service spring-boot:run
./mvnw -pl media-service spring-boot:run
./mvnw -pl admin-service spring-boot:run

# 2) 最后起网关
./mvnw -pl gateway-service spring-boot:run
```

### 3. 启动前端

```bash
cd SmartVillages-Web
npm install
npm run dev
```

## 联调入口

- **前端页面**：`http://127.0.0.1:5173`
- **后端统一入口**：`http://127.0.0.1:8090`
- **登录接口**：`POST /auth/login`  
  Body 示例：

```json
{
  "username": "admin",
  "password": "123456"
}
```

- 请求头使用 `token` 携带 JWT
- 前端开发/生产默认都走 Gateway，不要直连 `8081/8082/8083/8084`

### 主要网关路由

| 前缀 | 目标服务 |
| --- | --- |
| `/auth/**` | `auth-service` |
| `/admin/**` | `admin-service` |
| `/media/**` | `media-service` |
| `/announcements/**` 等业务前缀 | `business-service` |
| `/api/**` | `business-service`（`StripPrefix=1`） |

## 排查顺序

页面不通时，按这个顺序查：

1. MySQL / Redis / RabbitMQ / Nacos 是否都起来
2. 各服务是否注册到 Nacos
3. Gateway `8090` 是否可用
4. 前端请求是否都走网关，而不是直连业务端口
5. 看对应服务日志（登录、权限、媒体审核、MQ 消费）

更完整的启动与排错说明：

- [后端运行手册](SmartVillages-Backend/docs/backend-runbook.md)
- [后端 README](SmartVillages-Backend/README.md)
- [前端 README](SmartVillages-Web/README.md)

## 当前状态

已完成：

- 微服务拆分与网关统一入口
- JWT 鉴权与三角色权限控制
- Redis 缓存与登录限流
- 媒体上传审核与 RabbitMQ 异步绑定
- 基础部署联调与问题排查

适合继续推进：

- 关键页面端到端验收
- 业务链路联调与文档完善
