package hong.com.memo.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 更新备忘请求
 */
@Data
public class MemoUpdateRequest {
    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;
    private String backgroundImage;
    private LocalDateTime remindTime;
    private String cronExpression;
    private Integer sortOrder;
}
