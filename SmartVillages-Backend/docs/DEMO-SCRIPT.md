# 演示脚本（curl 版本）

> 目标：面试时 5-8 分钟跑完“登录 → 权限 → 业务闭环”，证明系统可用。
> 默认后端地址：`http://localhost:8080`（以实际端口为准）

## 0. 准备：示例账号

初始化数据：`service/src/main/resources/sql/auth.sql`

- 管理员：`admin` / `123456`
- 村干部：`cadre_wang` / `123456`
- 村民：`zhang_san` / `123456`

## 1. 登录拿 token

```bash
curl -s -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"cadre_wang","password":"123456"}'
```

把返回的 `data.token` 复制出来，记为：

- `TOKEN_CADRE=<token>`

同理再登录一个村民 token：

```bash
curl -s -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhang_san","password":"123456"}'
```

- `TOKEN_VILLAGER=<token>`

## 2. 演示权限边界（403/200）

用村民 token 调村干部接口（应 403）：

```bash
curl -i "http://localhost:8080/cadre/village-affairs?current=1&size=10" \
  -H "token: ${TOKEN_VILLAGER}"
```

用村干部 token 调同一接口（应 200）：

```bash
curl -i "http://localhost:8080/cadre/village-affairs?current=1&size=10" \
  -H "token: ${TOKEN_CADRE}"
```

## 3. 演示“村务公示”闭环

### 3.1 村干部创建一条草稿

```bash
curl -s -X POST "http://localhost:8080/cadre/village-affairs" \
  -H "Content-Type: application/json" \
  -H "token: ${TOKEN_CADRE}" \
  -d '{
    "affairType":"POLICY",
    "title":"面试演示-公示标题",
    "summary":"这是摘要",
    "content":"<p>这是正文</p>",
    "attachments":"[]"
  }'
```

记录返回的 id：

- `AFFAIR_ID=<id>`

### 3.2 村干部发布（审核通过）

```bash
curl -s -X PUT "http://localhost:8080/cadre/village-affairs/${AFFAIR_ID}/audit" \
  -H "Content-Type: application/json" \
  -H "token: ${TOKEN_CADRE}" \
  -d '{"status":2,"auditRemark":"同意发布"}'
```

### 3.3 公共端查看已发布列表与详情（无需登录）

```bash
curl -s "http://localhost:8080/village-affairs?current=1&size=10"
```

```bash
curl -s "http://localhost:8080/village-affairs/${AFFAIR_ID}"
```



