package hong.com.memo.application;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.common.infrastructure.exception.BusinessException;
import hong.com.common.infrastructure.result.ResultCode;
import hong.com.memo.domain.entity.Memo;
import hong.com.memo.domain.repository.MemoRepository;
import hong.com.memo.domain.types.MemoStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * 备忘更新应用服务（修改、完成、取消、删除）
 */
@Service
@RequiredArgsConstructor
public class MemoUpdateUseCase {

    private final MemoRepository memoRepository;

    /**
     * 更新备忘
     */
    @Transactional(rollbackFor = Exception.class)
    public Memo update(Long id, String title, String content,
                       String backgroundImage, LocalDateTime remindTime,
                       String cronExpression, Integer sortOrder) {
        long userId = StpUtil.getLoginIdAsLong();
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "备忘不存在"));

        // 校验归属
        if (!memo.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作该备忘");
        }

        memo.setTitle(title);
        memo.setContent(content);
        memo.setBackgroundImage(backgroundImage);
        memo.setRemindTime(remindTime);
        memo.setCronExpression(cronExpression);
        if (sortOrder != null) memo.setSortOrder(sortOrder);

        return memoRepository.save(memo);
    }

    /** 标记完成 */
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        checkOwner(id);
        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.markComplete();
        memoRepository.save(memo);
    }

    /** 标记取消 */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        checkOwner(id);
        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.markCancelled();
        memoRepository.save(memo);
    }

    /** 重新激活 */
    @Transactional(rollbackFor = Exception.class)
    public void reactivate(Long id) {
        checkOwner(id);
        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.markPending();
        memoRepository.save(memo);
    }

    /** 删除 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        checkOwner(id);
        memoRepository.deleteById(id);
    }

    /** 校验当前用户是否为备忘所有者 */
    private void checkOwner(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "备忘不存在"));
        if (!memo.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作该备忘");
        }
    }
}
