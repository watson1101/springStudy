package hong.com.memo.interfaces;

import hong.com.common.infrastructure.result.Result;
import hong.com.memo.application.MemoCreateUseCase;
import hong.com.memo.application.MemoQueryUseCase;
import hong.com.memo.application.MemoUpdateUseCase;
import hong.com.memo.domain.entity.Memo;
import hong.com.memo.interfaces.dto.MemoCreateRequest;
import hong.com.memo.interfaces.dto.MemoResponse;
import hong.com.memo.interfaces.dto.MemoUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 备忘接口层 - REST 控制器
 */
@RestController
@RequestMapping("/api/memo")
@RequiredArgsConstructor
public class MemoController {

    private final MemoCreateUseCase memoCreateUseCase;
    private final MemoUpdateUseCase memoUpdateUseCase;
    private final MemoQueryUseCase memoQueryUseCase;

    /** 创建备忘 */
    @PostMapping
    public Result<MemoResponse> create(@Valid @RequestBody MemoCreateRequest request) {
        Memo memo = memoCreateUseCase.create(
                request.getTitle(), request.getContent(), request.getMemoType(),
                request.getBackgroundImage(), request.getRemindTime(), request.getCronExpression());
        return Result.success("创建成功", MemoResponse.fromDomain(memo));
    }

    /** 更新备忘 */
    @PutMapping
    public Result<MemoResponse> update(@Valid @RequestBody MemoUpdateRequest request) {
        Memo memo = memoUpdateUseCase.update(
                request.getId(), request.getTitle(), request.getContent(),
                request.getBackgroundImage(), request.getRemindTime(),
                request.getCronExpression(), request.getSortOrder());
        return Result.success("更新成功", MemoResponse.fromDomain(memo));
    }

    /** 标记完成 */
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        memoUpdateUseCase.complete(id);
        return Result.success();
    }

    /** 标记取消 */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        memoUpdateUseCase.cancel(id);
        return Result.success();
    }

    /** 重新激活 */
    @PutMapping("/{id}/reactivate")
    public Result<Void> reactivate(@PathVariable Long id) {
        memoUpdateUseCase.reactivate(id);
        return Result.success();
    }

    /** 删除备忘 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memoUpdateUseCase.delete(id);
        return Result.success();
    }

    /** 查询用户所有备忘 */
    @GetMapping("/list")
    public Result<List<MemoResponse>> listAll() {
        List<Memo> memos = memoQueryUseCase.listAll();
        return Result.success(memos.stream().map(MemoResponse::fromDomain).collect(Collectors.toList()));
    }

    /** 按类型查询 */
    @GetMapping("/list/type/{memoType}")
    public Result<List<MemoResponse>> listByType(@PathVariable Integer memoType) {
        List<Memo> memos = memoQueryUseCase.listByType(memoType);
        return Result.success(memos.stream().map(MemoResponse::fromDomain).collect(Collectors.toList()));
    }

    /** 按状态查询 */
    @GetMapping("/list/status/{status}")
    public Result<List<MemoResponse>> listByStatus(@PathVariable Integer status) {
        List<Memo> memos = memoQueryUseCase.listByStatus(status);
        return Result.success(memos.stream().map(MemoResponse::fromDomain).collect(Collectors.toList()));
    }

    /** 查询单个备忘 */
    @GetMapping("/{id}")
    public Result<MemoResponse> getById(@PathVariable Long id) {
        Memo memo = memoQueryUseCase.getById(id);
        return Result.success(MemoResponse.fromDomain(memo));
    }

    /** 统计数量 */
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(memoQueryUseCase.count());
    }
}
