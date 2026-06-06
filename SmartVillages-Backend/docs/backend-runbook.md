# SmartVillages Backend Runbook

## 1. 项目目标

这份文档只解决一件事：

- 后端怎么启动
- 服务先起谁
- 网关怎么转发
- 前后端联调时从哪里进

适用于当前项目结构：

- `gateway-service`
- `auth-service`
- `business-service`
- `media-service`
- `admin-service`

---

## 2. 服务与端口

当前后端服务端口如下：

| 服务 | 端口 | 说明 |
| --- | --- | --- |
| `gateway-service` | `8090` | 前端统一入口 |
| `business-service` | `8081` | 公告、风采、留言、村务等业务服务 |
| `auth-service` | `8082` | 登录、认证、用户数据归属 |
| `media-service` | `8083` | 媒体上传、审核、绑定 |
| `admin-service` | `8084` | 管理端服务，内部通过 Feign 调 auth-service |

前端开发服务：

| 服务 | 端口 | 说明 |
| --- | --- | --- |
| `SmartVillages-Web` | `5173` | Vite 开发服务 |

---

## 3. 运行依赖

启动后端前，需要先准备这些基础环境：

- MySQL
- Redis
- RabbitMQ
- Nacos
- `.env` 配置文件

当前 `.env` 中已经使用到的变量包括：

### MySQL

- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`

### Redis

- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`
- `REDIS_DATABASE`

### RabbitMQ

- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`

### OSS

- `OSS_ENDPOINT`
- `OSS_BUCKET_NAME`
- `OSS_ACCESS_KEY_ID`
- `OSS_ACCESS_KEY_SECRET`

### Nacos

如果不额外配置，当前项目默认读取：

- `NACOS_SERVER_ADDR=127.0.0.1:8848`
- `NACOS_USERNAME=nacos`
- `NACOS_PASSWORD=nacos`

注意：

- Nacos 不只要 `8848` 可用
- 还要确保 `9848` gRPC 端口正常
- 如果 `9848` 没起来，服务可能编译通过但注册失败

---

## 4. 推荐启动顺序

建议按下面顺序启动。

### 第一步：基础环境

先确认这些都已启动：

1. MySQL
2. Redis
3. RabbitMQ
4. Nacos

建议至少检查：

```bash
lsof -nP -iTCP:3306 -sTCP:LISTEN
lsof -nP -iTCP:6379 -sTCP:LISTEN
lsof -nP -iTCP:5672 -sTCP:LISTEN
lsof -nP -iTCP:8848 -sTCP:LISTEN
lsof -nP -iTCP:9848 -sTCP:LISTEN
```

### 第二步：后端业务服务

先起被依赖的服务，再起入口服务：

1. `auth-service`
2. `business-service`
3. `media-service`
4. `admin-service`
5. `gateway-service`

### 第三步：前端

最后启动：

1. `SmartVillages-Web`

---

## 5. 常用启动命令

以下命令都在 `SmartVillages-Backend/` 目录执行。

### 启动 auth-service

```bash
./mvnw -pl auth-service spring-boot:run
```

### 启动 business-service

```bash
./mvnw -pl business-service spring-boot:run
```

### 启动 media-service

```bash
./mvnw -pl media-service spring-boot:run
```

### 启动 admin-service

```bash
./mvnw -pl admin-service spring-boot:run
```

### 启动 gateway-service

```bash
./mvnw -pl gateway-service spring-boot:run
```

前端在 `SmartVillages-Web/` 目录执行：

```bash
npm run dev
```

---

## 6. 网关统一入口

当前前端和联调都应该只认网关：

- `http://127.0.0.1:8090`

已经配置到网关的主要前缀：

### auth-service

- `/auth/**`

### admin-service

- `/admin/**`

### media-service

- `/media/**`

### business-service

- `/api/**`
- `/announcements/**`
- `/interactions/**`
- `/cadre/**`
- `/villager/**`
- `/public/**`
- `/features/**`

说明：

- 前端不要再直接访问 `8081/8082/8083/8084`
- 前端统一通过 `8090` 进入

---

## 7. 当前联调入口

前端开发地址：

- `http://127.0.0.1:5173/`

前端当前已经调整为：

- 开发环境通过 Vite 代理走网关 `8090`
- 生产构建默认请求网关 `8090`

---

## 8. 快速自检

如果项目启动后页面不通，按这个顺序排查。

### 1）先看服务是否注册到 Nacos

确认：

- `auth-service`
- `business-service`
- `media-service`
- `admin-service`
- `gateway-service`

都出现在 Nacos 服务列表里。

### 2）再看网关是否起来

访问：

- `http://127.0.0.1:8090`

或者直接看 `gateway-service` 日志。

### 3）最后看前端请求是不是走网关

浏览器开发者工具中，确认请求地址前缀是：

- `/auth/...`
- `/admin/...`
- `/media/...`
- `/announcements/...`
- `/interactions/...`
- `/cadre/...`
- `/villager/...`
- `/public/...`
- `/features/...`

而不是：

- `http://127.0.0.1:8081`
- `http://127.0.0.1:8082`
- `http://127.0.0.1:8083`
- `http://127.0.0.1:8084`

---

## 9. 当前项目阶段结论

当前项目已经完成的关键链路：

1. 媒体上传、审核、MQ、业务消费、回写
2. MQ 死信队列
3. auth / business / media / admin 的基础拆分
4. 网关统一入口
5. Nacos 注册与基础配置接入
6. admin-service 通过 Feign 调 auth-service

当前最适合继续推进的方向：

1. 前后端联调收尾
2. 补一轮关键页面的实际操作验证
3. 做项目展示材料和面试表达

