package hong.com.memo.infrastructure.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hong.com.memo.domain.entity.Memo;
import hong.com.memo.domain.repository.MemoRepository;
import hong.com.memo.infrastructure.converter.MemoConverter;
import hong.com.memo.infrastructure.persistence.MemoMapper;
import hong.com.memo.infrastructure.persistence.MemoPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 备忘仓储实现
 */
@Repository
@RequiredArgsConstructor
public class MemoRepositoryImpl implements MemoRepository {

    private final MemoMapper memoMapper;
    private final MemoConverter memoConverter;

    @Override
    public Optional<Memo> findById(Long id) {
        return Optional.ofNullable(memoConverter.toDomain(memoMapper.selectById(id)));
    }

    @Override
    public List<Memo> findByUserId(Long userId) {
        return memoMapper.selectList(new LambdaQueryWrapper<MemoPO>()
                        .eq(MemoPO::getUserId, userId)
                        .eq(MemoPO::getDeleted, 0)
                        .orderByDesc(MemoPO::getSortOrder)
                        .orderByDesc(MemoPO::getCreatedTime))
                .stream().map(memoConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Memo> findByUserIdAndType(Long userId, Integer memoType) {
        return memoMapper.selectList(new LambdaQueryWrapper<MemoPO>()
                        .eq(MemoPO::getUserId, userId)
                        .eq(MemoPO::getMemoType, memoType)
                        .eq(MemoPO::getDeleted, 0))
                .stream().map(memoConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Memo> findByUserIdAndStatus(Long userId, Integer status) {
        return memoMapper.selectList(new LambdaQueryWrapper<MemoPO>()
                        .eq(MemoPO::getUserId, userId)
                        .eq(MemoPO::getStatus, status)
                        .eq(MemoPO::getDeleted, 0))
                .stream().map(memoConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public Memo save(Memo memo) {
        MemoPO po = memoConverter.toPO(memo);
        if (po.getId() == null) {
            memoMapper.insert(po);
        } else {
            memoMapper.updateById(po);
        }
        return memoConverter.toDomain(po);
    }

    @Override
    public void deleteById(Long id) {
        memoMapper.deleteById(id);
    }

    @Override
    public List<Memo> findPendingReminders(Long userId, LocalDateTime now) {
        return memoMapper.selectList(new LambdaQueryWrapper<MemoPO>()
                        .eq(MemoPO::getUserId, userId)
                        .eq(MemoPO::getDeleted, 0)
                        .eq(MemoPO::getStatus, 1)
                        .le(MemoPO::getRemindTime, now)
                        .isNotNull(MemoPO::getRemindTime))
                .stream().map(memoConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByUserId(Long userId) {
        return memoMapper.selectCount(new LambdaQueryWrapper<MemoPO>()
                .eq(MemoPO::getUserId, userId)
                .eq(MemoPO::getDeleted, 0));
    }
}
