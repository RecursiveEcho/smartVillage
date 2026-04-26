# SmartVillages Backend（面试官版）

> 目标：让面试官在 **1-2 分钟**内看懂“你做了什么、怎么跑、怎么验收、亮点在哪”。  
> 如果你想复盘/照抄整个项目脚手架流程，请看：`开发手册.md`

## 项目一句话

智慧乡村综合管理系统后端：基于 **Spring Boot 3 + Maven 多模块**，实现 **Spring Security + JWT** 无状态鉴权与 **RBAC 三角色权限**，落地公告、互动留言、村务台账、民生服务工单等典型乡村治理业务闭环。

## 亮点（面试可讲）

- **多模块边界清晰**：业务域拆分（`auth/admin/announcement/interaction/management/media/feature`），`common` 承载基础设施，`service` 单进程聚合启动。
- **权限模型清晰**：`ROLE_ADMIN / ROLE_CADRE / ROLE_VILLAGER` 三角色职责边界明确，配合 `SecurityConfig` 的 URL 规则控制访问。
- **工程化规范**：统一返回 `Result`、统一业务异常 `BusinessException + GlobalExceptionHandler`、错误码枚举 `ErrorCode`。
- **数据一致性**：逻辑删除 + 创建/更新时间自动填充（MyBatis-Plus）。
- **性能意识**：Redis JSON 详情缓存（读缓存 + 更新/删除淘汰），适用于公告/台账详情等高频读接口。
- **配置安全**：运行配置“环境变量优先”，避免密码/AccessKey/密钥硬编码进仓库。

## 模块说明（你交付的能力范围）

- `auth`：登录/登出，签发 JWT
- `admin`：账号管理（用户分页、启用禁用、创建村干部账号）
- `announcement`：公告（公共端只读 + 村干部管理端）
- `interaction`：留言互动（村民提交 + 干部处理）
- `management`：村务治理台账（人口/房屋土地/党建组织/村务公示、民生服务工单等）
- `media`：文件上传与访问（对接 OSS 等）
- `feature`：乡村风采内容（前台展示 + 干部维护）

## 权限边界（RBAC）

- **ROLE_ADMIN**：只负责账号管理
- **ROLE_CADRE**：负责业务执行（公告、留言处理、村务台账、工单处理等）
- **ROLE_VILLAGER**：前台浏览与反馈（提交留言/工单、查看自己的记录等）

最终以 `common/src/main/java/com/backend/common/config/SecurityConfig.java` 为准。

## 运行与配置

### 1）构建与启动

```bash
./mvnw -pl service -am clean package
./mvnw -pl service -am spring-boot:run
```

### 2）环境变量（推荐）

- **MySQL**：`DB_URL`、`DB_USERNAME`、`DB_PASSWORD`
- **Redis**：`REDIS_HOST`、`REDIS_PORT`、`REDIS_PASSWORD`、`REDIS_DB`
- **JWT**：`JWT_SECRET`、`JWT_EXPIRE_MS`
- **阿里云 OSS（可选）**：`ALIYUN_OSS_ENDPOINT`、`ALIYUN_OSS_BUCKET`、`ALIYUN_OSS_ACCESS_KEY_ID`、`ALIYUN_OSS_ACCESS_KEY_SECRET`

示例模板：`service/src/main/resources/application-example.yml`

### 3）初始化数据库

SQL 目录：`service/src/main/resources/sql/`（按模块拆分）。  
建议至少执行：`auth.sql`、`admin.sql`、`announcement.sql`、`interaction.sql`、`management.sql`（以及你需要的其它模块 SQL）。

## 接口文档入口

- Knife4j：`http://localhost:8080/doc.html`
- OpenAPI：`http://localhost:8080/v3/api-docs`

## 示例账号（本地联调）

来源：`service/src/main/resources/sql/auth.sql`（统一明文密码 `123456`）

- 管理员：`admin`
- 村干部：`cadre_wang`
- 村民：`zhang_san`、`li_si`

## 最短验收（面试 2 分钟演示）

1. 打开 Knife4j：`/doc.html`（证明接口文档自解释）
2. 登录：`POST /auth/login`（拿 token）
3. 带 token 调一个业务接口（例如村干部管理端分页、公示管理、台账列表等），展示权限边界与返回结构 `Result`

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

### 配置与环境变量（重要）

后端运行配置位于 `service/src/main/resources/application.yml`，但已改为 **环境变量优先**，避免敏感信息写死在仓库里。

可以直接在启动前设置这些环境变量（未设置时会使用 `application.yml` 里的默认值或空值）：

- **MySQL**
  - `DB_URL`：JDBC URL（默认 `jdbc:mysql://127.0.0.1:3306/smartVillage?...`）
  - `DB_USERNAME`：用户名（默认 `root`）
  - `DB_PASSWORD`：密码（默认空）
- **Redis**
  - `REDIS_HOST`（默认 `localhost`）
  - `REDIS_PORT`（默认 `6379`）
  - `REDIS_PASSWORD`（默认空）
  - `REDIS_DB`（默认 `0`）
- **JWT**
  - `JWT_SECRET`（默认 `smartVillages`，生产务必替换）
  - `JWT_EXPIRE_MS`（默认 `86400000`，24h）
- **阿里云 OSS（可选，只有 media 上传相关才需要）**
  - `ALIYUN_OSS_ENDPOINT`
  - `ALIYUN_OSS_BUCKET`
  - `ALIYUN_OSS_ACCESS_KEY_ID`
  - `ALIYUN_OSS_ACCESS_KEY_SECRET`

示例配置文件（仅供参考，不参与运行）：`service/src/main/resources/application-example.yml`

SQL 初始化脚本：`service/src/main/resources/sql/`（按模块拆分）

### 启动前置条件

- MySQL：创建数据库 `smartVillage`（或你自定义并通过 `DB_URL` 指定），执行 `service/src/main/resources/sql/` 下的建表与初始化脚本
- Redis：建议启动（公告详情、台账详情等会用到缓存）；若不启用，涉及缓存的功能可能运行报错

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

- Knife4j：`http://localhost:8080/doc.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

> 端口以实际启动日志为准（默认通常是 8080）。

## 示例账号（本地联调）

初始化数据来源：`service/src/main/resources/sql/auth.sql`

- **管理员（ROLE_ADMIN）**
  - username：`admin`
  - password：`123456`
- **村干部（ROLE_CADRE）**
  - username：`cadre_wang`
  - password：`123456`
- **村民（ROLE_VILLAGER）**
  - username：`zhang_san` / `li_si`
  - password：`123456`

## 联调示例（curl）

登录拿 token：

```bash
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

之后访问需登录接口时，在请求头带上 `token`：

```bash
curl "http://localhost:8080/cadre/village-affairs?current=1&size=10" \
  -H "token: <JWT_TOKEN>"
```

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

## 模块职责说明
- `auth`
  - 做登录/登出、密码校验、JWT 颁发与续期策略。
  - 维护账号基础能力（改密、找回密码、登录风控）。
  - 认证相关功能统一归口到该模块。

- `admin`
  - 做后台管理能力：用户分页、账号状态管理、角色分配、后台个人信息。
  - 承接“管理员操作别人数据”的用例（例如创建村干部账号）。
  - 管理台权限菜单、操作审计等能力在该模块扩展。

- `announcement`
  - 做公告全链路：发布、编辑、上下架、审核、前台查询、热门列表。
  - 维护公告业务规则（发布时间、置顶、状态流转）与公告相关缓存。
  - 公告分类、附件、搜索排序等能力在该模块演进。

- `interaction`
  - 做村民互动链路：留言、回复、处理状态、可见性控制。
  - 目前已有留言创建入口，后续可补列表、回复、管理员处理等接口。
  - 村民与干部互动相关功能统一归口到该模块。

- `management`
  - 做村务治理数据域：党务、人口、土地、事项公示等台账能力。
  - 该包现在偏骨架状态，优先补 `@RequestMapping`、分页查询、增删改查和统计接口。
  - 村务管理后台核心功能在该模块实现。

- `media`
  - 做媒体资源域：图片/视频文件上传、存储、访问、元数据管理。
  - 该包目前是占位，建议先补上传接口、文件类型校验、访问 URL 生成。
  - 对接 OSS/MinIO/CDN 等能力在该模块落地。

- `feature`
  - 做乡村风采展示域：风采内容发布、列表、详情、推荐位管理。
  - 当前是占位，建议先定义“风采内容模型 + 前后台接口”。
  - 首页展示、专题内容等内容运营能力在该模块实现。

- `common`
  - 做全项目复用底座：安全过滤器、统一返回、错误码、全局异常、工具类、通用配置。
  - 任何跨业务域会重复出现的能力，优先下沉到这里。
  - 注意只放“通用能力”，不要把某个业务包的私有逻辑塞进来。

- `service`
  - 做应用启动与装配：启动类、配置文件、SQL 初始化资源。
  - 负责把各模块聚合成单进程服务，不承载具体业务规则。
  - 环境配置、多 profile、部署参数在该模块维护。

### 模块速览

- `auth`：让用户登录系统、拿到身份凭证（JWT），并处理退出登录。
- `admin`：给管理员做用户与后台管理能力。
- `announcement`：做公告的发、改、审、上架和查询。
- `interaction`：做村民与干部的留言互动。
- `management`：做村务核心台账与治理数据管理。
- `media`：做图片视频等文件上传、存储、访问。
- `feature`：做乡村风采等展示型内容管理。
- `common`：放全项目通用能力（鉴权、异常、统一返回、工具类）。
- `service`：负责应用启动、配置装配和模块聚合。
