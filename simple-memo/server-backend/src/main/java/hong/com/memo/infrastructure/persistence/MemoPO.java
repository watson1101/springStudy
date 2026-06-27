package hong.com.memo.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import hong.com.common.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 备忘持久化对象 - 对应 memo_service_memo 表
 */
@Getter
@Setter
@TableName("memo_service_memo")
public class MemoPO extends BaseEntity {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer memoType;
    private String backgroundImage;
    private LocalDateTime remindTime;
    private String cronExpression;
    private Integer status;
    private Integer sortOrder;
    private Integer deleted;
}
