package hong.com.memo;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.memo.application.MemoCreateUseCase;
import hong.com.memo.domain.entity.Memo;
import hong.com.memo.domain.repository.MemoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoCreateUseCaseTest {

    @Mock private MemoRepository memoRepository;
    @InjectMocks private MemoCreateUseCase memoCreateUseCase;

    @Test
    void testCreateSimpleMemo() {
        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            when(memoRepository.save(any(Memo.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Memo result = memoCreateUseCase.create("测试", "内容", 1, null, null, null);
            assertNotNull(result);
            assertEquals("测试", result.getTitle());
            assertEquals(1, result.getMemoType().getCode());
        }
    }

    @Test
    void testCreateTimerMemo() {
        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            when(memoRepository.save(any(Memo.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Memo result = memoCreateUseCase.create("定时", "提醒", 2, null, LocalDateTime.now(), null);
            assertNotNull(result);
            assertEquals(2, result.getMemoType().getCode());
            assertNotNull(result.getRemindTime());
        }
    }
}
