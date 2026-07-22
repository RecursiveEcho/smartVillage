# SmartVillages Backend

智慧乡村综合管理系统后端。  
基于 **Spring Boot 3 + Maven 多模块 + 微服务架构**，通过 **Gateway 统一入口 + Nacos 注册发现**，实现登录鉴权、公告/留言/村务业务、媒体上传审核与异步绑定。

- 开发说明：[`开发手册.md`](开发手册.md)
- 启动与排错：[`docs/backend-runbook.md`](docs/backend-runbook.md)
- 仓库总览：[`../README.md`](../README.md)

## 项目一句话

这是一个面向村务治理场景的后端系统：  
用 Gateway 做统一入口，用 JWT + Spring Security 做无状态鉴权，用 Redis 做缓存和登录限流，用 OSS + RabbitMQ 做媒体上传后的异步业务绑定。

## 架构概览

```text
                ┌────────────────┐
                │ Gateway 8090   │  统一入口
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

### 服务与端口

| 服务 | 端口 | 职责 |
| --- | --- | --- |
| `gateway-service` | 8090 | 路由转发、统一入口 |
| `business-service` | 8081 | 公告、留言、风采、村务等业务 |
| `auth-service` | 8082 | 登录、JWT、用户相关能力 |
| `media-service` | 8083 | 媒体上传、审核、MQ 发送 |
| `admin-service` | 8084 | 管理端能力，Feign 调用 auth-service |

## 核心亮点

### 1. Redis 缓存 + 登录限流

- 高频读接口使用 Redis 做详情/列表缓存
- 更新、删除、审核后做缓存失效
- 登录按 `IP + username` 限流，超限直接拒绝，降低撞库与无效查库

### 2. Spring Security + JWT

- 登录成功后签发 JWT
- `JwtSecurityFilter` 解析 token，写入 `SecurityContext`
- `SecurityConfig` 按 admin / cadre / villager 控制接口访问边界

### 3. OSS + RabbitMQ 异步绑定

- 媒体上传后先保存 OSS 地址和媒体表记录
- 审核通过且绑定信息完整时发送 RabbitMQ 消息
- 业务服务消费消息后，按 `bindTarget` 找到绑定器完成资源绑定
- 明确业务失败可进入死信队列，避免无限重试

### 4. 微服务与部署联调

- Gateway 使用 `lb://` 基于 Nacos 做服务发现与转发
- 前端统一走 `8090`，不再直连业务端口
- 真实排过错：Nacos 未启动、路由前缀不一致、环境变量缺失、数据库表结构不匹配等

## 模块说明

### 微服务

| 服务 | 关键依赖 |
| --- | --- |
| `auth-service` | `auth`、`common` |
| `business-service` | `announcement`、`feature`、`interaction`、`management`、`media`、`common` |
| `media-service` | `media`、`common` |
| `admin-service` | `admin`、`common` |
| `gateway-service` | 路由配置 + Nacos 发现 |

### 业务 jar 模块

- `auth`：登录、密码校验、JWT 颁发
- `admin`：账号管理
- `announcement`：公告
- `interaction`：留言互动
- `management`：村务台账 / 工单
- `media`：媒体模型与业务
- `feature`：乡村风采

### 公共库

- `common`：`JwtSecurityFilter`、`SecurityConfig`、统一返回 `Result`、全局异常处理、Redis/JWT 工具
- `common-lib`：跨服务 DTO/事件对象

## 权限模型

- `ROLE_ADMIN`：账号管理
- `ROLE_CADRE`：业务执行（公告、留言处理、村务等）
- `ROLE_VILLAGER`：前台浏览与反馈

详细规则见：

```text
common/src/main/java/com/backend/common/config/SecurityConfig.java
```

## 启动依赖

启动前准备：

- MySQL（库名建议：`smartVillage`）
- Redis
- RabbitMQ（媒体异步绑定链路需要）
- Nacos（`8848` + `9848`）
- `.env` 或环境变量

配置模板：

```text
env.example
```

初始化 SQL：

```text
service/src/main/resources/sql/
```

## 构建与启动

```bash
cd SmartVillages-Backend
./mvnw clean install -DskipTests
```

推荐启动顺序：

```bash
# 1. 先起业务服务
./mvnw -pl auth-service spring-boot:run
./mvnw -pl business-service spring-boot:run
./mvnw -pl media-service spring-boot:run
./mvnw -pl admin-service spring-boot:run

# 2. 最后起网关
./mvnw -pl gateway-service spring-boot:run
```

也可以打包后运行 jar：

```bash
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar
java -jar business-service/target/business-service-0.0.1-SNAPSHOT.jar
java -jar media-service/target/media-service-0.0.1-SNAPSHOT.jar
java -jar admin-service/target/admin-service-0.0.1-SNAPSHOT.jar
java -jar gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar
```

## 网关与联调

统一入口：

```text
http://127.0.0.1:8090
```

主要路由：

| 前缀 | 目标 |
| --- | --- |
| `/auth/**` | `auth-service` |
| `/admin/**` | `admin-service` |
| `/media/**` | `media-service` |
| `/announcements/**` `/interactions/**` `/cadre/**` `/villager/**` `/public/**` `/features/**` | `business-service` |
| `/api/**` | `business-service`（`StripPrefix=1`） |

登录示例：

```bash
curl -X POST http://127.0.0.1:8090/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

后续请求头携带：

```text
token: <jwt>
```

接口文档（直连服务时）：

```text
http://<host>:<port>/doc.html
```

## 统一返回约定

| 能力 | 位置 | 说明 |
| --- | --- | --- |
| `ErrorCode` | `common/enums/ErrorCode.java` | 业务错误码 |
| `Result` | `common/result/Result.java` | 统一返回 `code / message / data` |
| `GlobalExceptionHandler` | `common/exception/GlobalExceptionHandler.java` | 全局异常处理 |

## 排查顺序

1. MySQL / Redis / RabbitMQ / Nacos 是否启动
2. 服务是否注册到 Nacos
3. Gateway `8090` 是否可用
4. 前端是否统一走网关
5. 看对应服务日志：登录、权限、媒体审核、MQ 消费

## 常见问题

- 改了代码但行为像旧版本：先执行 `./mvnw clean install -DskipTests`
- 新增接口 403：检查 `SecurityConfig` 是否补了路径和角色规则
- Nacos 能访问但服务注册失败：同时检查 `8848` 和 `9848`
- 登录限流异常：确认 Redis 可用
- 媒体审核后业务未绑定：检查 RabbitMQ 与 consumer 日志

## 目录结构

```text
SmartVillages-Backend/
├── pom.xml
├── common/ / common-lib/
├── auth/ admin/ announcement/ feature/ interaction/ media/ management/
├── auth-service/
├── business-service/
├── media-service/
├── admin-service/
├── gateway-service/
├── service/                 # 遗留单进程启动模块 + SQL
├── docs/backend-runbook.md
└── scripts/
```
