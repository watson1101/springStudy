package hong.com.memo.domain.entity;

import hong.com.common.domain.BaseEntity;
import hong.com.memo.domain.types.MemoStatus;
import hong.com.memo.domain.types.MemoType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 备忘领域实体 - 支持简单备忘、单次定时、循环定时三种模式
 */
@Getter
@Setter
public class Memo extends BaseEntity {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private MemoType memoType;
    private String backgroundImage;
    private LocalDateTime remindTime;
    private String cronExpression;
    private MemoStatus status;
    private Integer sortOrder;
    private Integer deleted;

    public void markComplete() { this.status = MemoStatus.COMPLETED; }
    public void markCancelled() { this.status = MemoStatus.CANCELLED; }
    public void markPending() { this.status = MemoStatus.PENDING; }
    public boolean isTimed() { return memoType == MemoType.SINGLE_TIMER || memoType == MemoType.RECURRING_TIMER; }
    public boolean isRecurring() { return memoType == MemoType.RECURRING_TIMER; }
}
