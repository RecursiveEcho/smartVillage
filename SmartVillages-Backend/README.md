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
| **GlobalExceptionHandler** | `exception/GlobalExceptionHandler.java` | 处理 **已进入 Spring MVC** 的异常并返回 `Result`：`BusinessException`、参数校验、404/405 等；兜底 `Exception`。**不含** JWT 解析失败（见过滤器）。 |

## JWT 与接口文档（简要）

- **鉴权**（实现见 `JwtAuthenticationFilter`）：仅当 **`HttpServletRequest.getRequestURI()`**（不含 query）、在去除末尾 `/` 后规范化，**等于**或在 **`/api/user`、`/api/cadre`、`/api/admin`** 之下的子路径时，才校验请求头 **`token`**；路径以 **`/login`** 结尾（如 `/api/user/login`）或 **整段为 `/login`** 时不校验；**`OPTIONS`** 不校验。其余路径**不经过**该校验逻辑。若配置了 **`server.servlet.context-path`**，上述前缀需叠加该前缀（与 Spring 行为一致）。
- **401**：缺 token 或 `JwtUtils.parseToken` 抛错时，过滤器仅 **`response.setStatus(401)`**，**不写 `Result` JSON**。
- **文档**：Knife4j 页面一般为 **`/doc.html`**（`knife4j.enable` 等在 `service/src/main/resources/application.yml`；是否关闭默认 Swagger UI 以当前配置为准）。

## 目录与模块（详细）

```text
SmartVillages-Backend/
├── pom.xml                       # 父工程（packaging=pom），声明 modules & 依赖版本管理
├── common/                       # 公共模块（无启动类）：横切能力与基础设施
│   └── src/main/java/com/backend/common/
│       ├── config/               # 统一配置（如 OpenAPI/CORS/MyBatisPlus 等，是否存在以源码为准）
│       ├── enums/                # ErrorCode：统一错误码
│       ├── exception/            # BusinessException + GlobalExceptionHandler
│       ├── filter/               # JwtAuthenticationFilter：按路径要求 Header token
│       ├── interceptor/          # 拦截器（如有）
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

