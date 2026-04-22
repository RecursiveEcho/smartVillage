package com.backend.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器：
 * - insert: createTime / updateTime / publishTime(若存在) / deleted(or isDeleted)
 * - update: updateTime
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    private static final int NOT_DELETED = 0;

    @Override
    public void insertFill(MetaObject metaObject) {

        // 设置创建时间、更新时间、发布时间
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "publishTime", LocalDateTime.class, now);

        // 设置逻辑删除
        Integer notDeleted = NOT_DELETED;
        if (metaObject.hasGetter("isDeleted")) {
            this.strictInsertFill(metaObject, "isDeleted", Integer.class, notDeleted);
        }
        if (metaObject.hasGetter("deleted")) {
            this.strictInsertFill(metaObject, "deleted", Integer.class, notDeleted);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 设置更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
