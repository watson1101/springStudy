package com.ms.learn.goods.feign;

import com.ms.learn.common.result.Result;
import com.ms.learn.goods.feign.vo.DictItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 字典 Feign 降级处理（ms-ds-system 不可用时返回空列表）
 */
@Slf4j
@Component
public class DictClientFallback implements DictClient {

    @Override
    public Result<List<DictItemVO>> listItems(String dictType) {
        log.warn("[DictClient] listItems 降级: dictType={}", dictType);
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<List<DictItemVO>> listItemTree(String dictType) {
        log.warn("[DictClient] listItemTree 降级: dictType={}", dictType);
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<DictItemVO> getItem(Long itemId) {
        log.warn("[DictClient] getItem 降级: itemId={}", itemId);
        return Result.fail("字典服务不可用，无法获取字典项");
    }
}
