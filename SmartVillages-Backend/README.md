# SmartVillages Backend（后端）

本目录为后端工程（Spring Boot + Maven 多模块）。仓库根目录 `README.md` 只保留整体使用方式；本文件承载后端的详细说明。

## 工程概览

根工程 Maven 坐标为 `com.backend:smartVillages`，采用 **多模块 + 单 Spring Boot 进程**（`service` 模块打包可执行 jar）。  
各业务 jar **只依赖 `common`**；`common` 通过依赖引入 **Spring Web、MyBatis-Plus、Knife4j(OpenAPI3)、JJWT**，并提供 **`ErrorCode`、`Result`、`GlobalExceptionHandler`、`JwtUtils`、`JwtAuthenticationFilter`** 等（详见 `common/pom.xml` 与源码）。根包名为 **`com.backend.*`**。

> **与代码严格对齐**：下列各模块的「核心功能」多为**领域与表结构已规划**的能力说明；**是否已在对应 `controller`/`service` 中实现，以仓库源文件为准**。`common` 与子模块中存在的 **空类、TODO** 表示尚未补全，本文**不将其描述为已交付功能**。

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
- `service/src/main/resources/application.yml`（当前为本机 MySQL 示例：`smart_villages` / `root` / `1234`）
- `service/src/main/resources/sql/`（DDL/初始化脚本，按模块拆分）

## 统一返回与错误约定

| 能力 | 位置（`common`） | 说明 |
|------|------------------|------|
| **ErrorCode** | `enums/ErrorCode.java` | 按码段划分：`1xxx` 用户/认证，`2xxx` 权限，`3xxx` 文件/配置，`4xxx` 通用业务与村务域（`41xx` 等），`5xxx` 系统，`6xxx` 路由语义；**同一语义对应唯一数字**，新增时在对应段内递增。 |
| **Result** | `result/Result.java` | 统一 JSON 字段 **`code` / `message` / `data`**；成功常用 **`code = 200`**（见类常量 `SUCCESS_CODE`）。业务失败可在 Controller 中 `Result.fail(...)` / `Result.error(...)`，或由 **`BusinessException`** 进入全局处理器后返回（JWT 过滤器产生的 **401 不在此结构内**）。 |
| **GlobalExceptionHandler** | `exception/GlobalExceptionHandler.java` | 处理 **已进入 Spring MVC** 的异常并返回 `Result`：`BusinessException`、参数校验、404/405 等；`NO_PERMISSION` 映射 **HTTP 403** 与业务码 **2001**。JWT 在过滤器阶段失败仍为 **401** 且无 `Result` 体（见下）。 |

## 登录与鉴权（当前实现）

### 接口（auth / admin）

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/auth/login` | 登录，Body：`application/json`（`username`、`password`），成功返回 `data.token` 等 |
| `DELETE` | `/auth/logout` | 登出 v1（无状态：服务端不落会话；需带 `token`，成功后由前端清除本地 token） |
| `GET` | `/admin/me` | 当前用户（需 `token`；`/admin/**` 仅 `role=admin` 可访问） |

请求头：**`token`**（JWT 字符串）。JWT 的 `subject` 为 **`id:username:role`**，拦截器解析后写入 `request` 属性：`authId`、`username`、`role`；业务侧可用 `LoginUserContext`（`common/context`）读取。

### 两层校验（与代码一致）

1. **`JwtAuthenticationFilter`**（`common/filter`）：除白名单外，要求带 **`token`** 且能解析；否则 **HTTP 401**，通常无 `Result` JSON。  
   **白名单**（含）：`/auth/login`、`/doc.html`、`/v3/api-docs`、`/swagger-ui` 与 `/swagger-ui/**`、`/webjars` 与 `/webjars/**`、`OPTIONS`。
2. **`AuthInterceptor`**（`common/interceptor`）：同样白名单 + 解析 `subject` 为三段；路径以 **`/admin`** 开头时，若 `role` 不是 **`admin`**，抛 `BusinessException(NO_PERMISSION)` → **HTTP 403**，`code=2001`。

### JWT 配置

`service/src/main/resources/application.yml` 中：**`jwt.secret`**、**`jwt.expiration-ms`**（示例为 2 小时）。生产环境请改用环境变量或独立 profile，勿提交真实密钥。

### 接口文档

- Knife4j：**`/doc.html`**  
- OpenAPI JSON：**`/v3/api-docs`**  
（须在过滤器白名单内，否则未带 token 访问文档会 **401**。）

## 当前开发进度（阶段）

- 已完成：`login/logout/me` 最小闭环（JWT、401/403 语义、`/admin/**` 角色控制）。
- 文档状态：README 与执行手册已按当前实现对齐。
- 当前进行中：管理员分页查询用户（基于 `auth` 表，返回 VO，不返回密码）。

### 下一步建议（后端）

1. 完成 `GET /admin/users` 分页接口（先做分页，再加筛选）。
2. 增加筛选参数（用户名、角色、状态）与排序。
3. 增加状态变更接口（启用/禁用），并补接口验收清单。

### 已踩坑提醒

- `AuthServiceImpl` 中状态判断要用 `&&`，不要写成 `||`（否则会误判登录失败）。
- 多模块改动后若运行结果像旧代码，先在后端根目录执行 `./mvnw install -DskipTests`。
- Filter/Interceptor 白名单要保持一致，否则容易出现文档页或登录被误拦截。

## 目录与模块（详细）

```text
SmartVillages-Backend/
├── pom.xml                       # 父工程（packaging=pom），声明 modules & 依赖版本管理
├── common/                       # 公共模块（无启动类）：横切能力与基础设施
│   └── src/main/java/com/backend/common/
│       ├── config/               # 统一配置（如 OpenAPI/CORS/MyBatisPlus 等，是否存在以源码为准）
│       ├── enums/                # ErrorCode：统一错误码
│       ├── exception/            # BusinessException + GlobalExceptionHandler
│       ├── filter/               # JwtAuthenticationFilter：除白名单外校验 Header token
│       ├── interceptor/          # AuthInterceptor：token 解析、/admin 角色校验
│       ├── context/              # LoginUserContext：从 request 读取当前登录用户
│       ├── result/               # Result：统一返回结构
│       └── utils/                # JwtUtils 等工具
├── auth/                         # 认证域（包：com.backend.auth）
├── admin/                        # 后台用户域（包：com.backend.admin）
├── announcement/                 # 公告域（包：com.backend.announcement）
├── feature/                      # 风采域（包：com.backend.feature）
├── interaction/                  # 留言域（包：com.backend.interaction）
├── media/                        # 媒体域（包：com.backend.media）
├── management/                   # 村务管理域（包：com.backend.management）
└── service/                      # 启动模块：聚合所有模块（单进程启动）
    ├── pom.xml
    └── src/main/
        ├── java/com/backend/SmartVillagesApplication.java
        └── resources/
            ├── application.yml   # 唯一运行时配置（数据源/MP/Knife4j 等）
            └── sql/              # DDL/初始化脚本（按模块拆分）
```

