# 详情接口 Redis 缓存模板（本项目可直接照抄）

适用场景：任何 `GET /xxx/{id}` 的“详情查询”接口（公告、乡村风采、互动详情等），希望减少 DB 压力且保持一致性。

本项目缓存工具：

- `RedisJsonCacheTool`：JSON 序列化存储，统一 TTL（当前默认 30min）
- `CacheKeyUtils.detailKey(prefix, id)`：生成详情 key

---

## 1. Key 规则（先定唯一真相）

### 1.1 详情 key 前缀

每个业务模块定义一个常量前缀：

- `private static final String CACHE_KEY_PREFIX = "<biz>:detail:";`

示例：

- 公告：`announcement:detail:`
- 乡村风采：`feature:detail:`

### 1.2 生成 key

```java
String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
```

---

## 2. 详情查询模板（查缓存 → 查库 → 回写缓存）

**最小可用版本**（推荐默认用这个，维护成本最低）：

```java
public XxxVO getDetail(Long id) {
    String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);

    // 1) 读缓存
    XxxVO fromCache = redisJsonCacheTool.getObject(cacheKey, XxxVO.class);
    if (fromCache != null) {
        return fromCache;
    }

    // 2) 查库 + 存在性/可见性校验
    XxxEntity entity = getById(id);
    if (entity == null) {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "资源不存在");
    }
    // 可选：状态校验（比如仅已发布可见）
    // if (!Objects.equals(entity.getStatus(), STATUS_PUBLISHED)) throw ...

    // 3) 转 VO + 回写缓存
    XxxVO vo = new XxxVO();
    BeanUtils.copyProperties(entity, vo);
    redisJsonCacheTool.setObject(cacheKey, vo);
    return vo;
}
```

**记忆口诀**：详情 = **先缓存，后查库，再回写**。

---

## 3. 写操作模板（只删不改）

任何会改变详情内容的操作后，都应清理对应详情缓存（避免脏读）：

- create（若返回/可能使用该 id 的详情）
- update（编辑）
- updateStatus / audit / 上下架 / 关闭（影响可见性）
- delete（删除/逻辑删除）

统一封装一个方法，写操作结束后调用：

```java
private void evictDetailCache(Object id) {
    if (id != null) {
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    }
}
```

**原则**：写操作后缓存处理 = **只删不改**（不要尝试 patch 缓存字段，维护成本高且容易漏）。

---

## 4. 常见坑（上线前自检）

### 4.1 缓存穿透（不存在 id 一直打 DB）

现状建议：先不做，等真实有压力再做“空值缓存/布隆过滤器”。

### 4.2 脏缓存（最常见）

自检清单：

- 所有写入口最后都调用了 `evictDetailCache(id)` 吗？
- 是否存在“状态改变但详情仍可从缓存读到”的路径？（例如下架后仍命中缓存）

### 4.3 缓存命中时也要校验可见性（有状态机时）

如果“前台只能看已发布”，务必保证：

- **DB 路径校验 status**
- **缓存命中路径也校验 VO.status**（不满足就删缓存并回落到 DB/抛错）

公告模块就是这个思路的样板。

### 4.4 列表缓存不要轻易上

分页+筛选+排序会导致 key 爆炸，失效困难。

若必须上列表缓存，推荐用“版本号”方案：

- 列表 key = `feature:list:v<version>:<paramsHash>`
- 写操作时只需要 `INCR feature:list:version` 即可整体失效

---

## 5. 你可以直接复制的落地步骤（每个新模块 5 分钟搞定）

- 在 `ServiceImpl` 里注入 `RedisJsonCacheTool`
- 定义 `CACHE_KEY_PREFIX`
- `getDetail(id)` 按第 2 章模板改造
- 所有写方法末尾统一 `evictDetailCache(id)`
- 如果有“仅已发布可见”等规则，缓存命中路径也要做同样校验

