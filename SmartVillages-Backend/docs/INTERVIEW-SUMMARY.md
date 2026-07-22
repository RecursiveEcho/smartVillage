# 智慧乡村综合管理系统

## 1. 一句话介绍项目

这是一个围绕乡村治理场景做的多模块后端项目，后面我又把它继续往微服务方向推进，核心覆盖了：

- 登录鉴权
- 用户与角色管理
- 公告与乡村风采
- 村民留言互动
- 村务事项与民生工单
- 媒体上传、审核、绑定

我做这个项目的目标不是只把业务接口写出来，而是把它打磨成一个可以讲“完整业务闭环 + 工程化能力 + 微服务演进过程”的实习项目。

---

## 2. 技术栈

- Java 17
- Spring Boot 3
- Spring Security + JWT
- MyBatis-Plus
- Redis
- RabbitMQ
- Nacos
- OpenFeign
- Spring Cloud Gateway
- Vue 3 + Vite

---

## 3. 项目结构怎么讲

项目一开始是多模块后端，后面我把主链路逐步拆成了 5 个服务：

| 服务 | 职责 | 端口 |
| --- | --- | --- |
| `gateway-service` | 统一入口、路由转发 | `8090` |
| `auth-service` | 登录认证、用户身份、用户名查询 | `8082` |
| `business-service` | 公告、乡村风采、留言互动、村务等核心业务 | `8081` |
| `media-service` | 媒体上传、审核、资源绑定 | `8083` |
| `admin-service` | 管理端用户管理，内部通过 Feign 调 auth-service | `8084` |

我现在对这个项目的定位是：

- 前端统一走网关
- 用户数据归 `auth-service`
- 业务能力归 `business-service`
- 媒体能力归 `media-service`
- 管理端流程归 `admin-service`

---

## 4. 我做过的比较完整的一条业务链

我觉得这个项目里最能讲的一条链是“媒体上传 -> 审核 -> MQ -> 业务绑定”。

流程是：

1. 用户上传媒体
2. `media-service` 上传 OSS，并写入媒体记录
3. 村干部审核媒体
4. 审核通过后发送 RabbitMQ 消息
5. `business-service` 或 `auth-service` 消费消息
6. 根据 `bindTarget` 找到对应 Binder
7. 把媒体地址回写到具体业务表

这条链我已经跑通了：

- `AUTH + AVATAR`
- `FEATURE + COVER`
- `FEATURE + VIDEO`
- `FEATURE + IMAGES_APPEND`

这条链的价值在于它不只是一个上传功能，而是把这些能力串起来了：

- 服务拆分
- MQ 异步解耦
- 审核后绑定
- 失败重试
- 死信队列

---

## 5. 微服务这块我具体做了什么

### 5.1 网关统一入口

我加了 `gateway-service`，让前端不再直接打多个服务端口，而是统一走网关。

现在主要前缀是：

- `/auth/**`
- `/admin/**`
- `/media/**`
- `/api/**`
- `/announcements/**`
- `/interactions/**`
- `/cadre/**`
- `/villager/**`
- `/public/**`
- `/features/**`

这样做的原因是：

- 前端入口统一
- 后面加统一鉴权、限流、日志更自然
- 服务实例和端口对前端透明

### 5.2 Nacos 注册中心

我把几个服务都接进了 Nacos，服务之间不再写死 `localhost:808x`。

比如：

- `business-service` 调 `auth-service`
- `media-service` 调 `auth-service`
- `admin-service` 调 `auth-service`

现在都是按服务名调用。

### 5.3 Feign 调用替代直查库

我做过一个比较关键的边界改造：  
原来别的模块会直接碰认证表，后来我改成通过 `auth-service` 的内部接口统一查。

典型例子有两个：

1. `business-service` / `media-service`
   - 需要用户名时，不再直查认证表
   - 改成 Feign 调 `auth-service`

2. `admin-service`
   - 原来用户管理直接依赖 `AuthMapper`
   - 后来拆出来，改成 `admin-service -> Feign -> auth-service`

这样改的意义是：

- 用户数据归属更清楚
- 服务边界更真实
- 后续认证服务独立演进时影响更小

### 5.4 admin-service 拆分

这个是我最近完成的一块。

我把管理员相关能力独立成了 `admin-service`，并且：

- 接进 Nacos
- 接进 Gateway
- 通过 Feign 调 `auth-service`
- 保留了管理端这边的 Redis 缓存和分布式锁

这里我比较注意的一点是：

- 数据库读写权归 `auth-service`
- 管理端流程、缓存、删缓存、版本号归 `admin-service`

也就是说，我拆的不是“文件位置”，而是“职责”。

---

## 6. 我做过的工程化能力

### 6.1 Redis 缓存

我项目里主要做了：

- 详情缓存
- 列表分页缓存
- 空值占位
- 缓存随机过期抖动

列表缓存我没有用 `KEYS` 扫描删缓存，而是用了“版本号失效”：

- 读列表时，key 带版本号
- 写操作后，只 bump 一次版本号
- 新请求自然走新 key
- 旧 key 让它自己过期

这个比较适合面试讲“为什么这样设计”。

### 6.2 登录限流

登录接口我用了 Redis 做限流，按：

- `IP + 用户名`

做短时间窗口计数，超过阈值直接返回 429。

### 6.3 TraceId 与统一异常

为了让请求链路更容易排查，我做了：

- `TraceIdFilter`
- `JwtSecurityFilter`
- `GlobalExceptionHandler`
- `Result<T>` 统一返回

这样 Controller 不需要重复写公共逻辑，出问题时也能按 traceId 查日志。

### 6.4 RabbitMQ 死信队列

我不只是把消息“发出去”，还把失败链路补了。

现在的处理是：

- 业务异常：不无限重试，直接进死信队列
- 临时异常：保留重试机会

这样系统不会被坏消息拖死。

---

## 7. 如果面试官问“你这个项目最能体现你的点是什么”

我会这样答：

第一，这个项目不是只有业务接口，我做了比较完整的业务闭环，比如媒体上传、审核、MQ 绑定这条链。  
第二，我不是停在单体 CRUD，而是把项目继续往微服务方向推进了，做了 Gateway、Nacos、Feign 和 admin-service 拆分。  
第三，我比较重视工程细节，比如 Redis 缓存、版本号失效、登录限流、统一异常、死信队列，这些都是为了让系统不只是“能跑”，而是“出问题也可控”。

---

## 8. 如果面试官问“你项目里最有代表性的难点是什么”

我会优先讲两个：

### 难点一：媒体审核后绑定多个业务目标

难点不在上传本身，而在于：

- 一个媒体能力要服务头像、公告、风采等多个场景
- 还要审核通过后才能真正绑定

我最后是用：

- RabbitMQ
- `bindTarget`
- Binder 分发

把这件事拆开做的。

### 难点二：admin-service 的真实拆分

难点不在于“新建一个模块”，而在于：

- 不能只是把代码搬过去
- 要把 `admin -> auth` 的直查库关系切断
- 还要保住原来管理端的缓存和锁逻辑

我最后做法是：

- `auth-service` 继续管用户数据
- `admin-service` 通过 Feign 调内部接口
- Redis 缓存和流程控制留在 `admin-service`

---

## 9. 我还会怎么继续优化

如果继续打磨这个项目，我下一步会做：

1. 前后端联调收尾，把关键页面完整跑一遍
2. 补一轮更像真实项目的联调文档和运行说明
3. 补关键链路测试
4. 继续整理面试表达，让每个亮点都能讲成“问题 -> 方案 -> 验证”

