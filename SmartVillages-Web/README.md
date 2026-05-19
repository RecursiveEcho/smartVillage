# SmartVillages Web（前端）

本目录是智慧乡村前端工程，基于 **Vue 3 + Vite**。根目录 `README.md` 说明仓库整体启动方式；本文件说明**当前仓库真实结构**与后续扩展建议。

## 启动与构建

- **Node 版本**：以 `package.json` 中 `engines` 为准（建议 **20.19+** 或 **22.12+**）。
- **安装依赖**：

```bash
npm install
```

- **开发启动**：

```bash
npm run dev
```

- **打包构建**：

```bash
npm run build
```

## 当前仓库中的目录（真实）

```text
SmartVillages-Web/
├── index.html
├── package.json
├── vite.config.js              # @ -> src；开发代理到后端
├── jsconfig.json
└── src/
    ├── app/
    │   ├── main.js             # 路由 + 全局样式
    │   ├── App.vue
    │   ├── router/index.js
    │   └── layouts/
    │       ├── PublicLayout.vue
    │       ├── AdminLayout.vue
    │       └── VillageLayout.vue
    ├── pages/
    │   ├── auth/loginPage.vue
    │   ├── public/             # 门户：公告/风采/村务/互动 + 详情页
    │   ├── village/            # 村民中心：工单与我的留言
    │   └── admin/              # 管理端：按角色显示菜单
    ├── services/               # 按模块封装的 API
    ├── shared/                 # http、token、session、工具函数
    └── styles/theme.css
```

## 现状说明

- 已接入 **vue-router、axios**，门户与 **`/admin`（管理员/村干部）**、**`/village`（村民）** 分栏；开发环境 **`apiBaseUrl` 为空**，通过 Vite **proxy** 同源访问后端，避免局域网 IP 打开页面时 CORS 失败。
- 生产构建请配置 **`VITE_API_BASE_URL`** 为线上 API 根地址。

## 后续分层建议（与后端 monorepo 文档风格一致，按需采纳）

当需要与后端 **`Result{code,message,data}`** 及 **`token`** 头正式联调时，建议逐步演进为：

```text
src/
├── app/                 # 入口、根组件、router、layouts
├── shared/              # axios 实例、拦截器、token 存取、Result 适配
├── services/            # 按后端模块拆分的 API 封装（auth、admin、announcement…）
├── pages/               # 路由页面（保持「只放页面」的边界）
├── widgets/             # 可选：表格、侧栏等页级组合件
├── store/               # 可选：Pinia（auth、ui 等）
└── assets/
```

### 与后端对齐的联调要点

- 登录成功后保存 **`token`**，请求拦截器为受保护接口附加请求头 **`token`**。
- 后端已使用 **Spring Security**：**401 / 403** 的响应体不一定为 `Result`，前端需分别处理「未登录跳转登录页」与「权限不足提示」。

## 相关链接

- Vite 配置文档：`https://vite.dev/config/`
