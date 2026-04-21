package com.backend.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.backend.common.enums.DeletedFlag;
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

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "publishTime", LocalDateTime.class, now);

        Integer notDeleted = DeletedFlag.NOT_DELETED.getCode();
        if (metaObject.hasGetter("isDeleted")) {
            this.strictInsertFill(metaObject, "isDeleted", Integer.class, notDeleted);
        }
        if (metaObject.hasGetter("deleted")) {
            this.strictInsertFill(metaObject, "deleted", Integer.class, notDeleted);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
