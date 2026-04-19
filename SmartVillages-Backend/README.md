# SmartVillages Backend（后端）

本目录为后端工程（Spring Boot + Maven 多模块）。仓库根目录 `README.md` 只保留整体使用方式；本文件承载后端的详细说明。

## 工程概览

根工程 Maven 坐标为 `com.backend:smartVillages`，采用 **多模块 + 单 Spring Boot 进程**（`service` 模块打包可执行 jar）。  
各业务 jar **只依赖 `common`**；`common` 聚合 **Spring Web、Spring Security、MyBatis-Plus、Redis、Knife4j(OpenAPI3)、JJWT** 等（详见 `common/pom.xml` 与源码）。根包名为 **`com.backend.*`**。

> **与代码严格对齐**：下列「接口摘要」「鉴权」与 **`SecurityConfig`** 一致；各子模块中若仍有 **空类、TODO**，表示尚未补全，本文**不将其描述为已交付功能**。

## 快速开始

构建：

```bash
./mvnw -pl service -am clean package
```

运行（开发）：

```bash
./mvnw -pl service -am spring-boot:run
```

配置入口：

- `service/src/main/resources/application.yml`（当前示例：**MySQL** 库名 `smartVillage` / `root` / `1234`；**Redis** `localhost:6379` / 密码 `123456`；**JWT** `jwt.secret` / `jwt.expiration-ms`）
- `service/src/main/resources/sql/`（DDL/初始化脚本，按模块拆分）

本地启动前请确保 **MySQL 与 Redis** 可用且与配置一致；否则公告缓存等依赖 Redis 的功能可能在启动或运行时失败。

## 统一返回与错误约定

| 能力 | 位置（`common`） | 说明 |
|------|------------------|------|
| **ErrorCode** | `enums/ErrorCode.java` | 按码段划分：`1xxx` 用户/认证，`2xxx` 权限，`3xxx` 文件/配置，`4xxx` 通用业务与村务域（`41xx` 等），`5xxx` 系统，`6xxx` 路由语义；**同一语义对应唯一数字**，新增时在对应段内递增。 |
| **Result** | `result/Result.java` | 统一 JSON 字段 **`code` / `message` / `data`**；成功常用 **`code = 200`**（见类常量 `SUCCESS_CODE`）。业务失败可在 Controller 中 `Result.fail(...)` / `Result.error(...)`，或由 **`BusinessException`** 进入全局处理器后返回。 |
| **GlobalExceptionHandler** | `exception/GlobalExceptionHandler.java` | 处理 **已进入 Spring MVC** 的异常并返回 `Result`：`BusinessException`、参数校验、404/405 等。Spring Security 在过滤器链上产生的 **401/403** 多为框架默认响应，**不一定**为 `Result` JSON。 |

## 登录与鉴权（当前实现）

### 接口（auth / admin，节选）

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/auth/login` | 登录，Body：`application/json`（`username`、`password`），成功时 `data` 含 **`token`**（见 `JwtResponse`） |
| `DELETE` | `/auth/logout` | 登出 v1（无状态：服务端不落会话；成功返回统一文案，前端清理本地 `token`） |
| `GET` | `/admin/me` | 当前登录主体信息（需 **管理员** 权限） |
| `GET` | `/admin/users` | 分页查询用户（管理员）；支持 `username`、`role`、`status`、`current`、`size` 等查询参数 |
| `PUT` | `/admin/users/{id}/status` | 启用/禁用用户（管理员） |
| `POST` | `/admin/cadre` | 创建村干部账号（管理员）；Body 见 `AuthDTO`（校验注解以类为准） |

请求头：**`token`**（JWT 字符串）。JWT 的 `subject` 为 **`id:username:role`**。

### JwtSecurityFilter + Spring Security

1. **`JwtSecurityFilter`**（`common/filter/JwtSecurityFilter.java`）：若请求携带 **`token`** 且解析成功，则：  
   - 向 `HttpServletRequest` 写入 **`authId` / `username` / `role`**（供 **`LoginUserContext`** 等使用）；  
   - 向 **`SecurityContextHolder`** 写入 **`UsernamePasswordAuthenticationToken`**，`GrantedAuthority` 为 **`ROLE_` + 角色大写**（若 subject 中角色已带 `ROLE_` 前缀会规范化）。  
   若 token 无效或解析失败，会 **清除 `SecurityContext`**，后续由 Spring Security 判定是否允许访问。
2. **`SecurityConfig`**：通过 **`authorizeHttpRequests`** 配置 **匿名可访问路径** 与 **需认证/需特定角色** 的路径（源码为准，下表为便于阅读的摘要）。

### authorizeHttpRequests 摘要（以 `SecurityConfig` 为准）

| 规则 | 说明 |
|------|------|
| `permitAll` | 含 `/auth/login`、OpenAPI 与 Swagger 相关路径、`/announcements/**`、`/interactions/**`、预留的 `/guest/**` 等 |
| `hasAuthority("ROLE_ADMIN")` | `/admin/users/**`、`/admin/me` |
| `hasAuthority("ROLE_CADRE")` | `/cadre/announcements/**`、`/cadre/interactions/**` |
| `hasAnyAuthority("ROLE_VILLAGER")` | `/villager/**`（预留路径，若暂无 Controller 则仅占位） |
| `anyRequest().authenticated()` | 其余请求默认需登录（带有效 `token`） |

**说明**：`InteractionController` 当前映射为 **`/interactions/**`**，且位于 `permitAll` 的 `/interactions/**` 下；若业务要求「留言必须登录」，需后续收紧规则并在服务层校验 `LoginUserContext.getAuthId(request)` 等非空语义（以产品决策为准）。

### JWT 配置

`service/src/main/resources/application.yml`：**`jwt.secret`**、**`jwt.expiration-ms`**（示例为 24 小时）。生产环境请改用环境变量或独立 profile，勿提交真实密钥。

### 接口文档

- Knife4j：**`/doc.html`**
- OpenAPI JSON：**`/v3/api-docs`**（或 `/**` 子路径，以 springdoc 实际暴露为准）

## 业务模块接口线索（公告 / 留言）

- **公告**：实现类集中在 **`AnnouncementController`** — 前台只读 **`GET /announcements`**、**`GET /announcements/hot`**、**`GET /announcements/{id}`**；村干部侧 **`/cadre/announcements` 系列**（增删改查、审核、分页等）。公告详情等使用 **Redis** 缓存（见 `AnnouncementServiceImpl`）。
- **留言**：**`POST /interactions/messages`**（`InteractionController`），服务内使用 **`LoginUserContext.getAuthId(request)`** 写入用户关联字段。

## 当前开发进度（阶段）

- 已完成（主干）：登录颁发 JWT、登出接口；**Spring Security** 与 URL 级角色控制；管理员 **`/admin/me`、用户分页、状态变更、创建村干部**；公告模块前后台接口与 Redis 详情缓存；留言创建接口等。
- 文档：README / 开发手册与当前 `SecurityConfig`、`JwtSecurityFilter`、主要 Controller 行为对齐；字段级细节以 OpenAPI 与源码为准。

### 下一步建议

1. 按产品需求收紧 **`/interactions/**`** 等匿名接口的权限边界，并补集成测试。
2. 补齐 **`/villager/**`**、**`/guest/**`** 与 **management / media / feature** 等模块的路由与文档。
3. 配置外置化（JWT、数据源、Redis）与多环境 profile。

### 已踩坑提醒

- `AuthServiceImpl` 中状态判断要用 `&&`，不要写成 `||`（否则会误判登录失败）。
- 多模块改动后若运行结果像旧代码，先在后端根目录执行 `./mvnw install -DskipTests`。
- **`SecurityConfig` 与业务路径** 需同步演进：新增 Controller 前缀时记得更新 `requestMatchers`，避免出现「能登录但接口 403/401」的错位。

## 目录与模块（详细）

```text
SmartVillages-Backend/
├── pom.xml                       # 父工程（packaging=pom），声明 modules & 依赖版本管理（Spring Boot 3.3.x）
├── common/                       # 公共模块（无启动类）：横切能力与基础设施
│   └── src/main/java/com/backend/common/
│       ├── config/               # SecurityConfig、OpenAPI/CORS/MyBatisPlus 等（以源码为准）
│       ├── enums/                # ErrorCode
│       ├── exception/            # BusinessException + GlobalExceptionHandler
│       ├── filter/               # JwtSecurityFilter：解析 token，写入 SecurityContext 与 request attribute
│       ├── context/              # LoginUserContext：从 request 读取当前登录用户
│       ├── result/               # Result
│       └── utils/                # JwtUtils、RedisJsonCacheTool 等
├── auth/                         # 认证域
├── admin/                        # 管理员域
├── announcement/                 # 公告域
├── feature/                      # 风采域
├── interaction/                  # 留言域
├── media/                        # 媒体域
├── management/                   # 村务管理域
└── service/                      # 启动模块：聚合所有模块（单进程启动）
    ├── pom.xml
    └── src/main/
        ├── java/com/backend/SmartVillagesApplication.java
        └── resources/
            ├── application.yml
            └── sql/
```
