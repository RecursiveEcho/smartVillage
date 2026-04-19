# SmartVillages

本仓库为 **monorepo**：后端（Spring Boot 多模块）与前端（Vue 3 + Vite）放在同一仓库中。

## 目录结构（概览）

```text
SmartVillages/
├── SmartVillages-Backend/        # 后端：Spring Boot + Maven 多模块
└── SmartVillages-Web/            # 前端：Vue 3 + Vite
```

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

后端默认配置在 `SmartVillages-Backend/service/src/main/resources/application.yml`：

- **MySQL**：库名示例为 `smartVillage`，账号 `root` / `1234`（请与本机一致后再启动）。
- **Redis**：公告详情等会使用缓存；示例为 `localhost:6379`，密码 `123456`。若本地未装 Redis，需调整配置或先启动对应实例，否则相关能力可能启动失败。

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

## 文档入口

- **后端详细说明**（鉴权、Spring Security、接口摘要）：`SmartVillages-Backend/README.md`
- **前端现状与目录**：`SmartVillages-Web/README.md`
- **开发手册**：根目录 `开发手册.md`；后端执行版见 `SmartVillages-Backend/开发手册.md`

## 联调约定（摘要）

- 请求头 **`token`**：携带 JWT 字符串。
- 登录：**`POST /auth/login`**，Body JSON：`username`、`password`；成功时 `data` 为 `{ "token": "..." }`（以实际 `JwtResponse` 为准）。
- 登出：**`DELETE /auth/logout`**（无状态场景下多为客户端丢弃 token；接口路径以部署地址与 context-path 为准）。
- 接口文档：浏览器打开 **`http://<host>:<port>/doc.html`**；OpenAPI JSON：**`/v3/api-docs`**。

## 当前进度（概要）

- 后端已接入 **Spring Security**：JWT 由 **`JwtSecurityFilter`** 解析并写入 **`SecurityContext`**，与 URL 级 **`hasAuthority("ROLE_*")`** 规则配合；业务代码仍可通过 **`LoginUserContext`** 从 `HttpServletRequest` 读取 `authId` / `username` / `role`（过滤器同步写入 request attribute）。
- **管理员域**：`GET /admin/me`、分页用户 **`GET /admin/users`**、**`PUT /admin/users/{id}/status`**、创建村干部 **`POST /admin/cadre`** 等已落地（细节以后端 README 与 Knife4j 为准）。
- **公告域**：前台只读 **`/announcements*`** 与村干部维护 **`/cadre/announcements*`** 等已接入（含 Redis 详情缓存等，见实现类注释）。
- **留言域**：**`POST /interactions/messages`**（路径以 Controller 为准）。
- 前端工程目前为 **Vue 3 最小入口 + 页面文件占位**，尚未接入 `vue-router` / `pinia` / 统一 HTTP 客户端；完整联调需按 `SmartVillages-Web/README.md` 中的规划逐步补齐。
