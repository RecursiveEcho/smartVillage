# CRUD 完美闭环模板（可复用）

这份文档用于把“写了一堆 CRUD”升级为“可维护、可联调、可上线”的闭环交付。后续每新增一个业务模块，按本模板补齐即可。

---

## 0. 一句话定义（必填）

- **资源是什么**：例如公告 Announcement / 留言 Interaction / 村务事项 VillageAffair
- **服务对象**：前台用户、后台管理员/干部各能做什么
- **生命周期**：创建 → 列表/详情 → 状态流转（审核/上架/下架/关闭）→ 删除（逻辑/物理）

---

## 1. API 合同（接口闭环）

把接口按“前台读 / 后台写 / 状态流转 / 管理查询”分组列清楚。写完这章就等于你对外的接口合同（Swagger/前端联调以此为准）。

### 1.1 前台读接口（示例结构）

- `GET /<resource>`：分页列表
  - **分页参数**：`current,size`，范围约束（`@Min/@Max`）
  - **排序**：说明默认排序规则（例如置顶优先、发布时间倒序）
  - **过滤**：支持哪些查询条件（可选）
- `GET /<resource>/{id}`：详情
  - **可见性**：仅已发布？仅本人？仅未删除？
  - **副作用**：是否会刷浏览量/记录足迹（有则写清）
- `GET /<resource>/hot`：热门/推荐（如有）
  - **limit 默认值与上限**

### 1.2 后台写接口（示例结构）

- `POST /cadre/<resource>`：创建
  - **默认字段**：默认状态、默认计数、创建人等
- `PUT /cadre/<resource>/{id}`：编辑基础信息
  - **允许编辑字段**：明确列出
  - **不允许编辑字段**：例如创建人、创建时间
- `PUT /cadre/<resource>/{id}/status`：上下架/关闭
  - **允许的 status 值**：必须限定集合
  - **允许的流转**：必须限定状态机（见第 3 章）
- `PUT /cadre/<resource>/{id}/audit`：审核（如有）
- `DELETE /cadre/<resource>/{id}`：删除（逻辑/物理）

### 1.3 统一返回与错误码

- **成功**：`Result.success(data)` / `Result.success("文案")`
- **失败**：`BusinessException(ErrorCode.XXX)` → `GlobalExceptionHandler` 统一转 `Result.fail(code,msg)`
- **模块至少要有**：`*_NOT_FOUND`、`STATUS_INVALID`、`PARAM_INVALID`（或复用通用）

---

## 2. 模型闭环（DTO / Entity / VO）

### 2.1 DTO（入参）

- **只放客户端可提交字段**
- **必须有 JSR380 校验**：`@NotBlank/@NotNull/@Size/@Pattern/@Min/@Max`
- **禁止把内部字段放入 DTO**：如 `createUser/auditUser/deleted/viewCount`

### 2.2 Entity（持久化）

- **全量字段**：含审计字段、逻辑删除、状态、计数
- **逻辑删除**：统一使用 `@TableLogic` 字段，并在查询条件中保持一致

### 2.3 VO（出参）

- **只放对外展示字段**：前台/后台可以分 VO（需要的话）
- **避免敏感/内部字段泄露**

### 2.4 字段语义（强约束）

- `createUser` 永远是**创建人**，更新时不要覆盖
- 如需记录最后修改人，新增 `updateUser`
- `publishTime` 只在“发布/通过”时写入（不要在创建时自动填充）

---

## 3. 状态机闭环（强烈建议写成表）

### 3.1 状态枚举

以数字/枚举约定一份“唯一真相”：

- `0`：待审核
- `1`：已发布/已通过
- `2`：已拒绝
- `3`：已下架

### 3.2 允许流转（示例）

- `0 → 1/2`（审核）
- `1 → 3`（下架）
- `3 → 1`（重新上架，若允许）

### 3.3 状态校验落点（必须）

- **Controller 层**：参数范围校验（可选）
- **Service 层**：`validateStatus()` + `validateTransition(from,to)`
- **错误码**：非法 status → `ErrorCode.STATUS_INVALID`；非法流转 → `ErrorCode.OPERATION_NOT_ALLOWED`

---

## 4. 业务校验闭环（Service 层必须兜住）

每个写接口（update/status/audit/delete）至少具备：

- **存在性校验**：`mustGetEntity(id)`，不存在抛 `*_NOT_FOUND`
- **权限校验**：前台/干部接口必须收口（全局拦截也可以，但要写清“在哪校验”）
- **状态校验/流转校验**
- **幂等/重复提交（可选）**

---

## 5. 异常闭环（前端可预期）

- 全部业务错误走 `BusinessException(ErrorCode.xxx)`
- 全部参数错误走校验注解 + 全局异常处理器转 `ErrorCode.PARAM_INVALID`
- 禁止 NPE/500 暴露给前端作为业务结果

---

## 6. 性能与一致性闭环（按需）

可选能力（做了就写清策略）：

- **详情缓存**：key 规则、TTL、写操作删缓存
- **计数原子更新**：浏览量/点赞用 SQL 原子 `setSql(...)`，避免并发丢失
- **分页默认排序**：确保前后端一致

---

## 7. 可观测性闭环（上线后能定位问题）

- 关键操作日志：谁（userId）在什么时间对哪个资源 id 做了什么
- 失败日志：业务异常 warn + 堆栈（全局异常处理器已做则备注）

---

## 8. 测试闭环（最低标准）

每个模块至少覆盖：

- 正常流：创建 → 列表 → 详情 → 状态流转 → 再详情
- 异常流：id 不存在 / status 非法 / 非法流转 / 无权限

---

# 附录 A：本项目落地示例（Announcement 公告）

> 这一段是“可复制的样板”。你后面每个模块都可以照这个结构写一份。

## A1. 一句话定义

- **资源**：公告（announcement）
- **对象**：前台用户读“已发布”；干部创建/编辑/审核/上下架/删除
- **生命周期**：创建（待审核）→ 审核（发布/拒绝）→ 前台读（刷浏览量）→ 下架 → 删除（逻辑删除）

## A2. API 合同（与代码对齐）

### 前台

- `GET /announcements`：分页（仅已发布），排序：置顶优先 + 发布时间倒序
- `GET /announcements/{id}`：详情（仅已发布；浏览量 +1；优先读缓存）
- `GET /announcements/hot`：热门（仅已发布；按浏览量倒序）

### 后台（干部）

- `POST /cadre/announcements`：创建（默认待审核）
- `PUT /cadre/announcements/{id}`：编辑基础信息（标题/内容/类型/置顶）
- `PUT /cadre/announcements/{id}/audit`：审核（通过写 publishTime + auditTime/auditUser）
- `PUT /cadre/announcements/{id}/status`：上下架（状态合法性校验；写操作删详情缓存）
- `DELETE /cadre/announcements/{id}`：删除（不存在返回业务错误码）
- `GET /cadre/announcements`：后台分页查询（status/title/type/isTop/timeRange）
- `GET /cadre/announcements/pending`：待审核列表
- `GET /cadre/announcements/audited`：审核历史

## A3. 关键闭环点（代码级）

- **存在性校验**：统一 `mustGetEntity(id)`，不存在抛 `ErrorCode.ANNOUNCEMENT_NOT_FOUND`
- **状态合法**：统一 `validateStatus(status)`，非法抛 `ErrorCode.STATUS_INVALID`
- **前台可见性**：详情只允许 `status=已发布`；缓存命中也会校验 VO 的 status，避免下架仍刷浏览量
- **缓存一致性**：写操作统一 `evictDetailCache(id)`；缓存 TTL 由 `RedisJsonCacheTool` 统一控制
- **发布时间语义**：`publishTime` 只在发布/通过时写入，不在创建时自动填充

