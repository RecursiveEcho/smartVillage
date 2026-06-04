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
    ├── services/               # 按模块封装的 API（auth、announcement、admin、interaction、management、feature、media、village 等）
    ├── shared/                 # http、token、session、工具函数
    └── styles/theme.css
```

## 现状说明

- 已接入 **vue-router、axios**，门户与 **`/admin`（管理员/村干部）**、**`/village`（村民）** 分栏，**`services/`** 目录已按后端模块封装了完整的 API 调用层（auth、announcement、admin、interaction、management、feature、media、village 等）。
- 开发环境 **`apiBaseUrl` 为空**，通过 Vite **proxy** 同源访问后端，避免局域网 IP 打开页面时 CORS 失败。
- 生产构建请配置 **`VITE_API_BASE_URL`** 为线上 API 根地址（后端 Gateway 入口 `http://localhost:8080`）。

### 联调要点

- 登录成功后保存 **`token`**，请求拦截器为受保护接口附加请求头 **`token`**。
- 后端已使用 **Spring Security**：**401 / 403** 的响应体不一定为 `Result`，前端需分别处理「未登录跳转登录页」与「权限不足提示」。

## 相关链接

- Vite 配置文档：`https://vite.dev/config/`
