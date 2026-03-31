# SmartVillages Web（前端）

本目录是智慧乡村前端工程，基于 **Vue 3 + Vite**。根 `README.md` 只说明整体结构和启动方式，这里重点说明前端分层与目录。

## 启动与构建

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

## 分层架构（整体思路）

这个项目按“**应用装配层（app）→ 跨模块共享层（shared）→ 业务 API 层（services）→ 路由页面层（pages）→ 页级组件层（widgets）→ 状态层（store）**”分层，目的是让：

- 路由与页面只关心“展示与交互”
- 接口对接集中在 `services/`，不把请求散落到页面里
- 鉴权、拦截器、Result 适配等横切逻辑放在 `shared/`，可复用、可统一改动
- 公共/后台两套布局明确隔离，便于做登录态与菜单权限

### 根目录

```text
smartvillages-web/
├── index.html
├── package.json
├── vite.config.js
├── .env.example                      # VITE_API_BASE_URL=http://localhost:8080
├── public/
└── src/
    ├── app/
    ├── shared/
    ├── services/
    ├── pages/
    ├── widgets/
    ├── store/
    └── assets/
```

### `src/app/`（应用装配与路由）

- **用途**：把 Vue 应用“组装起来”，放入口文件、根组件、路由与布局。
- **你会在这里做什么**：挂载 `pinia/router`、配置全局路由守卫、定义公共/后台 Layout。

```text
src/app/
├── main.js                   # createApp + pinia + router 装配
├── App.vue
├── router/
│   ├── index.js              # createRouter + beforeEach 鉴权
│   └── routes/               # 分模块路由表（只放路由定义）
│       ├── auth.routes.js
│       ├── admin.routes.js
│       ├── announcement.routes.js
│       ├── feature.routes.js
│       ├── interaction.routes.js
│       ├── media.routes.js
│       └── management.routes.js
└── layouts/
    ├── PublicLayout.vue
    └── AdminLayout.vue
```

### `src/shared/`（跨模块共享能力）

- **用途**：放“横切关注点”与“可复用基础能力”，例如 axios 实例、拦截器、token 存取、环境变量读取、通用工具等。
- **核心约定**：页面组件不要直接 new axios；统一走 `shared/api/http.js` 创建的实例（这样 token 注入、401 处理才统一）。

```text
src/shared/
├── api/
│   ├── http.js               # axios 实例：baseURL、timeout
│   ├── interceptors.js       # request: 注入 header token；response: 401 处理
│   └── result.js             # 适配后端 Result{code,message,data}
├── auth/
│   ├── token.js              # localStorage 读写 token
│   └── guards.js             # needAuth / role 判定
├── config/
│   └── env.js                # 读取 import.meta.env
├── ui/                       # 基础组件封装
└── utils/
```

### `src/services/`（按后端模块划分的 API 客户端）

- **用途**：把“接口对接”从页面中抽离，形成稳定的“业务 API 层”。
- **命名建议**：一个文件对应后端一个模块/域（auth、announcement、media…），导出函数如 `login()`、`getAnnouncementList()` 等。

```text
src/services/
├── auth.api.js
├── admin.api.js
├── announcement.api.js
├── feature.api.js
├── interaction.api.js
├── media.api.js
└── management.api.js
```

### `src/pages/`（只放“路由页”）

- **用途**：这里的组件必须是“能被路由直接加载的页面”。页面负责：获取路由参数、组织页面布局、调用 `services/` 拉数据、把数据交给 `widgets/` 或更小组件渲染。
- **约定**：不要在 `pages/` 里堆很多可复用的表格/侧边栏，复用的部分放到 `widgets/` 或 `shared/ui/`。

```text
src/pages/
├── auth/
│   └── LoginPage.vue
├── public/
│   ├── HomePage.vue
│   ├── AnnouncementPage.vue
│   ├── FeaturePage.vue
│   └── InteractionPage.vue
└── admin/
    ├── DashboardPage.vue
    ├── AnnouncementManagePage.vue
    ├── MediaManagePage.vue
    └── ManagementPage.vue
```

### `src/widgets/`（页级组合件）

- **用途**：放“页面级但可复用”的组合组件，例如后台的 `Sidebar`、`Topbar`、通用 `DataTable`（分页、筛选、操作列）、上传面板等。
- **边界**：widgets 关注 UI 组合与交互，不直接依赖具体业务 API（业务 API 由页面注入/调用）。

### `src/store/`（Pinia 状态）

- **用途**：管理全局状态（如登录态、用户信息、权限集合、UI 状态），以及跨页面共享的业务状态（如筛选条件缓存）。
- **建议优先级**：先做 `auth`（token/user/perms）和 `ui`（sidebar 折叠等），其他模块等需求明确再加。

### `src/assets/`（静态资源）

- **用途**：样式（scss/css）、图标、图片等。

### 路由与权限的落点

- **路由表**：`src/app/router/routes/*.routes.js` 按模块拆分，避免一个文件越写越大。
- **鉴权守卫**：`src/app/router/index.js` 统一做“是否登录 / 是否有菜单权限”判断。
- **token 与 401 处理**：`src/shared/auth/token.js` 与 `src/shared/api/interceptors.js` 统一处理，页面不重复写。

## 相关链接

- Vite 配置文档：`https://vite.dev/config/`
