package hong.com.memo.application;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.memo.domain.entity.Memo;
import hong.com.memo.domain.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 备忘查询应用服务
 */
@Service
@RequiredArgsConstructor
public class MemoQueryUseCase {

    private final MemoRepository memoRepository;

    /** 查询用户的所有备忘 */
    public List<Memo> listAll() {
        return memoRepository.findByUserId(StpUtil.getLoginIdAsLong());
    }

    /** 按类型查询 */
    public List<Memo> listByType(Integer memoType) {
        return memoRepository.findByUserIdAndType(StpUtil.getLoginIdAsLong(), memoType);
    }

    /** 按状态查询 */
    public List<Memo> listByStatus(Integer status) {
        return memoRepository.findByUserIdAndStatus(StpUtil.getLoginIdAsLong(), status);
    }

    /** 查询单个备忘 */
    public Memo getById(Long id) {
        return memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("备忘不存在"));
    }

    /** 统计数量 */
    public long count() {
        return memoRepository.countByUserId(StpUtil.getLoginIdAsLong());
    }
}
