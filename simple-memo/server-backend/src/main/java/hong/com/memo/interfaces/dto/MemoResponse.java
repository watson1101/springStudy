package hong.com.memo.interfaces.dto;

import hong.com.memo.domain.entity.Memo;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 备忘响应
 */
@Data
public class MemoResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer memoType;
    private String memoTypeName;
    private String backgroundImage;
    private LocalDateTime remindTime;
    private String cronExpression;
    private Integer status;
    private String statusName;
    private Integer sortOrder;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public static MemoResponse fromDomain(Memo memo) {
        MemoResponse resp = new MemoResponse();
        resp.setId(memo.getId());
        resp.setUserId(memo.getUserId());
        resp.setTitle(memo.getTitle());
        resp.setContent(memo.getContent());
        resp.setMemoType(memo.getMemoType().getCode());
        resp.setMemoTypeName(memo.getMemoType().getDescription());
        resp.setBackgroundImage(memo.getBackgroundImage());
        resp.setRemindTime(memo.getRemindTime());
        resp.setCronExpression(memo.getCronExpression());
        resp.setStatus(memo.getStatus().getCode());
        resp.setStatusName(memo.getStatus().getDescription());
        resp.setSortOrder(memo.getSortOrder());
        resp.setCreatedTime(memo.getCreatedTime());
        resp.setUpdatedTime(memo.getUpdatedTime());
        return resp;
    }
}
