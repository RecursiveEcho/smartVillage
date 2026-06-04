# SmartVillages Backend（面试官版）

> 目标：让面试官在 **1-2 分钟**内看懂"你做了什么、怎么跑、怎么验收、亮点在哪"。
> 复盘/脚手架流程请看 `开发手册.md`；详细权限边界见 `docs/PERMISSIONS.md`；演示脚本见 `docs/DEMO-SCRIPT.md`。

## 项目一句话

智慧乡村综合管理系统后端：基于 **Spring Boot 3 + Maven 多模块 + Spring Cloud Gateway**，拆分为 **认证服务、业务服务、媒体服务** 三个子进程，通过 **Gateway 统一入口**，实现 **JWT 鉴权 + RBAC 三角色权限**，落地公告、互动留言、村务台账、民生服务工单等典型乡村治理业务闭环。

## 架构演进：单体多模块 → 微服务

```
                ┌──────────────┐
                │  Gateway     │  端口 8080（统一入口）
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

各微服务共享同一 Maven 父工程，依赖业务 jar 模块（`auth`、`admin`、`announcement`、`interaction`、`management`、`media`、`feature`）和公共库（`common`、`common-lib`）编译。

## 亮点（面试可讲）

- **微服务拆分**：单体 → 认证/业务/媒体三服务 + Gateway 路由网关，模块边界清晰，可独立部署扩缩。
- **多模块复用**：业务 jar 库（`auth`/`admin`/`announcement` 等）+ `common` 公共库同时被多个微服务复用，避免拷贝代码。
- **权限模型清晰**：`ROLE_ADMIN / ROLE_CADRE / ROLE_VILLAGER` 三角色职责明确，配合 Gateway + 各服务 `SecurityConfig` 控制访问。
- **工程化规范**：统一返回 `Result`、统一业务异常 `BusinessException + GlobalExceptionHandler`、错误码枚举 `ErrorCode`。
- **数据一致性**：逻辑删除 + 创建/更新时间自动填充（MyBatis-Plus）。
- **性能意识**：Redis JSON 详情缓存（读缓存 + 更新/删除淘汰），适用于公告/台账详情等高频读接口。
- **消息队列**：media-service 集成 RabbitMQ，支持异步媒体处理。
- **配置安全**：运行配置"环境变量优先"，避免密码/AccessKey/密钥硬编码进仓库。

## 模块与微服务对应

### 微服务

| 服务 | 端口 | 职责 | 关键依赖 |
|------|------|------|----------|
| `gateway-service` | 8080 | Spring Cloud Gateway，统一入口与路由转发 | 无业务依赖 |
| `auth-service` | 8082 | 登录/登出、JWT 签发、管理员账号管理 | auth, admin, common |
| `business-service` | 8081 | 公告、留言、风采、村务台账等业务 | announcement, feature, interaction, management, media, common |
| `media-service` | 8083 | 文件上传、OSS 存储、媒体元数据管理（含 RabbitMQ） | media, common |

### 业务 jar 模块（被微服务引用）

- `auth`：登录/登出核心逻辑、密码校验、JWT 颁发
- `admin`：账号管理（用户分页、启用禁用、创建村干部账号）
- `announcement`：公告（公共端只读 + 村干部管理端）
- `interaction`：留言互动（村民提交 + 干部处理）
- `management`：村务治理台账（人口/房屋土地/党建组织/村务公示、民生服务工单等）
- `media`：文件上传与存储模型层
- `feature`：乡村风采内容（前台展示 + 干部维护）

### 公共库

- `common`：安全过滤器（JwtSecurityFilter）、统一返回（Result）、错误码（ErrorCode）、全局异常处理、工具类（JwtUtils、RedisJsonCacheTool）、通用配置（SecurityConfig、CORS、Knife4j）
- `common-lib`：纯 POJO/DTO，无 Spring 依赖，用于跨服务数据传输

## 权限边界（RBAC）

- **ROLE_ADMIN**（管理员）：只负责账号管理
- **ROLE_CADRE**（村干部）：负责业务执行（公告、留言处理、村务台账、工单处理等）
- **ROLE_VILLAGER**（村民）：前台浏览与反馈（提交留言/工单、查看自己的记录等）

详细 URL 规则见 `common/src/main/java/com/backend/common/config/SecurityConfig.java`，摘要见 `docs/PERMISSIONS.md`。

## 运行与配置

### 启动依赖

- MySQL：创建数据库 `smartVillage`
- Redis（建议启动，公告/台账详情缓存会用；若不启用可能报错）
- RabbitMQ（仅 media-service 需要，非必须时可不启动该服务）

### 构建与启动

```bash
cd SmartVillages-Backend

# 构建所有模块
./mvnw clean install -DskipTests

# 微服务模式（建议，依次启动各服务）
# 认证服务
./mvnw -pl auth-service -am spring-boot:run
# 业务服务（另一个终端）
./mvnw -pl business-service -am spring-boot:run
# 媒体服务
./mvnw -pl media-service -am spring-boot:run
# 网关
./mvnw -pl gateway-service -am spring-boot:run
```

或者打包后运行：

```bash
./mvnw clean package -DskipTests
java -jar gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar
java -jar business-service/target/business-service-0.0.1-SNAPSHOT.jar
java -jar media-service/target/media-service-0.0.1-SNAPSHOT.jar
```

> 也保留单进程启动模块（`service`），可直接 `./mvnw -pl service -am spring-boot:run` 启动所有功能于同一进程。

### 环境变量（推荐）

各微服务的 `application.yml` 中已配置默认值，生产环境建议覆盖：

- **MySQL**：`DB_URL`、`DB_USERNAME`、`DB_PASSWORD`
- **Redis**：`REDIS_HOST`、`REDIS_PORT`、`REDIS_PASSWORD`、`REDIS_DB`
- **JWT**：`JWT_SECRET`、`JWT_EXPIRE_MS`
- **阿里云 OSS（可选）**：`ALIYUN_OSS_ENDPOINT`、`ALIYUN_OSS_BUCKET`、`ALIYUN_OSS_ACCESS_KEY_ID`、`ALIYUN_OSS_ACCESS_KEY_SECRET`

### 初始化数据库

SQL 目录：`service/src/main/resources/sql/`（按模块拆分），建议至少执行：

```
auth.sql  admin.sql  announcement.sql  interaction.sql  management.sql
feature.sql  media.sql  add_indexes.sql
```

## 接口文档入口

- Knife4j：`http://localhost:<port>/doc.html`（直连各微服务端口）
- OpenAPI JSON：`http://localhost:<port>/v3/api-docs`
- **网关统一入口**：`http://localhost:8080` + 路由前缀

## 示例账号（本地联调）

初始化数据来源：`service/src/main/resources/sql/auth.sql`（统一明文密码 `123456`）

- 管理员：`admin`
- 村干部：`cadre_wang`
- 村民：`zhang_san`、`li_si`

## 最短验收（面试 2 分钟演示）

1. 启动网关 + 任一微服务
2. 打开 Knife4j：`/doc.html`
3. 登录：`POST /auth/login`（拿 token）
4. 带 token 调一个业务接口（例如公告列表、台账列表），展示权限边界与统一返回结构 `Result`

更多演示步骤见 `docs/DEMO-SCRIPT.md`。

## 目录结构

```
SmartVillages-Backend/
├── pom.xml                        # 父工程（packaging=pom），声明 modules & 依赖版本管理
├── common/                        # 公共模块：安全、异常、统一返回、工具类、通用配置
├── common-lib/                    # 纯 POJO/DTO，无 Spring 依赖
├── auth/                          # 认证业务 jar
├── admin/                         # 管理员业务 jar
├── announcement/                  # 公告业务 jar
├── feature/                       # 风采业务 jar
├── interaction/                   # 留言业务 jar
├── media/                         # 媒体业务 jar
├── management/                    # 村务管理业务 jar
├── auth-service/                  # 认证微服务（可独立部署）
├── business-service/              # 业务微服务（聚合 announcement/feature/interaction/management/media）
├── media-service/                 # 媒体微服务（含 RabbitMQ）
├── gateway-service/               # Spring Cloud Gateway 路由网关
├── service/                       # 遗留单进程启动模块
├── docs/                          # 文档（权限边界、演示脚本、面试总结、API 导出等）
└── scripts/                       # 辅助脚本（批量数据生成等）
```

## 统一返回与错误约定

| 能力 | 位置（`common`） | 说明 |
|------|------------------|------|
| **ErrorCode** | `enums/ErrorCode.java` | 按码段划分：`1xxx` 用户/认证，`2xxx` 权限，`3xxx` 文件/配置，`4xxx` 通用业务与村务域，`5xxx` 系统，`6xxx` 路由语义 |
| **Result** | `result/Result.java` | 统一 JSON 字段 **`code` / `message` / `data`**；成功 `code = 200` |
| **GlobalExceptionHandler** | `exception/GlobalExceptionHandler.java` | 处理 Spring MVC 异常并返回 `Result` |

## 已踩坑提醒

- `AuthServiceImpl` 中状态判断要用 `&&`，不要写成 `||`（否则会误判登录失败）。
- 多模块改动后若运行结果像旧代码，先执行 `./mvnw install -DskipTests`。
- **SecurityConfig 与业务路径**需同步演进：新增 Controller 前缀时记得更新 `requestMatchers`。
- 微服务间调用通过 `services.auth.url` 配置（见 `business-service/application.yml` 和 `media-service/application.yml`），而非 Feign，可根据需要升级。
