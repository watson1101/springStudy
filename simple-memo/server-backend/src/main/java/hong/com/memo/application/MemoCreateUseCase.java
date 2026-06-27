package hong.com.memo.application;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.memo.domain.entity.Memo;
import hong.com.memo.domain.repository.MemoRepository;
import hong.com.memo.domain.types.MemoStatus;
import hong.com.memo.domain.types.MemoType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * 备忘创建应用服务
 */
@Service
@RequiredArgsConstructor
public class MemoCreateUseCase {

    private final MemoRepository memoRepository;

    /**
     * 创建备忘
     *
     * @param title           标题
     * @param content         内容
     * @param memoType        类型（1-简单备忘 2-单次定时 3-循环定时）
     * @param backgroundImage 背景图片（可选）
     * @param remindTime      提醒时间（单次定时用）
     * @param cronExpression  Cron表达式（循环定时用）
     * @return 创建的备忘
     */
    @Transactional(rollbackFor = Exception.class)
    public Memo create(String title, String content, Integer memoType,
                       String backgroundImage, LocalDateTime remindTime, String cronExpression) {
        long userId = StpUtil.getLoginIdAsLong();

        Memo memo = new Memo();
        memo.setUserId(userId);
        memo.setTitle(title);
        memo.setContent(content);
        memo.setMemoType(MemoType.fromCode(memoType));
        memo.setBackgroundImage(backgroundImage);
        memo.setStatus(MemoStatus.PENDING);
        memo.setSortOrder(0);
        memo.setDeleted(0);

        // 根据类型设置定时信息
        if (memoType == 2) {
            memo.setRemindTime(remindTime);
        } else if (memoType == 3) {
            memo.setCronExpression(cronExpression);
        }

        return memoRepository.save(memo);
    }
}
