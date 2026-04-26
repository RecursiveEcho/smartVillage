# 智慧乡村综合管理系统


## 1. 我做了什么

- 这是一个 **智慧乡村综合管理系统** 的后端，采用 **Spring Boot 3 + Maven 多模块** 的方式组织。
- 核心能力是 **登录鉴权（Spring Security + JWT）+ RBAC 三角色权限**，并围绕乡村治理的业务场景实现了多个模块闭环：
  - 公告（前台只读 + 村干部管理）
  - 村民留言互动（村民提交 + 干部处理）
  - 村务治理台账（人口、房屋土地、党建组织、村务公示）
  - 民生服务工单（村民提交工单 + 干部处理流转）
- 项目有 **统一返回结构、统一业务异常、逻辑删除、时间自动填充、Redis 详情缓存** 等工程化能力，适合作为求职项目展示。

## 2. 技术栈与关键选型

- **Spring Boot 3.x**：生态成熟，配套安全/数据/文档方案齐全。
- **Maven 多模块**：按业务域拆分（auth/admin/announcement/interaction/management/media/feature/common/service），保证边界清晰、便于迭代与维护。
- **Spring Security + JWT**：无状态鉴权，配合 URL 级权限控制（`hasAuthority("ROLE_*")`）实现 RBAC。
- **MyBatis-Plus**：快速实现 CRUD + 分页 + 条件查询，同时配合逻辑删除与自动填充字段提升一致性。
- **Redis**：对“高频详情接口”做 JSON 缓存，并在更新/删除时淘汰缓存，减少 DB 压力。
- **Knife4j/OpenAPI**：接口自文档化，便于联调与展示。

## 3. 权限模型

- **ROLE_ADMIN**：只负责账号与权限相关（用户列表、启用禁用、创建村干部账号）。
- **ROLE_CADRE（村干部）**：负责业务执行与治理（公告管理、留言处理、村务台账、民生工单处理等）。
- **ROLE_VILLAGER（村民）**：前台浏览与反馈（浏览公告/风采/公示、提交留言、提交民生工单等）。

更细的 URL 规则见：`docs/PERMISSIONS.md`

## 4. 业务闭环

- **民生服务工单闭环**（更贴近真实乡村治理）
  - 村民提交 → 村干部受理/处理中 → 办结/关闭 → 村民查看自己的工单状态
- **村务事项/公示闭环**
  - 村干部创建草稿 → 审核/发布 → 公共端只读已发布列表与详情（含浏览量统计）

具体演示顺序（可直接照着跑 curl）见：`docs/DEMO-SCRIPT.md`

## 5. 工程化与可维护性亮点

- **统一返回**：`Result(code/message/data)`
- **统一业务异常**：`BusinessException + GlobalExceptionHandler`
- **错误码枚举**：`ErrorCode` 按领域码段管理
- **逻辑删除 + 自动填充**：deleted/createTime/updateTime
- **缓存策略**：Redis JSON 详情缓存（读缓存/写淘汰）
- **配置安全**：运行配置优先使用环境变量，避免敏感信息入库（见 `service/src/main/resources/application.yml`）

## 6. 我会怎么继续演进（加分项）

- 为鉴权与关键业务流补 **集成测试**（Security 规则回归）
- 把状态字段（如 status、type）进一步 **枚举化/常量化**，提高可读性与文档质量
- 引入 **操作审计**（谁在什么时间做了什么操作）以贴近真实后台项目

