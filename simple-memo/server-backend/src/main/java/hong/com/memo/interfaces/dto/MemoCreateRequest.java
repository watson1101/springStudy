package hong.com.memo.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 创建备忘请求
 */
@Data
public class MemoCreateRequest {
    /** 标题 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 内容 */
    private String content;

    /** 类型：1-简单备忘 2-单次定时 3-循环定时 */
    @NotNull(message = "备忘类型不能为空")
    private Integer memoType;

    /** 背景图片 */
    private String backgroundImage;

    /** 提醒时间（单次定时用） */
    private LocalDateTime remindTime;

    /** Cron 表达式（循环定时用） */
    private String cronExpression;
}
