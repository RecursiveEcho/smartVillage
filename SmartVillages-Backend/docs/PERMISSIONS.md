# 权限与路由边界（RBAC）

## 1. 三角色职责边界

- **ROLE_ADMIN（管理员）**
  - 只负责账号管理：用户分页、启用禁用、创建村干部账号等
- **ROLE_CADRE（村干部）**
  - 负责村务业务执行：公告、留言处理、村务台账、民生工单处理等
- **ROLE_VILLAGER（村民）**
  - 前台浏览与反馈：浏览内容、提交留言/工单、查看自己的记录等

## 2. URL 权限规则入口

最终以 `common/src/main/java/com/backend/common/config/SecurityConfig.java` 为准。

## 3. 当前项目中常用的路由分区（摘要）


- **匿名可访问（permitAll）**
  - `/auth/login`
  - `/doc.html`、`/v3/api-docs/**`、`/swagger-ui/**` 等文档相关
  - `/announcements/**`（公告公共端）
  - `/features/**`（风采公共端）
  - `/village-affairs/**`（村务公示公共端）
  - `GET /interactions/messages`（留言公共列表，如果后续要收紧可再调整）

- **需要登录（authenticated）**
  - `/media/upload`、`/media/{id}` 等（以 `SecurityConfig` 当前配置为准）

- **管理员（ROLE_ADMIN）**
  - `/admin/users/**`

- **村干部（ROLE_CADRE）**
  - `/cadre/announcements/**`
  - `/cadre/interactions/**`
  - `/cadre/features/**`
  - `/cadre/management/**`（民生工单、人口台账等）
  - `/cadre/village-population/**`
  - `/cadre/village-house-land/**`
  - `/cadre/village-party/**`
  - `/cadre/village-affairs/**`

- **村民（ROLE_VILLAGER）**
  - `/villager/**`
  - `POST /interactions/messages`
  - `/interactions/messages/my/**`
  - `/villager/management/services/**`

