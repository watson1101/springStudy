package hong.com.common.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器，自动处理创建人/创建时间/修改人/修改时间
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdBy", String.class, "admin");
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedBy", String.class, "admin");
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedBy", String.class, "admin");
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}
