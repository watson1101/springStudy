package hong.com.memo.domain.repository;

import hong.com.memo.domain.entity.Memo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 备忘仓储接口
 */
public interface MemoRepository {
    Optional<Memo> findById(Long id);
    List<Memo> findByUserId(Long userId);
    List<Memo> findByUserIdAndType(Long userId, Integer memoType);
    List<Memo> findByUserIdAndStatus(Long userId, Integer status);
    Memo save(Memo memo);
    void deleteById(Long id);
    List<Memo> findPendingReminders(Long userId, LocalDateTime now);
    long countByUserId(Long userId);
}
