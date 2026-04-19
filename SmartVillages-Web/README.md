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

> 当前 `package.json` **仅声明 `vue` 依赖**，尚未引入 `vue-router`、`pinia`、`axios` 等；若下文中「规划目录」与你的分支不一致，以 **`package.json` + 实际文件树** 为准。

## 当前仓库中的目录（真实）

```text
SmartVillages-Web/
├── index.html
├── package.json
├── vite.config.js              # @ -> src；含 Vue 插件与 DevTools
├── jsconfig.json
└── src/
    ├── app/
    │   ├── main.js             # createApp(App).mount('#app')
    │   ├── App.vue             # 根组件（待接入路由/布局）
    │   └── layouts/
    │       ├── PublicLayout.vue
    │       └── AdminLayout.vue
    └── pages/
        ├── auth/
        │   └── loginPage.vue
        ├── public/             # 门户侧页面占位
        │   ├── HomePage.vue
        │   ├── Announcement.vue
        │   ├── FeaturePage.vue
        │   └── InteractionPage.vue
        └── admin/              # 后台侧页面占位
            ├── DashboardPage.vue
            ├── AnnouncementManagePage.vue
            ├── MediaManagePage.vue
            └── ManagementPage.vue
```

## 现状说明

- **`src/app/main.js`** 仅挂载 **`App.vue`**，**未注册路由与全局状态**。
- **`App.vue`** 当前为空模板，**各 `pages/` 与 `layouts/` 组件尚未被引用**；联调前需要在 `App.vue` 或路由视图中装配布局与页面。
- 仓库内 **无** `.env.example`；后端地址与 **`token`** 请求头约定可在引入 HTTP 客户端后，用 Vite 环境变量（如 `VITE_API_BASE_URL`）统一配置。

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
