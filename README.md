# SmartVillages

本仓库为 **monorepo**：后端（Spring Boot 多模块）与前端（Vue3 + Vite）统一放在一个仓库中。

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

后端默认数据源配置在 `SmartVillages-Backend/service/src/main/resources/application.yml`（当前为本机 MySQL 示例：`smart_villages` / `root` / `1234`），先确保本机数据库与账号一致再启动。

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

- **后端详细说明**（含登录、JWT、`/doc.html` 白名单）：`SmartVillages-Backend/README.md`
- **前端目录架构**：`SmartVillages-Web/README.md`
- **开发手册**：`开发手册.md`（根目录）；后端专项见 `SmartVillages-Backend/开发手册.md`

联调提示：后端接口默认请求头 **`token`** 携带 JWT；登录接口为 **`POST /auth/login`**（路径以实际后端地址与 context-path 为准）。

当前进度：后端登录/登出已完成，正在推进管理员用户分页接口。
