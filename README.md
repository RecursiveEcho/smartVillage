# SmartVillages

本仓库为 **monorepo**：后端（Spring Boot 多模块）与前端（Vue3 + Vite）统一放在一个仓库中。

## 目录结构

```text
SmartVillages/
├── SmartVillages-Backend/        # 后端：Spring Boot + Maven 多模块
└── SmartVillages-Web/            # 前端：Vue 3 + Vite
```

## 工程概览

根工程 Maven 坐标为 `com.backend:smartVillages`，采用 **多模块 + 单 Spring Boot 进程**（`service` 模块打包可执行 jar）。  
各业务 jar **只依赖 `common`**；`common` 通过依赖引入 **Spring Web、MyBatis-Plus、Knife4j(OpenAPI3)、JJWT**，并提供 **`ErrorCode`、`Result`、`GlobalExceptionHandler`、`JwtUtils`、`JwtAuthenticationFilter`** 等（详见 `SmartVillages-Backend/common/pom.xml` 与源码）。根包名为 **`com.backend.*`**。

> **与代码严格对齐**：下列各模块的「核心功能」多为**领域与表结构已规划**的能力说明；**是否已在对应 `controller`/`service` 中实现，以仓库源文件为准**。`common` 与子模块中存在的 **空类、TODO** 表示尚未补全，本文**不将其描述为已交付功能**。

## 快速开始

### 后端启动（SmartVillages-Backend）

构建：

```bash
cd SmartVillages-Backend
./mvnw -pl service -am clean package
```

运行（开发）：

```bash
cd SmartVillages-Backend
./mvnw -pl service -am spring-boot:run
```

### 前端启动（SmartVillages-Web）

```bash
cd SmartVillages-Web
npm install
npm run dev
```

构建：

```bash
cd SmartVillages-Web
npm run build
```

### 统一返回与错误约定

| 能力 | 位置（`common`） | 说明 |
|------|------------------|------|
| **ErrorCode** | `enums/ErrorCode.java` | 按码段划分：`1xxx` 用户/认证，`2xxx` 权限，`3xxx` 文件/配置，`4xxx` 通用业务与村务域（`41xx` 等），`5xxx` 系统，`6xxx` 路由语义；**同一语义对应唯一数字**，新增时在对应段内递增。 |
| **Result** | `result/Result.java` | 统一 JSON 字段 **`code` / `message` / `data`**；成功常用 **`code = 200`**（见类常量 `SUCCESS_CODE`）。业务失败可在 Controller 中 `Result.fail(...)` / `Result.error(...)`，或由 **`BusinessException`** 进入全局处理器后返回（JWT 过滤器产生的 **401 不在此结构内**）。 |
| **GlobalExceptionHandler** | `exception/GlobalExceptionHandler.java` | 处理 **已进入 Spring MVC** 的异常并返回 `Result`：`BusinessException`、参数校验、404/405 等；兜底 `Exception`。**不含** JWT 解析失败（见过滤器）。 |

### JWT 与接口文档（简要）

- **鉴权**（实现见 `JwtAuthenticationFilter`）：仅当 **`HttpServletRequest.getRequestURI()`**（不含 query）、在去除末尾 `/` 后规范化，**等于**或在 **`/api/user`、`/api/cadre`、`/api/admin`** 之下的子路径时，才校验请求头 **`token`**；路径以 **`/login`** 结尾（如 `/api/user/login`）或 **整段为 `/login`** 时不校验；**`OPTIONS`** 不校验。其余路径**不经过**该校验逻辑。若配置了 **`server.servlet.context-path`**，上述前缀需叠加该前缀（与 Spring 行为一致）。
- **401**：缺 token 或 `JwtUtils.parseToken` 抛错时，过滤器仅 **`response.setStatus(401)`**，**不写 `Result` JSON**。
- **文档**：Knife4j 页面一般为 **`/doc.html`**（`knife4j.enable` 等在 `SmartVillages-Backend/service/src/main/resources/application.yml`；是否关闭默认 Swagger UI 以当前配置为准）。

### 仓库目录（Maven 模块）

```text
SmartVillages-Backend/                   # 后端父工程（packaging=pom）
├── pom.xml
├── common/                              # 公共模块（无启动类）
│   └── src/main/java/com/backend/common/
│       ├── config/                      # 如 OpenApiConfig；其余 Config 可能尚未实现具体 Bean
│       ├── enums/                       # ErrorCode
│       ├── exception/                   # GlobalExceptionHandler、BusinessException
│       ├── filter/                      # JwtAuthenticationFilter（不负责请求/响应字符集）
│       ├── result/                      # Result
│       └── utils/                       # JwtUtils
├── auth/                                # 1. 认证（包：com.backend.auth）
├── admin/                               # 2. 后台用户
├── announcement/                        # 3. 村务公告
├── management/                          # 4. 村务管理（包：com.backend.management）
├── feature/                             # 5. 乡村风采
├── interaction/                         # 6. 村民留言
├── media/                               # 7. 媒体资源
└── service/                             # 启动与配置（聚合全部模块）
    ├── pom.xml                          # 依赖 common + 上述业务模块 + MySQL 驱动
    └── src/main/
        ├── java/com/backend/
        │   └── SmartVillagesApplication.java
        ├── resources/
        │   ├── application.yml
        │   └── sql/                     # 表结构/初始化脚本
        └── test/java/com/backend/
            └── SmartVillagesApplicationTests.java
```

## 前端与联调（补充）

前端工程位于 `SmartVillages-Web/`（Vue 3 + Vite）。前端的更完整目录分层说明见 `SmartVillages-Web/README.md`，这里补充与后端联调相关的关键点。

### 接口地址（建议通过环境变量统一配置）

前端 README 中已提供 `.env.example`（示例：`VITE_API_BASE_URL=http://localhost:8080`）。建议你：

- 本地新建 `.env`（或 `.env.development`），配置 `VITE_API_BASE_URL`
- 在请求封装（`src/shared/api/http.js` / `src/shared/config/env.js`）里统一读取该值作为 baseURL

### 跨域（开发期二选一）

- **方案 A：Vite Proxy（推荐开发期）**：由前端 dev server 代理到后端，浏览器侧无跨域
- **方案 B：后端 CORS**：由后端放行前端 origin

### 鉴权 Header 与 401 行为

- **token 位置**：后端过滤器读取请求头 `token`（不是 `Authorization: Bearer`）
- **401 返回**：JWT 不合法/缺 token 时，过滤器直接返回 `HTTP 401`，通常**不返回** `Result` JSON

## 更多文档


